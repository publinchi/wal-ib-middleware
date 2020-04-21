/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.datacustomers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.ecobis.ib.application.dtos.AddressRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AddressResponse;
import com.cobiscorp.ecobis.ib.application.dtos.BatchGenerateCustomerDataRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BatchGenerateCustomerDataResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.EntityIntegrated;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceBatchGenerateCustomerData;

@Service(value = { ICoreServiceBatchGenerateCustomerData.class, ICISSPBaseOrchestration.class, IOrchestrator.class })
@Component(name = "GenerateDataCustomer", immediate = false)
@Properties(value = { @Property(name = "service.description", value = "GenerateDataCustomer"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "GenerateDataCustomer") })
public class GenerateDataCustomer extends SPJavaOrchestrationBase implements ICoreServiceBatchGenerateCustomerData {
	private static final String CLASS_NAME = " >-----> ";
	private static ILogger logger = LogFactory.getLogger(GenerateDataCustomer.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.commons.components.ICOBISComponent#loadConfiguration(
	 * com.cobiscorp.cobis.commons.configuration.IConfigurationReader)
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceGenerateCustomerData#getGenerateCustomerData(com.cobiscorp.
	 * ecobis.ib.application.dtos.GenerateCustomerDataRequest)
	 */
	@Override
	public BatchGenerateCustomerDataResponse getGenerateCustomerData(
			BatchGenerateCustomerDataRequest generateCustomerDataRequest)
			throws CTSServiceException, CTSInfrastructureException {
		// Context context = ContextManager.getContext();
		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		String CODE_TRN = "1850015";
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, CODE_TRN);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		anOriginalRequest.setSpName("cobis..sp_bv_gen_datclient_ej");
		anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, CODE_TRN);
		anOriginalRequest.addInputParam("@i_fecha_proceso", ICTSTypes.SQLVARCHAR,
				generateCustomerDataRequest.getDateProcess()); // context.getProcessDate());
																// //
		anOriginalRequest.addInputParam("@i_fecha_ingreso", ICTSTypes.SQLVARCHAR,
				generateCustomerDataRequest.getDateAdmission());
		anOriginalRequest.addInputParam("@i_rowcount", ICTSTypes.SQLINT4,
				generateCustomerDataRequest.getRecordNumber().toString());
		anOriginalRequest.addInputParam("@i_ente_mis", ICTSTypes.SQLINT4,
				generateCustomerDataRequest.getNext().toString());

		anOriginalRequest.addInputParam("@i_sarta", ICTSTypes.SQLINT4,
				generateCustomerDataRequest.getBatchCollection().getSarta().toString());
		anOriginalRequest.addInputParam("@i_batch", ICTSTypes.SQLINT4,
				generateCustomerDataRequest.getBatchCollection().getBatch().toString());
		anOriginalRequest.addInputParam("@i_secuencial", ICTSTypes.SQLINT4,
				generateCustomerDataRequest.getBatchCollection().getSecuencial().toString());
		anOriginalRequest.addInputParam("@i_corrida", ICTSTypes.SQLINT4,
				generateCustomerDataRequest.getBatchCollection().getCorrida().toString());
		anOriginalRequest.addInputParam("@i_intento", ICTSTypes.SQLINT4,
				generateCustomerDataRequest.getBatchCollection().getIntento().toString());
		anOriginalRequest.addOutputParam("@o_max_registros", ICTSTypes.SQLINT4, "0");
		anOriginalRequest.addOutputParam("@o_tot_registros", ICTSTypes.SQLINT4, "0");
                
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Enviando solicitud al Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Request a enviar: " + anOriginalRequest.getProcedureRequestAsString());

		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Data devuelta del Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Response obtenido: " + response.getProcedureResponseAsString());

		return transformToGenerateCustomerDataResponse(response);
	}

	/**
	 * @param response
	 * @return
	 */
	private BatchGenerateCustomerDataResponse transformToGenerateCustomerDataResponse(
			IProcedureResponse aProcedureResponse) {

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "RESPONSE TO TRANSFORM: " + aProcedureResponse.getProcedureResponseAsString());
		EntityIntegrated aEntityIntegrated = null;
		List<EntityIntegrated> aEntityIntegratedList = new ArrayList<EntityIntegrated>();
		BatchGenerateCustomerDataResponse aGenerateCustomerDataResponse = new BatchGenerateCustomerDataResponse();

		if (aProcedureResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsGenerateCustomerData = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();

			for (IResultSetRow iResultSetRow : rowsGenerateCustomerData) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				aEntityIntegrated = new EntityIntegrated();
				if (columns[0].getValue() != null) {
					aEntityIntegrated.setFecha_crea(columns[0].getValue());
				}
				if (columns[1].getValue() != null) {
					aEntityIntegrated.setEstado(columns[1].getValue());
				}
				if (columns[2].getValue() != null) {
					aEntityIntegrated.setEnte(Integer.parseInt(columns[2].getValue()));
				}
				if (columns[3].getValue() != null) {
					aEntityIntegrated.setSubtipo(columns[3].getValue());
				}
				if (columns[4].getValue() != null) {
					aEntityIntegrated.setNombre_completo(columns[4].getValue());
				}
				if (columns[5].getValue() != null) {
					aEntityIntegrated.setCed_ruc(columns[5].getValue());
				}
				if (columns[6].getValue() != null) {
					aEntityIntegrated.setPasaporte(columns[6].getValue());
				}
				if (columns[7].getValue() != null) {
					aEntityIntegrated.setOficina(Integer.parseInt(columns[7].getValue()));
				}
				if (columns[8].getValue() != null) {
					aEntityIntegrated.setFecha_nac(columns[8].getValue());
				}
				if (columns[9].getValue() != null) {
					aEntityIntegrated.setEmail(columns[9].getValue());
				}
				if (columns[10].getValue() != null) {
					aEntityIntegrated.setOficial(Integer.parseInt(columns[10].getValue()));
				}
				if (columns[11].getValue() != null) {
					aEntityIntegrated.setTipo_ced(columns[11].getValue());
				}
				if (columns[12].getValue() != null) {
					aEntityIntegrated.setNombre(columns[12].getValue());
				}
				if (columns[13].getValue() != null) {
					aEntityIntegrated.setP_apellido(columns[13].getValue());
				}
				if (columns[14].getValue() != null) {
					aEntityIntegrated.setS_apellido(columns[14].getValue());
				}
				if (columns[15].getValue() != null) {
					aEntityIntegrated.setSegmento(columns[15].getValue());
				}
				if (columns[16].getValue() != null) {
					aEntityIntegrated.setLineaNegocio(columns[16].getValue());
				}
				if (columns[17].getValue() != null) {
					aEntityIntegrated.setApoderadoLegal(Integer.parseInt(columns[17].getValue()));
				}
				if (columns[18].getValue() != null) {
					aEntityIntegrated.setCedRucAnt(columns[18].getValue());
				}
				aEntityIntegratedList.add(aEntityIntegrated);
			}

			aGenerateCustomerDataResponse.setEntityIntegrateList(aEntityIntegratedList);
		} else {
			Message[] message = Utils.returnArrayMessage(aProcedureResponse);
			aGenerateCustomerDataResponse.setMessages(message);
		}

		if (aProcedureResponse.readValueParam("@o_max_registros") != null) {
			aGenerateCustomerDataResponse
					.setMaxRecord(Integer.parseInt(aProcedureResponse.readValueParam("@o_max_registros")));
		}
		if (aProcedureResponse.readValueParam("@o_tot_registros") != null) {
			aGenerateCustomerDataResponse
					.setTotalRecords(Integer.parseInt(aProcedureResponse.readValueParam("@o_tot_registros")));
		}

		aGenerateCustomerDataResponse.setReturnCode(aProcedureResponse.getReturnCode());
		return aGenerateCustomerDataResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase#
	 * executeJavaOrchestration(com.cobiscorp.cobis.cts.domains.
	 * IProcedureRequest, java.util.Map)
	 */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase#
	 * processResponse(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}
}
