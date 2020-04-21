package com.cobiscorp.ecobis.ib.orchestration.reexecution;

import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.components.ComponentLocator;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.exceptions.COBISInfrastructureRuntimeException;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cts.reentry.api.IReentry;
import com.cobiscorp.cts.reentry.api.IReentryPersister;
import com.cobiscorp.ecobis.ib.application.dtos.ReExecutionRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ReExecutionResponse;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceReexecutionComponent;

@Component(name = "ReexecutionIBCore", immediate = false)
@Service(value = { ICoreServiceReexecutionComponent.class })
@Properties(value = { @Property(name = "service.description", value = "ReexecutionIBCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ReexecutionIBCore") })
public class ReexecutionIBCore extends SPJavaOrchestrationBase implements ICoreServiceReexecutionComponent {

	private static final String CLASS_NAME = " >-----> ";
	private static final String REENTRY_FILTER = "(service.impl=ReentrySPPersisterServiceImpl)";
	private ComponentLocator componentLocator = null;
	private IReentryPersister reentryPersister = null;

	private static ILogger logger = LogFactory.getLogger(ReexecutionIBCore.class);

	/**
	 * Se realiza la instanciación de los objetos principales
	 */
	protected void inicialize() {

		componentLocator = ComponentLocator.getInstance(this);

		reentryPersister = componentLocator.find(IReentryPersister.class, REENTRY_FILTER);
		if (reentryPersister == null) {
			throw new COBISInfrastructureRuntimeException("Service IReentryPersister was not found");
		}
	}

