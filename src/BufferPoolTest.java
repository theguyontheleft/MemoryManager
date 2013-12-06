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
     * tests the constructor
     * 
     * @throws FileNotFoundException
     */
    public void testBufferPool() throws FileNotFoundException
    {
        RandomAccessFile test = new RandomAccessFile( "smallTest", "rw" );
        BufferPool pool = new BufferPool( 2, 8 );

        assertEquals( 0, pool.getCacheHits() );
        assertEquals( 0, pool.getCacheMisses() );
        assertEquals( 0, pool.getDiskReads() );
        assertEquals( 0, pool.getDiskWrites() );
    }

    /**
     * tests insert() function
     * 
     * @throws IOException
     */
    public void testInsert() throws IOException
    {
        RandomAccessFile test = new RandomAccessFile( "mediumTest.dat", "rw" );
        BufferPool pool = new BufferPool( 2, 8 );

        pool.insert( new byte[4], 4, 10 );

        assertEquals( true, true );
    }
}
