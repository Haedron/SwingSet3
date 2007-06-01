/*
 * DemoProperties.java
 *
 * Created on June 1, 2007, 1:55 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package swingset3;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation type for specifying meta-data about Demo
 * @author aim
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DemoProperties {
    String value(); // Name
    String category();
    String description();
    String iconFile() default ""; 
    String[] sourceFiles() default "";
}
