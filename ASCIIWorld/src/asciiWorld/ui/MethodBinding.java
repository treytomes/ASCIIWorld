package asciiWorld.ui;

public class MethodBinding {
	
	private Object _source;
	private Object _methodNameBinding;
	
	public MethodBinding(Object source, Object methodNameBinding) {
		_source = source;
		_methodNameBinding = methodNameBinding;
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
	
	public Object getValue() {
		try {
			Object source = getSource();
			
			if (MethodBinding.class.isInstance(source)) {
				source = ((MethodBinding)_source).getValue();
			}
			
			return source.getClass().getMethod(getMethodName()).invoke(source);
		} catch (Exception e) {
			return null;
		}
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