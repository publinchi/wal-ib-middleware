import pymssql
#import logging
import json
from decimal import Decimal
from datetime import datetime
from cobis.dbinteractiontype import DBInteractionType


class Cobisdmssql:
    #logger = logging.getLogger()
    #logger.setLevel(logging.INFO)
    database_connection = None
    secretname = None
    host = None
    port = None
    user = None
    password = None
    database = None
    commandtext = None
    parameters = None
    commandtype = None
    context = None
    rowcount = 0
    auto_increment_id = 0

    def __init__(self, secret_name, context):
        #global secretname
        self.secretname = secret_name
        self.context = context

    def _connect(self, secret_name):
        #global database_connection
        #global logger

        # Verificar si ya existe una conexión a la base de datos
        print(str(self.database_connection))

        if self.database_connection is None:
            """
            # Obtener el secreto de AWS Secrets Manager
            context_arn = self.context.invoked_function_arn
            values = context_arn.split(':')
            region_name = values[3]
            client = boto3.client('secretsmanager')
            try:
                get_secret_value_response = client.get_secret_value(SecretId=secret_name)
            except client.exceptions.ResourceNotFoundException:
                raise ValueError("The specified secret does not exist.")
            except client.exceptions.ClientError as e:
                raise ValueError("Error retrieving the secret: {}".format(e, secret_name))

            # Obtener los valores del secreto
            if 'SecretString' in get_secret_value_response:
                secret = get_secret_value_response['SecretString']
            else:
                secret = get_secret_value_response['SecretBinary']

            secret_dict = eval(secret)  # Convierte la cadena JSON en un diccionario
        """
            secret_dict = secret_name


            # Conexión a la base de datos de Aurora Serverless v2
            try:

                self.host = secret_dict['masterEndpoint']
                self.port = int(secret_dict['masterPort'])
                self.user = secret_dict['username']
                self.password = secret_dict['password']
                self.database = secret_dict['database']

                conn = pymssql.connect(
                    server=self.host,
                    port=self.port,
                    user=self.user,
                    password=self.password,
                    database=self.database
                    # ssl={'ssl': {'ca': '/etc/ssl/certs/ca-certificates.crt'}}  # Ruta al certificado SSL de confianza
                )
                #self.logger.info("=========Successfully connected to the database.")
                self.database_connection = conn  # Asignar la conexión a la variable global
            except pymssql.Error as e:
                #self.logger.info("=========Error connecting to the database", str(e))
                raise ValueError("Error connecting to the database: {}".format(e))
        else:
            if self.database_connection.open:
                #self.logger.info("=========Already connected")
                print("=========Already connected")
            else:
                #self.logger.info("=========Reconnecting DB")
                self.database_connection.connect()
            database_connection = self.database_connection

    def Connect(self):
        try:
            #global secretname
            #global logger
            if self.secretname == None or len(self.secretname) == 0:
                #self.logger.info("=========Error no secret defined")
                raise ValueError("Error no secret defined")
            else:
                self._connect(self.secretname)
        except Exception as e:
            raise e

    def CommandType(self, commandtype):
        self.commandtype = commandtype
      


    def CommandText(self, commandtext: str):
        if len(commandtext) > 1:
            self.commandtext = commandtext

    def Procedure(self, storedprocedure: str):
        if len( storedprocedure) > 1:
            self.commandtext =  storedprocedure

    def CommandParameters(self, parameters):
        self.parameters = parameters

    def Execute(self):
        #global logger

        #loginfo = '=========Begin executing'
        #self.logger.info(loginfo)
        errormessage = None
        if self.host == None or self.port == None or self.user == None or self.password == None or self.database == None or self.secretname == None:
            errormessage = 'Connection parameteres are incomplete'

        if self.database_connection == None:
            errormessage = 'No database connection'

        if self.commandtext == None:
            errormessage = 'No command to execute'

        if self.commandtype == None:
            errormessage = 'No command type is defined'

        if not errormessage == None:
            var1 = "Error executing query: " + errormessage
            #self.logger.info(var1)
            raise Exception(var1)

        loginfo = '=========Before executing'
        #self.logger.info(loginfo)

        if self.commandtype == 0:
            retorno = self._Execute_simple_query()
            loginfo = '=========OK simple query execution'
            #self.logger.info(loginfo)
            return retorno
        elif self.commandtype == 1:
            retorno = self._Execute_query_for_update()
            loginfo = '=========OK complex query execution'
            #self.logger.info(loginfo)
            return retorno
        elif self.commandtype == 2:
            retorno = self._Execute_query_for_deferred_update()
            loginfo = '=========OK complex query execution deferred'
            #self.logger.info(loginfo)
            return retorno
        elif self.commandtype == 3:
            retorno = self._Execute_procedure_complex()
            loginfo = '=========OK complex query execution deferred'
            #self.logger.info(loginfo)
            return retorno
        else:
            loginfo = '=========Error on execution'
            #self.logger.info(loginfo)

            return None

    def _Execute_simple_query(self):
        #global logger
        try:
            loginfo = '============Opening cursor'
            #self.logger.info(loginfo)

            # Realizar la consulta en la base de datos
            cursor = self.database_connection.cursor()
            cursor.execute(self.commandtext, self.parameters)
            result = cursor.fetchall()
            # Obtener la cantidad de registros afectados
            num_rows_affected = cursor.rowcount
            self.rowcount = num_rows_affected
            loginfo = '============Cursor Mapped, records: ' + str(num_rows_affected)
            #self.logger.info(loginfo)

            # Convertir el resultado a formato JSON con campos convertidos a str
            columns = [column[0] for column in cursor.description]
            json_result = []
            for row in result:
                json_row = {}
                for i, value in enumerate(row):
                    if isinstance(value, Decimal):
                        json_row[columns[i]] = str(value)
                    elif isinstance(value, datetime):
                        json_row[columns[i]] = value.strftime("%Y-%m-%d %H:%M:%S")  # Convertir a cadena
                    else:
                        json_row[columns[i]] = value
                json_result.append(json_row)

            # Agregar el campo de registros afectados al JSON
            json_result.append({"records_affected": num_rows_affected})

            loginfo = '============Json response has been constructed'
            #self.logger.info(loginfo)

            return json.dumps(json_result)


        except Exception as e:
            raise ValueError("Error in the database transaction: {}".format(e))

    def _Execute_query_for_update(self):
        #global logger
        resolved_sql = ''
        data = {
            "commandtype": "ComplexQuery",
            "sqlsentence": "none",
            "rowcount": 0,
            "auto_increment_id": 0,
            "status": "Error"
        }

        try:
            loginfo = '============Opening cursor'
            #self.logger.info(loginfo)

            # Realizar la consulta en la base de datos
            try:
                cursor = self.database_connection.cursor()
                #resolved_sql = cursor.mogrify(self.commandtext, self.parameters)
                cursor.execute(self.commandtext, self.parameters)
                # Obtener la cantidad de registros afectados
                num_rows_affected = cursor.rowcount
                self.rowcount = num_rows_affected
                try:
                    auto_increment_id = cursor.lastrowid
                except Exception:
                    auto_increment_id = -1
                self.auto_increment_id = auto_increment_id

                self.database_connection.commit()
                loginfo = '============Cursor Mapped and commited, records: ' + str(num_rows_affected)
                #self.logger.info(loginfo)
                data['sqlsentence'] = resolved_sql
                data['rowcount'] = num_rows_affected
                data['auto_increment_id'] = auto_increment_id
                data['status'] = "OK"





            except Exception as e:
                self.database_connection.rollback()
                loginfo = '============Transaction rollbacked ' + str(e)
                #self.logger.info(loginfo)
                data['rowcount'] = -1
                data['status'] = "Error"
                raise e

            return json.dumps(data)


        except Exception as e:
            data['rowcount'] = -1
            data['status'] = "Error"
            raise ValueError("Error in the database transaction: {}".format(e))

    def _Execute_query_for_deferred_update(self):
        #global logger
        resolved_sql = ''
        data = {
            "commandtype": "ComplexQuery",
            "sqlsentence": "none",
            "rowcount": 0,
            "auto_increment_id": 0,
            "status": "Error"
        }

        try:
            loginfo = '============Opening cursor'
            #self.logger.info(loginfo)

            # Realizar la consulta en la base de datos
            try:
                cursor = self.database_connection.cursor()
                resolved_sql = cursor.mogrify(self.commandtext, self.parameters)
                cursor.execute(self.commandtext, self.parameters)
                result = cursor.fetchall()
                # Obtener la cantidad de registros afectados
                num_rows_affected = cursor.rowcount
                self.rowcount = num_rows_affected
                try:
                    auto_increment_id = cursor.lastrowid
                except Exception:
                    auto_increment_id = -1
                self.auto_increment_id = auto_increment_id

                loginfo = '============Cursor Mapped and commited, records: ' + str(num_rows_affected)
                #self.logger.info(loginfo)
                data['sqlsentence'] = resolved_sql
                data['rowcount'] = num_rows_affected
                data['auto_increment_id'] = auto_increment_id
                data['status'] = "OK"





            except Exception as e:
                self.database_connection.rollback()
                loginfo = '============Transaction rollbacked ' + str(e)
                #self.logger.info(loginfo)
                data['rowcount'] = -1
                data['status'] = "Error"
                raise e

            return json.dumps(data)


        except Exception as e:
            data['rowcount'] = -1
            data['status'] = "Error"
            raise ValueError("Error in the database transaction: {}".format(e))


    def _Execute_procedure_complex(self):
        #global logger
        try:
            loginfo = '============Opening cursor'
            #self.logger.info(loginfo)

            # Realizar la consulta en la base de datos
            cursor = self.database_connection.cursor()
            cursor.execute(self.commandtext)
            result = cursor.fetchall()
            self.rowcount = None
            columns = [column[0] for column in cursor.description]
            json_main_result = []
            json_result = []
            num_rows_affected = 0
            for row in result:
                num_rows_affected += 1
                json_row = {}
                for i, value in enumerate(row):
                    if isinstance(value, Decimal):
                        json_row[columns[i]] = str(value)
                    elif isinstance(value, datetime):
                        json_row[columns[i]] = value.strftime("%Y-%m-%d %H:%M:%S")  # Convertir a cadena
                    else:
                        json_row[columns[i]] = str(value)

                json_result.append(json_row)

            # Agregar el campo de registros afectados al JSON
            json_result.append({"records_affected": num_rows_affected})

            json_main_result.append(json_result)


            while cursor.nextset():
                result = cursor.fetchall()
                self.rowcount = len(result)
                columns = [column[0] for column in cursor.description]
                json_result = []
                num_rows_affected = 0
                for row in result:
                    num_rows_affected += 1
                    json_row = {}
                    for i, value in enumerate(row):
                        if isinstance(value, Decimal):
                            json_row[columns[i]] = str(value)
                        elif isinstance(value, datetime):
                            json_row[columns[i]] = value.strftime("%Y-%m-%d %H:%M:%S")  # Convertir a cadena
                        else:
                            json_row[columns[i]] = str(value)
                    json_result.append(json_row)

                # Agregar el campo de registros afectados al JSON
                json_result.append({"records_affected": num_rows_affected})

                json_main_result.append(json_result)





            loginfo = '============Json response has been constructed'
            #self.logger.info(loginfo)

            return json.dumps(json_main_result)


        except Exception as e:
            raise ValueError("Error in the database transaction: {}".format(e))


    def commit_deferred_updates(self):
        loginfo = '============Executing commit for multiple updates/insert queries'
        #self.logger.info(loginfo)
        self.database_connection.commit()

    def _Execute_query_for_deferred_update(self):
        #global logger
        resolved_sql = ''
        data = {
            "commandtype": "ComplexQuery",
            "sqlsentence": "none",
            "rowcount": 0,
            "auto_increment_id": 0,
            "status": "Error"
        }

        try:
            loginfo = '============Opening cursor'
            #self.logger.info(loginfo)

            # Realizar la consulta en la base de datos
            try:
                cursor = self.database_connection.cursor()
                #resolved_sql = cursor.mogrify(self.commandtext, self.parameters)
                cursor.execute(self.commandtext, self.parameters)
                #result = cursor.fetchall()
                # Obtener la cantidad de registros afectados
                num_rows_affected = cursor.rowcount
                self.rowcount = num_rows_affected
                try:
                    auto_increment_id = cursor.lastrowid
                except Exception:
                    auto_increment_id = -1
                self.auto_increment_id = auto_increment_id

                loginfo = '============Cursor Mapped and commited, records: ' + str(num_rows_affected)
                #self.logger.info(loginfo)
                data['sqlsentence'] = resolved_sql
                data['rowcount'] = num_rows_affected
                data['auto_increment_id'] = auto_increment_id
                data['status'] = "OK"





            except Exception as e:
                self.database_connection.rollback()
                loginfo = '============Transaction rollbacked ' + str(e)
                #self.logger.info(loginfo)
                data['rowcount'] = -1
                data['status'] = "Error"
                raise e

            return json.dumps(data)


        except Exception as e:
            data['rowcount'] = -1
            data['status'] = "Error"
            raise ValueError("Error in the database transaction: {}".format(e))

    def EvaluateState(self):
        print("++++++++++only for debug purpose")
        print("++++++++++database_connection = ", str(self.database_connection))
        print("++++++++++secretname = ", str(self.secretname))
        print("++++++++++host = ", self.host)
        print("++++++++++port = ", self.port)
        print("++++++++++user = ", self.user)
        print("++++++++++password length = ", (0 if (self.password is None) else len(self.password)))
        print("++++++++++database = ", str(self.database))
        print("++++++++++CommandText = ", str(self.commandtext))
        print("++++++++++CommandParameters = ", str(self.parameters))
        print("++++++++++CommandType = ", str(self.commandtype))

    def Reset(self):
        self.commandText = None
        self.parameters = None
        self.commandType = None
        self.procedure = None

    def query_conform(self, procedurename, params):
        supported_types = ("<class 'DBint'>","<class 'DBnumeric'>","<class 'DBstr'>", "<class 'DBdate'>", "<class 'DBdatetime'>")
        equivalent_types = ("int", "float", "varchar(1024)", "date", "datetime")

        if not len(supported_types) == len(equivalent_types):
            raise Exception("Error in definition of supported types and equivalent base types")

        query = ""
        parameters = "declare @w_return int\n"
        parameters_in_sp = ""
        assign_values = ""
        response_variables = ""
        posicion = 0
        for param in params:
            param_type = param.gettype()
            parameter_sql = None
            flag = False
            i=0

            for one_type in supported_types:
                if param_type == one_type:
                    parameter_sql = equivalent_types[i]
                    flag = True
                    break
                i += 1

            if flag is False:
                raise ValueError("Not supported data type form mssql type")
                return

            parameters += f"declare @w_parameter_{posicion} as {parameter_sql}\n"
            value_tmp = "null" if (param.value is None) else f"'{param.value}'"
            assign_values += f"set @w_parameter_{posicion} = convert({parameter_sql},{value_tmp})\n"
            tipo_inout = "out " if param.inout == 1 else ""
            postfijo_inout = "_out" if param.inout == 1 else ""
            separador = "" if posicion == 0 else ","
            parameters_in_sp += f"{separador} @w_parameter_{posicion} {tipo_inout}"
            response_variables += f"{separador} @w_parameter_{posicion} as 'w_parameter_{posicion}{postfijo_inout}' "
            posicion += 1

        query = f"{parameters} \n {assign_values} \n exec @w_return = {procedurename}  {parameters_in_sp}"
        query += f"\nselect @w_return as 'w_return', {response_variables}"
        return query
