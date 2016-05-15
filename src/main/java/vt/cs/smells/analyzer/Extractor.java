package vt.cs.smells.analyzer;

import org.json.simple.JSONArray;

import vt.cs.smells.analyzer.nodes.ScratchProject;

public interface Extractor {

	public abstract void extract(ScratchProject scratchProject);

	public abstract String generateCSVOutput();

}