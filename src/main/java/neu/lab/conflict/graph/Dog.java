package neu.lab.conflict.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import neu.lab.conflict.Conf;
import neu.lab.conflict.util.UtilGetter;

/**
 * 不断进行深度优先遍历，获取想要获得的信息
 * 
 * @author asus
 *
 */
public class Dog {
	private Graph graph;
	protected String pos;// 记录dog的当前位置
	protected List<String> route;// 记录dog到达当前位置的路线，以便进行回退

	protected Map<String, Cross> graphMap = new HashMap<String, Cross>();

	protected Map<String, List<String>> circleMap = new HashMap<String, List<String>>();// 记录回路；a->b->c->a在map中的记录为
																						// <a,[b,c]>
	protected Map<String, Book> books = new HashMap<String, Book>();// node to
	// reachJar：记录每个方法节点可以到达的jar包

	protected Map<String, Book> tempBooks = new HashMap<String, Book>();

	public Dog(Graph graph) {
		this.graph = graph;
	}

	protected Book buyNodeBook(String nodeName) {
		return new Book(graph.getNode(nodeName));
	}

	public Map<String, Book> findRlt(Set<String> entrys) {
		long start = System.currentTimeMillis();
		for (String mthd : entrys) {
			route = new ArrayList<String>();
			if (books.containsKey(mthd))
				continue;
			else {
				forward(mthd);
				while (pos != null) {
					if (needChildBook()) {// 当前位置还有分支
						String frontNode = graphMap.get(pos).getBranch();
						getChildBook(frontNode);
					} else {
						back();
					}
				}
			}
		}
		long runtime = (System.currentTimeMillis() - start) / 1000;
		UtilGetter.i().getLog().info("dog run time:" + runtime);
		return this.books;
	}

	public boolean needChildBook() {
		return graphMap.get(pos).hasBranch() && route.size() < Conf.PATH_DEP;
		// return graphMap.get(pos).hasBranch();
	}

	private void getChildBook(String frontNode) {
		if (books.containsKey(frontNode)) {// child有完成的手册
			addChildBook(frontNode, pos);
		} else {
			forward(frontNode);
		}

	}

	/**
	 * frontNode是一个手册没有完成的节点，需要为这个节点建立手册
	 * 
	 * @param frontNode
	 */
	private void forward(String frontNode) {
		Node node = graph.getNode(frontNode);
		if (node != null) {
			if (!route.contains(frontNode)) {// 不构成回路
				pos = frontNode;// 位置前进
				route.add(pos);// 路线添加新节点
				Book nodeRch = buyNodeBook(frontNode);// 得到该节点的记录手册
				this.tempBooks.put(frontNode, nodeRch);// <方法节点，可达jar包>的临时map中放入该节点
				graphMap.put(pos, new Cross(node));// 更新地图
			} else {// 构成了回路，需要将回路记录下来，在回路的开始节点变为done的时候，done节点的可达节点为回路中所有节点的可达节点
				List<String> circle = new ArrayList<String>();
				int index = route.indexOf(frontNode) + 1;
				while (index < route.size()) {
					circle.add(route.get(index));
					index++;
				}
				this.circleMap.put(frontNode, circle);
			}
		}
	}

	private void back() {
		String donePos = route.get(route.size() - 1);
		graphMap.remove(donePos);// 不再需要这个method的地图

		// 在jar包的调用路径上将自己加入路径的最前面
		Book book = this.tempBooks.get(donePos);
		book.addSelf();
		// 键值对从临时map中放入最终map
		this.tempBooks.remove(donePos);
		this.books.put(donePos, book);

		// 有包含该节点的环路，则环路上的后面节点应该更新
		if (circleMap.containsKey(donePos)) {

			dealLoopNd(donePos);
			circleMap.remove(donePos);
		}

		route.remove(route.size() - 1);// 路线回退
		// 位置更新
		if (route.size() == 0) {
			pos = null;
		} else {
			pos = route.get(route.size() - 1);
			addChildBook(donePos, pos);// 将处理完的分支mthd内的调用添加到pos中
		}
	}

	private void addChildBook(String donePos, String pos) {
		Book doneBook = this.books.get(donePos);
		Book doingBook = this.tempBooks.get(pos);
		doingBook.addChild(doneBook);
	}

	protected void dealLoopNd(String donePos) {
		// TODO
	}

}
