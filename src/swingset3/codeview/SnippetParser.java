/*
 * SnippetParser.java
 *
 * Created on June 14, 2006, 3:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package swingset3.codeview;


import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Segment;
import swingset3.*;

/**
 * Parses a source code document, looking for blocks of code 
 * bracketed by comments including start and ending &quot;snippet&quot; markers.  
 *
 * @author aim
 */
public class SnippetParser {
    private final static String START_MARKER = "<snip>";
    private final static String END_MARKER = "</snip>";
    
    protected SnippetParser() {
        // never instantiate
    }
    
    public static HashMap<String,ArrayList<Snippet>> parse(Document document) {
        
        return parse(document, START_MARKER, END_MARKER);
    }
    
    public static HashMap<String,ArrayList<Snippet>> parse(Document document,
            String startMarker, String endMarker) {
        HashMap<String,ArrayList<Snippet>> snippetMap = new HashMap();
        Stack<Snippet> stack = new Stack(); // snippets may be nested
        char startMarkerChars[] = startMarker.toCharArray();;
        char endMarkerChars[] = endMarker.toCharArray();
        int nleft = document.getLength();
        Segment segment = new Segment();
        int offset = 0;
        int lineStart = 0;
        int charCount = 0;
        int startMarkerIndex = 0;
        int endMarkerIndex = 0;
        StringBuffer keyBuf = new StringBuffer();
        
        segment.setPartialReturn(true);
        try {
            while (nleft > 0) {
                
                document.getText(offset, nleft, segment);
                
                for(char c = segment.first(); c != CharacterIterator.DONE; 
                    c = segment.next()) {
                    
                    if (!stack.isEmpty()) {
                        // already found a begin marker, so looking for end marker
                        if (c == endMarkerChars[endMarkerIndex]) {
                            endMarkerIndex++;
                            if (endMarkerIndex == endMarkerChars.length) {
                                // found end marker, so register snippet
                                Snippet snippet = stack.pop();
                                if (snippet.key.equals("tempkey")) {
                                    // haven't stored key yet
                                    snippet.key = keyBuf.toString().trim();
                                }
                                snippet.endLine = charCount + 1;
                                ArrayList snippetList = snippetMap.get(snippet.key);
                                //System.out.println("Snippet pop: " + snippet.key + " " +
                                //        snippet.startLine + " to " + snippet.endLine);
                                if (snippetList == null) {
                                    snippetList = new ArrayList();
                                //    System.out.println("register new snippet key: " + snippet.key);
                                    snippetMap.put(snippet.key, snippetList);
                                }
                                //System.out.println("adding snippet:" +
                                //        snippet.key + " " + snippet.startLine);
                                snippetList.add(snippet);                                
                                endMarkerIndex = 0;
                            }
                        } else {
                            endMarkerIndex = 0;
                            
                            if (stack.peek().startLine == lineStart){
                                // build snippet key
                                keyBuf.append(c);
                            }
                        }                        
                    }
                    if (c == startMarkerChars[startMarkerIndex]) {
                        startMarkerIndex++;
                        if (startMarkerIndex == startMarkerChars.length) {
                            // found new begin marker
                            //System.out.println("Snippet push: "+ lineStart);
                            if (!stack.isEmpty()) {
                                // nested snippet, save previous key before pushing new one
                                Snippet snippet = stack.peek();
                                snippet.key = keyBuf.toString().trim();
                            }
                            stack.push(new Snippet(document, "tempkey", lineStart));
                            keyBuf.setLength(0);
                            startMarkerIndex = 0;
                        }
                        
                    } else {
                        startMarkerIndex = 0;
                    }
                    charCount++;
                    if (c == '\n') {
                        lineStart = charCount;                        
                    }
                }                
                nleft -= segment.count;
                offset += segment.count;
            }
        } catch (BadLocationException e) {
            System.err.println(e);
        }
        return snippetMap;        
    }    
}
