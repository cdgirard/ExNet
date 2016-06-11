package girard.sc.expt.web;

/* Panel that acts as a login screen for subjects and experiments to grant them
 access to the JoinExperimentPage panel.

 Author: Dudley Girard
 Created: 05-24-2001
 */

import girard.sc.awt.ColorTextField;
import girard.sc.awt.ErrorDialog;
import girard.sc.awt.GraphicButton;
import girard.sc.awt.GridBagPanel;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.msg.ExptSubjectLoginReqMsg;
import girard.sc.web.WebPanel;

import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

public class SubjectLoginPage extends WebPanel implements ActionListener
{
    ExptOverlord m_EOApp;

    GridBagPanel m_spacePanel = new GridBagPanel();
    GridBagPanel m_MainPanel = new GridBagPanel();

    ColorTextField m_UserNameField, m_PasswordField;
    GraphicButton m_LoginButton, m_CancelButton;
    int m_buttonWidth = 150;
    int m_buttonHeight = 40;

    public SubjectLoginPage(ExptOverlord app)
    {
	m_EOApp = app;

	initializeLabels();

	setLayout(new GridLayout(1, 1));
	setTitle(m_EOApp.getLabels().getObjectLabel("slp_title"));

	m_MainPanel.setBackground(m_EOApp.getDispBkgColor());
	m_MainPanel.setFont(m_EOApp.getMedLabelFont());

	m_spacePanel.setBackground(m_EOApp.getDispBkgColor());

	m_UserNameField = new ColorTextField(30);
	m_UserNameField.setBackground(m_EOApp.getObjectBkgColor());
	m_MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("slp_logn")), 1, 1, 1, 1);
	m_MainPanel.constrain(m_UserNameField, 2, 1, 3, 1);

	m_PasswordField = new ColorTextField(30);
	m_PasswordField.setEchoChar('*');
	m_PasswordField.setBackground(m_EOApp.getObjectBkgColor());
	m_MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("slp_password")), 1, 2, 1, 1);
	m_MainPanel.constrain(m_PasswordField, 2, 2, 3, 1);

	m_LoginButton = new GraphicButton(m_buttonWidth, m_buttonHeight, null);
	m_LoginButton.addActionListener(this);
	m_MainPanel.constrain(m_LoginButton, 1, 3, 2, 1, GridBagConstraints.CENTER);

	m_CancelButton = new GraphicButton(m_buttonWidth, m_buttonHeight, null);
	m_CancelButton.addActionListener(this);
	m_MainPanel.constrain(m_CancelButton, 3, 3, 2, 1, GridBagConstraints.CENTER);

	loadImages();

	m_spacePanel.constrain(m_MainPanel, 1, 1, 60, 40, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH);

	if (m_EOApp.getWidth() > 640)
	{
	    m_spacePanel.constrain(new Panel(new GridLayout(1, 1)), 61, 1, m_EOApp.getWidth() / 10, 40, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH);
	}
	if (m_EOApp.getHeight() > 480)
	{
	    m_spacePanel.constrain(new Panel(new GridLayout(1, 1)), 1, 41, m_EOApp.getWidth() / 10, m_EOApp.getHeight() / 10, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH);
	}

	add(m_spacePanel);
    }

    public void actionPerformed(ActionEvent e)
    {
	if (e.getSource() instanceof GraphicButton)
	{
	    GraphicButton theSource = (GraphicButton) e.getSource();

	    if (theSource == m_LoginButton)
	    {
		String usr = m_UserNameField.getText();
		String pwd = m_PasswordField.getText();
		if (SendLoginRequest(usr, pwd))
		{
		    m_EOApp.removeThenAddPanel(this, new JoinExperimentPage(m_EOApp));
		} else
		{
		    new ErrorDialog("Invalid Login");
		}
	    }
	    if (theSource == m_CancelButton)
	    {
		// Handle Cancel
		m_EOApp.getWB().dispose();
	    }
	}
    }

    public void initializeLabels()
    {
	m_EOApp.initializeLabels("girard/sc/expt/web/slp.txt");
    }

    public void removeLabels()
    {
	m_EOApp.removeLabels("girard/sc/expt/web/slp.txt");
    }

    private boolean SendLoginRequest(String usr, String pwd)
    {
	Object[] out_args = new Object[2];
	out_args[0] = usr;
	out_args[1] = pwd;

	ExptSubjectLoginReqMsg tmp = new ExptSubjectLoginReqMsg(out_args);
	ExptMessage em = m_EOApp.sendExptMessage(tmp);

	if (em instanceof ExptErrorMsg)
	{
	    return false;
	} else
	{
	    Integer uid = (Integer) em.getArgs()[0];
	    m_EOApp.setUserID(uid.intValue());
	    return true;
	}
    }

    private void loadImages()
    {
	int x, y;
	Graphics g;
	Image tmp, tmp2;

	// Initialize Login Button Image
	tmp = m_EOApp.getButtonImage();

	tmp2 = m_EOApp.createImage(m_buttonWidth - 6, m_buttonHeight - 6);

	g = tmp2.getGraphics();

	g.drawImage(tmp, 0, 0, m_buttonWidth - 6, m_buttonHeight - 6, m_EOApp.getWB());
	g.setFont(m_EOApp.getLgButtonFont());
	g.setColor(m_EOApp.getButtonLabelColor());
	x = (m_buttonWidth - 6 - m_EOApp.getLabels().getObjectLabel("slp_login").length() * 12) / 2;
	y = ((m_buttonHeight - 6) / 2) + 5;
	g.drawString(m_EOApp.getLabels().getObjectLabel("slp_login"), x, y);

	m_LoginButton.setImage(tmp2);

	// Create Cancel Button
	tmp2 = m_EOApp.createImage(m_buttonWidth - 6, m_buttonHeight - 6);

	g = tmp2.getGraphics();

	g.drawImage(tmp, 0, 0, m_buttonWidth - 6, m_buttonHeight - 6, m_EOApp.getWB());
	g.setFont(m_EOApp.getLgButtonFont());
	g.setColor(m_EOApp.getButtonLabelColor());
	x = (m_buttonWidth - 6 - m_EOApp.getLabels().getObjectLabel("slp_cancel").length() * 12) / 2;
	y = ((m_buttonHeight - 6) / 2) + 5;
	g.drawString(m_EOApp.getLabels().getObjectLabel("slp_cancel"), x, y);

	m_CancelButton.setImage(tmp2);
    }
}
