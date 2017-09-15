package lostagain.nl.spiffyresources.client.spiffygwt;

import java.util.List;
import java.util.logging.Logger;

import com.google.common.base.Splitter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import lostagain.nl.spiffyresources.client.spiffycore.SpiffyCommandManagement;
import lostagain.nl.spiffyresources.client.spiffycore.SpiffyGenericCommand;


/**
 * This is a popup panel that helps writing  scripts
 * currently it lists  aload of buttons that auto-inserts into the current textbox 
 * various script functions, putting the cursor and selection in a appropriate place.
 * 
 * 
 * 
 * @author Tom
 *
 **/
public class GenericScriptHelperPopup extends SpiffyPopupWithShadow {


	static Logger Log = Logger.getLogger("SpiffyGWT.GenericScriptHelperPopup");

	protected TextArea currentEditBox = null;
	KeyPressHandler editBoxTypingHandler;
	KeyDownHandler editBoxDeleteHandler;
	BlurHandler editBoxBlurHandler;
	
	//settings
	/**
	 * Allows a arbitary amount of spaces after the start mark
	 * -settext="gogogo"
	 * and
	 * - settext="gogogo"
	 * would be just as valid
	 */
	protected boolean optionalSpacesAfterStartMaker = false;
	



	//we also keep registration variables to let us remove the handlers later and re-use them on different objects
	HandlerRegistration editBoxTypingHandlerRegistration; //sort of a link between the handler and the edit box its attached too. We use this to remove it when needed as the box changes
	HandlerRegistration editBoxDeleteHandlerRegistration; //sort of a link between the handler and the edit box its attached too. We use this to remove it when needed as the box changes
	HandlerRegistration editBoxBlurHandlerRegistration;   //sort of a link between the handler and the edit box its attached too. We use this to remove it when needed as the box changes


	/** tracks if the editing is currently typing a command **/
	protected static boolean WrittingCommand = false;
	protected static boolean writtingParameters = false;

	//the above will be replaced with this enum based state system
	enum WrittingState {
		/** a textarea has focus **/
		Focused,
		/** no text area focused **/
		Unfocused,
		/** a command of some sort has started being written **/
		UnknownCommandStarted,
		/** we know what command is being written and they are typing parameters now **/
		WrittingParameters,

	}
	WrittingState currentStatus = WrittingState.Unfocused;
	Label stateFeedback = new Label("not set yet");
	Label PerameterFeedbackBox = new Label("param not set yet");


	/** used only by the helper panel itself to track if a command was just being written.
	 * The idea is even if the panel was blurred (and thus writing stopped) we know a command was half written**/
	protected static boolean WasJustWrittingCommand = false;

	/** current command being written (only after a command has been confirmed) **/
	protected static SpiffyGenericCommand currentCommand=null;

	/** current command being typed's end mark (ie what to look for to know the commands ended) **/
	protected static Character currentCommandEndMark;	

	/** lets typed so far in current command **/
	protected static String currentLettersInCommand = "";

	/** current suggested command **/
	protected SpiffyGenericCommand currentSuggestion=null;

	/** which suggestion do we want to select (the index down from the top, only counting visible indexs)**/
	static int currentSelectionIndex=0;

	/** container everything in this popup goes into **/
	protected VerticalPanel container = new VerticalPanel();

	/** clickdetector that  goes into so we can detect clicks **/
	protected FocusPanel overallContainer = new FocusPanel();


	/** sub-container to scroll the helpers **/
	protected ScrollPanel helperScrollPanel = new ScrollPanel();

	/** container for the helper buttons go into (NOT semantic assistance, just variables and IFs) **/
	protected VerticalPanel genericHelpers = new VerticalPanel();



	private static StandardInterfaceImages images = (StandardInterfaceImages) GWT.create(StandardInterfaceImages.class);


	/**
	 * simple reference reminders for editors
	 */
	static HelpIcon introLabelHelp = new HelpIcon(" Commands can be written in different cases. \n eg \n <hero> = keep case of result the default \n <Hero> = make first letter capital \n <HERO> = make all letters capital \n "+
			" <Hero_u> = make first letter capital and spaces become underscores \n <hero_nospace> = remove all spaces "+
			"\n To pick a random number <random(X-Y)> where X-Y is the range." +
			"\n To use semantics format as <:semantic query:> \n To use branching code [if ##>## :: ____ ::else:: ____] \n [Pickrandom:: OptionA|OptionB]","command help",images.HelpIconClose(),images.HelpIconCloseOver(),"Help_Group");	   

