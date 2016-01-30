package cs.vt.analysis.analyzer.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONArray;




import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.BlockSpec;
import cs.vt.analysis.analyzer.nodes.Script;

/**Example json file
 *http://projects.scratch.mit.edu/internalapi/project/25857120/get/ 
 */


public class Parser {
	static Logger logger = Logger.getLogger(Parser.class);
	 
	public Parser(){
        PropertyConfigurator
        .configure(Parser.class.getClassLoader()
                    .getResource("log4j.properties"));
        
		CommandLoader.loadCommand();
	}

	public Script loadScript(Object s) throws ParsingException {
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
	
	public Block loadBlock(Object b) throws ParsingException{
		JSONArray blockArray = (JSONArray)b;
		Block result = new Block();
		
		ArrayList<Object> args = new ArrayList<Object>();

		String command = (String) blockArray.get(0);
		BlockSpec blockSpec;
		
		
		if(command.equals("call")){ // CustomBlockType call
			String signature = (String) blockArray.remove(1);
			blockSpec = CommandLoader.COMMAND_TO_CUSTOM_BLOCKSPEC.get(signature);
			if(blockSpec==null){
				throw new ParsingException("Custom Block: "+signature+" is not defined");
			}
		} else{
			blockSpec = CommandLoader.COMMAND_TO_BLOCKSPEC.get(command);
			
			if(!command.equals("Position")&&blockSpec==null){
				throw new ParsingException("Block: "+command+" is not defined");
			}
		}
		
		if(command.equals("procDef")){	//CustomBlock
			Block customBlockArg = new Block();
			BlockSpec customBlockSpec = CommandLoader.COMMAND_TO_CUSTOM_BLOCKSPEC.get((String) blockArray.get(1));
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
					previous = loadBlock(blocks.get(0));

					((List)arg).add(previous);	//		add first block
					result.setFirstChild(previous);
					
					for (int argj = 1; argj < blocks.size(); argj++) {
						Block current  = null;
						current = loadBlock(blocks.get(argj));
			        	previous.setNextBlock(current);
			        	((List)arg).add(current);
			        	previous = current;	
					}
					result.setNestedBlocks(arg);
					
				}else{
					arg = loadBlock(blockArray.get(argi)); //block
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

	public void loadCustomBlock(JSONArray firstBlockArray) throws ParsingException {	
		
			try{
				Block customBlockArg = new Block();
				BlockSpec customBlockSpec = BlockSpec.parseCustomBlockSpec(firstBlockArray);
				CommandLoader.COMMAND_TO_CUSTOM_BLOCKSPEC.put((String) firstBlockArray.get(1), customBlockSpec);
				
			} catch(Exception e){
				throw new ParsingException(e);
			}
		

	}
}

