package com.cobiscorp.ecobis.orchestration.core.ib.based.billing;

import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
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
import com.cobiscorp.ecobis.ib.application.dtos.BasedBillingRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BasedBillingResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.BasedBilling;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreBasedBillingQuery;

@Component(name = "BasedBillingQueryOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "BasedBillingQueryOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "BasedBillingQueryOrchestrationCore") })
public class BasedBillingQueryOrchestrationCore extends SPJavaOrchestrationBase {

	private static ILogger logger = LogFactory.getLogger(BasedBillingQueryOrchestrationCore.class);
	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	private static final String CLASS_NAME = "--->";

	@Reference(referenceInterface = ICoreBasedBillingQuery.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreBasedBillingQuery", unbind = "unbindCoreBasedBillingQuery")
	private ICoreBasedBillingQuery coreBasedBillingQuery;

	public void bindCoreBasedBillingQuery(ICoreBasedBillingQuery service) {
		coreBasedBillingQuery = service;
	}

	public void unbindCoreBasedBillingQuery(ICoreBasedBillingQuery service) {
		coreBasedBillingQuery = null;
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
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "executeJavaOrchestration");
		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);
		if (logger.isDebugEnabled())
			logger.logDebug("executeJavaOrchestration");
		BasedBillingResponse baseBillingResponse = null;
		try {
			baseBillingResponse = coreBasedBillingQuery
					.getBasedBilling(transformToBasedBillingRequest(anOriginalRequest));

		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.logDebug("Error al obtener los datos getBasedBilling");
				logger.logDebug("*********  Error en " + e.getMessage(), e);
			}
			Message message = new Message();
			message.setCode("-1");
			message.setDescription("Error al ejecutar el metodo getBasedBilling");
			baseBillingResponse.setReturnCode(-1);
			baseBillingResponse.setSuccess(false);
			baseBillingResponse.setMessage(message);
		}
		;

		return transformToProcedureResponse(baseBillingResponse, aBagSPJavaOrchestration);
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

	public BasedBillingRequest transformToBasedBillingRequest(IProcedureRequest aRequest) {
		if (logger.isDebugEnabled())
			logger.logDebug("<<<Procedure Request to Transform->>>" + aRequest.getProcedureRequestAsString());

		BasedBillingRequest basedBillingRequestConverted = new BasedBillingRequest();
		basedBillingRequestConverted.setDateFormat(Integer.parseInt(aRequest.readValueParam("@i_formato_fecha")));
		basedBillingRequestConverted.setCriteria(aRequest.readValueParam("@i_busqueda1"));
		basedBillingRequestConverted.setIdentification(aRequest.readValueParam("@i_identificacion"));
		basedBillingRequestConverted.setName(aRequest.readValueParam("@i_nombre"));
		basedBillingRequestConverted.setContractId(Integer.parseInt(aRequest.readValueParam("@i_convenio")));
		basedBillingRequestConverted.setOperation(aRequest.readValueParam("@i_operacion"));

		if (logger.isInfoEnabled())
			logger.logInfo(" Finalizando metodo transformToBasedBillingRequest : " + basedBillingRequestConverted);
		return basedBillingRequestConverted;
	};

	public IProcedureResponse transformToProcedureResponse(BasedBillingResponse aResponse,
			Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureResponse procedureResponse = new ProcedureResponseAS();
		if (aResponse != null && aResponse.getSuccess()) {

			if (logger.isDebugEnabled())
				logger.logDebug("<<<Transform Procedure Response BasedBillingResponse>>>");

			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("Identificacion", ICTSTypes.SQLVARCHAR, 32));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Nombre Deudor", ICTSTypes.SQLVARCHAR, 128));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Referencia 1", ICTSTypes.SQLVARCHAR, 32));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Referencia 2", ICTSTypes.SQLVARCHAR, 32));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Referencia 3", ICTSTypes.SQLVARCHAR, 32));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Valor", ICTSTypes.SQLDECIMAL, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Fecha Pago", ICTSTypes.SQLVARCHAR, 30));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Secuencial", ICTSTypes.SQLINT4, 11));

			for (BasedBilling basedBilling : aResponse.getBasedBillingInformation()) {
				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, basedBilling.getIdentification()));
				row.addRowData(2, new ResultSetRowColumnData(false, basedBilling.getDebtorName()));
				row.addRowData(3, new ResultSetRowColumnData(false, basedBilling.getReference1()));
				row.addRowData(4, new ResultSetRowColumnData(false, basedBilling.getReference2()));
				row.addRowData(5, new ResultSetRowColumnData(false, basedBilling.getReference3()));
				row.addRowData(6, new ResultSetRowColumnData(false, basedBilling.getAmount().toString()));
				row.addRowData(7, new ResultSetRowColumnData(false, basedBilling.getPaymentDay()));
				row.addRowData(8, new ResultSetRowColumnData(false, basedBilling.getSequential().toString()));
				data.addRow(row);
			}
			IResultSetBlock resultBlock1 = new ResultSetBlock(metaData, data);
			procedureResponse.addResponseBlock(resultBlock1);
		} else {
			procedureResponse = Utils.returnException(aResponse.getMessages());
			procedureResponse.setReturnCode(aResponse.getReturnCode());

		}

		return procedureResponse;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

}
