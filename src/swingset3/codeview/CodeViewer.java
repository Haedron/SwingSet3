/*
 * CodeViewer.java
 *
 * Created on June 6, 2006, 2:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package swingset3.codeview;


import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import swingset3.Utilities;

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
    private static final Color DEFAULT_HIGHLIGHT_COLOR = new Color(255,255,176); 
    private static Image SNIPPET_GLYPH;
    private static final String NO_SNIPPET_SELECTED = "Select One";
    
    private static final Rectangle scrollRect = new Rectangle(5,5,50,50);
    
    static {
        try {
            URL url = CodeViewer.class.getResource("resources/images/SnippetArrow.png");
            SNIPPET_GLYPH = Toolkit.getDefaultToolkit().getImage(url);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
    // Cache all processed code files in case they are reloaded later
    private HashMap <URL,CodeFileInfo>codeCache = new HashMap();
    
    private JLabel snippetSetsLabel;
    private JComboBox snippetSetsComboBox;
    private JTabbedPane codeTabbedPane;

    private Color highlightColor;
    private Highlighter.HighlightPainter snippetPainter;

    // Current code file set
    private URL currentCodeFiles[] = null;
    private CodeFileInfo currentCodeFilesInfo[] = null;
    
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
        initCodeHighlightBar();
        initCodeTabbedPane();
    }
                
    protected void initCodeHighlightBar() {
        
        Box box = Box.createHorizontalBox();
        box.add(Box.createHorizontalStrut(6));  
        add(box, BorderLayout.NORTH);
        
        snippetSetsLabel = new JLabel("Highlight code to: ");
        snippetSetsComboBox = new JComboBox();
        snippetSetsComboBox.setAlignmentY(.51f);
        snippetSetsComboBox.setMaximumRowCount(20);
        snippetSetsComboBox.setPrototypeDisplayValue(NO_SNIPPET_SELECTED + "   "); // temporary item
        snippetSetsComboBox.addActionListener(new SnippetActivator());
        snippetSetsLabel.setLabelFor(snippetSetsComboBox);
        box.add(snippetSetsLabel);
        box.add(snippetSetsComboBox);
        snippetSetsLabel.setVisible(false);
        snippetSetsComboBox.setVisible(false);
              
    }
    
    protected void initCodeTabbedPane() {
        codeTabbedPane = new JTabbedPane();
        add(BorderLayout.CENTER, codeTabbedPane);
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
        }
    }
    
    public Color getHighlightColor() {
        return highlightColor;
    }
    
    public void setSourceFiles(URL sourceFiles[]) {
        if (currentCodeFiles != null && currentCodeFiles.equals(sourceFiles)) {
            // already loaded
            return;
        }
        
        currentCodeFiles = sourceFiles;
        
        codeTabbedPane.removeAll();
        clearAllSnippetHighlights();
        snippetMap.clear();
        
        if (sourceFiles == null) {
            // need to clear everything
            currentCodeFilesInfo = null;
            configureSnippetSetsComboBox();
        } else {
            currentCodeFilesInfo = new CodeFileInfo[sourceFiles.length];
            int i = 0;
            boolean needProcessing = false;
            for(URL sourceFile: sourceFiles) {
                if (sourceFile == null) {
                    throw new NullPointerException("cannot load source file because URL is null");
                }
                currentCodeFilesInfo[i] = codeCache.get(sourceFile);
                if (currentCodeFilesInfo[i++] == null) {
                    needProcessing = true;
                }
            }
            class SourceProcessor extends SwingWorker<CodeFileInfo[], CodeFileInfo> {
                URL[] sourceFiles;
                CodeFileInfo[] codeFileInfos;
                
                public SourceProcessor(URL sourceFiles[], CodeFileInfo[] codeFileInfos) {
                    this.sourceFiles = sourceFiles;
                    this.codeFileInfos = codeFileInfos;
                }
                public CodeFileInfo[] doInBackground() {
                    for(int i = 0; i < sourceFiles.length ; i++) {
                        // if not already fetched from cache, then process source code
                        if (codeFileInfos[i] == null) {                     
                            codeFileInfos[i] = initializeCodeFileInfo(sourceFiles[i]);
                        }
                        publish(codeFileInfos[i]);
                    }
                    return codeFileInfos;
                }
                // old signature, needed until OS X migrates to newer process() signature
                protected void process(CodeFileInfo... codeFileInfoSet) {
                    for(CodeFileInfo codeFileInfo: codeFileInfoSet) {
                        processOneFile(codeFileInfo);
                    }
                } 
                // updated signature
                protected void process(List<CodeFileInfo> codeFileInfoList) {
                    for(CodeFileInfo codeFileInfo: codeFileInfoList) {
                        processOneFile(codeFileInfo);
                    }
                }                
                private void processOneFile(CodeFileInfo codeFileInfo) {
                    // Store code info no matter what
                    codeCache.put(codeFileInfo.url, codeFileInfo);
                    
                    // It's possible that by now another demo has been made the "current" demo,
                    // so check first before adding the source tab;'
                    if (currentCodeFilesInfo == codeFileInfos) {
                        registerSnippets(codeFileInfo);
                        createCodeFileTab(codeFileInfo);
                    } else {
                        System.out.println("Demo was switched!!!!");
                    }
                    
                } // processOneFile
                
                protected void done() {
                    try { 
                         get();
                         configureSnippetSetsComboBox();
                    } catch (Exception ignore) {
                        System.err.println(ignore);
                    }
                }       
                
            } // SourceProcessor
            
     
            if (needProcessing) {
                // Do it on a separate thread
                new SourceProcessor(sourceFiles, currentCodeFilesInfo).execute();
            } else {
                //System.out.println("Just grabbing from cache!");
                for(CodeFileInfo codeFileInfo: currentCodeFilesInfo) {
                    registerSnippets(codeFileInfo);
                    createCodeFileTab(codeFileInfo);
                }
                configureSnippetSetsComboBox();
            }
        }
        
    }
    
    // Called from Source Processing Thread in SwingWorker
    private CodeFileInfo initializeCodeFileInfo(URL sourceFile) {
        CodeFileInfo CodeFileInfo = new CodeFileInfo();
        CodeFileInfo.url = sourceFile;
        CodeFileInfo.styled = loadSourceCode(sourceFile);
        CodeFileInfo.textPane = new JEditorPane();
        //System.out.println("glyph:"+ SNIPPET_GLYPH.getWidth(this));
        CodeFileInfo.textPane.setMargin(
                new Insets(0, SNIPPET_GLYPH.getWidth(this), 0, 0));
        CodeFileInfo.veneer = new CodeVeneer(CodeFileInfo);
        Stacker layers = new Stacker(CodeFileInfo.textPane);
        layers.add(CodeFileInfo.veneer, JLayeredPane.POPUP_LAYER);
        CodeFileInfo.textPane.setContentType("text/html");
        CodeFileInfo.textPane.setEditable(false); // HTML won't display correctly without this!
        CodeFileInfo.textPane.setText(CodeFileInfo.styled);
        CodeFileInfo.textPane.setCaretPosition(0);
        
        // MUST parse AFTER textPane Document has been created to ensure
        // snippet offsets are relative to the editor pane's Document model
        //System.out.println("initializing CodeFileInfo="+sourceFile.getPath());
        CodeFileInfo.snippets = SnippetParser.parse(CodeFileInfo.textPane.getDocument());
        return CodeFileInfo;
    }
    
    private void createCodeFileTab(CodeFileInfo codeFileInfo) {
        JLayeredPane layeredPane = JLayeredPane.getLayeredPaneAbove(codeFileInfo.textPane);
        JScrollPane scrollPane = new JScrollPane(layeredPane);
        scrollPane.setBorder(null);
        JPanel tabPanel = new JPanel();
        tabPanel.setLayout(new BorderLayout());
        
        if (snippetMap.getSize() > 0) {
            
            Box snippetControlBox = Box.createHorizontalBox();
            tabPanel.add(snippetControlBox, BorderLayout.NORTH);
            snippetControlBox.add(Box.createHorizontalGlue());
            SnippetNavigator snippetNavigator = new SnippetNavigator(snippetMap, codeFileInfo.url);
            snippetNavigator.setNavigateFirstAction(firstSnippetAction);
            snippetNavigator.setNavigateNextAction(nextSnippetAction);
            snippetNavigator.setNavigatePreviousAction(previousSnippetAction);
            snippetNavigator.setNavigateLastAction(lastSnippetAction);
            snippetControlBox.add(snippetNavigator);
        }
        
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
            int count = snippetMap.getSnippetCountForSet(snippetKey);
            snippetModel.addElement(snippetKey + " (" + count +
                      " snippet" + (count > 1? "s)" : ")"));
        }
        snippetModel.insertElementAt(NO_SNIPPET_SELECTED, 0);
        snippetModel.setSelectedItem(NO_SNIPPET_SELECTED);
        snippetSetsComboBox.setModel(snippetModel);
        snippetSetsLabel.setVisible(snippetModel.getSize() > 1);
        snippetSetsComboBox.setVisible(snippetModel.getSize() > 1);
        
    }

    /**
     * Reads the java source file at the specified URL and returns an
     * HTML version stylized for display
     */
    protected String loadSourceCode(URL sourceUrl) {
        InputStream is;
        InputStreamReader isr;
        CodeStyler cv = new CodeStyler();        
        String styledCode = new String("<html><body bgcolor=\"#ffffff\"><pre>");
        
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
            styledCode += new String("</pre></body></html>");
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Could not load file from: " + sourceUrl;
        }
        return styledCode;
    }
    
    public void clearAllSnippetHighlights() {
        if (currentCodeFilesInfo != null) {
            
            for(CodeFileInfo code : currentCodeFilesInfo) {
                if (code != null && code.textPane != null) {
                    Highlighter highlighter = code.textPane.getHighlighter();
                    highlighter.removeAllHighlights();
                }
            }
            snippetMap.setCurrentSet(null);
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
        snippetSetsComboBox.setSelectedItem(snippetKey);
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
        Snippet snippet = getCurrentSnippet();
        return snippet != null? snippet.key : NO_SNIPPET_SELECTED;
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
            String snippetKey = (String)snippetSetsComboBox.getSelectedItem();
            if (!snippetKey.equals(NO_SNIPPET_SELECTED)) {
                String key = snippetKey.substring(0, snippetKey.indexOf("(")-1);
                System.out.println("snippet:"+key+".");
                highlightSnippetSet(key);
            } else {
                clearAllSnippetHighlights();
            }
        }
    }
    private class FirstSnippetAction extends AbstractAction {
        public FirstSnippetAction() {
            super("FirstSnippet");
            putValue(AbstractAction.SHORT_DESCRIPTION, 
                    "move to first code snippet within highlighted set");
        } 
        public void actionPerformed(ActionEvent e) {
            moveToFirstSnippet();
        }        
    }
    
    private class NextSnippetAction extends AbstractAction {
        public NextSnippetAction() {
            super("NextSnippet");
            putValue(AbstractAction.SHORT_DESCRIPTION, 
                    "move to previous code snippet within highlighted set");
        } 
        public void actionPerformed(ActionEvent e) {
            moveToNextSnippet();
        }        
    }
    
    private class PreviousSnippetAction extends AbstractAction {
        public PreviousSnippetAction() {
            super("PreviousSnippet");
            putValue(AbstractAction.SHORT_DESCRIPTION, 
                    "move to next code fragment within highlighted sections");
        }
        public void actionPerformed(ActionEvent e) {
            moveToPreviousSnippet();
        }
    }
  
      private class LastSnippetAction extends AbstractAction {
        public LastSnippetAction() {
            super("LastSnippet");
            putValue(AbstractAction.SHORT_DESCRIPTION, 
                    "move to last code snippet within highlighted set");
        } 
        public void actionPerformed(ActionEvent e) {
            moveToLastSnippet();
        }        
    }
    private class CodeFileInfo {
        public URL url;
        public String styled;
        public HashMap<String,ArrayList<Snippet>> snippets = new HashMap();
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
        CodeFileInfo codeFileInfo;
        
        private Rectangle rect = new Rectangle();
        
        public CodeVeneer(CodeFileInfo codeFileInfo) {
            this.codeFileInfo = codeFileInfo;
            setOpaque(false);
            setLayout(null);            
        }
        
        protected void paintComponent(Graphics g) {

            String snippetKey = getCurrentSnippetKey();
            
            if (snippetKey != NO_SNIPPET_SELECTED && snippetMap.getCurrentSnippet() != null) { 
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
                    
                    Rectangle clipRect = g.getClipBounds();
                    
                    Font font = g.getFont();
                    g.setFont(font.deriveFont(9f));
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
                            
                            String glyphLabel = snippetIndex++ + "/" + snippetTotal;
                            Rectangle labelRect = metrics.getStringBounds(glyphLabel, g2).getBounds();
                            //System.out.println(Utilities.getURLFileName(code.url) + " glyph " + glyphLabel +
                            //        " at " + snipRect.x + "," + snipRect.y);
                            g2.drawImage(SNIPPET_GLYPH, 0, snipRect.y, this);
                            g2.drawString(glyphLabel,
                                    0 + (SNIPPET_GLYPH.getWidth(this) - labelRect.width)/2,
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
