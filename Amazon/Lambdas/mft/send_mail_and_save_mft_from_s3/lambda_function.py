import boto3
import pymssql
import paramiko
import json
import os
import socket
from botocore.exceptions import NoCredentialsError, PartialCredentialsError
from aws_secretsmanager_caching import SecretCache, SecretCacheConfig
from io import StringIO


SECRET_NAME_MFT = os.environ.get('SECRET_NAME_MFT', '')
S3_FILES_BUCKET_NAME = os.environ.get('S3_FILES_BUCKET_NAME', '')
SECRET_NAME_DB = os.environ.get('SECRET_NAME_DB', '')
TIMEOUT_MFT_CONNECTION = int(os.environ.get('TIMEOUT_MFT_CONNECTION', 60))
TYPE_CREDENTIAL = 'C'
TYPE_KEY = 'K'
YES_ENABLE_MFT = 'S'
PROCEDURE_NAME = 'cob_bvirtual..sp_mft_operations'
BUSINESS_CODE_ERROR = 600
BUSINESS_CODE_SUCCESS = 200
INFRASTRUCTURE_CODE_ERROR = 500
PATH_TMP = '/tmp/'
database_connection = None

def lambda_handler(event, context):
    print('init execute lambda_handler')
    file_name = None
    file_path_local = None
    file_names = []
    files_path_local = []

    flow_name = event.get('flow_name')
    user_execute = event.get('user_execute')
    if flow_name is None or flow_name == '':
        return {
            'statusCode': BUSINESS_CODE_SUCCESS,
            'body': "Nombre de flujo no puede ser vacio"
        }
    try:
        params = get_params_from_database(flow_name,context)
        path_s3 = params.get('path_origin')
        paths_s3 = path_s3.split('|')
        path_mft = params.get('path_destination')
        enable_mft = params.get('enable_mft')

        if enable_mft is None or enable_mft == '' or enable_mft != YES_ENABLE_MFT:
            return build_response(BUSINESS_CODE_SUCCESS, "No se procesaron archivos porque el proceso de MFT no está habilitado")

        for path in paths_s3:
            file_path_local = download_file_from_s3(S3_FILES_BUCKET_NAME, path)
            save_file_to_sftp(file_path_local, path_mft, flow_name ,context)
            file_name = get_basename(file_path_local)
            files_path_local.append(file_path_local)
            file_names.append(file_name)
        send_email_from_database(flow_name, 'E', f'{BUSINESS_CODE_SUCCESS}-Proceso completado exitosamente', '|'.join(file_names), user_execute,context)
    except BusinessException as e:
        handle_error(e, file_name, flow_name, user_execute,context)
        return build_response(BUSINESS_CODE_ERROR, e.description)
    except Exception as e:
        handle_error(e, file_name, flow_name, user_execute,context)
        return build_response(INFRASTRUCTURE_CODE_ERROR, str(e))
    finally:
        close_connection()
        remove_local_files(files_path_local)
        print('end execute lambda_handler')

    return build_response(BUSINESS_CODE_SUCCESS, "Proceso completado exitosamente")

def build_response(status_code, body):
    return {
        'statusCode': status_code,
        'body': body
    }

def handle_error(e, file_name, flow_name, user_execute,context):
    print(f"Error en ejecución: {e}")
    file_name_tmp = file_name if file_name is not None else ''
    try:
        send_email_from_database(flow_name, 'F', f"{BUSINESS_CODE_ERROR}-{e.description}", file_name_tmp, user_execute,context)
    except Exception as email_error:
        print(f"Error al enviar el correo: {email_error}")

def remove_local_files(files_path_local):
    for file_path_local in files_path_local:
        if file_path_local is not None:
            os.remove(file_path_local)
            print(f"File {file_path_local} deleted")

def get_params_from_database(flow_name, context):
    print('init execute get_params_from_database')
    operation = 'P'
    params = {'@i_nombre_flujo': flow_name, '@i_operacion': operation}

    results = execute_stored_procedure(PROCEDURE_NAME, params,context)

    if results is None or len(results) == 0:
        raise BusinessException(f"Consulta de parametros a base de datos no arrojó resultados")
    result = results[0]
    print('end execute get_params_from_database')
    return {'path_origin': result[0], 'path_destination': result[1], 'enable_mft': result[2]}

def send_email_from_database(flow_name, status, description, file_name, user_execute, context):
    print('init execute send_email_from_database')
    operation = 'E'
    params = {'@i_nombre_flujo': flow_name, '@i_estado': status, '@i_operacion': operation, '@i_descripcion': description, '@i_nombre_archivo': file_name, '@i_usuario': user_execute}
    execute_stored_procedure(PROCEDURE_NAME, params,context)
    print('end execute send_email_from_database')

def execute_stored_procedure(procedure_name, params,context):
    global database_connection
    print('init execute execute_stored_procedure')
    try:
        secret_data_base = get_secret(SECRET_NAME_DB, context)
        secret_data_base = json.loads(secret_data_base)
        if database_connection is None:
            database_connection = pymssql.connect(
                    host=secret_data_base.get('host'),
                    port=int(secret_data_base.get('port')),
                    user=secret_data_base.get('username'),
                    password=secret_data_base.get('password'),
                    database=secret_data_base.get('dbname')
                )
            print("Successfully connected to the database.")

        params = sanitize_params(params)
        placeholders = ', '.join([f'{key}=%s' for key in params.keys()])
        sql = f"SET NOCOUNT ON; exec {procedure_name} {placeholders}"
        cursor = database_connection.cursor(as_dict=False)
        cursor.execute(sql, tuple(params.values()))

        if cursor.description is None:
            database_connection.commit()
            cursor.close()
            return []
        result  = cursor.fetchall()
        database_connection.commit()
        cursor.close()
        return result
    except pymssql.Error as e:
        raise BusinessException(f"Error al ejecutar el procedimiento almacenado {procedure_name}: {e}")
    finally:
        print('end execute execute_stored_procedure')

