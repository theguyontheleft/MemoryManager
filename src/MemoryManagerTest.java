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

    /**
     * Instances the objects
     */
    public void setUp()
    {
        memoryManager_ = new MemoryManager();
    }

    /**
     * This function tests the insert and remove function of the memory manager
     */
    public void testInsertAndRemove()
    {
        // TODO add test cases
        assertEquals( 1, 1 );
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

            memoryPool.closeFile();
        }
        catch ( Exception ex )
        {
            System.err.print( "Error when testing the memory Pool: " + ex );
        }
    }
    
    /**
     * Tests the MemoryBuffer object used in the freelist
     */
    public void testMemoryBuffer()
    {
        MemoryBuffer memoryBuffer_ = new MemoryBuffer();
        
        memoryBuffer_.setLength( 1337 );
        assertEquals(memoryBuffer_.getLength(), 1337);
        
        memoryBuffer_.setPosition( 1337 );
        assertEquals(memoryBuffer_.getPosition(), 1337);
    }
}