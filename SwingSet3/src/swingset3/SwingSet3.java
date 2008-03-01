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

package swingset3;

import swingset3.utilities.AnimatingSplitPane;
import swingset3.utilities.Utilities;
import application.Action;
import application.ResourceMap;
import application.SingleFrameApplication;
import application.View;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import swingset3.codeview.CodeViewer;
import swingset3.utilities.RoundedBorder;
import swingset3.utilities.RoundedPanel;



/**
 *
 * @author  aim
 */
public class SwingSet3 extends SingleFrameApplication  {
    static final Logger logger = Logger.getLogger(SwingSet3.class.getName());
    
    private static ServiceLoader<LookAndFeel> lookAndFeelLoader = ServiceLoader.load(LookAndFeel.class); 
    private static ServiceLoader<DemoList> demoListLoader = ServiceLoader.load(DemoList.class);
    
    public static String title;

    public static final String controlVeryLightShadowKey = "controlVeryLightShadowColor";
    public static final String controlLightShadowKey = "controlLightShadowColor";
    public static final String controlMidShadowKey = "controlMidShadowColor";
    public static final String controlVeryDarkShadowKey = "controlVeryDarkShadowColor";
    public static final String controlDarkShadowKey = "controlDarkShadowColor";
    public static final String titleGradientColor1Key = "SwingSet3.titleGradientColor1";
    public static final String titleGradientColor2Key = "SwingSet3.titleGradientColor2";
    public static final String titleForegroundKey = "SwingSet3.titleForegroundColor";
    public static final String titleFontKey = "SwingSet3.titleFont";
    public static final String subPanelBackgroundColorKey = "SwingSet3.subPanelBackgroundColor";

    public static final int DEMO_WIDTH = 650;
    public static final int DEMO_HEIGHT = 500;
    public static final int SOURCE_HEIGHT = 250;
    
    public static final Insets UPPER_PANEL_INSETS = new Insets(12,12,8,12);
    public static final Insets SOURCE_PANE_INSETS = new Insets(4,8,8,8);
    
    public static final Border EMPTY_BORDER = new EmptyBorder(0,0,0,0);
    public static final Border PANEL_BORDER = new EmptyBorder(10,10,10,10);
        
