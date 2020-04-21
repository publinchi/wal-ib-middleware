package com.cobiscorp.ecobis.orchestration.core.ib.executecauseandcost;

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
import com.cobiscorp.cobis.commons.domains.log.ILogger;
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
import com.cobiscorp.ecobis.ib.application.dtos.AddressRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AddressResponse;
import com.cobiscorp.ecobis.ib.application.dtos.CauseAndCostRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CauseAndCostResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.CauseAndCost;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCauseAndCost;

@Service(value = { ICoreServiceCauseAndCost.class, ICISSPBaseOrchestration.class, IOrchestrator.class })
@Component(name = "ExecuteCuaseAndCostAdmin", immediate = false)
@Properties(value = { @Property(name = "service.description", value = "ExecuteCuaseAndCostAdmin"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.5.0"),
		@Property(name = "service.identifier", value = "ExecuteCuaseAndCostAdmin") })
public class ExecuteCuaseAndCostAdmin extends SPJavaOrchestrationBase implements ICoreServiceCauseAndCost {
	private static final String CLASS_NAME = " >-----> ";
	private static ILogger logger = LogFactory.getLogger(ExecuteCuaseAndCostAdmin.class);
	private static String operation;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCauseAndCost
	 * #executeCauseAndCost(com.cobiscorp.ecobis.ib.application.dtos.
	 * CauseAndCostRequest)
	 */
	@Override
	public CauseAndCostResponse executeCauseAndCost(CauseAndCostRequest CauseAndCostRequestRequest)
			throws CTSServiceException, CTSInfrastructureException {

		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		String CODE_TRN = CauseAndCostRequestRequest.getTrn().toString();
		if (logger.isInfoEnabled())
			logger.logInfo(
					"CauseAndCostRequestRequest.getOperation(); ==> " + CauseAndCostRequestRequest.getOperation());
		operation = CauseAndCostRequestRequest.getOperation();
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, CODE_TRN);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		anOriginalRequest.setSpName("cobis..sp_bv_ing_serv");
		anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, CODE_TRN);
		anOriginalRequest.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR,
				CauseAndCostRequestRequest.getOperation());
		anOriginalRequest.addInputParam("@i_transaccion", ICTSTypes.SQLINT4,
				CauseAndCostRequestRequest.getTransaction().toString());

		if (!CauseAndCostRequestRequest.getOperation().equals("S")
				&& !CauseAndCostRequestRequest.getOperation().equals("ST")) {
			anOriginalRequest.addInputParam("@i_producto", ICTSTypes.SQLINT4,
					CauseAndCostRequestRequest.getProduct().toString());
			anOriginalRequest.addInputParam("@i_servicio", ICTSTypes.SQLVARCHAR,
					CauseAndCostRequestRequest.getService());
			anOriginalRequest.addInputParam("@i_causa", ICTSTypes.SQLVARCHAR, CauseAndCostRequestRequest.getCause());
			anOriginalRequest.addInputParam("@i_costo_transaccion", ICTSTypes.SQLVARCHAR,
					CauseAndCostRequestRequest.getCostTransaction());
		}
		if (CauseAndCostRequestRequest.getOperation().equals("R")) {
			anOriginalRequest.addInputParam("@i_tran_ant", ICTSTypes.SQLINT4,
					CauseAndCostRequestRequest.getTransBefore().toString());
			anOriginalRequest.addInputParam("@i_tipo", ICTSTypes.SQLVARCHAR, CauseAndCostRequestRequest.getType());
			anOriginalRequest.addInputParam("@i_formato_fecha", ICTSTypes.SQLINT4,
					CauseAndCostRequestRequest.getDateFormat().toString());
		}

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Enviando solicitud al Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Request a enviar: " + anOriginalRequest.getProcedureRequestAsString());

		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Data devuelta del Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Response obtenido: " + response.getProcedureResponseAsString());

		return transformToCauseAndCostResponse(response);
	}

	/**
	 * @param response
	 * @return
	 */
	private CauseAndCostResponse transformToCauseAndCostResponse(IProcedureResponse aProcedureResponse) {
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "RESPONSE TO TRANSFORM: " + aProcedureResponse.getProcedureResponseAsString());
		CauseAndCost aCauseAndCost = null;
		List<CauseAndCost> aCauseAndCostList = new ArrayList<CauseAndCost>();
		CauseAndCostResponse aCauseAndCostResponse = new CauseAndCostResponse();
		Integer row; // Acumula el orden sucesivo de las columnas
		if (aProcedureResponse.getReturnCode() == 0) {

			if (!operation.equals("R") || !operation.equals("B")) {
				if (aProcedureResponse.getResultSet(1) != null) {
					IResultSetRow[] rowsCauseAndCostData = aProcedureResponse.getResultSet(1).getData()
							.getRowsAsArray();

					for (IResultSetRow iResultSetRow : rowsCauseAndCostData) {
						IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
						aCauseAndCost = new CauseAndCost();
						if (logger.isInfoEnabled())
							logger.logInfo("columns[0].getValue() ==> " + columns[0].getValue());
						if (columns[0].getValue() != null) {
							aCauseAndCost.setService(columns[0].getValue());
						}
						if (logger.isInfoEnabled())
							logger.logInfo("columns[1].getValue() ==> " + columns[1].getValue());
						if (columns[1].getValue() != null) {
							aCauseAndCost.setProduct(Integer.parseInt(columns[1].getValue()));
						}
						row = 2;
						if (logger.isInfoEnabled())
							logger.logInfo("operation ==> " + operation);
						if (operation.equals("S")) {
							if (logger.isInfoEnabled())
								logger.logInfo(
										"columns[" + row.toString() + "].getValue() ==> " + columns[row].getValue());
							if (columns[row].getValue() != null) {
								aCauseAndCost.setDescriptionService(columns[row].getValue());
							}
							row += 1;
						}
						if (logger.isInfoEnabled())
							logger.logInfo("columns[" + row.toString() + "].getValue() ==> " + columns[row].getValue());
						if (columns[row].getValue() != null) {
							aCauseAndCost.setDescriptionProduct(columns[row].getValue());
						}
						row += 1;
						if (logger.isInfoEnabled())
							logger.logInfo("columns[" + row.toString() + "].getValue() ==> " + columns[row].getValue());
						if (columns[row].getValue() != null) {
							aCauseAndCost.setCause(columns[row].getValue());
						}
						row += 1;
						if (logger.isInfoEnabled())
							logger.logInfo("columns[" + row.toString() + "].getValue() ==> " + columns[row].getValue());
						if (columns[row].getValue() != null) {
							aCauseAndCost.setType(columns[row].getValue());
						}
						row += 1;
						if (logger.isInfoEnabled())
							logger.logInfo("columns[" + row.toString() + "].getValue() ==> " + columns[row].getValue());
						if (columns[row].getValue() != null) {
							aCauseAndCost.setCreationDate(columns[row].getValue());
						}
						row += 1;
						if (logger.isInfoEnabled())
							logger.logInfo("columns[" + row.toString() + "].getValue() ==> " + columns[row].getValue());
						if (columns[row].getValue() != null) {
							aCauseAndCost.setModificationDate(columns[row].getValue());
						}
						aCauseAndCostList.add(aCauseAndCost);
					}
					aCauseAndCostResponse.setCauseAndCostCollection(aCauseAndCostList);
				}
			}
		} else {
			Message[] message = Utils.returnArrayMessage(aProcedureResponse);
			aCauseAndCostResponse.setMessages(message);
		}
		aCauseAndCostResponse.setReturnCode(aProcedureResponse.getReturnCode());
		return aCauseAndCostResponse;
	}
}
