package com.cobiscorp.ecobis.orchestration.core.ib.query.movements.accounts;

import java.util.HashMap;
import java.util.List;
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
import com.cobiscorp.ecobis.ib.application.dtos.DetailsMovementsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.DetailsMovementsResponse;
import com.cobiscorp.ecobis.ib.application.dtos.EnquiryRequest;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountStatement;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceDetailsMovementsQuery;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceQuery;

@Component(name = "DetailsMovementsAccountsQueryOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "DetailsMovementsAccountsQueryOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "DetailsMovementsAccountsQueryOrchestrationCore") })

public class DetailsMovementsAccountsQueryOrchestrationCore extends QueryBaseTemplate {
	ILogger logger = getLogger();
	private static final String CLASS_NAME = "DetailsMovementsAccountsQueryOrchestrationCore--->";
	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	static final String OPERATION1_REQUEST = "OPERATION1_REQUEST";
	static final String OPERATION1_RESPONSE = "OPERATION1_RESPONSE";
	public static final int PRODUCT_CTACTE = 3;
	public static final int PRODUCT_CTAAHO = 4;
	public static int PRODUCT_TYPE;

	@Reference(referenceInterface = ICoreServiceDetailsMovementsQuery.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	private ICoreServiceDetailsMovementsQuery coreServiceMovementsQuery;

	protected void bindCoreService(ICoreServiceDetailsMovementsQuery service) {
		this.coreServiceMovementsQuery = service;
	}

	protected void unbindCoreService(ICoreServiceDetailsMovementsQuery service) {
		this.coreServiceMovementsQuery = null;
	}

	private IProcedureResponse pResponse = null;

	public void loadConfiguration(IConfigurationReader arg0) {
		if (logger.isInfoEnabled())
			this.logger.logInfo(
					"Component-Name: " + getClass().getSimpleName() + ", Component-Version: $$project.version$$");
	}

	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (this.logger.isInfoEnabled()) {
			this.logger.logInfo("DetailsMovementsAccountsQueryOrchestrationCore--->executeJavaOrchestration");
		}
		Map<String, Object> mapInterfaces = new HashMap();
		mapInterfaces.put("coreServiceMovementsQuery", this.coreServiceMovementsQuery);
		// Utils.validateComponentInstance(mapInterfaces);
		try {
			return executeStepsQueryBase(anOriginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOriginalRequest, e);
		} catch (CTSInfrastructureException e) {
			return Utils.returnExceptionService(anOriginalRequest, e);
		}
	}

	private DetailsMovementsRequest transformAccountStatementRequest(IProcedureRequest aRequest) {
		if (this.logger.isInfoEnabled()) {
			this.logger.logInfo("Inicia transformAccountStatementRequest");
		}
		AccountStatement accountStatement = new AccountStatement();
		EnquiryRequest enquiryRequest = new EnquiryRequest();
		DetailsMovementsRequest detailsMovementsRequest = new DetailsMovementsRequest();

		enquiryRequest.setProductId(Integer.valueOf(Integer.parseInt(aRequest.readValueParam("@i_prod"))));
		enquiryRequest.setCurrencyId(Integer.valueOf(Integer.parseInt(aRequest.readValueParam("@i_mon"))));
		enquiryRequest.setProductNumber(aRequest.readValueParam("@i_cta"));

		enquiryRequest.setTransactionDate(aRequest.readValueParam("@i_fecha_trn"));
		enquiryRequest.setUserName(aRequest.readValueParam("@i_login"));
		if (aRequest.readValueParam("@i_prod").trim().equals(4))
			enquiryRequest
					.setDateFormatId(Integer.valueOf(Integer.parseInt(aRequest.readValueParam("@i_formato_fecha"))));

		accountStatement.setSequential(Integer.valueOf(Integer.parseInt(aRequest.readValueParam("@i_ssn"))));
		accountStatement.setAlternateCode(Integer.valueOf(Integer.parseInt(aRequest.readValueParam("@i_alt"))));
		accountStatement.setOperationType(Integer.valueOf(Integer.parseInt(aRequest.readValueParam("@i_trn"))));

		detailsMovementsRequest.setwAccountStatement(accountStatement);
		detailsMovementsRequest.setwEnquiryRequest(enquiryRequest);
		if (this.logger.isInfoEnabled()) {
			this.logger.logInfo("sale transformAccountStatementRequest");
		}
		return detailsMovementsRequest;
	}

