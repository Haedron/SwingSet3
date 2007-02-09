/*
 * JComboBoxDemo.java
 *
 * Created on September 22, 2006, 1:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package swingset3.demos.controls;

import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author aim
 */
public class JComboBoxDemo extends JPanel {

    // remind: replace with annotation?
    public static String getShortDescription() {
        return "Demonstrates JComboBox, Swing's popup choice component.";
    }
    
    public JComboBoxDemo() {        
        setToolTipText(getShortDescription());
        initComponents();
        setBorder(new EmptyBorder(12,10,12,10));
    }    
                
    protected void initComponents() {
        add(new JLabel("JComboBox Demo Coming Soon...."));
    }
    
    public static void main(String args[]) {
        final JComboBoxDemo demo = new JComboBoxDemo();
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("JComboBox Demo");
                frame.add(demo);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }    
}
