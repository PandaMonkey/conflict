package neu.lab.conflict;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.execution.MavenSession;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilderException;

import neu.lab.conflict.abandon.RiskCallAna;
import neu.lab.conflict.container.DepJars;
import neu.lab.conflict.container.FinalClasses;
import neu.lab.conflict.container.NodeAdapters;
import neu.lab.conflict.container.NodeConflicts;
import neu.lab.conflict.soot.CgAna;
import neu.lab.conflict.soot.JarAna;
import neu.lab.conflict.util.UtilGetter;
import neu.lab.conflict.writer.ResultWriter;

import java.io.File;
import java.util.List;

/**
 * Goal which touches a timestamp file.
 *
 */
@Mojo(name = "detect", defaultPhase = LifecyclePhase.VERIFY)
public class DetectMojo extends AbstractMojo {
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

	public void execute() throws MojoExecutionException {
		this.getLog().info(buildDir.getAbsolutePath());

		this.getLog().info("method detect start:");
		try {
			root = dependencyTreeBuilder.buildDependencyTree(project, localRepository, null);
		} catch (DependencyTreeBuilderException e) {
			throw new MojoExecutionException(e.getMessage());
		}
		initGlobalVar();

		getLog().info(NodeConflicts.i().toString());
		new ResultWriter().writeConflicts(NodeConflicts.i().getConflicts());
		// new LevelWriter().writeRisks(conflicts.getConflicts());

		getLog().info("jarDeconstrction time:" + JarAna.runtime);
		getLog().info("call graph time:" + CgAna.runtime);
		this.getLog().info("method detect end");

	}

	private void initGlobalVar() {
		UtilGetter.setMojo(this);

		NodeAdapters.init(root);
		DepJars.init(NodeAdapters.i());// occur jar in tree
		NodeConflicts.init(NodeAdapters.i());// version conflict in tree

		if (Conf.CLASS_DUP)
			FinalClasses.init(DepJars.i());
	}
}
// private void copyFile(File file, File file2) {
// try {
// FileUtils.copyFile(file, file2);
// } catch (IOException e) {
// getLog().error("cope error", e);
// }
// }

// /**
// * @param 将需要分析的依赖移到某个目录下，以供soot分析
// */
// private void copyDeps(Set<DepJar> deps) {
// for (DepJar dep : deps) {
// Artifact dep1 = getArtifact(dep.getGroupId(), dep.getArtifactId(),
// dep.getVersion(), dep.getClassifier());
// String outPath = dep.getDepCopyPath();
// getLog().info("copy from " + dep1.getFile().getAbsolutePath() + " to " +
// outPath);
// copyFile(dep1.getFile(), new File(outPath));
// }
// }

// private Artifact getArtifact(String groupId, String artifactId, String
// version, String classifier) {
//
// Artifact artifact = null;
// // Map dependency to artifact coordinate
// DefaultArtifactCoordinate coordinate = new DefaultArtifactCoordinate();
// coordinate.setGroupId(groupId);
// coordinate.setArtifactId(artifactId);
// coordinate.setVersion(version);
// coordinate.setClassifier(classifier);
//
// final String extension;
// ArtifactHandler artifactHandler =
// artifactHandlerManager.getArtifactHandler("jar");
// if (artifactHandler != null) {
// extension = artifactHandler.getExtension();
// } else {
// extension = "jar";
// }
// coordinate.setExtension(extension);
//
// try {
// artifact = artifactResolver.resolveArtifact(buildingRequest,
// coordinate).getArtifact();
// } catch (ArtifactResolverException e) {
// e.printStackTrace();
// getLog().error("Unable to find/resolve artifact.", e);
// }
// return artifact;
// }
