package cs.vt.analysis.analyzer.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ListAnalysisReport extends Report<JSONArray> {

	private int projectID;
	String title;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	Map<String,Object> summary;
	public Map<String, Object> getSummary() {
		return summary;
	}

	public void setSummary(Map<String, Object> summary) {
		this.summary = summary;
	}

	JSONArray resultJSON;
	
	public ListAnalysisReport(){
		resultJSON = new JSONArray();
		summary = new HashMap<String, Object>();
	}
	
	public int getProjectID() {
		return projectID;
	}

	public void setProjectID(int projectID) {
		this.projectID = projectID;
	}

	


	private void generateSummary() {
		summary.put("count", resultJSON.size());
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getJSONReport(){
		generateSummary();
		JSONObject container = new JSONObject();
		container.put("name",title);
		JSONObject report = new JSONObject();
			report.put("instances", resultJSON);
			report.put("count", resultJSON.size());
		container.put("records", report);
		return container;
	}
	
	@SuppressWarnings("unchecked")
	public void addRecord(Object record) {
		if(!resultJSON.contains(record)){
			resultJSON.add(record);
		}
		
	}

	@Override
	public JSONArray getResult() {
		return resultJSON;
	}
}
