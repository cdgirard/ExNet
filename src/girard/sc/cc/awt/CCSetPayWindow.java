package girard.sc.cc.awt;

/* Allows you to adjust the pay per point for the nodes in a CCNetworkAction.

   Author: Dudley Girard
   Started: 7-24-2001
*/

import girard.sc.awt.FixedList;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.NumberTextField;
import girard.sc.cc.obj.CCNetwork;
import girard.sc.expt.web.ExptOverlord;

import java.awt.Button;
import java.awt.Frame;
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

public class CCSetPayWindow extends Frame implements ActionListener,ItemListener
    {
    ExptOverlord m_EOApp;
    CCFormatNetworkActionWindow m_FNAWApp;
    CCNetwork m_activeNetwork;
    
  // Menu Area
    MenuBar m_mbar = new MenuBar();
    Menu m_File, m_Help;

    FixedList m_payList;
    NumberTextField m_payField;

    int m_selectedIndex = -1;

    public CCSetPayWindow(ExptOverlord app1, CCFormatNetworkActionWindow app2, CCNetwork app3)
        {
        super();

        m_EOApp = app1; /* Need to make pretty buttons. */
        m_FNAWApp = app2; /* Need so can unset edit mode */
        m_activeNetwork = app3; /* Makes referencing easier */

        initializeLabels();

        setLayout(new GridLayout(1,1));
        setTitle(m_EOApp.getLabels().getObjectLabel("ccspw_title"));
        setFont(m_EOApp.getMedWinFont());
        setBackground(m_EOApp.getWinBkgColor());

    // Start Setup for Menubar
        m_mbar.setFont(m_EOApp.getSmWinFont());

        setMenuBar(m_mbar);
    
        MenuItem tmpMI;
     // File Menu
        m_File = new Menu(m_EOApp.getLabels().getObjectLabel("ccspw_file"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ccspw_done"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        m_mbar.add(m_File);

     // Help Menu
        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("ccspw_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ccspw_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);
   // End Setup for Menubar

        GridBagPanel MainPanel = new GridBagPanel();

        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccspw_upr")),1,1,4,1);

        m_payList = new FixedList(8,false,2,10,FixedList.CENTER);
        double[] pay = (double[])m_activeNetwork.getExtraData("Pay");
        for (int i=0;i<pay.length;i++)
            {
            m_payList.addItem(BuildPayListEntry(i,pay[i]));
            }
        m_payList.addItemListener(this);
        MainPanel.constrain(m_payList,1,2,4,4);
      
        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccspw_pay")),1,6,2,1);
        m_payField = new NumberTextField(6);
        m_payField.setAllowNegative(false);
        m_payField.addActionListener(this);
        MainPanel.constrain(m_payField,3,6,2,1);
        
        add(MainPanel);
        pack();
 
        m_payList.setSize(m_payList.getPreferredSize());
        setSize(getPreferredSize());

        show();
        }

/********************************************************************************
Callback for the Ready button on the Ready Message Window
*********************************************************************************/
    public void actionPerformed(ActionEvent e)
        {
        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();
        
            }

        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();

         // File Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ccspw_done")))
                {
                m_FNAWApp.setEditMode(false);
                removeLabels();
                dispose();
                return;
                }
         // Help Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ccspw_help")))
                {
                m_EOApp.helpWindow("ehlp_ccspw");
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
        m_EOApp.initializeLabels("girard/sc/cc/awt/ccspw.txt");
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
        m_EOApp.removeLabels("girard/sc/cc/awt/ccspw.txt");
        }
    }
