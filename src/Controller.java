import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Jimmy Dagres
 * @author Matt Luckam
 * 
 * @version Dec 5, 2013
 * 
 *          This is the controller class which implements the program through
 *          the DiskBintree main input
 * 
 */
public class Controller
{
    // The bintree to store the coordinates of the watchers
    private Bintree<Point2D.Double, String> subscribersBintree_ = null;

    private Integer blockSize_;
    private String commandFileName_;
    private Integer numberBuffs_;

    // Instance to the bufferPool
    private static BufferPool bufPool_; 

    // Instance of the Memory Manager
    private static MemoryManager memoryManager_;

    /**
     * Constructor method initializes the references to the structures used, and
     * the sets the start time
     */
    public Controller()
    {
    }

    /**
     * Creates the bufferpool
     */
    void createBufferPool()
    {
        bufPool_ = new BufferPool( this.numberBuffs_, this.blockSize_ );
    }

    /**
     * Creates the memory manager
     */
    private void createMemoryManager()
    {
        // Create the memory manager passing it the BufferPool
        memoryManager_ = new MemoryManager( bufPool_ );
    }

    /**
     * Takes in the input string and handles it.
     * 
     * @param args
     *            the command line input
     */
    public void argumentParser( String[] args )
    {
        if ( 3 == args.length )
        {
            commandFileName_ = args[0];
            numberBuffs_ = Integer.parseInt( args[1] );
            blockSize_ = Integer.parseInt( args[2] );

            // Create the bufferPool and memoryManager
            createBufferPool();
            createMemoryManager();
            // Initialize the data structures
            subscribersBintree_ =
                    new Bintree<Point2D.Double, String>( memoryManager_ );

            // The ultimate try catch
            try
            {
                readCommandFile();
            }
            catch ( Exception ex )
            {
                System.err.print( "The following error occurred: " + ex );
            }
        }
        else
        {
            System.out.println( "Invalid input format!" );
        }
    }

    /**
     * Reads each command from the file and sends it to the commandParser
     * function
     * 
     * @throws IOException
     */
    private void readCommandFile() throws IOException
    {
        BufferedReader br =
                new BufferedReader( new FileReader( commandFileName_ ) );

        while ( br.ready() )
        {
            String command = br.readLine();
            commandParser( command );
        }
        br.close();

        subscribersBintree_.printStat();
        bufPool_.flush();
        bufPool_.closeFile();
    }

    /**
     * Processes the files commands by calling that command's helper function.
     * 
     * @param newCommand
     *            the command arguments to parse
     */
    private void commandParser( String newCommand )
    {
        // Break up the string by spaces
        String[] s = newCommand.split( "\\s+" );

        int tempLength = s.length;
        if ( 4 == tempLength )
        {
            // Check to see if the arguments are in the form:
            // 'add <latitude> <longitude> <user>' OR
            // 'search <x> <y> <radius>'
            if ( s[0].toLowerCase().contains( "add" ) )
            {
                addSubscriberHelper( s );
            }
            else if ( s[0].toLowerCase().contains( "search" ) )
            {
                searchHelper( s );
            }
        }
        else if ( 3 == s.length )
        {
            removeSubscriberHelper( s );
        }
        else if ( 1 == s.length )
        {
            debugBintree( s[0] );
        }
    }

    /**
     * The search command looks as follows. 'search <x> <y> <radius>'. First
     * this function will print out the line: "Search <x> <y> returned the
     * following watchers:" Then it will print out each record that lies in the
     * search circle listing its name and position, one record to each line.
     * After it completes searching, it prints out: "Watcher search caused
     * <number> bintree nodes to be visited."
     * 
     * @param s
     */
    private void searchHelper( String[] s )
    {

        Double longitudeX = Double.parseDouble( s[1].trim() );
        Double latitudeY = Double.parseDouble( s[2].trim() );
        Double radius = Double.parseDouble( s[3].trim() );

        System.out.println( "Search " + longitudeX + " " + latitudeY + " "
                + radius + " returned the following watchers:" );

        subscribersBintree_.rangeSearch( new Point2D.Double( longitudeX,
                latitudeY ), radius );

    }

