package com.cobiscorp.ecobis.ib.orchestration.core.ib.transferACH.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.Properties;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
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
import com.cobiscorp.ecobis.ib.application.dtos.TransferACHRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TransferACHresponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.TransferACH;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceTransferACH;

@Component(name = "TransfersACHQuery", immediate = false)
@Service(value = { ICoreServiceTransferACH.class })
@Properties(value = { @Property(name = "service.description", value = "TransfersACHQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "TransfersACHQuery") })
public class TransfersACHQuery extends SPJavaOrchestrationBase implements ICoreServiceTransferACH {
	private static final String COBIS_CONTEXT = "COBIS";

	private static ILogger logger = LogFactory.getLogger(TransfersACHQuery.class);
	private static final int FECHA = 0;
	private static final int CODIGO_TIPO_PRODUCTO = 1;
	private static final int TIPO_PRODUCTO = 2;
	private static final int NUMERO_PRODUCTO = 3;
	private static final int BANCO_DESTINO = 4;
	private static final int MONTO = 5;
	private static final int PROPOSITO = 6;
	private static final int HORA = 7;
	private static final int SECUENCIAL = 8;
	private static final int BENEFICIARIO = 9;
	private static final int CI_BENEFICIARIO = 10;
	private static final int TELEFONO_BENEFICIARIO = 11;
	private static final int ORDEN = 12;
	private static final int CURRENCY_ID = 13;

	public TransfersACHQuery() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public TransferACHresponse GetTransferACH(TransferACHRequest aTransferACHRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: GetTransferACH");
		}

		IProcedureRequest request = new ProcedureRequestAS();
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, request.readValueParam("@t_trn"));
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);

		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, request.readValueParam("@t_trn"));
		request.setSpName("cobis..sp_consulta_transferencias");
		request.addInputParam("@i_cuenta", ICTSTypes.SQLVARCHAR, aTransferACHRequest.getProductNumber());
		request.addInputParam("@i_fecha_ini", ICTSTypes.SQLVARCHAR, aTransferACHRequest.getInitialDate());
		request.addInputParam("@i_fecha_fin", ICTSTypes.SQLVARCHAR, aTransferACHRequest.getFinalDate());
		request.addInputParam("@i_formato_fecha", ICTSTypes.SQLINT2, aTransferACHRequest.getDateFormatId().toString());
		request.readValueParam("@i_tipo").equals("H");
		request.addInputParam("@i_secuencial", ICTSTypes.SQLINT2, aTransferACHRequest.getSecuential().toString());

		IProcedureResponse pResponse = executeCoreBanking(request);
		TransferACHresponse transferACHresponse = transformToTransferACHResponse(pResponse);

		return transferACHresponse;
	}

	private TransferACHresponse transformToTransferACHResponse(IProcedureResponse pResponse) {

		TransferACHresponse transferACHresp = new TransferACHresponse();
		List<TransferACH> atransferACHLIST = new ArrayList<TransferACH>();
		TransferACH atransferACH = null;

		if (logger.isDebugEnabled()) {
			logger.logDebug("ProcedureResponse: " + pResponse.getProcedureResponseAsString());
		}

		IResultSetRow[] rowsTransferACH = pResponse.getResultSet(1).getData().getRowsAsArray();
		for (IResultSetRow iResultSetRow : rowsTransferACH) {
			IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
			atransferACH = new TransferACH();
			atransferACH.setPaymentDate(columns[FECHA].getValue());
			atransferACH.setProductType(Integer.parseInt(columns[CODIGO_TIPO_PRODUCTO].getValue()));
			atransferACH.setAccountAlias(columns[TIPO_PRODUCTO].getValue());
			atransferACH.setCreditAccount(columns[NUMERO_PRODUCTO].getValue());
			atransferACH.setEntityName(columns[BANCO_DESTINO].getValue());
			atransferACH.setAmount(Double.parseDouble(columns[MONTO].getValue()));
			atransferACH.setNotes(columns[PROPOSITO].getValue());
			atransferACH.setCreationDate(columns[HORA].getValue());
			atransferACH.setSecuential(Integer.parseInt(columns[SECUENCIAL].getValue()));
			atransferACH.setBeneficiaryName(columns[BENEFICIARIO].getValue());
			atransferACH.setBeneficiaryId(Integer.parseInt(columns[CI_BENEFICIARIO].getValue()));
			atransferACH.setBeneficiaryPhone(columns[TELEFONO_BENEFICIARIO].getValue());
			atransferACH.setOrder(columns[ORDEN].getValue());
			atransferACH.setCurrencyId(Integer.parseInt(columns[CURRENCY_ID].getValue()));

		}

		transferACHresp.setTransferACH(atransferACHLIST);

		return transferACHresp;

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

}
