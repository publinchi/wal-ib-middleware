import json
import os
import logging

logger = None 
facade = {}

from generate_expedient_link import generate_expedient_link

def main_generate_link_expedient_files(event, context):
    # TODO implement
    logger = logging.getLogger()
    logger.setLevel(logging.INFO)
    
    facade["EVENT"] = event
    facade["LOGGER"] = logger 
    
    generate_expedient_link(facade, context)
    
    return {
        'statusCode': 200,
        'body': json.dumps(facade['BODY'])
    }
