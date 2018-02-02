package neu.lab.conflict.soot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SootAna {

	protected List<String> getArgs(String[] jarFilePaths) {
		List<String> argsList = new ArrayList<String>();

		addGenArg(argsList);
		addClassPath(argsList, jarFilePaths);
		addCgArgs(argsList);
		addIgrArgs(argsList);

		return argsList;
	}

	protected abstract void addCgArgs(List<String> argsList);

	protected void addClassPath(List<String> argsList, String[] jarFilePaths) {
		for (String jarFilePath : jarFilePaths) {
			argsList.add("-process-dir");
			argsList.add(jarFilePath);
		}
	}

	protected void addGenArg(List<String> argsList) {

		// argsList.add("-pp");// 将soot的classPath中的类用于解析
		argsList.add("-ire");// 忽略classPath中的无效实体
		argsList.add("-app");// 所有的类都将作为appClass
		argsList.add("-allow-phantom-refs");// 允许无效的类型解析
		argsList.add("-w");// 整个项目解析

		
	}

	protected void addIgrArgs(List<String> argsList) {
		argsList.addAll(Arrays.asList(new String[] { "-p", "wjop", "off", }));
		argsList.addAll(Arrays.asList(new String[] { "-p", "wjap", "off", }));
		argsList.addAll(Arrays.asList(new String[] { "-p", "jtp", "off", }));
		argsList.addAll(Arrays.asList(new String[] { "-p", "jop", "off", }));
		argsList.addAll(Arrays.asList(new String[] { "-p", "jap", "off", }));
		argsList.addAll(Arrays.asList(new String[] { "-p", "bb", "off", }));
		argsList.addAll(Arrays.asList(new String[] { "-p", "tag", "off", }));
		argsList.addAll(Arrays.asList(new String[] { "-f", "n", }));// 关闭文件的输出
	}
}
