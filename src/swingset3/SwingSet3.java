
/* SwingSet3.java
 *
 * Created on June 2, 2006, 1:57 PM
 */

package swingset3;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import swingset3.codeview.CodeViewer;

/**
 *
 * @author  aim
 */
public class SwingSet3 {
    private static String demoClassNames[] = {
            "swingset3.toplevels.JFrameDemo",
            "swingset3.toplevels.JDialogDemo",
            "swingset3.toplevels.JWindowDemo",
                    
            //"swingset3.layoutContainers.JPanelDemo",
            //"swingset3.layoutContainers.JTabbedPaneDemo",
            //"swingset3.layoutContainers.JScrollPaneDemo",
                    
            "swingset3.controls.JButtonDemo",
            "swingset3.controls.JCheckBoxDemo",
            "swingset3.controls.JComboBoxDemo",
            "swingset3.controls.JLabelDemo",
            
            "swingset3.data.JListDemo",
            "swingset3.data.JTableDemo",
            "swingset3.data.JTreeDemo",
            
            "swingset3.text.JEditorPaneDemo"
    };
    
    static {
        // Property must be set *early* due to Apple Bug#3909714
        System.setProperty("apple.laf.useScreenMenuBar", "true");                
    }
    
    public static boolean onMac() {
        return System.getProperty("os.name").equals("Mac OS X");
    }
    
    private JFrame frame;
    private JSplitPane vertSplitPane;
    private JSplitPane horizSplitPane;
    private JTree demoSelectorTree;
    private DefaultMutableTreeNode demos;
    private JLabel runningDemosPlaceholder;
    private JTabbedPane runningDemosTabbedPane;
    private JPanel sourceCodePane;
    private CodeViewer codeViewer;
    
    private JCheckBoxMenuItem sourceCodeCheckboxItem;
    
    private JPopupMenu popup;
    
    private int sourcePaneLocation;
    private int dividerSize;
    
    private DemoList runningDemos;
    
    private Action quitDemoAction;
    private Action quitAllDemosAction;
    
    private PropertyChangeSupport pcs;

    
    /** Creates new form SwingSet3 */
    public SwingSet3() {
        pcs = new PropertyChangeSupport(this);
        
        // Create application model
        runningDemos = new DemoList();
        runningDemos.addDemoListListener(new RunningDemosListener());
 
        // Create GUI
        initComponents();  
        runningDemos.addPropertyChangeListener(new CurrentDemoListener());

        // Create application-level actions
        quitDemoAction = new QuitDemoAction(); 
        quitAllDemosAction = new QuitAllDemosAction();
        
        // Show GUI
        frame.setVisible(true);
    }
    
    protected void initComponents() {
        // Create toplevel frame
        frame = new JFrame("SwingSet3");
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        // Create menubar
        JMenuBar menubar = new JMenuBar();
        menubar.setName("menubar");
        
        // Create file menu
        JMenu fileMenu = new JMenu();
        fileMenu.setText("File");
        fileMenu.setName("fileMenu");
        fileMenu.addMenuListener(new FileMenuListener());
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
        
        // Create vertical splitpane with demos on top, source on bottom
        vertSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        vertSplitPane.setBorder(null);
        frame.add(vertSplitPane);
        
        // Create nested horizontal splitpane with demo selection tree on left, 
        // running demos on right
        horizSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        vertSplitPane.setTopComponent(horizSplitPane);

        // Create demo selection tree
        demos = new DefaultMutableTreeNode("Components");        
        demoSelectorTree = new JTree(demos);
        demoSelectorTree.setBorder(new EmptyBorder(2,8,2,8));
        demoSelectorTree.setRowHeight(28);
        demoSelectorTree.setShowsRootHandles(false);
        demoSelectorTree.addMouseListener(new DemoTreeClickListener());
        demoSelectorTree.setCellRenderer(new DemoSelectorTreeRenderer());
        JScrollPane scrollPane = new JScrollPane(demoSelectorTree);
        scrollPane.setPreferredSize(new Dimension(210,400)); // wide enough to avoid horiz scrollbar
        horizSplitPane.setLeftComponent(scrollPane);

        // Create tabbedpane to contain running demos
        runningDemosPlaceholder = new JLabel(
                new ImageIcon(SwingSet3.class.getResource("resources/images/demo_area.jpg")));
        runningDemosTabbedPane = new JTabbedPane();
        runningDemosTabbedPane.setPreferredSize(new Dimension(580,400));
        runningDemosTabbedPane.addChangeListener(new TabSelectionListener());
        //horizSplitPane.setRightComponent(runningDemosTabbedPane);
        horizSplitPane.setRightComponent(runningDemosPlaceholder);
        
        
        // Create source code pane with dismissable close button
        sourceCodePane = new JPanel();
        sourceCodePane.setLayout(new BorderLayout());
        Box box = Box.createHorizontalBox();
        box.add(Box.createHorizontalGlue());
        CloseButton closeButton = new CloseButton(new HideSourceCodeAction());
        closeButton.setToolTipText("hide source code");
        box.add(closeButton);
        //box.add(Box.createHorizontalStrut(2));
        sourceCodePane.add(BorderLayout.NORTH, box);        
        codeViewer = new CodeViewer();
        sourceCodePane.add(BorderLayout.CENTER, codeViewer);
        sourceCodePane.setPreferredSize(new Dimension(600,230));
        vertSplitPane.setBottomComponent(sourceCodePane);
        
        sourcePaneLocation = vertSplitPane.getDividerLocation();
        dividerSize = vertSplitPane.getDividerSize();        

        // Start loading available demos
        initializeDemos(getDemoClassNames());        
        
        // Create shareable popup menu for demo actions
        popup = new JPopupMenu();
        popup.add(new EditPropertiesAction());
        popup.add(new ViewCodeSnippetAction());

        // Ensure the demo tree categories come up initially expanded
        TreeNode nodes[] = new TreeNode[2];
        nodes[0] = demos;
        for(int i = 0; i < demos.getChildCount(); i++) {
            nodes[1] = demos.getChildAt(i);
            demoSelectorTree.makeVisible(new TreePath(nodes));
        }

        frame.pack();        
    }
    
