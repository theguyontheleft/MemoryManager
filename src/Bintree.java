import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.nio.ByteBuffer;

//is a full tree all internals must have two children
// null children must be flyweight nodes instead

//flyweight private constructor public static getInstance function
/**
 * Spatial tree that stores values in leaf nodes
 * 
 * @author Matthew Luckam
 * @author James Dagres
 * @version Oct 12, 2013
 * @param <Key>
 *            key value of the Bintree nodes
 * @param <E>
 *            value of the Bintree nodes
 */
@SuppressWarnings( "all" )
public class Bintree<Key, E>
{
    /**
     * Depth of the Bintree
     */
    private int treeDepth;

    /**
     * root value of the tree
     */
    private Node root;
    /**
     * number of nodes
     */
    private int nodeCount;

    /**
     * nodes traversed in range search
     */
    private static int nodesTraversed;

    /**
     * instance of the memory manager
     */
    private static MemoryManager memoryManager;

    private Node flyWeight;

    private byte[] flyWeightHandle;

    /**
     * Default constructor
     */
    public Bintree()
    {
        treeDepth = 0;

        memoryManager = new MemoryManager();
        // ///////////////////
        // insert for memory manager doesnt care if it is a node or data b/c its
        // always going to be a byte array
        // ////////////////////

        // insert the empty flyweight watcher record
        flyWeightHandle = new byte[4];

        flyWeightHandle = memoryManager.insert( new byte[16] ).clone();
        flyWeight = new NodeLeaf( flyWeightHandle );
        byte[] currentHandle = memoryManager.insert( flyWeight.serialize() );
        flyWeight.setCurrentHandle( currentHandle );

        root = flyWeight;

        nodeCount++;
    }

    /**
     * takes a key, value pair and inserts them into the tree
     * 
     * @param k
     *            nodes key value
     * @param val
     *            nodes value
     */
    public E insert( Key k, E val )
    {
        int tempTreeDepth = 0;
        InsertMapping insertMap = new InsertMapping();

        byte[] watcherHandle = new byte[4];

        Watcher newWatcher =
                new Watcher( ((Point2D) k).getX(), ((Point2D) k).getY(),
                        (String) val );
        watcherHandle = memoryManager.insert( newWatcher.serialize() ).clone();

        root = insertHelper( root, k, watcherHandle, tempTreeDepth, insertMap );
        memoryManager.update( root.getCurrentHandle(), root.serialize() );

        nodeCount++;

        return val;
    }

