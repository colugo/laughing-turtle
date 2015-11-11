package main.java.avii.editor.contracts.metamodel.actionLanguage.rationaliser;

import main.java.avii.editor.contracts.metamodel.actionLanguage.InstanceBean.InstanceSelectRelatedByBean;
import main.java.avii.editor.contracts.metamodel.actionLanguage.syntax.languageConstructs.IRelationList;

public interface IInstanceSelectRelatedByActionLanguage extends IInstanceSelectActionLanguage {
	public InstanceSelectRelatedByBean getSelectBean();

	public IRelationList get_Relations();
}
