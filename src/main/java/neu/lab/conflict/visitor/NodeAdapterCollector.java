package neu.lab.conflict.visitor;

import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.traversal.DependencyNodeVisitor;

import neu.lab.conflict.container.NodeAdapters;
import neu.lab.conflict.util.UtilGetter;
import neu.lab.conflict.vo.NodeAdapter;

public class NodeAdapterCollector implements DependencyNodeVisitor {
	private NodeAdapters nodeAdapters;

	public NodeAdapterCollector(NodeAdapters nodeAdapters) {

		this.nodeAdapters = nodeAdapters;
	}

	public boolean visit(DependencyNode node) {

		UtilGetter.i().getLog().info(node.toNodeString() + " type:" + node.getArtifact().getType() + " version"
				+ node.getArtifact().getVersionRange());
		nodeAdapters.addNodeAapter(new NodeAdapter(node));
		return true;
	}

	public boolean endVisit(DependencyNode node) {
		return true;
	}
}
