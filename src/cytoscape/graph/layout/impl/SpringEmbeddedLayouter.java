package cytoscape.graph.layout.impl;

import cytoscape.graph.layout.algorithm.LayoutAlgorithm;
import cytoscape.graph.layout.algorithm.MutableGraphLayout;
import cytoscape.process.PercentCompletedCallback;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An implementation of Kamada and Kawai's spring embedded layout algorithm.
 **/
public final class SpringEmbeddedLayouter extends LayoutAlgorithm
{

  private static final
    int DEFAULT_NUM_LAYOUT_PASSES = 2;
  private static final
    double DEFAULT_AVERAGE_ITERATIONS_PER_NODE = 20.0;
  private static final
    double[] DEFAULT_NODE_DISTANCE_SPRING_SCALARS = new double[] { 1.0, 1.0 };
  private static final
    double DEFAULT_NODE_DISTANCE_STRENGTH_CONSTANT = 15.0;
  private static final
    double DEFAULT_NODE_DISTANCE_REST_LENGTH_CONSTANT = 200.0;
  private static final
    double DEFAULT_DISCONNECTED_NODE_DISTANCE_SPRING_STRENGTH = 0.05;
  private static final
    double DEFAULT_DISCONNECTED_NODE_DISTANCE_SPRING_REST_LENGTH = 2500.0;
  private static final
    double[] DEFAULT_ANTICOLLISION_SPRING_SCALARS = new double[] { 0.0, 1.0 };
  private static final
    double DEFAULT_ANTICOLLISION_SPRING_STRENGTH = 100.0;

  private final int m_numLayoutPasses;
  private final double m_averageIterationsPerNode;
  private final double[] m_nodeDistanceSpringScalars;
  private final double m_nodeDistanceStrengthConstant;
  private final double m_nodeDistanceRestLengthConstant;
  private final double m_disconnectedNodeDistanceSpringStrength;
  private final double m_disconnectedNodeDistanceSpringRestLength;
  private final double[] m_anticollisionSpringScalars;
  private final double m_anticollisionSpringStrength;

  private double[][] m_nodeDistanceSpringStrengths;
  private double[][] m_nodeDistanceSpringRestLengths;

  private final int m_nodeCount;
  private final int m_edgeCount;
  private int m_layoutPass;

  private boolean m_halt = false;

  /**
   * Constructs an object which is able to perform a specific layout algorithm
   * on a graph.  An instance of this class will perform a layout at most
   * once.  The constructor returns quickly; <code>run()</code> does the
   * computations to perform the layout.<p>
   * A word about the <code>PercentCompletedCallback</code> parameter that
   * is passed to this constructor.  <code>percentComplete</code> may be
   * <code>null</code>, in which case this layout algorithm will not report
   * percent completed to the parent application.  If
   * <code>percentComplete</code> is not <code>null</code> then this object
   * will call <code>percentComplete.setPercentCompleted()</code> <i>ONLY</i>
   * from the thread that calls <code>run()</code>, as frequently as this
   * object sees fit.
   *
   * @param graph the graph layout object that this layout algorithm
   *   operates on.
   * @param percentComplete a hook that a parent application may pass in
   *   in order to get information regarding what percentage of the layout
   *   has been completed.
   **/
  public SpringEmbeddedLayouter(MutableGraphLayout graph,
                                PercentCompletedCallback percentComplete)
  {
    super(graph);
    m_numLayoutPasses = DEFAULT_NUM_LAYOUT_PASSES;
    m_averageIterationsPerNode = DEFAULT_AVERAGE_ITERATIONS_PER_NODE;
    m_nodeDistanceSpringScalars = DEFAULT_NODE_DISTANCE_SPRING_SCALARS;
    m_nodeDistanceStrengthConstant = DEFAULT_NODE_DISTANCE_STRENGTH_CONSTANT;
    m_nodeDistanceRestLengthConstant =
      DEFAULT_NODE_DISTANCE_REST_LENGTH_CONSTANT;
    m_disconnectedNodeDistanceSpringStrength =
      DEFAULT_DISCONNECTED_NODE_DISTANCE_SPRING_STRENGTH;
    m_disconnectedNodeDistanceSpringRestLength =
      DEFAULT_DISCONNECTED_NODE_DISTANCE_SPRING_REST_LENGTH;
    m_anticollisionSpringScalars = DEFAULT_ANTICOLLISION_SPRING_SCALARS;
    m_anticollisionSpringStrength = DEFAULT_ANTICOLLISION_SPRING_STRENGTH;
    m_nodeCount = m_graph.getNumNodes();
    m_edgeCount = m_graph.getNumEdges();
  }

