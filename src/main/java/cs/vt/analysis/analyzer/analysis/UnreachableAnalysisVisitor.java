package cs.vt.analysis.analyzer.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;
import cs.vt.analysis.analyzer.visitor.PathKeeper;
import cs.vt.analysis.analyzer.visitor.PathRecorder;
import cs.vt.analysis.analyzer.visitor.Sequence;
import cs.vt.analysis.analyzer.visitor.Stop;
import cs.vt.analysis.analyzer.visitor.TopDown;
import cs.vt.analysis.analyzer.visitor.VisitFailure;
import cs.vt.analysis.analyzer.visitor.Visitor;

public class UnreachableAnalysisVisitor extends Sequence implements AnalysisVisitor {
	private List<String> messages = new ArrayList<String>();
	private Stack<String> path = new Stack<String>();
	private AnalysisReport report = new AnalysisReport();
	
	public UnreachableAnalysisVisitor(){
		super(null,null);
		class BroadCastCollector implements Visitor{
			public void visitProject(ScratchProject scratchProject)
					throws VisitFailure {}

			public void visitScriptable(Scriptable scriptable)
					throws VisitFailure {}

			public void visitScript(Script script) throws VisitFailure {}
			
			public void visitBlock(Block block) throws VisitFailure {
				if(block.getCommand().contains("broadcast")){
					List<String> args = (List<String>) block.getArgs();
					messages.add(args.get(0));
				}
			}
		}
		
		
	
		class UnreachableScriptCollector extends Stop implements PathKeeper {
			Stack<String> path;
			
			public UnreachableScriptCollector(){}
			
			public void registerPathListener(Stack<String> path){
				this.path = path;
			}

			@Override
			public void visitBlock(Block block) throws VisitFailure{
				if(block.getCommand().contains("whenIReceive")){
					List<String> args = (List<String>) block.getArgs();
					if (!messages.contains(args.get(0))){
						
						String fullPath = "";
						for(String elm: path) { 
							fullPath +=elm+"/";
						}
						
						report.addRecord(fullPath);
					}
				}
				throw new VisitFailure();
				
			}

			public Stack<String> getPath() {
				return path;
			}
			
		}
		
		first = new TopDown(new BroadCastCollector());
		PathRecorder pathRecorder = new PathRecorder(new UnreachableScriptCollector());
		then = pathRecorder;
		this.path = pathRecorder.getPath();
	}

	public AnalysisReport getReport() {
		report.setTitle("UnreachableCode");
		report.addSummary("count", report.result.size());
		return report;
	}
}
