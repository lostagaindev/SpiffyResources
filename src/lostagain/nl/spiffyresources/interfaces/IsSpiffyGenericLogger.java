package lostagain.nl.spiffyresources.interfaces;

public interface IsSpiffyGenericLogger {

	void log(String logthis);

	void error(String logthis);

	void info(String logthis);

	void log(String logthis, String color);

	void logTimer(String label);

	void settimer();

	void log(double contents);
	

}