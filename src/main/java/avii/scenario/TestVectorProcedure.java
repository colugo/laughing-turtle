package main.java.avii.scenario;

import main.java.avii.editor.metamodel.actionLanguage.instanceLifespan.StateInstanceLifespanManager;
import main.java.avii.editor.metamodel.entities.EntityDomain;
import main.java.avii.editor.metamodel.entities.EntityProcedure;

public abstract class TestVectorProcedure extends EntityProcedure {

	protected TestVector _vector;
	
	public TestVector getVector() {
		return this._vector;
	}

	@Override
	public void initialiseInstanceLifespanManager() {
		_instanceLifespanManager = new StateInstanceLifespanManager();
	}
	
	@Override
	protected EntityDomain getDomain() {
		return _vector.getScenario().getDomain();
	}

	public String getRawText() {
		return this._rawText;
	}

	public abstract void setVector(TestVector vector);

}