    /**
     * recursive call to insert key, value pairs into the tree
     * 
     * @param node
     *            subtrees root
     * @param k
     *            key of value to be inserted
     * @param val
     *            value to be inserted
     * @param level
     *            level of the tree the item is currently on
     * @return inserted node
     */
    private Node insertHelper( Node node, Key k, byte[] watcherHandle,
            int level, InsertMapping map )
    {
        // Base Case - leaf node with no value
        if ( node.isLeaf() && ((NodeLeaf) node).equalTo( flyWeightHandle ) )
        {
            // deletes old instance of the leaf node
            // memoryManager.delete( node.getCurrentHandle() );

            // creates new leaf node with a handle to the newly created watcher
            NodeLeaf newLeaf = new NodeLeaf( watcherHandle );
            // inserts to memory manager and returns a handle to itself
            byte[] newLeafHandle =
                    memoryManager.insert( newLeaf.serialize() ).clone();
            // sets its current handle
            newLeaf.setCurrentHandle( newLeafHandle );

            // TODO need to write this updated handle to memory manager at some
            // point

            if ( level > treeDepth )
            {
                treeDepth = level;
            }

            return newLeaf;
        }
        // leaf node with value
        else if ( node.isLeaf() && !((NodeLeaf) node).equalTo( flyWeightHandle ) )
        {
            NodeInternal newInternal =
                    new NodeInternal( flyWeight.getCurrentHandle() );

            treeDepth++;

            double tempUpXBound = map.getUpXBound();
            double tempUpYBound = map.getUpYBound();
            double tempLowXBound = map.getLowXBound();
            double tempLowYBound = map.getLowYBound();

            Watcher watcher =
                    (Watcher) deSerialize( ((NodeLeaf) node).getElement(),
                            false );
            // deletes old instance of the leaf node

            // inserts leaf node previously at this location
            insertHelper( newInternal, (Key) watcher.getWatcherPoint(),
                    ((NodeLeaf) node).getElement(), level, map );

            memoryManager.delete( node.getCurrentHandle(), true );

            map.setUpXBound( tempUpXBound );
            map.setUpYBound( tempUpYBound );
            map.setLowXBound( tempLowXBound );
            map.setLowYBound( tempLowYBound );

            // inserts new data into a leaf node
            node = insertHelper( newInternal, k, watcherHandle, level, map );

            // TODO update internal node

            byte[] internalCurrentlHandle =
                    memoryManager.insert( newInternal.serialize() );
            newInternal.setCurrentHandle( internalCurrentlHandle );
        }

        // divides on the x axis
        else if ( level % 2 == 0 )
        {
            level++;

            double xWidth = ((map.getUpXBound() - map.getLowXBound()) / 2);

            // sets node to the right
            if ( (((Point2D.Double) k).getX() + 180.0) >= xWidth
                    + map.getLowXBound() )
            {
                map.setLowXBound( xWidth + map.getLowXBound() );
                ((NodeInternal) node).setRight( insertHelper(
                        (Node) deSerialize( ((NodeInternal) node).getRight(),
                                true ), k, watcherHandle, level, map )
                        .getCurrentHandle() );
            }
            // sets node to the left
            else
            {
                map.setUpXBound( map.getUpXBound() - xWidth );
                ((NodeInternal) node).setLeft( insertHelper(
                        (Node) deSerialize( ((NodeInternal) node).getLeft(),
                                true ), k, watcherHandle, level, map )
                        .getCurrentHandle() );
            }
        }
        // divides on the y axis
        else
        {
            level++;

            double yHeight = ((map.getUpYBound() - map.getLowYBound()) / 2);

            // sets node to the right
            if ( (((Point2D.Double) k).getY() + 90.0) >= (yHeight + map
                    .getLowYBound()) )
            {
                map.setLowYBound( yHeight + map.getLowYBound() );

                ((NodeInternal) node).setRight( insertHelper(
                        (Node) deSerialize( ((NodeInternal) node).getRight(),
                                true ), k, watcherHandle, level, map )
                        .getCurrentHandle() );
            }
            // sets node to the left
            else
            {
                map.setUpYBound( map.getUpYBound() - yHeight );

                ((NodeInternal) node).setLeft( insertHelper(
                        (Node) deSerialize( ((NodeInternal) node).getLeft(),
                                true ), k, watcherHandle, level, map )
                        .getCurrentHandle() );
            }
        }
        return node;
    }

    /**
     * takes a key value and sees if its in the Bintree
     * 
     * @param k
     *            key to compare
     * @return true if it exists otherwise false
     * 
     */
    public boolean contains( Key k )
    {
        boolean toReturn = false;
        InsertMapping findMap = new InsertMapping();

        toReturn = containsHelper( root, k, 0, findMap );

        return toReturn;
    }

