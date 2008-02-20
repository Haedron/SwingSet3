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
      todo: tune gaps
     */
    private enum State {
        EMPTY,
        FILE_SELECTING,
        IMAGE_LOADED,
        IMAGE_CHANGED
    }

    private static final int MIN_FILTER_ID = 0;

    private static final int MAX_FILTER_ID = 7;

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

        private final JButton btnRotateLeft = new JButton("L"); // todo: add image

        private final JButton btnRotateRight = new JButton("R"); // todo: add image

        private final JButton btnFlipHorizontal = new JButton("H"); // todo: add image

        private final JButton btnFlipVertical = new JButton("V"); // todo: add image

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
                cbFilters.addItem(new FilterItem(i));
            }

            JGridPanel pnButtons = new JGridPanel(11, 1);

            pnButtons.cell(btnSelect).
                    cell().
                    cell(cbFilters).
                    cell().
                    cell(btnRotateLeft).
                    cell(btnRotateRight).
                    cell(btnFlipHorizontal).
                    cell(btnFlipVertical).
                    cell().
                    cell(btnApply).
                    cell(btnCancel);

            cell(pnImage, Layout.FILL, Layout.FILL);
            cell(pnButtons);

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

            cbFilters.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ((FilterItem) cbFilters.getSelectedItem()).applyFilter();

                    cbFilters.setSelectedItem(cbFilters.getItemAt(0));
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

        private void doAffineTransform(int width, int height, AffineTransform transform) {
            BufferedImage newImage = new BufferedImage(image.getColorModel(),
                    image.getRaster().createCompatibleWritableRaster(width, height),
                    image.isAlphaPremultiplied(), new Hashtable<Object, Object>());

            ((Graphics2D) newImage.getGraphics()).drawRenderedImage(image, transform);

            image = newImage;

            lbImage.setIcon(new ImageIcon(image));

            setState(State.IMAGE_CHANGED);
        }

        private void doFilter(float[] elements) {
            BufferedImage newImage = new BufferedImage(image.getColorModel(),
                    image.getRaster().createCompatibleWritableRaster(image.getWidth(), image.getHeight()),
                    image.isAlphaPremultiplied(), new Hashtable<Object, Object>());

            new ConvolveOp(new Kernel(3, 3, elements), ConvolveOp.EDGE_NO_OP, null).filter(image, newImage);

            image = newImage;

            lbImage.setIcon(new ImageIcon(image));

            setState(State.IMAGE_CHANGED);
        }

        private void doFilter(float scaleFactor, float offset) {
            BufferedImage newImage = new BufferedImage(image.getColorModel(),
                    image.getRaster().createCompatibleWritableRaster(image.getWidth(), image.getHeight()),
                    image.isAlphaPremultiplied(), new Hashtable<Object, Object>());

            new RescaleOp(scaleFactor, offset, null).filter(image, newImage);

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
            btnRotateRight.setEnabled(isImageLoaded);
            btnRotateLeft.setEnabled(isImageLoaded);
            btnFlipHorizontal.setEnabled(isImageLoaded);
            btnFlipVertical.setEnabled(isImageLoaded);

            boolean isImageChanged = state == State.IMAGE_CHANGED;

            btnApply.setEnabled(isImageChanged);
            btnCancel.setEnabled(isImageChanged);
        }

        private class FilterItem {
            /**
             * 0 - Empty filter
             * 1 - blur
             * 2 - edge
             * 3 - sharpen
             * 4 - darken
             * 5 - brighten
             * 6 - less contrast
             * 7 - more contrast
             */
            private final int id;

            private FilterItem(int id) {
                assert id >= MIN_FILTER_ID && id <= MAX_FILTER_ID;

                this.id = id;
            }

            public void applyFilter() {
                switch (id) {
                    case 1: {
                        // Blur
                        float[] elements = {
                                .1111f, .1111f, .1111f,
                                .1111f, .1111f, .1111f,
                                .1111f, .1111f, .1111f};

                        doFilter(elements);

                        break;
                    }

                    case 2: {
                        // Edge
                        float[] elements = {
                                0.0f, -1.0f, 0.0f,
                                -1.0f, 4.f, -1.0f,
                                0.0f, -1.0f, 0.0f};

                        doFilter(elements);

                        break;
                    }

                    case 3: {
                        // Sharpen
                        float[] elements = {
                                0.0f, -1.0f, 0.0f,
                                -1.0f, 5.f, -1.0f,
                                0.0f, -1.0f, 0.0f};

                        doFilter(elements);

                        break;
                    }

                    case 4: {
                        // Darken
                        doFilter(1, -5.0f);

                        break;
                    }

                    case 5: {
                        // Brighten
                        doFilter(1, 5.0f);

                        break;
                    }

                    case 6: {
                        // Less contrast
                        doFilter(0.9f, 0);

                        break;
                    }

                    case 7: {
                        // More contrast
                        doFilter(1.1f, 0);

                        break;
                    }
                }
            }

            public String toString() {
                switch (id) {
                    case 0:
                        return getString("FileChooserDemo.filter.selectfilter");

                    case 1:
                        return getString("FileChooserDemo.filter.blur");

                    case 2:
                        return getString("FileChooserDemo.filter.edge");

                    case 3:
                        return getString("FileChooserDemo.filter.sharpen");

                    case 4:
                        return getString("FileChooserDemo.filter.darken");

                    case 5:
                        return getString("FileChooserDemo.filter.brighten");

                    case 6:
                        return getString("FileChooserDemo.filter.lesscontrast");

                    case 7:
                        return getString("FileChooserDemo.filter.morecontrast");
                }

                return null;
            }
        }
    }
}
