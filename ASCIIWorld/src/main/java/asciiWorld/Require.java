package asciiWorld;

import java.util.List;

public class Require {
	
	@SuppressWarnings("rawtypes")
	public static ThatProvider<ThatProvider> that(Object value, String name) {
		return new ThatProvider<ThatProvider>(value, name);
	}
	
	@SuppressWarnings("rawtypes")
	public static ThatListProvider<ThatListProvider> that(List<?> value, String name) {
		return new ThatListProvider<ThatListProvider>(value, name);
	}
	
	private static class BasicThatProvider {
		
		private Object _value;
		private String _name;
		
		protected BasicThatProvider(Object value, String name) {
			_value = value;
			_name = name;
		}
	
		public Object getValue() {
			return _value;
		}
		
		public String getName() {
			return _name;
		}
	}
	
	public static class ThatProvider<T extends BasicThatProvider> extends BasicThatProvider {
		
		protected ThatProvider(Object value, String name) {
			super(value, name);
		}
		
		@SuppressWarnings("unchecked")
		public T isNull() throws Exception {
			if (getValue() != null) {
				throw new Exception(String.format("'%s' is not null.", getName()));
			}
			return (T)this;
		}
		
		@SuppressWarnings("unchecked")
		public T isNotNull() throws Exception {
			if (getValue() == null) {
				throw new Exception(String.format("'%s' is null", getName()));
			}
			return (T)this;
		}
	}

	public static class ThatListProvider<T extends BasicThatProvider> extends ThatProvider<T> {
		
		private ThatListProvider(List<?> value, String name) {
			super(value, name);
		}
		
		@SuppressWarnings("unchecked")
		public T contains(Object value) throws Exception {
			if (!((List<?>)getValue()).contains(value)) {
				throw new Exception(String.format("'%s' does not contain that value.", getName()));
			}
			return (T)this;
		}
		
		@SuppressWarnings("unchecked")
		public T doesNotContain(Object value) throws Exception {
			if (((List<?>)getValue()).contains(value)) {
				throw new Exception(String.format("'%s' already contains that value.", getName()));
			}
			return (T)this;
		}
		
		@SuppressWarnings("unchecked")
		public T isEmpty() throws Exception {
			if (!((List<?>)getValue()).isEmpty()) {
				throw new Exception(String.format("'%' is not empty", getName()));
			}
			return (T)this;
		}
		
		@SuppressWarnings("unchecked")
		public T isNotEmpty() throws Exception {
			if (((List<?>)getValue()).isEmpty()) {
				throw new Exception(String.format("'%' is empty", getName()));
			}
			return (T)this;
		}
	}
}