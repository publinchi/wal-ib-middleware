import json
import os
import logging   
from cobis.cobisdbconnect import execute_database

def get_event_expedient_files(facade, context):
    logger = facade["LOGGER"]
    consulta = os.getenv('QUERY_READ_EVENT_EXPEDIENT_FILES')
    
    resultado = execute_database(consulta, context)
    
    resultado =  json.loads(resultado)
    
    resultado = [entry for entry in resultado if "records_affected" not in entry]
    
    logger.info(f"Array event_expedient_files: {resultado}")
    
    facade['EVENT_EXPEDIENT_FILES'] = resultado
