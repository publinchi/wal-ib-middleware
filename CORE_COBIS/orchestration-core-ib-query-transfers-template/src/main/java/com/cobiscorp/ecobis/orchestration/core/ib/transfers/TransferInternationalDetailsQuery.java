package com.cobiscorp.ecobis.orchestration.core.ib.transfers;

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
import com.cobiscorp.cobisv.commons.context.CobisSession;
import com.cobiscorp.cobisv.commons.context.Context;
import com.cobiscorp.cobisv.commons.context.ContextManager;
import com.cobiscorp.ecobis.ib.application.dtos.TransferInternationalDetailsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TransferInternationalDetailsResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.dtos.TransferInternationalDetails;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServicelTransfersInternationaDetails;

@Service(value = { ICoreServicelTransfersInternationaDetails.class, ICISSPBaseOrchestration.class,
		IOrchestrator.class })
@Component(name = "TransferInternationalDetailsQuery", immediate = false)
@Properties(value = { @Property(name = "service.description", value = "TransferInternationalDetailsQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "TransferInternationalDetailsQuery") })

public class TransferInternationalDetailsQuery extends SPJavaOrchestrationBase
		implements ICoreServicelTransfersInternationaDetails {
	ILogger logger = LogFactory.getLogger(TransferInternationalDetailsQuery.class);
	private static final String CLASS_NAME = " >-----> ";

	@Override
	public TransferInternationalDetailsResponse searchTransferInternationalDetails(
			TransferInternationalDetailsRequest transferInternationalDetailsRequest)
			throws CTSServiceException, CTSInfrastructureException {

		Context context = ContextManager.getContext();
		CobisSession session = (CobisSession) context.getSession();

		String CODE_TRN = transferInternationalDetailsRequest.getCodeTransactionalIdentifier();

		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, CODE_TRN);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		anOriginalRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		anOriginalRequest.setSpName("cobis..sp_bv_cons_transf_int");
		anOriginalRequest.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, session.getServer());
		anOriginalRequest.addInputParam("@s_ofi", ICTSTypes.SQLINT2, session.getOffice());
		anOriginalRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, session.getUser());
		anOriginalRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, session.getTerminal());

		anOriginalRequest.addInputParam("@i_group", ICTSTypes.SQLVARCHAR,
				transferInternationalDetailsRequest.getCriteria2());
		if (transferInternationalDetailsRequest.getSequential() != null)
			anOriginalRequest.addInputParam("@i_secuencial", ICTSTypes.SQLINT4,
					transferInternationalDetailsRequest.getSequential());
		if (transferInternationalDetailsRequest.getInitialDate() != null)
			anOriginalRequest.addInputParam("@i_fecha_ini", ICTSTypes.SQLVARCHAR,
					transferInternationalDetailsRequest.getInitialDate());
		if (transferInternationalDetailsRequest.getFinalDate() != null)
			anOriginalRequest.addInputParam("@i_fecha_fin", ICTSTypes.SQLVARCHAR,
					transferInternationalDetailsRequest.getFinalDate());
		if (transferInternationalDetailsRequest.getMode() != null)
			anOriginalRequest.addInputParam("@i_formato_fecha", ICTSTypes.SQLINT4,
					transferInternationalDetailsRequest.getMode());
		if (transferInternationalDetailsRequest.getProductNumber() != null)
			anOriginalRequest.addInputParam("@i_account", ICTSTypes.SQLVARCHAR,
					transferInternationalDetailsRequest.getProductNumber());
		if (transferInternationalDetailsRequest.getNumberOfResults() != null)
			anOriginalRequest.addInputParam("@i_siguiente", ICTSTypes.SQLINT4,
					transferInternationalDetailsRequest.getNumberOfResults());
		if (transferInternationalDetailsRequest.getProductNumber() != null)
			anOriginalRequest.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR,
					transferInternationalDetailsRequest.getProductNumber());
		if (transferInternationalDetailsRequest.getProductId() != null)
			anOriginalRequest.addInputParam("@i_prod", ICTSTypes.SQLINT4,
					transferInternationalDetailsRequest.getProductId());
		if (transferInternationalDetailsRequest.getLogin() != null)
			anOriginalRequest.addInputParam("@i_login", ICTSTypes.SQLVARCHAR,
					transferInternationalDetailsRequest.getLogin());
		if (transferInternationalDetailsRequest.getCurrencyId() != null)
			anOriginalRequest.addInputParam("@i_mon", ICTSTypes.SQLINT4,
					transferInternationalDetailsRequest.getCurrencyId());
		if (transferInternationalDetailsRequest.getCriteria2().equals("NE")
				|| transferInternationalDetailsRequest.getCriteria2().equals("SE")) {
			anOriginalRequest.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR,
					transferInternationalDetailsRequest.getLastResult());
		}

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Obtencion del servicio");

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Enviando solicitud al Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Request a enviar: " + anOriginalRequest.getProcedureRequestAsString());

		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Data devuelta del Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Response obtenido: " + response.getProcedureResponseAsString());
		
		return transformToTransferInternationalDetailsResponse(response);
	}

	private TransferInternationalDetailsResponse transformToTransferInternationalDetailsResponse(
			IProcedureResponse aProcedureResponse) {
	
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "RESPONSE TO TRANSFORM: " + aProcedureResponse.getProcedureResponseAsString());

		TransferInternationalDetails aTransferInternationalDetails = null;
		List<TransferInternationalDetails> aTransferInternationalDetailsList = new ArrayList<TransferInternationalDetails>();
		TransferInternationalDetailsResponse aTransferInternationalDetailsResponse = new TransferInternationalDetailsResponse();
		if (aProcedureResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsTransfersInternational = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();

			Integer wColumns;
			wColumns = 41;
			for (IResultSetRow iResultSetRow : rowsTransfersInternational) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				aTransferInternationalDetails = new TransferInternationalDetails();

				// 41 Campos comunes para Trans. enviadas y recibidas
				if (columns[0].getValue() != null) {
					aTransferInternationalDetails.setDateTransaction(columns[0].getValue());
				}
				if (columns[1].getValue() != null) {
					aTransferInternationalDetails.setIdReference(columns[1].getValue());
				}
				if (columns[2].getValue() != null) {
					aTransferInternationalDetails.setAccountDebit(columns[2].getValue());
				}
				if (columns[3].getValue() != null) {
					aTransferInternationalDetails.setAccountType(columns[3].getValue());
				}
				if (columns[4].getValue() != null) {
					aTransferInternationalDetails.setAccountName(columns[4].getValue());
				}
				if (columns[5].getValue() != null) {
					aTransferInternationalDetails.setAmmount(Double.parseDouble(columns[5].getValue()));
				}
				if (columns[6].getValue() != null) {
					aTransferInternationalDetails.setMoney(columns[6].getValue());
				}
				if (columns[7].getValue() != null) {
					aTransferInternationalDetails.setReferency(columns[7].getValue());
				}
				if (columns[8].getValue() != null) {
					aTransferInternationalDetails.setBeneficiaryName(columns[8].getValue());
				}
				if (columns[9].getValue() != null) {
					aTransferInternationalDetails.setBeneficiaryAddressComplete(columns[9].getValue());
				}
				if (columns[10].getValue() != null) {
					aTransferInternationalDetails.setBeneficiaryCountry(columns[10].getValue());
				}
				if (columns[11].getValue() != null) {
					aTransferInternationalDetails.setBeneficiaryCity(columns[11].getValue());
				}
				if (columns[12].getValue() != null) {
					aTransferInternationalDetails.setBeneficiaryAddress(columns[12].getValue());
				}
				if (columns[13].getValue() != null) {
					aTransferInternationalDetails.setBeneficiaryAccount(columns[13].getValue());
				}
				if (columns[14].getValue() != null) {
					aTransferInternationalDetails.setBankBeneficiaryCountry(columns[14].getValue());
				}
				if (columns[15].getValue() != null) {
					aTransferInternationalDetails.setBankBeneficiaryName(columns[15].getValue());
				}
				if (columns[16].getValue() != null) {
					aTransferInternationalDetails.setBankBeneficiaryDescription(columns[16].getValue());
				}
				if (columns[17].getValue() != null) {
					aTransferInternationalDetails.setBankBeneficiaryAddress(columns[17].getValue());
				}
				if (columns[18].getValue() != null) {
					aTransferInternationalDetails.setBankBeneficiarySwift(columns[18].getValue());
				}
				if (columns[19].getValue() != null) {
					aTransferInternationalDetails.setTypeAddress(columns[19].getValue());
				}
				if (columns[20].getValue() != null) {
					aTransferInternationalDetails.setBankIntermediaryCountry(columns[20].getValue());
				}
				if (columns[21].getValue() != null) {
					aTransferInternationalDetails.setBankIntermediaryName(columns[21].getValue());
				}
				if (columns[22].getValue() != null) {
					aTransferInternationalDetails.setBankIntermediaryDescription(columns[22].getValue());
				}
				if (columns[23].getValue() != null) {
					aTransferInternationalDetails.setBankIntermediaryAddress(columns[23].getValue());
				}
				if (columns[24].getValue() != null) {
					aTransferInternationalDetails.setBankIntermediarySwift(columns[24].getValue());
				}
				if (columns[25].getValue() != null) {
					aTransferInternationalDetails.setTypeAddressIntermediary(columns[25].getValue());
				}
				if (columns[26].getValue() != null) {
					aTransferInternationalDetails.setCostTransaction(Double.parseDouble(columns[26].getValue()));
				}
				if (columns[27].getValue() != null) {
					aTransferInternationalDetails.setBeneficiaryContinentCode(columns[27].getValue());
				}
				if (columns[28].getValue() != null) {
					aTransferInternationalDetails.setBeneficiaryContinent(columns[28].getValue());
				}
				if (columns[29].getValue() != null) {
					aTransferInternationalDetails.setTransactionCode(columns[29].getValue());
				}
				if (columns[30].getValue() != null) {
					aTransferInternationalDetails.setMessageType(columns[30].getValue());
				}
				if (columns[31].getValue() != null) {
					aTransferInternationalDetails.setSucursalCode(Integer.parseInt(columns[31].getValue()));
				}
				if (columns[32].getValue() != null) {
					aTransferInternationalDetails.setSucursal(columns[32].getValue());
				}
				if (columns[33].getValue() != null) {
					aTransferInternationalDetails.setBankBeneficiaryId(Integer.parseInt(columns[33].getValue()));
				}
				if (columns[34].getValue() != null) {
					aTransferInternationalDetails.setBeneficiaryCountryId(Integer.parseInt(columns[34].getValue()));
				}
				if (columns[35].getValue() != null) {
					aTransferInternationalDetails.setBeneficiaryCityId(Integer.parseInt(columns[35].getValue()));
				}
				if (columns[36].getValue() != null) {
					aTransferInternationalDetails.setPayerCity(columns[36].getValue());
				}
				if (columns[37].getValue() != null) {
					aTransferInternationalDetails.setPayerName(columns[37].getValue());
				}
				if (columns[38].getValue() != null) {
					aTransferInternationalDetails.setId(Integer.parseInt(columns[38].getValue()));
				}
				if (columns[39].getValue() != null) {
					aTransferInternationalDetails.setBenCountryId(Integer.parseInt(columns[39].getValue()));
				}
				if (columns[40].getValue() != null) {
					aTransferInternationalDetails.setBenCityId(Integer.parseInt(columns[40].getValue()));
				}

				if (columns.length == 49 || columns.length == 60) {
					if (columns[41].getValue() != null) {
						aTransferInternationalDetails.setBcoSwiftBen(columns[41].getValue());
					}
					if (columns[42].getValue() != null) {
						aTransferInternationalDetails.setBcoSwiftInter(columns[42].getValue());
					}
					if (columns[43].getValue() != null) {
						aTransferInternationalDetails.setBcoPaisBen(Integer.parseInt(columns[43].getValue()));
					}
					if (columns[44].getValue() != null) {
						aTransferInternationalDetails.setBcoPaisInter(Integer.parseInt(columns[44].getValue()));
					}
					if (columns[45].getValue() != null) {
						aTransferInternationalDetails.setBcoBenId(Integer.parseInt(columns[45].getValue()));
					}
					if (columns[46].getValue() != null) {
						aTransferInternationalDetails.setBcoInterId(Integer.parseInt(columns[46].getValue()));
					}
					if (columns[47].getValue() != null) {
						aTransferInternationalDetails.setBcoDirBenId(Integer.parseInt(columns[47].getValue()));
					}
					if (columns[48].getValue() != null) {
						aTransferInternationalDetails.setBcoDirInterId(Integer.parseInt(columns[48].getValue()));
					}
					wColumns = 49;
				}
				if (columns.length == 60) {
					if (columns[49].getValue() != null) {
						aTransferInternationalDetails.setBeneficiaryFirstLastName(columns[49].getValue());
					}
					if (columns[50].getValue() != null) {
						aTransferInternationalDetails.setBeneficiarySecondLastName(columns[50].getValue());
					}
					if (columns[51].getValue() != null) {
						aTransferInternationalDetails.setBeneficiaryBusinessName(columns[51].getValue());
					}
					if (columns[52].getValue() != null) {
						aTransferInternationalDetails.setBeneficiaryTypeDocument(columns[52].getValue());
					}
					if (columns[53].getValue() != null) {
						aTransferInternationalDetails.setBeneficiaryDocumentNumber(columns[53].getValue());
					}
					if (columns[54].getValue() != null) {
						aTransferInternationalDetails.setCurrencyIdUSD(Integer.parseInt(columns[54].getValue()));
					}
					if (columns[55].getValue() != null) {
						aTransferInternationalDetails.setQuote(Double.parseDouble(columns[55].getValue()));
					}
					if (columns[56].getValue() != null) {
						aTransferInternationalDetails.setBeneficiaryTypeDocumentName(columns[56].getValue());
					}
					if (columns[57].getValue() != null) {
						aTransferInternationalDetails.setCodeNegotiation(Integer.parseInt(columns[57].getValue()));
					}
					if (columns[58].getValue() != null) {
						aTransferInternationalDetails.setBeneficiaryEmail1(columns[58].getValue());
					}
					if (columns[59].getValue() != null) {
						aTransferInternationalDetails.setBeneficiaryEmail2(columns[59].getValue());
					}
					wColumns = 60;
				}

				aTransferInternationalDetailsList.add(aTransferInternationalDetails);
			}
			aTransferInternationalDetailsResponse
					.setTransferInternationalDetailsCollection(aTransferInternationalDetailsList);
			aTransferInternationalDetailsResponse.setColumns(wColumns);
		} else {
			Message[] message = Utils.returnArrayMessage(aProcedureResponse);
			aTransferInternationalDetailsResponse.setMessages(message);
		}
		aTransferInternationalDetailsResponse.setReturnCode(aProcedureResponse.getReturnCode());
		
		return aTransferInternationalDetailsResponse;
	}

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
