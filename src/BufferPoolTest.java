import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

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
     * Tests the overlapping modifications of the bufferpool
     */
    public void testBufferAltercations()
    {
        
        // RandomAccessFile test = new RandomAccessFile( "mediumTest.dat", "rw" );
        byte[] byteToTest = new byte[20];
        byte[] byteToTest1 = new byte[20];

        BufferPool pool = new BufferPool( 4, 10 );

        pool.insert( byteToTest, 20, 6 );
        pool.getbytes( byteToTest1, 20, 6 );

        for ( int i = 0; i < 20; i++ )
        {
            assert (byteToTest[i] == byteToTest1[i]);
        }

    }

    /**
     * tests the constructor
     * 
     * @throws FileNotFoundException
     */
    public void testBufferPool() throws FileNotFoundException
    {
        RandomAccessFile test = new RandomAccessFile( "smallTest", "rw" );
        BufferPool pool = new BufferPool( 4, 10 );

        assertEquals( 0, pool.getCacheHits() );
        assertEquals( 0, pool.getCacheMisses() );
        assertEquals( 0, pool.getDiskReads() );
        assertEquals( 0, pool.getDiskWrites() );
    }
}
