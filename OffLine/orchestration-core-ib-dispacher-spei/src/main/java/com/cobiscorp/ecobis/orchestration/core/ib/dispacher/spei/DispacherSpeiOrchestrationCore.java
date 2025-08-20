package com.cobiscorp.ecobis.orchestration.core.ib.dispacher.spei;

import static com.cobiscorp.cobis.cts.domains.ICOBISTS.COBIS_HOME;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.concurrent.*;

import javax.xml.bind.JAXB;

import com.cobiscorp.ecobis.ib.application.dtos.ServerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.orchestration.core.ib.common.ParametrizationSPEI;
import com.cobiscorp.ecobis.orchestration.core.ib.common.SaveAdditionalDataImpl;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cache.ICacheManager;
import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.crypt.ReadAlgn;
import com.cobiscorp.cobis.commons.log.ILogger;
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
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.CacheUtils;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Constants;
import com.cobiscorp.ecobis.ib.orchestration.base.utils.commons.CardPAN;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Institucion;
import com.cobiscorp.ecobis.ib.orchestration.dtos.mensaje;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSelfAccountTransfers;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;
import com.cobiscorp.ecobis.orchestration.core.ib.dispacher.dto.Constans;
import com.cobiscorp.ecobis.orchestration.core.ib.dispacher.dto.Mensaje;
import com.cobiscorp.ecobis.orchestration.core.ib.dispacher.dto.Respuesta;
import com.cobiscorp.ecobis.orchestration.core.ib.transfer.template.DispatcherSpeiOfflineTemplate;


/**
 * Plugin of Dispacher Spei
 *
 * @since Dec 29, 2022
 * @author jolmos
 * @version 1.0.0
 *
 */
@Component(name = "DispacherSpeiOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "DispacherSpeiOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "DispacherSpeiOrchestrationCore") })
public class DispacherSpeiOrchestrationCore extends DispatcherSpeiOfflineTemplate {

	private java.util.Properties properties = null;
	private Map<String, Object> mapDataSigns = null;
	
	private static final String PROPERTY = "//property";
	private static final String JKSAlGNCON = "jksAlgncon";
	private static final String JKSURL = "jksurl";
	private PrivateKey privateKeyKarpay;
	
	//executor service lineamiento PES
	private ExecutorService executorService;
	private static final String DIRECT_PROPERTY = "direct";
	private boolean isDirect = false;
	private static final String POLICY_PROPERTY = "policy";
	private int policy = 4;
	private static final String CORE_POOL_SIZE_PROPERTY = "corePoolSize";
	private int corePoolSize = 15;//el numero de hilos en el pooll 10
	private static final String MAXIMUM_POOL_SIZE_PROPERTY = "maximumPoolSize";
	private int maximumPoolSize = 20;//toma la diferencia de hilos en caso de necesitar 15
	private static final String KEEP_ALIVE_TIME_PROPERTY = "keepAliveTime";
	private int keepAliveTime = 30;
	private static final String CAPACITY_PROPERTY = "capacity";
	private int capacity = 35;//capacidada de la cantidad de tareas encoladas 5
	
	
	@Override
	public void loadConfiguration(IConfigurationReader reader) {
		try {
			this.properties = reader.getProperties(PROPERTY);
			this.mapDataSigns = new HashMap<String, Object>();
			//archivo credenciales llave jks
			String pathAlgnJks = System.getProperty(COBIS_HOME) + properties.getProperty(JKSAlGNCON);
			//archivo path llave jks
			String pathCertificado = System.getProperty(COBIS_HOME) + properties.getProperty(JKSURL);
			File jksAlgn = new File(pathAlgnJks);
			if (jksAlgn.exists()) {
	            ReadAlgn rjksAlgncon = new ReadAlgn(pathAlgnJks);
	            String alias = rjksAlgncon.leerParametros().getProperty("l");
	            String keyPass = rjksAlgncon.leerParametros().getProperty("p");
	            // Cargar el keystore desde el archivo
	  	        KeyStore keystore = KeyStore.getInstance("JKS");
			    keystore.load(new FileInputStream(pathCertificado), keyPass.toCharArray());
		        // Obtener la clave privada y el certificado asociado
		        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keystore.getEntry(alias,
		                new KeyStore.PasswordProtection(keyPass.toCharArray()));
		        PrivateKey finalKey = privateKeyEntry.getPrivateKey();
		        
	            if (finalKey instanceof PrivateKey) {
	            	privateKeyKarpay = (PrivateKey) finalKey;
	            	if (logger.isDebugEnabled()) {
	            		logger.logDebug("privated key:"+privateKeyKarpay);
	        		}
	            }
	        }else {
	        	if (logger.isDebugEnabled()) {
	    			logger.logDebug("No existe archivo jks:"+pathCertificado);
	    		}
	        }
	        if (logger.isDebugEnabled()) {
				logger.logDebug("jksAlgncon:"+pathAlgnJks);
				logger.logDebug("jksurl:"+pathCertificado);
			}
	        //implementacion executor PES
	        isDirect = tryParseToBoolean(properties.getProperty(DIRECT_PROPERTY), isDirect);
			policy = tryParseToInteger(properties.getProperty(POLICY_PROPERTY), policy);
	        corePoolSize = tryParseToInteger(properties.getProperty(CORE_POOL_SIZE_PROPERTY), corePoolSize);
			maximumPoolSize = tryParseToInteger(properties.getProperty(MAXIMUM_POOL_SIZE_PROPERTY), maximumPoolSize);
			keepAliveTime = tryParseToInteger(properties.getProperty(KEEP_ALIVE_TIME_PROPERTY), keepAliveTime);
			capacity = tryParseToInteger(properties.getProperty(CAPACITY_PROPERTY), capacity);
			if (logger.isDebugEnabled())
			{
				logger.logDebug("policy:"+policy);
				logger.logDebug("corePoolSize:"+corePoolSize);
				logger.logDebug("maximumPoolSize:"+maximumPoolSize);
				logger.logDebug("keepAliveTime:"+keepAliveTime);
				logger.logDebug("capacity:"+capacity);
			}
			// Verfico la conexion con el gestor de colas
			RejectedExecutionHandler rejectionHandler=null;
	
			switch (policy) {
				case 1:
					rejectionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
					break;
				case 2:
					rejectionHandler = new ThreadPoolExecutor.DiscardOldestPolicy();
					break;
				case 3:
					rejectionHandler = new ThreadPoolExecutor.DiscardPolicy();
					break;
				case 4:
					rejectionHandler = new ThreadPoolExecutor.AbortPolicy();
					break;
				default:
					throw new IllegalArgumentException("Política de rechazo no válida: " + policy);
			}
		
			if (executorService != null && !executorService.isShutdown()) {
				executorService.shutdown();
			}
			
	    	executorService = new ThreadPoolExecutor(
					corePoolSize,
					maximumPoolSize,
					keepAliveTime,
					TimeUnit.SECONDS,
					new LinkedBlockingQueue<Runnable>(capacity),
					new CustomThreadFactory(),
					rejectionHandler
			);
		} catch (Exception xe) {
    		logger.logError("Error al cargar las configarciones y firmas: ",xe);
        } finally {
        	if (logger.isDebugEnabled()) {
				logger.logDebug("Fin load configuration DispacherSpeiOrchestrationCore");
			}
        }
	}

	/**
	 * Instance logger component
	 */
	private static ILogger logger = LogFactory.getLogger(DispacherSpeiOrchestrationCore.class);
	
	

	/**
	 * /** Execute transfer first step of service
	 * <p>
	 * This method is the main executor of transactional contains the original input
	 * parameters.
	 *
	 * @param anOriginalRequest       - Information original sended by user's.
	 * @param aBagSPJavaOrchestration - Object dictionary transactional steps.
	 *
	 * @return
	 *         <ul>
	 *         <li>IProcedureResponse - Represents the service execution.</li>
	 *         </ul>
	 */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		
		if (logger.isDebugEnabled())
		{
			logger.logDebug("JCOS DispacherSpeiOrchestrationCore: executeJavaOrchestration");
			logger.logDebug(anOriginalRequest);
		}
		
		aBagSPJavaOrchestration.putAll( mapDataSigns);
		aBagSPJavaOrchestration.put(TRANSFER_NAME, "SPEI DISPACHER");
		mensaje message = null;
		String xmls = anOriginalRequest.readValueParam("@i_pay_order");
		Integer idLog = 0;
		try {
			// METODO GUARDAR XML
			
			DispatcherUtil plot = new DispatcherUtil();
			message = plot.getDataMessage(xmls);
			if (message != null) {
				aBagSPJavaOrchestration.put("speiTransaction", message);
				//llamada a log entrante
				idLog =	logEntryApi(anOriginalRequest, aBagSPJavaOrchestration, "I", "Dispacher in", null, null, null, null,  xmls);
				executeStepsTransactionsBase(anOriginalRequest, aBagSPJavaOrchestration);
			}
			
		}catch (Exception xe) {
			logger.logError("Error dispacher:",xe);
		}
		//llamada a log update
		logEntryApi(anOriginalRequest, aBagSPJavaOrchestration, "U", "Dispacher in", null, null, aBagSPJavaOrchestration.get("result").toString(), idLog, xmls);
	
		IProcedureResponse valuesOutput = new ProcedureResponseAS();
		valuesOutput.addParam("@o_result", 39, 1, (String)aBagSPJavaOrchestration.get("result"));

