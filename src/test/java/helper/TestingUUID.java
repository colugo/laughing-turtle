package test.java.helper;

import java.util.UUID;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import main.java.avii.editor.service.IUUIDIdentifier;

@Element
public class TestingUUID implements IUUIDIdentifier {
	@Attribute
	private String _uuid = "";
	
	public String getUUIDString() {
		if(this._uuid == "")
		{
			this._uuid = UUID.randomUUID().toString();
		}
		return this._uuid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_uuid == null) ? 0 : _uuid.hashCode());
		return result;
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
		TestingUUID other = (TestingUUID) obj;
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

