import base64
import os 

def main_global_3ds_otp_auth(event, context):
    # Get the authorization token
    auth_token = event.get('authorizationToken')
    if not auth_token or not auth_token.startswith('Basic'):
        return generate_policy('user', 'Deny', event['methodArn'])

    # Decode the base64 encoded username:password
    encoded_credentials = auth_token.split(' ')[1]
    try:
        decoded_credentials = base64.b64decode(encoded_credentials).decode('utf-8')
        username, password = decoded_credentials.split(':')
    except (ValueError, UnicodeDecodeError):
        return generate_policy('user', 'Deny', event['methodArn'])

    # Validate credentials
    if validate_credentials(username, password):
        return generate_policy(username, 'Allow', event['methodArn'])
    else:
        return generate_policy(username, 'Deny', event['methodArn'])

def validate_credentials(username, password):
    # Replace with your actual username and password
    valid_username = os.getenv('USERNAME')
    valid_password = os.getenv('PASSWORD')
    return username == valid_username and password == valid_password

def generate_policy(principal_id, effect, resource):
    auth_response = {}
    auth_response['principalId'] = principal_id
    if effect and resource:
        policy_document = {}
        policy_document['Version'] = '2012-10-17'
        policy_document['Statement'] = []
        statement = {}
        statement['Action'] = 'execute-api:Invoke'
        statement['Effect'] = effect
        statement['Resource'] = resource
        policy_document['Statement'].append(statement)
        auth_response['policyDocument'] = policy_document
    return auth_response
