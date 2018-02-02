package neu.lab.conflict.abandon;

import neu.lab.conflict.soot.ThrowRiskTf;
import soot.jimple.toolkits.callgraph.Edge;
/**
 * collect risk call caused by throw class
 * @author asus
 *
 */
public class JarThrowTf extends ThrowRiskTf {
	protected boolean isRiskCall(Edge edge) {
		String srcCls = edge.src().getDeclaringClass().getName();
		return riskMthds.contains(edge.tgt().getSignature()) && !riskJarClses.contains(srcCls);// target method is risk
																								// and source method is
																								// from another jar
	}
}
