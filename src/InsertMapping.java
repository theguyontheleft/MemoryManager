/**
 * Defines the bounds of the box of the point being inserted
 * 
 * @author Matthew Luckam mcl209
 * @author James Dagres
 * @version Oct 12, 2013
 * 
 */
public class InsertMapping
{
    /**
     * upper x bound
     */
    private double upXBound;
    /**
     * upper y bound
     */
    private double upYBound;

    /**
     * lower x bound
     */
    private double lowXBound;
    /**
     * lower y bound
     */
    private double lowYBound;

    /**
     * Default constructor
     */
    public InsertMapping()
    {
        upXBound = 360.0;
        upYBound = 180.0;

        lowXBound = 0.0;
        lowYBound = 0.0;
    }

    /**
     * gets upper x bound
     * 
     * @return upper x bound
     */
    public double getUpXBound()
    {
        return upXBound;
    }

    /**
     * sets upper x bound
     * 
     * @param upXBound
     *            new upper x bound
     */
    public void setUpXBound( double upXBound )
    {
        this.upXBound = upXBound;
    }

    /**
     * gets upper y bound
     * 
     * @return upper y bound
     */
    public double getUpYBound()
    {
        return upYBound;
    }

    /**
     * sets upper y bound
     * 
     * @param upYBound
     *            new upper y bound
     */
    public void setUpYBound( double upYBound )
    {
        this.upYBound = upYBound;
    }

    /**
     * gets lower x bound
     * 
     * @return lower x bound
     */
    public double getLowXBound()
    {
        return lowXBound;
    }

    /**
     * sets lower x bound
     * 
     * @param lowXBound
     *            new lower x bound
     */
    public void setLowXBound( double lowXBound )
    {
        this.lowXBound = lowXBound;
    }

    /**
     * gets lower y bound
     * 
     * @return lower y bound
     */
    public double getLowYBound()
    {
        return lowYBound;
    }

    /**
     * sets lower y bound
     * 
     * @param lowYBound
     *            new lower y bound
     */
    public void setLowYBound( double lowYBound )
    {
        this.lowYBound = lowYBound;
    }
}
