/* vim: set ts=2: */
/**
 * Copyright (c) 2009 The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions, and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions, and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. Redistributions must acknowledge that this software was
 *      originally developed by the UCSF Computer Graphics Laboratory
 *      under support by the NIH National Center for Research Resources,
 *      grant P41-RR01081.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package coreCommands.namespaces;

import cytoscape.Cytoscape;
import cytoscape.command.AbstractCommandHandler;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandHandler;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandNamespace;
import cytoscape.command.CyCommandResult;
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;

import java.io.File;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This Command provides a set of subcommands related to the management
 * and setting of VizMaps
 */
public class VizMapNamespace extends AbstractCommandHandler {
	String styleName = "default";
	CyLogger logger = CyLogger.getLogger(VizMapNamespace.class);

	static String VIZMAP = "vizmap";
	static String APPLY = "apply";
	static String IMPORT = "import";

	static String STYLE = "style";
	static String FILE = "file";
	static String NETWORK = "network";
	static String CURRENT = "current";

	public VizMapNamespace(CyCommandNamespace ns) {
		super(ns);

		// Define our subcommands
		addDescription(APPLY, "Apply the named visual style to the current (or named) network");
		addArgument(APPLY);
		addArgument(APPLY, STYLE, "default");
		addArgument(APPLY, NETWORK, CURRENT);

		addDescription(IMPORT, "Import a visual style from a file");
		addArgument(IMPORT, FILE, null);
	}

	public CyCommandResult execute(String command, Collection<Tunable>args) throws CyCommandException {
		return execute(command, createKVMap(args));
	}

	public CyCommandResult execute(String command, Map<String, Object>args) throws CyCommandException { 
		CyCommandResult result = new CyCommandResult();
		if (command.equals(APPLY)) {
			// We want to apply a vizmap to the current network
			if (args.containsKey(STYLE))
				styleName = args.get(STYLE).toString();

			CyNetworkView networkView;

			if (args.containsKey(NETWORK) && !args.get(NETWORK).equals(CURRENT)) {
				networkView = Cytoscape.getNetworkView(args.get(NETWORK).toString());
				if (networkView == null)
					throw new CyCommandException("Can't find view for network "+args.get(NETWORK).toString());
			} else
				networkView = Cytoscape.getCurrentNetworkView();

			// Make sure the style exists
			VisualMappingManager vizMapper = 
				new VisualMappingManager(networkView);

			// We need to get the visual style directly from the catalog because,
			// unlike what the documentation says, setVisualStyle does /not/ return
			// null if the style doesn't exist!
			CalculatorCatalog catalog = vizMapper.getCalculatorCatalog();
			VisualStyle vs = catalog.getVisualStyle(styleName);
			if (vs == null)
				throw new CyCommandException("Unknown style "+styleName);
			vizMapper.setVisualStyle(vs);

			vizMapper.applyAppearances();
			networkView.updateView();
			result.addMessage("vizmap: applied style "+styleName);
			return result;
		} else if (command.equals(IMPORT)) {
			if (!args.containsKey(FILE) || args.get(FILE) == null)
				throw new CyCommandException("vizmap: no filename specified for import");

			File file = new File(args.get(FILE).toString());

			// Do a little sanity checking....
			if (!file.exists())
				throw new CyCommandException("vizmap: no such file: "+file.getAbsolutePath());
			if (!file.canRead())
				throw new CyCommandException("vizmap: can't read the file: "+file.getAbsolutePath());

			Cytoscape.firePropertyChange(Cytoscape.VIZMAP_LOADED, null, file.getAbsolutePath());
			result.addMessage("vizmap: new style imported");
			return result;
		}
		throw new CyCommandException("vizmap: unknown subcommand: "+command);
	}

	public static CyCommandHandler register(String namespace) throws RuntimeException {
		// Get the namespace
		return new VizMapNamespace(CyCommandManager.reserveNamespace(namespace));
	}

}
