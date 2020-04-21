package com.cobiscorp.ecobis.ib.orchestration.core.ib.transferACH;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;

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
import com.cobiscorp.ecobis.ib.application.dtos.TransferACHRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TransferACHresponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.TransferACH;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceTransferACH;

@Component(name = "TransferACHDetailsOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "TransferACHDetailsOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "TransferACHDetailsOrchestrationCore") })
public class TransferACHDetailsOrchestrationCore extends QueryBaseTemplate {

	@Reference(referenceInterface = ICoreServiceTransferACH.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceTransferACH coreService;
	ILogger logger = LogFactory.getLogger(TransferACHDetailsOrchestrationCore.class);

	public void bindCoreService(ICoreServiceTransferACH service) {
		coreService = service;
	}

	public void unbindCoreService(ICoreServiceTransferACH service) {
		coreService = null;
	}

	@Override
	protected IProcedureResponse executeQuery(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		String messageError = null;
		String messageLog = null;
		String queryName = null;
		TransferACHresponse aTransferACHRequestResponse = new TransferACHresponse();
		TransferACHRequest aTransferACHRequestRequest = transformTransferACHRequestRequest(request.clone());
		try {
			messageError = "getOperation: ERROR EXECUTING SERVICE";
			messageLog = "getInitialCheck " + aTransferACHRequestRequest.getSecuential();
			queryName = "getOperation";
			aTransferACHRequestRequest.setOriginalRequest(request);
			aTransferACHRequestResponse = coreService.GetTransferACH(aTransferACHRequestRequest);
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

		return transformProcedureResponse(aTransferACHRequestResponse, aBagSPJavaOrchestration);
	}

	private IProcedureResponse transformProcedureResponse(TransferACHresponse aTransferACHRequestResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		if (aTransferACHRequestResponse.getReturnCode() == 0) {
			metaData.addColumnMetaData(new ResultSetHeaderColumn("paymentDate", ICTSTypes.SQLVARCHAR, 12));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("productType", ICTSTypes.SQLVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("accountAlias", ICTSTypes.SQLVARCHAR, 35));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("creditAccount", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("entityName", ICTSTypes.SQLVARCHAR, 35));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("ammount", ICTSTypes.SQLMONEY, 24));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("notes", ICTSTypes.SQLVARCHAR, 50));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("creationDate", ICTSTypes.SQLVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("id", ICTSTypes.SQLINT4, 8));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("beneficiaryName", ICTSTypes.SQLVARCHAR, 35));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("beneficiaryId", ICTSTypes.SQLVARCHAR, 35));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("beneficiaryPhone", ICTSTypes.SQLVARCHAR, 16));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("order", ICTSTypes.SQLVARCHAR, 30));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("currencyId", ICTSTypes.SQLINT2, 3));

			for (TransferACH aTransferACH : aTransferACHRequestResponse.getTransferACH()) {

				if (!IsValidCheckbookResponse(aTransferACH))
					return null;
				IResultSetRow row = new ResultSetRow();
				if (aTransferACH.getPaymentDate() != null) {
					row.addRowData(1, new ResultSetRowColumnData(false, aTransferACH.getPaymentDate()));
				} else {
					row.addRowData(1, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferACH.getProductType() != null) {
					row.addRowData(2, new ResultSetRowColumnData(false, aTransferACH.getProductType().toString()));
				} else {
					row.addRowData(2, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferACH.getAccountAlias() != null) {
					row.addRowData(3, new ResultSetRowColumnData(false, aTransferACH.getAccountAlias()));
				} else {
					row.addRowData(3, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferACH.getCreditAccount() != null) {
					row.addRowData(4, new ResultSetRowColumnData(false, aTransferACH.getCreditAccount()));
				} else {
					row.addRowData(4, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferACH.getEntityName() != null) {
					row.addRowData(5, new ResultSetRowColumnData(false, aTransferACH.getEntityName()));
				} else {
					row.addRowData(5, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferACH.getAmount() != null) {
					row.addRowData(6, new ResultSetRowColumnData(false, aTransferACH.getAmount().toString()));
				} else {
					row.addRowData(6, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferACH.getNotes() != null) {
					row.addRowData(7, new ResultSetRowColumnData(false, aTransferACH.getNotes()));
				} else {
					row.addRowData(7, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferACH.getCreationDate() != null) {
					row.addRowData(8, new ResultSetRowColumnData(false, aTransferACH.getCreationDate()));
				} else {
					row.addRowData(8, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferACH.getSecuential() != null) {
					row.addRowData(9, new ResultSetRowColumnData(false, aTransferACH.getSecuential().toString()));
				} else {
					row.addRowData(9, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferACH.getBeneficiaryName() != null) {
					row.addRowData(10, new ResultSetRowColumnData(false, aTransferACH.getBeneficiaryName()));
				} else {
					row.addRowData(10, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferACH.getBeneficiaryId() != null) {
					row.addRowData(11, new ResultSetRowColumnData(false, aTransferACH.getBeneficiaryId().toString()));
				} else {
					row.addRowData(11, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferACH.getBeneficiaryPhone() != null) {
					row.addRowData(12, new ResultSetRowColumnData(false, aTransferACH.getBeneficiaryPhone()));
				} else {
					row.addRowData(12, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferACH.getOrder() != null) {
					row.addRowData(13, new ResultSetRowColumnData(false, aTransferACH.getOrder()));
				} else {
					row.addRowData(13, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferACH.getCurrencyId() != null) {
					row.addRowData(14, new ResultSetRowColumnData(false, aTransferACH.getCurrencyId().toString()));
				} else {
					row.addRowData(14, new ResultSetRowColumnData(false, "0"));
				}

				data.addRow(row);
			}

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
			wProcedureResponse.addResponseBlock(resultBlock);
		} else {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
					Utils.returnException(aTransferACHRequestResponse.getMessages())); // COLOCA
																						// ERRORES
																						// COMO
																						// RESPONSE
																						// DE
																						// LA
																						// TRANSACCIÃ“N
			wProcedureResponse = Utils.returnException(aTransferACHRequestResponse.getMessages());
		}

		wProcedureResponse.setReturnCode(aTransferACHRequestResponse.getReturnCode());
		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final -->" + wProcedureResponse.getProcedureResponseAsString());
		return wProcedureResponse;

	}

	private boolean IsValidCheckbookResponse(TransferACH aTransferACH) {
		String messageError = null;
		messageError = aTransferACH.getPaymentDate() == null ? "  Date_transactioncan't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);
		return true;
	}

	private TransferACHRequest transformTransferACHRequestRequest(IProcedureRequest aRequest) {
		TransferACHRequest aTransferACHRequest = new TransferACHRequest();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;
		messageError = aRequest.readValueParam("@i_cuenta") == null ? " - @i_cuenta can't be null" : "";
		messageError += aRequest.readValueParam("@i_fecha_ini") == null ? " - @i_fecha_ini can't be null" : "";
		messageError += aRequest.readValueParam("@i_fecha_fin") == null ? " - @i_fecha_fin can't be null" : "";
		messageError += aRequest.readValueParam("@i_formato_fecha") == null ? " - @i_formato_fecha can't be null" : "";
		messageError += aRequest.readValueParam("@i_secuencial") == null ? " - @i_secuencial can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);
		aTransferACHRequest.setInitialDate(aRequest.readValueParam("@i_cuenta"));
		aTransferACHRequest.setFinalDate(aRequest.readValueParam("@i_fecha_ini"));
		aTransferACHRequest.setInitialDate(aRequest.readValueParam("@i_fecha_fin"));
		aTransferACHRequest.setDateFormatId(Integer.parseInt(aRequest.readValueParam("@i_formato_fecha")));
		aTransferACHRequest.setSecuential(Integer.parseInt(aRequest.readValueParam("@i_secuencial")));

		return aTransferACHRequest;
	}

	public TransferACHDetailsOrchestrationCore() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		Map<String, Object> mapInterfaces = new HashMap<String, Object>();

		mapInterfaces.put("coreService", coreService);
		Utils.validateComponentInstance(mapInterfaces);

		try {
			aBagSPJavaOrchestration.put(LOG_MESSAGE, "CONSULTA DE TRANSFERENCIAS ACH ");
			executeStepsQueryBase(anOrginalRequest, aBagSPJavaOrchestration);
			return processResponse(anOrginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		} catch (CTSInfrastructureException e) {

			return Utils.returnExceptionService(anOrginalRequest, e);
		}
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
