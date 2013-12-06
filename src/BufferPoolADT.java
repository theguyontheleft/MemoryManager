import java.io.IOException;

/** Source code example for "A Practical Introduction to Data
 Structures and Algorithm Analysis, 3rd Edition (Java)" 
 by Clifford A. Shaffer
 Copyright 2008-2011 by Clifford A. Shaffer
 */

/**
 * ADT for buffer pools using the message-passing style
 * 
 * @author Matthew Luckam
 * @author James Dagres
 * @version Nov 5, 2013
 * 
 */
public interface BufferPoolADT
{
    /**
     * Copy "sz" bytes from "space" to position "pos" in the buffered storage
     * @param space 
     * @param sz 
     * @param pos 
     * 
     * @throws IOException
     */
    public void insert( byte[] space, int sz, int pos ) throws IOException;

    /**
     * Copy "sz" bytes from position "pos" of the buffered storage to "space".
     * @param space 
     * @param sz 
     * @param pos 
     * 
     * @throws IOException
     */
    public void getbytes( byte[] space, int sz, int pos ) throws IOException;
}
