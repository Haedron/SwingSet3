/*
 * JTreeDemo.java
 *
 * Created on September 22, 2006, 3:45 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package swingset3.data;

import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author aim
 */
public class JTreeDemo extends JPanel {
    
    public JTreeDemo() {        
        setToolTipText("Demonstrates JTree, Swing's popup choice component.");
        initComponents();
        setBorder(new EmptyBorder(12,10,12,10));
    }    
                
    protected void initComponents() {
        add(new JLabel("JTree Demo Coming Soon...."));
    }
    
    public static void main(String args[]) {
        final JTreeDemo demo = new JTreeDemo();
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("JTree Demo");
                frame.add(demo);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }    
}