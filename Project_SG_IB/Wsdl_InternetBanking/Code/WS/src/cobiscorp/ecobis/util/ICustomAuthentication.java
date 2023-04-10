
package cobiscorp.ecobis.util;

import cobiscorp.ecobis.commons.dto.ServiceRequestTO;
import cobiscorp.ecobis.commons.dto.ServiceResponseTO;
import cobiscorp.ecobis.bankingservicesoperations.service.security.SessionSecurityKey;

public interface ICustomAuthentication {
	public boolean authenticate(SessionSecurityKey wSessionSecurityKey);
}
	