package com.cobiscorp.ecobis.orchestration.core.ib.query.get.movements.detail;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

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
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
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
import com.cobiscorp.ecobis.ib.application.dtos.ServerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;

import Utils.*;
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
	private static final int ERROR40004 = 40004;
	private static final int ERROR40003 = 40003;
	private static final int ERROR40002 = 40002;
	private String cuenta;

	CISResponseManagmentHelper cisResponseHelper = new CISResponseManagmentHelper();

	protected static final int CHANNEL_REQUEST = 8;
	private static final String INRO_REGISTRO = "@i_nro_registros";
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

	/*
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
													   Map<String, Object> aBagSPJavaOrchestration) {

		String cuenta = anOriginalRequest.readValueParam("@i_cta");
		if(getOperation(cuenta).equals("X")){
			return executeNewJavaOrchestration(anOriginalRequest, aBagSPJavaOrchestration);
		}
		//String showFailed = anOriginalRequest.readValueParam("@i_show_failed") != null ? anOriginalRequest.readValueParam("@i_show_failed") : "N";

		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
		IProcedureResponse anProcedureResponseCentral;
		IProcedureResponse anProcedureResponse = new ProcedureResponseAS();

		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setChannelId("8");
		ServerResponse responseServer = null;
		int numRegistros = 10;
		try
		{
			responseServer = getServerStatus(serverRequest);
		} catch (CTSServiceException e)
		{
			if(logger.isErrorEnabled())
			{
				logger.logError("ResponseServer is null validate is online server code:"+e.getMessage());
			}

		} catch (CTSInfrastructureException e)
		{
			if(logger.isErrorEnabled())
			{
				logger.logError("ResponseServer is null validate is online server code:"+e.getMessage());
			}
		}

		if (responseServer == null)
		{
			if(logger.isErrorEnabled())
			{
				logger.logError("ResponseServer is null validate is online server code:"+responseServer.getReturnCode());
			}
			return processResponseError(anProcedureResponse);
		} else
		{
			if(logger.isDebugEnabled())
			{
				logger.logDebug("ResponseServer is not null");
			}
			if (responseServer.getOnLine())
			{
				if(logger.isDebugEnabled())
				{
					logger.logDebug("server is online");
				}
				anProcedureResponse = getMovementsDetail(anOriginalRequest, IMultiBackEndResolverService.TARGET_CENTRAL);
			} else {
				if(logger.isDebugEnabled())
				{
					logger.logDebug("server is offline");
				}

				if(anOriginalRequest.readValueParam(INRO_REGISTRO)!= null && anOriginalRequest.readValueParam(INRO_REGISTRO).matches("\\d+")){
					numRegistros = Integer.parseInt(anOriginalRequest.readValueParam(INRO_REGISTRO));
					logger.logDebug("numRegistros" + numRegistros);
				}

				anProcedureResponse = getMovementsDetail(anOriginalRequest, IMultiBackEndResolverService.TARGET_LOCAL);

				int numberOfRecordsLocal = 0;
				int numberOfRecords = 0;

				if (anProcedureResponse != null && !anProcedureResponse.getResultSets().isEmpty()) {
					numberOfRecordsLocal = anProcedureResponse.getResultSet(4).getData().getRowsAsArray().length;
				}
				if (numberOfRecordsLocal < numRegistros) {
					numberOfRecords = numRegistros - numberOfRecordsLocal;


					anOriginalRequest.setValueParam(INRO_REGISTRO, String.valueOf(numberOfRecords));
					anProcedureResponseCentral = getMovementsDetail(anOriginalRequest, IMultiBackEndResolverService.TARGET_CENTRAL);

					llenarRegistrosLocal(anProcedureResponse,anProcedureResponseCentral);
				}

			}
		}

		if (anProcedureResponse.getResultSets().size()>2) {

			proccessResponseCentralToObject(anProcedureResponse, aBagSPJavaOrchestration);

			if (!(Boolean) aBagSPJavaOrchestration.get("dataComrobanteExist")) {

				IProcedureResponse anProcedureResponseLocal;
				anProcedureResponseLocal = getMovementsDetailLocal(anOriginalRequest, aBagSPJavaOrchestration);

				return processTransformationResponse(anProcedureResponseLocal, aBagSPJavaOrchestration);
			} else {
				return processTransformationResponse(anProcedureResponse, aBagSPJavaOrchestration);
			}
		} else {
			return processResponseError(anProcedureResponse);
		}
	} */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
													   Map<String, Object> aBagSPJavaOrchestration) {

		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
		IProcedureResponse anProcedureResponseCentral;
		IProcedureResponse anProcedureResponse = new ProcedureResponseAS();


		String minDate = anOriginalRequest.readValueParam("@i_fecha_ini");
		String maxDate = anOriginalRequest.readValueParam("@i_fecha_fin");


		if(minDate == null || minDate.equals("null")){
			minDate  = null;
		} else if (!minDate.isEmpty() && !isDate(minDate)) {
			minDate = null;
		}
		if(maxDate == null || maxDate.equals("null")){
			maxDate  = null;
		} else if (!maxDate.isEmpty() && !isDate(maxDate)) {
			maxDate = null;
		}
		String showFailed = anOriginalRequest.readValueParam("@i_show_failed") != null ? anOriginalRequest.readValueParam("@i_show_failed") : "N";
		aBagSPJavaOrchestration.put("DATE_FILTER", ( minDate == null ||  maxDate  == null) ? "N" : "S");
		aBagSPJavaOrchestration.put(INRO_REGISTRO+"_ORG", anOriginalRequest.readValueParam(INRO_REGISTRO));
		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setChannelId("8");
		ServerResponse responseServer = null;
		int numRegistros = 10;
		try
		{
			responseServer = getServerStatus(serverRequest);
		} catch (CTSServiceException e)
		{
			if(logger.isErrorEnabled())
			{
				logger.logError("ResponseServer is null validate is online server code:"+e.getMessage());
			}

		} catch (CTSInfrastructureException e)
		{
			if(logger.isErrorEnabled())
			{
				logger.logError("ResponseServer is null validate is online server code:"+e.getMessage());
			}
		}

		if (responseServer == null)
		{
			if(logger.isErrorEnabled())
			{
				logger.logError("ResponseServer is null validate is online server code:"+responseServer.getReturnCode());
			}
			return processResponseError(anProcedureResponse);
		} else
		{
			if(logger.isDebugEnabled())
			{
				logger.logDebug("ResponseServer is not null");
			}
			if (responseServer.getOnLine())
			{
				if(logger.isDebugEnabled())
				{
					logger.logDebug("server is online");
				}
				anProcedureResponse = getMovementsDetail(anOriginalRequest, IMultiBackEndResolverService.TARGET_CENTRAL);

			} else {
				if(logger.isDebugEnabled())
				{
					logger.logDebug("server is offline");
				}

				if(anOriginalRequest.readValueParam(INRO_REGISTRO)!= null && anOriginalRequest.readValueParam(INRO_REGISTRO).matches("\\d+")){
					numRegistros = Integer.parseInt(anOriginalRequest.readValueParam(INRO_REGISTRO));
					logger.logDebug("numRegistros" + numRegistros);
				}

				anProcedureResponse = getMovementsDetail(anOriginalRequest, IMultiBackEndResolverService.TARGET_LOCAL);
				if(anProcedureResponse.getResultSets().size()>2){
					int numberOfRecordsLocal = 0;
					int numberOfRecords = 0;

					if (anProcedureResponse != null && !anProcedureResponse.getResultSets().isEmpty()) {
						numberOfRecordsLocal = anProcedureResponse.getResultSet(4).getData().getRowsAsArray().length;
					}
					if (numberOfRecordsLocal < numRegistros) {
						numberOfRecords = numRegistros - numberOfRecordsLocal;


						anOriginalRequest.setValueParam(INRO_REGISTRO, String.valueOf(numberOfRecords));
						anProcedureResponseCentral = getMovementsDetail(anOriginalRequest, IMultiBackEndResolverService.TARGET_CENTRAL);

						llenarRegistrosLocal(anProcedureResponse,anProcedureResponseCentral);
					}
				}
			}
			aBagSPJavaOrchestration.put("RESPONSE_MOVEMENTS",anProcedureResponse);
			if( "S".equals(showFailed)){
				IProcedureResponse failedMovementDetails = getFailedMovementsDetail(anOriginalRequest);
				aBagSPJavaOrchestration.put("RESPONSE_FAILED_MOVEMENTS",failedMovementDetails);
			}
			aBagSPJavaOrchestration.put("ORDER",anOriginalRequest.readValueParam("@i_ordenamiento"));
			aBagSPJavaOrchestration.put(INRO_REGISTRO,anOriginalRequest.readValueParam(INRO_REGISTRO));
		}

		if (anProcedureResponse.getResultSets().size()>2) {

			return processNewTransformationResponse(aBagSPJavaOrchestration);
		} else {
			return processResponseError(anProcedureResponse);
		}
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
	private IProcedureResponse getMovementsDetail(IProcedureRequest aRequest, String targetServer) {
		cuenta = aRequest.readValueParam("@i_cta");
		String operacion = getOperation(cuenta);
		cuenta = cuenta.replace("*","");
		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + " Entrando en getMovementsDetail :"+targetServer );
		}

		String minDate = aRequest.readValueParam("@i_fecha_ini");
		String maxDate = aRequest.readValueParam("@i_fecha_fin");

		if(minDate.equals("null")){
			minDate  = "";
		} else if (minDate != null && !minDate.isEmpty() && !isDate(minDate)) {
			minDate = "01/01/1910";
		}

		if(maxDate.equals("null")){
			maxDate  = "";
		} else if (maxDate != null && !maxDate.isEmpty() && !isDate(maxDate)) {
			maxDate = "01/01/1910";
		}


		request.setSpName("cob_ahorros..sp_tr04_cons_mov_ah_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, targetServer);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		request.addInputParam("@x_request_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_request_id"));
		request.addInputParam("@x_end_user_request_date", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_end_user_request_date"));
		request.addInputParam("@x_end_user_ip", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_end_user_ip"));
		request.addInputParam("@x_channel", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_channel"));

		request.addInputParam("@t_online", ICTSTypes.SYBCHAR, "S");
		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "A");
		request.addInputParam("@i_tipo", ICTSTypes.SQLCHAR, "T");

		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_cliente"));
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, cuenta);
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

	private IProcedureResponse getFailedMovementsDetail(IProcedureRequest aRequest) {
		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + " Entrando en getMovementsDetail : LOCAL" );
		}

		cuenta = aRequest.readValueParam("@i_cta");
		cuenta = cuenta.replace("*","");

		String minDate = aRequest.readValueParam("@i_fecha_ini");
		String maxDate = aRequest.readValueParam("@i_fecha_fin");

		if(minDate.equals("null")){
			minDate  = "";
		} else if (minDate != null && !minDate.isEmpty() && !isDate(minDate)) {
			minDate = "01/01/1910";
		}

		if(maxDate.equals("null")){
			maxDate  = "";
		} else if (maxDate != null && !maxDate.isEmpty() && !isDate(maxDate)) {
			maxDate = "01/01/1910";
		}


		request.setSpName("cob_ahorros..sp_tr04_cons_mov_ah_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, "local");
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		request.addInputParam("@x_request_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_request_id"));
		request.addInputParam("@x_end_user_request_date", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_end_user_request_date"));
		request.addInputParam("@x_end_user_ip", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_end_user_ip"));
		request.addInputParam("@x_channel", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_channel"));

		request.addInputParam("@t_online", ICTSTypes.SYBCHAR, "S");
		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "F");
		request.addInputParam("@i_tipo", ICTSTypes.SQLCHAR, "T");

		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_cliente"));
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, cuenta);
		request.addInputParam("@i_nro_registros", ICTSTypes.SQLINT4, aRequest.readValueParam("@i_nro_registros"));
		request.addInputParam("@i_fecha_ini", ICTSTypes.SQLVARCHAR, minDate);
		request.addInputParam("@i_fecha_fin", ICTSTypes.SQLVARCHAR, maxDate);
		request.addInputParam("@i_sec_unico", ICTSTypes.SQLINT4, aRequest.readValueParam("@i_sec_unico"));
		request.addInputParam("@i_mov_id", ICTSTypes.SQLINT4, aRequest.readValueParam("@i_mov_id"));
		request.addInputParam("@i_ordenamiento", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_ordenamiento")!= null ? aRequest.readValueParam("@i_ordenamiento"):"DESC");

		request.addInputParam("@i_servicio", ICTSTypes.SQLINT1, "8");
		request.addInputParam("@i_comision", ICTSTypes.SYBMONEYN, "0");
		request.addInputParam("@i_mon", ICTSTypes.SQLINT1, "0");
		request.addInputParam("@i_prod", ICTSTypes.SQLINT1, "4");
		request.addInputParam("@i_formato_fecha", ICTSTypes.SQLINT4, "101");
		request.addOutputParam("@o_total_registros", ICTSTypes.SQLINT4,"0");


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

		boolean dataComrobanteExist = false;

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
			respMovement.setIe_request(columns[25].getValue());
			respMovement.setIe_ente(columns[26].getValue());
			respMovement.setCausa(columns[27].getValue());
			respMovement.setIva(columns[28].getValue());
			respMovement.setComision(columns[29].getValue());
			respMovement.setUm_correccion(columns[30].getValue());

			String um_sec_correccion = columns[31].getValue();

			if(um_sec_correccion != null) {
				respMovement.setUm_sec_correccion(Integer.parseInt(um_sec_correccion));
			}

			if(null!= columns[15].getValue() && !"".equals(columns[15].getValue())) {

				respMovement.setDataComprobante(columns[15].getValue());
				String[] strBeneficiary = respMovement.getDataComprobante().split("\\|");

				if (strBeneficiary != null && strBeneficiary.length > 0 && strBeneficiary[0].contains("error")) {

					respMovement.setProblem(strBeneficiary[0]);
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
				} else {
					if (strBeneficiary.length > 0)
						respMovement.setOne_dataComprobante(strBeneficiary[0]);
					else
						respMovement.setOne_dataComprobante(" ");

					if (strBeneficiary.length > 1)
						respMovement.setTwo_dataComprobante(strBeneficiary[1]);
					else
						respMovement.setTwo_dataComprobante(" ");

					if (strBeneficiary.length > 2)
						respMovement.setThree_dataComprobante(strBeneficiary[2]);
					else
						respMovement.setThree_dataComprobante(" ");

					if (strBeneficiary.length > 3)
						respMovement.setFour_dataComprobante(strBeneficiary[3]);
					else
						respMovement.setFour_dataComprobante("0");

					if (strBeneficiary.length > 4)
						respMovement.setFive_dataComprobante(strBeneficiary[4]);
					else
						respMovement.setFive_dataComprobante("0");

					if (strBeneficiary.length > 5)
						respMovement.setSix_dataComprobante(strBeneficiary[5]);
					else
						respMovement.setSix_dataComprobante("0");
				}

			}else{
				respMovement.setDataComprobante(" | | |0|0|0");
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
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta"));
		request.addInputParam("@i_nro_registros", ICTSTypes.SQLINT4, aRequest.readValueParam("@i_nro_registros"));
		request.addInputParam("@i_sec_unico", ICTSTypes.SQLINT4, aRequest.readValueParam("@i_sec_unico"));


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
		String script = "";
				/*+ "	create table #ultimos_movimientos_local ( \r\n"
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
				+ "		um_ssn_branch       int			 null,\r\n"
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
				+ "		six_dataComprobante varchar(250) null,\r\n"
				+ "		ie_request varchar(max) null,\r\n"
				+ "		ie_ente int null,\r\n"
				+ "		causa int null)\r\n";*/

		for (ResponseMovements respMov : responseMovementsList) {
			script = script + "insert into #ultimos_movimientos_local values (\r\n";
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
			script = script + (respMov.getSix_dataComprobante() != null ? "'" + respMov.getSix_dataComprobante() + "'" : "null") + ",";
			script = script + (respMov.getIe_request() != null ? "'" + respMov.getIe_request() + "'" : "null") + ",";
			script = script + (respMov.getIe_ente() != null ? respMov.getIe_ente() : "null") + ",";
			script = script + (respMov.getCausa() != null ? respMov.getCausa() : "null") + ",";
			script = script + (respMov.getIva() != null ? respMov.getIva() : "null") + ",";
			script = script + (respMov.getComision() != null ? respMov.getComision() : "null") + ",";
			script = script + (respMov.getUm_correccion() != null ? "'" + respMov.getUm_correccion() + "'" : "null") + ",";
			script = script + (respMov.getUm_sec_correccion() != null ? respMov.getUm_sec_correccion() : "null") + ",";
			script = script + "null, null, null, null, null, null,  null, null, null, null,";
			script = script + "null, null, null, null, null, null,  null, null, null, null, null, null, null, null)\r\n";
		}

		return script;
	}


	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalProcedureReq,
											  Map<String, Object> aBagSPJavaOrchestration) {

		return null;
	}

	public IProcedureResponse processNewTransformationResponse(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled()) {
			logger.logInfo(" start processTransformationResponse--->");
		}
		IProcedureResponse anOriginalProcedureResponse = new ProcedureResponseAS();
		IProcedureResponse anOriginalProcedureRes = (IProcedureResponse) aBagSPJavaOrchestration.get("RESPONSE_MOVEMENTS");
		IProcedureResponse anOriginalProcedureResF = null;
		boolean showFailed = false;

		int numberOfResults = 0;
		int totalNumberOfResults = 0;
		int numberOfResultsSuccess = anOriginalProcedureRes.getResultSet(4).getData().getRowsAsArray().length;
		int numberOfResultsShow = 0 ;

		if((aBagSPJavaOrchestration.get(INRO_REGISTRO+"_ORG")==null ||
				aBagSPJavaOrchestration.get(INRO_REGISTRO+"_ORG").equals("null"))
		){
			if(aBagSPJavaOrchestration.get("DATE_FILTER")=="S")
				numberOfResultsShow = 100;
			else
				numberOfResultsShow = 10;
		}
		else{
			numberOfResultsShow = Integer.parseInt((String) aBagSPJavaOrchestration.get(INRO_REGISTRO+"_ORG"));
			numberOfResultsShow = Math.min(numberOfResultsShow, 50);
		}


		if(aBagSPJavaOrchestration.get("RESPONSE_FAILED_MOVEMENTS")!=null){
			showFailed = true;
			anOriginalProcedureResF = (IProcedureResponse) aBagSPJavaOrchestration.get("RESPONSE_FAILED_MOVEMENTS");
			numberOfResults =  numberOfResultsSuccess + anOriginalProcedureResF.getResultSet(1).getData().getRowsAsArray().length;
			numberOfResultsShow = Math.min(numberOfResultsShow, numberOfResults);
			numberOfResults = numberOfResultsShow;
			totalNumberOfResults = numberOfResults;
			if (aBagSPJavaOrchestration.get("DATE_FILTER")=="S"){
				totalNumberOfResults = numberOfResultsSuccess + Integer.parseInt(anOriginalProcedureResF.readValueParam("@o_total_registros"));
			}
		}
		else{
			numberOfResults =  numberOfResultsSuccess;
		}


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
		if(showFailed){
			metaData3.addColumnMetaData(new ResultSetHeaderColumn("totalNumberOfResults", ICTSTypes.SQLINT4, 5));
		}

		// Agregar Data
		IResultSetRow row = new ResultSetRow();

		row.addRowData(1, new ResultSetRowColumnData(false, "0"));
		row.addRowData(2, new ResultSetRowColumnData(false, "Success"));
		data.addRow(row);

		IResultSetRow row2 = new ResultSetRow();

		row2.addRowData(1, new ResultSetRowColumnData(false, "true"));
		data2.addRow(row2);

		IResultSetRow row3 = new ResultSetRow();

		row3.addRowData(1, new ResultSetRowColumnData(false, String.valueOf(numberOfResults)));
		if(showFailed){
			row3.addRowData(2, new ResultSetRowColumnData(false, String.valueOf(totalNumberOfResults)));
		}
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

			metaData0.addColumnMetaData(new ResultSetHeaderColumn("transactionId", ICTSTypes.SQLVARCHAR, 30));

			metaData0.addColumnMetaData(new ResultSetHeaderColumn("authorizationCode", ICTSTypes.SQLVARCHAR, 30));

			metaData0.addColumnMetaData(new ResultSetHeaderColumn("bankBranchCode", ICTSTypes.SQLVARCHAR, 30));

			metaData0.addColumnMetaData(new ResultSetHeaderColumn("purchaseAmount", ICTSTypes.SQLMONEY, 25));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("withdrawalAmount", ICTSTypes.SQLMONEY, 25));

			metaData0.addColumnMetaData(new ResultSetHeaderColumn("uuid", ICTSTypes.SQLVARCHAR, 32));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("cardId", ICTSTypes.SQLVARCHAR, 32));

			//commissionDetails
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("reason", ICTSTypes.SQLVARCHAR, 50));

			metaData0.addColumnMetaData(new ResultSetHeaderColumn("originMovementId", ICTSTypes.SQLVARCHAR, 50));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("originReferenceNumber", ICTSTypes.SQLVARCHAR, 50));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("commissionOriginMovementId", ICTSTypes.SQLVARCHAR, 50));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("commissionOriginReferenceNumber", ICTSTypes.SQLVARCHAR, 50));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("creditConcept", ICTSTypes.SQLVARCHAR, 50));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("originCode", ICTSTypes.SQLVARCHAR, 50));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("reversalConcept", ICTSTypes.SQLVARCHAR, 50));

			if(showFailed){
				metaData0.addColumnMetaData(new ResultSetHeaderColumn("pin", ICTSTypes.SQLVARCHAR, 50));
				metaData0.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLVARCHAR, 50));
				metaData0.addColumnMetaData(new ResultSetHeaderColumn("mode", ICTSTypes.SQLVARCHAR, 50));
				metaData0.addColumnMetaData(new ResultSetHeaderColumn("errorCode", ICTSTypes.SQLVARCHAR, 50));
				metaData0.addColumnMetaData(new ResultSetHeaderColumn("errorMessage", ICTSTypes.SQLVARCHAR, 50));
				metaData0.addColumnMetaData(new ResultSetHeaderColumn("transactionStatus", ICTSTypes.SQLVARCHAR, 50));
			}


			IResultSetData data0 = new ResultSetData();

			List<MovementDetails> movementDetailsList = getMovementsDetails(anOriginalProcedureRes);
			List<MovementDetails> failedMovementDetailsList = new ArrayList<MovementDetails>();
			logger.logDebug("Query success movement response: " + movementDetailsList.toString());
			if(showFailed){
				failedMovementDetailsList = getFailedMovementsDetails(anOriginalProcedureResF);
				logger.logDebug("Query failed movement response: " + failedMovementDetailsList.toString());
				if (movementDetailsList.size()>0)
					movementDetailsList.addAll(failedMovementDetailsList);
				else
					movementDetailsList = failedMovementDetailsList;
				if("ASC".equals(aBagSPJavaOrchestration.get("ORDER"))) {
					Collections.sort(movementDetailsList, new Comparator<MovementDetails>() {
						public int compare(MovementDetails m1, MovementDetails m2) {
							return m1.getTransactionDate().compareTo(m2.getTransactionDate());
						}
					});
				}
				else{
					Collections.sort(movementDetailsList, new Comparator<MovementDetails>() {
						public int compare(MovementDetails m1, MovementDetails m2) {
							return m2.getTransactionDate().compareTo(m1.getTransactionDate());
						}
					});
				}

				if (movementDetailsList.size() > numberOfResultsShow) {
					movementDetailsList.subList(numberOfResultsShow, movementDetailsList.size()).clear();
				}

				logger.logDebug("Query all movement response: " + movementDetailsList.toString());
			}

			for(MovementDetails movementDetails : movementDetailsList){
				BigDecimal purchaseAmount = movementDetails.getPurchaseAmount();
				BigDecimal withdrawalAmount = movementDetails.getWithdrawalAmount();
				Integer transactionReferenceNumber = movementDetails.getTransactionReferenceNumber();

				String puchaseAmountString = null;
				String withdrawalAmountString = null;
				String transactionReferenceNumberString = null;
				if(purchaseAmount != null){
					puchaseAmountString = purchaseAmount.toString();
				}
				if(withdrawalAmount != null){
					withdrawalAmountString = withdrawalAmount.toString();
				}
				if(transactionReferenceNumber != null){
					transactionReferenceNumberString = transactionReferenceNumber.toString();
				}
				IResultSetRow rowDat = new ResultSetRow();
				rowDat.addRowData(1, new ResultSetRowColumnData(false, movementDetails.getAccountingBalance().toString()));
				rowDat.addRowData(2, new ResultSetRowColumnData(false, movementDetails.getAvailableBalance().toString()));
				rowDat.addRowData(3, new ResultSetRowColumnData(false, movementDetails.getMovementType()));
				rowDat.addRowData(4, new ResultSetRowColumnData(false, movementDetails.getAmount().toString()));
				rowDat.addRowData(5, new ResultSetRowColumnData(false, movementDetails.getTransactionDate()));
				rowDat.addRowData(6, new ResultSetRowColumnData(false, movementDetails.getOperationType()));
				if(movementDetails.getCommission().compareTo(BigDecimal.ZERO) != 0){
					rowDat.addRowData(7, new ResultSetRowColumnData(false, movementDetails.getCommission().toString()));
				}else{
					rowDat.addRowData(7, new ResultSetRowColumnData(false, null));
				}
				if(movementDetails.getIva().compareTo(BigDecimal.ZERO) != 0){
					rowDat.addRowData(8, new ResultSetRowColumnData(false, movementDetails.getIva().toString()));
				}else{
					rowDat.addRowData(8, new ResultSetRowColumnData(false, null));
				}
				rowDat.addRowData(9, new ResultSetRowColumnData(false, transactionReferenceNumberString));
				rowDat.addRowData(10, new ResultSetRowColumnData(false, movementDetails.getDescription()));
				rowDat.addRowData(11, new ResultSetRowColumnData(false, movementDetails.getMaskedCardNumber()));
				rowDat.addRowData(12, new ResultSetRowColumnData(false, movementDetails.getOwnerNameSA()));
				rowDat.addRowData(13, new ResultSetRowColumnData(false, movementDetails.getAccountNumberSA()));
				rowDat.addRowData(14, new ResultSetRowColumnData(false, movementDetails.getBankNameSA()));
				rowDat.addRowData(15, new ResultSetRowColumnData(false, movementDetails.getOwnerNameDA()));
				rowDat.addRowData(16, new ResultSetRowColumnData(false, movementDetails.getAccountNumberDA()));
				rowDat.addRowData(17, new ResultSetRowColumnData(false, movementDetails.getBankNameDA()));
				rowDat.addRowData(18, new ResultSetRowColumnData(false, movementDetails.getReferenceCode()));
				rowDat.addRowData(19, new ResultSetRowColumnData(false, movementDetails.getTrackingId()));
				rowDat.addRowData(20, new ResultSetRowColumnData(false, movementDetails.getBankNameATM()));
				rowDat.addRowData(21, new ResultSetRowColumnData(false, movementDetails.getLocationId()));
				rowDat.addRowData(22, new ResultSetRowColumnData(false, movementDetails.getTransactionIdATM()));
				rowDat.addRowData(23, new ResultSetRowColumnData(false, movementDetails.getEstablishmentNameMD()));
				rowDat.addRowData(24, new ResultSetRowColumnData(false, movementDetails.getTransactionIdMD()));
				rowDat.addRowData(25, new ResultSetRowColumnData(false, movementDetails.getEstablishmentNameSD()));
				rowDat.addRowData(26, new ResultSetRowColumnData(false, movementDetails.getTransactionIdSD()));
				rowDat.addRowData(27, new ResultSetRowColumnData(false, movementDetails.getTransactionId()));
				rowDat.addRowData(28, new ResultSetRowColumnData(false, movementDetails.getAuthorizationCode()));
				rowDat.addRowData(29, new ResultSetRowColumnData(false, movementDetails.getBankBranchCode()));
				rowDat.addRowData(30, new ResultSetRowColumnData(false, puchaseAmountString));
				rowDat.addRowData(31, new ResultSetRowColumnData(false, withdrawalAmountString));
				rowDat.addRowData(32, new ResultSetRowColumnData(false, movementDetails.getUuid()));
				rowDat.addRowData(33, new ResultSetRowColumnData(false, movementDetails.getCardId()));
				rowDat.addRowData(34, new ResultSetRowColumnData(false, movementDetails.getReason()));

				//refound
				rowDat.addRowData(35, new ResultSetRowColumnData(false, movementDetails.getOriginMovementId()));
				rowDat.addRowData(36, new ResultSetRowColumnData(false, movementDetails.getOriginReferenceNumber()));
				rowDat.addRowData(37, new ResultSetRowColumnData(false, movementDetails.getCommissionOriginMovementId()));
				rowDat.addRowData(38, new ResultSetRowColumnData(false, movementDetails.getCommissionOriginReferenceNumber()));
				rowDat.addRowData(39, new ResultSetRowColumnData(false, movementDetails.getCreditConcept()));
				rowDat.addRowData(40, new ResultSetRowColumnData(false, movementDetails.getOriginCode()));
				rowDat.addRowData(41, new ResultSetRowColumnData(false, movementDetails.getReversalConcept()));

				if(showFailed){
					rowDat.addRowData(42, new ResultSetRowColumnData(false, movementDetails.getCardEntryPin()));
					rowDat.addRowData(43, new ResultSetRowColumnData(false, movementDetails.getCardEntryCode()));
					rowDat.addRowData(44, new ResultSetRowColumnData(false, movementDetails.getCardEntryMode()));
					rowDat.addRowData(45, new ResultSetRowColumnData(false, movementDetails.getErrorCode()));
					rowDat.addRowData(46, new ResultSetRowColumnData(false, movementDetails.getErrorMessage()));
					rowDat.addRowData(47, new ResultSetRowColumnData(false, movementDetails.getTransactionStatus()!=null?movementDetails.getTransactionStatus():"TRANSACTION_SUCCESS"));
				}
				data0.addRow(rowDat);
			}
			IResultSetBlock resultsetBlock0 = new ResultSetBlock(metaData0, data0);
			anOriginalProcedureResponse.addResponseBlock(resultsetBlock0);
		}

		logger.logInfo(CLASS_NAME + "processTransformationResponse final dco" + anOriginalProcedureResponse.getProcedureResponseAsString());
		return anOriginalProcedureResponse;
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

		row3.addRowData(1, new ResultSetRowColumnData(false, String.valueOf(anOriginalProcedureRes.getResultSet(4).getData().getRowsAsArray().length)));
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

			metaData0.addColumnMetaData(new ResultSetHeaderColumn("transactionId", ICTSTypes.SQLVARCHAR, 30));

			metaData0.addColumnMetaData(new ResultSetHeaderColumn("authorizationCode", ICTSTypes.SQLVARCHAR, 30));

			metaData0.addColumnMetaData(new ResultSetHeaderColumn("bankBranchCode", ICTSTypes.SQLVARCHAR, 30));

			metaData0.addColumnMetaData(new ResultSetHeaderColumn("purchaseAmount", ICTSTypes.SQLMONEY, 25));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("withdrawalAmount", ICTSTypes.SQLMONEY, 25));

			metaData0.addColumnMetaData(new ResultSetHeaderColumn("uuid", ICTSTypes.SQLVARCHAR, 32));
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("cardId", ICTSTypes.SQLVARCHAR, 32));

			//commissionDetails
			metaData0.addColumnMetaData(new ResultSetHeaderColumn("reason", ICTSTypes.SQLVARCHAR, 50));


			IResultSetBlock resulsetOrigin = anOriginalProcedureRes.getResultSet(4);
			IResultSetRow[] rowsTemp = resulsetOrigin.getData().getRowsAsArray();
			IResultSetData data0 = new ResultSetData();

			for (IResultSetRow iResultSetRow : rowsTemp) {

				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				IResultSetRow rowDat = new ResultSetRow();

				String destinyAccountNumber = null, destinyOwnerName = null, commission = "0", iva = "0";


				if(null!= columns[15].getValue() && !"".equals(columns[15].getValue())) {

					String[] strBeneficiary = columns[15].getValue().split("\\|");

					logger.logInfo("Prueba movements");
					for (int i = 0; i < strBeneficiary.length; i++)
					{
						logger.logInfo(strBeneficiary[i]);
					}
					logger.logInfo("Fin prueba movements");

					if(strBeneficiary!=null && strBeneficiary.length>0 && !strBeneficiary[0].trim().isEmpty())
						destinyAccountNumber = strBeneficiary[0].trim();
					else
						destinyAccountNumber = null;

					if(strBeneficiary!=null && strBeneficiary.length>2 && !strBeneficiary[2].trim().isEmpty())
						destinyOwnerName = strBeneficiary[2].trim();
					else
						destinyOwnerName = null;

					if(strBeneficiary.length>4)
						commission = strBeneficiary[4];
					else
						commission = "0";

					if(strBeneficiary.length>5)
						iva = strBeneficiary[5];
					else
						iva = "0";
				}

				if (destinyOwnerName!=null && isNumeric(destinyOwnerName.trim())) {
					destinyOwnerName = null;
				}

				String sourceOwnerName = columns[18].getValue();
				String sourceAccountNumber = columns[19].getValue();


				if (sourceOwnerName != null) {
					sourceOwnerName = sourceOwnerName.trim();
				}

				String type_movement = columns[35].getValue();
				String des_type = columns[1].getValue();
				String is_dock_idc = columns[36].getValue();
				String type_auth = columns[37].getValue();
				String operationType = columns[4].getValue();
				String movementType = null;
				//String referenciaSpei = columns[22].getValue();
				String status_spei = columns[40].getValue();
				String um_correccion = columns[41].getValue();
				String location_id_atm = columns[27].getValue();
				String name_location_atm = columns[45].getValue();
				String transaction_id_atm = columns[28].getValue();
				String bank_branch_code = columns[39].getValue();
				String establisment_name_merchant = columns[29].getValue();
				String transaction_id_merchant = columns[30].getValue();
				String establisment_name_store = columns[31].getValue();
				String transaction_id_store = columns[32].getValue();

				if (type_movement.equals("SPEI") || (um_correccion.equals("S") && type_movement.equals("P2P"))) {

					movementType = "SPEI_";
					if (operationType.equals("D")) {

						if (status_spei.trim().equals("A") || status_spei.trim().equals("F")) {
							movementType = movementType + "DEBIT";
						} else if ((status_spei.trim().equals("P"))) {
							movementType = movementType + "PENDING";
						}else{
							movementType = movementType + "DEBIT";
						}
					}

					if (operationType.equals("C")) {
						if (um_correccion.equals("S")) {
							movementType = movementType + "RETURN";
						} else {
							movementType = movementType + "CREDIT";
						}

					}
				} else if (type_movement.equals("P2P")) {

					movementType = "P2P_";
					if (operationType.equals("D")) {
						movementType = movementType + "DEBIT";
					}

					if (operationType.equals("C")) {
						movementType = movementType + "CREDIT";

						/*if(causa != null && causa.trim().equals("110")) {
							destinyOwnerName = sourceOwnerName;
							sourceOwnerName = null;

							destinyAccountNumber = sourceAccountNumber;
							sourceAccountNumber = null;
						}*/
					}
				} else if (type_movement.equals("AUTH")) {

					if (destinyOwnerName!=null) {
						destinyOwnerName = null;
					}

					if(destinyAccountNumber != null) {
						destinyAccountNumber = null;
					}

					if (type_auth.equals("WITHDRAWAL")) {
						movementType = "DEBIT_AT_STORE";
						/*if (is_dock_idc.equals("DOCK")) {
							movementType = "ATM_DEBIT";
						}

						if (is_dock_idc.equals("IDC")) {
							movementType = "DEBIT_AT_STORE";
						}*/
					}

					else if (type_auth.toUpperCase().contains("PURCHASE")) {
						if (type_auth.equals("PURCHASE ONLINE")) {
							movementType = "PURCHASE_ONLINE";
						} else {
							if (type_auth.equals("PURCHASE PHYSICAL") || type_auth.equals("PURCHASE")) {
								movementType = "PURCHASE_AT_STORE";
							} else if (type_auth.equals("PURCHASE_WITH_CASHBACK")) {
								movementType = "PURCHASE_WITH_CASHBACK";
							}
							establisment_name_store = (establisment_name_store == null
									|| establisment_name_store.isEmpty()) ? name_location_atm : establisment_name_store;
							transaction_id_store = (transaction_id_store == null || transaction_id_store.isEmpty())
									? transaction_id_atm
									: transaction_id_store;

							// Reset values
							name_location_atm = null;
							location_id_atm = null;
							transaction_id_atm = null;
							bank_branch_code = null;
						}
					}

					else if (type_auth.equals("DEPOSIT")) {

						if (is_dock_idc.equals("DOCK")) {
							movementType = "CREDIT_AT_STORE";

						}

						if (is_dock_idc.equals("IDC")) {
							movementType = "CREDIT_AT_STORE";
						}
					}

					else if (type_auth.toUpperCase().contains("REVERSAL")) {
						if (type_auth.equals("REVERSAL ONLINE")) {
							movementType = "REVERSAL ONLINE";
						} else if (type_auth.equals("REVERSAL PHYSICAL")) {
							movementType = "REVERSAL PHYSICAL";
						} else {
							movementType = "REVERSAL";
						}
					}

					if (movementType == null || movementType.isEmpty()) {
						if (type_movement.equals("BONUS")) {
							movementType = "BONUS";
						} else if (type_movement.equals("COMMISSION")) {
							movementType = "COMMISSION";
						} else {
							movementType = type_auth;
						}
					}

					/*
					 * if (type_auth.equals("REVERSAL")) {
					 *
					 * if (is_dock_idc.equals("DOCK")) {
					 * movementType = "REVERSAL";
					 * }
					 *
					 * if (is_dock_idc.equals("IDC")) {
					 * movementType = "REVERSAL";
					 * }
					 * }
					 */

				} else if (type_movement.equals("ISO")) {

					if (type_auth.equals("WITHDRAWAL")) {
						movementType = "ATM_DEBIT";
					}

					else if (type_auth.toUpperCase().contains("PURCHASE")) {
						if (type_auth.equals("PURCHASE ONLINE")) {
							movementType = "PURCHASE_ONLINE";
						} else {
							if (type_auth.equals("PURCHASE PHYSICAL") || type_auth.equals("PURCHASE")) {
								movementType = "PURCHASE_AT_STORE";
							} else if (type_auth.equals("PURCHASE_WITH_CASHBACK")) {
								movementType = "PURCHASE_WITH_CASHBACK";
							}
							establisment_name_store = (establisment_name_store == null
									|| establisment_name_store.isEmpty()) ? name_location_atm : establisment_name_store;
							transaction_id_store = (transaction_id_store == null || transaction_id_store.isEmpty())
									? transaction_id_atm
									: transaction_id_store;

							// Reset values
							name_location_atm = null;
							location_id_atm = null;
							transaction_id_atm = null;
							bank_branch_code = null;
						}
					}

					else if (type_auth.toUpperCase().contains("REVERSAL")) {
						if (type_auth.equals("REVERSAL ONLINE")) {
							movementType = "REVERSAL ONLINE";
						} else if (type_auth.equals("REVERSAL PHYSICAL")) {
							movementType = "REVERSAL PHYSICAL";
						} else if (type_auth.equals("REVERSAL")) {
							movementType = "REVERSAL";
						} else {
							movementType = "REVERSAL_PURCHASE_WITH_CASHBACK";
						}
					}

					if (movementType == null || movementType.isEmpty()) {
						if (type_movement.equals("BONUS")) {
							movementType = "BONUS";
						} else if (type_movement.equals("COMMISSION")) {
							movementType = "COMMISSION";
						} else {
							if (des_type.toUpperCase().contains("REVERSAL")) {
								movementType = "REVERSAL_PURCHASE_WITH_CASHBACK";
							} else {
								movementType = type_auth;
							}
							// movementType = type_auth;
						}
					}

				} /*else if(type_movement.equals("BONUS")){
					movementType = "BONUS";
				} else if (type_movement.equals("COMMISSION")){
					movementType = "COMMISSION";

				}*/

				if (movementType == null || movementType.isEmpty()) {
					if (type_movement.equals("BONUS")) {
						movementType = "BONUS";
					} else if (type_movement.equals("COMMISSION")) {
						movementType = "COMMISSION";
					} else {
						movementType = type_auth;
					}
				}

				if (operationType.equals("C")) {

					String copysourceOwnerName = sourceOwnerName;
					String copydestinyOwnerName = destinyOwnerName;
					String copysourceAccountNumber = sourceAccountNumber;
					String copydestinyAccountNumber = destinyAccountNumber;

					destinyOwnerName = copysourceOwnerName;
					destinyAccountNumber = copysourceAccountNumber;

					sourceOwnerName = copydestinyOwnerName;
					sourceAccountNumber = copydestinyAccountNumber;
				}

				String amount = columns[5].getValue();
				String iva_val = columns[33].getValue();
				String purchaseVal = null, withdrawalVal = null;

				if (movementType.equals("PURCHASE_WITH_CASHBACK")) {
					BigDecimal bigDecimalAmount = new BigDecimal(amount);
					BigDecimal bigDecimalIva = new BigDecimal(iva_val);

					amount = bigDecimalAmount.add(bigDecimalIva).toString();
					iva_val = null;
					purchaseVal = bigDecimalAmount.toString();
					withdrawalVal = bigDecimalIva.toString();

				}

				String reason_commission = columns[44].getValue();
				if(reason_commission != null){
					if(reason_commission.trim().equals("8110")){
						reason_commission = "CARD_DELIVERY";
					}else if(reason_commission.trim().equals("3101")){
						reason_commission = "FALSE_CHARGEBACK";
					}else{
						reason_commission = "";
					}
				}

				rowDat.addRowData(1, new ResultSetRowColumnData(false, columns[6].getValue()));
				rowDat.addRowData(2, new ResultSetRowColumnData(false, columns[7].getValue()));
				// rowDat.addRowData(3, new ResultSetRowColumnData(false, movementType));// 2?
				String tempMovementType = null;
				if (movementType == null) {
					tempMovementType = type_auth;
				} else if (movementType.equals("REVERSAL PHYSICAL") || movementType.equals("REVERSAL ONLINE")) {
					tempMovementType = "REVERSAL";
				} else {
					tempMovementType = movementType;
				}
				rowDat.addRowData(3, new ResultSetRowColumnData(false, tempMovementType));
				rowDat.addRowData(4, new ResultSetRowColumnData(false, amount));
				rowDat.addRowData(5, new ResultSetRowColumnData(false, columns[0].getValue()));
				rowDat.addRowData(6, new ResultSetRowColumnData(false, columns[4].getValue()));
				rowDat.addRowData(7, new ResultSetRowColumnData(false, columns[34].getValue()));
				rowDat.addRowData(8, new ResultSetRowColumnData(false, iva_val));
				rowDat.addRowData(9, new ResultSetRowColumnData(false, columns[24].getValue()));//8, 11?

				if (type_auth !=null && type_auth.equals("WITHDRAWAL") && is_dock_idc == null) {
					rowDat.addRowData(10, new ResultSetRowColumnData(false, null));
					rowDat.addRowData(11, new ResultSetRowColumnData(false, null));
				}else{
					rowDat.addRowData(10, new ResultSetRowColumnData(false, columns[1].getValue()));
					rowDat.addRowData(11, new ResultSetRowColumnData(false, columns[25].getValue()));
				}

				rowDat.addRowData(12, new ResultSetRowColumnData(false, sourceOwnerName));
				rowDat.addRowData(13, new ResultSetRowColumnData(false, sourceAccountNumber));
				rowDat.addRowData(14, new ResultSetRowColumnData(false, columns[20].getValue()));

				rowDat.addRowData(15, new ResultSetRowColumnData(false, destinyOwnerName));
				rowDat.addRowData(16, new ResultSetRowColumnData(false, destinyAccountNumber));
				rowDat.addRowData(17, new ResultSetRowColumnData(false, columns[21].getValue()));

				rowDat.addRowData(18, new ResultSetRowColumnData(false, columns[22].getValue()));
				rowDat.addRowData(19, new ResultSetRowColumnData(false, columns[23].getValue()));

				rowDat.addRowData(20, new ResultSetRowColumnData(false, columns[26].getValue()));
				// rowDat.addRowData(21, new ResultSetRowColumnData(false, location_id_atm));
				// rowDat.addRowData(22, new ResultSetRowColumnData(false, transaction_id_atm));
				// rowDat.addRowData(23, new ResultSetRowColumnData(false, columns[29].getValue()));
				// rowDat.addRowData(24, new ResultSetRowColumnData(false, columns[30].getValue()));
				// rowDat.addRowData(25, new ResultSetRowColumnData(false, establisment_name_store));
				// rowDat.addRowData(26, new ResultSetRowColumnData(false, transaction_id_store));

				if (type_auth == null) {
					rowDat.addRowData(21, new ResultSetRowColumnData(false, location_id_atm));
					rowDat.addRowData(22, new ResultSetRowColumnData(false, transaction_id_atm));
					rowDat.addRowData(23, new ResultSetRowColumnData(false, columns[29].getValue()));
					rowDat.addRowData(24, new ResultSetRowColumnData(false, columns[30].getValue()));
					rowDat.addRowData(25, new ResultSetRowColumnData(false, establisment_name_store));
					rowDat.addRowData(26, new ResultSetRowColumnData(false, transaction_id_store));

				} else {
					if (type_auth.equals("WITHDRAWAL")) {

						if (is_dock_idc == null) {
							rowDat.addRowData(20, new ResultSetRowColumnData(false, columns[45].getValue()));
							rowDat.addRowData(21, new ResultSetRowColumnData(false, location_id_atm));
							rowDat.addRowData(22, new ResultSetRowColumnData(false, transaction_id_atm));
							rowDat.addRowData(23, new ResultSetRowColumnData(false, columns[29].getValue()));
							rowDat.addRowData(24, new ResultSetRowColumnData(false, columns[30].getValue()));
							rowDat.addRowData(25, new ResultSetRowColumnData(false, establisment_name_store));
							rowDat.addRowData(26, new ResultSetRowColumnData(false, transaction_id_store));

						} else if (is_dock_idc.equals("DOCK") || is_dock_idc.equals("IDC")) {
							rowDat.addRowData(21, new ResultSetRowColumnData(false, null));
							rowDat.addRowData(22, new ResultSetRowColumnData(false, null));
							rowDat.addRowData(23, new ResultSetRowColumnData(false, null));
							rowDat.addRowData(24, new ResultSetRowColumnData(false, null));
							rowDat.addRowData(25, new ResultSetRowColumnData(false,
									is_dock_idc.equals("DOCK") ? establisment_name_merchant : establisment_name_store));
							rowDat.addRowData(26, new ResultSetRowColumnData(false,
									is_dock_idc.equals("DOCK") ? transaction_id_merchant : transaction_id_store));
						}

					} else if (type_auth.equals("DEPOSIT")) {
						bank_branch_code = null;
						rowDat.addRowData(21, new ResultSetRowColumnData(false, null));
						rowDat.addRowData(22, new ResultSetRowColumnData(false, null));
						rowDat.addRowData(23, new ResultSetRowColumnData(false, null));
						rowDat.addRowData(24, new ResultSetRowColumnData(false, null));
						rowDat.addRowData(25,
								new ResultSetRowColumnData(false,
										establisment_name_merchant != null ? establisment_name_merchant
												: establisment_name_store));
						rowDat.addRowData(26, new ResultSetRowColumnData(false,
								transaction_id_merchant != null ? transaction_id_merchant : transaction_id_store));
					} else if (movementType.equals("PURCHASE_AT_STORE")
							|| movementType.equals("PURCHASE_WITH_CASHBACK")) {
						if (is_dock_idc == null) {
							rowDat.addRowData(21, new ResultSetRowColumnData(false, null));
							rowDat.addRowData(22, new ResultSetRowColumnData(false, null));
							rowDat.addRowData(23, new ResultSetRowColumnData(false, null));
							rowDat.addRowData(24, new ResultSetRowColumnData(false, null));
							rowDat.addRowData(25, new ResultSetRowColumnData(false, establisment_name_store));
							rowDat.addRowData(26, new ResultSetRowColumnData(false, transaction_id_store));

						} else if (is_dock_idc.equals("DOCK") || is_dock_idc.equals("IDC")) {
							rowDat.addRowData(21, new ResultSetRowColumnData(false, null));
							rowDat.addRowData(22, new ResultSetRowColumnData(false, null));
							rowDat.addRowData(23, new ResultSetRowColumnData(false, null));
							rowDat.addRowData(24, new ResultSetRowColumnData(false, null));
							rowDat.addRowData(25, new ResultSetRowColumnData(false,
									is_dock_idc.equals("DOCK") ? establisment_name_merchant : establisment_name_store));
							rowDat.addRowData(26, new ResultSetRowColumnData(false,
									is_dock_idc.equals("DOCK") ? transaction_id_merchant : transaction_id_store));
						}

					} else if (movementType.equals("PURCHASE_ONLINE")) {
						establisment_name_merchant = (name_location_atm != null) ? name_location_atm
								: establisment_name_merchant;
						transaction_id_merchant = (transaction_id_atm != null) ? transaction_id_atm
								: transaction_id_merchant;
						name_location_atm = null;
						location_id_atm = null;
						transaction_id_atm = null;
						bank_branch_code = null;
						rowDat.addRowData(21, new ResultSetRowColumnData(false, null));
						rowDat.addRowData(22, new ResultSetRowColumnData(false, null));
						rowDat.addRowData(23, new ResultSetRowColumnData(false, establisment_name_merchant));
						rowDat.addRowData(24, new ResultSetRowColumnData(false, transaction_id_merchant));
						rowDat.addRowData(25, new ResultSetRowColumnData(false, null));
						rowDat.addRowData(26, new ResultSetRowColumnData(false, null));
					} else if (movementType.equals("REVERSAL PHYSICAL") || movementType.equals("REVERSAL")
							|| movementType.equals("REVERSAL_PURCHASE_WITH_CASHBACK")) {
						bank_branch_code = null;
						if (is_dock_idc == null) {
							rowDat.addRowData(21, new ResultSetRowColumnData(false, null));
							rowDat.addRowData(22, new ResultSetRowColumnData(false, null));
							rowDat.addRowData(23, new ResultSetRowColumnData(false, null));
							rowDat.addRowData(24, new ResultSetRowColumnData(false, null));
							rowDat.addRowData(25, new ResultSetRowColumnData(false,
									name_location_atm != null ? name_location_atm : establisment_name_store));
							rowDat.addRowData(26, new ResultSetRowColumnData(false,
									transaction_id_atm != null ? transaction_id_atm : transaction_id_store));

						} else if (is_dock_idc.equals("DOCK") || is_dock_idc.equals("IDC")) {
							rowDat.addRowData(21, new ResultSetRowColumnData(false, null));
							rowDat.addRowData(22, new ResultSetRowColumnData(false, null));
							rowDat.addRowData(23, new ResultSetRowColumnData(false, null));
							rowDat.addRowData(24, new ResultSetRowColumnData(false, null));
							rowDat.addRowData(25, new ResultSetRowColumnData(false,
									is_dock_idc.equals("DOCK") ? establisment_name_merchant : establisment_name_store));
							rowDat.addRowData(26, new ResultSetRowColumnData(false,
									is_dock_idc.equals("DOCK") ? transaction_id_merchant : transaction_id_store));
						}

					} else if (movementType.equals("REVERSAL ONLINE")) {
						establisment_name_merchant = (name_location_atm != null) ? name_location_atm
								: establisment_name_merchant;
						transaction_id_merchant = (transaction_id_atm != null) ? transaction_id_atm
								: transaction_id_merchant;
						name_location_atm = null;
						location_id_atm = null;
						transaction_id_atm = null;
						bank_branch_code = null;
						rowDat.addRowData(21, new ResultSetRowColumnData(false, null));
						rowDat.addRowData(22, new ResultSetRowColumnData(false, null));
						rowDat.addRowData(23, new ResultSetRowColumnData(false, establisment_name_merchant));
						rowDat.addRowData(24, new ResultSetRowColumnData(false, transaction_id_merchant));
						rowDat.addRowData(25, new ResultSetRowColumnData(false, null));
						rowDat.addRowData(26, new ResultSetRowColumnData(false, null));
					} else {
						rowDat.addRowData(20, new ResultSetRowColumnData(false, columns[26].getValue()));
						rowDat.addRowData(21, new ResultSetRowColumnData(false, location_id_atm));
						rowDat.addRowData(22, new ResultSetRowColumnData(false, transaction_id_atm));
						rowDat.addRowData(23, new ResultSetRowColumnData(false, establisment_name_merchant));
						rowDat.addRowData(24, new ResultSetRowColumnData(false, transaction_id_merchant));
						rowDat.addRowData(25, new ResultSetRowColumnData(false, establisment_name_store));
						rowDat.addRowData(26, new ResultSetRowColumnData(false, transaction_id_store));
					}
				}
				rowDat.addRowData(27, new ResultSetRowColumnData(false, columns[17].getValue()));
				if (tempMovementType.equals("P2P_DEBIT") || tempMovementType.equals("P2P_CREDIT")
						|| tempMovementType.equals("SPEI_CREDIT") || tempMovementType.equals("COMMISSION")
						|| tempMovementType.equals("SPEI_DEBIT") || tempMovementType.equals("SPEI_PENDING")) {
					rowDat.addRowData(28, new ResultSetRowColumnData(false, null));
				} else {
					rowDat.addRowData(28, new ResultSetRowColumnData(false, columns[38].getValue()));
				}

				rowDat.addRowData(29, new ResultSetRowColumnData(false, bank_branch_code));

				rowDat.addRowData(30, new ResultSetRowColumnData(false, purchaseVal));
				rowDat.addRowData(31, new ResultSetRowColumnData(false, withdrawalVal));

				rowDat.addRowData(32, new ResultSetRowColumnData(false, columns[42].getValue()));

				if (type_auth !=null && type_auth.equals("WITHDRAWAL") && is_dock_idc == null) {
					rowDat.addRowData(33, new ResultSetRowColumnData(false, null));
				}else {
					rowDat.addRowData(33, new ResultSetRowColumnData(false, columns[43].getValue()));
				}

				rowDat.addRowData(34, new ResultSetRowColumnData(false, reason_commission));

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

	public boolean isNumeric(String strNum) {

		Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

		if (strNum == null) {

			return false;
		}
		return pattern.matcher(strNum).matches();
	}

	private void llenarRegistrosLocal(IProcedureResponse anProcedureResponse, IProcedureResponse anProcedureResponseLocalOff) {

		if (anProcedureResponse.getResultSets().size() >= 4) {
			IResultSetBlock resultSetBlock = anProcedureResponse.getResultSet(4);
			IResultSetData data = resultSetBlock.getData();

			if (anProcedureResponseLocalOff.getResultSets().size() >= 4) {

				IResultSetBlock sourceResultSetBlock = anProcedureResponseLocalOff.getResultSet(4);
				IResultSetData sourceData = sourceResultSetBlock.getData();
				IResultSetRow[] sourceRows = sourceData.getRowsAsArray();
				for (IResultSetRow sourceRow : sourceRows) {
					IResultSetRow newRow = new ResultSetRow();
					IResultSetRowColumnData[] columns = sourceRow.getColumnsAsArray();
					for (int i = 0; i < columns.length; i++) {
						newRow.addRowData(i + 1, new ResultSetRowColumnData(false, columns[i].getValue()));
					}

					data.addRow(newRow);
				}
			}
		}

	}

	public List<MovementDetails> getFailedMovementsDetails(IProcedureResponse anProcedureResponse) {
		logger.logDebug("KCZ: getMovementsDetails" + anProcedureResponse.getProcedureResponseAsString());
		IResultSetBlock resulSetOrigin = anProcedureResponse.getResultSet(1);
		IResultSetRow[] rowsTemp = resulSetOrigin.getData().getRowsAsArray();
		List<MovementDetails> movementDetailsList = new ArrayList<>();
		for (IResultSetRow iResultSetRow : rowsTemp) {
			MovementDetails movementDetails = new MovementDetails();
			IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
			BigDecimal amount = getBigDecimalValue(columns[2].getValue());
			BigDecimal iva = getBigDecimalValue(columns[6].getValue());
			amount = amount.add(iva);
			movementDetails.setOperationType( columns[4].getValue());
			movementDetails.setMovementType( columns[12].getValue());
			movementDetails.setAccountingBalance( getBigDecimalValue(columns[0].getValue()));
			movementDetails.setAvailableBalance( getBigDecimalValue(columns[1].getValue()));
			movementDetails.setAmount( amount);
			movementDetails.setIva( getBigDecimalValue(columns[6].getValue()));
			movementDetails.setTransactionDate( columns[3].getValue());
			movementDetails.setCommission( getBigDecimalValue(columns[5].getValue()));
			movementDetails.setDescription( columns[7].getValue());
			movementDetails.setTransactionId(columns[8].getValue()); //falta movementId
			movementDetails.setOwnerNameSA( columns[18].getValue());
			movementDetails.setAccountNumberSA( columns[19].getValue());
			movementDetails.setOwnerNameDA( columns[21].getValue());
			movementDetails.setAccountNumberDA( columns[22].getValue());
			movementDetails.setUuid( columns[10].getValue());
			movementDetails.setReferenceCode( columns[24].getValue());
			movementDetails.setTransactionReferenceNumber( columns[26].getValue() != null ? Integer.parseInt(columns[26].getValue()):null);
			movementDetails.setBankNameDA( columns[23].getValue());
			movementDetails.setBankNameSA( columns[20].getValue());
			movementDetails.setTrackingId( columns[25].getValue());
			movementDetails.setMaskedCardNumber( columns[13].getValue());
			movementDetails.setCardId( columns[14].getValue());
			movementDetails.setAuthorizationCode( columns[9].getValue());
			movementDetails.setEstablishmentNameSD( columns[33].getValue());
			movementDetails.setEstablishmentNameMD( columns[31].getValue());
			movementDetails.setLocationId( columns[28].getValue());
			movementDetails.setBankBranchCode( columns[30].getValue());
			movementDetails.setReason( columns[35].getValue());
			movementDetails.setCardEntryPin( columns[16].getValue());
			movementDetails.setCardEntryCode( columns[15].getValue());
			movementDetails.setCardEntryMode( columns[17].getValue());
			movementDetails.setErrorCode( columns[36].getValue());
			movementDetails.setErrorMessage( columns[37].getValue());
			movementDetails.setTransactionStatus( columns[11].getValue());
			logger.logDebug("KCZ: Movement detail Objects: " + movementDetails.toString());
			movementDetailsList.add(movementDetails);
		}
		return movementDetailsList;
	}
	public List<MovementDetails> getMovementsDetails(IProcedureResponse anProcedureResponse) {
		logger.logDebug("KCZ: getMovementsDetails" + anProcedureResponse.getProcedureResponseAsString());
		IResultSetBlock resulSetOrigin = anProcedureResponse.getResultSet(4);
		IResultSetRow[] rowsTemp = resulSetOrigin.getData().getRowsAsArray();
		List<MovementDetails> movementDetailsList = new ArrayList<>();
		String isISO;

		for (IResultSetRow iResultSetRow : rowsTemp) {
			MovementDetails movementDetails = new MovementDetails();
			IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
			String[] additionalDataArray = columns[32].getValue().split("\\|");
			String typeMovement = columns[33].getValue();
			BigDecimal amount = getBigDecimalValue(columns[5].getValue());
			BigDecimal iva = getBigDecimalValue(columns[28].getValue());
			amount = amount.add(iva);
			String establishmentName;
			movementDetails.setOperationType(columns[4].getValue());
			movementDetails.setMovementType(typeMovement);
			movementDetails.setAccountingBalance(getBigDecimalValue(columns[6].getValue()));
			movementDetails.setAvailableBalance(getBigDecimalValue(columns[7].getValue()));
			movementDetails.setAmount(amount);
			movementDetails.setIva(getBigDecimalValue(columns[28].getValue()));
			movementDetails.setTransactionDate(columns[10].getValue());
			movementDetails.setCommission(getBigDecimalValue(columns[29].getValue()));
			movementDetails.setDescription(columns[12].getValue());
			movementDetails.setTransactionId(columns[8].getValue());
			switch (typeMovement){
				case Constants.P2P_DEBIT:
					movementDetails.setAccountNumberSA(cuenta);
					movementDetails.setOwnerNameDA(getAdditionalValue(additionalDataArray,0));
					movementDetails.setAccountNumberDA(getAdditionalValue(additionalDataArray,1));
					movementDetails.setUuid(getAdditionalValue(additionalDataArray,2));
					movementDetails.setOwnerNameSA(columns[18].getValue());
					break;
				case Constants.P2P_CREDIT:
					movementDetails.setOwnerNameSA(getAdditionalValue(additionalDataArray,0));
					movementDetails.setAccountNumberSA(getAdditionalValue(additionalDataArray,1));
					movementDetails.setUuid(getAdditionalValue(additionalDataArray,2));
					movementDetails.setOwnerNameDA(columns[18].getValue());
					movementDetails.setAccountNumberDA(cuenta);
					break;
				case Constants.SPEI_DEBIT :
				case Constants.SPEI_PENDING:
					movementDetails.setAccountNumberSA(cuenta);
					movementDetails.setOwnerNameSA(columns[18].getValue());
					movementDetails.setReferenceCode(getAdditionalValue(additionalDataArray,1));
					movementDetails.setTrackingId(getAdditionalValue(additionalDataArray,2));
					movementDetails.setTransactionReferenceNumber(Integer.parseInt(getAdditionalValue(additionalDataArray,3)));
					movementDetails.setAccountNumberDA(getAdditionalValue(additionalDataArray,4));
					movementDetails.setOwnerNameDA(getAdditionalValue(additionalDataArray,5));
					movementDetails.setBankNameDA(getAdditionalValue(additionalDataArray,7));
					movementDetails.setUuid(getAdditionalValue(additionalDataArray,8));
					movementDetails.setTransactionStatus(getAdditionalValue(additionalDataArray,9));
					break;

				case Constants.SPEI_CREDIT:
					movementDetails.setReferenceCode(getAdditionalValue(additionalDataArray,0));
					movementDetails.setTrackingId(getAdditionalValue(additionalDataArray,1));
					movementDetails.setTransactionReferenceNumber(Integer.parseInt(getAdditionalValue(additionalDataArray,2)));
					movementDetails.setOwnerNameSA(getAdditionalValue(additionalDataArray,3));
					movementDetails.setAccountNumberSA(getAdditionalValue(additionalDataArray,4));
					movementDetails.setBankNameSA(getAdditionalValue(additionalDataArray,7));
					movementDetails.setOwnerNameDA(columns[18].getValue());
					movementDetails.setAccountNumberDA(columns[19].getValue());
					break;
				case Constants.SPEI_RETURN:
					movementDetails.setAccountNumberSA(cuenta);
					movementDetails.setOwnerNameSA(columns[18].getValue());
					movementDetails.setUuid(getAdditionalValue(additionalDataArray,8));
					break;
				case Constants.CREDIT_AT_STORE:
					establishmentName = getAdditionalValue(additionalDataArray, 6);
					establishmentName = establishmentName == null
							? getAdditionalValue(additionalDataArray,7)
							: establishmentName;
					movementDetails.setOwnerNameDA(columns[18].getValue());
					movementDetails.setAccountNumberDA(cuenta);
					movementDetails.setCardId(getAdditionalValue(additionalDataArray,1));
					movementDetails.setTransactionIdSD(getAdditionalValue(additionalDataArray,5));
					movementDetails.setEstablishmentNameSD(establishmentName);
					movementDetails.setAuthorizationCode(getAdditionalValue(additionalDataArray,8));
					movementDetails.setUuid(getAdditionalValue(additionalDataArray,10));
					movementDetails.setMaskedCardNumber(getAdditionalValue(additionalDataArray,11));
					movementDetails.setCardEntryCode(getAdditionalValue(additionalDataArray,12));
					movementDetails.setCardEntryPin(getAdditionalValue(additionalDataArray,13));
					movementDetails.setCardEntryMode(getAdditionalValue(additionalDataArray,14));
					break;
				case Constants.DEBIT_AT_STORE:
				case Constants.PURCHASE_AT_STORE:
					establishmentName = getAdditionalValue(additionalDataArray, 6);
					establishmentName = establishmentName == null
							? getAdditionalValue(additionalDataArray,7)
							: establishmentName;
					movementDetails.setOwnerNameSA(columns[18].getValue());
					movementDetails.setAccountNumberSA(cuenta);
					movementDetails.setCardId(getAdditionalValue(additionalDataArray,1));
					movementDetails.setTransactionIdSD(getAdditionalValue(additionalDataArray,5));
					movementDetails.setEstablishmentNameSD(establishmentName);
					movementDetails.setAuthorizationCode(getAdditionalValue(additionalDataArray,8));
					movementDetails.setUuid(getAdditionalValue(additionalDataArray,10));
					movementDetails.setMaskedCardNumber(getAdditionalValue(additionalDataArray,11));
					movementDetails.setCardEntryCode(getAdditionalValue(additionalDataArray,12));
					movementDetails.setCardEntryPin(getAdditionalValue(additionalDataArray,13));
					movementDetails.setCardEntryMode(getAdditionalValue(additionalDataArray,14));
					break;
				case Constants.PURCHASE_ONLINE: //ISO y API
					isISO = getAdditionalValue(additionalDataArray,(additionalDataArray.length-1));
					if(!isISO.equals("N")){
						movementDetails.setOwnerNameSA(columns[18].getValue());
						movementDetails.setAccountNumberSA(cuenta);
						movementDetails.setMaskedCardNumber(getAdditionalValue(additionalDataArray,6));
						movementDetails.setCardId(getAdditionalValue(additionalDataArray,7));
						movementDetails.setEstablishmentNameMD(getAdditionalValue(additionalDataArray, 8));
						movementDetails.setTransactionIdMD(getAdditionalValue(additionalDataArray,4));
						movementDetails.setAuthorizationCode(getAdditionalValue(additionalDataArray,9));
						movementDetails.setCardEntryCode(getAdditionalValue(additionalDataArray,10));
						movementDetails.setCardEntryMode(getAdditionalValue(additionalDataArray,11));
					}else{
						establishmentName = getAdditionalValue(additionalDataArray, 6);
						establishmentName = establishmentName == null
								? getAdditionalValue(additionalDataArray,7)
								: establishmentName;
						movementDetails.setOwnerNameSA(columns[18].getValue());
						movementDetails.setAccountNumberSA(cuenta);
						movementDetails.setCardId(getAdditionalValue(additionalDataArray,1));
						movementDetails.setTransactionIdMD(getAdditionalValue(additionalDataArray,5));
						movementDetails.setEstablishmentNameMD(establishmentName);
						movementDetails.setAuthorizationCode(getAdditionalValue(additionalDataArray,8));
						movementDetails.setUuid(getAdditionalValue(additionalDataArray,10));
						movementDetails.setMaskedCardNumber(getAdditionalValue(additionalDataArray,11));
						movementDetails.setCardEntryCode(getAdditionalValue(additionalDataArray,12));
						movementDetails.setCardEntryPin(getAdditionalValue(additionalDataArray,13));
						movementDetails.setCardEntryMode(getAdditionalValue(additionalDataArray,14));
					}
					break;
				case Constants.PURCHASE_WITH_CASHBACK: //ISO
					movementDetails.setPurchaseAmount(getBigDecimalValue(columns[5].getValue()));
					movementDetails.setWithdrawalAmount(getBigDecimalValue(columns[5].getValue()));
					movementDetails.setOwnerNameSA(columns[18].getValue());
					movementDetails.setAccountNumberSA(cuenta);
					movementDetails.setMaskedCardNumber(getAdditionalValue(additionalDataArray,6));
					movementDetails.setCardId(getAdditionalValue(additionalDataArray,7));
					movementDetails.setEstablishmentNameSD(getAdditionalValue(additionalDataArray, 8));
					movementDetails.setTransactionIdSD(getAdditionalValue(additionalDataArray,4));
					movementDetails.setAuthorizationCode(getAdditionalValue(additionalDataArray,9));
					movementDetails.setCardEntryCode(getAdditionalValue(additionalDataArray,10));
					movementDetails.setCardEntryMode(getAdditionalValue(additionalDataArray,11));
					break;
				case Constants.REVERSAL_PURCHASE_WITH_CASHBACK: //ISO
					movementDetails.setOwnerNameDA(columns[18].getValue());
					movementDetails.setAccountNumberDA(cuenta);
					movementDetails.setMaskedCardNumber(getAdditionalValue(additionalDataArray,6));
					movementDetails.setCardId(getAdditionalValue(additionalDataArray,7));
					movementDetails.setEstablishmentNameSD(getAdditionalValue(additionalDataArray, 8));
					movementDetails.setTransactionIdATM(getAdditionalValue(additionalDataArray,4));
					movementDetails.setAuthorizationCode(getAdditionalValue(additionalDataArray,9));
					movementDetails.setCardEntryCode(getAdditionalValue(additionalDataArray,10));
					movementDetails.setCardEntryMode(getAdditionalValue(additionalDataArray,11));
					break;
				case Constants.REVERSAL: //ISO
					movementDetails.setOwnerNameDA(columns[18].getValue());
					movementDetails.setAccountNumberDA(cuenta);
					movementDetails.setMaskedCardNumber(getAdditionalValue(additionalDataArray,6));
					movementDetails.setCardId(getAdditionalValue(additionalDataArray,7));
					movementDetails.setEstablishmentNameMD(getAdditionalValue(additionalDataArray, 8));
					movementDetails.setTransactionIdMD(getAdditionalValue(additionalDataArray,4));
					movementDetails.setAuthorizationCode(getAdditionalValue(additionalDataArray,9));
					movementDetails.setCardEntryCode(getAdditionalValue(additionalDataArray,10));
					movementDetails.setCardEntryMode(getAdditionalValue(additionalDataArray,11));
					break;
				case Constants.REVERSAL_ONLINE:
				case Constants.REVERSAL_PHYSICAL:
					establishmentName = getAdditionalValue(additionalDataArray, 6);
					establishmentName = establishmentName == null
							? getAdditionalValue(additionalDataArray,7)
							: establishmentName;
					movementDetails.setOwnerNameDA(columns[18].getValue());
					movementDetails.setAccountNumberDA(cuenta);
					movementDetails.setCardId(getAdditionalValue(additionalDataArray,1));
					movementDetails.setTransactionIdSD(getAdditionalValue(additionalDataArray,5));
					movementDetails.setEstablishmentNameSD(establishmentName);
					movementDetails.setAuthorizationCode(getAdditionalValue(additionalDataArray,8));
					movementDetails.setUuid(getAdditionalValue(additionalDataArray,10));
					movementDetails.setMaskedCardNumber(getAdditionalValue(additionalDataArray,11));
					movementDetails.setMovementType(Constants.REVERSAL);
					movementDetails.setCardEntryCode(getAdditionalValue(additionalDataArray,12));
					movementDetails.setCardEntryPin(getAdditionalValue(additionalDataArray,13));
					movementDetails.setCardEntryMode(getAdditionalValue(additionalDataArray,14));
					break;
				case Constants.ATM_DEBIT: //ISO
					movementDetails.setOwnerNameDA(columns[18].getValue());
					movementDetails.setAccountNumberDA(cuenta);
					movementDetails.setBankNameATM(getAdditionalValue(additionalDataArray, 8));
					movementDetails.setTransactionIdATM(getAdditionalValue(additionalDataArray,4));
					movementDetails.setLocationId(getAdditionalValue(additionalDataArray,3));
					movementDetails.setBankBranchCode("");
					movementDetails.setAuthorizationCode(getAdditionalValue(additionalDataArray,9));
					movementDetails.setCardEntryCode(getAdditionalValue(additionalDataArray,10));
					movementDetails.setCardEntryMode(getAdditionalValue(additionalDataArray,11));
					break;
				case Constants.COMMISSION:
					movementDetails.setOwnerNameSA(columns[18].getValue());
					movementDetails.setAccountNumberSA(cuenta);
					String causa = columns[27].getValue();
					if(causa.equals("8110")){
						movementDetails.setReason("CARD_DELIVERY");
					}else if(causa.equals("3101")) {
						String reversalConcept = getAdditionalValue(additionalDataArray,2);
						if("REFUND_REVERSAL".equals(reversalConcept)){
							movementDetails.setCommissionOriginMovementId(getAdditionalValue(additionalDataArray,3));
							movementDetails.setCommissionOriginReferenceNumber(getAdditionalValue(additionalDataArray,4));
						}else{
							movementDetails.setOriginMovementId(getAdditionalValue(additionalDataArray,3));
							movementDetails.setOriginReferenceNumber(getAdditionalValue(additionalDataArray,4));
						}
						movementDetails.setReason(reversalConcept);
					}
					break;
				case Constants.ACCOUNT_CREDIT:
					movementDetails.setOwnerNameDA(columns[18].getValue());
					movementDetails.setAccountNumberDA(cuenta);
					movementDetails.setCreditConcept(getAdditionalValue(additionalDataArray,2));
					movementDetails.setOriginMovementId(getAdditionalValue(additionalDataArray,3));
					movementDetails.setOriginReferenceNumber(getAdditionalValue(additionalDataArray,4));
					movementDetails.setOriginCode(getAdditionalValue(additionalDataArray,5));
					break;
				case Constants.CREDIT_REVERSAL:
					movementDetails.setOwnerNameSA(columns[18].getValue());
					movementDetails.setAccountNumberSA(cuenta);
					movementDetails.setReversalConcept(getAdditionalValue(additionalDataArray,2));
					movementDetails.setOriginMovementId(getAdditionalValue(additionalDataArray,3));
					movementDetails.setOriginReferenceNumber(getAdditionalValue(additionalDataArray,4));
					//movementDetails.setReason(getAdditionalValue(additionalDataArray,2));
					//movementDetails.setCommissionOriginMovementId(getAdditionalValue(additionalDataArray,5));
					//movementDetails.setCommissionOriginReferenceNumber(getAdditionalValue(additionalDataArray,6));
					break;
				case Constants.BONUS:
					movementDetails.setOwnerNameDA(columns[18].getValue());
					movementDetails.setAccountNumberDA(cuenta);
					break;
				default:
					logger.logDebug("Unexpected typeMovement: " + typeMovement);

			}
			logger.logDebug("KCZ: Movement detail Objects: " + movementDetails.toString());
			movementDetailsList.add(movementDetails);
		}
		return movementDetailsList;
	}

	public String getAdditionalValue(String [] additionalData, int index){
		if(additionalData.length > index){
			String data = additionalData[index];
			return data.equals("null") ? null : data;
		}
		return null;
	}

	public BigDecimal getBigDecimalValue(String value){
		if(value != null && !value.isEmpty()){
			return new BigDecimal(value);
		}
		return new BigDecimal(0);

	}

	public String getOperation(String cuenta) {
		return cuenta.contains("*") ? "X":"A";
	}




}
