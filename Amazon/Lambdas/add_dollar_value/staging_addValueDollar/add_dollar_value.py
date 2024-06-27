import json
import os
import logging

from cobis.cobisdbconnect import execute_database


def add_dollar_value(facade, context):
    logger = facade["LOGGER"]

    value_dollar = facade["VALUE_DOLLAR"]
    date = facade["DATE"]
    
    consulta = os.getenv('QUERY_ADD_VALUE_DOLLAR')
    consulta = consulta % (date, value_dollar, value_dollar, value_dollar)
    print(consulta)
    resultado = execute_database(consulta, context)
    print(resultado)