package com.cobiscorp.test;


/**
 * Keeps CTS configuration to database, MQ, web port
 * 
 * @author fabad
 * 
 */
public abstract class CTSEnvironment extends com.cobiscorp.test.utils.CTSEnvironment {

	public static String TCPH_HOST = getEnvProp().getProperty("tcph.host");
	public static String TCPH_HOST_ISO = getEnvProp().getProperty("tcph.host.iso");
	public static Integer TCPH_PORT_ISO = Integer.parseInt(getEnvProp().getProperty("tcph.port.iso"));
	public static Integer TCPH_PORT_X11 = Integer.parseInt(getEnvProp().getProperty("tcph.port.x11"));

	public static String bvLogin = getEnvProp().getProperty("bv.login", "testCts");
	public static String bvLoginEmpresa = getEnvProp().getProperty("bv.login.empresa", "testCtsEmp");
	public static String bvLoginType = getEnvProp().getProperty("bv.login.type", "P");
	public static String bvLoginNumDoc = getEnvProp().getProperty("bv.login.numdoc", "0");
	public static String bvPassword = getEnvProp().getProperty("bv.password", "FC0CFD1CC67BFB20552868E2F474A09213669D15E2BE6083E504A969B69CC94B");
	public static String bvCulture = getEnvProp().getProperty("bv.culture", "ES_EC");
	public static String bvTerminalIp = getEnvProp().getProperty("bv.terminal.ip");
	public static String bvServer = getEnvProp().getProperty("bv.server", "CTSSRV");
	public static String bvWebServer = getEnvProp().getProperty("bv.webserver");
	public static Integer bvEnte = Integer.parseInt(getEnvProp().getProperty("bv.ente", "0"));
	public static Integer bvEnteMis = Integer.parseInt(getEnvProp().getProperty("bv.ente.mis", "13036"));
	public static Integer bvService = Integer.parseInt(getEnvProp().getProperty("bv.service", "1"));

	public static String bvDestinationAccCtaCteNumber = getEnvProp().getProperty("bv.des.acc.ctacte.number", "10410108595405215");
	public static Integer bvDestinationAccCtaCteType = Integer.parseInt(getEnvProp().getProperty("bv.des.acc.ctacte.type", "3"));
	public static Integer bvDestinationAccCtaCteCurrencyId = Integer.parseInt(getEnvProp().getProperty("bv.des.acc.ctacte.currencyid", "0"));

	public static Integer bvCompanyEnte = Integer.parseInt(getEnvProp().getProperty("bv.company.ente", "279"));
	public static Integer bvCompanyEnteMis = Integer.parseInt(getEnvProp().getProperty("bv.company.ente.mis", "137488"));
	public static String bvCompanyLogin = getEnvProp().getProperty("bv.company.login", "testCtsEmp");
	public static String bvCompanyLoginType = getEnvProp().getProperty("bv.company.login.type", "C");
	public static String bvCompanyPassword = getEnvProp().getProperty("bv.company.password", "");

	public static Integer bvGroupEnte = Integer.parseInt(getEnvProp().getProperty("bv.group.ente", "295"));
	public static String bvGroupLogin = getEnvProp().getProperty("bv.group.login", "testCtsGrupo");
	public static String bvGroupPassword = getEnvProp().getProperty("bv.group.password", "9D78034860AFD9982FC022DBAC382A51C0D406E48DB154C3177894BA89641173");
	public static Integer bvGroupEnteMis = Integer.parseInt(getEnvProp().getProperty("bv.group.ente.mis", "137488"));;
	public static String bvGroupLoginType = getEnvProp().getProperty("bv.group.login.type", "G");

	public static String bvGroupAccCtaCteNumber = getEnvProp().getProperty("bv.group.acc.ctacte.number", "10410108275407019");
	public static Integer bvGroupAccCtaCteType = Integer.parseInt(getEnvProp().getProperty("bv.group.acc.ctacte.type", "3"));
	public static Integer bvGroupAccCtaCteCurrencyId = Integer.parseInt(getEnvProp().getProperty("bv.group.acc.ctacte.currencyid", "0"));

