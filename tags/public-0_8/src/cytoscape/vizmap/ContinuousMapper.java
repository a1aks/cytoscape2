//ContinuousMapper.java
//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.vizmap;
//----------------------------------------------------------------------------
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Set;
import java.util.Iterator;
//----------------------------------------------------------------------------
/**
 * This class implements a mapping from a continuous domain value to
 * a range value via interpolation. A SortedMap should be provided
 * where the keys are the domain values (must be instances of Comparable)
 * and the values are BoundaryRangeValues objects. Additionally, an
 * appropriate Interpolator object must be provided which knows how
 * to operate on the specific type of range value.
 */
public class ContinuousMapper implements ValueMapper {

    private SortedMap boundaryValueMap;
    private Interpolator fInt;

    public ContinuousMapper() {
	this.setBoundaryRangeValuesMap( new TreeMap() );
	this.setInterpolator(null);
    }

    public ContinuousMapper(SortedMap boundaryValueMap,
			    Interpolator fInt) {
	this.setBoundaryRangeValuesMap(boundaryValueMap);
	this.setInterpolator(fInt);
    }

    public Map getValueMap() {return this.getBoundaryRangeValuesMap();}
    public SortedMap getBoundaryRangeValuesMap() {return boundaryValueMap;}
    public void setBoundaryRangeValuesMap(SortedMap boundaryValueMap) {
	/* we should check that the SortedMap argument contains
	 * Comparables as keys and BoundaryRangeValues objects as values */
	this.boundaryValueMap = boundaryValueMap;
    }

    public Interpolator getInterpolator() {return fInt;}
    public void setInterpolator(Interpolator fInt) {
	/* null argument could be a problem */
	this.fInt = fInt;
    }

    //--------------------------------------------------------------------

    /**
     * Key function. Given the domain value, search through the Map of
     * boundary values to find the bracketing boundary values. If the
     * provided domain value is exactly equal to one of the boundary
     * values, then we can immediately return the matching range value.
     * If the supplied values is smaller or larger than any of the
     * boundary values, then the matching value is returned without
     * interpolation. Otherwise, the interpolator function is called
     * with the boundary (domain,range) pairs and the target domain value.
     */
    public Object getRangeValue(Object domainValue) {
	if (domainValue == null || boundaryValueMap == null) {return null;}
	int numPoints = boundaryValueMap.size();
	if (numPoints == 0) {
	    return null;
	}

	if ( !(domainValue instanceof Comparable) ) {return null;}
	Comparable inValue = (Comparable)domainValue;

	Comparable minDomain = (Comparable)boundaryValueMap.firstKey();
	/* if given domain value is smaller than any in our Vector,
	   return the range value for the smallest domain value we have */
	if ( inValue.compareTo(minDomain) <= 0 ) {
	    BoundaryRangeValues bv =
		(BoundaryRangeValues)boundaryValueMap.get(minDomain);
	    if (inValue.compareTo(minDomain) < 0) {
		return bv.lesserValue;
	    } else {
		return bv.equalValue;
	    }
	}

	/* if given domain value is larger than any in our Vector,
	   return the range value for the largest domain value we have */
	Comparable maxDomain = (Comparable)boundaryValueMap.lastKey();
	if (inValue.compareTo(maxDomain) > 0) {
	    BoundaryRangeValues bv =
		(BoundaryRangeValues)boundaryValueMap.get(maxDomain);
	    return bv.greaterValue;
	}

	/* OK, it's somewhere in the middle, so find the boundaries and
	 * pass to our interpolator function. First check for a null
	 * interpolator function */
	if (this.fInt == null) {return null;}

	/* Note that the following set should be sorted since it comes from
	 * a SortedMap. Also, the case of the inValue equalling the smallest
	 * key was checked above */
	Set domainValues = boundaryValueMap.keySet();
	Iterator i = domainValues.iterator();
	Comparable lowerDomain = (Comparable)i.next();
	Comparable upperDomain = null;
	for ( ; i.hasNext(); ) {
	    upperDomain = (Comparable)i.next();
	    int cmpValue = inValue.compareTo(upperDomain);
	    if (cmpValue == 0) {
		BoundaryRangeValues bv =
		    (BoundaryRangeValues)boundaryValueMap.get(upperDomain);
		return bv.equalValue;
	    } else if (cmpValue < 0) {
		break;
	    } else {
		lowerDomain = upperDomain;
	    }
	}

	/* this is tricky. The desired domain value is greater than
	 * lowerDomain and less than upperDomain. Therefore, we want
	 * the "greater" field of the lower boundary value (because the
	 * desired domain value is greater) and the "lesser" field of
	 * the lower boundary value (semantic difficulties).
	 */
	BoundaryRangeValues lv =
	    (BoundaryRangeValues)boundaryValueMap.get(lowerDomain);
	Object lowerRange = lv.greaterValue;
	BoundaryRangeValues gv =
	    (BoundaryRangeValues)boundaryValueMap.get(upperDomain);
	Object upperRange = gv.lesserValue;

	return this.fInt.getRangeValue(lowerDomain, lowerRange,
				       upperDomain, upperRange,
				       domainValue);
    }
}