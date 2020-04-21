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
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.BankRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BankResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Bank;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceBanks;
import com.cobiscorp.ecobis.ib.utils.dtos.Utils;

/**
 *
 * @author eortega
 * @since Sep 17, 2014
 * @version 1.0.0
 */

@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Component(name = "BanksByCountryQueryCore", immediate = false)
@Properties(value = { @Property(name = "service.description", value = "BanksByCountryQueryCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "BanksByCountryQueryCore") })
public class BanksByCountryQueryCore extends SPJavaOrchestrationBase {

	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	private static final String CLASS_NAME = " >-----> ";
	private static ILogger logger = LogFactory.getLogger(BanksByCountryQueryCore.class);

	@Reference(referenceInterface = ICoreServiceBanks.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceBanksByCountry", unbind = "unbindCoreServiceBanksByCountry")
	private ICoreServiceBanks coreServiceBanksByCountry;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindCoreServiceBanksByCountry(ICoreServiceBanks service) {
		coreServiceBanksByCountry = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindCoreServiceBanksByCountry(ICoreServiceBanks service) {
		coreServiceBanksByCountry = null;
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Iniciando solicitud de ejecucion del servicio");
		return getBanksByCountry(anOriginalRequest, aBagSPJavaOrchestration);
	}

	protected IProcedureResponse getBanksByCountry(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Iniciando Servicio");
		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);

		Map<String, Object> wprocedureResponse1;
		try {
			wprocedureResponse1 = procedureResponse1(anOriginalRequest, aBagSPJavaOrchestration);

			Boolean wSuccessExecutionOperation1 = (Boolean) wprocedureResponse1.get("SuccessExecutionOperation");
			IProcedureResponse wIProcedureResponse1 = (IProcedureResponse) wprocedureResponse1
					.get("IProcedureResponse");
			IProcedureResponse wErrorProcedureResponse = (IProcedureResponse) wprocedureResponse1
					.get("ErrorProcedureResponse");

			if (wSuccessExecutionOperation1) {
				return wIProcedureResponse1;
			} else {
				return wErrorProcedureResponse;
			}
		} catch (CTSServiceException e) {
			if (logger.isErrorEnabled())
				logger.logError("Consulta Bancos Error:" + e.toString());
			IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalRequest);
			ErrorBlock eb = new ErrorBlock(-1, "Error en ejecucion del servicio");
			wProcedureRespFinal.addResponseBlock(eb);
			wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
			wProcedureRespFinal.setReturnCode(-1);
			return wProcedureRespFinal;

		} catch (CTSInfrastructureException e) {
			if (logger.isErrorEnabled())
				logger.logError("Consulta Bancos Error:" + e.toString());
			IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalRequest);
			ErrorBlock eb = new ErrorBlock(-1, "Error de Infrestructura");
			wProcedureRespFinal.addResponseBlock(eb);
			wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
			wProcedureRespFinal.setReturnCode(-1);
			return wProcedureRespFinal;
		}
	}

	protected Map<String, Object> procedureResponse1(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Consumiendo Servicio");

		HashMap<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("IProcedureResponse", null);
		returnMap.put("SuccessExecutionOperation", false);
		returnMap.put("ErrorProcedureResponse", null);

		IProcedureResponse wProcedureResponse = getBanksByCountry(transformRequestToDto(aBagSPJavaOrchestration),
				aBagSPJavaOrchestration);
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Respuesta devuelta del servicio:"
					+ wProcedureResponse.getProcedureResponseAsString());

		if ((wProcedureResponse == null) || wProcedureResponse.hasError()) {
			if (logger.isDebugEnabled())
				logger.logDebug(CLASS_NAME + "Error en servicio" + wProcedureResponse.getProcedureResponseAsString());
			returnMap.put("ErrorProcedureResponse", wProcedureResponse);
			return returnMap;
		}
		returnMap.put("SuccessExecutionOperation", true);
		returnMap.put("IProcedureResponse", wProcedureResponse);
		return returnMap;
	}

	private IProcedureResponse getBanksByCountry(BankRequest bankRequest, Map<String, Object> aBagSPJavaOrchestration)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Data de consulta:" + bankRequest.toString());
		BankResponse bankResponse = coreServiceBanksByCountry.executeGetBanksByCountry(bankRequest);
		IProcedureResponse pResponse = transformDtoToResponse(bankResponse, aBagSPJavaOrchestration);
		return pResponse;
	}

	private IProcedureResponse transformDtoToResponse(BankResponse bankResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Salida:" + bankResponse);
		IProcedureResponse pResponse = initProcedureResponse(
				(IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST));
		// setea errores
		Utils.transformBaseResponseToIprocedureResponse(bankResponse, pResponse);

		IResultSetData data = new ResultSetData();
		if (bankResponse.getSuccess()) {
			if (bankResponse != null && bankResponse.getBankCollection().size() > 0) {
				IResultSetHeader metaData = new ResultSetHeader();

				metaData.addColumnMetaData(new ResultSetHeaderColumn("CODIGO", ICTSTypes.SYBINT2, 6));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("NOMBRE DEL BANCO", ICTSTypes.SYBVARCHAR, 42));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("CONVENIO", ICTSTypes.SQLCHAR, 1));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("BANCO_ENTE", ICTSTypes.SYBINT4, 11));

				for (Bank obj : bankResponse.getBankCollection()) {
					IResultSetRow row = new ResultSetRow();
					row.addRowData(1, new ResultSetRowColumnData(false, obj.getId().toString()));
					row.addRowData(2, new ResultSetRowColumnData(false, obj.getDescription()));
					row.addRowData(3, new ResultSetRowColumnData(false, obj.getConvenio()));
					row.addRowData(4, new ResultSetRowColumnData(false, obj.getBancoEnte()));
					data.addRow(row);
				}
				IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
				pResponse.addResponseBlock(resultBlock);
			}
			pResponse.setReturnCode(0);
		}
		return pResponse;
	}

	private BankRequest transformRequestToDto(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Entrada");
		IProcedureRequest wOriginalRequest = (ProcedureRequestAS) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);

		BankRequest bankRequest = new BankRequest();
		Bank banco = new Bank();

		if (!Utils.isNullOrEmpty(wOriginalRequest.readValueParam("@i_banco")))
			bankRequest.setDescripcionBanco(wOriginalRequest.readValueParam("@i_banco"));
		if (!Utils.isNullOrEmpty(wOriginalRequest.readValueParam("@i_pais")))
			banco.setId(Integer.parseInt(wOriginalRequest.readValueParam("@i_pais")));
		if (!Utils.isNullOrEmpty(wOriginalRequest.readValueParam("@i_modo")))
			bankRequest.setModo(Integer.parseInt(wOriginalRequest.readValueParam("@i_modo")));

		bankRequest.setBanco(banco);

		bankRequest.setOriginalRequest(wOriginalRequest);
		return bankRequest;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
