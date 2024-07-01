import os
import boto3
import json

sqs = boto3.client('sqs')

def get_event_from_queue(facade, context):
    queue_name = os.environ["QUEUE_NAME"]
    event= facade["EVENT"]
    logger = facade["LOGGER"]
    
    response = sqs.get_queue_url(QueueName=queue_name)
    queue_url = response['QueueUrl']
    
    facade["DATA_JSON_QUEUE"] = []
    
    for record in event['Records']:
        # Obtener el cuerpo del mensaje
        message_body = json.loads(record['body'])
        logger.info("Mensaje recibido: " + json.dumps(message_body))
 
        # Aquí puedes realizar otras operaciones según sea necesario
 
        # Por ejemplo, eliminar el mensaje de la cola después de procesarlo
        receipt_handle = record['receiptHandle']
        sqs.delete_message(QueueUrl=queue_url, ReceiptHandle=receipt_handle)
        
        facade["DATA_JSON_QUEUE"].append(message_body["detail"])