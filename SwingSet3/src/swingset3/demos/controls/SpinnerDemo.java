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

import java.awt.BorderLayout;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import swingset3.DemoProperties;
import swingset3.demos.DemoBase;
import swingset3.demos.ResourceManager;
import swingset3.demos.controls.spinner.*;

/**
 * JSpinner, SwingWorker demos
 *
 * @author Mikhail Lapshin
 */
@DemoProperties(
        value = "Spinner Demo",
        category = "Controls",
        description = "Demonstrates JSpinner and SwingWorker",
        sourceFiles = {
                "swingset3/demos/DemoBase.java",
                "swingset3/demos/controls/SpinnerDemo.java",
                "swingset3/demos/controls/spinner/Coords.java",
                "swingset3/demos/controls/spinner/CosinePalette.java",
                "swingset3/demos/controls/spinner/CosinePaletteChooser.java",
                "swingset3/demos/controls/spinner/JMandelbrot.java",
                "swingset3/demos/controls/spinner/MandelbrotControl.java",
                "swingset3/demos/controls/spinner/Palette.java",
                "swingset3/demos/controls/spinner/JPaletteShower.java"
                }
)
        
public class SpinnerDemo extends DemoBase {
    private final ResourceManager resourceManager = new ResourceManager(this.getClass());

    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        SpinnerDemo demo = new SpinnerDemo();
        demo.mainImpl();
    }

    public SpinnerDemo() {
        CosinePaletteChooser chooser = 
                new CosinePaletteChooser(250, 180, resourceManager);
        final JMandelbrot mandelbrot = 
                new JMandelbrot(400, 400, chooser.getPalette(), resourceManager);
        MandelbrotControl mandelbrotControl = 
                new MandelbrotControl(mandelbrot, resourceManager);

        chooser.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(CosinePaletteChooser.PALETTE_PROPERTY_NAME)) {
                    mandelbrot.setPalette((Palette) evt.getNewValue());
                    mandelbrot.calculatePicture();
                }
            }
        });

        JPanel mPanel = new JPanel();
        mPanel.setLayout(new GridLayout());
        mPanel.add(mandelbrot, BorderLayout.WEST);
        add(mPanel);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        controlPanel.add(chooser, BorderLayout.NORTH);

        JPanel mandelbrotControlPanel = new JPanel();
        mandelbrotControlPanel.setLayout(new BorderLayout());
        mandelbrotControlPanel.add(mandelbrotControl, BorderLayout.NORTH);
        controlPanel.add(mandelbrotControlPanel, BorderLayout.CENTER);

        add(controlPanel, BorderLayout.EAST);
    }
}
