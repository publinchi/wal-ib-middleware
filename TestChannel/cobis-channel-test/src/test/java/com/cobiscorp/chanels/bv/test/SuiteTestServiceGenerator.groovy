package com.cobiscorp.chanels.bv.test

import junit.framework.Test
import junit.textui.TestRunner

import com.cobiscorp.channels.bv.sg.test.TestAccountService
import com.cobiscorp.channels.bv.sg.test.TestAdminService
import com.cobiscorp.channels.bv.sg.test.TestCustomerService
import com.cobiscorp.channels.bv.sg.test.TestEnquiriesService
import com.cobiscorp.channels.bv.sg.test.TestEnterpriseServices
import com.cobiscorp.channels.bv.sg.test.TestForeignExchangeService
import com.cobiscorp.channels.bv.sg.test.TestLoanService
import com.cobiscorp.channels.bv.sg.test.TestPaymentsService
import com.cobiscorp.channels.bv.sg.test.TestProductsService
import com.cobiscorp.channels.bv.sg.test.TestProgrammedSavingsService
import com.cobiscorp.channels.bv.sg.test.TestTransfersService
import com.cobiscorp.channels.bv.sg.test.TestUtilsService
/***
 * 
 * @author schancay
 *
 */
class AllTests {
	/***
	 * Administracion de Pruebas de regression para los
	 * servicios de IB
	 * @return
	 */
	static Test suite() {
		def allTests = new GroovyTestSuite()

		//Generador de Servicios
		//-------------------------------------------------
		//Namespace:COBISCorp.eCOBIS.InternetBanking.WebApp.Admin.Service.Service
		allTests.addTestSuite(TestAdminService.class)
		//Namespace:COBISCorp.eCOBIS.InternetBanking.WebApp.EnterpriseServices.Service.Service
		allTests.addTestSuite(TestEnterpriseServices.class)
		//COBISCorp.eCOBIS.InternetBanking.WebApp.Transfers.Service.Service
		allTests.addTestSuite(TestTransfersService.class)
		//COBISCorp.eCOBIS.InternetBanking.WebApp.Utils.Service.Service
		allTests.addTestSuite(TestUtilsService.class)
		//COBISCorp.eCOBIS.InternetBanking.WebApp.Enquiries.Service.Service
		allTests.addTestSuite(TestEnquiriesService.class)
		//COBISCorp.eCOBIS.InternetBanking.WebApp.Enquiries.Service.Service
		allTests.addTestSuite(TestForeignExchangeService.class)
		//COBISCorp.eCOBIS.InternetBanking.WebApp.Customer.Service.Service
		allTests.addTestSuite(TestCustomerService.class)
		//COBISCorp.eCOBIS.InternetBanking.WebApp.Account.Service.Service
		allTests.addTestSuite(TestAccountService.class)
		//COBISCorp.eCOBIS.InternetBanking.WebApp.Payments.Service.Service
		allTests.addTestSuite(TestPaymentsService.class)
		//COBISCorp.eCOBIS.InternetBanking.WebApp.ProgrammedSavings.Service.Service
		allTests.addTestSuite(TestProgrammedSavingsService.class)
		//COBISCorp.eCOBIS.InternetBanking.WebApp.Products.Service.Service
		allTests.addTestSuite(TestProductsService.class)
		//COBISCorp.eCOBIS.InternetBanking.WebApp.Loan.Service.Service
		allTests.addTestSuite(TestLoanService.class)

		return allTests
	}
}

TestRunner.run(AllTests.suite())