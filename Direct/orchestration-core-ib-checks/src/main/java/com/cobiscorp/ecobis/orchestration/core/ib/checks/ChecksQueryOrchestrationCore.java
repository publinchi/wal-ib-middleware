package com.cobiscorp.ecobis.orchestration.core.ib.checks;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.CheckRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CheckResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Check;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceChecksQuery;

/**
 * 
 * @author gyagual
 *
 */
@Component(name = "ChecksQueryOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "ChecksQueryOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ChecksQueryOrchestrationCore") })
public class ChecksQueryOrchestrationCore extends QueryBaseTemplate {

	@Reference(referenceInterface = ICoreServiceChecksQuery.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceChecksQuery coreServiceCheck;
	ILogger logger = this.getLogger();

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreService(ICoreServiceChecksQuery service) {
		coreServiceCheck = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreService(ICoreServiceChecksQuery service) {
		coreServiceCheck = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	protected IProcedureResponse executeQuery(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {

		String messageError = null;
		String messageLog = null;
		String queryName = null;
		String wCheckNumber = null;
		String wOpcion = "B"; // default Busqueda

		CheckResponse aCheckResponse = null;
		if (request.readValueParam("@i_num_cheque") != null)
			wOpcion = "V"; // Validacion

		try {

			if (wOpcion.toString().equals("B")) {
				CheckRequest aCheckRequest = transformCheckRequest(request.clone());
				wCheckNumber = request.readValueParam("@i_chq").toString();

				messageError = "get: ERROR EXECUTING SERVICE";
				messageLog = "getChecks " + aCheckRequest.getProductNumber().getProductNumber();
				queryName = "getChecks";
				aCheckRequest.setOriginalRequest(request);
				if (wCheckNumber.equals("0"))
					aCheckResponse = coreServiceCheck.getChecks(aCheckRequest);
				else
					aCheckResponse = coreServiceCheck.getChecksbyNumber(aCheckRequest);
			} else {
				CheckRequest aCheckRequest = transformValidateCheckRequest(request.clone());
				messageLog = "validateChecks " + aCheckRequest.getProductId().getProductName();
				queryName = "validateChecks";
				aCheckResponse = coreServiceCheck.validateCheckStatus(aCheckRequest);

			}
		} catch (CTSServiceException e) {
			e.printStackTrace();
			if (logger.isDebugEnabled())
				logger.logDebug(messageError);
			return null;
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
			if (logger.isDebugEnabled())
				logger.logDebug(messageError);
			return null;
		}

		aBagSPJavaOrchestration.put(LOG_MESSAGE, messageLog);
		aBagSPJavaOrchestration.put(QUERY_NAME, queryName);

		if (wOpcion.toString().equals("B")) {
			if (wCheckNumber.equals("0"))
				return transformProcedureResponse(aCheckResponse, aBagSPJavaOrchestration);
			else
				return transformProcedureResponseByNumber(aCheckResponse, aBagSPJavaOrchestration);
		} else {
			return transformValidationResponse(aCheckResponse, aBagSPJavaOrchestration);
		}

		// return null;
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		// Valida Inyección de dependencias
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreServiceCheck", coreServiceCheck);
		Utils.validateComponentInstance(mapInterfaces);
		try {
			if (logger.isDebugEnabled())
				logger.logDebug("INICIO> anOrginalRequest" + anOrginalRequest);
			executeStepsQueryBase(anOrginalRequest, aBagSPJavaOrchestration);
			if (logger.isDebugEnabled())
				logger.logDebug("executeJavaOrchestration");
			return processResponse(anOrginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		} catch (CTSInfrastructureException e) {

			return Utils.returnExceptionService(anOrginalRequest, e);
		}

	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = (IProcedureResponse) aBagSPJavaOrchestration.get("RESPONSE_TRANSACTION");
		return response;

	}

	/******************
	 * Transformación de ProcedureRequest a CheckbookRequest
	 ********************/

	private CheckRequest transformCheckRequest(IProcedureRequest aRequest) {
		CheckRequest wCheckRequest = new CheckRequest();
		Product wProduct = new Product();
		Currency wCurrency = new Currency();
		Check wCheck = new Check();
		String wCheckNumber;

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;
		wCheckNumber = aRequest.readValueParam("@i_chq").toString();
		messageError = aRequest.readValueParam("@i_cta") == null ? " - @i_cta can't be null" : "";
		if (wCheckNumber.equals("0"))//
		{
			messageError += aRequest.readValueParam("@i_fecha_ini") == null
					&& aRequest.readValueParam("@i_fecha_fin") == null && aRequest.readValueParam("@i_chq_ini") == null
					&& aRequest.readValueParam("@i_chq_fin") == null ? "@i_fecha and @i_chq  can't be null" : "";

			// messageError += aRequest.readValueParam("@i_fecha_ini") == null ?
			// " - @i_fecha_ini can't be null":"";
			// messageError += aRequest.readValueParam("@i_fecha_fin") == null ?
			// " - @i_fecha_fin can't be null":"";
			// messageError += aRequest.readValueParam("@i_monto_ini") == null ?
			// " - @i_monto_ini can't be null":"";
			// messageError += aRequest.readValueParam("@i_monto_fin") == null ?
			// " - @i_monto_fin can't be null":"";
			// messageError += aRequest.readValueParam("@i_chq_ini") == null ? "
			// - @i_chq_ini can't be null":"";
			// messageError += aRequest.readValueParam("@i_chq_fin") == null ? "
			// - @i_chq_fin can't be null":"";
		}
		messageError += aRequest.readValueParam("@i_chq") == null ? " - @i_chq can't be null" : "";
		messageError += aRequest.readValueParam("@i_login") == null ? " - @i_login can't be null" : "";
		messageError += aRequest.readValueParam("@i_formato_fecha") == null ? " - @i_formato_fecha can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		wProduct.setProductNumber(aRequest.readValueParam("@i_cta"));
		wProduct.setProductType(Integer.parseInt(aRequest.readValueParam("@i_prod")));
		wCurrency.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon")));
		wCheck.setCheckNumber(aRequest.readValueParam("@i_chq"));

		wCheckRequest.setEjec(aRequest.readValueParam("@t_ejec"));
		wCheckRequest.setRty(aRequest.readValueParam("@t_rty"));
		wCheckRequest.setCodeTransactionalIdentifier("18328");
		wCheckRequest.setProductId(wProduct);
		wCheckRequest.setCurrency(wCurrency);
		wCheckRequest.setUserName(aRequest.readValueParam("@i_login"));
		wCheckRequest.setProductNumber(wProduct);
		wCheckRequest.setCriteria(aRequest.readValueParam("@i_opcion"));
		if (wCheckNumber.equals("0")) {
			if (aRequest.readValueParam("@i_monto_ini") != null) {
				wCheckRequest.setInitialAmount(new BigDecimal(aRequest.readValueParam("@i_monto_ini")));
			}
			if (aRequest.readValueParam("@i_monto_fin") != null) {
				wCheckRequest.setFinalAmount(new BigDecimal(aRequest.readValueParam("@i_monto_fin")));
			}
			wCheckRequest.setStringInitialDate(aRequest.readValueParam("@i_fecha_ini"));
			wCheckRequest.setStringFinalDate(aRequest.readValueParam("@i_fecha_fin"));
			wCheckRequest.setInitialCheck(aRequest.readValueParam("@i_chq_ini"));
			wCheckRequest.setFinalCheck(aRequest.readValueParam("@i_chq_fin"));
		}
		wCheckRequest.setStatusCheck(aRequest.readValueParam("@i_chq_estado"));
		wCheckRequest.setDateFormatId(Integer.parseInt(aRequest.readValueParam("@i_formato_fecha")));
		wCheckRequest.setCheckNumber(wCheck);

		return wCheckRequest;
	}

	private CheckRequest transformValidateCheckRequest(IProcedureRequest aRequest) {
		CheckRequest wCheckRequest = new CheckRequest();
		Product wProduct = new Product();
		Check wCheck = new Check();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;

		messageError = aRequest.readValueParam("@i_cta") == null ? " - @i_cta can't be null" : "";
		messageError += aRequest.readValueParam("@i_num_cheque") == null ? " - @i_num_cheque can't be null" : "";
		messageError += aRequest.readValueParam("@i_desde") == null ? " - @i_desde can't be null" : "";
		messageError += aRequest.readValueParam("@i_estado") == null ? " - @i_estado can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		wProduct.setProductNumber(aRequest.readValueParam("@i_cta"));
		wCheck.setCheckNumber(aRequest.readValueParam("@i_desde"));
		wCheck.setStatus(aRequest.readValueParam("@i_estado"));
		wCheckRequest.setFinalCheck(aRequest.readValueParam("@i_num_cheque"));
		wCheckRequest.setProductId(wProduct);
		wCheckRequest.setCheckNumber(wCheck);

		return wCheckRequest;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/

	private IProcedureResponse transformValidationResponse(CheckResponse aCheckResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		if (aCheckResponse.getReturnCode() != 0) {
			return Utils.returnException(aCheckResponse.getMessages());
		}
		wProcedureResponse.setReturnCode(aCheckResponse.getReturnCode());
		if (logger.isDebugEnabled())
			logger.logDebug("transformProcedureResponse Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;

	}

	private IProcedureResponse transformProcedureResponse(CheckResponse aCheckResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		if (aCheckResponse.getReturnCode() != 0) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, Utils.returnException(aCheckResponse.getMessages()));
			return Utils.returnException(aCheckResponse.getMessages());
		} else {
			// Agregar Header
			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("checkNumber", ICTSTypes.SQLINT4, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("amount", ICTSTypes.SQLMONEY4, 30));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("status", ICTSTypes.SQLVARCHAR, 1));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("datePayment", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("officePayment", ICTSTypes.SQLINT4, 3));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("userName", ICTSTypes.SQLVARCHAR, 15));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("hour", ICTSTypes.SQLVARCHAR, 8));

			for (Check aCheck : aCheckResponse.getCheckCollection()) {

				if (!IsValidCheckResponse(aCheck))
					return null;

				IResultSetRow row = new ResultSetRow();

				row.addRowData(1, new ResultSetRowColumnData(false, aCheck.getCheckNumber().toString())); // productNumber
				row.addRowData(2, new ResultSetRowColumnData(false, aCheck.getAmount().toString()));
				row.addRowData(3, new ResultSetRowColumnData(false, aCheck.getStatus().toString()));
				if (aCheck.getDatePayment() == null) {
					row.addRowData(4, new ResultSetRowColumnData(false, ""));
				}
				if (aCheck.getDatePayment() != null) {
					row.addRowData(4, new ResultSetRowColumnData(false, aCheck.getDatePayment().toString()));
				}
				row.addRowData(5, new ResultSetRowColumnData(false, aCheck.getOfficePayment().toString()));
				row.addRowData(6, new ResultSetRowColumnData(false, aCheck.getUserName().toString()));
				if (aCheck.getHour() == null) {
					row.addRowData(7, new ResultSetRowColumnData(false, "00:00"));
				}
				if (aCheck.getHour() != null) {
					row.addRowData(7, new ResultSetRowColumnData(false, aCheck.getHour()));
				}

				data.addRow(row);
			}

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

			wProcedureResponse.addResponseBlock(resultBlock);

		}
		wProcedureResponse.setReturnCode(aCheckResponse.getReturnCode());
		if (logger.isDebugEnabled())
			logger.logDebug("transformProcedureResponse Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;

	}

	/*********************
	 * Transformación de Response a ProcedureResponseBy Number
	 ***********************/

	private IProcedureResponse transformProcedureResponseByNumber(CheckResponse aCheckResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response By Number");

		if (aCheckResponse.getReturnCode() != 0) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, Utils.returnException(aCheckResponse.getMessages()));
			return Utils.returnException(aCheckResponse.getMessages());

		} else {

			// Agregar Header
			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("checkNumber", ICTSTypes.SQLINT4, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("statusId", ICTSTypes.SQLVARCHAR, 1));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("status", ICTSTypes.SQLVARCHAR, 1));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("datePayment", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("hour", ICTSTypes.SQLVARCHAR, 8));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("amount", ICTSTypes.SQLMONEY4, 30));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("officePayment", ICTSTypes.SQLINT4, 3));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("userName", ICTSTypes.SQLVARCHAR, 15));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("beneficiary", ICTSTypes.SQLVARCHAR, 30));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("descriptionOffice", ICTSTypes.SQLVARCHAR, 30));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("nameAccount", ICTSTypes.SQLVARCHAR, 25));

			for (Check aCheck : aCheckResponse.getCheckCollection()) {

				if (!IsValidCheckResponse(aCheck))
					return null;

				IResultSetRow row = new ResultSetRow();
				if (logger.isDebugEnabled())
					logger.logDebug("aCheck" + aCheck.getStatusId());
				row.addRowData(1, new ResultSetRowColumnData(false, aCheck.getCheckNumber().toString())); // productNumber
				row.addRowData(2, new ResultSetRowColumnData(false, aCheck.getStatusId().toString()));
				row.addRowData(3, new ResultSetRowColumnData(false, aCheck.getStatus().toString()));
				if (aCheck.getDatePayment() == null) {
					row.addRowData(4, new ResultSetRowColumnData(false, ""));
				}
				if (aCheck.getDatePayment() != null) {
					row.addRowData(4, new ResultSetRowColumnData(false, aCheck.getDatePayment()));
				}
				if (aCheck.getHour() == null) {
					row.addRowData(5, new ResultSetRowColumnData(false, ""));
				}
				if (aCheck.getHour() != null) {
					row.addRowData(5, new ResultSetRowColumnData(false, aCheck.getHour()));
				}
				if (aCheck.getAmount() == null) {
					row.addRowData(6, new ResultSetRowColumnData(false, ""));
				}
				if (aCheck.getAmount() != null) {
					row.addRowData(6, new ResultSetRowColumnData(false, aCheck.getAmount().toString()));
				}
				if (aCheck.getOfficePayment() == null) {
					row.addRowData(7, new ResultSetRowColumnData(false, ""));
				}
				if (aCheck.getOfficePayment() != null) {
					row.addRowData(7, new ResultSetRowColumnData(false, aCheck.getOfficePayment().toString()));
				}
				if (aCheck.getUserName() == null) {
					row.addRowData(8, new ResultSetRowColumnData(false, ""));
				}
				if (aCheck.getUserName() != null) {
					row.addRowData(8, new ResultSetRowColumnData(false, aCheck.getUserName().toString()));
				}
				if (aCheck.getBeneficiary() == null) {
					row.addRowData(9, new ResultSetRowColumnData(false, ""));
				}
				if (aCheck.getBeneficiary() != null) {
					row.addRowData(9, new ResultSetRowColumnData(false, aCheck.getBeneficiary().toString()));
				}
				if (aCheck.getDescriptionOffice() == null) {
					row.addRowData(10, new ResultSetRowColumnData(false, ""));
				}
				if (aCheck.getDescriptionOffice() != null) {
					row.addRowData(10, new ResultSetRowColumnData(false, aCheck.getDescriptionOffice().toString()));
				}
				if (aCheck.getNameAccount() == null) {
					row.addRowData(11, new ResultSetRowColumnData(false, ""));
				}
				if (aCheck.getNameAccount() != null) {
					row.addRowData(11, new ResultSetRowColumnData(false, aCheck.getNameAccount().toString()));
				}

				data.addRow(row);
			}

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
			wProcedureResponse.addResponseBlock(resultBlock);
		}
		wProcedureResponse.setReturnCode(aCheckResponse.getReturnCode());
		if (logger.isDebugEnabled())
			logger.logDebug(
					"transformProcedureResponseByNumber Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;

	}

	private boolean IsValidCheckResponse(Check aCheck) {
		String messageError = null;

		messageError = aCheck.getCheckNumber() == null ? " - CheckNumber can't be null" : "";
		messageError += aCheck.getAmount() == null ? " - Amount can't be null" : "";
		messageError += aCheck.getStatus() == null ? " - Status can't be null" : "";
		// messageError += aCheck.getDatePayment() == null ? " - DatePayment
		// can't be null":"";
		messageError += aCheck.getOfficePayment() == null ? " - OfficePayment can't be null" : "";
		// messageError += aCheck.getUserName() == null ? " - UserName can't be
		// null":"";
		// messageError += aCheck.getHour() == null ? " - Hour can't be
		// null":"";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		return true;
	}

}
