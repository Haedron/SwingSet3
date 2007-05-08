/*
 * CollapsiblePanel.java
 *
 * Created on May 3, 2007, 10:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package swingset3;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author aim
 */
public class CollapsiblePanel extends JPanel {
    JCheckBox expandCheckBox;
    Component child;
    
    /** Creates a new instance of CollapsiblePanel */
    public CollapsiblePanel(String title, Component child) {
        setLayout(new BorderLayout());
        
        Box box = Box.createHorizontalBox();
        add(box, BorderLayout.NORTH);
        
        //box.add(Box.createRigidArea(new Dimension(1,20)));
        
        expandCheckBox = new JCheckBox(title);
        Font font = UIManager.getFont("Label.font");
        System.out.println(font);
        expandCheckBox.setFont(font.deriveFont(Font.PLAIN, 13));
        expandCheckBox.setHorizontalTextPosition(JCheckBox.RIGHT);
        expandCheckBox.setSelectedIcon(new ImageIcon(
                    CollapsiblePanel.class.getResource("resources/images/down_arrow.png")));
        expandCheckBox.setIcon(new ImageIcon(
                    CollapsiblePanel.class.getResource("resources/images/right_arrow.png")));
        expandCheckBox.addChangeListener(new CollapseListener());
        box.add(expandCheckBox);
        
        add(child, BorderLayout.CENTER);
        this.child = child;
                       
    }
    
    public void setExpanded(boolean expanded) {
        boolean oldExpanded = isExpanded();
        child.setVisible(expanded);
        firePropertyChange("expanded", oldExpanded, expanded);
    }
    
    public boolean isExpanded() {
        return child.isVisible();
    }
    
    private class CollapseListener implements ChangeListener {
        public void stateChanged(ChangeEvent event) {
            setExpanded(expandCheckBox.isSelected());
        }
    }

    
}
