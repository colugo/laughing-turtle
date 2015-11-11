package main.java.avii.editor.metamodel.actionLanguage.instanceLifespan;

import java.util.ArrayList;

import javax.naming.NameNotFoundException;
import javax.naming.OperationNotSupportedException;

public class InstanceLifespanManager {

	public static String DuplicateDeclareError = "Must end a range before starting a new one";
	private String _instanceName;
	private ArrayList<InstanceLifespanRange> _ranges = new ArrayList<InstanceLifespanRange>();

	public InstanceLifespanManager(String instanceName) {
		this._instanceName = instanceName;
	}

	private InstanceLifespanRange getLastRange() {
		return _ranges.get(_ranges.size() - 1);
	}

	private boolean isLastRangeClosed() {
		if (isRangesEmpty()) {
			return true;
		}
		return getLastRange().hasEndBeenSet();
	}

	private boolean isRangesEmpty() {
		return _ranges.isEmpty();
	}

	public void start(int startLine, String className, Boolean isInstanceSet) throws OperationNotSupportedException {
		if(isInstanceSet)
		{
			// don't care about previous declarations of instance sets
			_ranges.add(new InstanceLifespanRange(className, startLine, isInstanceSet));
			return;
		}
		
		if (!isLastRangeClosed()) {
			throw new OperationNotSupportedException(InstanceLifespanManager.DuplicateDeclareError  + " - " + _instanceName);
		}
		if(getRangeContainingLine(startLine)!=null)
		{
			throw new OperationNotSupportedException(  startLine + " overlaps with range " + getRangeContainingLine(startLine));
		}
		_ranges.add(new InstanceLifespanRange(className, startLine, isInstanceSet));
	}

	public void end(int endLine) throws OperationNotSupportedException, NameNotFoundException, IndexOutOfBoundsException {
		if(getLastRange().hasEndBeenSet()) {
			throw new OperationNotSupportedException("Can not end with a different class name");
		}
		getLastRange().addEndLine(endLine);

	}

	private InstanceLifespanRange getRangeContainingLine(int lineNumber)
	{
		for (InstanceLifespanRange range : _ranges) {
			if (range.isLineWithinRange(lineNumber)) {
				return range;
			}
		}
		return null;
	}
	
	public String getClassNameAtLine(int lineNumber) throws NameNotFoundException {
		InstanceLifespanRange range = getRangeContainingLine(lineNumber);
		if(range != null)
		{
			return range.getClassName();
		}
		throw new NameNotFoundException("Could not determine '" + _instanceName + "' on line : " + lineNumber);
	}

	public String getInstanceName()
	{
		return _instanceName;
	}

	public Boolean isInstanceSetAtLine(int lineNumber) throws NameNotFoundException {
		InstanceLifespanRange range = getRangeContainingLine(lineNumber);
		if(range != null)
		{
			return range.isInstanceSet();
		}
		throw new NameNotFoundException("Could not determine if '" + _instanceName + "' is an instance set on line : " + lineNumber);
	
	}

	public boolean wasStartedOnOrAfterLineNumber(int lineNumber) {
		if(this.isLastRangeClosed())
		{
			return false;
		}
		int lastRangeOpen = this.getLastRange().getStartLine();
		if(lineNumber <= lastRangeOpen)
		{
			return true;
		}
		return false;
	}
}
