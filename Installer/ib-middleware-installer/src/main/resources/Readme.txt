Cambios en instaladores

30-Oct-2013
Se agrega validacion para CTS FIX 3.2.2.11
Se agrega nuevo plan para las orquestaciones CIS para CTS FIX 3.2.2.11
Se eliminan plugins de orquestaciones de csp-ccm-client-config.xml para CTS FIX 3.2.2.11

22-Oct-2013
Se agrega validacion para indentificar si la instalacion es para DEMO
Si la instalacion es para DAVIVIENDA, se verifican los pre-requisitos

25-Sep-2013
Se agrega orquestacion Java orchestration7x24-ib-sinpe-transfer-1.0.0.jar
Se agregan archivos de configuracion para SINPE en la ruta [COBIS_HOME]/CIS/SERVICES/CTSTRANSFORMATION/services/PEL
- configuracion-enviar-debito-debito-tiempo-real.xml
- configuracion-enviar-debito.xml
- configuracion-enviar-credito.xml
- configuracion-enviar-credito-tiempo-real.xml

Se agrega verificacion de la existencia de los archivos [COBIS_HOME]/CIS/SERVICES/CSPROUTING/services/PEL
- cobis-sinpe-enviar-credito-config.xml
- cobis-sinpe-enviar-debito-config.xml
- cobis-sinpe-enviar-debito-tiempo-real-config.xml
- cobis-sinpe-enviar-transferencia-tiempo-real-config.xml

Se modifica metodo XPathUpdate.editCtstransformation para incluir parametrizacion en el archivo 
[COBIS_HOME]/CIS/SERVICES/CTSTRANSFORMATION/services/csp-ctstransformation-service-config.xml de los siguientes archivos
- configuracion-enviar-debito-debito-tiempo-real.xml
- configuracion-enviar-debito.xml
- configuracion-enviar-credito.xml
- configuracion-enviar-credito-tiempo-real.xml

09-Ago-2013
Se agrega orquestacion Java orchestration7x24-ib-query-contract-1.0.0.jar
Se elimina orquestacion Template-023-Referencias_servicios_ARP.xml

29-Jul-2013
Se agrega orquestacion Template-015-Validacion_Cuenta.xml

25-Jul-2013
Se agrega orquestacion Configuration-055-tr_preauth_check.xml

20-Jul-2013
Validacion configuracion DBMS para SYBCTS y SQLCTS

05-Jul-2013
Optimizacion de instaladores, se agrega funcionalidad para validar todos los archivos de la instalacion

02-Jul-2013
Validacion para sacar backup de carpeta [COBIS_HOME]/CTS_MF/plugins/IB para la unificacion de instaladores IB y BIB

21-Jun-2013
Unificacion de instaladores IB y BIB

20-Jun-2013
Se agrega validacion de instalacion en el archivo
[COBIS_HOME]/CTS_MF/services-as/session/cobisbv-session-manager-config.xml

29-May-2013
Se elimina las configuraciones por defecto instaladas por CIS del archivo 
[COBIS_HOME]/CIS/SERVICES/DYNAMICORCHESTRATOR/services/dynamicorchestrator-config.xml

Se elimina el archivo para evitar la inconsistencia con la codificacion de caracteres
[COBIS_HOME]/CIS/SERVICES/CTSTRANSFORMATION/services/configuracion-ruteo-servicios.xml

21-May-2013	
Se elimina el pre-requisito del archivo 
[COBIS_HOME]/CTS_MF/services-as/servexecutor/services/IB-services.xml

Se agrega los pre-requisitos de los siguientes archivos
[COBIS_HOME]/CTS_MF/services-as/reentry/reentry-immediate-service-config.xml
[COBIS_HOME]/CTS_MF/services-as/session/cobisbv-session-manager-config.xml
[COBIS_HOME]/CTS_MF/services-as/utils/cobis-ssn-unique-config.xml
[COBIS_HOME]/CTS_MF/services-as/utils/cobisbv-ssn-unique-config.xml
[COBIS_HOME]/CTS_MF/services-as/servexecutor/business-service-executor-config.xml
[COBIS_HOME]/CTS_MF/services-as/servexecutor/service-executor-config.xml 

Se agrega pre-requisitos de reentry en los archivos
[COBIS_HOME]/CTS_MF/services-as/reentry/reentry-immediate-service-config.xml
[COBIS_HOME]/CTS_MF/infrastructure/cts-ccm-plan-config.xml

Se agrega pre-requisitos para version de CTS  menores a 3.2.2.9, donde se deben verificar los siguientes archivos
[COBIS_HOME]/CTS_MF/services-as/trlogger/tr-logger-config.xml
[COBIS_HOME]/CTS_MF/services-as/authorization/bv-services-authorization-service-config.xml
[COBIS_HOME]/CTS_MF/services-as/authorization/cobisbv-authorization-service-config.xml

Se actualiza pre-requisito para version de CTS mayores a 3.2.2.6 para el archivo
[COBIS_HOME]/CTS_MF/infrastructure/cts-ccm-plan-config.xml
	y para versiones de CTS menores a 3.2.2.6 para los archivos
[COBIS_HOME]/CTS_MF/infrastructure/cts-ccm-config.xml
[COBIS_HOME]/CTS_MF/infrastructure/cts-ccm-client-config.xml