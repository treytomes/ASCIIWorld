package asciiWorld;

import java.lang.reflect.Method;
import java.util.Iterator;

public class MethodNameIterator implements Iterable<Method>, Iterator<Method> {
	
	private String _methodName;
	private MethodIterator _methods;
	
	public MethodNameIterator(MethodIterator methods, String methodName) {
		_methodName = methodName;
		_methods = methods;
	}

	@Override
	public boolean hasNext() {
		return _methods.hasNext();
	}

	@Override
	public Method next() {
		while (!currentMethod().getName().equals(_methodName)) {
			_methods.next();
		}
		return _methods.next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	private Method currentMethod() {
		return _methods.currentMethod();
	}

	@Override
	public Iterator<Method> iterator() {
		return this;
	}
}
