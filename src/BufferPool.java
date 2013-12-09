import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Acts as a cache to the binary file
 * 
 * @author Matthew Luckam
 * @author James Dagres
 * @version Nov 5, 2013
 * 
 */
public class BufferPool implements BufferPoolADT
{
    /**
     * array list of buffer objects
     */
    private AList<Buffer> pool;
    /**
     * file to access
     */
    private static MemoryPool memoryPool_;
    /**
     * block size of buffers in bytes
     */
    private int blockSize;
    /**
     * Stores the number of buffers
     */
    private int numberOfBuffers_;
    /**
     * number of cache hits
     */
    private int cacheHits;
    /**
     * number of cache misses
     */
    private int cacheMisses;
    /**
     * number of disk reads
     */
    private int diskReads;
    /**
     * number of disks writes
     */
    private int diskWrites;
    // static variables to help improvement //
    /**
     * used in insert()
     */
    private static ByteBuffer toInsert;

    /**
     * used in getBytes()
     */
    private static byte[] temp;

    /**
     * @return the block size
     */
    public int getBlockSize()
    {
        return blockSize;
    }

    /**
     * parameterized constructor
     * 
     * @param newDisk
     *            name of file
     * @param numberOfBuffers
     *            maximum number of buffers in the pool
     * @param newBlockSize
     *            size of the block
     */
    public BufferPool( int numberOfBuffers, int newBlockSize )
    {
        pool = new AList<Buffer>( numberOfBuffers, newBlockSize );

        try
        {
            memoryPool_ = new MemoryPool();
        }
        catch ( Exception e )
        {
            System.err.print( "Error initializing the memory pool: " + e );
        }

        blockSize = newBlockSize;
        numberOfBuffers_ = numberOfBuffers;

        toInsert = ByteBuffer.allocate( newBlockSize );
        temp = new byte[blockSize];

        // fills pool with empty buffers
        for ( int i = 0; i < numberOfBuffers_; i++ )
        {
            pool.append( new Buffer( new byte[newBlockSize], -1 ) );
        }

        cacheHits = 0;
        cacheMisses = 0;
        diskReads = 0;
        diskWrites = 0;
    }

    /**
     * Inserts a given byte array into the buffer pool
     * 
     * @param space
     *            byte array to be inserted
     * @param sz
     *            size of the array
     * @param pos
     *            position of the array in the source
     */
    public void insert( byte[] space, int sz, int pos )
    {

        int bytesToWrite = sz;
        int numberOfBytesInBlock = 0;
        int tempPosition = pos;
        int j = 0;

        while ( bytesToWrite > 0 )
        {
            int blockNumberInFile = pos / blockSize;

            int blockNumberInPool = this.contains( blockNumberInFile );

            // gets desired bytes from buffer pool
            int startingByteInBlock = tempPosition % blockSize;

            // Check how many bytes from record are in the current block
            numberOfBytesInBlock = blockSize - startingByteInBlock;
            byte[] tempByteArray;

            if ( numberOfBytesInBlock < bytesToWrite )
            {
                tempByteArray = new byte[numberOfBytesInBlock];
            }
            else
            {
                tempByteArray = new byte[bytesToWrite];
            }

            for ( int i = 0; i < tempByteArray.length && j < space.length; i++, j++ )
            {
                tempByteArray[i] = space[j];
            }

            // if buffer was not in the buffer pool
            if ( blockNumberInPool == -1 )
            {
                // this.getbytes( new byte[space.length], sz, pos ); Commented
                // out
                this.getbytes( new byte[tempByteArray.length],
                        tempByteArray.length, pos );
                blockNumberInPool = this.contains( blockNumberInFile );
            }

            pool.moveToPos( blockNumberInPool );

            // inputs record in given location in buffer
            toInsert = ByteBuffer.wrap( pool.getValue().getBlock() );
            toInsert.position( startingByteInBlock );
            toInsert.put( tempByteArray, 0, tempByteArray.length );

            pool.getValue().setBlock( toInsert.array() ); // TODO: is this still
                                                          // necessary
            pool.getValue().setDirtyBit( true );

            bytesToWrite -= numberOfBytesInBlock;

            tempPosition = 0;
            pos += numberOfBytesInBlock;
        }

        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Old
        // int blockNumberInFile = pos / blockSize;
        // int blockNumberInPool = this.contains( blockNumberInFile );
        //
        // // if buffer was not in the buffer pool
        // if ( blockNumberInPool == -1 )
        // {
        // this.getbytes( new byte[space.length], sz, pos );
        // blockNumberInPool = this.contains( blockNumberInFile );
        // }
        //
        // pool.moveToPos( blockNumberInPool );
        //
        // // inputs record in given location in buffer
        // toInsert = ByteBuffer.wrap( pool.getValue().getBlock() );
        // toInsert.position( pos - (blockNumberInFile * blockSize) );
        // toInsert.put( space, 0, sz );
        //
        // pool.getValue().setBlock( toInsert.array() );
        // pool.getValue().setDirtyBit( true );
    }

