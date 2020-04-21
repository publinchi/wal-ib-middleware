package com.cobiscorp.channels.atm.test

import static org.junit.Assert.assertEquals

import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.concurrent.atomic.AtomicLong

import org.junit.AfterClass
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test

import com.cobiscorp.channels.atm.util.Provider
import com.cobiscorp.cis.connector.model.BuiltTransaction
import com.cobiscorp.cis.iso8583.impl.ConnectorISO8583Impl
import com.cobiscorp.cis.iso8583.impl.MarshallerUtils
import com.cobiscorp.cobis.commons.domains.log.ILogger
import com.cobiscorp.cobis.commons.log.LogFactory
import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.utils.BDD
import com.cobiscorp.test.utils.SqlExecutorUtils
/**
 * Test ISO8583 ATM Transactions
 * @author cinapanta, fabad
 */
class ATMTestSimple {
	//4 SAVINGS, 3 CHECKING
	static Properties testAtmProperties;
	static AtomicLong p11SystemsTraceAuditNumber
	static AtomicLong p37RetrievalReferenceNumber
	static String pathProperties = System.getProperty("user.dir")+"/src/test/java/com/cobiscorp/channels/atm/test/test-atm.properties"
	static ConnectorISO8583Impl connector
	static Provider simulator 
	static ILogger logger 
	static SimpleDateFormat expirationDateFormat = new SimpleDateFormat("yyMM"); 
	
	public static void loadProperties() {
		InputStream is = null;
		testAtmProperties = new Properties();
		
		File wFile = new File(pathProperties);
		System.out.println("trying to read properties from:" + wFile.getAbsolutePath());
		try{
			testAtmProperties.load(new FileInputStream(wFile));
		}catch (IOException e){
			e.printStackTrace()
		}
	}

	@AfterClass 
	static void tearDown(){
		saveParamChanges()
	}
	@BeforeClass
	static void setUpClass() {
		SetUpTestEnvironment.setUp()
		loadProperties()
		p11SystemsTraceAuditNumber = new AtomicLong(Long.parseLong(testAtmProperties.getProperty("p11SystemsTraceAuditNumber")))
		p37RetrievalReferenceNumber = new AtomicLong(Long.parseLong(testAtmProperties.getProperty("p37RetrievalReferenceNumber")))
		println "value p11SystemsTraceAuditNumber:" + p11SystemsTraceAuditNumber.incrementAndGet()
		println "value p37RetrievalReferenceNumber:" + p37RetrievalReferenceNumber.incrementAndGet()
		
		def wUser1='usertest1' 
		SqlExecutorUtils.dropCOBISUser(CTSEnvironment.TARGETID_CENTRAL, wUser1)
		SqlExecutorUtils.deleteRelationship(CTSEnvironment.TARGETID_CENTRAL, 10, 10, wUser1  + "ext")
		SqlExecutorUtils.createCOBISUser(CTSEnvironment.TARGETID_CENTRAL, wUser1, wUser1)
		SqlExecutorUtils.createRelationship(
			CTSEnvironment.TARGETID_CENTRAL,  10, 'aplication iso', "term1", 10,
			wUser1, 1, 1 , 'FX11TRED', wUser1  + "ext")

		def wUser2='usertest2'
		SqlExecutorUtils.dropCOBISUser(CTSEnvironment.TARGETID_CENTRAL, wUser2)
		SqlExecutorUtils.deleteRelationship(CTSEnvironment.TARGETID_CENTRAL, 11, 11, wUser2  + "ext")
		SqlExecutorUtils.createCOBISUser(CTSEnvironment.TARGETID_CENTRAL, wUser2, wUser2)
		SqlExecutorUtils.createRelationship(
			CTSEnvironment.TARGETID_CENTRAL,  11, 'aplication iso', "term1", 11,
			wUser2, 1, 1 , 'ISOB24', wUser2  + "ext")


		SqlExecutorUtils.dropRolFromUser(CTSEnvironment.TARGETID_CENTRAL, 'usertest1',1)
		SqlExecutorUtils.addCOBISUserToRol(CTSEnvironment.TARGETID_CENTRAL, 'usertest1',1 , 1)

				SqlExecutorUtils.dropRolFromUser(CTSEnvironment.TARGETID_CENTRAL, 'usertest2',1)
		SqlExecutorUtils.addCOBISUserToRol(CTSEnvironment.TARGETID_CENTRAL, 'usertest2',1 , 1)
		
		//reads configuration from connector configuration
		connector = (ConnectorISO8583Impl) MarshallerUtils.unmarshal(
				ConnectorISO8583Impl.class.getName(), "configurations/ISO8583-B24-Connector.xml");
		
		// Instancia el simulador
		simulator = new Provider(CTSEnvironment.TCPH_HOST_ISO, 
			CTSEnvironment.TCPH_PORT_ISO, connector, 
			"P37-retrievalReferenceNumber", 5)
		
		logger = LogFactory.getLogger(ATMTestSimple.class);
	}

