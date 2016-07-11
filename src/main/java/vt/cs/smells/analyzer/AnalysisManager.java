package vt.cs.smells.analyzer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import vt.cs.smells.analyzer.analysis.BroadCastWorkAroundAnalyzer;
import vt.cs.smells.analyzer.analysis.BroadVarScopeAnalyzer;
import vt.cs.smells.analyzer.analysis.DuplicateCodeAnalyzer;
import vt.cs.smells.analyzer.analysis.DuplicateValueAnalyzer;
import vt.cs.smells.analyzer.analysis.HardCodedMediaSequenceAnalyzer;
import vt.cs.smells.analyzer.analysis.MasteryAnalyzer;
import vt.cs.smells.analyzer.analysis.ScriptLengthMetricAnalyzer;
import vt.cs.smells.analyzer.analysis.TooFineGrainScriptAnalyzer;
import vt.cs.smells.analyzer.analysis.TooLongScriptAnalyzer;
import vt.cs.smells.analyzer.analysis.UncommunicativeNamingAnalyzer;
import vt.cs.smells.analyzer.analysis.UnnecessaryBroadcastAnalyzer;
import vt.cs.smells.analyzer.analysis.UnreachableScriptAnalyzer;
import vt.cs.smells.analyzer.analysis.UnusedCustomBlockAnalyzer;
import vt.cs.smells.analyzer.analysis.UnusedVariableAnalyzer;
import vt.cs.smells.analyzer.analysis.VisitorBasedAnalyzer;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.parser.ParsingException;
import vt.cs.smells.analyzer.parser.Util;
import vt.cs.smells.analyzer.visitor.AnalysisVisitor;
import vt.cs.smells.analyzer.visitor.Visitor;
import vt.cs.smells.crawler.AnalysisDBManager;

public class AnalysisManager {
	JSONParser jsonParser;
	Visitor visitor;
	String input;
	private AnalysisConfigurator config = null;
	private int projectID;
	static Logger logger = Logger.getLogger(AnalysisManager.class);
	public static final String smallTestInput = "C:/Users/Peeratham/workspace/dataset/hundred.json";
	public static final String largeTestInput = "C:/Users/Peeratham/workspace/dataset/sources-0.json";
	private ArrayList<Report> reports;

	public AnalysisManager() {
		jsonParser = new JSONParser();
	}

