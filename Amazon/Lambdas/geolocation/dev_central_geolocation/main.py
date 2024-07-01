from cobis.cobisdbconnect import execute_database
import json
import os
import boto3
import re


def main_geolocation(event, context):
    idPais = 236#mex
    provincias = [
        {"acronimo": "AGS", "id_sepomex": "AS", "provincia": "Aguascalientes", "idEstado": 1},
        {"acronimo": "BC", "id_sepomex": "BC", "provincia": "Baja California", "idEstado": 2},
        {"acronimo": "BCS", "id_sepomex": "BS", "provincia": "Baja California Sur", "idEstado": 3},
        {"acronimo": "CAMP", "id_sepomex": "CC", "provincia": "Campeche", "idEstado": 4},
        {"acronimo": "CHIS", "id_sepomex": "CS", "provincia": "Chiapas", "idEstado": 5},
        {"acronimo": "CHIH", "id_sepomex": "CH", "provincia": "Chihuahua", "idEstado": 6},
        {"acronimo": "COAH", "id_sepomex": "CL", "provincia": "Coahuila", "idEstado": 7},
        {"acronimo": "COL", "id_sepomex": "CM", "provincia": "Colima", "idEstado": 8},
        {"acronimo": "CDMX", "id_sepomex": "DF", "provincia": "Ciudad de México", "idEstado": 9},
        {"acronimo": "DGO", "id_sepomex": "DG", "provincia": "Durango", "idEstado": 10},
        {"acronimo": "GTO", "id_sepomex": "GT", "provincia": "Guanajuato", "idEstado": 11},
        {"acronimo": "GRO", "id_sepomex": "GR", "provincia": "Guerrero", "idEstado": 12},
        {"acronimo": "HGO", "id_sepomex": "HG", "provincia": "Hidalgo", "idEstado": 13},
        {"acronimo": "JAL", "id_sepomex": "JC", "provincia": "Jalisco", "idEstado": 14},
        {"acronimo": "MEX", "id_sepomex": "MC", "provincia": "México", "idEstado": 15},
        {"acronimo": "MICH", "id_sepomex": "MN", "provincia": "Michoacán", "idEstado": 16},
        {"acronimo": "MOR", "id_sepomex": "MS", "provincia": "Morelos", "idEstado": 17},
        {"acronimo": "NAY", "id_sepomex": "NT", "provincia": "Nayarit", "idEstado": 18},
        {"acronimo": "NL", "id_sepomex": "NL", "provincia": "Nuevo León", "idEstado": 19},
        {"acronimo": "OAX", "id_sepomex": "OC", "provincia": "Oaxaca", "idEstado": 20},
        {"acronimo": "PUE", "id_sepomex": "PL", "provincia": "Puebla", "idEstado": 21},
        {"acronimo": "QRO", "id_sepomex": "QT", "provincia": "Querétaro", "idEstado": 22},
        {"acronimo": "QROO", "id_sepomex": "QR", "provincia": "Quintana Roo", "idEstado": 23},
        {"acronimo": "SLP", "id_sepomex": "SP", "provincia": "San Luis Potosí", "idEstado": 24},
        {"acronimo": "SIN", "id_sepomex": "SL", "provincia": "Sinaloa", "idEstado": 25},
        {"acronimo": "SON", "id_sepomex": "SR", "provincia": "Sonora", "idEstado": 26},
        {"acronimo": "TAB", "id_sepomex": "TC", "provincia": "Tabasco", "idEstado": 27},
        {"acronimo": "TAMS", "id_sepomex": "TS", "provincia": "Tamaulipas", "idEstado": 28},
        {"acronimo": "TLAX", "id_sepomex": "TL", "provincia": "Tlaxcala", "idEstado": 29},
        {"acronimo": "VER", "id_sepomex": "VZ", "provincia": "Veracruz", "idEstado": 30},
        {"acronimo": "YUC", "id_sepomex": "YN", "provincia": "Yucatán", "idEstado": 31},
        {"acronimo": "ZAC", "id_sepomex": "ZS", "provincia": "Zacatecas", "idEstado": 32}
    ]

    client = boto3.client('location', region_name='us-east-2')
    # cada uno de los valores los tienes que sacar del secreto
    try:
        client = boto3.client('location')
        consulta = os.getenv('LONGITUD_LATITUD')
        resultado = execute_database(consulta, context)

        coordenadas = json.loads(resultado)
        # inicio de hallar cordenadas con Amazon location server
        for coord in coordenadas:

            print(coord)
            if 'dg_ente' in coord:
                cliente = coord["dg_ente"]
                latitude = coord["dg_lat_seg"]
                longitude = coord["dg_long_seg"]
                
                print('antes while')

                # Realiza una consulta geoespacial utilizando Amazon Location Service
                if latitude >= -90 and latitude <= 90 and longitude >= -180 and longitude <= 180: 
                    response = client.search_place_index_for_position(
                        IndexName='wallgeolocation',  # Reemplaza 'TuIndexName' con el nombre de tu índice de lugares
                        Position=[longitude, latitude],  # Coordenadas de longitud y latitud
                    )

                    data = response['Results']
                    
                    print(data)
    
                    if data is not None and isinstance(data, list) and len(data) > 0 and data[0] is not None and 'Place' in data[0] and 'Country' in data[0]['Place']:  
    
                        try:
                            print('data place')
                            print(data[0]['Place'])
                            nombre_entidad_federativa = ''
                            if 'Region' in data[0]['Place']: 
                                nombre_entidad_federativa = data[0]['Place']['Region']
                            country = data[0]['Place']['Country']

                            id_pais_geo = 'ND'
                            
                            consulta_pais = f"select pa_iso_tif from cobis..cl_pais_autorizado, cobis..cl_pais where pa_abreviatura = '{country}' and pa_descripcion = pa_nombre"
                                    
                            response_consulta_pais = execute_database(consulta_pais, context)
                            response_consulta_pais =  json.loads(response_consulta_pais)
                            response_consulta_pais = [entry for entry in response_consulta_pais if "records_affected" not in entry]
                            if len(response_consulta_pais) > 0:
                                id_pais_geo =  response_consulta_pais[0]['pa_iso_tif']
                                
                            
                            dg_geolocalizacion  = data[0]['Place']['Label']
                            dg_geolocalizacion = dg_geolocalizacion.replace("'", "''")
                            
                            if any(provincia["provincia"] == nombre_entidad_federativa for provincia in provincias):
                                id_estado = next((provincia["idEstado"] for provincia in provincias if provincia["provincia"] == nombre_entidad_federativa), None)
                                    
                                consulta_geo = (f"""UPDATE cobis..cl_direccion_geo
                                    SET dg_geolocalizacion = '{dg_geolocalizacion}',
                                        dg_id_estado_geo = '{id_estado}',
                                        dg_id_pais_geo = '{id_pais_geo}'
                                    WHERE dg_ente = {cliente} """)
                                
                                execute_database(consulta_geo, context)
                            else:
                                pa_geolocalizacion = pa_geolocalizacion('IND','ND')
                                consulta_geo = (f"""UPDATE cobis..cl_direccion_geo  
                                    SET dg_geolocalizacion = '{dg_geolocalizacion}',
                                        dg_id_estado_geo = '99999999',
                                        dg_id_pais_geo = '{id_pais_geo}'
                                    WHERE dg_ente = {cliente} """)
                                
                                execute_database(consulta_geo, context)
                                # print(consulta_geo)
                        except Exception as xe:
                            print(f"SALTA::: {xe}")
                    else:
                        if data is not None and isinstance(data, list) and len(data) > 0 and data[0] is not None and 'Place' in data[0] and 'Label' in data[0]['Place']:
                            pa_geolocalizacion = data[0]['Place']['Label']
                        
                            consulta_geo = (f"""UPDATE cobis..cl_direccion_geo
                                        SET dg_geolocalizacion = '{pa_geolocalizacion}',
                                            dg_id_estado_geo = '99999999',
                                            dg_id_pais_geo = 'ND'
                                        WHERE dg_ente = {cliente} """)
                            execute_database(consulta_geo, context)


    except Exception as e:
        # Maneja cualquier excepción que pueda ocurrir durante la ejecución
        print(f"Error durante la ejecución: {e}")

    return {
        'statusCode': 200,
        'body': json.dumps("Proceso completado exitosamente")
    }
