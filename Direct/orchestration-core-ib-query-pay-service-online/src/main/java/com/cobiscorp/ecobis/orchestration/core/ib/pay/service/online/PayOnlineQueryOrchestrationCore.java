package com.cobiscorp.ecobis.orchestration.core.ib.pay.service.online;

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
import com.cobiscorp.ecobis.ib.application.dtos.OnlineServiceRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OnlineServiceResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.OnlinePaymentDetail;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreOnlineServiceInvoicingQuery;

@Component(name = "PayOnlineQueryOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = ""),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "PayOnlineQueryOrchestrationCore") })
public class PayOnlineQueryOrchestrationCore extends SPJavaOrchestrationBase {

	private static ILogger logger = LogFactory.getLogger(PayOnlineQueryOrchestrationCore.class);
	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	// private static final String CLASS_NAME = "--->";

	@Reference(referenceInterface = ICoreOnlineServiceInvoicingQuery.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreOnlineServiceInvoicingQuery", unbind = "unbindCoreOnlineServiceInvoicingQuery")
	private ICoreOnlineServiceInvoicingQuery coreOnlineServiceInvoicingQuery;

	public void bindCoreOnlineServiceInvoicingQuery(ICoreOnlineServiceInvoicingQuery service) {
		coreOnlineServiceInvoicingQuery = service;
	}

	public void unbindCoreOnlineServiceInvoicingQuery(ICoreOnlineServiceInvoicingQuery service) {
		coreOnlineServiceInvoicingQuery = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		OnlineServiceResponse onlineServiceResponse = null;
		if (logger.isDebugEnabled())
			logger.logDebug("executeJavaOrchestration");
		try {
			onlineServiceResponse = coreOnlineServiceInvoicingQuery
					.getOnlineService(transformToOnlineServiceRequest(anOriginalRequest));
		} catch (Exception ex) {
			if (logger.isDebugEnabled())
				logger.logDebug("Error al ejecutar getOnlineService : " + ex.toString());
		}
		return transformToProcedureResponse(onlineServiceResponse);
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public OnlineServiceRequest transformToOnlineServiceRequest(IProcedureRequest request) {
		OnlineServiceRequest onlineServiceRequest = new OnlineServiceRequest();
		return onlineServiceRequest;
	}

	public IProcedureResponse transformToProcedureResponse(OnlineServiceResponse response) {
		IProcedureResponse procedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("<<<Transform Procedure Response OnlineServiceResponse>>>");

		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		IResultSetHeader metaData1 = new ResultSetHeader();
		IResultSetData data1 = new ResultSetData();

		metaData1.addColumnMetaData(new ResultSetHeaderColumn("Colector", ICTSTypes.SQLINT4, 32));
		metaData1.addColumnMetaData(new ResultSetHeaderColumn("Oficina", ICTSTypes.SQLINT4, 32));
		metaData1.addColumnMetaData(new ResultSetHeaderColumn("Numero", ICTSTypes.SQLVARCHAR, 32));
		metaData1.addColumnMetaData(new ResultSetHeaderColumn("Identificacion", ICTSTypes.SQLVARCHAR, 32));
		metaData1.addColumnMetaData(new ResultSetHeaderColumn("Nombre", ICTSTypes.SQLVARCHAR, 32));
		metaData1.addColumnMetaData(new ResultSetHeaderColumn("Num Servicios", ICTSTypes.SQLINT4, 32));
		metaData1.addColumnMetaData(new ResultSetHeaderColumn("Estado", ICTSTypes.SQLINT4, 32));
		metaData1.addColumnMetaData(new ResultSetHeaderColumn("Respuesta", ICTSTypes.SQLVARCHAR, 32));
		for (OnlinePaymentDetail onlinePaymentDetail : response.getOnlinePaymentDetailList()) {
			IResultSetRow row = new ResultSetRow();
			row.addRowData(1, new ResultSetRowColumnData(false, onlinePaymentDetail.getCollectorId().toString()));
			row.addRowData(2, new ResultSetRowColumnData(false, onlinePaymentDetail.getOffice().toString()));
			row.addRowData(3, new ResultSetRowColumnData(false, onlinePaymentDetail.getNumber()));
			row.addRowData(4, new ResultSetRowColumnData(false, onlinePaymentDetail.getIdentification()));
			row.addRowData(5, new ResultSetRowColumnData(false, onlinePaymentDetail.getName()));
			row.addRowData(6, new ResultSetRowColumnData(false, onlinePaymentDetail.getNumberService().toString()));
			row.addRowData(7, new ResultSetRowColumnData(false, onlinePaymentDetail.getState().toString()));
			row.addRowData(8, new ResultSetRowColumnData(false, onlinePaymentDetail.getResponse()));
			data1.addRow(row);
		}
		IResultSetBlock resultBlock2 = new ResultSetBlock(metaData1, data1);
		procedureResponse.addResponseBlock(resultBlock2);

		metaData.addColumnMetaData(new ResultSetHeaderColumn("Convenio", ICTSTypes.SQLVARCHAR, 32));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("Numero", ICTSTypes.SQLVARCHAR, 32));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("NRecibos", ICTSTypes.SQLINT4, 32));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("TotalPago", ICTSTypes.SQLDECIMAL, 32));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("Periodo", ICTSTypes.SQLVARCHAR, 32));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("FechaVence", ICTSTypes.SQLVARCHAR, 32));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("NumeroRecibo", ICTSTypes.SQLVARCHAR, 32));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("TotalPago", ICTSTypes.SQLDECIMAL, 32));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("Self", ICTSTypes.SQLVARCHAR, 32));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("LlavePago", ICTSTypes.SQLVARCHAR, 32));

		for (OnlinePaymentDetail onlinePaymentDetail : response.getOnlinePaymentDetailList()) {
			IResultSetRow row = new ResultSetRow();
			row.addRowData(1, new ResultSetRowColumnData(false, onlinePaymentDetail.getContractId()));
			row.addRowData(2, new ResultSetRowColumnData(false, onlinePaymentDetail.getNumber()));
			row.addRowData(3, new ResultSetRowColumnData(false, onlinePaymentDetail.getReceipts().toString()));
			row.addRowData(4, new ResultSetRowColumnData(false, onlinePaymentDetail.getTotalPay().toString()));
			row.addRowData(5, new ResultSetRowColumnData(false, onlinePaymentDetail.getPeriod()));
			row.addRowData(6, new ResultSetRowColumnData(false, onlinePaymentDetail.getExpirationDate()));
			row.addRowData(7, new ResultSetRowColumnData(false, onlinePaymentDetail.getReceiptNumber()));
			row.addRowData(8, new ResultSetRowColumnData(false, onlinePaymentDetail.getTotalPayment().toString()));
			row.addRowData(9, new ResultSetRowColumnData(false, onlinePaymentDetail.getSelf()));
			row.addRowData(10, new ResultSetRowColumnData(false, onlinePaymentDetail.getThridPartyPaymentKey()));
			data.addRow(row);
		}
		IResultSetBlock resultBlock1 = new ResultSetBlock(metaData, data);
		procedureResponse.addResponseBlock(resultBlock1);
		procedureResponse.setReturnCode(0);
		return procedureResponse;
	}

}
