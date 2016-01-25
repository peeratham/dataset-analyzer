package cs.vt.analysis.analyzer.parser;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.BlockSpec;
import cs.vt.analysis.analyzer.nodes.Script;

/**Example json file
 *http://projects.scratch.mit.edu/internalapi/project/25857120/get/ 
 */


public class Parser {
	public Parser(){
		CommandLoader.loadCommand();
	}

	public Script loadScript(Object s) throws Exception {
		Script script = new Script();
		JSONArray scriptArray = (JSONArray)s;
		int x = ((Number)scriptArray.remove(0)).intValue();
		int y = ((Number)scriptArray.remove(0)).intValue();
		
		script.setPosition(x,y);
		JSONArray jsonBlocks = (JSONArray) scriptArray.remove(0);
		
		List<Block> blocks = new ArrayList<Block>();
		
		Block previous = null;
		try{
			previous = loadBlock(jsonBlocks.get(0));
		} catch(Exception e){
			throw new ParsingException("Error Parsing Block:"+e);
		}
		blocks.add(previous);	//		add first block
		
        for (int i = 1; i < jsonBlocks.size(); i++)
        {	
        	Block current = null;
        	try{
        		current = loadBlock(jsonBlocks.get(i));
        	} catch (Exception e){
        		throw new ParsingException("Error Parsing Block:"+e);
        	}
        	previous.setNextBlock(current);
        	blocks.add(current);
        	previous = current;	
        	
        }
        script.setBlocks(blocks);
        return script;
	}
	
	public Block loadBlock(Object b) throws Exception{
		JSONArray blockArray = (JSONArray)b;
		Block result = new Block();
		
		ArrayList<Object> args = new ArrayList<Object>();

		String command = (String) blockArray.get(0);
		BlockSpec blockSpec;
		
		
		if(command.equals("call")){ // CustomBlockType call
			blockSpec = CommandLoader.COMMAND_TO_CUSTOM_BLOCKSPEC.get((String) blockArray.remove(1));			
		} else{
			blockSpec = CommandLoader.COMMAND_TO_BLOCKSPEC.get(command);
		}
		
		if(command.equals("procDef")){	//CustomBlock
			Block customBlockArg = new Block();
			BlockSpec customBlockSpec = BlockSpec.parseCustomBlockSpec(blockArray);
			CommandLoader.COMMAND_TO_CUSTOM_BLOCKSPEC.put((String) blockArray.get(1), customBlockSpec);
			customBlockArg.setCommand(command);
			customBlockArg.setBlockSpec(customBlockSpec);
			args.add(customBlockArg);
			result.setCommand(command);
			result.setBlockSpec(blockSpec);
			result.setArgs(args);
			return result;
		}
		blockArray.remove(0);
		
		
		Object arg = null;
		for (int argi = 0; argi < blockArray.size(); argi++) {
			if(blockArray.get(argi) instanceof JSONArray){
				if(((JSONArray)blockArray.get(argi)).get(0) instanceof JSONArray){	//nested blocks
					result.hasNestedBlocks(true);
					arg = new ArrayList<Block>();//stack shape insert (nested blocks) will be list of blocks
					JSONArray blocks = (JSONArray)blockArray.get(argi);	//it's a list of blocks
					
					Block previous = null;
					try{
						previous = loadBlock(blocks.get(0));
					} catch(Exception e){
						throw new ParsingException("Error Parsing Block:"+e);
					}
					((List)arg).add(previous);	//		add first block
					result.setFirstChild(previous);
					
					for (int argj = 1; argj < blocks.size(); argj++) {
						Block current  = null;
						try{
							current = loadBlock(blocks.get(argj));
						}catch(Exception e){
							throw new ParsingException("Error Parsing Block:"+e);
						}
			        	previous.setNextBlock(current);
			        	((List)arg).add(current);
			        	previous = current;	
//						((List)arg).add(loadBlock(blocks.get(argj)));
					}
					result.setNestedBlocks(arg);
					
				}else{
					try{
						arg = loadBlock(blockArray.get(argi)); //block
					}catch(Exception e){
						throw new ParsingException("Error Parsing Block:"+blockArray);
					}
				}
			}else{
				arg = blockArray.get(argi); //primitive
			}
			
			args.add(arg);	
		}
		
		result.setCommand(command);
		result.setBlockSpec(blockSpec);
		result.setArgs(args);
		
		return result;

	}
}

