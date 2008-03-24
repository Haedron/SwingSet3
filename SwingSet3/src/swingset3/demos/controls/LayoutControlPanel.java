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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.*;

import swingset3.demos.DemoBase;
import swingset3.demos.ResourceManager;

/*
 * The LayoutControlPanel contains controls for setting an 
 * AbstractButton's horizontal and vertical text position and 
 * horizontal and vertical alignment.
 */

public class LayoutControlPanel extends JPanel implements SwingConstants {

    private boolean absolutePositions;
    private DirectionPanel textPosition = null;
    private DirectionPanel labelAlignment = null;
    private ButtonDemo demo = null;

    // private ComponentOrientChanger componentOrientChanger = null;

    LayoutControlPanel(ButtonDemo demo, ResourceManager resourceManager) {
        this.demo = demo;

        // this.componentOrientationChanger = componentOrientationChanger;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(LEFT_ALIGNMENT);
        setAlignmentY(TOP_ALIGNMENT);

        JLabel l;

        // If SwingSet has a ComponentOrientationChanger, then include control
        // for choosing between absolute and relative positioning.  This will
        // only happen when we're running on JDK 1.2 or above.
        //
        // if(componentOrientationChanger != null ) {
        //     l = new JLabel("Positioning:");
        //     add(l);
        //
        //    ButtonGroup group = new ButtonGroup();
        //    PositioningListener positioningListener = new PositioningListener();
        //    JRadioButton absolutePos = new JRadioButton("Absolute");
        //    absolutePos.setMnemonic('a');
        //    absolutePos.setToolTipText("Text/Content positioning is independant of line direction");
        //    group.add(absolutePos);
        //    absolutePos.addItemListener(positioningListener);
        //    add(absolutePos);
        //
        //    JRadioButton relativePos = new JRadioButton("Relative");
        //    relativePos.setMnemonic('r');
        //    relativePos.setToolTipText("Text/Content positioning depends on line direction.");
        //    group.add(relativePos);
        //    relativePos.addItemListener(positioningListener);
        //    add(relativePos);
        //
        //    add(Box.createRigidArea(demo.VGAP20));
        //
        //    absolutePositions = false;
        //    relativePos.setSelected(true);
        //
        //    componentOrientationChanger.addActionListener( new OrientationChangeListener() );
        //} else {
        absolutePositions = true;
        //}

        textPosition = new DirectionPanel(true, "E", new TextPositionListener());
        labelAlignment = new DirectionPanel(true, "C", new LabelAlignmentListener());

        // Make sure the controls' text position and label alignment match
        // the initial value of the associated direction panel.
        for (JComponent control : demo.getCurrentControls()) {
            setPosition(control, RIGHT, CENTER);
            setAlignment(control, CENTER, CENTER);
        }

        l = new JLabel(resourceManager.getString("LayoutControlPanel.textposition_label"));
        add(l);
        add(textPosition);

        add(Box.createRigidArea(DemoBase.VGAP20));

        l = new JLabel(resourceManager.getString("LayoutControlPanel.contentalignment_label"));
        add(l);
        add(labelAlignment);

        add(Box.createGlue());
    }


    private class OrientationChangeListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (!e.getActionCommand().equals("OrientationChanged")) {
                return;
            }
            if (absolutePositions) {
                return;
            }

            String currentTextPosition = textPosition.getSelection();
            if (currentTextPosition.equals("NW"))
                textPosition.setSelection("NE");
            else if (currentTextPosition.equals("NE"))
                textPosition.setSelection("NW");
            else if (currentTextPosition.equals("E"))
                textPosition.setSelection("W");
            else if (currentTextPosition.equals("W"))
                textPosition.setSelection("E");
            else if (currentTextPosition.equals("SE"))
                textPosition.setSelection("SW");
            else if (currentTextPosition.equals("SW"))
                textPosition.setSelection("SE");

