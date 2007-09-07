/*
 * ImageViewer.java
 *
 * Created on February 1, 2006, 6:55 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package swingset3;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * Debugging facility; if you create an ImageViewer with an image, it will
 * pop up a separate window displaying that image.  This is useful for
 * debugging, for example, issues with the transition images.
 *
 * @author Chet
 */
class ImageViewer extends JFrame {
    
    private Image image;
    
    /** 
     * Creates a simple JFrame that displays the given image in it
     */
    public ImageViewer(Image image, String title) {
        setTitle(title);
        this.image = image;
        this.setSize(image.getWidth(null) + 10, image.getHeight(null) + 30);
        ImageViewerComponent component = new ImageViewerComponent();
        component.setPreferredSize(new Dimension(image.getWidth(null),
                image.getHeight(null)));
        add(component);
        pack();
        setVisible(true);
    }
    
    /**
     * Private custom component class that displays the image of ImageViewer
     * in the component during the paint method
     */
    private class ImageViewerComponent extends JComponent {
        
        public void paint(Graphics g) {
            g.drawImage(image, 0, 0, null);
        }
    }
}
