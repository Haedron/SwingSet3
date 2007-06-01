
/* SwingSet3.java
 *
 * Created on June 2, 2006, 1:57 PM
 */

package swingset3;


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
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultMutableTreeNode;
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
public class SwingSet3 extends JFrame  {

    public static final String CONTROL_VERY_LIGHT_SHADOW_COLOR = "controlVeryLightShadowColor";
    public static final String CONTROL_LIGHT_SHADOW_COLOR = "controlLightShadowColor";
    public static final String CONTROL_MID_SHADOW_COLOR = "controlMidShadowColor";
    public static final String CONTROL_VERY_DARK_SHADOW_COLOR = "controlVeryDarkShadowColor";
    public static final String CONTROL_DARK_SHADOW_COLOR = "controlDarkShadowColor";

    private static final int DEMO_WIDTH = 600;
    private static final int DEMO_HEIGHT = 420;
    private static final int TREE_WIDTH = 220;
    private static final int SOURCE_HEIGHT = 300;
    private static final Insets UPPER_PANEL_INSETS = new Insets(12,12,8,12);
    private static final Insets TREE_INSETS = new Insets(2,8,2,8);
    private static final Insets SOURCE_PANE_INSETS = new Insets(4,8,8,8);
    
    // remind: initalize demos frome exterior list (?)
    //remind(aim): change to load default set of demo jars
    private static String demoClassNames[] = {
            "swingset3.demos.toplevels.JFrameDemo",
            "swingset3.demos.toplevels.JDialogDemo",
            "swingset3.demos.toplevels.JWindowDemo",
                    
            "swingset3.demos.controls.JButtonDemo",
            "swingset3.demos.controls.JCheckBoxesDemo",
            "swingset3.demos.controls.JComboBoxesDemo",
            "swingset3.demos.controls.JLabelsDemo",
            
            "swingset3.demos.data.JListsDemo",
            "swingset3.demos.data.JTablesDemo",
            "swingset3.demos.data.JTreesDemo",
            
            "swingset3.demos.text.JEditorPanesDemo",
                                        
            "swingset3.layoutContainers.JPanelDemo",
            "swingset3.layoutContainers.JTabbedPaneDemo",
            "swingset3.layoutContainers.JScrollPaneDemo"
    };
        
    static {
        // Property must be set *early* due to Apple Bug#3909714
        System.setProperty("apple.laf.useScreenMenuBar", "true");                
    }
    
    public static boolean onMac() {
        return System.getProperty("os.name").equals("Mac OS X");
    }        
    
    // Application models
    private DefaultMutableTreeNode demos; /* all available demos */    
    private HashMap<String, DemoPanel> demoCache;
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
    private int sourcePaneLocation;
    private int dividerSize;
    private int codeViewerHeight;    
    private boolean sourceVisible = true;
    
    // Animation
    Animator animator;
    ScreenTransition transition;
    LoadAnimator loadAnimator;
    CompositeEffect effect;
    ScaleMoveIn scaler;
    JComponent activePanel;
    JComponent nextPanel;
    
    private PropertyChangeSupport pcs;
    
    public SwingSet3() {
        this(getDefaultDemoClassNames());
    }
    
    public SwingSet3(List<String>demoClassNames) {
        super("SwingSet3");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
        } catch (Exception ex) {
        }
        
        pcs = new PropertyChangeSupport(this);
        
        // Create application model
        initDemos(demoClassNames);
        demoCache = new HashMap();
 
