package neu.lab.conflict;

public class Conf {
	public static final boolean CLASS_DUP = false;
	public static final boolean FLT_INTERFACE = false;
	
	public static final boolean FLT_CALL = false;// should filter method call before form graph
	public static final boolean FLT_OBJ = false;
	public static final boolean FLT_SET = false;
	
	public static final boolean FLT_DANGER_IMPL = true;
	public static final int DANGER_IMPL_T = 4;
	
	public static final boolean FLT_NODE = true;//filter node before find path
	
	public static final int PATH_DEP = 15;
}
