package cs.vt.analysis.analyzer.analysis;

import org.json.simple.JSONObject;


public abstract class Report<T> {
	public enum ReportType {SMELL, METRIC}
	private int projectID;
	
	ReportType type = ReportType.SMELL;
	String title;
	T result;
	
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
	public abstract T getResult();
	
	public void setReportType(ReportType type){
		this.type = type;
	}
	
	public ReportType getReportType(){
		return type;
	}
}
