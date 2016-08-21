package vt.cs.smells.analyzer.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.AnalysisUtil;
import vt.cs.smells.analyzer.Analyzer;
import vt.cs.smells.analyzer.ListAnalysisReport;
import vt.cs.smells.analyzer.Report;
import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.nodes.Scriptable;
import vt.cs.smells.analyzer.parser.ParsingException;
import vt.cs.smells.analyzer.parser.Util;
import vt.cs.smells.select.Collector;
import vt.cs.smells.select.Evaluator;

public class UnreachableScriptAnalyzer extends Analyzer {
	private static final String name = "UnreachableCode";
	private static final String abbr = "UC";
	
	Report report = new ListAnalysisReport(name,abbr);
	int count = 0;
	int nonHatCount = 0;
	int unMatchedMessageCount = 0;

	@Override
	public void analyze() throws AnalysisException {
		List<Block> broadcastWaitBlocks = AnalysisUtil.findBlock(project, "doBroadcastAndWait");
		List<Block> broadcasts = AnalysisUtil.findBlock(project, "broadcast:");
		List<Block> allBroadcasts = new ArrayList<>();
		allBroadcasts.addAll(broadcastWaitBlocks);
		allBroadcasts.addAll(broadcasts);
		
		//check nonhat
		for(Scriptable scriptable : project.getAllScriptables().values()){
			for(Script script: scriptable.getScripts()){
				Block firstBlock = script.getBlocks().get(0);
				if(!firstBlock.getBlockType().getShape().equals("hat")){
					report.addRecord(firstBlock.getBlockPath().toString());
					nonHatCount++;
				}
			}
		}
		
		List<String> messages = new ArrayList<>();
		for(Block block : allBroadcasts){
			List<Object> args =  block.getArgs();
			messages.add(args.get(0).toString());
		}
		
		
		List<Block> receiverBlocks = AnalysisUtil.findBlock(project, "whenIReceive");
		for(Block receiverBlock: receiverBlocks){
			List<Object> args =  receiverBlock.getArgs();
			if (!messages.contains(args.get(0))){
				report.addRecord(receiverBlock.getBlockPath().toString());
				unMatchedMessageCount++;
			}

		}
		count = nonHatCount+unMatchedMessageCount;
	}

	@Override
	public Report getReport() {
		JSONObject conciseReport = new JSONObject();
		
		conciseReport.put("count", count);
		conciseReport.put("nonHatCount", nonHatCount);
		conciseReport.put("unMatchedMessageCount", unMatchedMessageCount);
		report.setConciseJSONReport(conciseReport);
		return report;
	}
	
	public static void main(String[] args) throws IOException, ParseException, ParsingException, AnalysisException{
		String projectSrc = Util.retrieveProjectOnline(118377854);
		ScratchProject project = ScratchProject.loadProject(projectSrc);
		UnreachableScriptAnalyzer analyzer = new UnreachableScriptAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();
		System.out.println(analyzer.getReport().getConciseJSONReport());
	}

}
