package main.java.avii.scenario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.entities.EntityClass;


public class TestVector {

	private String _description;
	private TestScenario _scenario;
	private InitialTestVectorProcedure _initialProcedure;
	private ArrayList<TestVectorClassTable> _initialStateTables = new ArrayList<TestVectorClassTable>();
	private AssertionTestVectorProcedure _assertionProcedure;
	private String _initialGenerateActionLanguage = "";
	private HashSet<TestVectorRelationTable> _initialRelationTables = new HashSet<TestVectorRelationTable>();
	private boolean _isApplicableWhenWoven = true;

	public void setDescription(String vectorDescription) {
		this._description = vectorDescription;
	}

	public String getDescription() {
		return this._description;
	}

	public TestScenario getScenario() {
		return this._scenario;
	}

	public void setScenario(TestScenario testScenario) {
		this._scenario = testScenario;		
	}

	public InitialTestVectorProcedure getInitialProcedure() {
		return this._initialProcedure;
	}

	public void setInitialProcedure(InitialTestVectorProcedure vectorProcedure) {
		this._initialProcedure = vectorProcedure;
	}

	public void addClassTable(TestVectorClassTable table) {
		this._initialStateTables.add(table);
	}
	
	public void addRelationTable(TestVectorRelationTable relationTable) {
		this._initialRelationTables.add(relationTable);
	}

	public void createInitialProcedureFromTables() throws InvalidActionLanguageSyntaxException {
		StringBuffer rawText = new StringBuffer();
		for(TestVectorClassTable table : this._initialStateTables)
		{
			rawText.append(table.serialiseToInitialState());
		}
		
		for(TestVectorRelationTable table : this._initialRelationTables)
		{
			rawText.append(table.serialiseToInitialState());
		}
		
		for(TestVectorClassTable table : this._initialStateTables)
		{
			rawText.append(table.serialiseToInitialStateForAssociationClasses());
		}
		
		rawText.append("#HOOK FOR STATE CHANGE\n");
		
		rawText.append(this._initialGenerateActionLanguage);
		
		this._initialProcedure = new InitialTestVectorProcedure();
		this._initialProcedure.setVector(this);
		this._initialProcedure.setProcedure(rawText.toString());
	}

	public ArrayList<TestVectorClassTable> getTables() {
		return this._initialStateTables;		
	}

	public void setAssertionProcedure(AssertionTestVectorProcedure vectorProcedure) {
		this._assertionProcedure = vectorProcedure;		
	}

	public AssertionTestVectorProcedure getAssertionProcedure() {
		return this._assertionProcedure;
	}

	public void addInitialGenerateLanguage(String initialGenerateActionLanguage) throws InvalidActionLanguageSyntaxException {
		this._initialGenerateActionLanguage  = initialGenerateActionLanguage;
		
		// reset the procedure with the generate language
		this.createInitialProcedureFromTables();
	}

	public HashMap<String, EntityClass> calculateInstanceLookup() {
		HashMap<String, EntityClass> map = new HashMap<String, EntityClass>();
		for(TestVectorClassTable table : this._initialStateTables)
		{
			EntityClass theClass = table.getTableClass();
			for(TestVectorClassInstance isntance : table.getInstances())
			{
				String instanceName = isntance.getName();
				map.put(instanceName, theClass);
			}
		}
		return map;
	}

	public void isVectorApplicableWhenWoven(boolean applicability)
	{
		this._isApplicableWhenWoven = applicability;
	}
	
	public boolean isApplicableWhenWoven() {
		return this._isApplicableWhenWoven ;
	}



}
