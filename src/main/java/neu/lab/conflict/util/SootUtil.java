package neu.lab.conflict.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import neu.lab.conflict.vo.DepJar;
import soot.SourceLocator;

public class SootUtil {
	public static void modifyLogOut() {
		File outDir = MavenUtil.i().getBuildDir();
		if (!outDir.exists()) {
			outDir.mkdirs();
		}
		try {
			soot.G.v().out = new PrintStream(new File(outDir.getAbsolutePath() + "/" + "soot.log"));
		} catch (FileNotFoundException e) {
			soot.G.v().out = System.out;
		}
	}

	/**
	 * @param mthdSig
	 *            eg.:<org.slf4j.event.SubstituteLoggingEvent: org.slf4j.event.Level
	 *            getLevel()>
	 * @return eg.: org.slf4j.event.Level getLevel();
	 */
	public static String mthdSig2Name(String mthdSig) {
		return mthdSig.substring(mthdSig.indexOf(":") + 1, mthdSig.indexOf(")") + 1);
	}

	public static List<String> getJarClasses(DepJar depJar) {
		return getJarClasses(depJar.getClassPath());
	}

	public static List<String> getJarClasses(List<String> paths) {
		List<String> allCls = new ArrayList<String>();
		for (String classPath : paths) {
			if (new File(classPath).exists()) {
				if (!classPath.endsWith("tar.gz")&&!classPath.endsWith(".pom")) {
//					System.out.println("get class under:" + classPath);
					allCls.addAll(SourceLocator.v().getClassesUnder(classPath));
				}else {
					MavenUtil.i().getLog().warn(classPath + "is illegal classpath");
				}
			} else {
				MavenUtil.i().getLog().warn(classPath + "doesn't exist in local");
			}

		}
		return allCls;
	}
}
