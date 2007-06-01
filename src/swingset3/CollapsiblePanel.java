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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.interpolation.PropertySetter;

/**
 * 
 * @author aim
 */
//remind(aim): replace with JXCollapsiblePanel
public class CollapsiblePanel extends JPanel {
    protected JCheckBox expandCheckBox;
    protected Component child;
    
    protected boolean expanded = true;
    
    protected Dimension prefSize;
    protected Animator animator;        
    
    /** Creates a new instance of CollapsiblePanel */
    public CollapsiblePanel(String title, Component child) {
        setLayout(new BorderLayout());
        
        Box box = Box.createHorizontalBox();
        add(box, BorderLayout.NORTH);
        
        expandCheckBox = new JCheckBox(title);
        expandCheckBox.setToolTipText("Click arrow to expand or contract source code panel");
        Font font = UIManager.getFont("Label.font");
        System.out.println(font);
        expandCheckBox.setFont(font.deriveFont(Font.PLAIN, 13));
        expandCheckBox.setHorizontalTextPosition(JCheckBox.RIGHT);
        expandCheckBox.setSelectedIcon(new ImageIcon(
                    CollapsiblePanel.class.getResource("resources/images/down_arrow.png")));
        expandCheckBox.setIcon(new ImageIcon(
                    CollapsiblePanel.class.getResource("resources/images/right_arrow.png")));
        expandCheckBox.setSelected(isExpanded());
        expandCheckBox.addChangeListener(new CollapseListener());
        box.add(expandCheckBox);
        
        add(child, BorderLayout.CENTER);
        this.prefSize = child.getPreferredSize();
        this.child = child;
                       
    }
    
    public void setExpanded(boolean expanded) {
        boolean oldExpanded = this.expanded;
        if (oldExpanded != expanded) {
                //(animator == null || !animator.isRunning())) {
            this.expanded = expanded;
            PropertySetter resizer = new PropertySetter(this, "collapseHeight",
                    expanded? 0 : prefSize.height, expanded? prefSize.height : 0);
            animator = new Animator(600, resizer);
            animator.setStartDelay(10);
            animator.setAcceleration(.2f);
            animator.setDeceleration(.3f);
            animator.start();
            expandCheckBox.setSelected(expanded);
            firePropertyChange("expanded", oldExpanded, expanded);
        }
    }
    
    // intended only for animator, but must be public
    public void setCollapseHeight(int height) {
        child.setPreferredSize(new Dimension(prefSize.width, height));
        revalidate();
        repaint();
    }

    public boolean isExpanded() {
        return expanded;
    }
    
    private class CollapseListener implements ChangeListener {
        public void stateChanged(ChangeEvent event) {
            setExpanded(expandCheckBox.isSelected());
        }
    }

    
}
