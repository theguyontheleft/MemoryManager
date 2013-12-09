/**
 * Buffer object that contains a byte array
 * 
 * @author Matthew Luckam
 * @author James Dagres
 * @version Nov 5, 2013
 * 
 */
public class Buffer
{
    /**
     * the number of the block
     */
    private int blockNumberFile;

    /**
     * contains a block of the file
     */

    private byte[] block;

    /**
     * determines if the block has been modified
     */
    private boolean dirtyBit;

    /**
     * @param newBlock
     * @param newBlockNumber
     */
    public Buffer( byte[] newBlock, int newBlockNumber )
    {
        block = newBlock;
        blockNumberFile = newBlockNumber;
        dirtyBit = false;
    }

    /**
     * returns if the block has been modified
     * 
     * @return true if changed and false if not
     */
    public boolean isDirtyBit()
    {
        return dirtyBit;
    }

    /**
     * set to true
     * @param bool 
     */
    public void setDirtyBit( boolean bool )
    {
        dirtyBit = bool;
    }

    /**
     * gives block number of buffer
     * 
     * @return block number
     */
    public int getBlockNumberFile()
    {
        return blockNumberFile;
    }

    /**
     * sets the block number
     * 
     * @param blockNumber
     *            blocks number
     */
    public void setBlockNumberFile( int blockNumber )
    {
        this.blockNumberFile = blockNumber;
    }

    /**
     * gets the block
     * 
     * @return block that is a byte array
     */
    public byte[] getBlock()
    {
        return block;
    }

    /**
     * sets the block
     * 
     * @param block
     *            byte array
     */
    public void setBlock( byte[] block )
    {
        this.block = block;
    }

}
