import java.awt.geom.Point2D;

import junit.framework.TestCase;

/**
 * Tests the Bintree functionality
 * 
 * @author Matthew Luckam mcl209
 * @author James Dagres jdagres
 * @version Oct 12, 2013
 */
@SuppressWarnings( "all" )
public class BintreeTest extends TestCase
{

    /**
     * tests insert
     */
    public void testInsert()
    {
        Bintree<Point2D.Double, String> testTree =
                new Bintree<Point2D.Double, String>();

        Point2D.Double testPoint = new Point2D.Double();
        testPoint.setLocation( -100, -80 );
        String name = "John";

        Point2D.Double testPoint2 = new Point2D.Double();
        testPoint2.setLocation( 10, -80 );
        String name2 = "Pauly";

        Point2D.Double testPoint3 = new Point2D.Double();
        testPoint3.setLocation( 100, -80 );
        String name3 = "Dan";

        testTree.insert( testPoint, name );

        assertEquals( true, testTree.inTree( testPoint ) );

        testTree.insert( testPoint2, name2 );

        assertEquals( true, testTree.inTree( testPoint2 ) );

        testTree.insert( testPoint3, name3 );

        assertEquals( true, testTree.inTree( testPoint3 ) );

    }

    /**
     * tests going right when equal to divide
     */
    public void testInsertLimits()
    {
        Bintree<Point2D.Double, String> testTree =
                new Bintree<Point2D.Double, String>();

        // (0,0) input actual coordinates( -180, -90 )
        Point2D.Double testPoint = new Point2D.Double();
        testPoint.setLocation( 0, 0 );
        String name = "DoubleRight";

        Point2D.Double testPoint1 = new Point2D.Double();
        testPoint1.setLocation( 0, -1 );
        String name1 = "RightLeft";

        Point2D.Double testPoint2 = new Point2D.Double();
        testPoint2.setLocation( -1, -1 );
        String name2 = "DoubleLeft";

        Point2D.Double testPoint3 = new Point2D.Double();
        testPoint3.setLocation( -1, 0 );
        String name3 = "LeftRight";

        testTree.insert( testPoint, name );
        testTree.insert( testPoint1, name1 );
        testTree.insert( testPoint2, name2 );
        testTree.insert( testPoint3, name3 );

        assertEquals( true, testTree.inTree( testPoint ) );

    }

    /**
     * Test the contain function
     */
    public void testInTree()
    {
        Bintree<Point2D.Double, String> testTree =
                new Bintree<Point2D.Double, String>();

        Point2D.Double testPoint = new Point2D.Double();
        testPoint.setLocation( -140, -80 );
        String name = "Tom";

        Point2D.Double testPoint2 = new Point2D.Double();
        testPoint2.setLocation( -140, -50 );
        String name2 = "Jim";

        testTree.insert( testPoint, name );

        assertEquals( true, testTree.inTree( testPoint ) );

        testTree.insert( testPoint2, name2 );

        testTree.inTree( testPoint2 );

        assertEquals( true, testTree.inTree( testPoint2 ) );

        Point2D.Double testPointWrong = new Point2D.Double();
        testPointWrong.setLocation( 0, 0 );

        assertEquals( false, testTree.inTree( testPointWrong ) );

    }

    public void testRemove()
    {
        Bintree<Point2D.Double, String> testTree =
                new Bintree<Point2D.Double, String>();

        Point2D.Double testPoint = new Point2D.Double();
        testPoint.setLocation( -100, -80 );
        String name = "John";

        Point2D.Double testPoint2 = new Point2D.Double();
        testPoint2.setLocation( 10, -80 );
        String name2 = "Pauly";

        Point2D.Double testPoint3 = new Point2D.Double();
        testPoint3.setLocation( 100, -80 );
        String name3 = "Dan";

        testTree.insert( testPoint, name );
        testTree.insert( testPoint2, name2 );
        testTree.insert( testPoint3, name3 );

        testTree.remove( testPoint3 );

        testTree.remove( testPoint2 );

        testTree.remove( testPoint );

    }

