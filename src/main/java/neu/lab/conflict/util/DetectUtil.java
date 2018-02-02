package neu.lab.conflict.util;

import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import neu.lab.conflict.DetectMojo;
import neu.lab.conflict.vo.NodeAdapter;

public class DetectUtil extends MavenUtil {
	private static DetectUtil instance = new DetectUtil();

	 static DetectUtil i() {
		return instance;
	}

	private DetectUtil() {
	}

	private DetectMojo mojo;

	
	public boolean isInner(NodeAdapter nodeAdapter) {
		for (MavenProject mavenProject : mojo.reactorProjects) {
			if (nodeAdapter.isSelf(mavenProject))
				return true;
		}
		return false;
	}
	

	public MavenProject getMavenProject(NodeAdapter nodeAdapter) {
		for (MavenProject mavenProject : mojo.reactorProjects) {
			if (nodeAdapter.isSelf(mavenProject))
				return mavenProject;
		}
		return null;
	}
	
	public void setMojo(DetectMojo mojo) {
		this.mojo = mojo;
	}

	public void resolve(Artifact artifact) throws ArtifactResolutionException, ArtifactNotFoundException {
		mojo.resolver.resolve(artifact, mojo.remoteRepositories, mojo.localRepository);
	}
	public Log getLog() {
		return mojo.getLog();
	}
	public DetectMojo getMojoObj() {
		return mojo;
	}

	public Artifact getArtifact(String groupId, String artifactId, String versionRange, String type,
			String classifier, String scope) {
		try {
			return mojo.factory.createDependencyArtifact(groupId, artifactId, VersionRange.createFromVersionSpec(versionRange), type, classifier, scope);
		} catch (InvalidVersionSpecificationException e) {
			getLog().error("cant create Artifact!",e);
			return null;
		}
	}

	@Override
	public File getBuildDir() {
		return mojo.buildDir;
	}


}
