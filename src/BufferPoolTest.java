import java.io.FileNotFoundException;

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
        byte[] byteToTest = new byte[20];
        byte[] byteToTest1 = new byte[20];

        BufferPool pool = new BufferPool( 4, 10 );

        pool.insert( byteToTest, 20, 6 );
        pool.getbytes( byteToTest1, 20, 6 );

        for ( int j = 0; j < 20; j++ )
        {
            System.out.println("VALUE: " + byteToTest[j]);
            assert (byteToTest[j] == byteToTest1[j]);
        }
    }

    /**
     * tests the constructor
     * 
     * @throws FileNotFoundException
     */
    public void testBufferPool() throws FileNotFoundException
    {
        BufferPool pool = new BufferPool( 4, 10 );

        assertEquals( 0, pool.getCacheHits() );
        assertEquals( 0, pool.getCacheMisses() );
        assertEquals( 0, pool.getDiskReads() );
        assertEquals( 0, pool.getDiskWrites() );
    }
}