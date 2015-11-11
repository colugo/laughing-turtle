package main.java.avii.simulator.simulatedTypes;

import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;
import main.java.avii.editor.metamodel.entities.EntityAttribute;

public class SimulatedAttribute {

	private IEntityDatatype _type;
	private String _name;

	public SimulatedAttribute(EntityAttribute entityAttribute) {
		this._name = entityAttribute.getName();
		this._type = entityAttribute.getType();
	}

	public String getName() {
		return this._name;
	}

	public IEntityDatatype getType() {
		return this._type;
	}

}
