package main.java.avii.editor.contracts.metamodel.actionLanguage.rationaliser;

import main.java.avii.editor.contracts.metamodel.actionLanguage.InstanceBean.InstanceCreateBean;

public interface IInstanceCreateActionLanguage extends IInstanceCreateDestroyActionLanguage{
	public InstanceCreateBean getCreateInstance();
}