  private static class PartialDerivatives
  {

    final int nodeIndex;
    double x;
    double y;
    double xx;
    double yy;
    double xy;
    double euclideanDistance;

    PartialDerivatives(int nodeIndex)
    {
      this.nodeIndex = nodeIndex;
    }

    PartialDerivatives(PartialDerivatives copyFrom)
    {
      this.nodeIndex = copyFrom.nodeIndex;
      copyFrom(copyFrom);
    }

    void reset ()
    {
      x = 0.0;
      y = 0.0;
      xx = 0.0;
      yy = 0.0;
      xy = 0.0;
      euclideanDistance = 0.0;
    }

    void copyFrom (PartialDerivatives otherPartialDerivatives)
    {
      x = otherPartialDerivatives.x;
      y = otherPartialDerivatives.y;
      xx = otherPartialDerivatives.xx;
      yy = otherPartialDerivatives.yy;
      xy = otherPartialDerivatives.xy;
      euclideanDistance = otherPartialDerivatives.euclideanDistance;
    }

  }

  /**
   * This starts the layout process.  This method is called by a parent
   * application using this layout algorithm.
   **/
  public void run()
  {
    if (m_halt) return;

    // Stop if all nodes are closer together than this euclidean distance.
    final double euclideanDistanceThreshold =
      (0.5 * (m_nodeCount + m_edgeCount));

    final int numIterations =
      (int) (m_averageIterationsPerNode * m_nodeCount / m_numLayoutPasses);

    List partialsList = new ArrayList();
    final double[] potentialEnergy = new double[] { 0,0 };

    PartialDerivatives partials;
    PartialDerivatives furthestNodePartials = null;
    double currentProgressTemp;
    double setupProgress = 0.0;
    for (m_layoutPass = 0; m_layoutPass < m_numLayoutPasses; m_layoutPass++)
    {
    }
  }

  /**
   * Signals to a running layout that it's time to abort and exit.  This
   * method is called by a parent application using this layout algorithm.
   * This method will return immediately when called.  <code>run()</code>
   * will return eventually, and soon after calling <code>halt()</code>.
   **/
  public void halt()
  {
    m_halt = true;
  }

