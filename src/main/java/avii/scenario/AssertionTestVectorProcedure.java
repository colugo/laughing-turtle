package main.java.avii.scenario;

import java.util.HashMap;

import javax.naming.NameNotFoundException;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.InvalidActionLanguageSyntaxException;
import main.java.avii.editor.metamodel.actionLanguage.reader.ActionLanguageSyntaxRationaliserReader;
import main.java.avii.editor.metamodel.entities.EntityClass;

public class AssertionTestVectorProcedure extends TestVectorProcedure {

	public void setVector(TestVector vector) {
		this._vector = vector;
		_vector.setAssertionProcedure(this);
	}

	public void insertInitialVectorLookupForValidation(HashMap<String, EntityClass> tableLookup) {
		for(String instanceName : tableLookup.keySet())
		{
			String className = tableLookup.get(instanceName).getName();
			try {
				if(!this._instanceLifespanManager.haveInstanceLifespan(instanceName))
				{
					this._instanceLifespanManager.beginDeclaration(instanceName, className, false, 0);
				}
			} catch (NameNotFoundException e) {
			}
		}
	}

	public void calculateInstanceLifespanMapForValidation() throws InvalidActionLanguageSyntaxException {
		_syntaxRationaliserReader = new ActionLanguageSyntaxRationaliserReader(getInputStreamFromRawText(), getDomain(), _instanceLifespanManager);
		_syntaxRationaliserReader.read();
		this._syntaxMap = _syntaxRationaliserReader.getSyntaxMap();
		setTempVariableLifespanManager(_syntaxRationaliserReader.getTempVariableLifespanManager());
		setGeneratedEventManager(_syntaxRationaliserReader.getGeneratedEventManager());
	}

	public void setupForValidation(HashMap<String, EntityClass> tableLookup) throws InvalidActionLanguageSyntaxException
	{
		this.initialiseInstanceLifespanManager();
		this.insertInitialVectorLookupForValidation(tableLookup);
		this.calculateInstanceLifespanMapForValidation();
	}
	
}
