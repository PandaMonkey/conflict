package neu.lab.conflict.vo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import neu.lab.conflict.container.NodeAdapters;
import neu.lab.conflict.soot.JarAna;
import neu.lab.conflict.vo.risk.JarRiskAna;

/**
 * 对应一个jar文件，跟node是一对多的关系，因为多个节点可能对应一个jar文件
 * 
 * @author asus
 *
 */
public class DepJar {
	private String groupId;
	private String artifactId;// artifactId
	private String version;// version
	private String classifier;
	private String jarFilePath;
	private Map<String,ClassVO> clses;// all class in jar
	private Set<NodeAdapter> nodeAdapters;// all
	private JarRiskAna jarRisk;

	public DepJar(String groupId, String artifactId, String version, String classifier, String jarFilePath) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.classifier = classifier;
		this.jarFilePath = jarFilePath;
	}

	/**
	 * get jar may have risk thinking same class in different dependency,selected
	 * jar may have risk; Not thinking same class in different dependency,selected
	 * jar is safe
	 * 
	 * @return
	 */
	public boolean isRisk() {
		return !this.isSelected();
	}

	public JarRiskAna getJarRiskAna(Map<String, ClassVO> clsTb) {
		if (jarRisk == null) {
			jarRisk = new JarRiskAna(this, clsTb);
		}
			
		return jarRisk;
	}

	public Set<NodeAdapter> getNodeAdapters() {
		if (nodeAdapters == null)
			nodeAdapters = NodeAdapters.i().getNodeAdapters(this);
		return nodeAdapters;
	}

	public boolean isSelected() {
		for (NodeAdapter nodeAdapter : getNodeAdapters()) {
			if (nodeAdapter.isNodeSelected())
				return true;
		}
		return false;
	}

	public Map<String,ClassVO> getClsTb() {
		if (clses == null) {// havent initial
			if (null == this.getJarFilePath())// no file
				clses = new HashMap<String,ClassVO>();
			else {
				clses = JarAna.i().deconstruct(this.getJarFilePath());
				for (ClassVO clsVO : clses.values()) {
					clsVO.setDepJar(this);
				}
			}
		}
		return clses;
	}
	
//	public Map<String,ClassVO> getClsTb(){
//		Map<String,ClassVO> clsTb = new 
//		for (ClassVO clses : getClses()) {
//			String key = clses.getClsSig();
//			ClassVO oldCls = loadClses.get(key);
//				loadClses.put(newCls.getClsSig(), newCls);
//		}
//	}

	public Set<String> getAllMthd() {
		Set<String> allMthd = new HashSet<String>();
		for (ClassVO cls : getClsTb().values()) {
			for (MethodVO mthd : cls.getMthds()) {
				allMthd.add(mthd.getMthdSig());
			}
		}
		return allMthd;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DepJar) {
			return isSelf((DepJar) obj);

		}
		return false;
	}

	@Override
	public int hashCode() {
		return groupId.hashCode() * 31 * 31 + artifactId.hashCode() * 31 + version.hashCode()
				+ classifier.hashCode() * 31 * 31 * 31;
	}

	@Override
	public String toString() {
		return groupId + ":" + artifactId + ":" + version + ":" + classifier;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getClassifier() {
		return classifier;
	}

	public boolean isSame(String groupId2, String artifactId2, String version2, String classifier2) {
		return groupId.equals(groupId2) && artifactId.equals(artifactId2) && version.equals(version2)&&classifier.equals(classifier2);
	}

	public boolean isSelf(DepJar dep) {
		return isSame(dep.getGroupId(), dep.getArtifactId(), dep.getVersion(), dep.getClassifier());
	}


	public String getJarFilePath() {
		return jarFilePath;
	}

}
