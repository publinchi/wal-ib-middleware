Componentes a Recargar en el Master Plan cuando le cambian el servicio a implementar en una orquestacion
--------------------------------------------------------------------------------------------------------
BusinessServiceExecutor
BusinessServiceOrchestrator
BVExecutor
CoreBanking
CSPLicenses
CSPRoutingService
CtsExternalAuthenticationManagerServices
CTSTransformationService
DynamicOrchestratorCIS
Licenses
LockTransactionService
ServiceExecutorWS
SPExecutorService
SPOrchestrator15

Validaciones para cotizacion
1. debe estar el metodo GetExchangeRates en el plugin del CTS como omitido
2. En el caso de existir en la tabla cts_serv_catalog del central un registro para el servio GetExchangeRates el campo csc_procedure_validation debe estar en 'N' 
3. si existe una entrada en el archivo  C:\cobishome\CTS_MF\services-as\servexecutor\services\IB-services.xml para el metodo GetExchangeRates se debe colocar el atributo procedure-validation="false"

