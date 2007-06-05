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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 *
 * @author aim
 */
public class Utilities implements SwingConstants {
    
    private Utilities() {} // never instantiate
    
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
    
    public static BufferedImage createGradientImage(int width, int height, Color gradient1, Color gradient2) {
                   
            BufferedImage gradientImage = Utilities.createCompatibleImage(width, height);
            GradientPaint gradient = new GradientPaint(0, 0, gradient1, 0, height, gradient2, false);
            Graphics2D g2 = (Graphics2D)gradientImage.getGraphics();
            g2.setPaint(gradient);
            g2.fillRect(0, 0, width, height);
            g2.dispose();
            
            return gradientImage;
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
