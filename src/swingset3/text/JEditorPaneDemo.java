/*
 * JEditorPaneDemo.java
 *
 * Created on September 22, 2006, 1:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package swingset3.text;

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
public class JEditorPaneDemo extends JPanel {
    
    public JEditorPaneDemo() {        
        setToolTipText("Demonstrates JEditorPane, Swing's popup choice component.");
        initComponents();
        setBorder(new EmptyBorder(12,10,12,10));
    }    
                
    protected void initComponents() {
        add(new JLabel("JEditorPane Demo Coming Soon...."));
    }
    
    public static void main(String args[]) {
        final JEditorPaneDemo demo = new JEditorPaneDemo();
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("JEditorPane Demo");
                frame.add(demo);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }    
}