	@Ignore
	public void testGetBalanceX11() {
		
		
		// Respuesta esperada
		def expected = "TC01201234500808318920560+8340/#00000000110100022100000000000+00000000000+0000000000000+0000120530"
		
		// Instancia el simulador
		Provider simulator = new Provider(CTSEnvironment.TCPH_HOST, CTSEnvironment.TCPH_PORT_X11)
		
		// Envia la trama de solicitud autorizante al TCPHandler
		simulator.send("TR01201234500808318920560+A80012053008423900000084080000111018                                CAPITAL BANK CALLE 50                             0000000000110100022100000000000+0120000000000580812053003000001;5041960000004122=2304010171942871000?  0100001401")
		
		Thread.sleep(3000)
		
		// Obtiene la trama de respuesta
		def response = simulator.getInFrame(null);
		println response
		
		// Compara resultados
		Assert.assertEquals(expected, response);		
//		assert response == expected
		
	}
	@Test 
	void testGetBalanceB24(){
		//tipo  de operacion. consulta, retiro,
		//tipo de cajero: propio, local, arrendado
		//origen. nacional o internacional
		//tarjeta
		//monto
		//terminal
		//bitmap varia por operacion
		
		BuiltTransaction builtTransaction1 = performGetBalanceB24(ATMISOFinancialProduct.SAVINGS, ATMISOFinancialTransaction.GET_BALANCE, 
			"5041960000000146", ATMISOFinancialTerminal.ATM, ATMISOFinancialAcceptorTerminalID.LOCAL);
		
		validateSuccesResponse(builtTransaction1)
		
		BuiltTransaction builtTransaction2 = performGetBalanceB24(ATMISOFinancialProduct.CHECKING, ATMISOFinancialTransaction.GET_BALANCE, 
			"5041960000000021", ATMISOFinancialTerminal.ATM, ATMISOFinancialAcceptorTerminalID.LOCAL);
		validateSuccesResponse(builtTransaction2)
		
		BuiltTransaction builtTransaction3 = performGetBalanceB24(ATMISOFinancialProduct.SAVINGS, ATMISOFinancialTransaction.GET_BALANCE,
			"5041960000000468", ATMISOFinancialTerminal.ATM, ATMISOFinancialAcceptorTerminalID.LOCAL);
		validateSuccesResponse(builtTransaction3)
		
		BuiltTransaction builtTransaction4 = performGetBalanceB24(ATMISOFinancialProduct.CHECKING, ATMISOFinancialTransaction.GET_BALANCE,
			"5041960000000468", ATMISOFinancialTerminal.ATM, ATMISOFinancialAcceptorTerminalID.LOCAL);
		validateErrorResponse(builtTransaction4)

		BuiltTransaction builtTransaction5 = performGetBalanceB24(ATMISOFinancialProduct.SAVINGS, ATMISOFinancialTransaction.GET_BALANCE,
			"5041960000000476", ATMISOFinancialTerminal.ATM, ATMISOFinancialAcceptorTerminalID.LOCAL);
		validateSuccesResponse(builtTransaction3)

		/*BuiltTransaction builtTransaction6 = performGetBalanceB24(ATMISOFinancialProduct.SAVINGS, ATMISOFinancialTransaction.GET_BALANCE,
			"5041960000000484", ATMISOFinancialTerminal.ATM);
		validateSuccesResponse(builtTransaction6)*/

		
		
		
		
		
	}
	
