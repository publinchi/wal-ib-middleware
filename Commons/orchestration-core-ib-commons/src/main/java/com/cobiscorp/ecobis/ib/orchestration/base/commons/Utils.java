package com.cobiscorp.ecobis.ib.orchestration.base.commons;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import com.cobiscorp.cobis.commons.exceptions.COBISInfrastructureRuntimeException;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IMessageBlock;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureRequestParam;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ErrorBlock;
import com.cobiscorp.cobis.cts.dtos.MessageBlock;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.BaseResponse;
import com.cobiscorp.ecobis.ib.application.dtos.BaseRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ConsolidateResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ProductConsolidate;

public class Utils {

	protected static final String CLASS_NAME = " >-----> ";
	private static ILogger logger = LogFactory.getLogger(Utils.class);
	protected static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";

	public static void removeOutputparams(IProcedureRequest request) {
		Object[] params = request.getParams().toArray();
		for (int i = params.length - 1; i >= 0; i--) {
			if (params[i] instanceof IProcedureRequestParam) {
				IProcedureRequestParam param = (IProcedureRequestParam) params[i];
				if (param.getIOType() == 1) {
					if (logger.isDebugEnabled())
						logger.logDebug(CLASS_NAME + "Removiendo parametro output " + param.getName());
					request.removeParam(param.getName());
				}
			}
		}
	}

	public static void removeParameters(IProcedureRequest request) {
		Object[] params = request.getParams().toArray();
		for (int i = params.length - 1; i >= 0; i--) {
			if (params[i] instanceof IProcedureRequestParam) {
				IProcedureRequestParam param = (IProcedureRequestParam) params[i];
				if (param.getName().indexOf("@s_") == -1) {
					if (logger.isDebugEnabled())
						logger.logDebug(CLASS_NAME + "Removiendo parametro " + param.getName());
					request.removeParam(param.getName());
				}
			}
		}
	}

	public static void copyParam(String wParamName, IProcedureRequest wIProcedureRequestSource, IProcedureRequest wIProcedureRequestResult) {
		IProcedureRequestParam wPRParam = wIProcedureRequestSource.readParam(wParamName);
		if (wPRParam != null) {
			wIProcedureRequestResult.addParam(wPRParam.getName(), wPRParam.getDataType(), wPRParam.getIOType(), wPRParam.getLen(), wPRParam.getValue());
		}

	}

	public static boolean flowError(String stepName, IProcedureResponse response) {
		if ((response == null) || (response.getReturnCode() != 0) || !validateErrorCode(response, 0)) {
			if (logger.isWarningEnabled())
				logger.logWarning(CLASS_NAME + "Error en el flujo, " + stepName + " retorno:" + (response != null ? response.getReturnCode() : "null"));
			return true;
		}
		return false;
	}

