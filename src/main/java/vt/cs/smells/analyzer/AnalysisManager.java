package vt.cs.smells.analyzer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import vt.cs.smells.analyzer.analysis.BroadCastWorkAroundAnalyzer;
import vt.cs.smells.analyzer.analysis.BroadVarScopeAnalyzer;
import vt.cs.smells.analyzer.analysis.CloneAnalyzer;
import vt.cs.smells.analyzer.analysis.DuplicateValueAnalyzer;
import vt.cs.smells.analyzer.analysis.MasteryAnalyzer;
import vt.cs.smells.analyzer.analysis.ScriptLengthMetricAnalyzer;
import vt.cs.smells.analyzer.analysis.TooLongScriptAnalyzer;
import vt.cs.smells.analyzer.analysis.UncommunicativeNamingVisitor;
import vt.cs.smells.analyzer.analysis.UnnecessaryBroadcastAnalyzer;
import vt.cs.smells.analyzer.analysis.UnreachableAnalysisVisitor;
import vt.cs.smells.analyzer.analysis.UnusedBlockAnalyzer;
import vt.cs.smells.analyzer.analysis.UnusedVariableAnalyzer;
import vt.cs.smells.analyzer.analysis.VisitorBasedAnalyzer;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.parser.ParsingException;
import vt.cs.smells.analyzer.parser.Util;
import vt.cs.smells.analyzer.visitor.AnalysisVisitor;
import vt.cs.smells.analyzer.visitor.Visitor;

public class AnalysisManager {
	JSONParser jsonParser;
	Visitor visitor;
	String input;
	private AnalysisConfigurator config = null;
	private int projectID;
	static Logger logger = Logger.getLogger(AnalysisManager.class);
	public static final String testInput = "C:/Users/Peeratham/workspace/dataset/hundred.json";
	public static final String largeTestInput = "C:/Users/Peeratham/workspace/dataset/sources-0.json";

	public AnalysisManager() {
		jsonParser = new JSONParser();
	}

	public JSONObject analyze(String src) throws ParsingException, AnalysisException {

		ArrayList<JSONObject> smellReports = new ArrayList<JSONObject>();
		ArrayList<JSONObject> metricReports = new ArrayList<JSONObject>();
		if (config == null) {
			config = getDefaultConfig();
		}
		ScratchProject project = null;
		try {
			project = ScratchProject.loadProject(src);
			projectID = project.getProjectID();

			for (Class k : config.listAnalyzers()) {
				Analyzer analyzer = null;
				if (Arrays.asList(k.getInterfaces()).contains(AnalysisVisitor.class)) {
					AnalysisVisitor v = (AnalysisVisitor) k.newInstance();
					analyzer = new VisitorBasedAnalyzer();
					((VisitorBasedAnalyzer) analyzer).addAnalysisVisitor(v);
				} else {
					analyzer = (Analyzer) k.newInstance();
				}

				analyzer.setProject(project);

				try {
					analyzer.analyze();
				} catch (AnalysisException e) {
					logger.error(k + " fail to analyze project " + projectID);
					throw new AnalysisException("Analysis Error[" + analyzer.getClass() + "]:\n" + e.getMessage());
				}
				Report analysisReport = analyzer.getReport();
				switch (analysisReport.getReportType()) {
				case SMELL:
					smellReports.add(analysisReport.getJSONReport());
					break;
				case METRIC:
					metricReports.add(analysisReport.getJSONReport());
					break;
				default:
					smellReports.add(analysisReport.getJSONReport());
					break;
				}

			}
			JSONObject report = new JSONObject();
			report.put("_id", projectID);
			JSONObject smells = new JSONObject();

			for (JSONObject a : smellReports) {
				smells.put(a.get("name"), a.get("records"));
			}
			report.put("smells", smells);

			JSONObject metrics = new JSONObject();
			for (JSONObject m : metricReports) {
				metrics.put(m.get("name"), m.get("records"));
			}
			// additional
			metrics.put("scriptCount", project.getScriptCount());
			metrics.put("spriteCount", project.getSpriteCount());

			report.put("metrics", metrics);

			return report;

		} catch (ParseException e) {
			logger.error("Fail to read JSONObject of projectID: " + projectID);
			throw new ParsingException(e);
		} catch (ParsingException e) {
			logger.error("Fail to parse Scratch project: " + projectID);
			throw new ParsingException(e);
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error("Fail to instantiate analyzer: " + e);
			throw new AnalysisException(e);
		} catch (Exception e) {
			logger.error("Fail: " + projectID);
			throw new AnalysisException(e);
		}
	}

