// OntologyDescription.java

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

//------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape.data.annotation;
//-----------------------------------------------------------------------------------
import java.util.*;
import java.io.*;
//------------------------------------------------------------------------------
/**
 *  Distinguish among different ontologies, by curator and type.
 *  For example, KEGG maintains two ontologies:  metabolic pathways,
 *  and regulatory pathways.  GO has three: biological process, molecular 
 *  function, cellular component.
 *  This simple class captures these distinctions
 *  It will perhaps prove most useful when some data source (a biodata server,
 *  for example) contains a number of ontologies, and needs to communicate
 *  a summary of these to a client.
 */
public class OntologyDescription implements Serializable {

  protected String curator;       // KEGG, GO, ...
  protected String ontologyType;  // biological process, metabolic pathway, ...

//------------------------------------------------------------------------------
/**
 *  @param curator        The institute or group which maintains this ontology
 *  @param ontolotyType   The nature of this ontology, eg, "metabolic pathway",
 *                        "molecular function", or "cellular component"
 */
public OntologyDescription (String curator, String ontologyType)
{
  this.curator = curator;
  this.ontologyType = ontologyType;

} // ctor
//------------------------------------------------------------------------------
public String getCurator ()
{
  return curator;
}
//------------------------------------------------------------------------------
public String getType ()
{
  return ontologyType;
}
//------------------------------------------------------------------------------
public String toString ()
{
  StringBuffer sb = new StringBuffer ();
  sb.append ("ontology: " + curator + ", " + ontologyType);

  return sb.toString ();

} // toString
//------------------------------------------------------------------------------
} // class OntologyDescription

