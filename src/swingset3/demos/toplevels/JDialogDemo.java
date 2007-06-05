/*
 * Copyright %YEARS% Sun Microsystems, Inc.  All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