    /**
     * Prints a traversal for the bintree nodes. After that, it prints the block
     * IDs of the blocks currently contained in the bufferpool in order from
     * most recently to least recently used.
     * 
     * @param s
     *            the possible debug command
     */
    private void debugBintree( String s )
    {
        // Check to ensure the arguments are in the form: debug
        if ( s.contains( "debug" ) )
        {
            subscribersBintree_.print();
        }
    }

    /**
     * Handles the addition of the passed subscriber, displays the output if
     * successful
     * 
     * @param s
     *            the string
     */
    private void addSubscriberHelper( String[] s )
    {
        // Create a coordinate
        String longitudeX = s[1].trim();
        String latitudeY = s[2].trim();
        String name = s[3].trim();

        if ( addWatcherToBinTree( s[1], s[2], s[3] ) )
        {
            // If it was added successfully then display the output:
            // <Watcher name> <x> <y> is added to the bintree
            String outputString2 =
                    s[3] + " " + Double.parseDouble( longitudeX ) + " "
                            + Double.parseDouble( latitudeY )
                            + " is added into the bintree";
            System.out.println( outputString2 );
        }
        else
        {
            // If it failed to add to the bintree, then a watcher at
            // those coordinates already exists. Thus display the
            // following output:
            // <coordinate> duplicates a watcher already in the bintree
            String outputString =
                    name + " " + Double.parseDouble( longitudeX ) + " "
                            + Double.parseDouble( latitudeY )
                            + " duplicates a watcher already in the bintree";
            System.out.println( outputString );
        }
    }

    /**
     * Handles the removal of the passed subscriber, displays the output if
     * successful
     * 
     * @param s
     *            the string
     * @return whether the removal was a success
     */
    private Boolean removeSubscriberHelper( String[] s )
    {
        // Check to ensure the arguments are in the form: delete <user>a
        if ( s[0].contains( "delete" ) )
        {
            Double longitudeX = Double.parseDouble( s[1].trim() );
            Double latitudeY = Double.parseDouble( s[2].trim() );

            if ( removeWatcherFromBinTree( longitudeX, latitudeY ) )
            {
                // Displays the expected format:
                // <Watcher name> <x> <y> is removed from the bintree
                String removedFromBinTreeOutputString =
                        longitudeX + " " + latitudeY
                                + " is removed from the bintree";
                System.out.println( removedFromBinTreeOutputString );

                return true;
            }
            else
            {
                // Displays the expected format:
                // There is no record at <x> <y> in the bintree
                String removeFailed =
                        "There is no record at  " + longitudeX + " "
                                + latitudeY + " in the bintree";
                System.out.println( removeFailed );
            }
        }

        return false;
    }

    /**
     * Checks to see if duplicate coordinates exist, if not then it adds the
     * user to the bintree. Returns true if successful and false otherwise.
     * 
     * @param xLongitude
     *            The longitude coordinate
     * @param yLatitude
     *            The latitude coordinate
     * @param subscriberName
     *            name of subscriber
     * @return whether or not the watcher was added to the bintree
     */
    private Boolean addWatcherToBinTree( String xLongitude, String yLatitude,
            String subscriberName )
    {
        // Create a coordinate to insert into the BST
        Point2D.Double watcherCoordinate = new Point2D.Double();
        watcherCoordinate.setLocation( Double.parseDouble( xLongitude ),
                Double.parseDouble( yLatitude ) );

        // Ensure that a duplicate coordinate Point2D isn't inserted
        if ( !subscribersBintree_.inTree( watcherCoordinate ) )
        {
            subscribersBintree_.insert( watcherCoordinate, subscriberName );
            return true;
        }

        return false;
    }

    /**
     * Removes the user from the bintree, returns true if successful
     * 
     * @param xLongitude
     *            The longitude
     * @param yLatitude
     *            latitude coordinates
     * @return true if successful
     */
    private Boolean removeWatcherFromBinTree( double xLongitude,
            double yLatitude )
    {
        Point2D.Double watcherCoordinate = new Point2D.Double();
        watcherCoordinate.setLocation( xLongitude, yLatitude );

        if ( subscribersBintree_.inTree( watcherCoordinate ) )
        {
            subscribersBintree_.remove( watcherCoordinate );
            return true;
        }
        else
        {
            return false;
        }
    }
}