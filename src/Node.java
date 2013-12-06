/**
 * Node class
 * 
 * @author Matthew Luckam mcl209
 * @author James Dagres jdagres
 * @version Oct 12, 2013
 */
public interface Node
{
    /**
     * determines if node is a leaf
     * 
     * @return boolean true if leaf, false otherwise
     */
    public boolean isLeaf();

    /**
     * gets the current handle
     * 
     * @return current handle
     */
    public byte[] getCurrentHandle();

    /**
     * sets the nodes current handle
     * 
     * @param currentHandle
     *            current handle
     */
    public void setCurrentHandle( byte[] currentHandle );

    /**
     * converts the data in the node to a byte array
     * 
     * @return byte[]
     * 
     */
    public byte[] serialize();
}
