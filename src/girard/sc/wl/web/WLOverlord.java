package girard.sc.wl.web;


import girard.sc.awt.BorderPanel;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.LabelCanvas;
import girard.sc.io.NetObjectStream;
import girard.sc.web.WebPanel;
import girard.sc.web.WebResourceBundle;
import girard.sc.wl.io.msg.WLMessage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.image.ImageProducer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;

import javax.imageio.ImageIO;

/**
 * The WLOverlord is the core of ExNet III.  It provides the base
 * information necessary to run multi-user experiments over the web.
 * It has preset values for Button, Label, and Window fonts to help
 * ExNet III's look remain constant.
 *
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1
 */

public class  WLOverlord extends Panel
    {

/**
 *
 */ 
    private long   SECURITY_KEY = (Long.valueOf("3938457194759273")).longValue();
/**
 * Default location of the base directory where images are stored, can be changed by
 * adding the proper parameters to the html page that launched the Applet.
 */
    private String IMAGE_DIR = new String("/Java/ExSoc/images/");
/**
 * Default location of the base directory where the text files for the labels are 
 * stored, can be changed by adding the proper parameters to the html page that
 * launched the Applet.
 */
    private String LABEL_DIR = new String("/Java/ExSoc/labels/");

/**
 * Is the general access port;
 * Any messages that extend WLMessage should normally use this port.
 *
 * @see girard.sc.wl.io.msg.WLMessage
 * @see girard.sc.wl.web.WLOverlord#sendWLMessage(WLMessage wlm)
 */
    protected int    GENERAL_PORT = 8080;

/**
 * Is the location of the host computer that is acting as the Web-Server for the
 * applet;  Used by WLOverlord to know where to send messages and load images. 
 *
 * @see girard.sc.wl.web.WLOverlord#getImgURL(String image)
 * @see girard.sc.wl.web.WLOverlord#sendWLMessage(WLMessage wlm)
 */
    protected String HOST_NAME = new String("weblab.cs.ship.edu");
/**
 * The id of the user that is presently using ExNet 3.0;  Very important when determining
 * which files the user has access to.
 */
    protected int m_userID = -1;

    
/**
 * Is an address book giving the location of all the images that may be loaded
 * off the server;  Is not really used anymore, instead all images are assumed to
 * be stored off a base directory and the programmer simply passes in a static location
 * that is off of this directory paty; Probably something to be removed at a later date.
 */
    protected Hashtable<String,String> m_imgLoc;
/**
 * How wide the display area is in the Frame for ExNet 3.0's.
 */
    protected int m_width = 0;
/**
 * How tall the display area is the Frame for ExNet 3.0's.
 */
    protected int m_height = 0;

/**
 * A Preset color setting for making everything look similar.
 */
    protected Color m_dispBkgColor = Color.white;
/**
 * A Preset color setting for making everything look similar.
 */
    protected Color m_winBkgColor = Color.lightGray;
/**
 * A Preset color setting for making everything look similar.
 */
    protected Color m_buttonLabelColor = Color.lightGray;
/**
 * A Preset color setting for making everything look similar.
 */
    protected Color m_objectBkgColor = Color.lightGray;

/**
 * A Preset font setting for making everything look similar.
 */
    protected Font m_lgButtonFont = new Font("Monospaced",Font.BOLD,20);
/**
 * A Preset font setting for making everything look similar.
 */
    protected Font m_smButtonFont = new Font("Monospaced",Font.BOLD,16);
/**
 * A Preset font setting for making everything look similar.
 */
    protected Font m_titleFont = new Font("Monospaced",Font.BOLD,24);
/**
 * A Preset font setting for making everything look similar.
 */
    protected Font m_medLabelFont = new Font("Monospaced",Font.PLAIN,18);
/**
 * A Preset font setting for making everything look similar.
 */
    protected Font m_smLabelFont = new Font("Monospaced",Font.PLAIN,12);
/**
 * A Preset font setting for making everything look similar.
 */
    protected Font m_tinyLabelFont = new Font("Monospaced",Font.PLAIN,10);
/**
 * A Preset font setting for making everything look similar.
 */
    protected Font m_medWinFont = new Font("Monospaced",Font.PLAIN,16);
/**
 * A Preset font setting for making everything look similar.
 */
    protected Font m_smWinFont = new Font("Monospaced",Font.PLAIN,12);
/**
 * A Preset font setting for making everything look similar.
 */
    protected Font m_tinyWinFont = new Font("Monospaced",Font.PLAIN,10);

/**
 * Used as a flag, mainly for when keeping certain ActionEvents from getting processed.
 */
    protected boolean m_EditMode = false;

/**
 * The Label Language Lookup Table;
 * Allows multiple entries of the word or phrase to be stored and recalled 
 * based on language.
 *
 * @see girard.sc.web.WebResourceBundle
 */
    protected WebResourceBundle m_labels = new WebResourceBundle();

/**
 * Is the main JFrame for ExNet 3;  It is passed in when WLOverlord
 * is first initialized.
 */
    protected Frame m_WB;

/**
 *
 */
    protected GridBagPanel m_TitlePanel;
/**
 *
 */
    protected LabelCanvas m_TitleCanvas;
/**
 * Where the m_activePanel is displayed.
 */
    protected Panel m_WebpageBasePanel;
/**
 * The active WebPanel presently being displayed in the m_WebpageBasePanel.
 */
    protected WebPanel m_activePanel;

/**
 * A constructor for WLOverlord.
 *
 * @param app The JFrame running that created WLOverlord.
 */
    public WLOverlord(Frame app)
        {
        Font f1 = new Font("TimesRoman",Font.BOLD,18);
        Font f2 = new Font("TimesRoman",Font.BOLD,12);

        m_WB = app;

        setBackground(Color.lightGray);
        setLayout(new BorderLayout());

        m_TitlePanel = new GridBagPanel();

        m_WebpageBasePanel = new Panel(new GridLayout(1,1));

        add("North",new BorderPanel(m_TitlePanel));

        add("Center",new BorderPanel(m_WebpageBasePanel));
        }
/**
 * A constructor for WLOverlord.
 *
 * @param app The JFrame running that created WLOverlord.
 * @param width The value for m_width.
 * @param height The value for m_height.
 */
    public WLOverlord(Frame app, int width, int height)
        {
        Font f1 = new Font("TimesRoman",Font.BOLD,18);
        Font f2 = new Font("TimesRoman",Font.BOLD,12);

        m_WB = app;
        m_width = width;
        m_height = height;     

        setBackground(Color.lightGray);
        setLayout(new BorderLayout());

        m_TitlePanel = new GridBagPanel();

        m_WebpageBasePanel = new Panel(new GridLayout(1,1));

        add("North",new BorderPanel(m_TitlePanel));

        add("Center",new BorderPanel(m_WebpageBasePanel));
        }    

/**
 * Add a WebPanel to be displayed within the m_WebpageBasePanel.
 *
 * @param A The WebPanel to be displayed.
 */
    public void addPanel(WebPanel A)
        {
        A.initializeLabels();
        m_activePanel = A;
        m_WebpageBasePanel.add(A);
        m_TitleCanvas.setLabel("Web-Lab - "+A.getTitle());
        m_TitleCanvas.centerLabel();
        m_WebpageBasePanel.validate();
        }

/**
 * Used to create an Image object for drawing on.
 * Uses m_WB to call createImage().
 *
 * @param producer The ImageProducer creating the image.
 * @return The java.awt.Image object just created.
 * @see java.awt.Component#createImage(ImpageProducer producer)
 * @see java.awt.Image
 */
    public Image createImage(ImageProducer producer)
        {
        return m_WB.createImage(producer);
        }

/**
 * Used to create an Image object for drawing on.
 * Uses m_WB to call createImage().
 *
 * @param width How wide to make the image in pixels.
 * @param height How tall to make the image in pixels.
 * @return The java.awt.Image object just created.
 * @see java.awt.Component#createImage(int width, int height)
 * @see java.awt.Image
 */
    public Image createImage(int width, int height)
        {
        return m_WB.createImage(width,height);
        }

/**
 * Right now there is no means built into ExNet 3.0's interface to change
 * the active language.
 *
 * @return Returns the active language to display all labels in if possible.
 */
    public String getActiveLanguague()
        {
        return m_labels.getActiveLanguage();
        }
/**
 * @return Returns m_activePanel.
 */
    public WebPanel getActivePanel()
        {
        return m_activePanel;
        }

/**
 * @return Returns a defalut image for some of the GraphicButton objects used by 
 * ExNet 3.0.
 */
    public Image getButtonImage()
        {
        return getImage("images/girard/sc/wl/web/burgun.jpg");
        }
/**
 * @return Returns the value for m_buttonLabelColor.
 */
    public Color getButtonLabelColor()
        {
        return m_buttonLabelColor;
        }
/**
 * @return Returns the value for the m_dispBkgColor.
 */
    public Color getDispBkgColor()
        {
        return m_dispBkgColor;
        }
/**
 * @return Returns the value for the m_EditMode.
 */
    public boolean getEditMode()
        {
        return m_EditMode;
        }
/**
 * @return Returns the value for m_height.
 */
    public int getHeight()
        {
        return m_height;
        }
/**
 * Used to retrieve an Image object from a URL address.  The URL address is
 * created by calling getImgURL.  Uses a MediaTracker to ensure the Image returned
 * has been fully loaded across the network.
 *
 * @param loc The URL address of the image.
 * @return The java.awt.Image object just loaded,  will return null if fails
 * find the image.
 * @see girard.sc.wl.web.WLOverlord#getImgURL(String image)
 * @see java.awt.Image
 */
    public Image getImage(String loc)
        {
        Image tmp = null;

        ClassLoader myCL = WLOverlord.class.getClassLoader();
        
	try
	{
	    tmp = ImageIO.read(myCL.getResourceAsStream(loc));
	} 
	catch (IOException e)
	{
	    e.printStackTrace();
	}

        return tmp;
        }
    
    

/**
 * Used to get the WebResourceBundle object which contains labels grouped together
 * based on language.
 *
 * @return The WebResourceBundle object m_labels.
 * @see girard.sc.wl.web.WLOverlord#m_labels
 * @see girard.sc.web.WebResourceBundle
 */
    public WebResourceBundle getLabels()
        {
        return m_labels;
        }
/**
 * @return Returns the value for m_lgButtonFont.
 */
    public Font getLgButtonFont()
        {
        return m_lgButtonFont;
        }
/**
 * @return Returns the value for m_medLabelFont.
 */
    public Font getMedLabelFont()
        {
        return m_medLabelFont;
        }
/**
 * @return Returns the value for m_medWinFont.
 */
    public Font getMedWinFont()
        {
        return m_medWinFont;
        }
/**
 * @return Returns the value for m_objectBkgColor.
 */
    public Color getObjectBkgColor()
        {
        return m_objectBkgColor;
        }
/**
 * @return Returns the value for m_smButtonFont.
 */
    public Font getSmButtonFont()
        {
        return m_smButtonFont;
        }
/**
 * @return Returns the value for m_smLabelFont.
 */
    public Font getSmLabelFont()
        {
        return m_smLabelFont;
        }
/**
 * @return Returns the value for m_smWinFont.
 */
    public Font getSmWinFont()
        {
        return m_smWinFont;
        }
/**
 * @return Returns the value for m_tinyLabelFont.
 */
    public Font getTinyLabelFont()
        {
        return m_tinyLabelFont;
        }
/**
 * @return Returns the value for m_tinyWinFont.
 */
    public Font getTinyWinFont()
        {
        return m_tinyWinFont;
        }
/**
 * @return Returns the value for m_userID.
 */
    public int getUserID()
        {
        return m_userID;
        }
/**
 * @return Returns the value for m_width.
 */
    public int getWidth() 
        {
        return m_width;
        }
/**
 * @return Returns the value for m_winBkgColor.
 */
    public Color getWinBkgColor()
        {
        return m_winBkgColor;
        }
/**
 * Useful if you need to use one of the JFrame functions,
 * as the WLOverlord is not the main JFrame.
 *
 * @return Returns the main JFrame.
 * @see girard.sc.wl.web.WLOverlord#m_WB
 * @see java.swing.JFrame
 */
    public Frame getWB()
        {
        return m_WB;
        }

/**
 * Will load in a set of labels to be added to m_labels.  Format of str should be 
 * package/package/java filename.txt,
 * <p>
 * <li>Example: girard/sc/be/awt/RoundWindow.txt
 * <p>
 * The actual text file that is read in is formatted as follows:
 * <p> 
 * <ul>
 * <li>language (English, Spainish, etc...)
 * <li>word key (baw_ok)
 * <li>actual word (OK)
 * <li>.
 * <li>.
 * <li>.
 * </ul>
 * @param str The directory path and name of the file with the labels.
 * @see girard.sc.wl.web.WLOverlord#m_labels
 */
    public void initializeLabels(String str)
        {
        URL labelURL = null;

        int counter = 0;

        while ((labelURL == null) && (counter < 5))
            {
            try
                {
                labelURL = new URL("http://"+HOST_NAME+LABEL_DIR+str);
                           }
            catch(MalformedURLException e) { }
            counter++;
            }

        if (labelURL == null)
            return;

        URLConnection urlCon = null;
        BufferedReader in = null;

        counter = 0;

        while ((urlCon == null) && (counter < 5))
            {
            try
                {
                urlCon = labelURL.openConnection();
            
                in = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
                }
            catch(Exception e) { e.printStackTrace(); }
     
            counter++;
            }

        try
            {
       // Now read in the HTML line by line
            String lang = "None";
            String key = "None";
            String label = "None";
            String inputline = in.readLine();

            counter = 0;

            while(inputline != null ) 
                {
                if (counter == 0)
                    lang = inputline;
                if (counter == 1)
                    key = inputline;
                if (counter == 2)
                    label = inputline;
                counter++;

                if (counter == 3)
                    {
                    m_labels.addObjectLabel(lang,key,label);
                    counter = 0;
                    }
                inputline = in.readLine();
                }

            in.close();
            }
        catch(Exception e) { e.printStackTrace(); }
        }

/**
 * Used to initialize a WLMessage before it is sent.  Must be called on any created
 * WLMessage.  To make things easier this function is automatically called by 
 * sendWLMessage.
 *
 * @param msg The WLMessage to be initialized.
 */
    public void initializeWLMessage(WLMessage msg)
        {
        msg.setSecurityKey(SECURITY_KEY);
        msg.setUserID(m_userID);
        }

/**
 * Reads all the parameter settings from the web page.
 *
 * @param validate Whether to validate the user or not.
 * @return Returns true if processed everything correctly, false otherwise.
 * @see girard.sc.wl.web.WLOverlord#HOST_NAME
 */
    public boolean loadParameters(boolean validate)
        {
        IMAGE_DIR = "images";
        LABEL_DIR = "labels";
        GENERAL_PORT = 8080;

        if (m_width == 0)
            m_width = 800;
        if (m_height == 0)
            m_height = 600;

        return true;
        }

/**
 * Sets up the m_TitlePanel and displays it.
 */
    public void placeTitlePanel()
        {
        createTitleCanvas();
        m_TitlePanel.constrain(m_TitleCanvas,1,1,1,1,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
        m_TitlePanel.validate();
        }
    
    /**
     * Used to read in a text file in the JAR and stores it in the provided StringBuffer.
     * @param helpFile
     * @param dataFile
     * @throws IOException
     */
    public void readInFile(String file, StringBuffer dataFile)
    {
	ClassLoader myCL = WLOverlord.class.getClassLoader();
        
        BufferedReader in = new BufferedReader(new InputStreamReader(myCL.getResourceAsStream(file)));
        
	try
	{
	    // Now read in the HTML line by line
	    String inputline = in.readLine();
	    while (inputline != null)
	    {
		dataFile.append(inputline + "\n");
		inputline = in.readLine();
	    }

	    in.close();
	}
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
/**
 * Will remove a set of labels from m_labels.  Format of str should be 
 * package/package/java filename.txt,
 * <p>
 * <li>Example: girard/sc/be/awt/RoundWindow.txt
 * <p>
 * Is the same file that was used with initializeLabels.
 * <p> 
 *
 * @param str The directory path and name of the file with the labels.
 * @see girard.sc.wl.web.WLOverlord#m_labels
 * @see girard.sc.wl.web.WLOverlord#initializeLabels(String str)
 */
    public void removeLabels(String str)
        {
        try
            {
            URL labelURL = new URL("http://"+HOST_NAME+LABEL_DIR+str);
            URLConnection urlCon = labelURL.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));

       // Now read in the HTML line by line
            String inputline = in.readLine();
            String lang = "None";
            String key = "None";
            String label = "None";

            int counter = 0;

            while(inputline != null ) 
                {
                if (counter == 0)
                    lang = inputline;
                if (counter == 1)
                    key = inputline;
                if (counter == 2)
                    label = inputline;
                counter++;

                if (counter == 3)
                    {
                    m_labels.removeObjectLabel(lang,key);
                    counter = 0;
                    }
                inputline = in.readLine();
                }

            in.close();
            }
        catch(MalformedURLException e) { e.printStackTrace(); }
        catch(Exception e) { e.printStackTrace(); }
        }

/**
 * Removes a WebPanel from being dipslayed in m_WebpageBasePanel and sets m_activePanel
 * to null.
 *
 * @param R The WebPanel to be removed.
 */
    public void removePanel(WebPanel R)
        {
        R.removeLabels();
        m_activePanel = null;
        m_WebpageBasePanel.remove(R);
        m_TitleCanvas.setLabel("Web-Lab");
        m_TitleCanvas.centerLabel();
        m_WebpageBasePanel.validate();
        }
/**
 * Removes a WebPanel from being dipslayed in m_WebpageBasePanel and then adds 
 * a new WebPanel to be displayed within the m_WebpageBasePanel.
 *
 * @param R The WebPanel to be removed.
 * @param A The WebPanel to be displayed.
 */
    public void removeThenAddPanel(WebPanel R, WebPanel A)
        {
        R.removeLabels();
        m_WebpageBasePanel.remove(R);
        A.initializeLabels();
        m_activePanel = A;
        m_WebpageBasePanel.add(A);
        m_TitleCanvas.setLabel("Web-Lab - "+A.getTitle());
        m_TitleCanvas.centerLabel();
        m_WebpageBasePanel.validate();
        }

/**
 * Resets m_dispBkgColor, m_winBkgColor, and m_buttonLabelColor to their default
 * settings.  Not sure if I ever use this though.
 */
    public void resetViewSettings()
        {
        m_dispBkgColor = Color.white;
        m_winBkgColor = Color.lightGray;
        m_buttonLabelColor = Color.lightGray;
        }

/**
 * Used to send a WLMessage to the server.  Sends the message off to the server
 * listening at the GENERAL_PORT and returns any message sent back.  If no message
 * sent back it returns null.  Calls initializeWLMessage before sending the message.
 *
 * @param wlm The WLMessage to be sent.
 * @return The WLMessage sent back as a response, null if no message sent back.
 * @see girard.sc.wl.web.WLOverlord#HOST_NAME
 * @see girard.sc.wl.web.WLOverlord#GENERAL_PORT
 * @see girard.sc.wl.io.msg.WLMessage
 */
    public WLMessage sendWLMessage(WLMessage wlm)
        {
        NetObjectStream nos = new NetObjectStream(HOST_NAME,GENERAL_PORT);
        initializeWLMessage(wlm);

        int counter = 0;
        while ((!nos.getFlag()) && (counter < 5))
            {
            nos = new NetObjectStream(HOST_NAME,GENERAL_PORT);
            counter++;
            }

        if (nos.getFlag())
            {
            try 
                {
                nos.sendMessage(wlm);

                WLMessage wlmNew = (WLMessage)nos.getNextMessage();

                nos.close();

                return wlmNew;
                }
            catch (IOException ioe) { System.err.println(ioe); }
            catch (ClassNotFoundException cnfe) { System.err.println(cnfe); }
            }

        return null;
        }
/**
 * Sets the active language when reading Strings from m_labels.
 *
 * @param str The active language.
 * @see girard.sc.wl.web.WLOverlord#m_labels
 * @see girard.sc.web.WebResourceBundle
 */
    public void setActiveLanguague(String str)
        {
        m_labels.setActiveLanguague(str);
        }
/**
 * Changes the value of m_buttonLabelColor.  Also updates the m_TitleCanvas
 * as it uses this color for its m_labelColor.
 *
 * @param value The new Color to make m_buttonLabelColor.
 */
    public void setButtonLabelColor(Color value)
        {
        m_buttonLabelColor = value;
        m_TitleCanvas.setLabelColor(m_buttonLabelColor);
        }
/**
 * Changes the value of m_dispBkgColor.
 *
 * @param value The new Color to make m_dispBkgColor.
 */
    public void setDispBkgColor(Color value)
        {
        m_dispBkgColor = value;
        }
/**
 * Changes the value of m_EditMode.
 *
 * @param value The new boolean value for m_EditMode.
 */
    public void setEditMode(boolean value)
        {
        m_EditMode = value;
        }
/**
 * Changes the value of m_objectBkgColor.
 *
 * @param value The new Color to make m_objectBkgColor.
 */
    public void setObjectBkgColor(Color value)
        {
        m_objectBkgColor = value;
        }
/**
 * Changes the value of m_userID.
 *
 * @param value The new value to set m_userID to.
 */
    public void setUserID(int value)
        {
        m_userID = value;
        }
/**
 * Changes the value of m_winBkgColor.
 *
 * @param value The new Color to make m_winBkgColor.
 */
    public void setWinBkgColor(Color value)
        {
        m_winBkgColor = value;
        }
/**
 * Changes the size of m_WebpageBasePanel.
 *
 * @param x The new preferred width for m_WebpageBasePanel.
 * @param y The new preferred height of m_WebpageBasePanel.
 */
    public void setWPBPSize(int x, int y)
        {
        m_WebpageBasePanel.setSize(x,y);
        }

/**
 * Creates the m_TitleCanvas.
 */
    private void createTitleCanvas()
        {
        Graphics g;
        Image tmp1, tmp2;

        // Initialize Load Button Image
        tmp1 = this.getImage("images/girard/sc/wl/web/burgun.jpg");

        tmp2 = this.createImage(tmp1.getWidth(null),tmp1.getHeight(null));
        
        g = tmp2.getGraphics();

        g.drawImage(tmp1,0,0,m_WB);

        m_TitleCanvas = new LabelCanvas(m_width,50,tmp2,"Web-Lab");
        m_TitleCanvas.setFont(m_titleFont);
        m_TitleCanvas.centerLabel();
        m_TitleCanvas.setLabelColor(m_buttonLabelColor);
        }
    }
