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

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.interpolation.PropertySetter;
import org.jdesktop.animation.timing.triggers.TimingTrigger;
import org.jdesktop.animation.timing.triggers.TimingTriggerEvent;

/**
 *
 * @author aim
 */
//remind(aim): this class needs to be converted to use Components instead of Glyph
public class IntroPanel extends JComponent {
    private static final int GLEAM_X = 55;
    private static final int GLEAM_Y = 122;
    private static final int GLEAM_X_MAX = 362;
    private static final int HELMET_X = 342; 
    private static final int HELMET_Y = 161;
    private static final int REFLECTION_RANGE = 100;
    
    protected Color gradientColors[];
    protected BufferedImage backgroundImage;
    protected Glyph title;
    protected Glyph glowingTitle;
    protected Glyph gleam;
    protected Glyph sparkle;
    protected Glyph message;
    
    protected Animator gleamAnimator;
    protected Animator glowAnimator;

    protected JTextArea messageText;
    protected float messageAlpha;
    
    public IntroPanel(int preferredWidth, int preferredHeight) {
        super();
        setLayout(new BorderLayout());

        initialize();
        setPreferredSize(new Dimension(preferredWidth, preferredHeight));
        
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
            title = new ImageGlyph("resources/images/splash_graphics.png", 0, 0);
            glowingTitle = new ImageGlyph("resources/images/swingset3_glow.png", 0, 0, 0.0f);
            gleam = new ImageGlyph("resources/images/gleam.png", GLEAM_X, GLEAM_Y, 0.0f);
            sparkle = new ImageGlyph("resources/images/sparkle2.png", HELMET_X, HELMET_Y, 0.0f);
            message = new ImageGlyph("resources/images/message2.png", 0, 0, 0.0f);
             
        } catch (IOException e) {
            
        }
        gradientColors = new Color[2];
        gradientColors[0] = new Color(208, 207, 202);
        gradientColors[1] = new Color(32, 32, 37); 
        
        gleamAnimator = null;
        
        
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Rectangle bounds = getBounds();
        
        if (backgroundImage == null ||
                backgroundImage.getWidth(this) != bounds.width ||
                backgroundImage.getHeight(this) != bounds.height) {
            
            backgroundImage = Utilities.createGradientImage(bounds.width, bounds.height,
                    gradientColors[0], gradientColors[1]);
            
        }
        int x = 0;
        int y = 0;
        
        g.drawImage(backgroundImage, x, y, null);
        
        if (gleamAnimator == null) {
            PropertySetter gleamer = new PropertySetter(gleam, "x", GLEAM_X, GLEAM_X_MAX);
            gleamAnimator = new Animator(500, gleamer);
            gleamAnimator.setStartDelay(20);
            gleamAnimator.setAcceleration(.3f);
            gleamAnimator.setDeceleration(.1f);  
            
            PropertySetter sparkler = new PropertySetter(sparkle, "alpha", 0.0f, 1.0f);
            Animator sparkleAnimator = new Animator(600, sparkler);
            sparkleAnimator.setStartDelay(20);
            sparkleAnimator.setAcceleration(.2f);
            sparkleAnimator.setDeceleration(.1f);  
       
            PropertySetter glower = new PropertySetter(glowingTitle, "alpha", 0.0f, 1.0f);
            glowAnimator = new Animator(1200, glower);
            glowAnimator.setAcceleration(.4f);
            glowAnimator.setDeceleration(.3f);
            glowAnimator.setStartDelay(700);           
            
            TimingTrigger.addTrigger(gleamAnimator, glowAnimator, TimingTriggerEvent.STOP);
            
            //initFadeAnimator(duke);
            initFadeAnimator(title);
            initFadeAnimator(glowingTitle);
            initFadeAnimator(gleam);
            initFadeAnimator(sparkle);
            
            PropertySetter messageFadeIn = new PropertySetter(message, "alpha", 0.0f, 1.0f);
            Animator messageAnimator = new Animator(1000, messageFadeIn);
            messageAnimator.setStartDelay(100);
            messageAnimator.setAcceleration(.3f);
            messageAnimator.setDeceleration(.1f);
            TimingTrigger.addTrigger(glowAnimator, messageAnimator, TimingTriggerEvent.STOP);
      
            gleam.setAlpha(1.0f);
            gleamAnimator.start();
            sparkleAnimator.start();

        }

