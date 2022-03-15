/////////////////////////////////////////////////////////////////////////////
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU Library General Public License as published
// by the Free Software Foundation, version 2. (see COPYING.LIB)
//
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Library General Public License for more details.
//
// You should have received a copy of the GNU Library General Public License
// along with this program; if not, write to the Free Software Foundation
// Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307 USA
/////////////////////////////////////////////////////////////////////////////
package java.util;

/**
 * a class which implements a Hashtable data structure
 *
 * This implementation of Hashtable uses a hash-bucket approach. That is:
 * linear probing and rehashing is avoided; instead, each hashed value maps
 * to a simple linked-list which, in the best case, only has one node.
 * Assuming a large enough table, low enough load factor, and / or well
 * implemented hashCode() methods, Hashtable should provide O(1) 
 * insertion, deletion, and searching of keys.  Hashtable is O(n) in
 * the worst case for all of these (if all keys has to the same bucket).
 *
 * This is a JDK-1.2 compliant implementation of Hashtable.  As such, it 
 * belongs, partially, to the Collections framework (in that it implements
 * Map).  For backwards compatibility, it inherits from the obsolete and 
 * utterly useless Dictionary class.
 *
 * Being a hybrid of old and new, Hashtable has methods which provide redundant
 * capability, but with subtle and even crucial differences.
 * For example, one can iterate over various aspects of a Hashtable with
 * either an Iterator (which is the JDK-1.2 way of doing things) or with an
 * Enumeration.  The latter can end up in an undefined state if the Hashtable
 * changes while the Enumeration is open.
 *
 * @author      Jon Zeppieri
 */
public class Hashtable implements java.io.Serializable, Map
{
    // STATIC VARIABLES
    // ----------------
    private static final int DEFAULT_CAPACITY = 20; // tweak with this value

    /** the defaulty load factor; this is explicitly specified by Sun */
    private static final int DEFAULT_LOAD_FACTOR = 75;//(if it is div by 100 make it 75)

    private static final int KEYS = 0;
    private static final int VALUES = 1;
    private static final int ENTRIES = 2; 

    private int capacity;
    private int size;
    private int loadFactor;
    private int threshold;
    Bucket[] buckets;
    int modCount; 

