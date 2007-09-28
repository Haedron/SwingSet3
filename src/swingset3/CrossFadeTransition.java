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

package swingset3;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;

import org.jdesktop.animation.timing.TimingTargetAdapter;
import org.jdesktop.animation.transitions.TransitionTarget;

/**
 *
 * Class loosely based on org.jdesktop.animation.ScreenTransition which
 * simplifies the task of implementing a crossfade transition for
 * a GUI contained in a RootPane.
 *
 * @author aim
 */
public class CrossFadeTransition {
        
    /**
     * The component where the transition animation occurs.  This
     * component (which is set to be the glass pane) is visible
     * during the transition, but is otherwise invisible.
     */
    private AnimationLayer animationLayer;
    
    /**
     * The component supplied at contruction time that holds the
     * actual components added to ScreenTransition by the application.
     * Keeping this container separate from ScreenTransition allows us
     * to render the AnimationLayer during the transitions and separate
     * the animation sequence from the actual container of the components.
     */
    private JComponent containerLayer;
    
    private BufferedImage transitionImage;
    private BufferedImage beforeImage;
    private BufferedImage afterImage;
        
    /**
     * The user-defined code which ScreenTransition will call
     * to reset and setup the previous/next states of the application
     * during the transition setup process.
     */
    private TransitionTarget transitionTarget;

    private Component savedGlassPane;
    
    /**
     * Timing engine for the transition animation.
     */
    private Animator animator = null;

    /**
     * Constructor for CrossFadeTransition.  The application must supply the
     * JComponent that they wish to transition and the TransitionTarget
     * which supplies the callback methods called during the transition
     * process.
     * @param transitionComponent JComponent that the application wishes
     * to run the transition on.
     * @param transitionTarget Implementation of <code>TransitionTarget</code>
     * interface which will be called during transition process.
     */
    private CrossFadeTransition(JComponent transitionComponent,
                            TransitionTarget transitionTarget) {
        
        this.containerLayer = transitionComponent;
	this.transitionTarget = transitionTarget;
        this.animationLayer = new AnimationLayer();
        this.animationLayer.setVisible(false);
               
    }    

    /**`
     * Constructor that takes an Animator that will be used to drive the
     * ScreenTransition.  Transition will start if either {@link
     * #start} is called or {@link Animator#start} is called.
     * @throws IllegalStateException if animator is already running
     * @throws IllegalArgumentException animator must be non-null
     * @see Animator#isRunning()
     * @see Animator#start()
     */
    public CrossFadeTransition(JComponent transitionComponent,
            TransitionTarget transitionTarget, Animator animator) {
        this(transitionComponent, transitionTarget);
        setAnimator(animator);
    }    

    /**
     * Returns Animator object that drives this ScreenTransition.
     * @return the Animator that drives this ScreenTransition
     */
    public Animator getAnimator() {
        return animator;
    }
    
    /**
     * Sets animator that drives this ScreenTransition. Animator cannot
     * be null. Animator also cannot be running when this method is called
     * (because important setup information for ScreenTransition happens
     * at Animator start time).  Transition will start if either {@link
     * #start} is called or {@link Animator#start} is called.
     * @param animator non-null Animator object that will drive this
     * ScreenTransition.  Animator cannot be running when this is called.
     * @throws IllegalStateException if animator is already running
     * @throws IllegalArgumentException animator must be non-null
     * @see Animator#isRunning()
     */
    public void setAnimator(Animator animator) {
        if (animator == null) {
            throw new IllegalArgumentException("Animator must be non-null");
        }
        if (animator.isRunning()) {
            throw new IllegalStateException("Cannot perform this operation " +
                    "while animator is running");
        }
        this.animator = animator;
        animator.addTarget(transitionTimingTarget);
    }
    
    /**
     * Returns image used during timingEvent rendering.  This is called by
     * AnimationLayer to get the contents for the glass pane
     */
    Image getTransitionImage() {
        return transitionImage;
    }
    