	public static String bvCompanyAccCtaCteNumber = getEnvProp().getProperty("bv.group.acc.ctacte.number", "10410108275407019");
	public static Integer bvCompanyAccCtaCteType = Integer.parseInt(getEnvProp().getProperty("bv.group.acc.ctacte.type", "3"));
	public static Integer bvCompanyAccCtaCteCurrencyId = Integer.parseInt(getEnvProp().getProperty("bv.group.acc.ctacte.currencyid", "0"));

	public static String bvGroupAccCtaAhoNumber = getEnvProp().getProperty("bv.group.acc.ctaaho.number", "10410000005233616");
	public static Integer bvGroupAccCtaAhoType = Integer.parseInt(getEnvProp().getProperty("bv.group.acc.ctaaho.type", "4"));
	public static Integer bvGroupAccCtaAhoCurrencyId = Integer.parseInt(getEnvProp().getProperty("bv.group.acc.ctaaho.currencyid", "0"));
	public static String bvAbbreviationSavingAccount = getEnvProp().getProperty("bv.abbreviation.savingacc", "AHO");

	
	public static String bvAccGroupTarNumber = getEnvProp().getProperty("bv.grupo.acc.tar.number", "0");
	public static String bvAccGroupTarType = getEnvProp().getProperty("bv.grupo.acc.tar.type", "83");
	
	
	public static String bvCompanyAccCtaAhoNumber = getEnvProp().getProperty("bv.company.acc.ctaaho.number","10410000005233616");
	public static Integer bvCompanyAccCtaAhoType = Integer.parseInt(getEnvProp().getProperty("bv.company.acc.ctaaho.type", "4"));
	public static Integer bvCompanyAccCtaAhoCurrencyId = Integer.parseInt(getEnvProp().getProperty("bv.company.acc.ctaaho.currencyid", "0"));

	public static String bvCompanyAccCtaAhoUsdNumber = getEnvProp().getProperty("bv.acc.ctacte2.usd.number", "10410108275402927");
	public static Integer bvCompanyAccCtaAhoUsdType = Integer.parseInt(getEnvProp().getProperty("bv.acc.ctacte2.usd.type", "3"));
	public static Integer bvCompanyAccCtaAhoUsdCurrencyId = Integer.parseInt(getEnvProp().getProperty("bv.acc.ctacte2.usd.currencyid", "17"));
	
	public static String bvAccCompanyTarNumber = getEnvProp().getProperty("bv.company.acc.tar.number", "0");
	public static String bvAccCompanyTarType = getEnvProp().getProperty("bv.company.acc.tar.type", "83");

	public static Integer bvCompanyEnteA = Integer.parseInt(getEnvProp().getProperty("bv.company.ente.A", "21"));
	public static Integer bvCompanyEnteMisA = Integer.parseInt(getEnvProp().getProperty("bv.company.ente.mis.A", "137488"));
	public static String bvCompanyLoginA = getEnvProp().getProperty("bv.company.login.A", "testCtsEmpA");
	public static String bvCompanyLoginTypeA = getEnvProp().getProperty("bv.company.login.type.A", "A");
	public static String bvCompanyPasswordA = getEnvProp().getProperty("bv.company.password.A", "");

	public static String bvAccCtaCteNumber = getEnvProp().getProperty("bv.acc.ctacte.number", "10410108275406111");
	public static Integer bvAccCtaCteType = Integer.parseInt(getEnvProp().getProperty("bv.acc.ctacte.type", "3"));
	public static Integer bvAccCtaCteCurrencyId = Integer.parseInt(getEnvProp().getProperty("bv.acc.ctacte.currencyid", "0"));

	public static String bvAccCtaCteUsdNumber = getEnvProp().getProperty("bv.acc.ctacte.usd.number", "10410108640405011");
	public static Integer bvAccCtaCteUsdType = Integer.parseInt(getEnvProp().getProperty("bv.acc.ctacte.usd.type", "3"));
	public static Integer bvAccCtaCteUsdCurrencyId = Integer.parseInt(getEnvProp().getProperty("bv.acc.ctacte.usd.currencyid", "17"));

	public static String bvAccCtaAhoNumber = getEnvProp().getProperty("bv.acc.ctaaho.number", "10410108275249013");
	public static Integer bvAccCtaAhoType = Integer.parseInt(getEnvProp().getProperty("bv.acc.ctaaho.type", "4"));
	public static Integer bvAccCtaAhoCurrencyId = Integer.parseInt(getEnvProp().getProperty("bv.acc.ctaaho.currencyid", "0"));

