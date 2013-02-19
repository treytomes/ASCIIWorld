package asciiWorld;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class JavascriptContext {

	private Context _context;
	private Scriptable _scope;

	public JavascriptContext() {		
		initialize();
	}
	
	public static String toString(Object scriptResult) {
		return Context.toString(scriptResult);
	}
	
	public Object executeScript(String scriptText) throws Exception {
		return  _context.evaluateString(_scope, scriptText, "<cmd>", 1, null);
	}
	
	public void destroy() {
		Context.exit();
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
