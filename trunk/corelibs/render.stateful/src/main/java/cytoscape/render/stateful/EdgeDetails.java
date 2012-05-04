
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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

package cytoscape.render.stateful;

import cytoscape.render.immed.EdgeAnchors;
import cytoscape.render.immed.GraphGraphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.BasicStroke;


/**
 * Defines the visual properties of an edge.  Even though this class is not
 * declared abstract, in most situations it makes sense to override at least
 * some of its methods (especially segmentThickness()) in order to gain
 * control over edge visual properties.<p>
 * To understand the significance of each method's return value, it makes
 * sense to become familiar with the API cytoscape.render.immed.GraphGraphics.
 */
public class EdgeDetails {
	/**
	 * Specifies that an anchor point lies at the midpoint of an edge.
	 */
	public static final byte EDGE_ANCHOR_MIDPOINT = 16;

	/**
	 * Specifies that an anchor point lies at an edge's endpoint at source
	 * node.
	 */
	public static final byte EDGE_ANCHOR_SOURCE = 17;

	/**
	 * Specifies that an anchor point lies at an edge's endpoint at target
	 * node.
	 */
	public static final byte EDGE_ANCHOR_TARGET = 18;

	/**
	 * Instantiates edge details with defaults.  Documentation on each method
	 * describes defaults.  To override defaults, extend this class.
	 */
	public EdgeDetails() {
	}

	/**
	 * Returns the color of edge in low detail rendering mode.
	 * By default this method returns Color.blue.  It is an error to return null
	 * in this method.<p>
	 * In low detail rendering mode, this is the only method from this class
	 * that is looked at.  The rest of the methods in this class define visual
	 * properties that are used in full detail rendering mode.  In low detail
	 * rendering mode translucent colors are not supported whereas in full
	 * detail rendering mode they are.
	 */
	public Color colorLowDetail(final int edge) {
		return Color.blue;
	}

	/**
	 * Returns a GraphGraphics.ARROW_* constant; this defines the arrow
	 * to use when rendering the edge endpoint touching source node.
	 * By default this method returns GraphGraphics.ARROW_NONE.
	 * Take note of certain constraints specified in
	 * GraphGraphics.drawEdgeFull().
	 */
	public byte sourceArrow(final int edge) {
		return GraphGraphics.ARROW_NONE;
	}

	/**
	 * Returns the size of the arrow at edge endpoint touching source node.
	 * By default this method returns zero.  This return value is ignored
	 * if sourceArrow(edge) returns GraphGraphics.ARROW_NONE.
	 * Take note of certain constraints specified in
	 * GraphGraphics.drawEdgeFull().
	 */
	public float sourceArrowSize(final int edge) {
		return 0.0f;
	}

	/**
	 * Returns the paint of the arrow at edge endpoint touching source node.
	 * By default this method returns null.  This return value is ignored if
	 * sourceArrow(edge) returns GraphGraphics.ARROW_NONE 
	 *  it is an error to return null.
	 */
	public Paint sourceArrowPaint(final int edge) {
		return null;
	}

	/**
	 * Returns a GraphGraphics.ARROW_* constant; this defines the arrow
	 * to use when rendering the edge endpoint at the target node.
	 * By default this method returns GraphGraphics.ARROW_NONE.
	 * Take note of certain constraints specified in
	 * GraphGraphics.drawEdgeFull().
	 */
	public byte targetArrow(final int edge) {
		return GraphGraphics.ARROW_NONE;
	}

	/**
	 * Returns the size of the arrow at edge endpoint touching target node.
	 * By default this method returns zero.  
	 * Take note of certain constraints specified
	 * in GraphGraphics.drawEdgeFull().
	 */
	public float targetArrowSize(final int edge) {
		return 0.0f;
	}

	/**
	 * Returns the paint of the arrow at edge endpoint touching target node.
	 * By default this method returns null.  This return value is ignored if
	 * targetArrow(edge) returns GraphGraphics.ARROW_NONE,
	 * it is an error to return null.
	 */
	public Paint targetArrowPaint(final int edge) {
		return null;
	}

	/**
	 * Returns edge anchors to use when rendering this edge.
	 * By default this method returns null; returning null is the optimal
	 * way to specify that this edge has no anchors.  Take note of certain
	 * constraints, specified in GraphGraphics.drawEdgeFull(), pertaining to
	 * edge anchors.<p>
	 * The anchors returned are interpreted such that the anchor at index zero
	 * (the "first" anchor) is the anchor next to the source node of this edge;
	 * the last anchor is the anchor next to the target node of this edge.  The
	 * rendering engine works such that if the first anchor lies inside
	 * the source node shape or if the last anchor lies inside the target
	 * node shape, the edge is not rendered.
	 */
	public EdgeAnchors anchors(final int edge) {
		return null;
	}

