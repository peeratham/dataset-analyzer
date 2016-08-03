package vt.cs.smells.analyzer.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.math3.stat.Frequency;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.json.simple.JSONObject;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.Analyzer;
import vt.cs.smells.analyzer.DictAnalysisReport;
import vt.cs.smells.analyzer.Report;
import vt.cs.smells.analyzer.Report.ReportType;
import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.nodes.Scriptable;
import vt.cs.smells.analyzer.visitor.All;
import vt.cs.smells.analyzer.visitor.Identity;
import vt.cs.smells.analyzer.visitor.Sequence;
import vt.cs.smells.analyzer.visitor.VisitFailure;
import vt.cs.smells.analyzer.visitor.Visitor;

public class ProjectSizeMetricAnalyzer extends Analyzer {
	private static final String name = "ProjectSize";
	private static final String abbr = "PS";

	DictAnalysisReport report = new DictAnalysisReport(name, abbr);
	DescriptiveStatistics stats = new DescriptiveStatistics();
	Frequency freqCount = new Frequency();
	HashSet<Integer> uniqueScriptLengths = new HashSet<Integer>();
	JSONObject record = new JSONObject();
	int scriptableNum = 0;
	int scriptCount = 0;

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
		scriptableNum = project.getAllScriptables().size();
		scriptCount = 0;

		for (String name : project.getAllScriptables().keySet()) {
			Scriptable sc = project.getScriptable(name);
			for (Script s : sc.getScripts()) {
				if (s.getBlocks().get(0).getBlockType().getShape()
						.equals("hat")) {
					scriptCount++;
				}
			}
		}

		try {
			for (String name : project.getAllScriptables().keySet()) {
				Scriptable sc = project.getScriptable(name);
				for (Script s : sc.getScripts()) {
					int length = measureLength(s);
					if (length > 0) {
						stats.addValue(length);
						freqCount.addValue(length);
						uniqueScriptLengths.add(length);
					}
				}

			}
			record.put("mean", stats.getMean());
			record.put("max", stats.getMax());
			record.put("min", stats.getMin());
			record.put("sum", stats.getSum());
			JSONObject dist = new JSONObject();
			for (Integer cnt : uniqueScriptLengths) {
				dist.put(cnt, freqCount.getCount(cnt));
			}
			record.put("dist", dist);
			report.addRecord(record);
		} catch (VisitFailure e) {
			e.printStackTrace();
		}

	}

	public int measureLength(Script s) throws VisitFailure {
		CountScriptLengthVisitor counter = new CountScriptLengthVisitor();
		TopDownNestedNonConditional visitor = new TopDownNestedNonConditional(
				counter);
		s.accept(visitor);
		return counter.getCount();
	}

	@Override
	public Report getReport() {
		report.setReportType(ReportType.METRIC);
		JSONObject conciseReport = new JSONObject();
		conciseReport.put("sprite", scriptableNum);
		conciseReport.put("script", scriptCount);
		conciseReport.put("script_length", stats.getMean());
		conciseReport.put("bloc", stats.getSum());

		report.setConciseJSONReport(conciseReport);

		return report;
	}

}
