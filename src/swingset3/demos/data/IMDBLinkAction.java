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

package swingset3.demos.data;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import swingset3.hyperlink.HyperlinkCellRenderer;
import swingset3.hyperlink.Link;

/**
 *
 * @author aim
 */
public class IMDBLinkAction extends AbstractAction {
    @Override
    public void actionPerformed(ActionEvent event) {
        Link imdbLink = null;
        if (event.getSource() instanceof HyperlinkCellRenderer) {
            HyperlinkCellRenderer renderer = (HyperlinkCellRenderer)event.getSource();
            int row = renderer.getActiveHyperlinkRow();
            int column = renderer.getActiveHyperlinkColumn();
        }
            
            
        try {
            URI imdbURI = imdbLink.getUri();
            if (imdbURI == null) {
                imdbURI = new URI(getImdbURI(imdbLink.getDisplayText()));
                imdbLink.setUri(imdbURI);
            }
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(imdbURI);
                
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex);
        }
        
    }

    public static String getImdbURI(String searchKey) {
        ArrayList<String> matches = new ArrayList();
        String imdbURL = null;
        BufferedReader reader = null;
        try {
            // google rejects the request with a 403 return code!
            //URL url = new URL("http://www.google.com/search?q=Dazed+and+confused");
            String urlKey = URLEncoder.encode(searchKey, "UTF-8");
            URL url = new URL("http://search.yahoo.com/search?ei=utf-8&fr=sfp&p=imdb+"+urlKey+"&iscqry=");
            
            // Send data
            URLConnection conn = url.openConnection();
            conn.connect();
            
            // Get the response
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            
            String inputLine;
            String magicString = "www.imdb.com/title/";
            while ((inputLine = reader.readLine()) != null) {
                if (inputLine.contains(magicString)) {
                    int index = inputLine.indexOf(magicString);
                    imdbURL = "http://"+inputLine.substring(index, index+magicString.length()+9);
                    matches.add(imdbURL);                   
                }
            }
            reader.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }
        if (matches.size() > 1) {
            for(String matchURL: matches) {
                
            }
        }
    
    return imdbURL;
    }
}

/*    
    protected boolean verifyYear(String imdbURL, String movieYear) {
        boolean yearMatches = false;
        try {
            URLConnection conn = new URL(imdbURL).openConnection();
            conn.connect();
            
            // Get the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                int index = inputLine.indexOf("</title>");
                if (index != -1) {
                    // looking for "<title>movie title (YEAR)</title>"
                    String year = inputLine.substring(index-5, index-1);
                    System.out.println("comparing years: "+ movieYear+" to "+ year);
                    yearMatches = year.equals(movieYear);
                    break;
                }
            }
            reader.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e);
        }
        return yearMatches;
    }
    
}
 */
    
