package cs.vt.analysis.analyzer.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

public class ListAnalysisReport extends Report {

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

	List<String> result;
	
	public ListAnalysisReport(){
		result = new ArrayList<String>();
		summary = new HashMap<String, Object>();
	}
	
	public int getProjectID() {
		return projectID;
	}

	public void setProjectID(int projectID) {
		this.projectID = projectID;
	}

	public int getRecordCounts(){
		return result.size();
	}


	private void generateSummary() {
		summary.put("count", result.size());
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getJSONReport(){
		generateSummary();
		JSONObject container = new JSONObject();
		container.put("name",title);
		container.put("records", result);
		return container;
	}
	
	public void addRecord(Object record) {
		if(!result.contains(record)){
			result.add(record.toString());
		}
		
	}
}
