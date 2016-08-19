package vt.cs.smells.analyzer.nodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;

import vt.cs.smells.analyzer.visitor.VisitFailure;
import vt.cs.smells.analyzer.visitor.Visitor;

public class Scriptable implements Visitable {
	String name;
	ArrayList<Script> scripts;
	private Map<String,Object> variables;
	private ArrayList<String> costumes;
	private Map<String, List<String>> lists = new HashMap<>();
	private JSONArray scriptComments = new JSONArray();

	public Scriptable(){
		scripts = new ArrayList<Script>();
		variables = new HashMap<String, Object>();
		costumes = new ArrayList<String>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addScript(Script script) {
		scripts.add(script);
		
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Script scrpt : scripts) {
			sb.append(scrpt);
			sb.append("\n");
		}
		return sb.toString();
	}

	public Script getScript(int index){
		return scripts.get(index);
	}
	
	public ArrayList<Script> getScripts(){
		return scripts;
	}

	public void accept(Visitor v) throws VisitFailure {
		v.visitScriptable(this);
		
	}

	public int getNumScripts() {
		return scripts.size();
	}
	
	public void addVar(String name, Object value) {
		variables.put(name, value);
	}
	
	public Map<String, Object> getAllVariables() {
		return variables;
	}

	public void setVars(Map<String, Object> loadVariables) {
		this.variables = loadVariables;
		
	}

	public ArrayList<String> getCostumes() {
		return this.costumes;
	}

	public void setCostumes(ArrayList<String> costumes) {
		this.costumes.addAll(costumes);
		
	}

	public Map<String, List<String>> getAllList() {
		return lists;
	}
	
	public List<String> getList(String listName){
		return lists.get(listName);
	}
	

	public void setLists(Map<String, List<String>> listMap) {
		this.lists = listMap;
		
	}

	public JSONArray getScriptComments() {
		return this.scriptComments ;
	}

	public void setScriptComments(JSONArray comments) {
		this.scriptComments = comments;
		
	}
	

}
