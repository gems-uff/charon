package br.ufrj.cos.lens.odyssey.tools.charon.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author murta
 */
public class AllTests extends TestSuite {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(AllTests.suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for Charon");

		// Include tests here
		// Tests removed to avoid long nightly builds (tests should be rewritten to access local repository)
		//suite.addTest(new TestSuite(SubversionConnectorTest.class));
		//suite.addTest(new TestSuite(XArchADTConnectorTest.class));
		
		return suite;
	}
}