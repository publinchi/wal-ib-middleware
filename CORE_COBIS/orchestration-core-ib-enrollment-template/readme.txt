Instalación manual del componente

1) Modificar el archivo $COBIS_HOME/CIS/SERVICES/CTSTRANSFORMATION/services/configuracion-ruteo-servicios.xml
   Para agregarle la entrada
	  <indicator name="com.cobiscorp.cobis.csp.services.IOrchestrator" value="(service.identifier=SelfAccountTransferOrchestationCore)"/>
   y  comentar la entrada 
	  <!--<indicator name="com.cobiscorp.cobis.csp.services.IOrchestrator" value="(service.identifier=SelfAccountTransferOrchestation)"/>-->
	  
3) Agregar en el archivo $COBIS_HOME/CTS_MF/infrastructure/cts-ccm-plan-config.xml
	  <plugin name="orchestration-core-ib-enrollment-template" path="../../CIS/SERVICES/plugins/IBOrchestration/orchestration-core-ib-enrollment-template-4.5.0.jar"/> --> 
	  <plugin name="orchestration-core-ib-enrollment" path="../../CIS/SERVICES/plugins/IBOrchestration/orchestration-core-ib-enrollment-4.5.0.jar"/>

3) Recargar desde la consola de CTS el servicio de CTS Transformation

4) Deployar el orchestration-core-ib-enrollment-template y orchestration-core-ib-enrollment por medio de mvn >  install
   Para ello validar que la configuración en el pom.xml debe tener 
	  <skip>false</skip> 

	  
	  
	  
	  