    public Hashtable()
    {	init(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public Hashtable(int initialCapacity) throws IllegalArgumentException 
    {
	if (initialCapacity < 0)
	    throw new IllegalArgumentException();
	else
	    init(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public synchronized int size()
    {	return size;
    }

    public synchronized boolean isEmpty()
    {	return size == 0;
    }

    public synchronized Enumeration keys()
    {	return new HashtableEnumeration(KEYS);
    }

    public synchronized Enumeration elements()
    {	return new HashtableEnumeration(VALUES);
    }

    public boolean contains(Object value) throws NullPointerException
    {
	if (value == null)
	    throw new NullPointerException();
	else
	    return containsValue(value);
    }

    public synchronized boolean containsValue(Object value)
    {	int i;
    Bucket list;
    for (i = 0; i < capacity; i++)
	{	list = buckets[i];
	if (list != null && list.containsValue(value))
	    return true;
	}
    return false;
    }

    public synchronized boolean containsKey(Object key)
    {	return (internalGet(key) != null);
    }

    public synchronized Object get(Object key)
    {	return internalGet(key);
    }

    private Object internalGet(Object key)
    {	
	Bucket list;
	if (key == null || size == 0)
	    {
		return null;
	    }
	else
	    {
		list = buckets[hash(key)];
		return (list == null) ? null : list.getValueByKey(key);
	    }
    }

    /**
     * a private method used by inner class HashtableSet to implement its own 
     * <pre>contains(Map.Entry)</pre> method; returns true if the supplied
     * key / value pair is found in this Hashtable (again, using <pre>equals()</pre>,
     * rather than <pre>==</pre>)
     *
     * @param      entry      a Map.Entry to match against key / value pairs in 
     *                        this Hashtable
     */
    private synchronized boolean containsEntry(Map.Entry entry)
    {
	Object o;
	if (entry == null)
	    {
		return false;
	    }
	else
	    {
		o = internalGet(entry.getKey());
		return (o != null && o.equals(entry.getValue()));
	    }
    }

    protected void rehash()
    {
	int i;
	Bucket[] data = buckets;
	Bucket.Node node;
	modCount++;
	capacity = (capacity * 2) + 1;
	size = 0;
	// divide by 100(approx).. fix divide
	threshold = (capacity * loadFactor)>>7; 
	buckets = new Bucket[capacity];
	for (i = 0; i < data.length; i++)
	    {
		if (data[i] != null)
		    {	node = data[i].first;
		    while (node != null)
			{
			    internalPut(node.getKey(), node.getValue());
			    node = node.next;
			}
		    }
	    }
    }

    public synchronized Object put(Object key, Object value) 
	throws NullPointerException
    {	
	if (key == null || value == null)
	    throw new NullPointerException();
	else
	    return internalPut(key, value);
    }

    private Object internalPut(Object key, Object value)
    {
	HashtableEntry entry;
	Bucket list;
	int hashIndex;
	Object oResult;
	modCount++;
	if (size == threshold)
	    rehash();
	entry = new HashtableEntry(key, value);
	hashIndex = hash(key);
	list = buckets[hashIndex];
	if (list == null)
	    {
		list = new Bucket();
		buckets[hashIndex] = list;
	    }
	oResult = list.add(entry);
	if (oResult == null)
	    {
		size++;
		return null;
	    }
	else
	    {
		return ((Bucket.Node)oResult).getValue();
	    }
    }

    public synchronized Object remove(Object key)
    {
	Bucket list;
	int index;
	Object result = null;
	if (key != null && size > 0)
	    {
		index = hash(key);
		list = buckets[index];
		if (list != null)
		    {
			result = list.removeByKey(key);
			if (result != null)
			    {
				size--;
				modCount++;
				if (list.first == null)
				    buckets[index] = null;
			    }
		    }
	    }
	return result;
    }

    public synchronized void clear()
    {
	size = 0;
	modCount++;
	buckets = new Bucket[capacity];
    }

    public synchronized Object clone()
    {	// clone not supported
        return null;
    }

    /**
     * returns a String representation of this Hashtable
     *
     * the String representation of a Hashtable is defined by Sun and looks like this:
     * <pre>
     * {name_1=value_1, name_2=value_2, name_3=value_3, ..., name_N=value_N}
     * </pre>
     * for N elements in this Hashtable
     */
    public synchronized String toString()
    {
	Map.Entry entry;
	Iterator it = entrySet().iterator();
	StringBuffer sb = new StringBuffer("{");
	boolean isFirst = true;
	while (it.hasNext())
	    {
		entry = (Map.Entry) it.next();
		if (isFirst)
		    isFirst = false;
		else
		    sb.append(", ");
		sb.append(entry.getKey().toString()).append("=").append(entry.getValue().toString());
	    }
	sb.append("}");
	return sb.toString();
    }

    /** returns a Set of Keys in this Hashtable */
    public synchronized Set keySet()
    {
	return new HashtableSet(KEYS);
    }

    /**
     * part of the Map interface; for each Map.Entry in t, the key / value pair is
     * added to this Hashtable, <b>using the <pre>put()</pre> method -- this may not be
     * you want, so be warned (see notes to <pre>internalPut()</pre>, above</b>
     *
     * @param    t       a Map whose key / value pairs will be added to this Hashtable
     */
    public synchronized void putAll(Map t) throws NullPointerException
    {
	Map.Entry entry;
	Iterator it = t.entrySet().iterator();
	while (it.hasNext())
	    {
		entry = (Map.Entry) it.next();
		put(entry.getKey(), entry.getValue());
	    }
    }

    /** 
     * returns a Set of Map.Entry objects in this Hashtable;
     * note, this was called <pre>entries()</pre> prior to JDK-1.2b4 */
    public synchronized Set entrySet()
    {
	return new HashtableSet(ENTRIES);
    }

    public synchronized boolean equals(Object o)
    {	return false;
    }

    /** a Map's hashCode is the sum of the hashCodes of all of its Map.Entry objects */
    public synchronized int hashCode()
    {
	Iterator it = entrySet().iterator();
	int result = 0;
	while (it.hasNext())
	    result += it.next().hashCode();
	return result;
    }

    private int hash(Object key)
    {	return Math.abs(key.hashCode() % capacity);
    }

    private void init(int initialCapacity, int initialLoadFactor)
    {
        size = 0;
        modCount = 0;
        capacity = initialCapacity;
        loadFactor = initialLoadFactor;
        threshold =  (capacity * loadFactor) >> 7;// FIX DIVIDE (approx for div by 100)
        buckets = new Bucket[capacity];
    }

    private static class HashtableEntry extends Bucket.Node 
    {
	public HashtableEntry(Object key, Object value)
	{	super(key, value);
	}
    }

    private class HashtableEnumeration implements Enumeration
    {
	private int myType;
	private int position;
	private int bucketIndex;
	private Bucket.Node currentNode;

	HashtableEnumeration(int type)
	{
	    myType = type;
	    position = 0;
	    bucketIndex = -1;
	    currentNode = null;
	}

	public boolean hasMoreElements()
	{
	    return position < Hashtable.this.size();
	}

	public Object nextElement()
	{
	    Bucket list = null;
	    Object result = null;
	    //try
	    {
		while (currentNode == null)
		    {
			while (list == null)
			    list = Hashtable.this.buckets[++bucketIndex];
			currentNode = list.first;
		    }
		result = (myType == KEYS) ? currentNode.getKey() : 
		    currentNode.getValue();
		currentNode = currentNode.next;
	    }
	    /*
	      catch(Exception e)
	      {	throw new NoSuchElementException();
	      }
	    */
	    position++;
	    return result;
	}
    }

    /** returns a Collection of values in this Hashtable */
    public synchronized Collection values()
    {
	return new HashtableCollection();
    }
    
    /**
     * an inner class providing a Set view of a Hashtable; this implementation is 
     * parameterized to view either a Set of keys or a Set of Map.Entry objects
     *
     * Note:  a lot of these methods are implemented by AbstractSet, and would work 
     * just fine without any meddling, but far greater efficiency can be gained by
     * overriding a number of them.  And so I did.
     *
     * @author      Jon Zeppieri
     * @version     $Revision: 1.1 $
     * @modified    $Id: Hashtable.java,v 1.1 1998/10/13 00:38:38 jaz Exp $
     */
    private class HashtableSet extends AbstractSet
    {
	/** the type of this Set view:  KEYS or ENTRIES */
	private int setType;

	/** construct a new HashtableSet with the supplied view type */
	HashtableSet(int type)
	{
	    setType = type;
	}

	/**
	 * adding an element is unsupported; this method simply throws an exception 
	 *
	 * @throws       UnsupportedOperationException
	 */
	public boolean add(Object o) throws UnsupportedOperationException
	{
	    throw new UnsupportedOperationException();
	}

	/**
	 * adding an element is unsupported; this method simply throws an exception 
	 *
	 * @throws       UnsupportedOperationException
	 */
	public boolean addAll(Collection c) throws UnsupportedOperationException
	{
	    throw new UnsupportedOperationException();
	}

	/**
	 * clears the backing Hashtable; this is a prime example of an overridden implementation
	 * which is far more efficient than its superclass implementation (which uses an iterator
	 * and is O(n) -- this is an O(1) call)
	 */
	public void clear()
	{
	    Hashtable.this.clear();
	}

	/**
	 * returns true if the supplied object is contained by this Set
	 *
	 * @param     o       an Object being testing to see if it is in this Set
	 */
	public boolean contains(Object o)
	{
	    if (setType == KEYS)
		return Hashtable.this.containsKey(o);
	    else
		return (o instanceof Map.Entry) ? Hashtable.this.containsEntry((Map.Entry) o) : false;
	}

	/** 
	 * returns true if the backing Hashtable is empty (which is the only case either a KEYS
	 * Set or an ENTRIES Set would be empty)
	 */
	public boolean isEmpty()
	{
	    return Hashtable.this.isEmpty();
	}

	/**
	 * removes the supplied Object from the Set
	 *
	 * @param      o       the Object to be removed
	 */
	public boolean remove(Object o)
	{
	    if (setType == KEYS)
		return (Hashtable.this.remove(o) != null);
	    else
		return (o instanceof Map.Entry) ? 
		    (Hashtable.this.remove(((Map.Entry) o).getKey()) != null) : false;
	}

	/** returns the size of this Set (always equal to the size of the backing Hashtable) */
	public int size()
	{
	    return Hashtable.this.size();
	}

	/** returns an Iterator over the elements of this Set */
	public Iterator iterator()
	{
	    return new HashtableIterator(setType);
	}
    }

    /**
     * Like the above Set view, except this one if for values, which are not
     * guaranteed to be unique in a Hashtable; this prvides a Bag of values
     * in the Hashtable
     *
     * @author       Jon Zeppieri
     * @version      $Revision: 1.1 $
     * @modified     $Id: Hashtable.java,v 1.1 1998/10/13 00:38:38 jaz Exp $
     */
    private class HashtableCollection extends AbstractCollection
    {
	/** a trivial contructor for HashtableCollection */
	HashtableCollection()
	{
	}

	/** 
	 * adding elements is not supported by this Collection;
	 * this method merely throws an exception
	 *
	 * @throws     UnsupportedOperationException
	 */
	public boolean add(Object o) throws UnsupportedOperationException
	{
	    throw new UnsupportedOperationException();
	}

	/** 
	 * adding elements is not supported by this Collection;
	 * this method merely throws an exception
	 *
	 * @throws     UnsupportedOperationException
	 */
	public boolean addAll(Collection c) throws UnsupportedOperationException
	{
	    throw new UnsupportedOperationException();
	}

	/** removes all elements from this Set (and from the backing Hashtable) */
	public void clear()
	{
	    Hashtable.this.clear();
	}

	/** 
	 * returns true if this Collection contains at least one Object which equals() the
	 * supplied Object
	 *
	 * @param         o        the Object to compare against those in the Set
	 */
	public boolean contains(Object o)
	{
	    return Hashtable.this.containsValue(o);
	}

	/** returns true IFF the Collection has no elements */
	public boolean isEmpty()
	{
	    return Hashtable.this.isEmpty();
	}

	/** returns the size of this Collection */
	public int size()
	{
	    return Hashtable.this.size();
	}

	/** returns an Iterator over the elements in this Collection */
	public Iterator iterator()
	{
	    return new HashtableIterator(VALUES);
	}
    }

    /**
     * Hashtable's version of the JDK-1.2 counterpart to the Enumeration;
     * this implementation is parameterized to give a sequential view of
     * keys, values, or entries; it also allows the removal of elements, 
     * as per the Javasoft spec.
     *
     * @author       Jon Zeppieri
     * @version      $Revision: 1.1 $
     * @modified     $Id: Hashtable.java,v 1.1 1998/10/13 00:38:38 jaz Exp $
     */
    class HashtableIterator implements Iterator
    {
	/** the type of this Iterator: KEYS, VALUES, or ENTRIES */
	private int myType;
	/** 
	 * the number of modifications to the backing Hashtable for which
	 * this Iterator can account (idea ripped off from Stuart Ballard)
	 */
	private int knownMods;
	/** the location of our sequential "cursor" */
	private int position;
	/** the current index of the BucketList array */
	private int bucketIndex;
	/** a reference, originally null, to the specific Bucket our "cursor" is pointing to */
	private Bucket.Node currentNode;
	/** a reference to the current key -- used fro removing elements via the Iterator */
	private Object currentKey;

	/** construct a new HashtableIterator with the supllied type: KEYS, VALUES, or ENTRIES */
	HashtableIterator(int type)
	{
	    myType = type;
	    knownMods = Hashtable.this.modCount;
	    position = 0;
	    bucketIndex = -1;
	    currentNode = null;
	    currentKey = null;
	}

	/** 
	 * Stuart Ballard's code:  if the backing Hashtable has been altered through anything 
	 * but <i>this</i> Iterator's <pre>remove()</pre> method, we will give up right here,
	 * rather than risking undefined behavior
	 *
	 * @throws    ConcurrentModificationException
	 */
	private void checkMod() 
	{
	    if (knownMods < Hashtable.this.modCount)
		throw new ConcurrentModificationException();
	}

	/** returns true if the Iterator has more elements */
	public boolean hasNext()
	{
	    checkMod();
	    return position < Hashtable.this.size();
	}

	/** returns the next element in the Iterator's sequential view */
	public Object next()
	{
	    Bucket list = null;
	    Object result;
	    checkMod();	    
	    try
		{
		    while (currentNode == null)
			{
			    while (list == null)
				list = Hashtable.this.buckets[++bucketIndex];
			    currentNode = list.first;
			}
		    currentKey = currentNode.getKey();
		    result = (myType == KEYS) ? currentKey : 
			((myType == VALUES) ? currentNode.getValue() : currentNode);
		    currentNode = currentNode.next;
		}
	    catch(Exception e)
		{
		    throw new NoSuchElementException();
		}
	    position++;
	    return result;
	}

	/** 
	 * removes from the backing Hashtable the last element which was fetched with the
	 * <pre>next()</pre> method
	 */
	public void remove()
	{
	    checkMod();
	    if (currentKey == null)
		{
		    throw new IllegalStateException();
		}
	    else
		{
		    Hashtable.this.remove(currentKey);
		    knownMods++;
		    currentKey = null;
		}
	}
    }
}
