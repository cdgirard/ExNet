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

public class  WLOverlord extends Frame
    {

/**
 *
 */ 
    private long   SECURITY_KEY = (Long.valueOf("3938457194759273")).longValue();
/**
 * Default location of the base directory where images are stored, can be changed by
 * adding the proper parameters to the html page that launched the Applet.
 */
     public static final String IMAGE_DIR = new String("images/");
/**
 * Default location of the base directory where the text files for the labels are 
 * stored, can be changed by adding the proper parameters to the html page that
 * launched the Applet.
 */
    public static final String LABEL_DIR = new String("labels/");

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
    protected int m_width = 800;
/**
 * How tall the display area is the Frame for ExNet 3.0's.
 */
    protected int m_height = 600;

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
 *
 */
    protected GridBagPanel m_titlePanel;
/**
 *
 */
    protected LabelCanvas m_titleCanvas;
/**
 * Where the m_activePanel is displayed.
 */
    protected Panel m_webpageBasePanel;
/**
 * The active WebPanel presently being displayed in the m_WebpageBasePanel.
 */
    protected WebPanel m_activePanel;

/**
 * A constructor for WLOverlord.
 *
 */
    public WLOverlord()
        {
        setBackground(Color.lightGray);
        setLayout(new BorderLayout());

        m_titlePanel = new GridBagPanel();
        placeTitlePanel();

        m_webpageBasePanel = new Panel(new GridLayout(1,1));

        add(BorderLayout.NORTH, new BorderPanel(m_titlePanel));

        add(BorderLayout.CENTER, new BorderPanel(m_webpageBasePanel));
        
        setSize(m_width,m_height);
        setVisible(true);
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
        m_webpageBasePanel.add(A);
        m_titleCanvas.setLabel("Web-Lab - "+A.getTitle());
        m_titleCanvas.centerLabel();
        m_webpageBasePanel.validate();
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
        return getImage("girard/sc/wl/web/burgun.jpg");
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
	    tmp = ImageIO.read(myCL.getResourceAsStream(IMAGE_DIR+loc));
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

	StringBuffer data = new StringBuffer();
	String labelLoc = LABEL_DIR + str;
	readInFile(labelLoc, data);
	String[] lines = data.toString().split("\n");

	int counter = 0;

	// Now read in the HTML line by line
	String lang = "None";
	String key = "None";
	String label = "None";
	String inputline;

	while (counter < lines.length)
	{
	    inputline = lines[counter];
            int value = counter % 3;
	    if (value == 0)
		lang = inputline;
	    if (value == 1)
		key = inputline;
	    if (value == 2)
		label = inputline;

	    if (value == 2)
	    {
		m_labels.addObjectLabel(lang, key, label);
	    }
	    counter++;
	}
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
 * Sets up the m_TitlePanel and displays it.
 */
    public void placeTitlePanel()
        {
        createTitleCanvas();
        m_titlePanel.constrain(m_titleCanvas,1,1,1,1,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
        m_titlePanel.validate();
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
	String labelLoc = LABEL_DIR + str;
	StringBuffer data = new StringBuffer();
	readInFile(labelLoc, data);
	String[] lines = data.toString().split("\n");

	// Now read in the HTML line by line
	String inputline;
	String lang = "None";
	String key = "None";

	int counter = 0;

	while (counter < lines.length)
	{
	    inputline = lines[counter];
	    int value = counter % 3;
	    if (value == 0)
		lang = inputline;
	    if (value == 1)
		key = inputline;

	    if (value == 2)
	    {
		m_labels.removeObjectLabel(lang, key);
	    }
	    counter++;
	}
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
        m_webpageBasePanel.remove(R);
        m_titleCanvas.setLabel("Web-Lab");
        m_titleCanvas.centerLabel();
        m_webpageBasePanel.validate();
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
        m_webpageBasePanel.remove(R);
        A.initializeLabels();
        m_activePanel = A;
        m_webpageBasePanel.add(A);
        m_titleCanvas.setLabel("Web-Lab - "+A.getTitle());
        m_titleCanvas.centerLabel();
        m_webpageBasePanel.validate();
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
        m_titleCanvas.setLabelColor(m_buttonLabelColor);
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
        m_webpageBasePanel.setSize(x,y);
        }

/**
 * Creates the m_TitleCanvas.
 */
    private void createTitleCanvas()
        {
        Graphics g;
        Image tmp1;

        // Initialize Load Button Image
        tmp1 = this.getImage("girard/sc/wl/web/burgun.jpg");

        //tmp2 = this.createImage(tmp1.getWidth(null),tmp1.getHeight(null));
        
        g = tmp1.getGraphics();

        g.drawImage(tmp1,0,0,null);

        m_titleCanvas = new LabelCanvas(m_width,50,tmp1,"Web-Lab");
        m_titleCanvas.setFont(m_titleFont);
        m_titleCanvas.centerLabel();
        m_titleCanvas.setLabelColor(m_buttonLabelColor);
        }
    }
