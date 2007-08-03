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

import application.ResourceMap;
import application.SingleFrameApplication;
import application.View;
import com.sun.source.tree.ErroneousTree;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jnlp.ServiceManager;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
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
    private static final Logger logger = Logger.getLogger(SwingSet3.class.getName());
    private static final Insets zeroInsets = new Insets(0,0,0,0);
    
    private static final String defaultDemoJars[] = {"swingset3.jar", "dist/swingset3.jar"};

    public static final String controlVeryLightShadowKey = "controlVeryLightShadowColor";
    public static final String controlLightShadowKey = "controlLightShadowColor";
    public static final String controlMidShadowKey = "controlMidShadowColor";
    public static final String controlVeryDarkShadowKey = "controlVeryDarkShadowColor";
    public static final String controlDarkShadowKey = "controlDarkShadowColor";

    private static final int DEMO_WIDTH = 600;
    private static final int DEMO_HEIGHT = 500;
    private static final int TREE_WIDTH = 220;
    private static final int SOURCE_HEIGHT = 300;
    private static final Insets UPPER_PANEL_INSETS = new Insets(12,12,8,12);
    private static final Insets TREE_INSETS = new Insets(2,8,2,8);
    private static final Insets SOURCE_PANE_INSETS = new Insets(4,8,8,8);
        
    static {
        // Property must be set *early* due to Apple Bug#3909714
        System.setProperty("apple.laf.useScreenMenuBar", "true");                
    }
    
    public static void main(String[] args) {
        launch(SwingSet3.class, args);
    }
    
    public static boolean onMac() {
        return System.getProperty("os.name").equals("Mac OS X");
    } 
    
    public static boolean runningFromWebStart() {
        return ServiceManager.getServiceNames() != null;        
    }
    
    private static List readDemoClassNames(String fileName) throws IOException {
        ArrayList demoClassNames = new ArrayList();
        
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line = null;
        while((line = reader.readLine()) != null) {
            demoClassNames.add(line);
        }
        reader.close();
        return demoClassNames;
    }
    
    private static List readDemoClassNames(Manifest manifest) throws IOException {
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
    private JPanel vertSplitPane;
    private JPanel upperPanel;
    private JTree demoSelectorTree;
    private JComponent runningDemoPlaceholder;
    private CollapsiblePanel sourceCodePane;
    private CodeViewer codeViewer;    
    private JCheckBoxMenuItem sourceCodeCheckboxItem;
    
    private JPopupMenu popup;
    
    // GUI state
    private String lookAndFeel;
    private boolean sourceVisible = true;
    private int sourcePaneLocation;
    private int dividerSize;
    private int codeViewerHeight;    
    
    // Animation
    Animator animator;
    ScreenTransition transition;
    LoadAnimator loadAnimator;
    CompositeEffect effect;
    ScaleMoveIn scaler;
    JComponent activePanel;
    JComponent nextPanel;
    
    @Override
    protected void initialize(String args[]) {        
        resourceMap = getContext().getResourceMap();
        demoCache = new HashMap();
        setDemos(resourceMap.getString("demos.title"), getDemoList(args));
    }
                
    private List<String> getDemoList(String args[]) {
        final ArrayList<String> demoList = new ArrayList();
        boolean augment = false;
    
        if (runningFromWebStart()) {
            // Look inside each jar resource and check manifests for classes marked as demos
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader instanceof URLClassLoader) {
                URLClassLoader jnlpClassLoader = (URLClassLoader)classLoader;                                
                URL urls[] = jnlpClassLoader.getURLs();
                
                if (urls != null) {
                    for(URL url : urls) {
                        try {
                            URL jarURL = new URL("jar:"+url+"!/");
                            JarURLConnection jarConnection = (JarURLConnection)jarURL.openConnection();
                            demoList.addAll(readDemoClassNames(jarConnection.getManifest()));
                            
                        } catch (Exception e) {
                            logger.log(Level.SEVERE, "unable to obtain demo list from jar manifest :"+url, e);
                        }
                    }
                }
            } else {
                logger.log(Level.SEVERE, "unable to access JNLPClassLoader required to obtain demo list");
            }
        } else {     
            // Not running under Webstart
            // Must obtain list of demo classes from command-line arguments
            for(String arg : args) {           
                if (arg.endsWith(".jar")) {                    
                    try {
                        demoList.addAll(readDemoClassNames(new JarFile(arg).getManifest()));
                        
                    } catch (IOException e) {
                        System.err.println("cannot access jar "+arg+" :"+e);
                    }
                    
                } else {
                    // process argument as filename containing names of demo classes
                    try {
                        demoList.addAll(readDemoClassNames(arg /*filename*/));
                        
                    } catch (IOException e) {
                        System.err.println("cannot read class names from file: "+arg);
                    }
                }
            }
            if (demoList.isEmpty()) {
                // nothing specified on command-line, so load default swing demos from swingset jar
                for(String jar: defaultDemoJars) {
                    try {
                        demoList.addAll(readDemoClassNames(new JarFile(jar).getManifest()));
                    } catch (IOException e) {
                        logger.log(Level.FINE, "unable to read demos from jar: "+jar, e);
                    }
                }
                if (demoList.isEmpty()) {
                    JOptionPane.showMessageDialog(getMainFrame(), 
                            resourceMap.getString("error.noDemosLoaded"), 
                            resourceMap.getString("error.title"), JOptionPane.ERROR);
                }
            }
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
            // okay.  for interim purposes we'll show an inactive nodes for TBW demos
        }
        if (demoClass != null) {
            // If demo class happens to implement Demo, then instantiate it
            if (Demo.class.isAssignableFrom(demoClass)) {
                try {
                    demo = (Demo)demoClass.newInstance();
                    
                } catch (Exception ex) {
                    logger.log(Level.WARNING, "could not instantiate demo: "+ demoClass.getName(), ex);
                }
                
            } else {
                // Wrap Demo (the common case)
                demo = new Demo(demoClass);
            }
            demo.addPropertyChangeListener(getDemoPropertyChangeListener());
            category = demo.getCategory();
            
        } else {
            category = Demo.deriveCategoryFromPackageName(demoClassName);
        }
        
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
        categoryNode.add(new DefaultMutableTreeNode(demo != null? demo :
            Demo.deriveNameFromClassName(demoClassName)));
        
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

    }  
    
    protected JComponent createMainPanel() {
        
        // Create vertical splitpane with demos on top, source on bottom
        //vertSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        // Temporarily replaced splitpane with panel because animation fails with splitpane
        vertSplitPane = new JPanel();
        vertSplitPane.setLayout(new BorderLayout());
        
        // Create top panel to hold demo-selection-tree and current demo
        upperPanel = new JPanel();
        upperPanel.setLayout(new BorderLayout());
        //vertSplitPane.setTopComponent(upperPanel);
        upperPanel.setBorder(new EmptyBorder(UPPER_PANEL_INSETS));
        vertSplitPane.add(upperPanel, BorderLayout.CENTER);

        // Create demo selection tree
        UIManager.put("Tree.paintLines", Boolean.FALSE);
        // We must set these icons here because they will be initialized when TreeUI installs
        // and cannot be replaced afterwards (without touching the TreeUI direction)
        UIManager.put("Tree.expandedIcon", resourceMap.getImageIcon("demoSelectorTree.expandedIcon"));
        UIManager.put("Tree.collapsedIcon", resourceMap.getImageIcon("demoSelectorTree.collapsedIcon"));
        demoSelectorTree = new DemoSelectorTree(demoTreeModel);
        demoSelectorTree.setName("demoSelectorTree");
        demoSelectorTree.setShowsRootHandles(false);
        demoSelectorTree.setBorder(new EmptyBorder(TREE_INSETS));
        demoSelectorTree.setRowHeight(28);
        demoSelectorTree.addMouseListener(new DemoTreeClickListener());
        JScrollPane scrollPane = new JScrollPane(demoSelectorTree);
        scrollPane.setPreferredSize(new Dimension(TREE_WIDTH,DEMO_HEIGHT)); // wide enough to avoid horiz scrollbar
        scrollPane.setMinimumSize(new Dimension(TREE_WIDTH,DEMO_HEIGHT));
        upperPanel.add(scrollPane, BorderLayout.WEST);
        expandAllCategories(new TreePath(demoTreeTop)); // expand all demos in tree


        // Create pane to contain running demo
        runningDemoPlaceholder = new IntroPanel(DEMO_WIDTH, DEMO_HEIGHT);
        upperPanel.add(runningDemoPlaceholder, BorderLayout.CENTER);
        activePanel = runningDemoPlaceholder;
                
        // Create collapsible source code pane 
        codeViewer = new CodeViewer();
        codeViewer.setPreferredSize(new Dimension(TREE_WIDTH+DEMO_WIDTH, SOURCE_HEIGHT));
        sourceCodePane = new CollapsiblePanel("Demo Source Code", codeViewer);
        sourceCodePane.setBorder(new EmptyBorder(SOURCE_PANE_INSETS));
        sourceCodePane.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName().equals("expanded")) {
                    setSourceCodeVisible(((Boolean)event.getNewValue()).booleanValue());
                }
            }
        });
        vertSplitPane.add(sourceCodePane, BorderLayout.SOUTH);
        
        addPropertyChangeListener(new SourceVisibilityListener());
        
        //sourcePaneLocation = vertSplitPane.getDividerLocation();
        //dividerSize = vertSplitPane.getDividerSize();        
        
        // Create shareable popup menu for demo actions
        popup = new JPopupMenu();
        popup.add(new EditPropertiesAction());
        popup.add(new ViewCodeSnippetAction());

        return vertSplitPane;
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
        sourceCodeCheckboxItem = new JCheckBoxMenuItem();
        sourceCodeCheckboxItem.setSelected(isSourceCodeVisible());
        sourceCodeCheckboxItem.setName("sourceCodeCheckboxItem");
        sourceCodeCheckboxItem.addChangeListener(new SourceVisibilityChangeListener());
        viewMenu.add(sourceCodeCheckboxItem);
        menubar.add(viewMenu);

        return menubar;
    }
    
    protected void initAnimation() {
        loadAnimator = new LoadAnimator();
        
        // Initialize Animation
        // Set up non-linear timing behavior on animator. Accelerate for
        // first 20%, decelerate for final 30%.
        // first animation is 500ms longer, because it needs that warmup adjustment
        animator = new Animator(1500);
        animator.setAcceleration(.2f);
        animator.setDeceleration(.3f);
        // Create the screen transition. It uses 'this' as the container that
        // we are transitioning, 'this' as the target for the callback
        // 'setupNextScreen()', and animator as the underlying animation.
        transition = new ScreenTransition(upperPanel, loadAnimator, animator);         

    }
    
    private void expandAllCategories(TreePath parent) {
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
    
    private TreePath getTreePathForDemo(Demo demo) {
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
    
    public void setCurrentDemo(Demo demo) {
        if (currentDemo == demo) {
            return; // already there
        }
        Demo oldCurrentDemo = currentDemo;        

        if (oldCurrentDemo != null) {
            oldCurrentDemo.pause();
        }
        currentDemo = demo;
        DemoPanel demoPanel = null;
        if (demo != null) {
            demoPanel = demoCache.get(demo.getName());
            if (demoPanel == null || demo.getDemoComponent() == null) {                
                demo.initialize();
                demoPanel = new DemoPanel(demo);  
                demoPanel.setPreferredSize(activePanel.getPreferredSize());
                demoCache.put(demo.getName(), demoPanel);
            } else {
                currentDemo.start();
            }
            if (nextPanel != null) {
                animator.setDuration(1000);
            }
            nextPanel = demoPanel;
            TreePath demoPath = getTreePathForDemo(demo);
            Rectangle nodeBounds = demoSelectorTree.getRowBounds(
                    demoSelectorTree.getRowForPath(demoPath));
            transition.start();    
                    
            // Now, create a new ScaleMoveIn effect based on the tree node 
            scaler = new ScaleMoveIn();
            scaler.setStartLocation(nodeBounds.x + nodeBounds.width/2,
                                nodeBounds.y + nodeBounds.height/2);
            // Now, create a Composite effect that combines our custom effect
            // with a standard FadeIn effect
            
            FadeIn fader = new FadeIn();
            effect = new CompositeEffect(scaler);
            effect.addEffect(fader);
            //sourcePaneLocation = vertSplitPane.getDividerLocation();

            EffectsManager.setEffect(nextPanel, effect, EffectsManager.TransitionType.APPEARING);

        }

        if (currentDemo == null) {
            upperPanel.add(BorderLayout.CENTER, runningDemoPlaceholder);
        }
        
        if (isSourceCodeVisible()) {
            codeViewer.setSourceFiles(currentDemo != null?
                currentDemo.getSourceFiles() : null);
        }
        firePropertyChange("currentDemo", oldCurrentDemo, demo);
    }
    
    public Demo getCurrentDemo() {
        return currentDemo;
    }
    

    
    public void setSourceCodeVisible(boolean sourceVisible) {
        boolean oldSourceVisible = this.sourceVisible;
        this.sourceVisible = sourceVisible;
        firePropertyChange("sourceCodeVisible", oldSourceVisible, sourceVisible);
    }
    
    public boolean isSourceCodeVisible() {
        return sourceVisible;       
    }      

    private void registerPopups(JComponent component) {
        Component children[] = component.getComponents();
        for(Component child: children) {
            if (child instanceof JComponent) {
                registerPopups((JComponent)child);
            }
        }
        String snippetKey = (String)component.getClientProperty("snippetKey");
        if (snippetKey != null) {
            component.setComponentPopupMenu(popup);
        }
    }    
    
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
                                                
                        //Rectangle nodeBounds = demoTree.getRowBounds(selectedRow);
                        
                        //scaler.setStartLocation(nodeBounds.x + nodeBounds.width/2,
                                //nodeBounds.y + nodeBounds.height/2);
                        
                        // Finally, tell the EffectsManager to use our composite effect for
                        // the component that will be appearing during the transition
                        setCurrentDemo(demo);                       
                    }
                }
            }
        }
    }            
            
    private class LoadAnimator implements TransitionTarget {
        private int startX = 0;
        private int startY = 0;
        
        public LoadAnimator() {            
        }
        
        public void setStartLocation(int x, int y) {
            this.startX = x;
            this.startY = y;
        }
        
        /**
         * This is the single callback of TransitionTarget. It will be called
         * after the call totransition.start(). We set up the GUI that we are
         * transitioning to here.  In this case, we simply remove the old
         * ColoredPanel and add the new one selected
         */
        public void setupNextScreen() {
            upperPanel.remove(activePanel);
            upperPanel.add(nextPanel, BorderLayout.CENTER);
            activePanel = nextPanel;
            //vertSplitPane.setDividerLocation(sourcePaneLocation);
        }        
    }
           
    /**
     * Custom effect: scales and moves a component in to its end location
     * from a specified starting point
     */
    private class ScaleMoveIn extends Effect {
        
        private Point startLocation = new Point();
        
        public ScaleMoveIn() {
            this(0,0);
        }
        
        public ScaleMoveIn(int x, int y) {
            setStartLocation(x, y);
        }
        
        public void setStartLocation(int x, int y) {
            startLocation.x = x;
            startLocation.y = y;
        }
        
        @Override
        public void init(Animator animator, Effect parentEffect) {
            Effect targetEffect = (parentEffect == null) ? this : parentEffect;
            PropertySetter ps;
            ComponentState starts = getStart();
            ComponentState ends = getEnd();
            if (starts != null) System.out.println("start="+ starts.getX()+","+starts.getY()+" "+starts.getWidth()+"x"+starts.getHeight());
            if (ends != null) System.out.println("end="+ ends.getX()+","+ends.getY()+" "+ends.getWidth()+"x"+ends.getHeight());
            
            ps = new PropertySetter(targetEffect, "location",
                    startLocation, new Point(getEnd().getX(), getEnd().getY()));
            animator.addTarget(ps);
            ps = new PropertySetter(targetEffect, "width", 0,
                    getEnd().getWidth());
            animator.addTarget(ps);
            ps = new PropertySetter(targetEffect, "height", 0,
                    getEnd().getHeight());
            animator.addTarget(ps);
            super.init(animator, parentEffect);
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
                    if (demo == currentDemo) {
                        currentDemo.start();
                    }
                    registerPopups((JComponent)e.getNewValue());
                }
            } else if (propertyName.equals("state")) {
                demoSelectorTree.repaint();
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
                System.out.println("can't find source code snippet for target component");
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