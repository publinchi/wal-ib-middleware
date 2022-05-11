package com.cobiscorp.ecobis.orchestration.ib.expenses;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.dtos.ErrorBlock;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.*;
import com.cobiscorp.ecobis.ib.application.dtos.CurrencyRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CurrencyResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ServerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ExpensesAccount;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ExpensesAccountRequest;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ExpensesAccountResponse;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.IExpensesAccounts;
import org.apache.felix.scr.annotations.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(name = "ExpensesAccountsOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "ExpensesAccountsOrchestrationCore"),
        @Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
        @Property(name = "service.identifier", value = "ExpensesAccountsOrchestrationCore") })
public class ExpensesAccountsOrchestrationCore extends SPJavaOrchestrationBase {
    private static ILogger logger = LogFactory.getLogger(ExpensesAccountsOrchestrationCore.class);
    private static final String CLASS_NAME = "ExpensesAccountsOrchestrationCore--->";
    static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
    static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";

    /**
     * Instance plugin to use services other core banking
     */
    @Reference(referenceInterface = IExpensesAccounts.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindExpensesAccounts", unbind = "unbindExpensesAccounts")
    protected IExpensesAccounts coreExpensesAccounts;

    /**
     * Instance Service Interface
     *
     * @param service
     */
    public void bindExpensesAccounts(IExpensesAccounts service) {
        coreExpensesAccounts = service;
    }

    /**
     * Deleting Service Interface
     *
     * @param service
     */
    public void unbindExpensesAccounts(IExpensesAccounts service) {
        coreExpensesAccounts = null;
    }


    /**
     * Instance plugin to use services other core banking
     */
    @Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.MANDATORY_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
    private ICoreServer CoreServer;

    /**
     * Instance Service Interface
     *
     * @param service
     */
    protected void bindCoreServer(ICoreServer service) {
        CoreServer = service;
    }

    /**
     * Deleting Service Interface
     *
     * @param service
     */
    protected void unbindCoreServer(ICoreServer service) {
        CoreServer = null;
    }

    /**
     * /** Execute transfer first step of service
     * <p>
     * This method is the main executor of transactional contains the original
     * input parameters.
     *
     * @param anOriginalRequest
     *            - Information original sended by user's.
     * @param aBagSPJavaOrchestration
     *            - Object dictionary transactional steps.
     *
     * @return
     *         <ul>
     *         <li>IProcedureResponse - Represents the service execution.</li>
     *         </ul>
     */
    @Override
    public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
                                                       Map<String, Object> aBagSPJavaOrchestration) {
        if (logger.isInfoEnabled())
            logger.logInfo(CLASS_NAME + "executeJavaOrchestration: "+anOriginalRequest.getProcedureRequestAsString());
        aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);

