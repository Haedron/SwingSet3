
/* SwingSet3.java
 *
 * Created on June 2, 2006, 1:57 PM
 */

package swingset3;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
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
import javax.swing.Action;
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
import javax.swing.ToolTipManager;
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
public class SwingSet3  {
    
    // remind: initalize demos frome exterior list (?)
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
    

    private static final int DEMO_WIDTH = 600;
    private static final int DEMO_HEIGHT = 420;
    private static final int TREE_WIDTH = 220;
    private static final int SOURCE_HEIGHT = 300;
    
    
    // Application models
    private DefaultMutableTreeNode demos; /* all available demos */    
    private HashMap<String, DemoPanel> demoCache;
    private Demo currentDemo;

    // GUI components
    private JFrame frame;
    //private JSplitPane vertSplitPane;
private JPanel vertSplitPane;
    private JPanel topPanel;
    private JTree demoSelectorTree;
    private JLabel runningDemoPlaceholder;
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
    Animator animator = new Animator(1000);
    ScreenTransition transition = null;
    LoadAnimator loadAnimator = new LoadAnimator();
    CompositeEffect effect = null;
    JComponent activePanel = null;
    JComponent nextPanel = null;
    
    private PropertyChangeSupport pcs;

    
    public SwingSet3() {
        this(getDefaultDemoClassNames());
    }
    
    public SwingSet3(List<String>demoClassNames) {
        
        try {
            if (!onMac()) {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            }
        } catch (Exception ex) {
        }
        
        pcs = new PropertyChangeSupport(this);
        
        // Create application model
        initDemos(demoClassNames);
        demoCache = new HashMap();
 
        // Create GUI
        initComponents();  
        expandAll(new TreePath(demos)); // expand all demos in tree
        
        // Initialize Animation
        // Set up non-linear timing behavior on animator. Accelerate for
        // first 20%, decelerate for final 30%.
        animator.setAcceleration(.2f);
        animator.setDeceleration(.3f);
        // Create the screen transition. It uses 'this' as the container that
        // we are transitioning, 'this' as the target for the callback
        // 'setupNextScreen()', and animator as the underlying animation.
        transition = new ScreenTransition(topPanel, loadAnimator, animator);

        
        // Show GUI
        frame.setVisible(true);
    }

