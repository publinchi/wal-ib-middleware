/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.getbalancesdetail;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
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
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.ServerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.cobis.crypt.ICobisCrypt;
import com.cobiscorp.cobis.commons.components.ComponentLocator;

/**
 * @author cecheverria
 * @since Sep 2, 2014
 * @version 1.0.0
 */
@Component(name = "GetBalancesDetailOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "GetBalancesDetailOrchestrationCore"),
        @Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
        @Property(name = "service.identifier", value = "GetBalancesDetailOrchestrationCore"),
        @Property(name = "service.spName", value = "cob_procesador..sp_get_balances_detail_api")
})
public class GetBalancesDetailOrchestrationCore extends SPJavaOrchestrationBase {// SPJavaOrchestrationBase
    
    private static final int ERROR40004 = 40004;
    private static final int ERROR40003 = 40003;
    private static final int ERROR40002 = 40002;
    private ServerResponse responseServer;
    
    private ILogger logger = (ILogger) this.getLogger();
    private IResultSetRowColumnData[] columnsToReturn;

    @Override
    public void loadConfiguration(IConfigurationReader aConfigurationReader) {
        
    }
    
    @Override
    public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
        
        logger.logDebug("Begin flow, GetBalancesDetailOrchestrationCore starts...");    
        
        aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
        
        ServerRequest serverRequest = new ServerRequest();
        serverRequest.setChannelId("8");

        try {
            responseServer = getServerStatus(serverRequest);
        } catch (CTSServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CTSInfrastructureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        queryGetBalancesDetail(aBagSPJavaOrchestration);
        
        return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
    }
    
    private void queryGetBalancesDetail(Map<String, Object> aBagSPJavaOrchestration) {
        
        IProcedureRequest wQueryRequest = (IProcedureRequest) aBagSPJavaOrchestration.get("anOriginalRequest");
        
        aBagSPJavaOrchestration.clear();
        
        String xRequestId = wQueryRequest.readValueParam("@x_request_id");
        String xEndUserRequestDateTime = wQueryRequest.readValueParam("@x_end_user_request_date");
        String xEndUserIp = wQueryRequest.readValueParam("@x_end_user_ip"); 
        String xChannel = wQueryRequest.readValueParam("@x_channel");
        String idCustomer = wQueryRequest.readValueParam("@i_externalCustomerId");
        String accountNumber = wQueryRequest.readValueParam("@i_accountNumber");
        
        if (xRequestId.equals("null") || xRequestId.trim().isEmpty()) {
            aBagSPJavaOrchestration.put("400324", "x-request-id header is required");
            return;
        }
        
        if (xEndUserRequestDateTime.equals("null") || xEndUserRequestDateTime.trim().isEmpty()) {
            aBagSPJavaOrchestration.put("400325", "x-end-user-request-date-time header is required");
            return;
        }
        
        if (xEndUserIp.equals("null") || xEndUserIp.trim().isEmpty()) {
            aBagSPJavaOrchestration.put("400326", "x-end-user-ip header is required");
            return;
        }
        
        if (xChannel.equals("null") || xChannel.trim().isEmpty()) {
            aBagSPJavaOrchestration.put("400327", "x-channel header is required");
            return;
        }
        
        if (accountNumber.isEmpty()) {
            aBagSPJavaOrchestration.put("40082", "accountNumber must not be empty");
            return;
        }
                
        logger.logDebug("Begin flow, queryGetBalancesDetail with id: " + idCustomer);
        
        if (responseServer.getOnLine()) {
        
            IProcedureRequest reqTMPCentral = (initProcedureRequest(wQueryRequest));
            
            reqTMPCentral.setSpName("cobis..sp_get_balances_detail_central_api");
            reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "central");
            reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500102");
            
            reqTMPCentral.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, idCustomer);
            reqTMPCentral.addInputParam("@i_accountNumber", ICTSTypes.SQLVARCHAR, accountNumber);
            
            
            IProcedureResponse wProcedureResponseCentral = executeCoreBanking(reqTMPCentral);
            
            if (logger.isInfoEnabled()) {
                logger.logDebug("Ending flow, queryGetBalancesDetail with wProcedureResponseCentral: " + wProcedureResponseCentral.getProcedureResponseAsString());
            }
            
