//     
// $Id$
//------------------------------------------------------------------------------

// our package
package cytoscape.view.cytopanels;

// imports
import java.awt.*;
import javax.swing.Icon;

/**
 * Interface to a CytoPanel.
 *
 * @author Ben Gross.
 */
public interface CytoPanel {

    /**
     * Adds a component to the CytoPanel.
     *
     * @param component  Component reference.
	 * @return component Component reference.
     */
    public Component add(Component component);

    /**
     * Adds a component to the CytoPanel at specified index.
     *
     * @param component Component reference.
     * @param index     Component index.
	 * @return component Component reference.
     */
    public Component add(Component component, int index);

    /**
     * Adds a component to the CytoPanel with a specified title.
     *
     * @param title     Component title.
     * @param component Component reference.
	 * @return component Component reference.
     */
    public Component add(String title, Component component);

    /**
     * Adds a component to the CytoPanel with specified title and icon.
     *
     * @param title     Component title (can be null).
	 * @param icon      Component icon (can be null).
     * @param component Component reference.
     */
    public void add(String title, Icon icon, Component component);

    /**
     * Adds a component to the CytoPanel with specified title, icon, and tool tip.
     *
     * @param title     Component title (can be null).
	 * @param icon      Component icon (can be null).
     * @param component Component reference.
     * @param tip       Component Tool tip text.
     */
    public void add(String title, Icon icon, Component component, String tip);

	/**
	 * Returns the title of the CytoPanel.
	 *
	 * @return String Title.
	 */
	public String getTitle();

	/**
	 * Returns the number of components in the CytoPanel.
	 *
	 * @return int Number of components.
	 */
	public int getCytoPanelComponentCount();

	/**
	 * Returns the currently selected component.
	 *
	 * @return component Currently selected Component reference.
	 */
	public Component getSelectedComponent();

	/**
	 * Returns the currently selected index.
	 *
	 * @return index Currently selected index.
	 */
	public int getSelectedIndex();

	/**
	 * Returns the index for the specified component.
	 *
     * @param component Component reference.
	 * @return int      Index of the Component or -1 if not found.
	 */
	public int indexOfComponent(Component component);

	/**
	 * Returns the first Component index with given title.
	 *
     * @param title Component title.
	 * @return int  Component index with given title or -1 if not found.
	 */
	public int indexOfComponent(String title);

	/**
	 * Removes specified component from the CytoPanel.
	 *
	 * @param component Component reference.
	 */
	public void remove(Component component);

	/**
	 * Removes the component from the CytoPanel at the specified index.
	 *
     * @param index Component index.
	 */
	public void remove(int index);

	/**
	 * Removes all the components from the CytoPanel.
	 */
	public void removeAll();

    /**
     * Sets the state of the CytoPanel.
     *
     * @param cytoPanelState A CytoPanelState.
     */
    public void setState(CytoPanelState cytoPanelState);

    /**
     * Gets the state of the CytoPanel.
     *
	 * @return A CytoPanelState.
     */
    public CytoPanelState getState();

	/**
	 * Adds a CytoPanel listener.
	 *
	 * @param cytoPanelListener Reference to a CytoPanelListener.
	 */
	public void addCytoPanelListener(CytoPanelListener cytoPanelListener);

	/**
	 * Removes a CytoPanel listener.
	 *
	 * @param cytoPanelListener Reference to a CytoPanelListener.
	 */
	public void removeCytoPanelListener(CytoPanelListener cytoPanelListener);

}
