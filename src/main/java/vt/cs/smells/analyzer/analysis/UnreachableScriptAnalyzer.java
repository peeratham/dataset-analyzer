package vt.cs.smells.analyzer.analysis;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.AnalysisUtil;
import vt.cs.smells.analyzer.Analyzer;
import vt.cs.smells.analyzer.ListAnalysisReport;
import vt.cs.smells.analyzer.Report;
import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.select.Collector;
import vt.cs.smells.select.Evaluator;

public class UnreachableScriptAnalyzer extends Analyzer {
	private static final String name = "UnreachableScript";
	private static final String abbr = "US";
	
	Report report = new ListAnalysisReport(name,abbr);
	int count = 0;

	@Override
	public void analyze() throws AnalysisException {
		List<Block> broadcastWaitBlocks = AnalysisUtil.findBlock(project, "doBroadcastAndWait");
		List<Block> broadcasts = AnalysisUtil.findBlock(project, "broadcast:");
		List<Block> allBroadcasts = new ArrayList<>();
		allBroadcasts.addAll(broadcastWaitBlocks);
		allBroadcasts.addAll(broadcasts);
		
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
				count++;
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
