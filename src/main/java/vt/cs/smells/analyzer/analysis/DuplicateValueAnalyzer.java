package vt.cs.smells.analyzer.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.SortedMap;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.Analyzer;
import vt.cs.smells.analyzer.ListAnalysisReport;
import vt.cs.smells.analyzer.Report;
import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.BlockPath;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.nodes.Scriptable;
import vt.cs.smells.analyzer.parser.Insert;
import vt.cs.smells.select.Collector;
import vt.cs.smells.select.Evaluator;



public class DuplicateValueAnalyzer extends Analyzer{	
	private ListAnalysisReport report = new ListAnalysisReport();
	private HashMap<String,ArrayList<BlockPath>> finalPath = new HashMap<String,ArrayList<BlockPath>>();
	HashMap<String, SortedMap<String, List<BlockPath>>> subStringDupGroupMap = new HashMap<>();
	
	class CloneGroup{
		HashSet<String> group;
		HashMap<String, ArrayList<BlockPath>> instanceMap;
		public CloneGroup(String subStringKey) {
			group = new HashSet<>();
			instanceMap = new HashMap<>();
			addSubString(subStringKey);
		}

		public void addSubString(String substr){
			group.add(substr);
			if(!instanceMap.containsKey(substr)){
				instanceMap.put(substr,new ArrayList<>());
			}		
		}
		
		public void addDuplicateInstance(String subStr, ArrayList<BlockPath> instances){
			instanceMap.get(subStr).addAll(instances);
		}

		@Override
		public String toString() {
			return "CloneGroup [group=" + group + ", instanceMap=" + instanceMap + "]";
		}
		
	}
	
	public DuplicateValueAnalyzer(){}
	public HashMap<String,ArrayList<BlockPath>> updatePath(HashMap<String,ArrayList<BlockPath>> pathMap,Block b){
		ArrayList<Object> parts = new ArrayList<Object>();
		for(Object objPart : b.getBlockType().getParts()){
			if(objPart instanceof Insert){
				parts.add(objPart);				
			}
		}	
		for(int j=0;j<b.getArgs().size();j++){
			//get potential arg
			Object objPart = parts.get(j);
			if((((Insert) objPart).getType()!=null)&&(((Insert) objPart).getType().equals("string")||((Insert) objPart).getType().equals("number"))){
				Object objectValue = b.getArgs(j);
				if(objectValue instanceof java.lang.String ||objectValue instanceof java.lang.Long){
					if(pathMap.containsKey(objectValue.toString())){
		        		BlockPath bp = b.getBlockPath();
		        		ArrayList<BlockPath> array = pathMap.get(objectValue.toString());
		        		if(array.contains(bp)){
		        			continue;
		        		}
		        		else{
		        			array.add(bp);
		        		}
		        	}
		        	else{        		
		        		BlockPath bp = b.getBlockPath();
		        		JSONArray array = new JSONArray();
		        		array.add(bp);
		        		pathMap.put(objectValue.toString(), array);
		        	}
				}
			}
			
		}	
		return pathMap;
	}
	
	
	@Override
	public void analyze() throws AnalysisException {
		for (Scriptable name : project.getAllScriptables().values()) { //foreach sprite		
			HashMap<String,ArrayList<BlockPath>> pathMap = new HashMap<String,ArrayList<BlockPath>>();
					
			for(Script s:name.getScripts()){// foreach script				
				List<Block> allBlocks = Collector.collect(new Evaluator.AnyBlock(), s);	// all blocks
				for(Block b:allBlocks){
					int parameterCount = 0;
					for(Object o :b.getBlockType().getParts()){
						if(o instanceof Insert){							
							if(((Insert) o).getType()!=null&&(((Insert) o).getType().equals("string")||((Insert) o).getType().equals("number"))){
								parameterCount++;
							}
						}
					}
					if(parameterCount==1){
						updatePath(pathMap,b);
					}
					else if(parameterCount>1){
						updatePath(pathMap,b);
					}
				}
			}
			//if value is a substring (prefix or subfix)
			Trie dict = new PatriciaTrie(pathMap);
			for(String key :pathMap.keySet()){
				SortedMap prefixMap = dict.prefixMap(key);
				if(prefixMap.size()>1||pathMap.get(key).size()>1){
					subStringDupGroupMap.put(key, prefixMap);
				}
			}
			
			
//			//at this point, we get all the duplicate in this sprite
//			for(String s:pathMap.keySet()){
//				if(pathMap.get(s).size()>1){
//					if(finalPath.containsKey(s)){
//						ArrayList<BlockPath> tempPath = finalPath.get(s);
//						tempPath.addAll(pathMap.get(s));
//					}
//					else{
//						finalPath.put(s,pathMap.get(s));
//					}
//				}
//			}
		}		
	}
	public JSONObject newJsonInstance(HashMap<String,ArrayList<BlockPath>> path,String s){
		JSONObject json = new JSONObject();
		JSONArray locs = (JSONArray) path.get(s);
		json.put("loc", locs.toJSONString());
		json.put("value", s);
		return json;
	}
	
	@Override
	public Report getReport() {
		report.setTitle("Duplicate Value");
		for (String key: subStringDupGroupMap.keySet()){
			JSONObject cloneGroupJSON = new JSONObject();
			SortedMap<String, List<BlockPath>> subStrMap = subStringDupGroupMap.get(key);
			JSONObject subCloneJSON = new JSONObject();
			int size = 0;
			for(String subKey : subStrMap.keySet()){
				JSONArray subStrInstanceJSON = new JSONArray();
				subStrMap.get(subKey).forEach((v)->subStrInstanceJSON.add(v.toString()));
				subCloneJSON.put(subKey, subStrInstanceJSON);
				size += subStrMap.get(subKey).size();
			}
			cloneGroupJSON.put("value", key);
			cloneGroupJSON.put("instances", subCloneJSON);
			cloneGroupJSON.put("size", size);
			report.addRecord(cloneGroupJSON);
		}
		
		return report;
	}
	
}
