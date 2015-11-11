/**
 * @author Alastair Parker
 * Copyright Alastair Parker 2011
 */
package main.java.avii.util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author alaspark eases the burden of producing html tables for output
 */
public class AsciiTableBuilder {


	private String description = "No description has been set for this test.";

	static String newLine = System.getProperty("line.separator");

	private int rowCount;
	private int currentRow = 0;
	private int tableLen = 0;

	private boolean empty = true;

	private ArrayList<String> rowNames;
	private HashMap<String, ArrayList<String>> tableData;
	private HashMap<String, Integer> tableSizes;

	private HashMap<String, ArrayList<String>> warnings;

	/**
	 * constructor
	 * 
	 * @param rows
	 */
	public AsciiTableBuilder(ArrayList<String> rows) {
		rowNames = rows;
		this.rowCount = rows.size();
		tableData = new HashMap<String, ArrayList<String>>();
		tableSizes = new HashMap<String, Integer>();

		warnings = new HashMap<String, ArrayList<String>>();

		for (String header : rows) {
			ArrayList<String> temp = new ArrayList<String>();
			tableData.put(header, temp);
			tableSizes.put(header, header.length());
		}

	}

	/**
	 * sets the description for this test. Use '\n' for newlines
	 * 
	 * @param input
	 */
	public void description(String input) {
		this.description = input.replace("\n", newLine);
	}

	/**
	 * add a warning message to the output, grouped by categories
	 * 
	 * @param category
	 * @param message
	 */
	public void warn(String category, String message) {
		if (!warnings.containsKey(category)) {
			warnings.put(category, new ArrayList<String>());
		}
		warnings.get(category).add(message);

	}

	

	/**
	 * adds a td element to the table, automatically adds rows as required
	 * 
	 * @param data
	 */
	public void add(String data) {
		empty = false;
		if(data==null)
		{
			data = "";
		}
		String header = rowNames.get(currentRow);
		int len = data.length();
		tableData.get(header).add(data);
		if (tableSizes.get(header) < len) {
			tableSizes.put(header, len);
		}
		currentRow++;

		if (currentRow == rowCount) {
			currentRow = 0;
			tableLen++;
		}

	}

	public String toString() {
		if (empty)
			return "";

		StringBuffer output = new StringBuffer();

		int width = 0;

		output.append( newLine + newLine +newLine + newLine + newLine);
		output.append(">" + this.description);
		output.append(newLine);
		output.append(newLine);

		int width_before_headers = output.length();
		for (String header : rowNames) {
			output.append(pad(header, tableSizes.get(header)));
			output.append(" | ");
		}
		output.setLength(cut(3, output));
		width = output.length() - width_before_headers;
		output.append(newLine);
		for (int i = 0; i < width; i++) {
			output.append("-");
		}
		output.append(newLine);
		for (int i = 0; i < tableLen; i++) {
			for (String header : rowNames) {
				String data = tableData.get(header).get(i);
				output.append(pad(data, tableSizes.get(header)));
				output.append(" | ");
			}
			output.setLength(cut(3, output));
			output.append(newLine);
		}
		output.append(newLine);
		output.append(newLine);
		output.append(newLine);
		for (String category : warnings.keySet()) {
			output.append(category);
			output.append(newLine);
			for (int i = 0; i < category.length(); i++) {
				output.append("-");
			}
			output.append(newLine);
			for (String message : warnings.get(category)) {
				output.append(message);
				output.append(newLine);
			}
			output.append(newLine);
			output.append(newLine);
		}
		return output.toString();

	}

	private int cut(int amt, StringBuffer sb) {
		return sb.length() - amt;
	}

	private String pad(String data, int len) {
		while (data.length() < len) {
			data = data + " ";
		}
		return data;
	}

}
