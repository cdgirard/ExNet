package girard.sc.expt.awt;

import girard.sc.awt.BorderPanel;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.NumberTextField;
import girard.sc.expt.obj.Experiment;
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

public class FormatEndExperimentWindow extends Frame implements ActionListener
    {
    ExptOverlord m_EOApp;
    ExptBuilderWindow m_EBWApp;
    Experiment m_ExpApp;
    Hashtable m_presentSettings = new Hashtable();

  // Menu Area
    private MenuBar m_mbar = new MenuBar();
    private Menu m_Help;

    TextArea m_desc = new TextArea("",6,30,TextArea.SCROLLBARS_HORIZONTAL_ONLY);

    CheckboxGroup m_activateGroup = new CheckboxGroup();
    Checkbox m_yesBox;
    Checkbox m_noBox;

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

    public FormatEndExperimentWindow(ExptOverlord app1, ExptBuilderWindow app2, Experiment app3)
        {
        super();

        m_EOApp = app1; /* Need to make pretty buttons. */
        m_EBWApp = app2; /* Need so can unset edit mode */
        m_ExpApp = app3; /* Makes referencing easier */

        initializeLabels();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("feew_title"));
        setFont(m_EOApp.getMedWinFont());

// Setup Menubar
        setMenuBar(m_mbar);
    
        MenuItem tmpMI;

// Help Menu

        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("feew_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("feew_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);
// End Setup for Menubar.

// Setup North Panel

        GridBagPanel northPanel = new GridBagPanel();

        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("feew_activate")),1,1,4,1,GridBagConstraints.CENTER);

        String a = (String)m_ExpApp.getExtraData("EndWindow");
        Font f = new Font("Monospaced",Font.PLAIN,18);
        Point p = new Point(0,0);
        String cf = "Client";
        
        if (a.equals("Yes"))
            {
            m_presentSettings = (Hashtable)m_ExpApp.getExtraData("EndWindowDetails");
            String fontName = (String)m_presentSettings.get("FontName");
            int fontSize = ((Integer)m_presentSettings.get("FontSize")).intValue();
            int fontType = ((Integer)m_presentSettings.get("FontType")).intValue();

            f = new Font(fontName,fontType,fontSize);

            m_desc.setText((String)m_presentSettings.get("Message"));

            p = (Point)m_presentSettings.get("Loc");
 
            cf = (String)m_presentSettings.get("Continue");

            m_yesBox = new Checkbox(m_EOApp.getLabels().getObjectLabel("feew_yes"),m_activateGroup,true);
            m_noBox = new Checkbox(m_EOApp.getLabels().getObjectLabel("feew_no"),m_activateGroup,false);
            }
        else
            {
            m_yesBox = new Checkbox(m_EOApp.getLabels().getObjectLabel("feew_yes"),m_activateGroup,false);
            m_noBox = new Checkbox(m_EOApp.getLabels().getObjectLabel("feew_no"),m_activateGroup,true);
            }

        northPanel.constrain(m_yesBox,1,2,2,1,GridBagConstraints.CENTER);
        northPanel.constrain(m_noBox,3,2,2,1,GridBagConstraints.CENTER);

        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("feew_message")),1,3,4,1,GridBagConstraints.CENTER);
 
        m_desc.setFont(f);
        northPanel.constrain(m_desc,1,4,4,4,GridBagConstraints.CENTER);
// End Setup of North Panel

