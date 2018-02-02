package neu.lab.conflict.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import neu.lab.conflict.util.SootUtil;

public class Node {
	private String name;
	private boolean isHostNode;
	private Set<String> inNds;
	private Set<String> outNds;
	public Node(String name,boolean isHostNode) {
		this.name = name;
		this.isHostNode = isHostNode;
		inNds = new HashSet<String>();
		outNds = new HashSet<String>();
	}

	public Set<String> getOutNds() {
		return outNds;
	}

	public String getName() {
		return name;
	}
	public Set<String> getInNds(){
		return inNds;
	}
	public boolean isHostNode() {
		return isHostNode;
	}
	public void addInNd(String inNd) {
		inNds.add(inNd);
	}
	public void delInNd(String delNd) {
		inNds.remove(delNd);
	}
	public void addOutNd(String outNd) {
		outNds.add(outNd);
	}
	public void delOutNd(String delNd) {
		outNds.remove(delNd);
	}
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	@Override
	public boolean equals(Object other) {
		if(other instanceof Node) {
			Node otherNode = (Node)other;
			return name.equals(otherNode.getName());
		}
		return false;
	}
	/**
	 * 计算outMthds中每个函数名字出现的次数
	 * 
	 * @param outMthds
	 * @return <k,v> k:函数名字 v:出现的次数
	 */
	public Map<String, Integer> calNameCnt() {
		Map<String, Integer> result = new HashMap<String, Integer>();
		if (outNds != null) {
			// 对每一个方法进行计数
			for (String mthdSig : outNds) {
				String mthdName = SootUtil.mthdSig2Name(mthdSig);
				Integer cnt = result.get(mthdName);
				if (null == cnt) {
					result.put(mthdName, new Integer(1));
				} else {
					result.put(mthdName, cnt + 1);
				}
			}
		}
		return result;
	}
	public String toString() {
		StringBuilder sb = new StringBuilder(name+" isHost:"+isHostNode+"\n");
		sb.append("--inMthd:\n");
		for(String in:inNds) {
			sb.append(in);
			sb.append("\n");
		}
		sb.append("--outMthd:\n");
		for(String out:outNds) {
			sb.append(out);
			sb.append("\n");
		}
		return sb.toString();
	}
}
