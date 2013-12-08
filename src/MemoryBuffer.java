/**
 * @author Jimmy Dagres
 * @author Matt Luckam
 * 
 * @version Dec 6, 2013
 * 
 *          This class is used by the freelist
 */
public class MemoryBuffer
{
    int position_;
    int length_;

    /**
     * @return the position
     */
    public int getPosition()
    {
        return position_;
    }

    /**
     * @param position
     *            the position_ to set
     */
    public void setPosition( int position )
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