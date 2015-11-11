package main.java.avii.scenario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_AttributeExpression;
import main.java.avii.editor.metamodel.actionLanguage.syntax.Syntax_CreateInstanceFromClass;
import main.java.avii.editor.metamodel.entities.EntityAttribute;
import main.java.avii.editor.metamodel.entities.EntityClass;

public class TestVectorClassTable {

	private EntityClass _class;
	private ArrayList<TestVectorClassInstance> _instances = new ArrayList<TestVectorClassInstance>();
	private HashMap<String,EntityAttribute> _requestedAttributes = new HashMap<String,EntityAttribute>();
	private int _instanceCount = 0;

	public TestVectorClassTable(EntityClass theEntityClass) {
		this._class = theEntityClass;
	}

	public EntityClass getTableClass() {
		return this._class;
	}

	public ArrayList<TestVectorClassInstance> getInstances() {
		return this._instances;
	}

	public TestVectorClassInstance createInstance() {
		this._instanceCount++;
		TestVectorClassInstance instance = new TestVectorClassInstance(this);
		instance.setName(this._class.getName() + "_" + String.format("%03d", this._instanceCount));
		this._instances.add(instance);
		return instance;
	}

	public Collection<EntityAttribute> getRequestedAttributes() {
		return this._requestedAttributes.values();
	}

	public void addAttribute(EntityAttribute theAttribute) {
		String attributeName = theAttribute.getName();
		this._requestedAttributes.put(attributeName, theAttribute);
		for(TestVectorClassInstance instance : this._instances)
		{
			instance.addDefaultValueOfAttribute(theAttribute);
		}
	}

	public EntityAttribute getAttributeWithName(String attrbuteName) {
		return this._requestedAttributes.get(attrbuteName);
	}

	private String getCreateString(TestVectorClassInstance instance)
	{
		String className = this._class.getName();
		String instanceName = instance.getName();
		
		Syntax_CreateInstanceFromClass createSyntax = new Syntax_CreateInstanceFromClass();
		createSyntax.setClass(className);
		createSyntax.setInstance(instanceName);
		String asString = createSyntax.toString();
		asString = asString + "\n";
		return asString;
	}
	
	private String getAttributeString(TestVectorClassInstance instance) {
		StringBuffer attributeString = new StringBuffer();
		for(String attributeName : this._requestedAttributes.keySet())
		{
			String attributeValue = instance.getInitialAttribute(attributeName).toString();
			EntityAttribute theAttribute = this._requestedAttributes.get(attributeName);
			IEntityDatatype dataType = theAttribute.getType();
			String stringAttributeValue = dataType.getValueForInsertionInCode(attributeValue);
			
			Syntax_AttributeExpression attributeExpression = new Syntax_AttributeExpression();
			attributeExpression.setAttributeName(attributeName);
			attributeExpression.setInstanceName(instance.getName());
			attributeExpression.setNewLogic(stringAttributeValue);
			
			attributeString.append(attributeExpression.toString());
			attributeString.append("\n");
		}
		return attributeString.toString();
	}
	
	public String serialiseToInitialState() {
		if(this._class.isAssociation())
		{
			return "";
		}
		
		StringBuffer out = new StringBuffer();
		
		for(TestVectorClassInstance instance : this._instances)
		{
			String createString = this.getCreateString(instance);
			out.append(createString);
			String attributesString = this.getAttributeString(instance);
			out.append(attributesString);
		}
		return out.toString();
	}

	public String serialiseToInitialStateForAssociationClasses() {
		if(!this._class.isAssociation())
		{
			return "";
		}
		
		StringBuffer out = new StringBuffer();
		
		for(TestVectorClassInstance instance : this._instances)
		{
			String attributesString = this.getAttributeString(instance);
			out.append(attributesString);
		}
		return out.toString();
	}
	
}
