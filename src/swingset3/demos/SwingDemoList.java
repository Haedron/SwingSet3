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
        "swingset3.demos.toplevels.JFrameDemo",
       "swingset3.demos.toplevels.JDialogDemo",
       "swingset3.demos.toplevels.JWindowDemo",
       "swingset3.demos.containers.InternalFrameDemo",
       "swingset3.demos.containers.TabbedPaneDemo",
       "swingset3.demos.containers.ScrollPaneDemo",
       "swingset3.demos.containers.SplitPaneDemo",
       "swingset3.demos.controls.ButtonDemo",
       "swingset3.demos.controls.JButtonDemo",
       "swingset3.demos.controls.ComboBoxDemo",
       "swingset3.demos.controls.ProgressBarDemo",
       "swingset3.demos.controls.SliderDemo",
       "swingset3.demos.data.ListDemo",
       "swingset3.demos.data.TableDemo",
       "swingset3.demos.data.TreeDemo",
       "swingset3.demos.text.EditorPaneDemo",
       "swingset3.demos.choosers.ColorChooserDemo",
       "swingset3.demos.choosers.FileChooserDemo",
       "swingset3.demos.choosers.OptionPaneDemo",
       "swingset3.demos.general.ToolTipDemo");
    }
    
}
