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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.nio.Buffer;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

/**
 *
 * @author aim
 */
public class DemoSelectorTree extends JTree {
    private Color gradientColors[];
    private Image gradientImage;
    
    /** Creates a new instance of DemoSelectorTree */
    public DemoSelectorTree(TreeNode root) {
        super(root);
        setCellRenderer(new DemoSelectorTreeRenderer());
        setShowsRootHandles(false);
        ToolTipManager.sharedInstance().registerComponent(this);
    }
    
    public DemoSelectorTree(TreeNode root, Color gradientColor1, Color gradientColor2) {
        this(root);
        gradientColors = new Color[2];
        gradientColors[0] = gradientColor1;
        gradientColors[1] = gradientColor2;
        setOpaque(false);
    }
    
    protected void paintComponent(Graphics g) {
        if (gradientColors != null) {
            Rectangle bounds = getBounds();
            if (gradientImage == null || 
                    gradientImage.getWidth(this) != bounds.width ||
                    gradientImage.getHeight(this) != bounds.height) {
                
                gradientImage = Utilities.createGradientImage(bounds.width, bounds.height,
                        gradientColors[0], gradientColors[1]);
                
            }
            //System.out.println("painting gradient");
            g.drawImage(gradientImage, 0, 0, null);
        }
        super.paintComponent(g);
    }
    
    
    public class DemoSelectorTreeRenderer extends JLabel implements TreeCellRenderer {
        
        protected Color selectedForeground;
        protected Color selectedBackground;
        protected Color unselectedForeground;
        protected Color unselectedBackground;
        protected Color visitedForeground;
        protected Color errorForeground;
                
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
            setOpaque(false);
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

            this.hasFocus = hasFocus;
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
                    setOpaque(false);
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
                    selected = demoState == Demo.State.RUNNING ||
                            demoState == Demo.State.INITIALIZING;
                    setBackground(selected? selectedBackground : unselectedBackground);
                    setOpaque(selected);
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
                setOpaque(false);
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
    
}
