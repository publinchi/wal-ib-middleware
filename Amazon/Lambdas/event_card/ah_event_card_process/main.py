import json
import logging

from get_event_from_queue import get_event_from_queue
from call_card_event_webhook import call_card_event_webhook
from event_card_process_sp import event_card_process_sp

logger = None 
facade = {}

def main_ah_event_card_process(event, context):
    # TODO implement
    logger = logging.getLogger()
    logger.setLevel(logging.INFO)
    
    facade["EVENT"] = event
    facade["LOGGER"] = logger
    
    get_event_from_queue(facade, context)
    
    call_card_event_webhook(facade, context)
    
    event_card_process_sp(facade, context)
    
    return {
        'statusCode': 200,
        'body': json.dumps('Hello from Lambda!')
    }
