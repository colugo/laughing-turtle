package main.java.avii.editor.contracts.metamodel.actionLanguage.InstanceBean;

import java.util.ArrayList;

public class InstanceSelectRelatedByBean {

	private ArrayList<String> _relationNames = new ArrayList<String>();
	private String _createdInstanceName;
	private String _startingInstanceName;
	private Boolean _isInstanceSet;
	
	public InstanceSelectRelatedByBean(String theCreatedInstanceName, String theStartingInstanceName, Boolean isInstanceSet)
	{
		this._createdInstanceName = theCreatedInstanceName;
		this._startingInstanceName = theStartingInstanceName;
		this._isInstanceSet = isInstanceSet;
	}
	
	public void AddRelation(String relationName)
	{
		_relationNames.add(relationName);
	}
	
	public ArrayList<String> getOrderedRelationNames()
	{
		return _relationNames;
	}
	public String getCreatedInstanceName()
	{
		return _createdInstanceName;
	}
	public String getStartingInstanceName()
	{
		return _startingInstanceName;
	}
	public Boolean isInstanceSet()
	{
		return _isInstanceSet;
	}
}
