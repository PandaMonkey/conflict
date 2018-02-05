package neu.lab.conflict.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import neu.lab.conflict.Conf;

public class Book {
	protected Node node;
	private Set<Path> paths;

	public Book(Node node) {
		this.node = node;
		paths = new HashSet<Path>();
	}

	// 将子级node的手册copy到自己的手册中
	public void addChild(Book childBook) {
		Set<Path> childRecords = childBook.getPaths();
		for (Path path : childRecords) {
			paths.add(path.clone());
		}
	}

	// 在手册完成时，将自己的节点信息加入到手册
	public void addSelf() {
		if (paths.isEmpty()) {// 该节点没有任何的调用
			Path path = new Path(node.getName(), node.isHostNode(), 1);
			paths.add(path);
		} else {// 该节点有调用，将自己的jar包添加到每一条调用路径的最前面
			addNdToAll(node.getName());
		}
	}

	// 为book的每条record添加node;
	public void addNdToAll(String node) {
		for (Path path : paths) {
			path.addTail(node);
		}
	}

	// 得到book中的每条记录
	public Set<Path> getPaths() {
		return paths;
	}

	/**
	 * @return path from host to conflict node
	 */
	public List<Path> getRiskPath() {
		List<Path> riskPaths = new ArrayList<Path>();
		for (Path path : getPaths()) {
			if (path.isFromHost() && path.getPathLen() >= Conf.MIN_PATH_DEP)//path whose depth is 2 is unreasonable.
				riskPaths.add(path);
		}
		return riskPaths;
	}

	// //得到book中的每条记录
	// public void copy(Book book) {
	//
	// }
	public String toString() {
		StringBuilder sb = new StringBuilder(node.getName() + "\n");
		for (Path path : paths) {
			sb.append("-");
			sb.append(path.getPathStr());
			sb.append("\n");
		}
		return sb.toString();
	}
}
