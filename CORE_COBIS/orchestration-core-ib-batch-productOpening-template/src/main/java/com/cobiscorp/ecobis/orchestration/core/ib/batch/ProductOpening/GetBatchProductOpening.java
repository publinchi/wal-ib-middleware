package com.cobiscorp.ecobis.orchestration.core.ib.batch.ProductOpening;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

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
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.ecobis.ib.application.dtos.BatchProductOpeningRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BatchProductOpeningResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.BatchProductOpening;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceBatchProductOpening;

@Service(value = { ICoreServiceBatchProductOpening.class, ICISSPBaseOrchestration.class, IOrchestrator.class })
@Component(name = "BatchProductOpening", immediate = false)
@Properties(value = { @Property(name = "service.description", value = "BatchProductOpening"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "BatchProductOpening") })
public class GetBatchProductOpening extends SPJavaOrchestrationBase implements ICoreServiceBatchProductOpening {

	/**
	 * Instance logger component
	 */
	private static ILogger logger = LogFactory.getLogger(GetBatchProductOpening.class);
	private static final String CLASS_NAME = " >-----> ";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.commons.components.ICOBISComponent#loadConfiguration(
	 * com.cobiscorp.cobis.commons.configuration.IConfigurationReader)
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceBatchProductOpening#executeBatchProductOpening(com.cobiscorp.
	 * ecobis.ib.application.dtos.BatchProductOpeningRequest)
	 */
	@Override
	public BatchProductOpeningResponse executeBatchProductOpening(
			BatchProductOpeningRequest aBatchProductOpeningRequest)
			throws CTSServiceException, CTSInfrastructureException {
		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1801022");
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		anOriginalRequest.setSpName("cobis..sp_bv_gen_dataprod_ej");
		anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1801022");
		anOriginalRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		anOriginalRequest.addInputParam("@i_fecha_ini", ICTSTypes.SQLDATETIME,
				aBatchProductOpeningRequest.getfIni().toString());
		anOriginalRequest.addInputParam("@i_fecha_fin", ICTSTypes.SQLDATETIME,
				aBatchProductOpeningRequest.getfFin().toString());
		anOriginalRequest.addInputParam("@i_servicio", ICTSTypes.SQLINT4,
				aBatchProductOpeningRequest.getServicio().toString());
		anOriginalRequest.addInputParam("@i_sarta", ICTSTypes.SQLINT4,
				aBatchProductOpeningRequest.getBatch().getSarta().toString());
		anOriginalRequest.addInputParam("@i_batch", ICTSTypes.SQLINT4,
				aBatchProductOpeningRequest.getBatch().getBatch().toString());
		anOriginalRequest.addInputParam("@i_secuencial", ICTSTypes.SQLINT4,
				aBatchProductOpeningRequest.getBatch().getSecuencial().toString());
		anOriginalRequest.addInputParam("@i_corrida", ICTSTypes.SQLINT4,
				aBatchProductOpeningRequest.getBatch().getCorrida().toString());
		anOriginalRequest.addInputParam("@i_intento", ICTSTypes.SQLINT4,
				aBatchProductOpeningRequest.getBatch().getIntento().toString());
		anOriginalRequest.addInputParam("@i_numRows", ICTSTypes.SQLINT4,
				aBatchProductOpeningRequest.getRowsCount().toString());
		// Se toma la variable Customer para enviar el siguiente registro a
		// consultar, este tendra un secuencial
		anOriginalRequest.addInputParam("@i_cliente", ICTSTypes.SQLINT4,
				aBatchProductOpeningRequest.getCustomer().toString());
		anOriginalRequest.addOutputParam("@o_cliente", ICTSTypes.SQLINT4, "000000000");
		anOriginalRequest.addOutputParam("@o_producto", ICTSTypes.SQLINT4, "000000000");
		anOriginalRequest.addOutputParam("@o_moneda", ICTSTypes.SQLINT4, "000000000");
		anOriginalRequest.addOutputParam("@o_cuenta", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXX");
		anOriginalRequest.addOutputParam("@o_secuencial", ICTSTypes.SQLINT4, "000000000");

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Obtencion del servicio");

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Enviando solicitud al Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Request a enviar: " + anOriginalRequest.getProcedureRequestAsString());

		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Data devuelta del Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Response obtenido: " + response.getProcedureResponseAsString());

		return transformTohProductOpeningResponse(response);
	}
	
	@Override
	public void executeBatchLocalProduct(BatchProductOpeningResponse aBatchProductOpeningRequest)
			throws CTSServiceException, CTSInfrastructureException {
		
		logger.logInfo("JC Execute LOCAL Products ");
		
		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1801022");
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		anOriginalRequest.setSpName("cob_bvirtual..sp_bv_gen_doit_dataprod");


		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Obtencion del servicio");

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Enviando solicitud al Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Request a enviar: " + anOriginalRequest.getProcedureRequestAsString());

		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Data devuelta del Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Response obtenido: " + response.getProcedureResponseAsString());

	
	}
	

	/**
	 * @param response
	 * @return
	 */
	private BatchProductOpeningResponse transformTohProductOpeningResponse(IProcedureResponse aProcedureResponse) {
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "RESPONSE TO TRANSFORM: " + aProcedureResponse.getProcedureResponseAsString());
		BatchProductOpening aBatchProductOpening;
		List<BatchProductOpening> aBatchProductOpeningList = new ArrayList<BatchProductOpening>();
		BatchProductOpeningResponse aBatchProductOpeningResp = new BatchProductOpeningResponse();

		if (aProcedureResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsAddress = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();
			if (rowsAddress != null) {
				for (IResultSetRow iResultSetRow : rowsAddress) {
					IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
					aBatchProductOpening = new BatchProductOpening();

					if (columns[0].getValue() != null) {
						aBatchProductOpening.setDate(columns[0].getValue());
					}
					if (columns[1].getValue() != null) {
						aBatchProductOpening.setStatus(columns[1].getValue());
					}
					if (columns[2].getValue() != null) {
						aBatchProductOpening.setCustomerId(Integer.parseInt(columns[2].getValue()));
					}
					if (columns[3].getValue() != null) {
						aBatchProductOpening.setProductId(Integer.parseInt(columns[3].getValue()));
					}
					if (columns[4].getValue() != null) {
						aBatchProductOpening.setCurrencyId(Integer.parseInt(columns[4].getValue()));
					}
					if (columns[5].getValue() != null) {
						aBatchProductOpening.setAccount(columns[5].getValue());
					}
					if (columns[6].getValue() != null) {
						aBatchProductOpening.setStatusproduct(columns[6].getValue());
					}
					if (columns[7].getValue() != null) {
						aBatchProductOpening.setOfficeId(Integer.parseInt(columns[7].getValue()));
					}
					if (columns[8].getValue() != null) {
						aBatchProductOpening.setDestAccount(columns[8].getValue());
					}
					if (columns[9].getValue() != null) {
						aBatchProductOpening.setTypeSignature(columns[9].getValue());
					}
					if (columns[10].getValue() != null) {
						aBatchProductOpening.setTypeAccount(columns[10].getValue());
					}

					aBatchProductOpeningList.add(aBatchProductOpening);
				}
				if (aProcedureResponse.readValueParam("@o_cliente") != null) {
					aBatchProductOpeningResp
							.setMaxCustomer(Integer.parseInt(aProcedureResponse.readValueParam("@o_cliente")));
				}
				if (aProcedureResponse.readValueParam("@o_producto") != null) {
					aBatchProductOpeningResp
							.setMaxProduct(Integer.parseInt(aProcedureResponse.readValueParam("@o_producto")));
				}
				if (aProcedureResponse.readValueParam("@o_moneda") != null) {
					aBatchProductOpeningResp
							.setMaxCurrency(Integer.parseInt(aProcedureResponse.readValueParam("@o_moneda")));
				}
				if (aProcedureResponse.readValueParam("@o_cuenta") != null) {
					aBatchProductOpeningResp.setMaxAccount(aProcedureResponse.readValueParam("@o_cuenta"));
				}
				if (aProcedureResponse.readValueParam("@o_secuencial") != null) {
					aBatchProductOpeningResp
							.setSecuential(Integer.parseInt(aProcedureResponse.readValueParam("@o_secuencial")));
				}
				aBatchProductOpeningResp.setBatchProductOpeningList(aBatchProductOpeningList);
			}
		} else {
			aBatchProductOpeningResp.setMessages(Utils.returnArrayMessage(aProcedureResponse)); 
		}
		aBatchProductOpeningResp.setReturnCode(aProcedureResponse.getReturnCode());

		return aBatchProductOpeningResp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase#
	 * executeJavaOrchestration(com.cobiscorp.cobis.cts.domains.
	 * IProcedureRequest, java.util.Map)
	 */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase#
	 * processResponse(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}
}
