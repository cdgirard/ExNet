package girard.sc.be.awt;

import girard.sc.awt.BorderPanel;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.NumberTextField;
import girard.sc.be.obj.BENetwork;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

public class BEEditInitialWindow extends Frame implements ActionListener
    {
    ExptOverlord m_EOApp;
    BEFormatNetworkActionWindow m_FNAWApp;
    BENetwork m_activeNetwork;
    Hashtable m_presentSettings;

// Menu Area
    private MenuBar m_mbar = new MenuBar();
    private Menu m_Help;

    TextArea m_desc = new TextArea("",6,30,TextArea.SCROLLBARS_BOTH);

    CheckboxGroup m_fontGroup = new CheckboxGroup();
    Checkbox m_smallBox;
    Checkbox m_medBox;
    Checkbox m_largeBox;
    
    CheckboxGroup m_restartGroup = new CheckboxGroup();
    Checkbox m_clientBox;
    Checkbox m_exptBox;

    NumberTextField m_xLocField, m_yLocField;

 /* GUI Variables For Bottom Panel */
    Button m_updateButton;
    Button m_okButton, m_cancelButton;

    public BEEditInitialWindow(ExptOverlord app1, BEFormatNetworkActionWindow app2, BENetwork app3)
        {
        super();

        m_EOApp = app1; /* Need to make pretty buttons. */
        m_FNAWApp = app2; /* Need so can unset edit mode */
        m_activeNetwork = app3; /* Makes referencing easier */
        m_presentSettings = (Hashtable)m_activeNetwork.getExtraData("InitialWindow");

        initializeLabels();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("beeiw_title"));
        setFont(m_EOApp.getMedWinFont());

// Setup Menubar
        setMenuBar(m_mbar);
    
        MenuItem tmpMI;

// Help Menu

        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("beeiw_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("beeiw_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);
// End Setup for Menubar.


// Setup North Panel

        GridBagPanel northPanel = new GridBagPanel();

        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("beeiw_message")),1,1,4,1,GridBagConstraints.CENTER);

        String fontName = (String)m_presentSettings.get("FontName");
        int fontSize = ((Integer)m_presentSettings.get("FontSize")).intValue();
        int fontType = ((Integer)m_presentSettings.get("FontType")).intValue();

        Font f = new Font(fontName,fontType,fontSize);

        m_desc.setText((String)m_presentSettings.get("Message"));
        m_desc.setFont(f);
        northPanel.constrain(m_desc,1,2,4,4,GridBagConstraints.CENTER);
// End Setup of North Panel

// Start Setup of Center Panel
        GridBagPanel centerPanel = new GridBagPanel();

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("beeiw_fs")),1,1,6,1,GridBagConstraints.CENTER);

        if (f.getSize() == 18)
            m_largeBox = new Checkbox(m_EOApp.getLabels().getObjectLabel("beeiw_large"),m_fontGroup,true);
        else
            m_largeBox = new Checkbox(m_EOApp.getLabels().getObjectLabel("beeiw_large"),m_fontGroup,false);
        centerPanel.constrain(m_largeBox,1,2,2,1,GridBagConstraints.CENTER);

        if (f.getSize() == 15)
            m_medBox = new Checkbox(m_EOApp.getLabels().getObjectLabel("beeiw_medium"),m_fontGroup,true);
        else
            m_medBox = new Checkbox(m_EOApp.getLabels().getObjectLabel("beeiw_medium"),m_fontGroup,false);
        centerPanel.constrain(m_medBox,3,2,2,1,GridBagConstraints.CENTER);

        if (f.getSize() == 12)
            m_smallBox = new Checkbox(m_EOApp.getLabels().getObjectLabel("beeiw_small"),m_fontGroup,true);
        else
            m_smallBox = new Checkbox(m_EOApp.getLabels().getObjectLabel("beeiw_small"),m_fontGroup,false);
        centerPanel.constrain(m_smallBox,5,2,2,1,GridBagConstraints.CENTER);
 
        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("beeiw_location")),1,3,2,1,GridBagConstraints.CENTER);

        Point p = (Point)m_presentSettings.get("Loc");

        centerPanel.constrain(new Label("X:"),3,3,1,1,GridBagConstraints.CENTER);
        m_xLocField = new NumberTextField(""+p.x,3);
        centerPanel.constrain(m_xLocField,4,3,1,1);

        centerPanel.constrain(new Label("Y:"),5,3,1,1,GridBagConstraints.CENTER);
        m_yLocField = new NumberTextField(""+p.y,3);
        centerPanel.constrain(m_yLocField,6,3,1,1);

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("beeiw_cf")),1,4,6,1,GridBagConstraints.CENTER);

        String cf = (String)m_presentSettings.get("Continue");

        if (cf.equals("Client"))
            {
            m_clientBox = new Checkbox(m_EOApp.getLabels().getObjectLabel("beeiw_client"),m_restartGroup,true);
            m_exptBox = new Checkbox(m_EOApp.getLabels().getObjectLabel("beeiw_experimenter"),m_restartGroup,false);
            }
        else
            {
            m_clientBox = new Checkbox(m_EOApp.getLabels().getObjectLabel("beeiw_client"),m_restartGroup,false);
            m_exptBox = new Checkbox(m_EOApp.getLabels().getObjectLabel("beeiw_experimenter"),m_restartGroup,true);
            }

        centerPanel.constrain(m_clientBox,1,5,3,1,GridBagConstraints.CENTER);
        centerPanel.constrain(m_exptBox,4,5,3,1,GridBagConstraints.CENTER);
