package girard.sc.be.awt;

import girard.sc.awt.FixedList;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.NumberTextField;
import girard.sc.be.obj.BENetwork;
import girard.sc.expt.web.ExptOverlord;

import java.awt.Button;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.NumberFormat;

/****************************************************************************
Used to set the amount of pay per point a subject earns in a BENetworkAction.

Author: Dudley Girard
Started: 04-05-2001
*****************************************************************************/
public class BESetPayWindow extends Frame implements ActionListener,ItemListener
    {
    ExptOverlord m_EOApp;
    BEFormatNetworkActionWindow m_FNAWApp;
    BENetwork m_activeNetwork;

  // Menu Area
    MenuBar m_mbar = new MenuBar();
    Menu m_File, m_Help;

    FixedList m_payList;
    NumberTextField m_payField;

    int m_selectedIndex = -1;
   
    Button m_SetButton, m_SetAllButton;

    public BESetPayWindow(ExptOverlord app1, BEFormatNetworkActionWindow app2, BENetwork app3)
        {
        super();

        m_EOApp = app1; /* Need to make pretty buttons. */
        m_FNAWApp = app2; /* Need so can unset edit mode */
        m_activeNetwork = app3; /* Makes referencing easier */

        initializeLabels();

        setLayout(new GridLayout(1,1));
        setTitle(m_EOApp.getLabels().getObjectLabel("bespw_title"));
        setFont(m_EOApp.getMedWinFont());
        setBackground(m_EOApp.getWinBkgColor());

   // Start Setup for Menubar
        m_mbar.setFont(m_EOApp.getSmWinFont());

        setMenuBar(m_mbar);
    
        MenuItem tmpMI;
     // File Menu
        m_File = new Menu(m_EOApp.getLabels().getObjectLabel("bespw_file"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("bespw_done"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        m_mbar.add(m_File);

     // Help Menu
        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("bespw_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("bespw_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);
   // End Setup for Menubar

        GridBagPanel MainPanel = new GridBagPanel();

        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bespw_upr")),1,1,4,1);

        m_payList = new FixedList(8,false,2,10,FixedList.CENTER);
        double[] pay = (double[])m_activeNetwork.getExtraData("Pay");
        for (int i=0;i<pay.length;i++)
            {
            m_payList.addItem(BuildPayListEntry(i,pay[i]));
            }
        m_payList.addItemListener(this);
        MainPanel.constrain(m_payList,1,2,4,4);
      
        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bespw_pay")),1,6,2,1);
        m_payField = new NumberTextField(6);
        m_payField.setAllowNegative(false);
        m_payField.addActionListener(this);
        MainPanel.constrain(m_payField,3,6,2,1);
        
        m_SetButton = new Button(m_EOApp.getLabels().getObjectLabel("bespw_set"));
        m_SetButton.addActionListener(this);
        MainPanel.constrain(m_SetButton,1,7,2,1,GridBagConstraints.CENTER);

        m_SetAllButton = new Button(m_EOApp.getLabels().getObjectLabel("bespw_sa"));
        m_SetAllButton.addActionListener(this);
        MainPanel.constrain(m_SetAllButton,3,7,2,1,GridBagConstraints.CENTER);

        add(MainPanel);
        pack();
        show();

        m_payList.setSize(m_payList.getPreferredSize());
        setSize(getPreferredSize());
        }

/********************************************************************************
Callback for the Ready button on the Ready Message Window
*********************************************************************************/
    public void actionPerformed(ActionEvent e)
        {
        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();
        
            if ((theSource == m_SetButton) && (m_selectedIndex > -1) && (m_payField.getText().length() > 0))
                {
                Double newPay = new Double(m_payField.getText());
                double[] pay = (double[])m_activeNetwork.getExtraData("Pay");
                pay[m_selectedIndex] = newPay.doubleValue();
                m_payList.replaceItem(BuildPayListEntry(m_selectedIndex,newPay.doubleValue()),m_selectedIndex);
                m_payList.select(m_selectedIndex);
                }
            if ((theSource == m_SetAllButton) && (m_payField.getText().length() > 0))
                {
                Double newPay = new Double(m_payField.getText());
                double[] pay = (double[])m_activeNetwork.getExtraData("Pay");
                for (int x=0;x<pay.length;x++)
                    {
                    pay[x] = newPay.doubleValue();
                    m_payList.replaceItem(BuildPayListEntry(x,newPay.doubleValue()),x);
                    
                    }
                if (m_selectedIndex > -1)
                    m_payList.select(m_selectedIndex);
                }
            }
        if (e.getSource() instanceof NumberTextField)
            {
            NumberTextField theSource = (NumberTextField)e.getSource();

            if ((m_selectedIndex > -1) && (m_payField.getText().length() > 0))
                {
                String payStr = m_payField.getText();
                try 
                    {
                    Double newPay = new Double(payStr);
                    double[] pay = (double[])m_activeNetwork.getExtraData("Pay");
                    pay[m_selectedIndex] = newPay.doubleValue();
                    m_payList.replaceItem(BuildPayListEntry(m_selectedIndex,newPay.doubleValue()),m_selectedIndex);
                    m_payList.select(m_selectedIndex);
                    }
                catch(NumberFormatException nfe) { ; }
                }
            }
        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();

         // File Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("bespw_done")))
                {
                m_FNAWApp.setEditMode(false);
                removeLabels();
                dispose();
                return;
                }
         // Help Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("bespw_help")))
                {
                m_EOApp.helpWindow("ehlp_bespw");
                }
            }
        }

    public String[] BuildPayListEntry(int index, double pay)
        {
        String[] str = new String[2];

        str[0] = new String("User"+index);
        str[1] = formatPay(pay);

        return str;
        }

    public String formatPay(double value)
        {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(4);
        return nf.format(value);
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/be/awt/bespw.txt");
        }

    public void itemStateChanged(ItemEvent e)
        {
        if (e.getSource() instanceof FixedList)
            {
            FixedList theSource = (FixedList)e.getSource();

            if (theSource == m_payList)
                {
                m_selectedIndex = m_payList.getSelectedIndex();
                if (m_selectedIndex > -1)
                    {
                    double[] pay = (double[])m_activeNetwork.getExtraData("Pay");
                    m_payField.setText(formatPay(pay[m_selectedIndex]));
                    }
                }
            }
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/be/awt/bespw.txt");
        }
    }