    static {
        // Property must be set *early* due to Apple Bug#3909714
        if (System.getProperty("os.name").equals("Mac OS X")) {
            System.setProperty("apple.laf.useScreenMenuBar", "true"); 
        }
        
        UIManager.LookAndFeelInfo lafInfo[] = UIManager.getInstalledLookAndFeels();
        for(int i = 0; i < lafInfo.length; i++) {
            if (lafInfo[i].getName().equals("Nimbus")) {
                lafInfo[i] = new UIManager.LookAndFeelInfo("Nimbus",
                        "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
                break;
            }
        }
        UIManager.setInstalledLookAndFeels(lafInfo);
    }
    
    public static void main(String[] args) {
        launch(SwingSet3.class, args);
    }
    
    public static boolean onMac() {
        return System.getProperty("os.name").equals("Mac OS X");
    } 
    
    public static boolean usingNimbus() {
        return UIManager.getLookAndFeel().getName().equals("Nimbus");
    }
    
    private static List<String> readDemoClassNames(Reader reader) throws IOException {
        List<String> demoClassNames = new ArrayList<String>();
        
        BufferedReader breader = new BufferedReader(reader);
        String line;
        while((line = breader.readLine()) != null) {
            demoClassNames.add(line);
        }
        breader.close();
        return demoClassNames;
    }
    
    private static List<String> readDemoClassNames(Manifest manifest) throws IOException {
        // The problem with this approach is that with the entries map there is no way 
        // to specify the order that the demos should appear in the tree.
        // REMIND(aim): remove when sure this will not be supported
        List<String> demoClassNames = new ArrayList<String>();
        
        Map<String,Attributes> entries = manifest.getEntries();

        for (String key : entries.keySet()) {
            Attributes attrs = entries.get(key);
            Iterator attrKeys = attrs.keySet().iterator();            

            boolean isDemoClass = Boolean.parseBoolean(attrs.getValue("SwingSet3-Demo"));
            if (isDemoClass) {
                String demoPath = key.replaceAll("/", ".");
                demoClassNames.add(demoPath.replaceFirst(".class", ""));
            }
        }
        return demoClassNames;
        
    }
    
    private ResourceMap resourceMap;
    
    // Application models
    private String demoListTitle;
    private List<Demo> demoList;
    private PropertyChangeListener demoPropertyChangeListener;
    private Map<String, DemoPanel> runningDemoCache;
    private Demo currentDemo;

    // GUI components
    private JPanel mainPanel;
    private DemoSelectorPanel demoSelectorPanel;
    private AnimatingSplitPane demoSplitPane;
    private JPanel demoContainer;
    private JComponent currentDemoPanel;
    private JComponent demoPlaceholder;
    private JPanel codeContainer;
    private CodeViewer codeViewer;    
    private JCheckBoxMenuItem sourceCodeCheckboxItem;
    private ButtonGroup lookAndFeelRadioGroup;
    
    private JPopupMenu popup;
    
    // Properties
    private List availableLookAndFeels;
    private String lookAndFeel;
    private boolean sourceVisible = true;
    
    // GUI state
    private int sourcePaneLocation;
    private int dividerSize;
    private int codeViewerHeight;    
    
    @Override
    protected void initialize(String args[]) {        
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ex) {
            // not catestrophic
        }
        resourceMap = getContext().getResourceMap();
        
        title = resourceMap.getString("mainFrame.title");
        runningDemoCache = new HashMap();
        setDemoList(resourceMap.getString("demos.title"), getDemoClassNames(args));
        setDemoPlaceholder(new JPanel());

    }
    
    private List<String>getDemoClassNames(String args[]) {
        List<String> demoList = new ArrayList<String>();
        boolean augment = false;
        Exception exception = null; 

        // First look for any demo list files specified on the command-line
        List<String> userDemoList = new ArrayList<String>();
        for(String arg : args) {
            if (arg.equals("-a") || arg.equals("-augment")) {
                augment = true;
            } else {
                // process argument as filename containing names of demo classes
                try {
                    userDemoList.addAll(readDemoClassNames(new FileReader(arg) /*filename*/));
                    
                } catch (IOException ex) {
                    exception = ex;
                    logger.log(Level.WARNING, "unable to read demo class names from file: "+arg, ex);
                }
            }
        }
        
        if (userDemoList.isEmpty() || augment) {
            // Use ServiceLoader to find all DemoList implementations that may exist
            // within jar files supplied to swingset3
            demoListLoader.iterator();
            for(DemoList list: demoListLoader) {
                demoList.addAll(list.getDemoClassNames());
            }
        }
        demoList.addAll(userDemoList);
        
        if (demoList.isEmpty()) {
            displayErrorMessage(resourceMap.getString("error.noDemosLoaded"), 
                    exception);
        }        
        return demoList;
  
    }

    public void setDemoList(String demoListTitle, List<String> demoClassNamesList) {              
        List<Demo> demoList = new ArrayList<Demo>();
        for(String demoClassName: demoClassNamesList) {
            demoList.add(createDemo(demoClassName));
        }
        this.demoListTitle = demoListTitle;
        this.demoList = demoList;
    }
    
    /**
     */
    protected Demo createDemo(String demoClassName) {        
        Class<?> demoClass = null;
        Demo demo = null;
        try {
            demoClass = Class.forName(demoClassName);
        } catch (ClassNotFoundException cnfe) {
            logger.log(Level.WARNING, "demo class not found:"+ demoClassName);
        }        
        if (demoClass != null) {
            // Wrap Demo 
            demo = new Demo(demoClass);            
            demo.addPropertyChangeListener(getDemoPropertyChangeListener());          
        }
        return demo;
    }
    
