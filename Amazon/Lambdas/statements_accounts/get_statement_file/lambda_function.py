import json
import boto3
import os
from datetime import datetime, timedelta

bucketName = os.environ['bucket_name']
expire_presigned_url = int(os.environ['expire_presigned_url'])
prefix = os.environ['prefix']
s3 = boto3.resource('s3')
bucket = s3.Bucket(bucketName)
s3_client = boto3.client('s3')

def create_response(success, code, message, statement=None):
    body = {
        "success": success,
        "response": {"code": code, "message": message}
    }
    if statement:
        body["statement"] = statement
    return {
        "statusCode": 200,
        "body": json.dumps(body)
    }

def lambda_handler(event, context):
    file = ' '    
    if "body" in event:
        file = json.loads(event["body"]).get("statementFileName", "").strip()
        
    if not file:
        return create_response(False, 400566, "statementFileName is empty")
     
    # Buscar directamente usando el prefijo exacto del archivo
    target_key = f"{prefix}{file}"
    try:
        obj = s3.Object(bucketName, target_key)
        obj.load()  # Verificar si el objeto existe
        presigned_url = s3_client.generate_presigned_url(
            'get_object',
            Params={'Bucket': bucketName, 'Key': target_key},
            ExpiresIn=expire_presigned_url
        )
        dateRegistered = obj.last_modified
        validTill = datetime.today() + timedelta(seconds=expire_presigned_url)
        statement = {
            "fileName": file,
            "dateRegistered": dateRegistered.strftime("%Y-%m-%d %H:%M:%S"),
            "link": presigned_url,
            "validTill": validTill.strftime("%Y-%m-%d %H:%M:%S")
        }
        return create_response(True, 0, "Success", statement)

    except s3.meta.client.exceptions.NoSuchKey:
        return create_response(False, 400563, "the statement file does not exist")
