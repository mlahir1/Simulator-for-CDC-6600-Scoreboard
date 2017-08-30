package MainController;

public class ClockController {
	private long clock;
	public ClockController(){clock = 1;}
	public void sync(){clock++;}
	public long count(){return clock;}
}

