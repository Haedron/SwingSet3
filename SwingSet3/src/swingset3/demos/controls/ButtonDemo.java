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

package swingset3.demos.controls;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import swingset3.DemoProperties;
import swingset3.demos.DemoBase;

/**
 * JButton, JRadioButton, JToggleButton, JCheckBox Demos
 *
 * @version 1.15 11/17/05
 * @author Jeff Dinkins
 */
@DemoProperties(
        value = "ToggleButtons Demo",
        category = "Controls",
        description = "Demonstrates JCheckBox & JRadioButton",
        sourceFiles = {
                "swingset3/demos/controls/ButtonDemo.java",
                "swingset3/demos/DemoBase.java",
                "swingset3/demos/controls/LayoutControlPanel.java",
                "swingset3/demos/controls/DirectionPanel.java"
                }
)
public class ButtonDemo extends DemoBase implements ChangeListener {

    private final JTabbedPane tab;

    private final JPanel buttonPanel = new JPanel();
    private final JPanel checkboxPanel = new JPanel();
    private final JPanel radioButtonPanel = new JPanel();
    private final JPanel toggleButtonPanel = new JPanel();

    private final List<JButton> buttons = new ArrayList<JButton>();
    private final List<JCheckBox> checkboxes = new ArrayList<JCheckBox>();
    private final List<JRadioButton> radiobuttons = new ArrayList<JRadioButton>();
    private final List<JToggleButton> togglebuttons = new ArrayList<JToggleButton>();

    private List<? extends JComponent> currentControls = buttons;

    private final EmptyBorder border5 = new EmptyBorder(5, 5, 5, 5);

    private ItemListener buttonDisplayListener = null;
    private ItemListener buttonPadListener = null;

    private final Insets insets0 = new Insets(0, 0, 0, 0);
    private final Insets insets10 = new Insets(10, 10, 10, 10);

