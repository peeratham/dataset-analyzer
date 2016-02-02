package cs.vt.analysis.analyzer.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

public class AnalysisReport {


	private int projectID;
	String title;
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	Map<String,Object> summary;
	List<String> result;
	
	public AnalysisReport(){
		result = new ArrayList<String>();
		summary = new HashMap<String, Object>();
	}
	
	public int getProjectID() {
		return projectID;
	}

	public void setProjectID(int projectID) {
		this.projectID = projectID;
		summary.put("projectID", projectID);
	}




	private void generateSummary() {
		summary.put("count", result.size());
	}
	
	public JSONObject getJSONReport(){
		generateSummary();
		JSONObject container = new JSONObject();
		JSONObject report = new JSONObject();
		container.put("Analysis",title);
		report.put("Summary", summary);
		report.put("Records", result);
		
		
		container.put("Report",report);
		
		return container;
	}
	

	
	public void addRecord(String record) {
		result.add(record);
	}
}
