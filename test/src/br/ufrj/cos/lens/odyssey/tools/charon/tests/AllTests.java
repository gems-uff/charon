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
		suite.addTest(new TestSuite(CharonFacadeTest.class));
		
		return suite;
	}
}