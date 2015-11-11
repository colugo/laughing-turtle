package test.java.modelTesting;

import javax.naming.NameNotFoundException;

import junit.framework.Assert;
import junit.framework.TestCase;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.editor.metamodel.entities.EntityRelation.CardinalityType;
import main.java.avii.scenario.AssertionTestVectorProcedure;
import main.java.avii.scenario.TestScenario;
import main.java.avii.scenario.TestVector;
import main.java.avii.scenario.TestVectorClassTable;
import main.java.avii.scenario.TestVectorRelationInstance;
import main.java.avii.scenario.TestVectorRelationTable;
import main.java.avii.simulator.exceptions.BaseException;
import main.java.avii.simulator.exceptions.CannotSimulateDomainThatIsInvalidException;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.IRelationAlteringSyntax;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedNOP;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSynatx_CreateInstanceFromClass;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntax_AttributeExpressoin;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntax_CancelEventnameFromSenderToTarget;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntax_DeleteInstance;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntax_Else;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntax_EndFor;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntax_EndIf;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntax_FailAssertion;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntax_ForInstanceInInstanceset;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntax_GenerateEventParamsToClassCreator;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntax_GenerateEventParamsToClassCreatorDelayDuration;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntax_GenerateEventParamsToInstance;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntax_GenerateEventParamsToInstanceDelayDuration;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntax_IfEmptyInstancesetThen;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntax_IfLogic;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntax_IfNotEmptyInstancesetThen;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntax_ReclassifyToClass;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntax_RelateInstance1ToInstance2AcrossReflexiveRelation;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntax_RelateInstance1ToInstance2AcrossReflexiveRelationCreatingInstance3;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntax_RelateInstance1ToInstance2AcrossRelation;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntax_RelateInstance1ToInstance2AcrossRelationCreatingInstance3;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntax_Return;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntax_SelectAnyManyInstanceFromInstancesOfClass;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntax_SelectAnyManyInstanceFromInstancesOfClassWhereLogic;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntax_SelectAnyManyInstancesRelatedByRelations;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntax_SelectAnyManyInstancesRelatedByRelationsWhereLogic;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossReflexiveRelation;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossRelation;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntax_TempExpressoin;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntax_UnrelateInstance1FromInstance2AcrossReflexiveRelation;
import main.java.avii.simulator.simulatedTypes.simulatedActionLanguage.SimulatedSyntax_UnrelateInstance1FromInstance2AcrossRelation;
import test.java.tests.TestHarness;

public class SimulatedRelationshipExceptionTests extends TestCase {
	public SimulatedRelationshipExceptionTests(String name)
	{
		super(name);
	}
	
	private EntityDomain setupDomain()
	{
		EntityDomain domain = new EntityDomain("parent child");
		EntityClass parent = new EntityClass("Parent");
		EntityClass child = new EntityClass("Child");
		domain.addClass(parent);
		domain.addClass(child);
		EntityRelation r1 = new EntityRelation("R1");
		r1.setEndA(parent, CardinalityType.ONE_TO_MANY);
		r1.setEndB(child, CardinalityType.ONE_TO_ONE);
		return domain;
	}
	
	private EntityClass getParent(EntityDomain domain)
	{
		return domain.getEntityClassWithName("Parent");
	}
	
	private EntityClass getChild(EntityDomain domain)
	{
		return domain.getEntityClassWithName("Child");
	}
	
