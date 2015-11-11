package main.java.avii.editor.metamodel.actionLanguage.instanceLifespan;

import java.util.HashMap;

import javax.naming.NameNotFoundException;
import javax.naming.OperationNotSupportedException;

public class StateInstanceLifespanManager {

	private HashMap<String, InstanceLifespanManager> _instances = new HashMap<String, InstanceLifespanManager>();
	private String _invalid = null;

	private void invalidate(String reason) {
		_invalid = reason;
	}

	public boolean isInvalid() {
		return _invalid != null;
	}

	private boolean isValid() {
		return _invalid == null;
	}

	public void beginDeclaration(String instanceName, String className,Boolean isInstanceSet, int startLine) throws  NameNotFoundException {
		if (isValid()) {
			InstanceLifespanManager lifespan = getInstanceLifespanForNameOrNew(instanceName);
			try {
				lifespan.start(startLine, className, isInstanceSet);
			} catch (OperationNotSupportedException e) {
				this.invalidate(e.getExplanation());
			}
		}
	}
	
	public void endDeclaration(String instanceName, int endLine) throws OperationNotSupportedException, NameNotFoundException {
		if (isValid()) {
			InstanceLifespanManager lifespan = getInstanceLifespan(instanceName);
			if(isValid())
			{
				lifespan.end(endLine);
			}
		}
	}

	public boolean haveInstanceLifespan(String instanceName) {
		return _instances.containsKey(instanceName);
	}

	private InstanceLifespanManager getInstanceLifespan(String instanceName) {
		if (!haveInstanceLifespan(instanceName)) {
			invalidate("Can not find instance lifespan with name : " + instanceName);
			return null;
		}
		return _instances.get(instanceName);
	}

	private InstanceLifespanManager getInstanceLifespanForNameOrNew(String instanceName) throws NameNotFoundException {
		if (haveInstanceLifespan(instanceName)) {
			return getInstanceLifespan(instanceName);
		}
		return createNewInstanceLifespan(instanceName);
	}

	private InstanceLifespanManager createNewInstanceLifespan(String instanceName) {
		InstanceLifespanManager lifespan = new InstanceLifespanManager(instanceName);
		_instances.put(instanceName, lifespan);
		return lifespan;
	}

	public String identifyInstance(String instanceName, int lineNumber) throws NameNotFoundException, OperationNotSupportedException {
		if(isInvalid())
		{
			throw new OperationNotSupportedException(_invalid);
		}
		if(haveInstanceLifespan(instanceName))
		{
			InstanceLifespanManager lifespan = getInstanceLifespan(instanceName);
			return lifespan.getClassNameAtLine(lineNumber);
		}
		throw new NameNotFoundException("Could not find instance : " + instanceName);
	}

	public boolean isInstanceAnInstanceSetOnLine(String instanceName, int lineNumber) throws OperationNotSupportedException, NameNotFoundException {
		if(isInvalid())
		{
			throw new OperationNotSupportedException(_invalid);
		}
		if(haveInstanceLifespan(instanceName))
		{
			InstanceLifespanManager lifespan = getInstanceLifespan(instanceName);
			return lifespan.isInstanceSetAtLine(lineNumber);
		}
		throw new NameNotFoundException("Could not find instance : " + instanceName);
	}

	public void closeAllOpenDeclarationsThatWereOpenedOnOrAfterLine(int openingLineNumber, int closingLineNumber) throws OperationNotSupportedException, NameNotFoundException, IndexOutOfBoundsException {
		for(String instanceName : this._instances.keySet())
		{
			InstanceLifespanManager instanceLifespanManager = this._instances.get(instanceName);
			if(instanceLifespanManager.wasStartedOnOrAfterLineNumber(openingLineNumber))
			{
				instanceLifespanManager.end(closingLineNumber);
			}
		}
	}

}
