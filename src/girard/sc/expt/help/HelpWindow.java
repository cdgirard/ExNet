package girard.sc.expt.help;

import girard.sc.awt.GridBagPanel;
import girard.sc.awt.SortedFixedList;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * This is the base window for accessing the help system for the ExNet III system.
 * It loads in two index listing for the help files. These lists tell which files
 * are available and where to find them.
 * <p>
 * Started: 10-1-2001
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.3
 * @since JDK1.1 
*/

public class HelpWindow extends Frame implements ActionListener
    {
    ExptOverlord m_EOApp;

    MenuBar m_menuBar;
    Menu m_file, m_contents, m_help;

/**
 * Allows the list of help indexes to be displayed alpha-numerically.
 *
 */
    SortedFixedList m_helpIndexList;
    Hashtable m_helpAddresses = new Hashtable();
/**
 * Used to remove all help labels we added.
 *
 */
    Vector m_labelEntries = new Vector();
/**
 * Ties the list entries to their key values.
 *
 */
    Vector m_helpIndexes = new Vector();

/**
 * Where the actual help information is displayed.
 *
 */
    TextArea m_helpInfo;

/**
 * Keeps track of any images attached to a help file. Help files are allowed 
 * to display zero to many windows of images to help provide any additional 
 * information.
 * 
 */
    Vector m_figureWindows = new Vector();

    public HelpWindow(ExptOverlord app)
        {
        super();
        m_EOApp = app;

        initializeLabels();

        setLayout(new BorderLayout());
        setTitle(m_EOApp.getLabels().getObjectLabel("hw_title"));
        setFont(m_EOApp.getMedWinFont());
        setBackground(m_EOApp.getWinBkgColor());

   // Setup the Menubar.
        m_menuBar = new MenuBar();

        m_menuBar.setFont(m_EOApp.getSmWinFont());

        setMenuBar(m_menuBar);
    
        MenuItem tmpMI;

        m_file = new Menu(m_EOApp.getLabels().getObjectLabel("hw_file"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("hw_exit"));
        tmpMI.addActionListener(this);
        m_file.add(tmpMI);

        m_menuBar.add(m_file);

   // Edit Menu
        m_contents = new Menu(m_EOApp.getLabels().getObjectLabel("hw_contents"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("hw_retrieve"));
        tmpMI.addActionListener(this);
        m_contents.add(tmpMI);

        m_menuBar.add(m_contents);
 
    // Help Menu

        m_help = new Menu(m_EOApp.getLabels().getObjectLabel("hw_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("hw_help"));
        tmpMI.addActionListener(this);
        m_help.add(tmpMI);

        m_menuBar.add(m_help);

    // Setup Center Panel.
        GridBagPanel CenterPanel = new GridBagPanel();

        m_helpInfo = new TextArea("",20,50,TextArea.SCROLLBARS_VERTICAL_ONLY);
        m_helpInfo.setEditable(false);
        m_helpInfo.setBackground(Color.white);
        // m_helpInfo.setLocale(Locale.JAPAN);

        CenterPanel.constrain(m_helpInfo,1,1,4,4);
    // End Setup for Center Panel

    // Setup East Panel.
        GridBagPanel EastPanel = new GridBagPanel();

        EastPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("hw_index")),1,1,4,1,GridBagConstraints.CENTER);
    
        fillHelpIndex();
        fillHelpAddress();

        m_helpIndexList = new SortedFixedList(20,false,1,35);
        m_helpIndexList.setFont(m_EOApp.getSmWinFont());
        m_helpIndexList.addActionListener(this);

        Enumeration enm = m_helpAddresses.keys();
        while (enm.hasMoreElements())
            {
            String key = (String)enm.nextElement();
            String[] strEntry = new String[1];
            strEntry[0] = m_EOApp.getLabels().getObjectLabel(key);
            m_helpIndexList.addItem(strEntry);
            m_helpIndexes.insertElementAt(key,m_helpIndexList.last);
            }
        EastPanel.constrain(m_helpIndexList,1,2,4,4,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
   // End Setup for East Panel.

        add("Center",CenterPanel);
        add("East",EastPanel);
        pack();
        show();
        }

/**
 * Processes any action events for the HelpWindow.
 * <br>Exit -> Calls the dispose() function.
 * <br>Retrieve -> Removes present help information and displays the selected help file.
 * <br>Help -> Will call up the help file for the HelpWindow itself.
 * <br>Double Click on the m_helpIndexList will have the same effect as Retrieve.
 *
 * @param e The ActionEvent.
 */
    public void actionPerformed(ActionEvent e)
        {

        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();

            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("hw_exit")))
                {
                dispose();
                return;
                }
            if ((theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("hw_retrieve"))) && (m_helpIndexList.getSelectedIndex() != -1))
                {
                cleanUpHelpFile();
                String key = (String)m_helpIndexes.elementAt(m_helpIndexList.getSelectedIndex());
                displayHelpPage(key);
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("hw_help")))
                {
                }
            }
        if (e.getSource() instanceof SortedFixedList)
            {
            SortedFixedList theSource = (SortedFixedList)e.getSource();
            
            if ((theSource == m_helpIndexList) && (m_helpIndexList.getSelectedIndex() > -1))
                {
                cleanUpHelpFile();
                String key = (String)m_helpIndexes.elementAt(m_helpIndexList.getSelectedIndex());
                displayHelpPage(key);
                }
            }
        }

    private void cleanUpHelpFile()
        {
        m_helpInfo.setText("");

        Enumeration enm = m_figureWindows.elements();
        while(enm.hasMoreElements())
            {
            Frame f = (Frame)enm.nextElement();
            f.dispose();
            }
        m_figureWindows.removeAllElements();
        }

