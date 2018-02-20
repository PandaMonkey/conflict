package neu.lab.conflict.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import neu.lab.conflict.container.NodeConflicts;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.NodeAdapter;
import neu.lab.conflict.vo.NodeConflict;

public class UpVerWriter {
	public void write(String outPath) {
		try {
			PrintWriter printer = new PrintWriter(
					new BufferedWriter(new FileWriter(new File(outPath), true)));
			printer.println("===============projectPath->" + MavenUtil.i().getProjectInfo());
			for (NodeConflict nodeConflict : NodeConflicts.i().getConflicts()) {

				if (nodeConflict.getNodeAdapters().size() == 2) {// only two version
					NodeAdapter[] depJars = nodeConflict.getNodeAdapters().toArray(new NodeAdapter[2]);
					NodeAdapter node1 = depJars[0];
					NodeAdapter node2 = depJars[1];
					if (node1 != null && node2 != null) {
						if (node1.getNodeDepth() <= 2 || node2.getNodeDepth() <= 2) {
							printer.println("=======conflict:" + "<" + node1.toString() + ">" + "<" + node2.toString()
									+ ">" + " size:" + nodeConflict.getNodeAdapters().size());
							printer.println(node1.getWholePath());
							printer.println(node2.getWholePath());
							printer.println();
						}
					}
				}
			}

			printer.println("\n\n");
			printer.close();
		} catch (Exception e) {
			MavenUtil.i().getLog().error("can't write versionupdate:", e);
		}
	}
}