	/**
	 * For edges with anchors, the anchors can be rendered as squares.  To render
	 * an anchor, return a positive value in this method.  If zero is returned
	 * no edge anchor is rendered.  By default this method returns zero.
	 */
	public float anchorSize(final int edge, final int anchorInx) {
		return 0.0f;
	}

	/**
	 * Returns the paint to use when rendering an edge anchor.  The output of
	 * this method is ignored if anchorSize(edge, anchorInx) returned zero;
	 * otherwise, a non-null value must be returned.  By default this method
	 * returns null.
	 */
	public Paint anchorPaint(final int edge, final int anchorInx) {
		return null;
	}

	/**
	 * Returns the thickness of the edge segment.
	 * <font color="red">By default this method returns zero.</font>
	 * Take note of certain constraints specified in
	 * GraphGraphics.drawEdgeFull().
	 */
	public float segmentThickness(final int edge) {
		return 0.0f;
	}

	private static final Stroke default_stroke = new BasicStroke();
	public Stroke segmentStroke(final int edge) {
		return default_stroke;
	}

	/**
	 * Returns the paint of the edge segment.
	 * By default this method returns Color.blue.  It is an error to
	 * return null in this method.
	 */
	public Paint segmentPaint(final int edge) {
		return Color.blue;
	}

	/**
	 * Returns the length of dashes on edge segment, or zero to indicate
	 * that the edge segment is solid.  By default this method returns zero.
	 */
//	public float segmentDashLength(final int edge) {
//		return 0.0f;
//	}

	/**
	 * Returns the number of labels that this edge has.  By default this method
	 * returns zero.
	 */
	public int labelCount(final int edge) {
		return 0;
	}

	/**
	 * Returns a label's text.  By default this method always returns null.
	 * This method is only called by the rendering engine if labelCount(edge)
	 * returns a value greater than zero.  It is an error to return null if this
	 * method is called by the rendering engine.<p>
	 * To specify multiple lines of text in an edge label, simply insert the
	 * '\n' character between lines of text.
	 * @param labelInx a value in the range [0, labelCount(edge)-1] indicating
	 *   which edge label in question.
	 */
	public String labelText(final int edge, final int labelInx) {
		return null;
	}

	/**
	 * Returns the font to use when rendering this label.  By default this
	 * method always returns null.  This method is only called by the rendering
	 * engine if labelCount(edge) returns a value greater than zero.  It is an
	 * error to return null if this method is called by the rendering engine.
	 * @param labelInx a value in the range [0, labelCount(edge)-1] indicating
	 *   which edge label in question.
	 */
	public Font labelFont(final int edge, final int labelInx) {
		return null;
	}

	/**
	 * Returns an additional scaling factor that is to be applied to the font
	 * used to render this label; this scaling factor, applied to the point
	 * size of the font returned by labelFont(edge, labelInx), yields a new
	 * virtual font that is used to render the text label.  By default this
	 * method always returns 1.0.  This method is only called by the rendering
	 * engine if labelCount(edge) returns a value greater than zero.
	 * @param labelInx a value in the range [0, labelCount(edge)-1] indicating
	 *   which edge label in question.
	 */
	public double labelScaleFactor(final int edge, final int labelInx) {
		return 1.0d;
	}

	/**
	 * Returns the paint of a text label.  By default this method always
	 * returns null.  This method is only called by the rendering engine if
	 * labelCount(edge) returns a value greater than zero.  It is an error to
	 * return null if this method is called by the rendering engine.
	 * @param labelInx a value in the range [0, labelCount(edge)-1] indicating
	 *   which edge label in question.
	 */
	public Paint labelPaint(final int edge, final int labelInx) {
		return null;
	}

	/**
	 * By returning one of the NodeDetails.ANCHOR_* constants, specifies where
	 * on a text label's logical bounds box an anchor point lies.  This
	 * <i>text anchor point</i> together with the edge anchor point and label
	 * offset vector determines where, relative to the edge, the text's logical
	 * bounds box is to be placed.  The text's logical bounds box is placed
	 * such that the label offset vector plus the edge anchor point equals the
	 * text anchor point.<p>
	 * By default this method always returns NodeDetails.ANCHOR_CENTER.
	 * This method is only called by the rendering engine if labelCount(edge)
	 * returns a value greater than zero.
	 * @param labelInx a value in the range [0, labelCount(edge)-1] indicating
	 *   which edge label in question.
	 * @see NodeDetails#ANCHOR_CENTER
	 * @see #labelEdgeAnchor(int, int)
	 * @see #labelOffsetVectorX(int, int)
	 * @see #labelOffsetVectorY(int, int)
	 */
	public byte labelTextAnchor(final int edge, final int labelInx) {
		return NodeDetails.ANCHOR_CENTER;
	}

