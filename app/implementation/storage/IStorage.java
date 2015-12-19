package implementation.storage;

import main.java.avii.editor.command.history.EditorCommandHistory;
import main.java.avii.editor.service.IUUIDIdentifier;

public interface IStorage {
	boolean doesUUIDExist(IUUIDIdentifier uuid);
	EditorCommandHistory getHistory(IUUIDIdentifier uuid) throws StorageException;
	void saveHistory(EditorCommandHistory history, IUUIDIdentifier uuid);
}
