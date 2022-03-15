/////////////////////////////////////////////////////////////////////////////
// Bucket.java -- a class providing a hash-bucket data structure (a lightweight
//                linked list)
//
// Copyright (c) 1998 by Jon A. Zeppieri (jon@eease.com)
//
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

class Bucket
{
    Node first;
    
    Bucket()
    {
    }
    
    Node add(Node newNode)
    {
	Object oKey;
	Object oTestKey = newNode.getKey();
	Node it = first;
	Node prev = null;
	if (it == null) // if empty (ideal case), make a new single-node list
	    {
		first = newNode;
		return null;
	    }
	else // otherwise try to find where this key already exists in the list,
	    {// and if it does, replace the value with the new one (and return the old one)
		while (it != null)  
		    {
			oKey = it.getKey();
			if ((oKey == null) ? (oTestKey == null) :
			    oKey.equals(oTestKey))
			    {
				if (prev != null)
				    prev.next = newNode;
				else
				    first = newNode;
				newNode.next = it.next;
				return it; 
			    }
			prev = it;  
			it = it.next;
		    }
		prev.next = newNode; // otherwise, just stick this at the 
                return null;
	    }
    }
    
    Object removeByKey(Object key)
    {
	Object oEntryKey;
	Node prev = null;
	Node it = first;
	while (it != null)
	    {
		oEntryKey = it.getKey();
		if ((oEntryKey == null) ? (key == null) : oEntryKey.equals(key))
		    {
			if (prev == null) // we are removing the first element
			    first = it.next;
			else
			    prev.next = it.next;
			return it.getValue();
		    }
		else
		    {
			prev = it;
			it = it.next;
		    }
	    }
	return null;
    }
    
	Object getValueByKey(Object key)
	{
		Node entry = getEntryByKey(key);
		return (entry == null) ? null : entry.getValue();
	}

    Node getEntryByKey(Object key)
    {
	Object oEntryKey;
	Node it = first;
	while (it != null)
	    {
		oEntryKey = it.getKey();
		if ((oEntryKey == null) ? (key == null) : oEntryKey.equals(key))
		    return it;
		it = it.next;
	    }
	return null;
    }
    
    boolean containsValue(Object value)
    {
	Object oEntryValue;
	Node it = first;
	while (it != null)
	    {
		oEntryValue = it.getValue();
		if ((oEntryValue == null) ? (value == null) : oEntryValue.equals(value))
		    return true;
		it = it.next;
	    }
	return false;
    }

    static class Node 
	{
		Node next;
		Object key;
		Object value;
		
		Node(Object key, Object value)
		{
			this.key = key;
			this.value = value;
		}

		public void setValue(Object newValue) 
		{
			value = newValue;
		}

		public Object getKey() 
		{
			return key;
		}
		public Object getValue() 
		{
			return value;
		}
    }
}
