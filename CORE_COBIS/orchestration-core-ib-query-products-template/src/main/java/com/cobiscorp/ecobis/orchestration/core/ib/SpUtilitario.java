package com.cobiscorp.ecobis.orchestration.core.ib;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.cobiscorp.cobis.commons.components.ComponentLocator;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.exceptions.COBISInfrastructureRuntimeException;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.util.CSPUtil;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSMessage;
import com.cobiscorp.cobis.cts.domains.IHeaderField;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureRequestParam;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.IProcedureResponseParam;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ErrorBlock;
import com.cobiscorp.cobis.cts.dtos.MessageBlock;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.cobis.cts.services.orchestrator.ISPOrchestrator;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.IChannelService;

public final class SpUtilitario {

	private final static ILogger logger = LogFactory.getLogger(SpExecutor.class);
	private static ComponentLocator locator = null;
	private static ISPOrchestrator executor = null;
	private static Map<String, String> channelServices = new HashMap<String, String>();

	public static IProcedureResponse ejecutarSpLocal(IProcedureRequest procedureRequest)
			throws CTSServiceException, CTSInfrastructureException {
		IProcedureResponse procedureResponse = null;
		init();
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		if (logger.isDebugEnabled())
			logger.logDebug("Se va a ejecutar el sp: " + procedureRequest.getProcedureRequestAsString());
		procedureResponse = executor.executeSP(procedureRequest);
		return procedureResponse.parseMessageData();
	}

	/***
	 * Ejecuta un servicio o sp del CORE dependiendo si el nombre de sp esta o
	 * no en el archivo config-channel-adm.xml
	 * 
	 * @param procedureRequest
	 *            ProcedureRequest que contiene los parametros de entrada
	 * @return
	 * @throws CTSInfrastructureException
	 * @throws CTSServiceException
	 */
	public static IProcedureResponse ejecutarSpCore(IProcedureRequest procedureRequest)
			throws CTSServiceException, CTSInfrastructureException {
		IProcedureResponse procedureResponse = null;
		String filtro = null;
		String origSpName;
		if (procedureRequest == null) {
			throw new COBISInfrastructureRuntimeException("El parametro procedureRequest no puede ser nulo");
		}
		origSpName = procedureRequest.getSpName();
		init();
		if (channelServices.containsKey(origSpName)) {
			filtro = channelServices.get(origSpName);
		}
		if (filtro != null) {
			// se ejecuta el servicio que implementa el sp
			IChannelService channelService = locator.find(IChannelService.class, "(service.impl=" + filtro + ")");
			if (channelService == null) {
				throw new COBISInfrastructureRuntimeException("No existe implementacion para: " + IChannelService.class
						+ " con filtro: service.impl=" + filtro);
			}
			if (logger.isDebugEnabled())
				logger.logDebug("Se va a ejecutar el servicio: " + channelService.getClass().getName()
						+ " con parametro: " + procedureRequest.getProcedureRequestAsString());
			procedureResponse = channelService.executeService(procedureRequest);
		} else {
			if (logger.isDebugEnabled())
				logger.logDebug("Se va a ejecutar el sp en target"
						+ procedureRequest.readValueFieldInHeader(ICOBISTS.HEADER_TARGET_ID) + " content: "
						+ procedureRequest.getProcedureRequestAsString());
			procedureResponse = executor.executeSP(procedureRequest);
			procedureResponse = procedureResponse.parseMessageData();
		}

		return procedureResponse;
	}

	/***
	 * Añade un mensaje de error al ProcedureResponse
	 * 
	 * @param procedureResponse
	 *            ProcedureResponse al que se le va a añadir el error
	 * @param codigo
	 *            código numérico del error
	 * @param mensaje
	 *            texto del error
	 */
	public static void crearError(IProcedureResponse procedureResponse, int codigo, String mensaje) {
		ErrorBlock errorBlock = new ErrorBlock(codigo, mensaje);
		procedureResponse.addResponseBlock(errorBlock);
	}

