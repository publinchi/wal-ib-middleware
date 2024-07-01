import json
import os
import logging

logger = None 
facade = {}

from get_dollar_value import get_dollar_value
from add_dollar_value import add_dollar_value
 
def main_staging_add_value_dollar(event, context):

    logger = logging.getLogger()
    logger.setLevel(logging.INFO)
    
    facade["EVENT"] = event
    facade["LOGGER"] = logger

    get_dollar_value(facade, context)
    
    add_dollar_value(facade, context)

    return {
        'statusCode': 200
    }