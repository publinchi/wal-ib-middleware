package com.cobiscorp.ecobis.orchestration.core.ib.query.customer;

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
import com.cobiscorp.cobis.commons.log.LogFactory;
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
import com.cobiscorp.ecobis.ib.application.dtos.AddressRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AddressResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCustomerInformation;

/**
 * 
 * @author mvelez
 * @since Ago 06, 2014
 * @version 1.0.0
 */

@Component(name = "CustomerInformationOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "CustomerInformationOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "CustomerInformationOrchestrationCore") })

public class CustomerInformationOrchestrationCore extends QueryBaseTemplate {

	ILogger logger = LogFactory.getLogger(CustomerInformationOrchestrationCore.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate#
	 * validateLocalExecution(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	protected IProcedureResponse validateLocalExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
		// TODO Auto-generated method stub
		IProcedureResponse response = new ProcedureResponseAS();
		response.setReturnCode(0);
		return response;
	}

	@Reference(referenceInterface = ICoreServiceCustomerInformation.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceCustomerInformation coreService;

	protected void bindCoreService(ICoreServiceCustomerInformation service) {
		coreService = service;
	}

	/**
	 * Deleting Service Interface
	 * 
	 * @param service
	 */
	protected void unbindCoreService(ICoreServiceCustomerInformation service) {
		coreService = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		if (logger.isDebugEnabled())
			logger.logInfo(
					"Component-Name: " + this.getClass().getSimpleName() + ", Component-Version: $$project.version$$");
	}

	@Override
	protected IProcedureResponse executeQuery(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		String messageError = null;
		String messageLog = null;
		String queryName = null;

		AddressResponse aAddressResponse = null;
		AddressRequest aAddressRequest = transformAddressRequest(request.clone());

		try {
			messageError = "getCustomerInformation: ERROR EXECUTING SERVICE";
			messageLog = "getCustomerInformation " + aAddressRequest.getClientCollection().getIdCustomer();
			queryName = "getCustomerInformation";
			aAddressRequest.setOriginalRequest(request);
			aAddressResponse = coreService.getCustomerInformation(aAddressRequest);

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

		return transformProcedureResponse(aAddressResponse, aBagSPJavaOrchestration);
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		// Valida Inyección de dependencias
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreService", coreService);
		Utils.validateComponentInstance(mapInterfaces);
		try {
			aBagSPJavaOrchestration.put(LOG_MESSAGE, "ACTUALIZACION DE DIRECCIONES DE CLIENTES");

			executeStepsQueryBase(anOrginalRequest, aBagSPJavaOrchestration);
			return processResponse(anOrginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		} catch (CTSInfrastructureException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		}

	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalProcedureReq,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureRespFinal = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
		return wProcedureRespFinal;
	}

	/******************
	 * Transformación de ProcedureRequest a AddressRequest
	 ********************/
	private AddressRequest transformAddressRequest(IProcedureRequest aRequest) {
		AddressRequest aAddressRequest = new AddressRequest();
		Client aClient = new Client();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;

		messageError = aRequest.readValueParam("@i_cliente") == null ? " - @i_cliente can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		aClient.setIdCustomer(aRequest.readValueParam("@i_cliente"));

		aAddressRequest.setClientCollection(aClient);

		return aAddressRequest;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/
	private IProcedureResponse transformProcedureResponse(AddressResponse aAddressResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		if (aAddressResponse.getReturnCode() != 0) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, Utils.returnException(aAddressResponse.getMessages())); // COLOCA
																														// ERRORES
																														// COMO
																														// RESPONSE
																														// DE
																														// LA
																														// TRANSACCIÓN
			Utils.returnException(aAddressResponse.getMessages());
		} else {

			// Agregar Header
			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("additionaInformation", ICTSTypes.SQLVARCHAR, 1));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("phone", ICTSTypes.SQLVARCHAR, 200));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("neighborhood", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("street", ICTSTypes.SQLVARCHAR, 254));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("description", ICTSTypes.SQLVARCHAR, 100));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("house", ICTSTypes.SQLVARCHAR, 1));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("email", ICTSTypes.SQLVARCHAR, 200));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("phoneId", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("id", ICTSTypes.SQLVARCHAR, 254));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("emailId", ICTSTypes.SQLVARCHAR, 100));

			// if (!IsValidCheckbookResponse(aAddressResponse))
			// return null;

			IResultSetRow row = new ResultSetRow();
			if (aAddressResponse.getAddressCollection().getAdditionalInformation() != null) {
				row.addRowData(1, new ResultSetRowColumnData(false,
						aAddressResponse.getAddressCollection().getAdditionalInformation()));
			} else {
				row.addRowData(1, new ResultSetRowColumnData(false, ""));
			}
			if (aAddressResponse.getAddressCollection().getPhone() != null) {
				row.addRowData(2,
						new ResultSetRowColumnData(false, aAddressResponse.getAddressCollection().getPhone()));
			} else {
				row.addRowData(2, new ResultSetRowColumnData(false, ""));
			}
			if (aAddressResponse.getAddressCollection().getNeighborhood() != null) {
				row.addRowData(3,
						new ResultSetRowColumnData(false, aAddressResponse.getAddressCollection().getNeighborhood()));
			} else {
				row.addRowData(3, new ResultSetRowColumnData(false, ""));
			}
			if (aAddressResponse.getAddressCollection().getStreet() != null) {
				row.addRowData(4,
						new ResultSetRowColumnData(false, aAddressResponse.getAddressCollection().getStreet()));
			} else {
				row.addRowData(4, new ResultSetRowColumnData(false, ""));
			}
			if (aAddressResponse.getAddressCollection().getBuilding() != null) {
				row.addRowData(5,
						new ResultSetRowColumnData(false, aAddressResponse.getAddressCollection().getBuilding()));
			} else {
				row.addRowData(5, new ResultSetRowColumnData(false, ""));
			}
			if (aAddressResponse.getAddressCollection().getHouse() != null) {
				row.addRowData(6,
						new ResultSetRowColumnData(false, aAddressResponse.getAddressCollection().getHouse()));
			} else {
				row.addRowData(6, new ResultSetRowColumnData(false, ""));
			}
			if (aAddressResponse.getAddressCollection().getEmail() != null) {
				row.addRowData(7,
						new ResultSetRowColumnData(false, aAddressResponse.getAddressCollection().getEmail()));
			} else {
				row.addRowData(7, new ResultSetRowColumnData(false, ""));
			}
			if (aAddressResponse.getAddressCollection().getPhoneCode() != null) {
				row.addRowData(8, new ResultSetRowColumnData(false,
						aAddressResponse.getAddressCollection().getPhoneCode().toString()));
			} else {
				row.addRowData(8, new ResultSetRowColumnData(false, ""));
			}
			if (aAddressResponse.getAddressCollection().getAddressCode() != null) {
				row.addRowData(9, new ResultSetRowColumnData(false,
						aAddressResponse.getAddressCollection().getAddressCode().toString()));
			} else {
				row.addRowData(9, new ResultSetRowColumnData(false, ""));
			}
			if (aAddressResponse.getAddressCollection().getEmailCode() != null) {
				row.addRowData(10, new ResultSetRowColumnData(false,
						aAddressResponse.getAddressCollection().getEmailCode().toString()));
			} else {
				row.addRowData(10, new ResultSetRowColumnData(false, ""));
			}
			data.addRow(row);

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

			wProcedureResponse.addResponseBlock(resultBlock);

		}
		// GCO-manejo de mensajes de Errores
		wProcedureResponse.setReturnCode(aAddressResponse.getReturnCode());
		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final -->" + wProcedureResponse.getProcedureResponseAsString());
		return wProcedureResponse;
	}

	private boolean IsValidCheckbookResponse(AddressResponse aAddressResponse) {
		String messageError = null;

		messageError = aAddressResponse.getAddressCollection().getAdditionalInformation() == null
				? " - AdditionalInformation can't be null" : "";
		messageError += aAddressResponse.getAddressCollection().getPhone() == null ? " - Phone can't be null" : "";
		messageError += aAddressResponse.getAddressCollection().getNeighborhood() == null
				? " - Neighborhood can't be null" : "";
		messageError += aAddressResponse.getAddressCollection().getStreet() == null ? " - Street can't be null" : "";
		messageError = aAddressResponse.getAddressCollection().getBuilding() == null ? " - Building can't be null" : "";
		messageError += aAddressResponse.getAddressCollection().getHouse() == null ? " - House can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		return true;
	}

}