	public ArrayList<Report> analyze(String src) throws ParsingException,
			AnalysisException {
		reports = new ArrayList<>();
		if (config == null) {
			config = getDefaultConfig();
		}
		ScratchProject project = null;
		try {
			project = ScratchProject.loadProject(src);
			projectID = project.getProjectID();

			for (Class k : config.listAnalyzers()) {
				Analyzer analyzer = null;
				if (Arrays.asList(k.getInterfaces()).contains(
						AnalysisVisitor.class)) {
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
					throw new AnalysisException("Analysis Error["
							+ analyzer.getClass() + "]:\n" + e.getMessage());
				}
				reports.add(analyzer.getReport());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reports;
	}

	public JSONObject getConciseJSONReports() {
		boolean concise = true;
		ArrayList<JSONObject> smellReports = new ArrayList<JSONObject>();
		ArrayList<JSONObject> metricReports = new ArrayList<JSONObject>();
		for (Report analysisReport : reports) {
			switch (analysisReport.getReportType()) {
			case SMELL:
				smellReports.add(analysisReport.getJSONReport(concise));
				break;
			case METRIC:
				metricReports.add(analysisReport.getJSONReport(concise));
				break;
			default:
				smellReports.add(analysisReport.getJSONReport(concise));
				break;
			}
		}

		JSONObject report = new JSONObject();
		report.put("_id", projectID);
		JSONObject smells = new JSONObject();

		for (JSONObject smellReport : smellReports) {
			smells.put(smellReport.get("name"), smellReport.get("records"));
		}
		report.put("smells", smells);

		JSONObject metrics = new JSONObject();
		for (JSONObject metricReport : metricReports) {
			metrics.put(metricReport.get("name"), metricReport.get("records"));
		}
		report.put("metrics", metrics);

		return report;
	}
	
	public JSONObject getFullJSONReport(){
		boolean concise = false;
		ArrayList<JSONObject> smellReports = new ArrayList<JSONObject>();
		ArrayList<JSONObject> metricReports = new ArrayList<JSONObject>();
		for (Report analysisReport : reports) {
			switch (analysisReport.getReportType()) {
			case SMELL:
				smellReports.add(analysisReport.getJSONReport(concise));
				break;
			case METRIC:
				metricReports.add(analysisReport.getJSONReport(concise));
				break;
			default:
				smellReports.add(analysisReport.getJSONReport(concise));
				break;
			}
		}

		JSONObject report = new JSONObject();
		report.put("_id", projectID);
		JSONObject smells = new JSONObject();

		for (JSONObject smellReport : smellReports) {
			smells.put(smellReport.get("name"), smellReport.get("records"));
		}
		report.put("smells", smells);

		JSONObject metrics = new JSONObject();
		for (JSONObject metricReport : metricReports) {
			metrics.put(metricReport.get("name"), metricReport.get("records"));
		}
		report.put("metrics", metrics);
//		// additional
//					metrics.put("scriptCount", project.getScriptCount());
//					metrics.put("spriteCount", project.getSpriteCount());

		return report;
	}


	public int getProjectID() {
		return projectID;
	}

	private AnalysisConfigurator getDefaultConfig() {
		AnalysisConfigurator defaultConfig = new AnalysisConfigurator();
		try {
			defaultConfig.addAnalysis(MasteryAnalyzer.class.getName());
			defaultConfig.addAnalysis(UnreachableScriptAnalyzer.class
					.getName());
			defaultConfig.addAnalysis(TooLongScriptAnalyzer.class.getName());
			defaultConfig.addAnalysis(BroadCastWorkAroundAnalyzer.class
					.getName());
			defaultConfig.addAnalysis(UncommunicativeNamingAnalyzer.class
					.getName());
			defaultConfig.addAnalysis(BroadVarScopeAnalyzer.class.getName());
			defaultConfig.addAnalysis(DuplicateCodeAnalyzer.class.getName());
			defaultConfig.addAnalysis(UnnecessaryBroadcastAnalyzer.class
					.getName());
			defaultConfig.addAnalysis(UnusedVariableAnalyzer.class.getName());
			defaultConfig.addAnalysis(UnusedCustomBlockAnalyzer.class.getName());
			defaultConfig.addAnalysis(ScriptLengthMetricAnalyzer.class
					.getName());
			defaultConfig.addAnalysis(DuplicateValueAnalyzer.class.getName());
			defaultConfig.addAnalysis(TooFineGrainScriptAnalyzer.class
					.getName());
			defaultConfig.addAnalysis(HardCodedMediaSequenceAnalyzer.class
					.getName());
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

	public static String analysisReportGenerator(int[] projectIDs)
			throws Exception {
		AnalysisManager blockAnalyzer = new AnalysisManager();
		StringBuilder sb = new StringBuilder();
		for (int id : projectIDs) {
			String src = Util.retrieveProjectOnline(id);
			blockAnalyzer.analyze(src);
			JSONObject report = blockAnalyzer.getFullJSONReport();
			String result = report.toJSONString();
			sb.append(id);
			sb.append("\t");
			sb.append(result);
			sb.append("\n");
		}
		System.out.println(sb.toString());
		return sb.toString();
	}

	

	public static void runSingleAnalysis(String inputFile,
			String analysisClassURL) throws FileNotFoundException, IOException {
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
				JSONObject srcLine = (JSONObject) jsonParser.parse(line
						.toString());
				id = (Long) srcLine.get("_id");
				String src = srcLine.get("src").toString();
				blockAnalyzer.analyze(src);
				JSONObject report = blockAnalyzer.getFullJSONReport();
				JSONObject smells = (JSONObject) report.get("smells");
				String smellName = (String) smells.keySet().iterator().next();
				JSONObject record = (JSONObject) smells.get(smellName);
				Integer count = (Integer) record.get("count");
				stats.addValue(count);

				if (count > 0) {
					found += 1;
					logger.info(id);
					;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		DecimalFormat df2 = new DecimalFormat(".##");
		System.out.println("Found:" + found + " out of " + stats.getN()
				+ " or " + df2.format((double) found / stats.getN() * 100)
				+ "%");
		System.out.println("Average:" + df2.format(stats.getMean())
				+ " per project");
	}

	public static void runAnalysis(String analysisClassName)
			throws FileNotFoundException, IOException {
		runSingleAnalysis(smallTestInput, analysisClassName);
	}

	public static String runAnalysis2(Analyzer analyzer, double percent)
			throws IOException {
		DatasetFilter filter = new DatasetFilter();
		FileInputStream is = new FileInputStream(AnalysisManager.largeTestInput);
		AnalysisDBManager dbManager = new AnalysisDBManager("localhost",
				"exploration");

		String[] lines = IOUtils.toString(is).split("\n");
		List<String> data = new ArrayList<>(Arrays.asList(lines));
		int endIndex = (int) Math.round(percent * lines.length);
		List<String> partition = data.subList(0, endIndex);
		filter.setDataSource(partition);
		filter.setScriptableThreshold(5);
		filter.setAvgScriptPerSprite(3.0);

		HashMap<Integer, JSONObject> datasetDict = filter
				.getFilteredProjectsFrom(1);
		System.out.println("Original Size: " + lines.length);
		System.out.println("Filtered Size: " + datasetDict.size());

		CSVGenerator gen = new CSVGenerator();
		gen.setColumn(new String[] { "ID", "count" });

		for (Integer id : datasetDict.keySet()) {
			JSONObject projectJson = datasetDict.get(id);
			ScratchProject project = null;
			try {
				project = ScratchProject.loadProject((String) projectJson
						.get("src"));
				analyzer.setProject(project);
				analyzer.analyze();
				Document report = Document.parse(analyzer.getReport()
						.getJSONReport().toJSONString());
				if (((Document) report.get("records")).getInteger("count") > 0) {
					System.out.println(id);
					System.out.println(analyzer.getReport().getJSONReport());
				}
				gen.addLine(new Object[] { id,
						((Document) report.get("records")).getInteger("count") });
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (ParsingException e) {
				e.printStackTrace();
			} catch (AnalysisException e) {
				e.printStackTrace();
			}
		}
		return gen.generateCSV();
	}

	public static Report runSingleAnalysis(int i, Analyzer analyzer)
			throws AnalysisException, IOException, ParseException,
			ParsingException {
		String projectSrc = Util.retrieveProjectOnline(i);
		ScratchProject project = ScratchProject.loadProject(projectSrc);
		analyzer.setProject(project);
		analyzer.analyze();
		return analyzer.getReport();
	}
	
	public static void main(String[] args) throws IOException {
		AnalysisManager blockAnalyzer = new AnalysisManager();
		String src = null;
		int projectID = 97231677;
		try {
			src = Util.retrieveProjectOnline(projectID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
//			JSONObject result = blockAnalyzer.analyze(src);
			blockAnalyzer.analyze(src);
			JSONObject result = blockAnalyzer.getConciseJSONReports();
			System.out.println(result.toJSONString());
		} catch (AnalysisException e) {
			e.printStackTrace();
		} catch (ParsingException e) {
			e.printStackTrace();
		}
	}

}
