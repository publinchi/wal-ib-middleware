package Utils;

public enum ConstantsMessageResponse {

	MSG000("Success",0), 
	MSG400141("The client is not affiliated in channels",400141),
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
