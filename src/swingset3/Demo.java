/*
 * Copyright %YEARS% Sun Microsystems, Inc.  All Rights Reserved.
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

    public @interface Name {
        String value();
    }
    
    public @interface Category {
        String value();
    }
    
    public @interface Description {
        String value();
    }
    
    public @interface SourceFiles {
        String[] sourceFiles();
    }
    
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
    
    protected Class<?> demoClass;
    protected String name;
    protected String category;
    protected String shortDescription; // used for tooltips
    protected String iconPath;
    protected Icon icon;
    protected String[] sourceFilePaths;
    protected URL[] sourceFiles;
    
    protected JComponent component;
    
    protected State state;
    
    protected Exception failException;
    
    private PropertyChangeSupport pcs;
    
    public Demo(Class<?> demoClass) {
        this.demoClass = demoClass;

        initializeProperties();
    }
            
    protected void initializeProperties() {
        
        // First look for DemoProperties annotation if it exists
        DemoProperties properties = demoClass.getAnnotation(DemoProperties.class);
        if (properties != null) {
            this.name = properties.value();
            this.category = properties.category();
            this.shortDescription = properties.description();
            this.iconPath = properties.iconFile();
            this.sourceFilePaths = properties.sourceFiles();
        } else {
            this.name = deriveNameFromClassName(demoClass);
            this.category = deriveCategoryFromPackageName(demoClass);
            this.shortDescription = "No demo description, run it to find out...";            
        }
        state = State.UNINITIALIZED;
        pcs = new PropertyChangeSupport(this);
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
            if (iconPath != null && !iconPath.equals("")) {
                // icon path was specified in DemoProperties annotation
                icon = getIconFromPath(iconPath);
            } else {
                // Look for icon with same name as demo class
                for(String ext : imageExtensions) {
                    icon = getIconFromPath(getIconImagePath(ext));
                    if (icon != null) {
                        break;
                    }
                }
            }
        }
        return icon;
    }

    protected String getIconImagePath(String extension) {
        // by default look for an image with the same name as the demo class
        return "resources/images/" + 
                demoClass.getSimpleName() + extension;
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
            if (sourceFilePaths != null && !sourceFilePaths[0].equals("")) {
                initSourceFiles(sourceFilePaths);
                
            } else {
                // by default return just the demo class's source file url
                sourceFilePaths = new String[1];
                sourceFilePaths[0] = "sources/" +
                        demoClass.getName().replace(".", "/") + ".java";
                System.out.println(sourceFilePaths[0]);
                initSourceFiles(sourceFilePaths);               
            }
        }
        return sourceFiles;
    }
    
    protected void initSourceFiles(String sourceFilePaths[]) {
        sourceFiles = new URL[sourceFilePaths.length];
        for(int i = 0; i < sourceFilePaths.length; i++) {
            sourceFiles[i] = getClass().getClassLoader().getResource(sourceFilePaths[i]);
            if (sourceFiles[i] == null) {
                System.err.println("warning: unable to load source file: " + sourceFilePaths[i]);
            }
        }
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
