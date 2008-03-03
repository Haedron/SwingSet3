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
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import swingset3.DemoProperties;
import swingset3.demos.DemoBase;


/**
 * Internal Frames Demo
 *
 * @version 1.16 11/17/05
 * @author Jeff Dinkins
 */
@DemoProperties(
        value = "JInternalFrame Demo",
        category = "Containers",
        description = "Demonstrates JInternalFrame, a frame which can be embedded within another container to" +
                "implement an MDI style interface.",
        sourceFiles = {
                "swingset3/demos/containers/InternalFrameDemo.java",
                "swingset3/demos/DemoBase.java"
                }
)
public class InternalFrameDemo extends DemoBase {
    private static final int FRAME0_X = 10;
    private static final int FRAME0_Y = 230;

    private static final int FRAME0_WIDTH = 300;
    private static final int FRAME0_HEIGHT = 230;

    private static final int FRAME_WIDTH = 225;
    private static final int FRAME_HEIGHT = 150;

    private static final int PALETTE_X = 320;
    private static final int PALETTE_Y = 20;

    private static final int PALETTE_WIDTH = 250;
    private static final int PALETTE_HEIGHT = 250;

    private int windowCount = 0;
    private JDesktopPane desktop = null;

    private final ImageIcon icon1;
    private final ImageIcon icon2;
    private final ImageIcon icon3;
    private final ImageIcon icon4;
    private final ImageIcon smIcon1;
    private final ImageIcon smIcon2;
    private final ImageIcon smIcon3;
    private final ImageIcon smIcon4;

    private final Integer FIRST_FRAME_LAYER = new Integer(1);
    private final Integer DEMO_FRAME_LAYER = new Integer(2);
    private final Integer PALETTE_LAYER = new Integer(3);

    private JCheckBox windowResizable = null;
    private JCheckBox windowClosable = null;
    private JCheckBox windowIconifiable = null;
    private JCheckBox windowMaximizable = null;

