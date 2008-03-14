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
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.SingleSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import swingset3.DemoProperties;
import swingset3.demos.DemoBase;

/**
 * JTabbedPane Demo
 *
 * @version 1.11 11/17/05
 * @author Jeff Dinkins
 */
@DemoProperties(
        value = "JTabbedPane Demo",
        category = "Containers",
        description = "Demonstrates JTabbedPane, a container which allows tabbed navigation of components",
        sourceFiles = {
                "swingset3/demos/containers/TabbedPaneDemo.java",
                "swingset3/demos/DemoBase.java"
                }
)
public class TabbedPaneDemo extends DemoBase implements ActionListener {
    private final HeadSpin spin;

    private final JTabbedPane tabbedpane;

    private final ButtonGroup group;

    private final JRadioButton top;
    private final JRadioButton bottom;
    private final JRadioButton left;
    private final JRadioButton right;

    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        TabbedPaneDemo demo = new TabbedPaneDemo();
        demo.mainImpl();
    }

    /**
     * TabbedPaneDemo Constructor
     */
    public TabbedPaneDemo() {
        super();

        // create tab position controls
        JPanel tabControls = new JPanel();
        tabControls.add(new JLabel(getString("TabbedPaneDemo.label")));
        top = (JRadioButton) tabControls.add(new JRadioButton(getString("TabbedPaneDemo.top")));
        left = (JRadioButton) tabControls.add(new JRadioButton(getString("TabbedPaneDemo.left")));
        bottom = (JRadioButton) tabControls.add(new JRadioButton(getString("TabbedPaneDemo.bottom")));
        right = (JRadioButton) tabControls.add(new JRadioButton(getString("TabbedPaneDemo.right")));
        getDemoPanel().add(tabControls, BorderLayout.NORTH);

        group = new ButtonGroup();
        group.add(top);
        group.add(bottom);
        group.add(left);
        group.add(right);

        top.setSelected(true);

        top.addActionListener(this);
        bottom.addActionListener(this);
        left.addActionListener(this);
        right.addActionListener(this);

        // create tab 
        tabbedpane = new JTabbedPane();
        getDemoPanel().add(tabbedpane, BorderLayout.CENTER);

        String name = getString("TabbedPaneDemo.camille");
        JLabel pix = new JLabel(createImageIcon("tabbedpane/camille.jpg", name));
        tabbedpane.add(name, pix);

        name = getString("TabbedPaneDemo.ewan");
        pix = new JLabel(createImageIcon("tabbedpane/ewan.jpg", name));
        tabbedpane.add(name, pix);

        name = getString("TabbedPaneDemo.hania");
        pix = new JLabel(createImageIcon("tabbedpane/hania.jpg", name));
        tabbedpane.add(name, pix);

        name = getString("TabbedPaneDemo.bounce");
        spin = new HeadSpin();
        tabbedpane.add(name, spin);

        tabbedpane.getModel().addChangeListener(
                new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        SingleSelectionModel model = (SingleSelectionModel) e.getSource();
                        if (model.getSelectedIndex() == tabbedpane.getTabCount() - 1) {
                            spin.go();
                        }
                    }
                }
        );
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == top) {
            tabbedpane.setTabPlacement(JTabbedPane.TOP);
        } else if (e.getSource() == left) {
            tabbedpane.setTabPlacement(JTabbedPane.LEFT);
        } else if (e.getSource() == bottom) {
            tabbedpane.setTabPlacement(JTabbedPane.BOTTOM);
        } else if (e.getSource() == right) {
            tabbedpane.setTabPlacement(JTabbedPane.RIGHT);
        }
    }

    class HeadSpin extends JComponent implements ActionListener {
        private javax.swing.Timer animator;

        private final ImageIcon[] icon = new ImageIcon[6];

        private final static int numImages = 6;

        private final double[] x = new double[numImages];
        private final double[] y = new double[numImages];

        private final int[] xh = new int[numImages];
        private final int[] yh = new int[numImages];

        private final double[] scale = new double[numImages];

        private final Random rand = new Random();

        public HeadSpin() {
            setBackground(Color.black);
            icon[0] = createImageIcon("tabbedpane/ewan.gif", getString("TabbedPaneDemo.ewan"));
            icon[1] = createImageIcon("tabbedpane/stephen.gif", getString("TabbedPaneDemo.stephen"));
            icon[2] = createImageIcon("tabbedpane/david.gif", getString("TabbedPaneDemo.david"));
            icon[3] = createImageIcon("tabbedpane/matthew.gif", getString("TabbedPaneDemo.matthew"));
            icon[4] = createImageIcon("tabbedpane/blake.gif", getString("TabbedPaneDemo.blake"));
            icon[5] = createImageIcon("tabbedpane/brooke.gif", getString("TabbedPaneDemo.brooke"));

            /*
             for(int i = 0; i < 6; i++) {
                 x[i] = (double) rand.nextInt(500);
                 y[i] = (double) rand.nextInt(500);
             }
             */
        }

        public void go() {
            animator = new javax.swing.Timer(22 + 22 + 22, this);
            animator.start();
        }

        public void paint(Graphics g) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());

            for (int i = 0; i < numImages; i++) {
                if (x[i] > 3 * i) {
                    nudge(i);
                    squish(g, icon[i], xh[i], yh[i], scale[i]);
                } else {
                    x[i] += .05;
                    y[i] += .05;
                }
            }
        }

        public void nudge(int i) {
            x[i] += (double) rand.nextInt(1000) / 8756;
            y[i] += (double) rand.nextInt(1000) / 5432;
            int tmpScale = (int) (Math.abs(Math.sin(x[i])) * 10);
            scale[i] = (double) tmpScale / 10;
            int nudgeX = (int) (((double) getWidth() / 2) * .8);
            int nudgeY = (int) (((double) getHeight() / 2) * .60);
            xh[i] = (int) (Math.sin(x[i]) * nudgeX) + nudgeX;
            yh[i] = (int) (Math.sin(y[i]) * nudgeY) + nudgeY;
        }

        public void squish(Graphics g, ImageIcon icon, int x, int y, double scale) {
            if (isVisible()) {
                g.drawImage(icon.getImage(), x, y,
                        (int) (icon.getIconWidth() * scale),
                        (int) (icon.getIconHeight() * scale),
                        this);
            }
        }

        public void actionPerformed(ActionEvent e) {
            if (isVisible()) {
                repaint();
            } else {
                animator.stop();
            }
        }
    }
}

