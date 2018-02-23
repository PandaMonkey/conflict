package neu.lab.conflict;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import neu.lab.conflict.writer.JarDupRiskWriter;

@Mojo(name = "jarDupRisk", defaultPhase = LifecyclePhase.VALIDATE)
public class JarDupRiskMojo extends ConflictMojo{

	@Override
	public void run() {
		new JarDupRiskWriter().write(Conf.outDir + "jarDupRisk.txt");
	}

}
