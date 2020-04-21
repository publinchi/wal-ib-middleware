package com.cobiscorp.ecobis.orchestration.core.ib.query.programmedsavings;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.ProgrammedSavingsAccountRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ProgrammedSavingsAccountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ProgrammedSavingsExpirationDateRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ProgrammedSavingsExpirationDateResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ProgrammedSavingsMinimumAmountRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ProgrammedSavingsMinimumAmountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ProgrammedSavingsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ProgrammedSavingsResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ProgrammedSavings;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ProgrammedSavingsAccount;
import com.cobiscorp.ecobis.ib.orchestration.dtos.User;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceProgrammedSavings;

/**
 * @author gcondo
 * @since Sep 25, 2014
 * @version 1.0.0
 */

@Component(name = "ProgrammedSavingsQueryOrchestationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "ProgrammedSavingsQueryOrchestationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ProgrammedSavingsQueryOrchestationCore") })

public class ProgrammedSavingsQueryOrchestationCore extends QueryBaseTemplate {

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceProgrammedSavings.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceProgrammedSavings coreService;

	ILogger logger = this.getLogger();

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreService(ICoreServiceProgrammedSavings service) {
		coreService = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreService(ICoreServiceProgrammedSavings service) {
		coreService = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * executeQuery
	 **********************************************************************************************************/
	@Override
	protected IProcedureResponse executeQuery(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "------> JBA - inicia executeQuery de ProgrammedSavings:");
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		String messageError = null;
		String messageLog = null;
		String queryName = null;
		String lsOperacion = null;

		lsOperacion = request.readValueParam("@i_operacion");

		/** Objetos para ProgrammedSavings */
		ProgrammedSavingsResponse aProgrammedSavingsResponse = null;
		ProgrammedSavingsRequest aProgrammedSavingsRequest = null;

		/** Objetos para ProgrammedSavingAccount */
		ProgrammedSavingsAccountResponse aProgrammedSavingsAccountResponse = null;
		ProgrammedSavingsAccountRequest aProgrammedSavingsAccountRequest = null;

		/** Objetos para ProgrammedSavingsExpiration */
		ProgrammedSavingsExpirationDateResponse aProgrammedSavingsExpirationDateResponse = null;
		ProgrammedSavingsExpirationDateRequest aProgrammedSavingsExpirationDateRequest = null;

		/** Objetos para ProgrammedSavingsMinimumAmount */
		ProgrammedSavingsMinimumAmountResponse aProgrammedSavingsMinimumAmountResponse = null;
		ProgrammedSavingsMinimumAmountRequest aProgrammedSavingsMinimumAmountRequest = null;

		if (lsOperacion.equals("Q")) {
			aProgrammedSavingsRequest = transformProgrammedSavingsRequest(request.clone());
		}

		if (lsOperacion.equals("A")) {
			aProgrammedSavingsAccountRequest = transformProgrammedSavingsAccountRequest(request.clone());
		}

		if (lsOperacion.equals("F")) {
			aProgrammedSavingsExpirationDateRequest = transformProgrammedSavingsExpirationDateRequest(request.clone());
		}

		if (lsOperacion.equals("P")) {
			aProgrammedSavingsMinimumAmountRequest = transformProgrammedSavingsMinimumAmountRequest(request.clone());
		}

		try {
			messageError = "getProgrammedSavings: ERROR EXECUTING SERVICE";
			messageLog = "getProgrammedSavings ";
			queryName = "getProgrammedSavings";
			aBagSPJavaOrchestration.put(LOG_MESSAGE, messageLog);
			aBagSPJavaOrchestration.put(QUERY_NAME, queryName);

			if (lsOperacion.equals("Q")) {
				aProgrammedSavingsRequest.setOriginalRequest(request);
				aProgrammedSavingsResponse = coreService.getProgrammedSavings(aProgrammedSavingsRequest);
				return transformProcedureResponse(aProgrammedSavingsResponse, aBagSPJavaOrchestration);
			}

			if (lsOperacion.equals("A")) {
				aProgrammedSavingsAccountRequest.setOriginalRequest(request);
				aProgrammedSavingsAccountResponse = coreService
						.ProgrammedSavingsAccount(aProgrammedSavingsAccountRequest);
				return transformProcedureAccountResponse(aProgrammedSavingsAccountResponse, aBagSPJavaOrchestration);
			}

			if (lsOperacion.equals("F")) {
				aProgrammedSavingsExpirationDateRequest.setOriginalRequest(request);
				aProgrammedSavingsExpirationDateResponse = coreService
						.getExpirationDate(aProgrammedSavingsExpirationDateRequest);
				return transformProcedureExpirationDateResponse(aProgrammedSavingsExpirationDateResponse,
						aBagSPJavaOrchestration);
			}

			if (lsOperacion.equals("P")) {
				aProgrammedSavingsMinimumAmountRequest.setOriginalRequest(request);
				aProgrammedSavingsMinimumAmountResponse = coreService
						.getMinimunAmount(aProgrammedSavingsMinimumAmountRequest);
				return transformProcedureMinimumAmountResponse(aProgrammedSavingsMinimumAmountResponse,
						aBagSPJavaOrchestration);
			}
		} catch (CTSServiceException e) {
			e.printStackTrace();
			if (logger.isDebugEnabled())
				logger.logDebug(messageError);
			return null;
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
			if (logger.isDebugEnabled())
				logger.logDebug(messageError);
			return null;
		}
		return wProcedureResponse;
	}

	/**
	 * executeJavaOrchestration
	 **********************************************************************************************************/
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreService", coreService);
		Utils.validateComponentInstance(mapInterfaces);
		try {
			executeStepsQueryBase(anOrginalRequest, aBagSPJavaOrchestration);
			return processResponse(anOrginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		} catch (CTSInfrastructureException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		}
	}

	/**
	 * processResponse
	 **********************************************************************************************************/
	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = (IProcedureResponse) aBagSPJavaOrchestration.get("RESPONSE_TRANSACTION");
		return response;
	}

	/**
	 * transformProgrammedSavingsRequest
	 **********************************************************************************************************/
	private ProgrammedSavingsRequest transformProgrammedSavingsRequest(IProcedureRequest aRequest) {
		ProgrammedSavingsRequest ProgrammedSavingsReq = new ProgrammedSavingsRequest();
		Product product = new Product();
		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());
		ProgrammedSavingsReq.setCodeTransactionalIdentifier(aRequest.readValueParam("@i_operacion"));
		product.setProductNumber(aRequest.readValueParam("@i_cta_ahoprog"));
		ProgrammedSavingsReq.setProductNumber(product);
		return ProgrammedSavingsReq;
	}

	/**
	 * transformProgrammedSavingsAccountRequest
	 **********************************************************************************************************/
	private ProgrammedSavingsAccountRequest transformProgrammedSavingsAccountRequest(IProcedureRequest aRequest) {
		ProgrammedSavingsAccountRequest ProgrammedSavingsAccountReq = new ProgrammedSavingsAccountRequest();
		User wUser = new User();
		wUser.setEntityId(Integer.parseInt(aRequest.readValueParam("@i_cliente")));
		ProgrammedSavingsAccountReq.setCodeTransactionalIdentifier(aRequest.readValueParam("@i_operacion"));
		ProgrammedSavingsAccountReq.setUser(wUser);
		ProgrammedSavingsAccountReq.setOriginalRequest(aRequest);
		return ProgrammedSavingsAccountReq;
	}

	/**
	 * transformProgrammedSavingsExpirationDateRequest
	 **********************************************************************************************************/
	private ProgrammedSavingsExpirationDateRequest transformProgrammedSavingsExpirationDateRequest(
			IProcedureRequest aRequest) {
		ProgrammedSavingsExpirationDateRequest aProgrammedSavingsExpirationDateRequest = new ProgrammedSavingsExpirationDateRequest();
		aProgrammedSavingsExpirationDateRequest.setInitialDate(aRequest.readValueParam("@i_fecha_ini"));
		aProgrammedSavingsExpirationDateRequest.setTerm(aRequest.readValueParam("@i_plazo"));
		aProgrammedSavingsExpirationDateRequest.setCodeTransactionalIdentifier(aRequest.readValueParam("@i_operacion"));
		return aProgrammedSavingsExpirationDateRequest;
	}

	/**
	 * transformProgrammedSavingsMinimumAmountRequest
	 **********************************************************************************************************/
	private ProgrammedSavingsMinimumAmountRequest transformProgrammedSavingsMinimumAmountRequest(
			IProcedureRequest aRequest) {
		ProgrammedSavingsMinimumAmountRequest aProgrammedSavingsMinimumAmountRequest = new ProgrammedSavingsMinimumAmountRequest();
		aProgrammedSavingsMinimumAmountRequest.setCodeTransactionalIdentifier(aRequest.readValueParam("@i_operacion"));
		return aProgrammedSavingsMinimumAmountRequest;
	}

	/**
	 * transformProcedureResponse
	 * 
	 * @param aBagSPJavaOrchestration
	 **********************************************************************************************************/
	private IProcedureResponse transformProcedureResponse(ProgrammedSavingsResponse aProgrammedSavingsResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");
		// Agregar Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		metaData.addColumnMetaData(new ResultSetHeaderColumn("sequential", ICTSTypes.SQLMONEY, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("savingtime", ICTSTypes.SQLVARCHAR, 30));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("paymentDate", ICTSTypes.SQLVARCHAR, 30));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("amount", ICTSTypes.SQLMONEY, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("currency", ICTSTypes.SQLVARCHAR, 4));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("executed", ICTSTypes.SQLVARCHAR, 4));
		if (aProgrammedSavingsResponse.getReturnCode() == 0) {
			for (ProgrammedSavings aProgrammedSavings : aProgrammedSavingsResponse.getProgrammendSavingsCollection()) {
				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, aProgrammedSavings.getSequential().toString()));
				row.addRowData(2, new ResultSetRowColumnData(false, aProgrammedSavings.getSavingsTime()));
				row.addRowData(3, new ResultSetRowColumnData(false, aProgrammedSavings.getPaymentDate()));
				row.addRowData(4, new ResultSetRowColumnData(false, aProgrammedSavings.getAmount().toString()));
				row.addRowData(5,
						new ResultSetRowColumnData(false, aProgrammedSavings.getCurrency().getCurrencyNemonic()));
				row.addRowData(6, new ResultSetRowColumnData(false, aProgrammedSavings.getExecuted()));
				data.addRow(row);
			}
			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
			wProcedureResponse.addResponseBlock(resultBlock);
		} else {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
					Utils.returnException(aProgrammedSavingsResponse.getMessages()));
			wProcedureResponse = Utils.returnException(aProgrammedSavingsResponse.getMessages());
		}
		wProcedureResponse.setReturnCode(aProgrammedSavingsResponse.getReturnCode());
		wProcedureResponse.addParam("@o_descripcion_moneda", ICTSTypes.SQLVARCHAR, 0,
				aProgrammedSavingsResponse.getCurrencyDescription());
		wProcedureResponse.addParam("@o_nombre_cta", ICTSTypes.SQLVARCHAR, 0,
				aProgrammedSavingsResponse.getCurrencyDescription());
		wProcedureResponse.addParam("@o_tipo_cta", ICTSTypes.SQLVARCHAR, 0,
				aProgrammedSavingsResponse.getCurrencyDescription());
		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final -->" + wProcedureResponse.getProcedureResponseAsString());
		return wProcedureResponse;
	}

	/**
	 * transformProcedureAccountResponse
	 * 
	 * @param aBagSPJavaOrchestration
	 **********************************************************************************************************/
	private IProcedureResponse transformProcedureAccountResponse(
			ProgrammedSavingsAccountResponse aProgrammedSavingsAccountResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		// Agregar Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		metaData.addColumnMetaData(new ResultSetHeaderColumn("account", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("currencyId", ICTSTypes.SQLINT1, 3));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("clientName", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("productBalance", ICTSTypes.SQLMONEY, 21));
		if (aProgrammedSavingsAccountResponse.getReturnCode() == 0) {
			for (ProgrammedSavingsAccount aProgrammedSavingsAccount : aProgrammedSavingsAccountResponse
					.getProgrammedSavingsAccountCollection()) {
				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, aProgrammedSavingsAccount.getAccount()));
				row.addRowData(2, new ResultSetRowColumnData(false,
						aProgrammedSavingsAccount.getCurrencyId().getCurrencyId().toString()));
				row.addRowData(3,
						new ResultSetRowColumnData(false, aProgrammedSavingsAccount.getClient().getCompleteName()));
				row.addRowData(4, new ResultSetRowColumnData(false,
						Double.toString(aProgrammedSavingsAccount.getProductBalance())));
				data.addRow(row);
			}
			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
			wProcedureResponse.addResponseBlock(resultBlock);
		} else {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
					Utils.returnException(aProgrammedSavingsAccountResponse.getMessages()));
			wProcedureResponse = Utils.returnException(aProgrammedSavingsAccountResponse.getMessages());
		}
		return wProcedureResponse;
	}

	/**
	 * transformProcedureExpirationDateResponse
	 * 
	 * @param aBagSPJavaOrchestration
	 **********************************************************************************************************/
	private IProcedureResponse transformProcedureExpirationDateResponse(
			ProgrammedSavingsExpirationDateResponse aProgrammedSavingsExpirationDateResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (aProgrammedSavingsExpirationDateResponse.getReturnCode() == 0) {
			wProcedureResponse.addParam("@o_fecha_ven", ICTSTypes.SQLVARCHAR, 0,
					aProgrammedSavingsExpirationDateResponse.getExpirationDates());
		} else {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
					Utils.returnException(aProgrammedSavingsExpirationDateResponse.getMessages()));
			wProcedureResponse = Utils.returnException(aProgrammedSavingsExpirationDateResponse.getMessages());
		}
		return wProcedureResponse;
	}

	/**
	 * transformProcedureMinimumAmountResponse
	 * 
	 * @param aBagSPJavaOrchestration
	 **********************************************************************************************************/
	private IProcedureResponse transformProcedureMinimumAmountResponse(
			ProgrammedSavingsMinimumAmountResponse aProgrammedSavingsMinimumAmountResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		String Valor = new String();
		if (aProgrammedSavingsMinimumAmountResponse.getReturnCode() == 0) {
			if (logger.isDebugEnabled())
				logger.logDebug("Transform Procedure MinimumAmount Response: "
						+ aProgrammedSavingsMinimumAmountResponse.toString());
			Valor = aProgrammedSavingsMinimumAmountResponse.getMinimumAmount().toString();
			wProcedureResponse.addParam("@o_monto_min", ICTSTypes.SQLMONEY4, 0, Valor);
		} else {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
					Utils.returnException(aProgrammedSavingsMinimumAmountResponse.getMessages()));
			wProcedureResponse = Utils.returnException(aProgrammedSavingsMinimumAmountResponse.getMessages());
		}
		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Account MinimumAmount Response Final -->"
					+ wProcedureResponse.getProcedureResponseAsString());
		return wProcedureResponse;
	}

	@Override
	public IProcedureResponse validateLocalExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
		ProcedureResponseAS response = new ProcedureResponseAS();
		response.setReturnCode(0);
		return response;
	}

}
