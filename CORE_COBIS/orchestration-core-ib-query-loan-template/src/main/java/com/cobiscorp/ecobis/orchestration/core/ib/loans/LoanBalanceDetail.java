package com.cobiscorp.ecobis.orchestration.core.ib.loans;

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
import com.cobiscorp.ecobis.ib.application.dtos.BalanceDetailPaymentRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BalanceDetailPaymentResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.BalanceDetailPayment;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceLoanBalanceDetail;
//import com.cobiscorp.ecobis.ib.utils.dtos.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;

/**
 * <!-- Autor: Baque H Jorge nombreClase : Se coloca el nombre de la clase java
 * funcion : Es un arreglo de tipo de datos ["String", "List", "int",...]
 * descripcion : Es un arreglo que contiene los nombre de atributos ["altura",
 * "edad", "peso"] descripcionClase: Lleva una breve descripci&oacute;n de la
 * clase numeroAtributos : Numero total de atributos de [1,...n]-->
 * 
 * <script type="text/javascript"> var nombreClase = "LoanBalanceDetail"; var
 * funcion = ["BalanceDetailPaymentResponse
 * getBalanceDetail(BalanceDetailPaymentRequest aLoanDetailRequest)"]; var
 * descripcion = ["Recibe un objeto de tipo BalanceDetailPaymentRequest y
 * retorna otro de tipo BalanceDetailPaymentResponse"]; var descripcionClase =
 * "Template de consulta del detalle del balance del pr&eacute;stamo"; var
 * numeroFunciones = 1; </script>
 * 
 * <table>
 * <tbody>
 * <tr>
 * <th colspan="2" bgcolor="#CCCCFF"><div>Nombre Clase:
 * <script type="text/javascript">document.writeln(nombreClase);</script></th>
 * </tr>
 * <tr>
 * <td colspan="2"><div>Atributos</div></td>
 * </tr>
 * <tr>
 * <td width="auto" bgcolor="#CCCCFF"><div>Funci&oacute;n</div></td>
 * <td width="auto" bgcolor="#CCCCFF"><div>Descripci&oacute;n</div></td>
 * </tr>
 * <tr>
 * <td style="font-family:'Courier New', Courier, monospace; color:#906;"><div
 * align="left"><script type="text/javascript"> for(i=0;i<numeroFunciones;i++){
 * document.write(funcion[i]); document.write("<br />
 * "); }</script></td>
 * <td style=" font-family:'Courier New', Courier, monospace;color:#00F"><div
 * align="left"><script type="text/javascript"> for(i=0;i<numeroFunciones;i++){
 * document.write(descripcion[i]); document.write("<br />
 * "); }</script></td>
 * </tr>
 * 
 * <tr>
 * <td>Descripci&oacute;n Generica:</td>
 * <td><script type=
 * "text/javascript">document.writeln(descripcionClase);</script></td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * @author jbaque
 * @since 13/10/2014
 * @version 1.0.0
 * @see BalanceDetailPaymentRequest
 * @see BalanceDetailPaymentResponse
 */

@Component(name = "LoanBalanceDetail", immediate = false)
@Service(value = { ICoreServiceLoanBalanceDetail.class })
@Properties(value = { @Property(name = "service.description", value = "LoanBalanceDetail"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "LoanBalanceDetail") })
public class LoanBalanceDetail extends SPJavaOrchestrationBase implements ICoreServiceLoanBalanceDetail {
	private static final String COBIS_CONTEXT = "COBIS";
	private static final int COL_ADITIONALDATA = 0;
	private static final int COL_PRODUCTNUMBER = 1;
	private static final int COL_ENTITYNAME = 2;
	private static final int COL_OPERATIONTYPE = 3;
	private static final int COL_INITIALAMOUNT = 4;
	private static final int COL_MONTHLYPAYMENTDAY = 5;
	private static final int COL_STATUS = 6;
	private static final int COL_LASTPAYMENTDATE = 7;
	private static final int COL_EXPIRATIONDATE = 8;
	private static final int COL_EXECUTIVE = 9;
	private static final int COL_INITIALDATE = 10;
	private static final int COL_ARREARSDAYS = 11;
	private static final int COL_OVERDUECAPITAL = 12;
	private static final int COL_OVERDUEINTEREST = 13;
	private static final int COL_OVERDUEARREARSVALUE = 14;
	private static final int COL_OVERDUEANOTHERITEMS = 15;
	private static final int COL_OVERDUETOTAL = 16;
	private static final int COL_NEXTPAYMENTDATE = 17;
	private static final int COL_NEXTPAYMENTVALUE = 18;
	private static final int COL_ORDINARYINTERESTRATE = 19;
	private static final int COL_ARREARSINTERESTRATE = 20;
	private static final int COL_CAPITALBALANCE = 21;
	private static final int COL_TOTALBALANCE = 22;
	private static final int COL_ORIGINALTERM = 23;
	private static final int COL_SECTOR = 24;
	private static final int COL_OPERATIONDESCRIPTION = 25;
	private static final int COL_TAX = 26;
	private static final int COL_TOTALAMOUNTCAN = 27;
	private static final int COL_CAPITAL = 28;
	private static final int COL_INTEREST = 29;
	private static final int COL_MORATORIUM = 30;
	private static final int COL_INSURANCE = 31;
	private static final int COL_OTHER = 32;
	private static final int COL_DESMONEY = 33;
	private static final int COL_NEXTDUEDATE = 34;

