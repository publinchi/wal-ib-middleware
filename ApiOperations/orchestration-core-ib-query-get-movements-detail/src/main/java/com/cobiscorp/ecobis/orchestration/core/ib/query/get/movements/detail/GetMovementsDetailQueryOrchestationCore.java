package com.cobiscorp.ecobis.orchestration.core.ib.query.get.movements.detail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
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

import com.cobiscorp.cobis.cts.utils.BDDUtil;


import Utils.ResponseMovements;
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
	 */
	public void setServiceIntegration(ICTSServiceIntegration serviceIntegration) {
		this.serviceIntegration = serviceIntegration;
	}

	/**
	 * Method that unset the instance of ICTSServiceIntegration
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

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);

		IProcedureResponse anProcedureResponse = new ProcedureResponseAS();
		anProcedureResponse = getMovementsDetail(anOriginalRequest);
		
		if (anProcedureResponse.getResultSets().size()>2) {
			
			proccessResponseCentralToObject(anProcedureResponse, aBagSPJavaOrchestration);
			
			if (!(Boolean) aBagSPJavaOrchestration.get("dataComrobanteExist")) {
				
				IProcedureResponse anProcedureResponseLocal = new ProcedureResponseAS();
				anProcedureResponseLocal = getMovementsDetailLocal(anOriginalRequest, aBagSPJavaOrchestration);
				
				return processTransformationResponse(anProcedureResponseLocal, aBagSPJavaOrchestration); 
			} else {
				return processTransformationResponse(anProcedureResponse, aBagSPJavaOrchestration);
			}
		} else {
			return processResponseError(anProcedureResponse);
		}
	}
	
	private IProcedureResponse getMovementsDetail(IProcedureRequest aRequest) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getMovementsDetail");
		}
		
		String minDate = aRequest.readValueParam("@i_fecha_ini");
		String maxDate = aRequest.readValueParam("@i_fecha_fin");
		
		if (minDate != null && !minDate.isEmpty() && !isDate(minDate)) {
			minDate = "01/01/1950";
		}
		
		if (maxDate != null && !maxDate.isEmpty() && !isDate(maxDate)) {
			maxDate = "01/01/1950";
		}

		request.setSpName("cob_ahorros..sp_tr04_cons_mov_ah_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@t_online", ICTSTypes.SYBCHAR, "S");
		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "A");
		request.addInputParam("@i_tipo", ICTSTypes.SQLCHAR, "T");
		
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_cliente"));
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta"));
		request.addInputParam("@i_nro_registros", ICTSTypes.SQLINT4, aRequest.readValueParam("@i_nro_registros"));
		request.addInputParam("@i_fecha_ini", ICTSTypes.SQLVARCHAR, minDate);
		request.addInputParam("@i_fecha_fin", ICTSTypes.SQLVARCHAR, maxDate);
		request.addInputParam("@i_sec_unico", ICTSTypes.SQLINT4, aRequest.readValueParam("@i_sec_unico"));
		request.addInputParam("@i_mov_id", ICTSTypes.SQLINT4, aRequest.readValueParam("@i_mov_id"));
		
		request.addInputParam("@i_servicio", ICTSTypes.SQLINT1, "8");
		request.addInputParam("@i_comision", ICTSTypes.SYBMONEYN, "0");
		request.addInputParam("@i_mon", ICTSTypes.SQLINT1, "0");
		request.addInputParam("@i_prod", ICTSTypes.SQLINT1, "4");
		request.addInputParam("@i_formato_fecha", ICTSTypes.SQLINT4, "101");
		
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getMovementsDetail");
		}

		return wProductsQueryResp;
	}

	private void proccessResponseCentralToObject(IProcedureResponse anOriginalProcedureRes, Map<String, Object> aBagSPJavaOrchestration) {
		
		List<ResponseMovements> responseMovementsList = new ArrayList<ResponseMovements>();
		
		IResultSetBlock resulsetOrigin = anOriginalProcedureRes.getResultSet(4);
		IResultSetRow[] rowsTemp = resulsetOrigin.getData().getRowsAsArray();
		
		boolean dataComrobanteExist = true;

		for (IResultSetRow iResultSetRow : rowsTemp) {
			
			ResponseMovements respMovement =  new ResponseMovements();
			IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
			
			respMovement.setFecha(columns[0].getValue());
			respMovement.setTransaccion(columns[1].getValue());
			respMovement.setCod_tran(columns[2].getValue());
			respMovement.setReferencia(columns[3].getValue());
			respMovement.setD_c(columns[4].getValue());
			respMovement.setValor(columns[5].getValue());
			respMovement.setContable(columns[6].getValue());
			respMovement.setDisponible(columns[7].getValue());
			respMovement.setSecuencial(columns[8].getValue());
			respMovement.setCod_alterno(columns[9].getValue());
			respMovement.setHora(columns[10].getValue());
			respMovement.setSec(Integer.parseInt(columns[11].getValue()));
			respMovement.setConcepto(columns[12].getValue());
			respMovement.setRastreo(columns[13].getValue());
			respMovement.setTarjetNumber(columns[14].getValue());
			respMovement.setUm_ssn_branch(Integer.parseInt(columns[16].getValue()));
			respMovement.setUm_secuencial(Integer.parseInt(columns[17].getValue()));
			respMovement.setNombreOrdenante(columns[18].getValue());
			respMovement.setCuentaOrdenante(columns[19].getValue());
			respMovement.setBancoOrdenante(columns[20].getValue());
			respMovement.setBancoBeneficiario(columns[21].getValue());
			respMovement.setReferenciaSpei(columns[22].getValue());
			respMovement.setRastreoSpei(columns[23].getValue());
			respMovement.setTrnReferencia(Integer.parseInt(columns[24].getValue()));
			
			if(null!= columns[15].getValue() && !"".equals(columns[15].getValue())) {
				
				respMovement.setDataComprobante(columns[15].getValue());
				String[] strBeneficiary = respMovement.getDataComprobante().split("\\|");
				
				if (strBeneficiary != null && strBeneficiary.length > 0 && strBeneficiary[0].contains("error")) {
					
					dataComrobanteExist = false;
					respMovement.setProblem(strBeneficiary[0]);
					
					if (respMovement.getProblem().trim().equals("error 3")) {
						if (strBeneficiary.length > 1)
							respMovement.setOne_dataComprobante(strBeneficiary[1]);
						else
							respMovement.setOne_dataComprobante(" ");
						
						if (strBeneficiary.length > 2)
							respMovement.setTwo_dataComprobante(strBeneficiary[2]);
						else
							respMovement.setTwo_dataComprobante(" ");
						
						if (strBeneficiary.length > 3)
							respMovement.setThree_dataComprobante(strBeneficiary[3]);
						else
							respMovement.setThree_dataComprobante(" ");
						
						if (strBeneficiary.length > 4)
							respMovement.setFour_dataComprobante(strBeneficiary[4]);
						else
							respMovement.setFour_dataComprobante("0");
						
						if (strBeneficiary.length > 5)
							respMovement.setFive_dataComprobante(strBeneficiary[5]);
						else
							respMovement.setFive_dataComprobante("0");
						
						if (strBeneficiary.length > 6)
							respMovement.setSix_dataComprobante(strBeneficiary[6]);
						else
							respMovement.setSix_dataComprobante("0");
						
						if (strBeneficiary.length > 7)
							respMovement.setSeven_dataComprobante(strBeneficiary[7]);
						else
							respMovement.setSeven_dataComprobante("");
						
						if (strBeneficiary.length > 8)
							respMovement.setEight_dataComprobante(strBeneficiary[8]);
						else
							respMovement.setEight_dataComprobante("");
						
						if (strBeneficiary.length > 9)
							respMovement.setNine_dataComprobante(strBeneficiary[9]);
						else
							respMovement.setNine_dataComprobante("");
						
						if (strBeneficiary.length > 10)
							respMovement.setTen_dataComprobante(strBeneficiary[10]);
						else
							respMovement.setTen_dataComprobante("");
						
						if (strBeneficiary.length > 11)
							respMovement.setEleven_dataComprobante(strBeneficiary[11]);
						else
							respMovement.setEleven_dataComprobante("");
						
						if (strBeneficiary.length > 12)
							respMovement.setTwelve_dataComprobante(strBeneficiary[12]);
						else
							respMovement.setTwelve_dataComprobante("");
						
						if (strBeneficiary.length > 13)
							respMovement.setThirteen_dataComprobante(strBeneficiary[13]);
						else
							respMovement.setThirteen_dataComprobante("");
						
						if (strBeneficiary.length > 14)
							respMovement.setFourteen_dataComprobante(strBeneficiary[14]);
						else
							respMovement.setFourteen_dataComprobante("");
						
						if (strBeneficiary.length > 15)
							respMovement.setFifteen_dataComprobante(strBeneficiary[15]);
						else
							respMovement.setFifteen_dataComprobante("");
						
						if (strBeneficiary.length > 16)
							respMovement.setSixteen_dataComprobante(strBeneficiary[16]);
						else
							respMovement.setSixteen_dataComprobante("");
		
					} else {
						
						respMovement.setOne_dataComprobante(" ");
						respMovement.setTwo_dataComprobante(" ");
						respMovement.setThree_dataComprobante(" ");
						respMovement.setFour_dataComprobante(" ");
						respMovement.setFive_dataComprobante(" ");
						respMovement.setSix_dataComprobante(" ");
						respMovement.setSeven_dataComprobante(" ");
						respMovement.setEight_dataComprobante(" ");
						respMovement.setNine_dataComprobante(" ");
						respMovement.setTen_dataComprobante(" ");
						respMovement.setEleven_dataComprobante(" ");
						respMovement.setTwelve_dataComprobante(" ");
						respMovement.setThirteen_dataComprobante(" ");
						respMovement.setFourteen_dataComprobante(" ");
						respMovement.setFifteen_dataComprobante(" ");
						respMovement.setSixteen_dataComprobante(" ");
					}
				}
				
			}else{
				respMovement.setDataComprobante(" | | |0|0|0| | | | | | | | | | ");
			}
			
			responseMovementsList.add(respMovement);
		}
		
		aBagSPJavaOrchestration.put("responseMovementsList", responseMovementsList);
		aBagSPJavaOrchestration.put("dataComrobanteExist", dataComrobanteExist);
		
	}


	private IProcedureResponse getMovementsDetailLocal(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getMovementsDetailLocal");
		}

		request.setSpName("cob_ahorros..sp_tr04_cons_mov_ah_local_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		List<ResponseMovements> responseMovementsList = (List<ResponseMovements>) aBagSPJavaOrchestration.get("responseMovementsList");
		String script = createScriptFromDataCentral(responseMovementsList);
		
		request.addInputParam("@i_script", ICTSTypes.SQLVARCHAR, script);
		
	
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getMovementsDetailLocal");
		}

		return wProductsQueryResp;
	}

	private String createScriptFromDataCentral(List<ResponseMovements> responseMovementsList) {
		String script = ""
				+ "IF OBJECT_ID('ultimos_movimientos_local') IS NOT NULL\r\n"
				+ "	BEGIN		\r\n"
				+ "		drop TABLE ultimos_movimientos_local\r\n"
				+ "	END\r\n"
				+ "	ELSE\r\n"
				+ "\r\n"
				+ "	create table ultimos_movimientos_local ( \r\n"
				+ "		fecha				varchar(250),\r\n"
				+ "		transaccion			varchar(250) null,\r\n"
				+ "		cod_tran			varchar(250) null,\r\n"
				+ "		referencia			varchar(250) null,\r\n"
				+ "		d_c					varchar(250) null,\r\n"
				+ "		valor				varchar(250) null,\r\n"
				+ "		contable			varchar(250) null,\r\n"
				+ "		disponible			varchar(250) null,\r\n"
				+ "		secuencial			varchar(250) null,\r\n"
				+ "		cod_alterno			varchar(250) null,\r\n"
				+ "		hora				varchar(250) null,\r\n"
				+ "		sec					int null,\r\n"
				+ "		concepto			varchar(250) null,\r\n"
				+ "		rastreo				varchar(250) null,\r\n"
				+ "		tarjetNumber		varchar(250) null,\r\n"
				+ "		dataComprobante		varchar(250) null,\r\n"
				+ "		um_ssn_branch       int			 null,	\r\n"
				+ "		um_secuencial       int			 null,\r\n"
				+ "		nombreOrdenante		varchar(250) null,\r\n"
				+ "		cuentaOrdenante		varchar(250) null,\r\n"
				+ "		bancoOrdenante		varchar(250) null,\r\n"
				+ "		bancoBeneficiario   varchar(250) null,\r\n"
				+ "		referenciaSpei		varchar(250) null,\r\n"
				+ "		rastreoSpei		    varchar(250) null,\r\n"
				+ "		trnReferencia       int			 null,\r\n"
				+ "		problem				varchar(250) null,\r\n"
				+ "		one_dataComprobante varchar(250) null,\r\n"
				+ "		two_dataComprobante varchar(250) null,\r\n"
				+ "		three_dataComprobante varchar(250) null,\r\n"
				+ "		four_dataComprobante varchar(250) null,\r\n"
				+ "		five_dataComprobante varchar(250) null,\r\n"
				+ "		six_dataComprobante varchar(250) null)\r\n"
				+ "		seven_dataComprobante varchar(250) null)\r\n"
				+ "		eight_dataComprobante varchar(250) null)\r\n"
				+ "		nine_dataComprobante varchar(250) null)\r\n"
				+ "		ten_dataComprobante varchar(250) null)\r\n"
				+ "		eleven_dataComprobante varchar(250) null)\r\n"
				+ "		twelve_dataComprobante varchar(250) null)\r\n"
				+ "		thirteen_dataComprobante varchar(250) null)\r\n"
				+ "		fourteen_dataComprobante varchar(250) null)\r\n"
				+ "		fifteen_dataComprobante varchar(250) null)\r\n"
				+ "		sixteen_dataComprobante varchar(250) null)\r\n"
				;
		for (ResponseMovements respMov : responseMovementsList) {
			script = script + "insert into ultimos_movimientos_local values (\r\n";
			script = script + (respMov.getFecha() != null ? "'" + respMov.getFecha() + "'" : "null") + ",";
			script = script + (respMov.getTransaccion() != null ? "'" + respMov.getTransaccion() + "'" : "null") + ",";
			script = script + (respMov.getCod_tran() != null ? "'" + respMov.getCod_tran() + "'" : "null") + ",";
			script = script + (respMov.getReferencia() != null ? "'" + respMov.getReferencia() + "'" : "null") + ",";
			script = script + (respMov.getD_c() != null ? "'" + respMov.getD_c() + "'" : "null") + ",";
			script = script + (respMov.getValor() != null ? "'" + respMov.getValor() + "'" : "null") + ",";
			script = script + (respMov.getContable() != null ? "'" + respMov.getContable() + "'" : "null") + ",";
			script = script + (respMov.getDisponible() != null ? "'" + respMov.getDisponible() + "'" : "null") + ",";
			script = script + (respMov.getSecuencial() != null ? "'" + respMov.getSecuencial() + "'" : "null") + ",";
			script = script + (respMov.getCod_alterno() != null ? "'" + respMov.getCod_alterno() + "'" : "null") + ",";
			script = script + (respMov.getHora() != null ? "'" + respMov.getHora() + "'" : "null") + ",";
			script = script + respMov.getSec() + ",";
			script = script + (respMov.getConcepto() != null ? "'" + respMov.getConcepto() + "'" : "null") + ",";
			script = script + (respMov.getRastreo() != null ? "'" + respMov.getRastreo() + "'" : "null") + ",";
			script = script + (respMov.getTarjetNumber() != null ? "'" + respMov.getTarjetNumber() + "'" : "null") + ",";
			script = script + (respMov.getDataComprobante() != null ? "'" + respMov.getDataComprobante() + "'" : "null") + ",";
			script = script + respMov.getUm_ssn_branch() + ",";
			script = script + respMov.getUm_secuencial() + ",";
			script = script + (respMov.getNombreOrdenante() != null ? "'" + respMov.getNombreOrdenante() + "'" : "null") + ",";
			script = script + (respMov.getCuentaOrdenante() != null ? "'" + respMov.getCuentaOrdenante() + "'" : "null") + ",";
			script = script + (respMov.getBancoOrdenante() != null ? "'" + respMov.getBancoOrdenante() + "'" : "null") + ",";
			script = script + (respMov.getBancoBeneficiario() != null ? "'" + respMov.getBancoBeneficiario() + "'" : "null") + ",";
			script = script + (respMov.getReferenciaSpei() != null ? "'" + respMov.getReferenciaSpei() + "'" : "null") + ",";
			script = script + (respMov.getRastreoSpei() != null ? "'" + respMov.getRastreoSpei() + "'" : "null") + ",";
			script = script + respMov.getTrnReferencia() + ",";
			script = script + (respMov.getProblem() != null ? "'" + respMov.getProblem() + "'" : "null") + ",";
			script = script + (respMov.getOne_dataComprobante() != null ? "'" + respMov.getOne_dataComprobante() + "'" : "null") + ",";
			script = script + (respMov.getTwo_dataComprobante() != null ? "'" +respMov.getTwo_dataComprobante() + "'" : "null") + ",";
			script = script + (respMov.getThree_dataComprobante() != null ? "'" +  respMov.getThree_dataComprobante() + "'" : "null") + ",";
			script = script + (respMov.getFour_dataComprobante() != null ? "'" + respMov.getFour_dataComprobante() + "'" : "null") + ",";
			script = script + (respMov.getFive_dataComprobante() != null ? "'" + respMov.getFive_dataComprobante() + "'" : "null") + ",";
			script = script + (respMov.getSix_dataComprobante() != null ? "'" + respMov.getSix_dataComprobante() + "'" : "null") + ")\r\n";
			script = script + (respMov.getSeven_dataComprobante() != null ? "'" + respMov.getSeven_dataComprobante() + "'" : "null") + ")\r\n";
			script = script + (respMov.getEight_dataComprobante() != null ? "'" + respMov.getEight_dataComprobante() + "'" : "null") + ")\r\n";
			script = script + (respMov.getNine_dataComprobante() != null ? "'" + respMov.getNine_dataComprobante() + "'" : "null") + ")\r\n";
			script = script + (respMov.getTen_dataComprobante() != null ? "'" + respMov.getTen_dataComprobante() + "'" : "null") + ")\r\n";
			script = script + (respMov.getEleven_dataComprobante() != null ? "'" + respMov.getEleven_dataComprobante() + "'" : "null") + ")\r\n";
			script = script + (respMov.getTwelve_dataComprobante() != null ? "'" + respMov.getTwelve_dataComprobante() + "'" : "null") + ")\r\n";
			script = script + (respMov.getThirteen_dataComprobante() != null ? "'" + respMov.getThirteen_dataComprobante() + "'" : "null") + ")\r\n";
			script = script + (respMov.getFourteen_dataComprobante() != null ? "'" + respMov.getFourteen_dataComprobante() + "'" : "null") + ")\r\n";
			script = script + (respMov.getFifteen_dataComprobante() != null ? "'" + respMov.getFifteen_dataComprobante() + "'" : "null") + ")\r\n";
			script = script + (respMov.getSixteen_dataComprobante() != null ? "'" + respMov.getSixteen_dataComprobante() + "'" : "null") + ")\r\n";
		}
		
		return script;
	}


	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalProcedureReq,
			Map<String, Object> aBagSPJavaOrchestration) {

		return null;
	}

	public IProcedureResponse processTransformationResponse(IProcedureResponse anOriginalProcedureRes, Map<String, Object> aBagSPJavaOrchestration) {
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
		
		// Agregar Header 1
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		
		metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));

		// Agregar Header 2
		IResultSetHeader metaData2 = new ResultSetHeader();
		IResultSetData data2 = new ResultSetData();
		
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));
		
		// Agregar Header 3
		IResultSetHeader metaData3 = new ResultSetHeader();
		IResultSetData data3 = new ResultSetData();
		
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("numberOfResults", ICTSTypes.SQLINT4, 5));
		
		// Agregar Data
		IResultSetRow row = new ResultSetRow();
		
		row.addRowData(1, new ResultSetRowColumnData(false, "0"));
		row.addRowData(2, new ResultSetRowColumnData(false, "Success"));
		data.addRow(row);

		IResultSetRow row2 = new ResultSetRow();
		
		row2.addRowData(1, new ResultSetRowColumnData(false, "true"));
		data2.addRow(row2);

		IResultSetRow row3 = new ResultSetRow();
		
		row3.addRowData(1, new ResultSetRowColumnData(false, anOriginalProcedureRes.getResultSetRowColumnData(3, 1, 1).getValue()));
		data3.addRow(row3);
			
		//Result Blocks
		IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);
		IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);
		IResultSetBlock resultsetBlock3 = new ResultSetBlock(metaData3, data3);
		
		anOriginalProcedureResponse.setReturnCode(200);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock2);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock3);
		
		//AccountStatementArray
		if (anOriginalProcedureRes != null
				&& anOriginalProcedureRes.getResultSet(4).getData().getRowsAsArray().length > 0) {

			if (logger.isInfoEnabled()) {
				logger.logInfo(
						CLASS_NAME + " Response final: " + anOriginalProcedureResponse.getProcedureResponseAsString());
			}

			IResultSetHeader metaData0 = new ResultSetHeader();
			
			//response
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("accountingBalance", ICTSTypes.SQLMONEY, 25));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("availableBalance", ICTSTypes.SQLMONEY, 25));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("movementType", ICTSTypes.SQLVARCHAR, 24));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("amount", ICTSTypes.SQLMONEY, 25));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("transactionDate", ICTSTypes.SQLVARCHAR, 12));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("operationType", ICTSTypes.SQLVARCHAR, 5));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("commission", ICTSTypes.SQLMONEY, 25));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("iva", ICTSTypes.SQLMONEY, 25));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("transactionReferenceNumber", ICTSTypes.SQLINT4, 64));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("description", ICTSTypes.SQLVARCHAR, 64));
			
			//cardDetails
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("maskedCardNumber", ICTSTypes.SQLVARCHAR, 20));
			
			//sourceAccount
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("ownerNameSA", ICTSTypes.SQLVARCHAR, 64));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("accountNumberSA", ICTSTypes.SQLVARCHAR, 24));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("bankNameSA", ICTSTypes.SQLVARCHAR, 32));
			
			//destinationAccount
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("ownerNameDA", ICTSTypes.SQLVARCHAR, 64));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("accountNumberDA", ICTSTypes.SQLVARCHAR, 24));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("bankNameDA", ICTSTypes.SQLVARCHAR, 32));
			
			//speiDetails
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("referenceCode", ICTSTypes.SQLVARCHAR, 18));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("trackingId", ICTSTypes.SQLVARCHAR, 30));
			
			//atmDetails
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("bankNameATM", ICTSTypes.SQLVARCHAR, 32));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("locationId", ICTSTypes.SQLVARCHAR, 18));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("transactionIdATM", ICTSTypes.SQLVARCHAR, 30));
			
			//merchantDetails
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("establishmentNameMD", ICTSTypes.SQLVARCHAR, 32));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("transactionIdMD", ICTSTypes.SQLVARCHAR, 30));
			
			//storeDetails
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("establishmentNameSD", ICTSTypes.SQLVARCHAR, 32));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("transactionIdSD", ICTSTypes.SQLVARCHAR, 30));
	
		
			IResultSetBlock resulsetOrigin = anOriginalProcedureRes.getResultSet(4);
			IResultSetRow[] rowsTemp = resulsetOrigin.getData().getRowsAsArray();
			IResultSetData data0 = new ResultSetData();
			
			for (IResultSetRow iResultSetRow : rowsTemp) {
				
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				IResultSetRow rowDat = new ResultSetRow();
				
				rowDat.addRowData(1, new ResultSetRowColumnData(false, columns[6].getValue()));
				rowDat.addRowData(2, new ResultSetRowColumnData(false, columns[7].getValue()));
				rowDat.addRowData(3, new ResultSetRowColumnData(false, columns[12].getValue()));//2?
				rowDat.addRowData(4, new ResultSetRowColumnData(false, columns[5].getValue()));
				rowDat.addRowData(5, new ResultSetRowColumnData(false, columns[0].getValue()));
				rowDat.addRowData(6, new ResultSetRowColumnData(false, columns[4].getValue()));
				rowDat.addRowData(9, new ResultSetRowColumnData(false, columns[24].getValue()));//8, 11?
				rowDat.addRowData(10, new ResultSetRowColumnData(false, columns[1].getValue().trim()));
				
				rowDat.addRowData(12, new ResultSetRowColumnData(false, columns[18].getValue()));
				rowDat.addRowData(13, new ResultSetRowColumnData(false, columns[19].getValue()));
				rowDat.addRowData(14, new ResultSetRowColumnData(false, columns[20].getValue()));
				
				rowDat.addRowData(17, new ResultSetRowColumnData(false, columns[21].getValue()));
				
				rowDat.addRowData(18, new ResultSetRowColumnData(false, columns[22].getValue()));
				rowDat.addRowData(19, new ResultSetRowColumnData(false, columns[23].getValue()));
				
	
				if(null!= columns[15].getValue() && !"".equals(columns[15].getValue())) {
					
					String[] strBeneficiary = columns[15].getValue().split("\\|");
					
					logger.logInfo("Prueba movements");
					
					for (int i = 0; i < strBeneficiary.length; i++)
					{
						logger.logInfo(strBeneficiary[i]);
					}
					
					logger.logInfo("Fin prueba movements");
				    if(strBeneficiary.length>0)
				    	rowDat.addRowData(16, new ResultSetRowColumnData(false, strBeneficiary[0]));
				    else
				    	rowDat.addRowData(16, new ResultSetRowColumnData(false, " "));
	
				    if(strBeneficiary.length>2)
				    	rowDat.addRowData(15, new ResultSetRowColumnData(false, strBeneficiary[2]));
				    else
				    	rowDat.addRowData(15, new ResultSetRowColumnData(false, " "));
				    
				    if(strBeneficiary.length>4)
				    	rowDat.addRowData(7, new ResultSetRowColumnData(false, strBeneficiary[4]));
				    else
				    	rowDat.addRowData(7, new ResultSetRowColumnData(false, "0"));
				    
				    if(strBeneficiary.length>5)
				    	rowDat.addRowData(8, new ResultSetRowColumnData(false, strBeneficiary[5]));
				    else
				    	rowDat.addRowData(8, new ResultSetRowColumnData(false, "0"));
				    
				    if(strBeneficiary.length>6)
				    	rowDat.addRowData(11, new ResultSetRowColumnData(false, strBeneficiary[6]));
				    else
				    	rowDat.addRowData(11, new ResultSetRowColumnData(false, " "));
				    
				    if(strBeneficiary.length>7)
				    	rowDat.addRowData(20, new ResultSetRowColumnData(false, strBeneficiary[7]));
				    else
				    	rowDat.addRowData(20, new ResultSetRowColumnData(false, " "));
				    
				    if(strBeneficiary.length>8)
				    	rowDat.addRowData(21, new ResultSetRowColumnData(false, strBeneficiary[8]));
				    else
				    	rowDat.addRowData(21, new ResultSetRowColumnData(false, " "));
				    
				    if(strBeneficiary.length>9)
				    	rowDat.addRowData(22, new ResultSetRowColumnData(false, strBeneficiary[9]));
				    else
				    	rowDat.addRowData(22, new ResultSetRowColumnData(false, " "));
				    
				    if(strBeneficiary.length>10)
				    	rowDat.addRowData(23, new ResultSetRowColumnData(false, strBeneficiary[10]));
				    else
				    	rowDat.addRowData(23, new ResultSetRowColumnData(false, " "));
				    
				    if(strBeneficiary.length>11)
				    	rowDat.addRowData(24, new ResultSetRowColumnData(false, strBeneficiary[11]));
				    else
				    	rowDat.addRowData(24, new ResultSetRowColumnData(false, " "));
				    
				    if(strBeneficiary.length>12)
				    	rowDat.addRowData(25, new ResultSetRowColumnData(false, strBeneficiary[12]));
				    else
				    	rowDat.addRowData(25, new ResultSetRowColumnData(false, " "));
				    
				    if(strBeneficiary.length>13)
				    	rowDat.addRowData(26, new ResultSetRowColumnData(false, strBeneficiary[13]));
				    else
				    	rowDat.addRowData(26, new ResultSetRowColumnData(false, " "));
				}
				else{
					rowDat.addRowData(16, new ResultSetRowColumnData(false, ""));
					rowDat.addRowData(15, new ResultSetRowColumnData(false, ""));
					rowDat.addRowData(7, new ResultSetRowColumnData(false, "0"));
					rowDat.addRowData(8, new ResultSetRowColumnData(false, "0"));
					rowDat.addRowData(11, new ResultSetRowColumnData(false, ""));
					rowDat.addRowData(20, new ResultSetRowColumnData(false, ""));
					rowDat.addRowData(21, new ResultSetRowColumnData(false, ""));
					rowDat.addRowData(22, new ResultSetRowColumnData(false, ""));
					rowDat.addRowData(23, new ResultSetRowColumnData(false, ""));
					rowDat.addRowData(24, new ResultSetRowColumnData(false, ""));
					rowDat.addRowData(25, new ResultSetRowColumnData(false, ""));
					rowDat.addRowData(26, new ResultSetRowColumnData(false, ""));
				}

				data0.addRow(rowDat);
			}

			IResultSetBlock resultsetBlock0 = new ResultSetBlock(metaData0, data0);
			anOriginalProcedureResponse.addResponseBlock(resultsetBlock0);

		}

		logger.logInfo(CLASS_NAME + "processTransformationResponse final dco" + anOriginalProcedureResponse.getProcedureResponseAsString());
		return anOriginalProcedureResponse;
	}
	
	public IProcedureResponse processResponseError(IProcedureResponse anOriginalProcedureRes) {
		if (logger.isInfoEnabled()) {
			logger.logInfo(" start processResponseAccounts--->");
		}

		IProcedureResponse anOriginalProcedureResponse = new ProcedureResponseAS();
		
		// Agregar Header 1
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));

		// Agregar Header 2
		IResultSetHeader metaData2 = new ResultSetHeader();
		IResultSetData data2 = new ResultSetData();
		
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));

		
		// Agregar Data
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
	
	public static boolean isDate(String date) {
        try {
        	
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            
            dateFormat.setLenient(false);
            dateFormat.parse(date);
            
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

	private boolean dataComprobanteExists(IResultSetRow[] rowsTemp) {
		for (IResultSetRow iResultSetRow : rowsTemp) {
			IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

			if(null!= columns[15].getValue() && !"".equals(columns[15].getValue())) {
				return true;
			} else {
				return false;
			}		
		}
		return true;		
	}
}
