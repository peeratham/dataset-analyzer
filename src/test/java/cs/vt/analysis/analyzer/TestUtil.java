package cs.vt.analysis.analyzer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.parser.Parser;

public class TestUtil {
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
	
	public static JSONObject getScriptable(String inputString, String name) throws ParseException {
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