	public static String bvAccDpfNumber = getEnvProp().getProperty("bv.acc.dpf.number", "01414052458");
	public static Integer bvAccDpfType = Integer.parseInt(getEnvProp().getProperty("bv.acc.dpf.type", "14"));
	public static Integer bvAccDpfCurrencyId = Integer.parseInt(getEnvProp().getProperty("bv.acc.dpf.currencyid", "0"));
	public static Integer bvAccDpfSequence = Integer.parseInt(getEnvProp().getProperty("bv.acc.dpf.sequence", "-1"));

	public static String bvAccCarNumber = getEnvProp().getProperty("bv.acc.car.number", "0");
	public static Integer bvAccCarType = Integer.parseInt(getEnvProp().getProperty("bv.acc.car.type", "7"));
	public static Integer bvAccCarCurrencyId = Integer.parseInt(getEnvProp().getProperty("bv.acc.car.currencyid", "0"));

	public static String bvAccCexNumber = getEnvProp().getProperty("bv.acc.cex.number", "0");
	public static Integer bvAccCexType = Integer.parseInt(getEnvProp().getProperty("bv.acc.cex.type", "9"));
	public static Integer bvAccCexCurrencyId = Integer.parseInt(getEnvProp().getProperty("bv.acc.cex.currencyid", "0"));

	public static String bvAccTarNumber = getEnvProp().getProperty("bv.acc.tar.number", "0");
	public static String bvAccTarType = getEnvProp().getProperty("bv.acc.tar.type", "83");
	
	

	public static String bvProcessDate = getEnvProp().getProperty("bv.process.date", "10/01/2013");

	public static String ATM_CORE_TYPE = getEnvProp().getProperty("atm.coretype", "COBIS");

	public static String Prueba = getEnvProp().getProperty("pruebapropiedad", "123");
	public static String bvInitialDate = getEnvProp().getProperty("bv.initialdate", "01/01/2013");
	public static String bvFinalDate = getEnvProp().getProperty("bv.finaldate", "01/01/2014");
	public static Integer bvDateFormat = Integer.parseInt(getEnvProp().getProperty("bv.dateformat", "103"));

	public static String bvTransactionDate = getEnvProp().getProperty("bv.transaction.date", "10/25/2013");
	
	public static Integer bvPhone = Integer.parseInt(getEnvProp().getProperty("bv.phone", "2555555"));
	public static String bvEmail = getEnvProp().getProperty("bv.email", "test@gmail.com");
	public static String bvNeighborhood = getEnvProp().getProperty("bv.neighborhood", "Neighborhood Test");
	public static String bvStreet = getEnvProp().getProperty("bv.street", "Street Test");
	public static String bvHouse = getEnvProp().getProperty("bv.house", "House Test");
	

	public static Integer bvLoanType = Integer.parseInt(getEnvProp().getProperty("bv.loan.type", "7"));
	public static String bvLoanNumber = getEnvProp().getProperty("bv.loan.number", "10407740700943818");
	public static String bvCompanyLoanNumber = getEnvProp().getProperty("bv.company.loan.number", "10410000041700201");
	public static String bvGroupLoanNumber = getEnvProp().getProperty("bv.group.loan.number", "10410000041700201");
	public static Integer bvLoanCurrencyId = Integer.parseInt(getEnvProp().getProperty("bv.loan.currency.id", "0"));
	
	public static String bvCompanyDPFNumber = getEnvProp().getProperty("bv.company.acc.dpf.number","28414120590");
	public static Integer bvCompanyDpfCurrencyId = Integer.parseInt(getEnvProp().getProperty("bv.company.acc.dpf.currencyid","17"));
	public static String bvGroupDpfNumber = getEnvProp().getProperty("bv.group.acc.dpf.number","28414118610");
	public static Integer bvGroupDpfCurrencyId = Integer.parseInt(getEnvProp().getProperty("bv.grupo.acc.dpf.currencyid","17"));
	
	public static String bvAccDpfNumberHist = getEnvProp().getProperty("bv.acc.dpf.numberHist","01414052458");
	public static Integer bvAccDpfSequentialHist = Integer.parseInt(getEnvProp().getProperty("bv.acc.dpf.sequentialHist","0"));
	
