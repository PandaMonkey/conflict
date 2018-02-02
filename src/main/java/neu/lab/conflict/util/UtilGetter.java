package neu.lab.conflict.util;

import org.apache.maven.plugin.AbstractMojo;

import neu.lab.conflict.DebugMojo;
import neu.lab.conflict.DetectMojo;

public class UtilGetter {
	public static MavenUtil i() {
		if (null != DetectUtil.i().getMojoObj()) {
			return DetectUtil.i();
		}else {
			return DebugUtil.i();
		}
	}
	public static void setMojo(AbstractMojo mojo) {
		if(mojo instanceof DetectMojo)
			DetectUtil.i().setMojo((DetectMojo)mojo);
		if(mojo instanceof DebugMojo)
			DebugUtil.i().setValidObj((DebugMojo)mojo);
	}
}
