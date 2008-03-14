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
import javax.swing.JSplitPane;
import javax.swing.JTextField;
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
        
        day.setMinimumSize(new Dimension(20, 20));
        night.setMinimumSize(new Dimension(20, 20));

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
        
        // Create Orientation control
        Box box = Box.createHorizontalBox();
        ButtonGroup group = new ButtonGroup();
        
        JRadioButton button = new JRadioButton(getString("SplitPaneDemo.vert_split"));
        button.setMnemonic(getMnemonic("SplitPaneDemo.vert_split_mnemonic"));
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
            }
        });
        group.add(button);
        box.add(button);

        button = new JRadioButton(getString("SplitPaneDemo.horz_split"));
        button.setMnemonic(getMnemonic("SplitPaneDemo.horz_split_mnemonic"));
        button.setSelected(true);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
            }
        });
        group.add(button);
        box.add(button);
        addToGridbag(box, 0, 0, 1, 1, 
                GridBagConstraints.NONE, GridBagConstraints.WEST);

        // Create continuous layout checkbox
        JCheckBox checkBox = new JCheckBox(getString("SplitPaneDemo.cont_layout"));
        checkBox.setMnemonic(getMnemonic("SplitPaneDemo.cont_layout_mnemonic"));
        checkBox.setSelected(true);

        checkBox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                splitPane.setContinuousLayout(
                        ((JCheckBox) e.getSource()).isSelected());
            }
        });
        c.gridy++;
        addToGridbag(checkBox, 0, 1, 1, 1, 
                GridBagConstraints.NONE, GridBagConstraints.WEST);     

        // Create one-touch expandable checkbox
        checkBox = new JCheckBox(getString("SplitPaneDemo.one_touch_expandable"));
        checkBox.setMnemonic(getMnemonic("SplitPaneDemo.one_touch_expandable_mnemonic"));
        checkBox.setSelected(true);

        checkBox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                splitPane.setOneTouchExpandable(
                        ((JCheckBox) e.getSource()).isSelected());
            }
        });
        addToGridbag(checkBox, 0, 2, 1, 1, 
                            GridBagConstraints.NONE, GridBagConstraints.WEST); 
        
        JSeparator separator = new JSeparator(JSeparator.VERTICAL);
        addToGridbag(separator, 1, 0, 1, 3, 
                          GridBagConstraints.VERTICAL, GridBagConstraints.CENTER);
        
        fieldVerifier = new FieldVerifier();

        // Create divider size textfield
        JFormattedTextField divSize = new JFormattedTextField();
        divSize.setValue(new Integer(splitPane.getDividerSize()));
        //divSize.setInputVerifier(fieldVerifier);
        divSize.setColumns(3);
        divSize.getAccessibleContext().setAccessibleName(getString("SplitPaneDemo.divider_size"));
        divSize.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName().equals("value")) {
                    int minSize = (Integer)event.getNewValue();
                    if (minSize > 0) {
                        splitPane.setDividerSize(minSize);
                    } 
                }
            }
        });
        JLabel label = new JLabel(getString("SplitPaneDemo.divider_size"));
        label.setLabelFor(divSize);
        label.setDisplayedMnemonic(getMnemonic("SplitPaneDemo.divider_size_mnemonic"));
        addToGridbag(label, 2, 0, 1, 1,
                                GridBagConstraints.NONE, GridBagConstraints.EAST);
        addToGridbag(divSize, 3, 0, 1, 1,
                                GridBagConstraints.NONE, GridBagConstraints.WEST);

        // Create textfields to configure day & night's minimum sizes
        daySize = new JTextField(String.valueOf(day.getMinimumSize().width));
        daySize.setColumns(5);
        daySize.getAccessibleContext().setAccessibleName(getString("SplitPaneDemo.first_component_min_size"));
        daySize.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String value = ((JTextField) e.getSource()).getText();
                int newSize;

                try {
                    newSize = Integer.parseInt(value);
                } catch (Exception ex) {
                    newSize = -1;
                }
                if (newSize > 10) {
                    day.setMinimumSize(new Dimension(newSize, newSize));
                } else {
                    JOptionPane.showMessageDialog(splitPane,
                            getString("SplitPaneDemo.invalid_min_size") +
                                    getString("SplitPaneDemo.must_be_greater_than") + 10,
                            getString("SplitPaneDemo.error"),
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        label = new JLabel(getString("SplitPaneDemo.first_component_min_size"));
        label.setLabelFor(daySize);
        label.setDisplayedMnemonic(getMnemonic("SplitPaneDemo.first_component_min_size_mnemonic"));
        addToGridbag(label, 2, 1, 1, 1,
                                GridBagConstraints.NONE, GridBagConstraints.EAST);
        addToGridbag(daySize, 3, 1, 1, 1,
                                GridBagConstraints.NONE, GridBagConstraints.WEST);

        /* Create a text field that will change the preferred/minimum size
            of the moon component. */
        nightSize = new JTextField(String.valueOf(night.getMinimumSize().width));
        nightSize.setColumns(5);
        nightSize.getAccessibleContext().setAccessibleName(getString("SplitPaneDemo.second_component_min_size"));
        nightSize.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String value = ((JTextField) e.getSource()).getText();
                int newSize;

                try {
                    newSize = Integer.parseInt(value);
                } catch (Exception ex) {
                    newSize = -1;
                }
                if (newSize > 10) {
                    night.setMinimumSize(new Dimension(newSize, newSize));
                } else {
                    JOptionPane.showMessageDialog(splitPane,
                            getString("SplitPaneDemo.invalid_min_size") +
                                    getString("SplitPaneDemo.must_be_greater_than") + 10,
                            getString("SplitPaneDemo.error"),
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        label = new JLabel(getString("SplitPaneDemo.second_component_min_size"));
        label.setLabelFor(nightSize);
        label.setDisplayedMnemonic(getMnemonic("SplitPaneDemo.second_component_min_size_mnemonic"));
        addToGridbag(label, 2, 2, 1, 1,
                                GridBagConstraints.NONE, GridBagConstraints.EAST);
        addToGridbag(nightSize, 3, 2, 1, 1,
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
    
    public class FieldVerifier extends InputVerifier {
        public boolean verify(JComponent input) {
            JFormattedTextField ftf = (JFormattedTextField) input;
            AbstractFormatter formatter = ftf.getFormatter();
            if (formatter != null) {
                String text = ftf.getText();
                try {
                    Integer value = (Integer)formatter.stringToValue(text);
                    return value > 0;
                } catch (ParseException pe) {
                    return false;
                }
            }
            return true;
        }
    }
}
