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

package swingset3.utilities;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.Window;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.NumberFormat;
import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 *
 * @author aim
 */
public class Utilities implements SwingConstants {
    
    private Utilities() {} // never instantiate
    
    public static boolean runningFromWebStart() {
        return ServiceManager.getServiceNames() != null;        
    }
    
    public static void setToplevelLocation(Window toplevel, Component component,
                                           int relativePosition) {
    
        Rectangle compBounds = component.getBounds();
        
        // Convert component location to screen coordinates
        Point p = new Point();
        SwingUtilities.convertPointToScreen(p, component);

        int x = 0;
        int y = 0;
        
        // Set frame location to be centered on panel
        switch(relativePosition) {
            case NORTH: {
                x = (p.x + (compBounds.width/2)) - (toplevel.getWidth() / 2);
                y = p.y - toplevel.getHeight();
                break;                
            }
            case EAST: {
                x = p.x + compBounds.width;
                y = (p.y + (compBounds.height/2)) - (toplevel.getHeight() / 2);
                break;
            }
            case SOUTH: {
                x = (p.x + (compBounds.width/2)) - (toplevel.getWidth() / 2);            
                y = p.y + compBounds.height;
                break;
            }
            case WEST: {
                x = p.x - toplevel.getWidth();
                y = (p.y + (compBounds.height/2)) - (toplevel.getHeight() / 2);
                break;                                
            }
            case NORTH_EAST: {
                x = p.x + compBounds.width;
                y = p.y - toplevel.getHeight();
                break;                
            }
            case NORTH_WEST: {
                x = p.x - toplevel.getWidth();
                y = p.y - toplevel.getHeight();
                break;                                     
            }
            case SOUTH_EAST: {
                x = p.x + compBounds.width;            
                y = p.y + compBounds.height; 
                break;
            }
            case SOUTH_WEST: {
                x = p.x - toplevel.getWidth();
                y = p.y + compBounds.height; 
                break;                
            }
            default:
            case CENTER: {               
                x = (p.x + (compBounds.width/2)) - (toplevel.getWidth() / 2);
                y = (p.y + (compBounds.height/2)) - (toplevel.getHeight() / 2);
            }
        }
        toplevel.setLocation(x, y);        
    }
    
    public static String getURLFileName(URL url) {
        String path = url.getPath();
        return path.substring(path.lastIndexOf("/") + 1);
    }
    
    public static BufferedImage createCompatibleImage(int width, int height) {
        
        return GraphicsEnvironment.getLocalGraphicsEnvironment().
                    getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(width, height);

    }
    
    public static BufferedImage createTranslucentImage(int width, int height) {
        
        return GraphicsEnvironment.getLocalGraphicsEnvironment().
                    getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
               
    }
    
    public static BufferedImage createGradientImage(int width, int height, Color gradient1, Color gradient2) {
                   
            BufferedImage gradientImage = Utilities.createCompatibleImage(width, height);
            GradientPaint gradient = new GradientPaint(0, 0, gradient1, 0, height, gradient2, false);
            Graphics2D g2 = (Graphics2D)gradientImage.getGraphics();
            g2.setPaint(gradient);
            g2.fillRect(0, 0, width, height);
            g2.dispose();
            
            return gradientImage;
    }


    public static BufferedImage createGradientMask(int width, int height, int orientation) {
        // algorithm derived from Romain Guy's blog
        BufferedImage gradient = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = gradient.createGraphics();
        GradientPaint paint = new GradientPaint(0.0f, 0.0f,
                new Color(1.0f, 1.0f, 1.0f, 1.0f),
                orientation == SwingConstants.HORIZONTAL? width : 0.0f, 
                orientation == SwingConstants.VERTICAL? height : 0.0f,
                new Color(1.0f, 1.0f, 1.0f, 0.0f));
        g.setPaint(paint);
        g.fill(new Rectangle2D.Double(0, 0, width, height));

        g.dispose();
        gradient.flush();

        return gradient;
    }
    
