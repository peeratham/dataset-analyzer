package cs.vt.analysis.analyzer;

import org.json.simple.JSONArray;

import cs.vt.analysis.analyzer.nodes.ScratchProject;

public interface Extractor {

	public abstract void extract(ScratchProject scratchProject);

	public abstract String generateCSVOutput();

}