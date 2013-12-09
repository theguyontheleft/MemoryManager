/**
 * @author Jimmy Dagres
 * @author Matt Luckam
 * 
 * @version Dec 6, 2013
 * 
 *          This class is used by the freelist
 */
public class MemoryBlock
{
    int position_;
    private int length_;

    /**
     * Initializes the memory block of the free list with the necessary
     * information
     * 
     * @param position
     * @param length
     */
    public MemoryBlock( int position, int length )
    {
        position_ = position;
        length_ = length;
    }

    /** 
     * @return the end position
     */
    public int getEndPosition()
    {
        return position_ + length_;
    }

    /**
     * @return the position
     */
    public int getStartPosition()
    {
        return position_;
    }

    /**
     * @param position
     *            the position_ to set
     */
    public void setStartPosition( int position )
    {
        this.position_ = position;
    }

    /**
     * @return the length_
     */
    public int getLength()
    {
        return length_;
    }

    /**
     * @param length
     *            the length_ to set
     */
    public void setLength( int length )
    {
        this.length_ = length;
    }
}