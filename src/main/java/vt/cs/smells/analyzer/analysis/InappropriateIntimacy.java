package vt.cs.smells.analyzer.analysis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.AnalysisManager;
import vt.cs.smells.analyzer.Analyzer;
import vt.cs.smells.analyzer.ListAnalysisReport;
import vt.cs.smells.analyzer.Report;
import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.parser.ParsingException;
import vt.cs.smells.analyzer.parser.Util;
import vt.cs.smells.select.Collector;
import vt.cs.smells.select.Evaluator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;

public class InappropriateIntimacy extends Analyzer {
	Map<Pair<String, String>, List<Block>> dict = new HashMap<>();
	Map<String, Integer> intimacyCount = new HashMap<>();
	int totalIntimacy = 0;
	ListAnalysisReport report = new ListAnalysisReport("InappropriateIntimacy",
			"II");

	@Override
	public void analyze() throws AnalysisException {
		for (String scriptableName : project.getAllScriptables().keySet()) {
			ArrayList<Block> getterBlocks = Collector.collect(
					new Evaluator.BlockCommand("getAttribute:of:"),
					project.getScriptable(scriptableName));
			int count = 0;
			for (Block getter : getterBlocks) {
				Object attributeName = getter.getArgs(0);
				String otherSprite;
				try {
					otherSprite = getter.getArgs(1).toString();
				} catch (Exception e) {
					throw new AnalysisException("Invalid attribute access:" + e);
				}

				if (!getter.getArgs(1).equals(scriptableName)) {
					if (!dict.containsKey(Pair.of(scriptableName, otherSprite))) {
						dict.put(Pair.of(scriptableName, otherSprite),
								new ArrayList<>());
					}
					dict.get(Pair.of(scriptableName, otherSprite)).add(getter);
					count++;
				}

			}

			if (count > 0) {
				intimacyCount.put(scriptableName, count);
				JSONObject record = new JSONObject();
				record.put(scriptableName, count);
				report.addRecord(record);
			}

		}

		for (int intimacyVal : intimacyCount.values()) {
			totalIntimacy += intimacyVal;
		}
	}

	@Override
	public Report getReport() {
		JSONObject conciseReport = new JSONObject();
		conciseReport.put("count", totalIntimacy);
		report.setConciseJSONReport(conciseReport);
		return report;
	}

	public static void main(String[] args) throws IOException, ParseException,
			ParsingException, AnalysisException {
//		String projectSrc = Util.retrieveProjectOnline(13874988);
//		ScratchProject project = ScratchProject.loadProject(projectSrc);
//		InappropriateIntimacy analyzer = new InappropriateIntimacy();
//		analyzer.setProject(project);
//		analyzer.analyze();
//		System.out.println(analyzer.getReport().getConciseJSONReport());
//		System.out.println(analyzer.getReport().getJSONReport());

//		 AnalysisManager.runAnalysis(InappropriateIntimacy.class.getName(), AnalysisManager.largeTestInput);
		String csvResult = AnalysisManager.runAnalysis2(new InappropriateIntimacy(), 1);
		FileUtils.writeStringToFile(new File(InappropriateIntimacy.class
				+ ".csv"), csvResult);
	
	}
}
