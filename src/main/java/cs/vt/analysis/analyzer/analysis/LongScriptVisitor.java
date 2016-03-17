package cs.vt.analysis.analyzer.analysis;

import cs.vt.analysis.analyzer.analysis.visitors.AnalysisVisitor;
import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;
import cs.vt.analysis.analyzer.visitor.One;
import cs.vt.analysis.analyzer.visitor.TopDown;
import cs.vt.analysis.analyzer.visitor.VisitFailure;
import cs.vt.analysis.analyzer.visitor.Visitor;

public class LongScriptVisitor extends One implements AnalysisVisitor {
	private static final int LONG_SCRIPT_THRESHOLD = 5;
	private AnalysisReport report = new AnalysisReport();
	
	public LongScriptVisitor() {
		super(null);
		
		class LongScriptVisitorHelper implements Visitor {
			public void visitProject(ScratchProject scratchProject) throws VisitFailure {}

			public void visitScriptable(Scriptable scriptable) throws VisitFailure {}

			public void visitScript(Script script) throws VisitFailure {
				if(script.getBlocks().size()>LONG_SCRIPT_THRESHOLD){
					report.addRecord(script.getPath());
				}
			}

			public void visitBlock(Block block) throws VisitFailure {}
		}
		
		visitor = new TopDown(new LongScriptVisitorHelper());
	}

	

	
	public AnalysisReport getReport() {
		report.setTitle("Too Long Script");
		return report;
	}

}
