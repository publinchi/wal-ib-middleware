import json
import os
import logging

logger = None 
facade = {}

from event_confirmation import event_confirmation
  
def main_staging_event_contract_confirmation(event, context):

    logger = logging.getLogger()
    logger.setLevel(logging.INFO)
    
    facade["EVENT"] = event
    facade["LOGGER"] = logger
    
    event_confirmation(facade, context)

    return {
        'statusCode': 200,
        'body': json.dumps(facade['BODY'])
    }