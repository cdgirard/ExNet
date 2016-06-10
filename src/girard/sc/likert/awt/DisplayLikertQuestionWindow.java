package girard.sc.likert.awt;

import girard.sc.awt.GridBagPanel;
import girard.sc.likert.obj.LikertQuestion;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DisplayLikertQuestionWindow extends Dialog implements ActionListener
    {
    Button m_doneButton;

    public DisplayLikertQuestionWindow(LikertQuestion lq)
        {
        super(new Frame(), lq.getTitle(),true);

        setLayout(new BorderLayout());
        setFont(lq.getWinFont());

   // Setup North Area
        TextArea question = new TextArea("",lq.getWinRows(),lq.getWinColumns(),TextArea.SCROLLBARS_VERTICAL_ONLY);
        question.setText(lq.getQuestion());
        question.setFont(lq.getWinFont());

        add("North",question);
   // End Setup of North Area

   // Setup Central Area
        GridBagPanel centerPanel = new GridBagPanel();

        centerPanel.constrain(new Label(""),1,1,3,1); 

        centerPanel.constrain(new Label(lq.getLeft()),1,2,1,1,GridBagConstraints.WEST);
        centerPanel.constrain(new Label(lq.getCenter()),2,2,1,1,GridBagConstraints.CENTER);
        centerPanel.constrain(new Label(lq.getRight()),3,2,1,1,GridBagConstraints.EAST);

        add("Center",centerPanel);
    // End Setup of Central Area

    // Start Setup of South Area
        GridBagPanel southPanel = new GridBagPanel();

        Panel tmpPanel;
        for (int i=1;i<lq.getRange();i++)
            {
            tmpPanel = new Panel();
            tmpPanel.setLayout(new GridLayout(2,1));
            tmpPanel.add(new Label(""+i));
            tmpPanel.add(new Checkbox("",false));
            southPanel.constrain(tmpPanel,i*2-1,1,1,2,GridBagConstraints.CENTER);

            tmpPanel = new Panel();
            tmpPanel.setLayout(new GridLayout(2,1));
            tmpPanel.add(new Label("-"));
            tmpPanel.add(new Label(""));
            southPanel.constrain(tmpPanel,i*2,1,1,2,GridBagConstraints.CENTER);
            }
        tmpPanel = new Panel();
        tmpPanel.setLayout(new GridLayout(2,1));
        tmpPanel.add(new Label(""+lq.getRange()));
        tmpPanel.add(new Checkbox("",false));
        southPanel.constrain(tmpPanel,lq.getRange()*2-1,1,1,2,GridBagConstraints.CENTER);

        m_doneButton = new Button("Done");
        m_doneButton.addActionListener(this);
        southPanel.constrain(m_doneButton,1,3,lq.getRange()*2,1,GridBagConstraints.CENTER);

        add("South",southPanel);
   // End Setup of South Area.

        pack();
        setLocation(lq.getWinLoc().x,lq.getWinLoc().y);
        show();
        }

    public void actionPerformed(ActionEvent e)
        {
        if (e.getSource() instanceof Button)
            {
            dispose();
            }
        }
    }