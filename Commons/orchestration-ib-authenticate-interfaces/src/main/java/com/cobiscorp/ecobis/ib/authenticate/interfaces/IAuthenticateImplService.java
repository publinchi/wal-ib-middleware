/**
 * Archivo: IAuthenticateService.java
 * Fecha: 9:02:05 AM
 *
 * Esta aplicacion es parte de los paquetes bancarios propiedad de COBISCORP.
 * Su uso no autorizado queda expresamente prohibido asi como cualquier
 * alteracion o agregado hecho por alguno de sus usuarios sin el debido
 * consentimiento por escrito de COBISCORP.
 * Este programa esta protegido por la ley de derechos de autor y por las
 * convenciones internacionales de propiedad intelectual. Su uso no
 * autorizado dara derecho a COBISCORP para obtener ordenes de secuestro
 * o retencion y para perseguir penalmente a los autores de cualquier infraccion.
 */
package com.cobiscorp.ecobis.ib.authenticate.interfaces;

import java.util.Map;

import cobiscorp.ecobis.commons.dto.ServiceRequestTO;
import cobiscorp.ecobis.commons.dto.ServiceResponseTO;

import com.cobiscorp.cobis.commons.components.ICOBISComponent;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.service.executor.external.authenticator.api.IManagerFacadeService;

/**
 * Interfaz que contiene los metodos de autenticacion para Tarjeta de
 * Coordenadas
 * 
 * @author dmorla
 *
 */
public interface IAuthenticateImplService extends ICOBISComponent, IManagerFacadeService 
		 {

	static final String PREFIX = "authenticateImpl";

	

	/**
	 * Metodo para validar un token
	 * 
	 * @param request
	 * @return
	 */
	public IProcedureResponse validateChallenge(String userId, String authType,
			String challenge);

	/**
	 * Metodo para obtener el estado actual de la autenticacion asi como el
	 * numero de intentos fallidos
	 * 
	 * @param request
	 * @return
	 */
	String getCurrentAuthType(String userId);

	/**
	 * Metodo para verificar si una transaccion tiene habilitada la doble
	 * autenticacion
	 * 
	 * @param request
	 * @return
	 */
	public boolean hasTransactionDoubleAuth(int trn);

	/**
	 * Esa: Metodo para recuperar los parametros de validacion en base a un
	 * valor enviado en un DTO
	 * 
	 * @param request
	 * @return
	 */
	Map<String, Object> getChallengeParams(ServiceRequestTO request);

	static final String CURR_TRX_AUTH_TYPE = PREFIX + ".type";
	static final String CURR_TRX_AUTH_RETRY = PREFIX + ".retry";
	static final String RESPONSE = PREFIX + ".response";

	/**
	 * Esa: Metodo para actualizar el estado de un usuario local.
	 * 
	 * @param userEntrust
	 *            ,status
	 */
	void updateBVUser(String user, boolean status);

	/**
	 * Esa: Metodo para verificar el numero maximo de intentos definidos para un
	 * proveedor de autenticacion externa true si ha llegado al valor maximo
	 * 
	 * @param retryCounter
	 */
	boolean validateRetryCounter(int retryCounter);

	/**
	 * Esa: Metodo para actualizar el valor de reintentos
	 * 
	 * @param userEntrust
	 *            ,retryNum,
	 */
	void updateAuthUserInfo(String userEntrust, int retryNum);

	/**
	 * Esa: Metodo para loggear en bdd el estado de ejecucion de la
	 * autenticacion con entrust
	 * 
	 * @param request
	 * @param response
	 */
	void logTransactionStatus(ServiceRequestTO request,
			ServiceResponseTO response);

	/**
	 * Esa: Metodo para recuperar el Id del usuario enviado en un DTO
	 * UserContext
	 * 
	 * @return
	 */
	public String getUserIdFromRequest(ServiceRequestTO request);

}
