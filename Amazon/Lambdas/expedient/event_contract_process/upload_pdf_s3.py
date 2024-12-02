import logging
import boto3
import base64
import json
import os

s3 = boto3.client('s3')


def upload_pdf_s3(facade, context):
    logger = facade["LOGGER"]
    event = facade["EVENT"]
    body = json.loads(event["body"])
    blob_pdf = body["blobPdf"]
    external_customer_id = str(body["externalCustomerId"])  # Ensure it's a string for folder creation
    typeFile = body["typeFile"]
    provider = body.get('provider')

    if provider == None:
        provider="KARPAY"

    decoded_blob_data = base64.b64decode(blob_pdf)

    try:
        # Define the S3 bucket name from environment variable
        bucket_name = os.getenv('BUCKET_NAME')

        # Create folder path and key for the PDF file
        folder_name = f"files/{external_customer_id}"  # Include parent folder
        counter = 1
        key = f"{folder_name}/{typeFile}_{provider}_{counter}.pdf"
        facade['KEY_S3_PDF'] = key
        exists = s3.list_objects_v2(Bucket=bucket_name, Prefix=folder_name)
        
        while s3.list_objects_v2(Bucket=bucket_name, Prefix=key).get('Contents'):
            counter += 1
            key = f"{folder_name}/{typeFile}_{provider}_{counter}.pdf"


        # Check if folder exists (avoiding unnecessary calls)
        
        if not exists.get('Contents'):
            # Create the folder if it doesn't exist, including the parent folder ("files")
            s3.put_object(Bucket=bucket_name, Key=f"{folder_name}/", Body=b"")  # Empty object for parent folder
            logger.info(f"Folder created: {folder_name}")

        # Upload the PDF file to S3
        s3.put_object(Bucket=bucket_name, Key=key, Body=decoded_blob_data)
        logger.info(f"PDF file uploaded successfully to S3: {key}")

        body = {
            "status": "success",
        }

    except Exception as e:
        logger.error(f"Error uploading PDF file to S3: {e}")
        body = {
            "status": "failed",
        }

    facade['BODY'] = body
