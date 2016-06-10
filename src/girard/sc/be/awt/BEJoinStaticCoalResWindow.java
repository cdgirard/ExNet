package girard.sc.be.awt;

import girard.sc.awt.GridBagPanel;
import girard.sc.awt.NumberTextField;
import girard.sc.be.io.msg.BEJoinCoalAckMsg;
import girard.sc.be.obj.BENetwork;
import girard.sc.be.obj.BENode;
import girard.sc.be.obj.BENodeOrSubNet;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Informs the subjects of the results of the voting.  If they voted to
 * join and the coalition formed they also get to send out a proposed offer
 * that the coalition makes.
 * <br>
 * <br>Started: 07-09-2003
 * @author Dudley Girard
 */
public class BEJoinStaticCoalResWindow extends Frame implements ActionListener
    {
    BENetworkActionClientWindow m_CWApp;
    ExptOverlord m_EOApp;

    NumberTextField m_offerField;

    Button m_okButton;

    public BEJoinStaticCoalResWindow(BENetworkActionClientWindow app)
        {
        m_CWApp = app;
        m_EOApp = m_CWApp.getEOApp();

        initializeLabels();

        setLayout(new BorderLayout());
        setTitle(m_EOApp.getLabels().getObjectLabel("bejscrw_title"));
        setFont(m_EOApp.getMedWinFont());
        setBackground(m_EOApp.getWinBkgColor());

        BENode myNode = (BENode)m_CWApp.getNetwork().getExtraData("Me");
        BENodeOrSubNet myNOS = (BENodeOrSubNet)myNode.getExptData("BENodeExchange");
        BENetwork net = m_CWApp.getNetwork();

  // Start Setup for the Center Panel.

        GridBagPanel centerPanel = new GridBagPanel();

        int votes = myNOS.getCoalition().getNumJoinVotes(net);
        int members = myNOS.getCoalition().getNumCoalMembers(net);
        
        if (myNOS.getCoalition().getFormed())
            {
            centerPanel = createSuccessPanel(votes,members);
            }
        else
            {
            centerPanel = createFailedPanel(votes,members);
            }
                  
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
                out_args[0] = new Integer(myNode.getID());
                out_args[1] = new Boolean(myNOS.getCoalition().getFormed());
                if (myNOS.getCoalition().getFormed())
                    {
                    if (m_offerField.getText().length() == 0)
                        {
                        out_args[2] = new Integer(0);
                        }
                    else
                        {
                        out_args[2] = new Integer(m_offerField.getIntValue());
                        }
                    }
                else
                    {
                    out_args[2] = new Integer(0);
                    }
                //-kar-
                System.out.println("from BEJoinStaticCoalResWindow Node "+out_args[0]+" voted "+out_args[1]+" offered  "+out_args[2]);
                //-kar-
                BEJoinCoalAckMsg tmp = new BEJoinCoalAckMsg(out_args);
                m_CWApp.getSML().sendMessage(tmp);
                m_CWApp.removeSubWindow(this);
                m_CWApp.setMessageLabel("Please wait while others are deciding.");

                return;
                }
            }
        }

    private GridBagPanel createFailedPanel(int votes, int members)
        {
        int counter = 1;

        BENode myNode = (BENode)m_CWApp.getNetwork().getExtraData("Me");
        BENodeOrSubNet myNOS = (BENodeOrSubNet)myNode.getExptData("BENodeExchange");
        BENetwork net = m_CWApp.getNetwork();

        GridBagPanel centerPanel = new GridBagPanel();

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bejscrw_tcft")),1,counter++,8,1);
        if (myNOS.getCoalition().getReportMethod() == 0)
            {
            if (votes == 0)
                {
                centerPanel.constrain(new Label("No one joined the coalition."),1,counter++,8,1);
                }
            else if (votes == 1)
                {
                centerPanel.constrain(new Label("Only one person joined the coalition."),1,counter++,8,1);
                }
            else
                {
                centerPanel.constrain(new Label("Only "+votes+" people joined the coalition."),1,counter++,8,1);
                }
            }
        else
            {
            if (myNOS.getCoalition().getReportMethod() == 1)
                {
                centerPanel.constrain(new Label("Only one person joined the coalition."),1,counter++,8,1);
                }
            else
                {
                centerPanel.constrain(new Label("Only "+myNOS.getCoalition().getReportMethod()+" people joined the coalition."),1,counter++,8,1);
                }
            }
        return centerPanel;
        }
    private GridBagPanel createSuccessPanel(int votes, int members)
        {
        int counter = 1;

        BENode myNode = (BENode)m_CWApp.getNetwork().getExtraData("Me");
        BENodeOrSubNet myNOS = (BENodeOrSubNet)myNode.getExptData("BENodeExchange");
        BENetwork net = m_CWApp.getNetwork();

        GridBagPanel centerPanel = new GridBagPanel();

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bejscrw_tcsi")),1,counter++,8,1,GridBagConstraints.CENTER);

        if (votes == members)
            {
            centerPanel.constrain(new Label("Everyone joined the coalition."),1,counter++,8,1);
            }
        else if (votes == 1)
            {
            centerPanel.constrain(new Label("One person joined the coalition."),1,counter++,8,1);
            }
        else
            {
            centerPanel.constrain(new Label(""+votes+" people joined the coalition."),1,counter++,8,1);
            }

        centerPanel.constrain(new Label("You may now send how much the coalition will offer."),1,counter++,8,1,GridBagConstraints.CENTER);
        centerPanel.constrain(new Label("The values will then be averaged together to represent a consensus"),1,counter++,8,1,GridBagConstraints.CENTER);
        centerPanel.constrain(new Label("of what the groups feels should be offered."),1,counter++,8,1,GridBagConstraints.CENTER);
        centerPanel.constrain(new Label("This consensus offer helps you coordinate with others"),1,counter++,8,1,GridBagConstraints.CENTER);
        centerPanel.constrain(new Label("in the group, but does not determine the actual offer you send."),1,counter++,8,1,GridBagConstraints.CENTER);
        centerPanel.constrain(new Label("Proposed value for the consensus offer: "),1,counter,4,1);
        m_offerField = new NumberTextField(3);
        m_offerField.setAllowNegative(false);
        m_offerField.setAllowFloat(false);
        centerPanel.constrain(m_offerField,5,counter++,4,1);

        return centerPanel;
        }

    public void dispose()
        {
        removeLabels();
        super.dispose();
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/be/awt/bejscrw.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/be/awt/bejscrw.txt");
        }
    }