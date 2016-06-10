package girard.sc.awt;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

/**
 * A JPanel that allows addition of components using a GridBag layout. This
 * class is provided to allow simplified use of the GridBagLayout.
 * @author Clif Presser
 */
public class JGridBagPanel extends JPanel
    {
    GridBagLayout m_layout;
    GridBagConstraints m_constraint;

  /** 
   Constructor
   */
    public JGridBagPanel()
        {
      //create the layout and constraint
        m_layout = new GridBagLayout();
        m_constraint = new GridBagConstraints();
        setLayout(m_layout);
		
	// Set default values of the constraints.
        m_constraint.fill = GridBagConstraints.NONE;
        m_constraint.anchor = GridBagConstraints.NORTHWEST;
        m_constraint.weightx = 1.0;
        m_constraint.weighty = 1.0;
        m_constraint.insets = new Insets(1,1,1,1);
	  }
	
  /**
   Add a component to this Panel.
   @param component The Component to add to this Panel.
   @param iGrid_x  The x coordinate this component should have in the grid.
   @param iGrid_y  The y coordinate this component should have in the grid.
   @param iGrid_width The number of grid cells wide the component should be.
   @param iGrid_height The number of grid cells high the component should be.
   @param iFill The direction the component should grow as necesary.
   @param iAnchor How the component should be displayed within its grid when
      it is smaller than the space allotted.
   @param fWeight_x Specifies how much extra space this component should
      receive when there is extra in the x direction.
   @param fWeight_y Specifies how much extra space this component should
      receive when there is extra in the y direction.
   @param iTop The top margin for this component.
   @param iLeft The left margin for this component.
   @param iBottom The bottom margin for this component.
   @param iRight The right margin for this component.
   @see java.awt.GridBagLayout
   @see java.awt.GridBagConstraints
   @see java.awt.Insets
   */
    public void constrain(Component component,
                     int iGrid_x, int iGrid_y, int iGrid_width, 
                     int iGrid_height, int iAnchor, int iFill,
                     double fWeight_x, double fWeight_y, int iTop,
                     int iLeft, int iBottom, int iRight)
        {
        GridBagConstraints tmp_constraint = (GridBagConstraints)m_constraint.clone();

        tmp_constraint.gridx = iGrid_x;
        tmp_constraint.gridy = iGrid_y;
        tmp_constraint.gridwidth = iGrid_width;
        tmp_constraint.gridheight = iGrid_height;
        tmp_constraint.fill = iFill;
        tmp_constraint.anchor = iAnchor;
        tmp_constraint.weightx = fWeight_x;
        tmp_constraint.weighty = fWeight_y;
        if (iTop + iLeft + iBottom + iRight > 0)
		{
		tmp_constraint.insets = new Insets(iTop, iLeft, iBottom, iRight);
		}
		
        add(component,tmp_constraint);
	  }
	
  /**
   Add a component to this Panel.
   @param component The Component to add to this Panel.
   @param iGrid_x  The x coordinate this component should have in the grid.
   @param iGrid_y  The y coordinate this component should have in the grid.
   @param iGrid_width The number of grid cells wide the component should be.
   @param iGrid_height The number of grid cells high the component should be.
   @see java.awt.GridBagLayout
   @see java.awt.GridBagConstraints
   @see java.awt.Insets
   */
    public void constrain(Component component, int iGrid_x, int iGrid_y,
		              int iGrid_width, int iGrid_height)
        {
        GridBagConstraints tmp_constraint = (GridBagConstraints)m_constraint.clone();

        tmp_constraint.gridx = iGrid_x;
        tmp_constraint.gridy = iGrid_y;
        tmp_constraint.gridwidth = iGrid_width;
        tmp_constraint.gridheight = iGrid_height;
		
        add(component,tmp_constraint);
	  }
	
	
  /**
   Add a component to this Panel.
   @param component The Component to add to this Panel.
   @param iGrid_x  The x coordinate this component should have in the grid.
   @param iGrid_y  The y coordinate this component should have in the grid.
   @param iGrid_width The number of grid cells wide the component should be.
   @param iGrid_height The number of grid cells high the component should be.
   @param iFill The direction the component should grow as necesary.
   @param iAnchor How the component should be displayed within its grid when
      it is smaller than the space allotted.
   @see java.awt.GridBagLayout
   @see java.awt.GridBagConstraints
   @see java.awt.Insets
   */
    public void constrain(Component component, int iGrid_x, int iGrid_y,
	                  int iGrid_width, int iGrid_height, int iAnchor)
        {
        GridBagConstraints tmp_constraint = (GridBagConstraints)m_constraint.clone();

        tmp_constraint.gridx = iGrid_x;
        tmp_constraint.gridy = iGrid_y;
        tmp_constraint.gridwidth = iGrid_width;
        tmp_constraint.gridheight = iGrid_height;
        tmp_constraint.anchor = iAnchor;

        add(component,tmp_constraint);
	  }


    public void constrain(Component component, int iGrid_x, int iGrid_y,
	                  int iGrid_width, int iGrid_height, int iAnchor, int iFill)
        {
        GridBagConstraints tmp_constraint = (GridBagConstraints)m_constraint.clone();

        tmp_constraint.gridx = iGrid_x;
        tmp_constraint.gridy = iGrid_y;
        tmp_constraint.gridwidth = iGrid_width;
        tmp_constraint.gridheight = iGrid_height;
        tmp_constraint.anchor = iAnchor;
        tmp_constraint.fill = iFill;

        add(component,tmp_constraint);
        }

    /**
   Add a component to this Panel.
   @param component The Component to add to this Panel.
   @param iGrid_x  The x coordinate this component should have in the grid.
   @param iGrid_y  The y coordinate this component should have in the grid.
   @param iGrid_width The number of grid cells wide the component should be.
   @param iGrid_height The number of grid cells high the component should be.
   @see java.awt.GridBagLayout
   @see java.awt.GridBagConstraints
   @see java.awt.Insets
   */
    public void pad(Component component, int iGrid_x, int iGrid_y,
		              int iGrid_width, int iGrid_height, int iPad_x, int iPad_y)
        {
        GridBagConstraints tmp_constraint = (GridBagConstraints)m_constraint.clone();

        tmp_constraint.gridx = iGrid_x;
        tmp_constraint.gridy = iGrid_y;
        tmp_constraint.gridwidth = iGrid_width;
        tmp_constraint.gridheight = iGrid_height;
        tmp_constraint.ipadx = iPad_x;
        tmp_constraint.ipady = iPad_y;
		
        add(component,tmp_constraint);
	  }

    /**
   Add a component to this Panel.
   @param component The Component to add to this Panel.
   @param iGrid_x  The x coordinate this component should have in the grid.
   @param iGrid_y  The y coordinate this component should have in the grid.
   @param iGrid_width The number of grid cells wide the component should be.
   @param iGrid_height The number of grid cells high the component should be.
   @param iFill The direction the component should grow as necesary.
   @param iAnchor How the component should be displayed within its grid when
      it is smaller than the space allotted.
   @see java.awt.GridBagLayout
   @see java.awt.GridBagConstraints
   @see java.awt.Insets
   */
    public void pad(Component component, int iGrid_x, int iGrid_y,
	                  int iGrid_width, int iGrid_height, int iAnchor, int iPad_x, int iPad_y)
        {
        GridBagConstraints tmp_constraint = (GridBagConstraints)m_constraint.clone();

        tmp_constraint.gridx = iGrid_x;
        tmp_constraint.gridy = iGrid_y;
        tmp_constraint.gridwidth = iGrid_width;
        tmp_constraint.gridheight = iGrid_height;
        tmp_constraint.anchor = iAnchor;
        tmp_constraint.ipadx = iPad_x;
        tmp_constraint.ipady = iPad_y;

        add(component,tmp_constraint);
	  }
	
}
