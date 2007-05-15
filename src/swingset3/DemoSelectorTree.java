/*
 * DemoSelectorTree.java
 *
 * Created on May 14, 2007, 8:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package swingset3;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

/**
 *
 * @author aim
 */
public class DemoSelectorTree extends JTree {
    
    /** Creates a new instance of DemoSelectorTree */
    public DemoSelectorTree(TreeNode root) {
        super(root);
        setOpaque(false);
    }
    
    public void paint(Graphics g) {
        /*
        Rectangle visibleRect = getVisibleRect();
        Rectangle bounds = getBounds();
        
        Graphics2D g2 = (Graphics2D)g;
        GradientPaint gradient = new GradientPaint(0, 0, new Color(250,250,250),
                                                       0, bounds.height/2, new Color(200,200,200),
                                                       false);
        g2.setPaint(gradient);

        g2.fillRect(0, 0, bounds.width, bounds.height);
         */
        super.paint(g);        
    }
    
}
