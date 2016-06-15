package vt.cs.smells.crawler;

import java.util.ArrayList;

import org.json.simple.JSONObject;


public class Creator {

	private String creator;
	ArrayList<Integer> projectsCreated = new ArrayList<Integer>();
	private JSONObject masteryReport;
	
	public Creator(String name) {
		this.creator = name;
	}

	public void addProjectID(int projectID) {
		projectsCreated.add(projectID);
	}

	public void setMasteryReport(JSONObject masteryReport) {
		this.masteryReport = masteryReport;
	}

	@Override
	public String toString() {
		return "Creator ["
				+ (creator != null ? "creator=" + creator + ", " : "")
				+ (projectsCreated != null ? "projectsCreated="
						+ projectsCreated + ", " : "")
				+ (masteryReport != null ? "masteryReport=" + masteryReport
						: "") + "]";
	}

	public JSONObject toJSONObj() {
		JSONObject doc = new JSONObject();
		doc.put("_id", creator);
		doc.put("projects_created", projectsCreated);
		doc.put("mastery", masteryReport);
		return doc;
	}
	
	

}
