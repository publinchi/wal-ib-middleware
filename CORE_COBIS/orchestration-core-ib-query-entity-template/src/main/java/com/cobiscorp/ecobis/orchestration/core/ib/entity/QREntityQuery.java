package com.cobiscorp.ecobis.orchestration.core.ib.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
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
import com.cobiscorp.ecobis.ib.application.dtos.EntityRequest;
import com.cobiscorp.ecobis.ib.application.dtos.EntityResponse;
import com.cobiscorp.ecobis.ib.application.dtos.QREntityResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.FullEntity;
import com.cobiscorp.ecobis.ib.orchestration.dtos.QREntity;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceEntity;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceQREntity;

@Component(name = "QREntityQuery", immediate = false)
@Service(value = { ICoreServiceQREntity.class })
@Properties(value = { @Property(name = "service.description", value = "QREntityQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "QREntityQuery") })

public class QREntityQuery extends SPJavaOrchestrationBase implements ICoreServiceQREntity {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.commons.components.ICOBISComponent#loadConfiguration(
	 * com.cobiscorp.cobis.commons.configuration.IConfigurationReader)
	 */
	private static final String COBIS_CONTEXT = "COBIS";
	private static ILogger logger = LogFactory.getLogger(FullEntityQuery.class);
	private static final int COL_NOMBRE_COMP = 0;
	private static final int COL_APELL_CASADA = 1;
	private static final int COL_SUBTIPO = 2;
	private static final int COL_CED_RUC = 3;
	private static final int COL_RETENCION = 4;
	private static final int COL_MALA_REFERENCIA = 5;
	private static final int COL_NOMBRE_LARGO = 6;
	private static final int COL_NOMBRE_CORTO = 7;
	private static final int COL_RAZON_SOCIAL = 8;
	private static final int COL_GRUPO_ECON = 9;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceQREntity#
	 * GetQREntity(com.cobiscorp.ecobis.ib.application.dtos.EntityRequest)
	 */
	@Override
	public QREntityResponse GetQREntity(EntityRequest aEntityRequest)
			throws CTSServiceException, CTSInfrastructureException {
		// TODO Auto-generated method stub

		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: GetQREntity");
			logger.logInfo("*RESPUESTA CORE COBIS GENERADA");
		}
		IProcedureRequest request = new ProcedureRequestAS();

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");

		request.setSpName("cobis..sp_qrente");
		request.addInputParam("@i_ente", ICTSTypes.SQLINT4, aEntityRequest.getEnte().toString());
		request.addInputParam("@t_trn", ICTSTypes.SQLINT1, "1190");

		if (logger.isDebugEnabled()) {
			logger.logDebug("Request: " + request.getProcedureRequestAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("Start Request");
		}

		/* Ejecuta y obtiene la respuesta */
		IProcedureResponse pResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response: " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("Finalize Response");
		}

		QREntityResponse entityResponse = transformToEntityResponse(pResponse);
		return entityResponse;
	}

	private QREntityResponse transformToEntityResponse(IProcedureResponse aProcedureResponse) {
		QREntityResponse EntityResp = new QREntityResponse();
		List<QREntity> fullEntityCollection = new ArrayList<QREntity>();
		QREntity aQREntity = null;
		if (logger.isDebugEnabled()) {
			logger.logDebug("ProcedureResponse: " + aProcedureResponse.getProcedureResponseAsString());
		}
		IResultSetRow[] rowsEntity = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();

		for (IResultSetRow iResultSetRow : rowsEntity) {
			IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
			aQREntity = new QREntity();

			aQREntity.setNombre_completo(columns[COL_NOMBRE_COMP].getValue());
			aQREntity.setApellido_casada(columns[COL_APELL_CASADA].getValue());
			aQREntity.setSubtype(columns[COL_SUBTIPO].getValue());
			aQREntity.setCed_ruc(columns[COL_CED_RUC].getValue());
			aQREntity.setRetencion(columns[COL_RETENCION].getValue());
			aQREntity.setMala_referencia(columns[COL_MALA_REFERENCIA].getValue());
			aQREntity.setNombre_largo(columns[COL_NOMBRE_LARGO].getValue());
			aQREntity.setNombre_corto(columns[COL_NOMBRE_CORTO].getValue());
			aQREntity.setRazon_social(columns[COL_RAZON_SOCIAL].getValue());
			aQREntity.setGrupo_econ(columns[COL_GRUPO_ECON].getValue());
			fullEntityCollection.add(aQREntity);
		}
		EntityResp.setQREntityCollection(fullEntityCollection);
		return EntityResp;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
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
