package vt.cs.smells.analyzer.analysis;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.visual.Node;
import vt.cs.smells.visual.NodeClass;

public class TestNodeClass {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		ArrayList<Object> args1 = new ArrayList<Object>();
		args1.add("msg1");
		Block b1 = new Block("whenIReceive", null, args1);
		
		ArrayList<Object> args2 = new ArrayList<Object>();
		args2.add("msg2");
		Block b2 = new Block("whenIReceive", null, args2);
		
		ArrayList<Object> args3 = new ArrayList<Object>();
		args3.add("msg1");
		Block b3 = new Block("whenIReceive", null, args3);
		
		
		Node n1 = new Node(b1,0,0);
		Node n2 = new Node(b2,0,0);
		Node n3 = new Node(b3,0,0);
		assertFalse(n1.getNodeClass().equals(n2.getNodeClass()));
		assertTrue(n1.getNodeClass().equals(n3.getNodeClass()));
		
		HashMap<NodeClass, Integer> map = new HashMap<>();
		map.put(n1.getNodeClass(), 1);
		map.put(n2.getNodeClass(), 3);
		map.put(n3.getNodeClass(), map.get(n3.getNodeClass())+1);
		
		assertEquals(map.get(n1.getNodeClass()), new Integer(2));
		assertEquals(map.get(n3.getNodeClass()), new Integer(2));
		assertEquals(map.get(n2.getNodeClass()), new Integer(3));
	}

}
