package com.cobiscorp.ecobis.orchestration.core.ib.getCatalogue;

public class Util {

	public static final String  codeNullTable ="40003";
	public static final String messageNullTable="The value of catalogueTable is required";
	
	public static final String  codeNullUuid ="400324";
	public static final String messageNullUuid="x-request-id header is required";
	
	public static final String  codeNullDateTime ="400325";
	public static final String messageNullDateTime="x-end-user-request-date-time header is required";
	
	public static final String  codeNullIP ="400326";
	public static final String messageNullIP="x-end-user-ip header is required";
	
	public static final String  codeNullChannel ="400327";
	public static final String messageNullChannel="x-channel header is required";
	
	public static final String  codeEmptyCatalog ="40002";
	public static final String messagEmptyCatalog="There is no information for the catalog";
	
	public static final String codeInternalError="50000";
	public static final String messagInternalError="Internal server error";
	
}