    public static boolean browse(URI uri) throws IOException, UnavailableServiceException {
        // Try using the Desktop api first
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(uri);
            return true;
            
        } catch (SecurityException sex) {
            // Running in sandbox, try using WebStart service
            BasicService basicService = 
                        (BasicService)ServiceManager.lookup("javax.jnlp.BasicService");
                
            if (basicService.isWebBrowserSupported()) {
                return basicService.showDocument(uri.toURL());
            } 
        }
        return false;
    }
    
    /**
     * Derives a color by adding the specified offsets to the base color's 
     * hue, saturation, and brightness values.   The resulting hue, saturation,
     * and brightness values will be contrained to be between 0 and 1.
     * @param base the color to which the HSV offsets will be added
     * @param dH the offset for hue
     * @param dS the offset for saturation
     * @param dB the offset for brightness
     * @return Color with modified HSV values
     */
    public static Color deriveColorHSB(Color base, float dH, float dS, float dB) {
        float hsb[] = Color.RGBtoHSB(
                base.getRed(), base.getGreen(), base.getBlue(), null);

        hsb[0] += dH;
        hsb[1] += dS;
        hsb[2] += dB;
        return Color.getHSBColor(
                hsb[0] < 0? 0 : (hsb[0] > 1? 1 : hsb[0]),
                hsb[1] < 0? 0 : (hsb[1] > 1? 1 : hsb[1]),
                hsb[2] < 0? 0 : (hsb[2] > 1? 1 : hsb[2]));
                                               
    }
    
    /**
     * Derives a color by multiplying to the base color's 
     * hue, saturation, and brightness values by the specified multipliers.   
     * @param base the color to which the HSV offsets will be added
     * @param xH the multiplier for hue
     * @param xS the multiplier for saturation
     * @param xB the multiplier for brightness
     * @return Color with modified HSV values
     */
    public static Color deriveColorPercentHSB(Color base, float xH, float xS, float xB) {
        float hsb[] = Color.RGBtoHSB(
                base.getRed(), base.getGreen(), base.getBlue(), null);

        hsb[0] *= xH;
        hsb[1] *= xS;
        hsb[2] *= xB;
        return Color.getHSBColor(
                hsb[0] < 0? 0 : (hsb[0] > 1? 1 : hsb[0]),
                hsb[1] < 0? 0 : (hsb[1] > 1? 1 : hsb[1]),
                hsb[2] < 0? 0 : (hsb[2] > 1? 1 : hsb[2]));
                                               
    }
    
    public static String getHTMLColorString(Color color) {
        String red = Integer.toHexString(color.getRed());
        String green = Integer.toHexString(color.getGreen());
        String blue = Integer.toHexString(color.getBlue());

        return "#" + 
                (red.length() == 1? "0" + red : red) +
                (green.length() == 1? "0" + green : green) +
                (blue.length() == 1? "0" + blue : blue);        
    }

   public static void printColor(String key, Color color) {
       float hsb[] = Color.RGBtoHSB(
                color.getRed(), color.getGreen(),
                color.getBlue(), null);
       System.out.println(key+": RGB=" + 
               color.getRed() + ","+ color.getGreen() + ","+ color.getBlue() + "  " +
                "HSB=" + String.format("%.0f%n",hsb[0]*360) + "," + 
                            String.format("%.3f%n",hsb[1]) + "," + 
                            String.format("%.3f%n", hsb[2]));
   }
    
    private static void testSetToplevelLocation(Window base, int relativePosition) {        
        JFrame frame = new JFrame("frame "+ relativePosition);
        frame.setSize(130,130);
        Utilities.setToplevelLocation(frame, base, relativePosition);
        frame.setVisible(true);        
    }
    
    public static void main(String args[]) {
        JFrame baseframe = new JFrame("base");
        baseframe.setSize(230,230);
        baseframe.setLocation(new Point(400,400));
        baseframe.setVisible(true);
        
        Utilities.testSetToplevelLocation(baseframe, Utilities.CENTER);
        Utilities.testSetToplevelLocation(baseframe, Utilities.NORTH);
        Utilities.testSetToplevelLocation(baseframe, Utilities.EAST);
        Utilities.testSetToplevelLocation(baseframe, Utilities.SOUTH); 
        Utilities.testSetToplevelLocation(baseframe, Utilities.WEST);
        Utilities.testSetToplevelLocation(baseframe, Utilities.NORTH_EAST);
        Utilities.testSetToplevelLocation(baseframe, Utilities.NORTH_WEST);
        Utilities.testSetToplevelLocation(baseframe, Utilities.SOUTH_EAST);
        Utilities.testSetToplevelLocation(baseframe, Utilities.SOUTH_WEST);
    }
    
}