        try {
            Map<String, Object> mapInterfaces = new HashMap<String, Object>();
            mapInterfaces.put("coreExpensesAccounts", coreExpensesAccounts);
            Utils.validateComponentInstance(mapInterfaces);

            if (anOriginalRequest == null) {
                if (logger.isInfoEnabled())
                    logger.logInfo(CLASS_NAME + "Original Request ISNULL");
            }

            Map<String, Object> wprocedureResponse1 = procedureResponse(anOriginalRequest, aBagSPJavaOrchestration);
            Boolean wSuccessExecutionOperation1 = (Boolean) wprocedureResponse1.get("SuccessExecutionOperation");
            IProcedureResponse wIProcedureResponse1 = (IProcedureResponse) wprocedureResponse1.get("IProcedureResponse");
            IProcedureResponse wErrorProcedureResponse = (IProcedureResponse) wprocedureResponse1
                    .get("ErrorProcedureResponse");

            if (wErrorProcedureResponse != null) {
                return wErrorProcedureResponse;
            }
            if (Boolean.FALSE.equals(wSuccessExecutionOperation1)) {
                return wIProcedureResponse1;
            }
            wIProcedureResponse1 = (IProcedureResponse) aBagSPJavaOrchestration.get("GET_EXPENSES_RESPONSE");

            return wIProcedureResponse1;
        } catch (Exception e) {
            if (logger.isInfoEnabled()) {
                logger.logInfo("*********  Error en " + e.getMessage(), e);
            }

            IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalRequest);
            ErrorBlock eb = new ErrorBlock(-1, "Service is not available");
            wProcedureRespFinal.addResponseBlock(eb);
            wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
            wProcedureRespFinal.setReturnCode(-1);
            return wProcedureRespFinal;
        }
    }

    private Map<String, Object> procedureResponse(IProcedureRequest anOriginalRequest,
                                                  Map<String, Object> aBagSPJavaOrchestration) {
        if (logger.isDebugEnabled()) {
            logger.logInfo(CLASS_NAME + "getExpensesAccounts");
            logger.logInfo("INICIO ===================================>> procedureResponse - Orquestacion");
        }
        HashMap<String, Object> ret = new HashMap<String, Object>();
        ret.put("IProcedureResponse", null);
        ret.put("SuccessExecutionOperation", null);
        ret.put("ErrorProcedureResponse", null);

        boolean wSuccessExecutionOperation1 = executeGetExpensesAccounts(anOriginalRequest, aBagSPJavaOrchestration);
        ret.put("SuccessExecutionOperation", wSuccessExecutionOperation1);

        if (logger.isDebugEnabled())
            logger.logDebug("result exeuction operation 1: " + wSuccessExecutionOperation1);

        IProcedureResponse wProcedureResponseOperation1 = initProcedureResponse(anOriginalRequest);
        wProcedureResponseOperation1.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE,
                ICSP.ERROR_EXECUTION_SERVICE);
        ret.put("IProcedureResponse", wProcedureResponseOperation1);
        if (logger.isDebugEnabled())
            logger.logInfo("FIN ===================================>> procedureResponse - Orquestacion");
        return ret;
    }

    private boolean executeGetExpensesAccounts(IProcedureRequest anOriginalRequest,
                                               Map<String, Object> aBagSPJavaOrchestration) {
        if (logger.isDebugEnabled())
            logger.logInfo("INICIO ===================================>> executeGetExpensesAccounts - Orquestacion");
        IProcedureResponse wProcedureResponse = initProcedureResponse(anOriginalRequest);
        ExpensesAccountResponse aExpensesAccountResponse = new ExpensesAccountResponse();

        try {
            ExpensesAccountRequest wExpensesAccountRequest = transformExpensesAccountRequest(aBagSPJavaOrchestration);
            wExpensesAccountRequest.setOriginalRequest(anOriginalRequest);
            ServerResponse serverStatus = executeServerStatus(anOriginalRequest, aBagSPJavaOrchestration);

            if(null != serverStatus){
                if(Boolean.TRUE.equals(serverStatus.getOnLine())){
                    aExpensesAccountResponse = coreExpensesAccounts.getExpensesAccounts(wExpensesAccountRequest);
                    aExpensesAccountResponse = coreExpensesAccounts.getAccountsBalance(aExpensesAccountResponse, wExpensesAccountRequest);
                }else{
                    aExpensesAccountResponse = coreExpensesAccounts.getExpensesAccountsOffline(aExpensesAccountResponse, wExpensesAccountRequest);
                }
            }

            wProcedureResponse = transformExpensesAccountResponse(aExpensesAccountResponse, aBagSPJavaOrchestration);
            aBagSPJavaOrchestration.put("GET_EXPENSES_RESPONSE", wProcedureResponse);
            if (logger.isDebugEnabled())
                logger.logInfo("FIN ===================================>> executeGetExpensesAccounts - Orquestacion");
            return !wProcedureResponse.hasError();
        } catch (CTSServiceException e) {
            if (logger.isDebugEnabled())
                logger.logInfo(CLASS_NAME + "CTSServiceException:" + e.getMessage());
            e.printStackTrace();
            aBagSPJavaOrchestration.put("GET_EXPENSES_RESPONSE", null);
            return false;
        } catch (CTSInfrastructureException e) {
            if (logger.isDebugEnabled())
                logger.logInfo(CLASS_NAME + "CTSInfrastructureException:" + e.getMessage());
            aBagSPJavaOrchestration.put("GET_EXPENSES_RESPONSE", null);
            return false;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase
     * #processResponse(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
     * java.util.Map)
     */
    @Override
    public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> aBagSPJavaOrchestration) {

        return (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
    }


    private ExpensesAccountResponse filterByBalance(ExpensesAccountResponse responseFromQuery, ExpensesAccountRequest requestDto){
        String wInfo = CLASS_NAME+"filterByBalance";
        logger.logInfo(wInfo + "[init task --->]");
        ExpensesAccountResponse responseFiltered = new ExpensesAccountResponse();
        List<ExpensesAccount> listFiltered = new ArrayList<ExpensesAccount>();
        for (ExpensesAccount account:responseFromQuery.getExpensesAccountList()) {
            if(account.getBalance().compareTo(requestDto.getBalance()) == 0){
                listFiltered.add(account);
            }
        }
        responseFiltered.setExpensesAccountList(listFiltered);
        responseFiltered.setReturnCode(0);

        logger.logInfo(wInfo + "[end task --->]");

        return responseFiltered;
    }

    /******************
     * Transformación de ProcedureRequest a StockRequest
     ********************/

    private ExpensesAccountRequest transformExpensesAccountRequest(Map<String, Object> aBagSPJavaOrchestration) {
        ExpensesAccountRequest wExpensesAccountRequest = new ExpensesAccountRequest();

        if (logger.isDebugEnabled())
            logger.logDebug("aBagSPJavaOrchestration to Transform->" + aBagSPJavaOrchestration);

        IProcedureRequest aRequest = (ProcedureRequestAS) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);

        if (logger.isDebugEnabled())
            logger.logDebug("aBagSPJavaOrchestration to Transform->" + aRequest.getProcedureRequestAsString());

        if(null != aRequest.readValueParam("@i_operacion")){
            wExpensesAccountRequest.setOperation(aRequest.readValueParam("@i_operacion"));
        }
        if(null != aRequest.readValueParam("@i_cta_gasto_id")){
            wExpensesAccountRequest.setExpensesAccountId(Integer.parseInt(aRequest.readValueParam("@i_cta_gasto_id")));
        }
        if(null != aRequest.readValueParam("@i_cuenta_principal")){
            wExpensesAccountRequest.setMasterAccount(aRequest.readValueParam("@i_cuenta_principal"));
        }
        if(null != aRequest.readValueParam("@i_codigo_grupo")){
            wExpensesAccountRequest.setGroupCode(Integer.parseInt(aRequest.readValueParam("@i_codigo_grupo")));
        }
        if(null != aRequest.readValueParam("@i_saldo")){
            wExpensesAccountRequest.setBalance(new Double(aRequest.readValueParam("@i_saldo")));
        }
        if(null != aRequest.readValueParam("@i_numero_tarjeta")){
            wExpensesAccountRequest.setCardNumber(aRequest.readValueParam("@i_numero_tarjeta"));
        }
        if(null != aRequest.readValueParam("@i_cuenta_gasto")){
            wExpensesAccountRequest.setExpensesAccount(aRequest.readValueParam("@i_cuenta_gasto"));
        }

        return wExpensesAccountRequest;
    }

    /*********************
     * Transformación de Response a ProcedureResponse
     ***********************/

    private IProcedureResponse transformExpensesAccountResponse(ExpensesAccountResponse aExpensesAccountResponse,
                                                                Map<String, Object> aBagSPJavaOrchestration) {
        if (logger.isDebugEnabled())
            logger.logDebug("Transform Procedure Response");

        IProcedureResponse wProcedureResponse = initProcedureResponse(
                (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST));

        if (aExpensesAccountResponse.getReturnCode() != 0) {
            aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
                    Utils.returnException(aExpensesAccountResponse.getMessages()));

        } else {

            // Agregar Header
            IResultSetHeader metaData = new ResultSetHeader();
            IResultSetData data = new ResultSetData();

            metaData.addColumnMetaData(new ResultSetHeaderColumn("ID CUENTA", ICTSTypes.SYBINT4, 10));
            metaData.addColumnMetaData(new ResultSetHeaderColumn("CUENTA ORIGEN", ICTSTypes.SYBVARCHAR, 10));
            metaData.addColumnMetaData(new ResultSetHeaderColumn("CUENTA", ICTSTypes.SYBVARCHAR, 64));
            metaData.addColumnMetaData(new ResultSetHeaderColumn("NOMBRE COMPLETO", ICTSTypes.SYBVARCHAR, 64));
            metaData.addColumnMetaData(new ResultSetHeaderColumn("SALDO", ICTSTypes.SYBVARCHAR, 128));
            metaData.addColumnMetaData(new ResultSetHeaderColumn("EMAIL", ICTSTypes.SYBVARCHAR, 10));
            metaData.addColumnMetaData(new ResultSetHeaderColumn("GRUPO", ICTSTypes.SYBINT4, 128));
            metaData.addColumnMetaData(new ResultSetHeaderColumn("TARJETA", ICTSTypes.SYBVARCHAR, 128));
            metaData.addColumnMetaData(new ResultSetHeaderColumn("ID TARJETA", ICTSTypes.SYBINT4, 10));
            metaData.addColumnMetaData(new ResultSetHeaderColumn("LOGIN", ICTSTypes.SYBVARCHAR, 128));
            metaData.addColumnMetaData(new ResultSetHeaderColumn("LOTE", ICTSTypes.SYBINT4, 128));
            metaData.addColumnMetaData(new ResultSetHeaderColumn("CONVENIO TARJETA", ICTSTypes.SYBINT4, 128));

            for (ExpensesAccount aExpensesAccount : aExpensesAccountResponse.getExpensesAccountList()) {
                IResultSetRow row = new ResultSetRow();

                row.addRowData(1, new ResultSetRowColumnData(false,
                        String.valueOf(aExpensesAccount.getExpensesAccountId()) == null ? "" : String.valueOf(aExpensesAccount.getExpensesAccountId())));
                row.addRowData(2, new ResultSetRowColumnData(false,
                        aExpensesAccount.getMasterAccountNumber() == null ? "" : aExpensesAccount.getMasterAccountNumber()));
                row.addRowData(3,
                        new ResultSetRowColumnData(false, aExpensesAccount.getExpensesAccountNumber() == null ? "" : aExpensesAccount.getExpensesAccountNumber()));
                row.addRowData(4, new ResultSetRowColumnData(false,
                        aExpensesAccount.getOwnerAccountName() == null ? "" :aExpensesAccount.getOwnerAccountName()));
                row.addRowData(5, new ResultSetRowColumnData(false,
                        aExpensesAccount.getBalance() == null ? "" : String.valueOf(aExpensesAccount.getBalance())));
                row.addRowData(6, new ResultSetRowColumnData(false,
                        aExpensesAccount.getEmail() == null ? "" : aExpensesAccount.getEmail()));
                row.addRowData(7,
                        new ResultSetRowColumnData(false, aExpensesAccount.getGroupCode() == null ? "" : String.valueOf(aExpensesAccount.getGroupCode())));
                row.addRowData(8,
                        new ResultSetRowColumnData(false,  aExpensesAccount.getDebitCardNumber() == null ? "" : aExpensesAccount.getDebitCardNumber()));
                row.addRowData(9,
                        new ResultSetRowColumnData(false,  String.valueOf(aExpensesAccount.getDebitCardCode()) == null ? "" : String.valueOf(aExpensesAccount.getDebitCardCode())));
                row.addRowData(10,
                        new ResultSetRowColumnData(false,  aExpensesAccount.getLoginExpensesAccount() == null ? "" : aExpensesAccount.getLoginExpensesAccount()));
                row.addRowData(11,
                        new ResultSetRowColumnData(false,  String.valueOf(aExpensesAccount.getLotId()) == null ? "" : String.valueOf(aExpensesAccount.getLotId())));
                row.addRowData(12,
                        new ResultSetRowColumnData(false,  String.valueOf(aExpensesAccount.getAgreementCard()) == null ? "" : String.valueOf(aExpensesAccount.getAgreementCard())));
                data.addRow(row);
            }

            IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

            wProcedureResponse.addResponseBlock(resultBlock);

        }
        wProcedureResponse.setReturnCode(aExpensesAccountResponse.getReturnCode());
        if (logger.isDebugEnabled())
            logger.logDebug("transformProcedureResponse Final -->" + wProcedureResponse.getProcedureResponseAsString());

        return wProcedureResponse;

    }

    @Override
    public void loadConfiguration(IConfigurationReader iConfigurationReader) {

    }

    protected ServerResponse executeServerStatus(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {

        ServerRequest serverRequest = new ServerRequest();
        ServerResponse wServerStatusResp = null;

        serverRequest.setChannelId(request.readValueParam("@s_servicio").toString());
        serverRequest.setFormatDate(101);
        serverRequest.setCodeTransactionalIdentifier(request.readValueParam("@s_ssn"));

        if (logger.isDebugEnabled())
            logger.logDebug(CLASS_NAME + "Request Corebanking: " + serverRequest);

        try {
            wServerStatusResp = CoreServer.getServerStatus(serverRequest);
            if (logger.isDebugEnabled())
                logger.logDebug("Response Corebanking: " + wServerStatusResp);
            if (logger.isInfoEnabled())
                logger.logInfo(CLASS_NAME + " Saliendo de executeServerStatus");

        } catch (CTSServiceException e) {
            e.printStackTrace();
            return null;
        } catch (CTSInfrastructureException e) {
            e.printStackTrace();
            return null;
        }
        return wServerStatusResp;
    }
}
