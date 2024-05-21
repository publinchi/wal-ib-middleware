package Utils;

public enum ConstantsMessageResponse {

	MSG000("Success",0), 
	MSG40020("Non-existent client ID",40020),
	MSG40021("The client does not have main means of contact",40021),
	MSG40022("You can only send one of the following fields at a time: externalCustomerId or cardId",40022),
	MSG40023("OTP must not be entered when the field externalCustomerId is sent",40023),
	MSG40024("You must send the field externalCustomerId or cardId",40024),
	MSG40025("OTP generation is currently not available for this user",40025),
	MSG40026("OTP must be entered when the field cardId is sent",40026),
	MSG204("The request has been completed successfully but your response has no content",204); 
	
	private String descriptionMessage;
	private int idMessage;
	
	private ConstantsMessageResponse (String descriptionMessage, int idMessage){
		this.descriptionMessage = descriptionMessage;
		this.idMessage = idMessage;
	}
	
	public String getDescriptionMessage() {
		return descriptionMessage;
	}

	public int getIdMessage() {
		return idMessage;
	}	
}
