package com.cobiscorp.ecobis.ib.orchestration.core.ib.transferACH;

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
import com.cobiscorp.cobis.commons.log.LogFactory;
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
import com.cobiscorp.ecobis.ib.application.dtos.AchAccountFormatRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AchAccountFormatRespon;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AchAccountFormat;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreSerciceAchAccountFormat;

@Component(name = "AchFormatAccountOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "AchFormatAccountOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "AchFormatAccountOrchestrationCore") })

public class AchFormatAccountOrchestrationCore extends QueryBaseTemplate {

	@Reference(referenceInterface = ICoreSerciceAchAccountFormat.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreSerciceAchAccountFormat coreService;
	ILogger logger = LogFactory.getLogger(AchFormatAccountOrchestrationCore.class);

	public void bindCoreService(ICoreSerciceAchAccountFormat service) {
		coreService = service;
	}

	public void unbindCoreService(ICoreSerciceAchAccountFormat service) {
		coreService = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate#
	 * validateLocalExecution(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	protected IProcedureResponse validateLocalExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
		// TODO Auto-generated method stub
		IProcedureResponse response = new ProcedureResponseAS();
		response.setReturnCode(0);
		return response;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	protected IProcedureResponse executeQuery(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		String messageError = null;
		String messageLog = null;
		String queryName = null;
		if (logger.isInfoEnabled())
			logger.logInfo("INICIO ===================================>> executeQuery - Orquestacion");
		AchAccountFormatRespon aAchAccountFormatRespon = new AchAccountFormatRespon();
		AchAccountFormatRequest aTransferAchAccountFormatRequest = transformAchAccountFormatRequest(request.clone());
		try {
			messageError = "getOperation: ERROR EXECUTING SERVICE";
			messageLog = "getInitialCheck " + aTransferAchAccountFormatRequest.getId();
			queryName = "getOperation";
			aTransferAchAccountFormatRequest.setOriginalRequest(request);
			aAchAccountFormatRespon = coreService.getACHAcoountResponse(aTransferAchAccountFormatRequest);
		}

		catch (CTSServiceException e) {
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
		aBagSPJavaOrchestration.put(LOG_MESSAGE, messageLog);
		aBagSPJavaOrchestration.put(QUERY_NAME, queryName);
		if (logger.isInfoEnabled())
			logger.logInfo("FIN ===================================>> executeQuery - Orquestacion");
		return transformProcedureResponse(aAchAccountFormatRespon, aBagSPJavaOrchestration);
	}

	private IProcedureResponse transformProcedureResponse(AchAccountFormatRespon aAchAccountFormatRespon,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo("INGRESA EN LA ORUQETSCION transformProcedure");
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		if (aAchAccountFormatRespon.getReturnCode() == 0) {
			metaData.addColumnMetaData(new ResultSetHeaderColumn("id", ICTSTypes.SQLINT2, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("description", ICTSTypes.SQLVARCHAR, 40));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("subsidiary", ICTSTypes.SQLINT2, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("status", ICTSTypes.SQLVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("accountTypeId", ICTSTypes.SQLINT2, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("accountType", ICTSTypes.SQLVARCHAR, 40));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("lengthAccount", ICTSTypes.SQLINT2, 10));

			for (AchAccountFormat aAchAccountFormat : aAchAccountFormatRespon.getAchAccountFormatCollection()) {
				if (!IsValidCheckbookResponse(aAchAccountFormat))
					return null;
				IResultSetRow row = new ResultSetRow();
				if (aAchAccountFormat.getId() != null) {
					row.addRowData(1, new ResultSetRowColumnData(false, aAchAccountFormat.getId().toString()));
				} else {
					row.addRowData(1, new ResultSetRowColumnData(false, "0"));
				}

				if (aAchAccountFormat.getDescription() != null) {
					row.addRowData(2, new ResultSetRowColumnData(false, aAchAccountFormat.getDescription()));
				} else {
					row.addRowData(2, new ResultSetRowColumnData(false, " "));
				}

				if (aAchAccountFormat.getSubsidiary() != null) {
					row.addRowData(3, new ResultSetRowColumnData(false, aAchAccountFormat.getSubsidiary().toString()));
				} else {
					row.addRowData(3, new ResultSetRowColumnData(false, "0"));
				}

				if (aAchAccountFormat.getStatus() != null) {
					row.addRowData(4, new ResultSetRowColumnData(false, aAchAccountFormat.getStatus()));
				} else {
					row.addRowData(4, new ResultSetRowColumnData(false, " "));
				}

				if (aAchAccountFormat.getAccountTypeId() != null) {
					row.addRowData(5,
							new ResultSetRowColumnData(false, aAchAccountFormat.getAccountTypeId().toString()));
				} else {
					row.addRowData(5, new ResultSetRowColumnData(false, "0"));
				}

				if (aAchAccountFormat.getAccountType() != null) {
					row.addRowData(6, new ResultSetRowColumnData(false, aAchAccountFormat.getAccountType()));
				} else {
					row.addRowData(6, new ResultSetRowColumnData(false, " "));
				}

				if (aAchAccountFormat.getLengthAccount() != null) {
					row.addRowData(7,
							new ResultSetRowColumnData(false, aAchAccountFormat.getLengthAccount().toString()));
				} else {
					row.addRowData(7, new ResultSetRowColumnData(false, "0"));
				}
				data.addRow(row);
			}
			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
			wProcedureResponse.addResponseBlock(resultBlock);
		}

		else {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
					Utils.returnException(aAchAccountFormatRespon.getMessages())); // COLOCA
																					// ERRORES
																					// COMO
																					// RESPONSE
																					// DE
																					// LA
																					// TRANSACCIÃ“N
			wProcedureResponse = Utils.returnException(aAchAccountFormatRespon.getMessages());
		}

		wProcedureResponse.setReturnCode(aAchAccountFormatRespon.getReturnCode());
		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final -->" + wProcedureResponse.getProcedureResponseAsString());
		return wProcedureResponse;
	}

	private boolean IsValidCheckbookResponse(AchAccountFormat aAchAccountFormat) {
		if (logger.isInfoEnabled())
			logger.logInfo("INICIO ===================================>> IsValidCheckbookResponse - Orquestacion");
		String messageError = null;
		messageError = aAchAccountFormat.getId() == null ? "  Id  null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);
		if (logger.isInfoEnabled())
			logger.logInfo("FIN ===================================>> IsValidCheckbookResponse - Orquestacion");
		return true;
	}

	private AchAccountFormatRequest transformAchAccountFormatRequest(IProcedureRequest aRequest) {
		if (logger.isInfoEnabled())
			logger.logInfo("INICIO ===================================>> transformTransferACHRequestRequest");
		AchAccountFormatRequest aAchAccountFormatRequest = new AchAccountFormatRequest();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());
		String messageError = null;
		messageError = aRequest.readValueParam("@i_banco") == null ? " - @i_banco can't be null" : "";
		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);
		aAchAccountFormatRequest.setId(Integer.parseInt(aRequest.readValueParam("@i_banco")));

		return aAchAccountFormatRequest;
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();

		mapInterfaces.put("coreService", coreService);
		Utils.validateComponentInstance(mapInterfaces);

		try {
			aBagSPJavaOrchestration.put(LOG_MESSAGE, "CONSULTA ACH CUENTAS ");
			executeStepsQueryBase(anOrginalRequest, aBagSPJavaOrchestration);
			if (logger.isInfoEnabled())
				logger.logInfo("FIN ===================================>> executeJavaOrchestration - Orquestacion"
						+ anOrginalRequest.getProcedureRequestAsString());
			return processResponse(anOrginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		} catch (CTSInfrastructureException e) {

			return Utils.returnExceptionService(anOrginalRequest, e);
		}
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureRespFinal = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
		return wProcedureRespFinal;
	}
}
