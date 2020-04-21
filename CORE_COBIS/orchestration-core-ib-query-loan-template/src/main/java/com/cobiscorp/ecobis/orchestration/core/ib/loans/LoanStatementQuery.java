package com.cobiscorp.ecobis.orchestration.core.ib.loans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

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
import com.cobiscorp.ecobis.ib.application.dtos.LoanStatementRequest;
import com.cobiscorp.ecobis.ib.application.dtos.LoanStatementResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.LoanStatement;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceLoanStatement;

@Component(name = "LoanStatementQuery", immediate = false)
@Service(value = { ICoreServiceLoanStatement.class })
@Properties(value = { @Property(name = "service.description", value = "LoanStatementQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "LoanStatementQuery") })

public class LoanStatementQuery extends SPJavaOrchestrationBase implements ICoreServiceLoanStatement {
	private static final String CLASS_NAME = " >-----> ";
	private static ILogger logger = LogFactory.getLogger(LoanStatementQuery.class);

	/**
	 * M&eacute;todo getLoanStatement En este m&eacute;todo obtenemos la
	 * Consulta Estado del Prestamo, enviamos un objeto de tipo
	 * LoanStatementRequest y obtenemos de respuesta un objeto de tipo
	 * LoanStatementResponse, para m&aacute;s detalle de los objetos, revisar
	 * las siguientes referencias:
	 * 
	 * @see LoanStatementRequest
	 * @see LoanStatementResponse
	 */
	@Override
	public LoanStatementResponse getLoanStatement(LoanStatementRequest aLoanStatementRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Iniciando Servicio getLoanStatement");
		IProcedureRequest request = new ProcedureRequestAS();
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, "7802");
		request.setSpName("cob_cartera..sp_imp_estado_cuenta");
		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "D");
		request.addInputParam("@i_banco", ICTSTypes.SQLVARCHAR,
				aLoanStatementRequest.getProductNumber().getProductNumber());
		request.addInputParam("@i_formato_fecha", ICTSTypes.SQLINT4,
				aLoanStatementRequest.getDateFormatId().toString());
		request.addInputParam("@i_siguiente", ICTSTypes.SQLINT4, aLoanStatementRequest.getSequential().toString());
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Obtencion del servicio");
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Enviando solicitud al Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Request a enviar: " + request.getProcedureRequestAsString());
		IProcedureResponse response = executeCoreBanking(request);
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Data devuelta del Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Response obtenido: " + response.getProcedureResponseAsString());
		return transformToLoanStatementResponse(response);
	}

	private LoanStatementResponse transformToLoanStatementResponse(IProcedureResponse aProcedureResponse) {
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "RESPONSE TO TRANSFORM: " + aProcedureResponse.getProcedureResponseAsString());
		LoanStatementResponse LoanStatementResp = new LoanStatementResponse();
		LoanStatement aLoanStatement = null;
		List<LoanStatement> aloanStatementCollection = new ArrayList<LoanStatement>();
		IResultSetRow[] rowsLoanStatement = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();
		for (IResultSetRow iResultSetRow : rowsLoanStatement) {
			IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
			aLoanStatement = new LoanStatement();
			aLoanStatement.setPaymentDate(columns[1].getValue());
			aLoanStatement.setNormalInterest(Double.parseDouble(columns[3].getValue()));
			aLoanStatement.setArrearsInterest(Double.parseDouble(columns[4].getValue()));
			aLoanStatement.setAmount(Double.parseDouble(columns[7].getValue()));
			aLoanStatement.setSequential(Integer.parseInt(columns[17].getValue()));
			aLoanStatement.setPaymentType("A");
			aloanStatementCollection.add(aLoanStatement);
		}
		LoanStatementResp.setLoanStatementCollection(aloanStatementCollection);
		return LoanStatementResp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.commons.components.ICOBISComponent#loadConfiguration(
	 * com.cobiscorp.cobis.commons.configuration.IConfigurationReader)
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
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
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
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
}