from cobis.cobisdbconnect import execute_database
import traceback
import json

def event_card_process_sp(facade, context):
    logger = facade["LOGGER"]
    data_json_queue_with_pairs = facade["DATA_JSON_QUEUE_WITH_PAIRS"] 
    for data_json in data_json_queue_with_pairs: 
        logger.info("REQUEST_RESPONSE_PAIRS: " + json.dumps(data_json))
        
        try:
            #if True:
            if data_json["REQUEST_RESPONSE_PAIRS"]["status"] == 200 or data_json["retry"] >= 3:
              operation = "D"
              # Consulta
              consulta = """
                exec cob_atm..sp_atm_event_card
                  @i_cardId = %s,
                  @i_eventType = %s,
                  @i_ec_date = %s,
                  @i_operation = %s,
                  @i_process_status = %s,
                  @i_request = %s,
                  @i_response = %s
              """
                  
              # Parámetros 
              
              parametros = (data_json["card"]["cardId"], 
                            data_json["eventType"],
                            data_json["creation_date"],
                            operation, 
                            data_json["REQUEST_RESPONSE_PAIRS"]["success"], 
                            json.dumps(data_json["REQUEST_RESPONSE_PAIRS"]["request"]),
                            json.dumps(data_json["REQUEST_RESPONSE_PAIRS"]["response"]))
          
              try:
                # Llama a la función execute_database con la consulta de prueba y parámetros de prueba
                resultado = execute_database(consulta, parametros, context)
                data = json.loads(resultado)
          
              except Exception as e:
                # Maneja cualquier excepción que pueda ocurrir durante la ejecución
                print(f"Error durante la ejecución: {e}")
                
                traceback.print_exc()
            else:
              operation = "R"
              # Consulta
              consulta = """
                exec cob_atm..sp_atm_event_card
                  @i_cardId = %s,
                  @i_eventType = %s,
                  @i_ec_date = %s,
                  @i_retry = %s,
                  @i_operation = %s
              """
                  
              # Parámetros 
              
              parametros = (data_json["card"]["cardId"], 
                            data_json["eventType"],
                            data_json["creation_date"],
                            data_json["retry"],
                            operation)
          
              try:
                # Llama a la función execute_database con la consulta de prueba y parámetros de prueba
                resultado = execute_database(consulta, parametros, context)
                data = json.loads(resultado)
          
              except Exception as e:
                # Maneja cualquier excepción que pueda ocurrir durante la ejecución
                print(f"Error durante la ejecución: {e}")
                
                traceback.print_exc()
        
        except:
            # Este bloque finally se ejecutará siempre, independientemente de si se produce una excepción o no
            pass
            