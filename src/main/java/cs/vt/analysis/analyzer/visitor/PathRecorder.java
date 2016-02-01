package cs.vt.analysis.analyzer.visitor;

import java.util.Stack;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;

public class PathRecorder extends Sequence implements PathKeeper{
	private Stack<String> pathRecord = new Stack<String>();
	private Visitor checker = null;
	public PathRecorder(Visitor checker) {
		super(null, null);
		this.checker = checker;
				
		class PathRecorderEnter implements Visitor{

			public void visitProject(ScratchProject scratchProject)
					throws VisitFailure {} 
			public void visitScriptable(Scriptable scriptable)
					throws VisitFailure {
				pathRecord.push(scriptable.getName());
			}
			public void visitScript(Script script) throws VisitFailure {
				pathRecord.push("Script@pos("+script.getPosition()[0]+","+script.getPosition()[1]+")");
			}
			public void visitBlock(Block block) throws VisitFailure {
				pathRecord.push(block.toString());
			}
		}
		
		class PathRecorderExit implements Visitor {

			public void visitProject(ScratchProject scratchProject)
					throws VisitFailure {}

			public void visitScriptable(Scriptable scriptable)
					throws VisitFailure { pathRecord.pop();}

			public void visitScript(Script script) throws VisitFailure { pathRecord.pop();}

			public void visitBlock(Block block) throws VisitFailure { pathRecord.pop();}
			
		}
		
		((PathKeeper)this.checker).registerPathListener(pathRecord);
		first = new Sequence(new PathRecorderEnter(), new Choice(this.checker, new All(this)));
		then = new PathRecorderExit();
	}
	
	public Stack<String> getPath(){
		return pathRecord;
	}

	
	public void registerPathListener(Stack<String> path) {
		this.pathRecord = path;
		
	}
	
}
