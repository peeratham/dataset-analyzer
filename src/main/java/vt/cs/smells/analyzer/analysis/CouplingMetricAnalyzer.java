package vt.cs.smells.analyzer.analysis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;
import org.json.simple.parser.ParseException;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.AnalysisManager;
import vt.cs.smells.analyzer.Analyzer;
import vt.cs.smells.analyzer.ListAnalysisReport;
import vt.cs.smells.analyzer.Report;
import vt.cs.smells.analyzer.analysis.CouplingMetricAnalyzer.Node;
import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.nodes.Scriptable;
import vt.cs.smells.analyzer.parser.ParsingException;
import vt.cs.smells.analyzer.parser.Util;
import vt.cs.smells.select.Collector;
import vt.cs.smells.select.Evaluator;

public class CouplingMetricAnalyzer extends Analyzer {
	private static final String name = "IntensiveCoupling";
	private static final String abbr = "IC";
	private static final int SHORT_TERM_MEM = 7;
	
	ListAnalysisReport report = new ListAnalysisReport(name, abbr);
	HashMap<String, HashSet<Node>> msgSenders = new HashMap<>();
	HashMap<String, HashSet<Node>> msgReceivers = new HashMap<>();
	static Graph<Node, Message> graph = new DirectedSparseMultigraph<Node, Message>();
	static Graph<String, Message> couplingGraph = new DirectedSparseMultigraph<>();
	

	class Node {
		String sprite;
		String script;

		@Override
		public String toString() {
			return sprite + script;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Node && ((Node) o).script.equals(this.script) && ((Node) o).sprite.equals(this.sprite)) {
				return true;
			}
			return false;
		}

