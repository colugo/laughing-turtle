package main.java.avii.editor.contracts.metamodel.actionLanguage.rationaliser;

import main.java.avii.editor.contracts.metamodel.actionLanguage.InstanceBean.InstanceDestroyBean;

public interface IInstanceDestroyActionLanguage extends IInstanceCreateDestroyActionLanguage{
	public InstanceDestroyBean getDestroyInstance();
}