    protected PropertyChangeListener getDemoPropertyChangeListener() {
        if (demoPropertyChangeListener == null) {
            demoPropertyChangeListener = new DemoPropertyChangeListener();
        }
        return demoPropertyChangeListener;
    }
    
    @Override 
    protected void startup() {
        UIManager.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName().equals("lookAndFeel")) {
                    configureDefaults();
                }
            }
        });
        
        configureDefaults();
        
        View view = getMainView();
        view.setComponent(createMainPanel());
        view.setMenuBar(createMenuBar());
        
        // application framework should handle this!
        getMainFrame().setIconImage(resourceMap.getImageIcon("Application.icon").getImage());
        
        show(view);
     
    } 
    
    protected void configureDefaults() {
        
        // Color palette algorithm courtesy of Jasper Potts
        Color controlColor = UIManager.getColor("control");
        
	UIManager.put(controlVeryLightShadowKey, 
                Utilities.deriveColorHSB(controlColor, 0, 0, -0.02f));
        UIManager.put(controlLightShadowKey, 
                Utilities.deriveColorHSB(controlColor, 0, 0, -0.06f));
        UIManager.put(controlMidShadowKey, 
                Utilities.deriveColorHSB(controlColor, 0, 0, -0.16f));
        UIManager.put(controlVeryDarkShadowKey, 
                Utilities.deriveColorHSB(controlColor, 0, 0, -0.5f));
        UIManager.put(controlDarkShadowKey, 
                Utilities.deriveColorHSB(controlColor, 0, 0, -0.32f));
        
        // Calculate gradient colors for title panels
        Color titleColor = UIManager.getColor(usingNimbus()? "nimbusBase" : "activeCaption");        
        float hsb[] = Color.RGBtoHSB(
                titleColor.getRed(), titleColor.getGreen(), titleColor.getBlue(), null);
        UIManager.put(titleGradientColor1Key, 
                Color.getHSBColor(hsb[0]-.013f, .15f, .85f));
        UIManager.put(titleGradientColor2Key, 
                Color.getHSBColor(hsb[0]-.005f, .24f, .80f));
        UIManager.put(titleForegroundKey, 
                Color.getHSBColor(hsb[0], .54f, .40f));
       
        Font labelFont = UIManager.getFont("Label.font");
        UIManager.put(titleFontKey, labelFont.deriveFont(Font.BOLD, labelFont.getSize()+4f));        
 
        Color panelColor = UIManager.getColor("Panel.background");
        UIManager.put(subPanelBackgroundColorKey, 
                Utilities.deriveColorHSB(panelColor, 0, 0, -.06f));
        
    }  
    
    protected JComponent createMainPanel() {
        
        // Create main panel with demo selection on left and demo/source on right
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
       
        // Create demo selector panel on left
        demoSelectorPanel = new DemoSelectorPanel(demoListTitle, demoList);
        demoSelectorPanel.addPropertyChangeListener(new DemoSelectionListener());
        mainPanel.add(demoSelectorPanel, BorderLayout.WEST);
        
        // Create splitpane on right to hold demo and source code
        demoSplitPane = new AnimatingSplitPane(JSplitPane.VERTICAL_SPLIT);
        demoSplitPane.setDividerLocation(400);
        demoSplitPane.setBorder(EMPTY_BORDER);
        mainPanel.add(demoSplitPane, BorderLayout.CENTER);
        
        demoContainer = new JPanel();
        demoContainer.setLayout(new BorderLayout());
        demoContainer.setBorder(PANEL_BORDER);
        demoSplitPane.setTopComponent(demoContainer);

        // Create pane to contain running demo
        demoContainer.add(demoPlaceholder, BorderLayout.CENTER);
        currentDemoPanel = demoPlaceholder;
                
        // Create collapsible source code pane

        codeViewer = new CodeViewer();
        codeViewer.setPreferredSize(new Dimension(DEMO_WIDTH, SOURCE_HEIGHT));
        codeContainer = new JPanel(new BorderLayout());
        codeContainer.add(codeViewer);
        codeContainer.setBorder(PANEL_BORDER);
        demoSplitPane.setBottomComponent(codeContainer);
        
        addPropertyChangeListener(new SwingSetPropertyListener());
        
        sourcePaneLocation = demoSplitPane.getDividerLocation();
        dividerSize = demoSplitPane.getDividerSize();        
        
        // Create shareable popup menu for demo actions
        popup = new JPopupMenu();
        popup.add(new EditPropertiesAction());
        popup.add(new ViewCodeSnippetAction());

        return mainPanel;
    }
    
    protected JMenuBar createMenuBar() {
    
        // Create menubar
        JMenuBar menubar = new JMenuBar();
        menubar.setName("menubar");
        
        // Create file menu
        JMenu fileMenu = new JMenu();
        fileMenu.setName("file");
        menubar.add(fileMenu);
       
        // Create View menu
        JMenu viewMenu = new JMenu();
        viewMenu.setName("view");
        // View -> Look and Feel       
        viewMenu.add(createLookAndFeelMenu());
        // View -> Source Code Visible
        sourceCodeCheckboxItem = new JCheckBoxMenuItem();
        sourceCodeCheckboxItem.setSelected(isSourceCodeVisible());
        sourceCodeCheckboxItem.setName("sourceCodeCheckboxItem");
        sourceCodeCheckboxItem.addChangeListener(new SourceVisibilityChangeListener());
        viewMenu.add(sourceCodeCheckboxItem);
        menubar.add(viewMenu);

        return menubar;
    }
    
    protected JMenu createLookAndFeelMenu() {
        JMenu menu = new JMenu();
        menu.setName("lookAndFeel");
        
        // Look for toolkit look and feels first
        UIManager.LookAndFeelInfo lookAndFeelInfos[] = UIManager.getInstalledLookAndFeels();
        lookAndFeel = UIManager.getLookAndFeel().getClass().getName();
        lookAndFeelRadioGroup = new ButtonGroup();
        for(UIManager.LookAndFeelInfo lafInfo: lookAndFeelInfos) {
            menu.add(createLookAndFeelItem(lafInfo.getName(), lafInfo.getClassName()));
        }  
        // Now load any look and feels defined externally as service via java.util.ServiceLoader
        lookAndFeelLoader.iterator();
        for (LookAndFeel laf : lookAndFeelLoader) {           
            menu.add(createLookAndFeelItem(laf.getName(), laf.getClass().getName()));
        }
         
        return menu;
    }
    
    protected JRadioButtonMenuItem createLookAndFeelItem(String lafName, String lafClassName) {
        JRadioButtonMenuItem lafItem = new JRadioButtonMenuItem();

        lafItem.setSelected(lafClassName.equals(lookAndFeel));
        lafItem.setHideActionText(true);
        lafItem.setAction(getContext().getActionMap().get("setLookAndFeel"));
        lafItem.setText(lafName);
        lafItem.setActionCommand(lafClassName);
        lookAndFeelRadioGroup.add(lafItem);
        
        return lafItem;
    }
       
    // For displaying error messages to user
    protected void displayErrorMessage(String message, Exception ex) {
        JPanel messagePanel = new JPanel(new BorderLayout());       
        JLabel label = new JLabel(message);
        messagePanel.add(label);
        if (ex != null) {
            RoundedPanel panel = new RoundedPanel(new BorderLayout());
            panel.setBorder(new RoundedBorder());
            
            // remind(aim): provide way to allow user to see exception only if desired
            StringWriter writer = new StringWriter();
            ex.printStackTrace(new PrintWriter(writer));
            JTextArea exceptionText = new JTextArea();
            exceptionText.setText("Cause of error:\n" +
                    writer.getBuffer().toString());
            exceptionText.setBorder(new RoundedBorder());
            exceptionText.setOpaque(false);
            exceptionText.setBackground(
                    Utilities.deriveColorHSB(UIManager.getColor("Panel.background"),
                    0, 0, -.2f));
            JScrollPane scrollpane = new JScrollPane(exceptionText);
            scrollpane.setBorder(EMPTY_BORDER);
            scrollpane.setPreferredSize(new Dimension(600,240));
            panel.add(scrollpane);
            messagePanel.add(panel, BorderLayout.SOUTH);            
        }
        JOptionPane.showMessageDialog(getMainFrame(), messagePanel, 
                resourceMap.getString("error.title"),
                JOptionPane.ERROR_MESSAGE);
                
    }
    
    public void setDemoPlaceholder(JComponent demoPlaceholder) {
        if (this.demoPlaceholder == demoPlaceholder) {
            return;
        }
        JComponent oldDemoPlaceholder = this.demoPlaceholder;
        this.demoPlaceholder = demoPlaceholder;
        firePropertyChange("demoPlaceholder", oldDemoPlaceholder, demoPlaceholder);
    }
    
    public JComponent getDemoPlaceholder() {
        return demoPlaceholder;
    }
    
    public void setCurrentDemo(Demo demo) {
        if (currentDemo == demo) {
            return; // already there
        }
        Demo oldCurrentDemo = currentDemo;        
        currentDemo = demo;
        if (demo != null) {
            DemoPanel demoPanel = runningDemoCache.get(demo.getName());
            if (demoPanel == null || demo.getDemoComponent() == null) {
                demo.startInitializing();
                demoPanel = new DemoPanel(demo);  
                demoPanel.setPreferredSize(currentDemoPanel.getPreferredSize());
                runningDemoCache.put(demo.getName(), demoPanel);
            } 
           
            demoContainer.remove(currentDemoPanel);
            currentDemoPanel = demoPanel;
            demoContainer.add(currentDemoPanel, BorderLayout.CENTER);
            demoContainer.revalidate();
            demoContainer.repaint();
            getMainFrame().validate();
        }

        if (currentDemo == null) {
            demoContainer.add(BorderLayout.CENTER, demoPlaceholder);
        }
        
        if (isSourceCodeVisible()) {
            codeViewer.setSourceFiles(currentDemo != null?
                currentDemo.getSourceFiles() : null);
        }
        getMainFrame().setTitle(title +
                (currentDemo != null? (" :: " + currentDemo.getName()) : ""));
                
        firePropertyChange("currentDemo", oldCurrentDemo, demo);
    }
   
    
    public Demo getCurrentDemo() {
        return currentDemo;
    }
    
    public void setLookAndFeel(String lookAndFeel) throws ClassNotFoundException,
        InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        
        String oldLookAndFeel = this.lookAndFeel;
        
	if (oldLookAndFeel != lookAndFeel) {
            UIManager.setLookAndFeel(lookAndFeel);
            this.lookAndFeel = lookAndFeel;
            updateLookAndFeel();
            firePropertyChange("lookAndFeel", oldLookAndFeel, lookAndFeel);                     
	}
    }
    
    @Action 
    public void setLookAndFeel() {
        ButtonModel model = lookAndFeelRadioGroup.getSelection();
        String lookAndFeelName = model.getActionCommand();
        try {
            setLookAndFeel(lookAndFeelName);
        } catch (Exception ex) {
            displayErrorMessage(resourceMap.getString("error.unableToChangeLookAndFeel") +
                    "to "+lookAndFeelName, ex);
        }
    }
    
    public String getLookAndFeel() {
        return lookAndFeel;
    }

    public void setSourceCodeVisible(boolean sourceVisible) {
        boolean oldSourceVisible = this.sourceVisible;
        this.sourceVisible = sourceVisible;
        firePropertyChange("sourceCodeVisible", oldSourceVisible, sourceVisible);
    }
    
    public boolean isSourceCodeVisible() {
        return sourceVisible;       
    } 
    
    private void updateLookAndFeel() {
        Window windows[] = Frame.getWindows();

        for(Window window : windows) {
            SwingUtilities.updateComponentTreeUI(window);
            for(DemoPanel demoPanel : runningDemoCache.values()) {
                SwingUtilities.updateComponentTreeUI(demoPanel);
            }
        }
    }

    // hook used to detect if any components in the demo have registered a
    // code snippet key for the their creation code inside the source
    private void registerPopups(Component component) {
        
        if (component instanceof Container) {
            Component children[] = ((Container)component).getComponents();
            for(Component child: children) {
                if (child instanceof JComponent) {
                    registerPopups(child);
                }
            }
        }
        if (component instanceof JComponent) {
            JComponent jcomponent = (JComponent)component;
            String snippetKey = (String)jcomponent.getClientProperty("snippetKey");
            if (snippetKey != null) {
                jcomponent.setComponentPopupMenu(popup);
            }
        }
    }    
    
    private class DemoSelectionListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent event) {
            if (event.getPropertyName().equals("selectedDemo")) {
                setCurrentDemo((Demo)event.getNewValue());
            }
        }
    }
           
    
    // registered on Demo to detect when the demo component is instantiated.
    // we need this because when we embed the demo inside an HTML description pane,
    // we don't have control over the demo component's instantiation
    private class DemoPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            String propertyName = e.getPropertyName();
            if (propertyName.equals("demoComponent")) {
                Demo demo = (Demo)e.getSource();
                JComponent demoComponent = (JComponent)e.getNewValue();
                if (demoComponent != null) {
                    demoComponent.putClientProperty("swingset3.demo", demo);
                    demoComponent.addHierarchyListener(new DemoVisibilityListener());
                    registerPopups(demoComponent);
                }
            } 
        }
    }
    
    private class DemoVisibilityListener implements HierarchyListener {
        public void hierarchyChanged(HierarchyEvent event) {
            if ((event.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) > 0) {
                JComponent component = (JComponent)event.getComponent();
                final Demo demo = (Demo)component.getClientProperty("swingset3.demo");
                if (!component.isShowing()) {
                    demo.stop();
                } else {
                    System.out.println("calling start");
                    demoContainer.revalidate();
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            demo.start();
                        }
                    });
                }
            }            
        }        
    }
    // activated when user selects/unselects checkbox menu item
    private class SourceVisibilityChangeListener implements ChangeListener {
        public void stateChanged(ChangeEvent event) {
            setSourceCodeVisible(sourceCodeCheckboxItem.isSelected());
        }
    }

    private class SwingSetPropertyListener implements PropertyChangeListener {       
        public void propertyChange(PropertyChangeEvent event) {
            String propertyName = event.getPropertyName();
            if (propertyName.equals("sourceCodeVisible")) {
                boolean sourceVisible = ((Boolean)event.getNewValue()).booleanValue();
                if (sourceVisible) {
                    // update codeViewer in case current demo changed while
                    // source was invisible
                    codeViewer.setSourceFiles(currentDemo != null?
                        currentDemo.getSourceFiles() : null);
                }
                demoSplitPane.setExpanded(!sourceVisible);
                sourceCodeCheckboxItem.setSelected(sourceVisible);
            } 
        }        
    } 
 
    public class ViewCodeSnippetAction extends AbstractAction {
        public ViewCodeSnippetAction() {
            super("View Source Code");
        }
        public void actionPerformed(ActionEvent actionEvent) {
            Container popup = (JComponent)actionEvent.getSource();
            while(popup != null && !(popup instanceof JPopupMenu)) {
                popup = popup.getParent();
            }
            JComponent target = (JComponent)((JPopupMenu)popup).getInvoker();
            setSourceCodeVisible(true);
            
            String snippetKey = (String)target.getClientProperty("snippetKey");
            if (snippetKey != null) {
                codeViewer.highlightSnippetSet(snippetKey);
            } else {
                logger.log(Level.WARNING, "can't find source code snippet for:" + snippetKey);
            }                                    
        }
    }
    
    public class EditPropertiesAction extends AbstractAction {
        public EditPropertiesAction() {
            super("Edit Properties");
        }
        public boolean isEnabled() {
            return false;
        }
        public void actionPerformed(ActionEvent actionEvent) {
            
        }
    } 
}
