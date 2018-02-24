package neu.lab.conflict;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import neu.lab.conflict.container.NodeConflicts;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.NodeAdapter;
import neu.lab.conflict.vo.NodeConflict;

@Mojo(name = "debug", defaultPhase = LifecyclePhase.VALIDATE)
public class DebugMojo extends ConflictMojo{

	@Override
	public void run() {
		write(Conf.outDir+"debug.txt");
		
	}
	public void write(String outPath) {
		try {
			PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(new File(outPath), true)));
			printer.println("===============projectPath->" + MavenUtil.i().getProjectInfo());
			printer.close();
		} catch (Exception e) {
			MavenUtil.i().getLog().error("can't write versionupdate:", e);
		}
	}
}
