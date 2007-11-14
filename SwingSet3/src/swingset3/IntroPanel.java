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

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.interpolation.PropertySetter;
import org.jdesktop.animation.timing.triggers.TimingTrigger;
import org.jdesktop.animation.timing.triggers.TimingTriggerEvent;

/**
 *
 * @author aim
 */
//remind(aim): this class needs to be converted to use Components instead of Node
public class IntroPanel extends JPanel {
    private static final int GLEAM_X = 55;
    private static final int GLEAM_Y = 122;
    private static final int GLEAM_X_MAX = 362;
    private static final int HELMET_X = 342; 
    private static final int HELMET_Y = 161;
    private static final int REFLECTION_RANGE = 100;
    
    protected Color gradientColors[];
    protected BufferedImage backgroundImage;
    protected Node title;
    protected Node glowingTitle;
    protected Node gleam;
    protected Node sparkle;
    
    protected Animator gleamAnimator;
    protected Animator sparkleAnimator;
    protected Animator glowAnimator;
    protected Animator glowFadeAnimator;
    protected Animator slideAnimator;
    
    private boolean animating = false;
    
    public IntroPanel(int preferredWidth, int preferredHeight) {
        super();
        setLayout(null);

        setPreferredSize(new Dimension(preferredWidth, preferredHeight));
        initialize();
        
        // Temporary hook to re-run animation
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                initialize();
                repaint();
            }
        });
    }
    
    protected void initialize() {        
        try {
            title = new ImageNode("resources/images/splash_title_duke.png", 0, 0);
            glowingTitle = new ImageNode("resources/images/swingset3_glow.png", 0, 0, 0.0f);
            gleam = new ImageNode("resources/images/gleam.png", GLEAM_X, GLEAM_Y, 0.0f);
            sparkle = new ImageNode("resources/images/sparkle2.png", HELMET_X, HELMET_Y, 0.0f);
             
        } catch (IOException e) {            
        }
        gradientColors = new Color[2];
        //gradientColors[0] = new Color(208, 207, 202);
        gradientColors[0] = UIManager.getColor("Panel.background");
        gradientColors[1] = new Color(32, 32, 37); 
        
        PropertySetter gleamer = new PropertySetter(gleam, "x", GLEAM_X, GLEAM_X_MAX);
        gleamAnimator = new Animator(500, gleamer);
        gleamAnimator.setStartDelay(20);
        gleamAnimator.setAcceleration(.9f);
        gleamAnimator.setDeceleration(.1f);
        
        PropertySetter sparkler = new PropertySetter(sparkle, "alpha", 0.0f, 1.0f);
        sparkleAnimator = new Animator(600, sparkler);
        sparkleAnimator.setStartDelay(20);
        sparkleAnimator.setAcceleration(.2f);
        sparkleAnimator.setDeceleration(.1f);
        
        PropertySetter glower = new PropertySetter(glowingTitle, "alpha", 0.0f, 1.0f);
        glowAnimator = new Animator(1200, glower);
        glowAnimator.setAcceleration(.4f);
        glowAnimator.setDeceleration(.3f);
        glowAnimator.setStartDelay(700);
        
        TimingTrigger.addTrigger(gleamAnimator, glowAnimator, TimingTriggerEvent.STOP);
        
        PropertySetter glowFader = new PropertySetter(glowingTitle, "alpha", 1.0f, 0.0f);
        glowFadeAnimator = new Animator(600, glowFader);
        glowFadeAnimator.setAcceleration(.3f);
        glowFadeAnimator.setDeceleration(.1f);
        glowFadeAnimator.setStartDelay(300);
        
        TimingTrigger.addTrigger(glowAnimator, glowFadeAnimator, TimingTriggerEvent.STOP);
        
        animating = false;
    }
    
    protected void paintComponent(Graphics g) {
        Rectangle bounds = getBounds();
        
        if (backgroundImage == null ||
                backgroundImage.getWidth(this) != bounds.width ||
                backgroundImage.getHeight(this) != bounds.height) {
            // cache gradient
            backgroundImage = Utilities.createGradientImage(bounds.width, bounds.height,
                    gradientColors[0], gradientColors[1]);            
        }
        int x = 0;
        int y = 0;
        
        g.drawImage(backgroundImage, x, y, null);
        
        if (!animating) {
            gleam.setAlpha(1.0f);
            gleamAnimator.start();
            sparkleAnimator.start();
            animating = true;
        }
        
        // adjust position of painting to center the graphics
        int dx = Math.max(0, (bounds.width - title.getWidth())/2);
        int dy = Math.max(0, (bounds.height - title.getHeight())/2);
        g.translate(dx, dy);
        
        glowingTitle.paint((Graphics2D)g);
        title.paint((Graphics2D)g);
        gleam.paint((Graphics2D)g);
        sparkle.paint((Graphics2D)g);        
  
    }
    
    protected void initFadeAnimator(Node node) {
        PropertySetter fader = new PropertySetter(node, "alpha", 1.0f, 0.6f);
        Animator fadeAnimator = new Animator(5000, fader);
        fadeAnimator.setAcceleration(.3f);
        fadeAnimator.setDeceleration(.1f);
        fadeAnimator.setStartDelay(300);
        TimingTrigger.addTrigger(glowAnimator, fadeAnimator, TimingTriggerEvent.STOP);
    }
    
    protected void initSlideAnimator(Node node, int startx) {
        PropertySetter slider = new PropertySetter(node, "x", startx, startx + getWidth());
        slideAnimator = new Animator(1000, slider);
        slideAnimator.setAcceleration(.5f);
        slideAnimator.setStartDelay(800);
        TimingTrigger.addTrigger(glowFadeAnimator, slideAnimator, TimingTriggerEvent.STOP);
    }
    
    // remind(aim): could just use java.awt.Component. ?
    public abstract class Node {
        protected int x;
        protected int y;
        protected int width;
        protected int height;
        protected float alpha;
        
        public Node(int x, int y, int width, int height, float alpha) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.alpha = alpha;
        }
        
        public void setX(int x) {
            int oldX = this.x;
            this.x = x;
            if (x != oldX) {
                repaint();
            }
        }
        
        public int getX() {
            return x;
        }
        
        public void setY(int y) {
            int oldY = this.y;
            this.y = y;
            if (y != oldY) {
                repaint();
            }
        }
        
        public int getY() {
            return y;
        }
        
        public void setWidth(int width) {
            int oldWidth = this.width;
            this.width = width;
            if (width != oldWidth) {
                repaint(x, y, Math.max(oldWidth,width), height);
            }
        }
        
        public int getWidth() {
            return width;
        }
        
        public void setHeight(int height) {
            int oldHeight = this.height;
            this.height = height;
            if (height != oldHeight) {
                repaint(x, y, width, Math.max(oldHeight, height));
            }
        }
        
        public int getHeight() {
            return height;
        }
        
        public Rectangle getBounds() {
            return new Rectangle(x, y, width, height);
        }
        
        public void setAlpha(float alpha) {
            float oldAlpha = this.alpha;
            this.alpha = alpha;
            if (alpha != oldAlpha) {
                repaint(x, y, width, height);
            }
        }
        
        public float getAlpha() {
            return alpha;
        }  
        
        public abstract void paint(Graphics2D g);
    }
        
    public class ImageNode extends Node {
        
        BufferedImage image;
        
        public ImageNode(String imageURL, int x, int y) throws IOException {
            this(imageURL, x, y, 1.0f);
        }
        
        public ImageNode(String imageURL, int x, int y, float alpha) throws IOException {
            super(x, y, 0, 0, alpha);
            image = ImageIO.read(IntroPanel.class.getResource(imageURL));
            this.width = image.getWidth();
            this.height = image.getHeight();
        }
        
        public void paint(Graphics2D g) {
            if (alpha > 0 && g.getClipBounds().intersects(getBounds())) {
                Graphics2D g2 = g;
                if (alpha < 1.0) {
                    g2 = (Graphics2D)g.create();
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                }
                g2.drawImage(image, getX(), getY(), getWidth(), getHeight(), null);
            }
        }                
    }    
}