	public static boolean validateErrorCode(IProcedureResponse response, int code) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + " Validando existencia del codigo " + code + " en la respuesta :" + response.getProcedureResponseAsString());

		if ((response.hasError() == false) && (code == 0)) {
			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + " No existe mensajes de error");
			return true;
		}

		int messageNumber;

		Collection responseBlocks = response.getResponseBlocks();

		if (responseBlocks != null) {
			Iterator it = responseBlocks.iterator();

			while (it.hasNext()) {
				Object msgBlock = it.next();
				if (msgBlock instanceof IMessageBlock) {
					messageNumber = ((IMessageBlock) msgBlock).getMessageNumber();
					if (messageNumber == code) {
						if (logger.isInfoEnabled())
							logger.logInfo(CLASS_NAME + " Existe el c�digo " + code + " en la respuesta");

						return true;
					}
				}
			}
		}
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + " No existe el c�digo " + code + " en la respuesta");
		return false;
	}

	public static void addNullableInputParamFromRequest(IProcedureRequest request, IProcedureRequest newRequest, String newParamName, String orginalParamName) {
		if ((request.readValueParam(orginalParamName) != null) && !request.readValueParam(orginalParamName).equals("")) {
			newRequest.addInputParam(newParamName, request.readParam(orginalParamName).getDataType(), request.readValueParam(orginalParamName));
		} else {
			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + "::El par�metro " + orginalParamName + " no existe en el request");
		}
	}

	public static void addResultSetDataAsParam(Integer resultsetNumber, IProcedureResponse procedureResponse, String type) {
		String nameParam = "";
		String valueParam = "";
		int typeParam = 0;
		boolean addParam = false;

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + " ProcedureResponse que se va a pasar como parametros sus resultSets: " + procedureResponse.getProcedureResponseAsString());

		IResultSetBlock resultSet = procedureResponse.getResultSet(resultsetNumber);

		if (resultSet == null) {
			if (logger.isDebugEnabled())
				logger.logDebug(CLASS_NAME + "Resultset vacio, se retorna ProcedureResponse original");
			return;
		}

		IResultSetRow[] rowsTemp = resultSet.getData().getRowsAsArray();
		IResultSetHeaderColumn[] columns = resultSet.getMetaData().getColumnsMetaDataAsArray();

		for (int i = 0; i < rowsTemp.length; ++i) {
			IResultSetRowColumnData[] rows = rowsTemp[i].getColumnsAsArray();
			for (int j = 0; j < rows.length; ++j) {
				if ("V".equals(type)) {
					if (j == 0) {
						nameParam = rows[j].getValue();
					} else if (j == 1) {
						valueParam = rows[j].getValue();
						typeParam = columns[j].getType();
						addParam = true;
					}
				} else if ("H".equals(type)) {
					nameParam = columns[j].getName();
					typeParam = columns[j].getType();
					valueParam = rows[j].getValue();
					addParam = true;
				} else {
					continue;
				}

				if (addParam) {
					if (logger.isDebugEnabled())
						logger.logDebug(CLASS_NAME + " agregando resulset como parametro: nombre:" + nameParam + " tipo: " + typeParam + " valor:" + valueParam);

					int length = 0;
					if (valueParam != null)
						length = valueParam.length();
					procedureResponse.addParam(nameParam, typeParam, length, valueParam);
					addParam = false;
				}
			}
		}
	}

	/**
	 * Agrega un parametro del response como input en el request, si es que este existe
	 *
	 * @param request
	 * @param response
	 * @param inParamName
	 * @param outParamName
	 */
	public static void addNullableInputParamFromResponse(IProcedureRequest request, IProcedureResponse response, String inParamName, String outParamName) {
		if ((response.readValueParam(outParamName) != null) && !response.readValueParam(outParamName).equals("")) {
			request.addInputParam(inParamName, response.readParam(outParamName).getDataType(), response.readValueParam(outParamName));
		} else {
			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + "::El par�metro " + outParamName + " no existe en el response");
		}

	}

	public static void removeParam(IProcedureRequest request, String name) {
		if (request.readParam(name) != null) {
			request.removeParam(name);
		}
	}

	public static Boolean isNullOrEmty(String obj) {
		if (obj == null)
			return true;
		if (obj.isEmpty())
			return true;
		return false;
	}

	public static Boolean isNullOrEmty(Integer obj) {
		if (obj == null)
			return true;
		if (obj == 0)
			return true;
		return false;
	}

	public static Boolean isNull(Object obj) {
		if (obj == null)
			return true;
		return false;
	}

	public static void validateComponentInstance(Map<String, Object> objects) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Realizando Validacion de Injeccion de Dependencias");

		IProcedureResponse response = new ProcedureResponseAS();
		response.setReturnCode(0);

		for (Map.Entry<String, Object> entry : objects.entrySet()) {
			String key = entry.getKey();
			Object obj = entry.getValue();
			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + "Validando componente:" + key);
			if (obj == null) {
				throw new COBISInfrastructureRuntimeException("No se ha encontrado implementacion del componente:" + key);
			}
		}

	}

	public static IProcedureResponse returnExceptionService(IProcedureRequest anOrginalRequest, Exception e) {
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "ERROR EXECUTING SERVICE request:" + anOrginalRequest);
		e.getMessage();

		IProcedureResponse wProcedureRespFinal = new ProcedureResponseAS();
		ErrorBlock eb = new ErrorBlock(-1, "Service is not available");
		wProcedureRespFinal.addResponseBlock(eb);
		wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
		wProcedureRespFinal.setReturnCode(-1);
		return wProcedureRespFinal;
	}

	public static IProcedureResponse returnException(String messageError) {
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "ERROR EXECUTING SERVICE Ito " + messageError);

		IProcedureResponse wProcedureRespFinal = new ProcedureResponseAS();
		ErrorBlock eb = new ErrorBlock(0, "ERROR DE INFRAESTRUCTURA");
		wProcedureRespFinal.addResponseBlock(eb);
		wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
		wProcedureRespFinal.setReturnCode(-1);
		wProcedureRespFinal.addMessage(-1, messageError);

		new IllegalArgumentException(messageError).printStackTrace();
		return wProcedureRespFinal;

	}

	public static IProcedureResponse returnException(int returnCode, String messageError) {
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "ERROR EXECUTING SERVICE MessageError: "+ messageError);

		IProcedureResponse wProcedureRespFinal = new ProcedureResponseAS();
		ErrorBlock eb = new ErrorBlock(-1, "ERROR DE INFRAESTRUCTURA");
		wProcedureRespFinal.addResponseBlock(eb);
		wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
		wProcedureRespFinal.setReturnCode(returnCode);
		wProcedureRespFinal.addMessage(returnCode, messageError);

		new IllegalArgumentException(messageError).printStackTrace();
		return wProcedureRespFinal;

	}

	public static IProcedureResponse returnException(Message[] messagesError) {
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "ERROR EXECUTING SERVICE MessageError: "+ messagesError.toString());

		IProcedureResponse wProcedureRespFinal = new ProcedureResponseAS();
		ErrorBlock eb = new ErrorBlock(-1, "ERROR EN EJECUCIÓN");
		wProcedureRespFinal.addResponseBlock(eb);
		wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
		wProcedureRespFinal.setReturnCode(-1);
		StringBuilder sb = new StringBuilder();

		for (Message message : messagesError) {

			logger.logError(CLASS_NAME + "ERROR EXECUTING SERVICE " + message.getDescription());
			logger.logError(CLASS_NAME + "ERROR EXECUTING SERVICE " + message.getCode());

			if (Integer.parseInt(message.getCode()) != 0) {
				wProcedureRespFinal.addMessage(Integer.parseInt(message.getCode()), message.getDescription());
				sb.append(message.getDescription()).append("\n");
			}
		}
		new IllegalArgumentException(sb.toString()).printStackTrace();
		return wProcedureRespFinal;
	}

	public static Message[] returnArrayMessage(IProcedureResponse response) {
		ArrayList a = (ArrayList) response.getMessages();
		Message[] msgs = new Message[a.size()];

		for (int i = 0; i < a.size(); i++) {
			IMessageBlock msg = (IMessageBlock) a.get(i);
			Message mensaje = new Message();
			mensaje.setCode(String.valueOf(msg.getMessageNumber()));
			mensaje.setDescription(msg.getMessageText());
			msgs[i] = mensaje;
		}
		return msgs;
	}

	public static void addInputParam(IProcedureRequest request, String wParamName, int wTypeParam, String wParamValue) {

		if (request.readParam(wParamName) != null)
			request.setValueParam(wParamName, wParamValue);
		else
			request.addParam(wParamName, wTypeParam, 0, wParamValue.length(), wParamValue);

	}

	public static void addOutputParam(IProcedureRequest request, String wParamName, int wTypeParam, String wParamValue) {

		if (request.readParam(wParamName) != null)
			request.setValueParam(wParamName, wParamValue);
		else
			request.addParam(wParamName, wTypeParam, 1, wParamValue.length(), wParamValue);

	}

	// unificacion de capas utils
	/**
	 * Set properties IProcedureResponse to BaseResponse
	 *
	 * @param base
	 * @param response
	 */
	public static void transformIprocedureResponseToBaseResponse(BaseResponse base, IProcedureResponse response) {
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Respuesta  a validar" + response);
		Iterator itMessages = response.getMessages().iterator();
		IMessageBlock messageBlock = null;
		boolean outputParams = false;
		while (itMessages.hasNext()) {
			messageBlock = (IMessageBlock) itMessages.next();
			break;
		}

		Message message = new Message();
		base.setSuccess(!response.hasError());

		if (response.getMessages().size() > 0) {
			message.setCode("1");
			message.setDescription(messageBlock.getMessageText());

			base.setMessage(message);
		}
	}

	/**
	 * Set properties BaseResponse to IProcedureResponse
	 *
	 * @param base
	 * @param response
	 */
	public static void transformBaseResponseToIprocedureResponse(BaseResponse base, IProcedureResponse response) {
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Respuesta  a validar" + base);
		if (base.getSuccess() && base.getMessage() == null) {
			response.setReturnCode(0);
		} else {
			Message message = base.getMessage();

			MessageBlock mb = new MessageBlock();
			mb.setMessageText(message.getDescription());
			mb.setMessageNumber(Integer.parseInt(message.getCode()));

			response.addResponseBlock(mb);
			response.setReturnCode(Integer.parseInt(message.getCode() != "0" ? message.getCode() : "-1"));
		}
	}

	public static Date formatDate(String date) {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		try {
			if (date != null)
				return df.parse(date);
		} catch (ParseException e) {
			logger.logError("ERROR " + e);
			e.getMessage();
		}
		return null;
	}

	public static String formatDateToString(Date date) {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		String formattedDate = "";
		if (date != null)
			formattedDate = df.format(date);
		// logger.logInfo("dd-MMM-yyyy date is ==>" + formattedDate);
		return formattedDate;

	}

	// GCO-Se agrega mapSPJavaOrchestration para manejo de errores
	public static IProcedureResponse transformConsolidateResponseToIProcedureResponse(ConsolidateResponse consolidateResponse, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();

		// GCO-Manejo de Errores
		if (consolidateResponse.getReturnCode() != 0) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, Utils.returnException(consolidateResponse.getMessages())); // COLOCA ERRORES COMO
																															// RESPONSE DE LA
																															// TRANSACCIÓN
			wProcedureResponse = Utils.returnException(consolidateResponse.getMessages());
			if (logger.isDebugEnabled()) {
				logger.logDebug(CLASS_NAME + "*** Error al transformarServicio GetProductos-ErrorCode:" + consolidateResponse.getReturnCode());
			}
		} else {
			// Agregar Header
			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("productId", ICTSTypes.SQLINT2, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("productDescription", ICTSTypes.SQLVARCHAR, 100));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("productAbbreviation", ICTSTypes.SQLVARCHAR, 3));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("productNumber", ICTSTypes.SQLVARCHAR, 24));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("productBalance", ICTSTypes.SQLMONEY, 0));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("value2", ICTSTypes.SQLVARCHAR, 32));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("value3", ICTSTypes.SQLVARCHAR, 32));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("value4", ICTSTypes.SQLVARCHAR, 32));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("currencyId", ICTSTypes.SQLINT2, 0));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("currencyNemonic", ICTSTypes.SQLVARCHAR, 3));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("currencyName", ICTSTypes.SQLVARCHAR, 50));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("productAlias", ICTSTypes.SQLVARCHAR, 50));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("bankProductId", ICTSTypes.SQLINT2, 0));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("bankProduct", ICTSTypes.SQLVARCHAR, 50));
			if (logger.isDebugEnabled()) {
				logger.logDebug("ARMANDO RESPONSE");
			}
			// for (ProductConsolidate product : consolidateResponse.getProductCollection()) {
			int sizeCollection = consolidateResponse.getProductCollection().size();
			for (int k = 0; k < sizeCollection; k++) {
				ProductConsolidate product = consolidateResponse.getProductCollection().get(k);

				IResultSetRow row = new ResultSetRow();

				row.addRowData(1, new ResultSetRowColumnData(false, product.getProduct().getProductType().toString())); // productType
				row.addRowData(2, new ResultSetRowColumnData(false, product.getProduct().getProductDescription())); // productName
				row.addRowData(3, new ResultSetRowColumnData(false, product.getProduct().getProductNemonic())); // ProductNemonic
				row.addRowData(4, new ResultSetRowColumnData(false, product.getProduct().getProductNumber())); // ProductNumber
				row.addRowData(5, new ResultSetRowColumnData(false, product.getBalance().getAvailableBalance().toString())); // productBalance

				row.addRowData(6, new ResultSetRowColumnData(false, "")); // value2
				row.addRowData(7, new ResultSetRowColumnData(false, "")); // value3
				row.addRowData(8, new ResultSetRowColumnData(false, "")); // value3

				if (product.getCurrency().getCurrencyId() != null)
					row.addRowData(9, new ResultSetRowColumnData(false, product.getCurrency().getCurrencyId().toString())); // currencyId
				else
					row.addRowData(9, new ResultSetRowColumnData(false, "0")); // currencyId

				if (product.getCurrency().getCurrencyNemonic() != null)
					row.addRowData(10, new ResultSetRowColumnData(false, product.getCurrency().getCurrencyNemonic())); // currencyNemonic
				else
					row.addRowData(10, new ResultSetRowColumnData(false, "USD")); // currencyNemonic

				if (product.getCurrency().getCurrencyDescription() != null)
					row.addRowData(11, new ResultSetRowColumnData(false, product.getCurrency().getCurrencyDescription())); // currencyName
				else
					row.addRowData(11, new ResultSetRowColumnData(false, "DOLARES")); // currencyName
				row.addRowData(12, new ResultSetRowColumnData(false, product.getProduct().getProductAlias())); // productAlias
				row.addRowData(13, new ResultSetRowColumnData(false, String.valueOf(product.getProduct().getBankProductId()))); // productAlias
				row.addRowData(14, new ResultSetRowColumnData(false, product.getProduct().getBankProduct())); // productAlias

				data.addRow(row);
			}

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

			wProcedureResponse.addResponseBlock(resultBlock);
			wProcedureResponse.setReturnCode(0);
			wProcedureResponse.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "1");
		}
		wProcedureResponse.setReturnCode(consolidateResponse.getReturnCode());
		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;

	}

	// GCO-Se agrega mapSPJavaOrchestration para menejo de errores
	public static IProcedureResponse transformConsolidateResponseToIProcedureResponseAccounts(ConsolidateResponse consolidateResponse, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();

		// GCO-Manejo de erroresconsolidateResponse
		if (consolidateResponse.getReturnCode() != 0) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, Utils.returnException(consolidateResponse.getMessages())); // COLOCA ERRORES COMO
																															// RESPONSE DE LA
																															// TRANSACCIÓN
			wProcedureResponse = Utils.returnException(consolidateResponse.getMessages());
			if (logger.isDebugEnabled()) {
				logger.logDebug(CLASS_NAME + "*** Error al transformarServicio GetAccounts-ErrorCode:" + consolidateResponse.getReturnCode());
			}
		} else {

			// Agregar Header
			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("productNumber", ICTSTypes.SQLVARCHAR, 20)); // 1
			metaData.addColumnMetaData(new ResultSetHeaderColumn("currencyId", ICTSTypes.SQLINT2, 100));// 2
			metaData.addColumnMetaData(new ResultSetHeaderColumn("productId", ICTSTypes.SQLINT1, 3));// 3
			metaData.addColumnMetaData(new ResultSetHeaderColumn("productAlias", ICTSTypes.SQLVARCHAR, 24));// 4
			metaData.addColumnMetaData(new ResultSetHeaderColumn("currencyName", ICTSTypes.SQLVARCHAR, 0));// 5
			metaData.addColumnMetaData(new ResultSetHeaderColumn("productName", ICTSTypes.SQLVARCHAR, 32));// 6
			metaData.addColumnMetaData(new ResultSetHeaderColumn("productAbbreviation", ICTSTypes.SQLVARCHAR, 32));// 7
			metaData.addColumnMetaData(new ResultSetHeaderColumn("productBalance", ICTSTypes.SQLMONEY, 32));// 8
			metaData.addColumnMetaData(new ResultSetHeaderColumn("accountingBalance", ICTSTypes.SQLMONEY, 0));// 9
			metaData.addColumnMetaData(new ResultSetHeaderColumn("currencyNemonic", ICTSTypes.SQLVARCHAR, 3));// 10
			metaData.addColumnMetaData(new ResultSetHeaderColumn("expirationDate", ICTSTypes.SQLVARCHAR, 15));// 11 ITO
			metaData.addColumnMetaData(new ResultSetHeaderColumn("rate", ICTSTypes.SQLVARCHAR, 24));// 12 ITO
			metaData.addColumnMetaData(new ResultSetHeaderColumn("documentType", ICTSTypes.SQLVARCHAR, 50));// 13
			metaData.addColumnMetaData(new ResultSetHeaderColumn("document", ICTSTypes.SQLVARCHAR, 50));// 14
			metaData.addColumnMetaData(new ResultSetHeaderColumn("bankProductId", ICTSTypes.SQLINT2, 0));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("bankProduct", ICTSTypes.SQLVARCHAR, 50));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("clabeInterbank", ICTSTypes.SQLVARCHAR, 50));
			if (logger.isDebugEnabled()) {
				logger.logDebug("ARMANDO RESPONSE");
			}

			int sizeCollection = consolidateResponse.getProductCollection().size();
			for (int i = 0; i < sizeCollection; i++) {
				ProductConsolidate product = consolidateResponse.getProductCollection().get(i);

				IResultSetRow row = new ResultSetRow();

				row.addRowData(1, new ResultSetRowColumnData(false, product.getProduct().getProductNumber())); // ProductNumber
				if (product.getCurrency().getCurrencyId() != null)
					row.addRowData(2, new ResultSetRowColumnData(false, product.getCurrency().getCurrencyId().toString())); // currencyId
				else
					row.addRowData(2, new ResultSetRowColumnData(false, "0")); // currencyId

				row.addRowData(3, new ResultSetRowColumnData(false, product.getProduct().getProductType().toString())); // productType

				row.addRowData(4, new ResultSetRowColumnData(false, product.getProduct().getProductAlias())); // productAlias
				if (product.getCurrency().getCurrencyDescription() != null)
					row.addRowData(5, new ResultSetRowColumnData(false, product.getCurrency().getCurrencyDescription())); // currencyName
				else
					row.addRowData(5, new ResultSetRowColumnData(false, "DOLARES")); // currencyName

				row.addRowData(6, new ResultSetRowColumnData(false, product.getProduct().getProductDescription())); // productName
				row.addRowData(7, new ResultSetRowColumnData(false, product.getProduct().getProductNemonic())); // productAbbreviation

				row.addRowData(8, new ResultSetRowColumnData(false, product.getBalance().getAvailableBalance().toString())); // productBalance

				if (product.getBalance().getAccountingBalance() != null)
					row.addRowData(9, new ResultSetRowColumnData(false, product.getBalance().getAccountingBalance().toString())); // productBalance
				else
					row.addRowData(9, new ResultSetRowColumnData(false, product.getBalance().getAvailableBalance().toString())); // productBalance

				if (product.getCurrency().getCurrencyNemonic() != null)
					row.addRowData(10, new ResultSetRowColumnData(false, product.getCurrency().getCurrencyNemonic())); // currencyNemonic
				else
					row.addRowData(10, new ResultSetRowColumnData(false, "USD")); // currencyNemonic

				row.addRowData(11, new ResultSetRowColumnData(false, product.getBalance().getExpirationDate().toString())); // Date
				row.addRowData(12, new ResultSetRowColumnData(false, product.getBalance().getRate())); // type

				row.addRowData(13, new ResultSetRowColumnData(false, String.valueOf(product.getProduct().getBankProductId()))); // productAlias
				row.addRowData(14, new ResultSetRowColumnData(false, product.getProduct().getBankProduct())); // productAlias
				row.addRowData(15, new ResultSetRowColumnData(false, product.getProduct().getClabeInterbank()));
				data.addRow(row);
			}

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

			wProcedureResponse.addResponseBlock(resultBlock);
			wProcedureResponse.setReturnCode(0);
			wProcedureResponse.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "1");
		}

		wProcedureResponse.setReturnCode(consolidateResponse.getReturnCode());
		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;

	}

	public static BaseRequest setSessionParameters(BaseRequest request, IProcedureRequest anOriginalRequest) {
		if (!Utils.isNull(anOriginalRequest.readValueFieldInHeader("ofi")))
			request.setOfficeCode(Integer.parseInt(anOriginalRequest.readValueFieldInHeader("ofi")));
		else
			request.setOfficeCode(0);

		if (!Utils.isNull(anOriginalRequest.readValueFieldInHeader("rol")))
			request.setRole(Integer.parseInt(anOriginalRequest.readValueFieldInHeader("rol")));
		else
			request.setRole(1);

		if (!Utils.isNull(anOriginalRequest.readValueFieldInHeader("user")))
			request.setUserBv(anOriginalRequest.readValueFieldInHeader("user"));
		else
			request.setUserBv("usuariobv");

		if (!Utils.isNull(anOriginalRequest.readValueFieldInHeader("term")))
			request.setTerminal(anOriginalRequest.readValueFieldInHeader("term"));
		else
			request.setTerminal("TERMX");

		return request;

	}

	// GCO-Se copia metodo desde proyecto de DTO para el manejor de errores en la implementacion de consulta de pago de prestamos
	public static Boolean isNullOrEmpty(String obj) {
		if (obj == null)
			return true;
		if (obj.isEmpty())
			return true;
		return false;
	}

	public static int getTransactionMenu(int trn) {

		int returnTrn = 0;
		switch (trn) {
		case 1800009:
			returnTrn = 18056;
			break;
		case 1800012:

			returnTrn = 18059;
			break;
		case 1800015:

			returnTrn = 18844;
			break;
		case 1800016:

			returnTrn = 18862;
			break;

		case 1800025:
			returnTrn = 18057;
			break;

		case 1801035:
			returnTrn = 1801035;
			break;

		case 1801025:
			returnTrn = 1801025;
			break;

		case 1875050:

			returnTrn = 1801032;
			break;
		default:
			returnTrn = trn;
			break;

		}
		return returnTrn;
	}

}