    private ArrayList<String> getDemoClassNames() {
        ArrayList demoClassNamesList = new ArrayList<String>();
        for(String demoClassName: demoClassNames) {
            demoClassNamesList.add(demoClassName);
        }
        return demoClassNamesList;
    }
    
    protected void initializeDemos(List<String> demoClassNamesList) {
        DemoPropertyChangeListener demoPropertyListener = 
                new DemoPropertyChangeListener();
        
        for(String demoClassName : demoClassNamesList) {
            try {
                Class demoClass = Class.forName(demoClassName);
                Demo demo = null;
                if (Demo.class.isAssignableFrom(demoClass)) {
                    demo = (Demo)demoClass.newInstance();
                    
                } else {
                    // Wrap Demo 
                    demo = new Demo(demoClass);
                }
                demo.addPropertyChangeListener(demoPropertyListener);                

                String category = demo.getCategory();

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
                categoryNode.add(new DefaultMutableTreeNode(demo));
                       
                
            } catch (ClassNotFoundException cnfe) {
                System.err.println(cnfe);
                
            } catch (InstantiationException ie) {
                System.err.println(ie);
                
            } catch (IllegalAccessException iae) {
                System.err.println(iae);
            }
        }
        frame.validate(); // adjust size to fit loaded demos
    }
    
    public void runDemo(Demo demo) {
        if (!runningDemos.contains(demo)) {
            demo.setState(Demo.State.INITIALIZING);
            runningDemos.add(demo);
        } else {
            runningDemos.setSelected(demo);
        }
    }
    
    public void setSourceCodeVisible(boolean sourceVisible) {
        boolean oldSourceCodeVisible = isSourceCodeVisible();
        if (oldSourceCodeVisible != sourceVisible) {
            if (sourceVisible) {
                Demo currentDemo = runningDemos.getSelected();
                codeViewer.setSourceFiles(currentDemo != null?
                    currentDemo.getSourceFiles() : null);
            } else {
                // remember location of vertical split pane divider before hiding
                sourcePaneLocation = vertSplitPane.getDividerLocation(); 
                dividerSize = vertSplitPane.getDividerSize();
            } 
            sourceCodePane.setVisible(sourceVisible);
            if (sourceVisible) {
                vertSplitPane.setDividerLocation(sourcePaneLocation);
                vertSplitPane.setDividerSize(dividerSize);
            } else {
                vertSplitPane.setDividerSize(0);
            }
            sourceCodeCheckboxItem.setSelected(sourceVisible);
            frame.invalidate();
            vertSplitPane.revalidate();
            frame.validate();
            pcs.firePropertyChange("sourceCodeVisible", oldSourceCodeVisible, sourceVisible);
        }
    }
    
    public boolean isSourceCodeVisible() {
        return sourceCodePane.isVisible();
        
    } 
                                                        
    private void sourceCodeCheckboxItemStateChanged(ChangeEvent evt) {                                                    
        setSourceCodeVisible(sourceCodeCheckboxItem.isSelected());
    }
    
    class RunningDemosListener implements DemoList.Listener {
        public void added(DemoList.Event event) {
            Demo demo = event.getDemo();
            if (horizSplitPane.getRightComponent() != runningDemosTabbedPane) {
                horizSplitPane.setRightComponent(runningDemosTabbedPane);
            }
            runningDemosTabbedPane.addTab(demo.getName(), null,
                    new DemoPane(demo), demo.getOneLiner());
            runningDemos.setSelected(demo);

        }
        public void removed(DemoList.Event event) {
            Demo demo = event.getDemo();
            runningDemosTabbedPane.removeTabAt(
                    runningDemosTabbedPane.indexOfTab(demo.getName()));
            demo.setDemoComponent(null);
        }
    }
    
