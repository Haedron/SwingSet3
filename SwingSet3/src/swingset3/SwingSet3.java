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

import application.Action;
import application.ResourceMap;
import application.SingleFrameApplication;
import application.View;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jnlp.ServiceManager;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.interpolation.PropertySetter;
import org.jdesktop.animation.transitions.ComponentState;
import org.jdesktop.animation.transitions.Effect;
import org.jdesktop.animation.transitions.EffectsManager;
import org.jdesktop.animation.transitions.ScreenTransition;
import org.jdesktop.animation.transitions.TransitionTarget;
import org.jdesktop.animation.transitions.effects.CompositeEffect;
import org.jdesktop.animation.transitions.effects.FadeIn;
import swingset3.codeview.CodeViewer;



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

    public static final int DEMO_WIDTH = 650;
    public static final int DEMO_HEIGHT = 500;
    public static final int TREE_WIDTH = 230;
    public static final int SOURCE_HEIGHT = 250;
    public static final Insets UPPER_PANEL_INSETS = new Insets(12,12,8,12);
    public static final Insets TREE_INSETS = new Insets(0,8,0,0);
    public static final Insets SOURCE_PANE_INSETS = new Insets(4,8,8,8);
        
    static {
        // Property must be set *early* due to Apple Bug#3909714
        if (System.getProperty("os.name").equals("Mac OS X")) {
            System.setProperty("apple.laf.useScreenMenuBar", "true"); 
        }
    }
    
    public static void main(String[] args) {
        launch(SwingSet3.class, args);
    }
    
    public static boolean onMac() {
        return System.getProperty("os.name").equals("Mac OS X");
    } 
    
    private static List<String> readDemoClassNames(Reader reader) throws IOException {
        ArrayList<String> demoClassNames = new ArrayList();
        
        BufferedReader breader = new BufferedReader(reader);
        String line = null;
        while((line = breader.readLine()) != null) {
            demoClassNames.add(line);
        }
        breader.close();
        return demoClassNames;
    }
    
    private static List readDemoClassNames(Manifest manifest) throws IOException {
        // The problem with this approach is that with the entries map there is no way 
        // to specify the order that the demos should appear in the tree.
        // REMIND(aim): remove when sure this will not be supported
        ArrayList demoClassNames = new ArrayList();
        
        Map<String,Attributes> entries = manifest.getEntries();
        
        Iterator keys = entries.keySet().iterator();
        while(keys.hasNext()) {
            String key = (String)keys.next();
            Attributes attrs = entries.get(key);
            Iterator attrKeys = attrs.keySet().iterator();
            
            
            boolean isDemoClass = Boolean.parseBoolean(attrs.getValue("SwingSet3-Demo"));
            if (isDemoClass) {
                String demoPath = key.replaceAll("/", ".");
                demoClassNames.add(demoPath.replaceFirst(".class",""));
            }
        }
        return demoClassNames;
        
    }
    
    private ResourceMap resourceMap;
    
    // Application models
    private DefaultTreeModel demoTreeModel; // all available demos
    private DefaultMutableTreeNode demoTreeTop; 
    private PropertyChangeListener demoPropertyChangeListener;
    private Map<String, DemoPanel> demoCache;
    private Demo currentDemo;

    // GUI components
    //private JSplitPane vertSplitPane; //JSplitPane has trouble with animation
    private JPanel contentPanel;
    private JPanel upperPanel;
    private DemoSelectorTree demoSelectorTree;
    private JComponent demoPlaceholder;
    private CollapsiblePanel sourceCodePane;
    private CodeViewer codeViewer;    
    private JCheckBoxMenuItem sourceCodeCheckboxItem;
    private JCheckBoxMenuItem animationCheckboxItem;
    private ButtonGroup lookAndFeelRadioGroup;
    
    private JPopupMenu popup;
    
    // Properties
    private List availableLookAndFeels;
    private String lookAndFeel;
    private boolean sourceVisible = true;
    private boolean animationOn = true;
    private int treeRowHeight = 32;
    
    // GUI state
    private int sourcePaneLocation;
    private int dividerSize;
    private int codeViewerHeight;    
    
    // Demo transition animation
    private Animator launchAnimator;
    private ScreenTransition launchTransition;
    private LaunchTransitionTarget launchTransitionTarget;
    private CompositeEffect launchEffect;
    private ScaleMoveEffect scaleMoveEffect;
    private JComponent activeDemoPanel;
    private JComponent nextDemoPanel;
    
    // Look and Feel Change animation
    private Animator lafAnimator;
    private CrossFadeTransition lafTransition;
    private LafTransitionTarget lafTransitionTarget;

    
    // Transient variables
    private transient Point nodePoint = new Point();
    
    @Override
    protected void initialize(String args[]) {        
        resourceMap = getContext().getResourceMap();
        title = resourceMap.getString("mainFrame.title");
        demoCache = new HashMap();
        setDemoPlaceholder(new IntroPanel(DEMO_WIDTH, DEMO_HEIGHT));
        setDemos(resourceMap.getString("demos.title"), getDemoList(args));
    }
    
    private List<String>getDemoList(String args[]) {
        final ArrayList<String> demoList = new ArrayList();
        boolean augment = false;

        // First look for any demo list files specified on the command-line
        ArrayList userDemoList = new ArrayList();
        for(String arg : args) {
            if (arg.equals("-a") || arg.equals("-augment")) {
                augment = true;
                
            } else {
                // process argument as filename containing names of demo classes
                try {
                    userDemoList.addAll(readDemoClassNames(new FileReader(arg) /*filename*/));
                    
                } catch (IOException e) {
                    logger.log(Level.WARNING, "unable to read demo class names from file: "+arg, e);
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
            JOptionPane.showMessageDialog(getMainFrame(),
                    resourceMap.getString("error.noDemosLoaded"),
                    resourceMap.getString("error.title"), JOptionPane.ERROR_MESSAGE);
        }        
        return demoList;
  
    }

    /**
     * Establish the set of demos available inside swingset.
     * Note that the demo classes will be instantiated lazily, 
     * when/if the user clicks on them.
     * @param demosTitle the title of this demo set
     * @param demoClassNamesList the list of demo class names
     */
    public void setDemos(String demosTitle, List<String> demoClassNamesList) {
        
        demoTreeTop = new DefaultMutableTreeNode(demosTitle); 
        
        if (demoTreeModel == null) {
            demoTreeModel = new DefaultTreeModel(demoTreeTop);
        } else {
            // reset root 
            demoTreeModel.setRoot(demoTreeTop);
        }
                
        for(String demoClassName : demoClassNamesList) {
            addDemo(demoClassName);            
        }
    }
    
    /**
     * Adds a list of demos to the currently available set.
     * @param demoClassNamesList the list of demo class names
     */
    public void addDemos(List<String> demoClassNamesList) {
        for(String demoClassName : demoClassNamesList) {
            addDemo(demoClassName);
        }
    }
    
    /**
     * Adds a single demo to the currently available set.
     * @param demoClassName the name of the demo class
     */
    public void addDemo(String demoClassName) {        
        Class<?> demoClass = null;
        Demo demo = null;
        String category = null;
        try {
            demoClass = Class.forName(demoClassName);
        } catch (ClassNotFoundException cnfe) {
            logger.log(Level.WARNING, "demo class not found:"+ demoClassName);
        }
        
        if (demoClass != null) {
            // Wrap Demo 
            demo = new Demo(demoClass);            
            demo.addPropertyChangeListener(getDemoPropertyChangeListener());
            
            // Place demo in appropriate category node in tree
            category = demo.getCategory();           
            Enumeration categories = demoTreeTop.children();
            DefaultMutableTreeNode categoryNode = null;
            while (categories.hasMoreElements()) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)categories.nextElement();
                if (node.getUserObject().equals(category)) {
                    categoryNode = node;
                    break; // category already exists
                }
            }
            
            if (categoryNode == null) {
                // create new category
                categoryNode = new DefaultMutableTreeNode(category);
                demoTreeTop.add(categoryNode);
            }
            logger.log(Level.FINEST,"adding demo:" +
                    (demo != null? demo.getName() : "NULL") + "in category: "+category);
            
            categoryNode.add(new DefaultMutableTreeNode(demo != null? demo :
                Demo.deriveNameFromClassName(demoClassName)));
        }
        
    }
    
    protected PropertyChangeListener getDemoPropertyChangeListener() {
        if (demoPropertyChangeListener == null) {
            demoPropertyChangeListener = new DemoPropertyChangeListener();
        }
        return demoPropertyChangeListener;
    }
    
    @Override 
    protected void startup() {
        
        initColorPalette();
        
        View view = getMainView();
        view.setComponent(createMainPanel());
        view.setMenuBar(createMenuBar());
        
        // application framework should handle this!
        getMainFrame().setIconImage(resourceMap.getImageIcon("Application.icon").getImage());
        
        initAnimation();
        
        show(view);
    } 
    
    protected void initColorPalette() {
        
        // Color palette algorithm courtesy of Jasper Potts
        Color controlColor = UIManager.getColor("control");
        float[] controlHSB = Color.RGBtoHSB(
                controlColor.getRed(), controlColor.getGreen(),
                controlColor.getBlue(), null);
	UIManager.put(controlVeryLightShadowKey, 
                Color.getHSBColor(controlHSB[0], controlHSB[1], controlHSB[2] - 0.02f));
        UIManager.put(controlLightShadowKey, 
                Color.getHSBColor(controlHSB[0], controlHSB[1], controlHSB[2] - 0.06f));
        UIManager.put(controlMidShadowKey, 
                Color.getHSBColor(controlHSB[0], controlHSB[1], controlHSB[2] - 0.16f));
        UIManager.put(controlVeryDarkShadowKey, 
                Color.getHSBColor(controlHSB[0], controlHSB[1], controlHSB[2] - 0.5f));
        UIManager.put(controlDarkShadowKey, 
                Color.getHSBColor(controlHSB[0], controlHSB[1], controlHSB[2] - 0.32f));
        
        /*
         * At the moment setting the background of the tree to "mac tree blue" on
         * aqua laf causes painting problems;   the tree first flashes a white background
         * before the blue is painted and the tree's renderer also paints white
         * if non-opaque ! ?  bug in OSX/Java6?
         *
        if (onMac() && UIManager.getLookAndFeel().isNativeLookAndFeel()) {
            UIManager.put("Tree.background", new ColorUIResource(229, 237, 247));
        }
        */
        
        UIManager.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName().equals("lookAndFeel")) {
                    initColorPalette();
                }
            }
        });
       

    }  
    
    protected JComponent createMainPanel() {
        
        // Create content panel with demos on top, source on bottom
        //vertSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        // Temporarily replaced splitpane with panel because animation fails with splitpane
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        
        // Create top panel to hold demo selector tree and current demo
        upperPanel = new JPanel();
        upperPanel.setLayout(new BorderLayout());
        //vertSplitPane.setTopComponent(upperPanel);
        //upperPanel.setBorder(new EmptyBorder(UPPER_PANEL_INSETS));
        contentPanel.add(upperPanel, BorderLayout.CENTER);

        // Create demo selector tree
        UIManager.put("Tree.paintLines", Boolean.FALSE);
        // We must set these icons here because they will be initialized when TreeUI installs
        // and cannot be replaced afterwards (without touching the TreeUI direction)
        UIManager.put("Tree.expandedIcon", resourceMap.getImageIcon("selectionTree.expandedIcon"));
        UIManager.put("Tree.collapsedIcon", resourceMap.getImageIcon("selectionTree.collapsedIcon"));
        demoSelectorTree = new DemoSelectorTree(demoTreeModel);
        demoSelectorTree.setName("demoSelectorTree");
        demoSelectorTree.setRootVisible(false);
        demoSelectorTree.setShowsRootHandles(true);
        demoSelectorTree.setBorder(new EmptyBorder(TREE_INSETS));
        demoSelectorTree.setRowHeight(getTreeRowHeight());
        demoSelectorTree.addMouseListener(new DemoTreeClickListener());
        demoSelectorTree.setSelectionModel(new DemoTreeSelectionModel());
        JScrollPane scrollPane = new JScrollPane(demoSelectorTree);
        scrollPane.setPreferredSize(new Dimension(TREE_WIDTH,DEMO_HEIGHT)); // wide enough to avoid horiz scrollbar
        scrollPane.setMinimumSize(new Dimension(TREE_WIDTH,DEMO_HEIGHT));
        upperPanel.add(scrollPane, BorderLayout.WEST);
        expandAllCategories(new TreePath(demoTreeTop)); // expand all demos in tree

        // Create pane to contain running demo
        upperPanel.add(demoPlaceholder, BorderLayout.CENTER);
        activeDemoPanel = demoPlaceholder;
                
        // Create collapsible source code pane 
        codeViewer = new CodeViewer();
        codeViewer.setPreferredSize(new Dimension(TREE_WIDTH+DEMO_WIDTH, SOURCE_HEIGHT));
        sourceCodePane = new CollapsiblePanel("Demo Source Code", codeViewer);
        //sourceCodePane.setBorder(new EmptyBorder(SOURCE_PANE_INSETS));
        sourceCodePane.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName().equals("expanded")) {
                    setSourceCodeVisible(((Boolean)event.getNewValue()).booleanValue());
                }
            }
        });
        contentPanel.add(sourceCodePane, BorderLayout.SOUTH);
        
        addPropertyChangeListener(new SwingSetPropertyListener());
        
        //sourcePaneLocation = vertSplitPane.getDividerLocation();
        //dividerSize = vertSplitPane.getDividerSize();        
        
        // Create shareable popup menu for demo actions
        popup = new JPopupMenu();
        popup.add(new EditPropertiesAction());
        popup.add(new ViewCodeSnippetAction());

        return contentPanel;
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
        // View -> Source Code Visible
        sourceCodeCheckboxItem = new JCheckBoxMenuItem();
        sourceCodeCheckboxItem.setSelected(isSourceCodeVisible());
        sourceCodeCheckboxItem.setName("sourceCodeCheckboxItem");
        sourceCodeCheckboxItem.addChangeListener(new SourceVisibilityChangeListener());
        viewMenu.add(sourceCodeCheckboxItem);
        // View -> Animate Demos
        animationCheckboxItem = new JCheckBoxMenuItem();
        animationCheckboxItem.setSelected(isAnimationOn());
        animationCheckboxItem.setName("animationCheckboxItem");
        animationCheckboxItem.addChangeListener(new AnimationChangeListener());
        viewMenu.add(animationCheckboxItem);
        // View -> Look and Feel       
        viewMenu.add(createLookAndFeelMenu());
        
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
    
    protected void initAnimation() {
        
        // Initialize animated transition for when demos launch
        launchTransitionTarget = new LaunchTransitionTarget();
        launchAnimator = new Animator(1000);
        launchAnimator.setAcceleration(.2f);
        launchAnimator.setDeceleration(.3f);
        launchTransition = new ScreenTransition(upperPanel, launchTransitionTarget, launchAnimator);
        
        // Initialize animated transition (cross-fade) when look and feel is switched
        lafTransitionTarget = new LafTransitionTarget();
        lafAnimator = new Animator(1000);
        lafAnimator.setAcceleration(.3f);
        lafAnimator.setAcceleration(.5f);
        lafTransition = new CrossFadeTransition((JComponent)getMainFrame().getRootPane().getLayeredPane(),
                lafTransitionTarget, lafAnimator);

    }
    
    protected void expandAllCategories(TreePath parent) {
        // Traverse children
        TreeNode node = (TreeNode)parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e=node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode)e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAllCategories(path);
            }
        }
        demoSelectorTree.expandPath(parent);
    } 
    
    protected TreePath getTreePathForDemo(Demo demo) {
        DefaultMutableTreeNode nodes[] = new DefaultMutableTreeNode[3];
        nodes[0] = demoTreeTop;
        for(int i = 0; i < nodes[0].getChildCount(); i++) {
            nodes[1] = (DefaultMutableTreeNode)nodes[0].getChildAt(i);
            for(int j = 0; j < nodes[1].getChildCount(); j++) {
                nodes[2] = (DefaultMutableTreeNode)nodes[1].getChildAt(j);
                if (nodes[2].getUserObject() == demo) {
                    return new TreePath(nodes);
                }
            }
        }
        return null;
    } 
    
    // For displaying error messages to user
    protected void displayErrorMessage(String message) {
        
        JOptionPane.showMessageDialog(getMainFrame(), message, "SwingSet3 Error",
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
        DemoPanel demoPanel = null;
        if (demo != null) {
            demoPanel = demoCache.get(demo.getName());
            if (demoPanel == null || demo.getDemoComponent() == null) {
                demo.startInitializing();
                demoPanel = new DemoPanel(demo);  
                demoPanel.setPreferredSize(activeDemoPanel.getPreferredSize());
                demoCache.put(demo.getName(), demoPanel);
            } 
            if (isAnimationOn()) {
                animateLaunchingDemo(demo, demoPanel);

            } else {
                // no animated transition between demos
                nextDemoPanel = demoPanel;
                upperPanel.remove(activeDemoPanel);
                upperPanel.add(nextDemoPanel, BorderLayout.CENTER);
                activeDemoPanel = nextDemoPanel;
                upperPanel.revalidate();
                upperPanel.repaint();
                getMainFrame().validate();
            }
        }

        if (currentDemo == null) {
            upperPanel.add(BorderLayout.CENTER, demoPlaceholder);
        }
        
        if (isSourceCodeVisible()) {
            codeViewer.setSourceFiles(currentDemo != null?
                currentDemo.getSourceFiles() : null);
        }
        getMainFrame().setTitle(title +
                (currentDemo != null? (" :: " + currentDemo.getName()) : ""));
                
        firePropertyChange("currentDemo", oldCurrentDemo, demo);
    }
    
    protected void animateLaunchingDemo(Demo demo, JComponent demoPanel) {
        
        if (nextDemoPanel != null) {
            // Remove initialization padding from animator
            launchAnimator.setDuration(700);
        }
        nextDemoPanel = demoPanel;
        TreePath demoPath = getTreePathForDemo(demo);
        Rectangle nodeBounds = demoSelectorTree.getRowBounds(
                demoSelectorTree.getRowForPath(demoPath));
        // Must convert from tree's to frame's coordinate system
        nodePoint.x = nodeBounds.x;
        nodePoint.y = nodeBounds.y;
        Point p = SwingUtilities.convertPoint(demoSelectorTree, nodePoint, getMainFrame());

        launchTransition.start();
        
        // Create a new ScaleMoveEffect based on the launching demo's tree node
        scaleMoveEffect = new ScaleMoveEffect();
        scaleMoveEffect.setStartLocation(p.x + nodeBounds.width/2, p.y + nodeBounds.height/2);
        
        // Now create a Composite effect that combines our custom effect
        // with a standard FadeIn effect
        launchEffect = new CompositeEffect(scaleMoveEffect);
        launchEffect.addEffect(new FadeIn());
        //sourcePaneLocation = vertSplitPane.getDividerLocation();
        
        EffectsManager.setEffect(nextDemoPanel, launchEffect, EffectsManager.TransitionType.APPEARING);

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
            /*
            if (isAnimationOn()) {                
                lafTransition.start();                
            } else {
                // Update all GUI components with new look and feel
                updateLookAndFeel();
            }
             */
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
            logger.log(Level.WARNING, "exception setting look and feel", ex);
            displayErrorMessage("Unable to change the Look and Feel\n"+
                    "to "+lookAndFeelName);
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
    
    public void setAnimationOn(boolean animationOn) {
        boolean oldAnimationOn = this.animationOn;
        this.animationOn = animationOn;
        firePropertyChange("animationOn", oldAnimationOn, animationOn);
    }
    
    public boolean isAnimationOn() {
        return animationOn;
    }
    
    public void setTreeRowHeight(int rowHeight) {
        int oldTreeRowHeight = this.treeRowHeight;
        this.treeRowHeight = treeRowHeight;
        demoSelectorTree.setRowHeight(treeRowHeight);
        demoSelectorTree.repaint();
        firePropertyChange("treeRowHeight", oldTreeRowHeight, treeRowHeight);
    }
    
    public int getTreeRowHeight() {
        return treeRowHeight;
    }
    
    private void updateLookAndFeel() {
        Window windows[] = Frame.getWindows();
        System.out.println("updating look and feels");
        for(Window window : windows) {
            SwingUtilities.updateComponentTreeUI(window);
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
    
    // Event handler which launches the demo when the user clicks on its node
    // in the demo selector tree.
    private class DemoTreeClickListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            final JTree demoTree = (JTree)e.getSource();
            final int selectedRow = demoTree.getRowForLocation(e.getX(), e.getY());            
            if (selectedRow != -1) {
                TreePath selPath = demoTree.getPathForLocation(e.getX(), e.getY());
                if (e.getClickCount() == 1) {
                    TreeNode node = (TreeNode)selPath.getLastPathComponent();
                    Object demoObject = ((DefaultMutableTreeNode)node).getUserObject();
                    if (node.isLeaf() && demoObject instanceof Demo) {
                        // user clicked demo in tree, so run it
                        final Demo demo = (Demo)demoObject;
                        setCurrentDemo(demo);                       
                    }
                }
            }
        }
    }
    
    // Selection model which prevents non-leaf nodes from becoming selected
    private class DemoTreeSelectionModel extends DefaultTreeSelectionModel {
        
        public DemoTreeSelectionModel() {
            setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        }
        
        @Override
        public void setSelectionPath(TreePath path) {
            if (path == null ||
                    ((TreeNode)path.getLastPathComponent()).isLeaf()) {
                super.setSelectionPath(path);
            } 
        }
        
        @Override
        public void setSelectionPaths(TreePath[] pPaths) {
            if (pPaths == null ||
                    ((TreeNode)pPaths[0].getLastPathComponent()).isLeaf()) {
                super.setSelectionPaths(pPaths);
            } 
        }        
    }
            
    private class LaunchTransitionTarget implements TransitionTarget {
        private int startX = 0;
        private int startY = 0;
        
        public void setStartLocation(int x, int y) {
            this.startX = x;
            this.startY = y;
        }
        public void setupNextScreen() {
            upperPanel.remove(activeDemoPanel);
            upperPanel.add(nextDemoPanel, BorderLayout.CENTER);
            activeDemoPanel = nextDemoPanel;
            //vertSplitPane.setDividerLocation(sourcePaneLocation);
        }        
    }
           
    /**
     * Custom effect: scales and moves a component in to its end location
     * from a specified starting point
     */
    private class ScaleMoveEffect extends Effect {
        
        private Point startLocation = new Point();
        private PropertySetter ps[];
        
        public ScaleMoveEffect() {
            this(0,0);
        }
        
        public ScaleMoveEffect(int x, int y) {
            setStartLocation(x, y);
        }
        
        public void setStartLocation(int x, int y) {
            startLocation.x = x;
            startLocation.y = y;
        }
        
        @Override
        public void init(Animator animator, Effect parentEffect) {
            Effect targetEffect = (parentEffect == null) ? this : parentEffect;

            ComponentState starts = getStart();
            ComponentState ends = getEnd();
            
            ps = new PropertySetter[3];
            
            ps[0] = new PropertySetter(targetEffect, "location",
                    startLocation, new Point(getEnd().getX(), getEnd().getY()));
            animator.addTarget(ps[0]);
            ps[1] = new PropertySetter(targetEffect, "width", 0,
                    getEnd().getWidth());
            animator.addTarget(ps[1]);
            ps[2] = new PropertySetter(targetEffect, "height", 0,
                    getEnd().getHeight());
            animator.addTarget(ps[2]);
            super.init(animator, parentEffect);
        }
        @Override
        public void cleanup(Animator animator) {
            for(PropertySetter p: ps) {
                animator.removeTarget(p);
            }
        }
    }
    
    private class LafTransitionTarget implements TransitionTarget {
        public void setupNextScreen() {
            // Update all GUI components with new look and feel
            updateLookAndFeel();       
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
            } else if (propertyName.equals("state")) {
                demoSelectorTree.repaint();
            }
        }
    }
    
    private class DemoVisibilityListener implements HierarchyListener {
        public void hierarchyChanged(HierarchyEvent event) {
            if ((event.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) > 0) {
                JComponent component = (JComponent)event.getComponent();
                Demo demo = (Demo)component.getClientProperty("swingset3.demo");
                if (!component.isShowing()) {
                    demo.stop();
                } else {
                    demo.start();
                }
            }            
        }        
    }
    
    private class SourceVisibilityChangeListener implements ChangeListener {
        public void stateChanged(ChangeEvent event) {
            setSourceCodeVisible(sourceCodeCheckboxItem.isSelected());
        }
    }

    private class SourceVisibilityListener implements PropertyChangeListener {       
        public void propertyChange(PropertyChangeEvent event) {
            if (event.getPropertyName().equals("sourceCodeVisible")) {
                boolean sourceVisible = ((Boolean)event.getNewValue()).booleanValue();
                if (sourceVisible) {
                    // update codeViewer in case current demo changed while
                    // source was invisible
                    codeViewer.setSourceFiles(currentDemo != null?
                        currentDemo.getSourceFiles() : null);
                }
                sourceCodePane.setExpanded(sourceVisible);
                sourceCodeCheckboxItem.setSelected(sourceVisible);
            }
        }        
    }
    
    private class AnimationChangeListener implements ChangeListener {
        public void stateChanged(ChangeEvent event) {
            setAnimationOn(animationCheckboxItem.isSelected());
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
                sourceCodePane.setExpanded(sourceVisible);
                sourceCodeCheckboxItem.setSelected(sourceVisible);
            } else if (propertyName.equals("animationOn")) {
                animationCheckboxItem.setSelected(((Boolean)event.getNewValue()).booleanValue());
            }
        }        
    } 
 
    public class ViewCodeSnippetAction extends AbstractAction {
        public ViewCodeSnippetAction() {
            super("View Source Code");
        }
        public void actionPerformed(ActionEvent actionEvent) {
            JComponent source = (JComponent)actionEvent.getSource();
            Container popup = source;
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