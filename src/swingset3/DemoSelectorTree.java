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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;

/**
 *
 * @author aim
 */
public class DemoSelectorTree extends JTree {
    private Color gradientColors[];
    private Image gradientImage;
    
    /** Creates a new instance of DemoSelectorTree */
    public DemoSelectorTree(TreeModel model) {
        super(model);
        setCellRenderer(new DemoSelectorTreeRenderer(getCellRenderer()));
        setShowsRootHandles(false);
        ToolTipManager.sharedInstance().registerComponent(this);
        
    }
    
    public DemoSelectorTree(TreeModel model, Color gradientColor1, Color gradientColor2) {
        this(model);
        gradientColors = new Color[2];
        gradientColors[0] = gradientColor1;
        gradientColors[1] = gradientColor2;
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        if (gradientColors != null) {
            Rectangle bounds = getBounds();
            if (gradientImage == null || 
                    gradientImage.getWidth(this) != bounds.width ||
                    gradientImage.getHeight(this) != bounds.height) {
                
                gradientImage = Utilities.createGradientImage(bounds.width, bounds.height,
                        gradientColors[0], gradientColors[1]);
                
            }
            g.drawImage(gradientImage, 0, 0, null);
        }
        
        super.paintComponent(g);
    }
    
    
    public class DemoSelectorTreeRenderer implements TreeCellRenderer {
        private JLabel delegate;
        
        protected Color visitedForeground;
        protected Color errorForeground;
                
        protected boolean selected;
        protected boolean hasFocus;
        
        protected Demo demo;
        
        public DemoSelectorTreeRenderer(TreeCellRenderer delegate) {
            this.delegate = (JLabel)delegate;
            this.delegate.setHorizontalAlignment(JLabel.LEFT);
            visitedForeground = new Color(85, 145, 90);
            errorForeground = Color.RED;
            //setBorderSelectionColor(UIManager.getColor("Tree.selectionBorderColor"));
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
            delegate.setFont(font);
        }
        
        public Component getTreeCellRendererComponent(
                JTree tree,
                Object value,
                boolean isSelected,
                boolean isExpanded,
                boolean isLeaf,
                int row,
                boolean hasFocus) {
            
            JLabel renderer = (JLabel)((TreeCellRenderer)delegate).getTreeCellRendererComponent(tree,
                    value, isSelected, isExpanded, isLeaf, row, hasFocus);
            
            String stringValue = tree.convertValueToText(value, isSelected,
                    isExpanded, isLeaf, row, hasFocus);

            this.hasFocus = hasFocus;
            renderer.setText(stringValue);
            
            renderer.setComponentOrientation(tree.getComponentOrientation());
            renderer.setEnabled(tree.isEnabled());
            selected = false;
            
            if (isLeaf) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;                
                demo = (Demo)node.getUserObject();
                
                String demoName = demo.getName();
                if (demoName.endsWith("Demo")) {
                    renderer.setText(demoName.substring(0, demoName.indexOf("Demo")));
                } else {
                    renderer.setText(demoName);
                }
                if (isEnabled()) {
                    renderer.setIcon(demo.getIcon());
                } else {
                    renderer.setDisabledIcon(demo.getIcon());
                }
                renderer.setToolTipText(demo.getShortDescription());
                
                Demo.State demoState = demo.getState();
                selected = demoState == Demo.State.RUNNING ||
                        demoState == Demo.State.INITIALIZING;

                Color foreground = renderer.getForeground();
                switch(demoState) {
                    case FAILED:
                        foreground = errorForeground;
                        break;
                    case STOPPED:
                        foreground = visitedForeground;
                        break;
                }
                renderer.setForeground(foreground);
                
                
            } else {
                // don't display icon for categories
                demo = null;
                renderer.setIcon(null);
                // remind: Need to figure out how to get tooltip text on "category" node
                renderer.setToolTipText(null);
            }
            //renderer.setOpaque(selected);
            return renderer;
        }
    }   
}
