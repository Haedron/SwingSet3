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
package com.sun.swingset3.demos.spinner;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;

import com.sun.swingset3.demos.ResourceManager;

/**
 * @author Mikhail Lapshin
 */
public class MandelbrotControl extends JPanel {
    private JMandelbrot mandelbrot;
    private JSpinner zoomSpinner;
    private JSpinner threadSpinner;
    private JSpinner iterSpinner;
    private JSpinner xSpinner;
    private JTextField xSpinnerEditor;
    private JSpinner ySpinner;
    private JTextField ySpinnerEditor;
    private double COORD_SPINNER_STEP = 1d / 10;
    private final ResourceManager resourceManager;

    public MandelbrotControl(JMandelbrot mandelbrot, ResourceManager resourceManager) {
        this.mandelbrot = mandelbrot;
        this.resourceManager = resourceManager;
        createUI();
        installListeners();
    }

    private void createUI() {
        JSpinnerPanel spinnerPanel = new JSpinnerPanel();
        setBorder(BorderFactory.createTitledBorder(
                resourceManager.getString("SpinnerDemo.fractalControls")));

        iterSpinner = new JSpinner(
                new SpinnerNumberModel(mandelbrot.getMaxIteration(), 10, 100000, 50));
        iterSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                mandelbrot.setMaxIteration((Integer) iterSpinner.getValue());
                mandelbrot.calculatePicture();
            }
        });
        spinnerPanel.addSpinner(
                resourceManager.getString("SpinnerDemo.iterations"), iterSpinner);

        zoomSpinner = new JSpinner(
                new SpinnerNumberModel(mandelbrot.getZoomRate(), 1, 20, 1));
        zoomSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                mandelbrot.setZoomRate((Double) zoomSpinner.getValue());
            }
        });
        spinnerPanel.addSpinner(
                resourceManager.getString("SpinnerDemo.zoomRate"), zoomSpinner);

        threadSpinner = new JSpinner(
                new SpinnerNumberModel(mandelbrot.getNumOfThreads(), 1, 16, 1));
        threadSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                mandelbrot.setNumOfThreads((Integer) threadSpinner.getValue());
            }
        });
        spinnerPanel.addSpinner(
                resourceManager.getString("SpinnerDemo.threads"), threadSpinner);

        final double width = mandelbrot.getXHighLimit() - mandelbrot.getXLowLimit();
        final double xValue = mandelbrot.getCenter().getX();
        xSpinnerEditor = new JTextField();
        xSpinnerEditor.setText(Double.toString(xValue));
        xSpinner = new JSpinner(
                new SpinnerNumberModel(xValue, null, null, width * COORD_SPINNER_STEP));
        xSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Double newX = (Double) xSpinner.getValue();
                xSpinnerEditor.setText(newX.toString());
                mandelbrot.setCenter(new Coords(newX, mandelbrot.getCenter().getY()));
                mandelbrot.calculatePicture();
            }
        });
        xSpinner.setPreferredSize(new Dimension(180, xSpinner.getPreferredSize().height));
        xSpinner.setEditor(xSpinnerEditor);
        installSpinnerEditorListeners(xSpinner, xSpinnerEditor);
        spinnerPanel.addSpinner(
                resourceManager.getString("SpinnerDemo.x"), xSpinner);

        final double height = mandelbrot.getYHighLimit() - mandelbrot.getYLowLimit();
        final double yValue = mandelbrot.getCenter().getY();
        ySpinnerEditor = new JTextField();
        ySpinnerEditor.setText(Double.toString(yValue));
        ySpinner = new JSpinner(
                new SpinnerNumberModel(yValue, null, null, height * COORD_SPINNER_STEP));
        ySpinner.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        Double newY = (Double) ySpinner.getValue();
                        ySpinnerEditor.setText(newY.toString());
                        mandelbrot.setCenter(new Coords(mandelbrot.getCenter().getX(), newY));
                        mandelbrot.calculatePicture();
                    }
                });
        ySpinner.setPreferredSize(new Dimension(180, ySpinner.getPreferredSize().height));
        ySpinner.setEditor(ySpinnerEditor);
        installSpinnerEditorListeners(ySpinner, ySpinnerEditor);
        spinnerPanel.addSpinner(
                resourceManager.getString("SpinnerDemo.y"), ySpinner);

        add(spinnerPanel);
    }

    private void installListeners() {
        mandelbrot.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(JMandelbrot.CENTER_PROPERTY_NAME)) {
                    double width = mandelbrot.getXHighLimit() - mandelbrot.getXLowLimit();
                    double newX = mandelbrot.getCenter().getX();
                    xSpinner.setModel(new SpinnerNumberModel(newX, null, null,
                            width * COORD_SPINNER_STEP));
                    xSpinnerEditor.setText(Double.toString(newX));
                    double height = mandelbrot.getYHighLimit() - mandelbrot.getYLowLimit();
                    double newY = mandelbrot.getCenter().getY();
                    ySpinner.setModel(new SpinnerNumberModel(newY, null, null,
                            height * COORD_SPINNER_STEP));
                    ySpinnerEditor.setText(Double.toString(newY));
                }
            }
        });
    }

    private static JTextField installSpinnerEditorListeners(final JSpinner parentSpinner, JTextField editor) {
        editor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateCoordSpinnerValue(parentSpinner);
            }
        });
        editor.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
            }

            public void focusLost(FocusEvent e) {
                updateCoordSpinnerValue(parentSpinner);
            }
        });
        return editor;
    }

    private static void updateCoordSpinnerValue(JSpinner spinner) {
        double newValue = (Double) spinner.getModel().getValue();
        JTextField editor = (JTextField) spinner.getEditor();
        try {
            newValue = Double.parseDouble(editor.getText());
        } catch (NumberFormatException ex) {
            editor.setText(Double.valueOf(newValue).toString());
        }
        spinner.setValue(newValue);
    }

    private static class JSpinnerPanel extends JPanel {
        private JPanel labelPanel;
        private JPanel spinnerPanel;
        private boolean firstTime = true;

        public JSpinnerPanel() {
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

            labelPanel = new JPanel();
            labelPanel.setLayout(new GridLayout(1, 1));

            spinnerPanel = new JPanel();
            spinnerPanel.setLayout(new GridLayout(1, 1));

            add(labelPanel);
            add(Box.createHorizontalStrut(10));
            add(spinnerPanel);
        }

        public void addSpinner(String labelText, JSpinner spinner) {
            if (firstTime) {
                firstTime = false;
            } else {
                GridLayout gl = (GridLayout)labelPanel.getLayout();
                gl.setRows(gl.getRows() + 1);
                gl = (GridLayout)spinnerPanel.getLayout();
                gl.setRows(gl.getRows() + 1);
            }

            JLabel label = new JLabel(labelText);
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            labelPanel.add(label);

            JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
            flowPanel.add(spinner);
            spinnerPanel.add(flowPanel);
        }
    }
}

