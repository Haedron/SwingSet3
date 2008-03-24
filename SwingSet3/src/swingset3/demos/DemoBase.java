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

package swingset3.demos;

import java.awt.*;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 * Base class for demos ported from SwingSet2 (previously named DemoModule)
 * Demos may extend this class, but it is NOT required.
 *
 * @author Jeff Dinkins
 * @version 1.23 11/17/05
 */
public class DemoBase extends JPanel {
    private static final Logger logger = Logger.getLogger(DemoBase.class.getName());

    // The preferred size of the demo
    private int PREFERRED_WIDTH = 600;
    private int PREFERRED_HEIGHT = 600;

    public static Dimension HGAP5 = new Dimension(5, 1);
    public static Dimension VGAP5 = new Dimension(1, 5);

    public static Dimension HGAP10 = new Dimension(10, 1);
    public static Dimension VGAP10 = new Dimension(1, 10);

    public static Dimension HGAP15 = new Dimension(15, 1);
    public static Dimension VGAP15 = new Dimension(1, 15);

    public static Dimension HGAP20 = new Dimension(20, 1);
    public static Dimension VGAP20 = new Dimension(1, 20);

    public static Dimension HGAP25 = new Dimension(25, 1);
    public static Dimension VGAP25 = new Dimension(1, 25);

    public static Dimension HGAP30 = new Dimension(30, 1);
    public static Dimension VGAP30 = new Dimension(1, 30);

    // Resource bundle for internationalized and accessible text
    private ResourceBundle bundle = null;
    private String bundleName = null;


    protected DemoBase() {
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        setLayout(new BorderLayout());
    }

    public String getString(String key) {
        initBundle();
        return bundle != null ? bundle.getString(key) : key;
    }

    private void initBundle() {
        if (bundle == null) {
            bundleName = "a bundle";
            try {
                bundleName = getClass().getPackage().getName() + ".resources." + getClass().getSimpleName();
                bundle = ResourceBundle.getBundle(bundleName);
            } catch (MissingResourceException e) {
                logger.log(Level.SEVERE, "java.util.MissingResourceException: Couldn't load bundle: " + bundleName);
            }
        }
    }

    public Map getStrings() {
        Map<String, String> result = new HashMap<String, String>();
        initBundle();
        if (bundle != null) {
            try {
                Set<String> keys = bundle.keySet();
                for (String key : keys) {
                    result.put(key, bundle.getString(key));
                }
            } catch (MissingResourceException e) {
                logger.log(Level.SEVERE, "java.util.MissingResourceException: Couldn't load bundle: " + bundleName);
            }
        }
        return result;
    }

    public char getMnemonic(String key) {
        return (getString(key)).charAt(0);
    }

    public ImageIcon createImageIcon(String filename, String description) {
        String path = "resources/images/" + filename;
        URL imageURL = getClass().getResource(path);
        if (imageURL == null) {
            logger.log(Level.SEVERE, "unable to access image file: " + path);
        }
        return imageURL != null ?
                new ImageIcon(getClass().getResource(path), description) : null;

    }

    protected void mainImpl() {
        JFrame frame = new JFrame(getName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(this, BorderLayout.CENTER);
        setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

