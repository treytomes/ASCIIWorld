package asciiWorld.ui;

public class MethodBinding {
	
	private Object _source;
	private Object _methodNameBinding;
	private Object[] _arguments;
	
	public MethodBinding(Object source, Object methodNameBinding, Object ... arguments) {
		_source = source;
		_methodNameBinding = methodNameBinding;
		_arguments = arguments;
	}
	
	public Object getSource() {
		return _source;
	}
	
	public void setSource(Object value) {
		_source = value;
	}
	
	public Object getMethodNameBinding() {
		return _methodNameBinding;
	}
	
	public void setMethodNameBinding(Object value) {
		_methodNameBinding = value;
	}
	
	public String getMethodName() {
		return getMethodNameBinding().toString();
	}
	
	public void setMethodName(String value) {
		_methodNameBinding = value;
	}
	
	public void setArguments(Object ... arguments) {
		_arguments = arguments;
	}
	
	public Object getValue() {
		try {
			Object source = getSource();
			
			if (source instanceof MethodBinding) {
				source = ((MethodBinding)source).getValue();
			}
			
			return source.getClass().getMethod(getMethodName(), getClassesFor(_arguments)).invoke(source, _arguments);
		} catch (Exception e) {
			return null;
		}
	}
	
	private Class<?>[] getClassesFor(Object[] values) {
		if (values == null) {
			return null;
		}
		
		Class<?>[] classes = new Class<?>[values.length];
		for (int index = 0; index < values.length; index++) {
			classes[index] = values[index].getClass();
		}
		return classes;
	}
	
	@Override
	public String toString() {
		try {
			return getValue().toString();
		} catch (Exception e) {
			return "Binding error!";
		}
	}
}