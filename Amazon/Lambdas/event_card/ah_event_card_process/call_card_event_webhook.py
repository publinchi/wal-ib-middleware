import http.client
import json
import boto3
import io  
import gzip
import copy
from datetime import datetime

lambda_client = boto3.client('lambda')
 
def call_card_event_webhook(facade, context):
    print("llegue webhook")
    
    logger = facade["LOGGER"]
    get_key(facade)
    #logger.info("RESPONSE_JSON_SIGNATURE: " + facade["RESPONSE_JSON_SIGNATURE"]["key"])
    data_json_event_array = facade["DATA_JSON_QUEUE"]
    url = "/api-proxy/service/banking/customer-service/v3/customer/account/card/status"
    
    # Create a copy of the original data_json_event_array
    data_json_queue_with_pairs = copy.deepcopy(data_json_event_array)
    
    for data_json_event in data_json_event_array: 
    
        if 'retry' in data_json_event:     
             del data_json_event['retry']
             
        if 'creation_date' in data_json_event:     
             del data_json_event['creation_date']
             
        if 'card' in data_json_event:     
            
            del data_json_event['card']['statusReason']
            
            if 'type' in data_json_event['card']:  
                if data_json_event['card']['type'] == 'VIRTUAL':
                    data_json_event['card']['type'] = 'DIGITAL'
            
                if data_json_event['card']['type'] == 'PHYSICAL':
                    if 'expiryDate' in data_json_event['card']:
                        del data_json_event['card']['expiryDate']
            
            if data_json_event["eventType"] != "CARD_CREATED" and data_json_event["eventType"] != "CARD_ACTIVATED":
                if 'expiryDate' in data_json_event['card']:
                    del data_json_event['card']['expiryDate']
                if 'maskedCardNumber' in data_json_event['card']:   
                    del data_json_event['card']['maskedCardNumber']
             
            if data_json_event["eventType"] == "CARD_CREATED" or data_json_event["eventType"] == "CARD_ACTIVATED":
                if 'expiryDate' in data_json_event['card']:
                    #logger.info("fecha: " + data_json_event['card']['expiryDate'])
                    expiryDate = data_json_event['card']['expiryDate']
                    fecha_obj = datetime.strptime(expiryDate, "%Y-%m-%d %H:%M:%S")
                    data_json_event['card']['expiryDate'] = fecha_obj.strftime('%Y-%m-%dT%H:%M:%SZ')
                    #logger.info("fecha iso: " + data_json_event['card']['expiryDate'])
                    
            if data_json_event["eventType"] == "CARD_CREATED" and data_json_event['card']['type'] == 'PHYSICAL':
                if 'externalCustomerId' in data_json_event:
                    del data_json_event['externalCustomerId']
                if 'accountNumber' in data_json_event:
                    del data_json_event['accountNumber']
                
             
        print("llegue webhook 2")
             
        conn = http.client.HTTPSConnection("developer.api.us.stg.walmart.com")
        payload = json.dumps(data_json_event)
        headers = {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'accept-language': 'es',
            'WM_QOS.CORRELATION_ID': 'e393ea37-f689-46a5-920b-9a0cd51424af',
            'WM_SVC.NAME': 'banking-customer-service',
            'WM_CONSUMER.ID': 'e393ea37-f689-46a5-920b-9a0cd51424af',
            'WM_SEC.AUTH_SIGNATURE': facade["RESPONSE_JSON_SIGNATURE"]["key"],
            'WM_CONSUMER.INTIMESTAMP': facade["RESPONSE_JSON_SIGNATURE"]["timestamp"],
            'WM_SEC.KEY_VERSION': '1'
        }
        
        timeout = 1
        conn.timeout = timeout 
        
        print("llegue webhook 3")
        
        conn.request("POST", url, payload, headers)
        res = conn.getresponse()
        response_data = res.read()
        
        if res.status == 200:
            success = "Success"
        else:
            success = "Failed"

        # Check if the response data is gzipped
        if res.getheader('Content-Encoding') == 'gzip':
            buffer = io.BytesIO(response_data)
            with gzip.GzipFile(fileobj=buffer, mode='rb') as f:
                response_data = f.read()
        
        # Log the response data
        logger.info("Response weebhook: " + response_data.decode("utf-8"))
        
        # Parse the response JSON
        response_json = json.loads(response_data.decode("utf-8"))
        
        # Store the request and response pair
        request_response_pair = {
            "request": data_json_event,
            "response": response_json,
            "success": success,
            "status": res.status
        }
        
        # Add request-response pair to the corresponding element in the copied array
        index = data_json_event_array.index(data_json_event)
        data_json_queue_with_pairs[index]["REQUEST_RESPONSE_PAIRS"] = request_response_pair
    
    # Assign the modified array with request-response pairs to the facade
    facade["DATA_JSON_QUEUE_WITH_PAIRS"] = data_json_queue_with_pairs
    
def get_key(facade):
    response = lambda_client.invoke(
        FunctionName="generate_signature_event_card",
        InvocationType='RequestResponse',  # or 'Event' for asynchronous invocation
        Payload=''
    )
    
    response_payload = json.load(response['Payload'])
    facade["RESPONSE_JSON_SIGNATURE"] = response_payload