	protected IProcedureResponse executeQuery(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		String messageError = null;
		String messageLog = null;
		String queryName = null;
		if (this.logger.isInfoEnabled())
			this.logger.logInfo("Inicia executeQuery");

		this.pResponse = ((IProcedureResponse) aBagSPJavaOrchestration.get("RESPONSE_VALIDATE_LOCAL"));
		aBagSPJavaOrchestration.put("ORIGINAL_REQUEST", request);
		DetailsMovementsRequest detailsMovementsRequest = transformAccountStatementRequest(request.clone());
		DetailsMovementsResponse detailsMovementsResponse = null;
		try {

			messageError = "getMovementsDetail: ERROR EXECUTING SERVICE";
			messageLog = "getMovementsDetail " + detailsMovementsRequest.getwAccountStatement().getAccount();
			queryName = "getMovementsDetail";
			String wProd = request.readValueParam("@i_prod");
			PRODUCT_TYPE = Integer.parseInt(wProd.trim());
			switch (PRODUCT_TYPE) {
			case 4:
				if (logger.isInfoEnabled())
					this.logger.logInfo("entra getMovementsDetailSavingAccount");
				detailsMovementsResponse = this.coreServiceMovementsQuery
						.getMovementsDetailSavingAccount(detailsMovementsRequest);
				break;
			case 3:
				if (logger.isInfoEnabled())
					this.logger.logInfo("entra getMovementsDetailCheckingAccount");
				detailsMovementsResponse = this.coreServiceMovementsQuery
						.getMovementsDetailCheckingAccount(detailsMovementsRequest);
			}
		} catch (Exception e) {
			if (this.logger.isInfoEnabled()) {
				this.logger.logInfo("*********  Error en " + e.getMessage(), e);
			}
			IProcedureResponse wProcedureRespFinal = initProcedureResponse(request);
			ErrorBlock eb = new ErrorBlock(-1, messageError);
			wProcedureRespFinal.addResponseBlock(eb);
			wProcedureRespFinal.addFieldInHeader("executionResult", 'S', "1");
			wProcedureRespFinal.setReturnCode(-1);
			return wProcedureRespFinal;
		}
		aBagSPJavaOrchestration.put("DETAIL_RESPONSE", detailsMovementsResponse);
		if (logger.isInfoEnabled())
			this.logger.logInfo("fin executeQuery");
		aBagSPJavaOrchestration.put(LOG_MESSAGE, messageLog);
		aBagSPJavaOrchestration.put(QUERY_NAME, queryName);
		return processResponse(request, aBagSPJavaOrchestration);
	}

