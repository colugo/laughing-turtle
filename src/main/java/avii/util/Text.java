package main.java.avii.util;

public class Text {

	public static String replaceStringWhenNotWithinQuotes(String source, String lookFor, String replaceWith) {
		String result = "";
		boolean quotesOpen = false;
		for (int i = 0; i < source.length(); i++) {
			char currentChar = source.charAt(i);
			String subString = source.substring(i);

			if (source.charAt(i) == '"') {
				quotesOpen = !quotesOpen;
				result += currentChar;
			} else if (!quotesOpen) {
				if (subString.startsWith(lookFor)) {
					result += replaceWith;
					i += lookFor.length() - 1;
				} else {
					result += currentChar;
				}
			} else {
				result += currentChar;
			}
		}
		return result;
	}

	public static boolean isStringQuoted(String input) {
		if (input.startsWith("\"") && input.endsWith("\"")) {
			return true;
		}
		return false;
	}

	public static String quote(String input) {
		if (isStringQuoted(input)) {
			return input;
		}
		return '"' + input + '"';
	}

	public static String removeTrailingSemiColon(String input) {
		String trimInput = input.trim();
		if (trimInput.lastIndexOf(';') == trimInput.length() - 1) {
			return input.substring(0, trimInput.length() - 1);
		}
		return input;
	}

	public static int countOccurancesOfCharacterInString(char c, String s) {
		int count = 0;
		for (char temp : s.toCharArray()) {
			if (temp == c) {
				count++;
			}
		}
		return count;
	}

	public static String removeOuterQuotes(String theString) {
		String origString = theString + "";
		theString = theString.trim();
		boolean isFirstCharacterAQuote = theString.charAt(0) == '"';
		boolean isLastCharacterAQuote = theString.charAt(theString.length() - 1) == '"';

		if (isFirstCharacterAQuote && isLastCharacterAQuote) {
			return theString.substring(1, theString.length() - 1);
		}
		return origString;
	}

	public static String wrapNegativeNumbersInBrackets(String input) {
		StringBuffer output = new StringBuffer();
		boolean inQuotes = false;
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c == '"') {
				inQuotes = !inQuotes;
			}

			if (inQuotes) {
				output.append(c);
			} else {
				if (c == '-') {
					boolean okToWrap = false;
					if(i == input.length()-1)
					{
						output.append(c);
						continue;
					}
					if (i == 0) {
						okToWrap = true;
					} else {
						char previousChar = input.charAt(i - 1);
						char nextChar = input.charAt(i + 1);
						if ((previousChar == ' ' | Character.isLetter(previousChar) | previousChar == '*' | previousChar == '-' | previousChar == '+' | previousChar == '\\' | previousChar == '/' ) && Character.isDigit(nextChar)) {
							okToWrap = true;
						}
					}

					if (okToWrap) {
						String value = Text.getNumberFromString(input.substring(i));
						i += value.length() - 1;
						output.append('(');
						output.append(value);
						output.append(')');
					}
					else
					{
						output.append(c);
					}
				} else {
					output.append(c);
				}
			}
		}
		return output.toString();
	}

	public static String getNumberFromString(String input) {
		String output = "";
		for (char c : input.toCharArray()) {
			if (Character.isDigit(c) | c == '.' | c == '-') {
				output += c;
			} else {
				return output;
			}
		}
		return output;
	}

}
