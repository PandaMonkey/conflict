package neu.lab.conflict.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import neu.lab.conflict.Conf;
import neu.lab.conflict.container.DepJars;
import neu.lab.conflict.statics.ClassDup;
import neu.lab.conflict.statics.ClassDups;
import neu.lab.conflict.statics.JarCmp;
import neu.lab.conflict.statics.JarCmps;
import neu.lab.conflict.util.MavenUtil;

public class ClassDupRiskWriter {

	private ClassDups classDups;

	public ClassDupRiskWriter() {
		classDups = new ClassDups(DepJars.i());
	}

	public void writeByClass() {
		try {
			PrintWriter printer = new PrintWriter(
					new BufferedWriter(new FileWriter(new File(Conf.outDir + "classDupRisk.txt"), true)));
			printer.println("===============projectPath->" + MavenUtil.i().getProjectInfo());

			printer.println("=====class duplicate:");
			for (ClassDup classDup : classDups.getAllClsDup()) {
				printer.println(classDup.getRiskString());
			}
			printer.println("\n\n\n\n");
			printer.close();
		} catch (Exception e) {
			MavenUtil.i().getLog().error("can't write classDupByClass:", e);
		}
	}
	
	public void writeByJar() {
		JarCmps jarCmps = new JarCmps(classDups);
		try {
			PrintWriter printer = new PrintWriter(
					new BufferedWriter(new FileWriter(new File(Conf.outDir + "classDupByJar.txt"), true)));
			printer.println("===============projectPath->" + MavenUtil.i().getProjectInfo());

			for (JarCmp jarCmp : jarCmps.getAllJarCmp()) {
				printer.println(jarCmp.getRiskString());
			}
			printer.println("\n\n\n\n");
			printer.close();
		} catch (Exception e) {
			MavenUtil.i().getLog().error("can't write classDupByJar:", e);
		}
	}
}
