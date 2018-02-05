package neu.lab.conflict.vo.risk;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import neu.lab.conflict.Conf;
import neu.lab.conflict.graph.Book;
import neu.lab.conflict.graph.Path;
import neu.lab.conflict.vo.NodeAdapter;

/**
 * @author asus soot进行cg运算的单元
 */
public class NodeRiskAna {
	private LinkedList<NodeAdapter> jarPath;// there is order(from down to up)
	// private JarRiskAna jarRiskAna;
	private List<String> rchedMthds;// reached method in call-graph computed(entry class is host class)
	private Set<String> risk1Mthds;// reached and thrown
	private Set<String> risk2Mthds;// reached and thrown and called by method in other jar.

	private Map<String, Book> books;// reached path of method in risk2Mthds

	public NodeRiskAna(LinkedList<NodeAdapter> jarPath) {
		this.jarPath = jarPath;
	}

	public NodeAdapter getBottomJar() {
		return jarPath.getFirst();
	}

	public NodeAdapter getTopJar() {
		return jarPath.getLast();
	}

	public void add(NodeAdapter nodeAdapter) {
		jarPath.add(nodeAdapter);
	}

	public List<NodeAdapter> getAnaNodes() {
		return jarPath;
	}

	public List<String> getJarFilePaths() {
		List<String> paths = new ArrayList<String>();
		for (NodeAdapter nodeAdapter : jarPath) {
			paths.addAll(nodeAdapter.getFilePath());
		}
		return paths;
	}

	public String getRiskString() {
		List<String> confuseMthds = new ArrayList<String>();
		StringBuilder sb = new StringBuilder("risk for node:");
		sb.append(toString() + "\n");
		sb.append("reached size: " + rchedMthds.size() + " reached_thrown size:" + risk1Mthds.size()
				+ " reached_thrown_service:" + risk2Mthds.size() + "\n");
		for (String risk2Mthd : risk2Mthds) {
			Book book = books.get(risk2Mthd);
			if (book == null) {
				confuseMthds.add(risk2Mthd);
			} else {
				List<Path> riskPath = book.getRiskPath();
				if (riskPath.size() == 0) {
					// this method is reached by host on soot-call-graph,but can't find path for
					// it.Confusion may be cause by algorithm or by call filter
					confuseMthds.add(risk2Mthd);
				} else {
					sb.append("reached path for:" + risk2Mthd + "\n");
					for (Path path : riskPath) {
						sb.append("---->" + path.toString() + "\n");
					}
				}
			}
		}
		if (Conf.PRINT_CONFUSED_METHOD) {
			if (confuseMthds.size() != 0) {
				sb.append("can't find path for " + confuseMthds.size() + " risk method+\n");
				for (String confuseMthd : confuseMthds) {
					sb.append("confuseMthd:" + confuseMthd + "\n");
				}
			}
		}

		return sb.toString();
	}

	@Override
	public String toString() {
		String str = "";
		for (NodeAdapter nodeAdapter : jarPath) {
			str = str + nodeAdapter.toString() + "->";
		}
		return str;
	}

	public List<String> getRchedMthds() {
		return rchedMthds;
	}

	public void setRchedMthds(List<String> rchedMthds) {
		this.rchedMthds = rchedMthds;
	}

	public Set<String> getRisk1Mthds() {
		return risk1Mthds;
	}

	public void setRisk1Mthds(Set<String> risk1Mthds) {
		this.risk1Mthds = risk1Mthds;
	}

	public Set<String> getRisk2Mthds() {
		return risk2Mthds;
	}

	public void setRisk2Mthds(Set<String> risk2Mthds) {
		this.risk2Mthds = risk2Mthds;
	}

	public Map<String, Book> getBooks() {
		return books;
	}

	public void setBooks(Map<String, Book> books) {
		this.books = books;
	}

}
