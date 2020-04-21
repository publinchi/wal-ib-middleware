/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.receiving.printing.archive;

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
import com.cobiscorp.ecobis.ib.application.dtos.InventoryLotRequest;
import com.cobiscorp.ecobis.ib.application.dtos.InventoryLotResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ReceivingPrintingArchiveRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ReceivingPrintingArchiveResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ReceivingPrintingArchive;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceReceivingPrintingArchive;

/**
 * <!-- Autor: Isaac Torres nombreClase : Se coloca el nombre de la clase java
 * funcion : Es un arreglo de tipo de datos ["String", "List", "int",...]
 * descripcion : Es un arreglo que contiene los nombre de atributos ["altura",
 * "edad", "peso"] descripcionClase: Lleva una breve descripción de la clase
 * numeroAtributos : Numero total de atributos de [1,...n]-->
 * 
 * <script type="text/javascript"> var nombreClase =
 * "GenerateDataScheduledPayment"; var funcion = [ "GenerateCustomerDataResponse
 * getGenerateCustomerData(GenerateCustomerDataRequest
 * generateCustomerDataRequest)" ]; var descripcion = [ "Recibe un objeto de
 * tipo AddressRequest y retorna otro de tipo AddressResponse" ]; var
 * descripcionClase = "Template de Informaci&oacute;n del cliente"; var
 * numeroFunciones = 1; </script>
 * 
 * <table>
 * <tbody>
 * <tr>
 * <th colspan="2" bgcolor="#CCCCFF"><div>Nombre Clase:
 * <script type="text/javascript">document.writeln(nombreClase);</script></th>
 * </tr>
 * <tr>
 * <td colspan="2"><div>Atributos</div></td>
 * </tr>
 * <tr>
 * <td width="auto" bgcolor="#CCCCFF"><div>Funci&oacute;n</div></td>
 * <td width="auto" bgcolor="#CCCCFF"><div>Descripci&oacute;n</div></td>
 * </tr>
 * <tr>
 * <td style="font-family:'Courier New', Courier, monospace; color:#906;"><div
 * align="left"><script type="text/javascript"> for(i=0;i<numeroFunciones;i++){
 * document.write(funcion[i]); document.write("<br />
 * "); }</script></td>
 * <td style=" font-family:'Courier New', Courier, monospace;color:#00F"><div
 * align="left"><script type="text/javascript"> for(i=0;i<numeroFunciones;i++){
 * document.write(descripcion[i]); document.write("<br />
 * "); }</script></td>
 * </tr>
 * 
 * <tr>
 * <td>Descripci&oacute;n Generica:</td>
 * <td><script type=
 * "text/javascript">document.writeln(descripcionClase);</script></td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * @author itorres
 * @since Oct 13, 2014
 * @version 1.0.0
 * @see AddressRequest
 * @see AddressResponse
 */

