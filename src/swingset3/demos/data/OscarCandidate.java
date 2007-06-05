/*
 * Copyright 2007 Sun Microsystems, Inc.  All Rights Reserved.
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
