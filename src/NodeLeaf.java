import java.awt.geom.Point2D;

/**
 * 
 * @author Matthew Luckam mcl209
 * @author James Dagres jdagres
 * @version Oct 12, 2013
 */
@SuppressWarnings( "all" )
public class NodeLeaf implements Node
{
    /**
     * key value for the node
     */
    private byte isLeaf;
    /**
     * element value for the node
     */
    private byte[] watcherRecordHandle;
    /**
     * current handle
     */
    private byte[] currentHandle;

    /**
     * Default Constructor
     */
    public NodeLeaf()
    {
        isLeaf = 1;
        watcherRecordHandle = new byte[4];
    }

    /**
     * Parameterized Constructor
     * 
     * @param k
     *            key value
     * @param e
     *            element value
     */
    public NodeLeaf( byte[] e )
    {
        isLeaf = 1;

        watcherRecordHandle = e;
    }

    public NodeLeaf( byte[] e, byte[] myCurrentHandle )
    {
        isLeaf = e[0];
        watcherRecordHandle = new byte[4];

        for ( int i = 1; i < e.length; i++ )
        {
            watcherRecordHandle[i - 1] = e[i];
        }

        currentHandle = myCurrentHandle;
    }

    /**
     * sets the nodes element value
     * 
     * @param e
     *            elements value
     */
    public void setElement( byte[] e )
    {
        watcherRecordHandle = e;
    }

    /**
     * gets nodes element
     * 
     * @return nodes element value
     */
    public byte[] getElement()
    {
        return watcherRecordHandle;
    }

    /**
     * gets the current handle
     * 
     * @return current handle
     */
    @Override
    public byte[] getCurrentHandle()
    {
        return currentHandle;
    }

    /**
     * sets the nodes current handle
     * 
     * @param currentHandle
     *            current handle
     */
    @Override
    public void setCurrentHandle( byte[] currentHandle )
    {
        this.currentHandle = currentHandle;
    }

    /**
     * Returns true because this is a leaf node
     */
    @Override
    public boolean isLeaf()
    {
        return true;
    }

    @Override
    public byte[] serialize()
    {
        byte[] toReturn = new byte[5];

        toReturn[0] = isLeaf;

        int j = 0;
        for ( int i = 1; j < watcherRecordHandle.length; i++, j++ )
        {
            toReturn[i] = watcherRecordHandle[j];
        }

        return toReturn;
    }
    

    public boolean eqaulTo( byte[] handle )
    {
        for ( int i = 0; i < handle.length; i++ )
        {
            if ( watcherRecordHandle[i] != handle[i] )
            {
                return false;
            }
        }

        return true;
    }

}
