 /*
  File: CyCommand.java

  Copyright (c) 2009, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies
  - University of California San Francisco

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

package cytoscape.command;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import cytoscape.layout.Tunable;

/**
 * The CyCommand interface allows a Cytoscape plugin to make it's functions
 * available to other plugins in a loosely-coupled manner (by passing command
 * strings and arguments).  
 * <p>
 * CyCommand provide a simple method for a plugin to expose some functionality
 * that can be used by other plugins.  The idea is simple: a plugin registers
 * one or more CyCommands with the {@link CyCommandManager}.  In the simplest
 * case, the plugin will directly implement CyCommand:
 * <pre><code>
 * public class MyPlugin extend CytoscapePlugin implements CyCommand {
 *   public MyPlugin() {
 *      // Plugin initialization
 *      try {
 *        CyCommandManager.register(this);
 *      } catch (Exception e) {
 *        // Handle already registered exceptions
 *      }
 *   }
 * }
 * </code></pre>
 * Alternatively, a plugin could implement multiple CyCommands, but it's useful
 * whenever possible to avoid having a single plugin implement more than one or
 * two commands.  
 * <h3>Sub commands</h3>
 * Each command can provide any number of sub commands.
 * A sub command provides the actual functionality, and the passed arguments 
 * (name, value pairs) are specific to sub commands.  Note that there aren't any
 * restrictions about the number of words in a command or subcommand.  So, for
 * example, the <i>view layout</i> could can have a <i>get current</i> sub command.
 * <h3>Arguments</h3>
 * Arguments for sub commands may be specified either as name, value pairs
 * (using a string, string map) or as a list of {@link Tunables}.  It is recommended
 * that whenever possible, plugins use Tunables as they provide type information
 * that is useful to allow client plugins to perform some limited validation.  In
 * either case, both signatures (maps and Tunables) should be supported.
 * <h3>Return Values</h3>
 * CyCommands pass information about their execution in a {@link CyCommandReturn}.  It is
 * expected that CyCommands will provide some limited information in the CyCommandReturn
 * message and the actual information in the results map.
 * <h3>Exceptions</h3>
 * A CyCommand should always return a valid CyCommandReturn to provide feedback to the user
 * about the execution.  If a processing error occurs, the CyCommand should through a
 * CyCommandException with an appropriate message.
 * <h3>Client Usage</h3>
 * Using a CyCommand from another plugin is relatively straightforward: get the CyCommand,
 * get the arguments for the desired subCommand, set the argumnets, and execute the
 * the subCommand:
 * <pre><code>
 *     CyCommand command = CyCommandManager.getCommand("view layout");
 *     Map<String,Tunable> layoutTunables = command.getTunables("force-directed");
 *     Tunable t = layoutTunables.get("iterations");
 *     t.setValue("100");
 *     try {
 *       CyCommandResult layoutResult = command.execute("force-directed", layoutTunables);
 *     } catch (CyCommandException e) {
 *     }
 * </code></pre>
 */
public interface CyCommand {
	/**
 	 * This get the name for this command.  This is the index that will be used by
	 * other plugins to request access to this functionality through {@link CyCommandManager},
	 * so it's important to choose this carefully.  For example calling a command
	 * <b>analyze</b> wouldn't be a very good idea since this might collide with other
	 * similarly named commands.  
 	 *
 	 * @return command name
 	 */
	public String getCommandName();

	/**
 	 * Return the list of sub-commands supported by this CyCommand.  This
 	 * implies that a single CyCommand might support multiple commands, which is
 	 * the intent.  
 	 *
 	 * @return list of commands supported by this CyCommand
 	 */
	public List<String> getSubCommands();

	/**
 	 * Return the list of arguments supported by a particular subCommand.  CyCommand
 	 * argument values are always Strings that can be converted to base types
 	 * when passed.  Internally, these are usually directly mapped to Tunables.
 	 *
 	 * @param subCommand the subCommand we're inquiring about
 	 * @return list of arguments supported by that subCommand 
 	 */
	public List<String> getArguments(String subCommand);

	/**
 	 * Get the current values for all of the arguments for a specific subCommand.
 	 * Argument values are always Strings that can be converted to base types
 	 * when passed.  Note that this interface is expected to be deprecated in
 	 * Cytoscape 3.0 where everything is expected to be based on the new Tunable
 	 * implementation.
 	 *
 	 * @param subCommand the subCommand we're inquiring about
 	 * @return map of arguments supported by that command, where the key is the
 	 * argument and the value is the String value for that argument. 
 	 */
	public Map<String, String> getSettings(String subCommand);

	/**
 	 * Get the current Tunables for all of the arguments for a specific subCommand.
 	 * This is an alternative to getSettings for plugins that provide tunables,
 	 * and is expected to be the standard approach for Cytoscape 3.0 and beyond.
 	 *
 	 * @param subCommand the subCommand we're inquiring about
 	 * @return map of Tunables indexed by Tunable name supported by that subCommand, 
	 * or null if the plugin doesn't support tunables.
 	 */
	public Map<String,Tunable> getTunables(String subCommand);

	/**
 	 * Execute a given subCommand with a particular set of arguments.  As with the
 	 * getSettings method above, this is expected to be retired in 3.0 when most
 	 * things will use Tunables.
 	 *
 	 * @param subCommand the subCommand we're executing
 	 * @param arguments to map of key, value pairs for the arguments we want to pass
 	 * @return the results as a map of key, value pairs.  If null, then the execution failed.
	 * @throws CyCommandException
 	 */
	public CyCommandResult execute(String subCommand, Map<String, String>arguments) throws CyCommandException;

	/**
 	 * Execute a given subCommand with a particular set of arguments.  This is the preferred
 	 * method signature.
 	 *
 	 * @param subCommand the subCommand we're executing
 	 * @param arguments the list of Tunables
 	 * @return the results as a map of key, value pairs.  If null, then the execution failed.
	 * @throws CyCommandException
 	 */
	public CyCommandResult execute(String subCommand, Collection<Tunable>arguments) throws CyCommandException;
}
