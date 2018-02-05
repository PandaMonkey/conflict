package neu.lab.conflict.statics;

import java.util.ArrayList;
import java.util.List;
import neu.lab.conflict.vo.DepJar;

public class ClassDup {
	private String clsSig;
	private List<DepJar> depJars;

	public ClassDup(String clsSig) {
		this.clsSig = clsSig;
		depJars = new ArrayList<DepJar>();
	}

	public boolean isSelf(String otherSig) {
		return clsSig.equals(otherSig);
	}

	public void addDepJar(DepJar depJar) {
		// boolean isNew = true;
		// for(DepJar oldDep:depJars) {
		// if(oldDep.isSameLib(depJar))
		// isNew = false;
		// }
		// if (isNew)
			depJars.add(depJar);
	}

	public boolean isDup() {
		return depJars.size() > 1;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(clsSig + " exist in :");
		for (DepJar depJar : depJars) {
			sb.append(" (" + depJar.toString() + ")");
		}
		return sb.toString();
	}
}
