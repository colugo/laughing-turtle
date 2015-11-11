package test.java.modelTesting.weaveModelTesting;

import javax.naming.NameAlreadyBoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import test.java.modelTesting.SimulatedTestHelper;
import test.java.tests.TestHarness;
import test.java.weaveModelTests.DomainFactory;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.simulator.exceptions.CannotSimulateDomainThatIsInvalidException;


public class SimulatedFactoryTests extends TestCase {
	DomainFactory _domain = null;
	public SimulatedFactoryTests(String name) throws NameAlreadyBoundException, InvalidActionLanguageSyntaxException {
		super(name);
		_domain = new DomainFactory();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void test_run_simulated_factory_harness() throws CannotSimulateDomainThatIsInvalidException
	{
		TestHarness harness = new TestHarness(this._domain.domain);
		harness.execute();

		SimulatedTestHelper.printHarnessResults(harness);
		Assert.assertEquals(true, harness.allAssertionsPassed());
		Assert.assertEquals(false, harness.wereExceptionsRaised());
	}
}