	private EntityRelation getR1(EntityDomain domain)
	{
		try {
			return domain.getRelationWithName("R1");
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void test_check_specific_actionlanguage_types_can_affect_relations()
	{		
		Assert.assertEquals(false, new SimulatedSyntax_AttributeExpressoin() instanceof IRelationAlteringSyntax);
		Assert.assertEquals(false, new SimulatedNOP() instanceof IRelationAlteringSyntax);
		Assert.assertEquals(false, new SimulatedSyntax_CancelEventnameFromSenderToTarget() instanceof IRelationAlteringSyntax);
		Assert.assertEquals(false, new SimulatedNOP() instanceof IRelationAlteringSyntax);
		Assert.assertEquals(false, new SimulatedSynatx_CreateInstanceFromClass() instanceof IRelationAlteringSyntax);
		Assert.assertEquals(true, new SimulatedSyntax_DeleteInstance() instanceof IRelationAlteringSyntax);
		Assert.assertEquals(false, new SimulatedSyntax_Else() instanceof IRelationAlteringSyntax);
		Assert.assertEquals(false, new SimulatedSyntax_EndFor() instanceof IRelationAlteringSyntax);
		Assert.assertEquals(false, new SimulatedSyntax_EndIf() instanceof IRelationAlteringSyntax);
		Assert.assertEquals(false, new SimulatedSyntax_ForInstanceInInstanceset() instanceof IRelationAlteringSyntax);
		Assert.assertEquals(false, new SimulatedSyntax_GenerateEventParamsToInstance() instanceof IRelationAlteringSyntax);
		Assert.assertEquals(false, new SimulatedSyntax_GenerateEventParamsToInstanceDelayDuration() instanceof IRelationAlteringSyntax);
		Assert.assertEquals(false, new SimulatedSyntax_GenerateEventParamsToClassCreator() instanceof IRelationAlteringSyntax);
		Assert.assertEquals(false, new SimulatedSyntax_GenerateEventParamsToClassCreatorDelayDuration() instanceof IRelationAlteringSyntax);
		Assert.assertEquals(false, new SimulatedSyntax_IfEmptyInstancesetThen() instanceof IRelationAlteringSyntax);
		Assert.assertEquals(false, new SimulatedSyntax_IfNotEmptyInstancesetThen() instanceof IRelationAlteringSyntax);
		Assert.assertEquals(false, new SimulatedSyntax_IfLogic() instanceof IRelationAlteringSyntax);
		Assert.assertEquals(true, new SimulatedSyntax_ReclassifyToClass() instanceof IRelationAlteringSyntax);
		Assert.assertEquals(true, new SimulatedSyntax_RelateInstance1ToInstance2AcrossRelation() instanceof IRelationAlteringSyntax);
		Assert.assertEquals(true, new SimulatedSyntax_RelateInstance1ToInstance2AcrossReflexiveRelation() instanceof IRelationAlteringSyntax);
		Assert.assertEquals(true, new SimulatedSyntax_RelateInstance1ToInstance2AcrossRelationCreatingInstance3() instanceof IRelationAlteringSyntax);
		Assert.assertEquals(true, new SimulatedSyntax_RelateInstance1ToInstance2AcrossReflexiveRelationCreatingInstance3() instanceof IRelationAlteringSyntax);
		Assert.assertEquals(false, new SimulatedSyntax_Return() instanceof IRelationAlteringSyntax);
		Assert.assertEquals(false, new SimulatedSyntax_SelectAnyManyInstancesRelatedByRelations() instanceof IRelationAlteringSyntax);
		Assert.assertEquals(false, new SimulatedSyntax_SelectAnyManyInstancesRelatedByRelationsWhereLogic() instanceof IRelationAlteringSyntax);
		Assert.assertEquals(false, new SimulatedSyntax_SelectAnyManyInstanceFromInstancesOfClass() instanceof IRelationAlteringSyntax);
		Assert.assertEquals(false, new SimulatedSyntax_SelectAnyManyInstanceFromInstancesOfClassWhereLogic() instanceof IRelationAlteringSyntax);
		Assert.assertEquals(false, new SimulatedSyntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossRelation() instanceof IRelationAlteringSyntax);
		Assert.assertEquals(false, new SimulatedSyntax_SelectOneInstance1ThatRelatesInstance2ToInstance3AcrossReflexiveRelation() instanceof IRelationAlteringSyntax);
		Assert.assertEquals(false, new SimulatedSyntax_TempExpressoin() instanceof IRelationAlteringSyntax);
		Assert.assertEquals(true, new SimulatedSyntax_UnrelateInstance1FromInstance2AcrossRelation() instanceof IRelationAlteringSyntax);
		Assert.assertEquals(true, new SimulatedSyntax_UnrelateInstance1FromInstance2AcrossReflexiveRelation() instanceof IRelationAlteringSyntax);
		Assert.assertEquals(false, new SimulatedSyntax_FailAssertion() instanceof IRelationAlteringSyntax);
	}
	
	public void test_check_child_cannot_be_related_to_more_then_one_parent() throws CannotSimulateDomainThatIsInvalidException, InvalidActionLanguageSyntaxException
	{
		EntityDomain domain = setupDomain();
		TestScenario scenario = new TestScenario();
		domain.addScenario(scenario);
		TestVector vector = new TestVector();
		scenario.addVector(vector);
		
		TestVectorClassTable parentTable = new TestVectorClassTable(getParent(domain));
		parentTable.createInstance();
		parentTable.createInstance();
		vector.addClassTable(parentTable);
		
		TestVectorClassTable childTable = new TestVectorClassTable(getChild(domain));
		childTable.createInstance();
		vector.addClassTable(childTable);
		
		TestVectorRelationTable r1Table = new TestVectorRelationTable(getR1(domain));
		vector.addRelationTable(r1Table);
		TestVectorRelationInstance r1Instance =  r1Table.createInstance();
		r1Instance.setEndA("Parent_001");
		r1Instance.setEndB("Child_001");
		
		TestVectorRelationInstance r1Instance1 =  r1Table.createInstance();
		r1Instance1.setEndA("Parent_002");
		r1Instance1.setEndB("Child_001");
		vector.createInitialProcedureFromTables();

		AssertionTestVectorProcedure assertionProc = new AssertionTestVectorProcedure();
		assertionProc.setVector(vector);
		vector.setAssertionProcedure(assertionProc);
		assertionProc.setProcedure("");
		
		TestHarness harness = new TestHarness(domain);
		harness.execute();
		BaseException exception = harness.getScenarios().get(0).getSimulatedVectors().get(0).getException();
		Assert.assertEquals("Child(Child_001) related to more then 1 Parent across R1", exception.getMessage());
	}
	
	public void test_check_parent_must_be_related_to_at_least_one_child() throws CannotSimulateDomainThatIsInvalidException, InvalidActionLanguageSyntaxException
	{
		EntityDomain domain = setupDomain();
		TestScenario scenario = new TestScenario();
		domain.addScenario(scenario);
		TestVector vector = new TestVector();
		scenario.addVector(vector);
		
		TestVectorClassTable parentTable = new TestVectorClassTable(getParent(domain));
		parentTable.createInstance();
		vector.addClassTable(parentTable);

		vector.createInitialProcedureFromTables();

		AssertionTestVectorProcedure assertionProc = new AssertionTestVectorProcedure();
		assertionProc.setVector(vector);
		vector.setAssertionProcedure(assertionProc);
		assertionProc.setProcedure("");
		
		TestHarness harness = new TestHarness(domain);
		harness.execute();
		BaseException exception = harness.getScenarios().get(0).getSimulatedVectors().get(0).getException();
		
		Assert.assertNotNull(exception);
		Assert.assertEquals("Parent(Parent_001) was not related to a Child across R1", exception.getMessage());
	}
}
