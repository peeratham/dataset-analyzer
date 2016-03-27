package cs.vt.analysis.analyzer.analysis;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;
import cs.vt.analysis.analyzer.visitor.AnalysisVisitor;
import cs.vt.analysis.analyzer.visitor.Sequence;
import cs.vt.analysis.analyzer.visitor.TopDown;
import cs.vt.analysis.analyzer.visitor.VisitFailure;
import cs.vt.analysis.analyzer.visitor.Visitor;

public class UnreachableAnalysisVisitor extends Sequence implements AnalysisVisitor {
	private Set<String> messages = new HashSet<String>();
	private Stack<String> path = new Stack<String>();
	private ListAnalysisReport report = new ListAnalysisReport();
	
	public UnreachableAnalysisVisitor(){
		super(null,null);
		class BroadCastCollector implements Visitor{
			public void visitProject(ScratchProject scratchProject)
					throws VisitFailure {}

			public void visitScriptable(Scriptable scriptable)
					throws VisitFailure {
			}

			public void visitScript(Script script) throws VisitFailure {
			}
			
			public void visitBlock(Block block) throws VisitFailure {
				if(block.getCommand().contains("roadcast")){
					List<Object> args =  block.getArgs();
					messages.add(args.get(0).toString());
				}
			}
		}

		class UnreachableScriptDetector implements Visitor {

			public void visitProject(ScratchProject scratchProject) throws VisitFailure {}

			public void visitScriptable(Scriptable scriptable) throws VisitFailure {
	
			}

			public void visitScript(Script script) throws VisitFailure {}

			public void visitBlock(Block block) throws VisitFailure {
				if(block.getCommand().contains("whenIReceive")){
					List<Object> args =  block.getArgs();
					if (!messages.contains(args.get(0))){
						report.addRecord(block.getBlockPath().toString());
					}
				}
			}
		}
		first = new TopDown(new BroadCastCollector());
		then = new TopDown(new UnreachableScriptDetector());
	}
	
	public Report getReport() {
		report.setTitle("Unreachable Code");
		return report;
	}
}
