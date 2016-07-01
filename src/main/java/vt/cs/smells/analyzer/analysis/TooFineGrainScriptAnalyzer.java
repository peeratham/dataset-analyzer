package vt.cs.smells.analyzer.analysis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.AnalysisManager;
import vt.cs.smells.analyzer.Analyzer;
import vt.cs.smells.analyzer.ListAnalysisReport;
import vt.cs.smells.analyzer.Report;
import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.nodes.Scriptable;
import vt.cs.smells.analyzer.parser.ParsingException;
import vt.cs.smells.analyzer.visitor.VisitFailure;
import vt.cs.smells.visual.PropertiesCollector;
import vt.cs.smells.visual.ScriptProperty;

public class TooFineGrainScriptAnalyzer extends Analyzer {
	private ListAnalysisReport report;

	@Override
	public void analyze() throws AnalysisException {
		initialize();
		for (Scriptable s : project.getAllScriptables().values()) {
			HashMap<ScriptProperty, List<Script>> similarScripts = new HashMap<>();
			for (Script sc : s.getScripts()) {
				Block hatBlock = sc.getBlocks().get(0);
				int scriptSize;
				try {
					scriptSize = new ScriptLengthMetricAnalyzer().measureLength(sc);
				} catch (VisitFailure e) {
					continue;
				}
				if (scriptSize > 4) {
					continue;
				}

				if (!hatBlock.getBlockType().getShape().equals("hat")) {
					continue;
				}

				//ignore if no forever and 
				if (sc.containsBlock("doForever").isEmpty()) {
					continue;
				}
				
				//if there's a forever, ignore if there is wait until
				if (!sc.containsBlock("doWaitUntil").isEmpty()){
					continue;
				}

				ScriptProperty sProp = new ScriptProperty(sc);
				if (!similarScripts.containsKey(sProp)) {
					similarScripts.put(sProp, new ArrayList<>());
				}
				similarScripts.get(sProp).add(sc);

			}
			JSONArray groupsJSON = new JSONArray();
			for (ScriptProperty propertyKey : similarScripts.keySet()) {
				List<Script> group = similarScripts.get(propertyKey);
				if (!(group.size() >= 2)) {
					continue;
				}
				JSONObject grpRecord = new JSONObject();
				JSONArray grpJSON = new JSONArray();
				for (Script script : group) {
					grpJSON.add(script.getPath());
				}
				grpRecord.put("group", grpJSON);
				grpRecord.put("shared_vars", propertyKey.getVariables().toString());
				groupsJSON.add(grpRecord);
			}
			if (groupsJSON.isEmpty()) {
				continue;
			}
			JSONObject record = new JSONObject();
			record.put("scriptable", s.getName());
			record.put("groups", groupsJSON);
			report.addRecord(record);
		}

	}

	private void initialize() {
		report = new ListAnalysisReport();
	}

	@Override
	public Report getReport() {
		report.setTitle("UnnecessaryForever");
		return report;
	}

	public static void main(String[] args)
			throws FileNotFoundException, IOException, AnalysisException, ParseException, ParsingException {
		 String csvResult = AnalysisManager.runAnalysis2(new
		 TooFineGrainScriptAnalyzer(), 1);
		// FileUtils.writeStringToFile(new
		// File(UnnecessaryForeverAnalyzer.class+".csv"), csvResult);
//		Report result = AnalysisManager.runSingleAnalysis(18801413, new ExtremeEventBasedScriptAnalyzer());
		// System.out.println(result.getJSONReport().toJSONString());
		// Document doc = Document.parse(result.getJSONReport().toJSONString());
		// System.out.println(result.getJSONReport());
	}

}
