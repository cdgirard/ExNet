package girard.sc.gtp.awt;

/* Used to format tutorial pages which display a window of text and can have
   a window with an image attached to it assigned to the text window as well.

   Author: Dudley Girard
   Started: 10-25-2001
*/

import girard.sc.awt.GridBagPanel;
import girard.sc.awt.NumberTextField;
import girard.sc.awt.SortedFixedList;
import girard.sc.expt.awt.SaveBaseActionWindow;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.gtp.obj.GenericTutorialPage;
import girard.sc.tp.awt.FormatTutPageWindow;
import girard.sc.tp.awt.TutorialPageBuilderWindow;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.List;
import java.awt.MediaTracker;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Hashtable;

public class FormatGenericTutPageWindow extends FormatTutPageWindow implements ActionListener,ItemListener
    {
    GenericTutorialPage m_GTP;

 // Menu Area
    protected MenuBar m_mbar = new MenuBar();
    protected Menu m_File, m_Help;

    Hashtable m_images = new Hashtable();

    SortedFixedList m_imageTypeList;
    int m_imageTypeIndex = -1;

    SortedFixedList m_imageList;
    int m_imageIndex = -1;
    Button m_viewButton, m_attachButton;

    Frame m_infoWindow;
    TextArea m_information;
    TextField m_imageLabel;

    List m_fontList;

    NumberTextField m_numRowsField, m_numColField;
    Button m_updateButton;
    NumberTextField m_dwXLocField, m_dwYLocField;
    NumberTextField m_iwXLocField, m_iwYLocField;

    ImageWindow m_displayWindow = null;

    public FormatGenericTutPageWindow(ExptOverlord app1, TutorialPageBuilderWindow app2, GenericTutorialPage gtp)
        {
        super(app1,app2,gtp);

        m_GTP = gtp;

        initializeLabels();

        getContentPane().setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("fgtpw_title")+" : "+gtp.getFileName());
        setFont(m_EOApp.getMedWinFont());

        m_mbar.setFont(m_EOApp.getSmWinFont());

    // Setup Information Window
        m_infoWindow = new Frame("Infomation Window");

        m_infoWindow.setLayout(new GridLayout(1,1));

        m_information = new TextArea("",m_GTP.getWinRows(),m_GTP.getWinColumns(),TextArea.SCROLLBARS_VERTICAL_ONLY);
        m_information.setText(m_GTP.getInstructions());
        m_information.setFont(m_GTP.getWinFont());

        m_infoWindow.add(m_information);

        m_infoWindow.pack();
        m_infoWindow.setLocation(m_GTP.getWinLoc().x,m_GTP.getWinLoc().y);
        m_infoWindow.show();

    // End Setup of Information Window

    // Setup Menubar
        setMenuBar(m_mbar);
    
        MenuItem tmpMI;

        m_File = new Menu(m_EOApp.getLabels().getObjectLabel("fgtpw_file"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("fgtpw_save"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("fgtpw_exit"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        m_mbar.add(m_File);
 
    // Help Menu

        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("fgtpw_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("fgtpw_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);
    // End Setup of Menubar

    // Setup North Panel
        GridBagPanel northPanel = new GridBagPanel();

        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("fgtpw_iw")),1,1,10,1);
 
        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("fgtpw_location")),1,2,2,1);
        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("fgtpw_xl")),3,2,2,1);
        m_dwXLocField = new NumberTextField(""+m_GTP.getWinLoc().x,4);
        m_dwXLocField.setAllowNegative(false);
        m_dwXLocField.setAllowFloat(false);
        northPanel.constrain(m_dwXLocField,5,2,2,1);
        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("fgtpw_yl")),7,2,2,1);
        m_dwYLocField = new NumberTextField(""+m_GTP.getWinLoc().y,4);
        m_dwYLocField.setAllowNegative(false);
        m_dwYLocField.setAllowFloat(false);
        northPanel.constrain(m_dwYLocField,9,2,2,1);

        m_fontList = new List(3,false);
        m_fontList.add("Large");
        m_fontList.add("Medium");
        m_fontList.add("Small");

        if (m_GTP.getWinFont().getSize() == m_EOApp.getMedWinFont().getSize())
            m_fontList.select(0);
        else if (m_GTP.getWinFont().getSize() == m_EOApp.getSmWinFont().getSize())
            m_fontList.select(1);
        else if (m_GTP.getWinFont().getSize() == m_EOApp.getTinyWinFont().getSize())
            m_fontList.select(2);

        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("fgtpw_font")),1,3,10,1);
        northPanel.constrain(m_fontList,1,4,10,3);

        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("fgtpw_size")),1,7,2,1);
        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("fgtpw_rows")),3,7,2,1);
        m_numRowsField = new NumberTextField(""+m_GTP.getWinRows(),4);
        m_numRowsField.setAllowNegative(false);
        m_numRowsField.setAllowFloat(false);
        northPanel.constrain(m_numRowsField,5,7,2,1);
        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("fgtpw_cols")),7,7,2,1);
        m_numColField = new NumberTextField(""+m_GTP.getWinColumns(),4);
        m_numColField.setAllowNegative(false);
        m_numColField.setAllowFloat(false);
        northPanel.constrain(m_numColField,9,7,2,1);

        m_updateButton = new Button(m_EOApp.getLabels().getObjectLabel("fgtpw_update"));
        m_updateButton.addActionListener(this);
        northPanel.constrain(m_updateButton,1,8,10,1);
    // End Setup of North Panel

    // Setup South Panel
        GridBagPanel southPanel = new GridBagPanel();

        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("fgtpw_tlil")),1,1,10,1);

        m_imageTypeList = new SortedFixedList(7,false,1,18);
        m_imageTypeList.addItemListener(this);

        southPanel.constrain(m_imageTypeList,1,2,3,7);
        
        m_imageList = new SortedFixedList(7,false,1,37);

        loadAvailableImages();

        southPanel.constrain(m_imageList,4,2,7,7);

        m_viewButton = new Button(m_EOApp.getLabels().getObjectLabel("fgtpw_vi"));
        m_viewButton.addActionListener(this);
        southPanel.constrain(m_viewButton,1,9,5,1);

        m_attachButton = new Button(m_EOApp.getLabels().getObjectLabel("fgtpw_ai"));
        m_attachButton.addActionListener(this);
        southPanel.constrain(m_attachButton,6,9,5,1);

        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("fgtpw_aedi")),1,10,2,1);
        m_imageLabel = new TextField(m_GTP.getImageTitle(),30);
        m_imageLabel.setEditable(false);
        southPanel.constrain(m_imageLabel,3,10,8,1);

        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("fgtpw_location")),1,11,2,1);
        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("fgtpw_xl")),3,11,2,1);
        m_iwXLocField = new NumberTextField(""+m_GTP.getImgLoc().x,4);
        m_iwXLocField.setAllowNegative(false);
        m_iwXLocField.setAllowFloat(false);
        southPanel.constrain(m_iwXLocField,5,11,2,1);
        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("fgtpw_yl")),7,11,2,1);
        m_iwYLocField = new NumberTextField(""+m_GTP.getImgLoc().y,4);
        m_iwYLocField.setAllowNegative(false);
        m_iwYLocField.setAllowFloat(false);
        southPanel.constrain(m_iwYLocField,9,11,2,1);

     // End Setup of South Panel


     // End Setup User Fields

        getContentPane().add("North",northPanel);
        getContentPane().add("South",southPanel);

        pack();

        m_imageList.setSize(m_imageList.getPreferredSize());
        setSize(getPreferredSize());
        validate();

        show();
        }
    
    public void actionPerformed(ActionEvent e) 
        {
        if (getEditMode())
            return;

        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();
    
        // File Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("fgtpw_exit")))
                {
                m_infoWindow.dispose();

                try { m_displayWindow.dispose(); }
                catch (NullPointerException npe) { ; }

                m_TPBWApp.setEditMode(false);
                removeLabels();
                dispose();
                return;
                }

            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("fgtpw_help")))
                {
                m_EOApp.helpWindow("ehlp_fgtpw");
                }

            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("fgtpw_save")))
                {
                setEditMode(true);

                m_GTP.setInstructions(m_information.getText());
                m_GTP.setWinLoc(m_dwXLocField.getIntValue(),m_dwYLocField.getIntValue());
                m_GTP.setImgLoc(m_iwXLocField.getIntValue(),m_iwYLocField.getIntValue());
                new SaveBaseActionWindow(m_EOApp,this,m_GTP);
                }
            }
        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();

            if ((theSource == m_viewButton) && (m_imageList.getSelectedIndex() > -1))
                {
                String title = m_imageList.getSelectedSubItem(0);

                try { m_displayWindow.dispose(); }
                catch (NullPointerException npe) { ; }
                
                if (title.equals(GenericTutorialPage.NO_IMAGE))
                    {
                    }
                else
                    {
                    String type = m_imageTypeList.getSelectedSubItem(0);
                    Hashtable h = (Hashtable)m_images.get(type);
                    String[] imageInfo = (String[])h.get(title);
                    String loc = imageInfo[0];
                    m_GTP.setImgLoc(m_iwXLocField.getIntValue(),m_iwYLocField.getIntValue());
                    m_displayWindow = new ImageWindow(loadImage(loc,title),title,m_GTP.getImgLoc());
                    }
                }
            if ((theSource == m_attachButton) && (m_imageList.getSelectedIndex() > -1))
                {
                String title = m_imageList.getSelectedSubItem(0);
 
                m_imageLabel.setText(title);

                if (title.equals(GenericTutorialPage.NO_IMAGE))
                    {
                    m_GTP.setImageTitle(GenericTutorialPage.NO_IMAGE);
                    m_GTP.setImageLocation(new String(""));
                    }
                else
                    {
                    String type = m_imageTypeList.getSelectedSubItem(0);
                    Hashtable h = (Hashtable)m_images.get(type);
                    String[] imageInfo = (String[])h.get(title);
                    String loc = imageInfo[0];

                    m_GTP.setImageTitle(title);
                    m_GTP.setImageLocation(loc);
                    }
                }
            if (theSource == m_updateButton)
                {
                int columns = m_numColField.getIntValue();
                int rows = m_numRowsField.getIntValue();
                int fontType = m_fontList.getSelectedIndex();

                if ((columns > 10) && (columns < 100) && (rows > 0) && (rows < 80) && (fontType > -1))
                    {
                    m_GTP.setWinColumns(columns);
                    m_GTP.setWinRows(rows);
                    m_GTP.setWinLoc(m_dwXLocField.getIntValue(),m_dwYLocField.getIntValue());
                    if (fontType == 0)
                        m_GTP.setWinFont(m_EOApp.getMedWinFont());
                    else if (fontType == 1)
                        m_GTP.setWinFont(m_EOApp.getSmWinFont());
                    else
                        m_GTP.setWinFont(m_EOApp.getTinyWinFont());
                    m_infoWindow.hide();
                    m_information.setColumns(columns);
                    m_information.setRows(rows);
                    m_information.setFont(m_GTP.getWinFont());
                    m_infoWindow.pack();
                    m_infoWindow.setLocation(m_GTP.getWinLoc().x,m_GTP.getWinLoc().y);
                    m_infoWindow.show();
                    }
                }
            }
        }

    public boolean getEditMode()
        {
        return m_EditMode;
        }
    public ExptOverlord getEOApp()
        {
        return m_EOApp;
        }

    public void itemStateChanged(ItemEvent e)
        {
        if (e.getSource() instanceof SortedFixedList)
            {
            SortedFixedList theSource = (SortedFixedList)e.getSource();
 
            if (theSource == m_imageTypeList)
                {
                m_imageTypeIndex = m_imageTypeList.getSelectedIndex();
                if (m_imageTypeIndex > -1)
                    {
                    String type = m_imageTypeList.getSelectedSubItem(0);
                    
                    updateImageList(type);
                    }
                }
            }
        }

    public Image loadImage(String imageFile, String title)
        {
        String imageLoc = "image/"+imageFile;
        Image tmp = m_EOApp.getImage(imageLoc);

        return tmp;
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/gtp/awt/fgtpw.txt");
        }

    public void loadAvailableImages()
    {
	StringBuffer dataFile = new StringBuffer();
	String imageListLoc = "images/girard/sc/gtp/awt/imageList.txt";
	m_EOApp.readInFile(imageListLoc, dataFile);
	String[] lines = dataFile.toString().split("\n");

	// Now read in the HTML line by line
	String type = "None";
	String loc = "None";
	String title = "None";

	int counter = 0;
	int index = 0;
	String inputline = lines[index++];
	while (index < lines.length)
	{
	    if (counter == 0)
		type = inputline;
	    if (counter == 1)
		loc = inputline;
	    if (counter == 2)
		title = inputline;
	    inputline = lines[index++];
	    counter++;
	    if (counter == 3)
	    {
		if (m_images.containsKey(type))
		{
		    Hashtable h = (Hashtable) m_images.get(type);
		    String[] str = new String[2];
		    str[0] = loc;
		    str[1] = title;
		    h.put(title, str);
		} else
		{
		    Hashtable h = new Hashtable();
		    String[] str = new String[2];
		    str[0] = loc;
		    str[1] = title;
		    h.put(title, str);
		    m_images.put(type, h);
		}
		counter = 0;
	    }
	}

	Enumeration enm = m_images.keys();
	while (enm.hasMoreElements())
	{
	    String[] str = new String[1];
	    str[0] = (String) enm.nextElement();
	    m_imageTypeList.addItem(str);
	}

	for (int i = 0; i < m_imageTypeList.getItemCount(); i++)
	{
	    type = (String) m_imageTypeList.getSubItem(i, 0);
	    Hashtable h = (Hashtable) m_images.get(type);

	    if (h.containsKey(m_GTP.getImageTitle()))
	    {
		String[] str = (String[]) h.get(m_GTP.getImageTitle());
		if (m_GTP.getImageLocation().equals(str[0]))
		{
		    updateImageList(type);
		    m_imageTypeIndex = i;
		    for (int x = 0; x < m_imageList.getItemCount(); x++)
		    {
			String str2 = (String) m_imageList.getSubItem(x, 0);
			if (str2.equals(m_GTP.getImageTitle()))
			{
			    m_imageIndex = x;
			    m_imageList.select(x);
			    m_imageTypeList.select(i);
			    break;
			}
		    }
		    break;
		}
	    }
	}

    }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/gtp/awt/fgtpw.txt");
        }

    public void setEditMode(boolean value)
        {
        m_EditMode = value;
        setTitle(m_EOApp.getLabels().getObjectLabel("fgtpw_title")+" : "+m_GTP.getFileName());
        validate();
        }
    public void setEOApp(ExptOverlord obj)
        {
        m_EOApp = obj;
        }

    public void updateImageList(String type)
        {
        Hashtable h = (Hashtable)m_images.get(type);

        m_imageList.removeAll();
        m_imageIndex = -1;

        String[] str = new String[1];
        str[0] = GenericTutorialPage.NO_IMAGE;
        m_imageList.addItem(str);

        Enumeration enm = h.keys();
        while (enm.hasMoreElements())
            {
            str = new String[1];
            str[0] = (String)enm.nextElement();
            m_imageList.addItem(str);
            }
        }
    }
