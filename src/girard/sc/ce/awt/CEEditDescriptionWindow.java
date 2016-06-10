package girard.sc.ce.awt;

import girard.sc.awt.GridBagPanel;
import girard.sc.ce.obj.CENetworkAction;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Used to change the description for a CE Network Action that is
 * part of an Experiment.
 * <p>
 * <br> Started: 01-29-2003
 * <p>
 * @author Dudley Girard
 */

public class CEEditDescriptionWindow extends Frame implements ActionListener
    {
    ExptOverlord m_EOApp;
    CEFormatNetworkActionWindow m_FNAWApp;
    CENetworkAction m_activeNetwork;

    TextArea m_desc = new TextArea("",4,25,TextArea.SCROLLBARS_VERTICAL_ONLY);
    
    Button m_OkButton, m_CancelButton;

    public CEEditDescriptionWindow(ExptOverlord app1, CEFormatNetworkActionWindow app2, CENetworkAction app3)
        {
        super();

        m_EOApp = app1; /* Need to make pretty buttons. */
        m_FNAWApp = app2; /* Need so can unset edit mode */
        m_activeNetwork = app3; /* Makes referencing easier */

        initializeLabels();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("ceedw_title"));
        setFont(m_EOApp.getMedWinFont());


// Start Setup for Center Panel.
        GridBagPanel centerPanel = new GridBagPanel();

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ceedw_description")),1,1,4,1,GridBagConstraints.CENTER);
        m_desc.setText(m_activeNetwork.getDesc());
        centerPanel.constrain(m_desc,1,2,4,4,GridBagConstraints.CENTER); 

        m_OkButton = new Button(m_EOApp.getLabels().getObjectLabel("ceedw_ok"));
        m_OkButton.addActionListener(this);
        centerPanel.constrain(m_OkButton,1,6,2,1,GridBagConstraints.CENTER);

        m_CancelButton = new Button(m_EOApp.getLabels().getObjectLabel("ceedw_cancel"));
        m_CancelButton.addActionListener(this);
        centerPanel.constrain(m_CancelButton,3,6,2,1,GridBagConstraints.CENTER);

        add("Center",centerPanel);

        pack();
        show();
        }

    public void actionPerformed (ActionEvent e)
        {
        Button theSource = (Button)e.getSource();
       
        if (theSource == m_OkButton)
            {
            // Handle Save
            m_activeNetwork.setDesc(m_desc.getText());
            m_FNAWApp.setEditMode(false);
            removeLabels();
            dispose();
            }
        if (theSource == m_CancelButton)
            {
            m_FNAWApp.setEditMode(false);
            removeLabels();
            dispose();
            }
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/ce/awt/ceedw.txt");
        }  

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/ce/awt/ceedw.txt");
        }
    }