package cs.vt.analysis.analyzer.visitor;

import java.util.Stack;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;

public class PathRecorder extends Sequence implements PathKeeper{
	private Stack<String> st = new Stack<String>();
	public PathRecorder(Visitor emitter) {
		super(null, null);
				
		class PathRecorderEnter implements Visitor{

			public void visitProject(ScratchProject scratchProject)
					throws VisitFailure {} 
			public void visitScriptable(Scriptable scriptable)
					throws VisitFailure {
				st.push(scriptable.getName());
			}
			public void visitScript(Script script) throws VisitFailure {
				st.push("Script@pos("+script.getPosition()[0]+","+script.getPosition()[1]+")");
			}
			public void visitBlock(Block block) throws VisitFailure {
				st.push(block.getCommand()+":"+block.getBlockSpec().getSpec()+":"+block.getArgs());
			}
		}
		
		class PathRecorderExit implements Visitor {

			public void visitProject(ScratchProject scratchProject)
					throws VisitFailure {}

			public void visitScriptable(Scriptable scriptable)
					throws VisitFailure { st.pop();}

			public void visitScript(Script script) throws VisitFailure { st.pop();}

			public void visitBlock(Block block) throws VisitFailure { st.pop();}
			
		}
		
		((PathKeeper)emitter).setPath(st);
		first = new Sequence(new PathRecorderEnter(), new Choice(emitter, new All(this)));
		then = new PathRecorderExit();
	}
	
	public Stack<String> getPath(){
		return st;
	}

	
	public void setPath(Stack<String> path) {
		this.st = path;
		
	}
	
}
