package cs.vt.analysis.analyzer.nodes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import cs.vt.analysis.analyzer.parser.CommandLoader;
import cs.vt.analysis.analyzer.parser.Parser;
import cs.vt.analysis.analyzer.visitor.VisitFailure;
import cs.vt.analysis.analyzer.visitor.Visitor;

public class ScratchProject implements Visitable{
	int projectID;
	private Map<String, Scriptable> scriptables;
	static Parser parser;
	
	public int getProjectID(){
		return projectID;
	}

	public void addScriptable(String name, Scriptable obj) {
		this.scriptables.put(name, obj);
	}

	public ScratchProject(){
		scriptables = new HashMap<String,Scriptable>();
		parser = new Parser();
	}
	
	public Scriptable getScriptable(String name) {
		return scriptables.get(name);
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

	public static ScratchProject loadProject(String jsonInputString) throws ParseException {
		JSONParser jsonParser = new JSONParser();
		Object obj = jsonParser.parse(jsonInputString);
		JSONObject jsonObject = (JSONObject) obj;
		
		return loadProject(jsonObject);
	}

	public static ScratchProject loadProject(JSONObject jsonObject) {
		ScratchProject project = new ScratchProject();
		CommandLoader.loadCommand();
		JSONObject stageObj = jsonObject;
		
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
					} catch(Exception e){
						System.err.println("Error Parsing CustomBlocks in Scriptable: Stage");
						System.err.println(e);
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
				} catch(Exception e){
					System.err.println("Error Parsing Scriptable: Stage");
					System.err.println(e);
				}
			}
			project.addScriptable("Stage", stage);
		}
		
		if(stageObj.containsKey("info")){
			JSONObject infoObj = (JSONObject)stageObj.get("info");
			int projectID = Integer.parseInt((String)((JSONObject)infoObj).get("projectID"));
			project.setProjectID(projectID);
		}
		
		JSONArray children = (JSONArray)jsonObject.get("children");
		
		for (int i = 0; i < children.size(); i++) {
			JSONObject sprite = (JSONObject) children.get(i);
			if(!sprite.containsKey("objName")){ //not a sprite
				continue;
			}
			Scriptable s = new Scriptable();
			String spriteName = (String)sprite.get("objName");
			JSONArray scripts = (JSONArray)sprite.get("scripts");
			
			s.setName(spriteName);
			if(scripts==null){	//empty script
				project.addScriptable(spriteName, s);
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
						
					}catch(Exception e){
						System.err.println("Error Parsing Custom Block for Scriptable:"+spriteName);
						e.printStackTrace();
					}
				}
			}
			
			//parse script
			for (int j = 0; j < scripts.size(); j++) {
				Script scrpt=null;
				try{
					scrpt = parser.loadScript(scripts.get(j));
					s.addScript(scrpt);
				}catch(Exception e){
					System.err.println("Error Parsing Scriptable:"+spriteName);
					System.err.println("Index:"+j+" json:"+scripts.get(j));
					e.printStackTrace();
				}
			}
			project.addScriptable(spriteName, s);
		}
		return project;
	}

	private void setProjectID(int projectID) {
		this.projectID = projectID;
		
	}

	public Map<String, Scriptable> getScriptables() {
		return scriptables;
		
	}

	public void accept(Visitor v) throws VisitFailure {
		v.visitProject(this);
		
	}

}
