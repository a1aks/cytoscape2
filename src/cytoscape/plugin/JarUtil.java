package cytoscape.plugin;

/*
 Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import cytoscape.util.URLUtil;
import cytoscape.util.ZipUtil;
import java.util.zip.ZipFile;

/**
 * A utility class designed to capture methods used by multiple classes.
 */
class JarUtil {

	/** 
	 * Bug 2055 changing regexp used to match jars
	 * Was "\\w+\\.jar", which seemed unecessarily restrictive
	 */
	static final String MATCH_JAR_REGEXP = ".*\\.jar$";

	/**
	 * Iterate through all class files, return the subclass of CytoscapePlugin.
	 * Similar to CytoscapeInit, however only plugins with manifest files that
	 * describe the class of the CytoscapePlugin are valid.
	 */
	static String getPluginClass(String fileName, PluginInfo.FileType type) throws IOException {

		String pluginClassName = null;

		try {

		switch (type) {
		case JAR:
			JarFile jar = new JarFile(fileName);
			try {
				pluginClassName = getManifestAttribute(jar.getManifest());
			} finally {
				if (jar != null) 
					jar.close();
			}
			break;

		case ZIP:
			List<ZipEntry> Entries = ZipUtil.getAllFiles(fileName, MATCH_JAR_REGEXP);
			if (Entries.size() <= 0) {
				String[] filePath = fileName.split("/");
				fileName = filePath[filePath.length - 1];
				throw new IOException( fileName + 
				                       " does not contain any jar files or is not a zip file.");
			}

			ZipFile zf = null;

			try {
				zf = new ZipFile(fileName);
				for (ZipEntry entry : Entries) {
					String entryName = entry.getName();

					InputStream is = null;

					try {
						JarInputStream jis = null;

						is = ZipUtil.readFile(zf, entryName);
						try {
							jis = new JarInputStream(is);
							pluginClassName = getManifestAttribute(jis.getManifest());
						} finally {
							if (jis != null) 
								jis.close();
						}
					} finally {
						if (is != null) 
							is.close();
					}
				}
			} finally {
				if (zf != null) 
					zf.close();
			}
		}

		} catch (Exception e) {
			throw new IOException(e.toString());
		}

		return pluginClassName;
	}

	/*
	 * Gets the manifest file value for the Cytoscape-Plugin attribute
	 */
	static String getManifestAttribute(Manifest m) {
		String value = null;
		if (m != null) {
			value = m.getMainAttributes().getValue("Cytoscape-Plugin");
		}
		return value;
	}
}
