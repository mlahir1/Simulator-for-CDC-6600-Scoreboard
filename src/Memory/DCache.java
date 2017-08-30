package Memory;

import MainController.ClockController;

public class DCache {
	int validbits[], lrucounter[], tag[], dirtybit[];
	int cacheSize, blockSize, nWays, hits, requests;
	
	String cache[][];
	Memory mem;
	String Datapath;
	ClockController c;
	MemoryBus bus;
	
	public class DataCacheInfo{
		public String data;
		public int clockCycles;
		DataCacheInfo(String data, int clockCycles){
			this.data = data;
			this.clockCycles = clockCycles;
		}
	}
	
	public DCache(ClockController c, MemoryBus bus, String Datapath, Memory memory){
		cacheSize = 4;blockSize = 4;nWays = 2;requests = 0;hits = 0;this.Datapath = Datapath;validbits = new int[cacheSize];
		lrucounter = new int[cacheSize];dirtybit = new int[cacheSize];tag = new int[cacheSize];this.c = c;this.bus = bus;
		cache = new String[cacheSize][];this.mem = memory;
		for(int i =0 ; i < cacheSize; i++){
			cache[i] = new String[blockSize];
		}
	}
	
	public int getStatsRequests(){
		return this.requests;
	}
	
	public int getStatsHitss(){
		return this.hits;
	}
	
	public DataCacheInfo fetchData(long address){
		long oldaddress = address;
		address = address >> 2;
		long blockOffsetMask = 3;
		int blockOffset = (int)(address & blockOffsetMask);
		long addr = address >> 2;
		int setNum = (int)(addr %2);
		this.requests++;
		int index = setNum * this.nWays;
		int templrucounter[] = new int[4];
		templrucounter[index] = this.lrucounter[index];
		templrucounter[index+1] = this.lrucounter[index+1];
		this.lrucounter[index] = 0;
		this.lrucounter[index+1] = 0;
		if(this.validbits[index] == 1 && this.tag[index] == addr){
			this.lrucounter[index] = 1;
			this.hits++;
			return new DataCacheInfo(cache[index][blockOffset],1);
		}
		if(this.validbits[index+1] == 1 && this.tag[index+1] == addr){
			this.lrucounter[index+1] = 1;
			this.hits++;
			return new DataCacheInfo(cache[index+1][blockOffset],1);
		}
		String data[] = mem.fetchData(oldaddress);
		if(this.validbits[index] == 0){
			this.validbits[index] = 1;
			this.lrucounter[index] = 1;
			this.dirtybit[index] = 0;
			this.tag[index] = (int)addr;
			for(int i = 0 ; i < data.length; i++){
				cache[index][i] = data[i];
			}
			return new DataCacheInfo(cache[index][blockOffset],numClockCyclesNeed(12)+1);
		}
		if(this.validbits[index+1] == 0){
			this.validbits[index+1] = 1;
			this.dirtybit[index+1] = 0;
			this.lrucounter[index+1] = 1;
			this.tag[index+1] = (int)addr;
			for(int i = 0 ; i < data.length; i++){
				cache[index+1][i] = data[i];
			}
			return new DataCacheInfo(cache[index+1][blockOffset],numClockCyclesNeed(12)+1);
		}
		if(templrucounter[index] == 0){
			this.validbits[index] = 1;
			this.lrucounter[index] = 1;
			int extraCycles = 0;
			if(this.dirtybit[index] == 1){
				extraCycles = 12;
				mem.updateData(oldaddress, cache[index]);
			}
			this.dirtybit[index] = 0;
			this.tag[index] = (int)addr;
			for(int i = 0 ; i < data.length; i++){
				cache[index][i] = data[i];
			}
			return new DataCacheInfo(cache[index][blockOffset],numClockCyclesNeed(12+extraCycles)+1);
		}
		if(templrucounter[index+1] == 0){
			this.validbits[index+1] = 1;
			this.lrucounter[index+1] = 1;
			this.tag[index+1] = (int)addr;
			int extraCycles = 0;
			if(this.dirtybit[index+1] == 1){
				extraCycles = 12;
				mem.updateData(oldaddress, cache[index]);
			}
			this.dirtybit[index+1] = 0;
			for(int i = 0 ; i < data.length; i++){
				cache[index+1][i] = data[i];
			}
			return new DataCacheInfo(cache[index+1][blockOffset],numClockCyclesNeed(12+extraCycles)+1);
		}
		return null;
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
	public int updateValue(long address, String data){
		long oldaddress = address;
		address = address >> 2;
		long blockOffsetMask = 3;
		int blockOffset = (int)(address & blockOffsetMask);
		long addr = address >> 2;
		int setNum = (int)(addr %2);
		//this.requests++;
		int index = setNum * this.nWays;
		int templrucounter[] = new int[4];
		mem.updateSWData(oldaddress, data);
		templrucounter[index] = this.lrucounter[index];
		templrucounter[index+1] = this.lrucounter[index+1];
		this.lrucounter[index] = 0;
		this.lrucounter[index+1] = 0;
		if(this.validbits[index] == 1 && this.tag[index] == addr){
			this.lrucounter[index] = 1;
			this.dirtybit[index] = 1;
			//this.hits++;
			cache[index][blockOffset] = data;
			return 1;
		}
		if(this.validbits[index+1] == 1 && this.tag[index+1] == addr){
			this.lrucounter[index+1] = 1;
			this.dirtybit[index+1] = 1;
			cache[index+1][blockOffset] = data;
			//this.hits++;
			return 1;
		}
		String newdata[] = mem.updateGetData(oldaddress, blockOffset,data);
		if(this.validbits[index] == 0){
			this.validbits[index] = 1;
			this.lrucounter[index] = 1;
			this.dirtybit[index] = 0;
			this.tag[index] = (int)addr;
			for(int i = 0 ; i < newdata.length; i++){
				cache[index][i] = newdata[i];
			}
			return numClockCyclesNeed(12)+1;			
		}
		if(this.validbits[index+1] == 0){
			this.validbits[index+1] = 1;
			this.lrucounter[index+1] = 1;
			this.dirtybit[index+1] = 0;
			this.tag[index+1] = (int)addr;
			for(int i = 0 ; i < newdata.length; i++){
				cache[index+1][i] = newdata[i];
			}
			return numClockCyclesNeed(12)+1;	
		}
		if(templrucounter[index] == 0){
			this.validbits[index] = 1;
			this.lrucounter[index] = 1;
			this.tag[index] = (int)addr;
			int extraCycles = 0;
			if(this.dirtybit[index] == 1){
				extraCycles = 12;
				mem.updateData(oldaddress, cache[index]);
			}
			this.dirtybit[index] = 0;
			for(int i = 0 ; i < newdata.length; i++){
				cache[index][i] = newdata[i];
			}
			return numClockCyclesNeed(12+extraCycles)+1;
		}
		if(templrucounter[index+1] == 0){
			this.validbits[index+1] = 1;
			this.lrucounter[index+1] = 1;
			this.tag[index+1] = (int)addr;
			int extraCycles = 0;
			if(this.dirtybit[index+1] == 1){
				mem.updateData(oldaddress, cache[index+1]);
				extraCycles =12;
			}
			this.dirtybit[index+1] = 0;
			for(int i = 0 ; i < newdata.length; i++){
				cache[index+1][i] = newdata[i];
			}
			return numClockCyclesNeed(12+extraCycles)+1;
		}
		return -1;
	}	
	
	
}
