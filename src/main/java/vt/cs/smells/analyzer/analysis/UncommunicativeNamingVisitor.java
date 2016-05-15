package vt.cs.smells.analyzer.analysis;

import vt.cs.smells.analyzer.ListAnalysisReport;
import vt.cs.smells.analyzer.Report;
import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.nodes.Scriptable;
import vt.cs.smells.analyzer.visitor.AnalysisVisitor;
import vt.cs.smells.analyzer.visitor.One;
import vt.cs.smells.analyzer.visitor.TopDown;
import vt.cs.smells.analyzer.visitor.VisitFailure;
import vt.cs.smells.analyzer.visitor.Visitor;

public class UncommunicativeNamingVisitor extends One implements AnalysisVisitor {
	private ListAnalysisReport report = new ListAnalysisReport();
	
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

	public Report getReport() {
		report.setTitle("Uncommunicative Naming");
		return report;
	}

	

}
