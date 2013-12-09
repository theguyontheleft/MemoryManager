import java.nio.ByteBuffer;

/**
 * @author Jimmy Dagres
 * @author Matt Luckam
 * 
 * @version Dec 6, 2013
 * 
 */
public class MemoryManager
{
    private byte array[]; // TODO: remove
    private int currentPos;

    // Static reference to the buffer pool
    private static BufferPool bufferPool_;

    /**
     * Reference to the freeList
     */
    AList<MemoryBlock> freeList_;

    /**
     * TODO: remove un parametized constructor
     */
    public MemoryManager()
    {
        array = new byte[10000];
        currentPos = 0;
        freeList_ = new AList<MemoryBlock>( 0 );
    }

    /**
     * @param bufferPool
     * 
     *            The parametized constructor receives a bufferPool
     */
    public MemoryManager( BufferPool bufferPool )
    {
        currentPos = 0;
        bufferPool_ = bufferPool;

        array = new byte[1000];

        freeList_ = new AList<MemoryBlock>( 0 );
    }

    /**
     * @param newData
     * @return the byte array inserted
     */
    public byte[] insert( byte[] newData )
    {
        int handleLocation = -1;

        // adds 2 byte length of message to a watcher
        if ( newData.length > 9 )
        {
            byte[] messageLength =
                    ByteBuffer.allocate( 2 ).putShort( (short) newData.length )
                            .array();

            byte[] temp = new byte[messageLength.length + newData.length];
            for ( int i = 0; i < messageLength.length; i++ )
            {
                temp[i] = messageLength[i];
            }
            for ( int i = 0; i < newData.length; i++ )
            {
                temp[i + 2] = newData[i];
            }
            newData = temp.clone();
        }

        // Look through the freelist to find free space in the bufferpool
        int freeListPosition = findFreeSpace( newData.length );
        if ( -1 == freeListPosition )
        {
            handleLocation = currentPos;
            for ( int i = 0; i < newData.length; i++, currentPos++ )
            {
                array[currentPos] = newData[i];
            }
        }
        else
        {
            // There was a spot in the free list TODO
            freeList_.moveToPos( freeListPosition );

            MemoryBlock freeMemoryBlock = freeList_.getValue();
            int position = freeMemoryBlock.getPosition();
            handleLocation = position;
            for ( int i = 0; i < newData.length; i++, position++ )
            {
                array[position] = newData[i];
            }

            // Remove the newly used memory block from the freeList_
            removeNewlyUsedSpace( freeListPosition, newData.length );
        }

        return ByteBuffer.allocate( 4 ).putInt( handleLocation ).array();
    }

    /**
     * Message request, used by the bintree to get the content of the message
     * contained by the handle
     * 
     * @param handle
     * @param isNode
     * @return the byte of the object desired
     */
    public byte[] getObject( byte[] handle, boolean isNode )
    {
        byte[] toReturn = null;

        int position = ByteBuffer.wrap( handle ).getInt();

        // return internal node
        if ( array[position] == 0 && isNode )
        {
            toReturn = new byte[9];
            for ( int i = 0; i < 9; i++, position++ )
            {
                // TODO: ask the buffer for the data at this position
                // bufferPool_.getbytes( handle, handle.length, position );

                toReturn[i] = array[position];
            }
        }
        // return leaf node
        else if ( array[position] == 1 && isNode )
        {
            toReturn = new byte[5];
            for ( int i = 0; i < 5; i++, position++ )
            {
                toReturn[i] = array[position];
            }
        }
        // return watcher
        else
        {
            byte[] size = new byte[2];

            for ( int i = 0; i < 2; i++, position++ )
            {
                size[i] = array[position];
            }

            int sizeOfMessage = ByteBuffer.wrap( size ).getShort();

            toReturn = new byte[sizeOfMessage];

            for ( int i = 0; i < sizeOfMessage; i++, position++ )
            {
                toReturn[i] = array[position];
            }
        }

        return toReturn;
    }

