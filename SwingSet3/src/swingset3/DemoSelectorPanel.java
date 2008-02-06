/*
 * Copyright 2008 Sun Microsystems, Inc.  All Rights Reserved.
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

import swingset3.utilities.GradientPanel;
import swingset3.utilities.CollapsiblePanel;
import swingset3.utilities.ArrowIcon;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Administrator
 */
public class DemoSelectorPanel extends JPanel implements Scrollable {    

    private static final Border chiselBorder = new ChiselBorder();
    private static final Border panelBorder = new CompoundBorder(
            chiselBorder, new EmptyBorder(6,8,6,0));
    private static final Border categoryBorder = new CompoundBorder(
            chiselBorder, new EmptyBorder(0,0,10,0));    
    private static final Border buttonBorder = new CompoundBorder(
            new DemoButtonBorder(), new EmptyBorder(0, 18, 0, 0));    
    
    private List<JLabel> demoListLabels;
    private List<JPanel> viewPanels;
    private List<CollapsiblePanel> collapsePanels;
    private Icon expandedIcon;
    private Icon collapsedIcon;
       
    private ButtonGroup group;
    private ActionListener demoActionListener = new DemoActionListener();
    private int buttonHeight = 0;
    
    private Demo selectedDemo;
    
    public DemoSelectorPanel(Map<String,List<Demo>> demoMap) {
        super();
    
        GridBagLayout gridbag = new GridBagLayout();
        setLayout(gridbag);
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
                
        group = new ButtonGroup();
        demoListLabels = new ArrayList();
        viewPanels = new ArrayList();
        collapsePanels = new ArrayList();
        
        Set<String> demoSetTitles = demoMap.keySet();
        for(String title: demoSetTitles) {
            Component selector = createDemoSelector(title, demoMap.get(title));
            gridbag.addLayoutComponent(selector, c);
            add(selector);
            c.gridy++;
        }
    }
    
    public void updateUI() {
        super.updateUI();
        overrideDefaults();
    }
        
    protected void overrideDefaults() {
        
        expandedIcon = new ArrowIcon(ArrowIcon.SOUTH,
                UIManager.getColor(SwingSet3.titleForegroundKey));
        collapsedIcon = new ArrowIcon(ArrowIcon.EAST,
                UIManager.getColor(SwingSet3.titleForegroundKey));
        if (demoListLabels != null) {
            for(JLabel label: demoListLabels) {
                label.setForeground(UIManager.getColor(SwingSet3.titleForegroundKey));
                label.setFont(UIManager.getFont(SwingSet3.titleFontKey));
            }
        }
        if (viewPanels != null) {
            for(JPanel panel: viewPanels) {
                panel.setBackground(
                    UIManager.getColor(SwingSet3.subPanelBackgroundColorKey));
            }
        }
        if (collapsePanels != null) {
            for (CollapsiblePanel collapsePanel : collapsePanels) {
                collapsePanel.setFont(
                        UIManager.getFont("CheckBox.font").deriveFont(Font.BOLD));
                collapsePanel.setForeground(UIManager.getColor(SwingSet3.titleForegroundKey));
                collapsePanel.setExpandedIcon(expandedIcon);
                collapsePanel.setCollapsedIcon(collapsedIcon);
            }
        }
    }
        
    protected Component createDemoSelector(String demoSetTitle, List<Demo> demoSet) {
        JPanel selectorPanel = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();        
        selectorPanel.setLayout(gridbag);
        
        // Add label with title of demo set
        JPanel titlePanel = new GradientPanel(
                SwingSet3.titleGradientColor1Key,
                SwingSet3.titleGradientColor2Key);
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBorder(panelBorder);
        JLabel demoListLabel = new JLabel(demoSetTitle);
        demoListLabels.add(demoListLabel);
        demoListLabel.setOpaque(false);
        demoListLabel.setHorizontalAlignment(JLabel.LEADING);
        titlePanel.add(demoListLabel);
        c.gridx = c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        gridbag.addLayoutComponent(titlePanel, c);
        selectorPanel.add(titlePanel);
        
        // Add panel with view combobox
        JPanel viewPanel = new JPanel();
        viewPanels.add(viewPanel);
        viewPanel.setLayout(new BoxLayout(viewPanel, BoxLayout.X_AXIS));
        viewPanel.setBorder(new CompoundBorder(chiselBorder,
                new EmptyBorder(12,8,12,8)));
        JLabel viewLabel = new JLabel("View:");
        viewPanel.add(viewLabel);
        viewPanel.add(Box.createHorizontalStrut(6));
        JComboBox viewComboBox = new JComboBox();
        viewComboBox.addItem("by category");
        viewPanel.add(viewComboBox);
        c.gridy++;
        gridbag.addLayoutComponent(viewPanel, c);
        selectorPanel.add(viewPanel);
        
        HashMap<String,JPanel> categoryMap = new HashMap();
        GridBagLayout categoryGridbag = null;
        GridBagConstraints cc = new GridBagConstraints();
        cc.gridx = cc.gridy = 0;
        cc.weightx = 1;
        cc.fill = GridBagConstraints.HORIZONTAL;
        CollapsiblePanel collapsePanel = null;
        for(Demo demo: demoSet) {
            String category = demo.getCategory();
            JPanel categoryPanel = categoryMap.get(category);
            if (categoryPanel == null) {
                // Create category collapsible panel
                categoryPanel = new JPanel();
                categoryGridbag = new GridBagLayout();
                categoryPanel.setLayout(categoryGridbag);                
                collapsePanel = new CollapsiblePanel(categoryPanel, category, 
                        "click to expand or contract category");
                collapsePanels.add(collapsePanel);
                collapsePanel.setBorder(categoryBorder);
                categoryMap.put(category, categoryPanel);
                c.gridy++;
                gridbag.addLayoutComponent(collapsePanel, c);
                selectorPanel.add(collapsePanel);
            }
            DemoButton demoButton = new DemoButton(demo);
            categoryGridbag.addLayoutComponent(demoButton, cc);
            cc.gridy++;
            group.add(demoButton);
            categoryPanel.add(demoButton);
            if (buttonHeight == 0) {
                buttonHeight = demoButton.getPreferredSize().height;
            }
        }
        // add empty component to take up any extra room on bottom
        JPanel trailer = new JPanel();
        c.weighty = 1.0;
        gridbag.addLayoutComponent(trailer, c);
        selectorPanel.add(trailer);
        
        overrideDefaults();
        
        return selectorPanel;
    }
    
