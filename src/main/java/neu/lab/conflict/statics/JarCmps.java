package neu.lab.conflict.statics;

import java.util.ArrayList;
import java.util.List;

import neu.lab.conflict.vo.DepJar;

public class JarCmps {
	private List<JarCmp> container;

	public JarCmps(ClassDups classDups) {
		container = new ArrayList<JarCmp>();
		for (ClassDup classDup : classDups.getAllClsDup()) {
			List<DepJar> jars = classDup.getDepJars();
			for (int i = 0; i < jars.size() - 1; i++) {
				for (int j = i + 1; j < jars.size(); j++) {
					JarCmp jarCmp = getJarCmp(jars.get(i), jars.get(j));
					jarCmp.addClass(classDup.getClsSig());
				}
			}
		}
	}

	public List<JarCmp> getAllJarCmp() {
		return container;
	}

	private JarCmp getJarCmp(DepJar jarA, DepJar jarB) {
		for (JarCmp jarCmp : container) {
			if (jarCmp.isSelf(jarA, jarB))
				return jarCmp;
		}
		// can't find jarCmp in container,create a new one.
		JarCmp jarCmp = new JarCmp(jarA, jarB);
		container.add(jarCmp);
		return jarCmp;
	}
}
