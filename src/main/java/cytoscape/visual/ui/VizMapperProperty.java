package cytoscape.visual.ui;

import com.l2fprod.common.propertysheet.DefaultProperty;


/**
 * Extended version of DefaultProperty which accepts one more value as hidden
 * object.
 *
 * @author kono
 *
 */
public class VizMapperProperty extends DefaultProperty {
    
	private static final long serialVersionUID = -9103147252041414576L;
	
	private Object hiddenObject;

    /**
     * DOCUMENT ME!
     *
     * @param obj DOCUMENT ME!
     */
    public void setHiddenObject(Object obj) {
        this.hiddenObject = obj;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object getHiddenObject() {
        return hiddenObject;
    }

    /**
     * DOCUMENT ME!
     *
     * @param object DOCUMENT ME!
     */
    public void readFromObject(Object object) {
    }

    /**
     * DOCUMENT ME!
     *
     * @param object DOCUMENT ME!
     */
    public void writeToObject(Object object) {
    }
}
