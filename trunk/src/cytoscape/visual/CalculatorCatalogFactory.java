/*
 File: CalculatorCatalogFactory.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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

//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.logger.CyLogger;

import cytoscape.util.FileUtil;
import cytoscape.util.URLUtil;
import cytoscape.util.ZipUtil;

import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.PassThroughMapping;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.Iterator;
import java.util.Properties;
import java.util.Set;


/**
 * This class provides a static method for reading a CalculatorCatalog object
 * from file, using parameters specified in a supplied CytoscapeConfig. What's
 * provided here is the set of files from which to read the calculator
 * information, as well as the construction of a suitable default visual style
 * if one does not already exist.
 */
public abstract class CalculatorCatalogFactory {

		protected static CyLogger logger = CyLogger.getLogger(CalculatorCatalogFactory.class);
 
    private enum MapperNames {DISCRETE("Discrete Mapper"), 
        CONTINUOUS("Continuous Mapper"), PASSTHROUGH("Passthrough Mapper");
        private String name;

        private MapperNames(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static final String VIZMAP_PROPS_FILE_NAME = "vizmap.props";

    // static File propertiesFile;
    static Properties vizmapProps;
    static CalculatorCatalog calculatorCatalog = new CalculatorCatalog();

    public static CalculatorCatalog loadCalculatorCatalog() {
        return loadCalculatorCatalog(null);
    }

    /**
     * Loads a CalculatorCatalog object from the various properties files
     * specified by the options in the supplied CytoscapeConfig object. The
     * catalog will be properly initialized with known mapping types and a
     * default visual style (named "default").
     *
     * @deprecated The vmName parameter is no longer used - just use
     *             loadCalculatorCatalog(). Will be removed 10/06.
     */
    public static CalculatorCatalog loadCalculatorCatalog(String vmName) {
        vizmapProps = CytoscapeInit.getVisualProperties();

        initCatalog();

        Cytoscape.getSwingPropertyChangeSupport()
                 .addPropertyChangeListener(new VizMapListener());

        return calculatorCatalog;
    }

    private static void initCatalog() {
        calculatorCatalog.clear();

        calculatorCatalog.addMapping(
            MapperNames.DISCRETE.toString(),
            DiscreteMapping.class);
        calculatorCatalog.addMapping(
            MapperNames.CONTINUOUS.toString(),
            ContinuousMapping.class);
        calculatorCatalog.addMapping(
            MapperNames.PASSTHROUGH.toString(),
            PassThroughMapping.class);

        CalculatorIO.loadCalculators(vizmapProps, calculatorCatalog);
    }

    /**
     * Catch the signal and save/load VS.
     *
     */
    private static class VizMapListener
        implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName() == Cytoscape.SAVE_VIZMAP_PROPS) {
                /*
                 * This section is for saving VS in a vizmap.props file.
                 *
                 * If signal contains no new value, Cytoscape consider it as a
                 * default file. Otherwise, save it as a user file.
                 */
                File propertiesFile = null;

                if (e.getNewValue() == null)
                    propertiesFile = CytoscapeInit.getConfigFile(VIZMAP_PROPS_FILE_NAME);
                else
                    propertiesFile = new File((String) e.getNewValue());

                if (propertiesFile != null) {
                    Set test = calculatorCatalog.getVisualStyleNames();
                    Iterator it = test.iterator();
										String styles = "Saving the following Visual Styles: ";

                    while (it.hasNext())
                        styles += "\n    - " + it.next().toString();

                    logger.info(styles);
                    CalculatorIO.storeCatalog(calculatorCatalog, propertiesFile);
                    logger.info("Vizmap saved to: " + propertiesFile);
                }
            } else if ((e.getPropertyName() == Cytoscape.VIZMAP_RESTORED) ||
                    (e.getPropertyName() == Cytoscape.VIZMAP_LOADED)) {
                /*
                 * This section is for restoring VS from a file.
                 */

                // only clear the existing vizmap.props if we're restoring
                // from a session file
                if (e.getPropertyName() == Cytoscape.VIZMAP_RESTORED)
                    vizmapProps.clear();

                // get the new vizmap.props and apply it the existing properties
                Object vizmapSource = e.getNewValue();
                logger.info("vizmapSource: '" + vizmapSource.toString() +
                    "'");

                try {
                    InputStream is = null;

                    if (vizmapSource.getClass() == URL.class)
                        // is = ((URL) vizmapSource).openStream();
                        // Use URLUtil to get the InputStream since we might be using a proxy server 
        				// and because pages may be cached:
                        is = URLUtil.getBasicInputStream((URL) vizmapSource);                     
                    else if (vizmapSource.getClass() == String.class) {
                        // if its a RESTORED event the vizmap
                        // file will be in a zip file.
                        if (e.getPropertyName() == Cytoscape.VIZMAP_RESTORED) {
                            is = ZipUtil.readFile((String) vizmapSource,
                                    ".*vizmap.props");

                            // if its a LOADED event the vizmap file
                            // will be a normal file.
                        } else
                            is = FileUtil.getInputStream((String) vizmapSource);
                    }

                    if (is != null) {
                        vizmapProps.load(is);
                        is.close();
                    }
                } catch (FileNotFoundException e1) {
										logger.error("Unable to open visual mapper file: "+e1.getMessage());
                    // e1.printStackTrace();
                } catch (IOException e1) {
										logger.error("Unable to read visual mapper file: "+e1.getMessage());
                    // e1.printStackTrace();
                }

                initCatalog();

                /*
                 * TODO: Mechanism to apply VS is too complicated. Should be
                 * something like
                 * Cytoscape.getDesktop.getVisualMappingManger.apply(VS_NAME);
                 */
                logger.info("Applying visual styles from: " +
                    vizmapSource.toString());
                // Always re-create the vizmapper, otherwise things won't
                // initialize correctly... or figure out how to reinitialize
                // things, in particular the various VizMapAttrTabs.
//                Cytoscape.getDesktop()
//                         .setupVizMapper();
//                Cytoscape.getDesktop()
//                         .getVizMapUI()
//                         .getStyleSelector()
//                         .resetStyles();
//                Cytoscape.getDesktop()
//                         .getVizMapUI()
//                         .getStyleSelector()
//                         .repaint();
//                Cytoscape.getDesktop()
//                         .getVizMapUI()
//                         .refreshUI();

                // In the situation where the old visual style has been
                // overwritten
                // with a new visual style of the same name, then make sure it
                // is
                // reapplied.
                final VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
                vmm.setVisualStyle(vmm.getVisualStyle().getName());
                Cytoscape.getCurrentNetworkView()
                         .setVisualStyle(vmm.getVisualStyle().getName());
                Cytoscape.getCurrentNetworkView()
                         .redrawGraph(false, true);

                // Since the toolbar tends to get messed up, repaint it.
                Cytoscape.getDesktop()
                         .repaint();
            }
        }
    }
}
