package girard.sc.be.awt;

import girard.sc.awt.ColorTextField;
import girard.sc.awt.ErrorDialog;
import girard.sc.awt.GridBagPanel;
import girard.sc.be.obj.BECSIESimActor;
import girard.sc.expt.awt.FormatSimActorWindow;
import girard.sc.expt.awt.LoadSimActorWindow;
import girard.sc.expt.awt.SaveSimActorWindow;
import girard.sc.expt.awt.SimulantBuilderWindow;
import girard.sc.expt.obj.SimActor;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.TextArea;
import java.awt.event.ActionEvent;

/* Used to format the CSIE Simulant Actor for the BE network.
 
   Author: Dudley Girard
   Started: 8-17-2000
   Modified: 5-21-2001
*/

public class BEFormatCSIESimActorWindow extends FormatSimActorWindow
    {
    MenuBar m_mbar = new MenuBar();
    Menu m_File, m_Help;

    ColorTextField m_concessionRate;
    ColorTextField m_inclusionRate;
    ColorTextField m_exclusionRate;
    CheckboxGroup  m_timeEffect = new CheckboxGroup();
    CheckboxGroup  m_initialOffer = new CheckboxGroup();
    Checkbox m_randomOffer;
    Checkbox m_degreeOffer;
    ColorTextField m_startSpiteRate;
    ColorTextField m_stopSpiteRate;

    TextArea m_simDesc = new TextArea("",4,25,TextArea.SCROLLBARS_VERTICAL_ONLY);

    public BEFormatCSIESimActorWindow(ExptOverlord app1, SimulantBuilderWindow app2, BECSIESimActor app3)
        {
        super(app1,app2,app3);

        initializeLabels();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("befsaw_title"));
        setFont(m_EOApp.getMedWinFont());

   // Start setup for menubar.
        m_mbar.setFont(m_EOApp.getSmWinFont());

        setMenuBar(m_mbar);
    
        MenuItem tmpMI;
    
    // File Menu Options

        m_File = new Menu(m_EOApp.getLabels().getObjectLabel("befsaw_file"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("befsaw_open"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("befsaw_save"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("befsaw_exit"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        m_mbar.add(m_File);

        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("befsaw_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("befsaw_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);
    // End setup for menu bar.

    // Start setup for center panel.
        GridBagPanel centerGBPanel = new GridBagPanel();

        centerGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("befsaw_cr")),1,1,2,1);
        m_concessionRate = new ColorTextField(""+app3.getConcessionRate(),4);
        centerGBPanel.constrain(m_concessionRate,3,1,2,1);

        centerGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("befsaw_ir")),1,2,2,1);
        m_inclusionRate = new ColorTextField(""+app3.getInclusionRate(),4);
        centerGBPanel.constrain(m_inclusionRate,3,2,2,1);

        centerGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("befsaw_er")),1,3,2,1);
        m_exclusionRate = new ColorTextField(""+app3.getExclusionRate(),4);
        centerGBPanel.constrain(m_exclusionRate,3,3,2,1);

        centerGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("befsaw_stas")),1,4,2,1);
        m_startSpiteRate = new ColorTextField(""+app3.getStartSpite(),4);
        centerGBPanel.constrain(m_startSpiteRate,3,4,2,1);

        centerGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("befsaw_stos")),1,5,2,1);
        m_stopSpiteRate = new ColorTextField(""+app3.getStopSpite(),4);
        centerGBPanel.constrain(m_stopSpiteRate,3,5,2,1);
    // End setup for center Panel.

    // Start setup for east Panel.
        GridBagPanel eastGBPanel = new GridBagPanel();

        eastGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("befsaw_te")),1,1,2,1);
        eastGBPanel.constrain(new Checkbox(m_EOApp.getLabels().getObjectLabel("befsaw_yes"),m_timeEffect,true),1,2,2,1);
        eastGBPanel.constrain(new Checkbox(m_EOApp.getLabels().getObjectLabel("befsaw_no"),m_timeEffect,false),1,3,2,1);

        eastGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("befsaw_io")),1,4,2,1);
        m_randomOffer = new Checkbox(m_EOApp.getLabels().getObjectLabel("befsaw_random"),m_initialOffer,true);
        eastGBPanel.constrain(m_randomOffer,1,5,2,1);
        m_degreeOffer = new Checkbox(m_EOApp.getLabels().getObjectLabel("befsaw_degree"),m_initialOffer,false);
        eastGBPanel.constrain(m_degreeOffer,1,6,2,1);
    // End setup for east panel.

    // Start setup for south panel.
        GridBagPanel southGBPanel = new GridBagPanel();

        southGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("befsaw_description")),1,1,4,1);
        southGBPanel.constrain(m_simDesc,1,2,4,4);
    // End setup for south panel.

        add("Center",centerGBPanel);
        add("East",eastGBPanel);
        add("South",southGBPanel);

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

            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("befsaw_exit")))
                {
                m_SBWApp.setEditMode(false);
                removeLabels();
                dispose();
                return;
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("befsaw_open")))
                {
                setEditMode(true);
                new LoadSimActorWindow(m_EOApp,this,m_activeActor);
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("befsaw_save")))
                {
                setEditMode(true);
                fillInSimValues();
                new SaveSimActorWindow(m_EOApp,this,m_activeActor);
                }
       // Help Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("befsaw_help")))
                {
                m_EOApp.helpWindow("ehlp_befsaw");
                }
            }
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/be/awt/befcsiesaw.txt");
        }  

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/be/awt/befcsiesaw.txt");
        }

    public void setActiveActor(SimActor sa)
        {
        m_activeActor = sa;
        updateDisplay();
        }

    public void updateDisplay()
        {
        BECSIESimActor sa = (BECSIESimActor)m_activeActor;
        m_concessionRate.setText(""+sa.getConcessionRate());
        m_exclusionRate.setText(""+sa.getExclusionRate());
        m_inclusionRate.setText(""+sa.getInclusionRate());
        m_startSpiteRate.setText(""+sa.getStartSpite());
        m_stopSpiteRate.setText(""+sa.getStopSpite());

        if ((m_timeEffect.getSelectedCheckbox().getLabel().equals(m_EOApp.getLabels().getObjectLabel("befsaw_no"))) && (sa.getTimeEffect()))
            {
            m_timeEffect.getSelectedCheckbox().setState(false);
            }
        if ((m_timeEffect.getSelectedCheckbox().getLabel().equals(m_EOApp.getLabels().getObjectLabel("befsaw_yes"))) && (!sa.getTimeEffect()))
            {
            m_timeEffect.getSelectedCheckbox().setState(false);
            }

        if (sa.getInitialOfferMethod() == BECSIESimActor.RANDOM)
            {
            m_randomOffer.setState(true);
            }
        if (sa.getInitialOfferMethod() == BECSIESimActor.DEGREE)
            {
            m_degreeOffer.setState(true);
            }

        m_simDesc.setText(sa.getActorDesc());
        }

    private boolean fillInSimValues()
        {
        BECSIESimActor sa = (BECSIESimActor)m_activeActor;

        try { sa.setConcessionRate(Double.valueOf(m_concessionRate.getText()).doubleValue()); }
        catch(NumberFormatException nfe) 
            {
            new ErrorDialog("Connession Rate value not entered properly");
            return false;
            }
     
        try { sa.setInclusionRate(Double.valueOf(m_inclusionRate.getText()).doubleValue()); }
        catch(NumberFormatException nfe) 
            {
            new ErrorDialog("Inclusion Rate value not entered properly");
            return false;
            }

        try { sa.setExclusionRate(Double.valueOf(m_exclusionRate.getText()).doubleValue()); }
        catch(NumberFormatException nfe) 
            {
            new ErrorDialog("Exclusion Rate value not entered properly");
            return false;
            }

        try { sa.setStartSpite(Double.valueOf(m_startSpiteRate.getText()).doubleValue()); }
        catch(NumberFormatException nfe) 
            {
            new ErrorDialog("Start Spite Rate value not entered properly");
            return false;
            }

        try { sa.setStopSpite(Double.valueOf(m_stopSpiteRate.getText()).doubleValue()); }
        catch(NumberFormatException nfe) 
            {
            new ErrorDialog("Stop Spite Rate value not entered properly");
            return false;
            }

        if (m_timeEffect.getSelectedCheckbox().getLabel().equals(m_EOApp.getLabels().getObjectLabel("befsaw_yes")))
            {
            sa.setTimeEffect(true);
            }
        else
            {
            sa.setTimeEffect(false);
            }

        if (m_initialOffer.getSelectedCheckbox().getLabel().equals(m_EOApp.getLabels().getObjectLabel("befsaw_random")))
            {
            sa.setInitialOfferMethod(BECSIESimActor.RANDOM);
            }
        if (m_initialOffer.getSelectedCheckbox().getLabel().equals(m_EOApp.getLabels().getObjectLabel("befsaw_degree")))
            {
            sa.setInitialOfferMethod(BECSIESimActor.DEGREE);
            }

        sa.setActorDesc(m_simDesc.getText());

        return true;
        }
    }
