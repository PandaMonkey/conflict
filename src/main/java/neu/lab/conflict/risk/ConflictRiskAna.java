package neu.lab.conflict.risk;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import neu.lab.conflict.Conf;
import neu.lab.conflict.container.FinalClasses;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.ClassVO;
import neu.lab.conflict.vo.DepJar;
import neu.lab.conflict.vo.NodeConflict;

public class ConflictRiskAna {
	private List<JarRiskAna> jarRiskAnas;
	private NodeConflict nodeConflict;
	// private Set<String> rchMthds;

	private ConflictRiskAna(NodeConflict nodeConflict) {
		this.nodeConflict = nodeConflict;
	}

	public List<JarRiskAna> getJarRiskAnas() {
		return jarRiskAnas;
	}

	private void setJarRiskAnas(List<JarRiskAna> jarRiskAnas) {
		this.jarRiskAnas = jarRiskAnas;
	}

	public String getRiskString() {
		StringBuilder sb = new StringBuilder("risk for conflict:");
		sb.append(nodeConflict.toString() + "\n");
		// sb.append("reached size: " + rchedMthds.size() + " reached_thrown size:" +
		// risk1Mthds.size()
		// + " reached_thrown_service:" + risk2Mthds.size() + "\n");
		for (JarRiskAna jarRiskAna : getJarRiskAnas()) {
			sb.append(jarRiskAna.getRiskString());
			sb.append("\n");
		}
		return sb.toString();
	}
	// @Override
	// private String toString() {
	//
	// }

	public static ConflictRiskAna getConflictRiskAna(NodeConflict nodeConflict) {
		MavenUtil.i().getLog().info("risk ana for:"+nodeConflict.toString());
		ConflictRiskAna riskAna = new ConflictRiskAna(nodeConflict);
		List<JarRiskAna> jarRiskAnas = new ArrayList<JarRiskAna>();
		for (DepJar depJar : nodeConflict.getDepJars()) {
			jarRiskAnas.add(depJar.getJarRiskAna(getClsTb(nodeConflict)));
		}
		riskAna.setJarRiskAnas(jarRiskAnas);
		return riskAna;
	}

	private static Map<String, ClassVO> getClsTb(NodeConflict nodeConflict) {
		if (Conf.CLASS_DUP)
			return FinalClasses.i().getClsTb();
		else
			return nodeConflict.getUsedDepJar().getClsTb();
	}

}
