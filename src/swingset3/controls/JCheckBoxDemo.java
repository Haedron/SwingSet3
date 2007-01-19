/*
 * JCheckBoxDemo.java
 *
 * Created on August 22, 2006, 3:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package swingset3.controls;

import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author aim
 */
public class JCheckBoxDemo extends JPanel {
    
    public JCheckBoxDemo() {        
        setToolTipText("Demonstrates JCheckBox, Swing's boolean choice component.");
        initComponents();
    }
    
                
    protected void initComponents() {
        setLayout(new GridLayout(0,1));
        
        add(createSimpleCheckBoxes());
    }
    
    protected JPanel createSimpleCheckBoxes() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Simple CheckBoxes"));
        
        //<snip>Create simple checkbox
        JCheckBox checkbox = new JCheckBox("Over 18");

        //</snip>
 
        
        panel.add(checkbox);
        
        return panel;                
    }
    
    public static void main(String args[]) {
        final JCheckBoxDemo demo = new JCheckBoxDemo();
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("JCheckBox Demo");
                frame.add(demo);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }    
}
