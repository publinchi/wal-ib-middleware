package com.cobiscorp.ecobis.orchestration.core.ib.transfers;

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
import com.cobiscorp.cobis.commons.log.ILogger;
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
import com.cobiscorp.ecobis.ib.application.dtos.InternationalTransfersReceivedRequest;
import com.cobiscorp.ecobis.ib.application.dtos.InternationalTransfersReceivedResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.InternationalTransfersReceived;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceInternationalTransfersReceived;

@Component(name = "InternationalTransfersReceivedQueryOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = {
		@Property(name = "service.description", value = "InternationalTransfersReceivedQueryOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "InternationalTransfersReceivedQueryOrchestrationCore") })

public class InternationalTransfersReceivedQueryOrchestrationCore extends QueryBaseTemplate {

	@Reference(referenceInterface = ICoreServiceInternationalTransfersReceived.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceInternationalTransfersReceived coreService;
	ILogger logger = LogFactory.getLogger(InternationalTransfersReceivedQueryOrchestrationCore.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate#
	 * validateLocalExecution(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	protected IProcedureResponse validateLocalExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
		IProcedureResponse response = new ProcedureResponseAS();
		response.setReturnCode(0);
		return response;
	}

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreService(ICoreServiceInternationalTransfersReceived service) {
		coreService = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreService(ICoreServiceInternationalTransfersReceived service) {
		coreService = null;
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

		InternationalTransfersReceivedResponse aInternationalTransfersReceivedResponse = new InternationalTransfersReceivedResponse();
		InternationalTransfersReceivedRequest aInternationalTransfersReceivedRequest = transformInternationalTransfersReceivedRequest(
				request.clone());

		try {
			messageError = "getOperation: ERROR EXECUTING SERVICE";
			messageLog = "getOperation " + aInternationalTransfersReceivedRequest.getOperation();
			queryName = "getOperation";

			aInternationalTransfersReceivedRequest.setOriginalRequest(request);
			aInternationalTransfersReceivedResponse = coreService
					.searchInternationalTransfersReceived(aInternationalTransfersReceivedRequest);
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

		aBagSPJavaOrchestration.put(LOG_MESSAGE, messageLog);
		aBagSPJavaOrchestration.put(QUERY_NAME, queryName);
		return transformProcedureResponse(aInternationalTransfersReceivedResponse, aBagSPJavaOrchestration);
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreService", coreService);
		Utils.validateComponentInstance(mapInterfaces);

		try {
			aBagSPJavaOrchestration.put(LOG_MESSAGE, "CONSULTA DE TRANSFERENCIAS RECIBIDAS DEL EXTERIOR ");
			executeStepsQueryBase(anOrginalRequest, aBagSPJavaOrchestration);
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
		IProcedureResponse response = (IProcedureResponse) aBagSPJavaOrchestration.get("RESPONSE_TRANSACTION");
		return response;
	}

	private InternationalTransfersReceivedRequest transformInternationalTransfersReceivedRequest(
			IProcedureRequest aRequest) {

		InternationalTransfersReceivedRequest aInternationalTransfersReceivedRequest = new InternationalTransfersReceivedRequest();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;

		messageError = aRequest.readValueParam("@i_opeban") == null ? " - @i_ope can't be null" : "";
		messageError += aRequest.readValueParam("@i_fdate") == null ? " - @i_fdate can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		aInternationalTransfersReceivedRequest.setOperation(aRequest.readValueParam("@i_opeban"));
		aInternationalTransfersReceivedRequest.setDateFormat(Integer.parseInt(aRequest.readValueParam("@i_fdate")));

		return aInternationalTransfersReceivedRequest;
	}

	private IProcedureResponse transformProcedureResponse(
			InternationalTransfersReceivedResponse aInternationalTransfersReceivedResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		// Agregar Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("officeNumber", ICTSTypes.SQLINT4, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("officeDescription", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("reference", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("concept", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("lastBeneficiary", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("verificationDate", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("agreement", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("accountType", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("account", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("preliminaryAgreementDate", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("official", ICTSTypes.SQLINT4, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("officialDescription", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("category", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("categoryDescription", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("operationNumber", ICTSTypes.SQLINT4, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("operationDescription", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("officeBelongNumber", ICTSTypes.SQLINT4, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("officeName", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("originatorNumber", ICTSTypes.SQLINT4, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("originatorName", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("idNumber", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("originatorAddressNumber", ICTSTypes.SQLINT4, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("originatorAddress", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("beneficiaryName", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("beneficiaryAddress", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("continent", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("beneficiaryContinent", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("beneficiaryCountryNumber", ICTSTypes.SQLINT4, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("beneficiaryCountryName", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("beneficiaryCityNumber", ICTSTypes.SQLINT4, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("beneficiaryCityName", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("amount", ICTSTypes.SQLDECIMAL, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("currencyNumber", ICTSTypes.SQLINT4, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("currencyName", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("priority", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("messageName", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("issueDate", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("term", ICTSTypes.SQLINT4, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("dueDate", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("notification", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("onBehalfOfAddress", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("lastShipmentDate", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("issueReference", ICTSTypes.SQLVARCHAR, 24));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("transactionDate", ICTSTypes.SQLVARCHAR, 24));
		if (aInternationalTransfersReceivedResponse.getReturnCode() == 0) {
			for (InternationalTransfersReceived aInternationalTransfersReceived : aInternationalTransfersReceivedResponse
					.getInternationalTransfersReceivedCollection()) {
				if (!IsValidCheckbookResponse(aInternationalTransfersReceived))
					return null;

				IResultSetRow row = new ResultSetRow();

				if (aInternationalTransfersReceived.getOfficeNumber() != null) {
					row.addRowData(1, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getOfficeNumber().toString()));
				} else {
					row.addRowData(1, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getOfficeDescription() != null) {
					row.addRowData(2, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getOfficeDescription().toString()));
				} else {
					row.addRowData(2, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getReference() != null) {
					row.addRowData(3, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getReference().toString()));
				} else {
					row.addRowData(3, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getConcept() != null) {
					row.addRowData(4,
							new ResultSetRowColumnData(false, aInternationalTransfersReceived.getConcept().toString()));
				} else {
					row.addRowData(4, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getLastBeneficiary() != null) {
					row.addRowData(5, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getLastBeneficiary().toString()));
				} else {
					row.addRowData(5, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getVerificationDate() != null) {
					row.addRowData(6, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getVerificationDate().toString()));
				} else {
					row.addRowData(6, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getAgreement() != null) {
					row.addRowData(7, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getAgreement().toString()));
				} else {
					row.addRowData(7, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getAccountType() != null) {
					row.addRowData(8, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getAccountType().toString()));
				} else {
					row.addRowData(8, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getAccount() != null) {
					row.addRowData(9,
							new ResultSetRowColumnData(false, aInternationalTransfersReceived.getAccount().toString()));
				} else {
					row.addRowData(9, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getPreliminaryAgreementDate() != null) {
					row.addRowData(10, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getPreliminaryAgreementDate().toString()));
				} else {
					row.addRowData(10, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getOfficial() != null) {
					row.addRowData(11, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getOfficial().toString()));
				} else {
					row.addRowData(11, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getOfficialDescription() != null) {
					row.addRowData(12, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getOfficialDescription().toString()));
				} else {
					row.addRowData(12, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getCategory() != null) {
					row.addRowData(13, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getCategory().toString()));
				} else {
					row.addRowData(13, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getCategoryDescription() != null) {
					row.addRowData(14, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getCategoryDescription().toString()));
				} else {
					row.addRowData(14, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getOperationNumber() != null) {
					row.addRowData(15, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getOperationNumber().toString()));
				} else {
					row.addRowData(15, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getOperationDescription() != null) {
					row.addRowData(16, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getOperationDescription().toString()));
				} else {
					row.addRowData(16, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getOfficeBelongNumber() != null) {
					row.addRowData(17, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getOfficeBelongNumber().toString()));
				} else {
					row.addRowData(17, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getOfficeName() != null) {
					row.addRowData(18, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getOfficeName().toString()));
				} else {
					row.addRowData(18, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getOriginatorNumber() != null) {
					row.addRowData(19, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getOriginatorNumber().toString()));
				} else {
					row.addRowData(19, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getOriginatorName() != null) {
					row.addRowData(20, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getOriginatorName().toString()));
				} else {
					row.addRowData(20, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getIdNumber() != null) {
					row.addRowData(21, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getIdNumber().toString()));
				} else {
					row.addRowData(21, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getOriginatorAddressNumber() != null) {
					row.addRowData(22, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getOriginatorAddressNumber().toString()));
				} else {
					row.addRowData(22, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getOriginatorAddress() != null) {
					row.addRowData(23, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getOriginatorAddress().toString()));
				} else {
					row.addRowData(23, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getBeneficiaryName() != null) {
					row.addRowData(24, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getBeneficiaryName().toString()));
				} else {
					row.addRowData(24, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getBeneficiaryAddress() != null) {
					row.addRowData(25, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getBeneficiaryAddress().toString()));
				} else {
					row.addRowData(25, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getContinent() != null) {
					row.addRowData(26, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getContinent().toString()));
				} else {
					row.addRowData(26, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getBeneficiaryContinent() != null) {
					row.addRowData(27, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getBeneficiaryContinent().toString()));
				} else {
					row.addRowData(27, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getBeneficiaryCountryNumber() != null) {
					row.addRowData(28, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getBeneficiaryCountryNumber().toString()));
				} else {
					row.addRowData(28, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getBeneficiaryCountryName() != null) {
					row.addRowData(29, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getBeneficiaryCountryName().toString()));
				} else {
					row.addRowData(29, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getBeneficiaryCityNumber() != null) {
					row.addRowData(30, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getBeneficiaryCityNumber().toString()));
				} else {
					row.addRowData(30, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getBeneficiaryCityName() != null) {
					row.addRowData(31, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getBeneficiaryCityName().toString()));
				} else {
					row.addRowData(31, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getAmount() != null) {
					row.addRowData(32,
							new ResultSetRowColumnData(false, aInternationalTransfersReceived.getAmount().toString()));
				} else {
					row.addRowData(32, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getCurrencyNumber() != null) {
					row.addRowData(33, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getCurrencyNumber().toString()));
				} else {
					row.addRowData(33, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getCurrencyName() != null) {
					row.addRowData(34, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getCurrencyName().toString()));
				} else {
					row.addRowData(34, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getPriority() != null) {
					row.addRowData(35, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getPriority().toString()));
				} else {
					row.addRowData(35, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getMessage() != null) {
					row.addRowData(36,
							new ResultSetRowColumnData(false, aInternationalTransfersReceived.getMessage().toString()));
				} else {
					row.addRowData(36, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getMessageName() != null) {
					row.addRowData(37, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getMessageName().toString()));
				} else {
					row.addRowData(37, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getIssueDate() != null) {
					row.addRowData(38, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getIssueDate().toString()));
				} else {
					row.addRowData(38, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getTerm() != null) {
					row.addRowData(39,
							new ResultSetRowColumnData(false, aInternationalTransfersReceived.getTerm().toString()));
				} else {
					row.addRowData(39, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getDueDate() != null) {
					row.addRowData(40,
							new ResultSetRowColumnData(false, aInternationalTransfersReceived.getDueDate().toString()));
				} else {
					row.addRowData(40, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getNotification() != null) {
					row.addRowData(41, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getNotification().toString()));
				} else {
					row.addRowData(41, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getOnBehalfOfAddress() != null) {
					row.addRowData(42, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getOnBehalfOfAddress().toString()));
				} else {
					row.addRowData(42, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getLastShipmentDate() != null) {
					row.addRowData(43, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getLastShipmentDate().toString()));
				} else {
					row.addRowData(43, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getIssueReference() != null) {
					row.addRowData(44, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getIssueReference().toString()));
				} else {
					row.addRowData(44, new ResultSetRowColumnData(false, " "));
				}
				if (aInternationalTransfersReceived.getTransactionDate() != null) {
					row.addRowData(45, new ResultSetRowColumnData(false,
							aInternationalTransfersReceived.getTransactionDate().toString()));
				} else {
					row.addRowData(45, new ResultSetRowColumnData(false, " "));
				}

				data.addRow(row);
			}

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
			wProcedureResponse.addResponseBlock(resultBlock);
		} else {
			wProcedureResponse = Utils.returnException(aInternationalTransfersReceivedResponse.getMessages());
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
					Utils.returnException(aInternationalTransfersReceivedResponse.getMessages()));
		}

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final -->" + wProcedureResponse.getProcedureResponseAsString());
		return wProcedureResponse;
	}

	private boolean IsValidCheckbookResponse(InternationalTransfersReceived aInternationalTransfersReceived) {
		String messageError = null;
		messageError = aInternationalTransfersReceived.getOfficeNumber() == null ? " - bankOperation can't be null"
				: "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);
		return true;
	}

}
