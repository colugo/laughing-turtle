package test.java.simulatorTests;

import javax.naming.NameNotFoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageLineException;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.ActionLanguageSupportedSyntax;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.simulator.InstructionManager;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.exceptions.CannotSimulateDomainThatIsInvalidException;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.BaseSimulatedActionLanguage;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.IRelationAlteringSyntax;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.NoMatchingSimulatedActionLanguageException;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntaxFactory;
import test.java.helper.DomainShoppingCart;
import test.java.simulator.helper.SimulatorTestHelper;

public class ProgramInstructionCountManagerTests extends TestCase {

	public ProgramInstructionCountManagerTests(String name) {
		super(name);
	}

	public void test_can_add_simulated_language_to_the_manager() throws InvalidActionLanguageLineException, NoMatchingSimulatedActionLanguageException
	{
		String tempProc = "temp = 1;\n";
		IActionLanguageSyntax syntaxLine = ActionLanguageSupportedSyntax.getSyntaxForLine(tempProc);
		BaseSimulatedActionLanguage simulatedLanguage = SimulatedSyntaxFactory.getSimulatedSyntax(null, syntaxLine);
		
		InstructionManager manager = new InstructionManager();
		manager.pushSyntax(simulatedLanguage);
	}
	
	public void test_can_get_simulated_language_for_given_line() throws InvalidActionLanguageLineException, NoMatchingSimulatedActionLanguageException
	{
		String tempProc = "temp = 1;\n";
		IActionLanguageSyntax syntaxLine = ActionLanguageSupportedSyntax.getSyntaxForLine(tempProc);
		BaseSimulatedActionLanguage simulatedLanguage = SimulatedSyntaxFactory.getSimulatedSyntax(null, syntaxLine);
		
		String tempProc2 = "temp = 2;\n";
		IActionLanguageSyntax syntaxLine2 = ActionLanguageSupportedSyntax.getSyntaxForLine(tempProc2);
		BaseSimulatedActionLanguage simulatedLanguage2 = SimulatedSyntaxFactory.getSimulatedSyntax(null, syntaxLine2);
		
		InstructionManager manager = new InstructionManager();
		manager.pushSyntax(simulatedLanguage);
		manager.pushSyntax(simulatedLanguage2);
		
		Assert.assertEquals(manager.getSyntaxOnLine(1),simulatedLanguage);
		Assert.assertEquals(manager.getSyntaxOnLine(2),simulatedLanguage2);
	}
	
	public void test_can_get_program_counter() throws InvalidActionLanguageLineException, NoMatchingSimulatedActionLanguageException
	{
		String tempProc = "temp = 1;\n";
		IActionLanguageSyntax syntaxLine = ActionLanguageSupportedSyntax.getSyntaxForLine(tempProc);
		BaseSimulatedActionLanguage simulatedLanguage = SimulatedSyntaxFactory.getSimulatedSyntax(null, syntaxLine);
		
		String tempProc2 = "temp = 2;\n";
		IActionLanguageSyntax syntaxLine2 = ActionLanguageSupportedSyntax.getSyntaxForLine(tempProc2);
		BaseSimulatedActionLanguage simulatedLanguage2 = SimulatedSyntaxFactory.getSimulatedSyntax(null, syntaxLine2);
		
		InstructionManager manager = new InstructionManager();
		manager.pushSyntax(simulatedLanguage);
		manager.pushSyntax(simulatedLanguage2);
		
		Assert.assertEquals(1,manager.getProgramCounter());
		Assert.assertEquals(simulatedLanguage, manager.getCurrentSyntax());
	}
	
	public void test_can_get_increment_program_counter() throws InvalidActionLanguageLineException, NoMatchingSimulatedActionLanguageException, NameNotFoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		Simulator simulator = new Simulator(shoppingCartDomain);
		
		String tempProc = "temp = 1;\n";
		IActionLanguageSyntax syntaxLine = ActionLanguageSupportedSyntax.getSyntaxForLine(tempProc);
		BaseSimulatedActionLanguage simulatedLanguage = SimulatedSyntaxFactory.getSimulatedSyntax(simulator, syntaxLine);
		
