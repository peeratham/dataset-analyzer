package vt.cs.smells.analyzer.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import vt.cs.smells.analyzer.AnalysisManager;
import vt.cs.smells.analyzer.parser.Util;


public class TestUtils {
	public static JSONParser jsonParser = new JSONParser();

	public static JSONArray getScripts(String inputString, String name) throws ParseException {
		JSONObject jsonObject = (JSONObject) jsonParser.parse(inputString);
		JSONArray children = (JSONArray)jsonObject.get("children");
		JSONObject sprite = null;
		for (int i = 0; i < children.size(); i++) {
			sprite = (JSONObject) children.get(i);
			if(!sprite.containsKey("objName")){ //not a sprite
				continue;
			}
			String spriteName = (String)sprite.get("objName");
			if(spriteName.equals(name)){
				JSONArray scripts = (JSONArray)sprite.get("scripts");
				return scripts;
			}
		}
		return null;
		
	}
	
	public static JSONObject getJSONScriptable(String inputString, String name) throws ParseException {
		JSONObject jsonObject = (JSONObject) jsonParser.parse(inputString);
		if(name.equals("Stage")){
			return jsonObject;
		}
		
		JSONArray children = (JSONArray)jsonObject.get("children");
		JSONObject sprite = null;

		for (int i = 0; i < children.size(); i++) {
			sprite = (JSONObject) children.get(i);
			if(!sprite.containsKey("objName")){ //not a sprite
				continue;
			}
			String spriteName = (String)sprite.get("objName");
			if(spriteName.equals(name)){
				return sprite;
			}
		}
		return null;
		
	}
	
	
	
	public static int count(String word, String line){
	    Pattern pattern = Pattern.compile(word);
	    Matcher matcher = pattern.matcher(line);
	    int counter = 0;
	    while (matcher.find())
	        counter++;
	    return counter;
	}
	


}