package com.cobiscorp.ecobis.ib.orchestration.base.utils.commons;

public class CardPAN
{
	public CardPAN(){
	}
	
	public String maskNumber(String number)
	{
		int length = number.length();
		int start = length / 4;
		int end = length - start;
		StringBuilder maskedNumber = new StringBuilder(number);
		
		// Reemplazar los caracteres en el rango determinado por 'X'
		for (int i = start; i < end; i++) {
		    maskedNumber.setCharAt(i, 'X');
		}
		return maskedNumber.toString();
	}

}
