/*
 * Copyright 2008 Sun Microsystems, Inc.  All Rights Reserved.
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

package swingset3.utilities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 *
 * @author Administrator
 */
public class GradientPanel extends JPanel {
    private String colorKeys[] = new String[2];
    private Color colors[] = new Color[2];
    private Image gradientImage;
    
    public GradientPanel(String color1Key, String color2Key) {
        this(UIManager.getColor(color1Key), UIManager.getColor(color2Key));
        colorKeys[0] = color1Key;
        colorKeys[1] = color2Key;
    }
    
    public GradientPanel(Color color1, Color color2) {
        super();
        setOpaque(false);  // unfortunately required to disable automatic bg painting
        setBackground(color2); // in case colors are derived from background
        initColors(color1, color2);
    }
    
    @Override
    public void updateUI() {
        super.updateUI();
        if (colorKeys != null && colorKeys[0] != null &&
                colorKeys[1] != null) {            
            initColors(UIManager.getColor(colorKeys[0]),
                       UIManager.getColor(colorKeys[1]));
        }        
    }
    
    protected void initColors(Color color1, Color color2) {
        colors[0] = color1;
        colors[1] = color2;
    }
   
    @Override
    protected void paintComponent(Graphics g) {
        Dimension size = getSize();
        if (gradientImage == null ||
                gradientImage.getWidth(null) != size.width ||
                gradientImage.getHeight(null) != size.height) {

            gradientImage = Utilities.createGradientImage(size.width, size.height, 
                    colors[0], colors[1]);
        
        }
        g.drawImage(gradientImage, 0, 0, null);
        super.paintComponent(g);
    }

}
