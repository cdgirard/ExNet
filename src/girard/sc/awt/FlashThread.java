package girard.sc.awt;

public class FlashThread extends Thread
{
    FlashCanvas canvas;

    int flashCount;

    int sensor;

    public FlashThread(final int count, final FlashCanvas theCanvas, final int s)
    {
        this.flashCount = 2;
        this.flashCount = count;
        this.canvas = theCanvas;
        this.sensor = s;
    }

    public void run()
    {
        while (this.flashCount > 0)
        {
            --this.flashCount;
            if (this.sensor == 1)
            {
                this.canvas.flashOne();
            }
            else
            {
                this.canvas.flashTwo();
            }
            this.canvas.repaint();
            try
            {
                Thread.sleep(100L);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            this.canvas.repaint();
            try
            {
                Thread.sleep(150L);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
