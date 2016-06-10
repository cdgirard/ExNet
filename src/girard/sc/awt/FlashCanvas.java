package girard.sc.awt;

import java.awt.image.*;
import java.awt.*;

public class FlashCanvas extends Canvas
{
    boolean flashOne;

    boolean flashTwo;

    public FlashCanvas()
    {
        this.flashOne = false;
        this.flashTwo = false;
    }

    public void flashOne()
    {
        this.flashOne = true;
    }

    public void flashTwo()
    {
        this.flashTwo = true;
    }

    public void startFlashOne(final int count)
    {
        final FlashThread ft = new FlashThread(count, this, 1);
        ft.start();
    }

    public void startFlashTwo(final int count)
    {
        final FlashThread ft = new FlashThread(count, this, 2);
        ft.start();
    }

    public void paint(final Graphics g)
    {
        final Dimension dim = this.getSize();
        final Image img = this.createImage(dim.width, dim.height);
        final Graphics g2 = img.getGraphics();
        if (this.flashOne)
        {
            g2.setColor(Color.black);
            g2.fillRect(3, 3, 10, 10);
            this.flashOne = false;
        }
        else
        {
            g2.setColor(Color.white);
            g2.fillRect(3, 3, 10, 10);
        }
        if (this.flashTwo)
        {
            g2.setColor(Color.black);
            g2.fillRect(40, 3, 10, 10);
            this.flashTwo = false;
        }
        else
        {
            g2.setColor(Color.white);
            g2.fillRect(40, 3, 10, 10);
        }
        if (img != null)
        {
            g.drawImage(img, 0, 0, this);
        }
    }

    public void update(final Graphics g)
    {
        this.paint(g);
    }
}
