package main.java.avii.scenario;

import java.util.ArrayList;
import java.util.Collection;

import main.java.avii.editor.metamodel.entities.EntityDomain;

public class TestScenario {

	private String _name;
	private String _description;
	private EntityDomain _domain;
	private ArrayList<TestVector> _vectors = new ArrayList<TestVector>();

	public void setDescription(String scenarioDescription) {
		this._description = scenarioDescription;
	}

	public String getDescription() {
		return this._description;
	}

	public EntityDomain getDomain() {
		return this._domain;
	}

	public void setName(String scenarioName) {
		this._name = scenarioName;
	}

	public String getName() {
		return this._name;
	}

	public void setDomain(EntityDomain entityDomain) {
		this._domain = entityDomain;		
	}

	public void addVector(TestVector vector) {
		this._vectors.add(vector);
		vector.setScenario(this);
	}

	public Collection<TestVector> getVectors() {
		return this._vectors;
	}


}