        // Create GUI
        initColorPalette();
        initComponents(); 
        initAnimation();
        expandAll(new TreePath(demos)); // expand all demos in tree
        
    }
    
    protected void initDemos(List<String> demoClassNamesList) {
        demos = new DefaultMutableTreeNode("Components");  
        
        DemoPropertyChangeListener demoPropertyListener = 
                new DemoPropertyChangeListener();
        
        for(String demoClassName : demoClassNamesList) {
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
                        System.err.println("could not instantiate demo: "+ demoClass.getName());
                        ex.printStackTrace();                       
                    }
                    
                } else {
                    // Wrap Demo 
                    demo = new Demo(demoClass);
                }
                demo.addPropertyChangeListener(demoPropertyListener);                
                category = demo.getCategory();
                
            } else {
                category = Demo.deriveCategoryFromPackageName(demoClassName);
            }

            Enumeration categories = demos.children();
            DefaultMutableTreeNode categoryNode = null;
            while (categories.hasMoreElements()) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)categories.nextElement();
                if (node.getUserObject().equals(category)) {
                    categoryNode = node;
                    break; // category already exists
                }                    
            }
            
            if (categoryNode == null) {
                categoryNode = new DefaultMutableTreeNode(category);
                demos.add(categoryNode);
            }
            categoryNode.add(new DefaultMutableTreeNode(demo != null? demo : 
                Demo.deriveNameFromClassName(demoClassName)));

        }
    }
    
    protected void initColorPalette() {
        // Color palette algorithm courtesy of Jasper Potts
        Color controlColor = UIManager.getColor("control");
        float[] controlHSB = Color.RGBtoHSB(
                controlColor.getRed(), controlColor.getGreen(),
                controlColor.getBlue(), null);
	UIManager.put(CONTROL_VERY_LIGHT_SHADOW_COLOR, Color.getHSBColor(controlHSB[0], controlHSB[1],
                controlHSB[2] - 0.02f));
        UIManager.put(CONTROL_LIGHT_SHADOW_COLOR, Color.getHSBColor(controlHSB[0], controlHSB[1],
                controlHSB[2] - 0.06f));
        UIManager.put(CONTROL_MID_SHADOW_COLOR, Color.getHSBColor(controlHSB[0], controlHSB[1],
                controlHSB[2] - 0.16f));
        UIManager.put(CONTROL_VERY_DARK_SHADOW_COLOR, Color.getHSBColor(controlHSB[0], controlHSB[1],
                controlHSB[2] - 0.5f));
        UIManager.put(CONTROL_DARK_SHADOW_COLOR, Color.getHSBColor(controlHSB[0], controlHSB[1],
                controlHSB[2] - 0.32f));

    }  
    
    protected void initComponents() {
        // Set frame properties
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        createMenuBar();
        
        // Create vertical splitpane with demos on top, source on bottom
        //vertSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        // Temporarily replaced splitpane with panel because animation fails with splitpane
        vertSplitPane = new JPanel();
        vertSplitPane.setLayout(new BorderLayout());
        add(vertSplitPane);
        
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
        UIManager.put("Tree.expandedIcon", 
                new ImageIcon(SwingSet3.class.getResource("resources/images/down_arrow.png")));
        UIManager.put("Tree.collapsedIcon", 
                new ImageIcon(SwingSet3.class.getResource("resources/images/right_arrow.png")));
        demoSelectorTree = new DemoSelectorTree(demos);
                //UIManager.getColor("Tree.background"),
                //UIManager.getColor(CONTROL_MID_SHADOW_COLOR));
        demoSelectorTree.setBorder(new EmptyBorder(TREE_INSETS));
        demoSelectorTree.setRowHeight(28);
        demoSelectorTree.addMouseListener(new DemoTreeClickListener());
        JScrollPane scrollPane = new JScrollPane(demoSelectorTree);
        scrollPane.setPreferredSize(new Dimension(TREE_WIDTH,DEMO_HEIGHT)); // wide enough to avoid horiz scrollbar
        scrollPane.setMinimumSize(new Dimension(TREE_WIDTH,DEMO_HEIGHT));
        upperPanel.add(scrollPane, BorderLayout.WEST);


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

        pack();        
    }
    
    protected void createMenuBar() {
    
        // Create menubar
        JMenuBar menubar = new JMenuBar();
        menubar.setName("menubar");
        
        // Create file menu
        JMenu fileMenu = new JMenu();
        fileMenu.setText("File");
        fileMenu.setName("fileMenu");
        menubar.add(fileMenu);
       
        // Create View menu
        JMenu viewMenu = new JMenu();
        viewMenu.setText("View");
        viewMenu.setName("viewMenu");
        sourceCodeCheckboxItem = new JCheckBoxMenuItem();
        sourceCodeCheckboxItem.setSelected(isSourceCodeVisible());
        sourceCodeCheckboxItem.setText("Source Code");
        sourceCodeCheckboxItem.setName("sourceCodeCheckboxItem");
        sourceCodeCheckboxItem.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sourceCodeCheckboxItemStateChanged(evt);
            }
        });
        viewMenu.add(sourceCodeCheckboxItem);
        menubar.add(viewMenu);

        setJMenuBar(menubar);
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
    
    private TreePath getPathForDemo(Demo demo) {
        DefaultMutableTreeNode nodes[] = new DefaultMutableTreeNode[3];
        nodes[0] = demos;
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
    
    private void expandToCategories(TreeNode top) {    
        // Ensure the demo tree categories come up initially expanded
        TreeNode nodes[] = new TreeNode[2];
        nodes[0] = top;
        for(int i = 0; i < top.getChildCount(); i++) {
            nodes[1] = top.getChildAt(i);
            demoSelectorTree.makeVisible(new TreePath(nodes));
        }
    }
    
   private void expandAll(TreePath parent) {
        // Traverse children
        TreeNode node = (TreeNode)parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e=node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode)e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(path);
            }
        }
        demoSelectorTree.expandPath(parent);
    }
   
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        pcs.addPropertyChangeListener(pcl);
    }
    
    public void setSourceCodeVisible(boolean sourceVisible) {
        boolean oldSourceVisible = this.sourceVisible;
        this.sourceVisible = sourceVisible;
        pcs.firePropertyChange("sourceCodeVisible", oldSourceVisible, sourceVisible);
    }
    
    public boolean isSourceCodeVisible() {
        return sourceVisible;
        
    } 
                                                        
    private void sourceCodeCheckboxItemStateChanged(ChangeEvent evt) {                                                    
        setSourceCodeVisible(sourceCodeCheckboxItem.isSelected());
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
            TreePath demoPath = getPathForDemo(demo);
            Rectangle nodeBounds = demoSelectorTree.getRowBounds(
                    demoSelectorTree.getRowForPath(demoPath));
            transition.start();    
                    
            // Now, create a new ScaleMoveIn effect based on the button location
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
            validate();
        }

        if (currentDemo == null) {
            upperPanel.add(BorderLayout.CENTER, runningDemoPlaceholder);
        }
        
        if (isSourceCodeVisible()) {
            codeViewer.setSourceFiles(currentDemo != null?
                currentDemo.getSourceFiles() : null);
        }
        pcs.firePropertyChange("currentDemo", oldCurrentDemo, demo);
    }
    
    public Demo getCurrentDemo() {
        return currentDemo;
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
    
    class DemoTreeClickListener extends MouseAdapter {
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
            
            
    class LoadAnimator implements TransitionTarget {
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
    class ScaleMoveIn extends Effect {
        
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

    // registered on swingset to track source code visibility property
    class SourceVisibilityListener implements PropertyChangeListener {       
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
    
    // registered on Demo to detect when the demo component is instantiated.
    // we need this because when we embed the demo inside an HTML description pane,
    // we don't have control over the demo component's instantiation
    class DemoPropertyChangeListener implements PropertyChangeListener {
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
    
    public class HideSourceCodeAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            setSourceCodeVisible(false);
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
   

    private static ArrayList<String> getDefaultDemoClassNames() {
        ArrayList demoClassNamesList = new ArrayList<String>();
        for(String demoClassName: demoClassNames) {
            demoClassNamesList.add(demoClassName);
        }
        return demoClassNamesList;
    }
    
    private static void readDemoClassNames(String fileName, List demoList) {        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line = null;
            while((line = reader.readLine()) != null) {
                demoList.add(line);
            }
        } catch (Exception ex) {
            System.err.println("exception reading demos filename: " + fileName);
            System.err.println(ex);
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        Splash splash = new Splash();
        final Rectangle splashBounds = splash.getBounds();
        
        final ArrayList<String> demoList = new ArrayList();
        ArrayList<String> userDemoList = null;
        boolean augment = false;
        
        for(String arg : args) {
            if (arg.equals("-a") || arg.equals("-augment")) {
                augment = true;
            } else {
                // process argument as filename containing names of demo classes
                if (userDemoList == null) {
                    userDemoList = new ArrayList();
                }
                readDemoClassNames(arg /*filename*/, userDemoList);
            }            
        }
        if (augment || userDemoList == null) {
            // populate demo list with default Swing demos
            demoList.addAll(getDefaultDemoClassNames());
        }
        if (userDemoList != null) {
            // add demos specified by user on the command line
            demoList.addAll(userDemoList);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SwingSet3 swingset = new SwingSet3(demoList);
                
                // Show GUI
                /*        
                swingset.setLocation(splashBounds.x - (DEMO_WIDTH - splashBounds.width)/2 - TREE_WIDTH - UPPER_PANEL_INSETS.left,
                        splashBounds.y - ((DEMO_HEIGHT - splashBounds.height)/2) - UPPER_PANEL_INSETS.top);
                */
                swingset.setVisible(true);
            }
        });
    }                
    
}