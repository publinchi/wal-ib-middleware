package com.cobiscorp.ecobis.orchestration.core.ib.enrollment;

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
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.ecobis.ib.application.dtos.EnrollmentRequest;
import com.cobiscorp.ecobis.ib.application.dtos.EnrollmentResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceEnrollment;

/**
 * Plugin of enrollment
 * 
 * @since Oct 10, 2017
 * @author
 * @version 4.6.1.0
 * 
 */
@Component(name = "EnrollmentOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "EnrollmentOrchestrationCore"), @Property(name = "service.vendor", value = "COBISCORP"),
		@Property(name = "service.version", value = "4.6.1.0"), @Property(name = "service.identifier", value = "EnrollmentOrchestrationCore") })
public class EnrollmentOrchestrationCore extends SPJavaOrchestrationBase {

	private static final String OPERATION_VALIDATE = "V";
	private static final String OPERATION_INSERT = "I";
	private static final String CLASS_NAME = " >-----> ";
	private static ILogger logger = LogFactory.getLogger(EnrollmentOrchestrationCore.class);
	@Reference(referenceInterface = ICoreServiceEnrollment.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceEnrollment", unbind = "unbindCoreServiceEnrollment")
	private ICoreServiceEnrollment coreServiceEnrollment;

	protected void bindCoreServiceEnrollment(ICoreServiceEnrollment service) {
		coreServiceEnrollment = service;
	}

	protected void unbindCoreServiceEnrollment(ICoreServiceEnrollment service) {
		coreServiceEnrollment = null;
	}

	/**
	 * Read configuration of parent component
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + " loadConfiguration");
		}
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + " Inicia executeJavaOrchestration");
		}
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreServiceEnrollment", coreServiceEnrollment);
		Utils.validateComponentInstance(mapInterfaces);

		try {
			EnrollmentRequest wEnrollmentRequest = this.transformRequest(anOriginalRequest);
			aBagSPJavaOrchestration.put("wEnrollmentRequest", wEnrollmentRequest);

			EnrollmentResponse wEnrollmentResponse = new EnrollmentResponse();
			if (logger.isDebugEnabled()) {
				logger.logDebug("Enrollment Operation " + wEnrollmentRequest.getOperation());
			}

			if (OPERATION_INSERT.equals(wEnrollmentRequest.getOperation())) {
				wEnrollmentResponse = coreServiceEnrollment.executeEnrollment(aBagSPJavaOrchestration);
			} else if (OPERATION_VALIDATE.equals(wEnrollmentRequest.getOperation())) {
				wEnrollmentResponse = coreServiceEnrollment.validateCustomer(aBagSPJavaOrchestration);
			}
			aBagSPJavaOrchestration.put("wEnrollmentResponse", wEnrollmentResponse);
			return this.processResponse(anOriginalRequest, aBagSPJavaOrchestration);

		} catch (CTSServiceException e) {
			if (logger.isDebugEnabled()) {
				logger.logDebug("CTSServiceException ", e);
			}
		} catch (CTSInfrastructureException e) {
			if (logger.isDebugEnabled()) {
				logger.logDebug("CTSInfrastructureException ", e);
			}
		}
		return null;
	}

	/**
	 * Transforma objeto IProcedureRequest en EnrollmentRequest
	 * 
	 * @param anOriginalRequest
	 * @return
	 */
	private EnrollmentRequest transformRequest(IProcedureRequest anOriginalRequest) {
		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + " Inicia transformRequest");
		}

		EnrollmentRequest wRequest = new EnrollmentRequest();

		if (anOriginalRequest.readValueParam("@s_ofi") != null) {
			wRequest.setOfficeCode(Integer.parseInt(anOriginalRequest.readValueParam("@s_ofi")));
		}
		if (anOriginalRequest.readValueParam("@s_user") != null) {
			wRequest.setUserBv(anOriginalRequest.readValueParam("@s_user"));
		}
		if (anOriginalRequest.readValueParam("@s_term") != null) {
			wRequest.setTerminal(anOriginalRequest.readValueParam("@s_term"));
		}
		if (anOriginalRequest.readValueParam("@s_rol") != null) {
			wRequest.setRole(Integer.parseInt(anOriginalRequest.readValueParam("@s_rol")));
		}
		if (anOriginalRequest.readValueParam("@s_date") != null) {
			wRequest.setCreationDate(anOriginalRequest.readValueParam("@s_date"));
		}

		if (anOriginalRequest.readValueParam("@i_operacion") != null) {
			wRequest.setOperation(anOriginalRequest.readValueParam("@i_operacion"));
		}

		// CLIENTE
		Client wClient = new Client();
		if (anOriginalRequest.readValueParam("@i_fechanac") != null) {
			wClient.setBirthDate(anOriginalRequest.readValueParam("@i_fechanac"));
		}
		if (anOriginalRequest.readValueParam("@i_mail") != null) {
			wClient.setMail(anOriginalRequest.readValueParam("@i_mail"));
		}
		if (anOriginalRequest.readValueParam("@i_telefono") != null) {
			wClient.setPhone(anOriginalRequest.readValueParam("@i_telefono"));
		}
		if (anOriginalRequest.readValueParam("@i_mailOp") != null) {
			wClient.setMailOp(anOriginalRequest.readValueParam("@i_mailOp"));
		}
		if (anOriginalRequest.readValueParam("@i_telefonoOp") != null) {
			wClient.setPhoneOp(anOriginalRequest.readValueParam("@i_telefonoOp"));
		}
		if (anOriginalRequest.readValueParam("@i_login") != null) {
			wClient.setLogin(anOriginalRequest.readValueParam("@i_login"));
		}
		if (anOriginalRequest.readValueParam("@i_password") != null) {
			wClient.setPassword(anOriginalRequest.readValueParam("@i_password"));
		}

		// PRODUCTO
		Product wProduct = new Product();
		if (anOriginalRequest.readValueParam("@i_cuenta") != null) {
			wProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cuenta"));
		}
		if (anOriginalRequest.readValueParam("@i_tipo_producto") != null) {
			wProduct.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_tipo_producto")));
		}

		wRequest.setClient(wClient);
		wRequest.setProduct(wProduct);

		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + " Finaliza transformRequest");
		}
		return wRequest;
	}

	/**
	 * Transforma objeto EnrollmentResponse en IProcedureResponse
	 * 
	 * @param anOriginalRequest
	 * @return
	 */
	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + " Inicia processResponse");
		}

		IProcedureResponse wResponse = initProcedureResponse(anOriginalRequest);
		EnrollmentResponse wEnrollmentResponse = (EnrollmentResponse) aBagSPJavaOrchestration.get("wEnrollmentResponse");

		wResponse.setReturnCode(wEnrollmentResponse.getReturnCode());
		if (wEnrollmentResponse.getSuccess()) {
			wResponse.addParam("@o_referencia", ICTSTypes.SQLVARCHAR, 0, wEnrollmentResponse.getReferenceNumber());
			if (wEnrollmentResponse.getClient() != null) {
				wResponse.addParam("@o_ente_mis", ICTSTypes.SQLINT4, 0, wEnrollmentResponse.getClient().getId());
				wResponse.addParam("@o_nomlar", ICTSTypes.SQLVARCHAR, 0, wEnrollmentResponse.getClient().getCompleteName());
			}
		} else {
			wResponse = Utils.returnException(wEnrollmentResponse.getMessages());
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + " Finaliza processResponse");
		}
		return wResponse;
	}
}
