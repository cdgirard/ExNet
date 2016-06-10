package girard.sc.web;

import java.awt.LayoutManager;
import java.awt.Panel;

public class WebPanel extends Panel
    { 
    String m_title = new String("");

    public WebPanel()
        {
        }
    public WebPanel(String str)
        {
        m_title = str;
        }
    public WebPanel(LayoutManager layout)
        {
        super(layout);
        }
    public WebPanel (LayoutManager layout, String str)
        {
        super(layout);
 
        m_title = str;
        }

    public String getTitle()
        {
        return m_title;
        }

    public void init()
        { 
        }

    public void initializeLabels()
        {
        }

    public void removeLabels()
        {
        }

    public void setTitle(String str)
        {
        m_title = str;
        }
    }