    private final Border loweredBorder = new CompoundBorder(
            new SoftBevelBorder(SoftBevelBorder.LOWERED), new EmptyBorder(5, 5, 5, 5));

    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        ButtonDemo demo = new ButtonDemo();
        demo.mainImpl();
    }

    /**
     * ButtonDemo Constructor
     */
    public ButtonDemo() {
        tab = new JTabbedPane();
        tab.getModel().addChangeListener(this);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(tab);

        //addButtons();
        addRadioButtons();
        addCheckBoxes();
        //addToggleButtons();
        currentControls = checkboxes;
    }

    public void addButtons() {
        tab.addTab(getString("ButtonDemo.buttons"), buttonPanel);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBorder(border5);

        JPanel p1 = createVerticalPanel(true);
        p1.setAlignmentY(TOP_ALIGNMENT);
        buttonPanel.add(p1);

        // Text Buttons
        JPanel p2 = createHorizontalPanel(false);
        p1.add(p2);
        p2.setBorder(new CompoundBorder(new TitledBorder(null, getString("ButtonDemo.textbuttons"),
                TitledBorder.LEFT, TitledBorder.TOP), border5));

        JButton button1 = new JButton(getString("ButtonDemo.button1"));
        buttons.add(button1);
        p2.add(button1);
        p2.add(Box.createRigidArea(HGAP10));

        JButton button2 = new JButton(getString("ButtonDemo.button2"));
        buttons.add(button2);
        p2.add(button2);
        p2.add(Box.createRigidArea(HGAP10));

        JButton button3 = new JButton(getString("ButtonDemo.button3"));
        buttons.add(button3);
        p2.add(button3);

        // Image Buttons
        p1.add(Box.createRigidArea(VGAP30));
        JPanel p3 = createHorizontalPanel(false);
        p1.add(p3);
        p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));
        p3.setBorder(new TitledBorder(null, getString("ButtonDemo.imagebuttons"),
                TitledBorder.LEFT, TitledBorder.TOP));

        // home image button
        String description = getString("ButtonDemo.phone");
        JButton button = new JButton(createImageIcon("buttons/b1.gif", description));
        button.setPressedIcon(createImageIcon("buttons/b1p.gif", description));
        button.setRolloverIcon(createImageIcon("buttons/b1r.gif", description));
        button.setDisabledIcon(createImageIcon("buttons/b1d.gif", description));
        button.setMargin(new Insets(0, 0, 0, 0));
        p3.add(button);
        buttons.add(button);
        p3.add(Box.createRigidArea(HGAP10));

        // write image button
        description = getString("ButtonDemo.write");
        button = new JButton(createImageIcon("buttons/b2.gif", description));
        button.setPressedIcon(createImageIcon("buttons/b2p.gif", description));
        button.setRolloverIcon(createImageIcon("buttons/b2r.gif", description));
        button.setDisabledIcon(createImageIcon("buttons/b2d.gif", description));
        button.setMargin(new Insets(0, 0, 0, 0));
        p3.add(button);
        buttons.add(button);
        p3.add(Box.createRigidArea(HGAP10));

        // write image button
        description = getString("ButtonDemo.peace");
        button = new JButton(createImageIcon("buttons/b3.gif", description));
        button.setPressedIcon(createImageIcon("buttons/b3p.gif", description));
        button.setRolloverIcon(createImageIcon("buttons/b3r.gif", description));
        button.setDisabledIcon(createImageIcon("buttons/b3d.gif", description));
        button.setMargin(new Insets(0, 0, 0, 0));
        p3.add(button);
        buttons.add(button);

        p1.add(Box.createVerticalGlue());

        buttonPanel.add(Box.createHorizontalGlue());
        currentControls = buttons;
        buttonPanel.add(createControls());
    }

    public void addRadioButtons() {
        ButtonGroup group = new ButtonGroup();

        tab.addTab(getString("ButtonDemo.radiobuttons"), radioButtonPanel);
        radioButtonPanel.setLayout(new BoxLayout(radioButtonPanel, BoxLayout.X_AXIS));
        radioButtonPanel.setBorder(border5);

        JPanel p1 = createVerticalPanel(true);
        p1.setAlignmentY(TOP_ALIGNMENT);
        radioButtonPanel.add(p1);

        // Text Radio Buttons
        JPanel p2 = createHorizontalPanel(false);
        p1.add(p2);
        p2.setBorder(new CompoundBorder(
                new TitledBorder(
                        null, getString("ButtonDemo.textradiobuttons"),
                        TitledBorder.LEFT, TitledBorder.TOP), border5)
        );

        JRadioButton radio = (JRadioButton) p2.add(
                new JRadioButton(getString("ButtonDemo.radio1")));
        group.add(radio);
        radiobuttons.add(radio);
        p2.add(Box.createRigidArea(HGAP10));

        radio = (JRadioButton) p2.add(
                new JRadioButton(getString("ButtonDemo.radio2")));
        group.add(radio);
        radiobuttons.add(radio);
        p2.add(Box.createRigidArea(HGAP10));

        radio = (JRadioButton) p2.add(
                new JRadioButton(getString("ButtonDemo.radio3")));
        group.add(radio);
        radiobuttons.add(radio);

        // Image Radio Buttons
        group = new ButtonGroup();
        p1.add(Box.createRigidArea(VGAP30));
        JPanel p3 = createHorizontalPanel(false);
        p1.add(p3);
        p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));
        p3.setBorder(new TitledBorder(null, getString("ButtonDemo.imageradiobuttons"),
                TitledBorder.LEFT, TitledBorder.TOP));

        // image radio button 1
        String description = getString("ButtonDemo.customradio");
        String text = getString("ButtonDemo.radio1");
        radio = new JRadioButton(text, createImageIcon("buttons/rb.gif", description));
        radio.setPressedIcon(createImageIcon("buttons/rbp.gif", description));
        radio.setRolloverIcon(createImageIcon("buttons/rbr.gif", description));
        radio.setRolloverSelectedIcon(createImageIcon("buttons/rbrs.gif", description));
        radio.setSelectedIcon(createImageIcon("buttons/rbs.gif", description));
        radio.setMargin(new Insets(0, 0, 0, 0));
        group.add(radio);
        p3.add(radio);
        radiobuttons.add(radio);
        p3.add(Box.createRigidArea(HGAP20));

        // image radio button 2
        text = getString("ButtonDemo.radio2");
        radio = new JRadioButton(text, createImageIcon("buttons/rb.gif", description));
        radio.setPressedIcon(createImageIcon("buttons/rbp.gif", description));
        radio.setRolloverIcon(createImageIcon("buttons/rbr.gif", description));
        radio.setRolloverSelectedIcon(createImageIcon("buttons/rbrs.gif", description));
        radio.setSelectedIcon(createImageIcon("buttons/rbs.gif", description));
        radio.setMargin(new Insets(0, 0, 0, 0));
        group.add(radio);
        p3.add(radio);
        radiobuttons.add(radio);
        p3.add(Box.createRigidArea(HGAP20));

        // image radio button 3
        text = getString("ButtonDemo.radio3");
        radio = new JRadioButton(text, createImageIcon("buttons/rb.gif", description));
        radio.setPressedIcon(createImageIcon("buttons/rbp.gif", description));
        radio.setRolloverIcon(createImageIcon("buttons/rbr.gif", description));
        radio.setRolloverSelectedIcon(createImageIcon("buttons/rbrs.gif", description));
        radio.setSelectedIcon(createImageIcon("buttons/rbs.gif", description));
        radio.setMargin(new Insets(0, 0, 0, 0));
        group.add(radio);
        radiobuttons.add(radio);
        p3.add(radio);

        // verticaly glue fills out the rest of the box
        p1.add(Box.createVerticalGlue());

        radioButtonPanel.add(Box.createHorizontalGlue());
        currentControls = radiobuttons;
        radioButtonPanel.add(createControls());
    }


    public void addCheckBoxes() {
        tab.addTab(getString("ButtonDemo.checkboxes"), checkboxPanel);
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.X_AXIS));
        checkboxPanel.setBorder(border5);

        JPanel p1 = createVerticalPanel(true);
        p1.setAlignmentY(TOP_ALIGNMENT);
        checkboxPanel.add(p1);

        // Text Radio Buttons
        JPanel p2 = createHorizontalPanel(false);
        p1.add(p2);
        p2.setBorder(new CompoundBorder(
                new TitledBorder(
                        null, getString("ButtonDemo.textcheckboxes"),
                        TitledBorder.LEFT, TitledBorder.TOP), border5)
        );

        JCheckBox checkBox1 = new JCheckBox(getString("ButtonDemo.check1"));
        checkboxes.add(checkBox1);
        p2.add(checkBox1);
        p2.add(Box.createRigidArea(HGAP10));

        JCheckBox checkBox2 = new JCheckBox(getString("ButtonDemo.check2"));
        checkboxes.add(checkBox2);
        p2.add(checkBox2);
        p2.add(Box.createRigidArea(HGAP10));

        JCheckBox checkBox3 = new JCheckBox(getString("ButtonDemo.check3"));
        checkboxes.add(checkBox3);
        p2.add(checkBox3);

        // Image Radio Buttons
        p1.add(Box.createRigidArea(VGAP30));
        JPanel p3 = createHorizontalPanel(false);
        p1.add(p3);
        p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));
        p3.setBorder(new TitledBorder(null, getString("ButtonDemo.imagecheckboxes"),
                TitledBorder.LEFT, TitledBorder.TOP));

        // image checkbox 1
        String description = getString("ButtonDemo.customcheck");
        String text = getString("ButtonDemo.check1");
        JCheckBox check = new JCheckBox(text, createImageIcon("buttons/cb.gif", description));
        check.setRolloverIcon(createImageIcon("buttons/cbr.gif", description));
        check.setRolloverSelectedIcon(createImageIcon("buttons/cbrs.gif", description));
        check.setSelectedIcon(createImageIcon("buttons/cbs.gif", description));
        check.setMargin(new Insets(0, 0, 0, 0));
        p3.add(check);
        checkboxes.add(check);
        p3.add(Box.createRigidArea(HGAP20));

        // image checkbox 2
        text = getString("ButtonDemo.check2");
        check = new JCheckBox(text, createImageIcon("buttons/cb.gif", description));
        check.setRolloverIcon(createImageIcon("buttons/cbr.gif", description));
        check.setRolloverSelectedIcon(createImageIcon("buttons/cbrs.gif", description));
        check.setSelectedIcon(createImageIcon("buttons/cbs.gif", description));
        check.setMargin(new Insets(0, 0, 0, 0));
        p3.add(check);
        checkboxes.add(check);
        p3.add(Box.createRigidArea(HGAP20));

        // image checkbox 3
        text = getString("ButtonDemo.check3");
        check = new JCheckBox(text, createImageIcon("buttons/cb.gif", description));
        check.setRolloverIcon(createImageIcon("buttons/cbr.gif", description));
        check.setRolloverSelectedIcon(createImageIcon("buttons/cbrs.gif", description));
        check.setSelectedIcon(createImageIcon("buttons/cbs.gif", description));
        check.setMargin(new Insets(0, 0, 0, 0));
        p3.add(check);
        checkboxes.add(check);

        // verticaly glue fills out the rest of the box
        p1.add(Box.createVerticalGlue());

        checkboxPanel.add(Box.createHorizontalGlue());
        currentControls = checkboxes;
        checkboxPanel.add(createControls());
    }

    public void addToggleButtons() {
        tab.addTab(getString("ButtonDemo.togglebuttons"), toggleButtonPanel);
    }

    public JPanel createControls() {
        JPanel controls = new JPanel() {
            public Dimension getMaximumSize() {
                return new Dimension(300, super.getMaximumSize().height);
            }
        };
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        controls.setAlignmentY(TOP_ALIGNMENT);
        controls.setAlignmentX(LEFT_ALIGNMENT);

        JPanel buttonControls = createHorizontalPanel(true);
        buttonControls.setAlignmentY(TOP_ALIGNMENT);
        buttonControls.setAlignmentX(LEFT_ALIGNMENT);

        JPanel leftColumn = createVerticalPanel(false);
        leftColumn.setAlignmentX(LEFT_ALIGNMENT);
        leftColumn.setAlignmentY(TOP_ALIGNMENT);

        JPanel rightColumn = new LayoutControlPanel(this);

        buttonControls.add(leftColumn);
        buttonControls.add(Box.createRigidArea(HGAP20));
        buttonControls.add(rightColumn);
        buttonControls.add(Box.createRigidArea(HGAP20));

        controls.add(buttonControls);

        createListeners();

        // Display Options
        JLabel l = new JLabel(getString("ButtonDemo.controlpanel_label"));
        leftColumn.add(l);

        JCheckBox bordered = new JCheckBox(getString("ButtonDemo.paintborder"));
        bordered.setActionCommand("PaintBorder");
        bordered.setToolTipText(getString("ButtonDemo.paintborder_tooltip"));
        bordered.setMnemonic(getMnemonic("ButtonDemo.paintborder_mnemonic"));
        if (currentControls == buttons) {
            bordered.setSelected(true);
        }
        bordered.addItemListener(buttonDisplayListener);
        leftColumn.add(bordered);

        JCheckBox focused = new JCheckBox(getString("ButtonDemo.paintfocus"));
        focused.setActionCommand("PaintFocus");
        focused.setToolTipText(getString("ButtonDemo.paintfocus_tooltip"));
        focused.setMnemonic(getMnemonic("ButtonDemo.paintfocus_mnemonic"));
        focused.setSelected(true);
        focused.addItemListener(buttonDisplayListener);
        leftColumn.add(focused);

        JCheckBox enabled = new JCheckBox(getString("ButtonDemo.enabled"));
        enabled.setActionCommand("Enabled");
        enabled.setToolTipText(getString("ButtonDemo.enabled_tooltip"));
        enabled.setSelected(true);
        enabled.addItemListener(buttonDisplayListener);
        enabled.setMnemonic(getMnemonic("ButtonDemo.enabled_mnemonic"));
        leftColumn.add(enabled);

        JCheckBox filled = new JCheckBox(getString("ButtonDemo.contentfilled"));
        filled.setActionCommand("ContentFilled");
        filled.setToolTipText(getString("ButtonDemo.contentfilled_tooltip"));
        filled.setSelected(true);
        filled.addItemListener(buttonDisplayListener);
        filled.setMnemonic(getMnemonic("ButtonDemo.contentfilled_mnemonic"));
        leftColumn.add(filled);

        leftColumn.add(Box.createRigidArea(VGAP20));

        l = new JLabel(getString("ButtonDemo.padamount_label"));
        leftColumn.add(l);
        ButtonGroup group = new ButtonGroup();
        JRadioButton defaultPad = new JRadioButton(getString("ButtonDemo.default"));
        defaultPad.setToolTipText(getString("ButtonDemo.default_tooltip"));
        defaultPad.setMnemonic(getMnemonic("ButtonDemo.default_mnemonic"));
        defaultPad.addItemListener(buttonPadListener);
        group.add(defaultPad);
        defaultPad.setSelected(true);
        leftColumn.add(defaultPad);

        JRadioButton zeroPad = new JRadioButton(getString("ButtonDemo.zero"));
        zeroPad.setActionCommand("ZeroPad");
        zeroPad.setToolTipText(getString("ButtonDemo.zero_tooltip"));
        zeroPad.addItemListener(buttonPadListener);
        zeroPad.setMnemonic(getMnemonic("ButtonDemo.zero_mnemonic"));
        group.add(zeroPad);
        leftColumn.add(zeroPad);

        JRadioButton tenPad = new JRadioButton(getString("ButtonDemo.ten"));
        tenPad.setActionCommand("TenPad");
        tenPad.setMnemonic(getMnemonic("ButtonDemo.ten_mnemonic"));
        tenPad.setToolTipText(getString("ButtonDemo.ten_tooltip"));
        tenPad.addItemListener(buttonPadListener);
        group.add(tenPad);
        leftColumn.add(tenPad);

        leftColumn.add(Box.createRigidArea(VGAP20));
        return controls;
    }

    public void createListeners() {
        buttonDisplayListener = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                JCheckBox cb = (JCheckBox) e.getSource();
                String command = cb.getActionCommand();
                if (command == "Enabled") {
                    for (JComponent control : currentControls) {
                        control.setEnabled(cb.isSelected());
                        control.invalidate();
                    }
                } else if (command == "PaintBorder") {
                    if (currentControls.get(0) instanceof AbstractButton) {
                        for (JComponent control : currentControls) {
                            AbstractButton b = (AbstractButton) control;
                            b.setBorderPainted(cb.isSelected());
                            b.invalidate();
                        }
                    }
                } else if (command == "PaintFocus") {
                    if (currentControls.get(0) instanceof AbstractButton) {
                        for (JComponent control : currentControls) {
                            AbstractButton b = (AbstractButton) control;
                            b.setFocusPainted(cb.isSelected());
                            b.invalidate();
                        }
                    }
                } else if (command == "ContentFilled") {
                    if (currentControls.get(0) instanceof AbstractButton) {
                        for (JComponent control : currentControls) {
                            AbstractButton b = (AbstractButton) control;
                            b.setContentAreaFilled(cb.isSelected());
                            b.invalidate();
                        }
                    }
                }
                invalidate();
                validate();
                repaint();
            }
        };

        buttonPadListener = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                // *** pad = 0
                int pad = -1;
                JRadioButton rb = (JRadioButton) e.getSource();
                String command = rb.getActionCommand();
                if (command == "ZeroPad" && rb.isSelected()) {
                    pad = 0;
                } else if (command == "TenPad" && rb.isSelected()) {
                    pad = 10;
                }

                for (JComponent control : currentControls) {
                    AbstractButton b = (AbstractButton) control;
                    if (pad == -1) {
                        b.setMargin(null);
                    } else if (pad == 0) {
                        b.setMargin(insets0);
                    } else {
                        b.setMargin(insets10);
                    }
                }
                invalidate();
                validate();
                repaint();
            }
        };
    }

    public void stateChanged(ChangeEvent e) {
        SingleSelectionModel model = (SingleSelectionModel) e.getSource();
        if (model.getSelectedIndex() == 0) {
            currentControls = buttons;
        } else if (model.getSelectedIndex() == 1) {
            currentControls = radiobuttons;
        } else if (model.getSelectedIndex() == 2) {
            currentControls = checkboxes;
        } else {
            currentControls = togglebuttons;
        }
    }

    public List<? extends JComponent> getCurrentControls() {
        return currentControls;
    }
    
    private JPanel createHorizontalPanel(boolean threeD) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.setAlignmentY(TOP_ALIGNMENT);
        p.setAlignmentX(LEFT_ALIGNMENT);
        if(threeD) {
            p.setBorder(loweredBorder);
        }
        return p;
    }
    
    private JPanel createVerticalPanel(boolean threeD) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentY(TOP_ALIGNMENT);
        p.setAlignmentX(LEFT_ALIGNMENT);
        if(threeD) {
            p.setBorder(loweredBorder);
        }
        return p;
    }
}
