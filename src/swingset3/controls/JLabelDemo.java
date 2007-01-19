/*
 * JLabelDemo.java
 *
 * Created on September 22, 2006, 1:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package swingset3.controls;

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
public class JLabelDemo extends JPanel {
    
    public JLabelDemo() {        
        setToolTipText("Demonstrates JLabel, Swing's popup choice component.");
        initComponents();
        setBorder(new EmptyBorder(12,10,12,10));
    }    
                
    protected void initComponents() {
        add(new JLabel("JLabel Demo Coming Soon...."));
    }
    
    public static void main(String args[]) {
        final JLabelDemo demo = new JLabelDemo();
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("JLabel Demo");
                frame.add(demo);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }    
}
