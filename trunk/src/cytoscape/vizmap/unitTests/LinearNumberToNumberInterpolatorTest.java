// LinearNumberToNumberInterpolatorTest.java

/** Copyright (c) 2002 Institute for Sytems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** California Institute of Technology and Japan Science and Technology
 ** Corporation have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** California Institute of Technology or the Japan Science and Technology
 ** Corporation be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** California Institute of Technology and/or Japan Science and Technology
 ** Corporation have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.vizmap.unitTests;
//----------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;

import cytoscape.vizmap.LinearNumberToNumberInterpolator;
//----------------------------------------------------------------------------
public class LinearNumberToNumberInterpolatorTest extends TestCase {

//----------------------------------------------------------------------------
    public LinearNumberToNumberInterpolatorTest (String name) {super (name);}
//----------------------------------------------------------------------------
    public void setUp () throws Exception {}
//----------------------------------------------------------------------------
    public void tearDown () throws Exception {}
//----------------------------------------------------------------------------
    public void testFunction () throws Exception {
	LinearNumberToNumberInterpolator li =
	    new LinearNumberToNumberInterpolator();
            
        Integer lowerRange = new Integer(2);
        Integer upperRange = new Integer(3);
        double frac = 0.41;
        Object returnVal = li.getRangeValue(frac, lowerRange, upperRange);
        
        assertTrue(returnVal instanceof Double);
        assertTrue( ((Double)returnVal).doubleValue() - 2.41 < 1.0E-6 );
    }
//---------------------------------------------------------------------------
    public static void main (String [] args) {
	junit.textui.TestRunner.run (new TestSuite (LinearNumberToNumberInterpolatorTest.class));
    }
//----------------------------------------------------------------------------
}

