import json
import os
import logging

logger = None 
facade = {}

from upload_pdf_s3 import upload_pdf_s3
from send_mail_with_pdf import send_mail_with_pdf

def main_staging_event_contract_process(event, context):
    # TODO implement
    
    logger = logging.getLogger()
    logger.setLevel(logging.INFO)
    
    facade["EVENT"] = event
    facade["LOGGER"] = logger
    
    print(json.loads(event["body"]))
    typeFile = json.loads(event["body"])['typeFile']
    mail = json.loads(event["body"]).get("mail", None)
    
    if typeFile == 'CONTRATO':
        upload_pdf_s3(facade, context)
        
        body = facade['BODY']
            
        #if body['status'] == 'success':
            #send_mail_with_pdf(facade, context)
          
    elif typeFile == 'DATOS CLIENTE':
        upload_pdf_s3(facade, context)
    else:
        set_failed_status(facade)
    
    return {
        'statusCode': 200,
        'body': json.dumps(facade['BODY'])
    }
    
def set_failed_status(facade):
  facade['BODY'] = {
    "status": 'failed',
  }

