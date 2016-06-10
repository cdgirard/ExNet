package girard.sc.tut.awt;

/* Used to format the Tutorial Simulant Actor for the Tutorial Action.
 
   Author: Dudley Girard
   Modified: 03-05-2002
*/

import girard.sc.awt.GridBagPanel;
import girard.sc.expt.awt.FormatSimActorWindow;
import girard.sc.expt.awt.LoadSimActorWindow;
import girard.sc.expt.awt.SaveSimActorWindow;
import girard.sc.expt.awt.SimulantBuilderWindow;
import girard.sc.expt.obj.SimActor;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.tut.obj.TutorialSimActor;

import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.TextArea;
import java.awt.event.ActionEvent;

public class FormatTutorialSimActorWindow extends FormatSimActorWindow
    {
    MenuBar m_mbar = new MenuBar();
    Menu m_File, m_Help;

    TextArea m_simDesc = new TextArea("",4,25,TextArea.SCROLLBARS_VERTICAL_ONLY);

    public FormatTutorialSimActorWindow(ExptOverlord app1, SimulantBuilderWindow app2, TutorialSimActor app3)
        {
        super(app1,app2,app3);

        initializeLabels();

        setLayout(new GridLayout(1,1));
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("ftsaw_title"));
        setFont(m_EOApp.getMedWinFont());

   // Start setup for menubar.
        m_mbar.setFont(m_EOApp.getSmWinFont());

        setMenuBar(m_mbar);
    
        MenuItem tmpMI;
    
    // File Menu Options

        m_File = new Menu(m_EOApp.getLabels().getObjectLabel("ftsaw_file"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ftsaw_open"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ftsaw_save"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ftsaw_exit"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        m_mbar.add(m_File);

        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("ftsaw_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ftsaw_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);
    // End setup for menu bar.

    // Start setup for center panel.
        GridBagPanel centerGBPanel = new GridBagPanel();

        centerGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ftsaw_description")),1,1,4,1);
        centerGBPanel.constrain(m_simDesc,1,2,4,4);

    // End setup for center panel.

        add("Center",centerGBPanel);

        pack();
        show();
        }

    public void actionPerformed (ActionEvent e)
        {
        if (getEditMode())
            return;

        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();

            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ftsaw_exit")))
                {
                m_SBWApp.setEditMode(false);
                removeLabels();
                dispose();
                return;
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ftsaw_open")))
                {
                setEditMode(true);
                new LoadSimActorWindow(m_EOApp,this,m_activeActor);
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ftsaw_save")))
                {
                setEditMode(true);
                fillInSimValues();
                new SaveSimActorWindow(m_EOApp,this,m_activeActor);
                }
         // Help Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ftsaw_help")))
                {
                m_EOApp.helpWindow("ehlp_ftsaw");
                }
            }
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/tut/awt/ftsaw.txt");
        }  

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/tut/awt/ftsaw.txt");
        }

    public void setActiveActor(SimActor sa)
        {
        m_activeActor = sa;
        updateDisplay();
        }

    public void updateDisplay()
        {
        TutorialSimActor sa = (TutorialSimActor)m_activeActor;

        m_simDesc.setText(sa.getActorDesc());
        }

    private boolean fillInSimValues()
        {
        TutorialSimActor sa = (TutorialSimActor)m_activeActor;

        sa.setActorDesc(m_simDesc.getText());

        return true;
        }
    }