    class CurrentDemoListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals("selected")) {
                Demo oldCurrentDemo = (Demo)e.getOldValue();
                Demo currentDemo = (Demo)e.getNewValue();
                if (oldCurrentDemo != null){
                    System.out.println("DemoStop:"+ oldCurrentDemo.getName());
                    oldCurrentDemo.stop();
                }
                if (currentDemo != null) {
                    System.out.println("DemoStart:" + currentDemo.getName());
                    if (currentDemo.getDemoComponent() != null) {
                        // only start demo if we know the component has been instantiated
                        // by now; if embedded in html, it might not be quite yet...
                        currentDemo.start();
                    }
                    runningDemosTabbedPane.setSelectedIndex(
                            runningDemosTabbedPane.indexOfTab(currentDemo.getName()));
                }
                if (isSourceCodeVisible()) {
                    codeViewer.setSourceFiles(currentDemo != null? 
                        currentDemo.getSourceFiles() : null);
                }
            }
        }
    }
    
     class DemoTreeClickListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            JTree demoTree = (JTree)e.getSource();
            int selectedRow = demoTree.getRowForLocation(e.getX(), e.getY());            
            if (selectedRow != -1) {
                TreePath selPath = demoTree.getPathForLocation(e.getX(), e.getY());
                if (e.getClickCount() == 2) {
                    TreeNode node = (TreeNode)selPath.getLastPathComponent();
                    if (node.isLeaf()) {
                        // user double-clicked demo in tree, so run it
                        Demo demo = (Demo)((DefaultMutableTreeNode)node).getUserObject();
                        runDemo(demo);
                    }
                }                
            }
        }
    }
    
    // Listens to when the user selects a running demo in the tabbedpane
    class TabSelectionListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JTabbedPane tabbedPane = (JTabbedPane)e.getSource();
            DemoPane demoPane = (DemoPane)tabbedPane.getSelectedComponent();
            runningDemos.setSelected(demoPane != null? demoPane.getDemo() : null);
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
                    Demo currentDemo = runningDemos.getSelected();
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
    
    class FileMenuListener implements MenuListener {
        
        public void menuSelected(MenuEvent event) {
             JMenu fileMenu = (JMenu)event.getSource();
             Demo demos[] = runningDemos.toArray();
             for (Demo demo : demos) {
                 JMenuItem quitItem = new JMenuItem();
                 quitItem.putClientProperty("demo", demo);
                 quitItem.setHideActionText(true);
                 quitItem.setAction(quitDemoAction);
                 quitItem.setText("Quit " + demo.getName());
                 fileMenu.add(quitItem);
             }
             if (demos.length > 0) {
                 fileMenu.addSeparator();
                 JMenuItem quitAllItem = new JMenuItem("Quit All Demos");
                 quitAllItem.setAction(quitAllDemosAction);
                 fileMenu.add(quitAllItem);
             }
             if (!onMac()) {
                 // Mac puts Quit item in application menu
                 fileMenu.addSeparator();
                 JMenuItem quitMenuItem = new JMenuItem();
                 quitMenuItem.setText("Quit");
                 quitMenuItem.setName("quitMenuItem");
                 fileMenu.add(quitMenuItem);
             }
        }
        
        public void menuDeselected(MenuEvent event) {
            JMenu fileMenu = (JMenu)event.getSource();
            fileMenu.removeAll();
        }
        
        public void menuCanceled(MenuEvent event) {
            JMenu fileMenu = (JMenu)event.getSource();
            fileMenu.removeAll();
        }
    }
    
    public class QuitDemoAction extends AbstractAction {
        public void actionPerformed(ActionEvent event) {
            JMenuItem quitItem = (JMenuItem)event.getSource(); 
            Demo demo = (Demo)quitItem.getClientProperty("demo");
            runningDemos.remove(demo);
        }
    }
    
    public class QuitAllDemosAction extends AbstractAction {
        public QuitAllDemosAction() {
            super("Quit All Demos");
        }
        public void actionPerformed(ActionEvent e) {
            Demo demos[] = runningDemos.toArray();
            for(Demo demo: demos) {
                runningDemos.remove(demo);
            }
        }
    }
    
    
    private void registerPopups(JComponent component) {
        Component children[] = component.getComponents();
        for(Component child: children) {
            registerPopups((JComponent)child);
        }
        String snippetKey = (String)component.getClientProperty("snippetKey");
        if (snippetKey != null) {
            component.setComponentPopupMenu(popup);
        }
    }    
    

    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SwingSet3 swingset = new SwingSet3();
            }
        });
    }                
    
}