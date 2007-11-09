/*
 * Copyright 2007 Sun Microsystems, Inc.  All Rights Reserved.
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
    
    private boolean locationSet = false;
        
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
        
        addComponentListener(new SizeInitListener());
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
        // If location hasn't been initialed yet from SizeInitListener, then
        // defer visibility of dialog        
        if (locationSet) {
            dialog.setVisible(true);
        }
    };
    
    public void stop() {
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

    // This is a hack to deal with the asynchronous instantiation of this
    // demo component when embedded in HTML;   at the time start() is called,
    // we don't necessarily have the size/location of the demo, hence cannot
    // determine a reasonable relative location for the dialog.  So we wait
    // until the demo's size is initialized to set the location of the dialog.
    private class SizeInitListener extends ComponentAdapter {        
        public void componentResized(ComponentEvent event) {
            Component component = event.getComponent();
            if (component.getWidth() > 0 && component.getHeight() > 0) {
                Utilities.setToplevelLocation(dialog, component, Utilities.SOUTH);
                locationSet = true;
                dialog.setVisible(true);
                component.removeComponentListener(this);                
            }
        }        
    } // SizeInitListener

}