    /**
     * gets the requested bytes from the buffer pool. Puts the size memory
     * starting from pos into the space[] array.
     * 
     * @param space
     *            byte array to be inserted
     * @param sz
     *            size of the array
     * @param pos
     *            position of the array in the source
     */
    public void getbytes( byte[] space, int sz, int pos )
    {
        int blockNumberInFile = pos / blockSize;
        int blockNumberInPool = this.contains( blockNumberInFile );
        int bytesToRead = sz;
        int numberOfBytesInBlock = 0;

        // Handle messages that span multiple blocks TODO test this
        while ( bytesToRead > 0 )
        {
            // if buffer is not in the pool
            if ( blockNumberInPool == -1 )
            {
                pool.moveToEnd();

                // value in buffer has changed, write to file
                if ( pool.getValue().isDirtyBit() )
                {
                    // seeks and writes to file
                    try
                    {
                        memoryPool_.getDisk().seek(
                                pool.getValue().getBlockNumberFile()
                                        * blockSize );
                        memoryPool_.getDisk()
                                .write( pool.getValue().getBlock() );
                        diskWrites++;
                    }
                    catch ( IOException e )
                    {
                        System.out
                                .println( "error in bufferpool getbytes seek" );
                        e.printStackTrace();
                    }
                }
                // seeks and reads new block from file to buffer pool
                try
                {
                    // Whenever the file is being read from make sure the new
                    // position to read is less than the file length
                    if ( pos < (int) (memoryPool_.getFileLength()) )
                    {
                        // moves to correct position in file
                        memoryPool_.getDisk().seek(
                                blockNumberInFile * blockSize );
                        memoryPool_.getDisk().read( temp, 0, blockSize );
                        pool.getValue().setBlock( temp.clone() );
                        diskReads++;
                    }
                    else
                    {
                        // This can happen when the memory manager decides to
                        // “grow” the size of its memory pool
                        // System.out
                        // .println(
                        // "bufferpool getbytes tried reading from outside the file."
                        // );
                    }
                }
                catch ( IOException e )
                {
                    System.out.println( "error in bufferpool getbytes seek#2" );
                    e.printStackTrace();
                }

                pool.getValue().setBlockNumberFile( blockNumberInFile );
                pool.getValue().setDirtyBit( false );
                pool.itemUsed( pool.currPos() );
                cacheMisses++;
            }
            // buffer is in the pool
            else
            {
                pool.itemUsed( blockNumberInPool );
                cacheHits++;
            }

            // gets desired bytes from buffer pool
            int startingByteInBlock = pos % blockSize;

            // Check how many bytes from record are in the current block
            numberOfBytesInBlock = blockSize - startingByteInBlock;

            pool.moveToStart();

            // Read the bytes remaining in the block and less than the
            // bytesToRead variable
            for ( int i = 0; i < numberOfBytesInBlock && 0 < bytesToRead; i++, startingByteInBlock++, bytesToRead-- )
            {
                space[sz - bytesToRead] =
                        pool.getValue().getBlock()[startingByteInBlock];
            }

            // Update the remaining bytes to read and set pos to 0
            // bytesToRead = bytesToRead - numberOfBytesInBlock;
            pos = 0;
        }
    }

