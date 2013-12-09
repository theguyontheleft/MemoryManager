import java.io.IOException;

import junit.framework.TestCase;

/**
 * Tests the buffer pool class
 * 
 * @author Matthew Luckam
 * @author James Dagres
 * @version Nov 5, 2013
 * 
 */
public class BufferPoolTest extends TestCase
{
    /**
     * Tests the overlapping modifications of the bufferpool, test the insert
     * and getbytes functions
     */
    public void testBufferAltercations()
    {
        byte[] byteToInsert = new byte[20];
        for ( int j = 0; j < 20; j++ )
        {
            byteToInsert[j] = (byte) j;
        }

        byte[] byteToTest1 = new byte[20];

        BufferPool pool = new BufferPool( 10, 10 );

        pool.insert( byteToInsert, 20, 6 );
        pool.getbytes( byteToTest1, 20, 6 );

        // for ( int j = 0; j < 20; j++ )
        // {
        // System.out.println( "VALUE: " + byteToInsert[j] );
        // assert (byteToInsert[j] == byteToTest1[j]);
        // }
        
        assertEquals( byteToInsert[9], byteToTest1[9] );
    }

    /**
     * tests the constructor
     * 
     * @throws IOException
     */
    public void testBufferPool() throws IOException
    {
        BufferPool pool = new BufferPool( 4, 10 );

        MemoryPool memPool = new MemoryPool();
        memPool.getDisk();
        memPool.getDiskFile();

        assertEquals( 0, pool.getCacheHits() );
        assertEquals( 0, pool.getCacheMisses() );
        assertEquals( 0, pool.getDiskReads() );
        assertEquals( 0, pool.getDiskWrites() );
    }
}