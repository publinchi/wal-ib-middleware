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
import com.cobiscorp.ecobis.ib.application.dtos.TransferInternationalDetailsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TransferInternationalDetailsResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.TransferInternationalDetails;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServicelTransfersInternationaDetails;

@Component(name = "TransferInternationalDetailsOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "TransferInternationalDetailsOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "TransferInternationalDetailsOrchestrationCore") })

public class TransferInternationalDetailsOrchestrationCore extends QueryBaseTemplate {

	@Reference(referenceInterface = ICoreServicelTransfersInternationaDetails.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServicelTransfersInternationaDetails coreService;
	ILogger logger = LogFactory.getLogger(TransferInternationalDetailsOrchestrationCore.class);

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreService(ICoreServicelTransfersInternationaDetails service) {
		coreService = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreService(ICoreServicelTransfersInternationaDetails service) {
		coreService = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	protected IProcedureResponse validateLocalExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
		// TODO Auto-generated method stub
		IProcedureResponse response = new ProcedureResponseAS();
		response.setReturnCode(0);
		return response;
	}

	@Override
	protected IProcedureResponse executeQuery(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		String messageError = null;
		String messageLog = null;
		String queryName = null;

		TransferInternationalDetailsResponse aTransferInternationalDetailsResponse = new TransferInternationalDetailsResponse();
		TransferInternationalDetailsRequest aTransferInternationalDetailsRequest = transformTransferInternationalDetailsRequest(
				request.clone());

		try {
			messageError = "getOperation: ERROR EXECUTING SERVICE";
			messageLog = "getInitialCheck " + aTransferInternationalDetailsRequest.getCriteria();
			queryName = "getOperation";

			aTransferInternationalDetailsRequest.setOriginalRequest(request);
			aTransferInternationalDetailsResponse = coreService
					.searchTransferInternationalDetails(aTransferInternationalDetailsRequest);
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

		return transformProcedureResponse(aTransferInternationalDetailsResponse, aBagSPJavaOrchestration);
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

	private TransferInternationalDetailsRequest transformTransferInternationalDetailsRequest(
			IProcedureRequest aRequest) {
		TransferInternationalDetailsRequest aTransferInternationalDetailsRequest = new TransferInternationalDetailsRequest();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;

		messageError = aRequest.readValueParam("@i_group") == null ? " - @i_group can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		if (aRequest.readValueParam("@i_group") != null)
			aTransferInternationalDetailsRequest.setCriteria2(aRequest.readValueParam("@i_group"));
		if (aRequest.readValueParam("@i_secuencial") != null)
			aTransferInternationalDetailsRequest.setSequential(aRequest.readValueParam("@i_secuencial"));
		if (aRequest.readValueParam("@i_fecha_ini") != null)
			aTransferInternationalDetailsRequest.setInitialDate(aRequest.readValueParam("@i_fecha_ini"));
		if (aRequest.readValueParam("@i_fecha_fin") != null)
			aTransferInternationalDetailsRequest.setFinalDate(aRequest.readValueParam("@i_fecha_fin"));
		if (aRequest.readValueParam("@i_formato_fecha") != null)
			aTransferInternationalDetailsRequest.setMode(aRequest.readValueParam("@i_formato_fecha"));
		if (aRequest.readValueParam("@i_account") != null)
			aTransferInternationalDetailsRequest.setProductNumber(aRequest.readValueParam("@i_account"));
		if (aRequest.readValueParam("@i_siguiente") != null)
			aTransferInternationalDetailsRequest.setNumberOfResults(aRequest.readValueParam("@i_siguiente"));
		if (aRequest.readValueParam("@i_cta") != null)
			aTransferInternationalDetailsRequest.setProductNumber(aRequest.readValueParam("@i_cta"));
		if (aRequest.readValueParam("@i_prod") != null)
			aTransferInternationalDetailsRequest.setProductId(aRequest.readValueParam("@i_prod"));
		if (aRequest.readValueParam("@i_login") != null)
			aTransferInternationalDetailsRequest.setLogin(aRequest.readValueParam("@i_login"));
		if (aRequest.readValueParam("@i_mon") != null)
			aTransferInternationalDetailsRequest.setCurrencyId(aRequest.readValueParam("@i_mon"));

		if (aRequest.readValueParam("@i_group").equals("NE") || aRequest.readValueParam("@i_group").equals("SE")) {
			aTransferInternationalDetailsRequest.setLastResult(aRequest.readValueParam("@i_operacion"));
		}
		return aTransferInternationalDetailsRequest;
	}

	private IProcedureResponse transformProcedureResponse(
			TransferInternationalDetailsResponse aTransferInternationalDetailsResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		// Agregar Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		if (aTransferInternationalDetailsResponse.getReturnCode() == 0) {
			metaData.addColumnMetaData(new ResultSetHeaderColumn("date_transaction", ICTSTypes.SQLVARCHAR, 11));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("id_referency", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("account_debit", ICTSTypes.SQLVARCHAR, 30));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("account_type", ICTSTypes.SQLVARCHAR, 3));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("account_name", ICTSTypes.SQLVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("ammount", ICTSTypes.SQLMONEY, 24));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("money", ICTSTypes.SQLVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("referency", ICTSTypes.SQLVARCHAR, 255));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("beneficiary_name", ICTSTypes.SQLVARCHAR, 70));
			metaData.addColumnMetaData(
					new ResultSetHeaderColumn("beneficiary_address_complete", ICTSTypes.SQLVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("beneficiary_country", ICTSTypes.SQLVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("beneficiary_city", ICTSTypes.SQLVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("beneficiary_address", ICTSTypes.SQLVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("beneficiary_account", ICTSTypes.SQLVARCHAR, 30));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("bank_beneficiary_country", ICTSTypes.SQLVARCHAR, 70));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("bank_beneficiary_name", ICTSTypes.SQLVARCHAR, 70));
			metaData.addColumnMetaData(
					new ResultSetHeaderColumn("bank_beneficiary_description", ICTSTypes.SQLVARCHAR, 100));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("bank_beneficiary_address", ICTSTypes.SQLVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("bank_beneficiary_swift", ICTSTypes.SQLVARCHAR, 12));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("type_address", ICTSTypes.SQLVARCHAR, 1));
			metaData.addColumnMetaData(
					new ResultSetHeaderColumn("bank_Integerermediary_country", ICTSTypes.SQLVARCHAR, 30));
			metaData.addColumnMetaData(
					new ResultSetHeaderColumn("bank_Integerermediary_name", ICTSTypes.SQLVARCHAR, 30));
			metaData.addColumnMetaData(
					new ResultSetHeaderColumn("bank_Integerermediary_description", ICTSTypes.SQLVARCHAR, 50));
			metaData.addColumnMetaData(
					new ResultSetHeaderColumn("bank_Integerermediary_address", ICTSTypes.SQLVARCHAR, 50));
			metaData.addColumnMetaData(
					new ResultSetHeaderColumn("bank_Integerermediary_swift", ICTSTypes.SQLVARCHAR, 12));
			metaData.addColumnMetaData(
					new ResultSetHeaderColumn("type_address_Integerermediary", ICTSTypes.SQLVARCHAR, 1));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("cost_transaction", ICTSTypes.SQLMONEY, 10));
			metaData.addColumnMetaData(
					new ResultSetHeaderColumn("beneficiary_continent_code", ICTSTypes.SQLVARCHAR, 3));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("beneficiary_continent", ICTSTypes.SQLVARCHAR, 24));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("transaction_code", ICTSTypes.SQLVARCHAR, 30));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("message_type", ICTSTypes.SQLVARCHAR, 4));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("sucursal_code", ICTSTypes.SQLINT4, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("sucursal", ICTSTypes.SQLVARCHAR, 50));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("bank_beneficiary_id", ICTSTypes.SQLINT4, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("beneficiary_country_id", ICTSTypes.SQLINT4, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("beneficiary_city_id", ICTSTypes.SQLINT4, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("payer_city", ICTSTypes.SQLVARCHAR, 50));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("payer_name", ICTSTypes.SQLVARCHAR, 90));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("id", ICTSTypes.SQLINT4, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("ben_country_id", ICTSTypes.SQLINT4, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("ben_city_id", ICTSTypes.SQLINT4, 10));

			if (aTransferInternationalDetailsResponse.getColumns() == 49
					|| aTransferInternationalDetailsResponse.getColumns() == 60) {
				metaData.addColumnMetaData(new ResultSetHeaderColumn("bco_swift_ben", ICTSTypes.SQLVARCHAR, 30));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("bco_swift_Integerer", ICTSTypes.SQLVARCHAR, 30));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("bco_pais_ben", ICTSTypes.SQLINT4, 10));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("bco_pais_Integer", ICTSTypes.SQLINT4, 10));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("bco_ben_id", ICTSTypes.SQLINT4, 10));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("bco_Integer_id", ICTSTypes.SQLINT4, 10));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("bco_dir_ben_id", ICTSTypes.SQLINT4, 10));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("bco_dir_Integer_id", ICTSTypes.SQLINT4, 10));
			}
			if (aTransferInternationalDetailsResponse.getColumns() == 60) {
				metaData.addColumnMetaData(
						new ResultSetHeaderColumn("beneficiaryFirstLastName", ICTSTypes.SQLVARCHAR, 24));
				metaData.addColumnMetaData(
						new ResultSetHeaderColumn("beneficiarySecondLastName", ICTSTypes.SQLVARCHAR, 24));
				metaData.addColumnMetaData(
						new ResultSetHeaderColumn("beneficiaryBusinessName", ICTSTypes.SQLVARCHAR, 24));
				metaData.addColumnMetaData(
						new ResultSetHeaderColumn("beneficiaryTypeDocument", ICTSTypes.SQLVARCHAR, 24));
				metaData.addColumnMetaData(
						new ResultSetHeaderColumn("beneficiaryDocumentNumber", ICTSTypes.SQLVARCHAR, 24));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("currencyIdUSD", ICTSTypes.SQLINT4, 24));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("quote", ICTSTypes.SQLDECIMAL, 24));
				metaData.addColumnMetaData(
						new ResultSetHeaderColumn("beneficiaryTypeDocumentName", ICTSTypes.SQLVARCHAR, 24));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("codeNegotiation", ICTSTypes.SQLINT4, 24));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("beneficiaryEmail1", ICTSTypes.SQLVARCHAR, 24));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("beneficiaryEmail2", ICTSTypes.SQLVARCHAR, 24));
			}
			Integer count = 0;
			for (TransferInternationalDetails aTransferInternationalDetails : aTransferInternationalDetailsResponse
					.getTransferInternationalDetailsCollection()) {
				if (!IsValidCheckbookResponse(aTransferInternationalDetails))
					return null;
				count += 1;
				IResultSetRow row = new ResultSetRow();

				if (aTransferInternationalDetails.getDateTransaction() != null) {
					row.addRowData(1,
							new ResultSetRowColumnData(false, aTransferInternationalDetails.getDateTransaction()));
				} else {
					row.addRowData(1, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getIdReference() != null) {
					row.addRowData(2,
							new ResultSetRowColumnData(false, aTransferInternationalDetails.getIdReference()));
				} else {
					row.addRowData(2, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getAccountDebit() != null) {
					row.addRowData(3,
							new ResultSetRowColumnData(false, aTransferInternationalDetails.getAccountDebit()));
				} else {
					row.addRowData(3, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getAccountType() != null) {
					row.addRowData(4,
							new ResultSetRowColumnData(false, aTransferInternationalDetails.getAccountType()));
				} else {
					row.addRowData(4, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getAccountName() != null) {
					row.addRowData(5,
							new ResultSetRowColumnData(false, aTransferInternationalDetails.getAccountName()));
				} else {
					row.addRowData(5, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getAmmount() != null) {
					row.addRowData(6,
							new ResultSetRowColumnData(false, aTransferInternationalDetails.getAmmount().toString()));
				} else {
					row.addRowData(6, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getMoney() != null) {
					row.addRowData(7, new ResultSetRowColumnData(false, aTransferInternationalDetails.getMoney()));
				} else {
					row.addRowData(7, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getReferency() != null) {
					row.addRowData(8, new ResultSetRowColumnData(false, aTransferInternationalDetails.getReferency()));
				} else {
					row.addRowData(8, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getBeneficiaryName() != null) {
					row.addRowData(9,
							new ResultSetRowColumnData(false, aTransferInternationalDetails.getBeneficiaryName()));
				} else {
					row.addRowData(9, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getBeneficiaryAddressComplete() != null) {
					row.addRowData(10, new ResultSetRowColumnData(false,
							aTransferInternationalDetails.getBeneficiaryAddressComplete()));
				} else {
					row.addRowData(10, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getBeneficiaryCountry() != null) {
					row.addRowData(11,
							new ResultSetRowColumnData(false, aTransferInternationalDetails.getBeneficiaryCountry()));
				} else {
					row.addRowData(11, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getBeneficiaryCity() != null) {
					row.addRowData(12,
							new ResultSetRowColumnData(false, aTransferInternationalDetails.getBeneficiaryCity()));
				} else {
					row.addRowData(12, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getBeneficiaryAddress() != null) {
					row.addRowData(13,
							new ResultSetRowColumnData(false, aTransferInternationalDetails.getBeneficiaryAddress()));
				} else {
					row.addRowData(13, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getBeneficiaryAccount() != null) {
					row.addRowData(14,
							new ResultSetRowColumnData(false, aTransferInternationalDetails.getBeneficiaryAccount()));
				} else {
					row.addRowData(14, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getBankBeneficiaryCountry() != null) {
					row.addRowData(15, new ResultSetRowColumnData(false,
							aTransferInternationalDetails.getBankBeneficiaryCountry()));
				} else {
					row.addRowData(15, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getBankBeneficiaryName() != null) {
					row.addRowData(16,
							new ResultSetRowColumnData(false, aTransferInternationalDetails.getBankBeneficiaryName()));
				} else {
					row.addRowData(16, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getBankBeneficiaryDescription() != null) {
					row.addRowData(17, new ResultSetRowColumnData(false,
							aTransferInternationalDetails.getBankBeneficiaryDescription()));
				} else {
					row.addRowData(17, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getBankBeneficiaryAddress() != null) {
					row.addRowData(18, new ResultSetRowColumnData(false,
							aTransferInternationalDetails.getBankBeneficiaryAddress()));
				} else {
					row.addRowData(18, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getBankBeneficiarySwift() != null) {
					row.addRowData(19,
							new ResultSetRowColumnData(false, aTransferInternationalDetails.getBankBeneficiarySwift()));
				} else {
					row.addRowData(19, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getTypeAddress() != null) {
					row.addRowData(20,
							new ResultSetRowColumnData(false, aTransferInternationalDetails.getTypeAddress()));
				} else {
					row.addRowData(20, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getBankIntermediaryCountry() != null) {
					row.addRowData(21, new ResultSetRowColumnData(false,
							aTransferInternationalDetails.getBankIntermediaryCountry()));
				} else {
					row.addRowData(21, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getBankIntermediaryName() != null) {
					row.addRowData(22,
							new ResultSetRowColumnData(false, aTransferInternationalDetails.getBankIntermediaryName()));
				} else {
					row.addRowData(22, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getBankIntermediaryDescription() != null) {
					row.addRowData(23, new ResultSetRowColumnData(false,
							aTransferInternationalDetails.getBankIntermediaryDescription()));
				} else {
					row.addRowData(23, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getBankIntermediaryAddress() != null) {
					row.addRowData(24, new ResultSetRowColumnData(false,
							aTransferInternationalDetails.getBankIntermediaryAddress()));
				} else {
					row.addRowData(24, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getBankIntermediarySwift() != null) {
					row.addRowData(25, new ResultSetRowColumnData(false,
							aTransferInternationalDetails.getBankIntermediarySwift()));
				} else {
					row.addRowData(25, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getTypeAddressIntermediary() != null) {
					row.addRowData(26, new ResultSetRowColumnData(false,
							aTransferInternationalDetails.getTypeAddressIntermediary()));
				} else {
					row.addRowData(26, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getCostTransaction() != null) {
					row.addRowData(27, new ResultSetRowColumnData(false,
							aTransferInternationalDetails.getCostTransaction().toString()));
				} else {
					row.addRowData(27, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getBeneficiaryContinentCode() != null) {
					row.addRowData(28, new ResultSetRowColumnData(false,
							aTransferInternationalDetails.getBeneficiaryContinentCode()));
				} else {
					row.addRowData(28, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getBeneficiaryContinent() != null) {
					row.addRowData(29,
							new ResultSetRowColumnData(false, aTransferInternationalDetails.getBeneficiaryContinent()));
				} else {
					row.addRowData(29, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getTransactionCode() != null) {
					row.addRowData(30,
							new ResultSetRowColumnData(false, aTransferInternationalDetails.getTransactionCode()));
				} else {
					row.addRowData(30, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getMessageType() != null) {
					row.addRowData(31,
							new ResultSetRowColumnData(false, aTransferInternationalDetails.getMessageType()));
				} else {
					row.addRowData(31, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getSucursalCode() != null) {
					row.addRowData(32, new ResultSetRowColumnData(false,
							aTransferInternationalDetails.getSucursalCode().toString()));
				} else {
					row.addRowData(32, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getSucursal() != null) {
					row.addRowData(33, new ResultSetRowColumnData(false, aTransferInternationalDetails.getSucursal()));
				} else {
					row.addRowData(33, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getBankBeneficiaryId() != null) {
					row.addRowData(34, new ResultSetRowColumnData(false,
							aTransferInternationalDetails.getBankBeneficiaryId().toString()));
				} else {
					row.addRowData(34, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getBeneficiaryCountryId() != null) {
					row.addRowData(35, new ResultSetRowColumnData(false,
							aTransferInternationalDetails.getBeneficiaryCountryId().toString()));
				} else {
					row.addRowData(35, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getBeneficiaryCityId() != null) {
					row.addRowData(36, new ResultSetRowColumnData(false,
							aTransferInternationalDetails.getBeneficiaryCityId().toString()));
				} else {
					row.addRowData(36, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getPayerCity() != null) {
					row.addRowData(37, new ResultSetRowColumnData(false, aTransferInternationalDetails.getPayerCity()));
				} else {
					row.addRowData(37, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getPayerName() != null) {
					row.addRowData(38, new ResultSetRowColumnData(false, aTransferInternationalDetails.getPayerName()));
				} else {
					row.addRowData(38, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getId() != null) {
					row.addRowData(39,
							new ResultSetRowColumnData(false, aTransferInternationalDetails.getId().toString()));
				} else {
					row.addRowData(39, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getBenCountryId() != null) {
					row.addRowData(40, new ResultSetRowColumnData(false,
							aTransferInternationalDetails.getBenCountryId().toString()));
				} else {
					row.addRowData(40, new ResultSetRowColumnData(false, " "));
				}
				if (aTransferInternationalDetails.getBenCityId() != null) {
					row.addRowData(41,
							new ResultSetRowColumnData(false, aTransferInternationalDetails.getBenCityId().toString()));
				} else {
					row.addRowData(41, new ResultSetRowColumnData(false, " "));
				}

				if (count == 49 || count == 60) {
					if (aTransferInternationalDetails.getBcoSwiftBen() != null) {
						row.addRowData(42,
								new ResultSetRowColumnData(false, aTransferInternationalDetails.getBcoSwiftBen()));
					} else {
						row.addRowData(42, new ResultSetRowColumnData(false, " "));
					}
					if (aTransferInternationalDetails.getBcoSwiftInter() != null) {
						row.addRowData(43,
								new ResultSetRowColumnData(false, aTransferInternationalDetails.getBcoSwiftInter()));
					} else {
						row.addRowData(43, new ResultSetRowColumnData(false, " "));
					}
					if (aTransferInternationalDetails.getBcoPaisBen() != null) {
						row.addRowData(44, new ResultSetRowColumnData(false,
								aTransferInternationalDetails.getBcoPaisBen().toString()));
					} else {
						row.addRowData(44, new ResultSetRowColumnData(false, " "));
					}
					if (aTransferInternationalDetails.getBcoPaisInter() != null) {
						row.addRowData(45, new ResultSetRowColumnData(false,
								aTransferInternationalDetails.getBcoPaisInter().toString()));
					} else {
						row.addRowData(45, new ResultSetRowColumnData(false, " "));
					}
					if (aTransferInternationalDetails.getBcoBenId() != null) {
						row.addRowData(46, new ResultSetRowColumnData(false,
								aTransferInternationalDetails.getBcoBenId().toString()));
					} else {
						row.addRowData(46, new ResultSetRowColumnData(false, " "));
					}
					if (aTransferInternationalDetails.getBcoInterId() != null) {
						row.addRowData(47, new ResultSetRowColumnData(false,
								aTransferInternationalDetails.getBcoInterId().toString()));
					} else {
						row.addRowData(47, new ResultSetRowColumnData(false, " "));
					}
					if (aTransferInternationalDetails.getBcoDirBenId() != null) {
						row.addRowData(48, new ResultSetRowColumnData(false,
								aTransferInternationalDetails.getBcoDirBenId().toString()));
					} else {
						row.addRowData(48, new ResultSetRowColumnData(false, " "));
					}
					if (aTransferInternationalDetails.getBcoDirInterId() != null) {
						row.addRowData(49, new ResultSetRowColumnData(false,
								aTransferInternationalDetails.getBcoDirInterId().toString()));
					} else {
						row.addRowData(49, new ResultSetRowColumnData(false, " "));
					}
				}
				if (count == 60) {
					if (aTransferInternationalDetails.getBeneficiaryFirstLastName() != null) {
						row.addRowData(50, new ResultSetRowColumnData(false,
								aTransferInternationalDetails.getBeneficiaryFirstLastName()));
					} else {
						row.addRowData(50, new ResultSetRowColumnData(false, " "));
					}
					if (aTransferInternationalDetails.getBeneficiarySecondLastName() != null) {
						row.addRowData(51, new ResultSetRowColumnData(false,
								aTransferInternationalDetails.getBeneficiarySecondLastName()));
					} else {
						row.addRowData(51, new ResultSetRowColumnData(false, " "));
					}
					if (aTransferInternationalDetails.getBeneficiaryBusinessName() != null) {
						row.addRowData(52, new ResultSetRowColumnData(false,
								aTransferInternationalDetails.getBeneficiaryBusinessName()));
					} else {
						row.addRowData(52, new ResultSetRowColumnData(false, " "));
					}
					if (aTransferInternationalDetails.getBeneficiaryTypeDocument() != null) {
						row.addRowData(53, new ResultSetRowColumnData(false,
								aTransferInternationalDetails.getBeneficiaryTypeDocument()));
					} else {
						row.addRowData(53, new ResultSetRowColumnData(false, " "));
					}
					if (aTransferInternationalDetails.getBeneficiaryDocumentNumber() != null) {
						row.addRowData(54, new ResultSetRowColumnData(false,
								aTransferInternationalDetails.getBeneficiaryDocumentNumber()));
					} else {
						row.addRowData(54, new ResultSetRowColumnData(false, " "));
					}
					if (aTransferInternationalDetails.getCurrencyIdUSD() != null) {
						row.addRowData(55, new ResultSetRowColumnData(false,
								aTransferInternationalDetails.getCurrencyIdUSD().toString()));
					} else {
						row.addRowData(55, new ResultSetRowColumnData(false, " "));
					}
					if (aTransferInternationalDetails.getQuote() != null) {
						row.addRowData(56,
								new ResultSetRowColumnData(false, aTransferInternationalDetails.getQuote().toString()));
					} else {
						row.addRowData(56, new ResultSetRowColumnData(false, " "));
					}
					if (aTransferInternationalDetails.getBeneficiaryTypeDocumentName() != null) {
						row.addRowData(57, new ResultSetRowColumnData(false,
								aTransferInternationalDetails.getBeneficiaryTypeDocumentName()));
					} else {
						row.addRowData(57, new ResultSetRowColumnData(false, " "));
					}
					if (aTransferInternationalDetails.getCodeNegotiation() != null) {
						row.addRowData(58, new ResultSetRowColumnData(false,
								aTransferInternationalDetails.getCodeNegotiation().toString()));
					} else {
						row.addRowData(58, new ResultSetRowColumnData(false, " "));
					}
					if (aTransferInternationalDetails.getBeneficiaryEmail1() != null) {
						row.addRowData(59, new ResultSetRowColumnData(false,
								aTransferInternationalDetails.getBeneficiaryEmail1()));
					} else {
						row.addRowData(59, new ResultSetRowColumnData(false, " "));
					}
					if (aTransferInternationalDetails.getBeneficiaryEmail2() != null) {
						row.addRowData(60, new ResultSetRowColumnData(false,
								aTransferInternationalDetails.getBeneficiaryEmail2()));
					} else {
						row.addRowData(60, new ResultSetRowColumnData(false, " "));
					}
				}
				data.addRow(row);
			}
			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
			wProcedureResponse.addResponseBlock(resultBlock);
		} else {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
					Utils.returnException(aTransferInternationalDetailsResponse.getMessages()));
			wProcedureResponse = Utils.returnException(aTransferInternationalDetailsResponse.getMessages());
		}
		wProcedureResponse.setReturnCode(aTransferInternationalDetailsResponse.getReturnCode());
		if (logger.isDebugEnabled()) {
			logger.logDebug("Procedure Response Final -->" + wProcedureResponse.getProcedureResponseAsString());
		}
		return wProcedureResponse;
	}

	private boolean IsValidCheckbookResponse(TransferInternationalDetails aTransferInternationalDetails) {
		String messageError = null;
		messageError = aTransferInternationalDetails.getDateTransaction() == null ? "  Date_transactioncan't be null"
				: "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);
		return true;
	}

}
