package asciiWorld;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class JavascriptContext {

	private Context _context;
	private Scriptable _scope;

	public JavascriptContext() {		
		initialize();
	}
	
	public static String toString(Object scriptResult) {
		return Context.toString(scriptResult);
	}
	
	public Context getContext() {
		return _context;
	}
	
	public Scriptable getScope() {
		return _scope;
	}
	
	public Object executeScript(String scriptText) throws Exception {
		return  _context.evaluateString(_scope, scriptText, "<cmd>", 1, null);
	}
	
	public void destroy() {
		Context.exit();
	}
	
	public Object getObject(String name) {
		return _scope.get(name, _scope);
	}
	
	public void addObjectToContext(Object obj, String name) {
		Object jsObj = Context.javaToJS(obj, _scope);
		ScriptableObject.putProperty(_scope, name, jsObj);
	}
	
	@Override
	protected void finalize()
			throws Throwable {
		destroy();
		super.finalize();
	}
	
	private void initialize() {
		// Create and enter a Context.  The Context stores information about the execution environment of a script.
		_context = Context.enter();
		
		// Initialize the standard objects (Object, Function, etc.).
		// This must be done before scripts can be executed.
		// Returns a scope object that we use in later calls.
		_scope = _context.initStandardObjects();
	}
}
