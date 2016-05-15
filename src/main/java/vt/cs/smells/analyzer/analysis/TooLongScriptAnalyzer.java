package vt.cs.smells.analyzer.analysis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.AnalysisManager;
import vt.cs.smells.analyzer.Analyzer;
import vt.cs.smells.analyzer.ListAnalysisReport;
import vt.cs.smells.analyzer.Report;
import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.nodes.Scriptable;
import vt.cs.smells.analyzer.visitor.All;
import vt.cs.smells.analyzer.visitor.Identity;
import vt.cs.smells.analyzer.visitor.Sequence;
import vt.cs.smells.analyzer.visitor.VisitFailure;
import vt.cs.smells.analyzer.visitor.Visitor;

public class TooLongScriptAnalyzer extends Analyzer {
	private ListAnalysisReport report = new ListAnalysisReport();
	
	class TopDownNestedNonConditional extends Sequence {
		public TopDownNestedNonConditional(Visitor v) {
			super(v, null);
			then = new AllNestedNonConditional(this);
		}
	}

	class AllNestedNonConditional extends All {
		public AllNestedNonConditional(Visitor v) {
			super(v);
		}

		@Override
		public void visitBlock(Block block) throws VisitFailure {
			for (Object arg : block.getArgs()) {
				if (arg instanceof ArrayList) {
					for (Block b : (ArrayList<Block>) arg) {
						b.accept(v);
					}
				}
			}

		}
	}

	class CountScriptLengthVisitor extends Identity {
		int blockCounts;

		CountScriptLengthVisitor() {
			blockCounts = 0;
		}

		@Override
		public void visitBlock(Block block) throws VisitFailure {
			blockCounts++;
		}

		public int getCount() {
			return blockCounts;
		}
	}

	@Override
	public void analyze() throws AnalysisException {
		try {
			for (String name : project.getAllScriptables().keySet()) {
				Scriptable sc = project.getScriptable(name);
				for(Script s: sc.getScripts()){
					CountScriptLengthVisitor counter = new CountScriptLengthVisitor();
					TopDownNestedNonConditional visitor = new TopDownNestedNonConditional(counter);
					s.accept(visitor);
					if (counter.getCount() > 10) {
						report.addRecord(s.getPath());
					}
				}
				
			}
			
		} catch (VisitFailure e) {
			e.printStackTrace();
		}
	}

	@Override
	public Report getReport() {
		report.setTitle("Too Long Script");
		return report;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException{
		AnalysisManager.runAnalysis(TooLongScriptAnalyzer.class.getName());
	}
}
