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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

import swingset3.DemoProperties;
import swingset3.demos.DemoBase;
import swingset3.utilities.JGridPanel;

/**
 * JTextField Demo
 *
 * @author Pavel Porvatov
 */
@DemoProperties(
        value = "TextField Demo",
        category = "Controls",
        description = "Demonstrates the JTextField, a control which allows to input text",
        sourceFiles = {
                "swingset3/demos/controls/TextFieldDemo.java",
                "swingset3/demos/controls/JHistoryTextField.java",
                "swingset3/utilities/JGridPanel.java",
                "swingset3/demos/DemoBase.java"
                }
)
public class TextFieldDemo extends DemoBase {
    private final JLabel lbHistoryTextField = new JLabel(getString("TextField.historytextfield.text"));

    private final JHistoryTextField tfHistory = new JHistoryTextField();

    private final JLabel lbDow = new JLabel(getString("TextField.dow.text"));

    private final JFormattedTextField tfDow = new JFormattedTextField();

    private final JLabel lbDowResult = new JLabel();

    private final JLabel lbPassword = new JLabel(getString("TextField.password.text"));

    private final JPasswordField tfPassword1 = new JPasswordField(20);

    private final JPasswordField tfPassword2 = new JPasswordField(20);

    private final DocumentListener passwordListener = new DocumentListener() {
        public void insertUpdate(DocumentEvent e) {
            highlightPasswords();
        }

        public void removeUpdate(DocumentEvent e) {
            highlightPasswords();
        }

        public void changedUpdate(DocumentEvent e) {
            highlightPasswords();
        }

        private void highlightPasswords() {
            Color color;

            if (tfPassword1.getPassword().length > 0 && 
                    Arrays.equals(tfPassword1.getPassword(), tfPassword2.getPassword())) {
                color = Color.GREEN;
            } else {
                color = UIManager.getColor("TextField.background");
            }

            tfPassword1.setBackground(color);
            tfPassword2.setBackground(color);
        }
    };

    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        TextFieldDemo demo = new TextFieldDemo();

        demo.mainImpl();
    }

    public TextFieldDemo() {
        initUI();

        tfDow.addPropertyChangeListener("value", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                Calendar calendar = Calendar.getInstance(Locale.ENGLISH);

                calendar.setTime((Date) tfDow.getValue());

                lbDowResult.setText(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH));
            }
        });

        tfDow.setValue(new Date());

        tfPassword1.getDocument().addDocumentListener(passwordListener);

        tfPassword2.getDocument().addDocumentListener(passwordListener);
    }

    private void initUI() {
        tfHistory.setHistory(Arrays.asList(getString("TextField.history.words").split("\\,")));

        JGridPanel pnDow = new JGridPanel(2, 1);

        pnDow.cell(tfDow).
                cell(lbDowResult);

        JGridPanel pnPassword = new JGridPanel(3, 2);

        pnPassword.cell(tfPassword1).
                cell(tfPassword2).
                cell();

        JGridPanel pnContent = new JGridPanel(1, 0, 6);

        pnContent.setBorderEqual(10);

        pnContent.cell(lbHistoryTextField).
                cell(tfHistory).
                cell(lbDow, new Insets(20, 0, 0, 0)).
                cell(pnDow).
                cell(lbPassword, new Insets(20, 0, 0, 0)).
                cell(pnPassword).
                cell();

        add(pnContent);
    }
}
