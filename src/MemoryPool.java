import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author Jimmy Dagres
 * @author Matt Luckam
 * 
 * @version Dec 5, 2013
 * 
 *          This class creates the file and handles it's length and everything
 *          file related.
 * 
 */
public class MemoryPool
{
    /**
     * The constant final name of the file being created
     */
    public static final String DISK_FILE_NAME = "p4bin.dat";

    private RandomAccessFile disk_;

    /**
     * Basic constructor
     * 
     * @throws FileNotFoundException
     */
    MemoryPool() throws FileNotFoundException
    {
        createFileReference();
    }

    /**
     * Creates the random access file
     * 
     * @throws FileNotFoundException
     */
    private void createFileReference() throws FileNotFoundException
    {
        disk_ = new RandomAccessFile( DISK_FILE_NAME, "rw" );
    }

    /**
     * @return the length of the file
     * @throws IOException
     */
    public long getFileLength() throws IOException
    {
        return disk_.length();
    }

    /**
     * @return the disk_
     */
    public RandomAccessFile getDisk()
    {
        return disk_;
    }

    /**
     * @return the disk file
     */
    public RandomAccessFile getDiskFile()
    {
        return disk_;
    }

    /**
     * Closes the file.
     * 
     * @throws IOException
     */
    public void closeFile() throws IOException
    {
        disk_.close();
    }
}