    protected void initDemos(List<String> demoClassNamesList) {
        demos = new DefaultMutableTreeNode("Components");  
        
        DemoPropertyChangeListener demoPropertyListener = 
                new DemoPropertyChangeListener();
        
        for(String demoClassName : demoClassNamesList) {
            Class demoClass = null;
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
    
    protected void initComponents() {
        // Create toplevel frame
        frame = new JFrame("SwingSet3");
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        createMenuBar();
        
        // Create vertical splitpane with demos on top, source on bottom
        //vertSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        // Temporarily replaced splitpane with panel because animation fails with splitpane
        vertSplitPane = new JPanel();
        vertSplitPane.setLayout(new BorderLayout());
        frame.add(vertSplitPane);
        
        // Create top panel to hold demo-selection-tree and current demo
        topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        //vertSplitPane.setTopComponent(topPanel);
        topPanel.setBorder(new EmptyBorder(12,12,8,12));
        vertSplitPane.add(topPanel, BorderLayout.CENTER);

        // Create demo selection tree
        UIManager.put("Tree.paintLines", Boolean.FALSE);
        // We must set these icons here because they will be initialized when TreeUI installs
        // and cannot be replaced afterwards (without touching the TreeUI direction)
        UIManager.put("Tree.expandedIcon", 
                new ImageIcon(SwingSet3.class.getResource("resources/images/down_arrow.png")));
        UIManager.put("Tree.collapsedIcon", 
                new ImageIcon(SwingSet3.class.getResource("resources/images/right_arrow.png")));
        demoSelectorTree = new DemoSelectorTree(demos);
        demoSelectorTree.setBorder(new EmptyBorder(2,8,2,8));
        demoSelectorTree.setRowHeight(28);
        demoSelectorTree.setShowsRootHandles(false);
        demoSelectorTree.addMouseListener(new DemoTreeClickListener());
        demoSelectorTree.setCellRenderer(new DemoSelectorTreeRenderer());
        ToolTipManager.sharedInstance().registerComponent(demoSelectorTree);
        JScrollPane scrollPane = new JScrollPane(demoSelectorTree);
        scrollPane.setPreferredSize(new Dimension(TREE_WIDTH,DEMO_HEIGHT)); // wide enough to avoid horiz scrollbar
        scrollPane.setMinimumSize(new Dimension(TREE_WIDTH,DEMO_HEIGHT));
        topPanel.add(scrollPane, BorderLayout.WEST);


        // Create pane to contain running demo
        runningDemoPlaceholder = new JLabel(
                new ImageIcon(SwingSet3.class.getResource("resources/images/splash3.png")));
        runningDemoPlaceholder.setPreferredSize(new Dimension(DEMO_WIDTH, DEMO_HEIGHT));
        topPanel.add(runningDemoPlaceholder, BorderLayout.CENTER);
        activePanel = runningDemoPlaceholder;
                
        // Create collapsible source code pane 
        codeViewer = new CodeViewer();
        codeViewer.setPreferredSize(new Dimension(TREE_WIDTH+DEMO_WIDTH, SOURCE_HEIGHT));
        sourceCodePane = new CollapsiblePanel("Demo Source Code", codeViewer);
        sourceCodePane.setBorder(new EmptyBorder(4,8,8,8));
        sourceCodePane.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName().equals("expanded")) {
                    setSourceCodeVisible(((Boolean)event.getNewValue()).booleanValue());
                }
            }
        });
        addPropertyChangeListener(new PropertyChangeListener() {
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
                }
            }
        });
        vertSplitPane.add(sourceCodePane, BorderLayout.SOUTH);
        
        //sourcePaneLocation = vertSplitPane.getDividerLocation();
        //dividerSize = vertSplitPane.getDividerSize();        
        
        // Create shareable popup menu for demo actions
        popup = new JPopupMenu();
        popup.add(new EditPropertiesAction());
        popup.add(new ViewCodeSnippetAction());

        frame.pack();        
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
        sourceCodeCheckboxItem.setSelected(true);
        sourceCodeCheckboxItem.setText("Source Code");
        sourceCodeCheckboxItem.setName("sourceCodeCheckboxItem");
        sourceCodeCheckboxItem.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sourceCodeCheckboxItemStateChanged(evt);
            }
        });
        viewMenu.add(sourceCodeCheckboxItem);
        menubar.add(viewMenu);

        frame.setJMenuBar(menubar);
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
            nextPanel = demoPanel;
            //sourcePaneLocation = vertSplitPane.getDividerLocation();
            EffectsManager.setEffect(nextPanel, effect, EffectsManager.TransitionType.APPEARING);
            frame.validate();
        }

        if (currentDemo == null) {
            topPanel.add(BorderLayout.CENTER, runningDemoPlaceholder);
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
                        
                        transition.start();
                        
                        // The following code sets up a custom effect, based on the
                        // location of the button clicked
                        // If you comment it out, you will just get the standard
                        // fade-in effect
                        
                        // First, get the button center
                        
                        Rectangle nodeBounds = demoTree.getRowBounds(selectedRow);
                        Point nodeCenter = new Point(
                                nodeBounds.x + nodeBounds.width/2,
                                nodeBounds.y + nodeBounds.height/2);
                        
                        // Now, create a new ScaleMoveIn effect based on the button location
                        ScaleMoveIn scaler = new ScaleMoveIn(nodeCenter.x, nodeCenter.y);
                        
                        // Now, create a Composite effect that combines our custom effect
                        // with a standard FadeIn effect
                        FadeIn fader = new FadeIn();
                        effect = new CompositeEffect(scaler);
                        effect.addEffect(fader);
                        
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
            topPanel.remove(activePanel);
            topPanel.add(nextPanel, BorderLayout.CENTER);
            activePanel = nextPanel;
            //vertSplitPane.setDividerLocation(sourcePaneLocation);
            System.out.println("topPanel pref="+topPanel.getPreferredSize());
        }
        
        
        
    }
           
    /**
     * Custom effect: scales and moves a component in to its end location
     * from a specified starting point
     */
    class ScaleMoveIn extends Effect {
        
        private Point startLocation = new Point();
        
        public ScaleMoveIn(int x, int y) {
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
    
    public class SourceVisibilityListener implements PropertyChangeListener {       
        public void propertyChange(PropertyChangeEvent event) {
            if (event.getPropertyName().equals("sourceCodeVisible")) {
                boolean sourceVisible = ((Boolean)event.getNewValue()).booleanValue();
                sourceCodePane.setExpanded(sourceVisible);
            } 
        }
        
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
            }
        });
    }                
    
}