	/**
	 * 
	 * @param aProduct
	 * @param aTransaction
	 * @param aCard Card Number tm_tarjeta.ta_codigo
	 */
	private BuiltTransaction performGetBalanceB24(ATMISOFinancialProduct aProduct, ATMISOFinancialTransaction aTransaction, 
		String aCard, ATMISOFinancialTerminal aTerminal, ATMISOFinancialAcceptorTerminalID  aAcceptorTerminalID) {
		

		BigDecimal wCurrentBalance;
		
		if(CTSEnvironment.ATM_CORE_TYPE.equals("COBIS")){
			wCurrentBalance = getBalance(aCard, aProduct);
			
		}

		
		BuiltTransaction builtTransaction = performTransactionB24(aProduct, ATMISOFinancialProduct.DEFAULT, aTransaction, aCard, 
			new BigDecimal("0"), aTerminal, aAcceptorTerminalID)
		

		
		//println "saldo:" + builtTransaction.fieldValues.get("P44-additionalResponseData").value.substring(1,13)
		
		//BigDecimal wBalanceFromTransaction = new BigDecimal(builtTransaction.fieldValues.get("P44-additionalResponseData").value.substring(1,13));
		//wBalanceFromTransaction = wBalanceFromTransaction.divide(100d);
		
		//Assert.assertEquals("balance from transaction", wCurrentBalance, wBalanceFromTransaction)
		return builtTransaction
	}

