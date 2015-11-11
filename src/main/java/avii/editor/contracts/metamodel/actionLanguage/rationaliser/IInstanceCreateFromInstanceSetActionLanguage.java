package main.java.avii.editor.contracts.metamodel.actionLanguage.rationaliser;

import main.java.avii.editor.contracts.metamodel.actionLanguage.InstanceBean.InstanceCreateBean;

public interface IInstanceCreateFromInstanceSetActionLanguage extends IInstanceCreateDestroyActionLanguage{
	public InstanceCreateBean getCreateInstance();
}
