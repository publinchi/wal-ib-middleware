package com.cobiscorp.ecobis.orchestration.core.ib.query.get.movements.detail;

import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.CISResponseManagmentHelper;
import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
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

import cobiscorp.ecobis.cts.integration.services.ICTSServiceIntegration;

/**
 * Generated Transaction Factor
 * 
 * @since Mar 14, 2023
 * @author dcollaguazo
 * @version 1.0.0
 * 
 */
@Component(name = "GetMovementsDetailQueryOrchestationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "GetMovementsDetailQueryOrchestationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "GetMovementsDetailQueryOrchestationCore") })
public class GetMovementsDetailQueryOrchestationCore extends SPJavaOrchestrationBase {

	private static ILogger logger = LogFactory.getLogger(GetMovementsDetailQueryOrchestationCore.class);
	private static final String CLASS_NAME = "GetMovementsDetailQueryOrchestationCore--->";
	// private static final String SERVICE_OUTPUT_VALUES =
	// "com.cobiscorp.cobis.cts.service.response.output";

	CISResponseManagmentHelper cisResponseHelper = new CISResponseManagmentHelper();

	protected static final int CHANNEL_REQUEST = 8;

	/**
	 * Instance of ICTSServiceIntegration
	 */
	@Reference(bind = "setServiceIntegration", unbind = "unsetServiceIntegration", cardinality = ReferenceCardinality.OPTIONAL_UNARY)
	private ICTSServiceIntegration serviceIntegration;

	/**
	 * Method that set the instance of ICTSServiceIntegration
	 * 
	 * @param serviceIntegration
	 *            Instance of ICTSServiceIntegration
	 */
	public void setServiceIntegration(ICTSServiceIntegration serviceIntegration) {
		this.serviceIntegration = serviceIntegration;
	}

	/**
	 * Method that unset the instance of ICTSServiceIntegration
	 * 
	 * @param serviceIntegration
	 *            Instance of ICTSServiceIntegration
	 */
	public void unsetServiceIntegration(ICTSServiceIntegration serviceIntegration) {
		this.serviceIntegration = null;
	}

