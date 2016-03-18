package cs.vt.analysis.analyzer.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import cs.vt.analysis.analyzer.nodes.Block;
import cs.vt.analysis.analyzer.nodes.BlockSpec;
import cs.vt.analysis.analyzer.nodes.CustomBlock;
import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;

/**
 * Example json file
 * http://projects.scratch.mit.edu/internalapi/project/25857120/get/
 */

public class Parser {
	static Logger logger = Logger.getLogger(Parser.class);

	public Parser() {
		PropertyConfigurator.configure(Parser.class.getClassLoader()
				.getResource("log4j.properties"));

		CommandLoader.loadCommand();
	}

	public static ScratchProject loadProject(JSONObject jsonObject)
			throws ParsingException {
		ScratchProject project = new ScratchProject();
		CommandLoader.loadCommand();
		JSONObject stageObj = jsonObject;

		if (stageObj.containsKey("info")) {
			JSONObject infoObj = (JSONObject) stageObj.get("info");
			if ((String) ((JSONObject) infoObj).get("projectID") != null) {
				int projectID = Integer
						.parseInt((String) ((JSONObject) infoObj)
								.get("projectID"));
				project.setProjectID(projectID);
				logger.info("Load projectID:" + projectID);
			} else {
				throw new ParsingException("Project ID Not Found");
			}
			
			project.setScriptCount((Long) ((JSONObject) infoObj).get("scriptCount"));
			project.setSpriteCount((Long) ((JSONObject) infoObj).get("spriteCount"));
		}
		
		Scriptable stage = loadScriptable(stageObj);
		project.addScriptable("Stage", stage);

		JSONArray children = (JSONArray) jsonObject.get("children");

		for (int i = 0; i < children.size(); i++) {
			JSONObject spriteJSON = (JSONObject) children.get(i);
			if (!spriteJSON.containsKey("objName")) { // not a sprite
				continue;
			}

			Scriptable sprite = loadScriptable(spriteJSON);
			project.addScriptable(sprite.getName(), sprite);
		}
		CommandLoader.addCustomBlockIndex();
		
		return project;
	}

	public static Scriptable loadScriptable(JSONObject spriteJSON)
			throws ParsingException {
		Scriptable sprite = new Scriptable();
		String spriteName = (String) spriteJSON.get("objName");
		JSONArray scripts = (JSONArray) spriteJSON.get("scripts");
		JSONArray variables = (JSONArray) spriteJSON.get("variables");
		sprite.setName(spriteName);

		if (variables != null) {
			sprite.setVars(loadVariables(variables));
		}

		if (scripts == null) { // empty script
			return sprite;
		}

		// parse custom block for each sprite first
		for (int j = 0; j < scripts.size(); j++) {

			JSONArray scriptJSON = (JSONArray) ((JSONArray) scripts.get(j))
					.get(2);
			JSONArray firstBlockJSON = (JSONArray) scriptJSON.get(0);
			String command = (String) firstBlockJSON.get(0);

			if (command.equals("procDef")) {
				try {
					loadCustomBlock(firstBlockJSON);

				} catch (ParsingException e) {
					logger.error("Error Parsing Custom Block in Scriptable:"
							+ spriteName);
					logger.error("=>" + firstBlockJSON);
					throw new ParsingException(e);
				}
			}
		}

		// parse script
		for (int j = 0; j < scripts.size(); j++) {
			Script scrpt = null;
			String scriptStr = scripts.get(j).toString();
			try {
				scrpt = loadScript(scripts.get(j));
				sprite.addScript(scrpt);
				scrpt.setParent(sprite);
			} catch (ParsingException e) {
				logger.error("Error Parsing a script in Scriptable:"
						+ spriteName);
				logger.error("=>" + scriptStr);
				logger.error("==>" + e.getMessage());
				throw new ParsingException(e);
			}
		}
		return sprite;
	}

	private static Map<String, Object> loadVariables(JSONArray variables) {
		Map<String, Object> vars = new HashMap<String, Object>();
		for (int i = 0; i < variables.size(); i++) {
			JSONObject o = (JSONObject) variables.get(i);
			vars.put((String) o.get("name"), o.get("value"));
		}
		return vars;
	}

