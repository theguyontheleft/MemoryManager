/** Source code example for "A Practical Introduction to Data
 *Structures and Algorithm Analysis, 3rd Edition (Java)" 
 *by Clifford A. Shaffer
 *Copyright 2008-2011 by Clifford A. Shaffer
 */

/**
 * Array-based list implementation
 * 
 * @author Matthew Luckam
 * @author James Dagres
 * @version Nov 5, 2013
 * 
 * @param <E>
 *            parameter for the list
 */
class AList<E> implements List<E>
{
    /**
     * Maximum size of list
     */
    private int maxSize;
    /**
     * Current # of list items
     */
    private int listSize;
    /**
     * The size of the block
     */
    private int blockSize;
    /**
     * Position of current element
     */
    private int curr;
    /**
     * Array holding list elements
     */
    private E[] listArray;

    /**
     * Parameterized constructor
     * 
     * @param maxNumberOfBlocks
     *            Max # of elements list can contain.
     * @param newBlockSize
     */
    @SuppressWarnings( "unchecked" )
    public AList( int maxNumberOfBlocks, int newBlockSize )
    {
        blockSize = newBlockSize;
        maxSize = maxNumberOfBlocks * newBlockSize;
        listSize = curr = 0;
        listArray = (E[]) new Object[maxSize]; // Create listArray
    }

    /**
     * Reinitialize the list
     */
    public void clear()
    {
        listSize = curr = 0;
    }

    /**
     * Insert "it" at front position
     * 
     * @param it
     *            item being inserted
     */
    public void insert( E it )
    {
        if ( listSize == maxSize )
        {
            if ( maxSize == 0 )
            {
                maxSize = blockSize;
            }
            grow();
        }

        moveToStart();
        for ( int i = listSize; i > curr; i-- )
        {
            // Shift elements up to make room
            listArray[i] = listArray[i - 1];
        }

        moveToStart();
        listArray[curr] = it;
        // Increment list size
        listSize++;
    }

    /**
     * When item is used it is moved to the front of the array
     * 
     * @param itPos
     *            item being used
     * @return item at the given location
     */
    public E itemUsed( int itPos )
    {
        E toReturn = listArray[itPos];

        for ( int i = itPos; i > 0; i-- )
        {
            // Shift elements up to make room
            listArray[i] = listArray[i - 1];
        }

        moveToStart();
        listArray[curr] = toReturn;

        return toReturn;
    }

    /**
     * Append "it" to list
     * 
     * @param it
     *            item to append
     */
    public void append( E it )
    {
        listArray[listSize++] = it;
    }

    /**
     * Remove and return the rear element
     * 
     * @return the rear element
     */
    public E remove()
    {
        // No current element
        if ( (curr < 0) || (curr >= listSize) )
        {
            return null;
        }
        // Copy the element
        E it = listArray[curr];
        for ( int i = curr; i < listSize - 1; i++ )
        {
            // Shift them down
            listArray[i] = listArray[i + 1];
        }
        // Decrement size
        listSize--;
        return it;
    }

    /**
     * Sets curr to front
     */
    public void moveToStart()
    {
        curr = 0;
    }

    /**
     * sets curr to end
     */
    public void moveToEnd()
    {
        curr = listSize - 1;
    }

    /**
     * sets curr to previous
     */
    public void prev()
    {
        if ( curr != 0 )
        {
            curr--;
        }
    }

    /**
     * sets curr to next
     */
    public void next()
    {
        if ( curr < listSize )
        {
            curr++;
        }
    }

    /**
     * gets the list length
     * 
     * @return List size
     */
    public int length()
    {
        return listSize;
    }

    /**
     * gets the current position
     * 
     * @return Current position
     */
    public int currPos()
    {
        return curr;
    }

    /**
     * Set current list position to "pos"
     * 
     * @param pos
     *            position to move to
     */
    public void moveToPos( int pos )
    {
        curr = pos;
    }

    /**
     * gets the value of the current element
     * 
     * @return Current element
     */
    public E getValue()
    {
        return listArray[curr];
    }

    @SuppressWarnings( "unchecked" )
    private void grow()
    {
        // TODO: may need to grow by more than one block size
        maxSize += blockSize;
        E[] temp = (E[]) new Object[maxSize];
        for ( int i = 0; i < listArray.length; i++ )
        {
            temp[i] = listArray[i];
        }

        listArray = temp.clone();

    }
}