    public void testRangeSearch()
    {
        // String fileName = "p4bin.dat";
        // MemoryPool myMemoryPool = new MemoryPool(fileName);
        // myBufferPool = new BufferPool(100, 2, myMemoryPool);
        // memMan = new MemoryManager(myBufferPool);
        //
        //
        // testEarthQuakeWorld1 = new WorldBounds(0, 90, 0, 90);
        // testEarthQuakeWorld2 = new WorldBounds(180, 360, 90, 180);
        // testEarthQuakeWorld3 = new WorldBounds(180, 360, 0, 180);
        // testEarthQuakeWorld4 = new WorldBounds(0, 360, 0, 180);
        //
        // bTree = new BinTree(0, 360, 0, 180, memMan);
        //
        // loc1 = new Location(190, 50);
        // loc2 = new Location(275, 140);
        // loc3 = new Location(50, 15);
        //
        // loc4 = new Location(45, 45);
        // loc5 = new Location(200, 60);
        // loc6 = new Location(270, 135);
        //
        // watcher1 = new Watcher("add 10.0 -40.0 Ben");
        // watcher2 = new Watcher("add 95.0 50.0 Bob");
        // watcher3 = new Watcher("add -130.0 -75.0 Cah");
        // watcher4 = new Watcher("search -135.0 -45.0 40");
        // watcher5 = new Watcher("search 20.0 -30.0 40");
        // watcher6 = new Watcher("search 90.0 45.0 40");
        //
        //
        // // CASE EARTHQUAKE 1 - SHOULD RETURN 2, INTERSECTS ROOT, AND WATCHER3
        // bTree.insert(watcher2.getBytes(), loc2);
        // bTree.insert(watcher3.getBytes(), loc3);
        // bTree.insert(watcher1.getBytes(), loc1);
        //
        // int visited = bTree.regionSearch(testEarthQuakeWorld1, loc4,
        // watcher4.getRadius());
        // assertEquals(2, visited);
        //
        // // CASE EARTHQUAKE 2 - SHOULD RETURN 3, INTERSECTS ROOT, INTERNAL2,
        // WATCHER2
        // int visited2 = bTree.regionSearch(testEarthQuakeWorld2, loc5,
        // watcher5.getRadius());
        // assertEquals(3, visited2);
        //
        // // CASE EARTHQUAKE 3 - SHOULD RETURN 4, INTERSECTS ROOT, INTERNAL2,
        // WATCHER2
        // // WATCHER1
        // int visited3 = bTree.regionSearch(testEarthQuakeWorld3, loc6,
        // watcher6.getRadius());
        // assertEquals(4, visited3);

        Bintree<Point2D.Double, String> testTree =
                new Bintree<Point2D.Double, String>();

        Point2D.Double testPoint = new Point2D.Double();
        testPoint.setLocation( 10.0, -40.0 );
        String name = "Ben";

        Point2D.Double testPoint2 = new Point2D.Double();
        testPoint2.setLocation( 95.0, 50.0 );
        String name2 = "Bob";

        Point2D.Double testPoint3 = new Point2D.Double();
        testPoint2.setLocation( -130.0, -75.0 );
        String name3 = "Caaaaaaaaahhhhh";

        testTree.insert( testPoint, name );
        testTree.insert( testPoint2, name2 );
        testTree.insert( testPoint3, name3 );

        // watcher4 = new Watcher("search -135.0 -45.0 40");
        // watcher5 = new Watcher("search 20.0 -30.0 40");
        // watcher6 = new Watcher("search 90.0 45.0 40");

        Point2D.Double testSearch = new Point2D.Double();
        testSearch.setLocation( -135.0, -45.0 );

        Point2D.Double testSearch0 = new Point2D.Double();
        testSearch0.setLocation( 20.0, -30.0 );

        Point2D.Double testSearch1 = new Point2D.Double();
        testSearch1.setLocation( 90.0, 45.0 );

        // Point2D.Double testSearch2 = new Point2D.Double();
        // testSearch2.setLocation( 80.5, 8.3 );

        testTree.rangeSearch( testSearch, 40.0 );
        testTree.rangeSearch( testSearch0, 40.0 );
        testTree.rangeSearch( testSearch1, 40.0 );
        // testTree.rangeSearch( testPoint2, 170.4 );

    }
}
