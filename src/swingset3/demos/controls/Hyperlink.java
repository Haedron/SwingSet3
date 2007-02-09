/*
 * Hyperlink.java
 *
 * Created on August 29, 2006, 11:59 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package swingset3.demos.controls;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;


/**
 *
 * @author aim
 */
//<snip>Create HTML hyperlink
//<snip>Create HTML image hyperlink
public class Hyperlink extends JButton {
    private static BrowseAction defaultBrowseAction = new BrowseAction();

    private transient Rectangle viewRect = new Rectangle();
    private transient Rectangle iconRect = new Rectangle();
    private transient Rectangle textRect = new Rectangle();

    private Color normalForeground;
    private Color visitedForeground = new Color(85, 145, 90);
    private Color activeForeground = Color.red;
    private boolean visited = false;
    
    private URI targetURI;
    
    /** Creates a new instance of Hyperlink */
    public Hyperlink(String text) {
        super(text);
        setBorderPainted(false);
        setForeground(Color.blue);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(0,0,0,0));
        setAction(defaultBrowseAction);
        setText(text); // override the inheritence of the action's name
    }
    
    public Hyperlink(String text, String targetURI) throws URISyntaxException {
        this(text, new URI(targetURI));
    }
    
    public Hyperlink(String text, URI target) {
        this(text);
        setTarget(target);
    }
    
    public Hyperlink(String text, Action action) {
        this(text);
        setAction(action); // replaces default browse action
        setText(text); // override the inheritence of the action's name
    }
    
    public Hyperlink(String text, Icon icon) {
        this(text);
        setIcon(icon);
    }
    
    public Hyperlink(Icon icon, String targetURI) throws URISyntaxException {
        this(null, icon, targetURI);
    }
    
    public Hyperlink(String text, Icon icon, String targetURI) throws URISyntaxException {
        this(text, new URI(targetURI));
        setIcon(icon);
    }
    
    public Hyperlink(String text, Icon icon, URI target) {
        this(text);
        setIcon(icon);
        setTarget(target);        
    }
    
    public void setTarget(URI target) {
        this.targetURI = target;
        setToolTipText(target.toASCIIString());
    }
    
    public URI getTarget() {
        return targetURI;
    }
    
    public void setVisited(boolean visited) {
        this.visited = visited;
    }
    
    public boolean isVisited() {
        return visited;
    }
    
    public void setForeground(Color foreground) {
        normalForeground = foreground;
        super.setForeground(foreground);
    }
    
    protected void paintComponent(Graphics g) {
        // Set the foreground on the fly to ensure the text is painted
        // with the proper color in super.paintComponent
        ButtonModel model = getModel();
        if (model.isArmed()) {
            super.setForeground(activeForeground);
        } else if (visited) {
            super.setForeground(visitedForeground);
        } else {
            super.setForeground(normalForeground);
        }
        super.paintComponent(g);
        
        int baseline = getBaseline(getWidth(), getHeight());
        
        viewRect.x = viewRect.y = 0;
        viewRect.width = getWidth();
        viewRect.height = getHeight();
        
        SwingUtilities.layoutCompoundLabel(g.getFontMetrics(), getText(),
                getIcon(), getVerticalAlignment(), getHorizontalAlignment(),
                getVerticalTextPosition(), getHorizontalTextPosition(),
                viewRect, iconRect, textRect, getIconTextGap());
        
        g.setColor(getForeground());
        g.drawLine(textRect.x, 
                baseline + 2, 
                textRect.x + textRect.width,
                baseline + 2);        
        
    }

    // This action is stateless and hence can be shared across hyperlinks
    private static class BrowseAction extends AbstractAction {
        public BrowseAction() {
            super();
        }
        public void actionPerformed(ActionEvent e) {
            Hyperlink hyperlink = (Hyperlink)e.getSource();
            
            URI targetURI = hyperlink.getTarget();
            if (targetURI != null) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(targetURI);
                    hyperlink.setVisited(true);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    System.err.println(ioe);
                }
                
            }
        }
        
    }
    
}
//</snip>
//</snip>
