package vt.cs.smells.analyzer;

import java.util.HashMap;

import org.json.simple.JSONObject;

public class DictAnalysisReport extends Report<HashMap> {
	public DictAnalysisReport(String name, String abbr) {
		super(name, abbr);
	}

	HashMap<String, Object> result = null;
	
	@Override
	public JSONObject getJSONReport() {
		JSONObject container = new JSONObject();
		container.put("name",name);
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

	@Override
	public JSONObject getConciseJSONReport() {
		JSONObject container = new JSONObject();
		container.put("name",abbr);
		container.put("records", conciseJSONReport);
		return container;
	}

}
