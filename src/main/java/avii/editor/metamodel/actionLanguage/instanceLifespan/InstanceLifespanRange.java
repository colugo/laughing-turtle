package main.java.avii.editor.metamodel.actionLanguage.instanceLifespan;

public class InstanceLifespanRange {

	private String _className;
	private int _startLineInclusive;
	private int _endLineInclusive = Integer.MAX_VALUE;
	private boolean _hasEndBeenSet = false;
	private Boolean _isInstanceSet;

	public InstanceLifespanRange(String className, int startLine, Boolean isInstanceSet) {
		this._className = className;
		this._startLineInclusive = startLine;
		this._isInstanceSet = isInstanceSet;
	}
	
	public boolean isLineWithinRange(int theLine)
	{
		return isLineLessThenOrEqualToEndLine(theLine) && isLineLargerOrEqualToStart(theLine);
	}
	
	private boolean isLineLargerOrEqualToStart(int theLine)
	{
		return theLine >= _startLineInclusive;
	}
	
	private boolean isLineLessThenOrEqualToEndLine(int theLine)
	{
		return theLine <= _endLineInclusive;
	}
	
	private boolean isLineAllowedToBeEndLine(int theLine)
	{
		return theLine >= _startLineInclusive;
	}

	public Boolean isInstanceSet()
	{
		return this._isInstanceSet;
	}
	
	public String getClassName()
	{
		return _className;
	}
	
	public boolean hasEndBeenSet()
	{
		return _hasEndBeenSet;
	}
	
	public void addEndLine(int endLine) throws IndexOutOfBoundsException {
		if (!isLineAllowedToBeEndLine(endLine)) {
			throw new IndexOutOfBoundsException(endLine
					+ " is not larger then " + _startLineInclusive);
		}
		this._endLineInclusive = endLine;
		this._hasEndBeenSet = true;
	}
	
	public String toString()
	{
		return _className + " [" + _startLineInclusive + " : " + _endLineInclusive + " ]";
	}

	public int getStartLine() {
		return this._startLineInclusive;
	}
}
