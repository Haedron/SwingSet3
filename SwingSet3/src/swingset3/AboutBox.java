/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package swingset3;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 *
 * @author Administrator
 */
public class AboutBox extends JPanel {
    private BufferedImage bgImage;
    
    public AboutBox() {
        try {
            bgImage = ImageIO.read(AboutBox.class.getResource("resources/images/about.jpg"));
        } catch (Exception ex) {            
        }
        
        
        
        
    }

}
