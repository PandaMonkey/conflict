package neu.lab.conflict.soot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import neu.lab.conflict.util.SootUtil;
import neu.lab.conflict.Conf;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.ClassVO;
import neu.lab.conflict.vo.MethodVO;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.SourceLocator;
import soot.Transform;

public class JarAna extends SootAna {
	public static long runtime = 0;
	private static JarAna instance = new JarAna();

	private JarAna() {

	}

	public static JarAna i() {
		return instance;
	}

	public Map<String, ClassVO> deconstruct(List<String> jarFilePath) {
		MavenUtil.i().getLog().info("use soot to deconstruct " + jarFilePath);

		long startTime = System.currentTimeMillis();

		Map<String, ClassVO> clses = new HashMap<String, ClassVO>();

		PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTrans", new DsTransformer(clses, jarFilePath)));
		SootUtil.modifyLogOut();
		try {
			soot.Main.main(getArgs(jarFilePath.toArray(new String[0])).toArray(new String[0]));
		} catch (IllegalArgumentException e) {
			MavenUtil.i().getLog().error("cant deconstruct " + jarFilePath, e);
		}

		soot.G.reset();

		runtime = runtime + (System.currentTimeMillis() - startTime) / 1000;
		return clses;
	}

	protected void addCgArgs(List<String> argsList) {
		argsList.addAll(Arrays.asList(new String[] { "-p", "cg", "off", }));
	}

}

class DsTransformer extends SceneTransformer {
	private Map<String, ClassVO> clsTb;
	private List<String> jarPaths;

	public DsTransformer(Map<String, ClassVO> clses, List<String> jarPaths) {
		this.clsTb = clses;
		this.jarPaths = jarPaths;
	}

	@Override
	protected void internalTransform(String phaseName, Map<String, String> options) {
		for (String clsSig : SootUtil.getJarClasses(jarPaths)) {
			SootClass sootClass = Scene.v().getSootClass(clsSig);
			ClassVO clsVO = new ClassVO(sootClass.getName());
			clsTb.put(sootClass.getName(), clsVO);
			if (Conf.ONLY_GET_SIMPLE) {// only add simple method in simple class
				if (isSimpleCls(sootClass)) {
					for (SootMethod sootMethod : sootClass.getMethods()) {
						if (sootMethod.getParameterCount() == 0)
							clsVO.addMethod(new MethodVO(sootMethod.getSignature(), clsVO));
					}
				}
			} else {// add all method
				for (SootMethod sootMethod : sootClass.getMethods()) {
					clsVO.addMethod(new MethodVO(sootMethod.getSignature(), clsVO));
				}
			}
		}
	}

	private boolean isSimpleCls(SootClass sootClass) {
		for (SootMethod sootMethod : sootClass.getMethods()) {
			if (sootMethod.isConstructor() && sootMethod.getParameterCount() == 0) // exist constructor that doesn't
																					// need param
				return true;
		}
		return false;
	}

}
