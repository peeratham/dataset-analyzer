package vt.cs.smells.analyzer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.parser.Util;
import vt.cs.smells.analyzer.visitor.Printer;
import vt.cs.smells.analyzer.visitor.TopDown;
import vt.cs.smells.analyzer.visitor.VisitFailure;
import vt.cs.smells.analyzer.visitor.Visitor;

public class TestVisitorTraversal {
	

	private ScratchProject project;

	@Before
	public void setUp() throws Exception {
		String projectSrc = Util.retrieveProjectOnline(96445907);
		project = ScratchProject.loadProject(projectSrc);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTraversalForCShape() throws VisitFailure {
		Visitor v = new TopDown(new Printer());
		project.accept(v);
	}

}
