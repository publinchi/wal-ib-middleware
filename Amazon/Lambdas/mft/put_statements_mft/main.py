import os
import logging
import boto3 
import paramiko

# Create an S3 client 
s3 = boto3.client('s3')
logger = None 
facade = {}

def main_put_statements_mft(event, context):
    # TODO implement
    logger = logging.getLogger()
    logger.setLevel(logging.INFO)
    
    facade["EVENT"] = event
    facade["LOGGER"] = logger
    
    logger.info("event: %s", event)
    
    # Bucket and object key
    bucket_name = os.environ['bucket_name']
    file_key = event['Records'][0]['s3']['object']['key']
    
    if 'DOCK' in file_key or 'CORRESPONSALES' in file_key:
        try:
            # Get the object from S3
            response = s3.get_object(Bucket=bucket_name, Key=file_key)
            
            # Read the file content
            file_content = response['Body'].read().decode('utf-8')
            
            user_sftp_server = os.environ['user_sftp_server']
            password_sftp_server = os.environ['password_sftp_server']
            server_name_sftp = os.environ['server_name_sftp']
            
            # Initialize SFTP connection
            sftp = paramiko.SFTPClient.from_transport(
                paramiko.Transport((server_name_sftp, 22))
            )
            sftp.connect(
                username=user_sftp_server,
                password=password_sftp_server
            )
            
            arrayDirectory = file_key.split('/')
            fileName = arrayDirectory[len(arrayDirectory)-1]
            
            prefix_sftp_server = os.environ['prefix_sftp_server']
            
            # Upload the file to the SFTP server
            with sftp.open('/' + prefix_sftp_server + '/' + fileName, 'w') as remote_file:
                remote_file.write(file_content)
            
            # Close the SFTP connection
            sftp.close()
            
            # Optionally, you can return a success response
            return {
                'statusCode': 200,
                'body': "File uploaded successfully"
            }
        except Exception as e:
            print(f"Error uploading file to SFTP server: {e}")
            return {
                'statusCode': 500,
                'body': str(e)
            }
    
    return {
        'statusCode': 200,
        'body': json.dumps('Hello from Lambda!')
    }

