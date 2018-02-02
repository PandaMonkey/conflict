package neu.lab.conflict.util;

import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import neu.lab.conflict.vo.NodeAdapter;

public abstract class MavenUtil {

	public abstract boolean isInner(NodeAdapter nodeAdapter);

	public abstract MavenProject getMavenProject(NodeAdapter nodeAdapter);

	public abstract void resolve(Artifact artifact) throws ArtifactResolutionException, ArtifactNotFoundException;

	public abstract Log getLog();
	
	public abstract Artifact getArtifact(String groupId,String artifactId,String version,String type,String classifier,String scope);
	
	public abstract File getBuildDir();

}
