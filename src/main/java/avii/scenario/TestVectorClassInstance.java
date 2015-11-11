package main.java.avii.scenario;

import java.util.HashMap;

import main.java.avii.editor.metamodel.entities.EntityAttribute;

public class TestVectorClassInstance {

	private TestVectorClassTable _table;
	private HashMap<EntityAttribute, Object> _initialAttributes = new HashMap<EntityAttribute, Object>();
	private String _name;
	private String _stateName = null;

	public TestVectorClassInstance(TestVectorClassTable table) {
		this._table = table;
		for(EntityAttribute theAttribute : this._table.getRequestedAttributes())
		{
			addDefaultValueOfAttribute(theAttribute);
		}
	}

	public void addDefaultValueOfAttribute(EntityAttribute theAttribute) {
		this._initialAttributes.put(theAttribute, theAttribute.getType().getDefaultValueAsObject());
	}

	public Object getInitialAttribute(String attrbuteName) {
		EntityAttribute theAttribute = this._table.getAttributeWithName(attrbuteName);
		return this._initialAttributes.get(theAttribute);
	}

	public void setInitialAttribute(String attrbuteName, String attributeValue) {
		EntityAttribute theAttribute = this._table.getAttributeWithName(attrbuteName);
		this._initialAttributes.put(theAttribute, attributeValue);
	}

	public String getName() {
		return this._name;
	}

	public void setName(String instanceName) {
		this._name = instanceName;
	}

	public void setInitialState(String initialStatename) {
		this._stateName  = initialStatename;
	}

	public String getInitialState() {
		return this._stateName;
	}

	public boolean hasSpecifiedInitialState() {
		return this._stateName != null;
	}

}
