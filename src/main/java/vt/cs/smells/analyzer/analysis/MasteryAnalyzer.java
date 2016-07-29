package vt.cs.smells.analyzer.analysis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.AnalysisManager;
import vt.cs.smells.analyzer.Analyzer;
import vt.cs.smells.analyzer.DictAnalysisReport;
import vt.cs.smells.analyzer.Report;
import vt.cs.smells.analyzer.Report.ReportType;
import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.nodes.Scriptable;
import vt.cs.smells.analyzer.visitor.Identity;
import vt.cs.smells.analyzer.visitor.TopDown;
import vt.cs.smells.analyzer.visitor.VisitFailure;
import vt.cs.smells.analyzer.visitor.Visitor;

/*
 * Credit: Jesus Moreno Leon
 * Reimplementation of https://github.com/jemole/hairball/blob/master/hairball/plugins/mastery.py
 */

public class MasteryAnalyzer extends Analyzer {
	private static final String name = "MasteryScore";
	private static final String abbr = "MS";
	
	DictAnalysisReport report = new DictAnalysisReport(name,abbr);
	HashMap<String, Integer> blocks = new HashMap<String, Integer>();
	HashMap<String, Integer> concepts = new HashMap<String, Integer>();
	ArrayList<Script> allScripts = new ArrayList<Script>();
	int total = 0;

	private class BlockCollectorVisitor extends Identity {
		@Override
		public void visitBlock(Block block) throws VisitFailure {
			super.visitBlock(block);
			if (blocks.containsKey(block.getCommand())) {
				blocks.put(block.getCommand(),
						blocks.get(block.getCommand()) + 1);
			} else {
				blocks.put(block.getCommand(), 1);
			}
		}
	}

	@Override
	public void analyze() throws AnalysisException {
		Visitor v = new TopDown(new BlockCollectorVisitor());
		try {
			project.accept(v);
		} catch (VisitFailure e) {
			e.printStackTrace();
		}

		for (String scriptableName : project.getAllScriptables().keySet()) {
			Scriptable s = project.getAllScriptables().get(scriptableName);
			allScripts.addAll(s.getScripts());
		}

		try {
			synchronization(blocks);
			flowControl(blocks);
			abstraction(blocks);
			dataRepresentation(blocks);
			userInteractivity(blocks);
			logic(blocks);
			parallelization();
		} catch (Exception e) {
			throw new AnalysisException(e);
		}
		
		report.addRecord(concepts);
		
	}

	public void synchronization(HashMap<String, Integer> blocks) {
		int score = 0;
		if (blocks.containsKey("doWaitUntil")
				|| blocks.containsKey("startScene")
				|| blocks.containsKey("doBroadcastAndWait")) {
			score = 3;
		} else if (blocks.containsKey("broadcast:")
				|| blocks.containsKey("whenIReceive")
				|| blocks.containsKey("stopScripts")) {
			score = 2;
		} else if (blocks.containsKey("wait:elapsed:from:")) {
			score = 1;
		} else {
			score = 0;
		}
		concepts.put("Synchronization", score);
		total+=score;
	}

	public void flowControl(HashMap<String, Integer> blocks) {
		int score = 0;
		if (blocks.containsKey("doUntil")) {
			score = 3;
		} else if (blocks.containsKey("doRepeat")
				|| blocks.containsKey("doForever")) {
			score = 2;
		} else {
			for (Script s : allScripts) {
				if (s.getBlocks().size() > 1) {
					score = 1;
					break;
				}
			}
		}
		concepts.put("FlowControl", score);
		total+=score;
	}

	public void abstraction(HashMap<String, Integer> blocks) {
		int score = 0;
		if (blocks.containsKey("whenCloned")) {
			score = 3;
		} else if (blocks.containsKey("procDef")) {
			score = 2;
		} else if (project.getScriptCount() > 1) {
			score = 1;
		}
		concepts.put("abstraction", score);
		total+=score;
	}

	public void dataRepresentation(HashMap<String, Integer> blocks) {
		int score = 0;
		ArrayList<String> modifiers = new ArrayList<String>(Arrays.asList(
				"startScene", "nextScene", "lookLike:", "nextCostume",
				"turnRight:", "turnLeft:", "forward:", "heading:", "gotoX:y:",
				"glideSecs:toX:y:elapsed:from:", "changeXposBy:", "xpos:",
				"changeYposBy:", "ypos:", "changeSizeBy:", "setSizeTo:",
				"hide", "show", "setGraphicEffect:to:",
				"changeGraphicEffect:by:"));

		ArrayList<String> lists = new ArrayList<String>(Arrays.asList(
				"stringLength:", "showList:", "insert:at:ofList:",
				"deleteLine:ofList:", "append:toList:", "setLine:ofList:to:",
				"list:contains:", "hideList:", "getLine:ofList:"));

		for (String item : lists) {
			if (blocks.containsKey(item)) {
				concepts.put("DataRepresentation", 3);
				total+=3;
				return;
			}
		}

		if (blocks.containsKey("changeVar:by:")
				|| blocks.containsKey("setVar:to:")) {
			score = 2;
		} else {
			for (String modifier : modifiers) {
				if (blocks.containsKey(modifier)) {
					score = 1;
				}
			}
		}
		concepts.put("DataRepresentation", score);
		total+=score;
	}