	SpiffyLabel introLabel = new SpiffyLabel("Commands:",introLabelHelp);


	HorizontalPanel filterPanel = new HorizontalPanel();
	Label filterLabel = new Label("Filter:");
	public TextBox filterBox = new TextBox();




	//The helper panel has its own copies of the slider controllers
	//so people editing don't have to keep changing pages back/forward
	//SliderBar SliderViolence = new SliderBar(0,100);
	//	SliderBar SliderEgo = new SliderBar(0,100);
	//SliderBar SliderSex = new SliderBar(0,100);
	//SliderBar SliderCliche = new SliderBar(0,100);

	Button closeButton = new Button("close");

	private SpiffyCommandManagement commandManager;	



	/**
	 * This popup helps edit fanfic maker snippet scripts
	 */
	public GenericScriptHelperPopup(SpiffyGenericCommand[] AllKnownCommands) {

		super("250px", "701px","Script Helper Popup",null,true);
		//set the position in css to fixed, so if the editor scrolls the popup will scroll with the screen
		super.setPositionFixed(true);
		super.fixedZdepth(450);

		commandManager = new SpiffyCommandManagement(AllKnownCommands);

		populateHelpers();
		container.setHeight("100%");
		container.setWidth("100%");

		container.add(introLabel);
		filterPanel.add(filterLabel);
		filterPanel.add(filterBox);
		container.add(filterPanel);
		helperScrollPanel.add(genericHelpers);


		helperScrollPanel.setHeight("580px");		
		container.add(helperScrollPanel);



		container.add(closeButton);
		container.setCellHeight(closeButton, "25px");
		container.setCellVerticalAlignment(closeButton, HasVerticalAlignment.ALIGN_BOTTOM);
		container.setCellHorizontalAlignment(closeButton, HasHorizontalAlignment.ALIGN_CENTER);


		filterBox.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {

				String currentFilter = filterBox.getText();
				filter(currentFilter);
			}
		});

		closeButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				GenericScriptHelperPopup.this.hide();


			}
		});


		//this panel itself has a focus handler to ensure its unfilted if freshly clicked and not typing a command
		overallContainer.addFocusHandler(new FocusHandler() {

			@Override
			public void onFocus(FocusEvent event) {
				if (!WasJustWrittingCommand && !WrittingCommand){
					filter("");
					filterBox.setText("");
				}
				WasJustWrittingCommand=false;
				WrittingCommand=false;
				setState(WrittingState.Focused);

			}
		});




		container.add(stateFeedback);
		container.add(PerameterFeedbackBox);
		overallContainer.add(container);


		super.addWidgetCenter(overallContainer);

	}







	public void resetHelp() {

		filterBox.setText("");
		stopWrittingCommandAndReset(); 
		
	}
	
	



	protected void filter(String currentFilter) {

		currentSuggestion=null; //reset the suggested command;
		int numberOfVisibleWidgetsFromTop = 0; //we use this to let us select a widget a certain amount down from the top

		if (optionalSpacesAfterStartMaker){
			currentFilter=currentFilter.trim();
		}

		//update standard helpers
		for (Widget widgetInContainer : genericHelpers) {

			ScriptInsertButton currentHelperBut = (ScriptInsertButton)widgetInContainer; //cast to buttons as we should only be adding buttons to the helper vertical panel right now
			//

			//set its visibility based on if its name starts with the same letters as the filter
			//OR if the filter is empty
			String currentButtonText = currentHelperBut.getText().toLowerCase();
			if (currentButtonText.startsWith(currentFilter.toLowerCase()) || currentFilter.length()==0){
				currentHelperBut.setVisible(true);

				//unhighlight by default
				//crude (very wastefull to do this to all by default, in future track the one highlighted and only unhighlight last when
				//new one is selected
				currentHelperBut.getElement().getStyle().clearBorderColor();
				currentHelperBut.getElement().getStyle().clearBorderStyle();


				if ( numberOfVisibleWidgetsFromTop  == currentSelectionIndex){ //the first visible command becomes the suggested one;
					currentSuggestion=currentHelperBut.commandThisButtonInserts;
					//if we are currently writting we also highlight  the button
					if (WrittingCommand){
						currentHelperBut.getElement().getStyle().setBorderColor("#FF0000");
						currentHelperBut.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
					}


				}
				numberOfVisibleWidgetsFromTop++;


			} else {
				currentHelperBut.setVisible(false);
			}


		}

	}


	private void populateHelpers() {



		//insert some real ones from the fanficmaker core

		//First is the commands that just get swapped with a static variable value
		SpiffyGenericCommand commands[] = commandManager.getAllCommands();

		for (SpiffyGenericCommand scriptCommand : commands) {			



			addScriptInsertButton(scriptCommand.getDiscription(), scriptCommand);
		}




	}


	/** a button associated with a command. Lets use easy get the commands data from it if needed**/
	class ScriptInsertButton extends Button {
		SpiffyGenericCommand commandThisButtonInserts;

		public ScriptInsertButton(String html,SpiffyGenericCommand scriptCommand) {
			super(html);
			commandThisButtonInserts = scriptCommand;


		}

	}

	/**
	 * Adds a helper button that just inserts a fixed bit of text
	 * 
	 * @param name - name of this button
	 * @param insertsThis - text it inserts
	 */
	private void addScriptInsertButton(String name,
			final SpiffyGenericCommand scriptCommand) {



		//get the data needed from the associatedCommand
		final int  SelectLength;

		//the selection length will be either the whole length after the cursor, or two the start of the first Parameter	
		if (scriptCommand.getParamterSeperator()!=null)
		{
			String seperator = scriptCommand.getParamterSeperator();
			Log.info("getting param seperator index of:"+seperator);

			SelectLength = scriptCommand.getAfterCursor().indexOf(seperator);
			Log.info("SelectLength:"+SelectLength);

			writtingParameters = true;
		} else {
			SelectLength = scriptCommand.getAfterCursor().length();
			Log.info("selecting full length:"+SelectLength);
		}
		//-----------------------------

		String beforeCursorWM = scriptCommand.getScriptMarkStart() +  scriptCommand.getBeforeCursor();
		String afterCursorWM = scriptCommand.getAfterCursor();

		if (afterCursorWM!=""){
			//if a after Cursor text exists then we add the script mark end to the end of it
			afterCursorWM=afterCursorWM+scriptCommand.getScriptMarkEnd();
		} else {
			//else it goes after the before cursor
			beforeCursorWM=beforeCursorWM+scriptCommand.getScriptMarkEnd();
		}
		//we make a new final afterCursor and beforeCursor variables now we are sure they have the marks they need
		final String afterCursor  = afterCursorWM;
		final String beforeCursor = beforeCursorWM;


		//make the new button to insert
		final ScriptInsertButton helperButton = new ScriptInsertButton(name,scriptCommand);
		helperButton.getElement().getStyle().setTextAlign(TextAlign.LEFT);
		helperButton.setTitle(beforeCursor+""+afterCursor);

		helperButton.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				//if currently typing a command finish it off with this;
				if (WrittingCommand || WasJustWrittingCommand){

					Log.info("button selected while writting command:"+currentLettersInCommand);

					autoInsertCurrentCommand(helperButton.commandThisButtonInserts);

				} else 
				{
					//insert this command in full
					insertGeneric(beforeCursor,afterCursor, SelectLength);
					//set state depending on if theres stuff after the cursor
					if (afterCursor.isEmpty()){
						setState(WrittingState.Focused);
					} else {
						setState(WrittingState.WrittingParameters);
					}
				}
				//unfilter

				stopWrittingCommandAndReset();
			}
		});

		helperButton.setWidth("160px");

		genericHelpers.add(helperButton);


	}



	public void setCurrentEditBox(TextArea editThis){

		currentEditBox=editThis;
		setState(WrittingState.Focused);
		setupEditBoxHandlers();

	}


	public void manuallySendKeyPress(KeyPressEvent event){
		if (editBoxTypingHandler!=null){
		editBoxTypingHandler.onKeyPress(event);
		}
	};
	
	
	/**
	 * adds a handler to detect typing, this lets us support auto-complete in future.
	 */
	private void setupEditBoxHandlers() {
		
		if (editBoxTypingHandler==null){

			editBoxTypingHandler = new KeyPressHandler() {

				@Override
				public void onKeyPress(KeyPressEvent event) {

					Character keyCode = event.getCharCode(); //the code of the key released
					//	int codetest =  event.getNativeEvent().getKeyCode();

					//Log.info("native codetest:"+codetest);

					//if we are not currently writing a command we check for a command open mark
					if (!WrittingCommand){
						if (commandManager.testForOpenCommandCharacter(keyCode)){

							WrittingCommand=true;	
							setState(WrittingState.UnknownCommandStarted);

							WasJustWrittingCommand=false;

							currentLettersInCommand="";

							//get the endmark to look out for now (when we detect this
							currentCommandEndMark = commandManager.getEndMarkForStartMark(keyCode);

							Log.info("command start detected "+WrittingCommand+" ends set to:"+currentCommandEndMark);
						}
						return;
					}

					//stop writing command once the endmark is pressed					
					if (currentCommandEndMark!=null && keyCode==currentCommandEndMark){

						Log.info("command end detected. was:"+currentLettersInCommand);

						stopWrittingCommandAndReset();

						return;

					}

					//if we delete or backspace we remove letters and refilter
					//(also need to track how many total letters, then cancel start mark if its deleted)
					//if (){
					//
					//}



					currentLettersInCommand = currentLettersInCommand +  String.valueOf(keyCode);
					filter(currentLettersInCommand);
					filterBox.setText(currentLettersInCommand);

				}
			};



		};


		if (editBoxDeleteHandler==null){
			//delete and enter needs to be handled separately
			//This is because "KeyPress" events only fire on visible characters. Think of them as telling hte browser what letters been typed
			//KeyDown, on the other hand, tells the browser what physical key was pressed. (However, unlike KeyPress, it doesn't understand what letter that corisponds too. ie, 5 and % would be the same key)
			editBoxDeleteHandler = new KeyDownHandler() { //also this fires when the key first goes down, not when its released
				@Override
				public void onKeyDown(KeyDownEvent event) {

					//if command is known and we are writing the parameters
					if (currentStatus==WrittingState.WrittingParameters){
						int keyCode= event.getNativeKeyCode();

						//If a key is pressed to goto the next param or previous param
						if(keyCode == KeyCodes.KEY_TAB || keyCode == KeyCodes.KEY_RIGHT || keyCode == KeyCodes.KEY_LEFT ){

							Log.info("going to next param triggered");
							Log.info("current command after cursor:"+currentCommand.getAfterCursor());


							int currentCursorPos = currentEditBox.getCursorPos();
							//first we need to work out what command was being typed
							//This is the number of command seperators we have in the currentLettersInCommand (ie, whats written so far)
							//If no separators have been written yet, we know we are still on the first parameter
							//String sectionsofar[] = currentLettersInCommand.split(currentCommand.getParamterSeperator());

							//letters so far should be whats behind the cursor to the last opened scriptmark 
							//(we use this rather then "current letters in command" as its more reliable, given the editor could have moved the cursor)
							String currentText = currentEditBox.getText();
							int CurrentCommandStartedAt = currentText.lastIndexOf(currentCommand.getScriptMarkStart(),currentCursorPos);
							String whatsBehindTheCursor = currentText.substring(CurrentCommandStartedAt, currentCursorPos);


							Log.info("written so far:"              +whatsBehindTheCursor);

							List<String> sectionsofar    = Splitter.on(currentCommand.getParamterSeperator()).splitToList(whatsBehindTheCursor);

							//String sectionsintotal[] =  currentCommand.getAfterCursor().split(currentCommand.getParamterSeperator());

							List<String> sectionsintotal = Splitter.on(currentCommand.getParamterSeperator()).splitToList(currentCommand.getAfterCursor());

							//Note; we use guavas splitter as wedont want to use regexs



							int ParameterNum = sectionsofar.size();
							int OutOf = sectionsintotal.size();


							PerameterFeedbackBox.setText("Typed;"+currentLettersInCommand+" Param:"+ParameterNum+" of "+OutOf);

							//if no more params we finish by simply moving the cursor to the end and unselecting anything	
							if (keyCode == KeyCodes.KEY_RIGHT || keyCode == KeyCodes.KEY_TAB){

								//if (ParameterNum==OutOf){
								//	Log.info("exiting param typing");

								//	int newCursorPos =  currentText.indexOf(currentCommand.getScriptMarkEnd(), currentCursorPos);

								//	currentEditBox.setCursorPos(newCursorPos);
								//	currentEditBox.setSelectionRange(0, 0);

								//	currentStatus=WrittingState.Focused;

								//	currentEditBox.cancelKey();
								//	return;

								//} else {
									//if there is still params we move the cursor to the next position and select till the one after

								
								
									Log.info("going to next param");
									//this will automatically move to the end if no more parameters left
									boolean success = gotoNextParameter(currentCursorPos, currentText);


									Log.info("canceling key hit");
									currentEditBox.cancelKey();
									return;

								//}

							}

							//goto the previous parameter
							if (keyCode == KeyCodes.KEY_LEFT){
								
								Log.info("going to previous param");
								
								//if we are already at the start of the command this selects the first parameter
								boolean success = gotoPrevParameter(currentCursorPos, currentText);
								
								if (success){
									currentEditBox.cancelKey();								
								}
								
								return;
							}


						}



						//if key is pressed we stop writing parameters and finish the command of with what we have
						if (keyCode == KeyCodes.KEY_ESCAPE || keyCode == KeyCodes.KEY_UP || keyCode == KeyCodes.KEY_DOWN || keyCode == KeyCodes.KEY_ENTER ){

							stopWrittingCommandParameters();

							//if it was enter we also cancel the keyhit
							if (keyCode == KeyCodes.KEY_ENTER){
								currentEditBox.cancelKey();
							}

						}

						return;
					}

					//if we are writing a unknown command
					if (currentStatus==WrittingState.UnknownCommandStarted){
						int keyCode= event.getNativeKeyCode();

						if (keyCode == 0) {

							Log.warning("Are you using Firefox? Unfortunately there seems some oddness in handling key events in it. Please use Chrome or Vivaldi ");
							// Probably Firefox
							keyCode = event.getNativeEvent().getKeyCode();

						}

						if(keyCode == KeyCodes.KEY_BACKSPACE){
							Log.info("backspace pressed");
							//first we check theres characters in the current command left to delete
							//if not, it probably means already deleted them all!
							//(ie, that last backspace deleted the startMark)
							if (currentLettersInCommand.isEmpty()){
								WrittingCommand=false; //temp
								setState(WrittingState.Focused);
							} else {
								//(subtract from current command
								currentLettersInCommand=currentLettersInCommand.substring(0, currentLettersInCommand.length()-1);
							}
							//update filter
							filter(currentLettersInCommand);
							filterBox.setText(currentLettersInCommand);


						}
						
						if(keyCode == KeyCodes.KEY_TAB ||keyCode == KeyCodes.KEY_RIGHT  ){
							
							Log.info("tab pressed while command was unknown");
							SpiffyGenericCommand scriptCommand = currentSuggestion;	
							currentCommand = scriptCommand;
							autoInsertCurrentCommand(scriptCommand);
							currentEditBox.cancelKey(); //we dont want to really tab!
						}
						
						if(keyCode == KeyCodes.KEY_ENTER){
							Log.info("enter pressed");
							WrittingCommand=false; 
							//TODO: If we are currently writing parameters then hitting enter should skip to the next, 
							//or exit from writing them.
							//if (currentStatus==WrittingState.WrittingParameters){

							//	Log.info("enter hit while writing parameters");

							//}

							

							//get command at top of filtered list to complete it with							
							SpiffyGenericCommand scriptCommand = currentSuggestion;		

							currentCommand = scriptCommand;

							//now we check if the enter should be a real newline or not.
							//normally it just confirms we end the command
							//but if the command end is intended to be a newline anyway, then we let the enter behave as normal
							if (currentSuggestion.getScriptMarkEnd().equals("\n")){								
								
							} else {							
								//cancel the enter event from effecting the box
								currentEditBox.cancelKey(); //WE don't really want a newline! This stops it from happening.
							}
							
							
							//if theres none ignore and do nothing
							if (scriptCommand==null){
								return;
							}
							autoInsertCurrentCommand(scriptCommand);
							return;

						}


						//if they press an arrow key we stop writing commands and leave it as is
						if(KeyCodes.isArrowKey(keyCode)){
							Log.info("arrow pressed");

							if (keyCode == KeyCodes.KEY_UP && currentSelectionIndex>0){
								currentEditBox.cancelKey();
								currentSelectionIndex=currentSelectionIndex-1;
								filter(currentLettersInCommand);
								return;
							}

							if (keyCode == KeyCodes.KEY_DOWN){
								currentEditBox.cancelKey();
								currentSelectionIndex=currentSelectionIndex+1;
								filter(currentLettersInCommand);

								return;

							}

							if (keyCode == KeyCodes.KEY_LEFT || keyCode == KeyCodes.KEY_RIGHT){

								Log.info("arrow hit when no more params");


								stopWrittingCommandAndReset();


								//WrittingCommand=false; 
								//filter("");
								//filterBox.setText("");
								//currentLettersInCommand="";
							}




						}


					}

				}
			};
		}

		if (editBoxBlurHandler==null){
			editBoxBlurHandler = new BlurHandler() {				
				@Override
				public void onBlur(BlurEvent event) {


					WrittingCommand=false; 
					WasJustWrittingCommand = true;
					writtingParameters = false;

					editBoxTypingHandlerRegistration.removeHandler(); 
					editBoxDeleteHandlerRegistration.removeHandler(); 
					editBoxBlurHandlerRegistration.removeHandler();

					setState(WrittingState.Unfocused);

					//unfilter (we dont unfilter automaticaly any more, we do this if the helper panel is clicked and wasjustwritting is false)
					//filter("");
					//filterBox.setText("");
				}
			};
		}

		//remove any handler associations with other boxes first
		if (editBoxTypingHandlerRegistration!=null) {
			editBoxTypingHandlerRegistration.removeHandler(); 

		}
		if (editBoxDeleteHandlerRegistration!=null) {
			editBoxDeleteHandlerRegistration.removeHandler(); 

		}
		if (editBoxBlurHandlerRegistration!=null){
			editBoxBlurHandlerRegistration.removeHandler();
		}

		//add the handlers to this new box
		editBoxTypingHandlerRegistration=currentEditBox.addKeyPressHandler(editBoxTypingHandler);
		editBoxDeleteHandlerRegistration=currentEditBox.addKeyDownHandler(editBoxDeleteHandler);
		editBoxBlurHandlerRegistration  =currentEditBox.addBlurHandler(editBoxBlurHandler);

	}

	protected boolean gotoPrevParameter(int currentCursorPos, String currentText) {
		
		//get the location of the script start
		int ScriptCommandStart = currentText.lastIndexOf(currentCommand.getScriptMarkStart(), currentCursorPos);
		Log.info("ScriptCommandStart:"+ScriptCommandStart);
		//get the first parameter start before the cursor.
		//This becomes the new next Param position
		int nextParamPosition = currentText.lastIndexOf(currentCommand.getParamterSeperator(), currentCursorPos);
		Log.info("nextParamPosition:"+nextParamPosition);
		//now get the param before THAT as the new starting position
		int CurrentParamStartPosition = currentText.lastIndexOf(currentCommand.getParamterSeperator(), nextParamPosition-1);
		Log.info("CurrentParamStartPosition:"+CurrentParamStartPosition);
		
		
		//if there wasn't one before that we assume we are at the start and selecting the first parameter
		if (CurrentParamStartPosition == -1 || CurrentParamStartPosition<ScriptCommandStart){
			Log.info("no previous parameter");
			
			return gotoFirstParameter(currentCursorPos, currentText);
		}

		
		//else we select from the prev parameter to the one after it
		int newCursorPosition = CurrentParamStartPosition +currentCommand.getParamterSeperator().length();
		
		//if no parameter separator is found between the cursor and the start of the script, we select the first parameter
			
		int selLength =  nextParamPosition-newCursorPosition;
		Log.info("selLength"+selLength);
				
		currentEditBox.setCursorPos(newCursorPosition);
		currentEditBox.setSelectionRange(newCursorPosition, selLength);
		
		
		return true;
	}










	/**
	 * removes letters between two points in the currentEditBox
	 * 
	 * @param startOfRemovalSection
	 * @param endOfRemovalSection
	 */
	protected void removeBetween(int startOfRemovalSection,
			int endOfRemovalSection) {
		String currentText = currentEditBox.getText();
		String preserveBefore = currentText.substring(0, startOfRemovalSection);
		String preserveAfter= currentText.substring(endOfRemovalSection, currentText.length());

		//we simple set the text to the stuff before the bit to remove with the bit after the text to remove after it.
		currentEditBox.setText(preserveBefore+preserveAfter);
	}


	public void insertGeneric(
			String BeforeCursor,
			String AfterCursor,
			int SelectLength){




		//		
		//TextArea currentlyEditing = currentEditBox;


		//commented out is a attempt at getting the box under the cursor automatically so it wouldn't need to be set
		//However, I couldn't work out how to go from the HTML element to a GWT widget
		//Or, alternatively, from the HTML element to a Element of a type that could use
		//the setCursorPos or setSelectionRange methods (or something like them)

		//for now currentEditBox has to be set by setCurrentEditBox

		//		if (currentlyEditing==null){
		//			//if the edit box hasnt explicitly been set we look at whats under the cursor right now
		//			//this function isn't in GWT yet so we need to use native javascript 	(JSNI)
		//			
		//			Element elementWithFocus = getElementWithFocus();
		//			//check the class is a TextArea or superclass before assigning it
		//			if (elementWithFocus.getClass().isAssignableFrom(TextAreaElement.class)){
		//				currentlyEditing = (TextAreaElement)elementWithFocus;
		//			} else {
		//				Log.info("invalid selected element to insert into:"+elementWithFocus.getClass().toString());
		//			}
		//			currentlyEditing.focus();
		//			currentlyEditing.
		//		}


		String currentContents = currentEditBox.getText();
		int currentCursor = currentEditBox.getCursorPos();

		String newContents = currentContents.substring(0, currentCursor)
				+ BeforeCursor 
				+ AfterCursor
				+ " " //not we add a extra space after "AfterCursor". This is because almost every time you will be inserting a word you will want a space after it
				+ currentContents.substring(currentCursor,currentContents.length());

		currentEditBox.setText(newContents);

		//after insert we reselect the box
		currentEditBox.setFocus(true);

		//set the cursor position
		currentEditBox.setCursorPos(currentCursor+BeforeCursor.length()+1);

		//select from cursor to end to make it easy to type new stuff in		
		if (SelectLength>0){
			currentEditBox.setSelectionRange(currentCursor+BeforeCursor.length(), SelectLength);
		}


	}


	/**
	 * @param commandThisButtonInserts  - the command we want to finish this typing with
	 * 
	 */
	private void autoInsertCurrentCommand(SpiffyGenericCommand scriptCommand) {


		//else backtrack current typed letters
		//(that is all the letters from the current cursor position to when the scriptcommand started being typed
		//should be removed)
		int endOfRemovalSection   = currentEditBox.getCursorPos();
		int startOfRemovalSection = currentEditBox.getCursorPos() - currentLettersInCommand.length()-scriptCommand.getScriptMarkStart().length(); //the starting point of the command is the current cursor position - how much has been typed - what script mark used to start it.   eg the length of "her" - the length of "<" would remove "<her" to let "<hero>" be inserted correctly 

		Log.info("Removal section:"+startOfRemovalSection+" to "+endOfRemovalSection);


		removeBetween(startOfRemovalSection,endOfRemovalSection);

		//set the cursor to the new start
		currentEditBox.setCursorPos(startOfRemovalSection);							

		String beforeCursor = scriptCommand.getScriptMarkStart() +  scriptCommand.getBeforeCursor();
		String afterCursor = scriptCommand.getAfterCursor() + scriptCommand.getScriptMarkEnd();

		int  SelectLength = 0;
		if (scriptCommand.getParamterSeperator()!=null)
		{
			String seperator = scriptCommand.getParamterSeperator();
			Log.info("getting param seperator index of:"+seperator);

			SelectLength = scriptCommand.getAfterCursor().indexOf(seperator);

			Log.info("SelectLength::"+SelectLength);		
			writtingParameters = true;
			setState(WrittingState.WrittingParameters);

		}  else if (scriptCommand.getAfterCursor().isEmpty()) {
			Log.info("no params...");
			setState(WrittingState.Focused);

		} else {
			SelectLength = scriptCommand.getAfterCursor().length();
			Log.info("selecting full length:"+SelectLength);
			setState(WrittingState.WrittingParameters);

		}

		//the current letters are now set to the before custor
		currentLettersInCommand = scriptCommand.getBeforeCursor();

		//	int  SelectLength = scriptCommand.getAfterCursor().length();

		//enter it in 
		insertGeneric(beforeCursor,afterCursor,SelectLength);

		//clear filter
		filter("");
		filterBox.setText("");
	}


	/**
	 * 
	 */
	public void stopWrittingCommandAndReset() {

		filter("");
		currentLettersInCommand="";						
		WrittingCommand=false; 
		writtingParameters=false;
		currentLettersInCommand="";
		currentCommandEndMark=null;		
		currentSelectionIndex =0;
		currentCommand = null;
		setState(WrittingState.Focused);

	}





	/**
	 * Native javascript method to get the current element with focus
	 * 
			document
	 * @return
	 */

	public static native Element getElementWithFocus() /*-{
	 	return $doc.activeElement;
	}-*/;


	private void setState(WrittingState currentState){
		currentStatus = currentState;
		stateFeedback.setText(currentStatus.name());

	}










	/**
	 * 
	 */
	private void stopWrittingCommandParameters() {
		Log.info("exiting param typing");

		int currentCursorPos = currentEditBox.getCursorPos();
		int newCursorPos =  currentEditBox.getText().indexOf(currentCommand.getScriptMarkEnd(), currentCursorPos)+currentCommand.getScriptMarkEnd().length();


		currentEditBox.setSelectionRange(0, 0);
		currentEditBox.setCursorPos(newCursorPos);

		currentStatus=WrittingState.Focused;

		currentCommand = null;
		setState(WrittingState.Focused);
	}








	private boolean gotoFirstParameter(int currentCursorPos, String currentText) {
		
		Log.info("going to first parameter");
		
		//find script mark start
		int ScriptCommandStart = currentText.lastIndexOf(currentCommand.getScriptMarkStart(), currentCursorPos);
		
		//if the cursor position is already at the start of the script we cancel
		if ((ScriptCommandStart + currentCommand.getScriptMarkStart().length())==currentCursorPos){
			return false;
		}
		
		//add the beforeCursor stuff
		int ParamStart       = ScriptCommandStart + currentCommand.getBeforeCursor().length()+1;
		
		//find first parameter mark after it, as thats the end of the selection point
		int FirstParamEnd    = currentText.indexOf(currentCommand.getParamterSeperator(), ParamStart);
		int ScriptCommandEnd = currentText.indexOf(currentCommand.getScriptMarkEnd()    , ParamStart);
		
		if (FirstParamEnd==-1 || FirstParamEnd>ScriptCommandEnd){
			//if no other params we select mark the end of the command as the parameters end point.
			FirstParamEnd = ScriptCommandEnd;
		}
		
		Log.info(   "ParamStart:"+ParamStart);
		Log.info("FirstParamEnd:"+FirstParamEnd);
		
		currentEditBox.setCursorPos(ParamStart);
		currentEditBox.setSelectionRange(ParamStart, FirstParamEnd-ParamStart);
		
		
		return true;
		
	}

	/**
	 * @param currentCursorPos
	 * @param currentText
	 */
	private boolean  gotoNextParameter(int currentCursorPos, String currentText) {
		//next parameter is the position of the next seperator + its length
		int nextParameterPosition =  currentText.indexOf(currentCommand.getParamterSeperator(), currentCursorPos);
		if (nextParameterPosition==-1){
			//cancel writing command and move to the end
			stopWrittingCommandParameters();
			return false;

		}

		int nextCommandStartsAt = nextParameterPosition + currentCommand.getParamterSeperator().length();

		Log.info("next param starts at:"+nextCommandStartsAt);

		int seperatorAfterThat = currentText.indexOf(currentCommand.getParamterSeperator(), nextCommandStartsAt);
		Log.info("seperatorAfterThat:"+seperatorAfterThat);

		int commandEndPoint = currentText.indexOf(currentCommand.getScriptMarkEnd(), nextCommandStartsAt);
		Log.info("commandEndPoint:"+commandEndPoint);

		//if theres no more seperators before the end of the command we skip to the end
		//remember we are testing the whole box, so theres probably lots of seperators left outside this command
		if (seperatorAfterThat==-1 || seperatorAfterThat>commandEndPoint ){
			seperatorAfterThat = commandEndPoint;
		}


		currentEditBox.setCursorPos(nextCommandStartsAt);
		Log.info("new selection length = "+(seperatorAfterThat-nextCommandStartsAt));
		currentEditBox.setSelectionRange(nextCommandStartsAt, seperatorAfterThat-nextCommandStartsAt);

		return true;
	}


}
