package com.cobiscorp.cobis.ib.middleware.installer.validators;
import com.izforge.izpack.installer.DataValidator;

public abstract class AbstractDataValidator implements DataValidator{
	
	private final String simpleName;
	public String errorId;

	public AbstractDataValidator() {
		simpleName = getClass().getSimpleName();
	}

	public boolean getDefaultAnswer() {
		return false;
	}

	public String getErrorMessageId() {
		if (this.getErrorId() != null && !"".equals(this.getErrorId().trim())) {
			return this.getErrorId();
		}
		return simpleName + ".errorMessage";
	}

	public String getWarningMessageId() {
		return simpleName + ".warningMessage";
	}
	
	public String getErrorId() {
		return errorId;
	}

	public void setErrorId(String errorId) {
		this.errorId = simpleName + "." + errorId;
	}
}