		String tempProc2 = "temp = 2;\n";
		IActionLanguageSyntax syntaxLine2 = ActionLanguageSupportedSyntax.getSyntaxForLine(tempProc2);
		BaseSimulatedActionLanguage simulatedLanguage2 = SimulatedSyntaxFactory.getSimulatedSyntax(simulator, syntaxLine2);
		
		InstructionManager manager = new InstructionManager();
		manager.pushSyntax(simulatedLanguage);
		manager.pushSyntax(simulatedLanguage2);
		
		Assert.assertEquals(1,manager.getProgramCounter());
		Assert.assertEquals(simulatedLanguage, manager.getCurrentSyntax());
		
		SimulatorTestHelper.pickFirstStateFromFirstClassForTesting(simulator);
		manager.simulateCurrentSyntax();
		
		Assert.assertEquals(2,manager.getProgramCounter());
		Assert.assertEquals(simulatedLanguage2, manager.getCurrentSyntax());
	}
	
	public void test_can_automatically_increment_program_counter() throws InvalidActionLanguageLineException, NoMatchingSimulatedActionLanguageException, NameNotFoundException, InvalidActionLanguageSyntaxException, CannotSimulateDomainThatIsInvalidException
	{
		EntityDomain shoppingCartDomain = DomainShoppingCart.getShoppingCartDomainWithActionLanguage();
		Simulator simulator = new Simulator(shoppingCartDomain);
		
		String tempProc = "temp = 1;\n";
		IActionLanguageSyntax syntaxLine = ActionLanguageSupportedSyntax.getSyntaxForLine(tempProc);
		BaseSimulatedActionLanguage simulatedLanguage = SimulatedSyntaxFactory.getSimulatedSyntax(simulator, syntaxLine);
		
		String tempProc2 = "temp = 2;\n";
		IActionLanguageSyntax syntaxLine2 = ActionLanguageSupportedSyntax.getSyntaxForLine(tempProc2);
		BaseSimulatedActionLanguage simulatedLanguage2 = SimulatedSyntaxFactory.getSimulatedSyntax(simulator, syntaxLine2);
		
		InstructionManager manager = new InstructionManager();
		manager.pushSyntax(simulatedLanguage);
		manager.pushSyntax(simulatedLanguage2);
		
		Assert.assertTrue(manager.hasMoreInstructions());
		Assert.assertEquals(1,manager.getProgramCounter());
		Assert.assertEquals(simulatedLanguage, manager.getCurrentSyntax());
		
		SimulatorTestHelper.pickFirstStateFromFirstClassForTesting(simulator);
		manager.simulateCurrentSyntax();
		
		Assert.assertEquals(1.0d,simulator.getSimulatingState().getTempVariable("temp"));
		
		Assert.assertTrue(manager.hasMoreInstructions());
		Assert.assertEquals(2,manager.getProgramCounter());
		Assert.assertEquals(simulatedLanguage2, manager.getCurrentSyntax());
		
		manager.simulateCurrentSyntax();
		Assert.assertEquals(2.0d,simulator.getSimulatingState().getTempVariable("temp"));
		
		Assert.assertTrue(!manager.hasMoreInstructions());
	}

	public void test_manager_can_tell_if_one_of_its_syntax_alters_relations() throws InvalidActionLanguageLineException, NoMatchingSimulatedActionLanguageException
	{
		String tempProc = "RELATE fred TO bob ACROSS R1;\n";
		IActionLanguageSyntax syntaxLine = ActionLanguageSupportedSyntax.getSyntaxForLine(tempProc);
		BaseSimulatedActionLanguage simulatedLanguage = SimulatedSyntaxFactory.getSimulatedSyntax(null, syntaxLine);
		
		Assert.assertEquals(true, simulatedLanguage instanceof IRelationAlteringSyntax);
		
		InstructionManager manager = new InstructionManager();
		Assert.assertEquals(false, manager.doesSyntaxAlterRelations());
		manager.pushSyntax(simulatedLanguage);
		Assert.assertEquals(true, manager.doesSyntaxAlterRelations());

	}
	
}


