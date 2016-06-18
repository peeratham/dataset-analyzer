package vt.cs.smells.analyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class CSVGenerator {

	private String[] columns;
	private List<List<Object>> lines;
	
	public CSVGenerator(){
		this.lines = new ArrayList<>();
	}

	public void setColumn(String[] columns) {
		this.columns = columns;
		
	}

	public void addLine(Object[] record) {
		lines.add(Arrays.asList(record));
	}
	


	public String generateCSV() {
		StringBuilder sb = new StringBuilder();
		for(String col : columns){
			sb.append(col);
			sb.append(",");
		}
		sb.append("\n");
		
		for(List<Object> line: lines){
			for(Object val: line){
				sb.append(val);
				sb.append(",");
			}
			sb.append("\n");
		}
		return sb.toString();
		
	}

}
