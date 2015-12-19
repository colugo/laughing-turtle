package implementation;

import java.util.UUID;

import main.java.avii.editor.service.IUUIDIdentifier;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@Element
public class ConcreteUUIDIdentifier implements IUUIDIdentifier {
	@Attribute
	private String _uuid = "";
	
	public ConcreteUUIDIdentifier(){}
	public ConcreteUUIDIdentifier(String uuid)
	{
		this._uuid = uuid;
	}
	
	public String getUUIDString() {
		if(this._uuid == "")
		{
			this._uuid = UUID.randomUUID().toString();
		}
		return this._uuid;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ConcreteUUIDIdentifier other = (ConcreteUUIDIdentifier) obj;
		if (_uuid == null) {
			if (other._uuid != null) {
				return false;
			}
		} else if (!_uuid.equals(other._uuid)) {
			return false;
		}
		return true;
	}

}

