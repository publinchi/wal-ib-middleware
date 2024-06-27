import json
import os
import logging
import boto3
import zipfile
import io
from datetime import datetime
from cobis.cobisdbconnect import execute_database

def generate_expedient_link(facade, context):
    logger = facade["LOGGER"]
    event = facade["EVENT"]
    
    # Initialize S3 client
    s3_client = boto3.client('s3')
    bucket_name = os.environ.get('BUCKET_NAME')

    try:
        # Parse the incoming event body
        body = json.loads(event["body"])
        externalCustomerIds = body["externalCustomerIds"]
        externalCustomerIds = list(set(externalCustomerIds))
        id = body["id"]
        
        logger.info(f"Initial Folders: {externalCustomerIds}")
        
        # Validate existence of each folder and remove non-existing ones
        valid_customer_ids = []
        for customer_id in externalCustomerIds:
            prefix = f"files/{customer_id}/"
            response = s3_client.list_objects_v2(Bucket=bucket_name, Prefix=prefix)
            if 'Contents' in response:
                valid_customer_ids.append(customer_id)
            else:
                logger.info(f"Folder does not exist for customer ID: {customer_id}")
        
        #if not valid_customer_ids:
            #facade['BODY'] = {"status": 'false'}
            #return
        
        logger.info(f"Valid Folders: {valid_customer_ids}")
        
        # Create a zip file in memory
        zip_buffer = io.BytesIO()
        with zipfile.ZipFile(zip_buffer, 'w', zipfile.ZIP_DEFLATED) as zip_file:
            for customer_id in valid_customer_ids:
                prefix = f"files/{customer_id}/"
                response = s3_client.list_objects_v2(Bucket=bucket_name, Prefix=prefix)
                for obj in response.get('Contents', []):
                    file_key = obj['Key']
                    file_obj = s3_client.get_object(Bucket=bucket_name, Key=file_key)
                    file_data = file_obj['Body'].read()
                    zip_file.writestr(file_key, file_data)
        
        zip_buffer.seek(0)
        
        # Generate the zip file name
        date_str = datetime.now().strftime("%y%m%d")
        zip_key = f'files/files_{date_str}{id}.zip'
        
        # Upload the zip file to S3
        s3_client.upload_fileobj(zip_buffer, bucket_name, zip_key)
        
        # Generate a pre-signed URL that expires in one week
        url = s3_client.generate_presigned_url(
            'get_object',
            Params={'Bucket': bucket_name, 'Key': zip_key},
            ExpiresIn=604800  # 1 week in seconds
        ) 
        
        #consulta = f"update cobis..cl_expediente set ex_url_descarga = '{url}', ex_estado = 'V' where ex_id={id}"
    
        #resultado = execute_database(consulta, context)
        
        facade['BODY'] = {"status": 'success', "url": url}
    
    except Exception as e:
        logger.error(f"Error generating expedient link: {str(e)}")
        facade['BODY'] = {"status": 'false'}

