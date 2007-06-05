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

package swingset3;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.ComponentView;
import javax.swing.text.Document;
import javax.swing.text.View;

/**
 *
 * @author aim
 */
public class DemoPanel extends JPanel {
    private static Image progressImage;
    
    private static HyperlinkHandler hyperlinkHandler;
    private static Cursor defaultCursor;
    
    static {
        hyperlinkHandler = new HyperlinkHandler();
        try {            
            progressImage = ImageIO.read(DemoPanel.class.getResourceAsStream(
                    "resources/images/clock.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private JEditorPane descriptionPane;    
    private Demo demo;
    private boolean demoAdded = false;
    
    public DemoPanel(Demo demo) {  
        this.demo = demo;
                
        setLayout(new BorderLayout()); // ensure components fills panel
        setBorder(new CompoundBorder(new EmptyBorder(8,2,8,2),
                                     new TitledBorder(new EmptyBorder(0,0,0,0), demo.getName())));
        
        class DemoLoader extends SwingWorker<JComponent, Object> {
            
            public DemoLoader() {
            }
            public JComponent doInBackground() {
                //try {Thread.currentThread().sleep(10000);} catch (Exception e) {}
                // If demo has HTML description, load with embedded demo component
                URL descriptionURL = DemoPanel.this.demo.getHTMLDescription();
                if (descriptionURL != null) {
                    descriptionPane = new JEditorPane();
                    descriptionPane.setEditable(false);
                    descriptionPane.setContentType("text/html");
                    descriptionPane.setMargin(new Insets(0,8,0,8));
                    descriptionPane.setBorder(null);
                    descriptionPane.setOpaque(false);
                    descriptionPane.addPropertyChangeListener(new DemoLoadListener()); 
                    descriptionPane.addHyperlinkListener(hyperlinkHandler);
                    JScrollPane scrollPane = new JScrollPane(descriptionPane);
                    scrollPane.setBorder(null);
                                       
                    // Since page may load asynchronously, we need to wait for the event
                    // notifying it's done so we can grab the demo component handle
                    try {
                        descriptionPane.setPage(descriptionURL);
                    } catch (IOException e) {
                        System.err.println("couldn't load description from URL:" + descriptionURL);
                        e.printStackTrace();
                    }
                    return scrollPane;
                } else {
                    // no HTML description, just add demo component
                    return DemoPanel.this.demo.createDemoComponent();
                }
            }
            protected void done() {
                try {
                    
                    add(get());
                    demoAdded = true;
                    revalidate();
                                        
                } catch (Exception ignore) {
                    System.err.println(ignore);
                }
            }
        } // DemoLoader
        
        // Instantiate demo component on separate thread...
        new DemoLoader().execute();

    }
    
    public Demo getDemo() {
        return demo;
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!demoAdded) {
            Graphics2D g2 = (Graphics2D)g;
            Insets insets = getInsets();
            Rectangle bounds = getBounds(); 
            int thickness = 24;
            int diameter = Math.min(bounds.width - insets.left - insets.right - thickness, 
                    bounds.height - insets.top - insets.bottom - thickness);
            g2.setColor(new Color(200,200,200));
            g2.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            int x = insets.left + (bounds.width - insets.left - insets.right - diameter)/2;
            int y = insets.top + (bounds.height - insets.top - insets.bottom - diameter)/2;
            g2.drawOval(x, y, diameter, diameter);
            g2.drawLine(x + (diameter/2), y + (diameter/2),
                    x + (diameter/2) + 70, insets.top + 70);
            g2.drawLine(x + (diameter/2), y + (diameter/2),
                    x + diameter - 70, y + (diameter/2));
        } else {
            
        }
        
    }
    
    // recursively searches views in editor pane to find the embedded 
    // demo component matching the targetClass
    private JComponent findComponent(View view, Class targetClass) {
        JComponent component = null;
        int i =  0;
        while(i < view.getViewCount() && component == null) {
            View childView = view.getView(i++);
            component = findComponent(childView, targetClass);            
        }
        
        if (component == null && view instanceof ComponentView) {
            Component c = ((ComponentView)view).getComponent();
            if (targetClass.isInstance(c)) {
                component = (JComponent)c;
            }
        }
        return component;
    }
    
    // Registered on descriptionPane to track when a demo is loaded
    // and obtain the handle to the associated demo component
    private class DemoLoadListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent pce) {
            String propertyName = pce.getPropertyName();
            // if the current demo is switched before we get the "page" property change event,
            // then sometimes the demo component won't be found and will end up null;  so when
            // another event comes around ("Frame.active") we check for a null component and
            // try finding the component again.  not ideal, I know.
            if (propertyName.equals("page") ||
                    (demo.getDemoComponent() == null && propertyName.equals("Frame.active"))) {
                JComponent demoComponent =
                   findComponent(descriptionPane.getUI().getRootView(descriptionPane),
                        demo.getDemoClass());
                if (demoComponent != null) {
                    demo.setDemoComponent(demoComponent);
                    demoComponent.getTopLevelAncestor().validate();
                } else {
                    System.err.println(propertyName+": couldn't find demo component for " +
                            demo.getName());
                }
            }
        }
    }

    // single instance of handler is shared for ALL DemoPanel instances
    private static class HyperlinkHandler implements HyperlinkListener {
        public void hyperlinkUpdate(HyperlinkEvent event) {
            JEditorPane descriptionPane = (JEditorPane)event.getSource();
            HyperlinkEvent.EventType type = event.getEventType();
            if (type == HyperlinkEvent.EventType.ACTIVATED) {                    
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(event.getURL().toURI());
                       
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println(e);
                }
                
            } else if (type == HyperlinkEvent.EventType.ENTERED) {
                defaultCursor = descriptionPane.getCursor();
                descriptionPane.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                
            } else if (type == HyperlinkEvent.EventType.EXITED) {
                descriptionPane.setCursor(defaultCursor);                  
            }
        }
    }

    
}
