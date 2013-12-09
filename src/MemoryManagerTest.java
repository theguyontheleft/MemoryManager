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
    BufferPool bufPool_;
    MemoryManager memoryManager_;

    byte[] testByteArray8_;
    byte[] testByteArray4_;
    byte[] testByteArray2_;

    /**
     * Instances the objects
     */
    public void setUp()
    {
        bufPool_ = new BufferPool( 10, 10 );
        memoryManager_ = new MemoryManager( bufPool_ );

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
        // into the freed space
        byte[] handleOfFirst4 = memoryManager_.insert( testByteArray4_ );

        // The newly inserted four should have the same handle of the freespace
        assertEquals( 4, handleOfFirst4.length );
    }

    /**
     * This function tests the merge function.
     */
    public void testMerge()
    {
        bufPool_ = new BufferPool( 10, 10 );
        memoryManager_ = new MemoryManager( bufPool_ );

        byte[] leafNode1 = { (byte) 1, (byte) 0, (byte) 0, (byte) 0, (byte) 0 };

        byte[] leafNodeHandle1 = memoryManager_.insert( leafNode1 );
        byte[] leafNodeHandle2 = memoryManager_.insert( leafNode1 );
        byte[] leafNodeHandle3 = memoryManager_.insert( leafNode1 );
        byte[] leafNodeHandle4 = memoryManager_.insert( leafNode1 );
        byte[] leafNodeHandle5 = memoryManager_.insert( leafNode1 );

        memoryManager_.delete( leafNodeHandle2, true );
        memoryManager_.delete( leafNodeHandle4, true );
        memoryManager_.delete( leafNodeHandle3, true );

        byte[] leafNodeHandle6 = memoryManager_.insert( leafNode1 );
        byte[] leafNodeHandle7 = memoryManager_.insert( leafNode1 );
        byte[] leafNodeHandle8 = memoryManager_.insert( leafNode1 );
        byte[] leafNodeHandle9 = memoryManager_.insert( leafNode1 );
        byte[] leafNodeHandle10 = memoryManager_.insert( leafNode1 );
        byte[] leafNodeHandle11 = memoryManager_.insert( leafNode1 );
        byte[] leafNodeHandle12 = memoryManager_.insert( leafNode1 );
        byte[] leafNodeHandle13 = memoryManager_.insert( leafNode1 );
        byte[] leafNodeHandle14 = memoryManager_.insert( leafNode1 );

        memoryManager_.delete( leafNodeHandle6, true );
        memoryManager_.delete( leafNodeHandle8, true );
        memoryManager_.delete( leafNodeHandle10, true );
        memoryManager_.delete( leafNodeHandle12, true );
        memoryManager_.delete( leafNodeHandle14, true );
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