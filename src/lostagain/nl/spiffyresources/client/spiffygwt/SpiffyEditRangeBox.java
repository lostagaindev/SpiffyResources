package lostagain.nl.spiffyresources.client.spiffygwt;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**Its the one, the only, the amazing, Spiffy Edit Range Box!
 * Ok, not that amazing. But usefull.
 * It lets you drag up and down to change the number within a range **/
public class SpiffyEditRangeBox extends HorizontalPanel {

	Button LessThen = new Button("<");
	Button MoreThen = new Button(">");
	DoubleBox num = new DoubleBox();

	int mousestartx = 0;
	boolean dragging = false;

	double startval = 0;
	double endval = 0;

	double step = 1;
	double cstep = 200;

	double currentvalue;

	boolean countdown = false;
	boolean countup = false;

	Timer counter;

	Runnable runthisonupdate;

	FocusPanel dragoverlay = new FocusPanel();

	public int speedup = 0;

	public double getValue() {
		return num.getValue();
	}

	public SpiffyEditRangeBox(double startvalue, double endvalue,
			double defaultvalue, double setstep, Runnable runthisonupdate) {

		this.runthisonupdate = runthisonupdate;
		setup(startvalue, endvalue, defaultvalue, setstep);

	}

	public SpiffyEditRangeBox(double startvalue, double endvalue,
			double defaultvalue, double setstep) {
		setup(startvalue, endvalue, defaultvalue, setstep);
	}

	public void setValue(Double val) {

		if (val > endval) {
			val = startval + (val - endval);
		}
		if (val < startval) {
			val = endval - (startval - val);
		}

		num.setValue(val);
		currentvalue = val;
		if (currentvalue < 0) {
			num.getElement().getStyle().setColor("#990000");
		}
		if (currentvalue >= 0) {
			num.getElement().getStyle().setColor("#000000");
		}

	}

	public void setEnable(boolean enabled){
		
		if (!enabled){
			LessThen.setVisible(false);
			MoreThen.setVisible(false);
			num.setEnabled(false);
		} else {
			num.setEnabled(true);
		}
	}
	
	
	public void setup(double startvalue, double endvalue, double defaultvalue,
			double setstep) {
		LessThen.setVisible(false);
		MoreThen.setVisible(false);
		this.setVerticalAlignment(ALIGN_MIDDLE);
		this.setHorizontalAlignment(ALIGN_CENTER);
		this.startval = startvalue;
		this.endval = endvalue;
		this.step = setstep;

		this.setValue(defaultvalue);
		this.add(LessThen);
		this.add(num);
		this.add(MoreThen);
		num.setWidth("90%");

		counter = new Timer() {

			@Override
			public void run() {
				speedup++;
				if (speedup > 15) {
					speedup = 0;

					cstep = (cstep / 2) + 1;
					// counter.scheduleRepeating((int) cstep);

				}
				counter.scheduleRepeating((int) cstep);
				currentvalue = num.getValue();
				if (countdown) {

					currentvalue = currentvalue - step;
				}
				if (countup) {
					currentvalue = currentvalue + step;
				}
				setValue(currentvalue);

				num.setFocus(false);
			}

		};
		num.addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				mousestartx = event.getClientX();

				dragging = true;

				// drag overlay on
				RootPanel.get().add(dragoverlay, 0, 0);
				dragoverlay.setSize("100%", "100%");

				num.setFocus(false);

				dragoverlay.getElement().getStyle()
						.setPosition(Position.ABSOLUTE);
				dragoverlay.getElement().getStyle().setZIndex(999999999);
				// dragoverlay.getElement().getStyle().setBackgroundColor("#444");
				cstep = 100000;
				counter.scheduleRepeating(200);
			}

		});
		dragoverlay.addMouseMoveHandler(new MouseMoveHandler() {

			@Override
			public void onMouseMove(MouseMoveEvent event) {

				num.setFocus(false);
				num.setSelectionRange(0, 0);

				int def = -(mousestartx - event.getClientX());
				cstep = (int) (400 - Math.abs(def));

				if (cstep < 20) {
					cstep = 20;
				}

				// ArwaveMapClient.LOG(":"+cstep);
				if (dragging) {
					if (def > 0) {

						countup = true;
						countdown = false;

						speedup = 0;

						// counter.scheduleRepeating(200);
					}
					if (def < 0) {

						countup = false;
						countdown = true;
						speedup = 0;

						// counter.scheduleRepeating(200);
					}
					// counter.run();
				}
			}

		});
		dragoverlay.addMouseUpHandler(new MouseUpHandler() {

			@Override
			public void onMouseUp(MouseUpEvent event) {

				dragoverlay.removeFromParent();
				dragging = false;
				stopcount();
			}
		});

		//
		num.addMouseUpHandler(new MouseUpHandler() {

			@Override
			public void onMouseUp(MouseUpEvent event) {
				dragging = false;
				stopcount();
			}
		});
		num.addFocusHandler(new FocusHandler() {

			@Override
			public void onFocus(FocusEvent event) {

				LessThen.setVisible(true);
				MoreThen.setVisible(true);
			}

		});

		num.addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(BlurEvent event) {

				if (!countdown && !countup) {

					LessThen.getElement().getStyle().setDisplay(Display.NONE);

					MoreThen.getElement().getStyle().setDisplay(Display.NONE);
				}

			}

		});

		LessThen.addMouseUpHandler(new MouseUpHandler() {

			@Override
			public void onMouseUp(MouseUpEvent event) {

				stopcount();

			}

		});

		LessThen.addMouseOutHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent event) {
				stopcount();
			}

		});
		MoreThen.addMouseOutHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent event) {
				stopcount();
			}

		});

		MoreThen.addMouseUpHandler(new MouseUpHandler() {

			@Override
			public void onMouseUp(MouseUpEvent event) {
				stopcount();
			}

		});
		LessThen.addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {

				countdown = true;
				counter.run();
				counter.scheduleRepeating(200);

			}

		});
		MoreThen.addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				countup = true;
				counter.run();
				counter.scheduleRepeating(200);

			}

		});
	}

	public void stopcount() {
		cstep = 200;
		countdown = false;
		countup = false;
		counter.cancel();
		num.setFocus(true);

		// trigger runable if present
		if (runthisonupdate != null) {
			runthisonupdate.run();
		}
	}

	public void addBlurHandler(BlurHandler handler) {		
		num.addBlurHandler(handler);		
	}

	public void addFocusHandler(FocusHandler handler) {		
		num.addFocusHandler(handler);		
	}


}
