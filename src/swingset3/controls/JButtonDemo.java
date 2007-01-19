/*
 * JButtonDemo.java
 *
 * Created on August 22, 2006, 2:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package swingset3.controls;


import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.View;


/**
 *
 * @author aim
 */
public class JButtonDemo extends JPanel {
    
    public static URL[] getSourceFiles() {
        URL sources[] = new URL[2];
        String className = JButtonDemo.class.getName();
        sources[0] = JButtonDemo.class.getResource("../../sources/" + 
            className.replaceAll("\\.", File.separator) + ".java");
        sources[1] = JButtonDemo.class.getResource("../../sources/swingset3/controls/Hyperlink.java");
        return sources;
    }
    
    public JButtonDemo() {        
        setToolTipText("Demonstrates JButton, Swing's push button component.");
        initComponents();
    }
    
                
    protected void initComponents() {
        setLayout(new GridLayout(0,1));
        
        add(createSimpleButtons());
        add(createCreativeButtons());
    }
    
    protected JPanel createSimpleButtons() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Simple Buttons"));
        
        //<snip>Create simple button
        final JButton simpleButton = new JButton("Do it");
        simpleButton.setToolTipText("simple button");
        //</snip>
        //<snip>Add action listener using anonymous inner class
        // This style is useful when the action code is tied to a
        // single button instance and it's useful for simplicity
        // sake to keep the action code located near the button.
        // More global application actions should be implemented
        // using Action classes instead.
        simpleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                simpleButton.setText("Do it again");
                // Need to force toplevel to relayout to accommodate new button size
                SwingUtilities.getWindowAncestor(simpleButton).validate(); 
            }
        });
        //</snip>
        simpleButton.putClientProperty("snippetKey", "Create simple button");
        panel.add(simpleButton);

        
        //<snip>Create image button
        // Image is from the Java Look and Feel Graphics Repository:
        // http://java.sun.com/developer/techDocs/hi/repository 
        JButton button = new JButton(new ImageIcon(getClass().
                getResource("/toolbarButtonGraphics/general/Print24.gif")));
        button.setToolTipText("image button");
        //</snip>
        button.putClientProperty("snippetKey", "Create image button");
        panel.add(button); 
        
        //<snip>Create button with text and image
        // Image is from the Java Look and Feel Graphics Repository:
        // http://java.sun.com/developer/techDocs/hi/repository 
        button = new JButton("Find", 
                     new ImageIcon(getClass().
                      getResource("/toolbarButtonGraphics/general/Search24.gif")));
        button.setToolTipText("button with text and image");
        button.setHorizontalTextPosition(JButton.LEADING);
        button.setIconTextGap(6);
        //</snip>
        button.putClientProperty("snippetKey", "Create button with text and image");
        panel.add(button);
        
        //<snip>Create button with background color
        button = new JButton("Go");
        button.setBackground(Color.green);
        button.setContentAreaFilled(true);
        button.setOpaque(false);
        button.setToolTipText("button with background color");
        //</snip>
        button.putClientProperty("snippetKey", "Create button with background color");
        panel.add(button);
        
        return panel;
    }
    
    protected JPanel createCreativeButtons() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 16, 8));
        panel.setBorder(BorderFactory.createTitledBorder("More Interesting Buttons"));
        
        //<snip>Create button with no border
        JButton button = new JButton();
        button.setText("Connect");        
        button.setIcon(new ImageIcon(getClass().getResource("resources/images/earth_day.gif")));
        button.setPressedIcon(new ImageIcon(getClass().getResource("resources/images/earth_night.gif")));
        button.setBorderPainted(false);
        button.setVerticalTextPosition(JButton.BOTTOM);
        button.setHorizontalTextPosition(JButton.CENTER);
        button.setIconTextGap(0);
        button.setToolTipText("button with no border");
        //</snip>
        button.putClientProperty("snippetKey", "Create button with no border");
        panel.add(button);
        
        //<snip>Create image button with rollover image
        button = new JButton();
        button.setBorderPainted(false);
        button.setIcon(new ImageIcon(getClass().getResource("resources/images/redbutton.png")));
        button.setRolloverEnabled(true);
        button.setRolloverIcon(new ImageIcon(getClass().getResource("resources/images/redbutton_glow.png")));
        button.setPressedIcon(new ImageIcon(getClass().getResource("resources/images/redbutton_dark.png")));
        button.setToolTipText("button with rollover image");
        //</snip>
        button.putClientProperty("snippetKey", "Create image button with rollover image");
        panel.add(button);
        
        //<snip>Create HTML hyperlink        
        Hyperlink hyperlink;
        try {
            hyperlink = new Hyperlink("Get More Info", "http://java.sun.com/j2se");
        } catch (URISyntaxException use) {
            use.printStackTrace();
            hyperlink = new Hyperlink("Get More Info");
        }
        //</snip>
        hyperlink.putClientProperty("snippetKey", "Create HTML hyperlink");
        panel.add(hyperlink);        
        
        //<snip>Create HTML image hyperlink
        try {
            hyperlink = new Hyperlink(
                    new ImageIcon(getClass().getResource("resources/images/blogs.png")),
                    "http://weblogs.java.net");
        } catch (URISyntaxException use) {
            use.printStackTrace();
        }
        //</snip>
        button.putClientProperty("snippetKey", "Create HTML image hyperlink");
        panel.add(hyperlink);       
        
        return panel;                
    }
    
    public static void main(String args[]) {
        final JButtonDemo buttonDemo = new JButtonDemo();
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("JButton Demo");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(buttonDemo);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }    
}
