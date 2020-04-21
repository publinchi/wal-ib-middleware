package com.cobiscorp.chanels.bv.test;

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.BankCollection
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Country
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.utils.BDD
import com.cobiscorp.test.utils.MQClient
import com.cobiscorp.test.utils.SqlExecutorUtils
import com.cobiscorp.test.utils.VirtualBankingUtil

class TestServiceBV {

	def TOPIC_NAME = "CTS_CONSOLE_REQ";
	def RESPONSE_QUEUE = "CONSOLE_RESP_MF";
	def final static PATHPLUGINS =  "/src/test/resources/virtual_banking/plugins/";
	def final static PATHCTSPLUGINS ="\\CTS_MF\\plugins\\framework\\test\\"

	@Before
	void setUp(){
		// agrego un nuevo Plan
		//XMLUtil.addPlan(CTSEnvironment.COBIS_HOME_DIR, "TestBV");
		//agrego los nuevo plugins al plan
		//XMLUtil.AddPluginAtPlan(CTSEnvironment.COBIS_HOME_DIR, "TestBV", "COBISCorp.eCOBIS.Bv.Test.DTO", "../plugins/framework/test/COBISCorp.eCOBIS.Bv.Test.DTO-1.0.0.0.jar");
		//XMLUtil.AddPluginAtPlan(CTSEnvironment.COBIS_HOME_DIR, "TestBV", "COBISCorp.eCOBIS.Bv.Test.Service", "../plugins/framework/test/COBISCorp.eCOBIS.Bv.Test.Service-1.0.0.0.jar");

		// Copiar plug-in de resources al cobishome
		//copy configuration Files:
		//FileUtils.copyFile(new File(System.getProperty("user.dir")+PATHPLUGINS+"COBISCorp.eCOBIS.Bv.Test.DTO-1.0.0.0.jar"),
		//		new File(CTSEnvironment.COBIS_HOME_DIR + PATHCTSPLUGINS+"COBISCorp.eCOBIS.Bv.Test.DTO-1.0.0.0.jar"))
		//copy java orquestation sample
		//FileUtils.copyFile(new File(System.getProperty("user.dir")+PATHPLUGINS+"COBISCorp.eCOBIS.Bv.Test.Service-1.0.0.0.jar"),
		//		new File(CTSEnvironment.COBIS_HOME_DIR + PATHCTSPLUGINS+"COBISCorp.eCOBIS.Bv.Test.Service-1.0.0.0.jar"))

		//creo el usuario


		SqlExecutorUtils.executeSqlFile("Virtual_Banking\\create_login.sql", CTSEnvironment.TARGETID_LOCAL);

		//Autorizo la transacción.
		SqlExecutorUtils.executeSqlFile("Virtual_Banking\\autoriza_transaccion.sql", CTSEnvironment.TARGETID_LOCAL);

		// Creo el SP
		SqlExecutorUtils.executeSqlFile("Virtual_Banking\\sp_test_bv.sql",CTSEnvironment.TARGETID_LOCAL);

		//Registro el servicio
		//RegistertServiceUtill.registerservice(CTSEnvironment.getSybaseDataBaseInformation(), "Bv.Test.TestBv.TestOne", "cobiscorp.ecobis.bv.test.service.ITestBv", "testOne", "", 0)

		//recargo configuraciones
		//reloadConfiguration();

		def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)

		String wUser= CTSEnvironment.bvLogin

		sql.execute("delete from cob_bvirtual"+CTSEnvironment.DB_SEPARATOR+"bv_in_login where il_login=?",[wUser])
		sql.execute("delete from cob_bvirtual"+CTSEnvironment.DB_SEPARATOR+"bv_session where bv_usuario=?",[wUser])

