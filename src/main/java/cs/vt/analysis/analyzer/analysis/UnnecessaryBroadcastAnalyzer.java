package cs.vt.analysis.analyzer.analysis;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.json.simple.JSONObject;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;

public class UnnecessaryBroadcastAnalyzer extends Analyzer{
	ListAnalysisReport report = new ListAnalysisReport();
	
	class BroadCastReceivePair {
		private String message;
		HashSet<String> src = new HashSet<String>();
		HashMap<String, HashSet<Script>> dest = new HashMap<String, HashSet<Script>>();

		public void putSrc(String name) {
			src.add(name);
			
		}

		public void putDest(String name, Script parent) {
			if(!dest.containsKey(name)){
				dest.put(name, new HashSet<Script>());
			}
			HashSet<Script> scripts = dest.get(name);
			scripts.add(parent);
		}

		@Override
		public String toString() {
			return "BroadCastReceivePair ["
					+ (message != null ? "message=" + message + ", " : "")
					+ (src != null ? "src=" + src + ", " : "")
					+ (dest != null ? "dest=" + dest : "") + "]";
		}
		
		
		
	}
	@Override
	public void analyze() throws AnalysisException {

		//map (message: Pair(src,dest))
		//src = set(ScriptableName)
		//dest = set(tuple(ScriptableName, Set(ScriptableRef)))
		
		
		HashMap<String, BroadCastReceivePair> map = new HashMap<>();
		for(Scriptable scriptable: project.getAllScriptables().values()){
			for(Script s : scriptable.getScripts()){
				ArrayList<Block> broadcastWaitBlocks = AnalysisUtil.findBlock(s, "doBroadcastAndWait");
				ArrayList<Block> broadcasts = AnalysisUtil.findBlock(s, "broadcast:");
				
				if(broadcastWaitBlocks.size()>0){
					for(Block b:broadcastWaitBlocks){
						if(broadcasts.contains(b)){	//filter broadcast as it's concurrent behavior and not applicable to be removed
							continue;
						}
						String message = b.getArgs(0).toString();
						if(!map.containsKey(message)){
							map.put(message, new BroadCastReceivePair());
						}
						BroadCastReceivePair pair = map.get(message);
						pair.putSrc(scriptable.getName());
					}
				}
				
				ArrayList<Block> receiveBlocks = AnalysisUtil.findBlock(s, "whenIReceive");
				if(receiveBlocks.size()>0){
					for(Block b: receiveBlocks){
						String message = b.getArgs(0).toString();
						if(!map.containsKey(message)){
							map.put(message, new BroadCastReceivePair());
						}
						BroadCastReceivePair pair = map.get(message);
						pair.putDest(scriptable.getName(), (Script)b.getParent());
					}
				}

			}
			
		}
		
		for(String message: map.keySet()){
			BroadCastReceivePair pair = map.get(message);
			if(pair.src.size()==1 && pair.dest.keySet().size()==1){
				if(pair.dest.keySet().equals((pair.src))){
					String spriteName = pair.dest.keySet().iterator().next();
					if(pair.dest.get(spriteName).size()==1){
						JSONObject record = new JSONObject();
						record.put(spriteName, message);
						report.addRecord(record);
					}
				}
			}
		}
		
	}

	private HashSet<Block> findBroadCastBlock(Script s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListAnalysisReport getReport() {
		report.setTitle("Unnecessary Broadcast");
		return report;
	}

}
