import java.awt.geom.Point2D;

import junit.framework.TestCase;

/**
 * This class tests the eqSpatial main class
 * 
 * @author James Dagres
 * 
 * @author Matt Luckam
 * 
 * @version 1.1
 */
public class DiskBintreeTest extends TestCase
{
    /**
     * Sets up the test
     */
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /**
     * Test the main eqsimple class
     */
    public void testMain()
    {
        // Test the constructor
        DiskBintree eqDummy = new DiskBintree();
        assertEquals( eqDummy.toString(), eqDummy.toString() );

        // Test the command line input:
        String[] stringToPass = new String[3];
        stringToPass[0] = "watcherP4.txt";
        stringToPass[1] = "5";
        stringToPass[2] = "6";

        DiskBintree.main( stringToPass );
        assertEquals( stringToPass[0], "watcherP4.txt" );
    }

    /**
     * Tests the watcher class
     */
    public void testWatcher()
    {
        Watcher watcher = new Watcher( 13.0, 3.7, "Jimmy" );

        watcher.setWatcherName( "James Dagres" );
        assertEquals( watcher.getWatcherName(), "James Dagres" );

        Point2D.Double testPoint = new Point2D.Double();
        testPoint.setLocation( 13.0, 13.7 );
        watcher.setWatcherPoint( testPoint );
        assertEquals( watcher.getWatcherPoint(), testPoint );
    }
}
