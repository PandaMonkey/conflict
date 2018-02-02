package neu.lab.conflict.soot;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import neu.lab.conflict.util.DetectUtil;
import neu.lab.conflict.util.SootUtil;
import neu.lab.conflict.util.UtilGetter;
import neu.lab.conflict.vo.ClassVO;
import neu.lab.conflict.vo.MethodVO;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;

public class JarAna extends SootAna {
	public static long runtime = 0;
	private static JarAna instance = new JarAna();

	private JarAna() {

	}

	public static JarAna i() {
		return instance;
	}

	public Map<String,ClassVO> deconstruct(String jarFilePath) {
		UtilGetter.i().getLog().info("use soot to deconstruct " + jarFilePath);

		long startTime = System.currentTimeMillis();

		Map<String,ClassVO> clses = new HashMap<String,ClassVO>();
		if (!new File(jarFilePath).exists()) {
			UtilGetter.i().getLog().warn(jarFilePath + " doesnt exist!");
			return clses;
		}

		PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTrans", new DsTransformer(clses)));
		SootUtil.modifyLogOut();
		try {
			soot.Main.main(getArgs(new String[] { jarFilePath }).toArray(new String[0]));// 解析系统中存在的node以及node之间的关系
		} catch (IllegalArgumentException e) {
			UtilGetter.i().getLog().error("cant deconstruct " + jarFilePath,e);
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
	private Map<String,ClassVO> clses;

	public DsTransformer(Map<String,ClassVO> clses) {
		this.clses = clses;
	}

	@Override
	protected void internalTransform(String phaseName, Map<String, String> options) {
		for (SootClass sootClass : Scene.v().getApplicationClasses()) {
			ClassVO clsVO = new ClassVO(sootClass.getName());
			clses.put(sootClass.getName(), clsVO);
			for (SootMethod sootMethod : sootClass.getMethods()) {
				clsVO.addMethod(new MethodVO(sootMethod.getSignature(), clsVO));
			}
		}
	}

}
