/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.get.transaction.limit.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
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

/**
 * @author Sochoa
 * @since Ene 17, 2024
 * @version 1.0.0
 */
@Component(name = "GetTransactionLimitOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "GetTransactionLimitOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "GetTransactionLimitOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_get_transaction_limit_api")})
public class GetTransactionLimitOrchestrationCore extends SPJavaOrchestrationBase {
	
	private ILogger logger = (ILogger) this.getLogger();
	private static final String CLASS_NAME = "GetTransactionLimitOrchestrationCore";
	protected static final String CHANNEL_REQUEST = "8";
	protected static final String GET_TRANSACTION_LIMIT = "GET_TRANSACTION_LIMIT";
	protected static final String MODE_OPERATION = "PYS";

	@Override
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		
		logger.logDebug("Begin flow, GetTransactionLimit starts...");		
		
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
		
		IProcedureResponse anProcedureResponse = new ProcedureResponseAS();
		
		anProcedureResponse = getTransactionLimit(anOriginalRequest, aBagSPJavaOrchestration);
		
		return processResponseApi(anOriginalRequest, anProcedureResponse, aBagSPJavaOrchestration);
	}
	
	private IProcedureResponse getTransactionLimit(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getTransactionLimit: ");
		}
		
		IProcedureResponse wValDataCentral = new ProcedureResponseAS();
		wValDataCentral = valDataCentral(aRequest, aBagSPJavaOrchestration);
		
		logger.logInfo(CLASS_NAME + " code resp auth: " + wValDataCentral.getResultSetRowColumnData(2, 1, 1).getValue());
		if (wValDataCentral.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
			
			IProcedureResponse wGetDataLocal = new ProcedureResponseAS();
			wGetDataLocal = getDataLocal(aRequest, aBagSPJavaOrchestration);
			
			return wGetDataLocal;
		}
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Response " + wValDataCentral.toString());
			logger.logInfo(CLASS_NAME + " Saliendo de getTransactionLimit...");
		}

		return wValDataCentral;
	}
	
	private IProcedureResponse valDataCentral(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en valDataCentral");
		}
		
		request.setSpName("cobis..sp_val_trn_req_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@x_request_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_request_id"));
		request.addInputParam("@x_end_user_request_date", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_end_user_request_date"));
		request.addInputParam("@x_end_user_ip", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_end_user_ip"));
		request.addInputParam("@x_channel", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_channel"));
		
		request.addInputParam("@i_external_customer_id", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_external_customer_id"));
		request.addInputParam("@i_account_number", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_account_number"));
		request.addInputParam("@i_transaction_type", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_transaction_type"));
		request.addInputParam("@i_transaction_subtype", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_transaction_subtype"));
		
		request.addInputParam("@i_operation", ICTSTypes.SQLVARCHAR, "GTL");
		
		request.addOutputParam("@o_trn_subtype_1", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_trn_subtype_2", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_trn_subtype_3", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_trn_subtype_4", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_trn_subtype_5", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_trn_subtype_6", ICTSTypes.SQLVARCHAR, "X");
				
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("subtype1 es " +  wProductsQueryResp.readValueParam("@o_trn_subtype_1"));
			logger.logDebug("subtype2 es " +  wProductsQueryResp.readValueParam("@o_trn_subtype_2"));
			logger.logDebug("subtype3 es " +  wProductsQueryResp.readValueParam("@o_trn_subtype_3"));
			logger.logDebug("subtype4 es " +  wProductsQueryResp.readValueParam("@o_trn_subtype_4"));
			logger.logDebug("subtype5 es " +  wProductsQueryResp.readValueParam("@o_trn_subtype_5"));
			logger.logDebug("subtype6 es " +  wProductsQueryResp.readValueParam("@o_trn_subtype_6"));
		}
		
		aBagSPJavaOrchestration.put("subtype1", wProductsQueryResp.readValueParam("@o_trn_subtype_1"));
		aBagSPJavaOrchestration.put("subtype2", wProductsQueryResp.readValueParam("@o_trn_subtype_2"));
		aBagSPJavaOrchestration.put("subtype3", wProductsQueryResp.readValueParam("@o_trn_subtype_3"));
		aBagSPJavaOrchestration.put("subtype4", wProductsQueryResp.readValueParam("@o_trn_subtype_4"));
		aBagSPJavaOrchestration.put("subtype5", wProductsQueryResp.readValueParam("@o_trn_subtype_5"));
		aBagSPJavaOrchestration.put("subtype6", wProductsQueryResp.readValueParam("@o_trn_subtype_6"));
	
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking valDataLocal: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de valDataCentral");
		}

		return wProductsQueryResp;
	}
	
	private IProcedureResponse getDataLocal(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getDataLocal");
		}

		request.setSpName("cob_bvirtual..sp_bv_get_limit_data_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@i_external_customer_id", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_external_customer_id"));
		request.addInputParam("@i_transaction_type", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_transaction_type"));
		request.addInputParam("@i_trn_subtype_1", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("subtype1"));
		request.addInputParam("@i_trn_subtype_2", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("subtype2"));
		request.addInputParam("@i_trn_subtype_3", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("subtype3"));
		request.addInputParam("@i_trn_subtype_4", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("subtype4"));
		request.addInputParam("@i_trn_subtype_5", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("subtype5"));
		request.addInputParam("@i_trn_subtype_6", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("subtype6"));
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de trnDataCentral");
		}

		return wProductsQueryResp;
	}
	

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		return null;
	}
	
	public IProcedureResponse processResponseApi(IProcedureRequest aRequest, IProcedureResponse anOriginalProcedureRes, Map<String, Object> aBagSPJavaOrchestration) {
		
		logger.logInfo("getTransactionLimit processResponseApi [INI] --->" );
		
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();

		Integer codeReturn = anOriginalProcedureRes.getReturnCode();
		
		logger.logInfo("return code resp--->" + codeReturn );

		//metaData
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		
		metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));
		
		//metaData2
		IResultSetHeader metaData2 = new ResultSetHeader();
		IResultSetData data2 = new ResultSetData();
		
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));
		
		//metaData3
		IResultSetHeader metaData3 = new ResultSetHeader();
		IResultSetData data3 = new ResultSetData();
		
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("externalCustomerId", ICTSTypes.SQLINT4, 12));
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("accountNumber", ICTSTypes.SQLVARCHAR, 20));
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("transactionType", ICTSTypes.SQLVARCHAR, 255));
			
		//metaData4
		IResultSetHeader metaData4 = new ResultSetHeader();
		IResultSetData data4 = new ResultSetData();
		
		metaData4.addColumnMetaData(new ResultSetHeaderColumn("transactionSubType", ICTSTypes.SQLVARCHAR, 255));
		metaData4.addColumnMetaData(new ResultSetHeaderColumn("transactionLimitType", ICTSTypes.SQLVARCHAR, 255));
		metaData4.addColumnMetaData(new ResultSetHeaderColumn("amountTCL", ICTSTypes.SQLMONEY, 64));	
		metaData4.addColumnMetaData(new ResultSetHeaderColumn("currencyTCL", ICTSTypes.SQLVARCHAR, 10));
		metaData4.addColumnMetaData(new ResultSetHeaderColumn("transactionLimitsType", ICTSTypes.SQLVARCHAR, 255));
		metaData4.addColumnMetaData(new ResultSetHeaderColumn("amountCL", ICTSTypes.SQLMONEY, 64));	
		metaData4.addColumnMetaData(new ResultSetHeaderColumn("currencyCL", ICTSTypes.SQLVARCHAR, 10));
		metaData4.addColumnMetaData(new ResultSetHeaderColumn("amountBA", ICTSTypes.SQLMONEY, 64));	
		metaData4.addColumnMetaData(new ResultSetHeaderColumn("currencyBA", ICTSTypes.SQLVARCHAR, 10));	
		
		
		if (codeReturn == 0) {
			
			if(anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
				
				logger.logDebug("Return code response successful: " + anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).getValue());
				
				String externalCustomerId = aRequest.readValueParam("@i_external_customer_id");
				String accountNumber = aRequest.readValueParam("@i_account_number");
				String transactionType = aRequest.readValueParam("@i_transaction_type");
				
				if (anOriginalProcedureRes != null && anOriginalProcedureRes.getResultSet(3).getData().getRowsAsArray().length > 0
						&& anOriginalProcedureRes.getResultSet(4).getData().getRowsAsArray().length > 0) {
					
					logger.logDebug("Ending flow, getTransactionLimit processResponse successful...");
				
					//data
					IResultSetRow row = new ResultSetRow();
					
					row.addRowData(1, new ResultSetRowColumnData(false, "true"));
					
					data.addRow(row);
					
					//data2
					IResultSetRow row2 = new ResultSetRow();
					
					row2.addRowData(1, new ResultSetRowColumnData(false, "0"));
					row2.addRowData(2, new ResultSetRowColumnData(false, "Success"));
					
					data2.addRow(row2);
					
					//data3
					IResultSetRow row3 = new ResultSetRow();
					
					row3.addRowData(1, new ResultSetRowColumnData(false, externalCustomerId));
					row3.addRowData(2, new ResultSetRowColumnData(false, accountNumber));
					row3.addRowData(3, new ResultSetRowColumnData(false, transactionType));
					
					data3.addRow(row3);
					
					//transactionLimitsArray
					IResultSetBlock resulsetOrigin1 = anOriginalProcedureRes.getResultSet(3);
					IResultSetRow[] rowsTemp1 = resulsetOrigin1.getData().getRowsAsArray();
					
					//result set iteration
					int i = 4;
					
					for (IResultSetRow iResultSetRow1 : rowsTemp1) {
						
						//data4
						IResultSetRow row4 = new ResultSetRow();
						IResultSetRowColumnData[] columns1 = iResultSetRow1.getColumnsAsArray();
						
						String transactionSubtype, transactionLimitsType = null;
						
						transactionSubtype = columns1[1].getValue();
						
						if (transactionSubtype == null) {
							
							transactionSubtype = null;
							
						} else if (transactionSubtype.equals("18700103")) {
							
							transactionSubtype = "P2P_CREDIT";
							
						} else if (transactionSubtype.equals("18500069")) {
							
							transactionSubtype = "SPEI_CREDIT";
							
						} else if (transactionSubtype.equals("18500134")) {
							
							transactionSubtype = "CREDIT_AT_STORE";
							
						} else if (transactionSubtype.equals("18059")) {
							
							transactionSubtype = "P2P_DEBIT";

						} else if (transactionSubtype.equals("1870013")) {
							
							transactionSubtype = "SPEI_DEBIT";

						} else if (transactionSubtype.equals("18700104")) {
							
							transactionSubtype = "ATM_DEBIT";
							
						} else if (transactionSubtype.equals("18500133")) {
							
							transactionSubtype = "DEBIT_AT_STORE";
							
						} else {
							
							transactionSubtype = "UNDEFINED";
						}
						
						row4.addRowData(1, new ResultSetRowColumnData(false, transactionSubtype));
						row4.addRowData(2, new ResultSetRowColumnData(false, columns1[2].getValue()));
						row4.addRowData(3, new ResultSetRowColumnData(false, columns1[3].getValue()));
						row4.addRowData(4, new ResultSetRowColumnData(false, columns1[4].getValue()));
						
						//transactionSubTypeLimitsArray
						IResultSetBlock resulsetOrigin2 = anOriginalProcedureRes.getResultSet(i);
						IResultSetRow[] rowsTemp2 = resulsetOrigin2.getData().getRowsAsArray();
						
						for (IResultSetRow iResultSetRow2 : rowsTemp2) {
							
							IResultSetRowColumnData[] columns2 = iResultSetRow2.getColumnsAsArray();
							
							transactionLimitsType = columns2[0].getValue();
							
							if (transactionLimitsType == null) {
								
								transactionLimitsType = null;
									
							} else if (transactionLimitsType.equals("D")) {
								
								transactionLimitsType = "DAILY";
									
							} else if (transactionLimitsType.equals("W")) {
								
								transactionLimitsType = "WEEKLY";
								
							} else if (transactionLimitsType.equals("M")) {
								
								transactionLimitsType = "MONTHLY";
								
							} else {
								
								transactionLimitsType = "UNDEFINED";
							}
							
							row4.addRowData(5, new ResultSetRowColumnData(false, transactionLimitsType));
							row4.addRowData(6, new ResultSetRowColumnData(false, columns2[1].getValue()));
							row4.addRowData(7, new ResultSetRowColumnData(false, columns2[2].getValue()));
							row4.addRowData(8, new ResultSetRowColumnData(false, columns2[3].getValue()));
							row4.addRowData(9, new ResultSetRowColumnData(false, columns2[4].getValue()));
							
						}

						data4.addRow(row4);
						
						i = i + 1;
					}
				} else {
					
					logger.logDebug("Ending flow, getTransactionLimit processResponse failed with code 500014: ");
					
					IResultSetRow row = new ResultSetRow();
					
					row.addRowData(1, new ResultSetRowColumnData(false, "false"));
					
					data.addRow(row);
					
					IResultSetRow row2 = new ResultSetRow();
					
					row2.addRowData(1, new ResultSetRowColumnData(false, "500014"));
					row2.addRowData(2, new ResultSetRowColumnData(false, "Transaction limit is unavailable"));
					
					data2.addRow(row2);
				}
	
			} else {
				 
				logger.logDebug("Ending flow, getTransactionLimit processResponse error...");
				
				String success = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).isNull()?"false":anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).getValue();
				String code = anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).isNull()?"400218":anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).getValue();
				String message = anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 2).isNull()?"Service execution error":anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 2).getValue();
				
				//data
				IResultSetRow row = new ResultSetRow();
				
				row.addRowData(1, new ResultSetRowColumnData(false, success));
				
				data.addRow(row);
				
				//data2
				IResultSetRow row2 = new ResultSetRow();
				
				row2.addRowData(1, new ResultSetRowColumnData(false, code));
				row2.addRowData(2, new ResultSetRowColumnData(false, message));
				
				data2.addRow(row2);
			}
			
		} else {
			
			logger.logDebug("Ending flow, getTransactionLimit processResponse failed with code: ");
			
			IResultSetRow row = new ResultSetRow();
			
			row.addRowData(1, new ResultSetRowColumnData(false, "false"));
			
			data.addRow(row);
			
			IResultSetRow row2 = new ResultSetRow();
			
			row2.addRowData(1, new ResultSetRowColumnData(false, codeReturn.toString()));
			row2.addRowData(2, new ResultSetRowColumnData(false, anOriginalProcedureRes.getMessage(1).getMessageText()));
			
			data2.addRow(row2);
		}

		IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);
		IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);
		IResultSetBlock resultsetBlock3 = new ResultSetBlock(metaData3, data3);
		IResultSetBlock resultsetBlock4 = new ResultSetBlock(metaData4, data4);
		
		wProcedureResponse.setReturnCode(200);

		wProcedureResponse.addResponseBlock(resultsetBlock);
		wProcedureResponse.addResponseBlock(resultsetBlock2);
		wProcedureResponse.addResponseBlock(resultsetBlock3);
		wProcedureResponse.addResponseBlock(resultsetBlock4);
		
		return wProcedureResponse;		
	}
}
