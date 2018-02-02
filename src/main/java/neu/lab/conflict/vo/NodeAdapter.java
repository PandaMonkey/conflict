package neu.lab.conflict.vo;

import java.util.LinkedList;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.tree.DependencyNode;

import neu.lab.conflict.container.DepJars;
import neu.lab.conflict.container.NodeAdapters;
import neu.lab.conflict.util.ClassifierUtil;
import neu.lab.conflict.util.UtilGetter;
import neu.lab.conflict.vo.risk.JarRiskAna;
import neu.lab.conflict.vo.risk.NodeRiskAna;

/**
 * @author asus 提供未被版本管理之前的依赖树的节点信息:包括version和jarfilePath
 */
public class NodeAdapter {
	protected DependencyNode node;
	protected DepJar depJar;
	protected String filePath;
	protected NodeRiskAna nodeRiskAna;

	public NodeAdapter(DependencyNode node) {
		this.node = node;
	}

	public String getGroupId() {
		return node.getArtifact().getGroupId();
	}

	public String getScope() {
		return node.getArtifact().getScope();
	}

	public String getArtifactId() {
		return node.getArtifact().getArtifactId();
	}

	public String getVersion() {
		if (null != node.getPremanagedVersion()) {
			return node.getPremanagedVersion();
		} else {
			return node.getArtifact().getVersion();
		}
	}

	/**
	 * version changes because of dependency management
	 * 
	 * @return
	 */
	public boolean isVersionChanged() {
		return null != node.getPremanagedVersion();
	}

	protected String getType() {
		return node.getArtifact().getType();
	}

	public String getClassifier() {
		return ClassifierUtil.transformClf(node.getArtifact().getClassifier());
	}

	/**
	 * used version is select from this node
	 * 
	 * @return
	 */
	public boolean isNodeSelected() {
		if (isVersionChanged())
			return false;
		return node.getState() == DependencyNode.INCLUDED;
	}

	/**
	 * used version is select from this node
	 * 
	 * @return
	 */
	public boolean isVersionSelected() {
		return getDepJar().isSelected();
	}

	public String getManagedVersion() {
		return node.getArtifact().getVersion();
	}

	public NodeRiskAna getNodeAnaUnit(JarRiskAna jarRiskAna) {
		if (nodeRiskAna == null) {
			LinkedList<NodeAdapter> jarPath = new LinkedList<NodeAdapter>();
			jarPath.add(this);
			NodeAdapter father = getParent();
			while (null != father) {
				jarPath.add(father);
				father = father.getParent();
			}
			nodeRiskAna = new NodeRiskAna(jarPath);
		}
		return nodeRiskAna;
	}

	public NodeAdapter getParent() {
		if (null == node.getParent())
			return null;
		return NodeAdapters.i().getNodeAdapter(node.getParent());
	}

	public String getFilePath() {
		if (filePath == null) {
			if (isInnerProject()) {// inner project is target/classes
				filePath = UtilGetter.i().getMavenProject(this).getBuild().getOutputDirectory();
			} else {// dependency is repository address
				try {
					if (null == node.getPremanagedVersion()) {
						// artifact version of node is the version declared in pom.
						if (!node.getArtifact().isResolved())
							UtilGetter.i().resolve(node.getArtifact());
						filePath = node.getArtifact().getFile().getAbsolutePath();
					} else {
						Artifact artifact = UtilGetter.i().getArtifact(getGroupId(), getArtifactId(), getVersion(),
								getType(), getClassifier(), getScope());
						if (!artifact.isResolved())
							UtilGetter.i().resolve(artifact);
						filePath = artifact.getFile().getAbsolutePath();
					}
				} catch (ArtifactResolutionException e) {
					UtilGetter.i().getLog().warn("cant resolve " + this.toString());
					filePath = "";
				} catch (ArtifactNotFoundException e) {
					UtilGetter.i().getLog().warn("cant resolve " + this.toString());
					filePath = "";
				}

			}
		}
		UtilGetter.i().getLog().debug("node filepath for " + toString() + " : " + filePath);
		return filePath;

	}

	public boolean isInnerProject() {
		return UtilGetter.i().isInner(this);
	}

	public boolean isSelf(DependencyNode node2) {
		return node.equals(node2);
	}

	public boolean isSelf(MavenProject mavenProject) {
		return isSelf(mavenProject.getGroupId(), mavenProject.getArtifactId(), mavenProject.getVersion(),
				ClassifierUtil.transformClf(mavenProject.getArtifact().getClassifier()));
	}

	public boolean isSelf(String groupId2, String artifactId2, String version2, String classifier2) {
		return getGroupId().equals(groupId2) && getArtifactId().equals(artifactId2) && getVersion().equals(version2)
				&& getClassifier().equals(classifier2);
	}

	public MavenProject getSelfMavenProject() {
		return UtilGetter.i().getMavenProject(this);
	}

	public DepJar getDepJar() {
		if (depJar == null)
			depJar = DepJars.i().getDep(this);
		return depJar;
	}

	@Override
	public String toString() {
		return getGroupId() + ":" + getArtifactId() + ":" + getVersion() + ":" + getClassifier();
	}
}
