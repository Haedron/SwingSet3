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

package swingset3.hyperlink;

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
public class JHyperlink extends JButton {
    private static BrowseAction defaultBrowseAction = new BrowseAction();
    
    private URI targetURI;
    private boolean visited;

    private transient Rectangle viewRect = new Rectangle();
    private transient Rectangle iconRect = new Rectangle();
    private transient Rectangle textRect = new Rectangle();

    //remind(aim): lookup colors instead of hardcoding them
    private Color normalForeground;
    private Color visitedForeground = new Color(85, 145, 90);
    private Color activeForeground = Color.red;
    private boolean drawUnderline = true;
    
    
    /**
     * Creates a new instance of JHyperlink
     */
    public JHyperlink() {
        super();
        setBorderPainted(false);
        setContentAreaFilled(false);
        setForeground(Color.blue);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(0,0,0,0));
        setAction(defaultBrowseAction);
    }
    
    /**
     * Creates a new instance of JHyperlink
     */
    public JHyperlink(String text) {
        this();
        setText(text); // override the inheritence of the action's name
    }
    
    public JHyperlink(String text, String targetURI) throws URISyntaxException {
        this(text, new URI(targetURI));
    }
    
    public JHyperlink(String text, URI target) {
        this(text);
        setTarget(target);
    }
    
    public JHyperlink(String text, Action action) {
        this(text);
        setAction(action); // replaces default browse action
        setText(text); // override the inheritence of the action's name
    }
    
    public JHyperlink(String text, Icon icon) {
        this(text);
        setIcon(icon);
    }
    
    public JHyperlink(Icon icon, String targetURI) throws URISyntaxException {
        this(null, icon, targetURI);
    }
    
    public JHyperlink(String text, Icon icon, String targetURI) throws URISyntaxException {
        this(text, new URI(targetURI));
        setIcon(icon);
    }
    
    public JHyperlink(String text, Icon icon, URI target) {
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
    
    public void setDrawUnderline(boolean drawUnderline) {
        this.drawUnderline = drawUnderline;
    }
    
    public boolean getDrawUnderline() {
        return drawUnderline;
    }
    
    @Override
    public void setForeground(Color foreground) {
        normalForeground = foreground;
        super.setForeground(foreground);
    }
    
    @Override
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
        
        if (drawUnderline) {
            viewRect.x = viewRect.y = 0;
            viewRect.width = getWidth();
            viewRect.height = getHeight();
            int baseline = getBaseline(viewRect.width, viewRect.height);
            
            iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;
            textRect.x = textRect.y = textRect.width = textRect.height = 0;
            SwingUtilities.layoutCompoundLabel(g.getFontMetrics(), getText(),
                    getIcon(), getVerticalAlignment(), getHorizontalAlignment(),
                    getVerticalTextPosition(), getHorizontalTextPosition(),
                    viewRect, iconRect, textRect, getIconTextGap());

            g.setColor(new Color(200,200,200,200));
            g.drawRect(textRect.x, textRect.y + textRect.height, 
                    textRect.width, textRect.height);
            
            g.setColor(getForeground());
            g.drawLine(textRect.x,
                    baseline + 2,
                    textRect.x + textRect.width,
                    baseline + 2);
        }
        
    }

    // This action is stateless and hence can be shared across hyperlinks
    public static class BrowseAction extends AbstractAction {
        public BrowseAction() {
            super();
        }
        public void actionPerformed(ActionEvent e) {
            JHyperlink hyperlink = (JHyperlink)e.getSource();
            
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
//</snip>
//</snip>
    
}

