package implementation.storage;

import java.util.HashMap;

import main.java.avii.editor.command.history.EditorCommandHistory;
import main.java.avii.editor.service.IUUIDIdentifier;

public class InMemoryStorage implements IStorage {

	HashMap<String,EditorCommandHistory> _historyMap = new HashMap<String,EditorCommandHistory>();
	
	public boolean doesUUIDExist(IUUIDIdentifier uuid) {
		return _historyMap.containsKey(uuid.getUUIDString());
	}

	public EditorCommandHistory getHistory(IUUIDIdentifier uuid) {
		EditorCommandHistory retrievedHistory = _historyMap.get(uuid.getUUIDString());
		return retrievedHistory;
	}

	public void saveHistory(EditorCommandHistory history, IUUIDIdentifier uuid) {
		_historyMap.put(uuid.getUUIDString(), history);
	}
	
}
