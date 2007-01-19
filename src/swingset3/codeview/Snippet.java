/*
 * Snippet.java
 *
 * Created on June 6, 2006, 2:07 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package swingset3.codeview;

import javax.swing.text.Document;

/**
 *
 * @author aim
 */
public class Snippet {
    Document document;
    String key = null;
    int startLine = -1;
    int endLine = -1;
    
    public Snippet(Document document, String key, int startLine) {
        this.document = document;
        this.key = key;
        this.startLine = startLine;
    }
}