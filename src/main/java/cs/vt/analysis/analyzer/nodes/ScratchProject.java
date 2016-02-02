package cs.vt.analysis.analyzer.nodes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import cs.vt.analysis.analyzer.parser.CommandLoader;
import cs.vt.analysis.analyzer.parser.Parser;
import cs.vt.analysis.analyzer.parser.ParsingException;
import cs.vt.analysis.analyzer.visitor.VisitFailure;
import cs.vt.analysis.analyzer.visitor.Visitor;


public class ScratchProject implements Visitable{
	static Logger logger = Logger.getLogger(ScratchProject.class);
	
	static Parser parser;
	int projectID;
	private Map<String, Scriptable> scriptables;
	
	
	public ScratchProject(){
		scriptables = new HashMap<String,Scriptable>();
		parser = new Parser();
	}
	private void setProjectID(int projectID) {
		this.projectID = projectID;
	}
	
	public int getProjectID(){
		return projectID;
	}

	public void addScriptable(String name, Scriptable obj) {
		this.scriptables.put(name, obj);
	}
	
	public Scriptable getScriptable(String name) {
		return scriptables.get(name);
	}
	
	public Map<String, Scriptable> getAllScriptables() {
		return scriptables;
	}
	
	public static ScratchProject loadProject(String jsonInputString) throws ParseException, ParsingException {
		JSONParser jsonParser = new JSONParser();
		Object obj = jsonParser.parse(jsonInputString);
		JSONObject jsonObject = (JSONObject) obj;
		
		return loadProject(jsonObject);
	}

	private static ScratchProject loadProject(JSONObject jsonObject) throws ParsingException {
		ScratchProject project = new ScratchProject();
		CommandLoader.loadCommand();
		JSONObject stageObj = jsonObject;
		
		if(stageObj.containsKey("info")){
			JSONObject infoObj = (JSONObject)stageObj.get("info");
			if((String)((JSONObject)infoObj).get("projectID")!=null){
				int projectID = Integer.parseInt((String)((JSONObject)infoObj).get("projectID"));
				project.setProjectID(projectID);
				logger.info("Load projectID:"+projectID);
			}else{
				throw new ParsingException("Project ID Not Found");
			}
		}
		
		if(stageObj.containsKey("scripts")){
			JSONArray stageScripts = (JSONArray)stageObj.get("scripts");
			Scriptable stage = new Scriptable();
			
			//first parse any custom blocks
			for (int j = 0; j < stageScripts.size(); j++) {
				JSONArray scriptJSON = (JSONArray) ((JSONArray)stageScripts.get(j)).get(2);
				JSONArray firstBlockJSON = (JSONArray) scriptJSON.get(0);
				String command = (String) firstBlockJSON.get(0);
				
				if(command.equals("procDef")){
					try{						
						parser.loadCustomBlock(firstBlockJSON);
					} catch(ParsingException e){
						logger.error("Error parsing a custom block in Stage:"+firstBlockJSON);
						throw new ParsingException(e);
					}
				}
				
			}
			//scripts
			for (int j = 0; j < stageScripts.size(); j++) {
				Script scrpt=null;
				try{
					scrpt = parser.loadScript(stageScripts.get(j));
					stage.setName("Stage");
					stage.addScript(scrpt);
					scrpt.setParent(stage);
				} catch(Exception e){
					logger.error("Error parsing a script in Stage:"+stageScripts.get(j));
					throw new ParsingException(e);
				}
			}
			project.addScriptable("Stage", stage);
		}
		
		JSONArray children = (JSONArray)jsonObject.get("children");
		
		for (int i = 0; i < children.size(); i++) {
			JSONObject spriteJSON = (JSONObject) children.get(i);
			if(!spriteJSON.containsKey("objName")){ //not a sprite
				continue;
			}
			Scriptable sprite = new Scriptable();
			String spriteName = (String)spriteJSON.get("objName");
			JSONArray scripts = (JSONArray)spriteJSON.get("scripts");
			
			sprite.setName(spriteName);
			if(scripts==null){	//empty script
				project.addScriptable(spriteName, sprite);
				continue;
			}
			
			//parse custom block for each sprite first
			for (int j = 0; j < scripts.size(); j++) {
				
				JSONArray scriptJSON = (JSONArray) ((JSONArray)scripts.get(j)).get(2);
				JSONArray firstBlockJSON = (JSONArray) scriptJSON.get(0);
				String command = (String) firstBlockJSON.get(0);
				
				if(command.equals("procDef")){
					try{
						parser.loadCustomBlock(firstBlockJSON);
						
					}catch(ParsingException e){
						logger.error("Error Parsing Custom Block in Scriptable:"+spriteName);
						logger.error("=>"+firstBlockJSON);
						throw new ParsingException(e);
					}
				}
			}
			
			//parse script
			for (int j = 0; j < scripts.size(); j++) {
				Script scrpt=null;
				String scriptStr = scripts.get(j).toString();
				try{
					scrpt = parser.loadScript(scripts.get(j));
					sprite.addScript(scrpt);
					scrpt.setParent(sprite);
				}catch(ParsingException e){
					logger.error("Error Parsing a script in Scriptable:"+spriteName);
					logger.error("=>"+scriptStr);
					logger.error("==>"+e.getMessage());
					throw new ParsingException(e);
				}
			}
			project.addScriptable(spriteName, sprite);
		}
		return project;
	}

	public void accept(Visitor v) throws VisitFailure {
		v.visitProject(this);		
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator it = scriptables.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, Scriptable> pair = (Map.Entry<String, Scriptable>)it.next();
			sb.append(pair.getKey());
			sb.append("\n");
			sb.append(pair.getValue());
			sb.append("\n\n");
		}
		return "Project:"+projectID+"\n\n"+sb.toString();
	}

}
