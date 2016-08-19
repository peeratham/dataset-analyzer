package vt.cs.smells.analyzer.analysis;

import java.util.List;

import org.json.simple.JSONObject;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.Analyzer;
import vt.cs.smells.analyzer.DictAnalysisReport;
import vt.cs.smells.analyzer.Report;
import vt.cs.smells.analyzer.Report.ReportType;
import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.Scriptable;
import vt.cs.smells.select.Collector;
import vt.cs.smells.select.Evaluator;

public class ProgrammingElementMetricAnalyzer extends Analyzer{
	private static final String name = "ProgrammingElementMetric";
	private static final String abbr = "PE";
	DictAnalysisReport report = new DictAnalysisReport(name, abbr);
	
	public int totalVariableCount = 0;
	public int totalCustomBlock = 0;
	public int totalComment = 0;

	@Override
	public void analyze() throws AnalysisException {
		//var count
		for(String scriptableName: project.getAllScriptables().keySet()){
			Scriptable sprite = project.getScriptable(scriptableName);
			totalVariableCount +=sprite.getAllVariables().size();
		}
		//custom block count
		for(String scriptableName: project.getAllScriptables().keySet()){
			Scriptable sprite = project.getScriptable(scriptableName);
			List<Block> allBlocks = Collector.collect(new Evaluator.BlockCommand("procDef"), sprite);
			totalCustomBlock += allBlocks.size();
		}
		//comment count
		for(String scriptableName: project.getAllScriptables().keySet()){
			Scriptable sprite = project.getScriptable(scriptableName);
			int scriptComment = sprite.getScriptComments().size();
			totalComment += scriptComment;
		}
		
		JSONObject record = new JSONObject();
		record.put("varCount", totalVariableCount);
		record.put("customBlockCount", totalCustomBlock);
		record.put("scriptCommentCount", totalComment);
		report.addRecord(record);
	}

	@Override
	public Report getReport() {
		report.setReportType(ReportType.METRIC);
		JSONObject conciseReport = new JSONObject();
		conciseReport.put("varCount", totalVariableCount);
		conciseReport.put("customBlockCount", totalCustomBlock);
		conciseReport.put("commentCount", totalComment);
		report.setConciseJSONReport(conciseReport);
		return report;
	}

}
