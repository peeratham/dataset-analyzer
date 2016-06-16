package vt.cs.smells.visual;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import vt.cs.smells.analyzer.AnalysisException;
import vt.cs.smells.analyzer.AnalysisManager;
import vt.cs.smells.analyzer.Analyzer;
import vt.cs.smells.analyzer.DatasetFilter;
import vt.cs.smells.analyzer.ListAnalysisReport;
import vt.cs.smells.analyzer.Report;
import vt.cs.smells.analyzer.analysis.ScriptClusterer;
import vt.cs.smells.analyzer.nodes.Block;
import vt.cs.smells.analyzer.nodes.ScratchProject;
import vt.cs.smells.analyzer.nodes.Script;
import vt.cs.smells.analyzer.parser.ParsingException;
import vt.cs.smells.analyzer.parser.Util;
import vt.cs.smells.crawler.AnalysisDBManager;

public class ScriptCoordsAnalyzer extends Analyzer {
	VisualizationHelper visHelper = null;
	HashMap<String, List<Node>> nodesForEachScriptable = new HashMap<>();
	private static final int max_xi_id = 1000000;
	private ListAnalysisReport report = null;

	public void showVisualization() {
		visHelper.show();
	}

	static Transformer<Node, Point2D> vertexToPoint2D = new Transformer<Node, Point2D>() {
		public Point2D transform(Node n) {
			Point2D p = new Point2D.Float(n.x, n.y);
			return p;
		}
	};

	Transformer<Node, Paint> vertexPaints = new Transformer<Node, Paint>() {
		@Override
		public Paint transform(Node n) {
			return ScriptClusterer.colors[n.clusterIndex % 8];
		}
	};

	Transformer<Node, Stroke> vertexStroke = new Transformer<Node, Stroke>() {
		@Override
		public Stroke transform(Node arg0) {
			float dash[] = { 10.0f };
			BasicStroke stroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
			return stroke;
		}
	};

	private Paint stringToColor(int hash) {
		int r = (hash & 0xFF0000) >> 16;
		int g = (hash & 0x00FF00) >> 8;
		int b = hash & 0x0000FF;
		return new Color(r, g, b, 255);
	}

	private Graph<Node, String> constructGraph(ArrayList<Node> nodes) {
		Graph<Node, String> graph = new SparseMultigraph<Node, String>();
		for (Node n : nodes) {
			graph.addVertex(n);
		}
		return graph;
	}

	class VisualizationHelper {
		JFrame frame = new JFrame();
		JTabbedPane tabbedPane = new JTabbedPane();
		private String title;
		HashMap<String, VisualizationViewer> visMap = new HashMap<>();

		public void add(String scriptableName, Graph<?, ?> graph) {
			Layout<String, String> layout = new StaticLayout(graph, vertexToPoint2D);
			VisualizationViewer vv = new VisualizationViewer(layout);
			float dash[] = { 10.0f };
			final Stroke edgeStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash,
					0.0f);

			vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
			vv.getRenderContext().setVertexFillPaintTransformer(vertexPaints);
			vv.getRenderContext().setVertexStrokeTransformer(vertexStroke);

			vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
			DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
			gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
			vv.setGraphMouse(gm);
			JComponent panel = new JPanel(new BorderLayout());
			panel.add(new GraphZoomScrollPane(vv));
			tabbedPane.addTab(scriptableName, null, panel, "Does nothing");
			JSlider xi_id_slider = new JSlider(JSlider.HORIZONTAL, 1, max_xi_id, 1);
			xi_id_slider.setName(scriptableName);
			xi_id_slider.setMajorTickSpacing(max_xi_id / 5);
			xi_id_slider.setMinorTickSpacing(max_xi_id / 10);
			xi_id_slider.setPaintTicks(true);
			xi_id_slider.setPaintLabels(true);

