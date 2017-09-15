package lostagain.nl.spiffyresources.client.spiffygwt;

public interface HasFrameControl {

	/** Play the current animation forward.
	 * eg; 0123456 
	public void setPlayForward() {
	
		Log.info("Set setAnimateOpen on "+this.basefilename);
		animation.loop = false;
		animation.animation_direction = animationDirection.open;
	
	
		startFrameUpdates();
	
		animation.playuntill = 100 + animation.frametotal;
	}*/
	void setPlayForward();

	/** Play the current animation forward and loop
	 * eg; 0123456012345601234560123456..... 
	public void setPlayLoop() {
	
		Log.info("Set setAnimate loop "+this.basefilename);
		animation.animation_direction = animationDirection.open;
		animation.loop = true;
	
		animation.debug=true;
		//timer.run(); //change to update?
		checkAnimationFrame(); //emulates above run directly
		startFrameUpdates();
	
	}*/

	void setPlayLoop();

	/** Play the current animation backward from the frame its currently on
	 * eg; 6543210 
	public void setPlayBack() {
	
		Log.info("Set Animate Close "+this.basefilename);
	
		animation.animation_direction = animationDirection.close;
		animation.loop = false;
		
		
		startFrameUpdates();
	
		animation.playuntill = 100 + animation.frametotal;
	}
	*/
	void setPlayBack();

	/** Play the current animation forward and back
	 * eg; 0123456543210..... 
	public void setPlayForwardThenBack() {
	
		Log.info("Set setAnimateOpenThenClose");
		animation.loop = false;
		animation.animation_direction = animationDirection.open;
		close_after_open = true;
		startFrameUpdates();
		animation.playuntill = 100 + animation.frametotal;
	
	}*/

	void setPlayForwardThenBack();

	/** Play the current animation forward and back continuously
	 * eg; 0123456543210123456543210..... 
	public void setPlayForwardThenBackLoop() {
	
		Log.info("loop bouncing active");
	
		animation.loop = true;
		animation.animation_direction = animationDirection.open;
		close_after_open = true;
		open_after_close = true;
		//timer.cancel();		
		//timer.scheduleRepeating(timerDelay);
		//cancelFrameUpdates(); //we used to cancel first, why?
		startFrameUpdates();
		
		animation.playuntill = 100 + animation.frametotal;
	
	}*/
	void setPlayForwardThenBackLoop();

	/** Play the current animation forward until the specified frame
	 * eg; 01234 
	public void playUntill(int frame) {
		Log.info("playUntill active");
	
		animation.loop = false;
		animation.playuntill = frame;
		animation.animation_direction = animationDirection.open;
		startFrameUpdates();
	}*/

	void playUntill(int frame);

	void playBackUntill(int frame);
	/*
	public void playForwardXframes(int frames) {
		Log.info("playForwardXframes active");
		if (animation.currentlyAnimating == false) {
			animation.loop = true;
			animation.playuntill = animation.currentframe + frames;
			if (animation.playuntill > animation.frametotal) {
				animation.playuntill = animation.playuntill - (animation.frametotal + 1);
			}
			close_after_open = false;
			animation.animation_direction = animationDirection.open;
	
			startFrameUpdates();
		}
	
	}*/

	void playForwardXframes(int frames);
	/*
	public void nextFrameLoop() {
	
		animation.currentframe = animation.currentframe + 1;
		if (animation.currentframe > animation.frametotal) {
			animation.currentframe = 0;
		}
		this.setFrame(animation.currentframe);
	
	}*/

	void nextFrameLoop();
	/*
	public void prevFrameLoop() {
	
		animation.currentframe = animation.currentframe - 1;
		if (animation.currentframe < 0) {
			animation.currentframe = animation.frametotal;
		}
	
		this.setFrame(animation.currentframe);
	
	}*/

	void prevFrameLoop();

	/*
	public void nextFrame() {
	
		animation.currentframe = animation.currentframe + 1;
		
		if (animation.currentframe > animation.frametotal) {
			animation.currentframe = animation.frametotal;
			if (animation.runThisAfterOpen!=null){
				animation.runThisAfterOpen.run();
			}
		}
		
		this.setFrame(animation.currentframe);
	
	}*/
	void nextFrame();

	/*
	public void prevFrame() {
		animation.currentframe = animation.currentframe - 1;
		if (animation.currentframe < 0) {
			animation.currentframe = 0;
			if (animation.runThisAfterClose!=null){
				animation.runThisAfterClose.run();
			}
		}
		this.setFrame(animation.currentframe);
	}*/
	void prevFrame();

	/*
	public void gotoFrame(int newframe) {
	
		Log.info("setting frame to "+newframe+" total="+animation.frametotal);
	
		if (newframe<=animation.frametotal){
			animation.currentframe = newframe;
			this.setFrame(animation.currentframe);
		}
	
	}*/
	void gotoFrame(int newframe);


	void resumeAnimation();

	void pauseAnimation();

}