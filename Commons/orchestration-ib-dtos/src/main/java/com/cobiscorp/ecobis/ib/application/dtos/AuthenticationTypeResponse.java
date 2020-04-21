package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.AuthenticationType;
import java.util.List;
/**
 * 
 */

/**
 * @author bborja
 * @since 19/5/2015
 * @version 1.0.0
 */
public class AuthenticationTypeResponse extends BaseResponse{
	

	private List<AuthenticationType> AuthenticationTypeCollection;

	/**
	 * @return the AuthenticationTypeCollection
	 */
	public List<AuthenticationType> getAuthenticationTypeCollection() {
		return AuthenticationTypeCollection;
	}

	/**
	 * @param AuthenticationTypeCollection the AuthenticationTypeCollection to set
	 */
	public void setAuthenticationTypeCollection(List<AuthenticationType> AuthenticationTypeCollection) {
		this.AuthenticationTypeCollection = AuthenticationTypeCollection;
	}

	
	
}