	public int getProjectID() {
		return projectID;
	}

	private AnalysisConfigurator getDefaultConfig() {
		AnalysisConfigurator defaultConfig = new AnalysisConfigurator();
		try {
			defaultConfig.addAnalysis(MasteryAnalyzer.class.getName());
			defaultConfig.addAnalysis(UnreachableAnalysisVisitor.class.getName());
			defaultConfig.addAnalysis(TooLongScriptAnalyzer.class.getName());
			defaultConfig.addAnalysis(BroadCastWorkAroundAnalyzer.class.getName());
			defaultConfig.addAnalysis(UncommunicativeNamingVisitor.class.getName());
			defaultConfig.addAnalysis(BroadVarScopeAnalyzer.class.getName());
			defaultConfig.addAnalysis(CloneAnalyzer.class.getName());
			defaultConfig.addAnalysis(UnnecessaryBroadcastAnalyzer.class.getName());
			defaultConfig.addAnalysis(UnusedVariableAnalyzer.class.getName());
			defaultConfig.addAnalysis(UnusedBlockAnalyzer.class.getName());
			defaultConfig.addAnalysis(ScriptLengthMetricAnalyzer.class.getName());
			defaultConfig.addAnalysis(DuplicateValueAnalyzer.class.getName());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return defaultConfig;
	}

	private void setConfig(AnalysisConfigurator config) {
		this.config = config;

	}

	public static String analysisReportGenerator(int[] projectIDs) throws Exception {
		AnalysisManager blockAnalyzer = new AnalysisManager();
		StringBuilder sb = new StringBuilder();
		for (int id : projectIDs) {
			String src = Util.retrieveProjectOnline(id);
			JSONObject report = blockAnalyzer.analyze(src);
			String result = report.toJSONString();
			sb.append(id);
			sb.append("\t");
			sb.append(result);
			sb.append("\n");
		}
		System.out.println(sb.toString());
		return sb.toString();
	}

	public static void main(String[] args) throws IOException {
		AnalysisManager blockAnalyzer = new AnalysisManager();
		String src = null;
		int projectID = 104240489;
		try {
			src = Util.retrieveProjectOnline(projectID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			JSONObject result = blockAnalyzer.analyze(src);
			System.out.println(result.toJSONString());
		} catch (AnalysisException e) {
			e.printStackTrace();
		} catch (ParsingException e) {
			e.printStackTrace();
		}
	}

	public static void runSingleAnalysis(String inputFile, String analysisClassURL)
			throws FileNotFoundException, IOException {
		AnalysisManager blockAnalyzer = new AnalysisManager();
		JSONParser jsonParser = new JSONParser();
		AnalysisConfigurator mainConfig = new AnalysisConfigurator();
		
		try {
			
			mainConfig.addAnalysis(analysisClassURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		blockAnalyzer.setConfig(mainConfig);

		FileInputStream is = new FileInputStream(inputFile);
		String[] lines = IOUtils.toString(is).split("\n");
		DescriptiveStatistics stats = new DescriptiveStatistics();
		int found = 0;
		for (String line : lines) {
			Long id = null;
			try {
				JSONObject srcLine = (JSONObject) jsonParser.parse(line.toString());
				id = (Long) srcLine.get("_id");
				String src = srcLine.get("src").toString();
				JSONObject report = blockAnalyzer.analyze(src);
				JSONObject smells = (JSONObject) report.get("smells");
				String smellName = (String) smells.keySet().iterator().next();
				JSONObject record = (JSONObject) smells.get(smellName);
				Integer count = (Integer) record.get("count");
				stats.addValue(count);
				
				
				if(count>0){
					found +=1;
					logger.info(id);;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		DecimalFormat df2 = new DecimalFormat(".##");
		System.out.println("Found:"+found+" out of "+stats.getN()+" or "+df2.format((double)found/stats.getN()*100)+"%");
		System.out.println("Average:"+df2.format(stats.getMean())+" per project");
	}
	
	public static void runAnalysis(String analysisClassName) throws FileNotFoundException, IOException {
		runSingleAnalysis(testInput, analysisClassName);		
	}

}
