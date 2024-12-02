import json
import os
import logging
from cobis.cobisdbconnect import execute_database

def replace_null_with_empty_string(data):
    """Recursively replace None and 'null' values in a dictionary or list with an empty string."""
    if isinstance(data, dict):
        return {k: replace_null_with_empty_string(v) for k, v in data.items()}
    elif isinstance(data, list):
        return [replace_null_with_empty_string(item) for item in data]
    elif data is None or data == "null":
        return ""
    else:
        return data

def get_event_contract(facade, context):
    logger = facade["LOGGER"]
    
    try:
        # Fetch queries from environment variables
        query_event_contract = os.getenv('QUERY_READ_EVENT_CONTRACT')
        query_template = os.getenv('QUERY_TEMPLATE')
        
        # Execute first database query
        event_contract_results = execute_database(query_event_contract, context)
        logger.info("Executed query 1: %s", query_event_contract)
        logger.info("Result 1: %s", event_contract_results)
        
        # Execute second database query
        template_results = execute_database(query_template, context)
        logger.info("Executed query 2: %s", query_template)
        logger.info("Result 2: %s", template_results)
        
        # Load and filter results
        event_contract_results = json.loads(event_contract_results)
        template_results = json.loads(template_results)
        
        event_contract_results = replace_null_with_empty_string(
            [entry for entry in event_contract_results if "records_affected" not in entry]
        )
        template_results = replace_null_with_empty_string(
            [entry for entry in template_results if "records_affected" not in entry]
        )
        
        # Filter out items with empty strings for ec_clabe, ec_mail, or ec_name
        #event_contract_results = [
            #entry for entry in event_contract_results 
            #if entry.get('ec_clabe') != "" and entry.get('ec_mail') != "" and entry.get('ec_name') != ""
        #]
        

        for event_contract in event_contract_results:
            # Prepare and execute general data query
            query_general_data = os.getenv('QUERY_DATOS_GENERALES') % (event_contract['ec_external_customer'])
            general_data_results = execute_database(query_general_data, context)
            logger.info("Executed query 3: %s", query_general_data)
            logger.info("Result 3: %s", general_data_results)
            
            general_data_results = json.loads(general_data_results)
            general_data_results = replace_null_with_empty_string(
                [entry for entry in general_data_results if "records_affected" not in entry]
            )
            

            
            if general_data_results:
                general_data = general_data_results[0]
                
                # Construct full address
                address_elements = [
                    general_data['di_calle'],
                    str(general_data['di_casa']),
                    str(general_data['di_edificio']),
                    general_data['pq_descripcion'],
                    str(general_data['cp_codigo']),
                    general_data['ci_descripcion'],
                    general_data['pv_descripcion']
                ]
                
                full_address = ', '.join(address_elements) 
                general_data['fullAddress'] = full_address
                
                query_beneficiarios = os.getenv('QUERY_BENEFICIARIOS') % (event_contract['ec_external_customer'])
                results_beneficiarios = execute_database(query_beneficiarios, context)
                results_beneficiarios= json.loads(results_beneficiarios)
                general_data['beneficiarios']=results_beneficiarios
                
                # Truncate geolocation data if it exists
                if general_data.get('dg_geolocalizacion'):
                    general_data['dg_geolocalizacion'] = general_data['dg_geolocalizacion'][:150]
                
                # Remove unnecessary keys
                keys_to_remove = ["di_calle", "di_casa", "pq_descripcion", "ci_descripcion", "cp_codigo", "pv_descripcion"]
                for key in keys_to_remove:
                    general_data.pop(key, None)
                
                # Update event contract with general data
                event_contract.update(general_data)
                
                
            query_u_event_contract = os.getenv('QUERY_U_RETRY_CONTRACT') % (event_contract['ec_external_customer'])
            logger.info("Executed query 4: %s", query_u_event_contract)
            execute_database(query_u_event_contract, context)
        
        # Construct response body
        body = {
            "sp_bv_event_contract": event_contract_results,
            "dato_plantilla_contract": template_results
        }
        
        # Update facade with response body
        facade['BODY'] = json.dumps(body)
        
        
    except Exception as e:
        logger.error("Error in get_event_contract: %s", str(e))
        raise
