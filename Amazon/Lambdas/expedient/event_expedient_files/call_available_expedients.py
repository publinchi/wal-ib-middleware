import json
import os
import logging
import http.client
import boto3  
import io
import gzip
import copy
import urllib.parse
from cobis.cobisdbconnect import execute_database

lambda_client = boto3.client('lambda')
s3 = boto3.client('s3')

def call_available_expedients(facade, context):
    logger = facade["LOGGER"]
    
    event_expedient_files = facade['EVENT_EXPEDIENT_FILES']
    
    for event_expedient_file in event_expedient_files:
        customer_id = str(event_expedient_file['ef_external_customer'])
        ef_inea = event_expedient_file['ef_inea']
        ef_iner = event_expedient_file['ef_iner']
        ef_dom1 = event_expedient_file['ef_dom1']
        ef_dom2 = event_expedient_file['ef_dom2']
        ef_retry = event_expedient_file['ef_retry']
        
        consulta = os.getenv('QUERY_UPDATE_EVENT_EXPEDIENT_FILES_RETRY')
                    
        consulta = consulta % (customer_id, ef_retry)
                    
        resultado = execute_database(consulta, context)
        logger.info(f"Database updated for customer_id {customer_id} with QUERY_UPDATE_EVENT_EXPEDIENT_FILES_RETRY")
        
        get_key(facade)
        
        base_url = os.getenv("URL_IDC_GET_LIST_BASE")
        conn = http.client.HTTPSConnection(base_url)
        payload = ''
        headers = {
            'WM_CONSUMER.ID': os.getenv('CONSUMER_ID'),
            'WM_SEC.AUTH_SIGNATURE': facade["RESPONSE_JSON_SIGNATURE"]["key"],
            'WM_CONSUMER.INTIMESTAMP': facade["RESPONSE_JSON_SIGNATURE"]["timestamp"]
        }
        
        url_path = os.getenv("URL_IDC_GET_LIST") + customer_id
        full_url = f"https://{base_url}{url_path}"
        
        try:
            conn.request("GET", url_path, payload, headers)
            res = conn.getresponse()
            response_data = res.read()
            
            logger.info(f"customer_id: {customer_id}")
            
            if res.status == 200:
                
                # Check if the response data is gzipped
                if res.getheader('Content-Encoding') == 'gzip':
                    buffer = io.BytesIO(response_data)
                    with gzip.GzipFile(fileobj=buffer, mode='rb') as f:
                        response_data = f.read()
                
                # Parse the response JSON
                response_json = json.loads(response_data.decode("utf-8"))
                
                # Extract and filter documents with kycStatus as "SUCCESS"
                documents = response_json.get("documents", [])
                
                logger.info(f"Documents: {documents}")
                
                success_documents = [doc for doc in documents if doc.get("kycStatus") == "SUCCESS"]
                
                # Add customerId and headers to each document in success_documents
                response_headers = {
                    'sv': res.getheader('sv'),
                    'sr': res.getheader('sr'),
                    'st': res.getheader('st'),
                    'se': res.getheader('se'),
                    'sp': res.getheader('sp'),
                    'sig': res.getheader('sig'),
                    'xyz': res.getheader('xyz')
                }
                
                # Store filtered documents in a new variable
                facade["SUCCESS_DOCUMENTS"] = success_documents
                
                # Log the response data and filtered documents
                logger.info(f"Filtered success documents: {success_documents}")
                
                # Make additional GET requests for each document's path
                for doc in success_documents:
                    
                    path_url = doc['path']
                    
                    parsed_url = urllib.parse.urlparse(path_url)
                    file_name = os.path.basename(parsed_url.path)
                    
                    name, extension = os.path.splitext(file_name)
                    callRequestFile = False
        
                    if name == 'INE_FRONT' and ef_inea == 0:
                        callRequestFile = True
                        
                    if name == 'INE_BACK' and ef_iner == 0:
                        callRequestFile = True
                        
                    if name == 'ADDRESS_PROOF_VIA_INE_FRONT' and ef_dom1 == 0:
                        callRequestFile = True
                        
                    if name == 'ADDRESS_PROOF_MULTI' and ef_dom2 == 0:
                        callRequestFile = True
                        
                    if callRequestFile: 
                        # Log the full URL and headers being used
                        logger.info(f"Making request to: {parsed_url.path}?route=enabled&sv={response_headers['sv']}&sr={response_headers['sr']}&st={response_headers['st']}&se={response_headers['se']}&sp={response_headers['sp']}&sig={response_headers['sig']}&xyz={response_headers['xyz']}")
                        logger.info(f"Request headers: {headers}")
                        
                        conn2 = http.client.HTTPSConnection(os.getenv("URL_IDC_GET_LIST_BASE"))
                        conn2.request("GET", parsed_url.path + f"?route=enabled&sv={response_headers['sv']}&sr={response_headers['sr']}&st={response_headers['st']}&se={response_headers['se']}&sp={response_headers['sp']}&sig={response_headers['sig']}&xyz={response_headers['xyz']}", payload, headers)
                        res2 = conn2.getresponse()
                        additional_response_data = res2.read()
                        
                        # Log the response status and headers
                        logger.info(f"Response status: {res2.status}")
                        logger.info(f"Response headers: {res2.getheaders()}")
                        
                        if res2.status == 200:
                            additional_success = "Success"
                            
                            # Process application/octet-stream content type
                            if res2.getheader('Content-Type') == 'application/octet-stream':
                               upload_to_s3(facade, context, customer_id, file_name, additional_response_data)
                        else:
                            additional_success = "Failed"
                            # Log the response data for further debugging
                            #additional_response_json = json.loads(additional_response_data.decode("utf-8"))
                            #logger.error(f"Failed request to {path_url}, response data: {additional_response_json}")
                            logger.error(f"Failed request to {path_url}, response data:")
            else:
                if res.getheader('Content-Encoding') == 'gzip':
                    buffer = io.BytesIO(response_data)
                    with gzip.GzipFile(fileobj=buffer, mode='rb') as f:
                        response_data = f.read()
                # Log the response data for further debugging
                response_json = json.loads(response_data.decode("utf-8"))
                logger.error(f"status code: {res.status}")
                logger.error(f"Failed request to Get list of all KYC, response data: {response_json}")
                logger.error(f"Full URL: {full_url}")
                logger.error(f"Consumer ID: {os.getenv('CONSUMER_ID')}")
                logger.error(f"Auth Signature: {facade['RESPONSE_JSON_SIGNATURE']['key']}")
                logger.error(f"Timestamp: {facade['RESPONSE_JSON_SIGNATURE']['timestamp']}")
                
                # Check if the response contains the 'errors' key and the specific error code
                if 'errors' in response_json and response_json['errors'][0]['code'] == 'BCS-BA-2004':
                    consulta2 = os.getenv('QUERY_UPDATE_EVENT_EXPEDIENT_FILES_FAILED')
                    
                    consulta2 = consulta2 % (customer_id)
                    
                    resultado = execute_database(consulta2, context)
                    logger.info(f"Database updated for customer_id {customer_id} with QUERY_UPDATE_EVENT_EXPEDIENT_FILES_FAILED")
        
        except Exception as e:
            logger.error(f"Error in HTTP request: {e}")
            logger.error(f"Full URL: {full_url}")
            logger.error(f"Consumer ID: {os.getenv('CONSUMER_ID')}")
            logger.error(f"Auth Signature: {facade['RESPONSE_JSON_SIGNATURE']['key']}")
            logger.error(f"Timestamp: {facade['RESPONSE_JSON_SIGNATURE']['timestamp']}")
            
