/*
 * DemoList.java
 *
 * Created on October 17, 2006, 2:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package swingset3;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.EventListener;
import javax.swing.event.EventListenerList;

/**
 * Simple application model class for tracking all the running demos
 * as well as the currently viewable one.  
 *
 * TODO: could be made into a generified TalkingList class (?)
 *
 * @author aim
 */
public class DemoList {
    private ArrayList<Demo> demoList;
    private Demo selected;
    
    private EventListenerList listenerList = new EventListenerList();

    
    /** Creates a new instance of DemoList */
    public DemoList() {
        demoList = new ArrayList<Demo>();
    }
    
    public void add(Demo demo) {
        demoList.add(demo);
        fireDemoListEvent(new DemoList.Event(demo, 
                DemoList.Event.Type.ADDED));
    }
    
    public void remove(Demo demo) {
        if (selected == demo) {
            setSelected(null);
        }
        demoList.remove(demo);
        fireDemoListEvent(new DemoList.Event(demo, 
                DemoList.Event.Type.REMOVED));
    }
    
    public Demo get(int index) {
        return demoList.get(index);
    }
    
    public boolean contains(Demo demo) {
        return demoList.contains(demo);
    }
    
    public Demo[] toArray() {
        return (Demo[])demoList.toArray(new Demo[demoList.size()]);
    }
    
    public void setSelected(Demo demo) {
        if (demo == null || contains(demo)) {
            if (selected != demo) { 
               Demo oldSelected = selected;
               selected = demo;
               firePropertyChangeEvent("selected", oldSelected, selected);
            }
        }
    }
    
    public Demo getSelected() {
        return selected;
    }

    public void addDemoListListener(DemoList.Listener l) {
        listenerList.add(DemoList.Listener.class, l);
    }
    
    public void removeDemoListListener(DemoList.Listener l) {
        listenerList.remove(DemoList.Listener.class, l);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        listenerList.add(PropertyChangeListener.class, l);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener l) {
        listenerList.remove(PropertyChangeListener.class, l);
    }
    
    protected void firePropertyChangeEvent(String propertyName, Object oldValue, Object newValue) {
        PropertyChangeListener[] listeners = listenerList.getListeners(PropertyChangeListener.class);
 
        for (PropertyChangeListener l : listeners) {     
            l.propertyChange(new PropertyChangeEvent(this, propertyName, oldValue, newValue));
        }        
    }
    
    protected void fireDemoListEvent(DemoList.Event event) {
        DemoList.Listener[] listeners = listenerList.getListeners(DemoList.Listener.class);
 
        for (DemoList.Listener l : listeners) {
            switch(event.getType()) {
                case ADDED:
                    l.added(event);
                    break;
                case REMOVED:
                    l.removed(event);
            }
        }
    }
     
    public static class Event {
        public enum Type { ADDED, REMOVED }
        private Demo demo;
        private Type type;
        
        public Event(Demo demo, Type type) {
            this.demo = demo;
            this.type = type;
        }
        public Demo getDemo() { return demo; }
        public Type getType() { return type; }
    }
    
    public static interface Listener extends EventListener {
        void added(DemoList.Event e);
        void removed(DemoList.Event e);
    }
    
}