        glowingTitle.paint(g);
        title.paint(g);
        gleam.paint(g);
        sparkle.paint(g);        
        message.paint(g);
  
    }
    
    protected void initFadeAnimator(Glyph glyph) {
        PropertySetter fader = new PropertySetter(glyph, "alpha", glyph.getAlpha(), 0.0f);
        Animator fadeAnimator = new Animator(1000, fader);
        fadeAnimator.setAcceleration(.3f);
        fadeAnimator.setDeceleration(.1f);
        fadeAnimator.setStartDelay(300);
        TimingTrigger.addTrigger(glowAnimator, fadeAnimator, TimingTriggerEvent.STOP);
    }
    
    public void setMessageAlpha(float alpha) {
        this.messageAlpha = alpha;
        repaint();
    }
    
    private void drawText(Graphics2D g2, String text, int size, float opacity) {
        // algorithm courtesy of Romain Guy
        Composite oldComposite = g2.getComposite();
        float preAlpha = 1.0f;
        if (oldComposite instanceof AlphaComposite &&
                ((AlphaComposite) oldComposite).getRule() == AlphaComposite.SRC_OVER) {
            preAlpha = ((AlphaComposite) oldComposite).getAlpha();
        }
        
        g2.setFont(getFont());
        FontMetrics metrics = g2.getFontMetrics();
        int ascent = metrics.getAscent();
        int heightDiff = (metrics.getHeight() - ascent) / 2;
        
        g2.setColor(Color.BLACK);
        
        double tx = 2.0;
        double ty = 2.0 + heightDiff - size;
        g2.translate(tx, ty);
        
        for (int i = -size; i <= size; i++) {
            for (int j = -size; j <= size; j++) {
                double distance = i * i + j * j;
                float alpha = opacity;
                if (distance > 0.0d) {
                    alpha = (float) (1.0f / ((distance * size) * opacity));
                }
                alpha *= preAlpha;
                if (alpha > 1.0f) {
                    alpha = 1.0f;
                }
                g2.setComposite(AlphaComposite.SrcOver.derive(alpha));
                g2.drawString(text, i + size, j + size + ascent);
            }
        }
        
        g2.setComposite(oldComposite);
        g2.setColor(Color.WHITE);
        g2.drawString(text, size, size + ascent);
        
        g2.translate(-tx, -ty);
    }

    
    // remind(aim): could just use java.awt.Component. ?
    public abstract class Glyph {
        protected int x;
        protected int y;
        protected int width;
        protected int height;
        protected float alpha;
        
        public Glyph(int x, int y, int width, int height, float alpha) {
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
                repaint();
            }
        }
        
        public int getWidth() {
            return width;
        }
        
        public void setHeight(int height) {
            int oldHeight = this.height;
            this.height = height;
            if (height != oldHeight) {
                repaint();
            }
        }
        
        public int getHeight() {
            return height;
        }
        
        public void setAlpha(float alpha) {
            float oldAlpha = this.alpha;
            this.alpha = alpha;
            if (alpha != oldAlpha) {
                repaint();
            }
        }
        
        public float getAlpha() {
            return alpha;
        }  
        
        public abstract void paint(Graphics g);
    }
        
    public class ImageGlyph extends Glyph {
        
        BufferedImage image;
        
        public ImageGlyph(String imageURL, int x, int y) throws IOException {
            this(imageURL, x, y, 1.0f);
        }
        
        public ImageGlyph(String imageURL, int x, int y, float alpha) throws IOException {
            super(x, y, 0, 0, alpha);
            image = ImageIO.read(IntroPanel.class.getResource(imageURL));
            this.width = image.getWidth();
            this.height = image.getHeight();
        }
        
        public void paint(Graphics g) {
            if (alpha > 0) {
                Graphics2D g2 = (Graphics2D)g;            
                if (alpha < 1.0) {
                    g2 = (Graphics2D)g.create();
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                }
                g2.drawImage(image, getX(), getY(), getWidth(), getHeight(), null);
            }
        }                
    }
}