		BDD.closeInstance(CTSEnvironment.TARGETID_LOCAL, sql);

	}

	@After
	void clean(){
		// borro  el usuario
		SqlExecutorUtils.executeSqlFile("Virtual_Banking\\eliminar_Login.sql", CTSEnvironment.getSqlDataBaseInformation());

		//borro el SP
		SqlExecutorUtils.executeSqlFile("Virtual_Banking\\drop_sp_test_bv.sql",CTSEnvironment.getSqlDataBaseInformation());

		//Eliminar registro servicio cts_serv_catalgo
		//RegistertServiceUtill.unRegisterservice(CTSEnvironment.getSybaseDataBaseInformation(), "Bv.Test.TestBv.TestOne");

		// borro el nuevo Plan
		//XMLUtil.removePlan(CTSEnvironment.COBIS_HOME_DIR, "TestBV");
	}

	/*static main(args) {
	 String initSession = new VirtualBankingUtil()
	 .initSession(
	 "testCTS",
	 "9D60DFFDC13AD84CF4EF48353CF6657F5D4BB27CAC4E6E276DC5C5F6474424DB",
	 "ES_CR");
	 System.out.println(initSession);
	 // Preparo ejecución del servicio
	 ServiceRequestTO serviceRequestTO = new ServiceRequestTO();
	 serviceRequestTO.setSessionId(initSession);
	 serviceRequestTO.setServiceId("Bv.Test.TestBv.TestOne");
	 DtoIn inDto = new DtoIn();
	 inDto.setVar1(10);
	 inDto.setVar2("TestCTS");
	 serviceRequestTO.addValue("inDtoIn", inDto);
	 ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO);
	 new VirtualBankingUtil()
	 .finalizeSession(
	 "test",
	 initSession);
	 }*/

	@Test
	void testBV(){
		String initSession = new VirtualBankingUtil()
				.initSession(
				CTSEnvironment.bvLogin,
				CTSEnvironment.bvPassword,
				CTSEnvironment.bvCulture);
		try{
			System.out.println(initSession);
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO();
			serviceRequestTO.setSessionId(initSession);
			serviceRequestTO.setServiceId("InternetBanking.WebApp.Admin.Swift.GetBanksByCountry");

			//DTO IN
			Country wCountry = new Country();
			wCountry.code=4;
			SearchOption wSearch = new SearchOption();
			wSearch.mode=0;

			//DTO OUT
			BankCollection oBankCollection=new BankCollection();

			serviceRequestTO.addValue("inCountry", wCountry);
			serviceRequestTO.addValue("inSearchOption", wSearch);

			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO);

			//Valido si fue exitoso la ejecucion
			Assert.assertTrue("Ejecucion del Servicion Fallido", serviceResponseTO.success);

			//Valido que Numero de elementos sea mayor a una fila
			oBankCollection.banks = serviceResponseTO.getData().get("returnBank");
			Assert.assertTrue("Filas Vacias", oBankCollection.banks.collect().size()>0);

		}finally{
			new VirtualBankingUtil()
					.finalizeSession(CTSEnvironment.bvLogin,initSession);
		}
	}

	void reloadConfiguration() {
		String id = MQClient.publishMessage(CTSEnvironment.QMANAGER, TOPIC_NAME,
				xmlReloadConfiguration)
		println(id)
		String strMessage = MQClient.getMessageByCorrelationId(CTSEnvironment.QMANAGER, RESPONSE_QUEUE, id, 5000)
		println	"Message response: ["+strMessage+"]"
		//check if the result is not null
		Assert.assertNotNull(strMessage)
		//Check if the string is not empty
		Assert.assertNotSame(strMessage.isEmpty(), true)
		//Check the xml if success was true
		def root = new XmlParser().parseText(strMessage)
		def node = root."**".field.find { it.@name == "success" }
		//check if the field success was found
		//Assert.assertNotNull(node)
		//check if the execution was successful
		//Assert.assertEquals(node.@value, "true")

	}

	def xmlReloadConfiguration = '''
	<class id="1"
type="com.cobiscorp.cobis.cts.console.command.shared.impl.MessageCommandAction">
  <class id="2"
  type="com.cobiscorp.cobis.cts.console.command.shared.impl.BatchCommandAction"
  name="action">
    <collection type="List" length="1" id="3" name="actions">
      <class id="4"
      type="com.cobiscorp.cobis.cts.console.command.shared.impl.ReloadPlanCommandAction"
      index="0">
        <field type="String"
        value="${COBIS_HOME}/CTS_MF/infrastructure/cts-ccm-plan-config.xml"
        name="planLocation" />
        <field type="String" value="TestBV" name="plan" />
      </class>
    </collection>
  </class>
  <field type="String[]" length="1" id="5" name="nodes">
    <field type="String" value="cts1" index="0" />
  </field>
  <field type="String"
  value="${COBIS_HOME}/CTS_MF/services-as/console/cts-console-client-config.xml"
  name="consoleClientLocation" />
</class>
	''';
}