	public static String bvAccDpfPayableProdNumber = getEnvProp().getProperty("bv.acc.dpf.payable.prodNumber","01414052458");
	public static Integer bvAccDpfPayableProductid = Integer.parseInt(getEnvProp().getProperty("bv.acc.dpf.payable.productid","14"));
	public static Integer bvAccDpfPayableCurrencyid = Integer.parseInt(getEnvProp().getProperty("bv.acc.dpf.payable.currencyid","0"));
	public static Integer bvAccDpfPayableSequence = Integer.parseInt(getEnvProp().getProperty("bv.acc.dpf.payable.sequence","0"));	
	public static Integer bvCompanyAccDpfPayableCurrencyid = Integer.parseInt(getEnvProp().getProperty("bv.company.acc.dpf.payable.currencyid","17"));
	public static Integer bvCompanyAccDpfPayableSequence = Integer.parseInt(getEnvProp().getProperty("bv.company.acc.dpf.payable.sequence","0"));	
	public static Integer bvGroupAccDpfPayableCurrencyid = Integer.parseInt(getEnvProp().getProperty("bv.group.acc.dpf.payable.currencyid","0"));
	public static Integer bvGroupAccDpfPayableSequence = Integer.parseInt(getEnvProp().getProperty("bv.group.acc.dpf.payable.sequence","0"));

	public static String bvAccCtaAhoOfficeId = getEnvProp().getProperty("bv.acc.ctaaho.officeId","1");
	public static String bvAccCtaAhoBeneficiaryType = getEnvProp().getProperty("bv.acc.ctaaho.beneficiaryType","1.1");
	
	public static String bvGroupAccCtaAhoOfficeId = getEnvProp().getProperty("bv.group.acc.ctaaho.officeId","1");
	public static String bvGroupAccCtaAhoBeneficiaryType = getEnvProp().getProperty("bv.group.acc.ctaaho.beneficiaryType","1.1");
	
	public static String bvAccDpfNumberPaysched = getEnvProp().getProperty("bv.acc.dpf.number.paysched","01414052458");
	public static Integer bvGrupoAccDpfCurrencyidPaysched = Integer.parseInt(getEnvProp().getProperty("bv.grupo.acc.dpf.currencyid.paysched", "17"));
	public static Integer bvAccDpfQuota = Integer.parseInt(getEnvProp().getProperty("bv.acc.dpf.quota","0"));
	
	public static String bvCompanyAccDpfNumberPaysched = getEnvProp().getProperty("bv.company.acc.dpf.number.paysched","28414120590");
	public static String bvGroupAccDpfNumberPaysched = getEnvProp().getProperty("bv.group.acc.dpf.number.paysched","28414118610");	

	public static String bvCompanyAccDpfNumberHist = getEnvProp().getProperty("bv.company.acc.dpf.numberHist","28414120590");
	public static Integer bvCompanyAccDpfCurrencyidHist = Integer.parseInt(getEnvProp().getProperty("bv.company.acc.dpf.currencyidHist","17"));
	public static Integer bvCompanyAccDpfProductidHist =  Integer.parseInt(getEnvProp().getProperty("bv.company.acc.dpf.productidHist","14"));
	public static String bvGroupAccDpfNumberHist = getEnvProp().getProperty("bv.group.acc.dpf.numberHist","28414118610");
	public static Integer bvGroupAccDpfCurrencyidHist = Integer.parseInt(getEnvProp().getProperty("bv.group.acc.dpf.currencyidHist","17"));
	public static Integer bvGroupAccDpfProductidHist =  Integer.parseInt(getEnvProp().getProperty("bv.group.acc.dpf.productidHist","14"));
	
	public static Integer bvCheNextDayOfficeId = Integer.parseInt(getEnvProp().getProperty("bv.che.next.officeId","1"));
	

	public static String bvThirdTransfersNumber = getEnvProp().getProperty("bv.third.transfers.number", "10410108463406816");
	public static String bvCompanyThirdTransfersNumber = getEnvProp().getProperty("bv.company.third.transfers.number", "10410108463406816");
	public static String bvGroupThirdTransfersNumber = getEnvProp().getProperty("bv.group.third.transfers.number", "10410108463406816");
}
