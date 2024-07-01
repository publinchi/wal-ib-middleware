import json
import boto3
import os
from datetime import datetime
from dateutil.relativedelta import relativedelta


bucketName = os.environ['bucket_name']
prefix = os.environ['prefix']
s3 = boto3.resource('s3')
bucket = s3.Bucket(bucketName)

def lambda_handler(event, context):
    
    cta = ' '    
    if "body" in event:
        cta = json.loads(event["body"])["accountNumber"]
        
    if cta.strip() == '':
        body = {
            "success": False,
            "response": {"code": 400565, "message": "accountNumber is empty"}
        }
        
        body = json.dumps(body)
        return {
            "statusCode": 200,
            "body": body
        } 
        
    if not cta.isnumeric():
        body = {
            "success": False,
                "response": {"code": 400564, "message": "accountNumber format is invalid"}
        }
        
        body = json.dumps(body)
        return {
            "statusCode": 200,
            "body": body
        } 
    
    try:    
        numberOfStatements = int(event["queryStringParameters"]['numberOfStatements'])
    except:
        body = {
            "success": False,
            "response": {"code": 400560, "message": "numberOfStatements format is invalid"}
        }
        
        body = json.dumps(body)
        return {
            "statusCode": 200,
            "body": body
        } 
        
    if numberOfStatements < 1 or numberOfStatements > 12:
        body = {
            "success": False,
            "response": {"code": 400561, "message": "numberOfStatements must be between 1 and 12"}
        }
        
        body = json.dumps(body)
        return {
            "statusCode": 200,
            "body": body
        }
            
    objectlist = []
    dateToday = datetime.today()
    
    for object in bucket.objects.filter(Prefix=prefix):
        if object.key[len(object.key)-1] != '/':
            arrayDirectory = object.key.split('/')
            monthFile = arrayDirectory[len(arrayDirectory)-2][4]+arrayDirectory[len(arrayDirectory)-2][5]
            yearFile = arrayDirectory[len(arrayDirectory)-2][0]+arrayDirectory[len(arrayDirectory)-2][1]+arrayDirectory[len(arrayDirectory)-2][2]+arrayDirectory[len(arrayDirectory)-2][3]
            dateFile = datetime.strptime(monthFile + '/01/' + yearFile, '%m/%d/%Y')
            accountFile = arrayDirectory[len(arrayDirectory)-1].split('-')
            accountFile = accountFile[len(accountFile)-2]
            delta = relativedelta(dateToday, dateFile)
            res_months = delta.months + (delta.years * 12)

            #if res_months > -12 and res_months <= 0 and accountFile == cta:
            if accountFile == cta:
                fileName = arrayDirectory[len(arrayDirectory)-1]
                dateRegistered = object.last_modified
                objectlist.append({
                    "availableStatement": {
                        "fileName": fileName,
                        "date": dateFile.strftime("%Y-%m-%d %H:%M:%S"),
                        "dateRegistered": dateRegistered.strftime("%Y-%m-%d %H:%M:%S")
                    }
                })
    
    if len(objectlist) == 0:
        body = {
            "success": False,
            "response": {"code": 400562, "message": "there are no available statements"}
        }
        
        body = json.dumps(body)
        return {
            "statusCode": 200,
            "body": body
        } 

    length = len(objectlist)
    i = length - numberOfStatements
    if numberOfStatements > length:
        i = 0
    
    body = {
        "success": True,
        "response": {"code": 0, "message": "Success"},
        "availableStatements": objectlist[i:length]
    }  
    
    body = json.dumps(body)
    
    return {
        "statusCode": 200,
        "body": body
    } 