	private static ILogger logger = LogFactory.getLogger(LoanBalanceDetail.class);

	/**
	 * M&eacute;todo getBalanceDetail En este m&eacute;todo obtenemos la Detalle
	 * del Balance del pr&eacute;stamo, enviamos un objeto de tipo
	 * BalanceDetailPaymentRequest y obtenemos de respuesta un objeto de tipo
	 * BalanceDetailPaymentResponse, para m&aacute;s detalle de los objetos,
	 * revisar las siguientes referencias:
	 * 
	 * @see BalanceDetailPaymentRequest
	 * @see BalanceDetailPaymentResponse
	 */
	@Override
	public BalanceDetailPaymentResponse getBalanceDetail(BalanceDetailPaymentRequest aLoanDetailRequest)
			throws CTSServiceException, CTSInfrastructureException {
		// Context context = ContextManager.getContext();
		// CobisSession session = (CobisSession) context.getSession();
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: LoanBalanceDetail");
			logger.logInfo("CONSULTA EN EL CENTRAL NUEVO");
		}
		IProcedureRequest request = new ProcedureRequestAS();
		request.setSpName("cob_cartera..sp_tr07_cons_prestamos");
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18402");
		request.setSpName("cob_cartera..sp_tr07_cons_prestamos");
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, "18402");
		// request.addInputParam("@s_lsrv", ICTSTypes.SYBVARCHAR,
		// session.getServer());
		request.addInputParam("@i_servicio", ICTSTypes.SQLINT1, "1");
		request.addInputParam("@i_operacion", ICTSTypes.SYBVARCHAR, "C");
		request.addInputParam("@i_prod", ICTSTypes.SQLINT1, aLoanDetailRequest.getProductId().toString());
		request.addInputParam("@i_mon", ICTSTypes.SQLINT2, aLoanDetailRequest.getCurrencyID().toString());
		request.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, aLoanDetailRequest.getUserName().toString());
		request.addInputParam("@i_banco", ICTSTypes.SQLVARCHAR,
				aLoanDetailRequest.getProductNumber().getProductNumber());
		request.addInputParam("@i_valida_des", ICTSTypes.SQLVARCHAR, aLoanDetailRequest.getValidateAccount());
		if (logger.isDebugEnabled()) {
			logger.logDebug("Request: " + request.getProcedureRequestAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("Start Request");
		}
		IProcedureResponse wResponse = executeCoreBanking(request);
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response: " + wResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("Finalize Response");
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("Llama a transformar");
		}
		BalanceDetailPaymentResponse balanceDetailPaymentResponse = transformBalanceDetailPaymentResponse(wResponse);
		return balanceDetailPaymentResponse;
	}

	private BalanceDetailPaymentResponse transformBalanceDetailPaymentResponse(IProcedureResponse aProcedureResponse) {
		if (logger.isInfoEnabled()) {
			logger.logInfo("Entra a transformar");
		}
		BalanceDetailPaymentResponse response = new BalanceDetailPaymentResponse();
		List<BalanceDetailPayment> aBalanceDetailPaymentCollection = new ArrayList<BalanceDetailPayment>();

		BalanceDetailPayment aBalanceDetailPayment = null;

		if (logger.isDebugEnabled()) {
			logger.logDebug("ProcedureResponseJBA: " + aProcedureResponse.getProcedureResponseAsString());
		}

		// GCO - Manejo de Errores
		if (aProcedureResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsBalanceDatailPayment = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();
			for (IResultSetRow iResultSetRow : rowsBalanceDatailPayment) {
				Product producto = new Product();
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				producto.setProductNumber(columns[COL_PRODUCTNUMBER].getValue());
				aBalanceDetailPayment = new BalanceDetailPayment();
				aBalanceDetailPayment.setAditionalData(columns[COL_ADITIONALDATA].getValue());
				aBalanceDetailPayment.setProductNumber(producto);
				aBalanceDetailPayment.setEntityName(columns[COL_ENTITYNAME].getValue());
				aBalanceDetailPayment.setOperationType(columns[COL_OPERATIONTYPE].getValue());
				aBalanceDetailPayment.setInitialAmount(new BigDecimal(columns[COL_INITIALAMOUNT].getValue()));
				aBalanceDetailPayment.setMonthlyPaymentDay(columns[COL_MONTHLYPAYMENTDAY].getValue());
				if (Utils.isNullOrEmpty(columns[COL_STATUS].getValue()))
					aBalanceDetailPayment.setStatus(new String(" "));
				else
					aBalanceDetailPayment.setStatus(columns[COL_STATUS].getValue());

				if (Utils.isNullOrEmpty(columns[COL_LASTPAYMENTDATE].getValue()))
					aBalanceDetailPayment.setLastPaymentDate(new String(" "));
				else
					aBalanceDetailPayment.setLastPaymentDate(columns[COL_LASTPAYMENTDATE].getValue());

				aBalanceDetailPayment.setExpirationDate(columns[COL_EXPIRATIONDATE].getValue());

				if (Utils.isNullOrEmpty(columns[COL_EXECUTIVE].getValue()))
					aBalanceDetailPayment.setExecutive(new String(" "));
				else
					aBalanceDetailPayment.setExecutive(columns[COL_EXECUTIVE].getValue());

				aBalanceDetailPayment.setInitialDate(columns[COL_INITIALDATE].getValue());
				aBalanceDetailPayment.setArrearsDays(Integer.parseInt(columns[COL_ARREARSDAYS].getValue()));

				if (Utils.isNullOrEmpty(columns[COL_OVERDUECAPITAL].getValue()))
					aBalanceDetailPayment.setOverdueCapital(new BigDecimal(0));
				else
					aBalanceDetailPayment.setOverdueCapital(new BigDecimal(columns[COL_OVERDUECAPITAL].getValue()));

				if (Utils.isNullOrEmpty(columns[COL_OVERDUEINTEREST].getValue()))
					aBalanceDetailPayment.setOverdueInterest(new BigDecimal(0));
				else
					aBalanceDetailPayment.setOverdueInterest(new BigDecimal(columns[COL_OVERDUEINTEREST].getValue()));

				if (Utils.isNullOrEmpty(columns[COL_OVERDUEARREARSVALUE].getValue()))
					aBalanceDetailPayment.setOverdueArrearsValue(new BigDecimal(0));
				else
					aBalanceDetailPayment
							.setOverdueArrearsValue(new BigDecimal(columns[COL_OVERDUEARREARSVALUE].getValue()));

				if (Utils.isNullOrEmpty(columns[COL_OVERDUEANOTHERITEMS].getValue()))
					aBalanceDetailPayment.setOverdueAnotherItems(new BigDecimal(0));
				else
					aBalanceDetailPayment
							.setOverdueAnotherItems(new BigDecimal(columns[COL_OVERDUEANOTHERITEMS].getValue()));

				if (Utils.isNullOrEmpty(columns[COL_OVERDUETOTAL].getValue()))
					aBalanceDetailPayment.setOverdueTotal(new BigDecimal(0));
				else
					aBalanceDetailPayment.setOverdueTotal(new BigDecimal(columns[COL_OVERDUETOTAL].getValue()));

				aBalanceDetailPayment.setNextPaymentDate(columns[COL_NEXTPAYMENTDATE].getValue());

				if (Utils.isNullOrEmpty(columns[COL_NEXTPAYMENTVALUE].getValue()))
					aBalanceDetailPayment.setNextPaymentValue(new BigDecimal(0));
				else
					aBalanceDetailPayment.setNextPaymentValue(new BigDecimal(columns[COL_NEXTPAYMENTVALUE].getValue()));

				// aBalanceDetailPayment.setNextProjectedPaymentValue(new
				// BigDecimal(columns[COL_NEXTPROJECTEDPAYMENTVALUE].getValue()));
				if (Utils.isNullOrEmpty(columns[COL_ORDINARYINTERESTRATE].getValue()))
					aBalanceDetailPayment.setOrdinaryInterestRate(new BigDecimal(0));
				else
					aBalanceDetailPayment
							.setOrdinaryInterestRate(new BigDecimal(columns[COL_ORDINARYINTERESTRATE].getValue()));

				if (Utils.isNullOrEmpty(columns[COL_ARREARSINTERESTRATE].getValue()))
					aBalanceDetailPayment.setArrearsInterestRate(new BigDecimal(0));
				else
					aBalanceDetailPayment
							.setArrearsInterestRate(new BigDecimal(columns[COL_ARREARSINTERESTRATE].getValue()));

				if (Utils.isNullOrEmpty(columns[COL_CAPITALBALANCE].getValue()))
					aBalanceDetailPayment.setCapitalBalance(new BigDecimal(0));
				else
					aBalanceDetailPayment.setCapitalBalance(new BigDecimal(columns[COL_CAPITALBALANCE].getValue()));

				if (Utils.isNullOrEmpty(columns[COL_TOTALBALANCE].getValue()))
					aBalanceDetailPayment.setTotalBalance(new BigDecimal(0));
				else
					aBalanceDetailPayment.setTotalBalance(new BigDecimal(columns[COL_TOTALBALANCE].getValue()));

				aBalanceDetailPayment.setOriginalTerm(columns[COL_ORIGINALTERM].getValue());
				aBalanceDetailPayment.setSector(columns[COL_SECTOR].getValue());
				aBalanceDetailPayment.setOperationDescription(columns[COL_OPERATIONDESCRIPTION].getValue());

				if (Utils.isNullOrEmpty(columns[COL_TAX].getValue()))
					aBalanceDetailPayment.setTax(new BigDecimal(0));
				else
					aBalanceDetailPayment.setTax(new BigDecimal(columns[COL_TAX].getValue()));
				aBalanceDetailPaymentCollection.add(aBalanceDetailPayment);

				if (Utils.isNullOrEmpty(columns[COL_TOTALAMOUNTCAN].getValue()))
					aBalanceDetailPayment.setTotalAmountCancel(new BigDecimal(0));
				else
					aBalanceDetailPayment.setTotalAmountCancel(new BigDecimal(columns[COL_TOTALAMOUNTCAN].getValue()));

				if (Utils.isNullOrEmpty(columns[COL_CAPITAL].getValue()))
					aBalanceDetailPayment.setCapital(new BigDecimal(0));
				else
					aBalanceDetailPayment.setCapital(new BigDecimal(columns[COL_CAPITAL].getValue()));

				if (Utils.isNullOrEmpty(columns[COL_INTEREST].getValue()))
					aBalanceDetailPayment.setInterest(new BigDecimal(0));
				else
					aBalanceDetailPayment.setInterest(new BigDecimal(columns[COL_INTEREST].getValue()));

				if (Utils.isNullOrEmpty(columns[COL_MORATORIUM].getValue()))
					aBalanceDetailPayment.setMoratorium(new BigDecimal(0));
				else
					aBalanceDetailPayment.setMoratorium(new BigDecimal(columns[COL_MORATORIUM].getValue()));

				if (Utils.isNullOrEmpty(columns[COL_INSURANCE].getValue()))
					aBalanceDetailPayment.setInsurance(new BigDecimal(0));
				else
					aBalanceDetailPayment.setInsurance(new BigDecimal(columns[COL_INSURANCE].getValue()));

				if (Utils.isNullOrEmpty(columns[COL_OTHER].getValue()))
					aBalanceDetailPayment.setOther(new BigDecimal(0));
				else
					aBalanceDetailPayment.setOther(new BigDecimal(columns[COL_OTHER].getValue()));

				aBalanceDetailPayment.setDesMoney(columns[COL_DESMONEY].getValue());
				aBalanceDetailPayment.setNextDueDate(columns[COL_NEXTDUEDATE].getValue());

			}
			response.setBalanceDetailList(aBalanceDetailPaymentCollection);
		} else {
			response.setMessages(Utils.returnArrayMessage(aProcedureResponse)); // SETEA
																				// ARREGLO
																				// DE
																				// MENSAJES
		}
		response.setReturnCode(aProcedureResponse.getReturnCode());
		return response;
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