    /**
     * Begin the transition from the current application state to the
     * next one.  This method will call into the TransitionTarget specified
     * in the ScreenTransition constructor: <code>setupNextScreen()</code> will
     * be called to allow the application to set up the state of the next
     * screen.  After this call, the transition animation will begin.
     */
    public void start() {
        if (animator.isRunning()) {
            animator.stop();
        }
	animator.start();
    }
    
    /**
     * This class receives the timing events from the animator and performs
     * the appropriate operations on the ScreenTransition object. This could
     * be done by having ScreenTransition implement TimingTarget methods
     * directly, but there is no need to expose those methods as public API
     * (which would be necessary since TimingTarget needs the methods to
     * be public). Having this as an internal private class hides the 
     * methods from the ScreenTransition API while having the same
     * functionality.
     */
    private TimingTarget transitionTimingTarget = new TimingTargetAdapter() {

        public void begin() {
            
            // Create the transition image
            int cw = containerLayer.getWidth();
            int ch = containerLayer.getHeight();
            if (transitionImage == null || 
                    transitionImage.getWidth() != cw ||
                    transitionImage.getHeight() != ch) {
                // Recreate transition image and background for new dimensions
                transitionImage = 
                        (BufferedImage)containerLayer.createImage(cw, ch);
                
                beforeImage = 
                        (BufferedImage)containerLayer.createImage(cw, ch);
                
                afterImage = 
                        (BufferedImage)containerLayer.createImage(cw, ch);
                
            }
            
            containerLayer.paint(beforeImage.getGraphics());
            

            // Make the animationLayer visible and the contentPane invisible.  This
            // frees us to validate the application state for the next screen while
            // keeping that new state invisible from the user; the animationLayer
            // will only display contents appropriate to the transition (the previous
            // state before the transition begins, the transitioning state during
            // the transition).
            savedGlassPane = containerLayer.getRootPane().getGlassPane();
            containerLayer.getRootPane().setGlassPane(animationLayer);
            containerLayer.getRootPane().getGlassPane().setVisible(true);

            // Now that the contentPane is invisible to the user, have the
            // application setup the next screen.  This will define the end state
            // of the application for this transition.  
            transitionTarget.setupNextScreen();
            containerLayer.revalidate();
            containerLayer.paint(afterImage.getGraphics());

            // Validating the container layer component ensures correct layout
            // for the next screen of the application
            containerLayer.validate();

            // Now that we have recorded (and rendered) the initial state, make
            // the container invisible for the duration of the transition
            containerLayer.setVisible(false);

            // workaround: need glass pane to reflect initial contents when we
            // exit this function to avoid flash of blank container
            timingEvent(0);
        }
            
        /**
         * Implementation of the <code>TimingTarget</code> interface.  This method
         * is called repeatedly during the transition animation.  We force a 
         * repaint, which causes the current transition state to be rendered.
         */
        public void timingEvent(float elapsedFraction) {
            paintTransition((Graphics2D)transitionImage.getGraphics(), elapsedFraction);

            // Force transitionImage to be copied to the glass pane
            animationLayer.repaint();
        }
        
        protected void paintTransition(Graphics2D g2, float elapsedFraction) {
            // Paint cross-fade of before and after images
            g2.setComposite(AlphaComposite.SrcOver.derive(1.0f - elapsedFraction));
            g2.drawImage(beforeImage, 0, 0, null);
            g2.setComposite(AlphaComposite.SrcOver.derive(elapsedFraction));
            g2.drawImage(afterImage, 0, 0, null);            
            g2.dispose();            
        }

        /**
         * Override of <code>TimingTarget.end()</code>; switch the visibility of
         * the containerLayer and animationLayer and force repaint.
         */
        public void end() {
            containerLayer.getRootPane().setGlassPane(savedGlassPane);
            containerLayer.getRootPane().getGlassPane().setVisible(false);
            animationLayer.setVisible(false);
            containerLayer.setVisible(true);
            containerLayer.repaint();
        }
    };
    
    class AnimationLayer extends JComponent {
                
        public AnimationLayer() {
            setOpaque(false);
        }
   
        @Override
        public void paintComponent(Graphics g) {
            g.drawImage(transitionImage, 0, 0, null);
        }
    }    
}

