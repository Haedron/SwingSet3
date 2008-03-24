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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import swingset3.DemoProperties;
import swingset3.utilities.Utilities;

/**
 *
 * @author aim
 */
@DemoProperties(
      value = "JWindow Demo", 
      category = "Toplevel Containers",
      description = "Demonstrates JWindow, a toplevel container with no system border."
)
public class JWindowDemo extends JPanel { 
    
    private JWindow window;
    
    private JComponent windowSpaceholder;
            
    public JWindowDemo() {   
        initComponents();
    }
    
    protected void initComponents() {
        window = createWindow();
        
        setLayout(new BorderLayout());
        add(createControlPanel(), BorderLayout.WEST);
        windowSpaceholder = createWindowSpaceholder(window);
        add(windowSpaceholder, BorderLayout.CENTER);
    }
                
    protected JComponent createControlPanel() {
        Box controlPanel = Box.createVerticalBox();
        controlPanel.setBorder(new EmptyBorder(8,8,8,8));
        
        // Create button to control visibility of frame
        JButton showButton = new JButton("Show JWindow...");
        showButton.addActionListener(new ShowActionListener());
        controlPanel.add(showButton);  
        
        return controlPanel;
    }
    
    protected JComponent createWindowSpaceholder(JWindow window) {               
        JPanel windowPlaceholder = new JPanel();
        Dimension prefSize = window.getPreferredSize();
        prefSize.width += 12;
        prefSize.height += 12;
        windowPlaceholder.setPreferredSize(prefSize);

        return windowPlaceholder;        
    }
    
    protected JWindow createWindow() {
 
        //<snip>Create window
        JWindow window = new JWindow();
        //</snip>
        
        //<snip>Add a border to the window
        window.getRootPane().setBorder(new LineBorder(Color.BLACK, 1));
        //</snip>
        
        //<snip>Add window's content
        JLabel label = new JLabel("I have no system border.");
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setPreferredSize(new Dimension(250,200));
        window.add(label);
        //</snip>
        
        //<snip>Initialize window's size
        // which will shrink-to-fit its contents
        window.pack(); 
        //</snip>
        
        return window;
    }
    
    public void start() {
        Utilities.setToplevelLocation(window, windowSpaceholder, SwingConstants.CENTER);
        showWindow();
    }
    
    public void stop() {
        //<snip>Hide window
        window.setVisible(false);
        //</snip>
    };
    
    public void showWindow() {
        //<snip>Show window
        // if window already visible, then bring to the front
        if (window.isShowing()) {
            window.toFront();
        } else {
            window.setVisible(true);
        }
        //</snip>
    }
    
    private class ShowActionListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            showWindow();
        }
    }
}
