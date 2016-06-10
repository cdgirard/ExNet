package girard.sc.expt.awt;

import girard.sc.awt.FixedList;
import girard.sc.awt.GridBagPanel;
import girard.sc.expt.obj.ExperimentAction;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;

public class AddActionWindow extends Frame implements ActionListener,ItemListener
    {
    ExptOverlord m_EOApp;
    ExptBuilderWindow m_EBWApp;

    FixedList m_actionList;

    TextArea m_actDesc = new TextArea("",8,50,TextArea.SCROLLBARS_VERTICAL_ONLY);

    Button m_okButton, m_cancelButton, m_helpButton;

    public AddActionWindow(ExptOverlord eo, ExptBuilderWindow ebw)
        {
        super("Add Action Window");
       
        m_EOApp = eo;
        m_EBWApp = ebw;

        initializeLabels();
         
        setBackground(m_EOApp.getWinBkgColor());
        setLayout(new BorderLayout());
        setTitle(m_EOApp.getLabels().getObjectLabel("aaw_title"));

        m_actionList = new FixedList(8,false,1,20);
        m_actionList.addItemListener(this);

        Enumeration enm = m_EBWApp.getAvailableActions().elements();
        while(enm.hasMoreElements())
            {
            ExperimentAction ea = (ExperimentAction)enm.nextElement();
            String[] str = new String[1];
            str[0] = ea.getName();
            m_actionList.addItem(str);
            }

        add("North",new Label(m_EOApp.getLabels().getObjectLabel("aaw_pa")));
        add("Center",m_actionList);

    // Start Setup for the South Panel.
        Panel tmpPanel = new Panel(new GridLayout(1,3));
        m_okButton = new Button(m_EOApp.getLabels().getObjectLabel("aaw_ok"));
        m_okButton.addActionListener(this);
        tmpPanel.add(m_okButton);

        m_cancelButton = new Button(m_EOApp.getLabels().getObjectLabel("aaw_cancel"));
        m_cancelButton.addActionListener(this);
        tmpPanel.add(m_cancelButton);

        m_helpButton = new Button(m_EOApp.getLabels().getObjectLabel("aaw_help"));
        m_helpButton.addActionListener(this);
        tmpPanel.add(m_helpButton);

        add("South",tmpPanel);
    // End Setup for the South Panel.

        GridBagPanel eastGBPanel = new GridBagPanel();
        eastGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("aaw_description")),1,1,4,1);
        m_actDesc.setEditable(false);
        eastGBPanel.constrain(m_actDesc,1,2,4,4);
        add("East",eastGBPanel);    

        pack(); 
        show();
        }

    public void actionPerformed (ActionEvent e)
        {
        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();

            if ((theSource == m_okButton) && (m_actionList.getSelectedIndex() > -1))
                {
                ExperimentAction ea = (ExperimentAction)m_EBWApp.getAvailableActions().elementAt(m_actionList.getSelectedIndex());
                ExperimentAction eaClone = (ExperimentAction)ea.clone();   
                eaClone.initializeAction(m_EOApp,m_EBWApp);
                this.dispose();
                }
            if (theSource == m_cancelButton)
                {
                m_EBWApp.setEditMode(false);
                this.dispose();
                }
            if (theSource == m_helpButton)
                {
                m_EOApp.helpWindow("ehlp_aaw");
                }
            }
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/expt/awt/aaw.txt");
        }

    public void itemStateChanged(ItemEvent e)
        {
        if (e.getSource() instanceof FixedList)
            {
            FixedList theSource = (FixedList)e.getSource();

            if (theSource == m_actionList)
                {
                if (theSource.getSelectedIndex()!= -1)
                    {
                    String str = (String)m_EBWApp.getActionDescriptions().elementAt(theSource.getSelectedIndex());
                    m_actDesc.setText(str);
                    }
                }
            }
         }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/expt/awt/aaw.txt");
        }
    }