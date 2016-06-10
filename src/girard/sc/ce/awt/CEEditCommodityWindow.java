package girard.sc.ce.awt;

import girard.sc.awt.ErrorDialog;
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
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

/**
 * Used to edit a commodity for the CENetworkAction.
 * <p>
 * <br> Started: 01-24-2003
 * <p>
 * @author Dudley Girard
 */

public class CEEditCommodityWindow extends Frame implements ActionListener
    {
    ExptOverlord m_EOApp;
    CEEditResWindow m_ERWApp;
    CENetwork m_activeNetwork;
    CENodeResource m_nr;
    CEResource m_res;

    TextField m_NameField, m_LabelField;
    
    Button m_UpdateButton, m_CancelButton;

    public CEEditCommodityWindow(ExptOverlord app1, CEEditResWindow app2, CENetwork app3, CENodeResource app4, CEResource app5)
        {
        super();

        m_EOApp = app1; /* Need to make pretty buttons. */
        m_ERWApp = app2; /* Need so can unset edit mode */
        m_activeNetwork = app3; /* Makes referencing easier */
        m_nr = app4;
        m_res = app5;

        initializeLabels();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("ceecw_title"));
        setFont(m_EOApp.getMedWinFont());


  // Start Setup for the Center Panel

        GridBagPanel centerPanel = new GridBagPanel(); 

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ceecw_name")),1,1,4,1,GridBagConstraints.CENTER);
        m_NameField = new TextField(m_res.getName(),10);
        centerPanel.constrain(m_NameField,5,1,4,1);

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ceecw_label")),1,2,4,1,GridBagConstraints.CENTER);
        m_LabelField = new TextField(m_res.getLabel(),10);
        centerPanel.constrain(m_LabelField,5,2,4,1);

        m_UpdateButton = new Button(m_EOApp.getLabels().getObjectLabel("ceecw_update"));
        m_UpdateButton.addActionListener(this);
        centerPanel.constrain(m_UpdateButton,1,3,4,1,GridBagConstraints.CENTER);

        m_CancelButton = new Button(m_EOApp.getLabels().getObjectLabel("ceecw_cancel"));
        m_CancelButton.addActionListener(this);
        centerPanel.constrain(m_CancelButton,5,3,4,1,GridBagConstraints.CENTER);

        add("Center",centerPanel);

        pack();
        show();
        }

    public void actionPerformed (ActionEvent e)
        {
        Button theSource = (Button)e.getSource();
       
        if (theSource == m_UpdateButton)
            {
            // Handle Save
            String n = m_NameField.getText().trim();

            if (n.length() == 0)
                {
                new ErrorDialog(m_EOApp.getLabels().getObjectLabel("ceecw_error1"));
                return;
                }

            String l = m_LabelField.getText().trim();

            if (l.length() == 0)
                {
                new ErrorDialog(m_EOApp.getLabels().getObjectLabel("ceecw_error3"));
                return;
                }

            if ((!l.equals(m_res.getLabel())) && (m_nr.getInitialResources().containsKey(l)))
                {
                new ErrorDialog(m_EOApp.getLabels().getObjectLabel("ceecw_error2"));
                return;
                }

            String oldLabel = new String(m_res.getLabel());

            Enumeration enm = m_activeNetwork.getNodeList().elements();
            while (enm.hasMoreElements())
                {
                CENode Ntemp = (CENode)enm.nextElement();
                CENodeResource nr = (CENodeResource)Ntemp.getExptData("CENodeResource");
                CEResource r = nr.getInitialResources(oldLabel);
                nr.removeInitialResource(oldLabel);
                r.setName(n);
                r.setLabel(l);
                nr.addInitialResource(r);
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
        m_EOApp.initializeLabels("girard/sc/ce/awt/ceecw.txt");
        }  

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/ce/awt/ceecw.txt");
        }
    }