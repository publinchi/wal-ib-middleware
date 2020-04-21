package com.cobiscorp.channels.cc.connector.dto;

import java.util.ArrayList;

public class MovementsResponse extends BaseResponse{
	private ArrayList<Movement> movementsList;
	
	public MovementsResponse(ArrayList<Movement> movementsList) {
		super();
		this.movementsList = movementsList;
	}

	public MovementsResponse() {
		super();
	}

	public ArrayList<Movement> getMovementsList() {
		return movementsList;
	}

	public void setMovementsList(ArrayList<Movement> movementsList) {
		this.movementsList = movementsList;
	}

	@Override
	public String toString() {
		return "MovementsResponse [movementsList=" + movementsList + "]";
	}

	
}
