package girard.sc.ce.awt;

import girard.sc.awt.ErrorDialog;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.NumberTextField;
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
 * Used to create a new commodity for the CENetworkAction.
 * <p>
 * <br> Started: 01-22-2003
 * <p>
 * @author Dudley Girard
 */

public class CECreateCommodityWindow extends Frame implements ActionListener
    {
    ExptOverlord m_EOApp;
    CEEditResWindow m_ERWApp;
    CENetwork m_activeNetwork;
    CENodeResource m_nr;

    TextField m_NameField, m_LabelField;
    NumberTextField m_AmountField, m_UtilityField;
    
    Button m_CreateButton, m_CancelButton;

    public CECreateCommodityWindow(ExptOverlord app1, CEEditResWindow app2, CENetwork app3, CENodeResource app4)
        {
        super();

        m_EOApp = app1; /* Need to make pretty buttons. */
        m_ERWApp = app2; /* Need so can unset edit mode */
        m_activeNetwork = app3; /* Makes referencing easier */
        m_nr = app4;

        initializeLabels();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("ceccw_title"));
        setFont(m_EOApp.getMedWinFont());


  // Start Setup for the Center Panel

        GridBagPanel centerPanel = new GridBagPanel(); 

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ceccw_name")),1,1,4,1,GridBagConstraints.CENTER);
        m_NameField = new TextField("",10);
        centerPanel.constrain(m_NameField,5,1,4,1);

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ceccw_label")),1,2,4,1,GridBagConstraints.CENTER);
        m_LabelField = new TextField("",10);
        centerPanel.constrain(m_LabelField,5,2,4,1);

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ceccw_da")),1,3,4,1,GridBagConstraints.CENTER);
        m_AmountField = new NumberTextField("0",5);
        m_AmountField.setAllowFloat(false);
        m_AmountField.setAllowNegative(true);
        centerPanel.constrain(m_AmountField,5,3,4,1);
 
        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ceccw_du")),1,4,4,1);
        m_UtilityField = new NumberTextField("0",5);
        m_UtilityField.setAllowFloat(false);
        m_UtilityField.setAllowNegative(true);
        centerPanel.constrain(m_UtilityField,5,4,4,1);

        m_CreateButton = new Button(m_EOApp.getLabels().getObjectLabel("ceccw_create"));
        m_CreateButton.addActionListener(this);
        centerPanel.constrain(m_CreateButton,1,5,4,1,GridBagConstraints.CENTER);

        m_CancelButton = new Button(m_EOApp.getLabels().getObjectLabel("ceccw_cancel"));
        m_CancelButton.addActionListener(this);
        centerPanel.constrain(m_CancelButton,5,5,4,1,GridBagConstraints.CENTER);

        add("Center",centerPanel);

        pack();
        show();
        }

    public void actionPerformed (ActionEvent e)
        {
        Button theSource = (Button)e.getSource();
       
        if (theSource == m_CreateButton)
            {
            // Handle Save
            CEResource r = createResource();
            if (r != null)
                {
                Enumeration enm = m_activeNetwork.getNodeList().elements();
                while (enm.hasMoreElements())
                    {
                    CENode Ntemp = (CENode)enm.nextElement();
                    CENodeResource nr = (CENodeResource)Ntemp.getExptData("CENodeResource");
                    nr.addInitialResource((CEResource)r.clone());
                    }
                removeLabels();
                m_ERWApp.updateDisplay();
                m_ERWApp.setEditMode(false);
                dispose();
                }
            }
        if (theSource == m_CancelButton)
            {
            removeLabels();
            m_ERWApp.setEditMode(false);
            dispose();
            }
        }

    private CEResource createResource()
        {
        String n = m_NameField.getText().trim();

        if (n.length() == 0)
            {
            new ErrorDialog(m_EOApp.getLabels().getObjectLabel("ceccw_error1"));
            return null;
            }

        String l = m_LabelField.getText().trim();

        if (l.length() != 1)
            {
            new ErrorDialog(m_EOApp.getLabels().getObjectLabel("ceccw_error3"));
            return null;
            }

        if (m_nr.getInitialResources().containsKey(l))
            {
            new ErrorDialog(m_EOApp.getLabels().getObjectLabel("ceccw_error2"));
            return null;
            }

        CEResource r = new CEResource(n,l,m_AmountField.getIntValue(),m_UtilityField.getIntValue());

        return r;
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/ce/awt/ceccw.txt");
        }  

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/ce/awt/ceccw.txt");
        }
    }