	/**
	 * By returning one of the EDGE_ANCHOR_* constants, specifies where on
	 * an edge an anchor point lies.  This <i>edge anchor point</i> together
	 * with the text anchor point and label offset vector determines where,
	 * relative to the edge, the text's logical bounds box is to be placed.
	 * The text's logical bounds box is placed such that the label offset
	 * vector plus the edge anchor point equals the text anchor point.<p>
	 * By default this method always returns EDGE_ANCHOR_MIDPOINT.  This method
	 * is only called by the rendering engine if labelCount(edge) returns a
	 * value greater than zero.
	 * @param labelInx a value in the range [0, labelCount(edge)-1] indicating
	 *   which edge label in question.
	 * @see #EDGE_ANCHOR_MIDPOINT
	 * @see #labelTextAnchor(int, int)
	 * @see #labelOffsetVectorX(int, int)
	 * @see #labelOffsetVectorY(int, int)
	 */
	public byte labelEdgeAnchor(final int edge, final int labelInx) {
		return EDGE_ANCHOR_MIDPOINT;
	}

	/**
	 * Specifies the X component of the vector that separates a text anchor
	 * point from an edge anchor point.  This <i>label offset vector</i>
	 * together with the text anchor point and edge anchor point determines
	 * where, relative to the edge, the text's logical bounds box is to be
	 * placed.  The text's logical bounds box is placed such that the label
	 * offset vector plus the edge anchor point equals the text anchor point.<p>
	 * By default this method always returns zero.  This method is only called
	 * by the rendering engine if labelCount(edge) returns a value greater than
	 * zero.
	 * @param labelInx a value in the range [0, labelCount(edge)-1] indicating
	 *   which edge label in question.
	 * @see #labelOffsetVectorY(int, int)
	 * @see #labelTextAnchor(int, int)
	 * @see #labelEdgeAnchor(int, int)
	 */
	public float labelOffsetVectorX(final int edge, final int labelInx) {
		return 0.0f;
	}

	/**
	 * Specifies the Y component of the vector that separates a text anchor
	 * point from an edge anchor point.  This <i>label offset vector</i>
	 * together with the text anchor point and edge anchor point determines
	 * where, relative to the edge, the text's logical bounds box is to be
	 * placed.  The text's logical bounds box is placed such that the label
	 * offset vector plus the edge anchor point equals the text anchor point.<p>
	 * By default this method always returns zero.  This method is only called
	 * by the rendering engine if labelCount(edge) returns a value greater than
	 * zero.
	 * @param labelInx a value in the range [0, labelCount(edge)-1] indicating
	 *   which edge label in question.
	 * @see #labelOffsetVectorX(int, int)
	 * @see #labelTextAnchor(int, int)
	 * @see #labelEdgeAnchor(int, int)
	 */
	public float labelOffsetVectorY(final int edge, final int labelInx) {
		return 0.0f;
	}

	/**
	 * By returning one of the NodeDetails.LABEL_WRAP_JUSTIFY_* constants,
	 * determines how to justify an edge label spanning multiple lines.  The
	 * choice made here does not affect the size of the logical bounding box
	 * of an edge label's text.  The lines of text are justified within that
	 * logical bounding box.<p>
	 * By default this method always returns
	 * NodeDetails.LABEL_WRAP_JUSTIFY_CENTER.  This return value is ignored
	 * if labelText(edge, labelInx) returns a text string that does not span
	 * multiple lines.
	 * @see NodeDetails#LABEL_WRAP_JUSTIFY_CENTER
	 */
	public byte labelJustify(final int edge, final int labelInx) {
		return NodeDetails.LABEL_WRAP_JUSTIFY_CENTER;
	}

	//   /**
	//    * Determines whether or not a label is to be rendered horizontally or
	//    * following the tangent at edge anchor point.  By default this method
	//    * always returns true, which results in horizontal text labels.
	//    */
	//   public boolean labelHorizontal(final int edge, final int labelInx) {
	//     return true; }

	/**
	 * Returns the width of the label. 
	 * <font color="red">By default this method returns 100.</font>
	 * Take note of certain constraints specified in
	 * GraphGraphics.drawEdgeFull().
	 */
	public double labelWidth(final int edge) {
		return 100.0;
	}
}
