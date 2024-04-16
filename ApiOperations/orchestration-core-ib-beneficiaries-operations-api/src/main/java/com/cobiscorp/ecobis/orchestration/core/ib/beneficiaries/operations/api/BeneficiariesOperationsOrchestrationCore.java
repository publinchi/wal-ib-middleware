/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.beneficiaries.operations.api;

import java.util.Map;

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
 * @since Mar 20, 2024
 * @version 1.0.0
 */
@Component(name = "BeneficiariesOperationsOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "BeneficiariesOperationsOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "BeneficiariesOperationsOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_beneficiaries_operations_api")})
public class BeneficiariesOperationsOrchestrationCore extends SPJavaOrchestrationBase {
	
	private ILogger logger = (ILogger) this.getLogger();
	private static final String CLASS_NAME = "BeneficiariesOperationsOrchestrationCore";
	protected static final String CHANNEL_REQUEST = "8";
	protected static final String BENEFICIARIES_OPERATIONS= "BENEFICIARIES_OPERATIONS";
	protected static final String MODE_OPERATION = "PYS";

	@Override
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		
		logger.logDebug("Begin flow, BeneficiariesOperations starts...");		
		
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
		
		IProcedureResponse anProcedureResponse = new ProcedureResponseAS();
		
		anProcedureResponse = beneficiariesOperations(anOriginalRequest, aBagSPJavaOrchestration);
		
