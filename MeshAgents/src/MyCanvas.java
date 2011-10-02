import java.awt.Frame;

import processing.core.*;
// this example demonstrates how to use a ControlWindowCanvas
// which can from different windows.
// click the mouse in both windows to see the effect.

public class MyCanvas extends Frame {
	SecondApplet s;
	
    public MyCanvas() {
        setBounds(100,100,800,600);
        s = new SecondApplet();
        add(s);
        s.init();
        extracted();
    }

	private void extracted() {
		show();
	}
    
    public void update(){
    	s.redraw();
    }
}