			xi_id_slider.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					JSlider source = (JSlider) e.getSource();
					String scriptableName = source.getName();
					VisualizationViewer v = visMap.get(scriptableName);
					List<Node> nodes = nodesForEachScriptable.get(scriptableName);
					ScriptClusterer<Node> clusterer = new ScriptClusterer<Node>();
					int xi_id_slider_val = (int) source.getValue();
					double xi_id = (double) xi_id_slider_val / max_xi_id;
					clusterer.performClustering(nodes, xi_id);
					v.repaint();
				}
			});
			final JPanel control_panel = new JPanel();
			control_panel.add(xi_id_slider);
			panel.add(control_panel, BorderLayout.NORTH);
			visMap.put(scriptableName, vv);
			frame.getContentPane().add(tabbedPane);
		}

		public void show() {
			frame.setTitle(title);
			frame.pack();
			frame.setVisible(true);
		}

		public void setTitle(int projectID) {
			this.title = projectID + "";
		}
	}

	@Override
	public void analyze() throws AnalysisException {
		report = new ListAnalysisReport();
		visHelper = new VisualizationHelper();
		visHelper.setTitle(project.getProjectID());
		for (String scriptableName : project.getAllScriptables().keySet()) {
			ArrayList<Node> nodes = new ArrayList<Node>();
			for (Script s : project.getAllScriptables().get(scriptableName).getScripts()) {
				Block firstBlock = s.getBlocks().get(0);
				if (firstBlock.getBlockType().getShape().equals("hat")) {
					Node node = new Node(s);
					nodes.add(node);
				}
			}
			if (nodes.isEmpty()) {
				continue;
			}
			Graph<?, ?> graph = constructGraph(nodes);
			// clustering analysis
			ScriptClusterer<Node> clusterer = new ScriptClusterer<Node>();
			List<List<Node>> clusters = clusterer.performClustering(nodes, (double) 1 / max_xi_id);
			visHelper.add(scriptableName, graph);

			nodesForEachScriptable.put(scriptableName, nodes);

			// if every nodes are the same no purity evaluation is useless
			HashSet<NodeClass> nodeClassSet = new HashSet<NodeClass>();
			for (List<Node> cluster : clusters) {
				for (Node n : cluster) {
					nodeClassSet.add(n.getNodeClass());
				}
			}
			if (!(nodeClassSet.size()> 1)) {
				continue;
			}
			// purity evaluation

			for (List<Node> cluster : clusters) {
				HashMap<NodeClass, Integer> clusterCount = new HashMap<>();
				for (Node n : cluster) {
					if (clusterCount.get(n.getNodeClass()) == null) {
						clusterCount.put(n.getNodeClass(), 1);
					} else {
						int increment = clusterCount.get(n.getNodeClass()) + 1;
						clusterCount.put(n.getNodeClass(), increment);
					}
				}
				NodeClass majorityNodeClass = null;
				int majorityCount = -1;
				int totalNodes = cluster.size();
				for (NodeClass nc : clusterCount.keySet()) {
					if (clusterCount.get(nc) > majorityCount) {
						majorityCount = clusterCount.get(nc);
						majorityNodeClass = nc;
					}
				}

				double purityVal = (double) majorityCount / totalNodes;
				if (purityVal > 0.8 && clusters.size() > 1 && cluster.size() > 1) {
					JSONObject record = new JSONObject();
					record.put(scriptableName, majorityNodeClass.toString());
					report.addRecord(record);
					// showVisualization();
				}

			}

		}

	}

	@Override
	public Report getReport() {
		report.setTitle("ScriptOrganization");
		return report;
	}

	public static void main(String[] args) throws IOException, ParseException, ParsingException, AnalysisException {
		runMultiple();
//		 runOnce();
	}

	private static void runOnce() throws IOException, ParseException, ParsingException, AnalysisException {
		String projectSrc = Util.retrieveProjectOnline(94833586);
		ScratchProject project = ScratchProject.loadProject(projectSrc);
		ScriptCoordsAnalyzer analyzer = new ScriptCoordsAnalyzer();
		analyzer.setProject(project);
		analyzer.analyze();
		System.out.println(analyzer.getReport().getJSONReport());
		analyzer.showVisualization();
		Document report = Document.parse(analyzer.getReport().getJSONReport().toJSONString());

		if (((Document) report.get("records")).getInteger("count") > 0) {
			System.out.println(analyzer.getReport().getJSONReport());
		}
	}

	private static void runMultiple()
			throws FileNotFoundException, IOException, ParseException, ParsingException, AnalysisException {
		DatasetFilter filter = new DatasetFilter();
		FileInputStream is = new FileInputStream(AnalysisManager.largeTestInput);
		AnalysisDBManager dbManager = new AnalysisDBManager("localhost", "exploration");

		String[] lines = IOUtils.toString(is).split("\n");
		filter.setDataSource(lines);
		filter.setScriptableThreshold(5);
		filter.setAvgScriptPerSprite(3.0);
		HashMap<Integer, JSONObject> datasetDict = filter.getFilteredProjectsFrom(1);

		System.out.println("Original Size: " + lines.length);
		System.out.println("Filtered Size: " + datasetDict.size());

		ScriptCoordsAnalyzer analyzer = new ScriptCoordsAnalyzer();
		StringBuilder sb = new StringBuilder();
		sb.append("ID,scriptCount, spriteCount, avgScripts, remixes, views, counts\n");
		for (Integer id : datasetDict.keySet()) {
			sb.append(id + ",");
			JSONObject projectJson = datasetDict.get(id);
			ScratchProject project = ScratchProject.loadProject((String) projectJson.get("src"));
			analyzer.setProject(project);
			analyzer.analyze();
			// analyzer.showVisualization();

			Document report = Document.parse(analyzer.getReport().getJSONReport().toJSONString());
			if (((Document) report.get("records")).getInteger("count") > 0) {
				System.out.println(id);
				System.out.println(analyzer.getReport().getJSONReport());
			}
			//
			Document metricReport = dbManager.findMetricsReport(id);
			 sb.append(metricReport.getInteger("scriptCount")+",");
			 sb.append(metricReport.getInteger("spriteCount")+",");
			 double avgScripts = (double) metricReport.getInteger("scriptCount")/(metricReport.getInteger("spriteCount")+1);
			 sb.append(avgScripts+",");
			Document metadata = dbManager.findMetadata(id);
			if (metadata != null) {
				sb.append(metadata.getInteger("remixes") + ",");
				sb.append(metadata.getInteger("views")+",");
			}

			sb.append(((Document) report.get("records")).getInteger("count") + ",");
			sb.append("\n");
		}
		File f = new File("./result2.csv");
		FileUtils.writeStringToFile(f, sb.toString());
	}

	public HashMap<String, List<Node>> getAllScriptNodes() {
		return nodesForEachScriptable;

	}

}
