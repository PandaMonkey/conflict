package neu.lab.conflict.risk;

import java.util.Set;

import neu.lab.conflict.graph.Book;
import neu.lab.conflict.vo.ClassVO;
import neu.lab.conflict.vo.DepJar;
import neu.lab.conflict.vo.MethodVO;
import neu.lab.conflict.vo.NodeAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * risk caused by jar omitted
 * 
 * @author asus
 *
 */
public class JarRiskAna {

	private DepJar depJar;
	private Set<ClassVO> onlyClses;// exist in jar,but not in final
	private Map<String, List<MethodVO>> onlyMthds;// method exist in jar class,but not in final same name
													// class;key:className,value:risk methods in class
	private Set<String> rchedMthds;// reached method in call-graph computed(entry class is host class)
	private Set<String> risk1Mthds;// reached and thrown
	private Set<String> risk2Mthds;// reached and thrown and called by method in other jar.

	private List<NodeRiskAna> nodeRiskAnas;

	public JarRiskAna(DepJar depJar, Map<String, ClassVO> clsTb) {
		this.depJar = depJar;
		onlyClses = new HashSet<ClassVO>();
		onlyMthds = new HashMap<String, List<MethodVO>>();

		initOnly(clsTb);
		getNodeRiskAnas();
		sum();
	}

	private void sum() {
		rchedMthds = new HashSet<String>();
		risk1Mthds = new HashSet<String>();
		risk2Mthds = new HashSet<String>();
		for (NodeRiskAna nodeRiskAna : nodeRiskAnas) {
			rchedMthds.addAll(nodeRiskAna.getRchedMthds());
			risk1Mthds.addAll(nodeRiskAna.getRisk1Mthds());
			risk2Mthds.addAll(nodeRiskAna.getRisk2Mthds());
		}
	}

	public Set<String> getThrownMthds() {
		Set<String> riskMthds = new HashSet<String>();
		for (ClassVO onlyCls : onlyClses) {// all method in only class is risk
			for (MethodVO riskMthd : onlyCls.getMthds()) {
				riskMthds.add(riskMthd.getMthdSig());
			}
		}
		for (String bothCls : onlyMthds.keySet()) {
			for (MethodVO riskMthd : onlyMthds.get(bothCls)) {
				riskMthds.add(riskMthd.getMthdSig());
			}
		}
		return riskMthds;
	}

	private void initOnly(Map<String, ClassVO> clsTb) {
		for (ClassVO jarCls : depJar.getClsTb().values()) {
			ClassVO finalCls = clsTb.get(jarCls.getClsSig());
			if (finalCls == null) {// class only in jar
				addClass(jarCls);
			} else {// class both in jar and final
				for (MethodVO jarMthd : jarCls.getMthds()) {
					if (!finalCls.hasMethod(jarMthd.getMthdSig()))
						addMethod(jarMthd);
				}
			}
		}
	}

	public void addClass(ClassVO cls) {
		onlyClses.add(cls);
	}

	public List<NodeRiskAna> getNodeRiskAnas() {
		if (nodeRiskAnas == null) {
			nodeRiskAnas = new ArrayList<NodeRiskAna>();
			for (NodeAdapter node : depJar.getNodeAdapters()) {
				nodeRiskAnas.add(node.getNodeAnaUnit(this));
			}
			for (NodeRiskAna nodeRiskAna : nodeRiskAnas) {
				CgAna.i().cmpCg(nodeRiskAna, getThrownMthds());
			}
		}

		return nodeRiskAnas;
	}

	public void addMethod(MethodVO mthd) {
		String clsSig = mthd.getClassVO().getClsSig();
		List<MethodVO> riskMthds = onlyMthds.get(clsSig);
		if (riskMthds == null) {
			riskMthds = new ArrayList<MethodVO>();
			onlyMthds.put(clsSig, riskMthds);
		}
		riskMthds.add(mthd);
	}

	public String getRiskString() {
		StringBuilder sb = new StringBuilder("risk for jar:");
		sb.append(depJar.toString() + "\n");
		sb.append("reached size: " + rchedMthds.size() + " reached_thrown size:" + risk1Mthds.size()
				+ " reached_thrown_service:" + risk2Mthds.size() + "\n");
		for (NodeRiskAna nodeRiskAna : getNodeRiskAnas()) {
			sb.append(nodeRiskAna.getRiskString());
			sb.append("\n");
		}
		return sb.toString();
	}
	// public int getRiskMthdNum() {
	// int num = 0;
	// for (ClassVO cls : onlyClses) {
	// for (MethodVO mthd : cls.getMthds()) {
	// Set<String> mthdRiskCalls = riskCalls.getRiskCalls(mthd.getMthdSig());
	// if (mthdRiskCalls.size() > 0) {
	// num++;
	// }
	// }
	// }
	// for (String clsName : onlyMthds.keySet()) {
	// for (MethodVO method : onlyMthds.get(clsName)) {
	// Set<String> mthdRiskCalls = riskCalls.getRiskCalls(method.getMthdSig());
	// if (mthdRiskCalls.size() > 0) {
	// num++;
	// }
	// }
	// }
	// return num;
	// }

	// public String getRiskStr() {
	// StringBuilder sb = new StringBuilder("risk for ");
	// sb.append(depJar.toString() + ":" + getRiskMthdNum() + "\n");
	// sb.append("only class in jar:" + "\n");
	// for (ClassVO cls : onlyClses) {
	// sb.append("-");
	// sb.append(cls.getClsSig());
	// sb.append("\n");
	// for (MethodVO mthd : cls.getMthds()) {
	// sb.append(mthdRiskStr(mthd));
	// }
	// }
	// sb.append("only method in jar:" + "\n");
	// for (String clsName : onlyMthds.keySet()) {
	// sb.append("-");
	// sb.append(clsName);
	// sb.append("\n");
	// for (MethodVO method : onlyMthds.get(clsName)) {
	// sb.append(mthdRiskStr(method));
	// }
	//
	// }
	// return sb.toString();
	// }

	// private String mthdRiskStr(MethodVO method) {
	// StringBuilder sb = new StringBuilder("");
	// sb.append("---");
	// sb.append(method.getMthdSig());
	// sb.append("\n");
	// for (String callMthd : riskCalls.getRiskCalls(method.getMthdSig())) {
	// sb.append("---------called by " + callMthd + "\n");
	// }
	// return sb.toString();
	// }

	// public String getRiskCallPath() {
	// StringBuilder sb = new StringBuilder(depJar.toString() + "\n");
	// // for(String ) {
	// //
	// // }
	// return sb.toString();
	// }
}
