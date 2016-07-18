package vt.cs.smells.analyzer.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.BlockType;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.nodes.Scriptable;

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
			throws ParsingException, ProjectIDNotFoundException {
		ScratchProject project = new ScratchProject();
		CommandLoader.loadCommand();
		JSONObject stageObj = jsonObject;
		int projectID = 0;
		if (stageObj.containsKey("info")) {
			JSONObject infoObj = (JSONObject) stageObj.get("info");
			if ((String) ((JSONObject) infoObj).get("projectID") != null) {
				projectID = Integer.parseInt((String) ((JSONObject) infoObj)
						.get("projectID"));
				project.setProjectID(projectID);
				logger.debug("Load projectID:" + projectID);
			} else {
				ParsingException e = new ParsingException();
				e.initCause(new ProjectIDNotFoundException());
				throw e;
			}

			project.setScriptCount((Long) ((JSONObject) infoObj)
					.get("scriptCount"));
			project.setSpriteCount((Long) ((JSONObject) infoObj)
					.get("spriteCount"));
		}

		try {
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
		} catch (Exception e) {
			throw new ParsingException(
					"Fail to parse project ID: " + projectID, e);
		}

		return project;
	}

	public static Scriptable loadScriptable(JSONObject spriteJSON)
			throws ParsingException {
		Scriptable sprite = new Scriptable();
		String spriteName = (String) spriteJSON.get("objName");
		JSONArray scripts = (JSONArray) spriteJSON.get("scripts");
		JSONArray variables = (JSONArray) spriteJSON.get("variables");
		JSONArray costumes = (JSONArray) spriteJSON.get("costumes");
		sprite.setName(spriteName);

		if (variables != null) {
			sprite.setVars(loadVariables(variables));
		}

		if (costumes != null) {
			sprite.setCostumes(loadCostumes(costumes));
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
			try {
				if (command.equals("procDef")) {
					loadCustomBlock(firstBlockJSON);
				}
			} catch (ParsingException e) {
				logger.error("Error Parsing Custom Block in Scriptable:"
						+ spriteName);
				logger.error("=>" + firstBlockJSON);
				throw new ParsingException(e);
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
				String parseInfo = "Parsing Scriptable=" + spriteName;
				// parseInfo += "\n" + scriptStr;
				// logger.error(parseInfo);
				throw new ParsingException(parseInfo, e);
			}
		}
		return sprite;
	}

	private static ArrayList<String> loadCostumes(JSONArray costumes) {
		ArrayList<String> costumeList = new ArrayList<String>();
		for (int i = 0; i < costumes.size(); i++) {
			JSONObject costume = (JSONObject) costumes.get(i);
			costumeList.add((String) costume.get("costumeName"));
		}
		return costumeList;
	}

	private static Map<String, Object> loadVariables(JSONArray variables) {
		Map<String, Object> vars = new HashMap<String, Object>();
		for (int i = 0; i < variables.size(); i++) {
			JSONObject o = (JSONObject) variables.get(i);
			vars.put((String) o.get("name"), o.get("value"));
		}
		return vars;
	}

	public static Script loadScript(Object scriptJSON) throws ParsingException {
		Script script = new Script();
		JSONArray scriptArray = (JSONArray) scriptJSON;
		int x = ((Number) scriptArray.remove(0)).intValue();
		int y = ((Number) scriptArray.remove(0)).intValue();

		script.setPosition(x, y);
		JSONArray jsonBlocks = (JSONArray) scriptArray.remove(0);

		List<Block> blocks = new ArrayList<Block>();

		Iterator blockIter = jsonBlocks.iterator();
		Block previous = null;
		try {
			while (blockIter.hasNext()) {
				Block current = null;

				current = loadBlock(blockIter.next());

				if (previous != null) {
					previous.setNextBlock(current);
				}
				current.setPreviousBlock(previous);
				current.setParent(script);
				blocks.add(current);
				previous = current;
			}
		} catch (ParsingException e) {
			if (!blocks.isEmpty()) {
				throw new ParsingException("Fail after parse [" + blocks.get(0)
						+ "..." + previous + "]", e);
			} else {
				throw new ParsingException("Fail parsing script JSON ["
						+ scriptJSON + "]", e);
			}

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
		BlockType blockSpec;

		if (command.equals("procDef")) { // CustomBlock
			return Block.makeCustomBlock(blockArray);
			// return new CustomBlock(blockArray);
		}

		if (command.equals("call")) { // CustomBlockType call
			String customSpecStr = (String) blockArray.remove(1);
			blockSpec = CommandLoader.COMMAND_TO_CUSTOM_BLOCKSPEC
					.get(customSpecStr);
			if (blockSpec == null) {
				throw new UndefinedBlockException("Custom Block: "
						+ customSpecStr + " is not defined");
			}
		} else { // Normal Block
			blockSpec = CommandLoader.COMMAND_TO_BLOCKSPEC.get(command);
			if (!command.equals("Position") && blockSpec == null) {
				throw new UndefinedBlockException("Block: " + command
						+ " is not defined");
			}
		}

		resultBlock.setCommand(command);
		resultBlock.setBlockType(blockSpec);
		blockArray.remove(0);

		Object arg = null;
		for (int argi = 0; argi < blockArray.size(); argi++) {
			if (blockArray.get(argi) instanceof JSONArray) {
				if (((JSONArray) blockArray.get(argi)).get(0) instanceof JSONArray) { // nested
																						// blocks
					arg = new ArrayList<Block>();// stack shape insert (nested
													// blocks) will be list of
													// blocks
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
			BlockType customBlockSpec = BlockType
					.parseCustomBlockSpec(firstBlockArray);
			CommandLoader.COMMAND_TO_CUSTOM_BLOCKSPEC.put(
					(String) firstBlockArray.get(1), customBlockSpec);
		} catch (Exception e) {
			throw new ParsingException(e);
		}

	}

}
