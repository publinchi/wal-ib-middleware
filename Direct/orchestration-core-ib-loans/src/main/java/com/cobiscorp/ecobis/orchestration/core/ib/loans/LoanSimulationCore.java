/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.loans;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
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
import com.cobiscorp.ecobis.ib.application.dtos.LoanSimulationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.LoanOperationsTypeResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.LoanOperationsType;
//import com.cobiscorp.ecobis.ib.orchestration.dtos.QREntity;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceLoanSimulation;
//import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceQREntity;

/**
 * @author mvelez
 *
 */
@Component(name = "LoanSimulationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "LoanSimulationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "LoanSimulationCore") })

public class LoanSimulationCore extends SPJavaOrchestrationBase {
	@Reference(referenceInterface = ICoreServiceLoanSimulation.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceLoanSimulation coreService;

	ILogger logger = this.getLogger();

	public void bindCoreService(ICoreServiceLoanSimulation service) {
		coreService = service;
	}

	public void unbindCoreService(ICoreServiceLoanSimulation service) {
		coreService = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalProcedureReq,
			Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreService", coreService);
		Utils.validateComponentInstance(mapInterfaces);

		try {

			String messageLog = null;

			LoanOperationsTypeResponse wLoanSimulationResponse = null;
			LoanSimulationRequest wLoanSimulationRequest = transformLoanSimulationRequest(anOrginalRequest.clone());

			messageLog = "<<< GetLoans >>>";
			wLoanSimulationRequest.setOriginalRequest(anOrginalRequest);

			if (logger.isDebugEnabled())
				logger.logDebug(messageLog);

			wLoanSimulationResponse = coreService.GetLoans(wLoanSimulationRequest);

			return transformProcedureResponse(wLoanSimulationResponse);

		} catch (CTSServiceException e) {
			e.printStackTrace();
			return Utils.returnExceptionService(anOrginalRequest, e);

		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
			return Utils.returnExceptionService(anOrginalRequest, e);
		}
	}

	private LoanSimulationRequest transformLoanSimulationRequest(IProcedureRequest aRequest) {
		LoanSimulationRequest loanSimulationReq = new LoanSimulationRequest();

		if (logger.isDebugEnabled())
			logger.logDebug("<<<Procedure Request to Transform->>>" + aRequest.getProcedureRequestAsString());

		if (aRequest.readValueParam("@i_operacion") != null)
			loanSimulationReq.setOperation(aRequest.readValueParam("@i_operacion"));
		if (aRequest.readValueParam("@i_monto") != null)
			loanSimulationReq.setAmmount(new Double(aRequest.readValueParam("@i_monto")));
		if (aRequest.readValueParam("@i_sector") != null)
			loanSimulationReq.setSector(aRequest.readValueParam("@i_sector"));
		if (aRequest.readValueParam("@i_toperacion") != null)
			loanSimulationReq.setOperation_type(aRequest.readValueParam("@i_toperacion"));
		if (aRequest.readValueParam("@i_moneda") != null)
			loanSimulationReq.setCurrency_id(new Integer(aRequest.readValueParam("@i_moneda")));
		if (aRequest.readValueParam("@i_fecha_ini") != null)
			loanSimulationReq.setInitial_date(aRequest.readValueParam("@i_fecha_ini"));
		if (aRequest.readValueParam("@i_codigo") != null)
			loanSimulationReq.setCode(aRequest.readValueParam("@i_codigo"));
		if (aRequest.readValueParam("@i_tipo_amortizacion") != null)
			loanSimulationReq.setAmortization_type(aRequest.readValueParam("@i_tipo_amortizacion"));
		if (aRequest.readValueParam("@i_cuota") != null)
			loanSimulationReq.setCuota(new Integer(aRequest.readValueParam("@i_cuota")));
		if (aRequest.readValueParam("@i_plazo") != null)
			loanSimulationReq.setTerm(new Integer(aRequest.readValueParam("@i_plazo")));
		if (aRequest.readValueParam("@i_tipo_ente") != null)
			loanSimulationReq.setEntity_type(aRequest.readValueParam("@i_tipo_ente"));

		return loanSimulationReq;
	}

	private IProcedureResponse transformProcedureResponse(LoanOperationsTypeResponse loanOperationsTypeResponse) {
		// if (!IsValidLoanAmortizationResponse(loanAmortizationResponse))
		// return null;

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("<<<METHOD: transformProcedureResponse>>>");

		// Add Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 10)); /* mnemonico */
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 25)); /* descripcion */
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 10)); /* sector */

		for (LoanOperationsType aLoanOperationsType : loanOperationsTypeResponse.getLoanOperationsTypeCollection()) {
			// if (!IsValidAccountStatementResponse(aFullEntity)) return null;
			IResultSetRow row = new ResultSetRow();

			row.addRowData(1, new ResultSetRowColumnData(false, aLoanOperationsType.getMnemonic()));
			row.addRowData(2, new ResultSetRowColumnData(false, aLoanOperationsType.getDescription()));
			row.addRowData(3, new ResultSetRowColumnData(false, aLoanOperationsType.getSector()));

			data.addRow(row);
		} // for

		IResultSetBlock resultBlock1 = new ResultSetBlock(metaData, data);

		wProcedureResponse.addResponseBlock(resultBlock1);

		if (logger.isDebugEnabled())
			logger.logDebug("<<<ORCHESTRATION: Response Final >>>" + wProcedureResponse.getProcedureResponseAsString());
		return wProcedureResponse;
	}
}
