package com.cobiscorp.ecobis.orchestration.core.ib.timedeposits;

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
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositsMovementsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositsMovementsResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Bank;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Secuential;
import com.cobiscorp.ecobis.ib.orchestration.dtos.TimeDepositsMovements;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceTimeDeposits;

/**
 * 
 * @author jchonillo
 *
 */
@Component(name = "TimeDepositMovementQueryOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "TimeDepositMovementQueryOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "TimeDepositMovementQueryOrchestrationCore") })
public class TimeDepositMovementQueryOrchestrationCore extends QueryBaseTemplate {

	@Reference(referenceInterface = ICoreServiceTimeDeposits.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceTimeDeposits coreServiceTimeDeposit;
	ILogger logger = this.getLogger();

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreService(ICoreServiceTimeDeposits service) {
		coreServiceTimeDeposit = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreService(ICoreServiceTimeDeposits service) {
		coreServiceTimeDeposit = null;
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

		TimeDepositsMovementsResponse aMovementsResponse = null;
		TimeDepositsMovementsRequest aMovementsRequest = transformTimeDepositsMovementsRequest(request.clone());
		if (logger.isDebugEnabled())
			logger.logDebug("OBJ: " + aMovementsRequest.getBank().getId());

		try {
			if (logger.isDebugEnabled())
				logger.logDebug("executeQuery");
			messageError = "get: ERROR EXECUTING SERVICE";
			if (logger.isDebugEnabled())
				logger.logDebug("request movements: " + request);
			messageLog = "getTimeDepositMovement: "
					+ aMovementsRequest.getTimeDepositsMovements().getProduct().getProductNumber();
			queryName = "getTimeDepositMovements";
			aMovementsRequest.setOriginalRequest(request);
			aMovementsResponse = coreServiceTimeDeposit.getTimeDepositMovements(aMovementsRequest);
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

		return transformProcedureResponse(aMovementsResponse);
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isDebugEnabled())
			logger.logDebug("executeJavaOrchestration TimeDepositMovements");
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreServiceTimeDeposit", coreServiceTimeDeposit);
		Utils.validateComponentInstance(mapInterfaces);
		try {
			return executeStepsQueryBase(anOrginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		} catch (CTSInfrastructureException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		}
	}

	/******************
	 * Transformación de ProcedureRequest a TimeDepositsMovementsRequest
	 ********************/

	private TimeDepositsMovementsRequest transformTimeDepositsMovementsRequest(IProcedureRequest aRequest) {

		TimeDepositsMovementsRequest timeDeposMoveRequest = new TimeDepositsMovementsRequest();
		TimeDepositsMovements timeDeposMove = new TimeDepositsMovements();
		Bank bank = new Bank();
		Secuential secuential = new Secuential();
		Product wProduct = new Product();
		Currency wCurrency = new Currency();

		if (logger.isDebugEnabled())
			logger.logDebug(
					"Procedure TimeDepositsMovementsRequest to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;
		messageError = aRequest.readValueParam("@i_num_banco") == null ? " - @i_num_banco can't be null" : "";
		messageError += aRequest.readValueParam("@i_secuencia") == null ? " - @i_secuencia can't be null" : "";
		messageError += aRequest.readValueParam("@i_formato_fecha") == null ? " - @i_formato_fecha can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		wCurrency.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon")));

		wProduct.setCurrency(wCurrency);
		wProduct.setProductNumber(aRequest.readValueParam("@i_cta"));
		timeDeposMove.setProduct(wProduct);

		// bank.setId(Integer.parseInt(aRequest.readValueParam("@i_cta")));
		secuential.setSecuential(aRequest.readValueParam("@i_secuencia"));

		timeDeposMoveRequest.setBank(bank);
		timeDeposMoveRequest.setSecuential(secuential);
		timeDeposMoveRequest.setProductId(Integer.parseInt(aRequest.readValueParam("@i_prod")));
		timeDeposMoveRequest.setDateFormat(Integer.parseInt(aRequest.readValueParam("@i_formato_fecha")));

		timeDeposMoveRequest.setTimeDepositsMovements(timeDeposMove);

		return timeDeposMoveRequest;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/

	private IProcedureResponse transformProcedureResponse(
			TimeDepositsMovementsResponse aTimeDepositMovemenstsResponse) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		// Agregar Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("date", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("transactionName", ICTSTypes.SYBVARCHAR, 100));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("payFormat", ICTSTypes.SYBVARCHAR, 50));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("currency", ICTSTypes.SYBVARCHAR, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("internationalAmount", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("amount", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("status", ICTSTypes.SYBVARCHAR, 3));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("sequence", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("account", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("beneficiary", ICTSTypes.SYBVARCHAR, 100));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("valueDate", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("transactionNumber", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("subsequence", ICTSTypes.SYBVARCHAR, 10));

		if (aTimeDepositMovemenstsResponse.getReturnCode() == 0) {

			for (TimeDepositsMovements aTimeDepositsMovements : aTimeDepositMovemenstsResponse.getDepositsMovements()) {

				if (!IsValidTimeDepositsMovementsResponse(aTimeDepositsMovements))
					return null;

				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, aTimeDepositsMovements.getDate().toString()));
				row.addRowData(2, new ResultSetRowColumnData(false, aTimeDepositsMovements.getTransactionName()));
				row.addRowData(3, new ResultSetRowColumnData(false, aTimeDepositsMovements.getPayFormat()));
				row.addRowData(4, new ResultSetRowColumnData(false, aTimeDepositsMovements.getCurrency()));
				row.addRowData(5,
						new ResultSetRowColumnData(false, aTimeDepositsMovements.getInternationalAmount().toString()));
				row.addRowData(6, new ResultSetRowColumnData(false, aTimeDepositsMovements.getAmount().toString()));
				row.addRowData(7, new ResultSetRowColumnData(false, aTimeDepositsMovements.getStatus()));
				row.addRowData(8, new ResultSetRowColumnData(false, aTimeDepositsMovements.getSequence().toString()));
				row.addRowData(9, new ResultSetRowColumnData(false, aTimeDepositsMovements.getAccount()));
				row.addRowData(10, new ResultSetRowColumnData(false, aTimeDepositsMovements.getBeneficiary()));
				row.addRowData(11, new ResultSetRowColumnData(false, aTimeDepositsMovements.getValueDate()));
				row.addRowData(12,
						new ResultSetRowColumnData(false, aTimeDepositsMovements.getTransactionNumber().toString()));
				row.addRowData(13,
						new ResultSetRowColumnData(false, aTimeDepositsMovements.getSubsequence().toString()));

				data.addRow(row);
			}

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

			wProcedureResponse.addResponseBlock(resultBlock);

		} else {
			wProcedureResponse = Utils.returnException(aTimeDepositMovemenstsResponse.getMessages());
		}
		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final -->" + wProcedureResponse.getProcedureResponseAsString());
		return wProcedureResponse;

	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean IsValidTimeDepositsMovementsResponse(TimeDepositsMovements timeDepositsMovements) {
		String messageError = null;
		String msgErr = null;

		messageError = timeDepositsMovements.getDate() == null ? " Date can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsMovements.getTransactionName() == null ? " TransactionName can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsMovements.getPayFormat() == null ? " PayFormat can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsMovements.getCurrency() == null ? " Currency can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsMovements.getInternationalAmount() == null ? " InternationalAmount can't be null,"
				: "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsMovements.getAmount() == null ? " Amount can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsMovements.getStatus() == null ? " Status can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsMovements.getSequence() == null ? " Sequence can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsMovements.getAccount() == null ? " Account can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsMovements.getBeneficiary() == null ? " Beneficiary can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsMovements.getValueDate() == null ? " ValueDate can't be null" : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsMovements.getTransactionNumber() == null ? " TransactionNumber can't be null,"
				: "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsMovements.getSubsequence() == null ? " Subsequence can't be null" : "OK";
		msgErr = msgErr + messageError;

		if (!messageError.equals("OK"))
			// throw new IllegalArgumentException(messageError);
			throw new IllegalArgumentException(msgErr);
		return true;
	}

}
