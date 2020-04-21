package com.cobiscorp.ecobis.ib.orchestration.ib.cards;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.components.ComponentLocator;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.csp.services.inproc.IProvider;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IMessageBlock;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.dtos.MessageBlock;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.SummaryCreditCardRequest;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCardsQuery;

/**
 * This class implement methods for get information credit cards
 * 
 * @author cplua
 * @since Jun 19, 2014
 * @version 1.0.0
 */
@Component(name = "CreditCardsQuerys", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "CreditCardsQuerys"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "CreditCardsQuerys") })
public class CreditCardsQuerys extends SPJavaOrchestrationBase {
	protected static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";
	ILogger logger = this.getLogger();
	private static final String CLASS_NAME = " >-----> ";
	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceCardsQuery.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	private ICoreServiceCardsQuery ServiceCardsQuery;

	/**
	 * Instance Service Interface
	 * 
	 * @param service
	 */
	protected void bindCoreService(ICoreServiceCardsQuery service) {
		ServiceCardsQuery = service;
	}

	/**
	 * Deleting Service Interface
	 * 
	 * @param service
	 */
	protected void unbindCoreService(ICoreServiceCardsQuery service) {
		ServiceCardsQuery = null;
	}

	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";

	static final String OPERATION1_REQUEST = "OPERATION1_REQUEST";
	static final String OPERATION1_RESPONSE = "OPERATION1_RESPONSE";

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		if (logger.isInfoEnabled())
			logger.logInfo(
					"Component-Name: " + this.getClass().getSimpleName() + ", Component-Version: $$project.version$$");
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "executeJavaOrchestration  -- CreditCardsQuerys");
		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);

		Map<String, Object> wprocedureResponse1 = procedureResponse1(anOriginalRequest, aBagSPJavaOrchestration);
		Boolean wSuccessExecutionOperation1 = (Boolean) wprocedureResponse1.get("SuccessExecutionOperation");
		IProcedureResponse wIProcedureResponse1 = (IProcedureResponse) wprocedureResponse1.get("IProcedureResponse");
		IProcedureResponse wErrorProcedureResponse = (IProcedureResponse) wprocedureResponse1
				.get("ErrorProcedureResponse");

		if (wErrorProcedureResponse != null) {
			if (logger.isInfoEnabled())
				logger.logInfo("Entro por wErrorProcedureResponse");
			return wErrorProcedureResponse;
		}
		if (!wSuccessExecutionOperation1) {
			if (logger.isInfoEnabled())
				logger.logInfo("Entro por wIProcedureResponse1");
			return wIProcedureResponse1;
		}
		wIProcedureResponse1 = (IProcedureResponse) aBagSPJavaOrchestration.get(OPERATION1_RESPONSE);
		return wIProcedureResponse1;

	}

	protected Map<String, Object> procedureResponse1(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		String messageError = "El servicio no está disponible";
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "procedureResponse1");
		HashMap<String, Object> ret = new HashMap<String, Object>();
		ret.put("IProcedureResponse", null);
		ret.put("SuccessExecutionOperation", null);
		ret.put("ErrorProcedureResponse", null);

		IProcedureResponse wErrorProcedureResponse = validateParameters(aBagSPJavaOrchestration,
				new String[] { "@i_cliente" });

		if (wErrorProcedureResponse != null) {
			ret.put("ErrorProcedureResponse", wErrorProcedureResponse);
			return ret;
		}

		boolean wSuccessExecutionOperation1 = executeOperation1(aBagSPJavaOrchestration);
		ret.put("SuccessExecutionOperation", wSuccessExecutionOperation1);

		if (logger.isDebugEnabled())
			logger.logDebug("result exeuction operation 1: " + wSuccessExecutionOperation1);

		// if success execution
		if (!wSuccessExecutionOperation1) {

			IProcedureResponse wProcedureResponse = initProcedureResponse(anOriginalRequest);
			wProcedureResponse.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE,
					ICSP.ERROR_EXECUTION_SERVICE);

			MessageBlock mb = new MessageBlock();
			mb.setMessageText("Servicio no disponible");
			mb.setMessageNumber(201);

			wProcedureResponse.addResponseBlock(mb);
			wProcedureResponse.setReturnCode(201);

			ret.put("IProcedureResponse", wProcedureResponse);
			return ret;
		}

		IProcedureResponse wProcedureResponseOperation1 = initProcedureResponse(anOriginalRequest);
		wProcedureResponseOperation1.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE,
				ICSP.ERROR_EXECUTION_SERVICE);
		ret.put("IProcedureResponse", wProcedureResponseOperation1);
		return ret;

	}

	protected boolean executeOperation1(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "executeOperation1");

		String wServiceResponse;
		if (logger.isInfoEnabled())
			logger.logInfo("Ingresa a Procedure ProcedureRequestAS");
		ProcedureRequestAS wOriginalRequest = (ProcedureRequestAS) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		if (logger.isInfoEnabled())
			logger.logInfo("Sale de Procedure ProcedureRequestAS");
		wServiceResponse = service1(wOriginalRequest.readValueParam("@i_cliente"));
		// wServiceResponse =
		// "<?xml version=\"1.0\"?><TarCred_Cliente
		// xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"
		// xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><ParametroGeneralRespuesta><CodigoRespuesta>0</CodigoRespuesta><MensajeRespuesta>OK</MensajeRespuesta><Datos><DatosTarjeta><CodigoLinea>71321037</CodigoLinea><CodigoCuenta>6271741</CodigoCuenta><Marca>Visa</Marca><TipoTarjeta>Gold</TipoTarjeta><Mondeda>Bs</Mondeda><LimiteAprobado>4000</LimiteAprobado><SaldoDisponible>6000</SaldoDisponible><FechaActivacion>11/01/2015</FechaActivacion><FechaExpiracion>11/01/2020</FechaExpiracion><Estado>A</Estado><Seguro>S</Seguro><NombreTarjeta>CA</NombreTarjeta><Oficina>Camacho</Oficina></DatosTarjeta><DatosTarjeta><CodigoLinea>8888888888</CodigoLinea><CodigoCuenta>111122222</CodigoCuenta><Marca>Visa</Marca><TipoTarjeta>Gold</TipoTarjeta><Mondeda>$us</Mondeda><LimiteAprobado>4000</LimiteAprobado><SaldoDisponible>6000</SaldoDisponible><FechaActivacion>11/03/2014</FechaActivacion><FechaExpiracion>11/01/2019</FechaExpiracion><Estado>A</Estado><Seguro>S</Seguro><NombreTarjeta>CA</NombreTarjeta><Oficina>El
		// Tejar</Oficina></DatosTarjeta></Datos></ParametroGeneralRespuesta></TarCred_Cliente>";
		if (logger.isInfoEnabled())
			logger.logInfo("Sale de Procedure wServiceResponse");
		if (wServiceResponse != null) {
			IProcedureResponse wProcedureResponse = this.transformResponser(wServiceResponse, aBagSPJavaOrchestration);
			aBagSPJavaOrchestration.put(OPERATION1_RESPONSE, wProcedureResponse);
			if (logger.isInfoEnabled())
				logger.logInfo("Sale de Procedure transformResponser");
			// aBagSPJavaOrchestration.put(OPERATION1_RESPONSE,
			// wServiceResponse);
			return true;

		} else {
			return false;
		}

	}

	protected IProcedureRequest transformOperation1(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "transformOperation1");
		ProcedureRequestAS wOriginalRequest = (ProcedureRequestAS) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		ProcedureRequestAS wProcedureRequest = (ProcedureRequestAS) initProcedureRequest(wOriginalRequest);
		return wProcedureRequest;
	}

	protected SummaryCreditCardRequest transformOperation(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "transformOperation");
		ProcedureRequestAS wOriginalRequest = (ProcedureRequestAS) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		SummaryCreditCardRequest summaryCreditCardRequest = new SummaryCreditCardRequest();
		Client client = new Client();
		client.setId(wOriginalRequest.readValueParam("@i_cliente"));
		summaryCreditCardRequest.setClient(client);
		return summaryCreditCardRequest;
	}

	public IProcedureResponse validateParameters(Map<String, Object> aBagSPJavaOrchestration, String[] aParams) {

		IProcedureRequest wOriginalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		if (logger.isDebugEnabled()) {
			logger.logDebug("validate parameters sp: " + wOriginalRequest.getSpName());
		}

		List<MessageBlock> wErrorMessages = new ArrayList<MessageBlock>(aParams.length);
		boolean wError = false;
		for (int i = 0; i < aParams.length; i++) {
			if (logger.isDebugEnabled()) {
				logger.logDebug("validate parameter: " + aParams[i]);
			}

			if (wOriginalRequest.readParam(aParams[i]) == null) {
				wError = true;
				MessageBlock wMessageBlock = new MessageBlock();
				// 201 number of error in sybase when a parameter is expected
				wMessageBlock.setMessageNumber(201);
				wMessageBlock.setMessageText("Procedure " + wOriginalRequest.getSpName() + " expects parameter"
						+ aParams[i] + " , which was not supplied.");
				wErrorMessages.add(wMessageBlock);
			}

		}

		if (wError == false)
			return null;

		IProcedureResponse wProcedureResponse = processResponse(wOriginalRequest, aBagSPJavaOrchestration);
		wProcedureResponse.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
		Iterator<MessageBlock> wIterator = wErrorMessages.iterator();

		while (wIterator.hasNext()) {
			MessageBlock wMessageBlockTemp = wIterator.next();
			wProcedureResponse.addMessage(wMessageBlockTemp.getMessageNumber(), wMessageBlockTemp.getMessageText());
		}
		wProcedureResponse.setReturnCode(201);

		return wProcedureResponse;
	}

	/*********************
	 * Transformación de Response a ProcedureResponseBy
	 ***********************/

	private IProcedureResponse transformResponser(String xml, Map<String, Object> aBagSPJavaOrchestration) {
		// String xml =
		// "<?xml version=\"1.0\"?><TarCred_Cliente
		// xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"
		// xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><ParametroGeneralRespuesta><CodigoRespuesta>0</CodigoRespuesta><MensajeRespuesta>OK</MensajeRespuesta><Datos><DatosTarjeta><CodigoLinea>71321037</CodigoLinea><CodigoCuenta>6271741</CodigoCuenta><Marca>Visa</Marca><TipoTarjeta>Gold</TipoTarjeta><Mondeda>Bs</Mondeda><LimiteAprobado>4000</LimiteAprobado><SaldoDisponible>6000</SaldoDisponible><FechaActivacion>11/01/2015</FechaActivacion><FechaExpiracion>11/01/2020</FechaExpiracion><Estado>A</Estado><Seguro>S</Seguro><NombreTarjeta>CA</NombreTarjeta><Oficina>Camacho</Oficina></DatosTarjeta><DatosTarjeta><CodigoLinea>8888888888</CodigoLinea><CodigoCuenta>111122222</CodigoCuenta><Marca>Visa</Marca><TipoTarjeta>Gold</TipoTarjeta><Mondeda>$us</Mondeda><LimiteAprobado>4000</LimiteAprobado><SaldoDisponible>6000</SaldoDisponible><FechaActivacion>11/03/2014</FechaActivacion><FechaExpiracion>11/01/2019</FechaExpiracion><Estado>A</Estado><Seguro>S</Seguro><NombreTarjeta>CA</NombreTarjeta><Oficina>El
		// Tejar</Oficina></DatosTarjeta></Datos></ParametroGeneralRespuesta></TarCred_Cliente>";
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		int codRes = -1;
		String menRes = "";

		try {
			db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			try {
				Document doc = db.parse(is);
				NodeList cod = doc.getElementsByTagName("CodigoRespuesta");
				NodeList men = doc.getElementsByTagName("MensajeRespuesta");

				for (int i = 0; i < cod.getLength(); i++) {
					String res1 = cod.item(i).getTextContent();
					codRes = Integer.parseInt(res1);
					menRes = men.item(i).getNodeValue();
				}
				// list.item(0)
				// System.out.println(message);
			} catch (SAXException e) {
				// handle SAXException
				e.printStackTrace();
				logger.logError(e.getMessage());

			} catch (IOException e) {
				// handle IOException
				e.printStackTrace();
				logger.logError(e.getMessage());

			}
		} catch (ParserConfigurationException e1) {
			// handle ParserConfigurationException
			e1.printStackTrace();
			logger.logError(e1.getMessage());

		}

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response By Number");

		if (codRes != 0) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
					com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils.returnException(menRes));
			return com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils.returnException(menRes);
		} else {
			// Agregar Header

			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();
			metaData.addColumnMetaData(new ResultSetHeaderColumn("codLinea", ICTSTypes.SQLVARCHAR, 12));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("codCuenta", ICTSTypes.SYBFLT8i, 30));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("marca", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("tipoTar", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("moneda", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("limAut", ICTSTypes.SYBFLT8i, 30));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("salDispo", ICTSTypes.SQLFLT8i, 30));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("fecAct", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("fecExp", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("estado", ICTSTypes.SQLVARCHAR, 1));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("seguro", ICTSTypes.SQLVARCHAR, 1));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("nomTar", ICTSTypes.SQLVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("oficina", ICTSTypes.SQLVARCHAR, 20));

			try {
				db = dbf.newDocumentBuilder();
				InputSource is = new InputSource();
				is.setCharacterStream(new StringReader(xml));
				try {
					Document doc = db.parse(is);

					NodeList listdatos = doc.getElementsByTagName("DatosTarjeta");
					for (int i = 0; i < listdatos.getLength(); i++) {

						NodeList hijos = listdatos.item(i).getChildNodes();
						for (int j = 0; j < hijos.getLength(); j++) {
							if (logger.isDebugEnabled())
								logger.logDebug("Datos " + i + " : " + hijos.item(j).getNodeName() + ": "
										+ hijos.item(j).getTextContent());
						}

					}

					NodeList list = doc.getElementsByTagName("DatosTarjeta");
					for (int i = 0; i < list.getLength(); i++) {
						if (logger.isDebugEnabled())
							logger.logDebug("Ingreso al for");
						NodeList elements = list.item(i).getChildNodes();

						IResultSetRow row = new ResultSetRow();

						row.addRowData(1, new ResultSetRowColumnData(false, elements.item(0).getTextContent()));
						row.addRowData(2, new ResultSetRowColumnData(false, elements.item(1).getTextContent()));
						row.addRowData(3, new ResultSetRowColumnData(false, elements.item(2).getTextContent()));
						row.addRowData(4, new ResultSetRowColumnData(false, elements.item(3).getTextContent()));
						row.addRowData(5, new ResultSetRowColumnData(false, elements.item(4).getTextContent()));
						row.addRowData(6, new ResultSetRowColumnData(false, elements.item(5).getTextContent()));
						row.addRowData(7, new ResultSetRowColumnData(false, elements.item(6).getTextContent()));
						row.addRowData(8, new ResultSetRowColumnData(false, elements.item(7).getTextContent()));
						row.addRowData(9, new ResultSetRowColumnData(false, elements.item(8).getTextContent()));
						row.addRowData(10, new ResultSetRowColumnData(false, elements.item(9).getTextContent()));
						row.addRowData(11, new ResultSetRowColumnData(false, elements.item(10).getTextContent()));
						row.addRowData(12, new ResultSetRowColumnData(false, elements.item(11).getTextContent()));
						row.addRowData(13, new ResultSetRowColumnData(false, elements.item(12).getTextContent()));
						// row.addRowData(14, new ResultSetRowColumnData(false,
						// elements.item(13).getTextContent()));

						data.addRow(row);
					}
					// list.item(0)
					// System.out.println(message);
				} catch (SAXException e) {
					// handle SAXException
					e.printStackTrace();

					logger.logError(e.getMessage());

				} catch (IOException e) {
					// handle IOException
					e.printStackTrace();
					logger.logError(e.getMessage());

				}
			} catch (ParserConfigurationException e1) {
				// handle ParserConfigurationException
				e1.printStackTrace();

				logger.logError(e1.getMessage());

			}

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
			wProcedureResponse.addResponseBlock(resultBlock);
		}

		if (logger.isDebugEnabled())
			logger.logDebug(
					"transformProcedureResponseByNumber Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;

	}

	/**
	 * Copy messages from sourceResponse to targetResponse
	 * 
	 * @param aSourceResponse
	 * @param aTargetResponse
	 */
	public static void addMessagesFromResponse(IProcedureResponse aSourceResponse, IProcedureResponse aTargetResponse) {

		if (aSourceResponse.getMessages() != null) {
			@SuppressWarnings("rawtypes")
			Iterator wIterator = aSourceResponse.getMessages().iterator();
			while (wIterator.hasNext()) {
				Object wOMessage = wIterator.next();
				if (wOMessage instanceof IMessageBlock) {
					IMessageBlock wBlock = (IMessageBlock) wOMessage;
					aTargetResponse.addMessage(wBlock.getMessageNumber(), wBlock.getMessageText());
				}
			}
		}
		aTargetResponse.setReturnCode(aSourceResponse.getReturnCode());
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalProcedureReq,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalProcedureReq);
		return wProcedureRespFinal;
	}

	public String service1(String idCliente) {

		ComponentLocator locator = ComponentLocator.getInstance(SPJavaOrchestrationBase.class);
		IProvider provider = (IProvider) locator.find(IProvider.class);
		IProcedureRequest procedureRequest = new ProcedureRequestAS();
		procedureRequest.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "1");
		procedureRequest.addFieldInHeader("channel", ICOBISTS.HEADER_STRING_TYPE, "8");
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_STRING_TYPE, "2");
		procedureRequest.addFieldInHeader("com.cobiscorp.cobis.csp.services.ICSPExecutorConnector",
				ICOBISTS.HEADER_STRING_TYPE, "(service.identifier=CreditCardConectorQuery)");
		procedureRequest.addFieldInHeader("csp.skip.transformation", ICOBISTS.HEADER_STRING_TYPE, ICOBISTS.YES);
		procedureRequest.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, ICSP.SUCCESS);

		procedureRequest.addInputParam("@i_operacion", ICTSTypes.SYBVARCHAR, "C");
		procedureRequest.addInputParam("@i_codigo_cliente", ICTSTypes.SYBVARCHAR, idCliente);

		IProcedureResponse wProcedureResp = provider.executeProvider(procedureRequest);
		wProcedureResp.getReturnCode();

		String respuesta = wProcedureResp.readValueParam("@o_message");
		return respuesta;
	}

}
