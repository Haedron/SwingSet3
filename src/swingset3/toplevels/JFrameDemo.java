/*
 * JFrameDemo.java
 *
 * Created on February 1, 2006, 1:38 PM
 *
 * Demo which shows how to use basic features of the JFrame component.
 */

package swingset3.toplevels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import swingset3.Utilities;
import swingset3.toplevels.BusyGlass;

/**
 *
 * @author aim
 */
public class JFrameDemo extends JPanel {
    //<snip>Ensure system menubar is used on Mac OSX
    static {
        // Property must be set *early* due to Apple Bug#3909714
        // ignored on other platforms
        System.setProperty("apple.laf.useScreenMenuBar", "true");              
    }
    //</snip>
    
    public static URL[] getSourceFiles() {
        URL sources[] = new URL[3];
        String className = JFrameDemo.class.getName();
        sources[0] = JFrameDemo.class.getResource("../../sources/" + 
             className.replaceAll("\\.", File.separator) + ".java");
        sources[1] = JFrameDemo.class.getResource("../../sources/swingset3/Utilities.java");
        sources[2] = JFrameDemo.class.getResource("../../sources/swingset3/toplevels/BusyGlass.java");

        return sources;
    }
    
    // Panel components
    private JButton showButton;
    private JCheckBox busyCheckBox;
    
    // Toplevel frame component    
    private JFrame frame;
            
    public JFrameDemo() {        
        initComponents();
        setToolTipText("Demonstrates JFrame, Swing's top-level primary window container.");
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
        JLabel label = new JLabel("I'm content and a little blue.");
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setPreferredSize(new Dimension(400,300));
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
        Utilities.setToplevelLocation(frame, showButton, Utilities.SOUTH_EAST);
        
        //<snip>Show frame
        frame.setVisible(true);
        //</snip>
    };
    
    public void stop() {
        //<snip>Hide frame
        frame.setVisible(false);
        //</snip>
    };
    
    public void showFrame() {
        //<snip>Make frame visible
        // if frame already visible, then bring to the front
        if (frame.isShowing()) {
            frame.toFront();
        } else {
            Utilities.setToplevelLocation(frame, showButton, Utilities.SOUTH_EAST);
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
    
    
}