	/***
	 * Añade un mensaje de advertencia al ProcedureResponse
	 * 
	 * @param procedureResponse
	 *            ProcedureResponse al que se le va a añadir la advertencia
	 * @param codigoa
	 *            código numérico de la advertencia
	 * @param mensaje
	 *            texto de la advertencia
	 */
	public static void crearWarning(IProcedureResponse procedureResponse, int codigo, String mensaje) {
		MessageBlock messageBlock = new MessageBlock(codigo, mensaje);
		procedureResponse.addResponseBlock(messageBlock);
	}

	/***
	 * Crea un nuevo procedure response, este método se debe utilizar en el caso
	 * de mapeo con objetos
	 * 
	 * @param procedureRequest
	 *            ProcedureRequest del cual se toman los campos del mensaje CTS
	 *            para colocarlos en el ProcedureResponse
	 * @return el nuevo ProcedureResponse, no contiene header ni data, solo los
	 *         campos del mensaje CTS
	 */
	public static IProcedureResponse crearProcedureResponse(ICTSMessage procedureRequest) {
		IProcedureResponse procedureResponse = new ProcedureResponseAS();
		// copiar campos del request al response
		Iterator it = procedureRequest.getFields().iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj instanceof IHeaderField) {
				IHeaderField field = (IHeaderField) obj;
				procedureResponse.addFieldInHeader(field.getName(), field.getType(), field.getValue());
			}
		}
		return procedureResponse;
	}

	/***
	 * Añade una nueva columna al header del ResultSet especificado en el
	 * parámetro numeroRs
	 * 
	 * @param procedureResponse
	 *            ProcedureResponse al cual se le añade la nueva columna
	 * @param numeroRs
	 *            Número de ResultSet a modificar, el indice comienza en 1
	 * @param nombre
	 *            Nombre de la columna a añadir
	 * @param tipoDato
	 *            Tipo de deato de la columna a añadir
	 * @param longitud
	 *            Longitud de la columna a añadir, es obligatorio para los tipos
	 *            de datos de longitud variable
	 */
	public static void crearColumna(IProcedureResponse procedureResponse, int numeroRs, String nombre, int tipoDato,
			int longitud) {
		IResultSetBlock rsBlock = procedureResponse.getResultSet(numeroRs);
		IResultSetHeader header = rsBlock.getMetaData();
		IResultSetHeaderColumn column = new ResultSetHeaderColumn(nombre, tipoDato, longitud);
		header.addColumnMetaData(column);
	}

	/***
	 * Añade una nueva fila de datos al ResultSet especificado en el parámetro
	 * numeroRs
	 * 
	 * @param procedureResponse
	 *            ProcedureResponse al cual se le añade la nueva fila de datos
	 * @param numeroRs
	 *            Número de ResultSet a modificar, el indice comienza en 1
	 * @param items
	 *            Arreglo de objetos a ser considerados como datos de la nueva
	 *            fila, los datos de las columnas se toman en orden secuencial
	 */
	public static void crearFilaDato(IProcedureResponse procedureResponse, int numeroRs, Object[] items) {
		IResultSetBlock rsBlock = procedureResponse.getResultSet(numeroRs);
		IResultSetData data = rsBlock.getData();
		IResultSetRow row = new ResultSetRow();
		int i = 1;
		for (Object item : items) {
			boolean isNull = true;
			String valorTexto = null;
			if (item != null) {
				// FALTA: dar el formato de acuerdo al tipo de dato
				valorTexto = item.toString();
				isNull = false;
			}
			IResultSetRowColumnData column = new ResultSetRowColumnData(isNull, valorTexto);
			row.addRowData(i++, column);
		}
		data.addRow(row);
	}

	/***
	 * Añade un nuevo ResultSet al ProcedureResponse
	 * 
	 * @param procedureResponse
	 *            ProcedureResponse al cual se va a añadir el ResultSet
	 */
	public static void crearResultSet(IProcedureResponse procedureResponse) {
		IResultSetHeader header = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		IResultSetBlock rsBlock = new ResultSetBlock(header, data);
		procedureResponse.addResponseBlock(rsBlock);
	}

	/***
	 * Cambia el orden de las columnas del ResultSet, el movimiento se da tanto
	 * en cabecera como en la data
	 * 
	 * @param procedureResponse
	 *            ProcedureResponse a modificar
	 * @param numeroRs
	 *            Número de ResultSet a modificar, el indice comienza en 1
	 * @param ordenColumnas
	 *            Arreglo de las columnas a permutar, por ejemplo: si el arreglo
	 *            contiene los numeros 4, 2, 3, 1; el nuevo resultSet tendra las
	 *            columnas en este orden: C4, C2, C3, C1 del ResulSet original,
	 *            es decir la columna 4 del ResultOriginal la mueve a la columna
	 *            1 del nuevo ResultSet, la columna 2 se mueve a la posición 2 y
	 *            asi en adelante
	 * @return El nuevo ProcedureResponse con las columnas permutadas
	 */
	public static IProcedureResponse cambiarOrdenColumnas(IProcedureResponse procedureResponse, int numeroRs,
			int[] ordenColumnas) {
		IProcedureResponse procedureResponse2 = crearProcedureResponse(procedureResponse);
		for (int i = 1; i <= procedureResponse.getResultSetListSize(); i++) {
			// se copia la cabecera
			IResultSetHeader rsHeader2 = copiarHeader(procedureResponse, numeroRs, ordenColumnas);
			// se copian los datos
			ResultSetData rsData2 = null;
			if (i == numeroRs) {
				rsData2 = copiarData(procedureResponse, numeroRs, ordenColumnas);
			} else {
				rsData2 = copiarData(procedureResponse, numeroRs, null);
			}
			IResultSetBlock rsBlock2 = new ResultSetBlock(rsHeader2, rsData2);
			procedureResponse2.addResponseBlock(rsBlock2);
		}
		return procedureResponse2;
	}

	/***
	 * Cambia el nombre de la columna del ResultSet especificado en el parámetro
	 * numeroRs
	 * 
	 * @param procedureResponse
	 *            ProcedureResponse a modificar
	 * @param numeroRs
	 *            Número de ResultSet a modificar, el indice comienza en 1
	 * @param nombreAnterior
	 *            Nombre actual de la columna
	 * @param nombreNuevo
	 *            Nuevo nombre de la columna
	 */
	public static void cambiarNombreColumna(IProcedureResponse procedureResponse, int numeroRs, String nombreAnterior,
			String nombreNuevo) {
		IResultSetHeader rsHeader = procedureResponse.getResultSetMetaData(numeroRs);
		for (int i = 1; i <= rsHeader.getColumnsNumber(); i++) {
			ResultSetHeaderColumn col = (ResultSetHeaderColumn) rsHeader.getColumnMetaData(i);
			if (col.getName().equals(nombreAnterior)) {
				col.setName(nombreNuevo);
				break;
			}
		}
	}

	/***
	 * Cambia el nombre de un parametro ya sea en el ProcedureRequest o
	 * ProcedureResponse, de acuerdo al tipo de dato que se pase en el parámetro
	 * procedureReqRes
	 * 
	 * @param procedureReqRes
	 *            Se debe pasar un objeto de tipo IProcedureRequest o
	 *            IProcedureResponse
	 * @param nombreAnterior
	 *            Nombre actual del parámetro
	 * @param nombreNuevo
	 *            Nuevo nombre del parámetro
	 */
	public static void cambiarNombreParametro(Object procedureReqRes, String nombreAnterior, String nombreNuevo) {
		IProcedureRequestParam parametroReq = null;
		IProcedureResponseParam parametroRes = null;
		if (isProcedureRequest(procedureReqRes)) {
			IProcedureRequest procedureRequest = (IProcedureRequest) procedureReqRes;
			parametroReq = procedureRequest.readParam(nombreAnterior);
			if (parametroReq == null) {
				throw new COBISInfrastructureRuntimeException("No existe el parametro: " + nombreAnterior);
			}
			procedureRequest.removeParam(nombreAnterior);
			procedureRequest.addParam(nombreNuevo, parametroReq.getDataType(), parametroReq.getIOType(),
					parametroReq.getLen(), parametroReq.getValue());
		} else {
			IProcedureResponse procedureResponse = (IProcedureResponse) procedureReqRes;
			parametroRes = procedureResponse.readParam(nombreAnterior);
			if (parametroRes == null) {
				throw new COBISInfrastructureRuntimeException("No existe el parametro: " + nombreAnterior);
			}
			procedureResponse.removeParam(nombreAnterior);
			procedureResponse.addParam(nombreNuevo, parametroRes.getDataType(), parametroRes.getLen(),
					parametroRes.getValue());
		}
	}

	/* Inicio de métodos protegidos */

	protected static void addChannelService(String nombreSp, String nombreFiltro) {
		channelServices.put(nombreSp, nombreFiltro);
	}

	/* Inicio de métodos privados */

	private static boolean isProcedureRequest(Object procedureReqRes) {
		if (procedureReqRes instanceof IProcedureRequest) {
			return true;
		} else if (procedureReqRes instanceof IProcedureResponse) {
			return false;
		} else {
			throw new COBISInfrastructureRuntimeException("El objeto no es IProcedureRequest ni IProcedureResponse");
		}
	}

	private static IResultSetHeader copiarHeader(IProcedureResponse procedureResponse, int numeroRs,
			int[] ordenColumnas) {
		// se copia la cabecera
		IResultSetHeader rsHeader2 = new ResultSetHeader();
		IResultSetHeaderColumn[] colHeaderArr = procedureResponse.getResultSetMetaData(numeroRs)
				.getColumnsMetaDataAsArray();
		for (int i = 0; i < colHeaderArr.length; i++) {
			int indice = i;
			if (ordenColumnas != null && i < ordenColumnas.length) {
				indice = ordenColumnas[i] - 1;
			}
			IResultSetHeaderColumn colHeader = colHeaderArr[indice];
			rsHeader2.addColumnMetaData(colHeader);
		}
		return rsHeader2;
	}

	private static ResultSetData copiarData(IProcedureResponse procedureResponse, int numeroRs, int[] ordenColumnas) {
		// se copian los datos
		IResultSetData rsData = procedureResponse.getResultSetData(numeroRs);
		ResultSetData rsData2 = new ResultSetData();
		for (int i = 1; i <= rsData.getRowsNumber(); i++) {
			ResultSetRow row2 = new ResultSetRow();
			IResultSetRowColumnData[] valorArr = rsData.getRow(i).getColumnsAsArray();
			for (int j = 0; j < valorArr.length; j++) {
				int indice = j;
				ResultSetRowColumnData value2 = null;
				if (ordenColumnas != null && j < ordenColumnas.length) {
					indice = ordenColumnas[j] - 1;
				}
				boolean isNull = true;
				String valorTexto = null;
				if (valorArr[indice] != null) {
					valorTexto = valorArr[indice].getValue();
					isNull = false;
				}
				value2 = new ResultSetRowColumnData(isNull, valorTexto);
				row2.addRowData(j + 1, value2);
			}
			rsData2.addRow(row2);
		}
		return rsData2;
	}

	/* Fin de métodos privados */

	private static void init() {
		if (locator == null) {
			locator = ComponentLocator.getInstance(SpExecutor.class);
		}
		if (executor == null) {
			executor = locator.find(ISPOrchestrator.class);
			if (executor == null) {
				throw new COBISInfrastructureRuntimeException(
						"No se pudo obtener implementacion para " + ISPOrchestrator.class);
			}
		}
	}
}
