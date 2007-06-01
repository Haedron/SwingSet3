/*
 * JDialogDemo.java
 *
 * Created on February 8, 2006, 4:09 PM
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
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import swingset3.DemoProperties;
import swingset3.Utilities;

/**
 *
 * @author aim
 */
@DemoProperties(
      value = "JDialog Demo", 
      category = "Toplevel Containers",
      description = "Demonstrates JDialog, Swing's top-level dialog container."
)
public class JDialogDemo extends JPanel { 
    
    private JDialog dialog;
    
    private JButton showButton;
        
    public JDialogDemo() {        
        initComponents();
    }
    
    protected void initComponents() {
        dialog = createDialog();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        // Create button to control visibility of frame
        showButton = new JButton("Show JDialog...");
        showButton.addActionListener(new ShowActionListener());
        add(showButton);        
    }
    
    protected JDialog createDialog() {
 
        //<snip>Create dialog
        JDialog dialog = new JDialog(new JFrame(), "Demo JDialog", false);
        //</snip>
        
        //<snip>Add dialog's content
        JLabel label = new JLabel("I'm content.");
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setPreferredSize(new Dimension(300,200));
        dialog.add(label);
        //</snip>
        
        //<snip>Initialize dialog's size
        // which will shrink-to-fit its contents
        dialog.pack(); 
        //</snip>
        
        return dialog;
    }
    
    public void start() {
        Utilities.setToplevelLocation(dialog, showButton, Utilities.SOUTH_EAST);
        //<snip>Show dialog
        dialog.setVisible(true);
        //</snip>
    };
    
    public void pause() {
        //<snip>Hide dialog
        dialog.setVisible(false);
        //</snip>
    };
    
    private class ShowActionListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            //<snip>Make dialog visible
            // if dialog already visible, then bring to the front
            if (dialog.isShowing()) {
                dialog.toFront();
            } else {
                dialog.setVisible(true);
            }
            //</snip>
        }
    }
    

}
