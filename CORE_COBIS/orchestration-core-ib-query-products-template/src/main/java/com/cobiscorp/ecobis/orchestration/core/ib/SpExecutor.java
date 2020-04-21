package com.cobiscorp.ecobis.orchestration.core.ib;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Node;

import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.commons.messaging.IMessagingServiceProvider;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.csp.util.CSPUtil;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSMessage;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.dynamic.DynamicCodeManager;

public final class SpExecutor implements IOrchestrator {
	// Instancia del Logger
	private final ILogger logger = LogFactory.getLogger(SpExecutor.class);
	private static final String PATH_SERVICE_CONFIGURATION = "//config/servicios/servicio";
	private static final String NOMBRE_SP = "nombreSp";
	private static final String NOMBRE_FILTRO = "filtro";
	private static final String METHOD_SIGNATURE = "public void dynamicEvaluation(com.cobiscorp.cobis.cts.domains.ICTSMessage aProcedureRequest)";
	private static final String PATH_JAVA_CODE = "//code-evaluator";
	private static final String METHOD_NAME = "dynamicEvaluation";
	private static final String CLASS_NAME = "AdminChannelsDynamicEvaluator";

	@Override
	public void loadConfiguration(IConfigurationReader reader) {
		// se leen los mapeos de sp's a servicios
		String nombreSp = null;
		String nombreFiltro = null;
		List<Node> servicios = reader.getNodeList(PATH_SERVICE_CONFIGURATION);
		if (servicios != null) {
			for (Node servicio : servicios) {
				nombreSp = reader.getProperty(servicio, NOMBRE_SP);
				nombreFiltro = reader.getProperty(servicio, NOMBRE_FILTRO);
				if ((nombreFiltro != null) && (!nombreFiltro.trim().isEmpty())) {
					SpUtilitario.addChannelService(nombreSp, nombreFiltro);
					if (logger.isDebugEnabled())
						logger.logDebug("NombreSp=" + nombreSp + " NombreFiltro=" + nombreFiltro);
				}
			}
		}

		// ABU: 2013-10-16 Se aumenta logica para guardar el codigo java del
		// archivo de configuracion
		// get java code
		String wBodyMethod = reader.getProperty(PATH_JAVA_CODE);

		// Compile code dynamically
		String wCompletMethod = generateMethodCode(wBodyMethod);
		if (logger.isDebugEnabled())
			logger.logDebug("compile method for method body:" + wCompletMethod);

		// Save the arguments of the method in a List
		List<Class<?>> classArguments = new ArrayList<Class<?>>();
		classArguments.add(ICTSMessage.class);
		List<Class<?>> importClasses = new ArrayList<Class<?>>();
		importClasses.add(ICOBISTS.class);
		// Compile the class
		DynamicCodeManager.generateCompiledClass(CLASS_NAME, wCompletMethod, classArguments, importClasses,
				reader.getPath());
	}

	@Override
	public void executeReentry(String arg0, String arg1) {
	}

	@Override
	public IProcedureResponse executeTransaction(IProcedureRequest procedureRequest) {

		String wMessageId = procedureRequest.readValueFieldInHeader(ICOBISTS.HEADER_MESSAGE_ID);

		IProcedureResponse procedureResponse = null;
		String origSpName = null;

		// se coloca el numero de transaccion original
		procedureRequest.setValueParam("@t_trn", procedureRequest.readParam("@t_orig_trn").getValue());
		// se coloca el nombre de sp original
		origSpName = procedureRequest.readParam("@t_orig_db_name").getValue() + ".."
				+ procedureRequest.readParam("@t_orig_sp_name").getValue();
		procedureRequest.setSpName(origSpName);
		// se remueven los parametros utilitarios
		procedureRequest.removeParam("@t_orig_trn");
		procedureRequest.removeParam("@t_orig_db_name");
		procedureRequest.removeParam("@t_orig_sp_name");

		if (logger.isDebugEnabled())
			logger.logDebug("ProcedureRequest a ejecutar: " + procedureRequest.getProcedureRequestAsString());

		try {
			ICTSMessage wTemp = procedureRequest;
			Object[] args = new Object[] { wTemp };
			DynamicCodeManager.execute(args, CLASS_NAME, METHOD_NAME, this.getClass());

			procedureResponse = SpUtilitario.ejecutarSpCore(procedureRequest);
		} catch (CTSServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CSPUtil.copyHeaderFields(procedureRequest, procedureResponse);
		/* Add messageId to allow CTS get the response from the queue of CIS */
		procedureResponse.addFieldInHeader(ICOBISTS.HEADER_MESSAGE_ID, ICOBISTS.HEADER_STRING_TYPE, wMessageId);
		return procedureResponse;
	}

	@Override
	public IMessagingServiceProvider getMessagingServiceProvider() {
		return null;
	}

	@Override
	public String getOutFactoryDestination() {
		return null;
	}

	@Override
	public void manageReverse(IProcedureResponse procedureResponse) {
	}

	@Override
	public IProcedureResponse processResponse(IProcedureResponse procedureResponse) {
		return null;
	}

	private String generateMethodCode(String bodyMethod) {
		StringBuilder sb = new StringBuilder();
		sb.append(METHOD_SIGNATURE);
		sb.append("\n{");
		sb.append(bodyMethod);
		sb.append("\nreturn null;");
		sb.append("\n}");
		return sb.toString();

	}
}
