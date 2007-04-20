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
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Arc2D;
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
    
    private Color visitedForeground = new Color(85, 145, 90);
    private Color errorForeground = Color.red;
    
    // For demo loading progress animation
    private int pieDiameter = 20;
    private int pieOffset = 4;
    private int sliceCount = 6;
    private Timer animationTimer; 
    private Color sliceColor = new Color(110,100,180);
    private Color colorRamp[];
    private float loopCount = 0;
    private Demo animatingDemo;
    
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
        /*
        try {
            
            progressImage = ImageIO.read(DemoSelectorTreeRenderer.class.getResourceAsStream(
                    "resources/images/progress_ring.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
         */
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

    /*
    public void setBackground(Color color) {
	if(color instanceof ColorUIResource)
	    color = null;
	super.setBackground(color);
    }*/
    
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
        
        if (animationTimer == null) {
            animationTimer = new Timer(150, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    DemoSelectorTreeRenderer.this.tree.repaint();
                }
            });

            colorRamp = new Color[sliceCount];
            for(int i = 0; i < sliceCount; i++) {
                colorRamp[i] = new Color(sliceColor.getRed() + (i*10),
                        sliceColor.getGreen() + (i*10),
                        sliceColor.getBlue() + (i*10));
            }
        }
        
        setComponentOrientation(tree.getComponentOrientation());
        setEnabled(tree.isEnabled());
        
        if (isLeaf) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
            Object demoNode = node.getUserObject();
            if (demoNode instanceof String) {
                setText((String)demoNode);
                setIcon(null);
                setEnabled(false);
                setToolTipText("not yet implemented");
                
            } else {
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
                setBackground(demoState == Demo.State.RUNNING ||
                        demoState == Demo.State.INITIALIZING? getBackgroundSelectionColor() : 
                            getBackgroundNonSelectionColor());
                Color foreground = getTextNonSelectionColor();
                switch(demoState) {
                    case FAILED:
                        foreground = errorForeground;
                        break;
                    case RUNNING:
                    case INITIALIZING:
                        foreground = getTextSelectionColor();
                        break;
                    case PAUSED:
                        foreground = visitedForeground;
                        break;
                }                
                setForeground(foreground);
                
            }
                   
        } else {
            // don't display icon for categories
            demo = null;
            setBackground(getBackgroundNonSelectionColor());
            setForeground(getTextNonSelectionColor());
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
            if (demoState == Demo.State.INITIALIZING) {
                animatingDemo = demo;
                animationTimer.start();
                paintAnimation((Graphics2D)g, width - pieDiameter - 2, (height - pieDiameter)/2);
            } else if (animatingDemo == demo &&
                    demoState != Demo.State.INITIALIZING) {
                animatingDemo = null;
                animationTimer.stop();
                loopCount = 0;
            }
        }
         
    }
    
    private void paintAnimation(Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D)g.create();
        
        g2.translate(x,y);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int angle = 360 / sliceCount;
        int cycle = (int)(loopCount++ % sliceCount);
        for(int i = 0; i < sliceCount; i++) {
            Arc2D.Float arc = new Arc2D.Float(new Rectangle(0,0,pieDiameter,pieDiameter),
                    (i*angle), angle, Arc2D.PIE);
            g2.setColor(colorRamp[(cycle+i)%sliceCount]);
            g2.fill(arc);
        }
        g2.dispose();

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
            prefSize = new Dimension(prefSize.width + pieOffset + pieDiameter, 
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