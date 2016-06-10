package girard.sc.expt.awt;

import girard.sc.awt.FixedList;
import girard.sc.awt.GridBagPanel;
import girard.sc.expt.io.msg.ShutdownExptReqMsg;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Hashtable;

public class ShowPayResultsWindow extends Frame implements ActionListener
    {
    ExptOverlord m_EOApp;
    ExperimenterWindow m_ew;

    FixedList m_payList;

    Button m_doneButton;

    public ShowPayResultsWindow(Hashtable pay, ExperimenterWindow ew)
        {
        m_ew = ew;
        m_EOApp = m_ew.getEOApp();

        initializeLabels();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setFont(new Font("Monospaced",Font.PLAIN,14));
        setTitle(m_EOApp.getLabels().getObjectLabel("sprw_title"));

    // Setup Center Panel

        GridBagPanel centerPanel = new GridBagPanel();

        centerPanel.constrain(buildPayListLabel(pay.size()),1,1,4,1);
        m_payList = new FixedList(8,false,pay.size()+2,10,FixedList.CENTER);
        
        fillPayList(pay);

        centerPanel.constrain(m_payList,1,2,4,4);
   // End Setup for Center Panel

   // Start Setup for South Panel

        Panel southPanel = new Panel(new GridLayout(1,1));

        m_doneButton = new Button(m_EOApp.getLabels().getObjectLabel("sprw_done"));
        m_doneButton.addActionListener(this);
        southPanel.add(m_doneButton);

   // End Setup for South Panel

        add("South",southPanel);
        add("Center",centerPanel);
        
        pack();
        show();
        }
 
    public void actionPerformed(ActionEvent e) 
        {

        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();

            if (theSource == m_doneButton)
                {
                String a = (String)m_ew.getExpApp().getExtraData("EndWindow");
                if (a.equals("Yes"))
                    {
                    Hashtable details = (Hashtable)m_ew.getExpApp().getExtraData("EndWindowDetails");
                    String c = (String)details.get("Continue");
                    if (c.equals("Client"))
                        {
                        m_ew.setWatcher(false);
                        removeLabels();
                        dispose();
                        }
                    else
                        {
                        m_ew.getSML().sendMessage(new ShutdownExptReqMsg(null));
                        removeLabels();
                        dispose();
                        }
                    }
                else
                    {
                    m_ew.setWatcher(false);
                    removeLabels();
                    dispose();
                    }
                }
            }
        }

    public Label buildPayListLabel(int actions)
        {
        StringBuffer str = new StringBuffer("          ");
        for (int x=0;x<actions;x++)
            {
            str.append(" Action "+x+" ");
            }
        str.append("  Total   ");

        return new Label(str.toString());
        }

    public void fillPayList(Hashtable pay)
        {
        for (int x=0;x<m_ew.getExpApp().getNumUsers();x++)
            {
            String[] str = new String[pay.size()+2];  // One extra column for the user and one for the total.
            double total = 0;

            str[0] = new String("User"+x);
            for (int i=0;i<pay.size();i++)
                {
                double[] payments = (double[])pay.get(new Integer(i));
                total = total + payments[x];
                str[i+1] = formatPay(payments[x]);
                }
            str[pay.size()+1] = formatPay(total);

            m_payList.add(str);
            }
        }

    public String formatPay(double value)
        {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        return nf.format(value);
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/expt/awt/sprw.txt");
        }  
    
    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/expt/awt/sprw.txt");
        }
    }