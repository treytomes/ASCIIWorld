package asciiWorld;

import java.lang.reflect.Method;
import java.util.Iterator;

public class MethodIterator implements Iterable<Method>, Iterator<Method> {
	
	private Class<?> _type;
	private Method[] _methods;
	private int _index;
	
	public static MethodIterator getMethods(Class<?> type) {
		return new MethodIterator(type);
	}
	
	private MethodIterator(Class<?> type) {
		_type = type;
		_methods = _type.getMethods();
		_index = 0;
	}

	@Override
	public boolean hasNext() {
		return _index < _methods.length;
	}

	@Override
	public Method next() {
		Method nextMethod = currentMethod();
		_index++;
		return nextMethod;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	public Method currentMethod() {
		return _methods[_index];
	}

	@Override
	public Iterator<Method> iterator() {
		return this;
	}
	
	public MethodNameIterator withName(String methodName) {
		return new MethodNameIterator(this, methodName);
	}
}