	public static Script loadScript(Object s) throws ParsingException {
		Script script = new Script();
		JSONArray scriptArray = (JSONArray) s;
		int x = ((Number) scriptArray.remove(0)).intValue();
		int y = ((Number) scriptArray.remove(0)).intValue();

		script.setPosition(x, y);
		JSONArray jsonBlocks = (JSONArray) scriptArray.remove(0);

		List<Block> blocks = new ArrayList<Block>();

		Iterator blockIter = jsonBlocks.iterator();
		Block previous = null;

		while (blockIter.hasNext()) {
			Block current = null;
			try {
				current = loadBlock(blockIter.next());
			} catch (Exception e) {
				throw new ParsingException("Error Parsing Block:" + e);
			}
			if (previous != null) {
				previous.setNextBlock(current);
			}
			current.setPreviousBlock(previous);
			current.setParent(script);
			blocks.add(current);
			previous = current;
		}

		script.setBlocks(blocks);
		return script;
	}

	@SuppressWarnings("unchecked")
	public static Block loadBlock(Object b) throws ParsingException {
		JSONArray blockArray = (JSONArray) b;
		Block resultBlock = new Block();

		ArrayList<Object> args = new ArrayList<Object>();

		String command = (String) blockArray.get(0);
		BlockSpec blockSpec;
		
		if (command.equals("procDef")) { // CustomBlock
			return Block.makeCustomBlock(blockArray);
//			return new CustomBlock(blockArray);
		}

		if (command.equals("call")) { // CustomBlockType call
			String customSpecStr = (String) blockArray.remove(1);
			blockSpec = CommandLoader.COMMAND_TO_CUSTOM_BLOCKSPEC
					.get(customSpecStr);
			if (blockSpec == null) {
				throw new ParsingException("Custom Block: " + customSpecStr
						+ " is not defined");
			}
		} else { // Normal Block
			blockSpec = CommandLoader.COMMAND_TO_BLOCKSPEC.get(command);
			if (!command.equals("Position") && blockSpec == null) {
				throw new ParsingException("Block: " + command
						+ " is not defined");
			}
		}

		resultBlock.setCommand(command);
		resultBlock.setBlockType(blockSpec);
		blockArray.remove(0);

		Object arg = null;
		for (int argi = 0; argi < blockArray.size(); argi++) {
			if (blockArray.get(argi) instanceof JSONArray) {
				if (((JSONArray) blockArray.get(argi)).get(0) instanceof JSONArray) { // nested blocks
					arg = new ArrayList<Block>();// stack shape insert (nested blocks) will be list of blocks
					JSONArray blocks = (JSONArray) blockArray.get(argi); // it's
																			// a
																			// list
																			// of
																			// blocks

					Block previous = null;
					Iterator<Block> blockSequenceIter = blocks.iterator();
					while (blockSequenceIter.hasNext()) {
						Block current = loadBlock(blockSequenceIter.next());
						((List<Block>) arg).add(current);
						if (previous != null) {
							previous.setNextBlock(current);
						}
						current.setPreviousBlock(previous);
						current.setParent(resultBlock);
						previous = current;
					}
					resultBlock.addNestedBlocks(arg);

				} else {
					arg = loadBlock(blockArray.get(argi)); // block
					((Block) arg).setParent(resultBlock);
				}
			} else {
				arg = blockArray.get(argi); // primitive
			}

			args.add(arg);
		}


		resultBlock.setArgs(args);

		return resultBlock;

	}

	public static void loadCustomBlock(JSONArray firstBlockArray)
			throws ParsingException {

		try {
			BlockSpec customBlockSpec = BlockSpec
					.parseCustomBlockSpec(firstBlockArray);
			CommandLoader.COMMAND_TO_CUSTOM_BLOCKSPEC.put(
					(String) firstBlockArray.get(1), customBlockSpec);
		} catch (Exception e) {
			throw new ParsingException(e);
		}

	}

}
