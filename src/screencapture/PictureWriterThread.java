package screencapture;

import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.imageio.ImageIO;

public class PictureWriterThread extends Thread
{
    private ConcurrentLinkedQueue<PicNode> data; 
    private boolean running;
    
    /**
     * Default Constructor
     * Creates a new ConcurrentLinkedQueue to hold data taken from the PictureTakerThread.
     * Sets running to true; This makes our loop in run tick.
     */
    public PictureWriterThread()
    {
        data = new ConcurrentLinkedQueue<PicNode>();
        this.running = true;
    }
    
    /**
     * Main operation of the PictureWriterThread.
     * Converts the data from the PictureTakerThread and turns them into images.
     */
    @Override
    public void run()
    {
        // TODO: DEBUG prints out that the PictureWriterThread has started.
        System.out.println("PictureWriter has started.");
        // Sets a node that is going to be reused many times to null.
        PicNode pn = null;
        while(running)
        {
            // Write out a file
            write(pn);
        }
        // Write out everything left in the buffer.
        while(!data.isEmpty())
        {
            write(pn);
        }
        // TODO: DEBUG prints out that the PictureWriterThread has ended.
        System.out.println("PictureWriterThread has ended.");
    }
    
    /**
     * Creates a .jpg image of the next file in the ConcurrentLinkedQueue data.
     * @param pn A PictureNode used to hold the data removed from the ConcurrentLinkedQueue.
     */
    private synchronized void write(PicNode pn)
    {
        try
        {
            if(data != null && !data.isEmpty())
            {
                pn = data.remove();
                ImageIO.write(pn.getImage(), "jpg", new File(pn.getFilePath()));
            }
            else
            {
                this.yield();
            }
        }
        catch(Exception ex){}
    }
    
    /**
     * Ends the main loop in the run method.
     */
    public synchronized void kill()
    {
        this.running = false;
    }
    
    /**
     * Adds data from the PictureTakerThread to this thread.
     * @param data ConcurrentLinkedQueue containing the most recent pictures taken by the PictureTakerThread.
     */
    public synchronized void addData(ConcurrentLinkedQueue<PicNode> data)
    {
        this.data.addAll(data);
    }
    
    /**
     * @return If there is still data in the ConcurrentLinkedQueue return true. 
     */
    public synchronized boolean hasData()
    {
        return (this.data.size() > 0 ? true : false);
    }
}