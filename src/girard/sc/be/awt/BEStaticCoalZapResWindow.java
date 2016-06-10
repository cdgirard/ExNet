package girard.sc.be.awt;

import girard.sc.awt.FixedJList;
import girard.sc.awt.GridBagPanel;
import girard.sc.be.io.msg.BECoalZapAckMsg;
import girard.sc.be.obj.BENetwork;
import girard.sc.be.obj.BENode;
import girard.sc.be.obj.BENodeOrSubNet;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Informs the subjects of the results of the zapping.  
 * <br>
 * <br>Started: 07-26-2003
 * @author Dudley Girard
 */
public class BEStaticCoalZapResWindow extends Frame implements ActionListener
    {
    BENetworkActionClientWindow m_CWApp;
    ExptOverlord m_EOApp;

    FixedJList m_freeRiderList;
    Vector m_freeRiders = new Vector();
    Vector m_zappedFreeRiders = new Vector();

    Button m_okButton;

    public BEStaticCoalZapResWindow(BENetworkActionClientWindow app)
        {
        m_CWApp = app;
        m_EOApp = m_CWApp.getEOApp();

        initializeLabels();

        setLayout(new BorderLayout());
        setTitle(m_EOApp.getLabels().getObjectLabel("besczrw_title"));
        setFont(m_EOApp.getMedWinFont());
        setBackground(m_EOApp.getWinBkgColor());

        BENetwork net = m_CWApp.getNetwork();
        BENode myNode = (BENode)net.getExtraData("Me");
        BENodeOrSubNet myNOS = (BENodeOrSubNet)myNode.getExptData("BENodeExchange");
        

  // Start Setup for the Center Panel.

        GridBagPanel centerPanel = new GridBagPanel();

        int votes = myNOS.getCoalition().getNumZapVotes(net);
        int members = myNOS.getCoalition().getNumJoinedCoalMembers(net);

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("besczw_paeiz")),1,1,8,1);
        int[] colWidths = { 9, 13, 13 };
        m_freeRiderList = new FixedJList(3,colWidths,FixedJList.CENTER);
        m_freeRiderList.setVisibleRowCount(5);
        m_freeRiderList.setSorted(true);
        m_freeRiderList.setListFont(m_EOApp.getMedWinFont());
        int[] sortOrder = { 1, 0, 2 };
        m_freeRiderList.setSortOrder(sortOrder);
        m_freeRiderList.setSize(m_freeRiderList.getPreferredSize());
        m_freeRiderList.setBackground(Color.white);
        fillFreeRiderList(myNOS,net);
        centerPanel.constrain(m_freeRiderList,1,2,8,4);

        int numZapped = myNOS.getCoalition().getNumberZapped(votes,members,m_freeRiderList.getItemCount());

        if (numZapped > 0)
            {
            myNOS.getCoalition().assessCosts(myNOS,net);
            myNOS.getCoalition().assessZaps(m_freeRiders,numZapped,net);
            updateFreeRiderList(m_freeRiders,numZapped);
            }

        centerPanel.constrain(new Label("Zap Votes: "+votes),1,6,8,1);
        centerPanel.constrain(new Label("Number Zapped: "+numZapped),1,7,8,1);

   // End Setup of the Center Panel.


  // Start Setup of the South Panel.

        GridBagPanel southPanel = new GridBagPanel();
        
        m_okButton = new Button("OK");
        m_okButton.addActionListener(this);
        southPanel.constrain(m_okButton,1,1,4,1,GridBagConstraints.CENTER);
  // End Setup for the South Panel.

        add("Center",centerPanel);
        add("South",southPanel);

        pack();
        show();
        }

/**
 * The callback for the yes and no buttons on the BEJoinCoalitionWindow
 */
    public void actionPerformed(ActionEvent e)
        {
        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();

            if (theSource == m_okButton)
                {
                BENode myNode = (BENode)m_CWApp.getNetwork().getExtraData("Me");
                BENodeOrSubNet myNOS = (BENodeOrSubNet)myNode.getExptData("BENodeExchange");
                BENetwork net = m_CWApp.getNetwork();

                Object[] out_args = new Object[3];
                out_args[0] = new Integer(myNOS.getCoalition().getCoalition());
                out_args[1] = new String(myNOS.getCoalition().getCoalitionType());
                out_args[2] = m_zappedFreeRiders;
                BECoalZapAckMsg tmp = new BECoalZapAckMsg(out_args);
                m_CWApp.getSML().sendMessage(tmp);
                m_CWApp.removeSubWindow(this);
                m_CWApp.setMessageLabel("Please wait while others are reading.");

                return;
                }
            }
        }

    private void fillFreeRiderList(BENodeOrSubNet nos, BENetwork net)
        {
        Enumeration enm = net.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode tmpNode = (BENode)enm.nextElement();
            BENodeOrSubNet tmpNOS = (BENodeOrSubNet)tmpNode.getExptData("BENodeExchange");

            if (nos.getCoalition().getCoalitionType().equals(tmpNOS.getCoalition().getCoalitionType()))
                {
                if (nos.getCoalition().getCoalition() == tmpNOS.getCoalition().getCoalition())
                    {
                    if (!tmpNOS.getCoalition().getJoined())
                        {
                        int stoleAmt = tmpNOS.getCoalition().getEarnedCoalResources(tmpNOS,net);
                        if (stoleAmt > 0)
                            {
                            String[] str = new String[3];
                            str[0] = tmpNode.getLabel();
                            str[1] = new String(""+stoleAmt);
                            str[2] = new String(""+stoleAmt);
                            m_freeRiderList.addItem(str);
                            m_freeRiders.insertElementAt(tmpNode,m_freeRiderList.last);
                            }
                        }
                    }
                }
            }
        }

    public void dispose()
        {
        removeLabels();
        super.dispose();
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/be/awt/besczrw.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/be/awt/besczrw.txt");
        }

/**
 * Updates the free rider list with the outcomes of the zapping.
 */
    public void updateFreeRiderList(Vector freeRiders, int numZapped)
        {
        for (int x=0;x<numZapped;x++)
            {
            BENode node = (BENode)m_freeRiders.elementAt(x);
            BENodeOrSubNet nos = (BENodeOrSubNet)node.getExptData("BENodeExchange");
            int stoleAmt = nos.getCoalition().getEarnedCoalResources(nos,m_CWApp.getNetwork());
            String[] str = (String[])m_freeRiderList.getItem(x);
            str[2] = new String(""+stoleAmt);
            m_freeRiderList.replaceItem(str,x);
            m_zappedFreeRiders.addElement(new Integer(node.getID()));
            }
        }
    }