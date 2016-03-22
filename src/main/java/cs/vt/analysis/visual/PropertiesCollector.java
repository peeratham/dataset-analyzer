package cs.vt.analysis.visual;

import java.util.HashSet;

import cs.vt.analysis.analyzer.analysis.AnalysisUtil;
import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.Script;

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