/**
 * Used to dispose of the HelpWindow properly.
 *
 */
    public void dispose()
        {
        cleanUpHelpFile();
        m_EOApp.removeHelpWindow();
        removeLabels();
        super.dispose();
        }

    

/**
 * Used to display a help file.  The parameter passed in gives the location
 * for the help file information.
 *
 * @param key Gives the location of the help file information.
 */
    public void displayHelpPage(String key)
        {
        try
            {
            String engHelpFile = new String("");
            String engHelpFileTitle = new String("");
            Vector engImageFiles = new Vector();
            Vector engImageTitles = new Vector();

            String helpFile = new String("");
            String helpFileTitle = new String("");
            Vector imageFiles = new Vector();
            Vector imageTitles = new Vector();

            boolean haveLang = false;

            String location = (String)m_helpAddresses.get(key);
    
            String helpLoc = m_EOApp.getHelpLoc(location);
            BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(helpLoc)));

       // Now read in the HTML line by line
            String lang = "None";
            String type = "None";
            String loc = "None";
            String title = "None";

            int counter = 0;

            StringBuffer dataFile = new StringBuffer("");
            String inputline = in.readLine();
            while(inputline != null ) 
                {
                if (counter == 0)
                    lang = inputline;
                if (counter == 1)
                    type = inputline;
                if (counter == 2)
                    loc = inputline;
                if (counter == 3)
                    title = inputline;
                counter++;

                if (counter == 4)
                    {
                    String activeLang = m_EOApp.getLabels().getActiveLanguage();

                    if (lang.equals("English"))
                        {
                        if (type.equals("TEXT"))
                            {
                            engHelpFile = loc;
                            engHelpFileTitle = title;
                            }
                        else if (type.equals("IMAGE"))
                            {
                            engImageFiles.addElement(loc);
                            engImageTitles.addElement(title);
                            }
                        }
                    else if (lang.equals(activeLang))
                        {
                        if (type.equals("TEXT"))
                            {
                            helpFile = loc;
                            helpFileTitle = title;
                            }
                        else if (type.equals("IMAGE"))
                            {
                            imageFiles.addElement(loc);
                            imageTitles.addElement(title);
                            }
                        haveLang = true;
                        }
                    counter = 0;
                    }

                inputline = in.readLine();
                }

            in.close();

            if (!haveLang)
                {
                helpFile = engHelpFile;
                helpFileTitle = engHelpFileTitle;
                imageFiles = engImageFiles;
                imageTitles = engImageTitles;
                }

            if (helpFile.length() > 0)
                {
        	m_EOApp.readInFile(m_EOApp.getHelpLoc(helpFile), dataFile);
                m_helpInfo.setText(dataFile.toString());

                validate();
                }
            Enumeration enm = imageFiles.elements();
            Enumeration enum2 = imageTitles.elements();
            while (enm.hasMoreElements())
                {
                Image tmp;
                String imageFile = (String)enm.nextElement();
                String imageTitle = (String)enum2.nextElement();

                String imageStr = m_EOApp.getHelpLoc(imageFile);
                MediaTracker tracker = new MediaTracker(m_EOApp.getWB());
                tmp = m_EOApp.getImage(imageStr);

                m_figureWindows.addElement(new FigureWindow(tmp,imageTitle));
                }
            }
        catch(Exception e) { e.printStackTrace(); }
        }

    private void fillHelpAddress()
        {
        try
            {
       // Now read in the HTML line by line
            StringBuffer dataFile = new StringBuffer("");
            m_EOApp.readInFile(m_EOApp.getHelpLoc("indexLocations.txt"),dataFile);

            String indexes = dataFile.toString();

            String key = "None";
            String loc = "None";

            int counter = 0;
            int start = 0;

            for (int x=0;x<indexes.length();x++)
                {
                if (indexes.charAt(x) == '\n')
                    {
                    if (counter == 0)
                        key = indexes.substring(start,x);
                    if (counter == 1)
                        loc = indexes.substring(start,x);
                    counter++;
                    start = x+1;
                    }
                if (counter == 2)
                    {
                    m_helpAddresses.put(key,loc);
                    counter = 0;
                    }
                }
            }
        catch(Exception e) { e.printStackTrace(); }
        }
    private void fillHelpIndex()
        {
        try
            {
            StringBuffer dataFile = new StringBuffer("");
            m_EOApp.readInFile(m_EOApp.getHelpLoc("indexes.txt"),dataFile);

            String indexes = dataFile.toString();

            String lang = "None";
            String key = "None";
            String label = "None";

            int counter = 0;
            int start = 0;

            for (int x=0;x<indexes.length();x++)
                {
                if (indexes.charAt(x) == '\n')
                    {
                    if (counter == 0)
                        lang = indexes.substring(start,x);
                    if (counter == 1)
                        key = indexes.substring(start,x);
                    if (counter == 2)
                        label = indexes.substring(start,x);
                    counter++;
                    start = x+1;
                    }
                if (counter == 3)
                    {
                    m_EOApp.getLabels().addObjectLabel(lang,key,label);
                    String[] str = new String[2];
                    str[0] = lang;
                    str[1] = key;
                    m_labelEntries.addElement(str);
                    counter = 0;
                    }
                }
            }
        catch(Exception e) { e.printStackTrace(); }
        }

/**
 * Used to update the WebResourceBundle with any new entries for this window.
 * Normally you will override this function to include the file with your
 * new labels.
 * <p>
 * Example Code: m_EOApp.initializeLabels("girard/sc/expt/awt/dbaw.txt");
 * <p>
 * @see girard.sc.web.WebResourceBundle
 */
    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/expt/help/hw.txt");
        }

/**
 * Used to update the WebResourceBundle by removing any entries for this window.
 * Normally you will override this function to include the file with your
 * new labels.
 * <p>
 * Example Code: m_EOApp.removeLabels("girard/sc/expt/awt/dbaw.txt");
 * <p>
 * @see girard.sc.web.WebResourceBundle
 * @see girard.sc.expt.io.ExptServer
 */
    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/expt/help/hw.txt");

        Enumeration enm = m_labelEntries.elements();
        while (enm.hasMoreElements())
            {
            String[] str = (String[])enm.nextElement();
            m_EOApp.getLabels().removeObjectLabel(str[0],str[1]);
            }
        }
    }