		return processResponseApi(anOriginalRequest, anProcedureResponse,aBagSPJavaOrchestration);
	}
	
	private IProcedureResponse beneficiariesOperations(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en beneficiariesOperations: ");
		}
		
		IProcedureResponse wOperation = new ProcedureResponseAS();
		
		wOperation = executeOperation(aRequest, aBagSPJavaOrchestration);
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Response: " + wOperation.toString());
			logger.logInfo(CLASS_NAME + " beneficiariesOperations...");
		}

		return wOperation;
	}
	
	private IProcedureResponse executeOperation(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en executeOperation");
		}
		
		String operation = aRequest.readValueParam("@i_operacion");
		
		request.setSpName("cob_bvirtual..sp_beneficiaries_mant_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		//headers
		request.addInputParam("@x_request_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_request_id"));
		request.addInputParam("@x_end_user_request_date", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_end_user_request_date"));
		request.addInputParam("@x_end_user_ip", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_end_user_ip"));
		request.addInputParam("@x_channel", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_channel"));
		
		request.addInputParam("@i_ente", ICTSTypes.SQLINT4, aRequest.readValueParam("@i_ente"));
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, operation);
		
		aBagSPJavaOrchestration.put("operation", operation);
		
		if (operation.equals("I") || operation.equals("U") ) {
			
			request.addInputParam("@i_numero_producto", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_numero_producto"));
			request.addInputParam("@i_json_beneficiaries", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_json_beneficiaries"));
		} 

		if (operation.equals("D")) {
			
			request.addInputParam("@i_benf_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_benf_id"));
		}
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
	
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking executeOperation: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de executeOperation");
		}

		return wProductsQueryResp;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		return null;
	}
	
	public IProcedureResponse processResponseApi(IProcedureRequest aRequest, IProcedureResponse anOriginalProcedureRes, Map<String, Object> aBagSPJavaOrchestration) {
		
		logger.logInfo("beneficiariesOperations processResponseApi [INI] --->" );
		
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
		
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("id", ICTSTypes.SQLINT4, 10));
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("names", ICTSTypes.SQLVARCHAR, 50));

		if (codeReturn == 0) {
		
			if(anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
				
				logger.logDebug("Return code response: " + anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1));
				logger.logDebug("Ending flow, beneficiariesOperations processResponse successful...");
	
				
				//data
				IResultSetRow row = new ResultSetRow();
				
				row.addRowData(1, new ResultSetRowColumnData(false, "true"));
				
				data.addRow(row);
				
				//data2
				IResultSetRow row2 = new ResultSetRow();
				
				row2.addRowData(1, new ResultSetRowColumnData(false, "0"));
				row2.addRowData(2, new ResultSetRowColumnData(false, "Success"));
				
				data2.addRow(row2);
				
				//beneficiariesDataArray
				IResultSetBlock resulsetOrigin = anOriginalProcedureRes.getResultSet(3);
				IResultSetRow[] rowsTemp = resulsetOrigin.getData().getRowsAsArray();
				
				for (IResultSetRow iResultSetRow : rowsTemp) {
					
					IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
					
					String names = null, birthDate = null, relationship = null, percentage = null;
					
					names = columns[1].getValue();
					birthDate = columns[2].getValue();
					relationship = columns[3].getValue();
					percentage = columns[4].getValue();
					
					aBagSPJavaOrchestration.put("names", names);
					aBagSPJavaOrchestration.put("birthDate", birthDate);
					aBagSPJavaOrchestration.put("relationship", relationship);
					aBagSPJavaOrchestration.put("percentage", percentage);
					
					notifyBeneficiaryOperation(aRequest, aBagSPJavaOrchestration);
					
					if (aBagSPJavaOrchestration.get("operation").toString().equals("I")) {
						
						//data3
						IResultSetRow row3 = new ResultSetRow();
					
						row3.addRowData(1, new ResultSetRowColumnData(false, columns[0].getValue()));
						row3.addRowData(2, new ResultSetRowColumnData(false, names));
						
						data3.addRow(row3);
					}
				}
				
			} else {
				
				logger.logDebug("Ending flow, beneficiariesOperations processResponse error...");
				
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
			
			logger.logDebug("Ending flow, beneficiariesOperations processResponse failed with code: ");
			
			//data
			IResultSetRow row = new ResultSetRow();
			
			row.addRowData(1, new ResultSetRowColumnData(false, "false"));
			
			data.addRow(row);
			
			//data2
			IResultSetRow row2 = new ResultSetRow();
			
			row2.addRowData(1, new ResultSetRowColumnData(false, codeReturn.toString()));
			row2.addRowData(2, new ResultSetRowColumnData(false, anOriginalProcedureRes.getMessage(1).getMessageText()));
			
			data2.addRow(row2);
		}

		IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);
		IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);
		IResultSetBlock resultsetBlock3 = new ResultSetBlock(metaData3, data3);
		
		wProcedureResponse.setReturnCode(200);

		wProcedureResponse.addResponseBlock(resultsetBlock);
		wProcedureResponse.addResponseBlock(resultsetBlock2);
		wProcedureResponse.addResponseBlock(resultsetBlock3);
	
		return wProcedureResponse;		
	}
	
	private void notifyBeneficiaryOperation(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
        
        IProcedureRequest request = new ProcedureRequestAS();

        if (logger.isInfoEnabled()) {
            logger.logInfo(CLASS_NAME + " Entrando en notifyBeneficiaryOperation...");
        }
        
        String tittle = null;
        
        if (aBagSPJavaOrchestration.get("operation").toString().equals("I")) {
        	
        	tittle = "Alta de nuevo beneficiario";
        	
        } else if (aBagSPJavaOrchestration.get("operation").toString().equals("U")) {
        	
        	tittle = "Información de beneficiario actualizada";
        
        } else if (aBagSPJavaOrchestration.get("operation").toString().equals("D")) {
        	
        	tittle = "Baja de beneficiario";
        }
        
        request.setSpName("cob_bvirtual..sp_bv_enviar_notif_ib_api");

        request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
                IMultiBackEndResolverService.TARGET_LOCAL);
        request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
        
        request.addInputParam("@s_culture", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@s_culture"));
		request.addInputParam("@s_date", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@s_date"));
        
        request.addInputParam("@i_titulo", ICTSTypes.SQLVARCHAR, tittle);
        request.addInputParam("@i_notificacion", ICTSTypes.SQLVARCHAR, "N14");
        request.addInputParam("@i_servicio", ICTSTypes.SQLINTN, "8");
        request.addInputParam("@i_producto", ICTSTypes.SQLINTN, "18");
        request.addInputParam("@i_tipo", ICTSTypes.SQLVARCHAR, "M");
        request.addInputParam("@i_tipo_mensaje", ICTSTypes.SQLVARCHAR, "F");
        request.addInputParam("@i_print", ICTSTypes.SQLVARCHAR, "S");
        request.addInputParam("@i_ente_mis", ICTSTypes.SQLINTN, anOriginalRequest.readValueParam("@i_ente"));
        request.addInputParam("@i_ente_ib", ICTSTypes.SQLINTN, "0");
        request.addInputParam("@i_aux1", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("names").toString());
        request.addInputParam("@i_aux2", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("birthDate").toString());
        request.addInputParam("@i_aux3", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("relationship").toString());
        request.addInputParam("@i_aux4", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("percentage").toString());

        IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
        
        if (logger.isDebugEnabled()) {
            logger.logDebug("Response Corebanking DCO: "+wProductsQueryResp.getProcedureResponseAsString());
        }

        if (logger.isInfoEnabled()) {
            logger.logInfo(CLASS_NAME + " Saliendo de notifyBeneficiaryOperation...");
        }
    }
}
