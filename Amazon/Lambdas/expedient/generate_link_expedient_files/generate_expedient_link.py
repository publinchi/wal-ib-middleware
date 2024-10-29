import json
import os
import logging
import boto3
import zipfile
import io
from datetime import datetime

def generate_expedient_link(facade, context):
    logger = facade["LOGGER"]
    event = facade["EVENT"]
    
    # Initialize S3 client
    s3_client = boto3.client('s3')
    bucket_name = os.environ.get('BUCKET_NAME')

    try:
        # Parse the incoming event body
        body = json.loads(event["body"])
        externalCustomerIds = body["externalCustomerIds"]
        externalCustomerIds = list(set(externalCustomerIds))
        id = body["id"]
        
        logger.info(f"Initial Folders: {externalCustomerIds}")
        
        # Validate existence of each folder and remove non-existing ones
        valid_customer_ids = []
        for customer_id in externalCustomerIds:
            prefix = f"files/{customer_id}/"
            response = s3_client.list_objects_v2(Bucket=bucket_name, Prefix=prefix)
            if 'Contents' in response:
                valid_customer_ids.append(customer_id)
            else:
                logger.info(f"Folder does not exist for customer ID: {customer_id}")
               
        logger.info(f"Valid Folders - valid_customer_ids: {valid_customer_ids}")
        
        # Create a zip file in memory
        zip_buffer = io.BytesIO()
        with zipfile.ZipFile(zip_buffer, 'w', zipfile.ZIP_DEFLATED) as zip_file:
            for customer_id in valid_customer_ids:
                prefix = f"files/{customer_id}/"
                response = s3_client.list_objects_v2(Bucket=bucket_name, Prefix=prefix)
                #logger.info(f"Ruta completa - response: {response}")
                
                for obj in response.get('Contents', []):
                    file_key = obj['Key']                    

                    # Saltar si el objeto es una carpeta (tamaño 0 o termina con '/')
                    if obj['Size'] == 0 or file_key.endswith('/'):
                        continue
                       
                    logger.info(f"Ruta completa archivo - file_key: {file_key}")
                       
                    # Primero, guarda el archivo original
                    try:
                        file_obj = s3_client.get_object(Bucket=bucket_name, Key=file_key)
                        file_data = file_obj['Body'].read()
                        
                        # Obtener extensión y nombre del archivo
                        name_file = os.path.splitext(os.path.basename(file_key))[0]
                        ext_file = os.path.splitext(os.path.basename(file_key))[1]
                       
                        # Escribir en el archivo zip con la estructura de carpetas deseada
                        zip_file.writestr(f"files/{customer_id}/{name_file}{ext_file}", file_data)

                        # Ahora guarda las versiones, omitiendo la versión original
                        versions = s3_client.list_object_versions(Bucket=bucket_name, Prefix=file_key)
                                              
                        for vrs in versions.get('Versions', []):
                            # Verifica si la versión actual es la más reciente
                            if vrs['IsLatest']:
                                continue  # Omitir la versión original
                            
                            # Obtener la fecha de la versión
                            last_modified = vrs['LastModified']
                            # Formatear la fecha
                            date_str = last_modified.strftime("%d%m%Y%H%M%S")
                           
                            # Crear el nombre del archivo usando la fecha
                            version_file_key = f"files/{customer_id}/{name_file}_{date_str}{ext_file}"
                           
                            try:
                                version_obj = s3_client.get_object(Bucket=bucket_name, Key=file_key, VersionId=vrs['VersionId'])
                                version_data = version_obj['Body'].read()
                           
                                # Escribir en el archivo zip con la fecha
                                zip_file.writestr(version_file_key, version_data)
                            except s3_client.exceptions.NoSuchVersion as e:
                                logger.error(f"Version {vrs['VersionId']} not found for file {file_key}: {str(e)}")
                            except Exception as e:
                                logger.error(f"Error fetching version {vrs['VersionId']} of object {file_key}: {str(e)}")
                           
                    except s3_client.exceptions.NoSuchKey as e:
                        logger.error(f"File not found: {file_key}: {str(e)}")
                    except Exception as e:
                        logger.error(f"Error fetching object {file_key}: {str(e)}")
        
        zip_buffer.seek(0)
        
        # Generate the zip file name
        date_str = datetime.now().strftime("%y%m%d")
        zip_key = f'files/files_{date_str}{id}.zip'
        
        # Upload the zip file to S3
        s3_client.upload_fileobj(zip_buffer, bucket_name, zip_key)
        
        # Generate a pre-signed URL that expires in one week
        url = s3_client.generate_presigned_url(
            'get_object',
            Params={'Bucket': bucket_name, 'Key': zip_key},
            ExpiresIn=604800  # 1 week in seconds
        ) 
        
        facade['BODY'] = {"status": 'success', "url": url}
    
    except Exception as e:
        logger.error(f"Error generating expedient link: {str(e)}")
        facade['BODY'] = {"status": 'false'}
