package neu.lab.conflict;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import neu.lab.conflict.container.DepJars;
import neu.lab.conflict.container.FinalClasses;
import neu.lab.conflict.container.NodeConflicts;
import neu.lab.conflict.soot.CgAna;
import neu.lab.conflict.soot.JarAna;
import neu.lab.conflict.vo.NodeConflict;
import neu.lab.conflict.writer.ResultWriter;

/**
 * Goal which touches a timestamp file.
 *
 */
@Mojo(name = "detect", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST)
public class DetectMojo extends ConflictMojo {

	@Override
	public void run() {
		
		if (Conf.CLASS_DUP)
			FinalClasses.init(DepJars.i());
		
//		new ResultWriter().writeConflicts(NodeConflicts.i().getConflicts());
		for(NodeConflict conflict:NodeConflicts.i().getConflicts()) {
			System.out.println(conflict.getUsedDepJar().toString());
		}
		
		getLog().info("jarDeconstrction time:" + JarAna.runtime);
		getLog().info("call graph time:" + CgAna.runtime);

	}

}
