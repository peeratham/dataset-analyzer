package vt.cs.smells.analyzer.analysis;

import java.util.List;

import org.json.simple.JSONObject;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.Analyzer;
import vt.cs.smells.analyzer.ListAnalysisReport;
import vt.cs.smells.analyzer.Report;
import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.select.Collector;
import vt.cs.smells.select.Evaluator;

public class UncommunicativeNamingAnalyzer extends Analyzer {
	private static final String name = "UncommunicativeName";
	private static final String abbr = "UN";
	ListAnalysisReport report = new ListAnalysisReport(name, abbr);
	int count = 0;

	@Override
	public void analyze() throws AnalysisException {
		for (String scriptableName : project.getAllScriptables().keySet()) {
			if (scriptableName.contains("Sprite")) {
				report.addRecord(scriptableName);
				count++;
			}

			List<Block> broadCastBlocks = Collector.collect(
					new Evaluator.BlockCommand("broadcast:"), project.getScriptable(scriptableName));
			for (Block block : broadCastBlocks) {
				String messageName = block.getArgs().get(0).toString();
				if (messageName.contains("message")) {
					report.addRecord(messageName);
					count++;
				}
			}
		}
	}

	@Override
	public Report getReport() {
		JSONObject conciseReport = new JSONObject();
		conciseReport.put("count", count);
		report.setConciseJSONReport(conciseReport);
		return report;
	}

}
