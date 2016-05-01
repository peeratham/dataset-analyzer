package cs.vt.analysis.analyzer.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.BlockPath;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;
import cs.vt.analysis.analyzer.parser.Insert;
import cs.vt.analysis.select.Collector;
import cs.vt.analysis.select.Evaluator;
import weka.classifiers.bayes.net.ParentSet;

public class DuplicateAnalyzer extends Analyzer{	
	private ListAnalysisReport report = new ListAnalysisReport();
	private HashMap<String,ArrayList<BlockPath>> finalPath = new HashMap<String,ArrayList<BlockPath>>();
	
	public DuplicateAnalyzer(){}
	public HashMap<String,ArrayList<BlockPath>> updatePath(HashMap<String,ArrayList<BlockPath>> path,Block b,int parameterCount){
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
					if(path.containsKey(objectValue.toString())){
		        		BlockPath bp = b.getBlockPath();
		        		ArrayList<BlockPath> array = path.get(objectValue.toString());
		        		if(array.contains(bp)){
		        			continue;
		        		}
		        		else{
		        			array.add(bp);
		        		}
		        	}
		        	else{
		        		BlockPath bp = b.getBlockPath();
		        		ArrayList<BlockPath> array = new ArrayList<BlockPath> ();
		        		array.add(bp);
		        		path.put(objectValue.toString(), array);
		        	}
				}
			}
			
		}	
		return path;
	}
	
	
	@Override
	public void analyze() throws AnalysisException {
		// TODO Auto-generated method stub
		
		for (Scriptable name : project.getAllScriptables().values()) { //foreach sprite		
			HashMap<String,ArrayList<BlockPath>> path = new HashMap<String,ArrayList<BlockPath>>();
					
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
						updatePath(path,b,1);
					}
					else if(parameterCount>1){
						updatePath(path,b,0);
					}
				}
			}
			//at this point, we get all the duplicate in this sprite
			for(String s:path.keySet()){
				if(path.get(s).size()>1){
					if(finalPath.containsKey(s)){
						ArrayList<BlockPath> tempPath = finalPath.get(s);
						tempPath.addAll(path.get(s));
					}
					else{
						finalPath.put(s,path.get(s));
					}
				}
			}
		}		
	}
	public JSONObject newJsonInstance(HashMap<String,ArrayList<BlockPath>> path,String s){
		JSONObject json = new JSONObject();		
		json.put("loc", path.get(s));
		json.put("value", s);
		return json;
	}
	
	@Override
	public Report getReport() {
		// TODO Auto-generated method stub
		report.setTitle("Duplicate");
		//todo
		for (String s:finalPath.keySet()) {		
			report.addRecord(newJsonInstance(finalPath,s));			
		}	
		return report;
	}
	
}
