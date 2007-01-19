/*
 * BusyGlass.java
 *
 * Created on June 14, 2006, 10:54 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package swingset3.toplevels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;         
import java.awt.Graphics;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

/**
 * GlassPane component which can be set on toplevel
 * containers to makes those containers "busy" be disabling input.
 *
 * Example usage:
 * <pre><code>
 *    // Install glasspane
 *    frame.setGlassPane(new BusyGlass()); 
 *
 *    // Make frame busy
 *    frame.getGlassPane().setVisible(true);
 * </code></pre>
 * 
 * Caution: A well-written client should rarely need to make
 * a window "busy" because the app should be as responsive as possible;
 * long-winded operations should be off-loaded to non-GUI threads
 * whenever possible.  
 *
 * @author aim
 */
//<snip>Make toplevel "busy"
public class BusyGlass extends JPanel {
    
    /**
     * Create GlassPane component to block input on toplevel
     */    
    public BusyGlass() {
        setLayout(new BorderLayout());
        setVisible(false); //initially invisible
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        MouseInputAdapter inputBlocker = new MouseInputAdapter() {};
        addMouseListener(inputBlocker);
        addMouseMotionListener(inputBlocker);
    }
    
    protected void paintComponent(Graphics g) {
        // Render partially opaque to 'veil' the frame's contents so
        // that the user has visual feedback that the components
        // arn't responsive.
        Color bgColor = getBackground();
        g.setColor(new Color(bgColor.getRed(),
                bgColor.getGreen(),
                bgColor.getBlue(), 150));
        g.fillRect(0, 0, getWidth(), getHeight());
        
    }
 }
//</snip>