            IProcedureResponse wProcedureResponseLocal;
            if (!wProcedureResponseCentral.hasError()) {
                
                IResultSetRow resultSetRow = wProcedureResponseCentral.getResultSet(1).getData().getRowsAsArray()[0];
                IResultSetRowColumnData[] columns = resultSetRow.getColumnsAsArray();
                
                if (columns[0].getValue().equals("true")) {
                    
                    this.columnsToReturn = columns;
                    aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
                    
                    IProcedureRequest reqTMPLocal = (initProcedureRequest(wQueryRequest));
                    
                    reqTMPLocal.setSpName("cob_atm..sp_get_balances_detail_local_api");
                    reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "local");
                    reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500102");
                    
                    reqTMPLocal.addInputParam("@i_accountNumber", ICTSTypes.SQLVARCHAR, accountNumber);
                    reqTMPLocal.addInputParam("@i_offline", ICTSTypes.SQLVARCHAR, "N");
                    
                    wProcedureResponseLocal = executeCoreBanking(reqTMPLocal);
                    
                    if (logger.isInfoEnabled()) {
                        logger.logDebug("Ending flow, queryGetBalancesDetail with wProcedureResponseLocal: " + wProcedureResponseLocal.getProcedureResponseAsString());
                    }
    
                    if (!wProcedureResponseLocal.hasError()) {
                        
                        resultSetRow = wProcedureResponseLocal.getResultSet(1).getData().getRowsAsArray()[0];
                        columns = resultSetRow.getColumnsAsArray();
                        
                        if (columns[0].getValue().equals("true")) {
                            
                            this.columnsToReturn[20] = columns[3];
                            this.columnsToReturn[21] = columns[4];
                            this.columnsToReturn[22] = columns[5];
                            this.columnsToReturn[23] = columns[6];
                            this.columnsToReturn[24] = columns[7];
                            this.columnsToReturn[25] = columns[8];
                            
                            return;
                            
                        } else {
                        
                            return;
                        }                   
                    } else {
                        
                        return;
                    }
                                    
                } else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("40012")) {
                    aBagSPJavaOrchestration.put(columns[1].getValue(), "Customer with externalCustomerId: " + idCustomer + " does not exist");
                    return;
                    
                } else  {   
                    aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
                    return;
                } 
                
            } else {
                aBagSPJavaOrchestration.put("50009", "Error get balances detail");
                return;
            }
        } else {
            
            IProcedureRequest reqTMPLocal = (initProcedureRequest(wQueryRequest));
            
            reqTMPLocal.setSpName("cob_atm..sp_get_balances_detail_local_api");
            reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "local");
            reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500102");
            
            reqTMPLocal.addInputParam("@i_accountNumber", ICTSTypes.SQLVARCHAR, accountNumber);
            reqTMPLocal.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, idCustomer);
            reqTMPLocal.addInputParam("@i_offline", ICTSTypes.SQLVARCHAR, "S");
            
            IProcedureResponse wProcedureResponseLocal = executeCoreBanking(reqTMPLocal);
            
            if (logger.isInfoEnabled()) {
                logger.logDebug("Ending flow, queryGetBalancesDetail with wProcedureResponseLocal: " + wProcedureResponseLocal.getProcedureResponseAsString());
            }

            if (!wProcedureResponseLocal.hasError()) {
                
                IResultSetRow resultSetRow = wProcedureResponseLocal.getResultSet(1).getData().getRowsAsArray()[0];
                IResultSetRowColumnData[] columns = resultSetRow.getColumnsAsArray();
                
                if (columns[0].getValue().equals("true")) {
                    
                    this.columnsToReturn = columns;
                    aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
                    return;
                    
                } else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("40012")) {
                    aBagSPJavaOrchestration.put(columns[1].getValue(), "Customer with externalCustomerId: " + idCustomer + " does not exist");
                    return;
                    
                } else  {   
                    aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
                    return;
                }   
                
            } else {
                
                return;
            }
        }
    }

    @Override
    public IProcedureResponse processResponse(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
        
        ArrayList<String> keyList = new ArrayList<String>(aBagSPJavaOrchestration.keySet());
        
        IResultSetHeader metaData = new ResultSetHeader();
        IResultSetData data = new ResultSetData();
        IResultSetRow row = new ResultSetRow();
        
        IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
        
        metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SYBVARCHAR, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SYBINT4, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SYBVARCHAR, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("accountName", ICTSTypes.SYBVARCHAR, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("accountStatus", ICTSTypes.SYBVARCHAR, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("availableBalance", ICTSTypes.SYBDECIMAL, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("averageBalance", ICTSTypes.SYBDECIMAL, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("currencyId", ICTSTypes.SYBINT4, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("deliveryAddress", ICTSTypes.SYBVARCHAR, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("freezingsNumber", ICTSTypes.SYBINT4, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("frozenAmount", ICTSTypes.SYBDECIMAL, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("lastCutoffBalance", ICTSTypes.SYBVARCHAR, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("lastOperationDate", ICTSTypes.SYBVARCHAR, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("openingDate", ICTSTypes.SYBVARCHAR, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("overdraftAmount", ICTSTypes.SYBVARCHAR, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("productId", ICTSTypes.SYBINT4, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("toDrawBalance", ICTSTypes.SYBDECIMAL, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("accountingBalance", ICTSTypes.SYBDECIMAL, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("official", ICTSTypes.SYBINT4, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("clabeAccountNumber", ICTSTypes.SYBVARCHAR, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("cardIdVC", ICTSTypes.SYBVARCHAR, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("debitCardNumberVC", ICTSTypes.SYBVARCHAR, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("debitCardStateVC", ICTSTypes.SYBVARCHAR, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("cardIdPC", ICTSTypes.SYBVARCHAR, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("debitCardNumberPC", ICTSTypes.SYBVARCHAR, 255));
        metaData.addColumnMetaData(new ResultSetHeaderColumn("debitCardStatePC", ICTSTypes.SYBVARCHAR, 255));

        if (keyList.get(0).equals("0")) {
            
            logger.logDebug("Ending flow, processResponse success with code: " + keyList.get(0));
            String accountName = null;
            if (this.columnsToReturn[3].getValue() != null && !this.columnsToReturn[3].getValue().isEmpty()) {
                accountName = this.columnsToReturn[3].getValue().trim();
            }
            
            row.addRowData(1, new ResultSetRowColumnData(false, this.columnsToReturn[0].getValue()));
            row.addRowData(2, new ResultSetRowColumnData(false, this.columnsToReturn[1].getValue()));
            row.addRowData(3, new ResultSetRowColumnData(false, this.columnsToReturn[2].getValue()));
            row.addRowData(4, new ResultSetRowColumnData(false, accountName));
            row.addRowData(5, new ResultSetRowColumnData(false, this.columnsToReturn[4].getValue()));
            row.addRowData(6, new ResultSetRowColumnData(false, this.columnsToReturn[5].getValue()));
            row.addRowData(7, new ResultSetRowColumnData(false, this.columnsToReturn[6].getValue()));
            row.addRowData(8, new ResultSetRowColumnData(false, this.columnsToReturn[7].getValue()));
            row.addRowData(9, new ResultSetRowColumnData(false, this.columnsToReturn[8].getValue()));
            row.addRowData(10, new ResultSetRowColumnData(false, this.columnsToReturn[9].getValue()));
            row.addRowData(11, new ResultSetRowColumnData(false, this.columnsToReturn[10].getValue()));
            row.addRowData(12, new ResultSetRowColumnData(false, this.columnsToReturn[11].getValue()));
            row.addRowData(13, new ResultSetRowColumnData(false, this.columnsToReturn[12].getValue()));
            row.addRowData(14, new ResultSetRowColumnData(false, this.columnsToReturn[13].getValue()));
            row.addRowData(15, new ResultSetRowColumnData(false, this.columnsToReturn[14].getValue()));
            row.addRowData(16, new ResultSetRowColumnData(false, this.columnsToReturn[15].getValue()));
            row.addRowData(17, new ResultSetRowColumnData(false, this.columnsToReturn[16].getValue()));
            row.addRowData(18, new ResultSetRowColumnData(false, this.columnsToReturn[17].getValue()));
            row.addRowData(19, new ResultSetRowColumnData(false, this.columnsToReturn[18].getValue()));
            row.addRowData(20, new ResultSetRowColumnData(false, this.columnsToReturn[19].getValue()));
            row.addRowData(21, new ResultSetRowColumnData(false, this.columnsToReturn[20].getValue()));
            row.addRowData(22, new ResultSetRowColumnData(false, this.columnsToReturn[21].getValue()));
            row.addRowData(23, new ResultSetRowColumnData(false, this.columnsToReturn[22].getValue()));
            row.addRowData(24, new ResultSetRowColumnData(false, this.columnsToReturn[23].getValue()));
            row.addRowData(25, new ResultSetRowColumnData(false, this.columnsToReturn[24].getValue()));
            row.addRowData(26, new ResultSetRowColumnData(false, this.columnsToReturn[25].getValue()));
            
            data.addRow(row);

        } else {
            
            logger.logDebug("Ending flow, processResponse failed with code: " + keyList.get(0));
            
            row.addRowData(1, new ResultSetRowColumnData(false, "false"));
            row.addRowData(2, new ResultSetRowColumnData(false, keyList.get(0)));
            row.addRowData(3, new ResultSetRowColumnData(false, (String) aBagSPJavaOrchestration.get(keyList.get(0))));
            row.addRowData(4, new ResultSetRowColumnData(false, null));
            row.addRowData(5, new ResultSetRowColumnData(false, null));
            row.addRowData(6, new ResultSetRowColumnData(false, null));
            row.addRowData(7, new ResultSetRowColumnData(false, null));
            row.addRowData(8, new ResultSetRowColumnData(false, null));
            row.addRowData(9, new ResultSetRowColumnData(false, null));
            row.addRowData(10, new ResultSetRowColumnData(false, null));
            row.addRowData(11, new ResultSetRowColumnData(false, null));
            row.addRowData(12, new ResultSetRowColumnData(false, null));
            row.addRowData(13, new ResultSetRowColumnData(false, null));
            row.addRowData(14, new ResultSetRowColumnData(false, null));
            row.addRowData(15, new ResultSetRowColumnData(false, null));
            row.addRowData(16, new ResultSetRowColumnData(false, null));
            row.addRowData(17, new ResultSetRowColumnData(false, null));
            row.addRowData(18, new ResultSetRowColumnData(false, null));
            row.addRowData(19, new ResultSetRowColumnData(false, null));
            row.addRowData(20, new ResultSetRowColumnData(false, null));
            row.addRowData(21, new ResultSetRowColumnData(false, null));
            row.addRowData(22, new ResultSetRowColumnData(false, null));
            row.addRowData(23, new ResultSetRowColumnData(false, null));
            row.addRowData(24, new ResultSetRowColumnData(false, null));
            row.addRowData(25, new ResultSetRowColumnData(false, null));
            row.addRowData(26, new ResultSetRowColumnData(false, null));
            
            data.addRow(row);
        }
        
        IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
        
        wProcedureResponse.addResponseBlock(resultBlock);   
        
        return wProcedureResponse;      
    }
    
    public ServerResponse getServerStatus(ServerRequest serverRequest) throws CTSServiceException, CTSInfrastructureException {

        IProcedureRequest aServerStatusRequest = new ProcedureRequestAS();
        aServerStatusRequest.setSpName("cobis..sp_server_status");
        aServerStatusRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800039");
        aServerStatusRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1800039");
        aServerStatusRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, "central");
        aServerStatusRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

        aServerStatusRequest.setValueParam("@s_servicio", serverRequest.getChannelId());
        aServerStatusRequest.addInputParam("@i_cis", ICTSTypes.SYBCHAR, "S");
        aServerStatusRequest.addOutputParam("@o_en_linea", ICTSTypes.SYBCHAR, "S");
        aServerStatusRequest.addOutputParam("@o_fecha_proceso", ICTSTypes.SYBVARCHAR, "XXXX");

        if (logger.isDebugEnabled())
            logger.logDebug("Request Corebanking: " + aServerStatusRequest.getProcedureRequestAsString());

        IProcedureResponse wServerStatusResp = executeCoreBanking(aServerStatusRequest);

        if (logger.isDebugEnabled())
            logger.logDebug("Response Corebanking: " + wServerStatusResp.getProcedureResponseAsString());

        ServerResponse serverResponse = new ServerResponse();
        
        serverResponse.setSuccess(true);
        Utils.transformIprocedureResponseToBaseResponse(serverResponse, wServerStatusResp);
        serverResponse.setReturnCode(wServerStatusResp.getReturnCode());

        if (wServerStatusResp.getReturnCode() == 0) {
            serverResponse.setOfflineWithBalances(true);

            if (wServerStatusResp.readValueParam("@o_en_linea") != null)
                serverResponse.setOnLine(wServerStatusResp.readValueParam("@o_en_linea").equals("S") ? true : false);

            if (wServerStatusResp.readValueParam("@o_fecha_proceso") != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    serverResponse.setProcessDate(formatter.parse(wServerStatusResp.readValueParam("@o_fecha_proceso")));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } else if (wServerStatusResp.getReturnCode() == ERROR40002 || wServerStatusResp.getReturnCode() == ERROR40003 || wServerStatusResp.getReturnCode() == ERROR40004) {
            serverResponse.setOnLine(false);
            serverResponse.setOfflineWithBalances(wServerStatusResp.getReturnCode() == ERROR40002 ? false : true);
        }

        if (logger.isDebugEnabled())
            logger.logDebug("Respuesta Devuelta: " + serverResponse);
        if (logger.isInfoEnabled())
            logger.logInfo("TERMINANDO SERVICIO");

        return serverResponse;
    }
}