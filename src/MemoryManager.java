import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author Jimmy Dagres
 * @author Matt Luckam
 * 
 * @version Dec 6, 2013
 *
 */
public class MemoryManager
{

    private byte array[];
    private int currentPos;

    public MemoryManager()
    {
        array = new byte[1000];
        currentPos = 0;
    }

    public byte[] insert( byte[] newData )
    {
        Integer handleLocation = currentPos;

        // adds 2 byte length of message to a watcher
        if ( newData.length > 9 )
        {
            byte[] messageLength =
                    ByteBuffer.allocate( 2 ).putShort( (short) newData.length )
                            .array();

            for ( int i = 0; i < messageLength.length; i++, currentPos++ )
            {
                array[currentPos] = messageLength[i];
            }
        }

        for ( int i = 0; i < newData.length; i++, currentPos++ )
        {
            array[currentPos] = newData[i];
        }

        return ByteBuffer.allocate( 4 ).putInt( handleLocation ).array();
    }

    public byte[] getNode( byte[] handle )
    {
        byte[] toReturn = null;

        int position = ByteBuffer.wrap( handle ).getInt();

        // return internal node
        if ( array[position] == 0 )
        {
            toReturn = new byte[9];
            for ( int i = 0; i < 9; i++, position++ )
            {
                toReturn[i] = array[position];
            }
        }
        // return leaf node
        else if ( array[position] == 1 )
        {
            toReturn = new byte[5];
            for ( int i = 0; i < 5; i++, position++ )
            {
                toReturn[i] = array[position];
            }
        }

        return toReturn;
    }
}
