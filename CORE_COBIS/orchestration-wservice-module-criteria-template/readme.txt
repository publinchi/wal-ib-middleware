Nota:
1) si existe una entrada en el archivo  C:\cobishome\CTS_MF\services-as\servexecutor\services\IB-services.xml para el metodo GetBanksByCountry se debe colocar el atributo procedure-validation="true" ya que este servicio no es anonimo
      <service id="InternetBanking.WebApp.Admin.Swift.GetBanksByCountry" interface="cobiscorp.ecobis.internetbanking.webapp.admin.service.service.ISwift" method="getBanksByCountry" description=" " procedure-validation="true"/>
      