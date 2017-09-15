package lostagain.nl.spiffyresources.interfaces;

/** Things with login methods should have this **/
public interface hasLoginSystem {

	public void userLogin();
	
	public void userLogout();

	public void userLogin(String[] IncomingUserData);
	
	public void updateAllUserData(); /** This is intended for updating all places where user data is displayed, without logging in/out (ie, if they change their profile details) **/
	
	
}
