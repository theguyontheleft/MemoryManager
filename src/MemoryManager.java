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

    private byte array[];
    private int currentPos;

    // Static reference to the buffer pool
    private static BufferPool bufferPool_;

    /**
     * Reference to the freeList
     */
    AList<MemoryBuffer> freeList_;

    /**
     * TODO: remove un parametized constructor
     */
    public MemoryManager()
    {
        array = new byte[1000];
        currentPos = 0;
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

        freeList_ = new AList<MemoryBuffer>( 1000 );
    }

    /**
     * @param newData
     * @return the byte array inserted
     */
    public byte[] insert( byte[] newData )
    {
        Integer handleLocation = currentPos;

        // adds 2 byte length of message to a watcher
        if ( newData.length > 9 )
        {
            byte[] messageLength =
                    ByteBuffer.allocate( 2 ).putShort( (short) newData.length )
                            .array();

            for ( int i = 0; i < messageLength.length; i++, currentPos++ )
            {
                // freeList_[currentPos] = messageLength[i];
                array[currentPos] = messageLength[i]; // OLD TODO
            }
        }

        for ( int i = 0; i < newData.length; i++, currentPos++ )
        {
            // freeList_[currentPos] = newData[i];
            array[currentPos] = newData[i]; // OLD TODO
        }

        return ByteBuffer.allocate( 4 ).putInt( handleLocation ).array();
    }

    /**
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
                toReturn[i] = array[position]; // OLD TODO
            }
        }
        // return leaf node
        else if ( array[position] == 1 && isNode )
        {
            toReturn = new byte[5];
            for ( int i = 0; i < 5; i++, position++ )
            {
                toReturn[i] = array[position]; // OLD TODO
            }
        }
        // return watcher
        else
        {
            byte[] size = new byte[2];

            for ( int i = 0; i < 2; i++, position++ )
            {
                size[i] = array[position]; // OLD TODO
            }

            int sizeOfMessage = ByteBuffer.wrap( size ).getShort();

            toReturn = new byte[sizeOfMessage];

            for ( int i = 0; i < sizeOfMessage; i++, position++ )
            {
                toReturn[i] = array[position]; // OLD TODO
            }
        }

        return toReturn;
    }

    /**
     * @param handle
     * @param isLeaf
     */
    public void delete( byte[] handle, boolean isLeaf )
    {
        int handleLocation = ByteBuffer.wrap( handle ).getInt();

        // delete internal node
        if ( array[handleLocation] == 0 && isLeaf )
        {
            for ( int i = 0; i < 9; i++, handleLocation++ )
            {
                array[handleLocation] = 0; // OLD TODO
            }
        }
        // delete leaf node
        else if ( array[handleLocation] == 1 && isLeaf )
        {
            for ( int i = 0; i < 5; i++, handleLocation++ )
            {
                array[handleLocation] = 0; // OLD TODO
            }
        }
        // delete a watcher
        else
        {
            byte[] size = new byte[2];
            for ( int i = 0; i < 2; i++, handleLocation++ )
            {
                size[i] = array[handleLocation]; // OLD TODO
                array[handleLocation] = 0; // OLD 
            }
            int sizeOfMessage = ByteBuffer.wrap( size ).getShort();

            for ( int i = 0; i < sizeOfMessage; i++, handleLocation++ )
            {
                array[handleLocation] = 0; // OLD 
            }
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
     * The following functions merges two sections of the freelist array
     * if they are adjacent
     * 
     * @param firstPosition
     * @param secondPosition
     * @return true if merged and false if not
     */
    public boolean merge(int firstPosition, int secondPosition)
    {
        return false;
    }
}