		return valuesOutput;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> aBagSPJavaOrchestration) {
		return (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
	}

	@Override
	protected ICoreServiceSendNotification getCoreServiceNotification() {
		return coreServiceNotification;
	}

	@Override
	public ICoreServer getCoreServer() {
		return coreServer;
	}

	@Override
	protected IProcedureResponse executeTransaction(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
		return null;
	}
	
	protected void executeCreditTransferOrchest(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
	}

	@Override
	protected Boolean doSignature(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		return false;
	}
	
	@Override
	protected Object chargesSettled(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		
		//select ts_estado,* from cob_bvirtual..bv_transfer_spei order by ts_fecha_real desc
		//cambiar por el estado por A
		Mensaje msg = new Mensaje();
		Respuesta responseXml = new Respuesta();
		mensaje msjIn = (mensaje) aBagSPJavaOrchestration.get("speiTransaction");
		String response;
		 DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
         DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
         LocalDate fechaOperacion = LocalDate.parse(msjIn.getOrdenpago().getOpFechaOper(), inputFormatter);
         String fechaLiq = fechaOperacion.format(outputFormatter);
		if(logger.isDebugEnabled())
			logger.logDebug("BER Id:"+msjIn.getOrdenpago().getId());
		    logger.logDebug("JC HORA:"+msjIn.getOrdenpago().getOpHoraLiqBm());
		if( validateFields(request,aBagSPJavaOrchestration))
		{
			IProcedureResponse procedureResponseLocal=null;
			//llamar sp cambio de estado transfer spei 
			if(msjIn.getOrdenpago().getOpTpClave()==0)
				procedureResponseLocal = updateDevolution(request, aBagSPJavaOrchestration,"U",null, null, fechaLiq);
			else
				procedureResponseLocal = updateStatusOperation(request, aBagSPJavaOrchestration, "A", msjIn.getOrdenpago().getOpCveRastreo(), fechaLiq,msjIn.getOrdenpago().getOpHoraLiqBm());
			
			
			
			if(procedureResponseLocal.getReturnCode()!=0)
			{
				aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, procedureResponseLocal.getReturnCode());
				aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "Error en la actualizacion de la transferencia spei");
				
		       if(msjIn.getOrdenpago().getOpTpClave()==0) {
					//SPEI_RETURN FALLIDO
					aBagSPJavaOrchestration.put("eventWH", "F");
					aBagSPJavaOrchestration.put("code_error", procedureResponseLocal.getReturnCode());
					aBagSPJavaOrchestration.put("message_error",  "Error en la actualizacion de la transferencia spei");
					    
					registerTransactionWebHook(aBagSPJavaOrchestration, msjIn);
		       }
				
			}else
			{
				aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 0);
				aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "");
			}
			
		}
		
		responseXml.setErrCodigo(Integer.valueOf( String.valueOf( aBagSPJavaOrchestration.get(Constans.VALIDATE_CODE))));
		responseXml.setErrDescripcion(String.valueOf( aBagSPJavaOrchestration.get(Constans.MESSAJE_CODE)));
		responseXml.setFechaOper(msjIn.getOrdenpago().getOpFechaOper());
		responseXml.setId(msjIn.getOrdenpago().getId());
		msg.setCategoria(Constans.ODPS_LIQUIDADAS_CARGOS_RESPUESTA);
		msg.setRespuesta(responseXml);
		
		response = toStringXmlObject(msg);  
		aBagSPJavaOrchestration.put("result", response);
		return response;
	}

	@Override
	protected Object paymentIn(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration)
	{
		logHour("1");	
		long start = System.currentTimeMillis();
		String response = "";
		Mensaje msg = new Mensaje();
		Respuesta responseXml = new Respuesta();
		mensaje msjIn = (mensaje) aBagSPJavaOrchestration.get("speiTransaction");
		try
		{
			boolean isCoreError = false;
			if(logger.isDebugEnabled())
				logger.logDebug("BER Id:"+msjIn.getOrdenpago().getId());
			loadParameters(request, aBagSPJavaOrchestration);
			IProcedureResponse responSingTyp = singType(request, aBagSPJavaOrchestration, "bv_tipo_firma_pago", String.valueOf( msjIn.getOrdenpago().getOpTpClave()));
			if(responSingTyp.getReturnCode()==0)
			{
				logHour("2");
				if ( validateFields(request, aBagSPJavaOrchestration))
				{
					DispatcherUtil util = new DispatcherUtil();
					String sign = util.doSignature(request, aBagSPJavaOrchestration, privateKeyKarpay);
					if(logger.isDebugEnabled())
					{
						logger.logDebug("firma armada:"+sign);
						logger.logDebug("firma request:"+msjIn.getOrdenpago().getOpFirmaDig());
					}
					if(!sign.equals(msjIn.getOrdenpago().getOpFirmaDig()))
					{
						responseXml.setErrCodigo(Integer.valueOf(8));
						responseXml.setErrDescripcion("La firma del mensaje no es correcta");
					}else
					{	
						logHour("3");
						
						//validacion de tipos de pago en el catalogo cobis bv_tipo_pago_spei_in
						int typePaymentResult = catalog(request, "bv_tipo_pago_spei_in", String.valueOf(msjIn.getOrdenpago().getOpTpClave()), "","E","");
						//spein in tipo de pago 0 devolucion
						if(typePaymentResult == 0)
						{
							if(msjIn.getOrdenpago().getOpTpClave()==0 || msjIn.getOrdenpago().getOpTpClave()==17)
							{
								String clave = "";
								if( msjIn.getOrdenpago().getOpTpClave()==17)
									clave = msjIn.getOrdenpago().getOpRastreoOri();
								else
									clave =  msjIn.getOrdenpago().getOpCveRastreo();
								//consulta de datos para hacer un reverso de una devolucion
								IProcedureResponse procedureGetDataSpei = getDataSPEI(request, aBagSPJavaOrchestration, clave);
		
								if(procedureGetDataSpei.getReturnCode() != 0)
								{
									responseXml.setErrCodigo(procedureGetDataSpei.getReturnCode());
									responseXml.setErrDescripcion("Error en la devolución de la operacion");
								}else
								{
									msjIn.getOrdenpago().setOpCuentaBen((String)aBagSPJavaOrchestration.get("o_clabe"));
									msjIn.getOrdenpago().setOpCveRastreo(clave);
									msjIn.getOrdenpago().setOpTcClaveBen(aBagSPJavaOrchestration.get("o_tipo_cuenta_ori")!= null ? Integer.parseInt((String)aBagSPJavaOrchestration.get("o_tipo_cuenta_ori")) : 0);
									msjIn.getOrdenpago().setOpConceptoPag2((String)aBagSPJavaOrchestration.get("o_concepto"));
									msjIn.getOrdenpago().setOpNomOrd("NA");
									msjIn.getOrdenpago().setOpRfcCurpOrd("NA");
									msjIn.getOrdenpago().setOpCuentaOrd((String)aBagSPJavaOrchestration.get("o_clabe_des"));
									msjIn.getOrdenpago().setOpTcClaveOrd(aBagSPJavaOrchestration.get("o_tipo_cuenta_des")!= null ? Integer.parseInt((String)aBagSPJavaOrchestration.get("o_tipo_cuenta_des")) : 0);

								}
							}
						
							//busqueda tipo de pago extemporaneo
							int typePayExtResult = catalog(request, "bv_tipo_pago_extemporeano", String.valueOf(msjIn.getOrdenpago().getOpTpClave()), "","E","");
							
							if(typePayExtResult == 0)
							{
								msjIn.getOrdenpago().setOpCuentaBen(msjIn.getOrdenpago().getOpCuentaOrd());
								msjIn.getOrdenpago().setOpTcClaveBen(msjIn.getOrdenpago().getOpTcClaveOrd());
							}
							//consulta datos para hacer un retorno parcial de una devolucion
							if(msjIn.getOrdenpago().getOpTpClave()==23)
							{
								searchOriginAccountDestination(request,aBagSPJavaOrchestration,msjIn.getOrdenpago().getOpRastreoOri(),msjIn);
							}
							long startSpeiin = System.currentTimeMillis();
							
							
							IProcedureResponse procedureResponseLocal = speiIn(request, msjIn, aBagSPJavaOrchestration);
							long endSpeiin = System.currentTimeMillis();
							if(logger.isDebugEnabled())
							{
								logger.logDebug("CVE:"+msjIn.getOrdenpago().getOpCveRastreo());
								logger.logDebug("Spei in time: "+(endSpeiin-startSpeiin));
							}
							logHour("4");
							if(logger.isDebugEnabled())
								logger.logDebug("Response tranfer spei in: "+procedureResponseLocal.getProcedureResponseAsString());
							//implementar catalogo para los que si aplican esice
							if(procedureResponseLocal.getReturnCode()==0 && procedureResponseLocal.readValueParam("@o_id_causa_devolucion")!=null && Integer.parseInt(procedureResponseLocal.readValueParam("@o_id_causa_devolucion"))==0)
							{
								
								try {
									if(msjIn.getOrdenpago().getOpTpClave() == 1 || msjIn.getOrdenpago().getOpTpClave() == 12 || msjIn.getOrdenpago().getOpTpClave() == 30|| msjIn.getOrdenpago().getOpTpClave() == 36)
									{
										//moniotero de hilos esice
										if(logger.isDebugEnabled())
										{
											logger.logDebug("Entra esice CDA:"+msjIn.getOrdenpago().getOpCveRastreo());
											int activeCount = ((ThreadPoolExecutor) executorService).getActiveCount();
											logger.logDebug("Numero de hilos esice:"+activeCount);
											int queueCount = ((ThreadPoolExecutor) executorService).getQueue().size();
											logger.logDebug("Numero hilos en cola esice:"+queueCount);
										}
										//llamada a generacion de CDAS
										EsiceCallableTask esiceTask = new EsiceCallableTask(request, aBagSPJavaOrchestration);
										executorService.submit(esiceTask);
									}else 
										if(msjIn.getOrdenpago().getOpTpClave()==0 || msjIn.getOrdenpago().getOpTpClave()==17)
										{
											updateStatusOperation(request, aBagSPJavaOrchestration, "F", msjIn.getOrdenpago().getOpCveRastreo(),"",msjIn.getOrdenpago().getOpHoraLiqBm());
										}
									responseXml.setErrCodigo(Integer.parseInt(procedureResponseLocal.readValueParam("@o_id_causa_devolucion")));
									responseXml.setErrDescripcion(procedureResponseLocal.readValueParam("@o_descripcion"));
								}catch(Exception e)
								{
									logger.logError("Error de esice CDA", e);
								}
							}else
							{
								if(logger.isDebugEnabled()) {
									logger.logDebug("o_id_causa_devolucion: "+procedureResponseLocal.readValueParam("@o_id_causa_devolucion"));
									logger.logDebug("return code: "+procedureResponseLocal.getReturnCode());
								}
								isCoreError = true;
								//esto implementar en una tabla en la base de datos
								if("40020".equals(procedureResponseLocal.readValueParam("@o_id_causa_devolucion")) || procedureResponseLocal.getReturnCode()==40020)
								{
									responseXml.setErrCodigo(1);
									responseXml.setErrDescripcion("Cuenta inexistente");
								}else
								if("400335".equals(procedureResponseLocal.readValueParam("@o_id_causa_devolucion")))
								{
									responseXml.setErrCodigo(20);
									responseXml.setErrDescripcion("Excede el límite de saldo autorizado de la cuenta");
								}else
								if("400337".equals(procedureResponseLocal.readValueParam("@o_id_causa_devolucion")))
								{
									responseXml.setErrCodigo(21);
									responseXml.setErrDescripcion("Excede el límite de abonos permitidos en el mes en la cuenta");
								}else
								{
									responseXml.setErrCodigo(Integer.parseInt(procedureResponseLocal.readValueParam("@o_id_causa_devolucion")));
									responseXml.setErrDescripcion(procedureResponseLocal.readValueParam("@o_descripcion"));
								}
							}	
						}else
						{
							responseXml.setErrCodigo(15);
							responseXml.setErrDescripcion("Tipo de pago erróneo");
							isCoreError = true;
						}
					}
				} else {
					responseXml.setErrCodigo(Integer.valueOf( String.valueOf( aBagSPJavaOrchestration.get(Constans.VALIDATE_CODE))));
					responseXml.setErrDescripcion(String.valueOf( aBagSPJavaOrchestration.get(Constans.MESSAJE_CODE)));
				}
			}else
			{
				responseXml.setErrCodigo(15);
				responseXml.setErrDescripcion("Tipo de pago erróneo");
				isCoreError = true;
			}
			
			responseXml.setFechaOper(msjIn.getOrdenpago().getOpFechaOper());
			responseXml.setId(msjIn.getOrdenpago().getId());
			msg.setCategoria(Constans.ODPS_LIQUIDADAS_ABONOS_RESPUESTA);
		
			String returnCodeMsjSpeiIn = "";
			if(responseXml.getErrCodigo() != 0 && isCoreError)
			{
				logHour("5");
				if(logger.isDebugEnabled()) {
					logger.logDebug("error future: "+responseXml.getErrCodigo());
					logger.logDebug("Return CVE:"+msjIn.getOrdenpago().getOpCveRastreo());
				}
				
				msjIn.getOrdenpago().setOpCdClave(responseXml.getErrCodigo());
				//llamada a log entrante
				if(logger.isDebugEnabled())
				{
					logger.logDebug("Entra return payment:"+msjIn.getOrdenpago().getOpCveRastreo());
					int activeCount = ((ThreadPoolExecutor) executorService).getActiveCount();
					logger.logDebug("Numero de hilos payment:"+activeCount);
					int queueCount = ((ThreadPoolExecutor) executorService).getQueue().size();
					logger.logDebug("Numero hilos en cola payment:"+queueCount);
				}
				try {
					// Crear una instancia de MyCallableTask con parámetros
					ReturnPaymentCallableTask task = new ReturnPaymentCallableTask( request, aBagSPJavaOrchestration, msjIn, (String)aBagSPJavaOrchestration.get("paramInsBen"));
					// Enviar la tarea para su ejecución
					executorService.submit(task);
				}catch(Exception e)
				{
					logger.logError("Error de return payment", e);
				}
		
		        logHour("6");
			}
			//se manda el response correcto que se recibio la solicitud spei in
			if(isCoreError)
			{
				responseXml.setErrCodigo(0);
				responseXml.setErrDescripcion("Solicitud recibida correctamente.");
			}
			msg.setRespuesta(responseXml);
			response = toStringXmlObject(msg);  
			aBagSPJavaOrchestration.put("result", response);
			aBagSPJavaOrchestration.put("error", returnCodeMsjSpeiIn);
			logHour("7");
		} catch (Exception e) {
			logger.logError("Error de spei in karpay:"+msjIn.getOrdenpago().getOpCveRastreo(), e);
			
			msg.setCategoria(Constans.ODPS_LIQUIDADAS_ABONOS_RESPUESTA);
			responseXml.setFechaOper(msjIn.getOrdenpago().getOpFechaOper());
			responseXml.setId(msjIn.getOrdenpago().getId());
			responseXml.setErrCodigo(500);
			responseXml.setErrDescripcion("Error interno de infraestructura consulte con el administrador.");
			msg.setRespuesta(responseXml);
			response = toStringXmlObject(msg);
			aBagSPJavaOrchestration.put("result", response);
			if(logger.isDebugEnabled()) {
				logger.logDebug("response error spei in: "+response);
			}
		} finally {
			long end = System.currentTimeMillis();
			if (logger.isDebugEnabled()) {
				logger.logDebug("Tiempo proceso spei in: "+(end-start)+", "+msjIn.getOrdenpago().getOpCveRastreo());
			}
		}
		
		return response;
	}
	
	@Override
	protected Object cancellations(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration)
	{
		Mensaje msg = new Mensaje();
		Respuesta responseXml = new Respuesta();
		mensaje msjIn = (mensaje) aBagSPJavaOrchestration.get("speiTransaction");
		String response;
		if(logger.isDebugEnabled())
			logger.logDebug("cancellations:"+msjIn.getOrdenpago().getId());
		if( validateFields(request,aBagSPJavaOrchestration))
		{
			//consulta de datos para hacer un reverso de una devolucion
			IProcedureResponse procedureGetDataSpei = getDataSPEI(request, aBagSPJavaOrchestration, msjIn.getOrdenpago().getOpCveRastreo());

			if(procedureGetDataSpei.getReturnCode() == 0)
			{
				msjIn.getOrdenpago().setOpCuentaBen((String)aBagSPJavaOrchestration.get("o_clabe"));
				msjIn.getOrdenpago().setOpCveRastreo(msjIn.getOrdenpago().getOpCveRastreo());
				msjIn.getOrdenpago().setOpTcClaveBen(aBagSPJavaOrchestration.get("o_tipo_cuenta_ori")!= null ? Integer.parseInt((String)aBagSPJavaOrchestration.get("o_tipo_cuenta_ori")) : 0);
				msjIn.getOrdenpago().setOpConceptoPag2((String)aBagSPJavaOrchestration.get("o_concepto"));
				msjIn.getOrdenpago().setOpNomOrd("NA");
				msjIn.getOrdenpago().setOpRfcCurpOrd("NA");
				msjIn.getOrdenpago().setOpCuentaOrd((String)aBagSPJavaOrchestration.get("o_clabe_des"));
				msjIn.getOrdenpago().setOpTcClaveOrd(aBagSPJavaOrchestration.get("o_tipo_cuenta_des")!= null ? Integer.parseInt((String)aBagSPJavaOrchestration.get("o_tipo_cuenta_des")) : 0);
				
				IProcedureResponse procedureResponseReverse = speiIn(request, msjIn, aBagSPJavaOrchestration);
				
				if(!procedureResponseReverse.hasError() && procedureResponseReverse.getReturnCode()==0 
						&& procedureResponseReverse.readValueParam("@o_id_causa_devolucion")!=null 
						&& Integer.parseInt(procedureResponseReverse.readValueParam("@o_id_causa_devolucion"))==0)
				{
					responseXml.setErrCodigo(Integer.valueOf( procedureResponseReverse.readValueParam("@o_id_causa_devolucion")));
					responseXml.setErrDescripcion(procedureResponseReverse.readValueParam("@o_descripcion"));
					updateStatusOperation(request, aBagSPJavaOrchestration, "F", msjIn.getOrdenpago().getOpCveRastreo(),"",msjIn.getOrdenpago().getOpHoraLiqBm());
				}else
				{
					responseXml.setErrCodigo(procedureResponseReverse.getReturnCode());
					responseXml.setErrDescripcion("Error en el reverso de la operacion");
				}
				
			}else
			{
				responseXml.setErrCodigo(procedureGetDataSpei.getReturnCode());
				responseXml.setErrDescripcion("Error en la devolución de la operacion");
			}
		}else
		{
			responseXml.setErrCodigo(Integer.valueOf( String.valueOf( aBagSPJavaOrchestration.get(Constans.VALIDATE_CODE))));
			responseXml.setErrDescripcion(String.valueOf( aBagSPJavaOrchestration.get(Constans.MESSAJE_CODE)));
		}
		responseXml.setFechaOper(msjIn.getOrdenpago().getOpFechaOper());
		responseXml.setId(msjIn.getOrdenpago().getId());
		msg.setCategoria(Constans.ODPS_CANCELADAS_LOCAL_RESPUESTA);
		msg.setRespuesta(responseXml);
		
		response = toStringXmlObject(msg);  
		aBagSPJavaOrchestration.put("result", response);
		if(logger.isDebugEnabled())
			logger.logDebug("cancellations response:"+response);
		return response;
	}
	
	@Override
	protected Object ensesion(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration)
	{
		long start = System.currentTimeMillis();
		Mensaje msg = new Mensaje();
		Respuesta responseXml = new Respuesta();
		mensaje msjIn = (mensaje) aBagSPJavaOrchestration.get("speiTransaction");
		String response;
		
		if(logger.isDebugEnabled())
			logger.logDebug("BER categoria ensesion:"+msjIn.getCategoria());
		if(msjIn!=null)
		{
			if(logger.isDebugEnabled())
				logger.logDebug("BER fecha operacion ensesion:"+msjIn.getEnsesion().getFechaOperacionBanxico());
			
			//cambio de fecha operacion karpay
			 DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	         DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	         LocalDate fecha = LocalDate.parse(msjIn.getEnsesion().getFechaOperacionBanxico(), inputFormatter);
	         String fechaOperacion = fecha.format(outputFormatter);
	         int responseCatalog = 0;
	         for(Institucion inst : msjIn.getEnsesion().getTnstituciones().getListInstBancarias() )
	         {
				if(logger.isDebugEnabled())
					logger.logDebug("BER fecha operacion ensesion:"+inst.getInsNombre()+":"+inst.getClaveCesif());
			
				//actualizar catalogo
				responseCatalog = catalog(request, "bv_ifis_pago_directo", inst.getClaveCesif(), inst.getInsNombre(),"U", "V");
				if(responseCatalog!=0)
				{
					//inserta catalogo
					responseCatalog = catalog(request, "bv_ifis_pago_directo", inst.getClaveCesif(), inst.getInsNombre(),"I", "V");
				}
				insertUpdateInstitutions(request, inst.getClaveCesif(), inst.getInsNombre(), "B", inst.getEstadoInstitucion(), "C");
	         }
	         //llamado sp
	         setParamKarpayDate(request,"PRODAK", "AHO", fechaOperacion);
	         
		}
		else
		{
			responseXml.setErrCodigo(0);
			responseXml.setErrDescripcion("Procesamiento Exitoso");
		}
		responseXml.setFechaOper(msjIn.getEnsesion().getFechaOperacionBanxico());
		responseXml.setId("0");
		msg.setCategoria(Constans.ENSESION_RESPUESTA);
		msg.setRespuesta(responseXml);
		
		response = toStringXmlObject(msg);  
		aBagSPJavaOrchestration.put("result", response);
		if(logger.isDebugEnabled())
			logger.logDebug("cancellations response:"+response);
		long end = System.currentTimeMillis();
		if(logger.isDebugEnabled()) {
			logger.logDebug("tiempo proceso spei in: "+(end-start));
		}
		return response;
	}
	
	private IProcedureResponse speiIn(IProcedureRequest request, mensaje msjIn, Map<String, Object> aBagSPJavaOrchestration)
	{
		
		if(logger.isDebugEnabled())
			logger.logDebug("speiIn:Inicio" );
		IProcedureRequest procedureRequest = initProcedureRequest(request);
		procedureRequest.setSpName("cob_procesador..sp_bv_spei_transaction");
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500069");
		procedureRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500069");
		request.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "18500069");
		request.addFieldInHeader("trn_origen", ICOBISTS.HEADER_STRING_TYPE, "API_IN");
		request.addFieldInHeader("target", ICOBISTS.HEADER_STRING_TYPE, "SPExecutor");
		request.addFieldInHeader(ICOBISTS.HEADER_MESSAGE_TYPE, ICOBISTS.HEADER_STRING_TYPE, "ProcedureRequest");
		request.addFieldInHeader("com.cobiscorp.cobis.csp.services.IOrchestrator", ICOBISTS.HEADER_STRING_TYPE,
				"(service.identifier=SpeiInTransferOrchestrationCore)");
		request.addFieldInHeader("serviceMethodName", ICOBISTS.HEADER_STRING_TYPE, "executeTransaction");
		procedureRequest.addInputParam("@i_cuentaBeneficiario", ICTSTypes.SYBVARCHAR, msjIn.getOrdenpago().getOpCuentaBen());
		procedureRequest.addInputParam("@i_monto", ICTSTypes.SYBMONEY, msjIn.getOrdenpago().getOpMonto().toString());
		procedureRequest.addInputParam("@i_fechaOperacion", ICTSTypes.SYBDATETIME, msjIn.getOrdenpago().getOpFechaOper());
		procedureRequest.addInputParam("@i_cuentaOrdenante", ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpCuentaOrd());
		procedureRequest.addInputParam("@i_conceptoPago", ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpConceptoPag2());
		procedureRequest.addInputParam("@i_institucionOrdenante", ICTSTypes.SYBINT4, String.valueOf(msjIn.getOrdenpago().getOpInsClave()));
		procedureRequest.addInputParam("@i_institucionBeneficiaria", ICTSTypes.SYBINT4, (String)aBagSPJavaOrchestration.get("paramInsBen"));
		procedureRequest.addInputParam("@i_idSpei", ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getId());
		procedureRequest.addInputParam("@i_claveRastreo", ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpCveRastreo());
		procedureRequest.addInputParam("@i_nombreOrdenante", ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpNomOrd());
		procedureRequest.addInputParam("@i_rfcCurpOrdenante", ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpRfcCurpOrd());
		procedureRequest.addInputParam("@i_referenciaNumerica", ICTSTypes.SQLVARCHAR, String.valueOf(msjIn.getOrdenpago().getOpRefNumerica()));
		procedureRequest.addInputParam("@i_idTipoPago", ICTSTypes.SYBINT4,String.valueOf(msjIn.getOrdenpago().getOpTpClave()));
		procedureRequest.addInputParam("@i_string_request", ICTSTypes.SQLVARCHAR, toStringXmlObject(msjIn));
		procedureRequest.addInputParam("@i_tipoCuentaBeneficiario", ICTSTypes.SQLVARCHAR, String.valueOf(msjIn.getOrdenpago().getOpTcClaveBen()));
		procedureRequest.addInputParam("@i_tipoCuentaOrdenante", ICTSTypes.SQLVARCHAR, String.valueOf(msjIn.getOrdenpago().getOpTcClaveOrd()));
		procedureRequest.addOutputParam("@o_id_interno", ICTSTypes.SQLINT4, "");
		procedureRequest.addOutputParam("@o_nombre_beneficiario", ICTSTypes.SQLVARCHAR, "");
		procedureRequest.addOutputParam("@o_rfc_curp_beneficiario", ICTSTypes.SQLVARCHAR, "");
		procedureRequest.addOutputParam("@o_descripcion_error", ICTSTypes.SQLVARCHAR, "");
		procedureRequest.addOutputParam("@o_resultado_error", ICTSTypes.SQLINT4, "");
		procedureRequest.addOutputParam("@o_id_causa_devolucion", ICTSTypes.SQLVARCHAR, "-1");
		procedureRequest.addOutputParam("@o_descripcion", ICTSTypes.SQLVARCHAR, "Error desconocido");					
		
		procedureRequest.addInputParam("@i_operacion", ICTSTypes.SYBVARCHAR, "A");			
		procedureRequest.addInputParam("@i_clave_rastreo", ICTSTypes.SYBVARCHAR, msjIn.getOrdenpago().getOpCveRastreo());
		
		IProcedureResponse procedureResponseLocal = executeCoreBanking(procedureRequest);
		if(logger.isDebugEnabled())
			logger.logDebug("speiIn FIN procedureResponseLocal:"+procedureResponseLocal.getCTSMessageAsString() );
		return procedureResponseLocal;
	}
	
	private String getParam(IProcedureRequest anOriginalRequest, String nemonico, String producto) {
		if (logger.isDebugEnabled()) {
			logger.logDebug("Begin flow, getOperatingInstitutionFromParameters");
		}
		IProcedureRequest reqTMPCentral = (initProcedureRequest(anOriginalRequest));		
		reqTMPCentral.setSpName("cobis..sp_parametro");
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "central");
		reqTMPCentral.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "Q");
		reqTMPCentral.addInputParam("@i_nemonico",ICTSTypes.SQLVARCHAR, nemonico);
		reqTMPCentral.addInputParam("@i_producto",ICTSTypes.SQLVARCHAR, producto);	 
	    reqTMPCentral.addInputParam("@i_modo",ICTSTypes.SQLINT4, "4");

	    IProcedureResponse wProcedureResponseCentral = executeCoreBanking(reqTMPCentral);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Ending flow, getOperatingInstitutionFromParameters with wProcedureResponseCentral: " + wProcedureResponseCentral.getProcedureResponseAsString());
		}
		
		if (!wProcedureResponseCentral.hasError()) {
			
			if (wProcedureResponseCentral.getResultSetListSize() > 0) {
				IResultSetRow[] resultSetRows = wProcedureResponseCentral.getResultSet(1).getData().getRowsAsArray();
				
				if (resultSetRows.length > 0) {
					IResultSetRowColumnData[] columns = resultSetRows[0].getColumnsAsArray();
					return columns[2].getValue();
				} 
			} 
		} 
		
		return "";
	}
	
	private boolean validateAccountType(IProcedureRequest anOriginalRequest, String accountType, Map<String, Object> aBagSPJavaOrchestration) 
	{
		if (logger.isDebugEnabled()) 
		{
			logger.logDebug("Begin validateAccountType");
		}
		boolean validate = true ;
		IProcedureRequest reqTMPCentral = (initProcedureRequest(anOriginalRequest));		
		reqTMPCentral.setSpName("cob_bvirtual..sp_valida_tipo_destino");
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID,  ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500163");
		reqTMPCentral.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500163");
		reqTMPCentral.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "V");
		reqTMPCentral.addInputParam("@i_tipo_destino", ICTSTypes.SQLVARCHAR, accountType);

	    IProcedureResponse wProcedureResponseCentral = executeCoreBanking(reqTMPCentral);
		
		if (logger.isDebugEnabled()) 
		{
			logger.logDebug("Ending flow, validateAccountType with wProcedureResponseCentral: " + wProcedureResponseCentral.getProcedureResponseAsString());
		}
		
		if (wProcedureResponseCentral.hasError()) {
			validate = false;
		}else
		{
			if (wProcedureResponseCentral.getResultSetListSize() > 0) {
			
				IResultSetRow[] resultSetRows = wProcedureResponseCentral.getResultSet(1).getData().getRowsAsArray();
				
				if (resultSetRows.length > 0) {
					IResultSetRowColumnData[] columns = resultSetRows[0].getColumnsAsArray();
					aBagSPJavaOrchestration.put("tipoDestino", columns[0].getValue());
				} 
			} 
		}
		
		return validate;
	}
	private String toStringXmlObject(Object obj)
	{
		StringWriter wOrdenPago = new StringWriter();
		JAXB.marshal(obj, wOrdenPago);
		String xmlOrdenPago = wOrdenPago.toString();
		xmlOrdenPago = xmlOrdenPago.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", "");
		return xmlOrdenPago;
	}
	
	private Boolean validateFields(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration)
	{
		
		mensaje msjIn = (mensaje) aBagSPJavaOrchestration.get("speiTransaction");
		Boolean validate = true; 
		String opTcClaveBen = String.format("%02d", msjIn.getOrdenpago().getOpTcClaveBen());
		
		if(Constans.ODPS_LIQUIDADAS_CARGOS.equals(msjIn.getCategoria()))
		{
			if(msjIn.getOrdenpago().getOpFechaOper()==null)
			{
				aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 93);
				aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La fecha de operación  es obligatoria");
				validate = false;
			}else
			if(Integer.valueOf(msjIn.getOrdenpago().getOpFolio())==null)
			{
				aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 445);
				aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "El Folio CoDi es obligatorio");
				validate = false;
			}else
			if(Integer.valueOf(msjIn.getOrdenpago().getOpInsClave())==null)
			{
				aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 5);
				aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La clave de institución  ordenante es obligatoria para este Tipo de Pago");
				validate = false; 
			}else
			if(Integer.valueOf(msjIn.getOrdenpago().getOpTpClave())==null)
			{
				aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 81);
				aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "El tipo de operación  es obligatorio para este Tipo de Pago");
				validate = false;
			}else
			if(msjIn.getOrdenpago().getOpCveRastreo()==null)
			{
				aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 92);
				aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La clave de rastreo es obligatoria");
				validate = false;
			}else
			if(msjIn.getOrdenpago().getOpEstado()==null)
			{
				aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 98);
				aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "El Estado del envío  es obligatorio");
				validate = false;
			}else
			if(msjIn.getOrdenpago().getOpTipoOrden()==null)
			{
				aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 106);
				aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "Tipo de orden es requerido.");
				validate = false; 
			}else
			if(Integer.valueOf(msjIn.getOrdenpago().getOpPrioridad())==null)
			{
				aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 85);
				aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La prioridad de la orden es un dato obligatorio");
				validate = false;	 
			}else
			if(Integer.valueOf(msjIn.getOrdenpago().getOpMeClave())==null)
			{
				aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 93);
				aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La fecha de operación  es obligatoria");
				validate = false;	 
			}if(msjIn.getOrdenpago().getOpTopologia()==null)
			{
				aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 87);
				aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La Topología  de la orden es obligatorio");
				validate = false;	 
			}else
			if(msjIn.getOrdenpago().getOpUsuClave()==null)
			{
				aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 158);
				aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La clave del banco usuario es obligatoria para este Tipo de Pago");
				validate = false;	 
			}
		}else
			if(Constans.ODPS_LIQUIDADAS_ABONOS.equals(msjIn.getCategoria()))
			{

				if(msjIn.getOrdenpago().getOpFechaOper()==null)
				{
					aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 93);
					aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La fecha de operación  es obligatoria");
					validate = false;
				}else
				if(Integer.valueOf(msjIn.getOrdenpago().getOpFolio())==null)
				{
					aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 445);
					aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "El Folio CoDi es obligatorio");
					validate = false;
				}else
				if(Integer.valueOf(msjIn.getOrdenpago().getOpInsClave())==null)
				{
					aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 5);
					aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La clave de institución  ordenante es obligatoria para este Tipo de Pago");
					validate = false; 
				}else
				if(msjIn.getOrdenpago().getOpMonto()==null)
				{
					aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 9);
					aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "El Monto es obligatorio");
					validate = false;
				}else
				if(Integer.valueOf(msjIn.getOrdenpago().getOpTpClave())==null)
				{
					aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 57);
					aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La clave del pago es obligatorio para este Tipo de Pago");
					validate = false;
				}else
				if(msjIn.getOrdenpago().getOpCveRastreo()==null)
				{
					aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 92);
					aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La clave de rastreo es obligatoria");
					validate = false;
				}else
				if(msjIn.getOrdenpago().getOpEstado()==null)
				{
					aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 98);
					aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "El Estado del envío  es obligatorio");
					validate = false;
				}else
				if(msjIn.getOrdenpago().getOpTipoOrden()==null)
				{
					aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 106);
					aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "Tipo de orden es requerido.");
					validate = false; 
				}else
				if(Integer.valueOf(msjIn.getOrdenpago().getOpPrioridad())==null)
				{
					aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 85);
					aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La prioridad de la orden es un dato obligatorio");
					validate = false;	 
				}else
				if(Integer.valueOf(msjIn.getOrdenpago().getOpMeClave())==null)
				{
					aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 93);
					aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La fecha de operación  es obligatoria");
					validate = false;	 
				}if(msjIn.getOrdenpago().getOpTopologia()==null)
				{
					aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 87);
					aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La Topología  de la orden es obligatorio");
					validate = false;	 
				}else
				if(msjIn.getOrdenpago().getOpUsuClave()==null)
				{
					aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 158);
					aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La clave del banco usuario es obligatoria para este Tipo de Pago");
					validate = false;	 
				}else
				//valida tipo de cuentas entrantes
				if(!validateAccountType(request, opTcClaveBen, aBagSPJavaOrchestration ) && msjIn.getOrdenpago().getOpTpClave()==1)
				{
					aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 400602);
					aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "El tipo de destino no existe en el catalogo [bv_tipo_cuenta_spei].");
					validate = false;	
				}else
				{ 
					if (logger.isDebugEnabled()) 
					{
						logger.logDebug("JC Tipo de cuenta "+opTcClaveBen);
					}
					
					if(opTcClaveBen.equals(aBagSPJavaOrchestration.get("codTarDeb")))
					{
						if( !digitValidateNum(msjIn.getOrdenpago().getOpCuentaBen()))
						{
							aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 34);
							aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La cuenta del beneficiario solo puede ser numérica");
							validate = false;	
						}else
							if(!(msjIn.getOrdenpago().getOpCuentaBen().length()==16))
							{
								aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 38);
								aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "Para tipo de cuenta Tarjeta de Debito la cuenta del beneficiario debe ser de 16 dígitos.");
								validate = false;	
							}
					}else if(opTcClaveBen.equals(aBagSPJavaOrchestration.get("codTelDeb"))) {
						
						
						if( !digitValidateNum(msjIn.getOrdenpago().getOpCuentaBen()))
						{
							aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 34);
							aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La cuenta del beneficiario solo puede ser numérica");
							validate = false;	
						}else
							if(!(msjIn.getOrdenpago().getOpCuentaBen().length()==10))
							{
								aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 38);
								aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "Para tipo de cuenta Telefono, la cuenta del beneficiario debe ser de 10 dígitos.");
								validate = false;	
							}						
					}
						
				}

			}else
				if(Constans.ODPS_CANCELADAS_LOCAL.equals(msjIn.getCategoria()))
				{
					if(msjIn.getOrdenpago().getOpFechaOper()==null)
					{
						aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 93);
						aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La fecha de operación  es obligatoria");
						validate = false;
					}else
					if(Integer.valueOf(msjIn.getOrdenpago().getOpFolio())==null)
					{
						aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 445);
						aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "El Folio CoDi es obligatorio");
						validate = false;
					}else
					if(Integer.valueOf(msjIn.getOrdenpago().getOpInsClave())==null)
					{
						aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 5);
						aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La clave de institución  ordenante es obligatoria para este Tipo de Pago");
						validate = false; 
					}else
					if(Integer.valueOf(msjIn.getOrdenpago().getOpTpClave())==null)
					{
						aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 81);
						aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "El tipo de operación  es obligatorio para este Tipo de Pago");
						validate = false;
					}else
					if(msjIn.getOrdenpago().getOpCveRastreo()==null)
					{
						aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 92);
						aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La clave de rastreo es obligatoria");
						validate = false;
					}else
					if(msjIn.getOrdenpago().getOpEstado()==null)
					{
						aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 98);
						aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "El Estado del envío  es obligatorio");
						validate = false;
					}else
					if(msjIn.getOrdenpago().getOpTipoOrden()==null)
					{
						aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 106);
						aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "Tipo de orden es requerido.");
						validate = false; 
					}else
					if(Integer.valueOf(msjIn.getOrdenpago().getOpPrioridad())==null)
					{
						aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 85);
						aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La prioridad de la orden es un dato obligatorio");
						validate = false;	 
					}else
					if(Integer.valueOf(msjIn.getOrdenpago().getOpMeClave())==null)
					{
						aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 93);
						aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La fecha de operación  es obligatoria");
						validate = false;	 
					}if(msjIn.getOrdenpago().getOpTopologia()==null)
					{
						aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 87);
						aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La Topología  de la orden es obligatorio");
						validate = false;	 
					}else
					if(msjIn.getOrdenpago().getOpUsuClave()==null)
					{
						aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 158);
						aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La clave del banco usuario es obligatoria para este Tipo de Pago");
						validate = false;	 
					}
				}
		
		return validate;
	}
	public boolean digitValidateNum(String cadena) 
	{
	    Pattern patron = Pattern.compile("^\\d+$");
	    return patron.matcher(cadena).matches();
    }
	
	private IProcedureResponse getDataSPEI(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration, String clave) 
	{
		if (logger.isDebugEnabled()) 
		{
			logger.logDebug("Entrando a getDataSPEI");
		}
		//llamar sp cambio de estado transfer spei 
		IProcedureRequest procedureRequest = initProcedureRequest(request);

		procedureRequest.setSpName("cob_bvirtual..sp_act_transfer_spei");
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID,  ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500161");
		procedureRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500161");
		procedureRequest.addInputParam("@i_operacion", ICTSTypes.SYBVARCHAR, "S");			
		procedureRequest.addInputParam("@i_clave_rastreo", ICTSTypes.SYBVARCHAR, clave);
		
		procedureRequest.addOutputParam("@o_cuenta_ori", ICTSTypes.SYBVARCHAR, "");
		procedureRequest.addOutputParam("@o_concepto", ICTSTypes.SYBVARCHAR, "");
		procedureRequest.addOutputParam("@o_monto", ICTSTypes.SYBMONEY, "");
		procedureRequest.addOutputParam("@o_canal", ICTSTypes.SQLINTN, "");
		procedureRequest.addOutputParam("@o_id_transaccion_core", ICTSTypes.SQLINTN, "");
		procedureRequest.addOutputParam("@o_comision", ICTSTypes.SYBMONEY, "");
		procedureRequest.addOutputParam("@o_proceso_origen", ICTSTypes.SQLINTN, "");
		procedureRequest.addOutputParam("@o_clabe", ICTSTypes.SYBVARCHAR, "");
		procedureRequest.addOutputParam("@o_nombre", ICTSTypes.SYBVARCHAR, "");
		procedureRequest.addOutputParam("@o_ced_ruc", ICTSTypes.SYBVARCHAR, "");
		procedureRequest.addOutputParam("@o_clabe_des", ICTSTypes.SQLVARCHAR, "");
		procedureRequest.addOutputParam("@o_tipo_cuenta_ori", ICTSTypes.SQLVARCHAR, "");
		procedureRequest.addOutputParam("@o_tipo_cuenta_des", ICTSTypes.SQLVARCHAR, "");
		 
		IProcedureResponse procedureResponseLocal = executeCoreBanking(procedureRequest);
		if (logger.isDebugEnabled()) 
		{
			logger.logDebug("getDataSPEIreverse:"+procedureResponseLocal.getProcedureResponseAsString());
		}
		if(!procedureResponseLocal.hasError() && procedureResponseLocal.getReturnCode()==0)
		{
			aBagSPJavaOrchestration.put("o_cuenta_ori",procedureResponseLocal.readValueParam("@o_cuenta_ori"));
			aBagSPJavaOrchestration.put("o_concepto",procedureResponseLocal.readValueParam("@o_concepto"));
			aBagSPJavaOrchestration.put("o_monto",procedureResponseLocal.readValueParam("@o_monto"));
			aBagSPJavaOrchestration.put("o_canal",procedureResponseLocal.readValueParam("@o_canal"));
			aBagSPJavaOrchestration.put("o_id_transaccion_core",procedureResponseLocal.readValueParam("@o_id_transaccion_core"));
			aBagSPJavaOrchestration.put("o_comision",procedureResponseLocal.readValueParam("@o_comision"));
			aBagSPJavaOrchestration.put("o_proceso_origen",procedureResponseLocal.readValueParam("@o_proceso_origen"));
			aBagSPJavaOrchestration.put("o_clabe",procedureResponseLocal.readValueParam("@o_clabe"));
			aBagSPJavaOrchestration.put("o_nombre",procedureResponseLocal.readValueParam("@o_nombre"));
			aBagSPJavaOrchestration.put("o_ced_ruc",procedureResponseLocal.readValueParam("@o_ced_ruc"));
			aBagSPJavaOrchestration.put("o_clabe_des",procedureResponseLocal.readValueParam("@o_clabe_des"));
			aBagSPJavaOrchestration.put("o_tipo_cuenta_des",procedureResponseLocal.readValueParam("@o_tipo_cuenta_des"));
			aBagSPJavaOrchestration.put("o_tipo_cuenta_ori",procedureResponseLocal.readValueParam("@o_tipo_cuenta_ori"));
		}
		
		return procedureResponseLocal;
	}
	
	private String setParamKarpayDate(IProcedureRequest anOriginalRequest, String nemonico, String producto, String fecha) 
	{
		if (logger.isDebugEnabled()) {
			logger.logDebug("Begin flow, setParamKarpayDate");
		}
		
		IProcedureRequest reqTMPCentral = (initProcedureRequest(anOriginalRequest));		
		reqTMPCentral.setSpName("cob_bvirtual..sp_ensesion");
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_CENTRAL);
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500167");
		reqTMPCentral.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500167");
		reqTMPCentral.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "F");
		reqTMPCentral.addInputParam("@i_fecha",ICTSTypes.SQLDATETIME, fecha);
		reqTMPCentral.addInputParam("@i_nemonico",ICTSTypes.SQLVARCHAR, nemonico);
		reqTMPCentral.addInputParam("@i_producto",ICTSTypes.SQLVARCHAR, producto);	
		reqTMPCentral.addInputParam("@i_parametro",ICTSTypes.SQLVARCHAR, "PROCCESS DATE KARPAY");
		
	    IProcedureResponse wProcedureResponseCentral = executeCoreBanking(reqTMPCentral);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Ending flow, setParamKarpayDate with wProcedureResponseCentral: " + wProcedureResponseCentral.getProcedureResponseAsString());
		}
		
		if (!wProcedureResponseCentral.hasError()) {
			
			return fecha;
		} 
		
		return "";
	}
	
	private int catalog(IProcedureRequest anOriginalRequest, String tabla, String codigo, String valor, String operacion, String estado) {
		if (logger.isDebugEnabled()) {
			logger.logDebug("Begin flow, setCatalog");
		}
		
		IProcedureRequest reqTMPCentral = (initProcedureRequest(anOriginalRequest));		
		reqTMPCentral.setSpName("cobis..sp_catalogo");
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_CENTRAL);
		reqTMPCentral.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, operacion);
		reqTMPCentral.addInputParam("@i_tabla",ICTSTypes.SQLVARCHAR, tabla);
		reqTMPCentral.addInputParam("@i_codigo",ICTSTypes.SQLVARCHAR, codigo);	
		reqTMPCentral.addInputParam("@i_descripcion",ICTSTypes.SQLDATETIME, valor);	
		reqTMPCentral.addInputParam("@i_estado",ICTSTypes.SQLDATETIME, estado);	
	    IProcedureResponse wProcedureResponseCentral = executeCoreBanking(reqTMPCentral);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Ending flow, setCatalog: " + wProcedureResponseCentral.getProcedureResponseAsString());
		}
		
		if (wProcedureResponseCentral.getReturnCode()!=0) {
			
			return wProcedureResponseCentral.getReturnCode();
		} 
		
		return 0;
	}
	private void loadParameters( IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		ParametrizationSPEI params = new ParametrizationSPEI(); 
		params.getSpeiParameters( anOriginalRequest, aBagSPJavaOrchestration, cacheManager);
	}
	private IProcedureResponse singType(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration, String tabla, String codigo) {
		if (logger.isDebugEnabled()) {
			logger.logDebug("Begin flow, singType");
		}
		
		IProcedureRequest requestProcedureLocal = (initProcedureRequest(anOriginalRequest));		
		requestProcedureLocal.setSpName("cob_bvirtual..sp_firma_pago");
		requestProcedureLocal.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, 
				IMultiBackEndResolverService.TARGET_LOCAL);
		requestProcedureLocal.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500169");
		requestProcedureLocal.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "S");
		requestProcedureLocal.addInputParam("@i_tabla",ICTSTypes.SQLVARCHAR, tabla);
		requestProcedureLocal.addInputParam("@i_codigo",ICTSTypes.SQLVARCHAR, codigo);	
		requestProcedureLocal.addOutputParam("@o_valor", ICTSTypes.SYBVARCHAR, "X");
		
	    IProcedureResponse wProcedureResponseLocal = executeCoreBanking(requestProcedureLocal);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Ending flow, singType: " + wProcedureResponseLocal.getProcedureResponseAsString());
		}
		
		if (wProcedureResponseLocal.getReturnCode()==0) {
			
			aBagSPJavaOrchestration.put("tipoFirma", wProcedureResponseLocal.readValueParam("@o_valor"));
		} 
		return wProcedureResponseLocal;
		
	}
	private IProcedureResponse parameterizationLoad(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration, String paymentType) {
		if (logger.isDebugEnabled()) {
			logger.logDebug("Begin flow, singType");
		}
		IProcedureResponse wProcedureResponseLocal = new ProcedureResponseAS();
		
		if ( CacheUtils.getCacheValue(cacheManager, Constants.SPEI_CONF, Constants.SPEI_PARAMS) == null )
		{
			IProcedureRequest requestProcedureLocal = (initProcedureRequest(anOriginalRequest));		
			requestProcedureLocal.setSpName("cob_bvirtual..sp_firma_pago");
			requestProcedureLocal.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, 
					IMultiBackEndResolverService.TARGET_LOCAL);
			requestProcedureLocal.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500169");
			requestProcedureLocal.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "S");
			requestProcedureLocal.addInputParam("@i_tipo_pago",ICTSTypes.SQLVARCHAR, paymentType);	
			requestProcedureLocal.addOutputParam("@o_tipo_firma", ICTSTypes.SYBVARCHAR, "-1");
			requestProcedureLocal.addOutputParam("@o_param_ins_ben", ICTSTypes.SYBVARCHAR, "0");
			requestProcedureLocal.addOutputParam("@o_cod_tar_deb", ICTSTypes.SYBVARCHAR, "0");
			requestProcedureLocal.addOutputParam("@o_cod_tel_deb", ICTSTypes.SYBVARCHAR, "0");
			requestProcedureLocal.addOutputParam("@o_tipo_spei_in", ICTSTypes.SYBVARCHAR, "-1");
			requestProcedureLocal.addOutputParam("@o_tipo_pago_extemporeano", ICTSTypes.SYBVARCHAR, "-1");
			
			
		    wProcedureResponseLocal = executeCoreBanking(requestProcedureLocal);
			
			if (logger.isDebugEnabled()) {
				logger.logDebug("Ending flow, singType: " + wProcedureResponseLocal.getProcedureResponseAsString());
			}
			
			if (wProcedureResponseLocal.getReturnCode()==0) {
				
				Map<String, Object> parametrization = new HashMap<String, Object>();
				
				parametrization.put("tipoFirma", wProcedureResponseLocal.readValueParam("@o_tipo_firma"));
				parametrization.put("paramInsBen", wProcedureResponseLocal.readValueParam("@o_param_ins_ben"));
				parametrization.put("codTarDeb", wProcedureResponseLocal.readValueParam("@o_cod_tar_deb"));
				parametrization.put("codTelDeb", wProcedureResponseLocal.readValueParam("@o_cod_tel_deb"));
				parametrization.put("tipoSpeiIn", wProcedureResponseLocal.readValueParam("@o_tipo_spei_in"));
				parametrization.put("tipoPagoExtemporeano", wProcedureResponseLocal.readValueParam("@o_tipo_pago_extemporeano"));
				
				aBagSPJavaOrchestration.putAll(parametrization);
				CacheUtils.putCacheValue(cacheManager, Constants.SPEI_CONF,Constants.SPEI_PARAMS, parametrization);
			} 
		}else {
			
			Map<String, Object> parametrization = (Map<String, Object>) CacheUtils.getCacheValue(cacheManager, Constants.SPEI_CONF, Constants.SPEI_PARAMS);
			aBagSPJavaOrchestration.putAll(parametrization);
			if (logger.isDebugEnabled()) {
				logger.logDebug("Cache already exists for SPEI_IN_PARAMS: "+parametrization.toString());
			}
		}
		return wProcedureResponseLocal;
		
	}
	private int logEntryApi(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration, 
			String operacion, String tipoEntrada, String firma, String error, String response, Integer id, String request ) {
		if (logger.isDebugEnabled()) {
			logger.logDebug("Begin flow, logEntryApi");
		}
		Integer logId = 0;
		try
		{
			
			mensaje msjIn = (mensaje) aBagSPJavaOrchestration.get("speiTransaction");
			CardPAN card= new CardPAN();
			IProcedureRequest requestProcedureLocal = (initProcedureRequest(anOriginalRequest));		
			requestProcedureLocal.setSpName("cob_bvirtual..sp_bv_log_conn_karpay");
			requestProcedureLocal.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, 
					IMultiBackEndResolverService.TARGET_LOCAL);
			
			requestProcedureLocal.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18700121");
			requestProcedureLocal.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, operacion);
			requestProcedureLocal.addInputParam("@i_lc_tipo_entrada",ICTSTypes.SQLVARCHAR, tipoEntrada);
			requestProcedureLocal.addInputParam("@i_lc_categoria",ICTSTypes.SQLVARCHAR, msjIn.getCategoria());
			requestProcedureLocal.addInputParam("@i_lc_request",ICTSTypes.SQLVARCHAR, request);		
			if("I".equals(operacion) && 
				   (Constans.ODPS_LIQUIDADAS_CARGOS.equals( msjIn.getCategoria() )   || 
					Constans.ODPS_CANCELADAS_X_BANXICO.equals( msjIn.getCategoria() )||
					Constans.ODPS_LIQUIDADAS_ABONOS.equals( msjIn.getCategoria() )   ||
					Constans.ODPS_CANCELADAS_LOCAL.equals( msjIn.getCategoria() )    ||
					Constans.ODPS_CANCELADAS_X_BANXICO.equals( msjIn.getCategoria() )    
					))
			{
				DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		        LocalDate date = null;
		        //enmascara las tarjetas
		        String opCuentaBen="";
				String opCuentaOrd="";
		        // Convierte la fecha de String a LocalDate

				if (logger.isDebugEnabled()) {
					logger.logDebug("jc hour:::::::::" +msjIn.getOrdenpago().getOpHoraLiqBm());
				}
  
				date = LocalDate.parse(msjIn.getOrdenpago().getOpFechaOper(), inputFormatter);
				if( msjIn.getOrdenpago().getOpTcClaveBen()==3){
					opCuentaBen = card.maskNumber(msjIn.getOrdenpago().getOpCuentaBen());
					request = request.replace(msjIn.getOrdenpago().getOpCuentaBen(), card.maskNumber(msjIn.getOrdenpago().getOpCuentaBen()));
				}
				else
					opCuentaBen = msjIn.getOrdenpago().getOpCuentaBen();
	        
    	 		if( msjIn.getOrdenpago().getOpTcClaveOrd()==3){
    	 			opCuentaOrd = card.maskNumber(msjIn.getOrdenpago().getOpCuentaOrd());
    	 			request = request.replace(msjIn.getOrdenpago().getOpCuentaOrd(), card.maskNumber(msjIn.getOrdenpago().getOpCuentaOrd()));
    	 		}
    	 		else
    	 			opCuentaOrd = msjIn.getOrdenpago().getOpCuentaOrd();
  
		        // Formatea la fecha a MM/dd/yyyy
		        String processDate = date.format(outputFormatter);
	
		        requestProcedureLocal.setValueParam("@i_lc_request", request);	
		        requestProcedureLocal.addInputParam("@i_lc_clave_rastreo",ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpCveRastreo());
				requestProcedureLocal.addInputParam("@i_lc_tipo_pago",ICTSTypes.SQLINT4, String.valueOf( msjIn.getOrdenpago().getOpTpClave()));
				requestProcedureLocal.addInputParam("@i_lc_cuenta_ordenante",ICTSTypes.SQLVARCHAR, opCuentaOrd);
				requestProcedureLocal.addInputParam("@i_lc_institucion_ordenante",ICTSTypes.SQLVARCHAR, String.valueOf( msjIn.getOrdenpago().getOpInsClave()));
				requestProcedureLocal.addInputParam("@i_lc_cuenta_beneficiaria",ICTSTypes.SQLVARCHAR,  opCuentaBen);
				requestProcedureLocal.addInputParam("@i_lc_monto",ICTSTypes.SQLMONEY4,  String.valueOf(msjIn.getOrdenpago().getOpMonto()));
				requestProcedureLocal.addInputParam("@i_lc_firmarequest",ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpFirmaDig());
				requestProcedureLocal.addInputParam("@i_lc_fecha_proceso",ICTSTypes.SQLDATETIME, processDate);
				requestProcedureLocal.addInputParam("@i_lc_hora_bm",ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpHoraLiqBm());
			}else
				if("U".equals(operacion) )
				{
					requestProcedureLocal.addInputParam("@i_lc_firma",ICTSTypes.SQLVARCHAR, firma);
					requestProcedureLocal.addInputParam("@i_lc_error",ICTSTypes.SQLVARCHAR, error);
					requestProcedureLocal.addInputParam("@i_lc_response",ICTSTypes.SQLVARCHAR, response);
					requestProcedureLocal.addInputParam("@i_lc_id",ICTSTypes.SQLINT4, id.toString());	
				}
			
			requestProcedureLocal.addOutputParam("@o_lc_id", ICTSTypes.SQLINT4, "0");
		        
		    IProcedureResponse wProcedureResponseLocal = executeCoreBanking(requestProcedureLocal);
			
			if (logger.isDebugEnabled()) {
				logger.logDebug("Ending flow, logEntryApi: " + wProcedureResponseLocal.getProcedureResponseAsString());
			}
			
			if (wProcedureResponseLocal.getReturnCode()==0) {
				
				logId = Integer.parseInt(wProcedureResponseLocal.readValueParam("@o_lc_id"));
				aBagSPJavaOrchestration.put("idLog", logId);
			}
		}catch (Exception xe) {
			logger.logError("Error logEntryApi:",xe);
		}
		return logId;
		
	}

	private IProcedureResponse updateStatusOperation(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration, String state, String clave, String fechaLiq,String hour)
	{
		if(logger.isDebugEnabled())
			logger.logDebug("init updateStatus");
		IProcedureRequest procedureRequest = initProcedureRequest(request);

		procedureRequest.setSpName("cob_bvirtual..sp_act_transfer_spei");
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID,  ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500161");
		procedureRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500161");
		procedureRequest.addInputParam("@i_operacion", ICTSTypes.SYBVARCHAR, "A");
		procedureRequest.addInputParam("@i_clave_rastreo", ICTSTypes.SYBVARCHAR, clave);
		procedureRequest.addInputParam("@i_estado", ICTSTypes.SYBVARCHAR, state);
		procedureRequest.addInputParam("@i_fecha_liq", ICTSTypes.SQLDATETIME, fechaLiq);
		procedureRequest.addInputParam("@i_lc_hora_bm", ICTSTypes.SQLVARCHAR, hour);
		//poner fecha de operacion karpay
		procedureRequest.addOutputParam("@o_id_transaccion_core", ICTSTypes.SQLINTN, "");

		IProcedureResponse procedureResponseLocal = executeCoreBanking(procedureRequest);
		if(logger.isDebugEnabled()) {
			logger.logDebug("response updateStatus :" + procedureResponseLocal.getCTSMessageAsString());
			logger.logDebug("has Error: "+procedureResponseLocal.hasError());
		}

		if(!procedureResponseLocal.hasError()){
			aBagSPJavaOrchestration.put("@o_id_transaccion_core", procedureResponseLocal.readValueParam("@o_id_transaccion_core"));
			updateSpeiAdditionalData(state, clave, aBagSPJavaOrchestration);
		}


		return procedureResponseLocal;
	}
	
	private IProcedureResponse updateStatusOperation(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration, String state, String clave, String fechaLiq)
	{
		if(logger.isDebugEnabled())
			logger.logDebug("init updateStatus");
		IProcedureRequest procedureRequest = initProcedureRequest(request);
				
		procedureRequest.setSpName("cob_bvirtual..sp_act_transfer_spei");
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID,  ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500161");
		procedureRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500161");
		procedureRequest.addInputParam("@i_operacion", ICTSTypes.SYBVARCHAR, "A");			
		procedureRequest.addInputParam("@i_clave_rastreo", ICTSTypes.SYBVARCHAR, clave);
		procedureRequest.addInputParam("@i_estado", ICTSTypes.SYBVARCHAR, state);
		procedureRequest.addInputParam("@i_fecha_liq", ICTSTypes.SQLDATETIME, fechaLiq);
		//poner fecha de operacion karpay
		
		IProcedureResponse procedureResponseLocal = executeCoreBanking(procedureRequest);
		if(logger.isDebugEnabled())
			logger.logDebug("response updateStatus :"+procedureResponseLocal.getCTSMessageAsString() );
		
		return procedureResponseLocal;
	}

	private void updateSpeiAdditionalData(String state, String clave, Map<String, Object> aBagSPJavaOrchestration) {
		if(logger.isDebugEnabled())
			logger.logDebug("Staring updateSpeiAdditionalData" );

		String movementType = "SPEI_DEBIT";
		Map<String, String> bag = new HashMap<>();
		bag.put("secuential", (String) aBagSPJavaOrchestration.get("@o_id_transaccion_core") );
		bag.put("@i_estado_spei", state);
		bag.put("@i_clave_rastreo", clave);
		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setChannelId("8");
		try {
			if(logger.isDebugEnabled()){
				logger.logDebug("secuential: "+aBagSPJavaOrchestration.get("@o_id_transaccion_core"));
				logger.logDebug("@i_estado_spei: "+state);
				logger.logDebug("@i_clave_rastreo: "+clave);
			}
			ServerResponse serverResponse = getCoreServer().getServerStatus(serverRequest);
			SaveAdditionalDataImpl saveAdditionalData = new SaveAdditionalDataImpl();
			if(logger.isDebugEnabled())
				logger.logDebug("Server is: "+serverResponse.getOnLine());
			saveAdditionalData.saveData(movementType, serverResponse.getOnLine(), bag, "U");
			if(logger.isDebugEnabled())
				logger.logDebug("Finishing updateSpeiAdditionalData");
		} catch (CTSInfrastructureException | CTSServiceException e) {
			if(logger.isErrorEnabled())
				logger.logError(e);
        }
    }
	
	private IProcedureResponse insertUpdateInstitutions(IProcedureRequest request,
			String code, String bankName, String typeInstitution, String state, String stadoPgd)
	{
		if(logger.isDebugEnabled())
			logger.logDebug("init updateStatus");
		IProcedureRequest procedureRequest = initProcedureRequest(request);
			
		procedureRequest.setSpName("cob_bvirtual..sp_mant_ifis");
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID,  ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "1870009");
		procedureRequest.addInputParam("@t_trn", ICTSTypes.SQLINT4, "1870009");
		procedureRequest.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "A");			
		procedureRequest.addInputParam("@i_cod_ban", ICTSTypes.SQLINT4, code);
		procedureRequest.addInputParam("@i_nom_banco", ICTSTypes.SQLVARCHAR, bankName);
		procedureRequest.addInputParam("@i_tip_inst", ICTSTypes.SQLVARCHAR, typeInstitution);
		procedureRequest.addInputParam("@i_estado_spi", ICTSTypes.SQLVARCHAR, state);
		procedureRequest.addInputParam("@i_estado_pgd", ICTSTypes.SQLVARCHAR, stadoPgd);
		
		
		IProcedureResponse procedureResponseLocal = executeCoreBanking(procedureRequest);
		if(logger.isDebugEnabled())
			logger.logDebug("response updateStatus :"+procedureResponseLocal.getCTSMessageAsString() );
		
		return procedureResponseLocal;
	}
	private IProcedureResponse updateDevolution(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration, 
			 String operacion, Integer error, String errorDes, String fechaLiq ) {
		if (logger.isDebugEnabled()) {
			logger.logDebug("Begin flow, registerDevolution");
		}
		
		mensaje msjIn = (mensaje) aBagSPJavaOrchestration.get("speiTransaction");
		
		IProcedureRequest requestProcedureLocal = (initProcedureRequest(anOriginalRequest));		
		requestProcedureLocal.setSpName("cob_bvirtual..sp_bv_devoluciones");
		requestProcedureLocal.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, 
				IMultiBackEndResolverService.TARGET_LOCAL);
		
		requestProcedureLocal.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18700121");
		requestProcedureLocal.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, operacion);
		requestProcedureLocal.addInputParam("@i_de_clave_rastreo",ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpCveRastreo());
		requestProcedureLocal.addInputParam("@i_de_fecha_liquidacion", ICTSTypes.SQLDATETIME, fechaLiq);
		requestProcedureLocal.addOutputParam("@o_de_id", ICTSTypes.SQLINT4, "0");
	        
	    IProcedureResponse wProcedureResponseLocal = executeCoreBanking(requestProcedureLocal);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Ending flow, registerDevolution: " + wProcedureResponseLocal.getProcedureResponseAsString());
		}
		
		return wProcedureResponseLocal;
		
	}
	private void searchOriginAccountDestination(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration, String clave, mensaje msj)
	{
		IProcedureResponse procedureGetDataSpei = getDataSPEI(request, aBagSPJavaOrchestration, clave);
		if(procedureGetDataSpei.getReturnCode() == 0)
		{
			msj.getOrdenpago().setOpConceptoPag2((String)aBagSPJavaOrchestration.get("o_concepto"));
			msj.getOrdenpago().setOpTcClaveBen(aBagSPJavaOrchestration.get("o_tipo_cuenta_ori")!= null ? Integer.parseInt((String)aBagSPJavaOrchestration.get("o_tipo_cuenta_ori")) : 0);
			msj.getOrdenpago().setOpCuentaBen((String)aBagSPJavaOrchestration.get("o_clabe"));
	    }
	}
	public void logHour(String txt)
	{
		if(logger.isDebugEnabled())
		{
			LocalDateTime fechaHoraActual = LocalDateTime.now();
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
	        String fechaHoraFormateada = fechaHoraActual.format(formatter);
	        logger.logDebug("hour "+txt+":"+fechaHoraFormateada); 
		}
	}
	
	private int tryParseToInteger(String stringValue, int defaultValue) {
		try {
			return Integer.parseInt(stringValue);
		} catch(Exception ex) {
			return defaultValue;
		}
	}

	private boolean tryParseToBoolean(String stringValue, boolean defaultValue) {
		try {
			return Boolean.parseBoolean(stringValue);
		} catch(Exception ex) {
			return defaultValue;
		}
	}
	
	@Reference(referenceInterface = ICoreServiceMonetaryTransaction.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceMonetaryTransaction", unbind = "unbindCoreServiceMonetaryTransaction")
	protected ICoreServiceMonetaryTransaction coreServiceMonetaryTransaction;

	public void bindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = service;
	}

	public void unbindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = null;
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
	protected ICoreServer coreServer;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindCoreServer(ICoreServer service) {
		coreServer = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindCoreServer(ICoreServer service) {
		coreServer = null;
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceSelfAccountTransfers.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceSelfAccountTransfers", unbind = "unbindCoreServiceSelfAccountTransfers")
	private ICoreServiceSelfAccountTransfers coreServiceSelfAccountTransfers;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindCoreServiceSelfAccountTransfers(ICoreServiceSelfAccountTransfers service) {
		coreServiceSelfAccountTransfers = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindCoreServiceSelfAccountTransfers(ICoreServiceSelfAccountTransfers service) {
		coreServiceSelfAccountTransfers = null;
	}

	@Override
	public ICoreService getCoreService() {
		return coreService;
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreService.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreService coreService;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreService(ICoreService service) {
		coreService = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreService(ICoreService service) {
		coreService = null;
	}

	@Reference(referenceInterface = ICoreServiceSendNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceNotification", unbind = "unbindCoreServiceNotification")
	public ICoreServiceSendNotification coreServiceNotification;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreServiceNotification(ICoreServiceSendNotification service) {
		coreServiceNotification = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreServiceNotification(ICoreServiceSendNotification service) {
		coreServiceNotification = null;
	}
	private void registerTransactionWebHook(Map<String, Object> aBagSPJavaOrchestration, mensaje msjSPEI_In) {	
        String movementType = "SPEI_RETURN";
		String typeEvent = "S";
        String codeError = "0";
		String messageError = "";
		String logKarpay = "0";

        if (logger.isDebugEnabled()) {
            logger.logDebug(" Entrando en registerTransactionWebHook");
        }

        if (aBagSPJavaOrchestration.get("eventWH") != null) {
        	typeEvent = aBagSPJavaOrchestration.get("eventWH").toString();
        }
        
        if (aBagSPJavaOrchestration.get("idLog") != null) {
        	logKarpay = aBagSPJavaOrchestration.get("idLog").toString();
        }

    	try {
        	if (typeEvent.equals("F")) {
        		if (aBagSPJavaOrchestration.get("code_error")!= null) {
    				codeError = aBagSPJavaOrchestration.get("code_error").toString();
    			}
    			
    			if (aBagSPJavaOrchestration.get("message_error")!= null) {
    				messageError = aBagSPJavaOrchestration.get("message_error").toString();
    			}    			
        	}
        	 
			IProcedureRequest request = new ProcedureRequestAS();
			
			request.setSpName("cob_bvirtual..sp_bv_devolucion_spei_webhook");
			request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, "local");			
			request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
			
			request.addInputParam("@i_tipotrn", ICTSTypes.SQLVARCHAR, typeEvent);
			request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "U");
			request.addInputParam("@i_movementType", ICTSTypes.SQLVARCHAR, movementType);
			request.addInputParam("@i_logKarpay", ICTSTypes.SQLINT4, logKarpay);
			request.addInputParam("@i_speiTranckingId", ICTSTypes.SQLVARCHAR,  msjSPEI_In.getOrdenpago().getOpCveRastreo());
			request.addInputParam("@i_errorDetailsCode", ICTSTypes.SQLVARCHAR, codeError);
			request.addInputParam("@i_errorDetailsMessage", ICTSTypes.SQLVARCHAR, messageError);
			
			IProcedureResponse wProductsQueryResp = executeCoreBanking(request);

			if (logger.isDebugEnabled()) {
				logger.logDebug("Response Corebanking registerAllTransactionFailed: " + wProductsQueryResp.getProcedureResponseAsString());
			}
			
			if (logger.isInfoEnabled()) {
				logger.logInfo(" Saliendo de registerTransactionWebHook");
			}

        }catch(Exception e){
            logger.logError(" Error Catastrofico en registerTransactionWebHook SPEI_RETURN");
        }	
    }

	@Override
	protected Object categoryNotImplemented(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration)
	{
		if (logger.isDebugEnabled()){
			logger.logDebug("Inicio CategoryNotImplemented");
		}
		Mensaje msg = new Mensaje();
		Respuesta responseXml = new Respuesta();
		String response="";
		mensaje msjIn = (mensaje) aBagSPJavaOrchestration.get("speiTransaction");
		responseXml.setErrCodigo(-1);
		responseXml.setErrDescripcion("Categoria no implementada en el core");
	
		responseXml.setId("0");
		msg.setCategoria(msjIn.getCategoria()+Constans.RESPUESTA);
		msg.setRespuesta(responseXml);
		
		response = toStringXmlObject(msg);  
		aBagSPJavaOrchestration.put("result", response);
		
		if (logger.isDebugEnabled()){
			logger.logDebug("Fin CategoryNotImplemented:"+response);
		}
		return response;
	}	
	
	@Reference(bind = "setCacheManager", unbind = "unsetCacheManager", cardinality = ReferenceCardinality.MANDATORY_UNARY)
	private ICacheManager cacheManager;
	
	public void setCacheManager(ICacheManager cacheManager){
		this.cacheManager = cacheManager;
	}
	
	public void unsetCacheManager(ICacheManager cacheManager) {
		this.cacheManager = null;		
	}
}
