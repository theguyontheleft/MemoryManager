import java.io.IOException;
import java.io.RandomAccessFile;

import junit.framework.TestCase;

/**
 * @author Jimmy Dagres
 * 
 * @version Dec 6, 2013
 * 
 *          This class tests the memory manager class as well as the memory pool
 *          class.
 */
public class MemoryManagerTest extends TestCase
{
    MemoryManager memoryManager_;
    byte[] testByteArray8_;
    byte[] testByteArray4_;
    byte[] testByteArray2_;

    /**
     * Instances the objects
     */
    public void setUp()
    {
        memoryManager_ = new MemoryManager();

        testByteArray8_ = new byte[8];
        for ( int j = 0; j < 8; j++ )
        {
            testByteArray8_[j] = (byte) j;
        }

        testByteArray4_ = new byte[4];
        for ( int j = 0; j < 4; j++ )
        {
            testByteArray4_[j] = (byte) j;
        }

        testByteArray2_ = new byte[2];
        for ( int j = 0; j < 2; j++ )
        {
            testByteArray2_[j] = (byte) j;
        }
    }

    /**
     * This function tests the insert and remove function of the memory manager
     */
    public void testInsertAndRemove()
    {
        // Insert 4, insert 8, remove 4 (adds it to freespace), then insert 4
        // into the freedspace
        byte[] handleOfFirst4 = memoryManager_.insert( testByteArray4_ );
        // memoryManager_.insert( testByteArray8_ ); TODO
        // memoryManager_.delete( handleOfFirst4, true );
        // byte[] handleOfSecond4 = memoryManager_.insert( testByteArray4_ );

        // The newly inserted four should have the same handle of the freespace
        assertEquals( 4, handleOfFirst4.length );
    }

    /**
     * This function tests the merge function.
     */
    public void testMerge()
    {
        memoryManager_ = new MemoryManager();

        // Insert 8, then 4, then 4, then 2.
        // Remove the first 4, remove the second
        assertEquals( 4, 4 ); // TODO
    }

    /**
     * Tests the MemoryPool Class
     * 
     * @throws IOException
     */
    public void testMemoryPool()
    {
        try
        {
            MemoryPool memoryPool = new MemoryPool();
            RandomAccessFile raf = memoryPool.getDiskFile();

            assertEquals( memoryPool.getFileLength(), 0 );

            assertEquals( memoryPool.getDisk(), raf );

            memoryPool.closeFile();
        }
        catch ( Exception ex )
        {
            System.err.print( "Error when testing the memory Pool: " + ex );
        }
    }

    /**
     * Tests the MemoryBlock object used in the freelist
     */
    public void testMemoryBlock()
    {
        MemoryBlock memoryBlock_ = new MemoryBlock( 0, 0 );

        memoryBlock_.setLength( 1337 );
        assertEquals( memoryBlock_.getLength(), 1337 );

        memoryBlock_.setStartPosition( 1337 );
        assertEquals( memoryBlock_.getStartPosition(), 1337 );
    }
}