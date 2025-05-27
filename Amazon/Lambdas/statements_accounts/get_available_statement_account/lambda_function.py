import json
import boto3
import os
from datetime import datetime
from dateutil.relativedelta import relativedelta

bucketName = os.environ['bucket_name']
prefix = os.environ['prefix']
s3 = boto3.resource('s3')
bucket = s3.Bucket(bucketName)

def create_response(success, code, message, availableStatements=None):
    body = {
        "success": success,
        "response": {"code": code, "message": message}
    }
    if availableStatements is not None:
        body["availableStatements"] = availableStatements
    return {
        "statusCode": 200,
        "body": json.dumps(body)
    }

def lambda_handler(event, context):
    cta = json.loads(event.get("body", "{}")).get("accountNumber", "").strip()
    if not cta:
        return create_response(False, 400565, "accountNumber is empty")
    if not cta.isnumeric():
        return create_response(False, 400564, "accountNumber format is invalid")
    try:
        numberOfStatements = int(event["queryStringParameters"]['numberOfStatements'])
    except:
        return create_response(False, 400560, "numberOfStatements format is invalid")
    if not (1 <= numberOfStatements <= 12):
        return create_response(False, 400561, "numberOfStatements must be between 1 and 12")
    
    objectlist = []
    dateToday = datetime.today()
    
    # Crear un prefijo más específico para filtrar los objetos por número de cuenta
    accountPrefix = f"{prefix}{cta}-"
    
    for obj in bucket.objects.filter(Prefix=accountPrefix):
        if obj.key.endswith('.pdf'):
            fileName = obj.key.split('/')[-1]
            accountFile, dateStr = fileName.split('-')
            
            try:
                dateFile = datetime.strptime(dateStr[:6], '%Y%m')
            except ValueError:
                print(f"Invalid date format in file name: {fileName}")
                continue
            
            delta = relativedelta(dateToday, dateFile)
            res_months = delta.months + (delta.years * 12)
            
            if res_months > 0 and res_months <= 12:
                objectlist.append({
                    "availableStatement": {
                        "fileName": fileName,
                        "date": dateFile.strftime("%Y-%m-%d %H:%M:%S"),
                        "dateRegistered": obj.last_modified.strftime("%Y-%m-%d %H:%M:%S")
                    }
                })
    
    if not objectlist:
        return create_response(False, 400562, "there are no available statements")
    
    length = len(objectlist)
    start_index = max(0, length - numberOfStatements)
    
    return create_response(True, 0, "Success", objectlist[start_index:length])
