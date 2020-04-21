package com.cobiscorp.ecobis.orchestration.core.ib.loans;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
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
import com.cobiscorp.cobisv.commons.context.CobisSession;
import com.cobiscorp.cobisv.commons.context.Context;
import com.cobiscorp.cobisv.commons.context.ContextManager;
import com.cobiscorp.ecobis.ib.application.dtos.NegociationLoanAccounRequest;
import com.cobiscorp.ecobis.ib.application.dtos.NegociationLoanAccounResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.NegotiationLoanAccount;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceNegociationLoanAccount;

@Component(name = "NegociationLoanAccountQuery", immediate = false)
@Service(value = { ICoreServiceNegociationLoanAccount.class, ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "NegociationLoanAccountQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "NegociationLoanAccountQuery") })
public class NegociationLoanAccountQuery extends SPJavaOrchestrationBase implements ICoreServiceNegociationLoanAccount {

	private static final String COBIS_CONTEXT = "COBIS";
	private static final String CLASS_NAME = " >-----> ";
	private static ILogger logger = LogFactory.getLogger(NegociationLoanAccountQuery.class);

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {

	}

	/**
	 * M&eacute;todo GetNegociationLoanAccount En este m&eacute;todo Obtiene la
	 * negociaci&oacute;n del pr&eacute;stamo, enviamos un objeto de tipo
	 * LoanStatementRequest y obtenemos de respuesta un objeto de tipo
	 * LoanStatementResponse, para m&aacute;s detalle de los objetos, revisar
	 * las siguientes referencias:
	 * 
	 * @see NegociationLoanAccounRequest
	 * @see NegociationLoanAccounResponse
	 */

	@Override
	public NegociationLoanAccounResponse GetNegociationLoanAccount(
			NegociationLoanAccounRequest aNegociationLoanAccounRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Iniciando Servicio executeLoanACCOUNT");
		Context context = ContextManager.getContext();
		CobisSession session = (CobisSession) context.getSession();
		IProcedureRequest request = new ProcedureRequestAS();
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, request.readValueParam("@t_trn"));
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1800180");// request.readValueParam("@t_trn"));
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setSpName("cobis..sp_bv_operating_data");
		request.addInputParam("@i_banco", ICTSTypes.SQLVARCHAR, aNegociationLoanAccounRequest.getLoanNumber());
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aNegociationLoanAccounRequest.getLoanNumber());
		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, aNegociationLoanAccounRequest.getOperation());
		request.addInputParam("@i_mon", ICTSTypes.SQLINT4, aNegociationLoanAccounRequest.getCurrencyId().toString());
		request.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, aNegociationLoanAccounRequest.getUserName());
		request.addInputParam("@i_cuota_completa", ICTSTypes.SQLVARCHAR,
				aNegociationLoanAccounRequest.getCompleteQuota());
		request.addInputParam("@i_tipo_cobro", ICTSTypes.SQLVARCHAR, aNegociationLoanAccounRequest.getChargeRate());
		request.addInputParam("@i_tipo_reduccion", ICTSTypes.SQLVARCHAR,
				aNegociationLoanAccounRequest.getReductionRate());
		request.addInputParam("@i_efecto_pago", ICTSTypes.SQLVARCHAR, aNegociationLoanAccounRequest.getPaymentEffect());
		request.addInputParam("@i_tipo_prioridad", ICTSTypes.SQLVARCHAR,
				aNegociationLoanAccounRequest.getPriorityRate());
		request.addInputParam("@i_aceptar_anticipos", ICTSTypes.SQLVARCHAR,
				aNegociationLoanAccounRequest.getAdvancePayment());
		request.addInputParam("@s_ofi", ICTSTypes.SQLINT2, session.getOffice());
		request.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, session.getUser());
		if (request.readValueParam("@i_operacion").equals("D")) {
			request.addInputParam("@i_transaction_id", ICTSTypes.SQLINT4,
					aNegociationLoanAccounRequest.getTransactionId().toString());
		}

		String OPERACION = aNegociationLoanAccounRequest.getOperation();

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
		return transformToNegociationLoanAccounResponse(response, OPERACION);
	}

	private NegociationLoanAccounResponse transformToNegociationLoanAccounResponse(IProcedureResponse response,
			String OPERACION) {
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "RESPONSE TO TRANSFORM: " + response.getProcedureResponseAsString());

		NegotiationLoanAccount aNegotiationLoanAccount = null;
		aNegotiationLoanAccount = new NegotiationLoanAccount();
		List<NegotiationLoanAccount> aNegotiationLoanAccountList = new ArrayList<NegotiationLoanAccount>();

		NegociationLoanAccounResponse aNegociationLoanAccounResponse = new NegociationLoanAccounResponse();
		if (response.getReturnCode() != 0) {
			aNegociationLoanAccounResponse.setMessages(Utils.returnArrayMessage(response));
			aNegociationLoanAccounResponse.setReturnCode(response.getReturnCode());
			return aNegociationLoanAccounResponse;
		}

		if (!OPERACION.equals("M")) {

			IResultSetRow[] rowsNegotiationLoanAccount = response.getResultSet(1).getData().getRowsAsArray();

			for (IResultSetRow iResultSetRow : rowsNegotiationLoanAccount) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

				// if(aNegociationLoanAccounResponse.getReturnCode()==0){
				if (OPERACION.equals("N")) {

					if (columns[0].getValue() != null) {
						aNegociationLoanAccounResponse.setAccount(columns[0].getValue());
					}
				}

				else if (OPERACION.equals("P")) {

					if (columns[0].getValue() != null) {
						aNegotiationLoanAccount.setChargeRate(columns[0].getValue());
					}
					if (columns[1].getValue() != null) {
						aNegotiationLoanAccount.setAdvancePayment(columns[1].getValue());
					}
					if (columns[2].getValue() != null) {
						aNegotiationLoanAccount.setReductionRate(columns[2].getValue());
					}
					if (columns[3].getValue() != null) {
						aNegotiationLoanAccount.setAplicationRate(columns[3].getValue());
					}
					if (columns[4].getValue() != null) {
						aNegotiationLoanAccount.setCompleteQuota(columns[4].getValue());
					}
					if (columns[6].getValue() != null) {
						aNegotiationLoanAccount.setPriorityRate(columns[6].getValue());
					}
					if (columns[7].getValue() != null) {
						aNegotiationLoanAccount.setPaymentEffect(columns[7].getValue());
					}
					if (columns[8].getValue() != null) {
						aNegotiationLoanAccount.setCurrencyId(Integer.parseInt(columns[8].getValue()));
					}
					if (columns[9].getValue() != null) {
						aNegotiationLoanAccount.setCurrencyName(columns[9].getValue());
					}
					aNegotiationLoanAccountList.add(aNegotiationLoanAccount);
				} else if (OPERACION.equals("D")) {
					if (response.getReturnCode() == 0) {
						if (columns[0].getValue() != null) {
							aNegotiationLoanAccount.setQuota(new BigDecimal(columns[0].getValue()));
						}
						if (columns[1].getValue() != null) {
							aNegotiationLoanAccount.setConcept(columns[1].getValue());
						}
						if (columns[2].getValue() != null) {
							aNegotiationLoanAccount.setState(columns[2].getValue());
						}
						if (columns[3].getValue() != null) {
							aNegotiationLoanAccount.setAmount(new BigDecimal(columns[3].getValue()));
						}
						if (columns[4].getValue() != null) {
							aNegotiationLoanAccount.setAmountMN(new BigDecimal(columns[4].getValue()));
						}
						if (columns[5].getValue() != null) {
							aNegotiationLoanAccount.setCurrencyId(Integer.parseInt(columns[5].getValue().toString()));
						}
						aNegotiationLoanAccountList.add(aNegotiationLoanAccount);
					}
				}
			}
		}
		aNegociationLoanAccounResponse.setReturnCode(response.getReturnCode());

		if (OPERACION.equals("P") || OPERACION.equals("D")) {
			aNegociationLoanAccounResponse.setNegotiationDateList(aNegotiationLoanAccountList);
		}
		return aNegociationLoanAccounResponse;
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		return null;
	}
}
