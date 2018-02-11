package neu.lab.conflict.risk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import neu.lab.conflict.Conf;
import neu.lab.conflict.graph.Graph;
import neu.lab.conflict.graph.Node;
import neu.lab.conflict.soot.SootAna;
import neu.lab.conflict.util.SootUtil;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.MethodCall;
import soot.MethodOrMethodContext;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.SourceLocator;
import soot.Transform;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.util.queue.QueueReader;

public class CgAna extends SootAna {
	public static long runtime = 0;
	private static CgAna instance = new CgAna();

	private CgAna() {

	}

	public static CgAna i() {
		return instance;
	}

	public void cmpCg(NodeRiskAna nodeAnaUnit, Set<String> thrownMthds) {
		MavenUtil.i().getLog().info("use soot to compute reach methods for " + nodeAnaUnit.toString());

		long startTime = System.currentTimeMillis();
		CgTf transformer = new CgTf(nodeAnaUnit, thrownMthds);
		PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTrans", transformer));

		SootUtil.modifyLogOut();

		soot.Main.main(getArgs(nodeAnaUnit.getJarFilePaths().toArray(new String[0])).toArray(new String[0]));

		nodeAnaUnit.setRchedMthds(transformer.getRchMthds());
		nodeAnaUnit.setRisk1Mthds(transformer.getRisk1Mthds());
		nodeAnaUnit.setRisk2Mthds(transformer.getRisk2Mthds());

		nodeAnaUnit.setBooks(transformer.getGraph().getBooks());

		soot.G.reset();

		runtime = runtime + (System.currentTimeMillis() - startTime) / 1000;
	}

	@Override
	protected void addCgArgs(List<String> argsList) {
		argsList.addAll(Arrays.asList(new String[] { "-p", "cg", "off", }));
	}

}

class CgTf extends SceneTransformer {

	private Set<String> entryClses;
	private Set<String> conflictJarClses;// classes in duplicated jar

	private List<String> rchMthds;

	private Set<String> thrownMthds;
	private Set<String> risk1Mthds;// thrown,rch from host

	private Set<String> risk2Mthds;// all target Node in riskCalls.
	private List<MethodCall> riskCalls;// source is from other jar,target is to risk1Mthds.
	// private Map<String, Book> books;

	public CgTf(NodeRiskAna sootAnaUnit, Set<String> thrownMthds) {
		super();
		this.thrownMthds = thrownMthds;
		this.rchMthds = new ArrayList<String>();
		riskCalls = new ArrayList<MethodCall>();
		risk1Mthds = new HashSet<String>();
		risk2Mthds = new HashSet<String>();

		entryClses = getJarCls(sootAnaUnit.getTopJar().getFilePath());

		conflictJarClses = getJarCls(sootAnaUnit.getBottomJar().getFilePath());
		MavenUtil.i().getLog()
				.info("entryClses size:" + entryClses.size() + " dupClses size:" + conflictJarClses.size());
	}

	@Override
	protected void internalTransform(String phaseName, Map<String, String> options) {
		Map<String, String> cgMap = new HashMap<String, String>();
		cgMap.put("enabled", "true");
		cgMap.put("apponly", "true");
		List<SootMethod> entryMthds = new ArrayList<SootMethod>();
		for (SootClass sootClass : Scene.v().getApplicationClasses()) {
			if (entryClses.contains(sootClass.getName())) {// entry class
				for (SootMethod method : sootClass.getMethods()) {
					entryMthds.add(method);
				}
			}
		}

		Scene.v().setEntryPoints(entryMthds);
		CHATransformer.v().transform("wjtp", cgMap);

		QueueReader<MethodOrMethodContext> entryRchMthds = Scene.v().getReachableMethods().listener();

		while (entryRchMthds.hasNext()) {
			SootMethod method = entryRchMthds.next().method();
			if (isInDupJar(method)) {// is method in duplicate jar
				rchMthds.add(method.getSignature());
				if (thrownMthds.contains(method.getSignature()))// method is thrown when packaging
					risk1Mthds.add(method.getSignature());
			}

		}
		MavenUtil.i().getLog().info("soot reachMethod size:" + Scene.v().getReachableMethods().size()
				+ ";reachedMethod in jar size:" + rchMthds.size());

		CallGraph cg = Scene.v().getCallGraph();
		Iterator<Edge> ite = cg.iterator();
		while (ite.hasNext()) {
			Edge edge = ite.next();
			if (isRiskCall(edge)) {
				riskCalls.add(new MethodCall(edge.src().getSignature(), edge.tgt().getSignature()));
				risk2Mthds.add(edge.tgt().getSignature());
			}
		}
		// Graph graph = getGraph();
		// books = graph.getBooks();

	}

	public Graph getGraph() {
		Set<Node> nds = new HashSet<Node>();
		List<MethodCall> calls = new ArrayList<MethodCall>();

		// form calls and nds
		CallGraph cg = Scene.v().getCallGraph();
		Iterator<Edge> ite = cg.iterator();
		while (ite.hasNext()) {
			Edge edge = ite.next();
			if (Conf.FLT_INTERFACE) {
				if (edge.kind().name().equals("INTERFACE"))
					continue;
			}
			String srcClsName = edge.src().getDeclaringClass().getName();
			String tgtClsName = edge.tgt().getDeclaringClass().getName();
			if (entryClses.contains(tgtClsName)) {
				// edge to entry-jar
			} else if (conflictJarClses.contains(srcClsName)) {
				// edge from conflict-jar
			} else {
				String tgtMthdName = edge.tgt().getSignature();
				if (conflictJarClses.contains(tgtClsName) && !risk2Mthds.contains(tgtMthdName)) {
					// edge to conflict jar method which isn't in risk2Mthds
				} else {
					String srcMthdName = edge.src().getSignature();

					calls.add(new MethodCall(srcMthdName, tgtMthdName));
					nds.add(new Node(srcMthdName, entryClses.contains(srcClsName)));
					nds.add(new Node(tgtMthdName, entryClses.contains(tgtClsName)));
				}
			}
		}

		return new Graph(nds, calls, risk2Mthds);
	}

	protected boolean isRiskCall(Edge edge) {
		String srcCls = edge.src().getDeclaringClass().getName();
		return risk1Mthds.contains(edge.tgt().getSignature()) && !conflictJarClses.contains(srcCls);// target method is
																									// risk
		// and source method is
		// from another jar
	}

	private boolean isInDupJar(SootMethod method) {
		if (conflictJarClses.contains(method.getDeclaringClass().getName())) {
			return true;
		}
		return false;
	}

	private Set<String> getJarCls(List<String> jarPaths) {
		Set<String> jarClses = new HashSet<String>();
		for (String jarPath : jarPaths) {
			for (String cls : SourceLocator.v().getClassesUnder(jarPath)) {
				jarClses.add(cls);
			}
		}
		return jarClses;
	}

	public List<String> getRchMthds() {
		return rchMthds;
	}

	public List<MethodCall> getRiskCalls() {
		return riskCalls;
	}

	public Set<String> getRisk1Mthds() {
		return risk1Mthds;
	}

	public Set<String> getRisk2Mthds() {
		return risk2Mthds;
	}

}
