import json
import os
import logging
import http.client


def get_udi_value(facade, context):
    logger = facade["LOGGER"]
    currentDate = facade["CURRENT_DATE_FORMAT_1"]
    url = "/SieAPIRest/service/v1/series/SP68257/datos/" + currentDate + "/" + currentDate
    
    
    conn = http.client.HTTPSConnection("www.banxico.org.mx")
    payload = ''
    headers = {
      'Bmx-Token': '102121ff4b454462b082f18329148ba338b10bcab8179d3f376c695501877fd2',
      'Cookie': 'TS012f422b=01ab44a5a861a72d46049e813b34ff72d5908b5cb68c04b05964836d04113ba6b876758acd32896402b728223dccaa5c7abb93b88d'
    }
    conn.request("GET", url, payload, headers)
    res = conn.getresponse()
    data = res.read()
    logger.info("body get udi: " + data.decode("utf-8"))
    data =  json.loads((data.decode("utf-8")))
    data = data["bmx"]["series"][0]["datos"][0]["dato"]
    
    print(data)
    
    facade["VALUE_UDI"] = float(data)