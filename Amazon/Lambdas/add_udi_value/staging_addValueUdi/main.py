import json
import os
import logging
from datetime import datetime as dt
import datetime

logger = None 
facade = {}

from get_udi_value import get_udi_value
from add_udi_value import add_udi_value

def main_staging_add_value_udi(event, context):

    logger = logging.getLogger()
    logger.setLevel(logging.INFO)
    
    facade["EVENT"] = event
    facade["LOGGER"] = logger
    #facade["CURRENT_DATE_FORMAT_1"] = (dt.today()-datetime.timedelta(days=1)).strftime('%Y-%m-%d')
    #facade["CURRENT_DATE_FORMAT_2"] = (dt.today()-datetime.timedelta(days=1)).strftime('%m-%d-%Y')
    facade["CURRENT_DATE_FORMAT_1"] = (dt.today().strftime('%Y-%m-%d'))
    facade["CURRENT_DATE_FORMAT_2"] = (dt.today().strftime('%Y-%m-%d'))
    #facade["CURRENT_DATE_FORMAT_1"] = dt.today().strftime('%Y-%m-%d')
    #facade["CURRENT_DATE_FORMAT_2"] = dt.today().strftime('%m-%d-%Y')
    facade["CONNECTION"] = json.loads(os.environ['CONNECTION_STAGING'])  

    get_udi_value(facade, context)
    
    add_udi_value(facade, context)

    return {
        'statusCode': 200
    }