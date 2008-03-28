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


import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import swingset3.utilities.ArrowIcon;
import swingset3.utilities.RoundedBorder;
import swingset3.utilities.RoundedPanel;
import swingset3.utilities.Utilities;

/**
 * GUI component for viewing a set of one or more Java source code files,
 * providing the user with the ability to easily highlight specific code fragments.
 * A tabbedpane is used to control which source code file is shown within the set
 * if more than one file is loaded.
 * <p>
 * Example usage:
 * <pre><code>
 *    CodeViewer codeViewer = new CodeViewer();
 *    codeViewer.setSourceFiles(mySourceURLs);
 *    frame.add(codeViewer);
 * </code></pre>
 * 
 * <p>
 * When loading the source code, this viewer will automatically parse the files for
 * any source fragments which are marked with &quot;snippet&quot; start/end tags that
 * are embedded within Java comments.  The viewer will allow the user to highlight 
 * these code snippets for easier inspection of specific code.
 * <p>
 * The text following immediately after the start tag will be used
 * as the key for that snippet.  Multiple snippets may share the same 
 * key, defining a &quot;snippet set&quot;.  Snippet sets may even span across 
 * multiple source files.
 * The key for each snippet set is displayed in a combobox to allow the user to 
 * select which snippet set should be highlighted.  For example:<p>
 * <pre><code>
 *    //<snip>Create dog array
 *    ArrayList dogs = new ArrayList();
 *    //</snip>
 *
 *    [other code...]
 *
 *    //<snip>Create dog array
 *    dogs.add("Labrador");
 *    dogs.add("Golden Retriever");
 *    dogs.add("Australian Shepherd");
 *    //</snip>
 * </code></pre>
 * The above code would create a snippet set (containing 2 snippets) with the key 
 * &quot;Create dog array&quot;.
 * <p>
 * The viewer will allow the user to easily navigate across the currently highlighted
 * snippet set by pressing the navigation buttons or using accelerator keys.
 * 
 * @author aim
 */
public class CodeViewer extends JPanel {
    static final Logger logger = Logger.getLogger(CodeViewer.class.getName());
    
    private static final Color DEFAULT_HIGHLIGHT_COLOR = new Color(255,255,176); 
    private static BufferedImage SNIPPET_GLYPH;
    private static String NO_SNIPPET_SELECTED;
    
