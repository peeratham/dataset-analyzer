package cs.vt.analysis.analyzer.analysis;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import cs.vt.analysis.analyzer.analysis.Report.ReportType;
import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.Scriptable;
import cs.vt.analysis.analyzer.visitor.All;
import cs.vt.analysis.analyzer.visitor.Identity;
import cs.vt.analysis.analyzer.visitor.Sequence;
import cs.vt.analysis.analyzer.visitor.VisitFailure;
import cs.vt.analysis.analyzer.visitor.Visitor;

public class ScriptLengthMetricAnalyzer extends Analyzer {
	DictAnalysisReport report = new DictAnalysisReport();
	DescriptiveStatistics stats = new DescriptiveStatistics();
	HashMap<String, Double> record = new HashMap<String, Double>();

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
				CountScriptLengthVisitor counter = new CountScriptLengthVisitor();
				TopDownNestedNonConditional visitor = new TopDownNestedNonConditional(counter);
				sc.accept(visitor);
				if (counter.getCount() > 0) {
					stats.addValue(counter.getCount());
				}
			}
			record.put("mean", stats.getMean());
			record.put("max", stats.getMax());
			record.put("min", stats.getMin());
			record.put("std", stats.getStandardDeviation());
		} catch (VisitFailure e) {
			e.printStackTrace();
		}
	}

	@Override
	public Report getReport() {
		report.setTitle("Script Length");
		report.setReportType(ReportType.METRIC);
		report.addRecord(record);
		return report;
	}

}
