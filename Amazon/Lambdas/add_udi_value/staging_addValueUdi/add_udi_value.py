import json
import os
import logging

from cobis.cobisdbmssql import Cobisdmssql
from cobis.dbinteractiontype import DBInteractionType

from cobis.mssqldbtype import DBstr
from cobis.mssqldbtype import DBdate
from cobis.mssqldbtype import DBint
from cobis.mssqldbtype import DBnumeric
from cobis.mssqldbtype import DBdatetime

def add_udi_value(facade, context):
    logger = facade["LOGGER"]

    value_udi = facade["VALUE_UDI"]
    currentDate = facade["CURRENT_DATE_FORMAT_2"]
    
    connection = facade["CONNECTION"]
    
    conexion = Cobisdmssql(connection, context)
    conexion.Connect()

    #UDIS
    conexion.CommandType(3)
    s_user=DBstr("admuser")
    s_ofi=DBint(1)
    t_trn=DBint(6141)   
    i_operacion=DBstr("I")
    i_fecha=DBstr(currentDate)
    i_moneda=DBint(7)
    i_valor=DBnumeric(value_udi)
    i_empresa=DBint(1)
    i_compra=DBnumeric(value_udi)
    i_venta=DBnumeric(value_udi)
    
    params = (DBint(123), DBdatetime(None), s_user, DBstr(None), DBstr(None), DBint(None), s_ofi, DBstr(None), t_trn, 
        DBstr('N'), DBstr(None), DBstr(None), i_operacion, DBint(None), i_fecha, i_moneda, i_valor, i_empresa, 
        DBdatetime(None), i_compra, i_venta, DBnumeric(None), DBnumeric(None), DBnumeric(None), DBnumeric(None), 
        DBint(1), DBint(None), DBnumeric(1.00), DBnumeric(1.00))
        
    sql_command = conexion.query_conform("cob_conta..sp_cotizacion", params)
    
    conexion.CommandText(sql_command)

    return_data = conexion.Execute()
    conexion.commit_deferred_updates()

    logger.info(str(return_data))
    
    #facade["RESPONSE_SQL"] = return_data
    conexion.Reset()