    static {
        try {
            URL imageURL = CodeViewer.class.getResource("resources/images/snippetglyph.png");
            SNIPPET_GLYPH = ImageIO.read(imageURL);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
    // Cache all processed code files in case they are reloaded later
    private Map<URL,CodeFileInfo> codeCache = new HashMap<URL,CodeFileInfo>();
    
    private JComponent codeHighlightBar;
    private JComboBox snippetComboBox;
    private JComponent codePanel;
    private JLabel noCodeLabel;
    private JTabbedPane codeTabbedPane;
    
    private Color highlightColor;
    private Highlighter.HighlightPainter snippetPainter;
    
    private ResourceBundle bundle;

    // Current code file set
    private Map<URL, CodeFileInfo> currentCodeFilesInfo; 
    
    // Map of all snippets in current code file set    
    private SnippetMap snippetMap = new SnippetMap();
    
    private Action firstSnippetAction;
    private Action nextSnippetAction;
    private Action previousSnippetAction;
    private Action lastSnippetAction;
    
    /**
     * Creates a new instance of CodeViewer
     */
    public CodeViewer() {
        setHighlightColor(DEFAULT_HIGHLIGHT_COLOR);
        
        initActions();

        setLayout(new BorderLayout());
        codeHighlightBar = createCodeHighlightBar();
        codeHighlightBar.setVisible(false);
        add(codeHighlightBar, BorderLayout.NORTH);
        codePanel = createCodePanel();
        add(codePanel, BorderLayout.CENTER);
        
        applyDefaults();
    }
                
    protected JComponent createCodeHighlightBar() {
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        JPanel bar = new JPanel(gridbag);
        
        bar.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        NO_SNIPPET_SELECTED = getString("CodeViewer.snippets.selectOne", 
                                        "Select One");
       
        JLabel snippetSetsLabel = new JLabel(getString("CodeViewer.snippets.highlightCode",
                                                       "Highlight code to: "));
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0;
        gridbag.addLayoutComponent(snippetSetsLabel, c);
        bar.add(snippetSetsLabel);
        
        snippetComboBox = new JComboBox();
        snippetComboBox.setMaximumRowCount(20);
        snippetComboBox.setRenderer(new SnippetCellRenderer(snippetComboBox.getRenderer()));
        snippetComboBox.addActionListener(new SnippetActivator());
        snippetSetsLabel.setLabelFor(snippetComboBox);
        c.gridx++;
        c.weightx = 1;
        gridbag.addLayoutComponent(snippetComboBox, c);
        bar.add(snippetComboBox);
            
        SnippetNavigator snippetNavigator = new SnippetNavigator(snippetMap);
        snippetNavigator.setNavigateNextAction(nextSnippetAction);
        snippetNavigator.setNavigatePreviousAction(previousSnippetAction);
        c.gridx++;
        c.anchor = GridBagConstraints.EAST;
        c.weightx = 0;
        gridbag.addLayoutComponent(snippetNavigator, c);
        bar.add(snippetNavigator);
        
        return bar;              
    }
    
    protected JComponent createCodePanel() {
        
        JPanel panel = new RoundedPanel(new BorderLayout(), 10);
        panel.setBorder(new RoundedBorder(10));
        panel.add(Box.createVerticalStrut(12), BorderLayout.NORTH);
        
        noCodeLabel = new JLabel(getString("CodeViewer.noCodeLoaded", "no code loaded"));
        noCodeLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(noCodeLabel, BorderLayout.CENTER);
        return panel;
    }
    
    @Override
    public void updateUI() {
        super.updateUI();
        applyDefaults();        
    }
    
    protected void applyDefaults() {
        if (noCodeLabel != null) {
            noCodeLabel.setOpaque(false);
            noCodeLabel.setFont(UIManager.getFont("Label.font").deriveFont(24f));
            noCodeLabel.setForeground(
                    Utilities.deriveColorAlpha(UIManager.getColor("Label.foreground"), 110));
        }
        if (codePanel != null) {
            Color base = UIManager.getColor("Panel.background");
            codePanel.setBackground(Utilities.deriveColorHSB(base, 0, 0, -.06f));
        }
        if (snippetComboBox != null) {
            // Now that the look and feel has changed, we need to wrap the new delegate
            snippetComboBox.setRenderer(new SnippetCellRenderer(
                    new JComboBox().getRenderer()));
        }
        if (currentCodeFilesInfo != null) {
            Collection<CodeFileInfo> codeFiles = currentCodeFilesInfo.values();
            for(CodeFileInfo cfi : codeFiles) {
                makeSelectionTransparent(cfi.textPane, 180);
            }
        }
    }
    
    private void makeSelectionTransparent(JEditorPane textPane, int alpha) {
        Color c = textPane.getSelectionColor();
        textPane.setSelectionColor(
                new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
    }
    
    protected String getString(String key, String fallback) {
        String value = fallback;
        if (bundle == null) {
            String bundleName = getClass().getPackage().getName()+".resources."+getClass().getSimpleName();
            bundle = ResourceBundle.getBundle(bundleName);
        }
        try {
            value = bundle != null? bundle.getString(key) : fallback;
        
        } catch (MissingResourceException e) {
            logger.log(Level.WARNING, "missing String resource " + key + 
                    "; using fallback \"" +fallback + "\"");
        }
        return value;
    }
    
    protected void initActions() {
        firstSnippetAction = new FirstSnippetAction();
        nextSnippetAction = new NextSnippetAction();
        previousSnippetAction = new PreviousSnippetAction();
        lastSnippetAction = new LastSnippetAction();
        
        firstSnippetAction.setEnabled(false);
        nextSnippetAction.setEnabled(false);
        previousSnippetAction.setEnabled(false);
        lastSnippetAction.setEnabled(false);
        
        getActionMap().put("NextSnippet", nextSnippetAction);
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl N"),"NextSnippet");
        
        getActionMap().put("PreviousSnippet", previousSnippetAction);
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl P"),"PreviousSnippet");
    }
    
    public void setHighlightColor(Color highlight) {
        if (!highlight.equals(highlightColor)) {
            highlightColor = highlight;
            snippetPainter = new SnippetHighlighter.SnippetHighlightPainter(highlightColor);
            if (getCurrentSnippetKey() != null) {
                repaint();
            }
        }
    }
    
    public Color getHighlightColor() {
        return highlightColor;
    }
    
    public void setSourceFiles(URL sourceFiles[]) {
        if (currentCodeFilesInfo != null &&
                currentCodeFilesInfo.size() == sourceFiles.length &&
                currentCodeFilesInfo.keySet().containsAll(Arrays.asList(sourceFiles))) {
            // already loaded
            return;
        }

        // clear everything
        clearAllSnippetHighlights();
        snippetMap.clear();
        
        if (sourceFiles == null) {
            // being reset to having no source files; need to clear everything
            currentCodeFilesInfo = null;
            configureCodePane(false);
            configureSnippetSetsComboBox();
 
        } else {
            currentCodeFilesInfo = new HashMap<URL, CodeFileInfo>();
            boolean needProcessing = false;
            for (URL sourceFile : sourceFiles) {
                // look in cache first to avoid unnecessary processing
                CodeFileInfo cachedFilesInfo = codeCache.get(sourceFile);
                
                currentCodeFilesInfo.put(sourceFile, cachedFilesInfo);
                
                if (cachedFilesInfo == null) {
                    needProcessing = true;
                }
            }
            configureCodePane(true);

            if (needProcessing) {
                // Do it on a separate thread
                new SourceProcessor(currentCodeFilesInfo).execute();
            } else {
                for (CodeFileInfo codeFileInfo : currentCodeFilesInfo.values()) {
                    registerSnippets(codeFileInfo);
                    createCodeFileTab(codeFileInfo);
                }
                configureSnippetSetsComboBox();
            }
        }       
    }
    
    private class SourceProcessor extends SwingWorker<Void, CodeFileInfo> {
        private final Map<URL, CodeFileInfo> codeFilesInfo;

        public SourceProcessor(Map<URL, CodeFileInfo> codeFilesInfo) {
            this.codeFilesInfo = codeFilesInfo;
        }

        public Void doInBackground() {
            for (Map.Entry<URL, CodeFileInfo> entry : codeFilesInfo.entrySet()) {
                // if not already fetched from cache, then process source code
                if (entry.getValue() == null) {
                    entry.setValue(initializeCodeFileInfo(entry.getKey()));
                }
                publish(entry.getValue());
            }
            
            return null;
        }
        
        // old signature, needed until OS X migrates to newer process() signature
        protected void process(CodeFileInfo... codeFileInfoSet) {
            for (CodeFileInfo codeFileInfo : codeFileInfoSet) {
                processOneFile(codeFileInfo);
            }
        }
        // updated signature
        protected void process(List<CodeFileInfo> codeFileInfoList) {
            for (CodeFileInfo codeFileInfo : codeFileInfoList) {
                processOneFile(codeFileInfo);
            }
        }

        private void processOneFile(CodeFileInfo codeFileInfo) {
            // Store code info no matter what
            codeCache.put(codeFileInfo.url, codeFileInfo);

            // It's possible that by now another set of source files has been loaded.
            // so check first before adding the source tab;'
            if (currentCodeFilesInfo == codeFilesInfo) {
                registerSnippets(codeFileInfo);
                createCodeFileTab(codeFileInfo);
            } else {
                logger.log(Level.FINEST, "source files changed before " +
                        Utilities.getURLFileName(codeFileInfo.url) + "was processed.");
            }

        } // processOneFile
        
        private CodeFileInfo initializeCodeFileInfo(URL sourceFile) {
            CodeFileInfo codeFileInfo = new CodeFileInfo();
            codeFileInfo.url = sourceFile;
            codeFileInfo.styled = loadSourceCode(sourceFile);
            codeFileInfo.textPane = new JEditorPane();
            codeFileInfo.textPane.setHighlighter(new SnippetHighlighter());
            makeSelectionTransparent(codeFileInfo.textPane, 180);
            codeFileInfo.veneer = new CodeVeneer(codeFileInfo);
            Stacker layers = new Stacker(codeFileInfo.textPane);
            layers.add(codeFileInfo.veneer, JLayeredPane.POPUP_LAYER);
            codeFileInfo.textPane.setContentType("text/html");
            codeFileInfo.textPane.setEditable(false); // HTML won't display correctly without this!
            codeFileInfo.textPane.setText(codeFileInfo.styled);
            codeFileInfo.textPane.setCaretPosition(0);

            // MUST parse AFTER textPane Document has been created to ensure
            // snippet offsets are relative to the editor pane's Document model
            codeFileInfo.snippets = SnippetParser.parse(codeFileInfo.textPane.getDocument());
            return codeFileInfo;
        }

        protected void done() {
            try {
                get();
                configureSnippetSetsComboBox();
            } catch (Exception ex) {
                System.err.println(ex);
            }
        }
    } // SourceProcessor

    // Called from Source Processing Thread in SwingWorker
 
    private void configureCodePane(boolean hasCodeFiles) {
        if (hasCodeFiles) {
            if (codeTabbedPane == null) {
                codeTabbedPane = new JTabbedPane();
                codePanel.remove(noCodeLabel);
                codePanel.add(codeTabbedPane);
                revalidate();
            } else {
                codeTabbedPane.removeAll();
            }
        } else {
            // No code files
            if (codeTabbedPane != null) {
                codePanel.remove(codeTabbedPane);
                codeTabbedPane = null;
                codePanel.add(noCodeLabel);
                revalidate();               
            }
        }
    }
    
    private void createCodeFileTab(CodeFileInfo codeFileInfo) {
        JLayeredPane layeredPane = JLayeredPane.getLayeredPaneAbove(codeFileInfo.textPane);
        JScrollPane scrollPane = new JScrollPane(layeredPane);
        scrollPane.setBorder(null);
        JPanel tabPanel = new JPanel();
        tabPanel.setLayout(new BorderLayout());
        
        tabPanel.add(scrollPane, BorderLayout.CENTER);
        
        codeTabbedPane.addTab(Utilities.getURLFileName(codeFileInfo.url), tabPanel);
    }

    private void registerSnippets(CodeFileInfo codeFileInfo) {
        for(String snippetKey: codeFileInfo.snippets.keySet()) {
            ArrayList<Snippet> snippetCodeList = codeFileInfo.snippets.get(snippetKey);
            for(Snippet snippet: snippetCodeList) {
                snippetMap.add(snippetKey, codeFileInfo.url, snippet);
            }
        }
    }
    
    private void configureSnippetSetsComboBox() {        
        TreeSet sortedSnippets = new TreeSet(snippetMap.keySet());
        String snippetSetKeys[] = (String[])sortedSnippets.toArray(new String[0]);
        
        DefaultComboBoxModel snippetModel = new DefaultComboBoxModel();
        for(String snippetKey : snippetSetKeys) {
            snippetModel.addElement(snippetKey);
        }
        snippetModel.insertElementAt(NO_SNIPPET_SELECTED, 0);
        snippetModel.setSelectedItem(NO_SNIPPET_SELECTED);
        snippetComboBox.setModel(snippetModel);
        codeHighlightBar.setVisible(snippetModel.getSize() > 1);
        
    }

    /**
     * Reads the java source file at the specified URL and returns an
     * HTML version stylized for display
     */
    protected String loadSourceCode(URL sourceUrl) {
        InputStream is;
        InputStreamReader isr;
        CodeStyler cv = new CodeStyler();        
        String styledCode = "<html><body bgcolor=\"#ffffff\"><pre>";
        
        try {
            is = sourceUrl.openStream();
            isr = new InputStreamReader(is, "UTF-8");
            BufferedReader reader = new BufferedReader(isr);
            
            // Read one line at a time, htmlizing using super-spiffy
            // html java code formating utility from www.CoolServlets.com
            String line = reader.readLine();
            while(line != null) {
                styledCode += cv.syntaxHighlight(line) + " \n ";
                line = reader.readLine();
            }
            styledCode += "</pre></body></html>";
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Could not load file from: " + sourceUrl;
        }
        return styledCode;
    }
    
    public void clearAllSnippetHighlights() {
        if (currentCodeFilesInfo != null) {
            snippetMap.setCurrentSet(null);
            for(CodeFileInfo code : currentCodeFilesInfo.values()) {
                if (code != null && code.textPane != null) {
                    Highlighter highlighter = code.textPane.getHighlighter();
                    highlighter.removeAllHighlights();
                    code.textPane.repaint();
                    code.veneer.repaint();
                }
            }
        }
    }
    
    public void highlightSnippetSet(String snippetKey) {

        clearAllSnippetHighlights();
        snippetMap.setCurrentSet(snippetKey);
        
        URL files[] = snippetMap.getFilesForSet(snippetKey);
        CodeFileInfo firstCodeFileInfo = null;
        Snippet firstSnippet = null;
        for(URL file : files) {
            CodeFileInfo codeFileInfo = codeCache.get(file);
            Highlighter highlighter = codeFileInfo.textPane.getHighlighter();
            // now add highlight for each snippet in this file associated 
            // with the key
            Snippet snippets[] = snippetMap.getSnippetsForFile(snippetKey, file);
            if (firstCodeFileInfo == null) {
                firstCodeFileInfo = codeFileInfo;
                firstSnippet = snippets[0];
            }
            for (Snippet snippet : snippets) {
                try {
                    highlighter.addHighlight(snippet.startLine,
                            snippet.endLine, snippetPainter );
                    codeFileInfo.veneer.repaint();
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        }
        scrollToSnippet(firstCodeFileInfo, firstSnippet);
        snippetComboBox.setSelectedItem(snippetKey);
    }
    
    protected void scrollToSnippet(CodeFileInfo codeFileInfo, Snippet snippet) {
        if (!codeFileInfo.textPane.isShowing()) {
            // Need to switch tabs to source file with first snippet
            // remind: too brittle - need to find component some other way
            codeTabbedPane.setSelectedComponent(
                    JLayeredPane.getLayeredPaneAbove(codeFileInfo.textPane).getParent().getParent().getParent());
        }
        try {
            Rectangle r1 = codeFileInfo.textPane.modelToView(snippet.startLine);
            Rectangle r2 = codeFileInfo.textPane.modelToView(snippet.endLine);
            codeFileInfo.textPane.scrollRectToVisible(
                    SwingUtilities.computeUnion(r1.x, r1.y,
                    r1.width, r1.height, r2));
        } catch (BadLocationException e) {
            System.err.println(e);
        }
        nextSnippetAction.setEnabled(snippetMap.nextSnippetExists());
        previousSnippetAction.setEnabled(snippetMap.previousSnippetExists());
    }
    
    protected String getCurrentSnippetKey() {
        String key = snippetMap.getCurrentSet();
        return key != null? key : NO_SNIPPET_SELECTED;
    }
    
    protected Snippet getCurrentSnippet() {
        return snippetMap.getCurrentSnippet();
    }
    
    protected void moveToFirstSnippet() {
       Snippet firstSnippet = snippetMap.firstSnippet();    
       if (firstSnippet != null) {
            CodeFileInfo codeFileInfo = codeCache.get(snippetMap.getFileForSnippet(firstSnippet));
            scrollToSnippet(codeFileInfo, firstSnippet);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }        
    }
    
    protected void moveToNextSnippet() {
        Snippet nextSnippet = snippetMap.nextSnippet();    
        if (nextSnippet != null) {
            CodeFileInfo codeFileInfo = codeCache.get(snippetMap.getFileForSnippet(nextSnippet));
            scrollToSnippet(codeFileInfo, nextSnippet);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }
    
    protected void moveToPreviousSnippet() {
        Snippet previousSnippet = snippetMap.previousSnippet();
        if (previousSnippet != null) {
            CodeFileInfo codeFileInfo = codeCache.get(snippetMap.getFileForSnippet(previousSnippet));
            scrollToSnippet(codeFileInfo, previousSnippet);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }         
  
    }
 
    protected void moveToLastSnippet() {
       Snippet lastSnippet = snippetMap.lastSnippet();    
       if (lastSnippet != null) {
            CodeFileInfo codeFileInfo = codeCache.get(snippetMap.getFileForSnippet(lastSnippet));
            scrollToSnippet(codeFileInfo, lastSnippet);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }        
    }
    
    private class SnippetActivator implements ActionListener {        
        public void actionPerformed(ActionEvent e) {
            String snippetKey = (String)snippetComboBox.getSelectedItem();
            if (!snippetKey.equals(NO_SNIPPET_SELECTED)) {
                logger.log(Level.FINEST, "highlighting new snippet:"+snippetKey+".");
                highlightSnippetSet(snippetKey);
            } else {
                clearAllSnippetHighlights();
            }
        }
    }
    private class FirstSnippetAction extends AbstractAction {
        public FirstSnippetAction() {
            super("FirstSnippet");
            putValue(AbstractAction.SHORT_DESCRIPTION, 
                    getString("CodeViewer.snippets.navigateFirst",
                              "move to first code snippet within highlighted set"));
        } 
        public void actionPerformed(ActionEvent e) {
            moveToFirstSnippet();
        }        
    }
    
    private class NextSnippetAction extends AbstractAction {
        public NextSnippetAction() {
            super("NextSnippet");
            putValue(AbstractAction.SHORT_DESCRIPTION, 
                    getString("CodeViewer.snippets.navigateNext",
                              "move to next code snippet within highlighted set"));
        } 
        public void actionPerformed(ActionEvent e) {
            moveToNextSnippet();
        }        
    }
    
    private class PreviousSnippetAction extends AbstractAction {
        public PreviousSnippetAction() {
            super("PreviousSnippet");
            putValue(AbstractAction.SHORT_DESCRIPTION, 
                    getString("CodeViewer.snippets.navigatePrevious",
                              "move to previous code fragment within highlighted set"));
        }
        public void actionPerformed(ActionEvent e) {
            moveToPreviousSnippet();
        }
    }
  
    private class LastSnippetAction extends AbstractAction {
        public LastSnippetAction() {
            super("LastSnippet");
            putValue(AbstractAction.SHORT_DESCRIPTION, 
                    getString("CodeViewer.snippets.navigateLast",
                              "move to last code snippet within highlighted set"));
        } 
        public void actionPerformed(ActionEvent e) {
            moveToLastSnippet();
        }        
    }
    
    class SnippetCellRenderer implements ListCellRenderer {
        private JLabel delegate;
        
        public SnippetCellRenderer(ListCellRenderer delegate) {
            this.delegate = (JLabel)delegate;
        }
        public Component getListCellRendererComponent(JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            
            JLabel renderer = (JLabel)((ListCellRenderer)delegate).getListCellRendererComponent(list,
                    value, index, isSelected, cellHasFocus);
            
            int count = snippetMap.getSnippetCountForSet((String)value);
            
            Color foreground = renderer.getForeground();
            Color countForeground = Utilities.deriveColorHSB(foreground, 
                    0, 0, isSelected? -.6f : .45f);
            
            String text = "<html><font color=\"" +
                    Utilities.getHTMLColorString(foreground) + "\">" + value + 
                    "</font>" +
                    "<font color=\"" +
                    Utilities.getHTMLColorString(countForeground) + "\">" +
                    (count > 0? " (" + count + (count > 1? " snippets)" : " snippet)") : "") +
                    "</font></html>";

            renderer.setText(text);
            return renderer;
        }
    }
    
    private class SnippetNavigator extends JPanel {
        private String NO_SNIPPET;
        
        private SnippetMap snippetMap;
        
        private JLabel statusLabel;
        private JButton prevButton;
        private JButton nextButton;
        
        private Insets statusInsets = new Insets(1,0,1,0);
        private int arrowSize = 6;
        private int overlap = 6;
        
        public SnippetNavigator(SnippetMap snippetMap) {
            this.snippetMap = snippetMap;
            snippetMap.addPropertyChangeListener(new SnippetHighlightListener());
            
            setLayout(null);
            
            NO_SNIPPET = getString("CodeViewer.snippets.noCodeHighlighted",
                                   "No Code highlight selected");
            
            statusLabel = new JLabel(NO_SNIPPET);
            statusLabel.setHorizontalAlignment(JLabel.CENTER);
            statusLabel.setBorder(new Border() {
                public boolean isBorderOpaque() {
                    return true;
                }
                public Insets getBorderInsets(Component c) {
                    return statusInsets;
                }
                public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
                    if (prevButton.isVisible()) {
                        g.setColor(UIManager.getColor("controlDkShadow"));
                        g.drawLine(x, y, x + w, y);
                        g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
                    }
                }
            });
            add(statusLabel);
            
            prevButton = (JButton)add(new JButton());
            prevButton.setVisible(false);
                       
            nextButton = (JButton)add(new JButton());
            nextButton.setVisible(false);
            
            applyDefaults();
            
        }
        
        public void doLayout() {
            Dimension size = getSize();
            Insets insets = getInsets();
            Dimension labelSize = statusLabel.getPreferredSize();
            
            if (prevButton.isVisible()) {
                Dimension buttonSize = prevButton.getPreferredSize();

                prevButton.setBounds(insets.left, insets.top,
                        buttonSize.width, size.height - insets.top - insets.bottom);

                statusLabel.setBounds(insets.left + buttonSize.width - overlap,
                        insets.top,
                        labelSize.width + (2 * overlap), size.height - insets.top - insets.bottom);
                
                nextButton.setBounds(size.width - buttonSize.width,
                        insets.top,
                        buttonSize.width, size.height - insets.top - insets.bottom);
            } else {
                
                statusLabel.setBounds(insets.left, insets.top,
                        size.width - insets.left - insets.right,
                        size.height - insets.top - insets.bottom);
            }
        }
        
        public Dimension getPreferredSize() {
            Dimension prefSize;
            Insets insets = getInsets();

            Dimension labelSize = statusLabel.getPreferredSize();
           
            if (prevButton.isVisible()) {
                Dimension buttonSize = prevButton.getPreferredSize();
            
                prefSize = new Dimension(buttonSize.width*2 + labelSize.width  +       
                    insets.left + insets.right,
                    Math.max(buttonSize.height, 
                    labelSize.height) + insets.top + insets.bottom);
            } else {
                prefSize = new Dimension(labelSize.width + insets.left + insets.right,
                    labelSize.height + insets.top + insets.bottom);
            }
            return prefSize;
            
        }
        
        public void updateUI() {
            super.updateUI();
            applyDefaults();
        }
        
        protected void applyDefaults() {
            if (prevButton != null) {
                Color arrowColor = UIManager.getColor("Label.foreground");
                Color inactiveColor = UIManager.getColor("Label.disabledText");
                Dimension buttonSize = new Dimension(arrowSize + 12 + overlap, 
                                                     arrowSize + 12 + overlap);
                
                prevButton.setIcon(new ArrowIcon(ArrowIcon.WEST, arrowSize, arrowColor));
                prevButton.setDisabledIcon(new ArrowIcon(ArrowIcon.WEST, arrowSize, inactiveColor));
                prevButton.setPreferredSize(buttonSize);
                nextButton.setIcon(new ArrowIcon(ArrowIcon.EAST, arrowSize, arrowColor));
                nextButton.setDisabledIcon(new ArrowIcon(ArrowIcon.EAST, arrowSize, inactiveColor));
                nextButton.setPreferredSize(buttonSize);
                
                statusLabel.setOpaque(true);
                statusLabel.setFont(UIManager.getFont("Label.font").deriveFont(12f));
            }
        }
        
        public void setNavigatePreviousAction(Action action) {
            setButtonAction(prevButton, action);
        }
        
        public void setNavigateNextAction(Action action) {
            setButtonAction(nextButton, action);
        }
        
        private void setButtonAction(JButton button, Action action) {
            Icon icon = button.getIcon();
            button.setAction(action);
            button.setHideActionText(true);
            button.setIcon(icon); // icon gets obliterated when action set!
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
                
                if (currentKey == null) {
                    statusLabel.setText(NO_SNIPPET);
                    
                } else {
                    
                    String place = "<html><b>"+
                            snippetMap.getIndexForSnippet(snippetMap.getCurrentSnippet()) + 
                            "</b>" +
                            " of " + snippetMap.getSnippetCountForSet(currentKey) + "</html>";
                    statusLabel.setText(place);
                    
                }
                boolean moreThanOne = snippetMap.getSnippetCountForSet(currentKey) > 1;
                
                prevButton.setVisible(moreThanOne);
                prevButton.setEnabled(snippetMap.previousSnippetExists());
                nextButton.setVisible(moreThanOne);
                nextButton.setEnabled(snippetMap.nextSnippetExists());
            }
        }
    }
    
 
    private static class CodeFileInfo {
        public URL url;
        public String styled;
        public HashMap<String,ArrayList<Snippet>> snippets = new HashMap<String,ArrayList<Snippet>>();
        public JEditorPane textPane;
        public JPanel veneer;
    }
    
    private static class Stacker extends JLayeredPane {
        Component master; // dictates sizing, scrolling
        
        public Stacker(Component master) {
            this.master = master;
            setLayout(null);
            add(master, JLayeredPane.DEFAULT_LAYER);
        }
        
        public Dimension getPreferredSize() {
            return master.getPreferredSize();         
        }
        
        public void doLayout() {
            // ensure all layers are sized the same
            Dimension size = getSize();
            Component layers[] = getComponents();
            for(Component layer : layers) {
                layer.setBounds(0, 0, size.width, size.height);
            }                      
        } 
    }
    
    private class CodeVeneer extends JPanel {        
        private CodeFileInfo codeFileInfo;
        
        public CodeVeneer(CodeFileInfo codeFileInfo) {
            this.codeFileInfo = codeFileInfo;
            setOpaque(false);
            setLayout(null);            
        }
        
        @Override
        protected void paintComponent(Graphics g) {

            String snippetKey = getCurrentSnippetKey();
            if (snippetKey != NO_SNIPPET_SELECTED) { 
                // Count total number of snippets for key
                int snippetTotal = 0;
                int snippetIndex = 0;
                ArrayList<Snippet> snippetList = null;
                URL files[] = snippetMap.getFilesForSet(snippetKey);               
                for(URL file : files) {
                    CodeFileInfo codeFileInfo = codeCache.get(file);
                    if (this.codeFileInfo == codeFileInfo) {
                        snippetList = codeFileInfo.snippets.get(snippetKey);
                        snippetIndex = snippetTotal + 1;
                    }
                    snippetTotal += (codeFileInfo.snippets.get(snippetKey).size());
                }
                
                if (snippetList != null) {
                    Snippet currentSnippet = snippetMap.getCurrentSnippet();
                    CodeFileInfo currentSnippetCodeFileInfo = codeCache.get(
                            snippetMap.getFileForSnippet(currentSnippet));
                    
                    Font font = g.getFont();
                    g.setFont(font.deriveFont(10f));
                    FontMetrics metrics = g.getFontMetrics();
                    
                    g.setColor(getHighlightColor());
                    
                    Graphics2D g2Alpha = null; // cache composite                  
                    for(Snippet snippet : snippetList) {
                        Graphics2D g2 = (Graphics2D)g;

                        try {
                            if (currentSnippetCodeFileInfo != codeFileInfo ||
                                    currentSnippet != snippet) {
                                // if not painting the "current" snippet, then fade the glyph
                                if (g2Alpha == null) {
                                    // first time, so create composite
                                    g2Alpha = (Graphics2D)g2.create();                                
                                    g2Alpha.setComposite(AlphaComposite.getInstance(
                                            AlphaComposite.SRC_OVER, 0.6f));
                                }
                                g2 = g2Alpha;
                            }
                            Rectangle snipRect = codeFileInfo.textPane.modelToView(snippet.startLine);
                            
                            //String glyphLabel = snippetIndex++ + "/" + snippetTotal;
                            String glyphLabel = "" + snippetIndex++;
                            Rectangle labelRect = metrics.getStringBounds(glyphLabel, g2).getBounds();
      
                            g2.drawImage(SNIPPET_GLYPH, 0, snipRect.y, this);
                            g2.setColor(Color.black);
                            g2.drawString(glyphLabel,
                                    (SNIPPET_GLYPH.getWidth(this) - labelRect.width)/2,
                                    snipRect.y +
                                    (SNIPPET_GLYPH.getHeight(this) - labelRect.height)/2 +
                                    metrics.getAscent());
                            
                        } catch (BadLocationException e) {
                            System.err.println(e);
                        }
                    }
                }
            }
        }
    }
}
