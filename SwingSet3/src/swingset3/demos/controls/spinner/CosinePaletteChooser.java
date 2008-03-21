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
package swingset3.demos.controls.spinner;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.util.Map;

/**
 * @author Mikhail Lapshin
 */
public class CosinePaletteChooser extends JPanel {
    private final int MIN_COLOR = 50;
    private final int MAX_COLOR = 255;
    private final int R_STEPS = 5;
    private final int G_STEPS = 5;
    private final int B_STEPS = 5;
    private final int R_ANGLE = 270;
    private final int G_ANGLE = 90;
    private final int B_ANGLE = 0;
    private CosinePalette palette;
    public static final String PALETTE_PROPERTY_NAME = "palette";
    private JPaletteShower shower;
    private ChangeListener changeListener;
    private Map<String, String> strings;

    private JSpinner rsSpinner;
    private JSpinner gsSpinner;
    private JSpinner bsSpinner;
    private JSpinner raSpinner;
    private JSpinner gaSpinner;
    private JSpinner baSpinner;
    //todo add minColorSpinner and maxColorSpinner

    public CosinePaletteChooser(int width, int height, Map<String, String> strings) {
        this.strings = strings;
        palette = new CosinePalette(width, MIN_COLOR, MAX_COLOR,
                R_ANGLE * Math.PI / 180, G_ANGLE * Math.PI / 180, B_ANGLE * Math.PI / 180,
                R_STEPS, G_STEPS, B_STEPS);
        shower = new JPaletteShower(palette, width, 25);
        changeListener = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                setPalette(createPalette());
                shower.setPalette(palette);
                repaint();
            }
        };

        setPreferredSize(new Dimension(width, height));
        setBorder(BorderFactory.createTitledBorder(getString("SpinnerDemo.colorPalette")));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(shower);
        add(getControlPanel());
    }
    
    private String getString(String key) {
        String result = strings.get(key);
        return (result != null) ? result : "a string";
    }

    private CosinePalette createPalette() {
        return new CosinePalette(getWidth(), MIN_COLOR, MAX_COLOR,
                getRadianValue(raSpinner), getRadianValue(gaSpinner), getRadianValue(baSpinner),
                getIntValue(rsSpinner), getIntValue(gsSpinner), getIntValue(bsSpinner));
    }

    private int getIntValue(JSpinner spinner) {
        return (Integer) spinner.getValue();
    }

    private double getRadianValue(JSpinner spinner) {
        return getIntValue(spinner) * Math.PI / 180;
    }

    private JPanel getControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        controlPanel.add(getPaletteControlPanel(), BorderLayout.CENTER);
        return controlPanel;
    }

    private JPanel getPaletteControlPanel() {
        JPanel paletteControlPanel = new JPanel();
        paletteControlPanel.setLayout(new BorderLayout());
        paletteControlPanel.add(getStepPanel(), BorderLayout.WEST);
        paletteControlPanel.add(getStartAnglePanel(), BorderLayout.CENTER);
        return paletteControlPanel;
    }

    private JPanel getStartAnglePanel() {
        JPanel startAnglePanel = new JPanel();
        startAnglePanel.setBorder(BorderFactory.createTitledBorder(
                getString("SpinnerDemo.startAngles")));
        startAnglePanel.setLayout(new BoxLayout(startAnglePanel, BoxLayout.Y_AXIS));

        raSpinner = createSpinner(getString("SpinnerDemo.r"), 
                startAnglePanel, new SpinnerNumberModel(R_ANGLE, 0, 360, 10));
        gaSpinner = createSpinner(getString("SpinnerDemo.g"), 
                startAnglePanel, new SpinnerNumberModel(G_ANGLE, 0, 360, 10));
        baSpinner = createSpinner(getString("SpinnerDemo.b"), 
                startAnglePanel, new SpinnerNumberModel(B_ANGLE, 0, 360, 10));

        return startAnglePanel;
    }

    private JPanel getStepPanel() {
        JPanel stepPanel = new JPanel();
        stepPanel.setBorder(BorderFactory.createTitledBorder(
                getString("SpinnerDemo.steps")));
        stepPanel.setLayout(new BoxLayout(stepPanel, BoxLayout.Y_AXIS));

        rsSpinner = createSpinner(getString("SpinnerDemo.r"), 
                stepPanel, new SpinnerNumberModel(R_STEPS, 1, 1000, 1));
        gsSpinner = createSpinner(getString("SpinnerDemo.g"), 
                stepPanel, new SpinnerNumberModel(G_STEPS, 1, 1000, 1));
        bsSpinner = createSpinner(getString("SpinnerDemo.b"), 
                stepPanel, new SpinnerNumberModel(B_STEPS, 1, 1000, 1));

        return stepPanel;
    }

    private JSpinner createSpinner(String labelText, JComponent parent, SpinnerModel model) {
        JPanel panel = new JPanel();
        panel.add(new JLabel(labelText));
        JSpinner spinner = new JSpinner(model);
        spinner.addChangeListener(changeListener);
        panel.add(spinner);
        parent.add(panel);
        return spinner;
    }

    public CosinePalette getPalette() {
        return palette;
    }

    private void setPalette(CosinePalette palette) {
        Palette oldValue = this.palette;
        this.palette = palette;
        firePropertyChange(PALETTE_PROPERTY_NAME, oldValue, palette);
    }
}
