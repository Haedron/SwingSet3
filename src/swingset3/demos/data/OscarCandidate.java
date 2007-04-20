/*
 * OscarCandidate.java
 *
 * Created on March 30, 2007, 1:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package swingset3.demos.data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aim
 */
public class OscarCandidate {
    public enum Category { 
        BEST_ACTOR, BEST_ACTRESS, BEST_PICTURE,
        BEST_SUPPORTING_ACTOR, BEST_SUPPORTING_ACTRESS, BEST_ART_DIRECTION, 
        BEST_ASSISTANT_DIRECTOR, BEST_DIRECTOR, BEST_CINEMATOGRAPHY, 
        BEST_COSUME_DESIGN, BEST_DANCE_DIRECTION, BEST_FEATURE_DOCUMENTARY, 
        BEST_SHORT_DOCUMENTARY, BEST_FILM_EDITING, BEST_FOREIGN_FILM, 
        BEST_MAKEUP, BEST_MUSICAL_SCORE, BEST_SONG, 
        BEST_ADAPTED_SCREENPLAY, BEST_ORIGINAL_SCREENPLAY, BEST_ANIMATION_SHORT, 
        BEST_LIVE_ACTION_SHORT, BEST_SOUND, BEST_SOUND_EDITING, 
        BEST_SPECIAL_EFFECTS, BEST_VISUAL_EFFECTS, BEST_ENGINEERING_EFFECTS,
        BEST_WRITING, MOST_UNIQUE_ARTISTIC_PICTURE
    };        
    
    private Category category;
    private String year;
    private boolean winner = false;
    private String movie;
    private ArrayList persons;
    
    /** Creates a new instance of OscarCandidate */
    public OscarCandidate(Category category) {
        this.category = category;
        persons = new ArrayList();
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    public String getMovie() {
        return movie;
    }

    public void setMovie(String movie) {
        this.movie = movie;
    }

    public List getPersons() {
        return persons;
    }
    
    
}
