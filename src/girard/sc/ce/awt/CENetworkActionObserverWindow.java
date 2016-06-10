package girard.sc.ce.awt;

import girard.sc.awt.BorderPanel;
import girard.sc.awt.FixedLabel;
import girard.sc.awt.FixedList;
import girard.sc.awt.GridBagPanel;
import girard.sc.ce.obj.CEEdge;
import girard.sc.ce.obj.CEEdgeInteraction;
import girard.sc.ce.obj.CEExchange;
import girard.sc.ce.obj.CENetwork;
import girard.sc.ce.obj.CENode;
import girard.sc.ce.obj.CENodeResource;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.msg.GetRegUsersMsg;
import girard.sc.expt.obj.ObserverExptInfo;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Displays information to an Observer during the running of
 * a CENetworkAction.
 * <p>
 * <br> Started: 02-25-2003
 * <br> Modified: 04-03-2003
 * <p>
 * @author Dudley Girard
 */

public class CENetworkActionObserverWindow extends ObserverWindow
    {
    protected CENetwork m_network;

    MenuBar m_menuBar;
    Menu m_fileMenu;

    FixedList m_subjectList;
    
    TextArea m_profitArea = new TextArea("",8,15,TextArea.SCROLLBARS_VERTICAL_ONLY);

    FixedLabel m_exptNameLabel;
    FixedLabel m_activeActionLabel;
    FixedLabel m_roundLabel;
    FixedLabel m_periodLabel;
    FixedLabel m_timeLabel;

    CEObserverDisplayCanvas m_displayArea;
    CEObserverMiniDisplayCanvas m_miniDisplayArea;
/**
 * Min of 0 Max of 4, higher value more area shown.
 */
    int m_zoomLevel = 2;  

    Vector m_subWindows = new Vector();

    int m_regListenIndex = -1;

    public CENetworkActionObserverWindow(ExptOverlord app1, ObserverExptInfo app2, ExptMessageListener app3)
        {
        super(app1,app2,app3);
        
        m_network = (CENetwork)m_ExpApp.getActiveAction();

        initializeLabels();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("cenaow_title"));
        setSize(m_EOApp.getWidth(),m_EOApp.getHeight());
    
        m_displayArea = new CEObserverDisplayCanvas(this,m_network);
        m_miniDisplayArea = new CEObserverMiniDisplayCanvas(m_network,m_displayArea);

    // Set up menu bar

        MenuItem tmpItem;

        m_menuBar = new MenuBar();

        m_fileMenu = new Menu(m_EOApp.getLabels().getObjectLabel("cenaow_file"));

        tmpItem = new MenuItem(m_EOApp.getLabels().getObjectLabel("cenaow_exit"));
        tmpItem.addActionListener(this);
        m_fileMenu.add(tmpItem);

        m_menuBar.add(m_fileMenu);

    // North Panel

        GridBagPanel northPanel = new GridBagPanel();

        m_timeLabel = new FixedLabel(3,"-");
        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("cenaow_timer")),1,1,2,1);
        northPanel.constrain(m_timeLabel,3,1,2,1);

        m_roundLabel = new FixedLabel(3,"-");
        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("cenaow_round")),1,2,2,1);
        northPanel.constrain(m_roundLabel,3,2,2,1);

        m_periodLabel = new FixedLabel(3,"-");
        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("cenaow_period")),1,3,2,1);
        northPanel.constrain(m_periodLabel,3,3,2,1);

        String tmpStr = (String)m_network.getExtraData("ExperimentName");
        m_exptNameLabel = new FixedLabel(25,tmpStr);
        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("cenaow_experiment")),5,1,2,1);
        northPanel.constrain(m_exptNameLabel,7,1,4,1);

        tmpStr = ((Integer)m_network.getExtraData("ActionIndex")).toString();
        m_activeActionLabel = new FixedLabel(25,tmpStr);
        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("cenaow_aa")),5,2,2,1);
        northPanel.constrain(m_activeActionLabel,7,2,4,1);

        Panel tmpPanel = new Panel(new GridLayout(1,1));
        tmpPanel.add(m_miniDisplayArea);
        northPanel.constrain(new CEMiniDisplayPanel(tmpPanel,BorderPanel.FRAME),15,1,2,2,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
    // End Setup North Panel

    // Start Setup West Panel

        GridBagPanel westPanel = new GridBagPanel();

        westPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("cenaow_profit")),1,1,4,1);
        m_profitArea.setEditable(false);
        westPanel.constrain(m_profitArea,1,2,4,6);

        westPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("cenaow_sr")),1,13,4,1);
        m_subjectList = new FixedList(4,false,2,7);
        initializeSubjectList();
        westPanel.constrain(m_subjectList,1,14,4,4);
        
    // End Setup for West Panel

    // Setup Center Panel
        Panel centerPanel = new Panel(new GridLayout(1,1));

        centerPanel.add(m_displayArea);

        setMenuBar(m_menuBar);
        getContentPane().add("North",northPanel);
        getContentPane().add("West",westPanel);
        getContentPane().add("Center",new BorderPanel(centerPanel,BorderPanel.FRAME));

        show();

        GetRegUsersMsg tmpMsg = new GetRegUsersMsg(null);
        m_regListenIndex = m_SML.addListenRequest(tmpMsg,5000);
        }
 
    public void actionPerformed(ActionEvent e) 
        {
        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();

            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("cenaow_exit")))
                {
                m_ExpApp.setExptRunning(false);
                m_ExpApp.setJoined(false);
                setWatcher(false);
                }
            }

        if (e.getSource() instanceof ExptMessage)
            {
            synchronized(m_SML)  // Make sure we deal with only one message at a time.
                {
                ExptMessage em = (ExptMessage)e.getSource();

                em.getObserverResponse(this);
                }
            }
        }

    public void addSubWindow(Window w)
        {
        m_subWindows.addElement(w);
        }

    public void cleanUpWindow()
        {
        // Clear out any subwindows that may be hanging around.
        Enumeration enm = m_subWindows.elements();
        while (enm.hasMoreElements())
            {
            Frame f = (Frame)enm.nextElement();
            f.dispose();
            }

        m_subWindows.removeAllElements();  // Remove any references to the subwindows.

        removeLabels();
        }

    public CENetwork getNetwork()
        {
        return m_network;
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/ce/awt/cenaow.txt");
        }
    public void initializeSubjectList()
        {
        for (int x=0;x<m_ExpApp.getNumUsers();x++)
            {
            String[] str = new String[2];
            if (m_ExpApp.getHumanUser(x))
                str[0] = new String("User"+x);
            else
                str[0] = new String("Comp"+x);
            str[1] = new String(""+m_ExpApp.getRegistered(x));

            m_subjectList.addItem(str);
            }
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/ce/awt/cenaow.txt");
        }

    public void repaint()
        {
        m_displayArea.repaint();
        }    

    public void removeSubWindow(Window w)
        {
        m_subWindows.removeElement(w);
        w.dispose();
        }

    public void setPeriodLabel(int value)
        {
        m_periodLabel.setText(""+value);
        }
    public void setRoundLabel(int value)
        {
        m_roundLabel.setText(""+value);
        }
    public void setTimeLabel(int value)
        {
        m_timeLabel.setText(""+value);
        }

    public void updateDisplay()
        {
        updateUserList();
        }
    public void updateProfitDisplay(CEEdge edge)
        {
        CENode n1 = (CENode)m_network.getNode(edge.getNode1());
        CENodeResource nr1 = (CENodeResource)n1.getExptData("CENodeResource");
        CENode n2 = (CENode)m_network.getNode(edge.getNode2());
        CENodeResource nr2 = (CENodeResource)n2.getExptData("CENodeResource");
        StringBuffer newText = new StringBuffer("");
        newText.append(n1.getLabel()+" -> "+n2.getLabel()+"\n");
        int profN1 = 0;
        int profN2 = 0;
        if (edge.getActive())
            {
            CEEdgeInteraction ei = (CEEdgeInteraction)edge.getExptData("CEEdgeInteraction");
            if (edge.getCompleted())
                {
                Hashtable h = ei.getCompletedExchange(ei.getExchanges());
                if (h != null)
                    {
                    CEExchange cee1 = (CEExchange)h.get(""+n1.getLabel()+"-"+n2.getLabel());
                    profN1 = (int)(cee1.getNode1().getProfit() - cee1.getNode2().getProfit());
                    CEExchange cee2 = ei.getOffer(n2.getLabel()+"-"+n1.getLabel());
                    profN2 = (int)(cee2.getNode2().getProfit() - cee2.getNode1().getProfit());
                    }
                }
            else
                {
                profN1 = ei.getOfferProfit(n1.getID(),n1.getID(),n2.getID());
                profN2 = ei.getOfferProfit(n2.getID(),n1.getID(),n2.getID()); 
                }
            }
        newText.append(n1.getLabel()+" "+profN1+"\n");
        newText.append(n2.getLabel()+" "+profN2+"\n");

        newText.append(n2.getLabel()+" -> "+n1.getLabel()+"\n");
        profN1 = 0;
        profN2 = 0;
        if (edge.getActive())
            {
            CEEdgeInteraction ei = (CEEdgeInteraction)edge.getExptData("CEEdgeInteraction");
            if (edge.getCompleted())
                {
                Hashtable h = ei.getCompletedExchange(ei.getExchanges());
                if (h != null)
                    {
                    CEExchange cee1 = (CEExchange)h.get(""+n1.getLabel()+"-"+n2.getLabel());
                    profN1 = (int)(cee1.getNode1().getProfit() - cee1.getNode2().getProfit());
                    CEExchange cee2 = ei.getOffer(n2.getLabel()+"-"+n1.getLabel());
                    profN2 = (int)(cee2.getNode2().getProfit() - cee2.getNode1().getProfit());
                    }
                }
            else
                {
                profN1 = ei.getOfferProfit(n1.getID(),n2.getID(),n1.getID());
                profN2 = ei.getOfferProfit(n2.getID(),n2.getID(),n1.getID());
                }
            }
        newText.append(n1.getLabel()+" "+profN1+"\n");
        newText.append(n2.getLabel()+" "+profN2+"\n");

        m_profitArea.setText(newText.toString());
        }
    private void updateUserList()
        {
        for (int x=0;x<m_ExpApp.getNumUsers();x++)
            {
            if (m_ExpApp.getRegistered(x) != Boolean.valueOf(m_subjectList.getSubItem(x,1)).booleanValue())
                {
                String[] str = new String[2];
                if (m_ExpApp.getHumanUser(x))
                    str[0] = new String("User"+x);
                else
                    str[0] = new String("Comp"+x);
                str[1] = new String(""+m_ExpApp.getRegistered(x));

                m_subjectList.replaceItem(str,x);
                }
            }
        }
    }