package com.cobiscorp.ecobis.orchestration.core.ib.query.credit.line;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.csp.util.CSPUtil;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ErrorBlock;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.CreditLineRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CreditLineResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.application.dtos.StockRequest;
import com.cobiscorp.ecobis.ib.application.dtos.StockResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.orchestration.core.ib.utils.ProductPredicate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.CreditLine;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Entity;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.User;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCreditLine;

/**
 * Credit Lines
 * 
 * @since Jun 30, 2015
 * @author dmorla
 * @version 1.0.0
 * 
 */
@Component(name = "CreditLineOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "CreditLineOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "CreditLineOrchestrationCore") })
public class CreditLineOrchestrationCore extends SPJavaOrchestrationBase {

	private static ILogger logger = LogFactory.getLogger(CreditLineOrchestrationCore.class);
	private static final String CLASS_NAME = "CreditLineOrchestrationCore--->";
	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";

	protected static final String ORIGEN_IB = "IB";
	protected static final String ORIGEN_ADMIN = "AD";
	protected static final String PRODUCT_CREDIT_LINE = "21";
	protected static final int COL_PRODUCT_NUMBER = 1;
	protected static final int COL_CURRENCY = 4;

	// static final String RESPONSE_LOCAL_UPDATE = "RESPONSE_LOCAL_UPDATE";
	// static final String RESPONSE_PROVIDER = "RESPONSE_PROVIDER";

	/**
	 * Read configuration of parent component
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceCreditLine.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceCreditLine", unbind = "unbindCoreServiceCreditLine")
	protected ICoreServiceCreditLine coreServiceCreditLine;

	/**
	 * Instance Service Interface
	 * 
	 * @param service
	 */
	public void bindCoreServiceCreditLine(ICoreServiceCreditLine service) {
		coreServiceCreditLine = service;
	}

	/**
	 * Deleting Service Interface
	 * 
	 * @param service
	 */
	public void unbindCoreServiceCreditLine(ICoreServiceCreditLine service) {
		coreServiceCreditLine = null;
	}