def get_key(facade):
    logger = facade["LOGGER"]
    response = lambda_client.invoke(
        FunctionName="generate_signature_event_expedient",
        InvocationType='RequestResponse',  # or 'Event' for asynchronous invocation
        Payload=''
    )
    
    response_payload = json.load(response['Payload'])
    logger.info(f"Response generate_signature_event_expedient: {response_payload}")
    facade["RESPONSE_JSON_SIGNATURE"] = response_payload

def upload_to_s3(facade, context, customer_id, file_name, binary_data):
    logger = facade["LOGGER"]
    
    name, extension = os.path.splitext(file_name)
    
    if name == 'INE_FRONT':
        name = 'INE_1'
        
    if name == 'INE_BACK':
        name = 'INE_2'
        
    if name == 'ADDRESS_PROOF_VIA_INE_FRONT':
        name = 'COMPROBANTE_DOMICILIO'
        
    if name == 'ADDRESS_PROOF_MULTI':
        name = 'COMPROBANTE_DOMICILIO_DOM'
        
    file_name = f"{name}{extension}"

    try:
        bucket_name = os.getenv('BUCKET_NAME')
        if not bucket_name:
            raise ValueError("BUCKET_NAME environment variable is not set")

        folder_name = f"files/{customer_id}"
        key = f"{folder_name}/{file_name}"
        facade['KEY_S3_PDF'] = key

        exists = s3.list_objects_v2(Bucket=bucket_name, Prefix=folder_name)
        if not exists.get('Contents'):
            s3.put_object(Bucket=bucket_name, Key=f"{folder_name}/", Body=b"")
            logger.info(f"Customer folder created: {folder_name}")

        s3.put_object(Bucket=bucket_name, Key=key, Body=binary_data)
        logger.info(f"PDF file uploaded successfully to S3: {key}")
        
        update_event_expedient_files(facade, context, customer_id, name)

    except Exception as e:
        logger.error(f"Error uploading PDF file to S3: {e}")
        
def update_event_expedient_files(facade, context, customer_id, type_file):
    logger = facade["LOGGER"]
    
    consulta = os.getenv('QUERY_UPDATE_EVENT_EXPEDIENT_FILES')
    
    consulta = consulta % (customer_id, type_file)
    
    resultado = execute_database(consulta, context)
