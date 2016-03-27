package cs.vt.analysis.analyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.nodes.Script;
import cs.vt.analysis.analyzer.nodes.Scriptable;
import cs.vt.analysis.analyzer.parser.ParsingException;
import cs.vt.analysis.ml.ReportToCSVFormatter;
import cs.vt.analysis.visual.DistanceMeasure;
import cs.vt.analysis.visual.ScriptProperty;
import cs.vt.analysis.visual.VisualMeasure;

public class DistanceFeatureExtractor implements Extractor {
	static final String OUTPUT_DIR = "C:\\Users\\Peeratham\\workspace\\feature-selection-dataset";
	static final String INPUT_DIR = "C:\\Users\\Peeratham\\workspace\\scratch-dataset";

	ArrayList<String> attributeOrdering = new ArrayList<String>(Arrays.asList(
			"xA", "yA", "xB", "yB", "userDist", "varDist", "methodDist",
			"hatBlockType", "message", "horizontalAligned", "verticalAligned"));

	JSONArray instances = new JSONArray();

	@SuppressWarnings("unchecked")
	@Override
	public void extract(ScratchProject scratchProject) {
		for (String scriptableName : scratchProject.getAllScriptables()
				.keySet()) {
			Scriptable scriptable = scratchProject.getAllScriptables().get(
					scriptableName);
			ArrayList<Script> scripts = scriptable.getScripts();

			DistanceMeasure coordBasedDist = new DistanceMeasure.CoordinateBased();
			DistanceMeasure varBasedDist = new DistanceMeasure.SharedVariableBased();
			DistanceMeasure HatTypeSimilarity= new DistanceMeasure.BlockHatBased();
			DistanceMeasure sharedBlockDist= new DistanceMeasure.SharedBlockBased();
			for (int i = 0; i < scripts.size(); i++) {
				for (int j = 0; j < scripts.size(); j++) {
					if (i != j) {
						JSONObject instance = new JSONObject();
						ScriptProperty pA = new ScriptProperty(scripts.get(i));
						ScriptProperty pB = new ScriptProperty(scripts.get(j));
						instance.put("xA", pA.getCoordinate().getX());
						instance.put("yA", pA.getCoordinate().getY());
						instance.put("xB", pB.getCoordinate().getX());
						instance.put("yB", pB.getCoordinate().getY());
						instance.put("userDist", coordBasedDist.getDist(pA, pB));
						instance.put("varDist", varBasedDist.getDist(pA, pB));
						instance.put("methodDist",
								sharedBlockDist.getDist(pA, pB));
						instance.put("hatBlockType",
								HatTypeSimilarity.getDist(pA, pB));
						// instance.put("message", 5);

						int horizontalAligned = VisualMeasure
								.isHorizontalAligned(pA, pB) ? 1 : 0;
						instance.put("horizontalAligned", horizontalAligned);
						int verticalAligned = VisualMeasure
								.isVerticalAligned(pA, pB) ? 1 : 0;
						instance.put("verticalAligned", verticalAligned);
						instances.add(instance);
					}
				}

			}
		}

	}

	@Override
	public String generateCSVOutput() {
		ReportToCSVFormatter formatter = new ReportToCSVFormatter();
		formatter.setAttributeOrder(attributeOrdering);
		for (int i = 0; i < instances.size(); i++) {
			formatter.addInstance((JSONObject) instances.get(i));
		}
		return formatter.generateCSVString();
	}
	
	public static void main(String[] args){
		Extractor extractor = new DistanceFeatureExtractor();
		File inputDirPath = new File(INPUT_DIR);
		File baseOutputPath = new File(OUTPUT_DIR);
		
		ArrayList<String> dataset = new ArrayList<String>(Arrays.asList(
				"78704342"));
		for (String projectID : dataset) {
			File projectPath = new File(inputDirPath, projectID+".json");
			String projectSrc = null;
			try {
				projectSrc = FileUtils.readFileToString(projectPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ScratchProject project = null;
			try {
				project = ScratchProject.loadProject(projectSrc);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParsingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			extractor.extract(project);
		}
		
		String result = extractor.generateCSVOutput();
		File outputPath = new File(baseOutputPath,"out.csv");
		try {
			FileUtils.writeStringToFile(outputPath , result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