    /**
     * Delete's the memory with the passed handle
     * 
     * @param handle
     * @param isLeaf
     */
    public void delete( byte[] handle, boolean isLeaf )
    {
        int handleLocation = ByteBuffer.wrap( handle ).getInt();

        // space freed in memory
        int freedSpace = 0;
        // initial handle position
        int initialHandleLocation = handleLocation;

        // delete internal node
        if ( array[handleLocation] == 0 && isLeaf )
        {
            for ( int i = 0; i < 9; i++, handleLocation++ )
            {
                array[handleLocation] = 0;
            }
            freedSpace = 9;
        }
        // delete leaf node
        else if ( array[handleLocation] == 1 && isLeaf )
        {
            for ( int i = 0; i < 5; i++, handleLocation++ )
            {
                array[handleLocation] = 0;
            }
            freedSpace = 5;
        }
        // delete a watcher
        else
        {
            byte[] size = new byte[2];
            for ( int i = 0; i < 2; i++, handleLocation++ )
            {
                size[i] = array[handleLocation]; // OLD TODO
                array[handleLocation] = 0;
            }
            int sizeOfMessage = ByteBuffer.wrap( size ).getShort();

            for ( int i = 0; i < sizeOfMessage; i++, handleLocation++ )
            {
                array[handleLocation] = 0;
            }
            freedSpace = sizeOfMessage + 2;
        }

        // Update the freedSpace
        addNewlyFreedSpace( freedSpace, initialHandleLocation );
    }

    /**
     * This method adds the newly deleted/freed space to the free arraylist
     * 
     * @param lengthOfFreeSpace
     * @param handleLocation
     */
    private void
            addNewlyFreedSpace( int lengthOfFreedSpace, int handleLocation )
    {
        // Creates a memory block with the position and length
        MemoryBlock memoryBlock =
                new MemoryBlock( handleLocation, lengthOfFreedSpace );

        freeList_.insert( memoryBlock );
    }

    /**
     * When a memoryBlock in the freeList meets the requirements for a new
     * insert request this function is called to remove that memoryBlock from
     * the freeList.
     * 
     * @param positionToRemove
     * @param lengthOfFreeSpaceUsed
     */
    private void removeNewlyUsedSpace( int positionToRemove,
            int lengthOfFreeSpaceUsed )
    {
        // TODO:
        freeList_.moveToPos( positionToRemove );
        MemoryBlock memoryBlockUsed = freeList_.getValue();

        // Case 1: memory block still has free space
        if ( memoryBlockUsed.getLength() > lengthOfFreeSpaceUsed )
        {
            freeList_.getValue().setLength(
                    memoryBlockUsed.getLength() - lengthOfFreeSpaceUsed );
            freeList_.getValue().setPosition(
                    memoryBlockUsed.getPosition() + lengthOfFreeSpaceUsed );
        }
        else
        // Case 2: remove memory from freelist because it's full
        {
            freeList_.remove();
        }
    }

    /**
     * @param handle
     * @param dataToUpdate
     */
    public void update( byte[] handle, byte[] dataToUpdate )
    {
        int location = ByteBuffer.wrap( handle ).getInt();

        for ( int i = 0; i < dataToUpdate.length; i++, location++ )
        {
            array[location] = dataToUpdate[i];
        }
    }

    /**
     * The following functions merges two sections of the freelist arraylist if
     * they are adjacent
     * 
     * @param firstPosition
     * @param secondPosition
     * @return true if merged and false if not
     */
    public boolean merge( int firstPosition, int secondPosition )
    {
        // Make sure the position in inside the array
        if ( freeList_.length() > firstPosition
                || freeList_.length() > secondPosition )
        {
            // TODO:

            return true;
        }

        return false;
    }

    /**
     * Finds free space inside the free list using a circular fit.
     * 
     * @param lengthOfDataToInsert
     * @return the position of the compatible freeLst inside the freeList_ array
     */
    public int findFreeSpace( int lengthOfDataToInsert )
    {
        freeList_.moveToStart();

        // Iterate through the freelist arraylist looking for space equal or
        // greater than the length of the byteToStore
        for ( int j = 0; j < freeList_.length(); j++ )
        {
            freeList_.moveToPos( j );
            if ( freeList_.getValue().getLength() >= lengthOfDataToInsert )
            {
                return j;
            }
        }

        return -1;
    }

    public void print()
    {
        bufferPool_.print();
    }

    public void printStat()
    {
        System.out.println( "Number of cache hits: "
                + bufferPool_.getCacheHits() );
        System.out.println( "Number of cache misses: "
                + bufferPool_.getCacheMisses() );
        System.out.println( "Number of disk reads: "
                + bufferPool_.getDiskReads() );
        System.out.println( "Number of disk writes: "
                + bufferPool_.getDiskWrites() );

    }
}