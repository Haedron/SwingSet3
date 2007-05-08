
/*
 * JWindowDemo.java
 *
 * Created on March 22, 2006, 2:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package swingset3.demos.toplevels;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import swingset3.Utilities;

/**
 *
 * @author aim
 */
public class JWindowDemo extends JPanel { 

    // remind: replace with annotation?
    public static String getShortDescription() {
        return "Demonstrates JWindow, a toplevel container with no system border.";
    }
    
    private JWindow window;
    
    private JButton showButton;
        
    public JWindowDemo() {   
        initComponents();
        setToolTipText(getShortDescription());
    }
    
    protected void initComponents() {
        window = createWindow();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        // Create button to control visibility of frame
        showButton = new JButton("Show JWindow...");
        showButton.addActionListener(new ShowActionListener());
        add(showButton);        
    }
    
    protected JWindow createWindow() {
 
        //<snip>Create window
        JWindow window = new JWindow();
        //</snip>
        
        //<snip>Add window's content
        JLabel label = new JLabel("I have no system border.");
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setPreferredSize(new Dimension(300,300));
        window.add(label);
        //</snip>
        
        //<snip>Initialize window's size
        // which will shrink-to-fit its contents
        window.pack(); 
        //</snip>
        
        return window;
    }
    
    public void start() {
        Utilities.setToplevelLocation(window, showButton, Utilities.SOUTH_EAST);
        //<snip>Show window
        window.setVisible(true);
        //</snip>
    };
    
    public void pause() {
        //<snip>Hide window
        window.setVisible(false);
        //</snip>
    };
    
    private class ShowActionListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            //<snip>Make window visible
            // if window already visible, then bring to the front
            if (window.isShowing()) {
                window.toFront();
            } else {
                window.setVisible(true);
            }
            //</snip>
        }
    }
    
    
}
