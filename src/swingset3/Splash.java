/*
 * Splash.java
 *
 * Created on May 22, 2007, 9:49 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package swingset3;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.SplashScreen;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;

/**
 *
 * @author aim
 */
public class Splash {
    private static final boolean GLEAM_ON = false;
    private static final int GLEAM_X_MAX = 340;
    private static final int ANIMATION_INTERVAL = 12;
    
    protected SplashScreen splashScreen;
    protected BufferedImage gleam;
    protected BufferedImage gleamBurst;
    protected BufferedImage sparkle;
    protected int gleamX = 26;
    protected int gleamY = 62;
    protected int delta = 5;

    protected int helmutX = 325;
    protected int helmutY = 100;
        
    /** Creates a new instance of Splash */
    public Splash() {
        splashScreen = SplashScreen.getSplashScreen();
        
        if (GLEAM_ON) {            
            if (splashScreen != null) {
                try {
                    gleam = ImageIO.read(Splash.class.getResource("resources/images/gleam.png"));
                    sparkle = ImageIO.read(Splash.class.getResource("resources/images/sparkle2.png"));
                    gleamBurst = ImageIO.read(Splash.class.getResource("resources/images/gleam_sparkle.png"));
                } catch (IOException e) {
                    
                }
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new Animator(), 0, ANIMATION_INTERVAL);
                
            }
        }
    }
    
    public Rectangle getBounds() {
        return splashScreen != null? splashScreen.getBounds() : null;
    }
        
    protected void paintSplash(Graphics2D g) throws IllegalStateException {
        
        Dimension size = splashScreen.getSize();
        
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, size.width, size.height);
        g.setPaintMode();
        if (gleamX > GLEAM_X_MAX) {
            g.drawImage(gleamBurst, GLEAM_X_MAX, gleamY, null);
        } else {
            g.drawImage(gleam, Math.min(gleamX, GLEAM_X_MAX), gleamY, null);
        }
        
        if (gleamX > helmutX - 24) {
            g.drawImage(sparkle, helmutX + (gleamX%2), helmutY, null);
        }
        

    }
    
    protected class Animator extends TimerTask {        
        public void run() { 
            try {
                paintSplash(splashScreen.createGraphics());
                splashScreen.update();
                if (gleamX < GLEAM_X_MAX) {
                    gleamX += delta+=1;
                } else {
                    cancel();
                }
            } catch (IllegalStateException ise) {
                // ok, splash is already gone, so stop animation
                cancel();
            }
        }
    }
    
}
