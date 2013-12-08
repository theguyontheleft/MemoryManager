import junit.framework.TestCase;

/**
 * Tests the NodeInternal class and the NodeLeaf class
 * 
 * @author Matthew Luckam mcl209
 * @author James Dagres
 * @version Oct 12, 2013
 * 
 */
public class NodeTest extends TestCase
{

    /**
     * Tests internal node
     */
    public void testNodeInternal()
    {
        NodeInternal myNode = new NodeInternal();

        assertEquals( false, myNode.isLeaf() );
    }

    /**
     * Tests the leaf node
     */
    public void testNodeLef()
    {
        NodeLeaf nodeLeaf = new NodeLeaf();

        byte[] testByteArray = new byte[4];
        nodeLeaf.setElement( testByteArray );

        assertEquals( true, nodeLeaf.isLeaf() );
    }
}