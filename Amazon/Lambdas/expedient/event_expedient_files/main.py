import json
import os
import logging

logger = None 
facade = {} 

from get_event_expedient_files import get_event_expedient_files
from call_available_expedients import call_available_expedients

def main_event_expedient_files(event, context):
    
    logger = logging.getLogger()
    logger.setLevel(logging.INFO)
    
    facade["EVENT"] = event
    facade["LOGGER"] = logger 
    
    
    get_event_expedient_files(facade, context)
    
    call_available_expedients(facade, context)
    
    # TODO implement
    return {
        'statusCode': 200,
        'body': json.dumps('Hello from Lambda!')
    }
