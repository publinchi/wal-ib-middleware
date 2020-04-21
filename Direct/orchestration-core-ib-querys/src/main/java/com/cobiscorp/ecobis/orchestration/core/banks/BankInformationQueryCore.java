package com.cobiscorp.ecobis.orchestration.core.banks;

import java.util.HashMap;
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
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
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
import com.cobiscorp.ecobis.ib.application.dtos.OfficeBankRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficeBankResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Bank;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Country;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Office;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceBanks;

/**
 * Get information of bank with filter type.
 *
 * @author schancay
 * @since Sep 18, 2014
 * @version 1.0.0
 */

@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Component(name = "BankInformationQueryCore", immediate = false)
@Properties(value = { @Property(name = "service.description", value = "BankInformationQueryCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "BankInformationQueryCore") })
public class BankInformationQueryCore extends SPJavaOrchestrationBase {

	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	private static final String CLASS_NAME = " >-----> ";
	private static ILogger logger = LogFactory.getLogger(BankInformationQueryCore.class);

	@Reference(referenceInterface = ICoreServiceBanks.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceBanks", unbind = "unbindCoreServiceBanks")
	private ICoreServiceBanks coreServiceBanks;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindCoreServiceBanks(ICoreServiceBanks service) {
		coreServiceBanks = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindCoreServiceBanks(ICoreServiceBanks service) {
		coreServiceBanks = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "INICIANDO SERVICIO");
		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);

		Map<String, Object> listDependencies = new HashMap<String, Object>();
		listDependencies.put("ICoreServiceBanks", coreServiceBanks);

		Utils.validateComponentInstance(listDependencies);

		IProcedureResponse response = null;
		try {
			response = getInformationBank(aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			e.printStackTrace();
			Utils.returnExceptionService(anOriginalRequest, e);
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
			Utils.returnExceptionService(anOriginalRequest, e);
		}

		return response;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	private IProcedureResponse getInformationBank(Map<String, Object> aBagSPJavaOrchestration)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Obteniendo informacion del Core");
		IProcedureRequest originalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		OfficeBankResponse officeResponse = null;

		if (originalRequest.readParam("@i_swift_code") != null) {
			String TYPE_CODE = originalRequest.readValueParam("@i_swift_code");

			if (logger.isDebugEnabled())
				logger.logDebug(CLASS_NAME + "Filtro de busqueda por banco:" + TYPE_CODE);
			if (TYPE_CODE.equals("ABA")) {
				if (logger.isInfoEnabled())
					logger.logInfo(CLASS_NAME + "Consultando banco por codigo:" + TYPE_CODE);
				officeResponse = coreServiceBanks.getOfficeByCodeABA(transformRequestToDto(originalRequest));
			}

			if (TYPE_CODE.equals("SWIFT")) {
				if (logger.isInfoEnabled())
					logger.logInfo(CLASS_NAME + "Consultando banco por codigo :" + TYPE_CODE);
				officeResponse = coreServiceBanks.getOfficeByCodeSWIFT(transformRequestToDto(originalRequest));
			}

			if (officeResponse == null) {
				if (logger.isDebugEnabled())
					logger.logDebug(CLASS_NAME + "Tipo de filtro de busqueda no soportado");
				IProcedureResponse wProcedureRespFinal = new ProcedureResponseAS();
				ErrorBlock eb = new ErrorBlock(-1, "Tipo de filtro de busqueda no soportado");
				wProcedureRespFinal.addResponseBlock(eb);
				wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
				wProcedureRespFinal.setReturnCode(-1);
				return wProcedureRespFinal;
			}
		}

		return transformResponseToDto(officeResponse);
	}

	private OfficeBankRequest transformRequestToDto(IProcedureRequest request) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando request:" + request.getProcedureRequestAsString());
		OfficeBankRequest officeBankRequest = new OfficeBankRequest();
		officeBankRequest.setCodeTransactionalIdentifier(request.readValueParam("@t_trn"));
		Office office = new Office();
		office.setSubtype(request.readValueParam("@i_swift_code"));
		office.setCode(request.readValueParam("@i_code_type"));

		officeBankRequest.setOffice(office);

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Objeto Devuelto:" + officeBankRequest);
		return officeBankRequest;
	}

	private IProcedureResponse transformResponseToDto(OfficeBankResponse officeBankResponse) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto:" + officeBankResponse);

		IProcedureResponse response = new ProcedureResponseAS();
		com.cobiscorp.ecobis.ib.utils.dtos.Utils.transformBaseResponseToIprocedureResponse(officeBankResponse,
				response);

		if (officeBankResponse.getSuccess()) {
			Bank bank = officeBankResponse.getOfficeBankInformation().getBank();
			Office office = officeBankResponse.getOfficeBankInformation().getOffice();
			Country country = officeBankResponse.getOfficeBankInformation().getCountry();

			// Country
			IResultSetHeader metaDataCountry = new ResultSetHeader();
			metaDataCountry.addColumnMetaData(new ResultSetHeaderColumn("param1", ICTSTypes.SQLVARCHAR, 500));
			metaDataCountry.addColumnMetaData(new ResultSetHeaderColumn("param2", ICTSTypes.SQLVARCHAR, 500));
			IResultSetData dataCountry = new ResultSetData();
			if (country != null) {
				IResultSetRow rowCountry = new ResultSetRow();
				rowCountry.addRowData(1, new ResultSetRowColumnData(false, country.getCode().toString()));
				rowCountry.addRowData(2, new ResultSetRowColumnData(false, country.getName()));
				dataCountry.addRow(rowCountry);
			}
			IResultSetBlock resultBlockCountry = new ResultSetBlock(metaDataCountry, dataCountry);

			// Bank
			IResultSetHeader metaDataBank = new ResultSetHeader();
			metaDataBank.addColumnMetaData(new ResultSetHeaderColumn("param1", ICTSTypes.SQLVARCHAR, 500));
			metaDataBank.addColumnMetaData(new ResultSetHeaderColumn("param2", ICTSTypes.SQLVARCHAR, 500));
			IResultSetData dataBank = new ResultSetData();
			if (office != null) {
				IResultSetRow rowBank = new ResultSetRow();
				rowBank.addRowData(1, new ResultSetRowColumnData(false, bank.getId().toString()));
				rowBank.addRowData(2, new ResultSetRowColumnData(false, bank.getDescription()));
				dataBank.addRow(rowBank);
			}
			IResultSetBlock resultBlockBank = new ResultSetBlock(metaDataBank, dataBank);

			// Office
			IResultSetHeader metaDataOffice = new ResultSetHeader();
			metaDataOffice.addColumnMetaData(new ResultSetHeaderColumn("param1", ICTSTypes.SQLVARCHAR, 500));
			metaDataOffice.addColumnMetaData(new ResultSetHeaderColumn("param2", ICTSTypes.SQLVARCHAR, 500));
			metaDataOffice.addColumnMetaData(new ResultSetHeaderColumn("param3", ICTSTypes.SQLVARCHAR, 500));
			IResultSetData dataOffice = new ResultSetData();
			if (office != null) {
				IResultSetRow rowOffice = new ResultSetRow();
				rowOffice.addRowData(1, new ResultSetRowColumnData(false, office.getSubtype()));
				rowOffice.addRowData(2, new ResultSetRowColumnData(false, office.getId().toString()));
				rowOffice.addRowData(3, new ResultSetRowColumnData(false, office.getDescription()));
				dataOffice.addRow(rowOffice);
			}
			IResultSetBlock resultBlockOffice = new ResultSetBlock(metaDataOffice, dataOffice);

			response.addResponseBlock(resultBlockCountry);
			response.addResponseBlock(resultBlockBank);
			response.addResponseBlock(resultBlockOffice);
		}

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Objeto Devuelto:" + response.getProcedureResponseAsString());
		return response;
	}
}
