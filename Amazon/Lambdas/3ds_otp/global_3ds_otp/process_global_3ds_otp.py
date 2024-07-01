import json
import os
import logging
 
from cobis.cobisdbconnect import execute_database


def process_global_3ds_otp(facade, context):
    logger = facade["LOGGER"] 
    request = facade["REQUEST"]
    
    logger.info("request 3ds otp: %s", request)
    
    event_date = ''
    trace_id = ''
    if 'properties' in request and 'event_date' in request['properties']:
        event_date = request["properties"]["event_date"]
        
    if 'properties' in request and 'trace_id' in request['properties']:
        trace_id = request["properties"]["trace_id"]

    try:
        consult = os.getenv('QUERY_GLOBAL_3DS_OTP')
        consult = consult % (
            request["event_name"], 
            request["event_type"],   
            request["payload"]["card_id"],
            request["payload"]["transaction_id"],
            request["payload"]["otp_value"],
            event_date,
            trace_id)
            
        resultado = execute_database(consult, context)
        resultado =  json.loads(resultado)
        resultado = [entry for entry in resultado if "records_affected" not in entry]
        resultado = resultado[0]
        if resultado['status'] == 'success':
            facade['STATUS'] = True
        else:
            facade['STATUS'] = False
        
        
    
    except Exception as e:
        # Log the exception
        logger.error("An error occurred while processing global 3DS OTP: %s", str(e))
        # Handle the exception gracefully, for example, returning an error response
        facade['STATUS'] = False
