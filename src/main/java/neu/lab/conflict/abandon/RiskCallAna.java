package neu.lab.conflict.abandon;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import neu.lab.conflict.soot.SootAna;
import neu.lab.conflict.soot.ThrowRiskTf;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.MethodCall;
import neu.lab.conflict.vo.risk.NodeRiskAna;
import soot.PackManager;
import soot.Transform;

public class RiskCallAna extends SootAna {
	public static long runtime = 0;
	private Set<String> riskMthds;

	public RiskCallAna(Set<String> riskMthds) {
		this.riskMthds = riskMthds;
	}

	public Set<MethodCall> getRiskCalls(NodeRiskAna sootAnaUnit,ThrowRiskTf transformer) {
		MavenUtil.i().getLog().info("use soot to compute risk call for " + sootAnaUnit.toString());

		long startTime = System.currentTimeMillis();

		Set<MethodCall> relations = new HashSet<MethodCall>();
		transformer.init(sootAnaUnit, relations, riskMthds);
		PackManager.v().getPack("wjtp")
				.add(new Transform("wjtp.myTrans", transformer));
		soot.Main.main(getArgs(sootAnaUnit.getJarFilePaths().toArray(new String[0])).toArray(new String[0]));// 解析系统中存在的node以及node之间的关系
		soot.G.reset();

		runtime = runtime + (System.currentTimeMillis() - startTime) / 1000;
		return relations;
	}

	protected void addCgArgs(List<String> argsList) {
		argsList.addAll(Arrays.asList(new String[] { "-p", "cg", "off", }));
	}

}
