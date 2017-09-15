package lostagain.nl.spiffyresources.client.spiffycore;

/**
 * All classes with animation should implement this.
 * This method will then be fired each frame with the difference since the last frame put into the delta float
 * @author Tom
 *
 */
public interface HasDeltaUpdate{
	public void update(float delta);
	//public void cancel();
}