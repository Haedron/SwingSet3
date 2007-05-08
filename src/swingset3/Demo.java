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
    
    public enum State { UNINITIALIZED, INITIALIZING, INITIALIZED, RUNNING, PAUSED, FAILED }
    
    private static final String imageExtensions[] = {".gif", ".png", ".jpg"};
    
    public static String deriveCategoryFromPackageName(String className) {
        String parts[] = className.split("\\.");
        // return the right-most package name
        return parts.length >= 2? parts[parts.length-2] : "general";
    }
    
    public static String deriveCategoryFromPackageName(Class demoClass) {
        String packageName = demoClass.getPackage() != null? 
            demoClass.getPackage().getName() : null;
        if (packageName != null) {
            // if root package is swingset3, then remove it
            String swingsetPackageName = Demo.class.getPackage().getName();
            if (packageName.startsWith(swingsetPackageName + ".demos.")) {
                packageName = packageName.replaceFirst(swingsetPackageName + ".demos.",
                        "");
            }
        }
        return packageName != null? packageName : "general";        
    }
    
    public static String deriveNameFromClassName(String className) {
        String simpleName = className.substring(className.lastIndexOf(".")+1, className.length());
        return convertToDemoName(simpleName);
    }
    
    public static String deriveNameFromClassName(Class demoClass) {
        String className = demoClass.getSimpleName();
        return convertToDemoName(className);
    }
        
    protected static String convertToDemoName(String simpleClassName) {
        StringBuffer nameBuffer = new StringBuffer();
        if (simpleClassName.endsWith("Demo")) {
            nameBuffer.append(simpleClassName.substring(0, simpleClassName.indexOf("Demo")));
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
    
    protected Exception failException;
    
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
    
    public String getShortDescription() {
        String shortDescription = null;
        
        // look for static getShortDescription method on demo class
        try {
            Method getShortDescriptionMethod = demoClass.getMethod("getShortDescription", (Class[])null);
            shortDescription = (String)getShortDescriptionMethod.invoke(component, (Object[])null);
        } catch (NoSuchMethodException nsme) {
            // okay, no getShortDescription method exists
        } catch (IllegalAccessException iae) {
            System.err.println(iae);
            iae.printStackTrace();
        } catch (java.lang.reflect.InvocationTargetException ite) {
            System.err.println(demoClass.getName() +
                    " getShortDescription method failed: " + ite.getMessage());
            ite.printStackTrace();
        }
        if (shortDescription == null) {
            // last resort: try inheriting tooltip if component is instantiated
            shortDescription =  component != null? component.getToolTipText() : null;
        }
        return shortDescription;
    }
    
    public void initialize() {
        setState(State.INITIALIZING);
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
                        " getSourceFiles method failed: " + ite.getCause());
                ite.printStackTrace();
            }
            // by default return just the demo class's source file url
            if (sourceFiles == null) {
                sourceFiles = new URL[1];
                String className = demoClass.getName();
                
                ClassLoader cl = getClass().getClassLoader();
                sourceFiles[0] = cl.getResource("sources/" +
                    className.replace(".", "/") + ".java");
                
            }
        }
        return sourceFiles;
    }
    
    void setDemoComponent(JComponent component) {
        if (component != null && !demoClass.isInstance(component)) {
            setState(State.FAILED);
            IllegalArgumentException e =
                    new IllegalArgumentException("component must be an instance of " +
                    demoClass.getCanonicalName());
            failException = e;
            throw e;
        }
        JComponent old = this.component;
        this.component = component;
        
        System.out.println(this.getName() + ":setDemoComponent: " + this.component);
        setState(component != null? State.INITIALIZED : State.UNINITIALIZED);
        pcs.firePropertyChange("demoComponent", old, component);

    }
    
    public JComponent createDemoComponent() {
        JComponent component = null;
        try {
            component = (JComponent)demoClass.newInstance();
            setDemoComponent(component);
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
            failException = e;
            setState(State.FAILED);
        }        
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
        System.out.println("***** "+getName() + ":setState="+state);
        pcs.firePropertyChange("state", oldState, state);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        pcs.addPropertyChangeListener(pcl);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        pcs.removePropertyChangeListener(pcl);
    }
        
    public void start() {

        try {
            Method startMethod = demoClass.getMethod("start", (Class[])null);
            startMethod.invoke(component, (Object[])null);
            setState(State.RUNNING);
        } catch (NoSuchMethodException nsme) {
            setState(State.RUNNING);
            // okay, no start method exists
        } catch (IllegalAccessException iae) {
            System.err.println(iae);
            iae.printStackTrace();
            failException = iae;
            setState(State.FAILED);
        } catch (java.lang.reflect.InvocationTargetException ite) {
            System.err.println(demoClass.getName() +
                    " start method failed: " + ite.getMessage());
            ite.printStackTrace();
            failException = ite;
            setState(State.FAILED);
        } catch (NullPointerException npe) {
            System.out.println(getName()+":started before demo component was created.");
            failException = npe;
            setState(State.FAILED);
        }
    };
    
    public void pause() {
        setState(State.PAUSED);
        try {
            Method stopMethod = demoClass.getMethod("pause", (Class[])null);
            stopMethod.invoke(component, (Object[])null);

        } catch (NoSuchMethodException nsme) {
            // okay, no pause method exists

        } catch (IllegalAccessException iae) {
            System.err.println(iae);
            failException = iae;
            setState(State.FAILED);
            iae.printStackTrace();
        } catch (java.lang.reflect.InvocationTargetException ite) {
            System.err.println(demoClass.getName() + 
                    " pause method failed: " + ite.getMessage());
            failException = ite;
            setState(State.FAILED);
            ite.printStackTrace();
        } catch (NullPointerException npe) {
            System.out.println(getName()+":paused before demo component was created.");
        }
    };
}
