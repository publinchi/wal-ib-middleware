
package cobiscorp.ecobis.util;

import cobiscorp.ecobis.commons.dto.ServiceRequestTO;
import cobiscorp.ecobis.commons.dto.ServiceResponseTO;

public interface ICustomCode {
	public String getErrorCode();
	public String getErrorMessage();
	public void beforeExecute(String operationName, ServiceRequestTO serviceRequestTO, Object... request);
	public void afterExecute(String operationName, ServiceResponseTO serviceResponse, Object response);
	public Object onError(String operationName, Object exception, Object response);
}
	