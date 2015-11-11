package main.java.avii.editor.metamodel.actionLanguage;


public class ActionLanguageSyntaxHelper {

	public static final String GenericName = "(\\S+)";
	public static final String RelationName = "([^. -]+)";
	public static final String AnyMany = " (ANY|MANY) ";
	public static final String InstanceAttributeRegex = "(?!rcvd_event\\.)(?![0-9])" + GenericName + "\\." + GenericName;
	public static final String RcvdEventParam = "rcvd_event\\." + GenericName;
	public static final String NotAnInstanceAttribute = "(\\w+)";
	public static final String Equals = "\\s?+=\\s?+";
	public static final String LogicExpression = "(.*)";
	public static final String RelationCain = "(.*)";
	public static final String EOL = ";$";
	public static final String ReflexiveRelation = RelationName + ".\"(.*)\"";
	public static final String FailAssertion = "FAIL";
	
	public enum ENUM_ANY_MANY {ANY,MANY};
	
	
			

}
