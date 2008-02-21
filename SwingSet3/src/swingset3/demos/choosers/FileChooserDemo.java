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

package swingset3.demos.choosers;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Hashtable;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import swingset3.DemoProperties;
import swingset3.demos.DemoBase;

/**
 * JFileChooserDemo
 *
 * @author Jeff Dinkins
 * @version 1.18 02/03/06
 */
@DemoProperties(
        value = "JFileChooser Demo",
        category = "Choosers",
        description = "Demonstrates JFileChooser, a component which allows the user to open and save files.",
        sourceFiles = {
                "swingset3/demos/choosers/FileChooserDemo.java",
                "swingset3/demos/choosers/ExampleFileView.java",
                "swingset3/demos/choosers/JGridPanel.java",
                "swingset3/demos/DemoBase.java"
                }
)
public class FileChooserDemo extends DemoBase {
    /*
      todo: fix buttons images
     */
    private enum State {
        EMPTY,
        FILE_SELECTING,
        IMAGE_LOADED,
        IMAGE_CHANGED
    }

    private static final int MIN_FILTER_ID = 0;

    private static final int MAX_FILTER_ID = 7;

    private static final String[] FILTER_NAMES = {
            "FileChooserDemo.filter.blur",
            "FileChooserDemo.filter.edge",
            "FileChooserDemo.filter.sharpen",
            "FileChooserDemo.filter.darken",
            "FileChooserDemo.filter.brighten",
            "FileChooserDemo.filter.lesscontrast",
            "FileChooserDemo.filter.morecontrast",
            "FileChooserDemo.filter.gray"
    };

