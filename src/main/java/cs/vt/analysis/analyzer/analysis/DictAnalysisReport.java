package cs.vt.analysis.analyzer.analysis;

import java.util.HashMap;

import org.json.simple.JSONObject;

public class DictAnalysisReport extends Report {
	HashMap<String, Object> result = null;
	
	@Override
	public JSONObject getJSONReport() {
		JSONObject container = new JSONObject();
		container.put("name",title);
		container.put("records", result);
		return container;
	}

	@Override
	public void addRecord(Object record) {
		result = (HashMap<String, Object>) record;
		
	}

}
