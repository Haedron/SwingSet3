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

import swingset3.utilities.HTMLPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.interpolation.PropertySetter;
import org.jdesktop.animation.timing.triggers.TimingTrigger;
import org.jdesktop.animation.timing.triggers.TimingTriggerEvent;
import org.jdesktop.swingx.JXPanel;

/**
 *
 * @author aim
 */
public class DemoPanel extends JPanel {
    private JXPanel jxPanel;
    private HTMLPanel descriptionPane; // used when resources/[DemoClassName].html is supplied
    private Demo demo;
    private LoadAnimationPanel loadAnimationPanel;
    
    public DemoPanel(Demo demo) {
        this.demo = demo;
        setLayout(new BorderLayout()); 
        
        jxPanel = new JXPanel();
        jxPanel.setLayout(new BorderLayout());
        add(jxPanel);
        
        loadAnimationPanel = new LoadAnimationPanel();
        jxPanel.add(loadAnimationPanel);
        loadAnimationPanel.setAnimating(true);
        
        // Instantiate demo component on separate thread...
        new DemoLoader().execute();
        
    }
    
    public Demo getDemo() {
        return demo;
    }
    
    protected class DemoLoader extends SwingWorker<Component, Object> {
        private Animator fadeAnimator;
        public DemoLoader() {            
        }
        public Component doInBackground() {
            //try {Thread.currentThread().sleep(2000);} catch (Exception e) {}
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
                loadAnimationPanel.setAnimating(false);
                Animator fadeOutAnimator = new Animator(400, 
                        new FadeOut(jxPanel, loadAnimationPanel, (JComponent)get()));
                fadeOutAnimator.setAcceleration(.2f);
                fadeOutAnimator.setDeceleration(.3f);
                Animator fadeInAnimator = new Animator(400,
                        new PropertySetter(jxPanel, "alpha", 0.3f, 1.0f));
                TimingTrigger trigger =
                        TimingTrigger.addTrigger(fadeOutAnimator, fadeInAnimator, TimingTriggerEvent.STOP);
                fadeOutAnimator.start();
                //remove(loadAnimationPanel);
                //add(get());
                
                
            } catch (Exception ignore) {
                System.err.println(ignore);
                ignore.printStackTrace();
            }
        }
        
        
    } // DemoLoader
    
    private class FadeOut extends PropertySetter {
        JXPanel parent;
        JComponent out;
        JComponent in;
        public FadeOut(JXPanel parent, JComponent out, JComponent in) {
            super(out, "alpha", 1.0f, 0.3f);
            this.parent = parent;
            this.out = out;
            this.in = in;
        }
        public void end() {
            parent.setAlpha(0.3f);
            parent.remove(out);
            parent.add(in);
            parent.revalidate();
        }
    } // Fader
    
    private class ComponentCreationListener implements HTMLPanel.ComponentCreationListener {
        public void componentCreated(HTMLPanel panel, Component component) {
            Component demoComponent = (Component)component;
            System.out.println("demoComponent="+demoComponent.getClass().getName());
            if (demoComponent.getClass().equals(demo.getDemoClass())) {
                demo.setDemoComponent(demoComponent);
            }
        }
    } // ComponentCreationListener
    
    public static class LoadAnimationPanel extends JXPanel {
        private final String message = "loading demo";
        private BufferedImage gearAImage;
        private BufferedImage gearBImage;
        
        private boolean animating = false;
        private int rotation = 0;
        private Animator animator;
        
        public LoadAnimationPanel() {
            try {
                gearAImage = /*GraphicsUtilities.createCompatibleImage(*/
                        ImageIO.read(getClass().getResource("resources/images/gearA.png"))/*)*/;
                gearBImage = /*GraphicsUtilities.createCompatibleImage(*/
                        ImageIO.read(getClass().getResource("resources/images/gearB.png"))/*)*/;
                
            } catch (IOException ioe) {
                System.out.println(ioe);
            }
            setForeground(UIManager.getColor("textInactiveText"));
            setFont(UIManager.getFont("Label.font").deriveFont(36f));
            setPreferredSize(new Dimension(gearAImage.getWidth() + gearBImage.getWidth(),
                    gearAImage.getHeight() + gearBImage.getHeight()));
            
            PropertySetter rotator = new PropertySetter(this, "rotation", 0, 360);
            animator = new Animator(1000, Animator.INFINITE,
                    Animator.RepeatBehavior.LOOP, rotator);
            // Don't animate gears if loading is quick
            animator.setStartDelay(200);
            
        }
        
        public void setAnimating(boolean animating) {
            this.animating = animating;
            if (animating) {
                animator.start();
            } else {
                animator.stop();
            }
        }
        
        public boolean isAnimating() {
            return animating;
        }
        
        public void setRotation(int rotation) {
            this.rotation = rotation;
            repaint();
        }
        
        public int getRotation() {
            return rotation;
        }
        
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;
            super.paintComponent(g2);
            
            Dimension size = getSize();
            
            if (false) {
                int xoffset = 53;
                int yoffset = 53;
                
                int gearAWidth = gearAImage.getWidth();
                int gearAHeight = gearAImage.getHeight();
                int gearBWidth = gearBImage.getWidth();
                int gearBHeight = gearBImage.getHeight();
                
                int gearsWidth = gearAWidth + gearBWidth - xoffset;
                int gearsHeight = gearAHeight + gearBHeight - yoffset;
                
                int gearsX = (size.width - gearsWidth)/2;
                int gearsY = (size.height - gearsHeight)/2;
                
                if (rotation > 0) {
                    AffineTransform tA = new AffineTransform();
                    tA.translate(gearsX, gearsY);
                    tA.rotate(rotation, gearAWidth/2,gearAHeight/2);
                    AffineTransform tB = new AffineTransform();
                    tB.translate(gearsX + gearAWidth - xoffset, gearsY + gearAHeight - yoffset);
                    tB.rotate(-rotation, gearBWidth/2, gearBHeight/2);
                    g2.drawImage(gearAImage, tA, null);
                    g2.drawImage(gearBImage, tB, null);
                    
                } else {
                    g2.drawImage(gearAImage, gearsX, gearsY, null);
                    g2.drawImage(gearBImage, gearsX + gearAWidth - xoffset,
                            gearsY + gearAHeight - yoffset, null);
                }
            }
            Color textColor = getForeground();
            Color dotColor = textColor.darker();
            g2.setColor(textColor);
            g2.setFont(getFont());
            FontMetrics metrics = g2.getFontMetrics();
            Rectangle2D rect = metrics.getStringBounds(message, g2);
            Rectangle2D dotRect = metrics.getStringBounds(".", g2);
            float x = (float)(size.width - (rect.getWidth() + 3*dotRect.getWidth()))/2;
            float y = (float)(size.height - rect.getHeight())/2;
            g2.drawString(message, x, y);
            int tri = rotation / 120;
            float dx = 0;
            for(int i = 0; i < 3; i++) {
                g2.setColor(animator.isRunning() && i == tri? 
                    dotColor : 
                    textColor);
                g2.drawString(".", x + (float)(rect.getWidth() + dx), y);
                dx += dotRect.getWidth();
            }
            
        }
    } // LoadAnimationPanel
    
    public static void main(String args[]) {
        JFrame frame = new JFrame();
        LoadAnimationPanel p = new LoadAnimationPanel();
        p.setAnimating(true);
        frame.add(p);
        frame.setSize(new Dimension(500,400));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
}
