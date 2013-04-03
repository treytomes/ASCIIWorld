package asciiWorld;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class QueryableList<T> implements List<T> {
	
	private List<T> _baseList;
	
	public QueryableList(List<T> baseList) {
		_baseList = baseList;
	}
	
	public T first() {
		return get(0);
	}
	
	public T last() {
		return get(size() - 1);
	}

	@Override
	public boolean add(T e) {
		return _baseList.add(e);
	}

	@Override
	public void add(int index, T element) {
		_baseList.add(index, element);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return _baseList.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		return _baseList.addAll(index, c);
	}

	@Override
	public void clear() {
		_baseList.clear();
	}

	@Override
	public boolean contains(Object o) {
		return _baseList.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return _baseList.containsAll(c);
	}

	@Override
	public T get(int index) {
		return _baseList.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return _baseList.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return _baseList.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		return _baseList.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return _baseList.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator() {
		return _baseList.listIterator();
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return _baseList.listIterator(index);
	}

	@Override
	public boolean remove(Object o) {
		return _baseList.remove(o);
	}

	@Override
	public T remove(int index) {
		return _baseList.remove(index);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return _baseList.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return _baseList.retainAll(c);
	}

	@Override
	public T set(int index, T element) {
		return _baseList.set(index, element);
	}

	@Override
	public int size() {
		return _baseList.size();
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return _baseList.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return _baseList.toArray();
	}

	@SuppressWarnings("hiding")
	@Override
	public <T> T[] toArray(T[] a) {
		return _baseList.toArray(a);
	}
}
