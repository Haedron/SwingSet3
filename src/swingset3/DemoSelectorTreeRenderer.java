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
import java.awt.Font;
import java.awt.Rectangle;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

/**
 *
 * @author aim
 */
class DemoSelectorTreeRenderer extends JLabel implements TreeCellRenderer {

    protected Color selectedForeground;
    protected Color selectedBackground;
    protected Color unselectedForeground;
    protected Color unselectedBackground;
    protected Color visitedForeground; 
    protected Color errorForeground;
    
    /** Last tree the renderer was painted in. */
    protected JTree tree;

    protected boolean selected;
    protected boolean hasFocus;
    
    protected Demo demo;    
    
    public DemoSelectorTreeRenderer() {
        setHorizontalAlignment(JLabel.LEFT);
 	selectedForeground = UIManager.getColor("Tree.selectionForeground");
        selectedBackground = UIManager.getColor("Tree.selectionBackground");
	unselectedForeground = UIManager.getColor("Tree.textForeground");
	unselectedBackground = UIManager.getColor("Tree.textBackground");
        visitedForeground = new Color(85, 145, 90);
        errorForeground = Color.RED;
	//setBorderSelectionColor(UIManager.getColor("Tree.selectionBorderColor"));
        setOpaque(true);
    }

    /**
     * Subclassed to map <code>FontUIResource</code>s to null. If 
     * <code>font</code> is null, or a <code>FontUIResource</code>, this
     * has the effect of letting the font of the JTree show
     * through. On the other hand, if <code>font</code> is non-null, and not
     * a <code>FontUIResource</code>, the font becomes <code>font</code>.
     */
    public void setFont(Font font) {
	if (font instanceof FontUIResource)
	    font = null;
	super.setFont(font);
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
        
        setComponentOrientation(tree.getComponentOrientation());
        setEnabled(tree.isEnabled());
        
        if (isLeaf) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
            Object demoNode = node.getUserObject();
            if (demoNode instanceof String) {
                // Demo class listed, but class doesn't exist yet
                setText((String)demoNode);
                setBackground(unselectedBackground);
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
                        demoState == Demo.State.INITIALIZING? selectedBackground : 
                            unselectedBackground);
                Color foreground = unselectedForeground;
                switch(demoState) {
                    case FAILED:
                        foreground = errorForeground;
                        break;
                    case RUNNING:
                    case INITIALIZING:
                        foreground = selectedForeground;
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
            setBackground(unselectedBackground);
            setForeground(unselectedForeground);
	    setIcon(null);
            // remind: Need to figure out how to get tooltip text on "category" node
            setToolTipText(null);
        }
        return this;
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
    
}