	public IProcedureResponse processResponse(IProcedureRequest anOriginalProcedureReq,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			this.logger.logInfo("Inicia processResponse");
		IProcedureResponse response = new ProcedureResponseAS();
		// response.addFieldInHeader("executionResult", 'S', "1");
		IResultSetData data = new ResultSetData();
		DetailsMovementsResponse aDetailsMovementsResponse = (DetailsMovementsResponse) aBagSPJavaOrchestration
				.get("DETAIL_RESPONSE");
		if (!IsValidDetailsMovementsResponse(aDetailsMovementsResponse)) {
			return null;
		}
		if (aDetailsMovementsResponse.getReturnCode().intValue() == 0) {
			IResultSetHeader metaData = new ResultSetHeader();
			metaData.addColumnMetaData(new ResultSetHeaderColumn("account", 60, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("transactionDate", 60, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("hour", 60, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("description", 60, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("concept", 60, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("cause", 60, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("amount", 60, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("documentNumber", 60, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("typeDC", 52, 6));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("office", 52, 6));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("ownChecksBalance", 56, 6));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("localChecksBalance", 52, 4));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("internationalChecksBalance", 39, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("totalChecksBalance", 56, 4));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("causeId", 60, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("clabeInterbank", 60, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("rastreo", 60, 20));
			
			metaData.addColumnMetaData(new ResultSetHeaderColumn("montoTran", 39, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("beneficiario", 39, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("cuentaOrig", 39, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("cuentaDest", 39, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("comisionTran", 39, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("ivaTran", 39, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("mensajeTran", 39, 20));
			
			metaData.addColumnMetaData(new ResultSetHeaderColumn("originAccountProp", 39, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("currencySymbol", 39, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("referenceNumber", 39, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("destinationAccountType", 39, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("originAccountType", 39, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("bank", 39, 20));
			
			List<AccountStatement> accountStatementsCollection = aDetailsMovementsResponse
					.getAccountStatementsCollection();
			if (logger.isInfoEnabled())
				this.logger.logInfo("Inicia processResponse" + accountStatementsCollection.get(0).getAccount());
			if (logger.isInfoEnabled())
				this.logger.logInfo("Inicia processResponse" + accountStatementsCollection.get(0).getStringDate());
			if (logger.isInfoEnabled())
				this.logger.logInfo("Inicia processResponse" + accountStatementsCollection.get(0).getHour());
			if (logger.isInfoEnabled())
				this.logger.logInfo("Inicia processResponse" + accountStatementsCollection.get(0).getAmount());
			IResultSetRow row = new ResultSetRow();
			row.addRowData(1, new ResultSetRowColumnData(false, accountStatementsCollection.get(0).getAccount()));
			row.addRowData(2, new ResultSetRowColumnData(false, accountStatementsCollection.get(0).getStringDate()));
			row.addRowData(3, new ResultSetRowColumnData(false, accountStatementsCollection.get(0).getHour()));
			row.addRowData(4, new ResultSetRowColumnData(false, accountStatementsCollection.get(0).getDescription()));
			row.addRowData(5, new ResultSetRowColumnData(false, accountStatementsCollection.get(0).getConcept()));
			row.addRowData(6, new ResultSetRowColumnData(false, accountStatementsCollection.get(0).getCause()));
			row.addRowData(7,
					new ResultSetRowColumnData(false, accountStatementsCollection.get(0).getAmount().toString()));
			row.addRowData(8,
					new ResultSetRowColumnData(false, accountStatementsCollection.get(0).getDocumentNumber()));
			row.addRowData(9, new ResultSetRowColumnData(false, accountStatementsCollection.get(0).getTypeDC()));
			row.addRowData(10, new ResultSetRowColumnData(false, accountStatementsCollection.get(0).getOffice()));
			row.addRowData(11,
					new ResultSetRowColumnData(false, accountStatementsCollection.get(0).getOwnChecksBalance()));
			row.addRowData(12,
					new ResultSetRowColumnData(false, accountStatementsCollection.get(0).getLocalChecksBalance()));
			row.addRowData(13, new ResultSetRowColumnData(false,
					accountStatementsCollection.get(0).getInternationalCheckBookBalance()));
			row.addRowData(14,
					new ResultSetRowColumnData(false, accountStatementsCollection.get(0).getTotalChecksBalance()));
			row.addRowData(15, new ResultSetRowColumnData(false, accountStatementsCollection.get(0).getCauseId()));
			row.addRowData(16, new ResultSetRowColumnData(false, ""));
			row.addRowData(17, new ResultSetRowColumnData(false, accountStatementsCollection.get(0).getRastreo()));
			
			row.addRowData(18, new ResultSetRowColumnData(false, accountStatementsCollection.get(0).getMontoTran()));
			row.addRowData(19, new ResultSetRowColumnData(false, accountStatementsCollection.get(0).getBeneficiario()));
			row.addRowData(20, new ResultSetRowColumnData(false, accountStatementsCollection.get(0).getCuentaDest()));
			row.addRowData(21, new ResultSetRowColumnData(false, accountStatementsCollection.get(0).getCuentaOrig()));
			row.addRowData(22, new ResultSetRowColumnData(false, accountStatementsCollection.get(0).getComisionTran()));
			row.addRowData(23, new ResultSetRowColumnData(false, accountStatementsCollection.get(0).getIvaTran()));
			row.addRowData(24, new ResultSetRowColumnData(false, accountStatementsCollection.get(0).getMensajeTran()));
			
			row.addRowData(25, new ResultSetRowColumnData(false, accountStatementsCollection.get(0).getOriginAccountProp()));
			row.addRowData(26, new ResultSetRowColumnData(false, accountStatementsCollection.get(0).getCurrencySymbol()));
			row.addRowData(27, new ResultSetRowColumnData(false, accountStatementsCollection.get(0).getReferenceNumber()));
			row.addRowData(28, new ResultSetRowColumnData(false, accountStatementsCollection.get(0).getDestinationAccountType()));
			row.addRowData(29, new ResultSetRowColumnData(false, accountStatementsCollection.get(0).getOriginAccountType()));
			row.addRowData(30, new ResultSetRowColumnData(false, accountStatementsCollection.get(0).getBank()));
			
			
			data.addRow(row);

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
			response.addResponseBlock(resultBlock);
		} else {
			aBagSPJavaOrchestration.put("RESPONSE_TRANSACTION",
					Utils.returnException(aDetailsMovementsResponse.getMessages()));
			response = Utils.returnException(aDetailsMovementsResponse.getMessages());
		}
		response.setReturnCode(aDetailsMovementsResponse.getReturnCode().intValue());
		if (logger.isInfoEnabled())
			this.logger.logInfo("RESPUESTA SECUENCIAL" + response.getProcedureResponseAsString());

		if (logger.isInfoEnabled())
			this.logger.logInfo("fin processResponse");
		return response;
	}

	private boolean IsValidDetailsMovementsResponse(DetailsMovementsResponse aDetailsMovementsResponse) {
		String messageError = null;
		messageError = aDetailsMovementsResponse.getAccountStatementsCollection() == null
				? "getAccountStatementsCollection can't be null" : "OK";
		if (!messageError.equals("OK")) {
			throw new IllegalArgumentException(messageError);
		}
		return true;
	}
}
