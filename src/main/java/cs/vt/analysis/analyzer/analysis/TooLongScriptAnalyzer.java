package cs.vt.analysis.analyzer.analysis;

import java.util.ArrayList;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;
import cs.vt.analysis.analyzer.visitor.All;
import cs.vt.analysis.analyzer.visitor.Identity;
import cs.vt.analysis.analyzer.visitor.Sequence;
import cs.vt.analysis.analyzer.visitor.VisitFailure;
import cs.vt.analysis.analyzer.visitor.Visitor;

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
}
