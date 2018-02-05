package neu.lab.conflict.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

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
}
