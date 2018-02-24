package neu.lab.conflict;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import neu.lab.conflict.writer.JarDupRiskWriter;
import neu.lab.conflict.writer.VersionWriter;

@Mojo(name = "findVersion", defaultPhase = LifecyclePhase.VALIDATE)
public class FindVersionMojo extends ConflictMojo{

	@Override
	public void run() {
		new VersionWriter().write(Conf.outDir + "projectVersions.txt");
		
	}

}
