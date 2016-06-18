package girard.sc.expt.web;

import girard.sc.awt.ColorTextField;
import girard.sc.awt.ErrorDialog;
import girard.sc.awt.GraphicButton;
import girard.sc.awt.GridBagPanel;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptExperimenterLoginReqMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.web.WebPanel;

import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
/**
 * Created for the new stand alone application so that Experimenters
 * can log into the system.
 * @author cdgira
 *
 */
public class ExperimenterLoginPage extends WebPanel implements ActionListener
{
    ExptOverlord m_EOApp;

    GridBagPanel m_spacePanel = new GridBagPanel();
    GridBagPanel m_MainPanel = new GridBagPanel();

    ColorTextField m_UserNameField, m_PasswordField;
    GraphicButton m_LoginButton, m_CancelButton;
    int m_buttonWidth = 150;
    int m_buttonHeight = 40;

    public ExperimenterLoginPage(ExptOverlord app)
    {
	m_EOApp = app;

	initializeLabels();

	setLayout(new GridLayout(1, 1));
	setTitle(m_EOApp.getLabels().getObjectLabel("elp_title"));

	m_MainPanel.setBackground(m_EOApp.getDispBkgColor());
	m_MainPanel.setFont(m_EOApp.getMedLabelFont());

	m_spacePanel.setBackground(m_EOApp.getDispBkgColor());

	m_UserNameField = new ColorTextField(30);
	m_UserNameField.setBackground(m_EOApp.getObjectBkgColor());
	m_MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("elp_logn")), 1, 1, 1, 1);
	m_MainPanel.constrain(m_UserNameField, 2, 1, 3, 1);

	m_PasswordField = new ColorTextField(30);
	m_PasswordField.setEchoChar('*');
	m_PasswordField.setBackground(m_EOApp.getObjectBkgColor());
	m_MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("elp_password")), 1, 2, 1, 1);
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
		if (sendLoginRequest(usr, pwd))
		{
		    m_EOApp.removeThenAddPanel(this, new OptionsPage(m_EOApp));
		} else
		{
		    new ErrorDialog("Invalid Login");
		}
	    }
	    if (theSource == m_CancelButton)
	    {
		m_EOApp.dispose();
	    }
	}
    }

    public void initializeLabels()
    {
	m_EOApp.initializeLabels("girard/sc/expt/web/elp.txt");
    }

    public void removeLabels()
    {
	m_EOApp.removeLabels("girard/sc/expt/web/elp.txt");
    }

    private boolean sendLoginRequest(String usr, String pwd)
    {
	Object[] out_args = new Object[2];
	out_args[0] = usr;
	out_args[1] = pwd;

	ExptExperimenterLoginReqMsg tmp = new ExptExperimenterLoginReqMsg(out_args);
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
	Image tmp;

	// Initialize Login Button Image
	tmp = m_EOApp.getButtonImage();

	BufferedImage tmp2 = new BufferedImage(m_buttonWidth - 6, m_buttonHeight - 6,BufferedImage.TYPE_3BYTE_BGR);

	g = tmp2.getGraphics();

	g.drawImage(tmp, 0, 0, m_buttonWidth - 6, m_buttonHeight - 6, null);
	g.setFont(m_EOApp.getLgButtonFont());
	g.setColor(m_EOApp.getButtonLabelColor());
	x = (m_buttonWidth - 6 - m_EOApp.getLabels().getObjectLabel("elp_login").length() * 12) / 2;
	y = ((m_buttonHeight - 6) / 2) + 5;
	g.drawString(m_EOApp.getLabels().getObjectLabel("elp_login"), x, y);

	m_LoginButton.setImage(tmp2);

	// Create Cancel Button
	tmp2 = new BufferedImage(m_buttonWidth - 6, m_buttonHeight - 6,BufferedImage.TYPE_3BYTE_BGR);

	g = tmp2.getGraphics();

	g.drawImage(tmp, 0, 0, m_buttonWidth - 6, m_buttonHeight - 6, null);
	g.setFont(m_EOApp.getLgButtonFont());
	g.setColor(m_EOApp.getButtonLabelColor());
	x = (m_buttonWidth - 6 - m_EOApp.getLabels().getObjectLabel("elp_cancel").length() * 12) / 2;
	y = ((m_buttonHeight - 6) / 2) + 5;
	g.drawString(m_EOApp.getLabels().getObjectLabel("elp_cancel"), x, y);

	m_CancelButton.setImage(tmp2);
    }
}
