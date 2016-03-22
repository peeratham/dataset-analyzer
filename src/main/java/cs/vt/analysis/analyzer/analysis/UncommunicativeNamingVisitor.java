package cs.vt.analysis.analyzer.analysis;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;
import cs.vt.analysis.analyzer.visitor.AnalysisVisitor;
import cs.vt.analysis.analyzer.visitor.One;
import cs.vt.analysis.analyzer.visitor.TopDown;
import cs.vt.analysis.analyzer.visitor.VisitFailure;
import cs.vt.analysis.analyzer.visitor.Visitor;

public class UncommunicativeNamingVisitor extends One implements AnalysisVisitor {
	private AnalysisReport report = new AnalysisReport();
	
	public UncommunicativeNamingVisitor() {
		super(null);
		
		class UncommunicativeNamingVisitorHelper implements Visitor{

			public void visitProject(ScratchProject scratchProject)
					throws VisitFailure {
				// TODO Auto-generated method stub
				
			}

			public void visitScriptable(Scriptable scriptable)
					throws VisitFailure {
				if(scriptable.getName().contains("Sprite")){
					report.addRecord(scriptable.getName());
				}
			}

			public void visitScript(Script script) throws VisitFailure {
			}

			public void visitBlock(Block block) throws VisitFailure {
				if(block.getCommand().toLowerCase().contains("broadcast")){
					String messageName = block.getArgs().get(0).toString();
					if(messageName.contains("message")){
						report.addRecord(messageName);
					}
				}
			}
			
		}
		
		visitor = new TopDown(new UncommunicativeNamingVisitorHelper());
	}

	public AnalysisReport getReport() {
		report.setTitle("Uncommunicative Naming");
		return report;
	}

	

}
