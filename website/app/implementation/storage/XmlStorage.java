package implementation.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import play.api.Play;

import main.java.avii.editor.command.history.EditorCommandHistory;
import main.java.avii.editor.command.history.EditorCommandHistorySerializer;
import main.java.avii.editor.service.IUUIDIdentifier;

public class XmlStorage implements IStorage {

	public boolean doesUUIDExist(IUUIDIdentifier uuid) {
		File historyFile = getFileForUUID(uuid);
		return historyFile.exists();
	}

	public EditorCommandHistory getHistory(IUUIDIdentifier uuid) throws StorageException {
		try {
			File historyFile = getFileForUUID(uuid);
			EditorCommandHistory history;
			history = EditorCommandHistorySerializer.deSerialize(readUUIDFile(historyFile));
			history.playAll();
			return history;
		} catch (Exception e) {
			e.printStackTrace();
			throw new StorageException(e.getMessage());
		}
	}

	public void saveHistory(EditorCommandHistory history, IUUIDIdentifier uuid){
		File historyFile = getFileForUUID(uuid);

		try {
			String serialisedInitialHistoryString = EditorCommandHistorySerializer.serialize(history);
			FileWriter fw = new FileWriter(historyFile);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(serialisedInitialHistoryString);
			bw.close();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static File getFileForUUID(IUUIDIdentifier uuid) {
		String basePath = "storage" + File.separatorChar + "domains" + File.separatorChar;
		String filePath = basePath + uuid.getUUIDString() + ".xml";
		File f = Play.getFile(filePath, Play.current());
		return f;
	}

	private static String readUUIDFile(File f) {

		StringBuffer contents = new StringBuffer();
		try {
			FileReader r = new FileReader(f);
			BufferedReader br = new BufferedReader(r);
			String line = br.readLine();
			while (line != null) {
				contents.append(line);
				line = br.readLine();
			}
			br.close();
			r.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contents.toString();
	}

}
