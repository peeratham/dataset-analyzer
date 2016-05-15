package vt.cs.smells.visual;

import java.util.HashSet;

import vt.cs.smells.analyzer.AnalysisUtil;
import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.Script;

public class PropertiesCollector {
	public static final HashSet<String> collectVariables(Script script){
		HashSet<String> result= new HashSet<String>();
		
		for(Block b:AnalysisUtil.getVarDefBlocks(script)){
			result.add(b.arg("varName"));
		}
		for(Block b:AnalysisUtil.getVarRefBlocks(script)){
			result.add(b.arg("varName"));
		}
		
		return result;
		
	}
}
