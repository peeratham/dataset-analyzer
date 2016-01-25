package cs.vt.analysis.analyzer;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs.vt.analysis.analyzer.nodes.ScratchProject;
import cs.vt.analysis.analyzer.nodes.Scriptable;

public class ScratchProjectTest {
	JSONParser jsonParser = new JSONParser();
	ScratchProject project;
	@Before
	public void setUp() throws Exception {
		try {
			InputStream in = Main.class.getClassLoader().getResource("project03.json").openStream();
			Object obj = jsonParser.parse((new BufferedReader(new InputStreamReader(in))));
            JSONObject jsonObject = (JSONObject) obj;
            project = new ScratchProject().loadProject(jsonObject);

        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLoadProject() {
		Map<String, Scriptable> scriptables= project.getScriptables();
		assertEquals(3, scriptables.size());
	}

}
