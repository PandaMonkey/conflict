package neu.lab.conflict;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import neu.lab.conflict.writer.UpVerWriter;

@Mojo(name = "upVersion", defaultPhase = LifecyclePhase.VALIDATE)
public class UpdateVersionMojo extends ConflictMojo{

	@Override
	public void run() {
		new UpVerWriter().write(Conf.outDir + "versionUpdate.txt");
	}

}
