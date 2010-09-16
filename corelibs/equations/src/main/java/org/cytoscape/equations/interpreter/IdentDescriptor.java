/*
  File: IdentDescriptor.java

  Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package org.cytoscape.equations.interpreter;


import java.util.List;


public class IdentDescriptor {
	private final Class type;
	private final Object value;

	public IdentDescriptor(final Object o) {
		if (o == null)
			throw new IllegalArgumentException("argument must not be null!");

		if (o.getClass() == Integer.class) { // Need to map Integer to Long!
			final Integer i = (Integer)o;
			this.type = Long.class;
			this.value = new Long(i);
			return;
		}
		else if (o instanceof List)
			this.type = List.class;
		else if (o instanceof Long)
			this.type = Long.class;
		else if (o.getClass() != Double.class && o.getClass() != Boolean.class && o.getClass() != String.class)
			throw new IllegalArgumentException("argument is of an unsupported type (" + o.getClass() + ")!");
		else
			this.type = o.getClass();

		this.value = o;
	}

	Class getType() { return type; }
	Object getValue() { return value; }
}
