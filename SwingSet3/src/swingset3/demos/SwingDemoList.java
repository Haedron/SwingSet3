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

package swingset3.demos;

import java.util.Arrays;
import java.util.List;

import swingset3.DemoList;

/**
 *
 * @author aim
 */
public class SwingDemoList implements DemoList {

    public List<String> getDemoClassNames() {
        return Arrays.asList(
                "com.sun.swingset3.demos.frame.FrameDemo",
                "com.sun.swingset3.demos.dialog.DialogDemo",
                "com.sun.swingset3.demos.window.WindowDemo",

                "com.sun.swingset3.demos.internalframe.InternalFrameDemo",
                "com.sun.swingset3.demos.tabbedpane.TabbedPaneDemo",
                "com.sun.swingset3.demos.scrollpane.ScrollPaneDemo",
                "com.sun.swingset3.demos.splitpane.SplitPaneDemo",
                "com.sun.swingset3.demos.gridbaglayout.GridBagLayoutDemo",

                "swingset3.demos.data.TableDemo",
                "swingset3.demos.data.TreeDemo",
                "com.sun.swingset3.demos.list.ListDemo",

                "com.sun.swingset3.demos.togglebutton.ToggleButtonDemo",
                "swingset3.demos.controls.JButtonDemo",
                "com.sun.swingset3.demos.combobox.ComboBoxDemo",
                "com.sun.swingset3.demos.progressbar.ProgressBarDemo",
                "com.sun.swingset3.demos.slider.SliderDemo",
                "com.sun.swingset3.demos.spinner.SpinnerDemo",

                "com.sun.swingset3.demos.textfield.TextFieldDemo",
                "com.sun.swingset3.demos.editorpane.EditorPaneDemo",

                "com.sun.swingset3.demos.colorchooser.ColorChooserDemo",
                "com.sun.swingset3.demos.filechooser.FileChooserDemo",
                "com.sun.swingset3.demos.optionpane.OptionPaneDemo",

                "com.sun.swingset3.demos.tooltip.ToolTipDemo");
    }
}
