import java.awt.geom.Point2D;
import java.nio.ByteBuffer;

/**
 * 
 * @author Matthew Luckam
 * @author Jimmy Dagres
 * @version Dec 6, 2013
 * 
 */
public class Watcher
{

    Point2D.Double watcherPoint;

    String watcherName;

    public Watcher( double x, double y, String name )
    {
        watcherPoint = new Point2D.Double();
        watcherPoint.setLocation( x, y );
        watcherName = name;
    }

    public Point2D.Double getWatcherPoint()
    {
        return watcherPoint;
    }

    public void setWatcherPoint( Point2D.Double watcherPoint )
    {
        this.watcherPoint = watcherPoint;
    }

    public String getWatcherName()
    {
        return watcherName;
    }

    public void setWatcherName( String watcherName )
    {
        this.watcherName = watcherName;
    }

    public byte[] serialize()
    {
        byte[] x;
        byte[] y;
        byte[] name;
        x = ByteBuffer.allocate( 8 ).putDouble( watcherPoint.getX() ).array();
        y = ByteBuffer.allocate( 8 ).putDouble( watcherPoint.getY() ).array();
        name = watcherName.getBytes();
        byte[] data = new byte[16 + name.length];
        int currentPos = 0;
        for ( int i = 0; i < 8; currentPos++, i++ )
        {
            data[currentPos] = x[i];
        }
        for ( int i = 0; i < 8; currentPos++, i++ )
        {
            data[currentPos] = y[i];
        }
        for ( int i = 0; i < name.length; i++, currentPos++ )
        {
            data[currentPos] = name[i];
        }
        return data;
    }

}