// End Setup of Center Panel


// Setup South Panel

        GridBagPanel southPanel = new GridBagPanel();

        m_updateButton = new Button(m_EOApp.getLabels().getObjectLabel("beeiw_update"));
        m_updateButton.addActionListener(this);
        southPanel.constrain(m_updateButton,1,1,6,1,GridBagConstraints.CENTER);

        m_okButton = new Button(m_EOApp.getLabels().getObjectLabel("beeiw_ok"));
        m_okButton.addActionListener(this);
        southPanel.constrain(m_okButton,1,2,3,1,GridBagConstraints.CENTER);

        m_cancelButton = new Button(m_EOApp.getLabels().getObjectLabel("beeiw_cancel"));
        m_cancelButton.addActionListener(this);
        southPanel.constrain(m_cancelButton,4,2,3,1,GridBagConstraints.CENTER);

        add("North",new BorderPanel(northPanel,BorderPanel.FRAME));
        add("Center",new BorderPanel(centerPanel,BorderPanel.FRAME));
        add("South",new BorderPanel(southPanel,BorderPanel.FRAME));

        pack();
        show();

        setLocation(p.x,p.y);
        }

    public void actionPerformed (ActionEvent e)
        {
        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();
       
            if (theSource == m_cancelButton)
                {
                m_FNAWApp.setEditMode(false);
                removeLabels();
                dispose();
                return;
                }
            if (theSource == m_okButton)
                {
                m_presentSettings.put("Message",m_desc.getText());
                m_presentSettings.put("Loc",new Point(m_xLocField.getIntValue(),m_yLocField.getIntValue()));

                if (m_largeBox.getState())
                    m_presentSettings.put("FontSize",new Integer(18));
                if (m_medBox.getState())
                    m_presentSettings.put("FontSize",new Integer(15));
                if (m_smallBox.getState())
                    m_presentSettings.put("FontSize",new Integer(12));

                if (m_clientBox.getState())
                    m_presentSettings.put("Continue","Client");
                else
                    m_presentSettings.put("Continue","Experimenter");
                m_FNAWApp.setEditMode(false);
                removeLabels();
                dispose();
                return;
                }
            if (theSource == m_updateButton)
                {
                // Handle Update
                setLocation(m_xLocField.getIntValue(),m_yLocField.getIntValue());
                if (m_largeBox.getState())
                    m_desc.setFont(new Font("Monospaced",Font.PLAIN,18));
                if (m_medBox.getState())
                    m_desc.setFont(new Font("Monospaced",Font.PLAIN,15));
                if (m_smallBox.getState())
                    m_desc.setFont(new Font("Monospaced",Font.PLAIN,12));
                }
            }
        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();
 
            // Help Menu.
           if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("beeiw_help")))
                {
                m_EOApp.helpWindow("ehlp_beeiw");
                }
            }
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/be/awt/beeiw.txt");
        }  

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/be/awt/beeiw.txt");
        }
    }
