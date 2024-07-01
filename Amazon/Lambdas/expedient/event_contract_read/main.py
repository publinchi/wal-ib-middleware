import json
import os
import logging

logger = None 
facade = {}

from get_event_contract import get_event_contract
  
def main_staging_event_contract_read(event, context):

    logger = logging.getLogger()
    logger.setLevel(logging.INFO)
    
    facade["EVENT"] = event
    facade["LOGGER"] = logger

    get_event_contract(facade, context)

    return {
        'statusCode': 200,
        'body': facade['BODY']
    }