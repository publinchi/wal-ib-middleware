package com.cobiscorp.ecobis.orchestration.core.ib.banks;

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
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.ecobis.ib.application.dtos.BankRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BankResponse;
import com.cobiscorp.ecobis.ib.application.dtos.OfficeBankRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficeBankResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Bank;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Country;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Office;
import com.cobiscorp.ecobis.ib.orchestration.dtos.OfficeBankInformation;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceBanks;
import com.cobiscorp.ecobis.ib.utils.dtos.Utils;

/**
 *
 * @author eortega
 * @since Sep 17, 2014
 * @version 1.0.0
 */

@Service(value = { ICoreServiceBanks.class, ICISSPBaseOrchestration.class, IOrchestrator.class })
@Component(name = "BanksByCountryQuery", immediate = false)
@Properties(value = { @Property(name = "service.description", value = "BanksByCountryQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "BanksByCountryQuery") })
public class BankInformationQuerys extends SPJavaOrchestrationBase implements ICoreServiceBanks {

	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	protected static final String COBIS_CONTEXT = "COBIS";
	private static final String CLASS_NAME = " >-----> ";
	private static ILogger logger = LogFactory.getLogger(BankInformationQuerys.class);

	@Override
	public BankResponse executeGetBanksByCountry(BankRequest bankRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + "INICIANDO SERVICIO");
			logger.logInfo(CLASS_NAME + "RESPUESTA CORE GENERADA");
		}

		BankResponse wBankResponse = transformResponse(executeGetBanksByCountryCobis(bankRequest));

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "TERMINANDO SERVICIO");
		return wBankResponse;
	}

	private IProcedureResponse executeGetBanksByCountryCobis(BankRequest bankRequest) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando consulta de bancos por pais CORE COBIS");

		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		// anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN,
		// "1800010");

		anOriginalRequest.setSpName("cob_comext..sp_query_pais_banco");

		// anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4,
		// "1800010");
		Bank banco = bankRequest.getBanco();

		if (banco.getId() != null)
			anOriginalRequest.addInputParam("@i_pais", ICTSTypes.SQLINT4, banco.getId().toString());

		if (bankRequest.getDescripcionBanco() != null)
			anOriginalRequest.addInputParam("@i_banco", ICTSTypes.SQLINT4, bankRequest.getDescripcionBanco());

		if (bankRequest.getModo() != null)
			anOriginalRequest.addInputParam("@i_modo", ICTSTypes.SQLINT2, bankRequest.getModo().toString());

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Data enviada a ejecutar:" + anOriginalRequest.toString());

		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta del Core:" + response.getProcedureResponseAsString());

		return response;
	}

	private BankResponse transformResponse(IProcedureResponse response) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando Transformacion de Respuesta" + response);

		BankResponse wBankResponse = new BankResponse();
		List<Bank> bankCollection = new ArrayList<Bank>();

		if (!response.hasError()) {
			IResultSetBlock resulsetOriginBalance = response.getResultSet(1);
			IResultSetRow[] rowsTemp = resulsetOriginBalance.getData().getRowsAsArray();

			if (rowsTemp.length > 0) {

				for (IResultSetRow iResultSetRow : rowsTemp) {
					IResultSetRowColumnData[] rows = iResultSetRow.getColumnsAsArray();
					Bank banco = new Bank();

					if (rows[0].getValue() != null)
						banco.setId(new Integer(rows[0].getValue().toString()));

					if (rows[1].getValue() != null)
						banco.setDescription(rows[1].getValue().toString());
					if (rows[2].getValue() != null)
						banco.setConvenio(rows[2].getValue().toString());
					if (rows[3].getValue() != null)
						banco.setBancoEnte(rows[3].getValue().toString());

					bankCollection.add(banco);
				}
			}
			wBankResponse.setBankCollection(bankCollection);
			wBankResponse.setSuccess(response.getReturnCode() == 0);
		}

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta" + wBankResponse);
		return wBankResponse;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceBanks#
	 * getOfficeByCodeSWIFT(com.cobiscorp.ecobis.ib.application.dtos.
	 * OfficeBankRequest)
	 */
	@Override
	public OfficeBankResponse getOfficeByCodeSWIFT(OfficeBankRequest bankRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando Consulta CORE COBIS");
		IProcedureResponse response = getInformactionBankByTypeCode(bankRequest);
		return transformResponseToDto(response);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceBanks#
	 * getOfficeByCodeABA(com.cobiscorp.ecobis.ib.application.dtos.
	 * OfficeBankRequest)
	 */
	@Override
	public OfficeBankResponse getOfficeByCodeABA(OfficeBankRequest bankRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando Consulta CORE COBIS");
		IProcedureResponse response = getInformactionBankByTypeCode(bankRequest);
		return transformResponseToDto(response);
	}

	private OfficeBankResponse transformResponseToDto(IProcedureResponse response) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Response to Dto");
		OfficeBankResponse officeBankResponse = new OfficeBankResponse();
		Utils.transformIprocedureResponseToBaseResponse(officeBankResponse, response);

		if (!response.hasError() && (response.getReturnCode() == 0)) {

			Country country = new Country();
			Bank bank = new Bank();
			Office office = new Office();

			// Country
			IResultSetBlock rsCountry = response.getResultSet(1);
			IResultSetRow[] rowsCountry = rsCountry.getData().getRowsAsArray();
			if (rowsCountry.length == 1) {
				IResultSetRowColumnData[] rows = rowsCountry[0].getColumnsAsArray();
				if (rows[0].getValue() != null)
					country.setCode(Integer.parseInt(rows[0].getValue().toString()));
				if (rows[1].getValue() != null)
					country.setName(rows[1].getValue().toString());
			}

			// Bank
			IResultSetBlock rsBank = response.getResultSet(2);
			IResultSetRow[] rowsBank = rsBank.getData().getRowsAsArray();
			if (rowsBank.length == 1) {
				IResultSetRowColumnData[] rows = rowsBank[0].getColumnsAsArray();
				if (rows[0].getValue() != null)
					bank.setId(Integer.parseInt(rows[0].getValue().toString()));
				if (rows[1].getValue() != null)
					bank.setDescription(rows[1].getValue().toString());
			}

			// Office
			IResultSetBlock rsOffice = response.getResultSet(3);
			IResultSetRow[] rowsOffice = rsOffice.getData().getRowsAsArray();
			if (rowsOffice.length == 1) {
				IResultSetRowColumnData[] rows = rowsOffice[0].getColumnsAsArray();
				if (rows[0].getValue() != null)
					office.setSubtype(rows[0].getValue().toString());
				if (rows[1].getValue() != null)
					office.setId(Integer.parseInt(rows[1].getValue().toString()));
				if (rows[2].getValue() != null)
					office.setDescription(rows[2].getValue().toString());
			}

			OfficeBankInformation officeBankInformation = new OfficeBankInformation();
			officeBankInformation.setBank(bank);
			officeBankInformation.setCountry(country);
			officeBankInformation.setOffice(office);
			officeBankResponse.setOfficeBankInformation(officeBankInformation);
		}

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Objeto devuelto:" + officeBankResponse);
		return officeBankResponse;
	}

	/**
	 * Get information from database Core
	 *
	 * @param bankRequest
	 * @return
	 */
	private IProcedureResponse getInformactionBankByTypeCode(OfficeBankRequest bankRequest) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando Consulta CORE COBIS");

		String CODE_TRN = bankRequest.getCodeTransactionalIdentifier();

		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, CODE_TRN);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		anOriginalRequest.setSpName("cobis..sp_bv_int_trans_swift");

		anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, CODE_TRN);

		if (!Utils.isNullOrEmpty(bankRequest.getOffice())) {
			if (!Utils.isNullOrEmpty(bankRequest.getOffice().getCode()))
				anOriginalRequest.addInputParam("@i_swift_code", ICTSTypes.SYBVARCHAR,
						bankRequest.getOffice().getCode());
			if (!Utils.isNullOrEmpty(bankRequest.getOffice().getSubtype()))
				anOriginalRequest.addInputParam("@i_code_type", ICTSTypes.SYBVARCHAR,
						bankRequest.getOffice().getSubtype());
		}

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Data enviada a ejecutar:" + anOriginalRequest.toString());

		IProcedureResponse response = executeCoreBanking(anOriginalRequest);
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta del Core:" + response.getProcedureResponseAsString());
		return response;
	}
}