	@Test
	public void testWithdrawalB24() {
		performWithdrawalB24(ATMISOFinancialProduct.SAVINGS, ATMISOFinancialTransaction.WITHDRAWAL,
			 "5041960000000146", new BigDecimal("680"), ATMISOFinancialTerminal.ATM, ATMISOFinancialAcceptorTerminalID.LOCAL);
		performWithdrawalB24(ATMISOFinancialProduct.CHECKING, ATMISOFinancialTransaction.WITHDRAWAL,
			"5041960000000021", new BigDecimal("677"), ATMISOFinancialTerminal.ATM, ATMISOFinancialAcceptorTerminalID.LOCAL);

		performWithdrawalB24(ATMISOFinancialProduct.SAVINGS, ATMISOFinancialTransaction.WITHDRAWAL,
			"5041960000000476", new BigDecimal("680"), ATMISOFinancialTerminal.ATM, ATMISOFinancialAcceptorTerminalID.LOCAL);

		performWithdrawalB24(ATMISOFinancialProduct.CHECKING, ATMISOFinancialTransaction.WITHDRAWAL,
			"5041960000000476", new BigDecimal("677"), ATMISOFinancialTerminal.ATM, ATMISOFinancialAcceptorTerminalID.LOCAL);

		

	}
	/**
	 * Performs withdrawal test, it verifies balance parameters 
	 * @param aProduct
	 * @param aTransaction
	 * @param aCard
	 * @param aAmount
	 */
	private void performWithdrawalB24(ATMISOFinancialProduct aProduct, 
		ATMISOFinancialTransaction aTransaction, String aCard, 
		BigDecimal aAmount, ATMISOFinancialTerminal aTerminal,
		ATMISOFinancialAcceptorTerminalID  aAcceptorTerminalID) {

	
		BigDecimal wBalanceBefore, wBalanceAfter;
		
		
		if(CTSEnvironment.ATM_CORE_TYPE.equals("COBIS")){
			wBalanceBefore = getBalance(aCard, aProduct);
		}
	
		BuiltTransaction builtTransaction =  performTransactionB24(aProduct, ATMISOFinancialProduct.DEFAULT, aTransaction, aCard, aAmount, 
			aTerminal, aAcceptorTerminalID)

		validateSuccesResponse(builtTransaction)
		
		//if cobis verify account balance
		if(CTSEnvironment.ATM_CORE_TYPE.equals("COBIS")){
			wBalanceAfter = getBalance(aCard, aProduct);
			//Assert.assertEquals ("new Balance",(wBalanceBefore.subtract(aAmount)),wBalanceAfter)
			Assert.assertTrue ("new Balance", wBalanceBefore > wBalanceAfter)
			//verifyLogTransaction (aCard, aProduct, aAmount)
		}
		
		
	}
	@Test
	public void testTransferB24() {
		/*performTransferB24(ATMISOFinancialProduct.SAVINGS, ATMISOFinancialProduct.CHECKING, ATMFinancialTransaction.TRANSFER,
			 "5041960000000146", new BigDecimal("680"));*/

		 performTransferB24(ATMISOFinancialProduct.SAVINGS, ATMISOFinancialProduct.CHECKING, ATMISOFinancialTransaction.TRANSFER,
			 "5041960000000021", new BigDecimal("690"), ATMISOFinancialTerminal.ATM, ATMISOFinancialAcceptorTerminalID.LOCAL);
		 performTransferB24(ATMISOFinancialProduct.SAVINGS, ATMISOFinancialProduct.CHECKING, ATMISOFinancialTransaction.TRANSFER,
			 "5041960000000476", new BigDecimal("679"), ATMISOFinancialTerminal.ATM, ATMISOFinancialAcceptorTerminalID.LOCAL);

		 
		 
	}
	/**
	 * @param aProduct
	 * @param aToProduct targetProduct of the transfer
	 * @param aTransaction
	 * @param aCard
	 * @param aAmount to be transfered
	 */
	private void performTransferB24(ATMISOFinancialProduct aProduct, ATMISOFinancialProduct aToProduct,
		ATMISOFinancialTransaction aTransaction, String aCard, BigDecimal aAmount, 
		ATMISOFinancialTerminal aTerminal, ATMISOFinancialAcceptorTerminalID  aAcceptorTerminalID) {
		
		BigDecimal wBalanceBefore, wBalanceAfter;
		
		
		if(CTSEnvironment.ATM_CORE_TYPE.equals("COBIS")){
			wBalanceBefore = getBalance(aCard, aProduct);
		}
		
		BuiltTransaction builtTransaction = performTransactionB24(aProduct, aToProduct, aTransaction, aCard, aAmount, 
			aTerminal, aAcceptorTerminalID)
		
		validateSuccesResponse(builtTransaction)
		
		//if cobis verify account balance
		if(CTSEnvironment.ATM_CORE_TYPE.equals("COBIS")){
			wBalanceAfter = getBalance(aCard, aProduct);
			//Assert.assertEquals ("new Balance",(wBalanceBefore.subtract(aAmount)),wBalanceAfter)
			Assert.assertTrue ("new Balance", wBalanceBefore > wBalanceAfter)
			//verifyLogTransaction (aCard, aProduct, aAmount)
		}


	}
	/**
	 * Test purchase
	 */
	@Test
	public void testPurchaseB24() {
		/*performTransferB24(ATMISOFinancialProduct.SAVINGS, ATMISOFinancialProduct.CHECKING, ATMFinancialTransaction.TRANSFER,
			 "5041960000000146", new BigDecimal("680"));*/
	
		 performPurchaseB24(ATMISOFinancialProduct.CHECKING, ATMISOFinancialProduct.DEFAULT, ATMISOFinancialTransaction.PURCHASE,
			 "5041960000000021", new BigDecimal("675"), ATMISOFinancialTerminal.POS, ATMISOFinancialAcceptorTerminalID.LOCAL);

		 performPurchaseB24(ATMISOFinancialProduct.CHECKING, ATMISOFinancialProduct.DEFAULT, ATMISOFinancialTransaction.PURCHASE,
			 "5041960000000476", new BigDecimal("693"), ATMISOFinancialTerminal.POS, ATMISOFinancialAcceptorTerminalID.LOCAL);

		 
		 performPurchaseB24(ATMISOFinancialProduct.CHECKING, ATMISOFinancialProduct.DEFAULT, ATMISOFinancialTransaction.PURCHASE,
			 "5041960000000476", new BigDecimal("693"), ATMISOFinancialTerminal.POS, ATMISOFinancialAcceptorTerminalID.LOCAL);

		 performPurchaseB24(ATMISOFinancialProduct.CHECKING, ATMISOFinancialProduct.DEFAULT, ATMISOFinancialTransaction.PURCHASE,
			 "5041960000000021", new BigDecimal("693"), ATMISOFinancialTerminal.POS, ATMISOFinancialAcceptorTerminalID.INTERNATIONAL);

				 
	}
	/**
	 * Performs execution and validation for purchase
	 */
	private void performPurchaseB24(ATMISOFinancialProduct aProduct, ATMISOFinancialProduct aToProduct,
		ATMISOFinancialTransaction aTransaction, String aCard, BigDecimal aAmount, 
		ATMISOFinancialTerminal aTerminal, ATMISOFinancialAcceptorTerminalID  aAcceptorTerminalID) {

		//performs validation	
		BuiltTransaction builtTransaction  = performTransactionB24(aProduct, aToProduct, aTransaction, aCard, aAmount, 
			aTerminal, aAcceptorTerminalID)
		
		validateSuccesResponse(builtTransaction)
	
	}
	/**
	 * Method generic to send transactions ISO8583
	 * @param aProduct
	 * @param aToProduct
	 * @param aTransaction
	 * @param aCard
	 * @param aAmount
	 * @return
	 */
	private BuiltTransaction performTransactionB24(ATMISOFinancialProduct aProduct, 
		ATMISOFinancialProduct aToProduct,	ATMISOFinancialTransaction aTransaction, 
		String aCard, BigDecimal aAmount, ATMISOFinancialTerminal aTerminal, 
		ATMISOFinancialAcceptorTerminalID  aAcceptorTerminalID) {
	
		
			
		def wTime = getTransmitionDateAndTime()
		String wTerminal = aTerminal.code;
		String p3P = aTransaction.code + aProduct.code + aToProduct.code //"012000"//"012000 withdrawal checkings, 011000 withdrawal savings"
		//add 0 at the beginning until complete 12 characters and eliminate decimals
		String p4Transac = String.format("%012d", aAmount.multiply(100).longValueExact());
		
		String p6Cardhol="000002080000"//P6-cardholderBillingAmount
		
		String p7Trans = wTime[0] // "0331093408" //it must be different
		String p10Co = "00000000"
		String p11 = String.format("%06d", p11SystemsTraceAuditNumber.incrementAndGet()) //"202332" //it must increment
		String p12 = wTime[1]//"093408" //substring p7
		String p = "8408" //p32-acquiringInstitutionIdentificationCode
		String wExpirationDate = getExpirationDate(aCard);
		String p35Track2Data = "${aCard}D${wExpirationDate}1012463015500000" //37
		String p41CardAccept = aAcceptorTerminalID.code
	
		String p37Retrie = String.format("%012d", p37RetrievalReferenceNumber.incrementAndGet()) // //it must increment
	
		BuiltTransaction builtTransaction =  null;
		if(ATMISOFinancialTransaction.PURCHASE == aTransaction){

			builtTransaction = simulator.sendFrame(
				"ISO${wTerminal}50000500200B678840128E1A0100000000000000100${p3P}${p4Transac}${p6Cardhol}${p7Trans}${p10Co}${p11}${p12}0401000000004${p}37${                     p35Track2Data}${p37Retrie}${p41CardAccept}ATH COSTA RICA                       Banco de prueba CR044ABPT                    40000018818800000001188000012CR27TES2-120033ATH COSTA RICA               ATHC",
				p37Retrie)
			//   ISO02          50000500200B678840128E1A010000000000000010000200000000009000000000208000004041445570000000010252614455704040000000049999375041960000000021D1805101246301550000000000010261311023           ATH COSTA RICA PRUEBAS MC BANDA      SJ______________11044ABPT                    40000090118800000001188000012CR27TES2-120033ATH COSTA RICA               ATHC
			//   ISO02          50000500420B67884012EA0A018000000401600000000200000000009000000000208000004041445570000000004272514455704040000000049999375041960000000021D18051012463015500000000000102613xxxxxxxx11023           PRUEBAS MC BANDA      SJ______________11188000012CR27TES2-120013CR36TES20000P02000000001026130404144557#              #111880000003628xxxxxxxxxxxxxxxxxxxxxxxxxxxx28xxxxxxxxxxxxxxxxxxxxxxxxxxxx
			//   ISO0250000500200B678840128E1A010000000000000010000200000000008000000000208000004011659320000000010243316593204010000000048408375041960000000021D18051012463015500000000000102520CR&7100         ATH COSTA RICA PRUEBAS MC BANDA      SJ______________CR044ABPT                    40000018818800000001188000012CR27TES2-120033ATH COSTA RICA               ATHC
			//purchase 5041960000000021

		}else{
			builtTransaction = simulator.sendFrame(
				"ISO${wTerminal}50000500200B278840128E1B0180000000010000104${p3P}${p4Transac}${p7Trans}${p10Co}${p11}${p12}0401000000004${p}37${                     p35Track2Data}${p37Retrie}CR&7100         ATH COSTA RICA                       Banco de prueba CR044ABPT                    400000188188000000011880000B2FB8F4EE2020C9012CR27TES2-120013CR36TES20000P1118800000036033ATH COSTA RICA               ATHC042& 0000200042! PB00020     090706          ",
				p37Retrie)

		}
		/*
		Assert.assertNotNull("response", builtTransaction)
	
		Assert.assertNotNull("error code", builtTransaction.fieldValues.get("P39-responseCode"))
	
		Assert.assertEquals("00",builtTransaction.fieldValues.get("P39-responseCode").value)
		*/
			
		for ( e in builtTransaction.fieldValues ) {
			logger.logDebug("param name = ${e.key}, value = ${e.value}")
		}
		
		return builtTransaction
			
			
			
			
			
	}
	private static void validateSuccesResponse(BuiltTransaction aBuiltTransaction){
		Assert.assertNotNull("response", aBuiltTransaction)
		Assert.assertNotNull("error code", aBuiltTransaction.fieldValues.get("P39-responseCode"))
		Assert.assertEquals("00",aBuiltTransaction.fieldValues.get("P39-responseCode").value)
	}
	private static void validateErrorResponse(BuiltTransaction aBuiltTransaction){
		Assert.assertNotNull("response", aBuiltTransaction)
		Assert.assertNotNull("error code", aBuiltTransaction.fieldValues.get("P39-responseCode"))
		println "error code:" + aBuiltTransaction.fieldValues.get("P39-responseCode")
		Assert.assertNotSame("00",aBuiltTransaction.fieldValues.get("P39-responseCode").value)
	}

	
	/**
	 * search balance for COBIS Products
	 * @param aAccount
	 * @param aProduct
	 * @return
	 */
	private static BigDecimal getBalance(String aCard, ATMISOFinancialProduct aProduct ){
		//String wAccount = "10410108463405200"
		def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)
		