	/**
	 * /** Execute transfer first step of service
	 * <p>
	 * This method is the main executor of transactional contains the original
	 * input parameters.
	 * 
	 * @param anOriginalRequest
	 *            - Information original sended by user's.
	 * @param aBagSPJavaOrchestration
	 *            - Object dictionary transactional steps.
	 * 
	 * @return
	 *         <ul>
	 *         <li>IProcedureResponse - Represents the service execution.</li>
	 *         </ul>
	 */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "executeJavaOrchestration");
		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);

		try {
			Map<String, Object> mapInterfaces = new HashMap<String, Object>();
			mapInterfaces.put("coreServiceCreditLine", coreServiceCreditLine);

			Utils.validateComponentInstance(mapInterfaces);

			if (anOriginalRequest == null)
				if (logger.isInfoEnabled())
					logger.logInfo(CLASS_NAME + "Original Request ISNULL");

			executeSteps(aBagSPJavaOrchestration);

			return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.logInfo("*********  Error en " + e.getMessage(), e);
			}
			return Utils.returnException("Service is not available");
		}
	}

	private IProcedureResponse executeSteps(Map<String, Object> aBag) {

		IProcedureRequest anOriginalRequest = (IProcedureRequest) aBag.get(ORIGINAL_REQUEST);

		IProcedureResponse responseProc = null;
		IProcedureResponse responseAffiliated = null;
		String wOrigen = null;
		try {
			// consultaado lineas de credito afiliadas
			wOrigen = anOriginalRequest.readValueParam("@i_origen");
			if (wOrigen.equals(ORIGEN_IB)) {
				responseAffiliated = executeProductsAffiliated(anOriginalRequest, aBag);
				if (Utils.flowError("executeProductsAffiliated -->", responseAffiliated)) {
					aBag.put(RESPONSE_TRANSACTION, Utils.returnException(Utils.returnArrayMessage(responseAffiliated)));
					return Utils.returnException(Utils.returnArrayMessage(responseAffiliated));
				}

				if (logger.isDebugEnabled())
					logger.logDebug("Response Affiliated  --> " + responseAffiliated.getProcedureResponseAsString());
			}
			// consulta de lineas de credito
			responseProc = executeCreditLineCore(anOriginalRequest, aBag);

			if (Utils.flowError("executeCreditLineCore -->", responseProc)) {
				aBag.put(RESPONSE_TRANSACTION, Utils.returnException(Utils.returnArrayMessage(responseProc)));
				return Utils.returnException(Utils.returnArrayMessage(responseProc));
			}
			aBag.remove(RESPONSE_TRANSACTION);
			aBag.put(RESPONSE_TRANSACTION, responseProc);

			if (logger.isDebugEnabled())
				logger.logDebug("Response Credit Line Core  --> " + responseProc.getProcedureResponseAsString());

			if (wOrigen.equals(ORIGEN_IB)) {
				if (!intersectAffiliated(responseAffiliated, responseProc, aBag)) {

					if (anOriginalRequest.readValueFieldInHeader("culture").equals("EN-US"))
						aBag.put(RESPONSE_TRANSACTION,
								Utils.returnException("Do not have Credit Line to Internet Banking"));
					else
						aBag.put(RESPONSE_TRANSACTION,
								Utils.returnException("No tiene líneas de crédito asociado a Banca en Línea"));
				}
			}

			return responseProc;
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.logInfo("*********  Error en " + e.getMessage(), e);
			}

			return Utils.returnException("Service is not available");

		}

	}

	/**
	 * name executeProviderTransaction
	 * 
	 * @param anOriginalRequest
	 * @param aBag
	 * @return IProcedureResponse
	 */
	private IProcedureResponse executeCreditLineCore(IProcedureRequest anOriginalRequest, Map<String, Object> aBag) {

		CreditLineResponse wCreditLineResp = new CreditLineResponse();
		CreditLineRequest wCreditLineRequest = transformCreditLineRequest(anOriginalRequest.clone());
		String wOrigen = null;
		try {
			wCreditLineRequest.setOriginalRequest(anOriginalRequest);
			wOrigen = anOriginalRequest.readValueParam("@i_origen");
			wCreditLineResp = coreServiceCreditLine.getLines(wCreditLineRequest);
		} catch (CTSServiceException e) {
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
		}

		return transformCreditLineResponse(wCreditLineResp, aBag, wOrigen);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase
	 * #processResponse(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
		CSPUtil.copyHeaderFields((IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST), response);
		return response;
	}

	/******************
	 * Transformación de ProcedureRequest a StockRequest
	 ********************/

	private CreditLineRequest transformCreditLineRequest(IProcedureRequest aRequest) {
		CreditLineRequest wCreditLineRequest = new CreditLineRequest();
		Entity wEntity = new Entity();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;

		messageError = aRequest.readValueParam("@i_cliente") == null ? " - @i_cliente can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		wEntity.setEnte(Integer.parseInt(aRequest.readValueParam("@i_cliente")));

		wCreditLineRequest.setEntity(wEntity);

		if (!(aRequest.readValueParam("@i_origen") == null)) {
			wCreditLineRequest.setOrigin(aRequest.readValueParam("@i_origen"));

		}

		return wCreditLineRequest;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/

	private IProcedureResponse transformCreditLineResponse(CreditLineResponse aCreditLineResponse,
			Map<String, Object> aBagSPJavaOrchestration, String wOrigen) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		if (aCreditLineResponse.getReturnCode() != 0) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, Utils.returnException(aCreditLineResponse.getMessages()));

		} else {

			// Agregar Header
			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("CODIGO", ICTSTypes.SYBINT4, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("LINEA", ICTSTypes.SYBVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("MONEDA", ICTSTypes.SYBVARCHAR, 64));
			if (wOrigen.equals(ORIGEN_IB)) {
				metaData.addColumnMetaData(new ResultSetHeaderColumn("DISPONIBLE", ICTSTypes.SYBMONEY, 25));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("COD MONEDA", ICTSTypes.SYBINT2, 2));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("FECHA APERTURA", ICTSTypes.SYBVARCHAR, 15));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("FECHA VENCIMIENTO", ICTSTypes.SYBVARCHAR, 15));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("MONTO", ICTSTypes.SYBMONEY, 25));
			} else {
				metaData.addColumnMetaData(new ResultSetHeaderColumn("COD MONEDA", ICTSTypes.SYBINT2, 2));
			}
			for (CreditLine aCreditLine : aCreditLineResponse.getCreditLineCollection()) {

				IResultSetRow row = new ResultSetRow();

				row.addRowData(1, new ResultSetRowColumnData(false, aCreditLine.getCode().toString()));
				row.addRowData(2, new ResultSetRowColumnData(false, aCreditLine.getCredit()));
				row.addRowData(3, new ResultSetRowColumnData(false, aCreditLine.getMoney()));
				if (wOrigen.equals(ORIGEN_IB)) {
					row.addRowData(4, new ResultSetRowColumnData(false, aCreditLine.getAvailable().toString()));
					row.addRowData(5, new ResultSetRowColumnData(false, aCreditLine.getCurrency().toString()));
					row.addRowData(6, new ResultSetRowColumnData(false, aCreditLine.getOpeningDate()));
					row.addRowData(7, new ResultSetRowColumnData(false, aCreditLine.getExpirationDate()));
					row.addRowData(8, new ResultSetRowColumnData(false, aCreditLine.getAmount().toString()));
				} else {
					row.addRowData(4, new ResultSetRowColumnData(false, aCreditLine.getCurrency().toString()));
				}
				data.addRow(row);
			}

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

			wProcedureResponse.addResponseBlock(resultBlock);

		}
		wProcedureResponse.setReturnCode(aCreditLineResponse.getReturnCode());
		if (logger.isDebugEnabled())
			logger.logDebug("transformProcedureResponse Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;

	}

	protected IProcedureResponse executeProductsAffiliated(IProcedureRequest aRequest, Map<String, Object> bag) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en executeProductsQuery");
		}

		request.setSpName("cob_bvirtual..sp_consulta_cuentas");

		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18752");
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, "18752");
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		request.addInputParam("@s_servicio", ICTSTypes.SQLINT1, aRequest.readValueParam("@s_servicio"));
		request.addInputParam("@i_tipo_ejec", ICTSTypes.SQLCHAR, "L");

		Utils.copyParam("@s_date", aRequest, request);
		Utils.copyParam("@i_operacion", aRequest, request);
		Utils.copyParam("@i_login", aRequest, request);
		Utils.copyParam("@s_cliente", aRequest, request);
		request.addInputParam("@i_valor_producto", ICTSTypes.SQLINT2, PRODUCT_CREDIT_LINE);
		request.addInputParam("@i_valor_cuenta", ICTSTypes.SQLVARCHAR, "");

		Utils.copyParam("@t_trn", aRequest, request);
		request.addInputParam("@i_bl_net", ICTSTypes.SQLCHAR, "S");
		Utils.copyParam("@i_nregistros", aRequest, request);
		request.addOutputParam("@o_registros", ICTSTypes.SQLINT4, "0");

		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de executeProductsQuery");
		}
		return wProductsQueryResp;

	}

	private boolean intersectAffiliated(IProcedureResponse aResponseAffiliated,
			IProcedureResponse aResponseProductsCore, Map<String, Object> bag) {

		String wsName = "";
		Product product = null;
		Currency currency = null;
		IProcedureResponse wResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("--->>>Productos Afiliados--->>" + aResponseAffiliated.getProcedureResponseAsString());
		if (logger.isDebugEnabled())
			logger.logDebug("--->>>Productos Core--->>" + aResponseProductsCore.getProcedureResponseAsString());

		IResultSetHeader metaData = aResponseProductsCore.getResultSet(1).getMetaData();

		IResultSetData dataCtaPrincipal = new ResultSetData();
		List<IResultSetRow> dataListFinal = new ArrayList<IResultSetRow>();

		List<IResultSetData> dataProductsAffiliated = (List<IResultSetData>) aResponseAffiliated.getResultSet(1)
				.getData().getRows();

		IResultSetRow[] rowsTemplocal = aResponseAffiliated.getResultSet(1).getData().getRowsAsArray();

		IResultSetRow[] rowsTemp = aResponseProductsCore.getResultSet(1).getData().getRowsAsArray();

		if (rowsTemplocal.length == 0)
			return false;

		if (rowsTemp.length == 0)
			return false;

		if (logger.isDebugEnabled())
			logger.logDebug("--->>>columns--->>cuenta:" + rowsTemplocal[0].getColumns().get(4));

		Map<String, String> mapProd = new HashMap<String, String>();
		for (IResultSetRow iResultSetRow : rowsTemp) {
			IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

			product = new Product();
			currency = new Currency();
			product.setProductNumber(columns[COL_PRODUCT_NUMBER].getValue());
			product.setProductType(Integer.parseInt(PRODUCT_CREDIT_LINE));
			currency.setCurrencyId(Integer.parseInt(columns[COL_CURRENCY].getValue()));
			product.setCurrency(currency);
			// Validacion de productos Afiliados contra productos del Core
			IResultSetRow row = (IResultSetRow) CollectionUtils.find(dataProductsAffiliated,
					new ProductPredicate(product));

			if (row != null) {

				IResultSetRow wIResultSetRowNew = new ResultSetRow();// creo un
																		// nuevo
																		// resultset
																		// row

				wsName = product.getProductNumber() + product.getProductType().toString()
						+ product.getCurrency().getCurrencyId().toString();
				if (logger.isDebugEnabled())
					logger.logDebug("-->>>Clave-" + wsName);
				// Validacion que la interseccion de productos no tenga datos
				// repetidos
				if (mapProd.get(wsName) == null) {
					mapProd.put(wsName, "OK");
					if (logger.isDebugEnabled())
						logger.logDebug("ENCONTRE PRODUCTO " + product.toString() + " IGUAL --->" + row.toString());

					int wNumColummns = iResultSetRow.getColumnsNumber();// obtengo
																		// la
																		// cantidad
																		// de
																		// columnas
																		// del
																		// core

					for (int i = 0; i < wNumColummns; i++) {
						IResultSetRowColumnData wColumnData = new ResultSetRowColumnData(false, columns[i].getValue());// creo
																														// un
																														// nuevo
																														// column
																														// data
																														// para
																														// agregarlo
																														// al
																														// resulsetRow
						wIResultSetRowNew.addRowData(i + 1, wColumnData);
					}
					dataListFinal.add(wIResultSetRowNew);
				}
			}

		}

		if (logger.isDebugEnabled())
			logger.logDebug("dataListFinal --->" + dataListFinal.toString());

		for (IResultSetRow obj : dataListFinal) {

			dataCtaPrincipal.addRow(obj);
		}

		IResultSetBlock resultsetBlockPrincipal = new ResultSetBlock(metaData, dataCtaPrincipal);
		wResponse.addResponseBlock(resultsetBlockPrincipal);

		bag.put(RESPONSE_TRANSACTION, wResponse);

		if (logger.isDebugEnabled())
			logger.logDebug("Response Products Core Intersected -->" + wResponse.getProcedureResponseAsString());

		return true;
	}

}