	/**
	 * Read configuration of parent component
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
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

		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);

		IProcedureResponse anProcedureResponse = new ProcedureResponseAS();
		anProcedureResponse = getMovementsDetail(anOriginalRequest);
		
		if (anProcedureResponse.getResultSets().size()>2) {
			
			return processTransformationResponse(anProcedureResponse);
		} else {
			return processResponseError(anProcedureResponse);
		}

	}

	public IProcedureResponse processResponseError(IProcedureResponse anOriginalProcedureRes) {
		if (logger.isInfoEnabled()) {
			logger.logInfo(" start processResponseAccounts--->");
		}

		IProcedureResponse anOriginalProcedureResponse = new ProcedureResponseAS();

		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));

		// Agregar Header 2
		IResultSetHeader metaData2 = new ResultSetHeader();
		IResultSetData data2 = new ResultSetData();
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));

		IResultSetRow row = new ResultSetRow();

		row.addRowData(1,
				new ResultSetRowColumnData(false, anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).getValue()));
		row.addRowData(2, new ResultSetRowColumnData(false, anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 2).getValue()));

		data.addRow(row);

		IResultSetRow row2 = new ResultSetRow();
		row2.addRowData(1, new ResultSetRowColumnData(false, anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).getValue()));
		data2.addRow(row2);

		IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);
		IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);
		
		anOriginalProcedureResponse.setReturnCode(200);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock2);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock);

		return anOriginalProcedureResponse;
	}


	private IProcedureResponse getMovementsDetail(IProcedureRequest aRequest) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getMovementsDetail");
		}

		request.setSpName("cob_ahorros..sp_tr04_cons_mov_ah_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "A");
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_cliente"));
		request.addInputParam("@t_online", ICTSTypes.SYBCHAR, "S");
		request.addInputParam("@i_comision", ICTSTypes.SYBMONEYN, "0");
		request.addInputParam("@i_servicio", ICTSTypes.SQLINT1, "8");
		request.addInputParam("@i_mon", ICTSTypes.SQLINT1, "0");
		request.addInputParam("@i_prod", ICTSTypes.SQLINT1, "4");
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta"));
		request.addInputParam("@i_formato_fecha", ICTSTypes.SQLINT4, "101");
		request.addInputParam("@i_operacion",  ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_operacion"));
		request.addInputParam("@i_nro_registros", ICTSTypes.SQLINT4, aRequest.readValueParam("@i_nro_registros"));
		request.addInputParam("@i_tipo", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_tipo"));
		request.addInputParam("@i_fecha_ini", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_fecha_ini"));
		request.addInputParam("@i_fecha_fin", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_fecha_fin"));
		request.addInputParam("@i_sec_unico", ICTSTypes.SQLINT4, aRequest.readValueParam("@i_sec_unico"));
		request.addInputParam("@i_mov_id", ICTSTypes.SQLINT4, aRequest.readValueParam("@i_mov_id"));

		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getMovementsDetail");
		}

		return wProductsQueryResp;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalProcedureReq,
			Map<String, Object> aBagSPJavaOrchestration) {

		return null;
	}

	public IProcedureResponse processTransformationResponse(IProcedureResponse anOriginalProcedureRes) {
		if (logger.isInfoEnabled()) {
			logger.logInfo(" start processTransformationResponse--->");
		}

		IProcedureResponse anOriginalProcedureResponse = new ProcedureResponseAS();

		if (anOriginalProcedureRes != null) {

			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + " ProcessResponse original anOriginalProcedureRes:"
						+ anOriginalProcedureRes.getProcedureResponseAsString());
			}

		}

		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));

		// Agregar Header 2
		IResultSetHeader metaData2 = new ResultSetHeader();
		IResultSetData data2 = new ResultSetData();
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));

		IResultSetHeader metaData3 = new ResultSetHeader();
		IResultSetData data3 = new ResultSetData();
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("numberOfResults", ICTSTypes.SQLINT4, 5));
		
		
		IResultSetRow row = new ResultSetRow();
		row.addRowData(1, new ResultSetRowColumnData(false, "0"));
		row.addRowData(2, new ResultSetRowColumnData(false, "success"));
		data.addRow(row);

		IResultSetRow row2 = new ResultSetRow();
		row2.addRowData(1, new ResultSetRowColumnData(false, "true"));
		data2.addRow(row2);

		IResultSetRow row3 = new ResultSetRow();
		row3.addRowData(1, new ResultSetRowColumnData(false, anOriginalProcedureRes.getResultSetRowColumnData(3, 1, 1).getValue()));
		data3.addRow(row3);
		
		logger.logInfo("XDC NUM" + anOriginalProcedureRes.getResultSetRowColumnData(3, 1, 1).getValue());
		
		IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);
		IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);
		IResultSetBlock resultsetBlock3 = new ResultSetBlock(metaData3, data3);
		anOriginalProcedureResponse.setReturnCode(200);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock2);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock3);

		if (anOriginalProcedureRes != null
				&& anOriginalProcedureRes.getResultSet(4).getData().getRowsAsArray().length > 0) {

			if (logger.isInfoEnabled()) {
				logger.logInfo(
						CLASS_NAME + " Response final: " + anOriginalProcedureResponse.getProcedureResponseAsString());
			}
			// anOriginalProcedureResponse.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT,
			// ICOBISTS.HEADER_STRING_TYPE,
			// ICSP.ERROR_EXECUTION_SERVICE);

			// anOriginalProcedureResponse = new ProcedureResponseAS();

			IResultSetHeader metaData0 = new ResultSetHeader();
			
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("accountingBalance", ICTSTypes.SQLMONEY, 25));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("alternateCode", ICTSTypes.SQLINT4, 5));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("amount", ICTSTypes.SQLMONEY, 25));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("availableBalance", ICTSTypes.SQLMONEY, 25));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("concept", ICTSTypes.SQLVARCHAR, 20));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("description", ICTSTypes.SQLVARCHAR, 64));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("hour", ICTSTypes.SQLVARCHAR, 12));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("image", ICTSTypes.SQLVARCHAR, 64));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("tracking", ICTSTypes.SQLVARCHAR, 32));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("operationType", ICTSTypes.SQLVARCHAR, 5));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("reference", ICTSTypes.SQLINT4, 12));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("sequential", ICTSTypes.SQLINT4, 12));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("signDC", ICTSTypes.SQLVARCHAR, 5));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("transactionDate", ICTSTypes.SQLVARCHAR, 12));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("uniqueSequential", ICTSTypes.SQLINT4, 12));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("processDate", ICTSTypes.SQLVARCHAR, 12));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("tarjetNumber", ICTSTypes.SQLVARCHAR, 64));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("destinyAccount", ICTSTypes.SQLVARCHAR, 12));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("typeAccount", ICTSTypes.SQLVARCHAR, 5));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("beneficiary", ICTSTypes.SQLVARCHAR, 32));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("referenceNumber", ICTSTypes.SQLINT4, 64));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("commission", ICTSTypes.SQLMONEY, 25));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("iva", ICTSTypes.SQLMONEY, 25));

			IResultSetBlock resulsetOrigin = anOriginalProcedureRes.getResultSet(4);
			IResultSetRow[] rowsTemp = resulsetOrigin.getData().getRowsAsArray();
			IResultSetData data0 = new ResultSetData();

			for (IResultSetRow iResultSetRow : rowsTemp) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

				IResultSetRow rowDat = new ResultSetRow();

				rowDat.addRowData(1, new ResultSetRowColumnData(false, columns[6].getValue()));
				rowDat.addRowData(2, new ResultSetRowColumnData(false, columns[9].getValue()));
				rowDat.addRowData(3, new ResultSetRowColumnData(false, columns[5].getValue()));
				rowDat.addRowData(4, new ResultSetRowColumnData(false, columns[7].getValue()));
				rowDat.addRowData(5, new ResultSetRowColumnData(false, columns[13].getValue()));
				rowDat.addRowData(6, new ResultSetRowColumnData(false, columns[1].getValue().trim()));
				rowDat.addRowData(7, new ResultSetRowColumnData(false, columns[10].getValue()));
				rowDat.addRowData(8, new ResultSetRowColumnData(false, columns[12].getValue()));
				rowDat.addRowData(9, new ResultSetRowColumnData(false, columns[14].getValue()));
				rowDat.addRowData(10, new ResultSetRowColumnData(false, columns[2].getValue()));
				rowDat.addRowData(11, new ResultSetRowColumnData(false, columns[3].getValue()));
				rowDat.addRowData(12, new ResultSetRowColumnData(false, columns[8].getValue()));
				rowDat.addRowData(13, new ResultSetRowColumnData(false, columns[4].getValue()));
				rowDat.addRowData(14, new ResultSetRowColumnData(false, columns[0].getValue()));
				rowDat.addRowData(15, new ResultSetRowColumnData(false, columns[11].getValue()));
				rowDat.addRowData(16, new ResultSetRowColumnData(false, columns[0].getValue()));
				rowDat.addRowData(17, new ResultSetRowColumnData(false, columns[15].getValue()));
				
				if(null!= columns[16].getValue() && !"".equals(columns[16].getValue())) {
					String[] strBeneficiary = columns[16].getValue().split("\\|");
					
				    if(strBeneficiary.length>0)
				    	rowDat.addRowData(18, new ResultSetRowColumnData(false, strBeneficiary[0]));
		        	
				    if(strBeneficiary.length>1)
				    	rowDat.addRowData(19, new ResultSetRowColumnData(false, strBeneficiary[1]));
				    
				    if(strBeneficiary.length>2)
					
				    	rowDat.addRowData(20, new ResultSetRowColumnData(false, strBeneficiary[2]));
				    if(strBeneficiary.length>3)
					
				    	rowDat.addRowData(21, new ResultSetRowColumnData(false, strBeneficiary[3]));
				    if(strBeneficiary.length>4)
					
				    	rowDat.addRowData(22, new ResultSetRowColumnData(false, strBeneficiary[4]));
				    if(strBeneficiary.length>5)
					
				    	rowDat.addRowData(23, new ResultSetRowColumnData(false, strBeneficiary[5]));
				}
				else{
					rowDat.addRowData(18, new ResultSetRowColumnData(false, " "));
					rowDat.addRowData(19, new ResultSetRowColumnData(false, " "));
					rowDat.addRowData(20, new ResultSetRowColumnData(false, " "));
					rowDat.addRowData(21, new ResultSetRowColumnData(false, "0"));
					rowDat.addRowData(22, new ResultSetRowColumnData(false, "0"));
					rowDat.addRowData(23, new ResultSetRowColumnData(false, "0"));
				}

				data0.addRow(rowDat);

			}

			IResultSetBlock resultsetBlock0 = new ResultSetBlock(metaData0, data0);
			anOriginalProcedureResponse.addResponseBlock(resultsetBlock0);

		}

		logger.logInfo(CLASS_NAME + "processTransformationResponse final dco" + anOriginalProcedureResponse.getProcedureResponseAsString());
		return anOriginalProcedureResponse;
	}


}
