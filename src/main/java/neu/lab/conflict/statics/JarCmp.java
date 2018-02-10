package neu.lab.conflict.statics;

import java.util.ArrayList;
import java.util.List;

import neu.lab.conflict.vo.DepJar;
import neu.lab.conflict.vo.MethodVO;

public class JarCmp {
	private DepJar jar1;
	private DepJar jar2;
	private List<String> clsSigs;

	public JarCmp(DepJar jarA, DepJar jarB) {
		jar1 = jarA;
		jar2 = jarB;
		clsSigs = new ArrayList<String>();
	}

	public void addClass(String clsSig) {
		clsSigs.add(clsSig);
	}

	public boolean isSelf(DepJar jarA, DepJar jarB) {
		return (jar1.equals(jarA) && jar2.equals(jarB)) || (jar1.equals(jarB) && jar2.equals(jarA));
	}

	public DepJar getJar1() {
		return jar1;
	}

	public DepJar getJar2() {
		return jar2;
	}

	public String getRiskString() {
		StringBuilder sb = new StringBuilder("risk for jar-pair:");
		sb.append("<" + jar1.toString() + ">");
		sb.append("<" + jar2.toString() + ">\n");
		sb.append(getJarString(jar1, jar2));
		sb.append(getJarString(jar2, jar1));
		return sb.toString();
	}

	private String getJarString(DepJar total, DepJar some) {
		StringBuilder sb = new StringBuilder();
		List<String> onlyMthds = getOnlyMethod(total, some);
		if (onlyMthds.size() > 0) {
			sb.append("   methods that only exist in "  + total.getValidDepPath() + "\n");
			for (String onlyMthd : onlyMthds) {
				sb.append(onlyMthd + "\n");
			}
		}
		return sb.toString();
	}

	private List<String> getOnlyMethod(DepJar total, DepJar some) {
		List<String> onlyMthds = new ArrayList<String>();
		for (String clsSig : clsSigs) {
			for (MethodVO mthd : total.getClassVO(clsSig).getMthds()) {
				if (!some.getClassVO(clsSig).hasMethod(mthd.getMthdSig()))
					onlyMthds.add(mthd.getMthdSig());
			}
		}
		return onlyMthds;
	}

	// @Override
	// public int hashCode() {
	// return jar1.hashCode() + jar2.hashCode();
	// }
	//
	// @Override
	// public boolean equals(Object obj) {
	// if (obj instanceof JarCmp) {
	// JarCmp other = (JarCmp) obj;
	// return (jar1.equals(other.getJar1()) && jar2.equals(other.getJar2()))
	// || (jar1.equals(other.getJar2()) && jar2.equals(other.getJar1()));
	// }
	// return false;
	// }
}