    private JTextField windowTitleField = null;
    private JLabel windowTitleLabel = null;


    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        InternalFrameDemo demo = new InternalFrameDemo();
        demo.mainImpl();
    }

    /**
     * InternalFrameDemo Constructor
     */
    public InternalFrameDemo() {
        super();

        // preload all the icons we need for this demo
        icon1 = createImageIcon("ImageClub/misc/fish.gif", getString("InternalFrameDemo.fish"));
        icon2 = createImageIcon("ImageClub/misc/moon.gif", getString("InternalFrameDemo.moon"));
        icon3 = createImageIcon("ImageClub/misc/sun.gif", getString("InternalFrameDemo.sun"));
        icon4 = createImageIcon("ImageClub/misc/cab.gif", getString("InternalFrameDemo.cab"));

        smIcon1 = createImageIcon("ImageClub/misc/fish_small.gif", getString("InternalFrameDemo.fish"));
        smIcon2 = createImageIcon("ImageClub/misc/moon_small.gif", getString("InternalFrameDemo.moon"));
        smIcon3 = createImageIcon("ImageClub/misc/sun_small.gif", getString("InternalFrameDemo.sun"));
        smIcon4 = createImageIcon("ImageClub/misc/cab_small.gif", getString("InternalFrameDemo.cab"));

        // Create the desktop pane
        desktop = new JDesktopPane();
        getDemoPanel().add(desktop, BorderLayout.CENTER);

        // Create the "frame maker" palette
        createInternalFramePalette();

        // Create an initial internal frame to show
        JInternalFrame frame1 = createInternalFrame(icon1, FIRST_FRAME_LAYER, 1, 1);
        frame1.setBounds(FRAME0_X, FRAME0_Y, FRAME0_WIDTH, FRAME0_HEIGHT);

        // Create more starter windows
        createInternalFrame(icon3, DEMO_FRAME_LAYER, FRAME_WIDTH, FRAME_HEIGHT);
        createInternalFrame(icon4, DEMO_FRAME_LAYER, FRAME_WIDTH, FRAME_HEIGHT);
        createInternalFrame(icon2, DEMO_FRAME_LAYER, FRAME_WIDTH, FRAME_HEIGHT);
    }


    /**
     * Create an internal frame and add a scrollable imageicon to it
     */
    public JInternalFrame createInternalFrame(Icon icon, Integer layer, int width, int height) {
        JInternalFrame jif = new JInternalFrame();

        if (!windowTitleField.getText().equals(getString("InternalFrameDemo.frame_label"))) {
            jif.setTitle(windowTitleField.getText() + "  ");
        } else {
            jif = new JInternalFrame(getString("InternalFrameDemo.frame_label") + " " + windowCount + "  ");
        }

        // set properties
        jif.setClosable(windowClosable.isSelected());
        jif.setMaximizable(windowMaximizable.isSelected());
        jif.setIconifiable(windowIconifiable.isSelected());
        jif.setResizable(windowResizable.isSelected());

        jif.setBounds(20 * (windowCount % 10), 20 * (windowCount % 10), width, height);
        jif.setContentPane(new ImageScroller(icon));

        windowCount++;

        desktop.add(jif, layer);

        // Set this internal frame to be selected

        try {
            jif.setSelected(true);
        } catch (java.beans.PropertyVetoException e2) {
        }

        jif.show();

        return jif;
    }

    public JInternalFrame createInternalFramePalette() {
        JInternalFrame palette = new JInternalFrame(
                getString("InternalFrameDemo.palette_label")
        );
        palette.putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
        palette.getContentPane().setLayout(new BorderLayout());
        palette.setBounds(PALETTE_X, PALETTE_Y, PALETTE_WIDTH, PALETTE_HEIGHT);
        palette.setResizable(true);
        palette.setIconifiable(true);
        desktop.add(palette, PALETTE_LAYER);

        // *************************************
        // * Create create frame maker buttons *
        // *************************************
        JButton b1 = new JButton(smIcon1);
        JButton b2 = new JButton(smIcon2);
        JButton b3 = new JButton(smIcon3);
        JButton b4 = new JButton(smIcon4);

        // add frame maker actions
        b1.addActionListener(new ShowFrameAction(this, icon1));
        b2.addActionListener(new ShowFrameAction(this, icon2));
        b3.addActionListener(new ShowFrameAction(this, icon3));
        b4.addActionListener(new ShowFrameAction(this, icon4));

        // add frame maker buttons to panel
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JPanel buttons1 = new JPanel();
        buttons1.setLayout(new BoxLayout(buttons1, BoxLayout.X_AXIS));

        JPanel buttons2 = new JPanel();
        buttons2.setLayout(new BoxLayout(buttons2, BoxLayout.X_AXIS));

        buttons1.add(b1);
        buttons1.add(Box.createRigidArea(HGAP15));
        buttons1.add(b2);

        buttons2.add(b3);
        buttons2.add(Box.createRigidArea(HGAP15));
        buttons2.add(b4);

        p.add(Box.createRigidArea(VGAP10));
        p.add(buttons1);
        p.add(Box.createRigidArea(VGAP15));
        p.add(buttons2);
        p.add(Box.createRigidArea(VGAP10));

        palette.getContentPane().add(p, BorderLayout.NORTH);

        // ************************************
        // * Create frame property checkboxes *
        // ************************************
        p = new JPanel() {
            final Insets insets = new Insets(10, 15, 10, 5);

            public Insets getInsets() {
                return insets;
            }
        };
        p.setLayout(new GridLayout(1, 2));


        Box box = new Box(BoxLayout.Y_AXIS);
        windowResizable = new JCheckBox(getString("InternalFrameDemo.resizable_label"), true);
        windowIconifiable = new JCheckBox(getString("InternalFrameDemo.iconifiable_label"), true);

        box.add(Box.createGlue());
        box.add(windowResizable);
        box.add(windowIconifiable);
        box.add(Box.createGlue());
        p.add(box);

        box = new Box(BoxLayout.Y_AXIS);
        windowClosable = new JCheckBox(getString("InternalFrameDemo.closable_label"), true);
        windowMaximizable = new JCheckBox(getString("InternalFrameDemo.maximizable_label"), true);

        box.add(Box.createGlue());
        box.add(windowClosable);
        box.add(windowMaximizable);
        box.add(Box.createGlue());
        p.add(box);

        palette.getContentPane().add(p, BorderLayout.CENTER);

        // ************************************
        // *   Create Frame title textfield   *
        // ************************************
        p = new JPanel() {
            final Insets insets = new Insets(0, 0, 10, 0);

            public Insets getInsets() {
                return insets;
            }
        };

        windowTitleField = new JTextField(getString("InternalFrameDemo.frame_label"));
        windowTitleLabel = new JLabel(getString("InternalFrameDemo.title_text_field_label"));

        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(Box.createRigidArea(HGAP5));
        p.add(windowTitleLabel, BorderLayout.WEST);
        p.add(Box.createRigidArea(HGAP5));
        p.add(windowTitleField, BorderLayout.CENTER);
        p.add(Box.createRigidArea(HGAP5));

        palette.getContentPane().add(p, BorderLayout.SOUTH);

        palette.show();

        return palette;
    }


    class ShowFrameAction extends AbstractAction {
        final InternalFrameDemo demo;
        final Icon icon;

        public ShowFrameAction(InternalFrameDemo demo, Icon icon) {
            this.demo = demo;
            this.icon = icon;
        }

        public void actionPerformed(ActionEvent e) {
            demo.createInternalFrame(icon,
                    getDemoFrameLayer(),
                    getFrameWidth(),
                    getFrameHeight()
            );
        }
    }

    public int getFrameWidth() {
        return FRAME_WIDTH;
    }

    public int getFrameHeight() {
        return FRAME_HEIGHT;
    }

    public Integer getDemoFrameLayer() {
        return DEMO_FRAME_LAYER;
    }

    private static class ImageScroller extends JScrollPane {

        public ImageScroller(Icon icon) {
            super();
            JPanel p = new JPanel();
            p.setBackground(Color.white);
            p.setLayout(new BorderLayout());

            p.add(new JLabel(icon), BorderLayout.CENTER);

            getViewport().add(p);
            getHorizontalScrollBar().setUnitIncrement(10);
            getVerticalScrollBar().setUnitIncrement(10);
        }

        public Dimension getMinimumSize() {
            return new Dimension(25, 25);
        }

    }
}
