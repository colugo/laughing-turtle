package main.java.avii.editor.contracts.metamodel.actionLanguage.InstanceBean;

public class InstanceCreateBean {

	private String _instanceName;
	private String _entityClassName;
	private Boolean _isSet = false;	
	private boolean _isEffectiveOnNextLine = false;
	private Boolean _isInstanceSet = false;
	
	public InstanceCreateBean(String instanceName, String entityClass)
	{
		this._instanceName = instanceName;
		this._entityClassName = entityClass;
	}

	public InstanceCreateBean(String instanceName, String entityClass, Boolean isInstanceSet) {
		this._instanceName = instanceName;
		this._entityClassName = entityClass;
		this._isInstanceSet  = isInstanceSet;
	}

	public void itsEffectiveFromTheNextLine()
	{
		_isEffectiveOnNextLine = true;
	}
	
	public boolean isItEffectiveFromTheNextLine()
	{
		return _isEffectiveOnNextLine;
	}
	
	public void itsASet()
	{
		this._isSet = true;
	}
	
	public Boolean isItASet()
	{
		return this._isSet;
	}
	
	public String getInstanceName() {
		return _instanceName;
	}
	public String getEntityClassName() {
		return _entityClassName;
	}
	public Boolean isInstanceSet()
	{
		return this._isInstanceSet;
	}
}
