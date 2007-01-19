/*
 * CloseButton.java
 *
 * Created on September 15, 2006, 2:36 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package swingset3;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.UIManager;

/**
 *
 * @author aim
 */
public class CloseButton extends JButton {
    private static Icon closeBoxIcon = new CloseBoxIcon(14, 14,
            CloseButton.class.getResource("resources/images/close.png"));
    private static Icon darkCloseBoxIcon = new CloseBoxIcon(14, 14,
            CloseButton.class.getResource("resources/images/close_dark.png"));    
    private static Icon pressedCloseBoxIcon = new CloseBoxIcon(14, 14,
            CloseButton.class.getResource("resources/images/close_pressed.png")); 
    
    /** Creates a new instance of TabLabel */
    public CloseButton() {
        this(null);
    }
    
    public CloseButton(Action action) {
        super();
        setAction(action);
        setText(null);
        setIcon(closeBoxIcon);
        setRolloverIcon(darkCloseBoxIcon);
        setPressedIcon(pressedCloseBoxIcon);
        setBorderPainted(false);
        setForeground(UIManager.getColor("controlShadow"));
        addMouseListener(new MouseDetector());
    }
    
    private class MouseDetector extends MouseAdapter {
        public void mouseEntered(MouseEvent e) {
            setForeground(UIManager.getColor("controlDkShadow"));            
        }
        public void mouseExited(MouseEvent e) {
            setForeground(UIManager.getColor("controlShadow"));
        }
    }
    
    private static class CloseBoxIcon extends ImageIcon {
        
        private int width;
        private int height;
        
        public CloseBoxIcon(int width, int height, URL imageURL) {
            super(imageURL);
            this.width = width;
            this.height = height;
        }
        
        public int getIconHeight() {
            return height;
        }
        
        public int getIconWidth() {
            return width;
        }
        
        public void paintIcon(Component c, Graphics g, int x, int y) {
            super.paintIcon(c, g, x, y);
            Graphics2D g2 = (Graphics2D)g;
            g2.setColor(c.getForeground());
            g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(x+4, y+4, x + width - 4, y + height - 4);
            g2.drawLine(x+4, y + height - 4, x + width - 4, y+4);
        }
    }
    
}
