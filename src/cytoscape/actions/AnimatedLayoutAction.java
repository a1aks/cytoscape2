package cytoscape.actions;

import java.awt.event.ActionEvent;
import javax.swing.*;

import cytoscape.view.NetworkView;
import cytoscape.dialogs.GraphObjectSelection;

import phoebe.*;
import phoebe.util.*;
import phoebe.event.*;
import java.util.*;

import giny.model.*;
import giny.view.*;

public class AnimatedLayoutAction extends AbstractAction {

  NetworkView networkView;
  boolean bool = false;
  public AnimatedLayoutAction ( NetworkView networkView ) {
    super("Animate Layout");
    this.networkView = networkView;
  }

  public void actionPerformed (ActionEvent e) {

    JDialog dialog = new JDialog();
    JPanel main = new JPanel();

    main.add(  new JButton (new AbstractAction( "Grand Tour" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  PGraphView gv = ( PGraphView )networkView.getView();
                  PGrandTour tour = new PGrandTour( gv );
                  tour.takeTour();
                }
              } ); } } ) );

    main.add(  new JButton (new AbstractAction( "Force" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  PGraphView gv = ( PGraphView )networkView.getView();
                  ModelBasedSpringLayout mbsl = new ModelBasedSpringLayout( gv );
                  mbsl.doLayout();
                }
              } ); } } ) );

     main.add(  new JButton (new AbstractAction( "Tree Select" ) {
          public void actionPerformed ( ActionEvent e ) {
            // Do this in the GUI Event Dispatch thread...
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                  PGraphView gv = ( PGraphView )networkView.getView();
                  int[] sel = gv.getSelectedNodeIndices();
                  TreeLayout tl = new TreeLayout();
                  GraphPerspective p = tl.doLayout( gv ) ;
                  int[] edges = p.getEdgeIndicesArray();
                  for ( int i = 0; i < p.getEdgeCount(); ++i ) {
                    //System.out.println( "Index of Edge is: "+edges[i] );
                    EdgeView ev = gv.getEdgeView( edges[i] );
                    ev.setSelected( true );
                  }


                }
              } ); } } ) );


    main.add(  new JButton (new AbstractAction( "Tree" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                PGraphView gv = ( PGraphView )networkView.getView();
                int[] sel = gv.getSelectedNodeIndices();
                 TreeLayout tl = new TreeLayout();
                 tl.doLayout( gv ) ;
              }
            } ); } } ) );
    


    main.add(  new JButton (new AbstractAction( "Update" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                PGraphView gv = ( PGraphView )networkView.getView();
                Iterator nodes = gv.getNodeViewsIterator();
                while ( nodes.hasNext() ) {
                  ( ( PNodeView )nodes.next() ).setNodePosition( false );
                }
              }
            } ); } } ) );
     main.add(  new JButton (new AbstractAction( "Animate" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                PGraphView gv = ( PGraphView )networkView.getView();
                Iterator nodes = gv.getNodeViewsIterator();
                while ( nodes.hasNext() ) {
                  ( ( PNodeView )nodes.next() ).setNodePosition( true );
                }
              }
            } ); } } ) );


     
    
 

    dialog.getContentPane().add( main );
    dialog.pack();
    dialog.setVisible( true );

  }
}
