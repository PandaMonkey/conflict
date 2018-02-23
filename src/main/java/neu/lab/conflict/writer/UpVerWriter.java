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
			PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(new File(outPath), true)));

			for (NodeConflict nodeConflict : NodeConflicts.i().getConflicts()) {
				if (nodeConflict.getNodeAdapters().size() == 2) {// only two version
					NodeAdapter[] depJars = nodeConflict.getNodeAdapters().toArray(new NodeAdapter[2]);
					NodeAdapter node1 = depJars[0];
					NodeAdapter node2 = depJars[1];
					if (node1 != null && node2 != null) {
						if (node1.getNodeDepth() <= 2 || node2.getNodeDepth() <= 2) {
							if (canUp(node1.getWholePath(), node2.getWholePath())) {
								printer.println("===============projectPath->" + MavenUtil.i().getProjectInfo());
								printer.println("=======conflict:" + "<" + node1.toString() + ">" + "<"
										+ node2.toString() + ">" + " size:" + nodeConflict.getNodeAdapters().size());
								printer.println(node1.getWholePath());
								printer.println(node2.getWholePath());
								printer.println();
							}
						}
					}
				}
			}

			printer.close();
		} catch (Exception e) {
			MavenUtil.i().getLog().error("can't write versionupdate:", e);
		}
	}

	public boolean canUp(String path1, String path2) {
		String[] arr1 = path1.split("->");
		String[] arr2 = path2.split("->");
		String longDep = "";
		String shortDep = "";
		if (arr1.length > arr2.length) {
			shortDep = arr2[arr2.length - 1];
			longDep = arr1[arr1.length - 1];
			return shortIsLow(shortDep, longDep);
		} else if (arr2.length > arr1.length) {
			shortDep = arr1[arr1.length - 1];
			longDep = arr2[arr2.length - 1];
			return shortIsLow(shortDep, longDep);
		}
		return false;
	}

	private boolean shortIsLow(String shortDep, String longDep) {
		if (shortDep.endsWith("::compile") && longDep.endsWith("::compile")) {
			String[] arr1 = shortDep.split(":");
			String[] arr2 = longDep.split(":");
			if (arr1.length == 5 && arr2.length == 5) {
				String[] shortVersion = arr1[2].split("\\.");
				String[] longVersion = arr2[2].split("\\.");
				for (int i = 0; i < shortVersion.length; i++) {
					int num1 = 0;
					int num2 = 0;
					try {
						num1 = Integer.parseInt(shortVersion[i]);
						num2 = Integer.parseInt(longVersion[i]);
						if (num1 < num2) {
							return true;
						}
						if (num1 > num2) {
							return false;
						}
					} catch (Exception e) {
						return false;
					}
				}
			}
		}
		return false;
	}
}
