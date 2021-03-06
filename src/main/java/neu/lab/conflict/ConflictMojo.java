package neu.lab.conflict;

import java.io.File;
import java.util.List;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilderException;

import neu.lab.conflict.container.DepJars;
import neu.lab.conflict.container.NodeAdapters;
import neu.lab.conflict.container.NodeConflicts;
import neu.lab.conflict.util.MavenUtil;

public abstract class ConflictMojo extends AbstractMojo{
	@Parameter(defaultValue = "${session}", readonly = true, required = true)
	public MavenSession session;

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	public MavenProject project;

	@Parameter(defaultValue = "${reactorProjects}", readonly = true, required = true)
	public List<MavenProject> reactorProjects;

	@Parameter(defaultValue = "${project.remoteArtifactRepositories}", readonly = true, required = true)
	public List<ArtifactRepository> remoteRepositories;

	@Parameter(defaultValue = "${localRepository}", readonly = true)
	public ArtifactRepository localRepository;

	@Component
	public DependencyTreeBuilder dependencyTreeBuilder;

	@Parameter(defaultValue = "${project.build.directory}", required = true)
	public File buildDir;

	@Component
	public ArtifactFactory factory;

	@Component
	public ArtifactHandlerManager artifactHandlerManager;
	@Component
	public ArtifactResolver resolver;
	DependencyNode root;
	
	@Parameter(defaultValue = "${project.compileSourceRoots}", readonly = true, required = true)
	public List<String> compileSourceRoots;
	protected void initGlobalVar() {
		MavenUtil.i().setMojo(this);

		NodeAdapters.init(root);
		DepJars.init(NodeAdapters.i());// occur jar in tree
		NodeConflicts.init(NodeAdapters.i());// version conflict in tree

		
	}
	public void execute() throws MojoExecutionException {
		this.getLog().info("method detect start:");
		try {
			root = dependencyTreeBuilder.buildDependencyTree(project, localRepository, null);
		} catch (DependencyTreeBuilderException e) {
			throw new MojoExecutionException(e.getMessage());
		}
		initGlobalVar();
		run();
		this.getLog().info("method detect end");

	}
	public abstract void run();
}
