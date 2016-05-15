package vt.cs.smells.ml;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringJoiner;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;

public class ReportToCSVFormatter {
	ArrayList<String> attributes = new ArrayList<String>(Arrays.asList(""));
	private static final String VISUAL_DATASET_DIR = "C:\\Users\\Peeratham\\workspace\\feature-selection-dataset";
	StringBuilder sb = new StringBuilder();
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		ReportToCSVFormatter formatter = new ReportToCSVFormatter();
		
		File f = new File(VISUAL_DATASET_DIR, "dat-01.csv");

		ArrayList<String> attributeOrdering = new ArrayList<String>(Arrays.asList(
				"userDist", "varDist","methodDist","hatBlockType", "message", "horizontalAligned", "verticalAligned"
				));
		
		formatter.setAttributeOrder(attributeOrdering);
		JSONObject instance = new JSONObject();
			instance.put("userDist", 1);
			instance.put("varDist", 2);
			instance.put("methodDist", 3);
			instance.put("hatBlockType", 4);
			instance.put("message", 5);
			instance.put("horizontalAligned", 6);
			instance.put("verticalAligned", 7);
		
		
		formatter.addInstance(instance);
		formatter.addInstance(instance);

		FileUtils.writeStringToFile(f, formatter.generateCSVString());
	}
	
	public void addInstance(JSONObject instance) {
		sb.append(formatArrayListToCSV(instance, this.attributes));
		sb.append("\n");
		
	}

	public void setAttributeOrder(ArrayList<String> attributes){
		this.attributes = attributes;
	}
	
	public String formatArrayListCSVLine(ArrayList<String> arr){
		StringJoiner headJoiner = new StringJoiner(",");
		for (String attr : arr) {
			headJoiner.add(attr);
		}
		return headJoiner.toString();
	}
	
	public String formatArrayListToCSV(JSONObject instance, ArrayList<String> attributeOrdering){
		StringJoiner joiner = new StringJoiner(",");
		for (String attr : attributeOrdering) {
			joiner.add((CharSequence) (instance.get(attr)+""));
		}
		return joiner.toString();
		
	}
	
	public String generateCSVString(){
		String header = formatArrayListCSVLine(attributes);
		String csv = header+"\n"+sb.toString();
		return csv;
	}
	 
	
}