// Start Setup of Center Panel
        GridBagPanel centerPanel = new GridBagPanel();

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("feew_fs")),1,1,6,1,GridBagConstraints.CENTER);

        if (f.getSize() == 18)
            m_largeBox = new Checkbox(m_EOApp.getLabels().getObjectLabel("feew_large"),m_fontGroup,true);
        else
            m_largeBox = new Checkbox(m_EOApp.getLabels().getObjectLabel("feew_large"),m_fontGroup,false);
        centerPanel.constrain(m_largeBox,1,2,2,1,GridBagConstraints.CENTER);

        if (f.getSize() == 15)
            m_medBox = new Checkbox(m_EOApp.getLabels().getObjectLabel("feew_medium"),m_fontGroup,true);
        else
            m_medBox = new Checkbox(m_EOApp.getLabels().getObjectLabel("feew_medium"),m_fontGroup,false);
        centerPanel.constrain(m_medBox,3,2,2,1,GridBagConstraints.CENTER);

        if (f.getSize() == 12)
            m_smallBox = new Checkbox(m_EOApp.getLabels().getObjectLabel("feew_small"),m_fontGroup,true);
        else
            m_smallBox = new Checkbox(m_EOApp.getLabels().getObjectLabel("feew_small"),m_fontGroup,false);
        centerPanel.constrain(m_smallBox,5,2,2,1,GridBagConstraints.CENTER);
 
        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("feew_location")),1,3,2,1,GridBagConstraints.CENTER);

        centerPanel.constrain(new Label("X:"),1,3,1,1,GridBagConstraints.CENTER);
        m_xLocField = new NumberTextField(""+p.x,3);
        centerPanel.constrain(m_xLocField,4,3,1,1);

        centerPanel.constrain(new Label("Y:"),5,3,1,1,GridBagConstraints.CENTER);
        m_yLocField = new NumberTextField(""+p.y,3);
        centerPanel.constrain(m_yLocField,6,3,1,1);

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("feew_cf")),1,4,6,1,GridBagConstraints.CENTER);

        if (cf.equals("Client"))
            {
            m_clientBox = new Checkbox(m_EOApp.getLabels().getObjectLabel("feew_client"),m_restartGroup,true);
            m_exptBox = new Checkbox(m_EOApp.getLabels().getObjectLabel("feew_experimenter"),m_restartGroup,false);
            }
        else
            {
            m_clientBox = new Checkbox(m_EOApp.getLabels().getObjectLabel("feew_client"),m_restartGroup,false);
            m_exptBox = new Checkbox(m_EOApp.getLabels().getObjectLabel("feew_experimenter"),m_restartGroup,true);
            }

        centerPanel.constrain(m_clientBox,1,5,3,1,GridBagConstraints.CENTER);
        centerPanel.constrain(m_exptBox,4,5,3,1,GridBagConstraints.CENTER);
// End Setup of Center Panel


// Setup South Panel

        GridBagPanel southPanel = new GridBagPanel();

        m_updateButton = new Button(m_EOApp.getLabels().getObjectLabel("feew_update"));
        m_updateButton.addActionListener(this);
        southPanel.constrain(m_updateButton,1,1,6,1,GridBagConstraints.CENTER);

        m_okButton = new Button(m_EOApp.getLabels().getObjectLabel("feew_ok"));
        m_okButton.addActionListener(this);
        southPanel.constrain(m_okButton,1,2,3,1,GridBagConstraints.CENTER);

        m_cancelButton = new Button(m_EOApp.getLabels().getObjectLabel("feew_cancel"));
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
                m_EBWApp.setEditMode(false);
                removeLabels();
                dispose();
                return;
                }
            if (theSource == m_okButton)
                {
                if (m_yesBox.getState())
                    {
                    m_ExpApp.setExtraData("EndWindow","Yes");

                    m_presentSettings.put("Message",m_desc.getText());
                    m_presentSettings.put("Loc",new Point(m_xLocField.getIntValue(),m_yLocField.getIntValue()));

                    if (m_largeBox.getState())
                        m_presentSettings.put("FontSize",new Integer(18));
                    if (m_medBox.getState())
                        m_presentSettings.put("FontSize",new Integer(15));
                    if (m_smallBox.getState())
                        m_presentSettings.put("FontSize",new Integer(12));

                    m_presentSettings.put("FontName","Monospaced");
                    m_presentSettings.put("FontType",new Integer(Font.PLAIN));

                    if (m_clientBox.getState())
                        m_presentSettings.put("Continue","Client");
                    else
                        m_presentSettings.put("Continue","Experimenter");
                    m_ExpApp.setExtraData("EndWindowDetails",m_presentSettings);
                    }
                else
                    {
                    m_ExpApp.setExtraData("EndWindow","No");
                    m_ExpApp.removeExtraData("EndWindowDetails");
                    }

                m_EBWApp.setEditMode(false);
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
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("feew_help")))
                {
                m_EOApp.helpWindow("ehlp_feew");
                }
            }
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/expt/awt/feew.txt");
        }  

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/expt/awt/feew.txt");
        }
    }
