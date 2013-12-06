import junit.framework.TestCase;

/**
 * Tests the NodeInternal class
 * 
 * @author Matthew Luckam mcl209
 * @author James Dagres
 * @version Oct 12, 2013
 * 
 */
public class NodeInternalTest extends TestCase
{

    /**
     * tests internal node
     */
    public void testNodeInternal()
    {
        NodeInternal myNode = new NodeInternal();

        assertEquals( false, myNode.isLeaf() );
    }

}
