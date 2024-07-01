import json  
import logging
 
logger = None 
facade = {}      

from process_global_3ds_otp import process_global_3ds_otp

def main_global_3ds_otp(event, context):
    print(event['body'])         
    body = json.loads(event["body"])
    logger = logging.getLogger()
    logger.setLevel(logging.INFO)
    
    facade["EVENT"] = event
    facade["LOGGER"] = logger
    facade['REQUEST'] = body
    
    process_global_3ds_otp(facade, context)
    
    status = facade['STATUS']
    
    if status:
        statusCode = 200
    else:
        statusCode = 500
    
    return {
        'statusCode': statusCode
    }
