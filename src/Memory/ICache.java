package Memory;

import java.util.HashMap;

import MainController.ClockController;

public class ICache {
	private int numBlocks;
	private int blockSize;
	HashMap<Integer,Integer> cache;
	ClockController c;
	MemoryBus bus;
	long blockOffsetMask;
	int offsetcount;
	int requests;
	int hits;
	public ICache(int numBlocks, int blockSize, ClockController c, MemoryBus bus){
		this.numBlocks = numBlocks;this.blockSize = blockSize;this.c = c;this.bus = bus;
		cache = new HashMap<Integer, Integer>();updateoffsetMask();hits = 0;requests = 0;
	}
	public int getStatsRequests(){
		return this.requests;
	}
	public int getStatsHitss(){
		return this.hits;
	}
	
	int numClockCyclesNeed(int clockcyclestake){
		if(bus.isBus(c.count()) == false){
			bus.updateBusyTimel(c.count()+clockcyclestake);
			return clockcyclestake;
		}else{
			int busyCount = (int)((bus.getBusyTime() - c.count())+clockcyclestake);
			bus.updateBusyTimel(bus.getBusyTime()+clockcyclestake);
			return busyCount;
		}
	}
	
	private void updateoffsetMask(){
		int tempBlockSize = this.blockSize;
		this.blockOffsetMask = 0;
		this.offsetcount = 0;
		
		while(tempBlockSize != 0){
			tempBlockSize = tempBlockSize/2;
			this.blockOffsetMask = this.blockOffsetMask << 1;
			this.blockOffsetMask = this.blockOffsetMask | 1;
			this.offsetcount++;
		}
		this.blockOffsetMask = this.blockOffsetMask >> 1;
		this.offsetcount = this.offsetcount -1;
	}
	
	public int fetchInstruction(long address){
		//int blockOffset = (int)(address & this.blockOffsetMask);
		int blockNumber = (int) ((address >> offsetcount)%this.numBlocks);
		int tag = (int) (address >> offsetcount);
		this.requests++;
		if(cache.containsKey(blockNumber)){
			if(cache.get(blockNumber) == tag){
				this.hits++;
				return 1;
			}else{
				cache.put(blockNumber,tag);
				return numClockCyclesNeed(this.blockSize*3 )+1;	
			}
		}else{
			cache.put(blockNumber,tag);
			return numClockCyclesNeed(this.blockSize*3 )+1;
		}
	}
}
