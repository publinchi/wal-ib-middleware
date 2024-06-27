import json
import os
import logging
import http.client
from datetime import datetime


def get_dollar_value(facade, context):
    logger = facade["LOGGER"]
    url = os.environ['URL']
    
    
    conn = http.client.HTTPSConnection("www.banxico.org.mx")
    payload = ''
    headers = {
      'Bmx-Token': os.environ['BMX_TOKEN'], 
      'Cookie': 'TS012f422b=01ab44a5a861a72d46049e813b34ff72d5908b5cb68c04b05964836d04113ba6b876758acd32896402b728223dccaa5c7abb93b88d'
    }
    conn.request("GET", url, payload, headers)
    print(payload)
    res = conn.getresponse()
    data = res.read()
    logger.info("body get udi: " + data.decode("utf-8"))
    data =  json.loads((data.decode("utf-8")))
    value = data["bmx"]["series"][0]["datos"][0]["dato"]
    date = data["bmx"]["series"][0]["datos"][0]["fecha"]
    #date = datetime.strptime(date, "%d/%m/%Y")

    
    facade["DATE"] = datetime.today().strftime('%Y-%m-%d')
    facade["VALUE_DOLLAR"] = float(value)