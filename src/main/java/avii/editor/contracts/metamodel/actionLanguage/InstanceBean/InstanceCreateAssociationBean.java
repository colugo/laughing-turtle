package main.java.avii.editor.contracts.metamodel.actionLanguage.InstanceBean;

public class InstanceCreateAssociationBean {

	private String instanceName;
	private String relationName;
	
	public InstanceCreateAssociationBean(String instanceName, String relationName)
	{
		this.instanceName = instanceName;
		this.relationName = relationName;
	}
	
	public String getInstanceName() {
		return instanceName;
	}
	public String getRelationName() {
		return relationName;
	}
}
