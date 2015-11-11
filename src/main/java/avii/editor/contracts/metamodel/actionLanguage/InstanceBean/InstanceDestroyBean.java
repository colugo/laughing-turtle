package main.java.avii.editor.contracts.metamodel.actionLanguage.InstanceBean;

public class InstanceDestroyBean {

	private String instanceName;
	
	public InstanceDestroyBean(String instanceName)
	{
		this.instanceName = instanceName;
	}
	
	public String getInstanceName() {
		return instanceName;
	}
}
