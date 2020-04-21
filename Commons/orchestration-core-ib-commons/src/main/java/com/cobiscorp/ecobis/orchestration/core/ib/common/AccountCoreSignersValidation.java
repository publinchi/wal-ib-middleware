/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.common;

import java.math.BigDecimal;
import java.util.Map;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.application.dtos.SignerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.SignerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;

/**
 * @author bborja
 * @since 5/11/2014
 * @version 1.0.0
 */
public class AccountCoreSignersValidation {
	protected static final String CLASS_NAME = " >-----> ";
	protected static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	private static ILogger logger = LogFactory.getLogger(AccountCoreSignersValidation.class);

	/**
	 * Validates account signers to authorize transaction.
	 *
	 * @param anOriginalRequest
	 * @param aBagSPJavaOrchestration
	 * @return
	 * @throws CTSServiceException
	 * @throws CTSInfrastructureException
	 */
	public static IProcedureResponse validateCoreSigners(ICoreService coreService, Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Iniciando consulta de Firmas Fisicas");

		IProcedureRequest originalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);

		try {
			SignerRequest signerRequest = new SignerRequest();
			Product product = new Product();
			Client client = new Client();

			if (!Utils.isNull(originalRequest.readParam("@i_ente"))) {
				client.setId(originalRequest.readValueParam("@i_ente"));
			}

			if (!Utils.isNull(originalRequest.readParam("@i_cta"))) {
				product.setProductNumber(originalRequest.readValueParam("@i_cta").toString());
				if (!Utils.isNullOrEmpty(originalRequest.readValueParam("@i_prod"))) {
					if (originalRequest.readValueParam("@i_prod").equals("3"))
						product.setProductNemonic("CTE");
					if (originalRequest.readValueParam("@i_prod").equals("4"))
						product.setProductNemonic("AHO");
				}
			}

			signerRequest.setUser(client);
			signerRequest.setOriginProduct(product);

			if (!Utils.isNull(originalRequest.readValueParam("@i_val"))) {
				signerRequest.setAmmount(new BigDecimal(originalRequest.readValueParam("@i_val").toString()));
			} else
				signerRequest.setAmmount(new BigDecimal(0));

			signerRequest.setChannelId(originalRequest.readValueFieldInHeader("servicio"));

			if (logger.isDebugEnabled())
				logger.logDebug(CLASS_NAME + "Consultando firmantes:" + signerRequest);

			SignerResponse signerResponse = coreService.getSignatureCondition(signerRequest);

			if (logger.isDebugEnabled())
				logger.logDebug(CLASS_NAME + "Respuesta firmantes:" + signerResponse);

			IProcedureResponse res = new ProcedureResponseAS();
			res.addParam("@o_condiciones_firmantes", ICTSTypes.SQLVARCHAR, 0, (signerResponse.getSigner() != null ? signerResponse.getSigner().getCondition() : ""));

			if (logger.isDebugEnabled())
				logger.logDebug(CLASS_NAME + "RESPUESTA CORE:" + res);
			res.setReturnCode(0);
			return res;
		} catch (CTSServiceException e) {
			logger.logError(CLASS_NAME + " ERROR EN EJECUCION DEL SERVICIO" + e.getMessage());

			throw e;
		} catch (CTSInfrastructureException e) {
			logger.logError(CLASS_NAME + " ERROR EN EJECUCION DEL SERVICIO" + e.getMessage());

			throw e;
		}
	}

}
