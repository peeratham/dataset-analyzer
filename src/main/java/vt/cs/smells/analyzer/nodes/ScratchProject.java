package vt.cs.smells.analyzer.nodes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import vt.cs.smells.analyzer.parser.Parser;
import vt.cs.smells.analyzer.parser.ParsingException;
import vt.cs.smells.analyzer.visitor.VisitFailure;
import vt.cs.smells.analyzer.visitor.Visitor;


public class ScratchProject implements Visitable{
	static Logger logger = Logger.getLogger(ScratchProject.class);
	
	public static Parser parser;
	int projectID;
	private Map<String, Scriptable> scriptables;

	private Long scriptCount;

	private Long spriteCount;
	
	
	public ScratchProject(){
		scriptables = new HashMap<String,Scriptable>();
		parser = new Parser();
	}
	public void setProjectID(int projectID) {
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
	
	public static ScratchProject loadProject(JSONObject jsonObject) throws ParseException, ParsingException {
		return Parser.loadProject(jsonObject);
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
	public Long getScriptCount() {
		return this.scriptCount;
	}
	public Long getSpriteCount() {
		return this.spriteCount;
	}
	public void setScriptCount(Long long1) {
		this.scriptCount = long1;
	}
	public void setSpriteCount(Long long1) {
		this.spriteCount = long1;
		
	}

}
