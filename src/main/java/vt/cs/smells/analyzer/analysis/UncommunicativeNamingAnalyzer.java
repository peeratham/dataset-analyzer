package vt.cs.smells.analyzer.analysis;

import java.io.IOException;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.Analyzer;
import vt.cs.smells.analyzer.ListAnalysisReport;
import vt.cs.smells.analyzer.Report;
import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.nodes.Scriptable;
import vt.cs.smells.analyzer.parser.ParsingException;
import vt.cs.smells.analyzer.parser.Util;
import vt.cs.smells.select.Collector;
import vt.cs.smells.select.Evaluator;

public class UncommunicativeNamingAnalyzer extends Analyzer {
	private static final String name = "UncommunicativeName";
	private static final String abbr = "UN";
	ListAnalysisReport report = new ListAnalysisReport(name, abbr);
	int spriteNameCount = 0;
	int messageNameCount = 0;
	boolean noSprite = false;
	boolean noMessage = false;
	public int count;
	@Override
	public void analyze() throws AnalysisException {
		//check if there is sprite at all
		if(project.getAllScriptables().keySet().size()<2){
			noSprite = true;
		}

		//check if there is broadcast block at all
		List<Block> allBroadCastBlocks = Collector.collect(
				new Evaluator.BlockCommand("broadcast:"), project);
		if(allBroadCastBlocks.isEmpty()){
			noMessage = true;
		}
		
		
		for (String scriptableName : project.getAllScriptables().keySet()) {
			if (scriptableName.contains("Sprite")) {
				report.addRecord(scriptableName);
				spriteNameCount++;
			}

			List<Block> broadCastBlocks = Collector.collect(
					new Evaluator.BlockCommand("broadcast:"), project.getScriptable(scriptableName));

			
			for (Block block : broadCastBlocks) {
				String messageName = block.getArgs().get(0).toString();
				if (messageName.contains("message")) {
					report.addRecord(messageName);
					messageNameCount++;
				}
			}
		}
	}

	@Override
	public Report getReport() {
		JSONObject conciseReport = new JSONObject();
		count = spriteNameCount+messageNameCount;
		if(!noSprite){
			conciseReport.put("count", count);
			conciseReport.put("sprite", spriteNameCount);
		}
		if(!noMessage){
			conciseReport.put("message", messageNameCount);
		}
		
		report.setConciseJSONReport(conciseReport);
		return report;
	}
	
	public static void main(String[] args) throws IOException, ParseException, ParsingException, AnalysisException{
		String projectSrc = Util.retrieveProjectOnline(118377854);
		ScratchProject project = ScratchProject.loadProject(projectSrc);
		UncommunicativeNamingAnalyzer analyzer = new UncommunicativeNamingAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();
		System.out.println(analyzer.getReport().getConciseJSONReport());
		
	}

}
