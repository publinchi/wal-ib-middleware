package com.cobiscorp.ecobis.orchestration.core.ib.common;

import java.util.Map;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;

public abstract class SaveAdditionalData extends SPJavaOrchestrationBase {

    private static final String CLASS_NAME = "SaveAdditionalData ---> ";
    private static final String TRANSACTION_CODE = "18700135";
    private static ILogger logger = LogFactory.getLogger(SaveAdditionalData.class);

    
    /**
     * Guarda datos adicionales en el sistema de orquestación.
     *
     * Este método construye una solicitud de procedimiento y la envía al sistema de
     * gestión central o local, dependiendo del tipo de movimiento. Los parámetros
     * se extraen de un mapa de entrada que contiene los detalles necesarios para
     * realizar la operación.
     *
     * @param movementType El tipo de movimiento que se está realizando. Este
     *                     parámetro determina cómo se procesan los datos.
     * @param isOnline     Indica si la operación se realiza en línea (true) o
     *                     fuera de línea (false).
     * @param aBagSPJavaOrchestration Un mapa que contiene los parámetros necesarios
     *                                  para la operación, como "secuential",
     *                                  "secBranch", "alternateCod", "transaction",
     *                                  y "data".
     * @return true si los datos se guardaron correctamente; false en caso contrario.
     *
     * @throws IllegalArgumentException si el mapa de parámetros es nulo o falta
     *                                   alguna clave necesaria.
     */
    protected Boolean saveAdditionalData(String movementType, boolean isOnline,
                                       Map<String, String> aBagSPJavaOrchestration) {
        validateInput(aBagSPJavaOrchestration);

        String target = isOnline ? IMultiBackEndResolverService.TARGET_CENTRAL
                                  : IMultiBackEndResolverService.TARGET_LOCAL;

        IProcedureRequest request = createProcedureRequest(movementType, target, aBagSPJavaOrchestration);
        IProcedureResponse response = executeCoreBanking(request);

        return handleResponse(response);
    }

    protected Boolean saveAdditionalData(String movementType, boolean isOnline,
                                         Map<String, String> aBagSPJavaOrchestration, String operation) {
        validateInput(aBagSPJavaOrchestration);

        String target = isOnline ? IMultiBackEndResolverService.TARGET_CENTRAL
                : IMultiBackEndResolverService.TARGET_LOCAL;

        IProcedureRequest request = createProcedureRequest(movementType, target, aBagSPJavaOrchestration, operation);
        IProcedureResponse response = executeCoreBanking(request);

        return handleResponse(response);
    }

    private void validateInput(Map<String, String> aBagSPJavaOrchestration) {
        if (aBagSPJavaOrchestration == null || !aBagSPJavaOrchestration.containsKey("secuential")) {
            throw new IllegalArgumentException("El mapa de parámetros no contiene las claves necesarias.");
        }
    }

    private IProcedureRequest createProcedureRequest(String movementType, String target,
                                                     Map<String, String> aBagSPJavaOrchestration) {
        IProcedureRequest request = new ProcedureRequestAS();

        request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, target);
        request.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, TRANSACTION_CODE);
        request.setSpName("cob_ahorros..sp_tr04_cons_mov_ah_add_api");

        request.addInputParam("@t_trn", ICTSTypes.SQLINT4, TRANSACTION_CODE);
        request.addInputParam("@i_secuencial", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("secuential"));
        request.addInputParam("@i_ssn_branch", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("secBranch"));
        request.addInputParam("@i_cod_alterno", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("alternateCod"));
        request.addInputParam("@i_transaccion", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("transaction"));
        request.addInputParam("@i_tipo_movimiento", ICTSTypes.SQLCHAR, movementType);
        request.addInputParam("@i_data", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("data"));
        request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "C");

        if (logger.isDebugEnabled()) {
            logger.logDebug(CLASS_NAME + "Data de entrada:" + request.getProcedureRequestAsString());
        }

        return request;
    }

    private IProcedureRequest createProcedureRequest(String movementType, String target,
                                                     Map<String, String> aBagSPJavaOrchestration, String operation) {
        IProcedureRequest request = new ProcedureRequestAS();

        request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, target);
        request.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, TRANSACTION_CODE);
        request.setSpName("cob_ahorros..sp_tr04_cons_mov_ah_add_api");

        request.addInputParam("@t_trn", ICTSTypes.SQLINT4, TRANSACTION_CODE);
        request.addInputParam("@i_secuencial", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("secuential"));
        request.addInputParam("@i_ssn_branch", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("secBranch"));
        request.addInputParam("@i_cod_alterno", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("alternateCod"));
        request.addInputParam("@i_transaccion", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("transaction"));
        request.addInputParam("@i_tipo_movimiento", ICTSTypes.SQLCHAR, movementType);
        request.addInputParam("@i_estado_spei", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("@i_estado_spei"));
        request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR,  operation);
        request.addInputParam("@i_clave_rastreo", ICTSTypes.SQLVARCHAR,  aBagSPJavaOrchestration.get("@i_clave_rastreo"));

        if (logger.isDebugEnabled()) {
            logger.logDebug(CLASS_NAME + "Data de entrada:" + request.getProcedureRequestAsString());
        }

        return request;
    }


    private Boolean handleResponse(IProcedureResponse response) {
        if (logger.isDebugEnabled()) {
            logger.logDebug(CLASS_NAME + "Response obtenido: " + response.getProcedureResponseAsString());
        }
        return response.getReturnCode() == 0;
    }
}