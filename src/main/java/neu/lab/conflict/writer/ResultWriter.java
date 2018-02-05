package neu.lab.conflict.writer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.DepJar;
import neu.lab.conflict.vo.NodeConflict;

public class ResultWriter {
	PrintStream printer;

	public ResultWriter() {
		try {
			String outPath = "d://conflict.txt";
			// File out = new File(outPath);
			// if (out.exists())
			// out.delete();
			printer = new PrintStream(new FileOutputStream(new File(outPath), true));
		} catch (FileNotFoundException e) {
			MavenUtil.i().getLog().error("cant open risk result file!", e);
		}
	}

	public void writeConflicts(List<NodeConflict> conflicts) {
		printer.println("===============" + MavenUtil.i().getBuildDir().getAbsolutePath());
		if (conflicts.size() == 0) {
			printer.println("NO_CONFLICT");
		}
		for (NodeConflict conflict : conflicts) {
			// if (conflict.sameArtifact("org.apache.avro", "avro"))
			printer.println(conflict.getRiskAna().getRiskString());
			// writeConflict(conflict);
		}

		printer.close();
	}

	private void writeConflict(NodeConflict conflict) {
		// printer.println(conflict.toString());
		//
		// Set<String> rchMthds = conflict.getRchedMthds();
		//
		// DepJar usedJar = conflict.getUsedDepJar();
		// writeJarRch(rchMthds, usedJar);
		// for (DepJar depJar : conflict.getDepJars()) {
		// if (depJar != usedJar) {
		// writeJarRch(rchMthds, depJar);
		// }
		// }
		// printer.println("\n");
		//
		// for (JarRiskAna jarRisk : conflict.getJarRiskAnas()) {
		//// printer.println(jarRisk.getRiskStr());
		// printer.println();
		// }
	}

	private void writeJarRch(Set<String> rchMthds, DepJar depJar) {
		Set<String> onlyMthds = min(rchMthds, depJar.getAllMthd());
		printer.println("reachmethod dont exist in " + depJar.toString() + " (" + onlyMthds.size() + "/"
				+ rchMthds.size() + ")");
		for (String onlyMthd : onlyMthds) {
			printer.println("-" + onlyMthd);
		}
		printer.println();
	}

	private Set<String> min(Set<String> total, Set<String> some) {
		Set<String> only = new HashSet<String>();
		for (String str : total) {
			if (!some.contains(str))
				only.add(str);
		}
		return only;
	}

}
