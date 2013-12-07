import junit.framework.TestCase;

/**
 * 
 * @author Jimmy Dagres 
 * @author Matt Luckam
 * 
 *         This class tests the controller class
 * 
 * @version 2.6
 */
public class ControllerTest extends TestCase
{
    // Three test references
    private Controller controller1;
    private Controller controller2;
    private Controller controller3;

    /**
     * Instances the objects
     */
    public void setUp()
    {
        resetTestControllerInstances();
    }

    /**
     * Rests the tests
     */
    private void resetTestControllerInstances()
    {
        controller1 = new Controller();
        controller2 = new Controller();
        controller3 = new Controller();
    }

    /**
     * Tests the wrapper and the argument parser function
     */
    public void testArgumentParser()
    {
        // Test with the watcherP4 file
        String[] arg = new String[3];
        arg[0] = "watcherP4.txt";
        arg[1] = "20";
        arg[2] = "6234";
        controller1.argumentParser( arg );
        assertEquals( "watcherP4.txt", arg[0] );

        arg = new String[3];
        arg[0] = "watcherP4.txt";
        arg[1] = "1";
        arg[2] = "6";
        controller2.argumentParser( arg );
        assertEquals( "watcherP4.txt", arg[0] );

        arg = new String[3];
        arg[0] = "watcherP4.txt";
        arg[1] = "10";
        arg[2] = "6234";
        controller3.argumentParser( arg );
        assertEquals( "watcherP4.txt", arg[0] );

        resetTestControllerInstances();

        // Test with the comprehensive file
        arg = new String[3];
        arg[0] = "comprehensive.txt";
        arg[1] = "10";
        arg[2] = "6234";
        controller1.argumentParser( arg );
        assertEquals( "comprehensive.txt", arg[0] );

        arg = new String[3];
        arg[0] = "comprehensive.txt";
        arg[1] = "1";
        arg[2] = "6";
        controller2.argumentParser( arg );
        assertEquals( "comprehensive.txt", arg[0] );
    }

    /**
     * Continues to test it with different inputs which tests most of the
     * controller classes error handlers. The assert at the end ensures the
     * program hasn't crashed
     */
    public void testInvalidSubscriberInput()
    {
        // Test with too many input arguments
        String[] arg = new String[4];
        arg[0] = "--extraInput";
        arg[1] = "testBadWatcherInput.txt";
        arg[2] = "whatIsThisEven";
        arg[3] = "this shouldn't be here";
        controller1.argumentParser( arg );
        assertEquals( "--extraInput", arg[0] );

        // Test file with bad commands
        arg = new String[3];
        arg[0] = "badInputTestFile.txt";
        arg[1] = "10";
        arg[2] = "4096";
        controller3.argumentParser( arg );
        assertEquals( "badInputTestFile.txt", arg[0] );
    }
}
