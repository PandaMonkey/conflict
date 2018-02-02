package neu.lab.conflict.vo;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.shared.dependency.tree.DependencyNode;

import neu.lab.conflict.container.NodeAdapters;
import neu.lab.conflict.util.UtilGetter;

/**
 * some depjar may be from dependency management instead of dependency tree.We
 * design ManageNodeAdapter for depJar of this type.
 * 
 * @author asus
 *
 */
public class ManageNodeAdapter extends NodeAdapter {
	private String groupId;
	private String artifactId;// artifactId
	private String version;// version
	private String classifier;
	private String type;
	private String scope;

	public ManageNodeAdapter(NodeAdapter nodeAdapter) {
		super(null);
		groupId = nodeAdapter.getGroupId();
		artifactId = nodeAdapter.getArtifactId();
		version = nodeAdapter.getManagedVersion();
		classifier = nodeAdapter.getClassifier();
		type = nodeAdapter.getType();
		scope = nodeAdapter.getScope();
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

	public String getClassifier() {
		return classifier;
	}

	public boolean isNodeSelected() {
		return true;
	}

	public boolean isVersionSelected() {
		return true;
	}

	public String getManagedVersion() {
		return version;
	}

	public NodeAdapter getParent() {
		return null;
	}

	protected String getType() {
		return type;
	}

	public String getScope() {
		return scope;
	}

	public boolean isVersionChanged() {
		return false;
	}

	public String getFilePath() {
		if (filePath == null) {
			if (isInnerProject()) {// inner project is target/classes
				filePath = UtilGetter.i().getMavenProject(this).getBuild().getOutputDirectory();
			} else {// dependency is repository address
				try {
					Artifact artifact = UtilGetter.i().getArtifact(getGroupId(), getArtifactId(), getVersion(),
							getType(), getClassifier(), getScope());
					if (!artifact.isResolved())
						UtilGetter.i().resolve(artifact);
					filePath = artifact.getFile().getAbsolutePath();
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

	public boolean isSelf(DependencyNode node2) {
		return false;
	}
}
