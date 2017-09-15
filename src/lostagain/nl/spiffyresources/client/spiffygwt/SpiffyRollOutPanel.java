package lostagain.nl.spiffyresources.client.spiffygwt;


import java.util.logging.Logger;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.gen2.logging.shared.Log;
import com.google.gwt.user.client.ui.HasAnimation;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import lostagain.nl.spiffyresources.client.spiffycore.DeltaTimerController;
import lostagain.nl.spiffyresources.client.spiffycore.HasDeltaUpdate;

/** 
 * This panel acts a bit like a Disclosure Panel, allowing
 * a hidden widget to "roll out" when a button is pressed.</br>
 * The advantage of this panel, however, is it lets you adjust the 
 * speed of the animation.</br>
 * You can also extend this panel, unlike the Disclosure one
 * which for some reason prevents you doing that and forces you
 * to make your own widget for such a simple purpose.</br>
 * I mean, what's the point of stopping that? 
 * **/

public class SpiffyRollOutPanel extends SimplePanel implements HasAnimation, HasDeltaUpdate {

	static Logger Log = Logger.getLogger("SpiffyGWT.SpiffyRollOutPanel");
	
	Boolean animationEnabled = true;

	public Widget content;

	//updates the size
	RepeatingCommand updateWidgetSize;

	//Determines if its opening or closing
	Boolean isOpening = false;
	Boolean isClosing = false;

	int maxHeight= 0;
	int currentHeight = 0;
	private Runnable openOpened;
	private Runnable openClosed;

	int pixelGap = 50;
	double totalOpenTime = 500.0;

	//new animation system
	double HeightChangePerMS = 0.0;


	public SpiffyRollOutPanel(Widget contents){		
		super(contents);
		super.getElement().getStyle().setOverflowY(Overflow.HIDDEN);
		content = contents;
		
	

		setupAnimation();

	}

	@Override
	public void onLoad(){
		if (content!=null){
			maxHeight = content.getOffsetHeight();
		}
	}

	private void setupAnimation() {
		//ensure delta controller is setup as this handles the animation
		SpiffyGWTDeltaController.setup();
		Log.info("delta controller setup");
		
		
		updateWidgetSize = new RepeatingCommand(){

			@Override
			public boolean execute() {

				if (isOpening){
					currentHeight=currentHeight+pixelGap;
				} else if (isClosing){
					currentHeight=currentHeight-pixelGap;
				}

				if (currentHeight <= 0){
					currentHeight=0;
				}
				if (currentHeight  >= maxHeight){
					currentHeight=maxHeight;
				}

				setHeight(currentHeight+"px");		

				if ((currentHeight >= maxHeight) && isOpening){

					isOpening=false;
					isClosing=false;

					if (openOpened!=null){
						openOpened.run();
					}

					//set to auto height
					setHeight("auto");		


					return false;
				}
				if ((currentHeight <= 0) && isClosing){
					isOpening=false;
					isClosing=false;

					if (openClosed!=null){
						openClosed.run();
					}

					return false;
				}
				return true;
			}

		};
	}

	public SpiffyRollOutPanel() {
		super();
		super.getElement().getStyle().setOverflowY(Overflow.HIDDEN);
		setupAnimation();
	}

	public void setContent(Widget contents){
		content = contents;
		maxHeight = content.getOffsetHeight();
		super.setWidget(contents);
	}
	
	public Widget getContent() {
		return content;
	}
	
	@Override
	public boolean isAnimationEnabled() {
		return animationEnabled;
	}

	@Override
	public void setAnimationEnabled(boolean enable) {
		animationEnabled=enable;

	}

	public void setOpen(boolean b) {

		maxHeight = content.getOffsetHeight();

		if (!animationEnabled){		

			if (b){
				setHeight(currentHeight+"px");	

			} else {
				setHeight(0+"px");	
			}

			return;
		}




		if (!isClosing && !isOpening){

			HeightChangePerMS = maxHeight / totalOpenTime;

			//int timeGap = (pixelGap) / (maxHeight /totalOpenTime);
			//Scheduler.get().scheduleFixedDelay(updateWidgetSize, timeGap);
			
			//should never be zero (in future have a minimum speed)
			if (HeightChangePerMS==0){
				HeightChangePerMS=10;
			}
			
			Log.info("starting to animate :"+HeightChangePerMS+" per frame ("+maxHeight+"/"+totalOpenTime+")");
			Log.info("was told to open; ="+b);
			
			startAnimating();
			Log.info("frame updates running:"+DeltaTimerController.isFrameUpdatesRunning());
			
		} else {
			Log.info("is still opening or closing");
			Log.info("was told to open;"+b+" maxheight:"+maxHeight);
		}

		if (b){

			Log.info("---- now set to open");
			isOpening = true;
			isClosing = false;

		} else {

			Log.info("---- now set to close");
			isOpening = false;
			isClosing = true;
		}
	}


	public void addOnOpenedRunable(Runnable onOpened){
		openOpened=onOpened;


	}
	public void addOnClosedRunable(Runnable onClosed){
		openClosed=onClosed;


	}


	public boolean isClosed() {
		if (currentHeight<=0){
			return true;
		}
		return false;
	}

	public void recheckContentSize() {
		//Log.info("rechecking size of contents");

		//if its bigger then it was, we need to open it a bit.

		//if its smaller, we need to close it a bit.

		//temp
		maxHeight = content.getOffsetHeight();
		currentHeight=maxHeight;
		setHeight(currentHeight+"px");	

	}

	private void stopAnimating(){
	//	Log.info("stopAnimating");
		SpiffyGWTDeltaController.removeObjectToUpdateOnFrame(this);
	}

	private void startAnimating(){
		SpiffyGWTDeltaController.addObjectToUpdateOnFrame(this);
		//Log.info("startAnimating:"+DeltaTimerController.getObjectsCurrentlyUpdatedEachFrame().size()+" objects");
	}

	@Override
	public void update(float delta) {
		//Log.info("update-"+delta);
		
		//height per ms x delta
		int changeInHeight  = (int) Math.ceil((HeightChangePerMS * delta));
		//Log.info("change in height:"+changeInHeight);

		
		if (isOpening){
			currentHeight=currentHeight+changeInHeight;
		} else if (isClosing) {
			currentHeight=currentHeight-changeInHeight;
		}

		if (currentHeight <= 0){
			currentHeight = 0;
		}
		if (currentHeight >= maxHeight){
			currentHeight =  maxHeight;
		}

		setHeight(currentHeight+"px");		

		if ((currentHeight >= maxHeight) && isOpening){

			isOpening=false;
			isClosing=false;
			stopAnimating();
			Log.info("finnished opening");
			if (openOpened!=null){
				openOpened.run();
			}
		
			//set to auto height
			setHeight("auto");		

			
			//return false;
		}
		if ((currentHeight <= 0) && isClosing){
			isOpening=false;
			isClosing=false;
			stopAnimating();
			Log.info("finnished closing");
			if (openClosed!=null){
				openClosed.run();
			}
		
			
			//return false;
		}
		//return true;

	}

	



}
