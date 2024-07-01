import json
import os
import logging    
from cobis.cobisdbconnect import execute_database

def event_confirmation(facade, context):
    logger = facade["LOGGER"]
    event = facade["EVENT"]
    body = json.loads(event["body"])
    externalCustomerId = body["externalCustomerId"]
    typeFile = body["typeFile"]
    
    logger.info(f"the type file is: {typeFile}")
    
    if typeFile == 'CONTRATO' or typeFile == 'DATOS CLIENTE':
    
        consulta = os.getenv('QUERY_READ_EVENT_CONFIRMATION')
        consulta = consulta % (externalCustomerId, typeFile)
        resultado = execute_database(consulta, context)
        
        logger.info(f"updated successfully")
        
        facade['BODY'] = {
            "status": 'success',
        }
    else:
        logger.info(f"failed updating")
        facade['BODY'] = {
            "status": 'failed',
        }