    public Demo getSelectedDemo() {
        return selectedDemo;
    }
    
    protected void setSelectedDemo(Demo demo) {
        Demo oldSelectedDemo = selectedDemo;
        selectedDemo = demo;
        firePropertyChange("selectedDemo", oldSelectedDemo, demo);
    }
    
    public Dimension getPreferredScrollableViewportSize() {
        Dimension prefSize = getPreferredSize();
        return new Dimension(prefSize.width + UIManager.getInt("ScrollBar.width") + 4,
                prefSize.height);
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return orientation == SwingConstants.VERTICAL?  buttonHeight : 4;        
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
    
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 1;
    }
    
    protected class DemoButton extends JToggleButton {
        private Demo demo;
        public DemoButton(Demo demo) {
            super();
            this.demo = demo;
            String demoName = demo.getName();
            if (demoName.endsWith("Demo")) {
                setText(demoName.substring(0, demoName.indexOf("Demo")));
            } else {
                setText(demoName);
            }
            setIcon(demo.getIcon());
            setIconTextGap(10);
            setHorizontalTextPosition(JToggleButton.TRAILING);
            setHorizontalAlignment(JToggleButton.LEADING);
            setOpaque(true);
            setBorder(buttonBorder);
            setFocusPainted(false);
            setContentAreaFilled(false);
            addActionListener(demoActionListener);
        }
        @Override
        protected void paintComponent(Graphics g) {
            if (isSelected()) {
                g.setColor(UIManager.getColor("Tree.selectionBackground"));
                Dimension size = getSize();
                g.fillRect(0, 0, size.width, size.height); 
                setForeground(UIManager.getColor("Tree.selectionForeground"));
            } else {
                setForeground(UIManager.getColor("Button.foreground"));
            }
            super.paintComponent(g);
        }
        
        public Demo getDemo() {
            return demo;
        }
    }
    
    private class DemoActionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            DemoButton demoButton = (DemoButton)event.getSource();
            setSelectedDemo(demoButton.getDemo());
        }
    }
    
    protected static class DemoButtonBorder implements Border {
        private Insets insets = new Insets(2, 1, 1, 1);
        
        public DemoButtonBorder() {            
        }
        
        public Insets getBorderInsets(Component c) {
            return insets;
        }
        public boolean isBorderOpaque() {
            return true;
        }
         public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            AbstractButton b = (AbstractButton)c;
            if (b.isSelected()) {
                g.setColor(UIManager.getColor("controlDkShadow"));
                g.drawLine(x, y, x + width, y);
                g.setColor(UIManager.getColor("controlShadow"));
                g.drawLine(x, y + 1, x + width, y + 1);
                g.drawLine(x, y + 2, x, y + height - 2);
                g.setColor(Color.white);
                g.drawLine(x, y + height - 1, x + width, y + height-1);
            }
        }
    }
    
    protected static class ChiselBorder implements Border {
        private Insets insets = new Insets(1, 0, 1, 0);
        
        public ChiselBorder() {            
        }
        
        public Insets getBorderInsets(Component c) {
            return insets;
        }
        public boolean isBorderOpaque() {
            return true;
        }
         public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Color bg = c.getBackground();
            // render highlight at top
            Color highlight = new Color((int)Math.min(bg.getRed()*1.1f,255),
                                        (int)Math.min(bg.getGreen()*1.1f, 255),
                                        (int)Math.min(bg.getBlue()*1.1f, 255));            
            g.setColor(highlight);
            g.drawLine(x, y, x + width, y);
            // render shadow on bottom
            Color shadow = new Color((int)(bg.getRed()*.9f),
                                     (int)(bg.getGreen()*.9f), 
                                     (int)(bg.getBlue()*.9f));
            g.setColor(shadow);
            g.drawLine(x, y + height - 1, x + width, y + height - 1);
        }
    }
}