    private static final BufferedImageOp[] FILTER_OPERATIONS = {
            new ConvolveOp(new Kernel(3, 3,
                    new float[]{.1111f, .1111f, .1111f, .1111f, .1111f, .1111f, .1111f, .1111f, .1111f}),
                    ConvolveOp.EDGE_NO_OP, null),
            new ConvolveOp(new Kernel(3, 3,
                    new float[]{0.0f, -1.0f, 0.0f, -1.0f, 4.f, -1.0f, 0.0f, -1.0f, 0.0f}),
                    ConvolveOp.EDGE_NO_OP, null),
            new ConvolveOp(new Kernel(3, 3,
                    new float[]{0.0f, -1.0f, 0.0f, -1.0f, 5.f, -1.0f, 0.0f, -1.0f, 0.0f}),
                    ConvolveOp.EDGE_NO_OP, null),
            new RescaleOp(1, -5.0f, null),
            new RescaleOp(1, 5.0f, null),
            new RescaleOp(0.9f, 0, null),
            new RescaleOp(1.1f, 0, null),
            new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null)
    };

    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        FileChooserDemo demo = new FileChooserDemo();

        demo.mainImpl();
    }

    /**
     * FileChooserDemo Constructor
     */
    public FileChooserDemo() {
        super();

        add(new ImageEditor());
    }

    private class ImageEditor extends JGridPanel {
        private final JLabel lbImage = new JLabel(getString("FileChooserDemo.image.text"), JLabel.CENTER);

        private final JScrollPane pnImage = new JScrollPane(lbImage);

        private final JButton btnSelect = new JButton(getString("FileChooserDemo.select.text"));

        private final JComboBox cbFilters = new JComboBox();

        private final JButton btnApplyFilter = createButton("filechooser/apply.png", "FileChooserDemo.applyfilter.tooltip");

        private final JButton btnRotateLeft = createButton("filechooser/rotateleft.png", "FileChooserDemo.rotateleft.tooltip");

        private final JButton btnRotateRight = createButton("filechooser/rotateright.png", "FileChooserDemo.rotateright.tooltip");

        private final JButton btnFlipHorizontal = createButton("filechooser/fliphor.png", "FileChooserDemo.fliphorizontal.tooltip");

        private final JButton btnFlipVertical = createButton("filechooser/flipvert.png", "FileChooserDemo.flipvertical.tooltip");

        private final JButton btnApply = new JButton(getString("FileChooserDemo.apply.text"));

        private final JButton btnCancel = new JButton(getString("FileChooserDemo.cancel.text"));

        private final JFileChooser chooser = new JFileChooser();

        private State state;

        private File file;

        private BufferedImage image;

        private ImageEditor() {
            super(1, 0, 0);

            chooser.setControlButtonsAreShown(false);

            chooser.addChoosableFileFilter(new FileNameExtensionFilter("JPEG images", "jpg"));

            FileNameExtensionFilter filter = new FileNameExtensionFilter("All supported images",
                    ImageIO.getWriterFormatNames());

            chooser.addChoosableFileFilter(filter);
            chooser.setFileFilter(filter);

            for (int i = MIN_FILTER_ID; i <= MAX_FILTER_ID; i++) {
                cbFilters.addItem(new FilterItem(i, getString(FILTER_NAMES[i])));
            }

            JGridPanel pnButtons = new JGridPanel(12, 1);

            Insets smallInsets = new Insets(0, 2, 0, 0);
            
            pnButtons.cell(btnSelect).
                    cell().
                    cell(cbFilters).
                    cell(btnApplyFilter, smallInsets).
                    cell().
                    cell(btnRotateLeft).
                    cell(btnRotateRight, smallInsets).
                    cell(btnFlipHorizontal, smallInsets).
                    cell(btnFlipVertical, smallInsets).
                    cell().
                    cell(btnApply).
                    cell(btnCancel);

            cell(pnImage, Layout.FILL, Layout.FILL);
            cell(pnButtons, new Insets(10, 10, 10, 10));

            chooser.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    file = chooser.getSelectedFile();

                    loadFile();
                }
            });

            btnSelect.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (state == State.FILE_SELECTING) {
                        file = chooser.getSelectedFile();

                        loadFile();
                    } else {
                        setState(State.FILE_SELECTING);
                    }
                }
            });

            btnApplyFilter.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    doFilter(FILTER_OPERATIONS[((FilterItem) cbFilters.getSelectedItem()).getId()]);
                }
            });

            btnRotateLeft.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    doAffineTransform(image.getHeight(), image.getWidth(),
                            new AffineTransform(0, -1, 1, 0, 0, image.getWidth()));
                }
            });

            btnRotateRight.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    doAffineTransform(image.getHeight(), image.getWidth(),
                            new AffineTransform(0, 1, -1, 0, image.getHeight(), 0));
                }
            });

            btnFlipHorizontal.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    doAffineTransform(image.getWidth(), image.getHeight(),
                            new AffineTransform(-1, 0, 0, 1, image.getWidth(), 0));
                }
            });

            btnFlipVertical.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    doAffineTransform(image.getWidth(), image.getHeight(),
                            new AffineTransform(1, 0, 0, -1, 0, image.getHeight()));
                }
            });

            btnApply.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String fileName = file.getName();

                    int i = fileName.lastIndexOf('.');

                    try {
                        ImageIO.write(image, fileName.substring(i + 1), file);

                        setState(State.IMAGE_LOADED);
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(ImageEditor.this,
                                MessageFormat.format(getString("FileChooserDemo.errorsavefile.message"), e1),
                                getString("FileChooserDemo.errorsavefile.title"),
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            btnCancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    loadFile();
                }
            });

            setState(State.EMPTY);
        }

        private JButton createButton(String image, String toolTip) {
            JButton res = new JButton(createImageIcon(image, null));

            res.setPreferredSize(new Dimension(34, 34));
            res.setMinimumSize(new Dimension(34, 34));
            res.setToolTipText(getString(toolTip));

            return res;
        }

        private void doAffineTransform(int width, int height, AffineTransform transform) {
            BufferedImage newImage = new BufferedImage(image.getColorModel(),
                    image.getRaster().createCompatibleWritableRaster(width, height),
                    image.isAlphaPremultiplied(), new Hashtable<Object, Object>());

            ((Graphics2D) newImage.getGraphics()).drawRenderedImage(image, transform);

            image = newImage;

            lbImage.setIcon(new ImageIcon(image));

            setState(State.IMAGE_CHANGED);
        }

        private void doFilter(BufferedImageOp imageOp) {
            BufferedImage newImage = new BufferedImage(image.getColorModel(),
                    image.getRaster().createCompatibleWritableRaster(image.getWidth(), image.getHeight()),
                    image.isAlphaPremultiplied(), new Hashtable<Object, Object>());

            imageOp.filter(image, newImage);

            image = newImage;

            lbImage.setIcon(new ImageIcon(image));

            setState(State.IMAGE_CHANGED);
        }

        private void loadFile() {
            if (file == null) {
                JOptionPane.showMessageDialog(ImageEditor.this,
                        getString("FileChooserDemo.selectfile.message"),
                        getString("FileChooserDemo.selectfile.title"),
                        JOptionPane.INFORMATION_MESSAGE);

                return;
            }

            try {
                image = ImageIO.read(file);

                if (image != null) {
                    lbImage.setText(null);
                    lbImage.setIcon(new ImageIcon(image));

                    setState(State.IMAGE_LOADED);

                    return;
                }
            } catch (IOException e1) {
                // Do nothing
            }

            JOptionPane.showMessageDialog(ImageEditor.this,
                    getString("FileChooserDemo.errorloadfile.message"),
                    getString("FileChooserDemo.errorloadfile.title"),
                    JOptionPane.ERROR_MESSAGE);
        }

        private void setState(State state) {
            if (this.state != State.FILE_SELECTING && state == State.FILE_SELECTING) {
                setComponent(chooser, 0, 0);
            }

            if (this.state == State.FILE_SELECTING && state != State.FILE_SELECTING) {
                setComponent(pnImage, 0, 0);
            }

            this.state = state;

            boolean isImageLoaded = state == State.IMAGE_LOADED || state == State.IMAGE_CHANGED;

            cbFilters.setEnabled(isImageLoaded);
            btnApplyFilter.setEnabled(isImageLoaded);
            btnRotateRight.setEnabled(isImageLoaded);
            btnRotateLeft.setEnabled(isImageLoaded);
            btnFlipHorizontal.setEnabled(isImageLoaded);
            btnFlipVertical.setEnabled(isImageLoaded);

            boolean isImageChanged = state == State.IMAGE_CHANGED;

            btnApply.setEnabled(isImageChanged);
            btnCancel.setEnabled(isImageChanged);
        }
    }

    private static class FilterItem {
        /**
         * 0 - blur
         * 1 - edge
         * 2 - sharpen
         * 3 - darken
         * 4 - brighten
         * 5 - less contrast
         * 6 - more contrast
         * 7 - gray
         */
        private final int id;

        private final String name;

        private FilterItem(int id, String name) {
            assert id >= MIN_FILTER_ID && id <= MAX_FILTER_ID;

            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String toString() {
            return name;
        }
    }
}
