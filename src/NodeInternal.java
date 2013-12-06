/**
 * Node that contains only two pointer
 * 
 * @author Matthew Luckam mcl209
 * @author James Dagres jdagres
 * @version Oct 12, 2013
 */
@SuppressWarnings( "all" )
public class NodeInternal implements Node
{

    /**
     * determines if the node is internal or leaf
     */
    private byte isLeaf;

    /**
     * handle to left child
     */
    private byte[] handleLeft;

    /**
     * handle to right child
     */
    private byte[] handleRight;

    /**
     * current handle
     */
    private byte[] currentHandle;

    /**
     * default constructor
     */
    public NodeInternal()
    {
        handleLeft = new byte[9];
        handleRight = new byte[9];
        currentHandle = new byte[9];
        isLeaf = 0;
    }

    /**
     * creates internal node with left, right, and current handles defined
     * 
     * @param newHandleLeft
     * @param newHandleRight
     * @param newCurrentHandle
     */
    public NodeInternal( byte[] newHandleLeft, byte[] newHandleRight,
            byte[] newCurrentHandle )
    {
        handleLeft = newHandleLeft;
        handleRight = newHandleRight;
        currentHandle = newCurrentHandle;
    }

    public NodeInternal( byte[] handles, byte[] newCurrentHandle )
    {
        isLeaf = handles[0];
        handleLeft = new byte[9];
        handleRight = new byte[9];
        currentHandle = new byte[9];

        // sets left handle
        for ( int i = 1; i < 5; i++ )
        {
            handleLeft[i - 1] = handles[i];
        }

        for ( int i = 5; i < 9; i++ )
        {
            handleRight[i - 5] = handles[i];
        }

        currentHandle = newCurrentHandle;
    }

    /**
     * parameterized constructor with two pointer
     * @param flyWeight 
     * 
     * @param leftChild
     *            pointer to left child
     * @param rightChild
     *            pointer to right child
     */
    public NodeInternal( byte[] flyWeight )
    {
        handleLeft = flyWeight;
        handleRight = flyWeight;
    }

    /**
     * gets nodes left child pointer
     * 
     * @return left child pointer
     */
    public byte[] getLeft()
    {
        return handleLeft;
    }

    /**
     * sets nodes left child pointer
     * 
     * @param left
     *            pointer to left child
     */
    public void setLeft( byte[] left )
    {
        this.handleLeft = left;
    }

    /**
     * gets nodes right pointer
     * 
     * @return nodes right pointer
     */
    public byte[] getRight()
    {
        return handleRight;
    }

    /**
     * sets nodes right pointer
     * 
     * @param right
     *            pointer to right child
     */
    public void setRight( byte[] right )
    {
        this.handleRight = right;
    }

    /**
     * determines if node is a leaf
     */
    public boolean isLeaf()
    {
        return false;
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

    @Override
    public byte[] serialize()
    {
        byte[] toReturn = new byte[9];

        toReturn[0] = isLeaf;

        int j = 0;
        int i = 0;
        for ( i = 1; j < handleLeft.length; i++, j++ )
        {
            toReturn[i] = handleLeft[j];
        }
        for ( j = 0; j < handleRight.length; i++, j++ )
        {
            toReturn[i] = handleRight[j];
        }

        return toReturn;
    }
}
