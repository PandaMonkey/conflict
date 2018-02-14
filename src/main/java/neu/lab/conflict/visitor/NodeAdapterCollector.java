package neu.lab.conflict.visitor;

import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.traversal.DependencyNodeVisitor;

import neu.lab.conflict.Conf;
import neu.lab.conflict.container.NodeAdapters;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.NodeAdapter;

public class NodeAdapterCollector implements DependencyNodeVisitor {
	private NodeAdapters nodeAdapters;

	public NodeAdapterCollector(NodeAdapters nodeAdapters) {

		this.nodeAdapters = nodeAdapters;
	}

	public boolean visit(DependencyNode node) {

		MavenUtil.i().getLog().info(node.toNodeString() + " type:" + node.getArtifact().getType() + " version"
				+ node.getArtifact().getVersionRange() + " optional:" + node.getArtifact().isOptional());
		if (Conf.DEL_OPTIONAL) {
			if (node.getArtifact().isOptional()) {
				return false;
			}else {
				nodeAdapters.addNodeAapter(new NodeAdapter(node));
			}
		} else {
			nodeAdapters.addNodeAapter(new NodeAdapter(node));
		}

		return true;
	}

	public boolean endVisit(DependencyNode node) {
		return true;
	}
}
