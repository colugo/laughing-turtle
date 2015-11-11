package main.java.avii.scenario;

import java.util.ArrayList;

import main.java.avii.editor.metamodel.entities.EntityRelation;
import main.java.avii.util.Text;

public class TestVectorRelationTable {

	private EntityRelation _relation;
	private ArrayList<TestVectorRelationInstance> _instances = new ArrayList<TestVectorRelationInstance>();

	public TestVectorRelationTable(EntityRelation relation) {
		this._relation = relation;
	}

	public EntityRelation getTableRelation() {
		return this._relation;
	}

	public ArrayList<TestVectorRelationInstance> getInstances() {
		return this._instances;
	}

	public TestVectorRelationInstance createInstance() {
		TestVectorRelationInstance newInstance = new TestVectorRelationInstance(this);
		this._instances.add(newInstance);
		return newInstance;
	}

	public String serialiseToInitialState() {
		StringBuffer out = new StringBuffer();
		int count = 0;
		for(TestVectorRelationInstance instance : this._instances)
		{
			out.append(this.generateRelateString(instance, count++));
		}
		
		return out.toString();
	}

	private String generateRelateString(TestVectorRelationInstance instance, int relationCount) {
		String output = "";
		output = "RELATE " + instance.getEndA() + " TO " + instance.getEndB() + " ACROSS " + this._relation.getName();
		
		if(this._relation.isReflexive())
		{
			output += "." + Text.quote(this._relation.getClassAVerb());
		}
		
		if(this._relation.hasAssociation())
		{
			String associaionInstance = instance.getAssociation();
			if(!instance.hasAssociation())
			{
				String associationClassName = this._relation.getAssociation().getName();
				associaionInstance = associationClassName + "_" + Integer.toString(relationCount);
			}
			output += " CREATING " + associaionInstance;
		}
		output += ";\n";
		return output;
	}

}
