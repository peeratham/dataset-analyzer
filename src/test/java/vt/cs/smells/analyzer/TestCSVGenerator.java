package vt.cs.smells.analyzer;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestCSVGenerator {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws IOException {
		CSVGenerator gen = new CSVGenerator();
		gen.setColumn(new String[]{"a","b"});
		gen.addLine(new Object[]{1, 2.2});
		gen.addLine(new Object[]{2, 3.2});
		String result = gen.generateCSV();
		System.out.println(result);
	}

}