@Service(value = { ICoreServiceReceivingPrintingArchive.class, ICISSPBaseOrchestration.class, IOrchestrator.class })
@Component(name = "ReceivingPrintingArchiveTemplate", immediate = false)
@Properties(value = { @Property(name = "service.description", value = "ReceivingPrintingArchiveTemplate"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ReceivingPrintingArchiveTemplate") })
public class ReceivingPrintingArchiveTemplate extends SPJavaOrchestrationBase
		implements ICoreServiceReceivingPrintingArchive {
	private static final String CLASS_NAME = "ReceivingPrintingArchiveTemplate >-----> ";

	private static ILogger logger = LogFactory.getLogger(ReceivingPrintingArchive.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.commons.components.ICOBISComponent#loadConfiguration
	 * (com.cobiscorp.cobis.commons.configuration.IConfigurationReader)
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceReceivingPrintingArchive#getBatchProcessing(com.cobiscorp.
	 * ecobis.ib.application.dtos.ReceivingPrintingArchiveRequest)
	 */
	@Override
	public ReceivingPrintingArchiveResponse getBatchProcessing(
			ReceivingPrintingArchiveRequest aReceivingPrintingArchiveRequest)
			throws CTSServiceException, CTSInfrastructureException {
		// Context context = ContextManager.getContext();
		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		String CODE_TRN = "1801052";

		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, CODE_TRN);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		anOriginalRequest.setSpName("cob_bvirtual..sp_bv_archivo_imprenta");

		anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, CODE_TRN);

		anOriginalRequest.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR,
				aReceivingPrintingArchiveRequest.getOperation());

		anOriginalRequest.addInputParam("@i_lote", ICTSTypes.SQLINT4,
				aReceivingPrintingArchiveRequest.getLote().toString());

		anOriginalRequest.addInputParam("@i_tarjeta", ICTSTypes.SQLINT4,
				aReceivingPrintingArchiveRequest.getNextCard().toString());

		anOriginalRequest.addInputParam("@i_observacion", ICTSTypes.SQLVARCHAR,
				aReceivingPrintingArchiveRequest.getObservation());

		anOriginalRequest.addOutputParam("@o_num_reg", ICTSTypes.SQLINT4, "0");

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Enviando solicitud al Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Request a enviar: " + anOriginalRequest.getProcedureRequestAsString());

		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Data devuelta del Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Response obtenido: " + response.getProcedureResponseAsString());

		return transformToReceivingPrintingArchiveResponse(response);
	}
    
	private ReceivingPrintingArchiveResponse transformToReceivingPrintingArchiveResponse(
			IProcedureResponse aProcedureResponse) {

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "ReceivingPrintingArchiveResponse - RESPONSE TO TRANSFORM: "
					+ aProcedureResponse.getProcedureResponseAsString());

		ReceivingPrintingArchive aReceivingPrintingArchive = null;
		List<ReceivingPrintingArchive> aReceivingPrintingArchiveCollection = new ArrayList<ReceivingPrintingArchive>();
		ReceivingPrintingArchiveResponse aReceivingPrintingArchiveResponse = new ReceivingPrintingArchiveResponse();

		if (aProcedureResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsReceivingPrintingArchiveData = aProcedureResponse.getResultSet(1).getData()
					.getRowsAsArray();

			for (IResultSetRow iResultSetRow : rowsReceivingPrintingArchiveData) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

				aReceivingPrintingArchive = new ReceivingPrintingArchive();

				if (columns[0].getValue() != null) {
					aReceivingPrintingArchive.setLote(Integer.parseInt(columns[0].getValue()));
				}
				if (columns[1].getValue() != null) {
					aReceivingPrintingArchive.setNumCard(Integer.parseInt(columns[1].getValue()));
				}
				if (columns[2].getValue() != null) {
					aReceivingPrintingArchive.setUser(columns[2].getValue());
				}
				if (columns[3].getValue() != null) {
					aReceivingPrintingArchive.setObservation(columns[3].getValue());
				}

				aReceivingPrintingArchiveCollection.add(aReceivingPrintingArchive);
			}
			aReceivingPrintingArchiveResponse
					.setReceivingPrintingArchiveCollection(aReceivingPrintingArchiveCollection);
		} else {
			Message[] message = Utils.returnArrayMessage(aProcedureResponse);
			aReceivingPrintingArchiveResponse.setMessages(message);
		}

		if (aProcedureResponse.readValueParam("@o_num_reg") != null) {
			aReceivingPrintingArchiveResponse
					.setNumReg(Integer.parseInt(aProcedureResponse.readValueParam("@o_num_reg")));
		}
		aReceivingPrintingArchiveResponse.setReturnCode(aProcedureResponse.getReturnCode());

		return aReceivingPrintingArchiveResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase
	 * #executeJavaOrchestration
	 * (com.cobiscorp.cobis.cts.domains.IProcedureRequest, java.util.Map)
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
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase
	 * #processResponse(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceReceivingPrintingArchive#getGenerateBatch(com.cobiscorp.
	 * ecobis.ib.application.dtos.InventoryLotRequest)
	 */
	@Override
	public InventoryLotResponse getGenerateBatch(InventoryLotRequest aInventarioLoteRequest)
			throws CTSServiceException, CTSInfrastructureException {
		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		String CODE_TRN = "1801054";

		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, CODE_TRN);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		anOriginalRequest.setSpName("cobis..sp_bv_inventario_lote");
		anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, CODE_TRN);
		anOriginalRequest.addInputParam("@i_lote", ICTSTypes.SQLINT4, aInventarioLoteRequest.getLote().toString());
        
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Enviando solicitud al Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Request a enviar: " + anOriginalRequest.getProcedureRequestAsString());

		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Data devuelta del Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Response obtenido: " + response.getProcedureResponseAsString());

		return transformToInventoryLotResponse(response);
	}
	
	private InventoryLotResponse transformToInventoryLotResponse(IProcedureResponse aProcedureResponse) {

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "InventoryLotResponse - RESPONSE TO TRANSFORM: "
					+ aProcedureResponse.getProcedureResponseAsString());

		InventoryLotResponse aInventoryLotResponse = new InventoryLotResponse();

		if (aProcedureResponse.getReturnCode() == 0) {
			aInventoryLotResponse.setResponse(1);
		} else {
			Message[] message = Utils.returnArrayMessage(aProcedureResponse);
			aInventoryLotResponse.setMessages(message);
			aInventoryLotResponse.setResponse(0);
		}
		aInventoryLotResponse.setReturnCode(aProcedureResponse.getReturnCode());
		return aInventoryLotResponse;
	}
}
