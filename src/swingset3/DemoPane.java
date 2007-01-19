/*
 * DemoPane.java
 *
 * Created on September 8, 2006, 10:35 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package swingset3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.text.ComponentView;
import javax.swing.text.Document;
import javax.swing.text.View;

/**
 *
 * @author aim
 */
public class DemoPane extends JPanel {
    
    private JEditorPane descriptionPane;    
    private Demo demo;
    
    public DemoPane(Demo demo) {  
        this.demo = demo;
        
        setLayout(new BorderLayout()); // ensure components fills panel

        // If demo has HTML description, load with embedded demo component
        URL descriptionURL = demo.getHTMLDescription();
        if (descriptionURL != null) {
            // Create scrollable text component to display demo HTML description
            descriptionPane = new JEditorPane();
            descriptionPane.setEditable(false);
            descriptionPane.setContentType("text/html");
            descriptionPane.setMargin(new Insets(0,8,0,8));
            descriptionPane.setBorder(null);
            descriptionPane.setOpaque(false);
            descriptionPane.addPropertyChangeListener(new DemoLoadListener());
            
            JScrollPane scrollPane = new JScrollPane(descriptionPane);
            scrollPane.setBorder(null);
            add(scrollPane);            
 
            // Since page may load asynchronously, we need to wait for the event
            // notifying it's done so we can grab the demo component handle
            try {
                descriptionPane.setPage(descriptionURL);
            } catch (IOException e) {
                System.err.println("couldn't load description from URL:" + descriptionURL);
                e.printStackTrace();
            } 
 
        } else {
            // no HTML description, just add demo component
            add(demo.createDemoComponent());
        }
      
    }
    
    public Demo getDemo() {
        return demo;
    }
    
    // recursively searches views in editor pane to find the embedded 
    // demo component matching the targetClass
    private JComponent findComponent(View view, Class targetClass) {
        JComponent component = null;
        int i =  0;
        while(i < view.getViewCount() && component == null) {
            View childView = view.getView(i++);
            System.out.print("  ");
            component = findComponent(childView, targetClass);            
        }
        
        if (component == null && view instanceof ComponentView) {
            Component c = ((ComponentView)view).getComponent();
            if (targetClass.isInstance(c)) {
                component = (JComponent)c;
            }
        }
        return component;
    }
    
    // Registered on descriptionPane to track when a demo is loaded
    // and obtain the handle to the associated demo component
    private class DemoLoadListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent pce) {
            String propertyName = pce.getPropertyName();
            if (propertyName.equals("page")) {
                JComponent demoComponent =
                   findComponent(descriptionPane.getUI().getRootView(descriptionPane),
                        demo.getDemoClass());
                if (demoComponent != null) {
                    demo.setDemoComponent(demoComponent);
                } else {
                    System.err.println("error: couldn't find demo component for " +
                            demo.getName());
                }
            }
        }
    }

    
}
