/*
 * DemoSelectorTreeRenderer.java
 *
 * Created on November 9, 2006, 3:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package swingset3;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

/**
 *
 * @author aim
 */
class DemoSelectorTreeRenderer extends JLabel implements TreeCellRenderer {

    transient protected Icon closedIcon;
    transient protected Icon openIcon;

    protected Color textSelectionColor;
    protected Color textNonSelectionColor;
    protected Color backgroundSelectionColor;
    protected Color backgroundNonSelectionColor;
    protected Color borderSelectionColor;
    
    private BufferedImage greyLED;
    private BufferedImage greenLED;
    private BufferedImage yellowLED;
    
    private int LEDsize = 10;
    private int LEDoffset = 4;
    private Timer blinkTimer;    
    private Demo blinkingDemo;
    private boolean blinkOn = false;
    private float blinkCount = 0;
    
    /** Last tree the renderer was painted in. */
    private JTree tree;

    protected boolean selected;
    protected boolean hasFocus;
    
    private Demo demo;
    
    
    public DemoSelectorTreeRenderer() {
	setHorizontalAlignment(JLabel.LEFT);
        setClosedIcon(UIManager.getIcon("Tree.closedIcon"));
	setOpenIcon(UIManager.getIcon("Tree.openIcon"));
 	setTextSelectionColor(UIManager.getColor("Tree.selectionForeground"));
	setTextNonSelectionColor(UIManager.getColor("Tree.textForeground"));
	setBackgroundSelectionColor(UIManager.getColor("Tree.selectionBackground"));
	setBackgroundNonSelectionColor(UIManager.getColor("Tree.textBackground"));
	setBorderSelectionColor(UIManager.getColor("Tree.selectionBorderColor"));
        try {
            greyLED = ImageIO.read(DemoSelectorTreeRenderer.class.getResourceAsStream(
                    "resources/images/greydot.png"));
            greenLED = ImageIO.read(DemoSelectorTreeRenderer.class.getResourceAsStream(
                    "resources/images/greendot.png"));
            yellowLED = ImageIO.read(DemoSelectorTreeRenderer.class.getResourceAsStream(
                    "resources/images/yellowdot.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setOpaque(true);

    }
    
    /**
      * Sets the icon used to represent non-leaf nodes that are expanded.
      */
    public void setOpenIcon(Icon newIcon) {
	openIcon = newIcon;
    }

    /**
      * Returns the icon used to represent non-leaf nodes that are expanded.
      */
    public Icon getOpenIcon() {
	return openIcon;
    }

    /**
      * Sets the icon used to represent non-leaf nodes that are not expanded.
      */
    public void setClosedIcon(Icon newIcon) {
	closedIcon = newIcon;
    }

    /**
      * Returns the icon used to represent non-leaf nodes that are not
      * expanded.
      */
    public Icon getClosedIcon() {
	return closedIcon;
    }
    /**
      * Sets the color the text is drawn with when the node is selected.
      */
    public void setTextSelectionColor(Color newColor) {
	textSelectionColor = newColor;
    }

    /**
      * Returns the color the text is drawn with when the node is selected.
      */
    public Color getTextSelectionColor() {
	return textSelectionColor;
    }

    /**
      * Sets the color the text is drawn with when the node isn't selected.
      */
    public void setTextNonSelectionColor(Color newColor) {
	textNonSelectionColor = newColor;
    }

    /**
      * Returns the color the text is drawn with when the node isn't selected.
      */
    public Color getTextNonSelectionColor() {
	return textNonSelectionColor;
    }

    /**
      * Sets the color to use for the background if node is selected.
      */
    public void setBackgroundSelectionColor(Color newColor) {
	backgroundSelectionColor = newColor;
    }


    /**
      * Returns the color to use for the background if node is selected.
      */
    public Color getBackgroundSelectionColor() {
	return backgroundSelectionColor;
    }

    /**
      * Sets the background color to be used for non selected nodes.
      */
    public void setBackgroundNonSelectionColor(Color newColor) {
	backgroundNonSelectionColor = newColor;
    }

    /**
      * Returns the background color to be used for non selected nodes.
      */
    public Color getBackgroundNonSelectionColor() {
	return backgroundNonSelectionColor;
    }

    /**
      * Sets the color to use for the border.
      */
    public void setBorderSelectionColor(Color newColor) {
	borderSelectionColor = newColor;
    }

    /**
      * Returns the color the border is drawn.
      */
    public Color getBorderSelectionColor() {
	return borderSelectionColor;
    }

    /**
     * Subclassed to map <code>FontUIResource</code>s to null. If 
     * <code>font</code> is null, or a <code>FontUIResource</code>, this
     * has the effect of letting the font of the JTree show
     * through. On the other hand, if <code>font</code> is non-null, and not
     * a <code>FontUIResource</code>, the font becomes <code>font</code>.
     */
    public void setFont(Font font) {
	if(font instanceof FontUIResource)
	    font = null;
	super.setFont(font);
    }
    
    public void setBackground(Color color) {
	if(color instanceof ColorUIResource)
	    color = null;
	super.setBackground(color);
    }
    
    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean isSelected,
            boolean isExpanded,
            boolean isLeaf,
            int row,
            boolean hasFocus) {
        
	String         stringValue = tree.convertValueToText(value, isSelected,
					  isExpanded, isLeaf, row, hasFocus);

        this.tree = tree;
	this.hasFocus = hasFocus;
        this.selected = isSelected;
	setText(stringValue);
        
        if (blinkTimer == null) {
            blinkTimer = new Timer(500, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    DemoSelectorTreeRenderer.this.tree.repaint();
                }
            });
        }
        
        setBackground(selected? getBackgroundSelectionColor() :
            getBackgroundNonSelectionColor());
        setForeground(selected? getTextSelectionColor() : getTextNonSelectionColor());
        setComponentOrientation(tree.getComponentOrientation());
        setEnabled(tree.isEnabled());
        
        if (isLeaf) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
            demo = (Demo)node.getUserObject();
            String demoName = demo.getName();
            if (demoName.endsWith("Demo")) {
                setText(demoName.substring(0, demoName.indexOf("Demo")));
            } else {
                setText(demoName);
            }
            if (isEnabled()) {
                setIcon(demo.getIcon());
            } else {
                setDisabledIcon(demo.getIcon());
            }
            setToolTipText(demo.getShortDescription());
            
            Demo.State demoState = demo.getState();
            if (demo != blinkingDemo &&
                    (demoState == Demo.State.INITIALIZING ||
                    demoState == Demo.State.RUNNING)) {
                blinkingDemo = demo;
                blinkCount = 0;
            }
                   
        } else {
            // don't display icon for categories
            demo = null;
            setIcon(null);
            setToolTipText(null);
        }
        return this;
    }
    
    public void paint(Graphics g) {
        
        super.paint(g);
        
        if (demo != null) {
            Demo.State demoState = demo.getState();
            int width = getWidth();
            int height = getHeight();
            if (demoState == Demo.State.RUNNING || demoState == Demo.State.INITIALIZING) {
                
                if (blinkCount < 3 || demoState == Demo.State.INITIALIZING) {
                    if (!blinkTimer.isRunning()) {
                        blinkTimer.restart();
                    }                
                    paintLED(g, blinkCount%1 == 0? greenLED : greyLED);
                    blinkCount += .5;
                
                
                } else { // not blinking anymore
                    paintLED(g, greenLED);                
                    if (blinkCount >= 3.5) {
                        blinkTimer.stop();
                    }
                }
            } else if (demoState == Demo.State.PAUSED) {
                paintLED(g, yellowLED);
            }
        }
    }
    
    private void paintLED(Graphics g, Image LED) {
        int width = getWidth();
        int height = getHeight();
        g.drawImage(LED,
                width - LEDsize - 1, getBaseline(width, height) - LEDsize + 1,
                LEDsize, LEDsize, this);
    }

    private void paintFocus(Graphics g, int x, int y, int w, int h, Color notColor) {
	Color       focusColor = getBorderSelectionColor();

	if (focusColor != null && (selected)) {
	    g.setColor(focusColor);
	    g.drawRect(x, y, w - 1, h - 1);
	}
    }

    
    public Dimension getPreferredSize() {
        Dimension  prefSize = super.getPreferredSize();
        
        if (prefSize != null) {
            prefSize = new Dimension(prefSize.width + LEDoffset + LEDsize, 
                    prefSize.height);
        }
        return prefSize;
    }
    
    public void validate() {}
    public void invalidate() {}
    public void revalidate() {}
    public void repaint(long tm, int x, int y, int width, int height) {}
    public void repaint(Rectangle r) {}
    public void repaint() {}

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {	
	// Strings get interned...
	if (propertyName == "text"
                || ((propertyName == "font" || propertyName == "foreground")
                    && oldValue != newValue
                    && getClientProperty(javax.swing.plaf.basic.BasicHTML.propertyKey) != null)) {

	    super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

   /**
    * Overridden for performance reasons.
    * See the <a href="#override">Implementation Note</a>
    * for more information.
    */
    public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {}
    public void firePropertyChange(String propertyName, char oldValue, char newValue) {}
    public void firePropertyChange(String propertyName, short oldValue, short newValue) {}
    public void firePropertyChange(String propertyName, int oldValue, int newValue) {}
    public void firePropertyChange(String propertyName, long oldValue, long newValue) {}
    public void firePropertyChange(String propertyName, float oldValue, float newValue) {}
    public void firePropertyChange(String propertyName, double oldValue, double newValue) {}
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}

    
    class BlinkRepainter implements ActionListener {
        private JTree tree;
        
        public BlinkRepainter(JTree tree) {
        }
        public void actionPerformed(ActionEvent e) {
            tree.repaint();
        }
    }
    
    
    
}