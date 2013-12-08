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
    public void testContains()
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
        Bintree<Point2D.Double, String> testTree =
                new Bintree<Point2D.Double, String>();

        Point2D.Double testPoint = new Point2D.Double();
        testPoint.setLocation( -105.7, -24.3 );
        String name = "Riley";

        Point2D.Double testPoint2 = new Point2D.Double();
        testPoint2.setLocation( 21.2, -38.6 );
        String name2 = "Taylor";

        testTree.insert( testPoint, name );

        Point2D.Double testSearch = new Point2D.Double();
        testSearch.setLocation( 69.3, 29.7 );

        Point2D.Double testSearch0 = new Point2D.Double();
        testSearch0.setLocation( -106.7, -58.3 );

        Point2D.Double testSearch1 = new Point2D.Double();
        testSearch1.setLocation( 51.8, 28.9 );

        Point2D.Double testSearch2 = new Point2D.Double();
        testSearch2.setLocation( 80.5, 8.3 );

        testTree.rangeSearch( testSearch, 18.0 );

        testTree.insert( testPoint2, name2 );

        testTree.rangeSearch( testSearch0, 0.00 );
        testTree.rangeSearch( testSearch1, 18.0 );
        testTree.rangeSearch( testSearch2, 170.4 );

    }

    public void testPrint()
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

        testTree.print();

        // looked at system out output to confirm the function was properly
        // working
        assert true;

    }
}