		@Override
		public int hashCode() {
			int hash = 1;
			hash = hash * 31 + this.sprite.hashCode();
			hash = hash * 31 + this.script.hashCode();
			return hash;
		}
	}

	class Message {
		String message;
		int index;

		@Override
		public String toString() {
			return message;
		}

		@Override
		public boolean equals(Object o) {
			boolean sameIndex = ((Message) o).index == this.index;
			return (o instanceof Message && ((Message) o).message.equals(this.message) && sameIndex);
		}

		@Override
		public int hashCode() {
			int hash = 3;
			hash = hash * 31 + this.message.hashCode();
			hash = hash * 31 + new Integer(this.index).hashCode();
			return hash;
		}
	}

	@Override
	public void analyze() throws AnalysisException {
		ArrayList<String> broadcastCommands = new ArrayList<String>();
		broadcastCommands.add("doBroadcastAndWait");
		broadcastCommands.add("broadcast:");
		String receiveCmd = "whenIReceive";
		for (String scriptableName : project.getAllScriptables().keySet()) {
			Scriptable scriptable = project.getScriptable(scriptableName);
			
			HashSet<Block> broadcastBlocks = new HashSet<>();
			for (String cmdName : broadcastCommands) {
				ArrayList<Block> atemp = Collector.collect(new Evaluator.BlockCommand(cmdName), scriptable);
				broadcastBlocks.addAll(atemp);
			}
			
			HashSet<Block> receiveBlocks = new HashSet<>();
			receiveBlocks.addAll(Collector.collect(new Evaluator.BlockCommand(receiveCmd), scriptable));
			
			
			
			for (Block b : broadcastBlocks) {
				String msg = b.arg("message");
				if (!msgSenders.containsKey(msg)) {
					msgSenders.put(msg, new HashSet<>());
				}
				HashSet<Node> broadcasts = msgSenders.get(msg);
				Node senderNode = new Node();
				int[] pos = b.getBlockPath().getScript().getPosition();
				senderNode.script = "@" + pos[0] + "," + pos[1];
				senderNode.sprite = b.getBlockPath().getScriptable().getName();
				broadcasts.add(senderNode);
				msgSenders.put(msg, broadcasts);
			}

			for (Block b : receiveBlocks) {
				String msg = b.arg("message");
				if (!msgReceivers.containsKey(msg)) {
					msgReceivers.put(msg, new HashSet<>());
				}
				HashSet<Node> receivers = msgReceivers.get(msg);
				Node receiverNode = new Node();
				int[] pos = b.getBlockPath().getScript().getPosition();
				receiverNode.script = "@" + pos[0] + "," + pos[1];
				receiverNode.sprite = b.getBlockPath().getScriptable().getName();
				receivers.add(receiverNode);

				msgReceivers.put(msg, receivers);
			}
		}
		
		for (String msg : msgSenders.keySet()) {
			int counter = 0;
			for (Node sender : msgSenders.get(msg)) {
				graph.addVertex(sender);
				couplingGraph.addVertex(sender.sprite);
				for (Node receiver : msgReceivers.get(msg)) {
					graph.addVertex(receiver);
					couplingGraph.addVertex(receiver.sprite);
					Message msgObj = new Message();
					msgObj.message = msg;
					msgObj.index = counter;
					graph.addEdge(msgObj, sender, receiver, EdgeType.DIRECTED);
					couplingGraph.addEdge(msgObj, sender.sprite, receiver.sprite, EdgeType.DIRECTED);
					
					counter++;
				}
			}
		}
		
		
		for (Node n : graph.getVertices()) {
			int CINT = graph.getSuccessorCount(n);
			HashSet<String> scriptables = new HashSet<>();
			graph.getSuccessors(n).forEach((s) -> scriptables.add(s.sprite));
			
			
			int CDISP = scriptables.size();
			double HALF_THRESHOLD = (double) project.getAllScriptables().size() / 2;
			if (CINT > SHORT_TERM_MEM && CDISP > HALF_THRESHOLD) {
//				System.out.println(n);
//				System.out.println("CINT" + CINT + " CDISP" + CDISP);
				report.addRecord(n.toString());
			}

		}
		
		Set<Pair<String>> uniqueMessagePerReceiver = new HashSet<>();
		
		for (String node : couplingGraph.getVertices()) {
			int uniqueReceiverScriptables = couplingGraph.getSuccessorCount(node);
			int out = couplingGraph.getOutEdges(node).size();
			HashSet<Pair> uniqueMessagesPerScriptable = new HashSet<Pair>(); 
			for(Message message : couplingGraph.getOutEdges(node)){
				Pair<String> endPoints = couplingGraph.getEndpoints(message);
				Pair msgToReceiver = new Pair(message.message, endPoints.getSecond());
				uniqueMessagesPerScriptable.add(msgToReceiver);
			}
			System.out.println(uniqueMessagesPerScriptable.size());
			double intensity = (double)uniqueMessagesPerScriptable.size()/uniqueReceiverScriptables;
			System.out.println(intensity);
			
		}
	}

	private static Paint stringToColor(int hash) {
		int r = (hash & 0xFF0000) >> 16;
		int g = (hash & 0x00FF00) >> 8;
		int b = hash & 0x0000FF;
		return new Color(r, g, b, 255);
	}

	private static void showGraph(Graph<Node, Message> graph) {
		Layout<String, String> layout = new CircleLayout(graph);
		// layout.setSize(new Dimension(300, 300));
		VisualizationViewer vv = new VisualizationViewer(new FRLayout(graph));
		// Set up a new stroke Transformer for the edges
		float dash[] = { 10.0f };
		final Stroke edgeStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash,
				0.0f);
		Transformer<Message, Paint> edgePaint = new Transformer<Message, Paint>() {
			public Paint transform(Message msg) {
				return stringToColor(msg.hashCode());
			}
		};

		Transformer<Node, Paint> vertexColor = new Transformer<Node, Paint>() {
			public Paint transform(Node n) {
				return stringToColor(n.sprite.hashCode());
			}
		};

		vv.getRenderContext().setVertexFillPaintTransformer(vertexColor);
		vv.getRenderContext().setEdgeDrawPaintTransformer(edgePaint);
		// vv.setPreferredSize(new Dimension(350, 350));
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
		gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
		vv.setGraphMouse(gm);

		JFrame frame = new JFrame("Script-Sprite Coupling Viewer");
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public Report getReport() {
		return report;
	}

	public static void main(String[] args) throws IOException, ParseException, ParsingException, AnalysisException {
		// 110445064
		String projectSrc = Util.retrieveProjectOnline(115367090);
		ScratchProject project = ScratchProject.loadProject(projectSrc);
		CouplingMetricAnalyzer analyzer = new CouplingMetricAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();
//		showGraph(graph);
		
//		AnalysisManager.runAnalysis(CouplingMetricAnalyzer.class.getName());
	}

	public Set<Node> getMessageReceiverNodes(String string) {
		
		return null;
	}
}