	@Override
	public ReExecutionResponse saveReexecutionComponent(ReExecutionRequest reexecutionRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + "INICIANDO SERVICIO");
			logger.logInfo(CLASS_NAME + "RESPUESTA CORE GENERADA");
		}
		inicialize();
		ReExecutionResponse wReExecutionResponse = transformResponse(saveReexecutionComponentCobis(reexecutionRequest));

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "TERMINANDO SERVICIO");
		return wReExecutionResponse;
	}

	private IProcedureResponse saveReexecutionComponentCobis(ReExecutionRequest reexecutionRequest) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Registrando transaccion en reentry CORE COBIS");

		IProcedureRequest anOriginalRequest = reexecutionRequest.getOriginalRequest();

		if (reexecutionRequest.getPriority() != null)
			anOriginalRequest.addFieldInHeader(IReentry.REENTRY_PRIORITY, ICOBISTS.HEADER_STRING_TYPE,
					reexecutionRequest.getPriority());
		if (reexecutionRequest.getSsnBranch() != null)
			anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_SSN_BRANCH, ICOBISTS.HEADER_STRING_TYPE,
					reexecutionRequest.getSsnBranch());
		if (reexecutionRequest.getSsnCentral() != null)
			anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_SSN, ICOBISTS.HEADER_NUMBER_TYPE,
					reexecutionRequest.getSsnCentral());
		if (reexecutionRequest.getTrn() != null)
			anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE,
					reexecutionRequest.getTrn());
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		anOriginalRequest.addFieldInHeader(IReentry.REENTRY_SSN_TRX, ICOBISTS.HEADER_STRING_TYPE,
				anOriginalRequest.readValueFieldInHeader(ICOBISTS.HEADER_SSN));

		if (reexecutionRequest.getOriginProduct().getProductNumber() != null)
			anOriginalRequest.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR,
					reexecutionRequest.getOriginProduct().getProductNumber());
		if (reexecutionRequest.getOriginProduct().getProductType() != null)
			anOriginalRequest.addInputParam("@i_prod", ICTSTypes.SQLINT4,
					reexecutionRequest.getOriginProduct().getProductType().toString());
		if (reexecutionRequest.getOriginProduct().getCurrency() != null)
			anOriginalRequest.addInputParam("@i_mon", ICTSTypes.SQLINT2,
					reexecutionRequest.getOriginProduct().getCurrency().getCurrencyId().toString());
		if (reexecutionRequest.getOriginProduct().getProductNemonic() != null)
			anOriginalRequest.addInputParam("@i_producto", ICTSTypes.SQLVARCHAR,
					reexecutionRequest.getOriginProduct().getProductNemonic());

		if (reexecutionRequest.getDestinationProduct().getProductNumber() != null)
			anOriginalRequest.addInputParam("@i_cta_des", ICTSTypes.SQLVARCHAR,
					reexecutionRequest.getDestinationProduct().getProductNumber());
		if (reexecutionRequest.getDestinationProduct().getProductType() != null)
			anOriginalRequest.addInputParam("@i_prod_des", ICTSTypes.SQLINT4,
					reexecutionRequest.getDestinationProduct().getProductType().toString());
		if (reexecutionRequest.getDestinationProduct().getCurrency() != null)
			anOriginalRequest.addInputParam("@i_mon_des", ICTSTypes.SQLINT2,
					reexecutionRequest.getDestinationProduct().getCurrency().getCurrencyId().toString());

		if (reexecutionRequest.getCliente().getLogin() != null)
			anOriginalRequest.addInputParam("@i_login", ICTSTypes.SQLVARCHAR,
					reexecutionRequest.getCliente().getLogin());
		if (reexecutionRequest.getTransferRequest().getAmmount() != null)
			anOriginalRequest.addInputParam("@i_val", ICTSTypes.SQLMONEY,
					reexecutionRequest.getTransferRequest().getAmmount().toString());
		if (reexecutionRequest.getTransferRequest().getDescriptionTransfer() != null)
			anOriginalRequest.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR,
					reexecutionRequest.getTransferRequest().getDescriptionTransfer());
		if (reexecutionRequest.getCliente().getId() != null)
			anOriginalRequest.addInputParam("@i_ente", ICTSTypes.SQLINT4, reexecutionRequest.getCliente().getId());

		anOriginalRequest.addOutputParam("@o_condicion", ICTSTypes.SYBINT4, "0");
		anOriginalRequest.addOutputParam("@o_autorizacion", ICTSTypes.SYBCHAR, "0");
		anOriginalRequest.addOutputParam("@o_referencia", ICTSTypes.SYBINT4, "0");
		anOriginalRequest.addOutputParam("@o_retorno", ICTSTypes.SYBINT4, "0");
		anOriginalRequest.addOutputParam("@o_ssn_branch", ICTSTypes.SYBINT4, "0");
		anOriginalRequest.addOutputParam("@o_clave", ICTSTypes.SYBINT4, "0");

		if (reexecutionRequest.getTrn() != null)
			anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SQLINT4, reexecutionRequest.getTrn());
		anOriginalRequest.addInputParam("@t_ejec", ICTSTypes.SQLVARCHAR, "R");
		if (reexecutionRequest.getRty() != null)
			anOriginalRequest.addInputParam("@t_rty", ICTSTypes.SQLVARCHAR, reexecutionRequest.getRty());
		if (reexecutionRequest.getSrv() != null)
			anOriginalRequest.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, reexecutionRequest.getSrv());

		anOriginalRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR,
				reexecutionRequest.getOriginalRequest().readValueParam("@s_user"));
		anOriginalRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR,
				reexecutionRequest.getOriginalRequest().readValueParam("@s_term"));
		anOriginalRequest.addInputParam("@s_ofi", ICTSTypes.SQLVARCHAR,
				reexecutionRequest.getOriginalRequest().readValueParam("@s_ofi"));
		anOriginalRequest.addInputParam("@s_rol", ICTSTypes.SQLVARCHAR,
				reexecutionRequest.getOriginalRequest().readValueParam("@s_rol"));
		anOriginalRequest.addInputParam("@ssn_branch", ICTSTypes.SQLINTN,
				reexecutionRequest.getOriginalRequest().readValueParam("@ssn_branch"));
		anOriginalRequest.addInputParam("@s_ssn_branch", ICTSTypes.SQLINT4,
				reexecutionRequest.getOriginalRequest().readValueParam("@s_ssn_branch"));
		anOriginalRequest.addInputParam("@s_sesn", ICTSTypes.SQLVARCHAR,
				reexecutionRequest.getOriginalRequest().readValueParam("@s_sesn"));
		anOriginalRequest.addInputParam("@s_date", ICTSTypes.SQLDATETIME,
				reexecutionRequest.getOriginalRequest().readValueParam("@s_date"));
		anOriginalRequest.addInputParam("@s_sesn", ICTSTypes.SQLVARCHAR,
				reexecutionRequest.getOriginalRequest().readValueParam("@s_sesn"));
		anOriginalRequest.addInputParam("@s_org", ICTSTypes.SQLVARCHAR,
				reexecutionRequest.getOriginalRequest().readValueParam("@s_org"));
		anOriginalRequest.addInputParam("@s_cliente", ICTSTypes.SQLINTN,
				reexecutionRequest.getOriginalRequest().readValueParam("@s_cliente"));
		anOriginalRequest.addInputParam("@s_perfil", ICTSTypes.SQLINT4,
				reexecutionRequest.getOriginalRequest().readValueParam("@s_perfil"));
		anOriginalRequest.addInputParam("@s_servicio", ICTSTypes.SQLINT4,
				reexecutionRequest.getOriginalRequest().readValueParam("@s_servicio"));
		anOriginalRequest.addInputParam("@s_filial", ICTSTypes.SQLINT4,
				reexecutionRequest.getOriginalRequest().readValueParam("@s_filial"));
		anOriginalRequest.addInputParam("@s_culture", ICTSTypes.SQLVARCHAR,
				reexecutionRequest.getOriginalRequest().readValueParam("@s_culture"));
		if (reexecutionRequest.getSrv() != null)
			anOriginalRequest.addInputParam("@s_lsrv", ICTSTypes.SQLVARCHAR, reexecutionRequest.getSrv());
		if (reexecutionRequest.getIn_line() != null)
			anOriginalRequest.addInputParam("@i_en_linea", ICTSTypes.SQLVARCHAR, reexecutionRequest.getIn_line());

		anOriginalRequest.setSpName(reexecutionRequest.getServiceName());

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Data enviada a guardar en reejecutador:" + anOriginalRequest.toString() + ""
					+ reentryPersister);

		// Se añade la transacción al Reentry y retorna un booleano
		Boolean reentryResponse = reentryPersister.addTransaction(anOriginalRequest);

		IProcedureResponse response = initProcedureResponse(anOriginalRequest);
		if (!reentryResponse) {
			response.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
			response.addMessage(1, "Ocurrio un error al tratar de registrar la transaccion en el Reentry CORE COBIS");
		} else {
			response.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, ICSP.SUCCESS);

		}
		return response;
	}

	private ReExecutionResponse transformResponse(IProcedureResponse response) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando Transformacion de Respuesta");
		ReExecutionResponse reExecutionResponse = new ReExecutionResponse();

		reExecutionResponse.setSuccess(response.getReturnCode() == 0);
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta" + reExecutionResponse);
		return reExecutionResponse;
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
}
