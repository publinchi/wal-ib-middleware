package com.cobiscorp.ecobis.orchestration.core.ib.transfer.spei.api;

public class Constants {

	public static final String CLIENT = "CLIENT";
    public static final String AUTHORIZATION_SERVER_URL = "AUTH_URL";
    public static final String GRANT_TYPE = "GRANT_TYPE";
    public static final String CLIENT_ID = "ID";
    public static final String LOGIN_SERVER_URL = "/autenticacion";
    public static final String USERNAME = "USER";
    public static final String PASSWORD = "PWD";
    public static final String SCOPE = "SCOPE";
    public static final String PREDEFINED_REFERENCES_URL = "/referenciaVirtual/predefinida";
    public static final String CLIENT_SECRET = "SECRET";
    public static final String ID_CUENTA_ORDENANTE = "ID_CTA_ORD";
    public static final String QUERY_REFERENCES_URL = "/referenciaVirtual/";
    public static final String TERCERO_ORDENANTE_URL = "/ordenante/beneficiario/capturarTercero";
    public static final String SUCCESS_CODE = "HUBP0000";
    public static final String API_KEY = "APIKEY";
    public static final String ESTATUS_AUTORIZADA = "AU";
    public static final String UPDATE_REFERENCES_URL = "/referenciaVirtual/status";
    public static final String BASE_URL = "BASE_URL";
    public static final String SERVER_ERROR = "SERVER ERROR";
    public static final String ACCENDO_EMPTY_VALUE = "N/A";
    public static final String ERROR_CODE_ON_RFC_CURP = "HUBP0001";
    
    public static final String URL_AUTH_SINGLE_TOKEN = "AUTH_URL";
    public static final String URL_AUTH_SESION_TOKEN = "LOGIN_URL";
    public static final String URL_SPEI_REGISTER = "SPEIURL";
    public static final String SECURITY_ALGORITH = "SINGALGOTH";
    public static final String CAT_APP_CLIENT = "CATPPCL";

    public static final String COMPANY_ID = "COMPID";
    public static final String TRACKING_KEY_PREFIX = "BANKPREFIX";
    
    public static final String REVERSE = "reverse";
    public static final String SECUENTIAL = "secuential";
    public static final String MOVEMENT_TYPE = "movementType";
    public static final String SPEI_RETURN = "SPEI_RETURN";
    public static final String SPEI_DEBIT=  "SPEI_DEBIT";
    public static final String TRN_18500115=  "18500115";
    public static final String TRANSACTION=  "transaction";

    /*------------------ SPEI -----------------------------------*/
    //ENTRADAS
    public static final String I_CLAVE_RASTREO = "@i_clave_rastreo";
    public static final String I_MENSAJE_ACC = "@i_mensaje_acc";
    public static final String I_ID_SPEI_ACC = "@i_id_spei_acc";
    public static final String I_CODIGO_ACC = "@i_codigo_acc";
    public static final String I_CONCEPTO = "@i_concepto";
    public static final String I_CUENTA = "@i_cta";
    public static final String I_CUENTA_DESTINO = "@i_cta_des";
    public static final String I_NOMBRE_BENEFICIARIO = "@i_nombre_benef";
    public static final String I_BANCO_BENEFICIARIO = "@i_banco_ben";
    public static final String I_BANCO_DESTINO = "@i_banco_dest";
    public static final String I_VALOR = "@i_val";
    public static final String I_PROD_DES = "@i_prod_des";
    public static final String I_FAIL_PROVIDER = "@i_fail_provider";
    
    public static final String TRANSACCION_SPEI = "@i_transaccion_spei";


    //SALIDAS
    public static final String O_SPEI_REQUEST = "@o_spei_request";
    public static final String O_SPEI_RESPONSE = "@o_spei_response";
    public static final String O_CLAVE_RASTREO = "@o_clave_rastreo";

    //MENSAJES
    public static final String INIT_TASK = "init task ---->";
    public static final String END_TASK = "end task ---->";
    
    public static final String ORIGIN_PROCESS_SINGLE_SPEI = "1";
    public static final String SPEI_ERROR_TYPE = "7";
    public static final String ERROR_SPEI = "ERROR EN TRANSFERENCIA SPEI";



}
