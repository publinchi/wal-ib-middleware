/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.programmed.savings;

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
import com.cobiscorp.ecobis.ib.application.dtos.ProgrammedSavingsAddProgrammedSavingsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ProgrammedSavingsAddProgrammedSavingsResponse;
import com.cobiscorp.ecobis.ib.application.dtos.TransferResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceProgrammedSavingsOpenning;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;

@Component(name = "ProgrammedSavingsOpenning", immediate = false)
@Service(value = { ICoreServiceProgrammedSavingsOpenning.class })
@Properties(value = { @Property(name = "service.description", value = "ProgrammedSavingsOpenning"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ProgrammedSavingsOpenning") })

public class ProgrammedSavingsOpenning extends SPJavaOrchestrationBase
		implements ICoreServiceProgrammedSavingsOpenning {
	private static ILogger logger = LogFactory.getLogger(ProgrammedSavingsOpenning.class);
	private static final String SP_PROGRAMMED_SAVINGS = "cobis..sp_tr4_ahorro_programado";

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
	}

	/**
	 * M&eacute;todo addProgrammedSavings En este m&eacute;todo generamos el
	 * ahorro programado, enviamos un objeto de tipo
	 * ProgrammedSavingsAddProgrammedSavingsRequest y obtenemos de respuesta un
	 * objeto de tipo ProgrammedSavingsAddProgrammedSavingsResponse, para
	 * m&aacute;s detalle de los objetos, revisar las siguientes referencias:
	 * 
	 * @see ProgrammedSavingsAddProgrammedSavingsRequest
	 * @see ProgrammedSavingsAddProgrammedSavingsResponse
	 */
	@Override
	public ProgrammedSavingsAddProgrammedSavingsResponse addProgrammedSavings(
			ProgrammedSavingsAddProgrammedSavingsRequest aProgrammedSavingsAddProgrammedSavingsRequest)
			throws CTSServiceException, CTSInfrastructureException {
		
		IProcedureResponse pResponse = AgregarAhoProg(SP_PROGRAMMED_SAVINGS,
				aProgrammedSavingsAddProgrammedSavingsRequest);
		ProgrammedSavingsAddProgrammedSavingsResponse aProgrammedSavingsAddProgrammedSavingsResponse = transformToAddProgrammedSavingsResponse(
				pResponse);
		return aProgrammedSavingsAddProgrammedSavingsResponse;
	}

	private IProcedureResponse AgregarAhoProg(String SpName,
			ProgrammedSavingsAddProgrammedSavingsRequest aProgrammedSavingsAddProgrammedSavingsRequest)
			throws CTSServiceException, CTSInfrastructureException {
		;
		IProcedureRequest request = initProcedureRequest(
				aProgrammedSavingsAddProgrammedSavingsRequest.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1875056");
		String msg = "X";
		for (int i = 0; i < 999; i++) {
			msg = msg + "X";
		}
		request.setSpName(SpName);
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1875056");
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR,
				aProgrammedSavingsAddProgrammedSavingsRequest.getCodeTransactionalIdentifier());
		request.addInputParam("@i_cliente", ICTSTypes.SQLINT4,
				aProgrammedSavingsAddProgrammedSavingsRequest.getUser().getEntityId().toString());
		request.addInputParam("@i_login", ICTSTypes.SQLVARCHAR,
				aProgrammedSavingsAddProgrammedSavingsRequest.getUser().getName());
		request.addInputParam("@i_frecuencia", ICTSTypes.SQLVARCHAR,
				aProgrammedSavingsAddProgrammedSavingsRequest.getProgrammedSavings().getFrequency());
		request.addInputParam("@i_monto", ICTSTypes.SQLMONEY,
				aProgrammedSavingsAddProgrammedSavingsRequest.getProgrammedSavings().getAmount().toString());
		request.addInputParam("@i_moneda", ICTSTypes.SQLINT1, aProgrammedSavingsAddProgrammedSavingsRequest
				.getProgrammedSavings().getCurrency().getCurrencyId().toString());
		request.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR,
				aProgrammedSavingsAddProgrammedSavingsRequest.getProgrammedSavings().getConcept());
		request.addInputParam("@i_fecha_ini", ICTSTypes.SQLVARCHAR,
				aProgrammedSavingsAddProgrammedSavingsRequest.getProgrammedSavings().getInitialDate());
		request.addInputParam("@i_plazo", ICTSTypes.SQLVARCHAR,
				aProgrammedSavingsAddProgrammedSavingsRequest.getProgrammedSavings().getTerm());
		request.addInputParam("@i_fecha_ven", ICTSTypes.SQLVARCHAR,
				aProgrammedSavingsAddProgrammedSavingsRequest.getProgrammedSavings().getExpirationDate());
		request.addInputParam("@i_mail", ICTSTypes.SQLVARCHAR,
				aProgrammedSavingsAddProgrammedSavingsRequest.getProgrammedSavings().getMail());
		request.addInputParam("@i_sucursal", ICTSTypes.SQLINT4,
				aProgrammedSavingsAddProgrammedSavingsRequest.getProgrammedSavings().getBranch().toString());
		request.addInputParam("@i_id_beneficiary", ICTSTypes.SQLVARCHAR,
				aProgrammedSavingsAddProgrammedSavingsRequest.getProgrammedSavings().getIdBeneficiary());
		request.addInputParam("@i_cta_deb", ICTSTypes.SQLVARCHAR,
				aProgrammedSavingsAddProgrammedSavingsRequest.getProduct2().getProductNumber().toString());
		request.addInputParam("@i_prod_deb", ICTSTypes.SQLINT1,
				aProgrammedSavingsAddProgrammedSavingsRequest.getProduct2().getProductId().toString());
		request.addInputParam("@i_mon_deb", ICTSTypes.SQLINT1,
				aProgrammedSavingsAddProgrammedSavingsRequest.getProduct2().getCurrency().getCurrencyId().toString());
		request.addInputParam("@i_cta_ahoprog", ICTSTypes.SQLVARCHAR,
				aProgrammedSavingsAddProgrammedSavingsRequest.getProduct1().getProductNumber().toString());
		request.addOutputParam("@o_cta_ahoprog", ICTSTypes.SQLVARCHAR, "0000000000000000000000");
		request.addOutputParam("@o_body", ICTSTypes.SQLVARCHAR, msg);
		if (logger.isDebugEnabled()) {
			logger.logDebug("<<<Request JBA: >>>" + request.getProcedureRequestAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("<<<Start Request JBA>>>" + msg);
		}
		IProcedureResponse pResponse = executeCoreBanking(request);
		if (logger.isDebugEnabled()) {
			logger.logDebug("<<<Response JBA: >>>" + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("<<<Finalize Response JBA>>>");
		}
		return pResponse;
	}

	private ProgrammedSavingsAddProgrammedSavingsResponse transformToAddProgrammedSavingsResponse(
			IProcedureResponse aProcedureResponse) {
		ProgrammedSavingsAddProgrammedSavingsResponse aProgrammedSavingsAddProgrammedSavingsResponse = new ProgrammedSavingsAddProgrammedSavingsResponse();
		TransferResponse aTransferResponse = new TransferResponse();
		aTransferResponse.setProductNumber(aProcedureResponse.readValueParam("@o_cta_ahoprog"));
		aTransferResponse.setBody(new String(aProcedureResponse.readValueParam("@o_body")));
		aProgrammedSavingsAddProgrammedSavingsResponse.setTransferResponse(aTransferResponse);
		Message[] message = Utils.returnArrayMessage(aProcedureResponse);
		aProgrammedSavingsAddProgrammedSavingsResponse.setMessages(message);
		aProgrammedSavingsAddProgrammedSavingsResponse.setReturnCode(aProcedureResponse.getReturnCode());
		return aProgrammedSavingsAddProgrammedSavingsResponse;
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
