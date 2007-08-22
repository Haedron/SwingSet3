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

package swingset3;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

/**
 *
 * @author aim
 */
public class DemoPanel extends JPanel {
    
    private HTMLPanel descriptionPane;    
    private Demo demo;
    private boolean demoAdded = false;
    
    public DemoPanel(Demo demo) {  
        this.demo = demo;
                
        setLayout(new BorderLayout()); // ensure components fills panel
        setBorder(new CompoundBorder(new EmptyBorder(8,2,8,2),
                                     new TitledBorder(new EmptyBorder(0,0,0,0), demo.getName())));
                
        // Instantiate demo component on separate thread...
        new DemoLoader().execute();

    }
    
    public Demo getDemo() {
        return demo;
    }
    
    
    private void printComponent(Component c, String indent) {
        System.out.println(indent+ c.getClass().getSimpleName()+ "  visible="+c.isVisible());
        if (c instanceof Container) {
            Container p = (Container)c;
            Component children[] = p.getComponents();
            for(Component child: children) {
                printComponent(child, indent+"  ");
            }            
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

//printComponent(this,"");
        if (!demoAdded) {
            Graphics2D g2 = (Graphics2D)g;
            Insets insets = getInsets();
            Rectangle bounds = getBounds(); 
            int thickness = 24;
            int diameter = (int)(Math.min(bounds.width - insets.left - insets.right - thickness, 
                    bounds.height - insets.top - insets.bottom - thickness) * .75);
            g2.setColor(UIManager.getColor(SwingSet3.controlMidShadowKey));
            g2.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            int x = insets.left + (bounds.width - insets.left - insets.right - diameter)/2;
            int y = insets.top + (bounds.height - insets.top - insets.bottom - diameter)/2;
            g2.drawOval(x, y, diameter, diameter);
            g2.drawLine(x + (diameter/2), y + (diameter/2),
                    x + (diameter/2) + 70, y + (int)((diameter/2) * .4));
            g2.drawLine(x + (diameter/2), y + (diameter/2),
                    x + diameter - 70, y + (diameter/2));
        } else {
            
        }
        
    }
    
    protected class DemoLoader extends SwingWorker<Component, Object> {
        
        public DemoLoader() {
        }
        public Component doInBackground() {
            //try {Thread.currentThread().sleep(10000);} catch (Exception e) {}
            // If demo has HTML description, load with embedded demo component
            URL descriptionURL = DemoPanel.this.demo.getHTMLDescription();
            if (descriptionURL != null) {
                descriptionPane = new HTMLPanel();
                descriptionPane.setMargin(new Insets(0,8,0,8));
                descriptionPane.setBorder(null);
                descriptionPane.setOpaque(false);
                descriptionPane.addComponentCreationListener(new ComponentCreationListener());
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
    
    protected class ComponentCreationListener implements HTMLPanel.ComponentCreationListener {
        public void componentCreated(HTMLPanel panel, Component component) {
            Component demoComponent = (Component)component;
            demo.setDemoComponent(demoComponent);
        }
    }
    
}