  /*
  public Object construct () {


      setupForLayoutPass();

      //System.out.println( " DO Layout Pass " );
      
      // initialize this layout pass
      potential_energy.reset();
      partials_list.clear();

      // Calculate all node distances.  Keep track of the furthest.
      node_views_iterator = graphView.getNodeViewsIterator();
      while( node_views_iterator.hasNext() ) {
        node_view = ( NodeView )node_views_iterator.next();

        //System.out.println( "Calculate Partials for: "+node_view.getGraphPerspectiveIndex() );

        partials = new PartialDerivatives( node_view );
        calculatePartials(
          partials,  
          null,
          potential_energy,
          false
        );
        partials_list.add( partials );
        if( ( furthest_node_partials == null ) ||
            ( partials.euclideanDistance >
              furthest_node_partials.euclideanDistance )
          ) {
          //  //System.out.println( "P: "+furthest_node_partials.euclideanDistance+" E: "+partials.euclideanDistance );
          furthest_node_partials = partials;
        }
      }

      // Until num_iterations, or the furthest node is not-so-fur, move the
      // furthest node towards where it wants to be.
      for( int iterations_i = 0;
           ( ( iterations_i < num_iterations ) &&
             ( furthest_node_partials.euclideanDistance >=
               euclidean_distance_threshold ) );
           iterations_i++
         ) {
        // TODO: REMOVE
        //System.out.println( "At iteration " + layoutPass + ":" + iterations_i + ", furthest_node_partials is " + furthest_node_partials + "." );
        furthest_node_partials =
          moveNode( furthest_node_partials, partials_list, potential_energy );
      } // End for each iteration, attempt to minimize the total potential
        // energy by moving the node that is furthest from where it should be.
    } // End for each layout pass
    return null;
  } // doLayout()

  // Called at the beginning of each layoutPass iteration.
  protected void setupForLayoutPass () {
    setupNodeDistanceSprings();
  } // setupForLayoutPass()

  protected void setupNodeDistanceSprings () {
    // We only have to do this once.
    if( layoutPass != 0 ) {
      return;
    }

    nodeDistanceSpringRestLengths = new double[ nodeCount ][ nodeCount ];
    nodeDistanceSpringStrengths = new double[ nodeCount ][ nodeCount ];

    if( nodeDistanceSpringScalars[ layoutPass ] == 0.0 ) {
      return;
    }


    
    NodeDistances ind = new NodeDistances( graphView.getGraphPerspective().nodesList(), null, graphView.getGraphPerspective() );
    int[][] node_distances = ( int[][] )ind.calculate();

    if( node_distances == null ) {
      return;
    }

    // TODO: A good strength_constant is the characteristic path length of the
    // graph.  For now we'll just use nodeDistanceStrengthConstant.
    double node_distance_strength_constant = nodeDistanceStrengthConstant;

    // TODO: rest_length_constant can be chosen to scale the whole graph.
    // To make it the size of the current view, try
    // rest_length_constant = Math.sqrt( ( ( graphView.getViewRect().width / graphView.getViewRect.height() ) / 4 ) / graphView.getGraphDiameter() );
    // To make it bigger, try
    // rest_length_constant = graphView.averageEdgeLength();
    // To make it smaller, try
    // rest_length_constant = Math.sqrt( ( graphView.getViewRect().width * graphView.getViewRect.height() ) / graphView.getGraphDiameter() );
    // For now we'll just use nodeDistanceRestLengthConstant.
    double node_distance_rest_length_constant = nodeDistanceRestLengthConstant;

    // Calculate the rest lengths and strengths based on the node distance data
    for( int node_i = 0; node_i < nodeCount; node_i++ ) {
      for( int node_j = ( node_i + 1 ); node_j < nodeCount; node_j++ ) {


        //System.out.println( "APSP: node_i: "+node_i+ " node_j: "+ node_j+" == "+node_distances[ node_i ][node_j ] );

        if( node_distances[ node_i ][ node_j ] == Integer.MAX_VALUE ) {
          nodeDistanceSpringRestLengths[ node_i ][ node_j ] =
            disconnectedNodeDistanceSpringRestLength;
          //System.out.println( "disconnectedNodeDistanceSpringRestLength 1: "+ disconnectedNodeDistanceSpringRestLength );
        } else {
          nodeDistanceSpringRestLengths[ node_i ][ node_j ] =
            ( node_distance_rest_length_constant *
              node_distances[ node_i ][ node_j ] );
          //System.out.println( " ELSE 1: "+nodeDistanceSpringRestLengths[ node_i ][ node_j ] );
        }
        // Mirror over the diagonal.
        nodeDistanceSpringRestLengths[ node_j ][ node_i ] =
          nodeDistanceSpringRestLengths[ node_i ][ node_j ];

        if( node_distances[ node_i ][ node_j ] == Integer.MAX_VALUE ) {
          nodeDistanceSpringStrengths[ node_i ][ node_j ] =
            disconnectedNodeDistanceSpringStrength;
        } else {
          nodeDistanceSpringStrengths[ node_i ][ node_j ] =
            ( node_distance_strength_constant /
              ( node_distances[ node_i ][ node_j ] *
                node_distances[ node_i ][ node_j ] )
            );
        }
        // Mirror over the diagonal.
        nodeDistanceSpringStrengths[ node_j ][ node_i ] =
          nodeDistanceSpringStrengths[ node_i ][ node_j ];

     
      }
     
    }
    // currentProgress has been increased by ( nodeCount * nodeCount ).

  } // setupNodeDistanceSprings()

  // If partials_list is given, adjust all partials (bidirectional) for the
  // current location of the given partials and return the new furthest node's
  // partials.  Otherwise, just adjust the given partials (using the
  // graphView's nodeViewsIterator), and return it.  If reversed is true then
  // partials_list must be provided and all adjustments made by a non-reversed
  // call (with the same partials with the same graphNodeView at the same
  // location) will be undone.
  // Complexity is O( #Nodes ).
  protected PartialDerivatives calculatePartials (
    PartialDerivatives partials,
    List partials_list,
    PotentialEnergy potential_energy,
    boolean reversed
  ) {

    partials.reset();

    NodeView node_view = partials.getNodeView();
    int node_view_index = node_view.getGraphPerspectiveIndex() - 1;
    double node_view_radius = node_view.getWidth();
    double node_view_x = node_view.getXPosition();
    double node_view_y = node_view.getYPosition();


    //System.out.println( "index: "+node_view_index+" x: "+node_view_x+" y:" +node_view_y );

    PartialDerivatives other_node_partials = null;
    NodeView other_node_view;
    int other_node_view_index;
    double other_node_view_radius;

    PartialDerivatives furthest_partials = null;

    Iterator iterator;
    if( partials_list == null ) {
      iterator = graphView.getNodeViewsIterator();
    } else {
      iterator = partials_list.iterator();
    }
    double delta_x;
    double delta_y;
    double euclidean_distance;
    double euclidean_distance_cubed;
    double distance_from_rest;
    double distance_from_touching;
    double incremental_change;
    while( iterator.hasNext() ) {
      if( partials_list == null ) {
        other_node_view = ( NodeView )iterator.next();
      } else {
        other_node_partials = ( PartialDerivatives )iterator.next();
        other_node_view = other_node_partials.getNodeView();
      }

      

      //System.out.println( "Node_View: "+ (node_view.getGraphPerspectiveIndex() - 1 ));
      //System.out.println( "Other_Node_View: "+ (other_node_view.getGraphPerspectiveIndex() - 1 ) );

      if ( node_view.getGraphPerspectiveIndex() - 1 == other_node_view.getGraphPerspectiveIndex() - 1 ) {
        //System.out.println( "Nodes are the same. " );
        continue;
      }

      other_node_view_index = other_node_view.getGraphPerspectiveIndex() - 1;
      other_node_view_radius = other_node_view.getWidth();

      delta_x = ( node_view_x - other_node_view.getXPosition() );
      delta_y = ( node_view_y - other_node_view.getYPosition() );

      //System.out.println( "Delta's Calculated: "+delta_y+ "  "+delta_x );

      euclidean_distance =
        Math.sqrt( ( delta_x * delta_x ) + ( delta_y * delta_y ) );
      euclidean_distance_cubed = Math.pow( euclidean_distance, 3 );


      //System.out.println( "Euclidean_Distance: "+euclidean_distance+" Euclidean_Distance_Cubed: "+euclidean_distance_cubed );

      distance_from_touching =
        ( euclidean_distance -
          ( node_view_radius + other_node_view_radius ) );

      //System.out.println( "Distance_From_Touching: "+distance_from_touching );

      incremental_change =
        ( nodeDistanceSpringScalars[ layoutPass ] *
          ( nodeDistanceSpringStrengths[ node_view_index ][ other_node_view_index ] *
            ( delta_x -
              (
               ( nodeDistanceSpringRestLengths[ node_view_index ][ other_node_view_index ] *
                 delta_x ) /
               euclidean_distance
              )
            )
          )
        );

      //System.out.println( "Incremental_Change: "+incremental_change );

      if( !reversed ) {
        partials.x += incremental_change;
      }
      if( other_node_partials != null ) {
        incremental_change =
          ( nodeDistanceSpringScalars[ layoutPass ] *
            ( nodeDistanceSpringStrengths[ other_node_view_index ][ node_view_index ] *
              ( -delta_x -
                (
                 ( nodeDistanceSpringRestLengths[ other_node_view_index ][ node_view_index ] *
                   -delta_x ) /
                 euclidean_distance
                )
              )
            )
          );
        if( reversed ) {
          other_node_partials.x -= incremental_change;
        } else {
          other_node_partials.x += incremental_change;
        }
      }
      if( distance_from_touching < 0.0 ) {
        incremental_change =
          ( anticollisionSpringScalars[ layoutPass ] *
            ( anticollisionSpringStrength *
              ( delta_x -
                (
                 ( ( node_view_radius + other_node_view_radius ) *
                   delta_x ) /
                 euclidean_distance
                )
              )
            )
          );
        if( !reversed ) {
          partials.x += incremental_change;
        }
        if( other_node_partials != null ) {
          incremental_change =
            ( anticollisionSpringScalars[ layoutPass ] *
              ( anticollisionSpringStrength *
                ( -delta_x -
                  (
                   ( ( node_view_radius + other_node_view_radius ) *
                     -delta_x ) /
                   euclidean_distance
                  )
                )
              )
            );
          if( reversed ) {
            other_node_partials.x -= incremental_change;
            //System.out.println( "Other_Node_Partials (-): "+other_node_partials.x );
          } else {
            other_node_partials.x += incremental_change;
            //System.out.println( "Other_Node_Partials (+): "+other_node_partials.x );
          }
        }
      }
      incremental_change =
        ( nodeDistanceSpringScalars[ layoutPass ] *
          ( nodeDistanceSpringStrengths[ node_view_index ][ other_node_view_index ] *
            ( delta_y -
              (
               ( nodeDistanceSpringRestLengths[ node_view_index ][ other_node_view_index ] *
                 delta_y ) /
               euclidean_distance
              )
            )
          )
        );

      //System.out.println( "Incremental_Change: "+incremental_change );

      if( !reversed ) {
        partials.y += incremental_change;
      }
      if( other_node_partials != null ) {
        incremental_change =
          ( nodeDistanceSpringScalars[ layoutPass ] *
            ( nodeDistanceSpringStrengths[ other_node_view_index ][ node_view_index ] *
              ( -delta_y -
                (
                 ( nodeDistanceSpringRestLengths[ other_node_view_index ][ node_view_index ] *
                   -delta_y ) /
                 euclidean_distance
                )
              )
            )
          );
        if( reversed ) {
          other_node_partials.y -= incremental_change;
        } else {
          other_node_partials.y += incremental_change;
        }
      }
      if( distance_from_touching < 0.0 ) {
        incremental_change =
          ( anticollisionSpringScalars[ layoutPass ] *
            ( anticollisionSpringStrength *
              ( delta_y -
                (
                 ( ( node_view_radius + other_node_view_radius ) *
                   delta_y ) /
                 euclidean_distance
                )
              )
            )
          );
        if( !reversed ) {
          partials.y += incremental_change;
        }
        if( other_node_partials != null ) {
          incremental_change =
            ( anticollisionSpringScalars[ layoutPass ] *
              ( anticollisionSpringStrength *
                ( -delta_y -
                  (
                   ( ( node_view_radius + other_node_view_radius ) *
                     -delta_y ) /
                   euclidean_distance
                  )
                )
              )
            );
          if( reversed ) {
            other_node_partials.y -= incremental_change;
          } else {
            other_node_partials.y += incremental_change;
          }
        }
      }

      incremental_change =
        ( nodeDistanceSpringScalars[ layoutPass ] *
          ( nodeDistanceSpringStrengths[ node_view_index ][ other_node_view_index ] *
            ( 1.0 -
              (
               ( nodeDistanceSpringRestLengths[ node_view_index ][ other_node_view_index ] *
                 ( delta_y * delta_y )
               ) /
               euclidean_distance_cubed
              )
            )
          )
        );
      //System.out.println( "Incremental_Change: "+incremental_change );

      if( reversed ) {
        if( other_node_partials != null ) {
          other_node_partials.xx -= incremental_change;
        }
      } else {
        partials.xx += incremental_change;
        if( other_node_partials != null ) {
          other_node_partials.xx += incremental_change;
        }
      }
      if( distance_from_touching < 0.0 ) {
        incremental_change =
          ( anticollisionSpringScalars[ layoutPass ] *
            ( anticollisionSpringStrength *
              ( 1.0 -
                (
                 ( ( node_view_radius + other_node_view_radius ) *
                   ( delta_y * delta_y )
                 ) /
                 euclidean_distance_cubed
                )
              )
            )
          );
        if( reversed ) {
          if( other_node_partials != null ) {
            other_node_partials.xx -= incremental_change;
          }
        } else {
          partials.xx += incremental_change;
          if( other_node_partials != null ) {
            other_node_partials.xx += incremental_change;
          }
        }
      }
      incremental_change =
        ( nodeDistanceSpringScalars[ layoutPass ] *
          ( nodeDistanceSpringStrengths[ node_view_index ][ other_node_view_index ] *
            ( 1.0 -
              (
               ( nodeDistanceSpringRestLengths[ node_view_index ][ other_node_view_index ] *
                 ( delta_x * delta_x )
               ) /
               euclidean_distance_cubed
              )
            )
          )
        );

      //System.out.println( "Incremental_Change: "+incremental_change );

      if( reversed ) {
        if( other_node_partials != null ) {
          other_node_partials.yy -= incremental_change;
        }
      } else {
        partials.yy += incremental_change;
        if( other_node_partials != null ) {
          other_node_partials.yy += incremental_change;
        }
      }
      if( distance_from_touching < 0.0 ) {
        incremental_change =
          ( anticollisionSpringScalars[ layoutPass ] *
            ( anticollisionSpringStrength *
              ( 1.0 -
                (
                 ( ( node_view_radius + other_node_view_radius ) *
                   ( delta_x * delta_x )
                 ) /
                 euclidean_distance_cubed
                )
              )
            )
          );
        if( reversed ) {
          if( other_node_partials != null ) {
            other_node_partials.yy -= incremental_change;
          }
        } else {
          partials.yy += incremental_change;
          if( other_node_partials != null ) {
            other_node_partials.yy += incremental_change;
          }
        }
      }
      incremental_change =
        ( nodeDistanceSpringScalars[ layoutPass ] *
          ( nodeDistanceSpringStrengths[ node_view_index ][ other_node_view_index ] *
            ( ( nodeDistanceSpringRestLengths[ node_view_index ][ other_node_view_index ] *
                ( delta_x * delta_y )
              ) /
              euclidean_distance_cubed
            )
          )
        );

      //System.out.println( "Incremental_Change: "+incremental_change );

      if( reversed ) {
        if( other_node_partials != null ) {
          other_node_partials.xy -= incremental_change;
        }
      } else {
        partials.xy += incremental_change;
        if( other_node_partials != null ) {
          other_node_partials.xy += incremental_change;
        }
      }
      if( distance_from_touching < 0.0 ) {
        incremental_change =
          ( anticollisionSpringScalars[ layoutPass ] *
            ( anticollisionSpringStrength *
              (
               ( ( node_view_radius + other_node_view_radius ) *
                 ( delta_x * delta_y )
               ) /
               euclidean_distance_cubed
              )
            )
          );
        if( reversed ) {
          if( other_node_partials != null ) {
            other_node_partials.xy -= incremental_change;
          }
        } else {
          partials.xy += incremental_change;
          if( other_node_partials != null ) {
            other_node_partials.xy += incremental_change;
          }
        }
      }

      distance_from_rest =
        ( euclidean_distance -
          nodeDistanceSpringRestLengths[ node_view_index ][ other_node_view_index ]
        );
      incremental_change =
        ( nodeDistanceSpringScalars[ layoutPass ] *
          ( ( nodeDistanceSpringStrengths[ node_view_index ][ other_node_view_index ] *
              ( distance_from_rest * distance_from_rest )
            ) /
            2
          )
        );

      //System.out.println( "Distance_From_Rest: "+distance_from_rest+" Incremental_Change: "+incremental_change );

      if( reversed ) {
        if( other_node_partials != null ) {
          potential_energy.totalEnergy -= incremental_change;
        }
      } else {
        potential_energy.totalEnergy += incremental_change;
        if( other_node_partials != null ) {
          potential_energy.totalEnergy += incremental_change;
        }
      }
      if( distance_from_touching < 0.0 ) {
        incremental_change =
          ( anticollisionSpringScalars[ layoutPass ] *
            ( ( anticollisionSpringStrength *
                ( distance_from_touching * distance_from_touching )
              ) /
              2
            )
          );
        if( reversed ) {
          if( other_node_partials != null ) {
            potential_energy.totalEnergy -= incremental_change;
          }
        } else {
          potential_energy.totalEnergy += incremental_change;
          if( other_node_partials != null ) {
            potential_energy.totalEnergy += incremental_change;
          }
        }
      }
      if( other_node_partials != null ) {
        other_node_partials.euclideanDistance =
          Math.sqrt( ( other_node_partials.x * other_node_partials.x ) +
                     ( other_node_partials.y * other_node_partials.y ) );
        if( ( furthest_partials == null ) ||
            ( other_node_partials.euclideanDistance >
              furthest_partials.euclideanDistance )
          ) {
          furthest_partials = other_node_partials;
        }
      }

    }

    if( !reversed ) {
      partials.euclideanDistance =
        Math.sqrt( ( partials.x * partials.x ) +
                   ( partials.y * partials.y ) );
    }

    if( ( furthest_partials == null ) ||
        ( partials.euclideanDistance >
          furthest_partials.euclideanDistance )
        ) {
      furthest_partials = partials;
    }

    //System.out.println( "Furthest_Partials: "+furthest_partials );

    return furthest_partials;
  } // calculatePartials( PartialDerivatives, List, PotentialEnergy, boolean )

  // Move the node with the given partials and adjust all partials in the given
  // List to reflect that move, and adjust the potential energy too.
  // @return the PartialDerivatives of the furthest node after the move.
  protected PartialDerivatives moveNode (
    PartialDerivatives partials,
    List partials_list,
    PotentialEnergy potential_energy
  ) {
    NodeView node_view = partials.getNodeView();

    PartialDerivatives starting_partials = new PartialDerivatives( partials );
    calculatePartials(
      partials,
      partials_list,
      potential_energy,
      true
    );
    simpleMoveNode( starting_partials );
    return
      calculatePartials(
        partials,
        partials_list,
        potential_energy,
        false
      );
  } // moveNode( PartialDerivatives, List, PotentialEnergy )

  protected void simpleMoveNode (
    PartialDerivatives partials
  ) {
    NodeView node_view = partials.getNodeView();
    double denomenator =
      ( ( partials.xx * partials.yy ) -
        ( partials.xy * partials.xy ) );
    double delta_x =
      (
       ( ( -partials.x * partials.yy ) -
         ( -partials.y * partials.xy ) ) /
       denomenator
      );
    double delta_y =
      (
       ( ( -partials.y * partials.xx ) -
         ( -partials.x * partials.xy ) ) /
       denomenator
      );

    // REMOVE
    //System.out.println( "moving node \"" + node_view + "\" to ( " + ( node_view.getXPosition() + delta_x ) + ", " + ( node_view.getYPosition() + delta_y ) + " )." );

    // TODO: figure out movement
    //node_view.setXPosition(
    //  node_view.getXPosition() + delta_x
    //);
    //node_view.setYPosition(
    //  node_view.getYPosition() + delta_y
    //);

    Point2D p = node_view.getOffset();
    node_view.setOffset( p.getX() + delta_x, p.getY() + delta_y );

  } // simpleMoveNode( PartialDerivatives )



*/

}
