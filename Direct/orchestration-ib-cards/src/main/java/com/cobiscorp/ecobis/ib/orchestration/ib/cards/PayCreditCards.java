package com.cobiscorp.ecobis.ib.orchestration.ib.cards;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
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

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.components.ComponentLocator;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.domains.ICSP;
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
import com.cobiscorp.ecobis.ib.application.dtos.PaymentServiceRequest;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCardsQuery;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.IServicePayCreditCard;

/**
 * This class implement methods for get information credit cards
 * 
 * @author cplua
 * @since Jun 19, 2014
 * @version 1.0.0
 */
@Component(name = "PayCreditCards", immediate = false)
@Service(value = IServicePayCreditCard.class)
@Properties(value = { @Property(name = "service.description", value = "PayCreditCards"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "PayCreditCards") })
public class PayCreditCards implements IServicePayCreditCard {
	protected static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";
	ILogger logger = LogFactory.getLogger(PayCreditCards.class);
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

	protected boolean executeOperation1(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "executeOperation1");

		String wServiceResponse;
		PaymentServiceRequest paymentService = transformOperation(aBagSPJavaOrchestration);
		wServiceResponse = service1(paymentService);
		// wServiceResponse =
		// "<?xml version=\"1.0\"?><TarCred_Cliente
		// xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"
		// xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><ParametroGeneralRespuesta><CodigoRespuesta>0</CodigoRespuesta><MensajeRespuesta>OK</MensajeRespuesta><Datos><DatosTarjeta><CodigoCliente>71321037</CodigoCliente><pagoMin>250</pagoMin><monto>250</monto><Mondeda>Bs</Mondeda><fecExp>11/01/2015</fecExp><CodigoCuenta>6271741</CodigoCuenta><fechaVenc>11/01/2015</fechaVenc><salDispo>250</salDispo><estado>250</estado></DatosTarjeta></Datos></ParametroGeneralRespuesta></TarCred_Cliente>";
		IProcedureResponse wProcedureResponse = this.transformResponser(wServiceResponse, aBagSPJavaOrchestration);
		aBagSPJavaOrchestration.put(OPERATION1_RESPONSE, wProcedureResponse);
		return true;
	}

	protected IProcedureRequest transformOperation1(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "transformOperation1");
		ProcedureRequestAS wOriginalRequest = (ProcedureRequestAS) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		// ProcedureRequestAS wProcedureRequest = (ProcedureRequestAS)
		// initProcedureRequest(wOriginalRequest);
		return wOriginalRequest;
	}

	protected PaymentServiceRequest transformOperation(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "transformOperation");
		ProcedureRequestAS wOriginalRequest = (ProcedureRequestAS) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		PaymentServiceRequest paymentServiceRequest = new PaymentServiceRequest();
		paymentServiceRequest.setCodeLine(wOriginalRequest.readValueParam("@i_cod_linea"));
		paymentServiceRequest.setIdClient(Integer.parseInt(wOriginalRequest.readValueParam("@i_cliente")));
		paymentServiceRequest.setDocumentId(wOriginalRequest.readValueParam("@i_doc_cliente"));
		paymentServiceRequest.setValue(Double.parseDouble(wOriginalRequest.readValueParam("@i_monto")));
		paymentServiceRequest.setCurrency(wOriginalRequest.readValueParam("@i_moneda"));
		paymentServiceRequest.setFecha(wOriginalRequest.readValueParam("@i_fecha_mov"));
		return paymentServiceRequest;
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
		String messageError = null;
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
				if (logger.isDebugEnabled())
					logger.logDebug(messageError);
				return null;
			} catch (IOException e) {
				// handle IOException
				e.printStackTrace();
				if (logger.isDebugEnabled())
					logger.logDebug(messageError);
				return null;
			}
		} catch (ParserConfigurationException e1) {
			// handle ParserConfigurationException
			e1.printStackTrace();
			if (logger.isDebugEnabled())
				logger.logDebug(messageError);
			return null;
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

			metaData.addColumnMetaData(new ResultSetHeaderColumn("responseCode", ICTSTypes.SYBFLT8i, 12));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("descriptionCode", ICTSTypes.SQLVARCHAR, 30));

			try {
				db = dbf.newDocumentBuilder();
				InputSource is = new InputSource();
				is.setCharacterStream(new StringReader(xml));
				try {
					Document doc = db.parse(is);

					NodeList listdatos = doc.getElementsByTagName("Datos");
					for (int i = 0; i < listdatos.getLength(); i++) {

						NodeList hijos = listdatos.item(i).getChildNodes();
						for (int j = 0; j < hijos.getLength(); j++) {
							if (logger.isDebugEnabled())
								logger.logDebug("Datos " + i + " : " + hijos.item(j).getNodeName() + ": "
										+ hijos.item(j).getTextContent());
						}

					}

					NodeList list = doc.getElementsByTagName("Datos");
					for (int i = 0; i < list.getLength(); i++) {
						NodeList elements = list.item(i).getChildNodes();

						IResultSetRow row = new ResultSetRow();

						row.addRowData(1, new ResultSetRowColumnData(false, elements.item(0).getTextContent()));
						aBagSPJavaOrchestration.put("codigoResponse", elements.item(0).getTextContent());
						row.addRowData(2, new ResultSetRowColumnData(false, elements.item(1).getTextContent()));

						data.addRow(row);
					}
					// list.item(0)
					// System.out.println(message);
				} catch (SAXException e) {
					// handle SAXException
					e.printStackTrace();
					if (logger.isDebugEnabled())
						logger.logDebug(messageError);
					return null;
				} catch (IOException e) {
					// handle IOException
					e.printStackTrace();
					if (logger.isDebugEnabled())
						logger.logDebug(messageError);
					return null;
				}
			} catch (ParserConfigurationException e1) {
				// handle ParserConfigurationException
				e1.printStackTrace();
				if (logger.isDebugEnabled())
					logger.logDebug(messageError);
				return null;
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
		@SuppressWarnings("rawtypes")
		Iterator wIterator = aSourceResponse.getMessages().iterator();
		while (wIterator.hasNext()) {
			Object wOMessage = wIterator.next();
			if (wOMessage instanceof IMessageBlock) {
				IMessageBlock wBlock = (IMessageBlock) wOMessage;
				aTargetResponse.addMessage(wBlock.getMessageNumber(), wBlock.getMessageText());
			}
		}
		aTargetResponse.setReturnCode(aSourceResponse.getReturnCode());
	}

	public IProcedureResponse processResponse(IProcedureRequest anOriginalProcedureReq,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureRespFinal = new ProcedureResponseAS();
		return wProcedureRespFinal;
	}

	public String service1(PaymentServiceRequest paramInput) {

		ComponentLocator locator = ComponentLocator.getInstance(SPJavaOrchestrationBase.class);
		IProvider provider = (IProvider) locator.find(IProvider.class);
		IProcedureRequest procedureRequest = new ProcedureRequestAS();
		procedureRequest.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "1");
		procedureRequest.addFieldInHeader("channel", ICOBISTS.HEADER_STRING_TYPE, "8");
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_STRING_TYPE, "2");
		procedureRequest.addFieldInHeader("com.cobiscorp.cobis.csp.services.ICSPExecutorConnector",
				ICOBISTS.HEADER_STRING_TYPE, "(service.identifier=CreditCardConectorPayment)");
		procedureRequest.addFieldInHeader("csp.skip.transformation", ICOBISTS.HEADER_STRING_TYPE, ICOBISTS.YES);
		procedureRequest.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, ICSP.SUCCESS);

		procedureRequest.addInputParam("@i_operacion", ICTSTypes.SYBVARCHAR, "C");
		procedureRequest.addInputParam("@i_cod_linea", ICTSTypes.SYBVARCHAR, String.valueOf(paramInput.getCodeLine()));
		procedureRequest.addInputParam("@i_cliente", ICTSTypes.SYBVARCHAR, String.valueOf(paramInput.getIdClient()));
		procedureRequest.addInputParam("@i_doc_cliente", ICTSTypes.SYBVARCHAR, paramInput.getDocumentId());
		procedureRequest.addInputParam("@i_monto", ICTSTypes.SYBVARCHAR, String.valueOf(paramInput.getValue()));
		procedureRequest.addInputParam("@i_moneda", ICTSTypes.SYBVARCHAR, paramInput.getCurrency());
		procedureRequest.addInputParam("@i_fecha_mov", ICTSTypes.SYBVARCHAR, paramInput.getFecha());

		IProcedureResponse wProcedureResp = provider.executeProvider(procedureRequest);
		wProcedureResp.getReturnCode();

		String respuesta = wProcedureResp.readValueParam("@o_message");
		return respuesta;
	}

	@Override
	public boolean payCreditCard(Map<String, Object> aBagSPJavaOrchestration) {
		return executeOperation1(aBagSPJavaOrchestration);

	}

}
