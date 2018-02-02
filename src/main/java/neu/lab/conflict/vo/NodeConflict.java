package neu.lab.conflict.vo;

import java.util.HashSet;
import java.util.Set;

import neu.lab.conflict.util.UtilGetter;
import neu.lab.conflict.vo.risk.ConflictRiskAna;

/**
 * 冲突的节点形成的组
 * 
 * @author asus
 *
 */
public class NodeConflict {
	private String groupId;
	private String artifactId;

	private Set<NodeAdapter> nodes;
	private Set<DepJar> depJars;
	private DepJar usedDepJar;
//	private ConflictRiskAna riskAna;

	public NodeConflict(String groupId, String artifactId) {
		nodes = new HashSet<NodeAdapter>();
		this.groupId = groupId;
		this.artifactId = artifactId;
	}

	public DepJar getUsedDepJar() {
		if (null == usedDepJar) {
			for (DepJar depJar : depJars) {
				if (depJar.isSelected()) {
					if (null != usedDepJar)
						UtilGetter.i().getLog()
								.warn("duplicate used version for dependency:" + groupId + ":" + artifactId);
					usedDepJar = depJar;
				}
			}
		}
		return usedDepJar;

	}

	public void addNode(NodeAdapter nodeAdapter) {
		nodes.add(nodeAdapter);
	}

	public boolean sameArtifact(String groupId2, String artifactId2) {
		return groupId.equals(groupId2) && artifactId.equals(artifactId2);
	}

	public Set<DepJar> getDepJars() {
		if (depJars == null) {
			depJars = new HashSet<DepJar>();
			for (NodeAdapter nodeAdapter : nodes) {
				depJars.add(nodeAdapter.getDepJar());
			}
		}
		return depJars;
	}

	/**
	 * 判断该对象是否真的为冲突；包含多个版本的node
	 * 
	 * @return
	 */
	public boolean isConflict() {
		return getDepJars().size() > 1;
	}

	public ConflictRiskAna getRiskAna() {
//		if(riskAna==null) {
//			riskAna = ConflictRiskAna.getConflictRiskAna(this);
//		}
//		return riskAna;
		return ConflictRiskAna.getConflictRiskAna(this);
	}

	@Override
	public String toString() {
		String str = groupId + ":" + artifactId + " conflict version:";
		for (DepJar depJar : depJars) {
			str = str + depJar.getVersion() + ":" + depJar.getClassifier() + "-";
		}
		str = str + "-->used jar:" + getUsedDepJar().getVersion();
		return str;
	}
}