    /**
     * recursive call of contains
     * 
     * @param node
     *            root
     * @param k
     *            key to searh for
     * @param level
     *            tree depth
     * @param map
     *            map of current divide
     * @return true if found, false if not
     */
    private boolean containsHelper( Node node, Key k, int level,
            InsertMapping map )
    {
        // if leaf node
        if ( node.isLeaf() )
        {
            if ( !((NodeLeaf) node).equalTo( flyWeightHandle )
                    && ((Watcher) (deSerialize( ((NodeLeaf) node).getElement(),
                            false ))).getWatcherPoint().equals( k ) )
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        // divides on the x axis
        else if ( level % 2 == 0 )
        {
            level++;
            double xWidth = ((map.getUpXBound() - map.getLowXBound()) / 2);

            // sets node to the right
            if ( (((Point2D.Double) k).getX() + 180.0) >= xWidth
                    + map.getLowXBound() )
            {
                map.setLowXBound( xWidth + map.getLowXBound() );
                return containsHelper(
                        (Node) deSerialize( ((NodeInternal) node).getRight(),
                                true ), k, level, map );
            }
            // gets node to the left
            else
            {
                map.setUpXBound( map.getUpXBound() - xWidth );
                return containsHelper(
                        (Node) deSerialize( ((NodeInternal) node).getLeft(),
                                true ), k, level, map );
            }
        }
        // divides on the y axis
        else
        {
            level++;
            double yHeight = ((map.getUpYBound() - map.getLowYBound()) / 2);

            // sets node to the right
            if ( (((Point2D.Double) k).getY() + 90.0) >= (yHeight + map
                    .getLowYBound()) )
            {
                map.setLowYBound( yHeight + map.getLowYBound() );
                return containsHelper(
                        (Node) deSerialize( ((NodeInternal) node).getRight(),
                                true ), k, level, map );
            }
            // gets node to the left
            else
            {
                map.setUpYBound( map.getUpYBound() - yHeight );
                return containsHelper(
                        (Node) deSerialize( ((NodeInternal) node).getLeft(),
                                true ), k, level, map );
            }
        }

    }

    /**
     * Given a key value it removes a record
     * 
     * @param k
     *            key value to remove
     */
    public void remove( Key k )
    {
        int tempTreeDepth = 0;
        // NodeLeaf toRemove = (NodeLeaf) find( k );
        root = removeHelper( root, k, tempTreeDepth, new InsertMapping() );
        memoryManager.update( root.getCurrentHandle(), root.serialize() );
        nodeCount--;
    }

    private Node removeHelper( Node node, Key k, int level, InsertMapping map )
    {
        // Base Case
        if ( node.isLeaf() )
        {
            // leaf node with no value
            if ( ((NodeLeaf) node).equalTo( flyWeightHandle )
                    || !((Watcher) (deSerialize(
                            ((NodeLeaf) node).getElement(), false )))
                            .getWatcherPoint().equals( k ) )
            {
                return node;
            }

            // leaf node with value
            else if ( !((NodeLeaf) node).equalTo( flyWeightHandle )
                    && ((Watcher) (deSerialize( ((NodeLeaf) node).getElement(),
                            false ))).getWatcherPoint().equals( k ) )
            {
                // deletes the watcher
                memoryManager.delete( ((NodeLeaf) node).getElement(), false );
                // deletes the node
                memoryManager.delete( ((NodeLeaf) node).getCurrentHandle(),
                        true );
                // inserts the flyweight
                // memoryManager.insert( flyWeight.serialize() );

                return flyWeight;
            }
        }

        // divides on the x axis
        else if ( level % 2 == 0 )
        {
            level++;

            double xWidth = ((map.getUpXBound() - map.getLowXBound()) / 2);

            // sets node to the right
            if ( (((Point2D.Double) k).getX() + 180.0) >= xWidth
                    + map.getLowXBound() )
            {
                map.setLowXBound( xWidth + map.getLowXBound() );
                ((NodeInternal) node).setRight( removeHelper(
                        (Node) deSerialize( ((NodeInternal) node).getRight(),
                                true ), k, level, map ).getCurrentHandle() );
            }
            // sets node to the left
            else
            {
                map.setUpXBound( map.getUpXBound() - xWidth );
                ((NodeInternal) node).setLeft( removeHelper(
                        (Node) deSerialize( ((NodeInternal) node).getLeft(),
                                true ), k, level, map ).getCurrentHandle() );
            }
        }
        // divides on the y axis
        else
        {
            level++;

            double yHeight = ((map.getUpYBound() - map.getLowYBound()) / 2);

            // sets node to the right
            if ( (((Point2D.Double) k).getY() + 90.0) >= (yHeight + map
                    .getLowYBound()) )
            {
                map.setLowYBound( yHeight + map.getLowYBound() );

                ((NodeInternal) node).setRight( removeHelper(
                        (Node) deSerialize( ((NodeInternal) node).getRight(),
                                true ), k, level, map ).getCurrentHandle() );
            }
            // sets node to the left
            else
            {
                map.setUpYBound( map.getUpYBound() - yHeight );

                ((NodeInternal) node).setLeft( removeHelper(
                        (Node) deSerialize( ((NodeInternal) node).getLeft(),
                                true ), k, level, map ).getCurrentHandle() );
            }
        }

        // after leaf is found and removed
        if ( ((Node) deSerialize( (((NodeInternal) node).getRight()), true ))
                .isLeaf()
                && ((Node) deSerialize( (((NodeInternal) node).getLeft()), true ))
                        .isLeaf() )
        {

            // right and left child are empty replace internal node with a
            // empty leaf
            if ( ((NodeLeaf) deSerialize( ((NodeInternal) node).getRight(),
                    true )).equalTo( flyWeightHandle )
                    && ((NodeLeaf) deSerialize(
                            ((NodeInternal) node).getLeft(), true ))
                            .equalTo( flyWeightHandle ) )
            {
                // deletes the right child
                memoryManager.delete(
                        ((NodeLeaf) deSerialize(
                                ((NodeInternal) node).getRight(), true ))
                                .getCurrentHandle(), true );
                // deletes the left child
                memoryManager.delete(
                        ((NodeLeaf) deSerialize(
                                ((NodeInternal) node).getLeft(), true ))
                                .getCurrentHandle(), true );
                // deletes the internal
                memoryManager.delete( ((NodeInternal) node).getCurrentHandle(),
                        true );

                // writes new flyweight
                memoryManager.insert( flyWeight.serialize() );

                return flyWeight;
            }

            // right child is empty and left child is a point
            else if ( ((NodeLeaf) deSerialize(
                    ((NodeInternal) node).getRight(), true ))
                    .equalTo( flyWeightHandle )
                    && !((NodeLeaf) deSerialize(
                            ((NodeInternal) node).getLeft(), true ))
                            .equalTo( flyWeightHandle ) )
            {
                // deletes the internal
                memoryManager.delete( ((NodeInternal) node).getCurrentHandle(),
                        true );
                return (NodeLeaf) deSerialize( ((NodeInternal) node).getLeft(),
                        true );
            }

            // right child is a point and left child is null
            else if ( !((NodeLeaf) deSerialize(
                    ((NodeInternal) node).getRight(), true ))
                    .equalTo( flyWeightHandle )
                    && ((NodeLeaf) deSerialize(
                            ((NodeInternal) node).getLeft(), true ))
                            .equalTo( flyWeightHandle ) )
            {
                // deletes the internal
                memoryManager.delete( ((NodeInternal) node).getCurrentHandle(),
                        true );

                return (NodeLeaf) deSerialize(
                        ((NodeInternal) node).getRight(), true );
            }
            // right and left children contain values
            else
            {
                return node;
            }
        }
        return node;
    }

    /**
     * searches the bintree for values in a given region
     * 
     * @param k
     *            point of region
     * @param radius
     *            radius of area
     * 
     */
    public void rangeSearch( Key k, double radius )
    {
        // bounds of the entire map
        Rectangle2D.Double mapBounds = new Rectangle2D.Double();
        mapBounds.setRect( -180.0, -90.0, 360.0, 180.0 );

        // bounds of the item to find
        Rectangle2D.Double eqBounds = new Rectangle2D.Double();
        eqBounds.setRect( ((Point2D.Double) k).getX() - radius,
                ((Point2D.Double) k).getY() - radius, 2 * radius, 2 * radius );

        nodesTraversed = 0;
        rangeSearchHelper( root, mapBounds, eqBounds, 0, k, radius );

        System.out.println( "Watcher search caused " + nodesTraversed
                + " bintree nodes to be visited." );

    }

    /**
     * recursive call of rangeSearch
     * 
     * @param node
     *            root
     * @param map
     *            bounds of search area
     * @param eq
     *            bounds of items to find
     * @param level
     *            tree depth
     * @param k
     *            value containing point of origin
     * @param radius
     *            radius of the earthquake
     */
    private void rangeSearchHelper( Node node, Rectangle2D.Double map,
            Rectangle2D.Double eq, int level, Key k, double radius )
    {
        nodesTraversed++;
        // flyweight
        if ( node.isLeaf() && ((NodeLeaf) node).equalTo( flyWeightHandle ) )
        {
            return;
        }

        // node in rectangle
        else if ( node.isLeaf()
                && eq.intersects( map )
                && eq.contains(
                        ((Watcher) deSerialize( ((NodeLeaf) node).getElement(),
                                false )).getWatcherPoint().getX(),
                        ((Watcher) deSerialize( ((NodeLeaf) node).getElement(),
                                false )).getWatcherPoint().getY() ) )
        {
            double x =
                    Math.pow(
                            (double) ((Point2D.Double) k).getX()
                                    - ((Watcher) deSerialize(
                                            ((NodeLeaf) node).getElement(),
                                            false )).getWatcherPoint().getX(),
                            2 );

            double y =
                    Math.pow(
                            (double) ((Point2D.Double) k).getY()
                                    - ((Watcher) deSerialize(
                                            ((NodeLeaf) node).getElement(),
                                            false )).getWatcherPoint().getY(),
                            2 );

            double distance = Math.sqrt( x + y );

            // node in circle
            if ( distance < radius )
            {

                System.out.println( ((NodeLeaf) node).getElement()
                        + " "
                        + ((Watcher) deSerialize(
                                ((NodeLeaf) node).getElement(), false ))
                                .getWatcherPoint().getX()
                        + " "
                        + ((Watcher) deSerialize(
                                ((NodeLeaf) node).getElement(), false ))
                                .getWatcherPoint().getY() );
            }
        }

        // x divide
        else if ( !node.isLeaf() && level % 2 == 0 )
        {
            level++;

            map.width = map.width / 2;

            Rectangle2D.Double rightBound = new Rectangle2D.Double();
            Rectangle2D.Double leftBound = new Rectangle2D.Double();
            rightBound = (Double) map.clone();
            leftBound = (Double) map.clone();

            rightBound.x += rightBound.width;

            // gets left
            if ( eq.intersects( leftBound ) )
            {
                rangeSearchHelper(
                        (Node) deSerialize( ((NodeInternal) node).getLeft(),
                                true ), leftBound, eq, level, k, radius );
            }

            // gets to the right
            if ( eq.intersects( rightBound ) )
            {

                rangeSearchHelper(
                        (Node) deSerialize( ((NodeInternal) node).getRight(),
                                true ), rightBound, eq, level, k, radius );
            }
        }

        // y divide
        else if ( !node.isLeaf() && level % 2 == 1 )
        {
            level++;

            map.height = map.height / 2;

            Rectangle2D.Double rightBound = new Rectangle2D.Double();
            Rectangle2D.Double leftBound = new Rectangle2D.Double();
            rightBound = (Double) map.clone();
            leftBound = (Double) map.clone();

            rightBound.y += rightBound.height;

            // gets to the left
            if ( eq.intersects( leftBound ) )
            {
                rangeSearchHelper(
                        (Node) deSerialize( ((NodeInternal) node).getLeft(),
                                true ), leftBound, eq, level, k, radius );
            }

            // gets to the right
            if ( eq.intersects( rightBound ) )
            {
                rangeSearchHelper(
                        (Node) deSerialize( ((NodeInternal) node).getRight(),
                                true ), rightBound, eq, level, k, radius );
            }

        }
    }

    private Object deSerialize( byte[] handle, boolean isNode )
    {
        Object toReturn = null;

        byte[] newObject = memoryManager.getObject( handle, isNode ).clone();

        // flyweight
        if ( handle.equals( flyWeightHandle ) )
        {
            toReturn = flyWeight;
        }
        // create internal node
        else if ( newObject[0] == 0 && isNode )
        {
            byte[] handleLeft = new byte[4];
            byte[] handleRight = new byte[4];

            // sets left handle
            for ( int i = 1; i < 5; i++ )
            {
                handleLeft[i - 1] = newObject[i];
            }

            for ( int i = 5; i < 9; i++ )
            {
                handleRight[i - 5] = newObject[i];
            }

            toReturn = new NodeInternal( handleLeft, handleRight, handle );

        }
        // creates leaf node
        else if ( newObject[0] == 1 && isNode )
        {
            // creates a node from the information in the memory manager
            byte[] watcherRecordHandle = new byte[4];
            for ( int i = 1; i < 5; i++ )
            {
                watcherRecordHandle[i - 1] = newObject[i];
            }

            toReturn = new NodeLeaf( watcherRecordHandle, handle );
        }
        // watcher object
        else
        {
            // variables to create a watcher
            double x = 0.0;
            double y = 0.0;
            String name = null;

            byte[] xByte = new byte[8];
            byte[] yByte = new byte[8];
            byte[] wName = new byte[newObject.length - 16];

            // gets values from object
            for ( int i = 0; i < newObject.length; i++ )
            {
                if ( i < 8 )
                {
                    xByte[i] = newObject[i];
                }
                else if ( i >= 8 && i < 16 )
                {
                    yByte[i - 8] = newObject[i];
                }
                else
                {
                    wName[i - 16] = newObject[i];
                }
            }

            // convert byte arrays
            x = ByteBuffer.wrap( xByte ).getDouble();
            y = ByteBuffer.wrap( yByte ).getDouble();
            name = new String( wName );

            // create watcher
            toReturn = new Watcher( x, y, name );

        }
        return toReturn;
    }

    //
    // /**
    // * prints preorder traversal of tree to the command line
    // */
    // public void print()
    // {
    // printHelper( root );
    // }
    //
    // /**
    // * recursive call of print
    // *
    // * @param node
    // * root
    // */
    // private void printHelper( Node node )
    // {
    // printVisit( node );
    //
    // if ( !node.isLeaf() )
    // {
    // printHelper( ((NodeInternal) node).getLeft() );
    // printHelper( ((NodeInternal) node).getRight() );
    // }
    // }
    //
    // /**
    // * prints out type of node
    // *
    // * @param node
    // * current node
    // */
    // private void printVisit( Node node )
    // {
    // // empty leaf
    // if ( node.isLeaf() && ((NodeLeaf) node).getElement() == null )
    // {
    // System.out.println( "E" );
    // }
    // // leaf with value
    // else if ( node.isLeaf() && ((NodeLeaf) node).getElement() != null )
    // {
    // System.out.println( ((NodeLeaf) node).getElement() + " "
    // + ((Point2D.Double) ((NodeLeaf) node).getKey()).getX()
    // + " "
    // + ((Point2D.Double) ((NodeLeaf) node).getKey()).getY() );
    // }
    // // internal node
    // else
    // {
    // System.out.println( "I" );
    // }
    //
    // }
    //
    // /**
    // * returns depth of the Bintree
    // *
    // * @return depth of the Bintree
    // */
    // public int treeDepth()
    // {
    // return treeDepth;
    // }
    //
}
