import json
import os
import logging
import boto3
from botocore.exceptions import ClientError 

def send_mail_with_pdf(facade, context):
  logger = facade["LOGGER"]
  logger.info("Sending email with PDF link...")
  subject = "CONTRACT"

  

  recipient = "jolmossolis@hotmail.com"
  attachment_key = facade['KEY_S3_PDF']  # S3 key of the uploaded PDF file (not used)

  attachment_link = generate_presigned_url(attachment_key, logger)
  
  # Email body content in HTML format
  body_html = f"""
  Hi customer,
  
  There is your contract available for download:
  {attachment_link}
  """


  if attachment_link:
    response = send_email_with_link(subject, body_html, recipient, attachment_link, logger)

    if response:
      facade['BODY'] = {
        "status": 'success',
      }
    else:
      facade['BODY'] = {
        "status": 'failed',
      }
  else:
    facade['BODY'] = {
      "status": 'failed',
    }


def generate_presigned_url(key, logger):
  s3_client = boto3.client('s3')
  try:
    url = s3_client.generate_presigned_url('get_object', Params={'Bucket': os.getenv('BUCKET_NAME'), 'Key': key}, ExpiresIn=604800)  # URL expires in less than a week
    logger.info(f"Presigned URL generated: {url}")
    return url
  except ClientError as e:
    logger.error(f"Failed to generate presigned URL: {e}")
    return None


def send_email_with_link(subject, body_html, recipient, attachment_link, logger):
  logger.info("Creating SES client...")
  # Create an SES client
  ses_client = boto3.client('ses')

  # Specify the sender's email address
  sender = "Nelson.Jimenez@cobistopaz.com"

  logger.info("Creating email message...")
  # Create the email message with HTML content
  message = f"Subject: {subject}\nFrom: {sender}\nTo: {recipient}\n\n{body_html}"  # Use body_html directly

  logger.info("Sending email...")
  try:
    # Send the email with link in HTML body
    response = ses_client.send_raw_email(
      Source=sender,
      Destinations=[recipient],
      RawMessage={'Data': message.encode('utf-8')}  # Encode message as UTF-8
    )
    logger.info(f"Email sent! Message ID: {response['MessageId']}")
    return response
  except ClientError as e:
    logger.error(f"Error sending email: {e}")
    return None
  # You might want to handle the failure case here
