/*
 * JListDemo.java
 *
 * Created on September 22, 2006, 3:45 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package swingset3.demos.data;

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
public class JListDemo extends JPanel {
    
    // remind: replace with annotation?
    public static String getShortDescription() {
        return "Demonstrates JList, Swing's component which displays a list of data";
    }
    
    public JListDemo() {        
        setToolTipText(getShortDescription());
        initComponents();
        setBorder(new EmptyBorder(12,10,12,10));
    }    
                
    protected void initComponents() {
        add(new JLabel("JList Demo Coming Soon...."));
    }
    
    public static void main(String args[]) {
        final JListDemo demo = new JListDemo();
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("JList Demo");
                frame.add(demo);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }    
}