            String currentLabelAlignment = labelAlignment.getSelection();
            if (currentLabelAlignment.equals("NW"))
                labelAlignment.setSelection("NE");
            else if (currentLabelAlignment.equals("NE"))
                labelAlignment.setSelection("NW");
            else if (currentLabelAlignment.equals("E"))
                labelAlignment.setSelection("W");
            else if (currentLabelAlignment.equals("W"))
                labelAlignment.setSelection("E");
            else if (currentLabelAlignment.equals("SE"))
                labelAlignment.setSelection("SW");
            else if (currentLabelAlignment.equals("SW"))
                labelAlignment.setSelection("SE");
        }
    }

    private class PositioningListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {
            JRadioButton rb = (JRadioButton) e.getSource();
            if (rb.getText().equals("Absolute") && rb.isSelected()) {
                absolutePositions = true;
            } else if (rb.getText().equals("Relative") && rb.isSelected()) {
                absolutePositions = false;
            }

            for (JComponent control : demo.getCurrentControls()) {
                int hPos, vPos, hAlign, vAlign;
                if (control instanceof AbstractButton) {
                    hPos = ((AbstractButton) control).getHorizontalTextPosition();
                    vPos = ((AbstractButton) control).getVerticalTextPosition();
                    hAlign = ((AbstractButton) control).getHorizontalAlignment();
                    vAlign = ((AbstractButton) control).getVerticalAlignment();
                } else if (control instanceof JLabel) {
                    hPos = ((JLabel) control).getHorizontalTextPosition();
                    vPos = ((JLabel) control).getVerticalTextPosition();
                    hAlign = ((JLabel) control).getHorizontalAlignment();
                    vAlign = ((JLabel) control).getVerticalAlignment();
                } else {
                    continue;
                }
                setPosition(control, hPos, vPos);
                setAlignment(control, hAlign, vAlign);
            }

            demo.invalidate();
            demo.validate();
            demo.repaint();
        }
    }

    // Text Position Listener
    private class TextPositionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JRadioButton rb = (JRadioButton) e.getSource();
            if (!rb.isSelected()) {
                return;
            }
            String cmd = rb.getActionCommand();
            int hPos, vPos;
            if (cmd.equals("NW")) {
                hPos = LEFT;
                vPos = TOP;
            } else if (cmd.equals("N")) {
                hPos = CENTER;
                vPos = TOP;
            } else if (cmd.equals("NE")) {
                hPos = RIGHT;
                vPos = TOP;
            } else if (cmd.equals("W")) {
                hPos = LEFT;
                vPos = CENTER;
            } else if (cmd.equals("C")) {
                hPos = CENTER;
                vPos = CENTER;
            } else if (cmd.equals("E")) {
                hPos = RIGHT;
                vPos = CENTER;
            } else if (cmd.equals("SW")) {
                hPos = LEFT;
                vPos = BOTTOM;
            } else if (cmd.equals("S")) {
                hPos = CENTER;
                vPos = BOTTOM;
            } else /*if(cmd.equals("SE"))*/ {
                hPos = RIGHT;
                vPos = BOTTOM;
            }
            for (JComponent control : demo.getCurrentControls()) {
                setPosition(control, hPos, vPos);
            }
            demo.invalidate();
            demo.validate();
            demo.repaint();
        }
    }

    // Label Alignment Listener
    private class LabelAlignmentListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JRadioButton rb = (JRadioButton) e.getSource();
            if (!rb.isSelected()) {
                return;
            }
            String cmd = rb.getActionCommand();
            int hPos, vPos;
            if (cmd.equals("NW")) {
                hPos = LEFT;
                vPos = TOP;
            } else if (cmd.equals("N")) {
                hPos = CENTER;
                vPos = TOP;
            } else if (cmd.equals("NE")) {
                hPos = RIGHT;
                vPos = TOP;
            } else if (cmd.equals("W")) {
                hPos = LEFT;
                vPos = CENTER;
            } else if (cmd.equals("C")) {
                hPos = CENTER;
                vPos = CENTER;
            } else if (cmd.equals("E")) {
                hPos = RIGHT;
                vPos = CENTER;
            } else if (cmd.equals("SW")) {
                hPos = LEFT;
                vPos = BOTTOM;
            } else if (cmd.equals("S")) {
                hPos = CENTER;
                vPos = BOTTOM;
            } else /*if(cmd.equals("SE"))*/ {
                hPos = RIGHT;
                vPos = BOTTOM;
            }
            for (JComponent control : demo.getCurrentControls()) {
                setAlignment(control, hPos, vPos);
                control.invalidate();
            }
            demo.invalidate();
            demo.validate();
            demo.repaint();
        }
    }

    // Position
    void setPosition(Component c, int hPos, int vPos) {
        boolean ltr = c.getComponentOrientation().isLeftToRight();
        if (absolutePositions) {
            if (hPos == LEADING) {
                hPos = ltr ? LEFT : RIGHT;
            } else if (hPos == TRAILING) {
                hPos = ltr ? RIGHT : LEFT;
            }
        } else {
            if (hPos == LEFT) {
                hPos = ltr ? LEADING : TRAILING;
            } else if (hPos == RIGHT) {
                hPos = ltr ? TRAILING : LEADING;
            }
        }
        if (c instanceof AbstractButton) {
            AbstractButton x = (AbstractButton) c;
            x.setHorizontalTextPosition(hPos);
            x.setVerticalTextPosition(vPos);
        } else if (c instanceof JLabel) {
            JLabel x = (JLabel) c;
            x.setHorizontalTextPosition(hPos);
            x.setVerticalTextPosition(vPos);
        }
    }

    void setAlignment(Component c, int hPos, int vPos) {
        boolean ltr = c.getComponentOrientation().isLeftToRight();
        if (absolutePositions) {
            if (hPos == LEADING) {
                hPos = ltr ? LEFT : RIGHT;
            } else if (hPos == TRAILING) {
                hPos = ltr ? RIGHT : LEFT;
            }
        } else {
            if (hPos == LEFT) {
                hPos = ltr ? LEADING : TRAILING;
            } else if (hPos == RIGHT) {
                hPos = ltr ? TRAILING : LEADING;
            }
        }
        if (c instanceof AbstractButton) {
            AbstractButton x = (AbstractButton) c;
            x.setHorizontalAlignment(hPos);
            x.setVerticalAlignment(vPos);
        } else if (c instanceof JLabel) {
            JLabel x = (JLabel) c;
            x.setHorizontalAlignment(hPos);
            x.setVerticalAlignment(vPos);
        }
    }
}
