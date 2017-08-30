package Memory;

public class MemoryBus {
	private long busBusyTill;
	public MemoryBus(){
		busBusyTill = 0;
	}
	
	public boolean isBus(long clockCount){
		if(busBusyTill > clockCount){
			return true;
		}
		return false;
	}
	
	public void updateBusyTimel(long clockCount){
		busBusyTill = clockCount;
	}
	public long getBusyTime(){
		return busBusyTill;
	}
}
