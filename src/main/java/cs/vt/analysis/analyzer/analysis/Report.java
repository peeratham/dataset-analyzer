package cs.vt.analysis.analyzer.analysis;

import org.json.simple.JSONObject;

public abstract class Report {
	private int projectID;
	String title;
	
	public String getTitle(){
		return title;
	}
	public void setTitle(String title){
		this.title = title;
	}
	public int getProjectID(){
		return projectID;
	}
	public void setProjectID(int projectID){
		this.projectID = projectID;
	}
	public abstract JSONObject getJSONReport();
	public abstract void addRecord(Object record);
}
