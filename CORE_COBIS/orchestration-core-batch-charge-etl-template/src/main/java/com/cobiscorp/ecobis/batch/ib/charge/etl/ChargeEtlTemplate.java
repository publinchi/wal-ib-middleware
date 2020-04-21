package com.cobiscorp.ecobis.batch.ib.charge.etl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
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
import com.cobiscorp.ecobis.ib.application.dtos.BatchChargeEtlRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BatchChargeEtlResponse;
import com.cobiscorp.ecobis.ib.application.dtos.BatchEtlTotalResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.BatchEtlTotal;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Entity;
import com.cobiscorp.ecobis.ib.orchestration.dtos.EntityService;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Log;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceBatchChargeEtl;

@Component(name = "ChargeEtlTemplate", immediate = false)
@Service(value = { ICoreServiceBatchChargeEtl.class })
@Properties(value = { @Property(name = "service.description", value = "ChargeEtlTemplate"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ChargeEtlTemplate") })
public class ChargeEtlTemplate extends SPJavaOrchestrationBase implements ICoreServiceBatchChargeEtl {

	private static ILogger logger = LogFactory.getLogger(ChargeEtlTemplate.class);
	private static final String CLASS_NAME = " >-----> ";
	protected static final String COBIS_CONTEXT = "COBIS";
	protected static final String AHORRO = "4";
	protected static final String CTE = "3";

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public BatchChargeEtlResponse chargeEtl(BatchChargeEtlRequest wBatchChargeEtlrequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + "INICIANDO SERVICIO");
			logger.logInfo(CLASS_NAME + "RESPUESTA CORE GENERADA");
		}
		IProcedureRequest request = new ProcedureRequestAS();
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);

		request.setSpName("cob_bvirtual..sp_bv_bv18etlbv_rs");

		if (wBatchChargeEtlrequest.getDateProcess() != null)
			request.addInputParam("@i_fecha_proceso", ICTSTypes.SQLVARCHAR,
					wBatchChargeEtlrequest.getDateProcess().toString());

		request.addInputParam("@i_registros", ICTSTypes.SQLINT4, wBatchChargeEtlrequest.getTotal().toString());
		request.addOutputParam("@o_secuencial", ICTSTypes.SQLINT4, wBatchChargeEtlrequest.getNext().toString());

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Enviando solicitud al Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Request a enviar: " + request.getProcedureRequestAsString());

		IProcedureResponse response = executeCoreBanking(request);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta del Core:" + response.getProcedureResponseAsString());

		BatchChargeEtlResponse wChargeEtlResponse = transformToEtlResponse(response);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "TERMINANDO SERVICIO");

		return wChargeEtlResponse;
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	private BatchChargeEtlResponse transformToEtlResponse(IProcedureResponse aProcedureResponse) {

		BatchChargeEtlResponse wBatchChargeEtlResponse = new BatchChargeEtlResponse();
		Entity oEntity = null;
		EntityService oEntityService = null;
		Log oLog = null;

		if (aProcedureResponse == null) {
			if (logger.isInfoEnabled())
				logger.logInfo("transformToEtlResponse --> Response null");
			return null;
		}

		if (aProcedureResponse.getReturnCode() == 0) {
			List<EntityService> EntityServiceCollection = new ArrayList<EntityService>();
			IResultSetRow[] rowsEntityService = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();
			for (IResultSetRow iResultSetRow : rowsEntityService) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				oEntityService = new EntityService();

				oEntityService.setCompany(Integer.parseInt(columns[0].getValue()));
				if (columns[1].getValue() != null)
					oEntityService.setEntity(Integer.parseInt(columns[1].getValue()));
				if (columns[2].getValue() != null)
					oEntityService.setService(Integer.parseInt(columns[2].getValue()));
				if (columns[3].getValue() != null)
					oEntityService.setState(columns[3].getValue());
				if (columns[4].getValue() != null)
					oEntityService.setCreator(columns[4].getValue());
				if (columns[5].getValue() != null)
					oEntityService.setDate(columns[5].getValue());
				if (columns[6].getValue() != null)
					oEntityService.setOffice(Integer.parseInt(columns[6].getValue()));
				if (columns[7].getValue() != null)
					oEntityService.setCategory(Integer.parseInt(columns[7].getValue()));
				if (columns[8].getValue() != null)
					oEntityService.setAux(Integer.parseInt(columns[8].getValue()));

				EntityServiceCollection.add(oEntityService);
			}
			wBatchChargeEtlResponse.setEntityServiceCollection(EntityServiceCollection);

			List<Entity> EntityCollection = new ArrayList<Entity>();
			IResultSetRow[] rowsEntity = aProcedureResponse.getResultSet(2).getData().getRowsAsArray();
			for (IResultSetRow iResultSetRow2 : rowsEntity) {
				IResultSetRowColumnData[] columns = iResultSetRow2.getColumnsAsArray();
				oEntity = new Entity();

				oEntity.setCompany(Integer.parseInt(columns[0].getValue()));
				if (columns[1].getValue() != null)
					oEntity.setEnte(Integer.parseInt(columns[1].getValue()));
				if (columns[2].getValue() != null)
					oEntity.setCodCustomer(Integer.parseInt(columns[2].getValue()));

				EntityCollection.add(oEntity);
			}
			wBatchChargeEtlResponse.setEntityCollection(EntityCollection);

			List<Log> LogCollection = new ArrayList<Log>();
			IResultSetRow[] rowsLog = aProcedureResponse.getResultSet(3).getData().getRowsAsArray();
			for (IResultSetRow iResultSetRow3 : rowsLog) {
				IResultSetRowColumnData[] columns = iResultSetRow3.getColumnsAsArray();
				oLog = new Log();

				oLog.setCompany(Integer.parseInt(columns[0].getValue()));
				if (columns[1].getValue() != null)
					oLog.setTransaction(Integer.parseInt(columns[1].getValue()));
				if (columns[2].getValue() != null)
					oLog.setEntity(Integer.parseInt(columns[2].getValue()));
				if (columns[3].getValue() != null)
					oLog.setService(Integer.parseInt(columns[3].getValue()));

				if (columns[4].getValue() != null)
					oLog.setStatus(columns[4].getValue());

				if (columns[5].getValue() != null)
					oLog.setDate(columns[5].getValue());

				if (columns[6].getValue() != null)
					oLog.setSequency(Integer.parseInt(columns[6].getValue()));

				if (columns[7].getValue() != null)
					oLog.setHour(columns[7].getValue());

				if (columns[8].getValue() != null)
					oLog.setProduct(Integer.parseInt(columns[8].getValue()));

				if (columns[9].getValue() != null)
					oLog.setMoney(Integer.parseInt(columns[9].getValue()));

				if (columns[10].getValue() != null)
					oLog.setAccount(columns[10].getValue());

				if (columns[11].getValue() != null)
					oLog.setFee(new BigDecimal(columns[11].getValue()));

				if (columns[12].getValue() != null)
					oLog.setOffice(Integer.parseInt(columns[12].getValue()));

				if (columns[13].getValue() != null)
					oLog.setAux(Integer.parseInt(columns[13].getValue()));

				if (columns[14].getValue() != null)
					oLog.setOriginatorFunds(columns[14].getValue());

				if (columns[15].getValue() != null)
					oLog.setReceiverFunds(columns[15].getValue());

				LogCollection.add(oLog);
			}
			wBatchChargeEtlResponse.setLogColletion(LogCollection);

			if (aProcedureResponse.readValueParam("@o_secuencial") != null) {
				wBatchChargeEtlResponse
						.setMaxRecord(Integer.parseInt(aProcedureResponse.readValueParam("@o_secuencial")));
			}

		} else {
			// wBatchBalanceRefreshResponse.setMessages(Utils.returnArrayMessage(aProcedureResponse));
			wBatchChargeEtlResponse.setSuccess(false);
		}
		wBatchChargeEtlResponse.setReturnCode(aProcedureResponse.getReturnCode());

		return wBatchChargeEtlResponse;

	}

	@Override
	public BatchEtlTotalResponse generateTotalEtl(BatchChargeEtlRequest aBatchChargeEtlRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + "INICIANDO SERVICIO");
			logger.logInfo(CLASS_NAME + "RESPUESTA CORE GENERADA");
		}
		IProcedureRequest request = new ProcedureRequestAS();
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);

		request.setSpName("cob_bvirtual..sp_pr_cuadre_etl_rs");

		if (aBatchChargeEtlRequest.getDateProcess() != null)
			request.addInputParam("@i_fecha_proceso", ICTSTypes.SQLVARCHAR,
					aBatchChargeEtlRequest.getDateProcess().toString());

		IProcedureResponse response = executeCoreBanking(request);

		return transformPREtlTotal(response);
	}

	private BatchEtlTotalResponse transformPREtlTotal(IProcedureResponse aProcedureResponse) {
		BatchEtlTotalResponse wBatchChargeEtlResponse = new BatchEtlTotalResponse();

		if (aProcedureResponse == null) {
			if (logger.isInfoEnabled())
				logger.logInfo("transformPREtlTotal --> Response null");
			return null;
		}

		if (aProcedureResponse.getReturnCode() == 0) {
			List<BatchEtlTotal> batchEtlTotalCollection = new ArrayList<BatchEtlTotal>();
			IResultSetRow[] rowsEntityService = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();
			for (IResultSetRow iResultSetRow : rowsEntityService) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				BatchEtlTotal batchEtlTotal = new BatchEtlTotal();

				if (columns[0].getValue() != null)
					batchEtlTotal.setCompany(Integer.parseInt(columns[0].getValue()));

				if (columns[1].getValue() != null)
					batchEtlTotal.setProcessDate(columns[1].getValue());
				if (columns[2].getValue() != null)
					batchEtlTotal.setModule(Integer.parseInt(columns[2].getValue()));

				if (columns[3].getValue() != null)
					batchEtlTotal.setTable(columns[3].getValue());

				if (columns[4].getValue() != null)
					batchEtlTotal.setCriteria(columns[4].getValue());

				if (columns[5].getValue() != null)
					batchEtlTotal.setCriteriaValue(columns[5].getValue());

				if (columns[6].getValue() != null)
					batchEtlTotal.setNumberOfRecords(Integer.parseInt(columns[6].getValue()));

				if (columns[7].getValue() != null)
					batchEtlTotal.setValue(new BigDecimal(columns[7].getValue()));

				batchEtlTotalCollection.add(batchEtlTotal);
			}
			wBatchChargeEtlResponse.setEtlTotalCollection(batchEtlTotalCollection);
		}
		return wBatchChargeEtlResponse;

	}

}
