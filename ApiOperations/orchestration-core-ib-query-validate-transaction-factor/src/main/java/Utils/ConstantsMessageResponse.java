package Utils;

public enum ConstantsMessageResponse {

	MSG000("Success",0), 
	MSG40020("Non-existent client ID",40020),
	MSG40021("The client does not have main means of contact",40021),
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
