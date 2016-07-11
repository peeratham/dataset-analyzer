package vt.cs.smells.analyzer;

import org.json.simple.JSONObject;


public abstract class Report<T> {
	public enum ReportType {SMELL, METRIC}
	private int projectID;
	
	ReportType type = ReportType.SMELL;
	String name;
	T result;
	public Report(String name, String abbr){
		this.name = name;
		this.abbr = abbr;
	}

	protected JSONObject conciseJSONReport = new JSONObject();

	protected String abbr;
	
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
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
	public void setConciseJSONReport(JSONObject conciseJSONObj){
		this.conciseJSONReport = conciseJSONObj;
	}
	public abstract JSONObject getConciseJSONReport();
	
	public void setReportType(ReportType type){
		this.type = type;
	}
	
	public ReportType getReportType(){
		return type;
	}
	public JSONObject getJSONReport(boolean concise) {
		if(concise){
			return getConciseJSONReport();
		}else{
			return getJSONReport();
		}
	}
	
	public void setAbbr(String abbr) {
		this.abbr = abbr;
		
	}
	
	@Override
	public String toString() {
		return "Report [type=" + type + ", name=" + name + ", result="
				+ result + "]";
	}
	
	
}
