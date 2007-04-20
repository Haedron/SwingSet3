/*
 * OscarDataParser.java
 *
 * Created on March 30, 2007, 1:40 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package swingset3.demos.data;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class OscarDataParser extends DefaultHandler {
    private static String categoryStrings[] = {
        "actor", "actress", "bestPicture",
        "actorSupporting", "actressSupporting", "artDirection", 
        "assistantDirector", "director", "cinematography",
        "costumeDesign", "danceDirection", "docFeature",
        "docShort", "filmEditing", "foreignFilm",
        "makeup", "musicScore", "musicSong",
        "screenplayAdapted", "screenplayOriginal", "shortAnimation",
        "shortLiveAction", "sound", "soundEditing",
        "specialEffects", "visualEffects", "writing",
        "engEffects", "uniqueArtisticPicture"
    };
    
    private static OscarCandidate.Category categories[] = {
        OscarCandidate.Category.BEST_ACTOR, OscarCandidate.Category.BEST_ACTRESS, OscarCandidate.Category.BEST_PICTURE,
        OscarCandidate.Category.BEST_SUPPORTING_ACTOR, OscarCandidate.Category.BEST_SUPPORTING_ACTRESS, OscarCandidate.Category.BEST_ART_DIRECTION, 
        OscarCandidate.Category.BEST_ASSISTANT_DIRECTOR, OscarCandidate.Category.BEST_DIRECTOR, OscarCandidate.Category.BEST_CINEMATOGRAPHY, 
        OscarCandidate.Category.BEST_COSUME_DESIGN, OscarCandidate.Category.BEST_DANCE_DIRECTION, OscarCandidate.Category.BEST_FEATURE_DOCUMENTARY, 
        OscarCandidate.Category.BEST_SHORT_DOCUMENTARY, OscarCandidate.Category.BEST_FILM_EDITING, OscarCandidate.Category.BEST_FOREIGN_FILM, 
        OscarCandidate.Category.BEST_MAKEUP, OscarCandidate.Category.BEST_MUSICAL_SCORE, OscarCandidate.Category.BEST_SONG, 
        OscarCandidate.Category.BEST_ADAPTED_SCREENPLAY, OscarCandidate.Category.BEST_ORIGINAL_SCREENPLAY, OscarCandidate.Category.BEST_ANIMATION_SHORT, 
        OscarCandidate.Category.BEST_LIVE_ACTION_SHORT, OscarCandidate.Category.BEST_SOUND, OscarCandidate.Category.BEST_SOUND_EDITING, 
        OscarCandidate.Category.BEST_SPECIAL_EFFECTS, OscarCandidate.Category.BEST_VISUAL_EFFECTS, OscarCandidate.Category.BEST_ENGINEERING_EFFECTS, 
        OscarCandidate.Category.BEST_WRITING, OscarCandidate.Category.MOST_UNIQUE_ARTISTIC_PICTURE
    };          
    
    
    ArrayList<OscarCandidate> candidates;
    
    private String tempVal;
    
    //to maintain context
    private OscarCandidate tempOscarCandidate;
        
    public OscarDataParser(){
    }
    
    public List<OscarCandidate> parseDocument(URL oscarURL) {
        candidates = new ArrayList();
                
        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            
            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();
            
            //parse the file and also register this class for call backs
            sp.parse(oscarURL.openStream(), this);
            
        }catch(SAXException se) {
            se.printStackTrace();
        }catch(ParserConfigurationException pce) {
            pce.printStackTrace();
        }catch (IOException ie) {
            ie.printStackTrace();
        }
        return candidates;
    }
        
    
    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        for(int i = 0; i < categoryStrings.length; i++) {            
            if(qName.equalsIgnoreCase(categoryStrings[i])) {
                tempOscarCandidate = new OscarCandidate(categories[i]);
                tempOscarCandidate.setYear(attributes.getValue("year"));
                return;
            }
        }
    }
    
    
    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }
    
    public void endElement(String uri, String localName, String qName) throws SAXException {
        for(String category: categoryStrings) {        
            if (qName.equalsIgnoreCase(category)) {
                //add it to the list
                candidates.add(tempOscarCandidate);
                System.out.print(".");
            
            } else if (qName.equalsIgnoreCase("won")) {
                tempOscarCandidate.setWinner(true);
            } else if (qName.equalsIgnoreCase("lost")) {
                tempOscarCandidate.setWinner(false);
            } else if (qName.equalsIgnoreCase("movie")) {
                tempOscarCandidate.setMovie(tempVal);
            } else if (qName.equalsIgnoreCase("person")) {
                tempOscarCandidate.getPersons().add(tempVal);
            }
        }
        
    }
    
 
    
}

