/** Copyright (c) 2004 Institute for Systems Biology and the Whitehead Institute
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
 ** Institute for Systems Biology and the Whitehead Institute
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

package cytoscape.plugin;

/**
 * Encapsulates a change to a PluginRegistry.
 */
public class PluginEvent {
    protected String msg;
    protected long timeInMillis;
    /**
     * Creates a PluginEvent with no message.
     */
    public PluginEvent(long timeInMillis) {
        this.timeInMillis = timeInMillis;
        this.msg = "";
    }
    /**
     * Creates a PluginEvent with a stored message.
     */
    public PluginEvent(long timeInMillis, String msg) {
        this.timeInMillis = timeInMillis;
        this.msg = msg;
    }
    /**
     * Returns time of event.
     */
    public long getTimeInMillis() {
        return timeInMillis;
    }
    /**
     * Returns any stored message.
     */
    public String toString() {
        return msg;
    }
}