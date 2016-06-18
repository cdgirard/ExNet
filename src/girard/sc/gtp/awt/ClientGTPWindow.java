package girard.sc.gtp.awt;

import girard.sc.awt.ErrorDialog;
import girard.sc.awt.GridBagPanel;
import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.obj.ClientExptInfo;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.gtp.obj.GenericTutorialPage;
import girard.sc.tp.awt.ClientTPWindow;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Used to display tutorial pages which display a window of text and can have
 * a window with an image attached to it assigned to the text window as well.
 * <p>
 * <br> Started: 01-08-2002
 * <br> Modified: 10-09-2002
 * <br> Modified: 04-03-2003
 * <p>
 * @author Dudley Girard
 * 
 */

public class ClientGTPWindow extends ClientTPWindow
    {
    GenericTutorialPage m_tutorialPage;

    Button m_nextButton, m_prevButton;

    TextArea m_information;

    ClientImageWindow m_displayWindow = null;

    public ClientGTPWindow(ExptOverlord app1, ClientExptInfo app2, ExptMessageListener app3)
        {
        super(app1,app2,app3);

        m_tutorialPage = (GenericTutorialPage)m_tp;

        initializeLabels();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(m_EOApp.getWinBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("cgtpw_title"));
        getContentPane().setFont(m_EOApp.getMedWinFont());

     // Setup Central Panel
        GridBagPanel centerPanel = new GridBagPanel();

        m_information = new TextArea("",m_tutorialPage.getWinRows(),m_tutorialPage.getWinColumns(),TextArea.SCROLLBARS_VERTICAL_ONLY);
        m_information.setFont(m_tutorialPage.getWinFont());
        m_information.setText(m_tutorialPage.getInstructions());
        m_information.setEditable(false);

        centerPanel.constrain(m_information,1,1,4,4);
     // End setup of Central Panel

     // Start setup of South Panel
        GridBagPanel southPanel = new GridBagPanel();

        Hashtable h = (Hashtable)m_ExpApp.getActiveAction();
        Vector pages = (Vector)h.get("TutPages");
        Integer currentPage = (Integer)h.get("CurrentPage");

        if (currentPage.intValue() == 0)
            {
            m_nextButton = new Button(m_EOApp.getLabels().getObjectLabel("cgtpw_done"));
            m_nextButton.addActionListener(this);
            southPanel.constrain(m_nextButton,1,1,4,1,GridBagConstraints.CENTER);
            }
        else
            {
            m_prevButton = new Button(m_EOApp.getLabels().getObjectLabel("cgtpw_prev"));
            m_prevButton.addActionListener(this);
            southPanel.constrain(m_prevButton,1,1,2,1,GridBagConstraints.WEST);

            if (currentPage.intValue() != pages.size() - 1)
                {
                m_nextButton = new Button(m_EOApp.getLabels().getObjectLabel("cgtpw_done"));
                m_nextButton.addActionListener(this);
                southPanel.constrain(m_nextButton,3,1,2,1,GridBagConstraints.EAST);
                }
            else
                {
                m_nextButton = new Button(m_EOApp.getLabels().getObjectLabel("cgtpw_finish"));
                m_nextButton.addActionListener(this);
                southPanel.constrain(m_nextButton,3,1,2,1,GridBagConstraints.EAST);
                }
            }

        
     // End setup of South Panel

        showImageWindow();

     // End Setup User Fields

        getContentPane().add("Center",centerPanel);
        getContentPane().add("South",southPanel);

        pack();
        this.setLocation(m_tutorialPage.getWinLoc().x,m_tutorialPage.getWinLoc().y);
        show();
        }
    
    public void actionPerformed(ActionEvent e) 
        {
        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();

            if (theSource == m_nextButton)
                {
                removeTutWindow(1);
                }
            if (theSource == m_prevButton)
                {
                removeTutWindow(-1);
                }
            }
        if (e.getSource() instanceof ExptMessage)
            {
            synchronized(m_SML)
                {
                ExptMessage em = (ExptMessage)e.getSource();

                if (em instanceof ExptErrorMsg)
                    {
                    String str = (String)em.getArgs()[0];
                    new ErrorDialog(str);
                    }
                else
                    {
                    em.getClientResponse(this);
                    }
                }
            }
        }

    public void cleanUpWindow()
        {
        if (m_displayWindow != null)
            m_displayWindow.dispose();
        removeLabels();
        dispose();
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/gtp/awt/cgtpw.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/gtp/awt/cgtpw.txt");
        }
    
    public void showImageWindow()
        {
        if (!m_tutorialPage.getImageTitle().equals(GenericTutorialPage.NO_IMAGE))
            {
            Hashtable h = (Hashtable)m_ExpApp.getActiveAction();
            Hashtable ed = (Hashtable)h.get("ExtraData");
            String str = new String(m_tutorialPage.getImageLocation()+"-"+m_tutorialPage.getImageTitle());
            Image img = null;
            if (ed.contains(str))
                {
                img = (Image)ed.get(str);
                }
            else
                {
                img = m_EOApp.getImage(m_tutorialPage.getImageLocation());
                }
            
            m_displayWindow = new ClientImageWindow(img,m_tutorialPage.getImageTitle());
            m_displayWindow.setLocation(m_tutorialPage.getImgLoc().x,m_tutorialPage.getImgLoc().y);
            m_displayWindow.show();
            }
        }
    }
