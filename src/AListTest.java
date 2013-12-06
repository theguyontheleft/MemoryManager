import junit.framework.TestCase;

/**
 * Tests the AList class
 * 
 * @author Matthew Luckam
 * @author James Dagres
 * @version Nov 5, 2013
 * 
 */
public class AListTest extends TestCase
{
    /**
     * Tests the insert function
     */
    public void testInsert()
    {
        AList<Integer> myList = new AList<Integer>( 5 );

        myList.insert( 4 );
        myList.insert( 3 );
        myList.insert( 2 );
        myList.insert( 1 );
        myList.insert( 0 );
        myList.currPos();
        myList.prev();
        myList.next();

        myList.moveToPos( 3 );
        assertEquals( myList.getValue(), (Integer) 3 );

        myList.insert( 5 );
        myList.insert( 5 );
        myList.itemUsed( 3 );
        myList.insert( 1 );
        myList.clear();
        myList.remove();

    }

    /**
     * Tests the remove function
     */
    public void testRemove()
    {
        AList<Integer> myList = new AList<Integer>( 5 );

        myList.insert( 4 );
        myList.insert( 3 );
        myList.insert( 2 );
        myList.insert( 1 );
        myList.insert( 0 );

        myList.moveToStart();
        while ( 0 < myList.length() )
        {
            myList.remove();
        }

        assertEquals( myList.length(), 0 );
    }

}
