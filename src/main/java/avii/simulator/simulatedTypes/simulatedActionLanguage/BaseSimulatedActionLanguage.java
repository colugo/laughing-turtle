package main.java.avii.simulator.simulatedTypes.simulatedActionLanguage;

import javax.naming.NameNotFoundException;

import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.IActionLanguageSyntax;
import main.java.avii.editor.metamodel.entities.EntityClass;
import main.java.avii.simulator.ISimulator;
import main.java.avii.simulator.Simulator;
import main.java.avii.simulator.exceptions.NullSimulatedInstanceCannotBeUsedException;
import main.java.avii.simulator.simulatedTypes.NullSimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedClass;
import main.java.avii.simulator.simulatedTypes.SimulatedInstance;
import main.java.avii.simulator.simulatedTypes.SimulatedState;

public abstract class BaseSimulatedActionLanguage implements ISimulatedActionLanguage {

	protected Simulator _simulator = null;
	protected IActionLanguageSyntax _concreteSyntax = null;
	protected int _lineNumber;
	
	public BaseSimulatedActionLanguage(Simulator theSimulator, IActionLanguageSyntax theConcreteSyntax)
	{
		this._simulator = theSimulator;
		this._concreteSyntax = theConcreteSyntax;
	}
	
	protected BaseSimulatedActionLanguage(){}
	
	public IActionLanguageSyntax getConcreteSyntax()
	{
		return this._concreteSyntax;
	}
	
	public abstract void simulate();
	
	public void setLineNumber(int lineNumber)
	{
		this._lineNumber = lineNumber;
	}
	
	public int getLineNumber()
	{
		return this._lineNumber;
	}
	
	public int getNextInstructionLineNumber()
	{
		return this._lineNumber + 1;
	}

	public IActionLanguageSyntax getSyntax() {
		return this._concreteSyntax;
	}

	public ISimulator getSimulator() {
		return this._simulator;
	}

	protected void checkForNullInstance(SimulatedInstance instance)
	{
		String instanceName = this._simulator.getSimulatingState().getNameForInstanceId(instance.getIdentifier());
		if(instance instanceof NullSimulatedInstance)
		{
			throw new NullSimulatedInstanceCannotBeUsedException(instanceName);
		}
	}
	
	
	
	public SimulatedInstance getInstanceWithName(String instanceName)
	{
		SimulatedInstance instance = null;
		SimulatedState simulatingState = this._simulator.getSimulatingState();
		try {
			instance = simulatingState.getInstanceWithName(instanceName);
			if(instance == null)
			{
				throw new NameNotFoundException();
			}
		} catch (NameNotFoundException e) {
		
			try{
				EntityClass theClass = simulatingState.getProcedure().identifyClass(instanceName, this._lineNumber);
				SimulatedClass theSimulatedClass = this._simulator.getSimulatedClass(theClass.getName());
				return new NullSimulatedInstance(theSimulatedClass);
			}catch(Exception e2){}
		}
		return instance;
	}
	
	public String asString()
	{
		return this._concreteSyntax.toString();
	}
	
}