    // /**
    // * gets the requested bytes from the buffer pool TODO: OLD
    // *
    // * @param space
    // * byte array to be inserted
    // * @param sz
    // * size of the array
    // * @param pos
    // * position of the array in the source
    // */
    // @Override
    // public void getbytes( byte[] space, int sz, int pos )
    // {
    // int blockNumberInFile = pos / blockSize;
    // int blockNumberInPool = this.contains( blockNumberInFile );
    //
    // // if buffer is not in the pool
    // if ( blockNumberInPool == -1 )
    // {
    // pool.moveToEnd();
    // // value in buffer has changed, write to file
    // if ( pool.getValue().isDirtyBit() )
    // {
    // // seeks and writes to file
    // try
    // {
    // memoryPool_.getDisk().seek(
    // pool.getValue().getBlockNumberFile() * blockSize );
    //
    // memoryPool_.getDisk().write( pool.getValue().getBlock() );
    // diskWrites++;
    // }
    // catch ( IOException e )
    // {
    // System.out.println( "error in bufferpool getbytes seek" );
    // e.printStackTrace();
    // }
    //
    // }
    // // seeks and reads new block from file to buffer pool
    // try
    // {
    // // moves to correct position in file
    // memoryPool_.getDisk().seek( blockNumberInFile * blockSize );
    // // overwrites LRU block with new block values from file
    // // temp = new byte[blockSize];
    // memoryPool_.getDisk().read( temp, 0, blockSize );
    // pool.getValue().setBlock( temp.clone() );
    // diskReads++;
    // }
    // catch ( IOException e )
    // {
    // System.out.println( "error in bufferpool getbytes seek#2" );
    // e.printStackTrace();
    // }
    //
    // pool.getValue().setBlockNumberFile( blockNumberInFile );
    // pool.getValue().setDirtyBit( false );
    // pool.itemUsed( pool.currPos() );
    // cacheMisses++;
    //
    // }
    // // buffer is in the pool
    // else
    // {
    // pool.itemUsed( blockNumberInPool );
    // cacheHits++;
    // }
    //
    // // gets desired bytes from buffer pool
    // int startingByteInBlock = pos % blockSize;
    // pool.moveToStart();
    // for ( int i = 0; i < sz; i++, startingByteInBlock++ )
    // {
    // space[i] = pool.getValue().getBlock()[startingByteInBlock];
    // }
    //
    // }

    /**
     * flushes all values from cache to disk
     */
    public void flush()
    {
        pool.moveToStart();
        for ( int i = 0; i < pool.length(); pool.moveToPos( ++i ) )
        {
            if ( pool.getValue().isDirtyBit() )
            {
                try
                {
                    memoryPool_.getDisk().seek(
                            pool.getValue().getBlockNumberFile() * blockSize );
                    memoryPool_.getDisk().write( pool.getValue().getBlock() );
                }
                catch ( IOException e )
                {
                    System.out.println( "error on flush" );
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * Determines if the desired buffer block is in the BufferPool
     * 
     * @param blockNumber
     *            block number you are looking for
     * @return blocks location in the pool if it exists, -1 otherwise
     */
    private int contains( int blockNumber )
    {
        pool.moveToStart();
        for ( int i = 0; i < pool.length(); pool.moveToPos( ++i ) )
        {
            if ( blockNumber == pool.getValue().getBlockNumberFile() )
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * number of times item was in the buffer pool
     * 
     * @return number of hits
     */
    public int getCacheHits()
    {
        return cacheHits;
    }

    /**
     * number of times item was not in the buffer pool
     * 
     * @return number of misses
     */
    public int getCacheMisses()
    {
        return cacheMisses;
    }

    /**
     * number of times disk was read from
     * 
     * @return number of reads
     */
    public int getDiskReads()
    {
        return diskReads;
    }

    /**
     * number of times disk was written to
     * 
     * @return number of writes
     */
    public int getDiskWrites()
    {
        return diskWrites;
    }

    /**
     * Prints the bufferPool
     */
    public void print()
    {
        for ( int i = 0; i < pool.length(); i++ )
        {
            pool.moveToPos( i );
            int output = pool.getValue().getBlockNumberFile();

            if ( output >= 0 )
            {
                System.out
                        .println( "Block ID of buffer" + i + " is " + output );
            }
            else
            {
                System.out
                        .println( "Block ID of buffer" + i + " is " + "Empty" );
            }
        }
    }

    /**
     * Closes the file the memorypool uses
     * 
     * @throws IOException
     */
    public void closeFile() throws IOException
    {
        memoryPool_.closeFile();
    }
}