package com.cobiscorp.test;

import org.junit.rules.ExternalResource;

/**
 * Class that setup configuration before and after, any expensive resource must be allocated and deallocated here
 * 
 * @author fabad
 *
 */
public class SetUpTestEnvironment extends ExternalResource {
	/**
	 * This method must be call from every test in annotation: @BeforeClass of the test
	 */
	public static void setUp() {
		System.setProperty("testPropertyFile", "ib.properties");
		System.out.println("setting up environment with: " + System.getProperty("testPropertyFile"));
	}

	@Override
	protected void after() {
		System.out.println("clossing setupenvironment");
		// FABAD
		// BDD.close();
	};

	@Override
	protected void before() throws Throwable     {
		setUp();
	};

}