		def wBalanceStr = null
		if(aProduct == ATMISOFinancialProduct.CHECKING){
			wBalanceStr = sql.firstRow("select cc_disponible from cob_cuentas"+CTSEnvironment.DB_SEPARATOR+
				"cc_ctacte, cob_atm"+CTSEnvironment.DB_SEPARATOR+"tm_emision where em_cuenta = cc_cta_banco and em_codigo = ? ",
				[aCard])?.cc_disponible 
		}else if(aProduct == ATMISOFinancialProduct.SAVINGS){
			wBalanceStr = sql.firstRow("select ah_disponible from cob_ahorros"+CTSEnvironment.DB_SEPARATOR+
				"ah_cuenta, cob_atm"+CTSEnvironment.DB_SEPARATOR+"tm_emision where em_cuenta = ah_cta_banco and em_codigo = ? ",
				[aCard])?.ah_disponible

		}else {
			throw new RuntimeException("product not supported");
		}
		println "balance:" +  wBalanceStr
		BigDecimal wBalance =new BigDecimal(wBalanceStr)

		//MathContext wMC = new MathContext(2)
		//wBalance = wBalance.round(wMC)
		//wBalance = wBalance.setScale(2, RoundingMode.HALF_UP);
		//select cc_disponible from cob_cuentas..cc_ctacte where cc_cta_banco='10410108463405200'
		BDD.closeInstance(CTSEnvironment.TARGETID_LOCAL, sql);
		return wBalance;

	}

	/**
	 * search balance for COBIS Products
	 * @param aAccount
	 * @param aProduct
	 * @return
	 */
	private static void verifyLogTransaction(String aCard, ATMISOFinancialProduct aProduct, BigDecimal aAmount ){
		//String wAccount = "10410108463405200"
		def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)
		

		def wValueStr = sql.firstRow("select tm_valor from cob_remesas"+CTSEnvironment.DB_SEPARATOR+
				"re_tran_monet where tm_tarjeta_atm = ? ",
				[aCard])?.tm_valor
		println "Value:" +  wValueStr
		BigDecimal wValue = new BigDecimal(wValueStr)
		
		Assert.assertEquals("Value", aAmount, wValue)
		BDD.closeInstance(CTSEnvironment.TARGETID_LOCAL, sql);


	}

	/**
	 * gets time taking into account provider format
	 * @return
	 */
	private static String[] getTransmitionDateAndTime(){
		
		Date wCurrentDate = new Date()
		//wCurrentDate.minutes = 50
		SimpleDateFormat sdf1 = new SimpleDateFormat("MMddHHmmss");
		sdf1.setLenient(false)
		SimpleDateFormat sdf2 = new SimpleDateFormat("HHmmss");
		sdf2.setLenient(false)
		return [sdf1.format(wCurrentDate), sdf2.format(wCurrentDate)]
		
	}

	/**
	 * gets expiration date for the card
	 */
	private static String getExpirationDate(String aCard) {
		def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)
		def wExpirationDateStr = null;
		def wExpirationDate = sql.firstRow("select ta_fecha_expiracion from cob_atm..tm_tarjeta where ta_codigo = ? ",
			[aCard])?.ta_fecha_expiracion
		wExpirationDateStr = expirationDateFormat.format(wExpirationDate);
		println " expiration date:" + wExpirationDateStr
		BDD.closeInstance(CTSEnvironment.TARGETID_LOCAL, sql);
		return wExpirationDateStr;
		
	}

	@Test
	public void testDate() {
		def wH ="a"
		def wTime = getTransmitionDateAndTime() 
		println "d${  wH}"
		def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)
		
		def wBalanceStr = sql.firstRow("select ta_fecha_expiracion from cob_atm..tm_tarjeta where ta_codigo = ? ",
			["5041960000000146"])?.ta_fecha_expiracion
		println " fecha;:" + new SimpleDateFormat("yyMM").format(wBalanceStr);
		BDD.closeInstance(CTSEnvironment.TARGETID_LOCAL, sql);
		
	}	
	public static void saveParamChanges() {
		testAtmProperties.setProperty("p11SystemsTraceAuditNumber", Long.toString(p11SystemsTraceAuditNumber.get()))
		testAtmProperties.setProperty("p37RetrievalReferenceNumber", Long.toString(p37RetrievalReferenceNumber.get()))


		
		try {
			File f = new File(pathProperties);
			OutputStream out = new FileOutputStream( f );
			testAtmProperties.store(out, "This is an optional header comment string");
		}
		catch (Exception e ) {
			e.printStackTrace();
		}
	}
	

}