	public void userInteractivity(HashMap<String, Integer> blocks) {
		int score = 0;
		ArrayList<String> proficiency = new ArrayList<String>(Arrays.asList(
				"setVideoState", "senseVideoMotion", "whenSensorGreaterThan",
				"setVideoTransparency", "soundLevel"));

		ArrayList<String> developing = new ArrayList<String>(Arrays.asList(
				"whenKeyPressed", "whenClicked", "mousePressed", "keyPressed:",
				"doAsk", "answer"));
		
		boolean havingProficiencyBlock = false;
		for (String item : proficiency) {
			if (blocks.containsKey(item)) {
				havingProficiencyBlock = true;
				break;
			}
		}
		
		
		boolean isDeveloping = false;
		for (String item : developing) {
			if (blocks.containsKey(item)) {
				isDeveloping = true;
				break;
			}
		}
		
		if(havingProficiencyBlock){
			score = 3;
		}else if(isDeveloping){
			score = 2;
		}else if (blocks.containsKey("whenGreenFlag")) {
			score = 1;
		}else{
			score = 0;
		}

		concepts.put("User Interactivity", score);
		total+=score;
	}

	public void logic(HashMap<String, Integer> blocks) {
		ArrayList<String> operations = new ArrayList<String>(Arrays.asList("&",
				"|", "not", "="));
		int score = 0;
		for (String operation : operations) {
			if (blocks.containsKey(operation)) {
				concepts.put("Logic", 3);
				score = 3;
				total+=score;
				return;
			}
		}
		
		if (blocks.containsKey("doIfElse")) {
			score = 2;
		} else if (blocks.containsKey("doIf")) {
			score = 1;
		}
		concepts.put("Logic", score);
		total+=score;
	}

	public void parallelization() {
		int score = 0;

		ArrayList<String> messages = new ArrayList<String>();
		ArrayList<String> backdrops = new ArrayList<String>();
		ArrayList<Pair<String, Object>> multimedia = new ArrayList<Pair<String, Object>>();
		ArrayList<String> keys = new ArrayList<String>();
		int greenFlag = 0;

		for (Script script : allScripts) {
			// 2 Scripts start on the same received message
			if (firstBlockOf(script).getCommand().equals("whenIReceive")
					&& script.getBlocks().size() > 1) {
				String message = firstBlockOf(script).arg("message");
				if (messages.contains(message)) {
					score = 3;
					break;
				} else {
					messages.add(message);
				}
			}

			// 2 Scripts start on the same backdrop change
			else if (firstBlockOf(script).getCommand()
					.equals("whenSceneStarts") && script.getBlocks().size() > 1) {
				String backdrop = firstBlockOf(script).arg("backdrop");
				if (backdrops.contains(backdrop)) {
					score = 3;
					break;
				} else {
					backdrops.add(backdrop);
				}
			}

			// 2 Scripts start on the same multimedia (video, audio, timer)
			// event
			else if (firstBlockOf(script).getCommand().equals(
					"whenSensorGreaterThan")
					&& script.getBlocks().size() > 1) {
				Pair<String, Object> multimediaEvent = Pair.of(
						(String) firstBlockOf(script).getArgs(0),
						firstBlockOf(script).getArgs(1));
				if (multimedia.contains(multimediaEvent)) {
					score = 3;
					break;
				} else {
					multimedia.add(multimediaEvent);
				}
			}

			// 2 Scripts on the same key pressed
			else if (firstBlockOf(script).getCommand().equals("whenKeyPressed")
					&& script.getBlocks().size() > 1) {
				String key = firstBlockOf(script).getArgs(0).toString();
				if (keys.contains(key)) {
					score = 2;
					break;
				} else {
					keys.add(key);
				}
			}

			// 2 scripts on greenFlag
			else if (firstBlockOf(script).getCommand().equals("whenGreenFlag")
					&& script.getBlocks().size() > 1) {
				greenFlag += 1;
				if (greenFlag > 1 && score == 0) {
					score = 1;
					break;
				}
			}
		}

		// Sprite with 2 scripts on clicked
		for (String scriptableName : project.getAllScriptables().keySet()) {
			Scriptable sprite = project.getAllScriptables().get(scriptableName);
			int clicked = 0;
			for (Script sc : sprite.getScripts()) {
				if (firstBlockOf(sc).hasCommand("whenClicked")
						&& sc.getBlocks().size() > 1) {
					clicked += 1;
				}
				if (clicked > 1) {
					score = Math.max(2, score);
				}
			}
		}

		concepts.put("Parallelization", score);
		total+=score;
	}

	private Block firstBlockOf(Script script) {
		return script.getBlocks().get(0);
	}

	@Override
	public Report getReport() {
		report.setReportType(ReportType.METRIC);
		JSONObject conciseReport = new JSONObject();
		conciseReport.put("total", total);
		report.setConciseJSONReport(conciseReport);
		return report;
	}
	

}
