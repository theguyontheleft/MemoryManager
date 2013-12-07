import java.io.FileNotFoundException;
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
     * parameterized constructor
     * 
     * @param newDisk
     *            name of file
     * @param maxPoolSize
     *            maximum number of buffers in the pool
     * @param newBlockSize
     */
    public BufferPool( int maxPoolSize,
            int newBlockSize )
    {
        pool = new AList<Buffer>( maxPoolSize );

        try
        {
            memoryPool_ = new MemoryPool();
        }
        catch ( Exception e )
        {
            System.err.print( "Error initializing the memory pool: " + e );
        }

        blockSize = newBlockSize;

        toInsert = ByteBuffer.allocate( newBlockSize );
        temp = new byte[blockSize];

        // fills pool with empty buffers
        for ( int i = 0; i < maxPoolSize; i++ )
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
        int blockNumberInFile = pos / blockSize;
        int blockNumberInPool = this.contains( blockNumberInFile );

        // if buffer was not in the buffer pool
        if ( blockNumberInPool == -1 )
        {
            this.getbytes( new byte[space.length], sz, pos );
            blockNumberInPool = this.contains( blockNumberInFile );
        }
        //
        // // inputs record in given location in buffer
        // toInsert = Bytion( pos - (blockNumberInFile * blockSize) );
        // toInsert.put( space, 0, sz );
        //
        // pool.getValue().setBlock( toInsert.array() );
        // pool.getValue().setDirtyBit( true );

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

        // Handle messages that span multiple blocks TODO
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

                        memoryPool_.getDisk().write(
                                pool.getValue().getBlock() );
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
                    if ( pos < memoryPool_.getFileLength() )
                    {
                        // moves to correct position in file
                        memoryPool_.getDisk().seek(
                                blockNumberInFile * blockSize );
                        // overwrites LRU block with new block values from file
                        // temp = new byte[blockSize];
                        memoryPool_.getDisk().read( temp, 0, blockSize );
                        pool.getValue().setBlock( temp.clone() );
                        diskReads++;
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
            for ( int i = 0; i < numberOfBytesInBlock
                    && i < numberOfBytesInBlock; i++, startingByteInBlock++ )
            {
                // space[i] = (byte) i; // TODO: remove this is for debugging
                // purposes
                space[i] = pool.getValue().getBlock()[startingByteInBlock];
            }

            // resetTempPosition cause it
            bytesToRead = bytesToRead - numberOfBytesInBlock;
            pos = 0;
        }
    }

    /*
     * OLD get bytes public void getbytes( byte[] space, int sz, int pos ) { int
     * blockNumberInFile = pos / blockSize; int blockNumberInPool =
     * this.contains( blockNumberInFile );
     * 
     * int bytesToRead = sz; int numberOfBytesInBlock = 0;
     * 
     * // Handle messages that span multiple blocks TODO while ( bytesToRead > 0
     * ) { // TODO Check how many bytes from record are in the current // block
     * numberOfBytesInBlock = blockSize - pos; // TODO: move between buffers,
     * use for loops
     * 
     * // if buffer is not in the pool if ( blockNumberInPool == -1 ) {
     * pool.moveToEnd();
     * 
     * // value in buffer has changed, write to file if (
     * pool.getValue().isDirtyBit() ) { // seeks and writes to file try {
     * memoryPool_.getDisk().seek( pool.getValue().getBlockNumberFile()
     * blockSize );
     * 
     * memoryPool_.getDisk().write( pool.getValue().getBlock() ); diskWrites++;
     * } catch ( IOException e ) { System.out .println(
     * "error in bufferpool getbytes seek" ); e.printStackTrace(); } } // seeks
     * and reads new block from file to buffer pool try { // Whenever the file
     * is being read from make sure the new // position to read is less than the
     * file length // TODO: test if ( pos < memoryPool_.getFileLength() ) { //
     * moves to correct position in file memoryPool_.getDisk().seek(
     * blockNumberInFile * blockSize ); // overwrites LRU block with new block
     * values from file // temp = new byte[blockSize];
     * memoryPool_.getDisk().read( temp, 0, blockSize );
     * pool.getValue().setBlock( temp.clone() ); diskReads++; } } catch (
     * IOException e ) { System.out.println(
     * "error in bufferpool getbytes seek#2" ); e.printStackTrace(); }
     * 
     * pool.getValue().setBlockNumberFile( blockNumberInFile );
     * pool.getValue().setDirtyBit( false ); pool.itemUsed( pool.currPos() );
     * cacheMisses++; } // buffer is in the pool else { pool.itemUsed(
     * blockNumberInPool ); cacheHits++; }
     * 
     * }
     * 
     * // gets desired bytes from buffer pool int startingByteInBlock = pos %
     * blockSize; pool.moveToStart(); for ( int i = 0; i < sz; i++,
     * startingByteInBlock++ ) { space[i] =
     * pool.getValue().getBlock()[startingByteInBlock]; } }
     */

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
                            pool.getValue().getBlockNumberFile()
                                    * blockSize );
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
}