package vt.cs.smells.analyzer;

import java.util.HashMap;

import org.json.simple.JSONObject;

public class DictAnalysisReport extends Report<HashMap> {
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

	@Override
	public HashMap getResult() {
		return result;
	}

}
