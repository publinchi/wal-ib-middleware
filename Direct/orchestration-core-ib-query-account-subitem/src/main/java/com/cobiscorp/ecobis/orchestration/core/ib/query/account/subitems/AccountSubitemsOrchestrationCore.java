package com.cobiscorp.ecobis.orchestration.core.ib.query.account.subitems;

import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
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
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.AccountSubitemsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AccountSubitemsResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Module;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountSubitem;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.IWSQueryAccountSubitem;
import com.cobiscorp.ecobis.orchestration.ws.base.SintesisBaseTemplate;

@Component(name = "AccountSubitemsOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "AccountSubitemsOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "AccountSubitemsOrchestrationCore") })
public class AccountSubitemsOrchestrationCore extends SintesisBaseTemplate {

	private static ILogger logger = LogFactory.getLogger(AccountSubitemsOrchestrationCore.class);
	private static final String CLASS_NAME = "AccountSubitemsOrchestrationCore--->";
	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";

	public void loadConfiguration(IConfigurationReader arg0) {
		super.loadConfiguration(arg0);
		this.properties = arg0.getProperties("//property");
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = IWSQueryAccountSubitem.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceAccountSubitem", unbind = "unbindCoreServiceModules")
	protected IWSQueryAccountSubitem coreServiceAccountSubitem;

	/**
	 * Instance Service Interface
	 * 
	 * @param service
	 */
	public void bindCoreServiceAccountSubitem(IWSQueryAccountSubitem service) {
		coreServiceAccountSubitem = service;
	}

	/**
	 * Deleting Service Interface
	 * 
	 * @param service
	 */

	public void unbindCoreServiceModules(IWSQueryAccountSubitem service) {
		coreServiceAccountSubitem = null;
	}

	@Override
	public IProcedureResponse executeWSMethod(Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureRequest anOriginalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		IProcedureResponse responseProc = null;
		try {

			// consulta de items
			responseProc = queryProviderTransaction(anOriginalRequest, aBagSPJavaOrchestration);

			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseProc);

			if (Utils.flowError("updateProviderTransaction", responseProc)) {
				return responseProc;
			}

			return responseProc;
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

	private IProcedureResponse queryProviderTransaction(IProcedureRequest anOriginalRequest, Map<String, Object> aBag) {

		AccountSubitemsResponse wQueryAccountSubitemsResp = new AccountSubitemsResponse();
		AccountSubitemsRequest wQueryAccountSubitemsRequest = transformSubitemRequest(anOriginalRequest.clone());

		try {
			wQueryAccountSubitemsRequest.setOriginalRequest(anOriginalRequest);
			wQueryAccountSubitemsResp = coreServiceAccountSubitem.getAccountSubitems(wQueryAccountSubitemsRequest, aBag,
					properties);
		} catch (CTSServiceException e) {
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
		}

		return transformSubitemResponse(wQueryAccountSubitemsResp, aBag);
	}

	private AccountSubitemsRequest transformSubitemRequest(IProcedureRequest aRequest) {
		AccountSubitemsRequest wAccountSubitemsRequest = new AccountSubitemsRequest();
		Module wModule = new Module();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());
		if (aRequest.readValueParam("@i_id_operativo") != null)
			wAccountSubitemsRequest.setIdOperativo(aRequest.readValueParam("@i_id_operativo"));
		if (aRequest.readValueParam("@i_codmodulo") != null) {
			wModule.setCodModule(Integer.parseInt(aRequest.readValueParam("@i_codmodulo")));
			wAccountSubitemsRequest.setModule(wModule);
		}
		if (aRequest.readValueParam("@i_nrooperacion") != null)
			wAccountSubitemsRequest.setOperation(Integer.parseInt(aRequest.readValueParam("@i_nrooperacion")));
		if (aRequest.readValueParam("@i_fechaoperativa") != null)
			wAccountSubitemsRequest.setDate(Integer.parseInt(aRequest.readValueParam("@i_fechaoperativa")));
		if (aRequest.readValueParam("@i_cuenta") != null)
			wAccountSubitemsRequest.setAccount(aRequest.readValueParam("@i_cuenta"));
		if (aRequest.readValueParam("@i_servicio") != null)
			wAccountSubitemsRequest.setService(Integer.parseInt(aRequest.readValueParam("@i_servicio")));
		if (aRequest.readValueParam("@i_nroitem") != null)
			wAccountSubitemsRequest.setItem(aRequest.readValueParam("@i_nroitem"));
		if (aRequest.readValueParam("@i_cantidadmonto") != null)
			wAccountSubitemsRequest.setAmount(new BigDecimal(aRequest.readValueParam("@i_cantidadmonto")));

		return wAccountSubitemsRequest;
	}

	private IProcedureResponse transformSubitemResponse(AccountSubitemsResponse aQueryAccountSubitemsResponse,
			Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		if (aQueryAccountSubitemsResponse.getReturnCode() != 0) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
					Utils.returnException(aQueryAccountSubitemsResponse.getMessages()));
			wProcedureResponse.addMessage(aQueryAccountSubitemsResponse.getReturnCode(),
					aQueryAccountSubitemsResponse.getMessageError());

		} else {

			// Agregar Header
			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();
			IResultSetRow row = new ResultSetRow();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("DESCRIPCION", ICTSTypes.SQLVARCHAR, 60));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("MONTO", ICTSTypes.SQLMONEY, 20));

			for (AccountSubitem aQueryAccountSubitem : aQueryAccountSubitemsResponse.getSubitemsCollection()) {

				row = new ResultSetRow();

				row.addRowData(1, new ResultSetRowColumnData(false, aQueryAccountSubitem.getDescription()));
				row.addRowData(2, new ResultSetRowColumnData(false, aQueryAccountSubitem.getAmount().toString()));

				data.addRow(row);
			}

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

			wProcedureResponse.addResponseBlock(resultBlock);
			wProcedureResponse.addParam("@o_coderror", ICTSTypes.SQLINT4,
					aQueryAccountSubitemsResponse.getCodError().toString().length(),
					aQueryAccountSubitemsResponse.getCodError().toString());
			wProcedureResponse.addParam("@o_mensaje", ICTSTypes.SQLVARCHAR,
					aQueryAccountSubitemsResponse.getMessageError().length(),
					aQueryAccountSubitemsResponse.getMessageError());
			if (aQueryAccountSubitemsResponse.getCodError() == 0) {
				wProcedureResponse.addParam("@o_totaltem", ICTSTypes.SQLDECIMAL,
						aQueryAccountSubitemsResponse.getTotalItem().toString().length(),
						aQueryAccountSubitemsResponse.getTotalItem().toString());
			}
		}
		wProcedureResponse.setReturnCode(aQueryAccountSubitemsResponse.getReturnCode());
		if (logger.isDebugEnabled())
			logger.logDebug("transformProcedureResponse Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "executeJavaOrchestration");

		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);

		try {
			Map<String, Object> mapInterfaces = new HashMap<String, Object>();
			mapInterfaces.put("coreServiceAccountSubitem", coreServiceAccountSubitem);

			Utils.validateComponentInstance(mapInterfaces);

			if (anOriginalRequest == null)
				if (logger.isInfoEnabled())
					logger.logInfo(CLASS_NAME + "Original Request ISNULL");

			executeSteps(aBagSPJavaOrchestration);

			return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
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

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> aBagSPJavaOrchestration) {

		return (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
	}

}
