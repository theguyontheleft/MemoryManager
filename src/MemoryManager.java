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
    // private byte array[]; // TODO: remove
    private int currentPos;

    // Static reference to the buffer pool
    private static BufferPool bufferPool_;

    /**
     * Reference to the freeList
     */
    AList<MemoryBlock> freeList_;

    // /**
    // * TODO: remove un parametized constructor
    // */
    // public MemoryManager()
    // {
    // array = new byte[10000];
    // currentPos = 0;
    // freeList_ = new AList<MemoryBlock>( 0 );
    // }

    /**
     * @param bufferPool
     * 
     *            The parametized constructor receives a bufferPool
     */
    public MemoryManager( BufferPool bufferPool )
    {
        currentPos = 0;
        bufferPool_ = bufferPool;

        // array = new byte[1000];

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
            bufferPool_.insert( newData, newData.length, currentPos );
            // handleLocation = currentPos;
            // for ( int i = 0; i < newData.length; i++, currentPos++ )
            // {
            // array[currentPos] = newData[i];
            // }
        }
        else
        {
            // There was a spot in the free list TODO
            freeList_.moveToPos( freeListPosition );

            MemoryBlock freeMemoryBlock = freeList_.getValue();
            int position = freeMemoryBlock.getStartPosition();

            handleLocation = position;
            bufferPool_.insert( newData, newData.length, position );

            // handleLocation = position;
            // for ( int i = 0; i < newData.length; i++, position++ )
            // {
            // // bufferPool_.o TODO
            // array[position] = newData[i];
            // }

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

        if ( !isNode )
        {
            byte[] size = new byte[2];
            bufferPool_.getbytes( size, size.length, position );

            int sizeOfMessage = ByteBuffer.wrap( size ).getShort();

            toReturn = new byte[sizeOfMessage];
            bufferPool_.getbytes( toReturn, sizeOfMessage, position + 2 );
        }
        else
        {
            byte[] nodeType = new byte[1];

            bufferPool_.getbytes( nodeType, 1, position );

            if ( nodeType[0] == 0 )
            {
                toReturn = new byte[9];

                bufferPool_.getbytes( toReturn, toReturn.length, position );
            }
            // return leaf node
            else if ( nodeType[0] == 1 )
            {
                toReturn = new byte[5];

                bufferPool_.getbytes( toReturn, toReturn.length, position );
            }
        }

        return toReturn;
    }

    /**
     * Delete's the memory with the passed handle
     * 
     * @param handle
     * @param isNode
     */
    public void delete( byte[] handle, boolean isNode )
    {
        // initial handle position
        int position = ByteBuffer.wrap( handle ).getInt();

        // space freed in memory
        int freedSpace = 0;

        if ( !isNode )
        {
            // delete a watcher
            byte[] size = new byte[2];
            bufferPool_.getbytes( size, size.length, position );

            int sizeOfMessage = ByteBuffer.wrap( size ).getShort();

            freedSpace = sizeOfMessage + 2;

        }
        else
        {

            byte[] nodeType = new byte[1];

            bufferPool_.getbytes( nodeType, 1, position );

            if ( nodeType[0] == 0 )
            {
                freedSpace = 9;
            }
            // return leaf node
            else if ( nodeType[0] == 1 )
            {
                freedSpace = 5;
            }
        }

        // Update the freedSpace
        addNewlyFreedSpace( freedSpace, position );
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
        merge();
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
            freeList_.getValue().setStartPosition(
                    memoryBlockUsed.getStartPosition() + lengthOfFreeSpaceUsed );
        }
        else
        // Case 2: remove memory from freelist because it's full
        {
            freeList_.remove();
        }
    }

    /**
     * 
     * Updates internal nodes handles while traversing up and down the tree
     * 
     * @param handle
     * @param dataToUpdate
     */
    public void update( byte[] handle, byte[] dataToUpdate )
    {
        int position = ByteBuffer.wrap( handle ).getInt();

        bufferPool_.insert( dataToUpdate, dataToUpdate.length, position );
    }

    /**
     * The following functions merges two sections of the freelist arraylist if
     * they are adjacent
     * 
     * @param positionToCompare
     * @param secondPosition
     */
    public void merge()
    {
        freeList_.moveToStart();

        int startCompare = freeList_.getValue().getStartPosition();
        int endCompare = freeList_.getValue().getEndPosition();

        for ( int i = 1; i < freeList_.length(); i++ )
        {
            freeList_.moveToPos( i );

            // extends free block to the left
            if ( startCompare == freeList_.getValue().getEndPosition() )
            {
                // remove old block
                int newStart = freeList_.getValue().getStartPosition();
                int addLength = freeList_.getValue().getLength();
                freeList_.remove();
                i--;

                // extend block to the left
                freeList_.moveToStart();
                freeList_.getValue().setStartPosition( newStart );
                freeList_.getValue().setLength(
                        freeList_.getValue().getLength() + addLength );
                startCompare = freeList_.getValue().getStartPosition();

                // returns to next item in the list
                freeList_.moveToPos( i );
            }
            // extends free block to the right
            if ( endCompare == freeList_.getValue().getStartPosition() )
            {
                // remove old block
                int addLength = freeList_.getValue().getLength();
                freeList_.remove();
                i--;

                // extend block to the right
                freeList_.moveToStart();
                freeList_.getValue().setLength(
                        freeList_.getValue().getLength() + addLength );
            }
        }
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