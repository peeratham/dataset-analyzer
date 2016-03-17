package cs.vt.analysis.analyzer.nodes;

import java.util.ArrayList;

import org.json.simple.JSONArray;

import cs.vt.analysis.analyzer.parser.CommandLoader;

public class CustomBlock extends Block {
	
	public CustomBlock(JSONArray blockArray){
		args = new ArrayList<Object>();
		command = (String) blockArray.get(0);
		blockSpec = CommandLoader.COMMAND_TO_BLOCKSPEC.get(command);
		
		Block customBlockArg = new Block();
		BlockSpec customBlockSpec = CommandLoader.COMMAND_TO_CUSTOM_BLOCKSPEC
				.get((String) blockArray.get(1));
		customBlockArg.setCommand(command);
		customBlockArg.setBlockSpec(customBlockSpec);
		customBlockArg.setParent(this);
		customBlockArg.setArgs(new ArrayList<Object>());
		args.add(customBlockArg);
	}


}
