/*
 * Demo.java
 *
 * Created on June 6, 2006, 10:40 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package swingset3;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * Wrapper class which encapsulates a GUI component to be displayed
 * as a SwingSet3 demo.
 * @author aim
 */
public class Demo {
    
    public enum State { UNINITIALIZED, INITIALIZING, RUNNING, PAUSED }
    
    private static final String imageExtensions[] = {".gif", ".png", ".jpg"};
    
    private static String deriveCategoryFromPackageName(Class demoClass) {
        String packageName = demoClass.getPackage() != null? 
            demoClass.getPackage().getName() : null;
        if (packageName != null) {
            // if root package is swingset3, then remove it
            String swingsetPackageName = Demo.class.getPackage().getName();
            if (packageName.startsWith(swingsetPackageName + ".")) {
                packageName = packageName.replaceFirst(swingsetPackageName + ".",
                        "");
            }
        }
        return packageName != null? packageName : "general";        
    }
    
    private static String deriveNameFromClassName(Class demoClass) {
        String className = demoClass.getSimpleName();
        StringBuffer nameBuffer = new StringBuffer();
        if (className.endsWith("Demo")) {
            nameBuffer.append(className.substring(0, className.indexOf("Demo")));
            nameBuffer.append(" ");
            nameBuffer.append("Demo");
        }
        return nameBuffer.toString();
    }
    
    protected Class demoClass;
    protected String name;
    protected String category;
    
    protected JComponent component;
    protected Icon icon = null;
    protected URL[] sourceFiles = null;
    
    protected State state;
    
    private PropertyChangeSupport pcs;
    
    public Demo(Class demoClass) {
        this(demoClass, 
             deriveCategoryFromPackageName(demoClass), 
             deriveNameFromClassName(demoClass));
    }
    
    public Demo(Class demoClass, String category, String name) {
        this.demoClass = demoClass;
        this.category = category;
        this.name = name;
        this.pcs = new PropertyChangeSupport(this);
        this.state = State.UNINITIALIZED;
    }
    
    public Class getDemoClass() {
        return demoClass;
    }
 
    public String getName() {
        return name;
    }
    
    public String getCategory() {
        return category;
    }
    
    public Icon getIcon() {
        if (icon == null) {
            for(String ext : imageExtensions) {
                icon = getIconFromPath(getIconImagePath(ext));
                if (icon != null) {
                    break;
                }
            }
            if (icon == null) {
                icon = getIconFromPath(getBeanInfoIconPath());
            }
        }
        return icon;
    }

    protected String getIconImagePath(String extension) {
        // by default look for an image with the same name as the demo class
        return "resources/images/" + 
                demoClass.getSimpleName() + extension;
    }
    
    protected String getBeanInfoIconPath() {
        // look for standard Swing component beaninfo icon
        return "resources/images/" + 
                getClass().getSimpleName().replaceFirst("Demo", "Color32") + 
                ".gif";
    }
    
    private Icon getIconFromPath(String path) {
        Icon icon = null;
        URL imageURL = demoClass.getResource(path);
        if (imageURL != null) {
            icon = new ImageIcon(imageURL);
        }
        return icon;
    }
    
    public String getOneLiner() {
        return component != null? component.getToolTipText() : "need to fix this";
    }
    
    public URL getHTMLDescription() {
        // by default look for an html file with the same name as the demo class
        return demoClass.getResource("resources/" + 
                demoClass.getSimpleName() + ".html");    
    }
    
    public URL[] getSourceFiles() {
        if (sourceFiles == null) {
            // first look for getSourceFiles method
            try {
                Method getSourceFilesMethod = demoClass.getMethod("getSourceFiles", (Class[])null);
                sourceFiles = (URL[])getSourceFilesMethod.invoke(component, (Object[])null);
            } catch (NoSuchMethodException nsme) {
                // okay, no getSourceFiles method exists
            } catch (IllegalAccessException iae) {
                System.err.println(iae);
                iae.printStackTrace();
            } catch (java.lang.reflect.InvocationTargetException ite) {
                System.err.println(demoClass.getName() +
                        " getSourceFiles method failed: " + ite.getMessage());
                ite.printStackTrace();
            }
            // by default return just the demo class's source file url
            if (sourceFiles == null) {
                sourceFiles = new URL[1];
                String className = demoClass.getName();
                System.out.println("source: "+
                    "../sources/" +
                    className.replaceAll("\\.", "/") + ".java");
                sourceFiles[0] = getClass().getResource("../sources/" +
                    className.replaceAll("\\.", File.separator) + ".java");
            }
        }
        return sourceFiles;
    }
    
    void setDemoComponent(JComponent component) {
        if (component != null && !demoClass.isInstance(component)) {
            throw new IllegalArgumentException("component must be an instance of " +
                    demoClass.getCanonicalName());
        }
        JComponent old = this.component;
        this.component = component;
        pcs.firePropertyChange("demoComponent", old, component);
        if (component == null) {
            setState(State.UNINITIALIZED);
        }
    }
    
    public JComponent createDemoComponent() {
        JComponent component = null;
        try {
            component = (JComponent)demoClass.newInstance();
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
        }
        setDemoComponent(component);
        return component;          
    }
    
    public JComponent getDemoComponent() {
        return component;    
    } 
    
    public State getState() {
        return state;
    }
    
    protected void setState(State state) {
        State oldState = this.state;
        this.state = state;
        pcs.firePropertyChange("state", oldState, state);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        pcs.addPropertyChangeListener(pcl);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        pcs.removePropertyChangeListener(pcl);
    }
        
    public void start() {
        setState(State.RUNNING);
        try {
            Method startMethod = demoClass.getMethod("start", (Class[])null);
            startMethod.invoke(component, (Object[])null);
        } catch (NoSuchMethodException nsme) {
            // okay, no start method exists
        } catch (IllegalAccessException iae) {
            System.err.println(iae);
            iae.printStackTrace();
        } catch (java.lang.reflect.InvocationTargetException ite) {
            System.err.println(demoClass.getName() +
                    " start method failed: " + ite.getMessage());
            ite.printStackTrace();
        }
    };
    
    public void stop() {
        setState(State.PAUSED);
        try {
            Method stopMethod = demoClass.getMethod("stop", (Class[])null);
            stopMethod.invoke(component, (Object[])null);
        } catch (NoSuchMethodException nsme) {
            // okay, no start method exists
        } catch (IllegalAccessException iae) {
            System.err.println(iae);
            iae.printStackTrace();
        } catch (java.lang.reflect.InvocationTargetException ite) {
            System.err.println(demoClass.getName() + 
                    " stop method failed: " + ite.getMessage());
            ite.printStackTrace();
        }
    };
}
