package main.java.avii.editor.metamodel.actionLanguage.token;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.java.avii.editor.contracts.metamodel.actionLanguage.token.IActionLanguageToken;
import main.java.avii.editor.contracts.metamodel.entities.datatypes.IEntityDatatype;
import main.java.avii.editor.contracts.metamodel.entities.helpers.datatypes.CouldNotDetermineDatatypeFromValueException;
import main.java.avii.editor.metamodel.actionLanguage.ActionLanguageSyntaxHelper;
import main.java.avii.editor.metamodel.entities.datatypes.AvailableDatatypes;

public class ActionLanguageTokenIdentifier {

	
	public static IActionLanguageToken IdentifyToken(String tokenValue) {
		if(isTokenLiteral(tokenValue))
		{
			return createTokenLiteral(tokenValue);
		}
		if(isTokenAttribute(tokenValue))
		{
			return createTokenAttribute(tokenValue);
		}
		if(isRcvdEventAttribute(tokenValue))
		{
			return createTokenRcvd(tokenValue);
		}
		if(isIgnoreToken(tokenValue)){
			return createIgnoreToken(tokenValue);
		}
		return createTokenTemp(tokenValue);
	}
	
	private static IActionLanguageToken createIgnoreToken(String tokenValue) {
		return new ActionLanguageTokenIgnore(tokenValue);
	}

	@SuppressWarnings("serial")
	private static ArrayList<String> ignoreStrings = new ArrayList<String>()
	{{
		add("AND");
		add("OR");
		add("+");
		add("-");
		add("*");
		add("\\");
		add("/");
		add("=");
		add("==");
		add("<");
		add(">");
		add("<=");
		add(">=");
		add("(");
		add(")");
	}};
	
	private static boolean isIgnoreToken(String tokenValue) {
		return ignoreStrings.contains(tokenValue);
	}

	private static IActionLanguageToken createTokenRcvd(String tokenValue) {
		String paramName = tokenValue.replace("rcvd_event.","");
		return new ActionLanguageTokenRcvdEvent(paramName);
	}

	private static IActionLanguageToken createTokenTemp(String tokenValue)
	{
		return new ActionLanguageTokenTemp(tokenValue);
	}
	
	private static boolean isTokenAttribute(String tokenValue)
	{
		return tokenValue.matches(ActionLanguageSyntaxHelper.InstanceAttributeRegex);
	}
	
	private static boolean isRcvdEventAttribute(String tokenValue)
	{
		return tokenValue.matches(ActionLanguageSyntaxHelper.RcvdEventParam);
	}
	
	private static IActionLanguageToken createTokenAttribute(String tokenValue)
	{
		Pattern p = Pattern.compile(ActionLanguageSyntaxHelper.InstanceAttributeRegex);
		Matcher m = p.matcher(tokenValue);
		m.find();
		String theInstanceName = m.group(1);
		String theInstanceAttribute = m.group(2);
		ActionLanguageTokenAttribute attributeToken = new ActionLanguageTokenAttribute(theInstanceName, theInstanceAttribute);
		return attributeToken;
	}
	
	private static IActionLanguageToken createTokenLiteral(String tokenValue)
	{
		IEntityDatatype theDatatype = getDatatypeFromLiteralString(tokenValue);
		IActionLanguageToken token = new ActionLanguageTokenLiteral(theDatatype, tokenValue);
		return token;
	}
	
	private static boolean isTokenLiteral(String tokenValue)
	{
		return getDatatypeFromLiteralString(tokenValue) != null;
	}

	private static IEntityDatatype getDatatypeFromLiteralString(String tokenValue) {
		try {
			IEntityDatatype theDatatype = AvailableDatatypes.getDatatypeForInput(tokenValue);
			return theDatatype;
		} catch (CouldNotDetermineDatatypeFromValueException e) {
			return null;
		}
	}
}
