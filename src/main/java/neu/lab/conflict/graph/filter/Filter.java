package neu.lab.conflict.graph.filter;

public interface Filter {
	/**should filte method 应该过滤掉方法
	 * @param mthdSig
	 * @return
	 */
	public boolean shdFltM(String mthdSig);
}
