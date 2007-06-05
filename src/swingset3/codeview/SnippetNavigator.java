/*
 * Copyright 2007 Sun Microsystems, Inc.  All Rights Reserved.
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

package swingset3.codeview;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import swingset3.Utilities;

/**
 *
 * @author aim
 */
public class SnippetNavigator extends JComponent {
    private static final String NO_SNIPPET = "No Code Snippet highlight selected.";
    private static final String SHOWING_SNIPPET = " Showing Code Snippet: ";
    private static final String NO_SNIPPETS_IN_FILE = "No highlighted Code Snippets in ";
            
    private URL sourceFile;
    private SnippetMap snippetMap;
    
    JLabel statusLabel;
    JButton firstButton;
    JButton prevButton;
    JButton nextButton;
    JButton lastButton;
    
    /** Creates a new instance of SnippetNavigator */
    public SnippetNavigator(SnippetMap snippetMap, URL sourceFile) {
        this.sourceFile = sourceFile;
        this.snippetMap = snippetMap;
        snippetMap.addPropertyChangeListener(new SnippetHighlightListener());
        
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        
        firstButton = addButton(new JButton(), new EndArrowIcon(ArrowIcon.Direction.BACKWARD, 9));
        prevButton = addButton(new JButton(), new ArrowIcon(ArrowIcon.Direction.BACKWARD, 9));
       
        statusLabel = new JLabel(NO_SNIPPET);
        add(statusLabel);
        
        nextButton = addButton(new JButton(), new ArrowIcon(ArrowIcon.Direction.FORWARD, 9));        
        lastButton = addButton(new JButton(), new EndArrowIcon(ArrowIcon.Direction.FORWARD, 9));
    }
    
    protected JButton addButton(JButton button, Icon icon) {
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(2,2,2,2));
        button.setIcon(icon);
        add(button);
        button.setVisible(false);
        return button;
    }
    
    public void setNavigateFirstAction(Action action) {
        setButtonAction(firstButton, action);
    }
    
    public void setNavigatePreviousAction(Action action) {
        setButtonAction(prevButton, action);
    }
    
    public void setNavigateNextAction(Action action) {
        setButtonAction(nextButton, action);
    }
    
    public void setNavigateLastAction(Action action) {
        setButtonAction(lastButton, action);
    }
    
    private void setButtonAction(JButton button, Action action) {
        Icon icon = button.getIcon();
        button.setAction(action);
        button.setHideActionText(true);
        button.setIcon(icon); // icon gets obliterated when action set?
    }
    
    private class SnippetHighlightListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            String propertyName = e.getPropertyName();
            if (propertyName.equals("currentSet")) {
                String key = (String)e.getNewValue();
                setComponentState(key);
                                
            } else if (propertyName.equals("currentSnippet")) {
                setComponentState(snippetMap.getCurrentSet());
            }
        }
        
        private void setComponentState(String currentKey) {
            boolean showButtons = false;
            if (currentKey == null) {
                statusLabel.setText(NO_SNIPPET);
                
            } else {
                Snippet snippets[] = snippetMap.getSnippetsForFile(currentKey, sourceFile);
                if (snippets != null && snippets.length > 0) {
                    String place = snippetMap.getIndexForSnippet(snippetMap.getCurrentSnippet()) +
                            " of " + snippetMap.getSnippetCountForSet(currentKey) + " ";
                    statusLabel.setText(SHOWING_SNIPPET + place);
                    showButtons = true;
                } else {
                    statusLabel.setText(NO_SNIPPETS_IN_FILE + Utilities.getURLFileName(sourceFile));
                }
            }
            boolean moreThanOne = snippetMap.getSnippetCountForSet(currentKey) > 1;
            boolean previousExists = snippetMap.previousSnippetExists();
            boolean nextExists = snippetMap.nextSnippetExists();
            firstButton.setVisible(showButtons);
            firstButton.setEnabled(moreThanOne && previousExists);
            prevButton.setVisible(showButtons);
            prevButton.setEnabled(previousExists);
            nextButton.setVisible(showButtons);
            nextButton.setEnabled(nextExists);
            lastButton.setVisible(showButtons);
            lastButton.setEnabled(moreThanOne && nextExists);
        }
    }
    
    public static class ArrowIcon implements Icon {
        public enum Direction { FORWARD, BACKWARD }
        
        protected Direction direction;
        protected int height;
        protected int width;
        
        public ArrowIcon(Direction direction, int height) {
            this.direction = direction;
            this.height = height;
            this.width = 6;
        }
        
        public int getIconHeight() {
            return height;
        }
        public int getIconWidth() {
            return width;
        }
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(c.isEnabled()? c.getForeground() : Color.gray);
            if (direction == Direction.FORWARD) {
                g.fillRect(x, y, 2, height);
                g.fillRect(x + 2, y+1, 1, height - 2);
                g.fillRect(x + 3, y+2, 1, height - 4);
                g.fillRect(x + 4, y+3, 1, height - 6);
                g.fillRect(x + 5, y+4, 1, height - 8);
            } else {
                g.fillRect(x + width - 2, y, 2, height);
                g.fillRect(x + width - 3, y+1, 1, height - 2);
                g.fillRect(x + width - 4, y+2, 1, height - 4);
                g.fillRect(x + width - 5, y+3, 1, height - 6);
                g.fillRect(x + width - 6, y+4, 1, height - 8);
            }
        }
    }
    
    public class EndArrowIcon extends ArrowIcon {
        
        public EndArrowIcon(Direction direction, int height) {
            super(direction, height);
            width += 3;
        }
       
        public void paintIcon(Component c, Graphics g, int x, int y) {
            super.paintIcon(c, g, x, y);
            g.setColor(c.isEnabled()? c.getForeground() : Color.gray);
            if (direction == Direction.FORWARD) {
                g.fillRect(x + width - 2, y, 2, height);
            } else {
                // BACKWARD
                g.fillRect(x, y, 2, height);
            }
        }        
    }
    
}
