package neu.lab.conflict;


import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import neu.lab.conflict.writer.ClassDupRiskWriter;

@Mojo(name = "classDupRisk", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST)
public class ClassDupRiskMojo extends ConflictMojo{

	@Override
	public void run() {
		new ClassDupRiskWriter().writeByJar();		
	}

}
