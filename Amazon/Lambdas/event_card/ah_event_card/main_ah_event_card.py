from cobis.cobisdbconnect import execute_database
import json
import os
import uuid
from datetime import datetime as dt
import datetime
import boto3
import re
import time

events = boto3.client('events')


def even_card(event, context):
    try:
        # Consulta
        consulta = """
          exec cob_atm..sp_atm_event_card
            @i_operation = %s
        """

        # Parámetros
        parametros = ("Y")

        try:
            # Llama a la función execute_database con la consulta de prueba y parámetros de prueba
            resultado = execute_database(consulta, parametros, context)
            data = json.loads(resultado)
            # jsonPath = os.environ['LAMBDA_TASK_ROOT'] + "/format.json"
            # jsonContents = open(jsonPath).read()
            aws_request_id = context.aws_request_id
            uuid_value = str(uuid.uuid4())
            account_arn = context.invoked_function_arn
            account = extract_account_and_function(account_arn)

            filtered_data = [entry for entry in data if 'records_affected' not in entry]

            for row in filtered_data:

                if row["ec_type_card"] is not None and row["ec_type_card"] != "":

                    data_json_row = {
                        "eventType": row["ec_event_type"],
                        "externalCustomerId": str(row["ec_external_customer"]),
                        "accountNumber": row["ec_account"],
                        "card": {
                            "cardId": row["ec_card_id"],
                            "type": row["ec_type_card"],
                            "status": row["ec_status"],
                            "statusReason": 'TBD',
                            "expiryDate": row["ec_expiry_date"],
                            "maskedCardNumber": row["ec_masked_card_number"]
                        },
                        "retry": row["ec_retry"],
                        "creation_date": row["ec_creation_date"]
                    }
                    print("INVOCANDO 1")

                    nombre_bus_eventos = 'staging_demand_deposits'

                    print("INVOCANDO 2")
                    response = events.put_events(
                        Entries=[{
                            'Source': 'cobis.lambda.function',
                            'DetailType': '"staging-ah_event_card"',
                            'Detail': json.dumps(data_json_row),
                            'EventBusName': nombre_bus_eventos

                        }]
                    )
                    print("INVOCANDO 3")
                    time.sleep(1)

                    print(json.dumps(data_json_row))

                    # Verifica el resultado del envío del evento
                    if response.get('FailedEntryCount', 0) == 0:
                        print("Evento enviado exitosamente.")
                        # Verifica el nombre del bus en la respuesta
                        entries = response.get('Entries')
                        if entries and isinstance(entries, list):
                            for entry in entries:
                                event_bus_name = entry.get('EventBusName')
                                if event_bus_name and event_bus_name != nombre_bus_eventos:
                                    print(f"El evento fue enviado a un bus diferente: {event_bus_name}")
                    else:
                        print(f"Error al enviar el evento: {response}")




        except Exception as e:
            # Maneja cualquier excepción que pueda ocurrir durante la ejecución
            print(f"Error durante la ejecución: {e}")
            import traceback
            traceback.print_exc()

    except:
        # Este bloque finally se ejecutará siempre, independientemente de si se produce una excepción o no
        pass


def extract_account_and_function(arn):
    # Define el patrón de expresión regular para extraer el número de cuenta
    patron_arn = re.compile(r'arn:aws:lambda:[a-z0-9\-]+:(\d+):function:[a-zA-Z0-9-_]+')

    # Encuentra el número de cuenta en el ARN usando el patrón
    coincidencia = patron_arn.match(arn)

    if coincidencia:
        numero_cuenta = coincidencia.group(1)
        return numero_cuenta
    else:
        return None

