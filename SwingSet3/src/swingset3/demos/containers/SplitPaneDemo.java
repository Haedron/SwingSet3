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

package swingset3.demos.containers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.InputVerifier;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import swingset3.DemoProperties;
import swingset3.demos.DemoBase;

/**
 * Split Pane demo
 *
 * @version 1.12 11/17/05
 * @author Scott Violet
 * @author Jeff Dinkins
 */
@DemoProperties(
        value = "JSplitPane Demo",
        category = "Containers",
        description = "Demonstrates JSplitPane, a container which lays out two components in an adjustable split view (horizontal or vertical)",
        sourceFiles = {
                "swingset3/demos/containers/SplitPaneDemo.java",
                "swingset3/demos/DemoBase.java"
                }
)
public class SplitPaneDemo extends DemoBase {
    private static final Insets insets = new Insets(4,8,4,8);

    private JSplitPane splitPane;
    private JLabel day;
    private JLabel night;
    
    private JPanel controlPanel;
    private GridBagLayout gridbag;
    private GridBagConstraints c;


    private JTextField divSize;
    private JTextField daySize;
    private JTextField nightSize;
    
    private InputVerifier fieldVerifier;

    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        SplitPaneDemo demo = new SplitPaneDemo();
        demo.mainImpl();
    }

    /**
     * SplitPaneDemo Constructor
     */
    public SplitPaneDemo() {
        super();

        //<snip>Create horizontal SplitPane with day and night       
        day = new JLabel(createImageIcon("splitpane/day.jpg", getString("SplitPaneDemo.day")));
        night = new JLabel(createImageIcon("splitpane/night.jpg", getString("SplitPaneDemo.night")));
        
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, day, night);        
        //</snip>
        
        //<snip>Turn on continuous layout
        splitPane.setContinuousLayout(true);
        //</snip>
        
        //<snip>Turn on one-touch expansion
        splitPane.setOneTouchExpandable(true);
        //</snip>

        //<snip>Set divider location
        splitPane.setDividerLocation(200);
        //</snip>
        
        //<snip>Set minimum size for each child
        day.setMinimumSize(new Dimension(20, 20));
        night.setMinimumSize(new Dimension(20, 20));
        //</snip>

        getDemoPanel().add(splitPane, BorderLayout.CENTER);
        getDemoPanel().setBackground(Color.black);

        getDemoPanel().add(createSplitPaneControls(), BorderLayout.SOUTH);
    }

    /**
     * Creates controls to alter the JSplitPane.
     */
    protected JPanel createSplitPaneControls() {
        
        gridbag = new GridBagLayout();
        c = new GridBagConstraints();
        controlPanel = new JPanel(gridbag);
        
        //<snip>Create radio box to edit splitpane orientation
        Box box = Box.createHorizontalBox();
        ButtonGroup group = new ButtonGroup();
        
        OrientationListener orientationListener = new OrientationListener();
        
        JRadioButton button = new JRadioButton(getString("SplitPaneDemo.vert_split"));
        button.setActionCommand("vertical");
        button.addActionListener(orientationListener);
        group.add(button);
        box.add(button);

        button = new JRadioButton(getString("SplitPaneDemo.horz_split"));
        button.setActionCommand("horizontal");
        button.setSelected(true);
        button.addActionListener(orientationListener);
        group.add(button);
        box.add(button);
        //</snip>
        
        addToGridbag(box, 0, 0, 1, 1, 
                GridBagConstraints.NONE, GridBagConstraints.WEST);

        //<snip>Create checkbox to edit continuous layout
        JCheckBox checkBox = new JCheckBox(getString("SplitPaneDemo.cont_layout"));
        checkBox.setSelected(true);

        checkBox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                splitPane.setContinuousLayout(
                        ((JCheckBox) e.getSource()).isSelected());
            }
        });
        //</snip>
        
        c.gridy++;
        addToGridbag(checkBox, 0, 1, 1, 1, 
                GridBagConstraints.NONE, GridBagConstraints.WEST);     

        //<snip>Create checkbox to edit one-touch-expandable
        checkBox = new JCheckBox(getString("SplitPaneDemo.one_touch_expandable"));
        checkBox.setSelected(true);

        checkBox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                splitPane.setOneTouchExpandable(
                        ((JCheckBox) e.getSource()).isSelected());
            }
        });
        //</snip>
                
        addToGridbag(checkBox, 0, 2, 1, 1, 
                            GridBagConstraints.NONE, GridBagConstraints.WEST); 
        
        JSeparator separator = new JSeparator(JSeparator.VERTICAL);
        addToGridbag(separator, 1, 0, 1, 3, 
                          GridBagConstraints.VERTICAL, GridBagConstraints.CENTER);
      

        //<snip>Create spinner to edit divider size
        final JSpinner spinner = new JSpinner(
                new SpinnerNumberModel(splitPane.getDividerSize(), 5, 50, 2));
        
        spinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
                SpinnerNumberModel model = (SpinnerNumberModel)spinner.getModel();
                splitPane.setDividerSize(model.getNumber().intValue());       
            }
        });
        //</snip>
        
        JLabel label = new JLabel(getString("SplitPaneDemo.divider_size"));
        label.setLabelFor(spinner);
        addToGridbag(label, 2, 0, 1, 1,
                                GridBagConstraints.NONE, GridBagConstraints.EAST);
        addToGridbag(spinner, 3, 0, 1, 1,
                                GridBagConstraints.NONE, GridBagConstraints.WEST);

        //<snip>Create spinners to edit day & night's minimum sizes
        JSpinner minSizeSpinner = new JSpinner(
                new SpinnerNumberModel(day.getMinimumSize().width, 0, 300, 10));
        
        minSizeSpinner.addChangeListener(new MinimumSizeListener(day));
        //</snip>
                
        label = new JLabel(getString("SplitPaneDemo.first_component_min_size"));
        label.setLabelFor(minSizeSpinner);
        addToGridbag(label, 2, 1, 1, 1,
                                GridBagConstraints.NONE, GridBagConstraints.EAST);
        addToGridbag(minSizeSpinner, 3, 1, 1, 1,
                                GridBagConstraints.NONE, GridBagConstraints.WEST);
        
        //<snip>Create spinners to edit day & night's minimum sizes
        minSizeSpinner = new JSpinner(
                new SpinnerNumberModel(night.getMinimumSize().width, 0, 300, 10));
        
        minSizeSpinner.addChangeListener(new MinimumSizeListener(night));
        //</snip>

        label = new JLabel(getString("SplitPaneDemo.second_component_min_size"));
        label.setLabelFor(minSizeSpinner);
        addToGridbag(label, 2, 2, 1, 1,
                                GridBagConstraints.NONE, GridBagConstraints.EAST);
        addToGridbag(minSizeSpinner, 3, 2, 1, 1,
                                GridBagConstraints.NONE, GridBagConstraints.WEST);

        return controlPanel;
    }
    
    protected void addToGridbag(JComponent child, int gx, int gy, 
            int gwidth, int gheight, int fill, int anchor) {
        c.insets = insets;
        c.gridx = gx;
        c.gridy = gy;
        c.gridwidth = gwidth;
        c.gridheight = gheight;
        c.fill = fill;
        c.anchor = anchor;
        gridbag.addLayoutComponent(child, c);
        controlPanel.add(child);
        
    }
    //<snip>Create radio box to edit splitpane orientation   
    public class OrientationListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            splitPane.setOrientation(event.getActionCommand().equals("vertical")?
                JSplitPane.VERTICAL_SPLIT : JSplitPane.HORIZONTAL_SPLIT);
        }
        
    }
    //</snip>
    
    //<snip>Create spinners to edit day & night's minimum sizes
    public class MinimumSizeListener implements ChangeListener {
        private JComponent component;
        
        public MinimumSizeListener(JComponent c) {
            this.component = c;
        }
        
        public void stateChanged(ChangeEvent event) {
            JSpinner spinner = (JSpinner)event.getSource();
            SpinnerNumberModel model = (SpinnerNumberModel)spinner.getModel();
            int min = model.getNumber().intValue();
            component.setMinimumSize(new Dimension(min, min));       
            
        }
    }
    //</snip>
}
