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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.net.URL;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import swingset3.DemoProperties;
import swingset3.Utilities;
import swingset3.demos.toplevels.BusyGlass;

/**
 *
 * @author aim
 */
@DemoProperties(
      value = "JFrame Demo", 
      category = "Toplevel Containers",
      description = "Demonstrates JFrame, Swing's top-level primary window container.",
      sourceFiles = {
        "swingset3/demos/toplevels/JFrameDemo.java",
        "swingset3/demos/toplevels/BusyGlass.java",
        "swingset3/Utilities.java"       
      }
)
public class JFrameDemo extends JPanel {
    //<snip>Ensure system menubar is used on Mac OSX
    static {
        // Property must be set *early* due to Apple Bug#3909714
        // ignored on other platforms
        if (System.getProperty("os.name").equals("Mac OS X")) {
            System.setProperty("apple.laf.useScreenMenuBar", "true"); 
        }
    }
    //</snip>
    
    // Panel components
    private JButton showButton;
    private JCheckBox busyCheckBox;
    
    // Toplevel frame component    
    private JFrame frame;
    
    private boolean locationSet = false;
            
    public JFrameDemo() {        
        initComponents();
    }

    protected void initComponents() {
        frame = createFrame();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        // Create button to control visibility of frame
        showButton = new JButton("Show JFrame...");
        showButton.addActionListener(new ShowActionListener());
        add(showButton);
        
        // Create checkbox to control busy state of frame
        busyCheckBox = new JCheckBox("Frame busy");
        busyCheckBox.setSelected(false);
        busyCheckBox.addChangeListener(new BusyChangeListener());
        add(busyCheckBox);
        
        addComponentListener(new SizeInitListener());
        
    }
    
    protected JFrame createFrame() {
 
        //<snip>Create frame and set simple properties
        JFrame frame = new JFrame("Demo JFrame");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);      
        //</snip>
        
        //<snip>Set Minimized/titlebar icon Image
        //Note: How the image is used is platform-dependent
        ImageIcon frameIcon = new ImageIcon("resources/images/swingingduke.gif");
        frame.setIconImage(frameIcon.getImage());
        //</snip>
        
        //<snip>Make toplevel "busy"
        // busy glasspane is initially invisible
        frame.setGlassPane(new BusyGlass());
        //</snip>

        //<snip>Add a menubar
        JMenuBar menubar = new JMenuBar();
        frame.setJMenuBar(menubar);
        JMenu menu = new JMenu("File");
        menubar.add(menu);
        menu.add("Open");
        menu.add("Save");
        //</snip>
        
        //<snip>Add a horizontal toolbar
        JToolBar toolbar = new JToolBar();
        frame.getContentPane().add(BorderLayout.NORTH, toolbar);
        toolbar.add(new JButton("Toolbar Button"));
        //</snip>
        
        //<snip>Add the content area
        JLabel label = new JLabel("I'm content but a little blue.");
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setPreferredSize(new Dimension(400,200));
        label.setBackground(new Color(197, 216, 236));
        label.setOpaque(true); // labels non-opaque by default
        frame.add(label);
        //snip
        
        //<snip>Add a statusbar
        JLabel statusLabel = new JLabel("I show status.");
        statusLabel.setHorizontalAlignment(JLabel.LEADING);
        frame.getContentPane().add(BorderLayout.SOUTH, statusLabel);
        //</snip>
        
        //<snip>Initialize frame's size
        // which will shrink-to-fit its contents
        frame.pack(); 
        //</snip>
        
        return frame;
    }
    
    private class ShowActionListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            showFrame();
        }
    }
    
    private class BusyChangeListener implements ChangeListener {
        public void stateChanged(ChangeEvent changeEvent) {
            setFrameBusy(busyCheckBox.isSelected());
            showFrame(); // bring frame back to front for demo purposes
        }
    }    

    public void start() {
        // If location hasn't been initialed yet from SizeInitListener, then
        // defer visibility of frame        
        if (locationSet) {
            frame.setVisible(true);
        }
    };
    
    public void stop() {
        //<snip>Hide frame
        frame.setVisible(false);
        //</snip>
    };
    
    public void showFrame() {
        //<snip>Show frame
        // if frame already visible, then bring to the front
        if (frame.isShowing()) {
            frame.toFront();
        } else {
            frame.setVisible(true);
        }
        //</snip>
    }
    
    //<snip>Make toplevel "busy"  
    public void setFrameBusy(boolean busy) {
        frame.getGlassPane().setVisible(busy);
        // Must explicitly disable the menubar because on OSX it will be
        // in the system menubar and not covered by the glasspane
        frame.getJMenuBar().setEnabled(!busy);
    }
    
    public boolean isFrameBusy() {
        return frame.getGlassPane().isVisible();
    }
    //</snip>
    
    // This is a hack to deal with the asynchronous instantiation of this
    // demo component when embedded in HTML;   at the time start() is called,
    // we don't necessarily have the size/location of the demo, hence cannot
    // determine a reasonable relative location for the frame.  So we wait
    // until the demo's size is initialized to set the location of the frame.
    private class SizeInitListener extends ComponentAdapter {        
        public void componentResized(ComponentEvent event) {
            Component component = event.getComponent();
            if (component.getWidth() > 0 && component.getHeight() > 0) {
                Utilities.setToplevelLocation(frame, component, Utilities.SOUTH);
                locationSet = true;
                frame.setVisible(true);
                component.removeComponentListener(this);                
            }
        }        
    } // SizeInitListener
    
    
}
