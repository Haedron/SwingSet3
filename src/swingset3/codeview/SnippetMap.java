/*
 * SnippetMap.java
 *
 * Created on January 5, 2007, 1:36 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package swingset3.codeview;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Data structure to support maintaining Snippet information across a 
 * set of source code files:
 *
 *                        SnippetMap
 *                             |
 *            ---------------------------------------
 *            |                   |                 |
 *    snippet key "A"       Snippet key "B"     Snippet key "C"
 *          |                     |                 |
 *    ----------------          ------          -------------
 *    |               |              |                   |          
 *  File1.java    File3.java     File2.java          File3.java
 *       |            |              |                   |
 *  ----------       ---            ---                -----
 *  |        |        |              |                   |
 * snipA-1  snipA-2  snipA-3       snipB-1            snipC-1
 *
 *
 * This class also maintains a pointer to a &quot;current&quot; snippet within
 * a selected set.
 *
 *
 * @author aim
 */
public class SnippetMap {
    private HashMap <String,HashMap>snippetSets = new HashMap();

    private String currentKey;
    private HashMap<URL,ArrayList>currentSet;
    private URL currentFiles[];
    private int currentFileIndex;
    private int currentSnippetIndex;
    
    /** Creates a new instance of SnippetMap */
    public SnippetMap() {
    }
    
    public void add(String key, URL codeFile, Snippet snippet) {
        HashMap snippetSet = snippetSets.get(key);
        if (snippetSet == null) {
            // new key! so create new set...
            snippetSet = new HashMap<URL,ArrayList>();
            snippetSets.put(key, snippetSet);
        }
        addSnippetToSet(snippetSet, codeFile, snippet);
                                
    }
    
    protected void addSnippetToSet(HashMap<URL,ArrayList>set, URL file, Snippet snippet) {
        ArrayList snippets = set.get(file);
        if (snippets == null) {
            snippets = new ArrayList();
            set.put(file, snippets);
        }
        if (!snippets.contains(snippet)) {
            snippets.add(snippet);
        }
    }
    
    public Set keySet() {
        return snippetSets.keySet();
    }
    
    public URL[] getFilesForSet(String key) {
        HashMap<URL,ArrayList> snippetSet = snippetSets.get(key); 
        return (URL[])snippetSet.keySet().toArray(new URL[0]);
    }
    
    public Snippet[] getSnippetsForFile(String key, URL file) {
        HashMap<URL,ArrayList> snippetSet = snippetSets.get(key); 
        return (Snippet[])snippetSet.get(file).toArray(new Snippet[0]);
    }
    
    public URL getFileForSnippet(Snippet snippet) {
        HashMap<URL,ArrayList> snippetSet = snippetSets.get(snippet.key); 
        URL files[] = getFilesForSet(snippet.key);
        for(URL file : files) {
            ArrayList<Snippet> snippets = snippetSet.get(file);
            for(Snippet s : snippets) {
                if (s == snippet) {
                    return file;
                }
            }
        }
        return null;            
    }
    
    public boolean isEmpty() {
        return snippetSets.isEmpty();
    }
    
    public int getSize() {
        return snippetSets.size();
    }
    
    public void clear() {
        snippetSets.clear(); 
        setCurrentSet(null);
    }
    
    public void setCurrentSet(String key) {
        if (key == null) {
            // current snippet being cleared
            currentKey = null;
            currentSet = null;
            currentFiles = null;
            currentFileIndex = -1;
            currentSnippetIndex = -1;
        } else {            
            HashMap<URL,ArrayList>snippetSet = snippetSets.get(key);
            if (snippetSet == null) {
                throw new IllegalArgumentException("snippet key " + key + " does not exist.");
            }
            currentKey = key;
            currentSet = snippetSet;
            currentFiles = (URL[])currentSet.keySet().toArray(new URL[0]);
            currentFileIndex = 0;
            currentSnippetIndex = 0;
        }
        
    }
    
    public Snippet getCurrentSnippet() {
        if (currentKey != null && currentFileIndex != -1) {
            ArrayList<Snippet> snippets = currentSet.get(currentFiles[currentFileIndex]);
            return snippets.get(currentSnippetIndex);
        } else {
            return null;
        }
    }
    
    public boolean nextSnippetExists() {
        if (currentKey != null) {
            ArrayList<Snippet> snippets = currentSet.get(currentFiles[currentFileIndex]);
            if (currentSnippetIndex+1 < snippets.size()) {
                // There is a next snippet in the current file
                return true;
            }
            if (currentFileIndex+1 < currentFiles.length) {
                // There is another file containing the next snippet
                return true;
            }
        }
        return false;
    }
    
    public Snippet nextSnippet() {
        if (currentKey != null) {
            ArrayList<Snippet> snippets = currentSet.get(currentFiles[currentFileIndex]);
            if (currentSnippetIndex+1 < snippets.size()) {
                // There is a next snippet in the current file
                return snippets.get(++currentSnippetIndex);
            }
            if (currentFileIndex+1 < currentFiles.length) {
                // The next snippet is contained in the next file
                snippets = currentSet.get(currentFiles[++currentFileIndex]);
                currentSnippetIndex = 0;
                return snippets.get(currentSnippetIndex);
            }
        }
        return null;
    }
    
    public boolean previousSnippetExists() {
        if (currentKey != null) {
            ArrayList<Snippet> snippets = currentSet.get(currentFiles[currentFileIndex]);
            if (currentSnippetIndex-1 >= 0) {
                // There is a previous snippet in the current file
                return true;
            }
            if (currentFileIndex-1 >= 0) {
                // There is a previous file containing the previous snippet
                return true;
            }
        }
        return false;        
    }
    
    public Snippet previousSnippet() {
        if (currentKey != null) {
            ArrayList<Snippet> snippets = currentSet.get(currentFiles[currentFileIndex]);
            if (currentSnippetIndex-1 >= 0) {
                // There is a previous snippet in the current file
                return snippets.get(--currentSnippetIndex);
            }
            if (currentFileIndex-1 >= 0) {
                // The previous snippet is contained in the previous file
                snippets = currentSet.get(currentFiles[--currentFileIndex]);
                currentSnippetIndex = snippets.size() - 1;
                return snippets.get(currentSnippetIndex);
            }
        }
        return null;      
    }
        
}
