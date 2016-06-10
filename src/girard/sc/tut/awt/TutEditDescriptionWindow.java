package girard.sc.tut.awt;

import girard.sc.awt.BorderPanel;
import girard.sc.awt.GridBagPanel;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.tut.obj.TutorialAction;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TutEditDescriptionWindow extends Frame implements ActionListener
    {
    ExptOverlord m_EOApp;
    FormatTutorialActionWindow m_FTAWApp;
    TutorialAction m_activeTutorial;

    TextArea m_desc = new TextArea("",4,25,TextArea.SCROLLBARS_VERTICAL_ONLY);
    TextField m_detailName = new TextField(15);
    
 /* GUI Variables For Bottom Panel */
    Button m_DescOK, m_DescCancel;

    public TutEditDescriptionWindow(ExptOverlord app1, FormatTutorialActionWindow app2, TutorialAction app3)
        {
        super();

        m_EOApp = app1; /* Need to make pretty buttons. */
        m_FTAWApp = app2; /* Need so can unset edit mode */
        m_activeTutorial = app3; /* Makes referencing easier */

        initializeLabels();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("tedw_title"));
        setFont(m_EOApp.getMedWinFont());

  // Setup Center Panel.
        GridBagPanel centerPanel = new GridBagPanel();

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("tedw_description")),1,1,4,1,GridBagConstraints.CENTER);

        m_desc.setText(m_activeTutorial.getDesc());
        centerPanel.constrain(m_desc,1,2,4,4,GridBagConstraints.CENTER); 

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("tedw_dn")),1,6,2,1,GridBagConstraints.CENTER);

        m_detailName.setText(m_activeTutorial.getDetailName());
        centerPanel.constrain(m_detailName,3,6,2,1,GridBagConstraints.CENTER);

// Setup South Panel

        GridBagPanel southPanel = new GridBagPanel();

        m_DescOK = new Button(m_EOApp.getLabels().getObjectLabel("tedw_ok"));
        m_DescOK.addActionListener(this);
        southPanel.constrain(m_DescOK,1,1,2,1,GridBagConstraints.CENTER);

        m_DescCancel = new Button(m_EOApp.getLabels().getObjectLabel("tedw_cancel"));
        m_DescCancel.addActionListener(this);
        southPanel.constrain(m_DescCancel,3,1,2,1,GridBagConstraints.CENTER);

        add("Center",new BorderPanel(centerPanel,BorderPanel.FRAME));
        add("South",new BorderPanel(southPanel,BorderPanel.FRAME));

        pack();
        show();
        }

    public void actionPerformed (ActionEvent e)
        {
        Button theSource = (Button)e.getSource();
       
        if (theSource == m_DescOK)
            {
            // Handle Save
            m_activeTutorial.setDesc(m_desc.getText());
            m_activeTutorial.setDetailName(m_detailName.getText());
            m_FTAWApp.setEditMode(false);
            removeLabels();
            dispose();
            }
        if (theSource == m_DescCancel)
            {
            m_FTAWApp.setEditMode(false);
            removeLabels();
            dispose();
            }
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/tut/awt/tedw.txt");
        }  

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/tut/awt/tedw.txt");
        }
    }
