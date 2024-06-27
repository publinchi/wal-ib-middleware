import json
import boto3
import os
from datetime import datetime, timedelta
from dateutil.relativedelta import relativedelta


bucketName = os.environ['bucket_name']
expire_presigned_url = int(os.environ['expire_presigned_url'])
prefix = os.environ['prefix']
s3 = boto3.resource('s3')
bucket = s3.Bucket(bucketName)
s3_client = boto3.client('s3')

def lambda_handler(event, context):

    file = ' '    
    if "body" in event:
        file = json.loads(event["body"])["statementFileName"]
        
    if file.strip() == '':
        body = {
            "success": False,
            "response": {"code": 400566, "message": "statementFileName is empty"}
        }
        
        body = json.dumps(body)
        return {
            "statusCode": 200,
            "body": body
        } 
     
    objectlist = []
    
    for object in bucket.objects.filter(Prefix=prefix):
        if object.key[len(object.key)-1] != '/':
            arrayDirectory = object.key.split('/')
            accountFile = arrayDirectory[len(arrayDirectory)-1].split('-')
            accountFile = accountFile[len(accountFile)-2]
            fileName = arrayDirectory[len(arrayDirectory)-1]
            
            if fileName == file :
                presigned_url = s3_client.generate_presigned_url(
                    'get_object',
                    Params= {'Bucket': bucketName, 'Key': object.key},
                    ExpiresIn=expire_presigned_url
                )
                dateRegistered = object.last_modified
                validTill = datetime.today() + timedelta(seconds=expire_presigned_url)
                objectlist.append({"fileName":fileName, "dateRegistered": dateRegistered.strftime("%Y-%m-%d %H:%M:%S"), "link": presigned_url, "validTill": validTill.strftime("%Y-%m-%d %H:%M:%S")})
          
    if len(objectlist) == 0:
        body = {
            "success": False,
            "response": {"code": 400563, "message": "the statement file does not exist"}
        }
        
        body = json.dumps(body)
        return {
            "statusCode": 200,
            "body": body
        } 

        
    body = {
        "success": True,
        "response": {"code": 0, "message": "Success"},
        "statement": objectlist[0]
    }  
    
    body = json.dumps(body)
    
    return {
        "statusCode": 200,
        "body": body
    }