def close_connection():
    global database_connection
    if database_connection is not None:
        database_connection.close()
        database_connection = None
        print("Database connection closed")

def sanitize_params(params):
    sanitized_params = {}
    for key, value in params.items():
        if isinstance(value, str):
            sanitized_value = value.replace("'", "''")
        else:
            sanitized_value = value
        sanitized_params[key] = sanitized_value
    return sanitized_params


def get_secret(secret_name, context):
    print('init execute get_secret')
    if secret_name is None or secret_name == '':
        raise BusinessException("Nombre de secreto no puede ser vacio")

    session = boto3.session.Session()
    region_name = context.invoked_function_arn.split(':')[3]
    client = session.client(service_name='secretsmanager', region_name=region_name)

    # Setup the cache
    cache_config = SecretCacheConfig()
    cache = SecretCache(config=cache_config, client=client)

    try:
        secret_value = cache.get_secret_string(secret_name)

        if secret_value is None:
            raise BusinessException(f"Secreto {secret_name} no encontrado")

        print('end execute get_secret')
        return secret_value
    except Exception as e:
        raise BusinessException(f"Error al obtener el secreto {secret_name}: {e}")

def download_file_from_s3(bucket_name, prefix):
    print('init execute download_file_from_s3')
    try:
        s3 = boto3.client('s3')
        s3_key = get_most_recent_file(s3, bucket_name, prefix)
        file_name = get_basename(s3_key)
        file_name_tmp = f'{PATH_TMP}{file_name}'
        s3.download_file(bucket_name, s3_key, file_name_tmp)
        return file_name_tmp
    except NoCredentialsError:
        raise BusinessException("S3: Credenciales no encontradas")
    except PartialCredentialsError:
        raise BusinessException("S3: Credenciales incompletas")
    except Exception as e:
        raise BusinessException(f"S3: Error al descargar archivo {e}")
    finally:
        print('end execute download_file_from_s3')

def get_most_recent_file(s3,bucket_name, prefix):
    print('init execute get_most_recent_file')
    try:
        if prefix.startswith('/'):
            prefix = prefix[1:]

        response = s3.list_objects_v2(Bucket=bucket_name, Prefix=prefix)
        if 'Contents' not in response:
            raise BusinessException(f"No se encontraron archivos en el bucket {bucket_name} con la ruta {prefix}")

        objects = response['Contents']
        most_recent = max(objects, key=lambda obj: obj['LastModified'])
        return most_recent['Key']
    except Exception as e:
        raise BusinessException(f"Error al obtener archivos de S3: {e}")
    finally:
        print('end execute get_most_recent_file')


def save_file_to_sftp(file_path_local, path, flow_name, context):
    print('init execute save_file_to_sftp')
    secret_value = json.loads(get_secret(SECRET_NAME_MFT, context))
    values = secret_value.get(flow_name)

    user = values.get('user')
    host = values.get('host')
    port = int(values.get('port'))
    type = values.get('type')

    transport = None
    sftp = None
    try:
        transport = paramiko.Transport((host, port))

        if type == TYPE_CREDENTIAL:
            password = values.get('password')
            if not password:
                raise BusinessException("La contraseña no está definida en el secreto MFT")
            transport.connect(username=user, password=password)
        elif type == TYPE_KEY:
            secret_name_pem= values.get('name_secret_pem')
            if not secret_name_pem:
                raise BusinessException("El nombre del secreto PEM no está definido en el secreto MFT")
            secret_value_pem = get_secret(secret_name_pem,context)
            private_key = paramiko.RSAKey.from_private_key(StringIO(secret_value_pem))
            transport.connect(username=user, pkey=private_key)
        else:
            raise BusinessException("Tipo de autenticación SFTP no soportada, revisar configuración para el flujo" + flow_name)

        sftp = paramiko.SFTPClient.from_transport(transport)
        sftp.get_channel().settimeout(TIMEOUT_MFT_CONNECTION)
        create_sftp_directory(sftp, path)
        file_name = get_basename(file_path_local)

        path = os.path.join(path, file_name)
        sftp.put(file_path_local, remotepath=path)
    except socket.timeout as e:
        raise BusinessException(f"Error de tiempo de espera al subir archivo a SFTP. Revisar configuración de timeout")
    except Exception as e:
        raise BusinessException(f"Error al subir archivo a SFTP: {e}")
    finally:
        if sftp:
            sftp.close()
        if transport:
            transport.close()
        print('end execute save_file_to_sftp')

def create_sftp_directory(sftp, remote_directory):
    dirs = remote_directory.split('/')
    path = ''
    for dir in dirs:
        if dir:
            path += f'/{dir}'
            try:
                sftp.stat(path)
            except FileNotFoundError:
                sftp.mkdir(path)

def get_basename(file_path):
    return os.path.basename(file_path)

class BusinessException(Exception):
    def __init__(self, description):
        self.description = description
        super().__init__(description)