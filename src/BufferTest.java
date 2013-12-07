import junit.framework.TestCase;

/**
 * Tests the Buffer class
 * 
 * @author Matthew Luckam
 * @author James Dagres
 * @version Nov 5, 2013
 * 
 */
public class BufferTest extends TestCase
{

    /**
     * test the isDirtyBit() and setDirtyBit()
     */
    public void testDirtyBit()
    {
        byte[] test = new byte[4];
        Buffer buff = new Buffer( test, 1 );

        assertEquals( false, buff.isDirtyBit() );

        buff.setDirtyBit( true );

        assertEquals( true, buff.isDirtyBit() );
    }

    /**
     * tests getBlockNumberFile() and setBlockNumberFile()
     */
    public void testBlockNumberFile()
    {
        byte[] test = new byte[4];
        Buffer buff = new Buffer( test, 0 );

        assertEquals( 0, buff.getBlockNumberFile() );

        buff.setBlockNumberFile( 10 );

        assertEquals( 10, buff.getBlockNumberFile() );
    }

    /**
     * tests getBlock() and setBlock()
     */
    public void testBlock()
    {
        byte[] test = new byte[4];
        Buffer buff = new Buffer( test, 0 );

        assertEquals( test, buff.getBlock() );

        byte[] test1 = new byte[10];
        buff.setBlock( test1 );

        assertEquals( test1, buff.getBlock() );
    }

}
