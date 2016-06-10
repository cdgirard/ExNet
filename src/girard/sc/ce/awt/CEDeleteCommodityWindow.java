package girard.sc.ce.awt;

import girard.sc.awt.GridBagPanel;
import girard.sc.ce.obj.CENetwork;
import girard.sc.ce.obj.CENode;
import girard.sc.ce.obj.CENodeResource;
import girard.sc.ce.obj.CEResource;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

/**
 * Used to delete a commodity from the CENetworkAction.
 * <p>
 * <br> Started: 01-24-2003
 * <p>
 * @author Dudley Girard
 */

public class CEDeleteCommodityWindow extends Frame implements ActionListener
    {
    ExptOverlord m_EOApp;
    CEEditResWindow m_ERWApp;
    CENetwork m_activeNetwork;
    CEResource m_res;
    
    Button m_OkButton, m_CancelButton;

    public CEDeleteCommodityWindow(ExptOverlord app1, CEEditResWindow app2, CENetwork app3, CEResource app4)
        {
        super();

        m_EOApp = app1; /* Need to make pretty buttons. */
        m_ERWApp = app2; /* Need so can unset edit mode */
        m_activeNetwork = app3; /* Makes referencing easier */
        m_res = app4;

        initializeLabels();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("cedcw_title"));
        setFont(m_EOApp.getMedWinFont());


  // Start Setup for the Center Panel

        GridBagPanel centerPanel = new GridBagPanel(); 

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("cedcw_message1")),1,1,8,1,GridBagConstraints.CENTER);

        m_OkButton = new Button(m_EOApp.getLabels().getObjectLabel("cedcw_ok"));
        m_OkButton.addActionListener(this);
        centerPanel.constrain(m_OkButton,1,2,4,1,GridBagConstraints.CENTER);

        m_CancelButton = new Button(m_EOApp.getLabels().getObjectLabel("cedcw_cancel"));
        m_CancelButton.addActionListener(this);
        centerPanel.constrain(m_CancelButton,5,2,4,1,GridBagConstraints.CENTER);

        add("Center",centerPanel);

        pack();
        show();
        }

    public void actionPerformed (ActionEvent e)
        {
        Button theSource = (Button)e.getSource();
       
        if (theSource == m_OkButton)
            {
            Enumeration enm = m_activeNetwork.getNodeList().elements();
            while (enm.hasMoreElements())
                {
                CENode Ntemp = (CENode)enm.nextElement();
                CENodeResource nr = (CENodeResource)Ntemp.getExptData("CENodeResource");
                nr.removeInitialResource(m_res.getLabel());
                }
            removeLabels();
            m_ERWApp.updateDisplay();
            m_ERWApp.setEditMode(false);
            dispose();
            }
        if (theSource == m_CancelButton)
            {
            removeLabels();
            m_ERWApp.setEditMode(false);
            dispose();
            }
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/ce/awt/cedcw.txt");
        }  

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/ce/awt/cedcw.txt");
        }
    }
