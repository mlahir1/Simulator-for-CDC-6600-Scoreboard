package MainController;

import java.util.ArrayList;

import Evaluation.Conversion;
import Evaluation.Execution;
import Evaluation.Resources;
import Memory.DCache;
import Memory.MemoryBus;

public class ScoreBoardPipeController {
 boolean raw;
 boolean war;
 boolean waw;
 boolean struct;
 boolean isNextStateFree;
 int current_state; //0-fetch 1-Isse 2-read 3-exec 4-mem
 int next_state;
 boolean isEligibletopush;
 boolean csFinish;
 boolean isFetchFree = false;
 Resources res;
 int ctr=0;
 int FetchCycles;
 int ExecCycles;
 
 ProgramCounter pInst;
 int clk;
 int fe,is,rd,ex,wb;
 char sh, h_waw, h_war, h_raw;
 int issueclk = 0;
 DCache DCache;
 boolean isDCacheHit = false;
 String arg1;
 String arg2;
 String arg3;
 String inst;
 String branch;
 ArrayList<ProgramCounter> list;
 boolean isBrachTaken;
 public ScoreBoardPipeController(Resources res, int FetchCycles, ProgramCounter pInst, int clk, ClockController c, MemoryBus MB, ArrayList<ProgramCounter> list, DCache DCache) {
	 this.raw = false;this.war=false; this.waw = false; this.current_state = 0;
	 this.next_state = 1; this.isEligibletopush = false;this.csFinish=false; 
	 this.res = res; this.FetchCycles = FetchCycles; this.pInst = pInst;this.clk = clk;
	 this.fe = 0; this.is = 0; this.rd = 0; this.ex = 0; this.wb = 0; this.sh = 'N';
	 this.h_war = 'N'; this.h_waw = 'N'; this.h_raw = 'N'; this.DCache = DCache;
	 this.arg1= null; this.arg2=null;this.arg3=null;this.inst=null; this.branch=null;this.list = list; this.isBrachTaken = false;
 }
 void isEtPush(int current_state){
	 this.isEligibletopush = false;
	 switch(current_state){
	 case 0:{
		 if(this.isNextStateFree){
			 this.isEligibletopush = true;
		 }
	 break;}
	 case 1:{
		 if(this.struct == false && this.waw == false && this.isNextStateFree){
			 this.isEligibletopush = true;
		 }
	 break;}
	 case 2:{
		 if(this.raw == false && this.isNextStateFree){
			 this.isEligibletopush = true;
		 }
	 break;}
	 case 3:{
		 if(this.isNextStateFree){
			 this.isEligibletopush = true;
		 }
	 break;}
	 case 4:{
		 if(this.isNextStateFree && !this.war){
			 this.isEligibletopush = true;
		 }
	 break;}
	 case 5:{
		 if(this.isNextStateFree){
			 this.isEligibletopush = true;
		 }
	 break;}
	 }
 }
 boolean isResourcesFree(ProgramCounter pInst){//complete the code
	 switch(pInst.inst){
	 	case "mul.d":{
	 		this.ExecCycles = res.multCycles;
	 		for(int i=0;i<res.multSize;i++){
	 			if(res.mult[i] == false){
	 				return true;
	 			}
	 		}
	 	break;}
	 	case "add.d":
	 	case "sub.d":{
	 		this.ExecCycles = res.addCycles;
	 		for(int i=0;i<res.addSize;i++){
	 			if(res.add[i] == false){
	 				return true;
	 			}
	 		}
	 	break;}
	 	case "div.d":{
			this.ExecCycles = res.divCycles;
	 		for(int i=0;i<res.divSize;i++){
	 			if(res.div[i] == false){
	 				return true;
	 			}
	 		}
	 	break;}
	 	case "and":
	 	case "andi":
	 	case "dadd":
	 	case "daddi":
	 	case "dsub":
	 	case "dsubi":
	 	case "or":
	 	case "ori":
	 	case "lui":
	 	case "li":{
	 		this.ExecCycles = 1;
	 		if(res.intgrUnit == false){
	 			return true;
	 		}
	 	break;}
	 	case "lw":
	 	case "sw":
	 		this.ExecCycles = 1;
	 		if(res.loadUnit == false){	
	 			return true;
	 		}
	 	case "l.d":
	 	case "s.d":{
	 		this.ExecCycles = 1;
	 		if(res.loadUnit == false){
	 			return true;
	 		}
	 	break;}
	 	case "beq":
	 	case "bne":
	 	case "j":
	 	case "hlt":
	 		this.ExecCycles = 1;
	 		if(res.brunit == false){
	 			return true;
	 		}
	 		break;
	 	default: 
			System.out.println("Invalid Instruction given as Input! Please check Instructions and Run again");
			System.exit(0);
	 }
	 //this.ExecCycles = 1;
	 return false;
 }
 
 void setResourcesBusy(ProgramCounter pInst){//complete the code
	 switch(pInst.inst){
	 	case "mul.d":{
	 		for(int i=0;i<res.multSize;i++){
	 			if(res.mult[i] == false){
	 				res.mult[i] = true;
	 				return;
	 			}
	 		}
	 	break;}
	 	case "add.d":
	 	case "sub.d":{
	 		for(int i=0;i<res.addSize;i++){
	 			if(res.add[i] == false){
	 				res.add[i] = true;
	 				return;
	 			}
	 		}
	 	break;}
	 	case "div.d":{
	 		for(int i=0;i<res.divSize;i++){
	 			if(res.div[i] == false){
	 				res.div[i] = true;
	 				return;
	 			}
	 		}
	 	break;}
	 	case "and":
	 	case "andi":
	 	case "dadd":
	 	case "daddi":
	 	case "dsub":
	 	case "dsubi":
	 	case "or":
	 	case "ori":
	 	case "lui":
	 	case "li":{
	 		res.intgrUnit = true;
	 	break;}
	 	case "lw":
	 	case "sw":
	 	case "l.d":
	 	case "s.d":{
	 		res.loadUnit = true;
	 	break;}
	 	case "beq":
	 	case "bne":
	 	case "j":
	 	case "hlt":
	 		res.brunit = true;
	 		break;
	 	//needs more cases
	 }
	 return;
 }
 
 void makeResourcesFree(ProgramCounter pInst){//complete the code
	 switch(pInst.inst){
	 	case "mul.d":{
	 		for(int i=0;i<res.multSize;i++){
	 			if(res.mult[i] == true){
	 				res.mult[i] = false;
	 				return;
	 			}
	 		}
	 	break;}
	 	case "add.d":
	 	case "sub.d":{
	 		for(int i=0;i<res.addSize;i++){
	 			if(res.add[i] == true){
	 				res.add[i] = false;
	 				return;
	 			}
	 		}
	 	break;}
	 	case "div.d":{
	 		for(int i=0;i<res.divSize;i++){
	 			if(res.div[i] == true){
	 				res.div[i] = false;
	 				return;
	 			}
	 		}
	 	break;}
	 	case "and":
	 	case "andi":
	 	case "dadd":
	 	case "daddi":
	 	case "dsub":
	 	case "dsubi":
	 	case "or":
	 	case "ori":
	 	case "lui":
	 	case "li":{
	 		if(res.intgrUnit == true){
	 			res.intgrUnit = false;
	 		}
	 	break;}
	 	case "lw":
	 	case "sw":
	 	case "l.d":
	 	case "s.d":{
	 		if(res.loadUnit == true){
	 			res.loadUnit = false;
	 		}
	 	break;}
	 	//needs more cases
	 }
	 return;
 }

 String chkLoadfromCache(int offset){
	if( pInst.inst.equals("lw") || pInst.inst.equals("l.d") || pInst.inst.equals("sw") || pInst.inst.equals("s.d")){
		int r2 = res.returnRegNumber(pInst.arg2);
		int x = pInst.arg2.indexOf('(');
		int r1 = res.returnRegNumber(pInst.arg1);
		int address;
		if(x>1){
			address = res.Reg[r2][0]+Integer.parseInt(pInst.arg2.substring(0, x));
		}
		else{
			address = res.Reg[r2][0]+Character.getNumericValue(pInst.arg2.charAt(0));
		}
		DCache.DataCacheInfo DcInfo = DCache.fetchData(address+offset);
		this.ExecCycles = this.ExecCycles+DcInfo.clockCycles;
		if(pInst.inst.equals("lw") || pInst.inst.equals("sw")){
			this.ExecCycles--;
		}
		if(pInst.inst.equals("sw")){
			DCache.updateValue(address+offset, Conversion.toBinary(res.Reg[r1][0]));
		}
		this.isDCacheHit = true;
		return DcInfo.data;
	}
	return "000";
 }
 
void updateRegFlags(ProgramCounter pInst){
	 int regval = 0;
	 if(pInst.arg2 != null){
		 char temp;
		 temp = res.retrunRegtype(pInst.arg2);//pInst.arg2.charAt(0);
		 regval = res.returnRegNumber(pInst.arg2);
		 if(temp == 'r' && regval>0){
			 res.Reg[regval][2] = 0;
		 }
		 if(temp == 'f' && regval>0){
			 res.FReg[regval][2] = 0;
		 }
	 }
	 if(pInst.arg3 != null){
		 char temp;
		 temp = res.retrunRegtype(pInst.arg3);//pInst.arg2.charAt(0);
		 regval = res.returnRegNumber(pInst.arg3);
		 if(temp == 'r' && regval>0){
			 res.Reg[regval][2] = 0;
		 }
		 if(temp == 'f' && regval>0){
			 res.FReg[regval][2] = 0;
		 }
	 }
 }
void updatewriteRegFlags(ProgramCounter pInst){
	 int regval = 0;
	 if(pInst.arg1 != null){
		 char temp;
		 temp = res.retrunRegtype(pInst.arg1);
		 regval = res.returnRegNumber(pInst.arg1);
		 if(temp == 'r' && regval>0){
			 res.Reg[regval][1] = 0;
		 }
		 if(temp == 'f' && regval>0){
			 res.FReg[regval][1] = 0;
		 }
	 }
	 
}
 
 boolean hasWAW(ProgramCounter pInst){
	 int regval = 0;
	 if(pInst.arg1 != null && pInst.arg2 != null){
		 if(pInst.arg1.length() == 3){
			 regval = Integer.parseInt(pInst.arg1.substring(1, 3));
		 }
		 else if(pInst.arg1.length() == 2){
			 regval = Character.getNumericValue(pInst.arg1.charAt(1));
		 }
	 }
	 else{
		 return false;
	 }
	 char temp;
	 temp = pInst.arg1.charAt(0);
	 if(temp == 'r'){
		 if(res.Reg[regval][1] > 0){
			 return true;
		 }
	 }
	 if(temp == 'f'){
		 if(res.FReg[regval][1] > 0){
			 return true;
		 }
	 }
	 
	 return false;
 }

 boolean hasWAR(String arg1, String arg2, String arg3, int pc){
	 int regval = 0;
	 if(arg2 != null){
		 regval = res.returnRegNumber(arg2);
		 char temp;
		 temp = res.retrunRegtype(arg2);
		 if(temp == 'r' && regval>0 ){
			 if(res.Reg[regval][1] > 0 && res.Reg[regval][1] < pc && !arg1.equals(arg2)){
				 return true;
			 }
		 }
		 if(temp == 'f' && regval>0 ){
			 if(res.FReg[regval][1] > 0 && res.FReg[regval][1] < pc && !arg1.equals(arg2)){
				 return true;
			 }
		 }
	 }
	 else {
		 return false;
	 }
	 if(pInst.arg3 != null){
		 regval = res.returnRegNumber(arg3);
		 char temp;
		 temp = res.retrunRegtype(arg3);
		 if(temp == 'r' && regval>0){
			 if(res.Reg[regval][1] > 0 && res.Reg[regval][1] < pc &&  !arg1.equals(arg3)){
				 return true;
			 }
		 }
		 if(temp == 'f' && regval>0){
			 if(res.FReg[regval][1] > 0 && res.FReg[regval][1] < pc && !arg1.equals(arg3)){
				 return true;
			 }
		 }
	 }
	 else {
		 return false;
	 }
	 return false;
 }
 int branch(String branch, int prgramCounter, int presentPC){
	 int pc=prgramCounter;
	 int r2 = 0;
	 int r1 = 0;
	 res.isBranch = 0;
	 switch(branch){
	 case "bne":
		 r2 = res.returnRegNumber(pInst.arg2);
		 r1 = res.returnRegNumber(pInst.arg1);
		 if(res.Reg[r1][0] != res.Reg[r2][0]){
			 for(int i = 0;i<list.size()-1;i++){
				 if (list.get(i).branch != null){
					 if(list.get(i).branch.equals(pInst.arg3)){
						 if(presentPC != i){
							 return i;	
						 }
						 else{
							 return prgramCounter;
						 }
					 }
				 }
			 }
		 }
		 break;
	 case "beq":
		 r1 = res.returnRegNumber(pInst.arg1);
		 r2 = res.returnRegNumber(pInst.arg2);
		 if(res.Reg[r1][0] == res.Reg[r2][0]){
			 for(int i = 0;i<list.size()-1;i++){
				 if (list.get(i).branch != null){
					 if(list.get(i).branch.equals(pInst.arg3)){
						 if(presentPC != i){
							 return i;	
						 }
						 else{
							 return prgramCounter;
						 }
					 }
				 }
			 }
		 }
		 break;
	 case "j":
		 for(int i = 0;i<list.size()-1;i++){
			 if (list.get(i).branch != null){
				 if(list.get(i).branch.equals(pInst.arg1)){
					 if(presentPC != i){
						 return i;	
					 }
					 else{
						 return prgramCounter;
					 }
				 }
			 }
		 }
		 break;
	 }
	 res.isBranch = 2;
	 return pc;
 }
 boolean hasRAW(ProgramCounter pInst){
	 int regval = 0;
	 if(pInst.arg1 != null && pInst.arg2 != null){
		 if(pInst.arg1.length() == 3){
			 regval = Integer.parseInt(pInst.arg1.substring(1, 3));
		 }
		 else if(pInst.arg1.length() == 2){
			 regval = Character.getNumericValue(pInst.arg1.charAt(1));
		 }
	 }
	 else{
		 return false;
	 }
	 char temp;
	 temp = pInst.arg1.charAt(0);
	 if(temp == 'r' && regval>0){
		 if(res.Reg[regval][2] == 1){
			 return true;
		 }
	 }
	 if(temp == 'f' && regval>0){
		 if(res.FReg[regval][2] == 1){
			 return true;
		 }
	 }
	 return false;
 }
 
void setWriteReg(String arg, int pc){
	int regval = 0;
	if(arg != null){
		 if(arg.length() == 3){
			 regval = Integer.parseInt(arg.substring(1, 2));
		 }
		 else if(arg.length() == 2){
			 regval = Character.getNumericValue(arg.charAt(1));
		 }
	
	char temp = arg.charAt(0);
	 if(temp == 'r' && regval>0){
		 res.Reg[regval][1] = pc;
	 }
	 if(temp == 'f' && regval>0){
		 res.FReg[regval][1] = pc;
	 }
	}
}
void setReadReg(String arg){
	int regval = 0;
	if(arg != null){
		 if(arg.length() == 3){
			 regval = Integer.parseInt(arg.substring(1, 3));
		 }
		 else if(arg.length() == 2){
			 regval = Character.getNumericValue(arg.charAt(1));
		 }
	 char temp = arg.charAt(0);
	 if(temp == 'r'){
		 res.Reg[regval][2] = 1;
	 }
	 if(temp == 'f'){
		 res.FReg[regval][2] = 1;
	 }
	}
}
 
 int update(int clkCounter, int prgramCounter, int warPC, boolean flag){
	 if(this.csFinish){
		 isEtPush(this.current_state);
	 }
	 if(this.isEligibletopush == true){
		 this.current_state = this.next_state;
		 this.next_state = this.next_state+1;
	 }
	 this.csFinish = false;
	 this.isNextStateFree = false;
	 this.isFetchFree = false;
	 this.isEligibletopush =false;
	 switch(this.current_state){
	 	case 0:{
	 		if(flag){
	 			this.isBrachTaken = true;
	 		}
	 		this.FetchCycles--;
	 		this.arg1= pInst.arg1;
 			this.arg2= pInst.arg2;
 			this.arg3= pInst.arg3;
 			this.inst= pInst.inst;
 			this.branch = pInst.branch;
	 		if(!res.issue && pInst.inst.equals("hlt") && this.FetchCycles <= 0){
	 			this.csFinish = true;
	 			this.isFetchFree = true;
	 			this.fe = clkCounter;
	 			this.isNextStateFree = true;
	 			//this.current_state = 4;
	 			//this.next_state = 5;
	 			break;
	 		}
	 		if(!res.issue && this.FetchCycles <= 0){
	 			this.csFinish = true;
	 			this.isFetchFree = true;
	 			this.fe = clkCounter;
	 			this.isNextStateFree = true;
	 		}
	 	break;}
	 	case 1:{
	 		//check for waw hazard and Structural Hazard
	 		
	 		res.issue = true;
	 		this.waw = hasWAW(this.pInst);
	 		boolean hasRes = true;
	 		hasRes = isResourcesFree(this.pInst);
	 		
	 		if(pInst.inst.equals("bne")){
	 			this.csFinish = true;
	 			res.issue = false;
	 			this.is = clkCounter;
	 			//setResourcesBusy(this.pInst);
	 			this.isNextStateFree = true;
	 			this.waw = false;
	 			this.struct =false;
	 			prgramCounter = branch("bne", prgramCounter, warPC);
	 			break;
	 		}
	 		if(pInst.inst.equals("j")){
	 			this.csFinish = true;
	 			res.issue = false;
	 			//setResourcesBusy(this.pInst);
	 			this.is = clkCounter;
	 			this.isNextStateFree = true;
	 			this.waw = false;
	 			this.struct =false;
	 			prgramCounter = branch("j", prgramCounter, warPC);
	 			break;
	 		}
	 		if(pInst.inst.equals("beq")){
	 			this.csFinish = true;
	 			res.issue = false;
	 			//setResourcesBusy(this.pInst);
	 			this.is = clkCounter;
	 			this.isNextStateFree = true;
	 			this.waw = false;
	 			this.struct = false;
	 			prgramCounter = branch("beq", prgramCounter, warPC);
	 			break;
	 		}
	 		
	 		if(pInst.inst.equals("s.d")||pInst.inst.equals("sw")){
	 			this.waw = false;
	 		}if(!hasRes){
	 			this.sh = 'Y';
	 		}
	 		if(pInst.inst.equals("hlt")){
	 			this.sh = 'N';
	 		}
	 		if(this.waw){
	 			this.h_waw = 'Y';
	 		}
	 		if(hasRes && !this.waw && res.isBranch == 0){
	 			setResourcesBusy(this.pInst);
	 			this.csFinish = true;
	 			res.issue = false;
	 			if(!(pInst.inst.equals("s.d")||pInst.inst.equals("sw"))){
	 				setWriteReg(pInst.arg1, warPC);
	 			}
	 			this.is = clkCounter;
	 			//if(pInst.inst.equals("hlt")){this.is = clkCounter+1;}
	 			this.isNextStateFree = true;
	 		}
	 	break;}
	 	case 2:{/*
	 		if(pInst.inst.equals("bne")){
	 			prgramCounter = branch("bne", prgramCounter, warPC);
	 			break;
	 		}
	 		if(pInst.inst.equals("j")){
	 			prgramCounter = branch("j", prgramCounter, warPC);
	 			break;
	 		}
	 		if(pInst.inst.equals("beq")){
	 			prgramCounter = branch("beq", prgramCounter, warPC);
	 			break;
	 		}*/
	 		if(pInst.inst.equals("hlt") || this.isBrachTaken){
	 			this.csFinish = true;
	 			res.issue = false;
	 			updatewriteRegFlags(this.pInst);
	 			makeResourcesFree(this.pInst);
	 			this.isNextStateFree = true;
	 			this.current_state = 4;
	 			this.next_state = 5;
	 			break;
	 		}
	 		
	 		if(pInst.inst.equals("bne") || pInst.inst.equals("beq") || pInst.inst.equals("j")){
	 			setResourcesBusy(this.pInst);
	 		}
	 		if(pInst.inst.equals("bne") || pInst.inst.equals("beq") || pInst.inst.equals("sw")|| pInst.inst.equals("s.d")){
	 			this.war = hasWAR("Hello", this.pInst.arg1, this.pInst.arg2, warPC);
	 		}
	 		else{
	 			this.war = hasWAR(this.pInst.arg1, this.pInst.arg2, this.pInst.arg3, warPC);
	 		}	
	 		if(this.war){
	 			this.h_war='Y';
	 		}
	 		if(!this.war){
	 			this.csFinish = true;
	 			res.brunit = false;
	 			this.rd = clkCounter;
	 			setReadReg(pInst.arg2);
	 			setReadReg(pInst.arg3);
	 			this.isNextStateFree = true;
	 			if(!isDCacheHit){
	 				String data = "";
		 			data = chkLoadfromCache(0); //dealy from Dmem
		 			Execution exec = new Execution(res, DCache);
		 			exec.executeInst(this.pInst, data);
		 			isDCacheHit = true;
		 			if(pInst.inst.equals("l.d") || pInst.inst.equals("s.d")){
		 				isDCacheHit = false;
		 			}
		 		}
	 		}
	 	break;}
	 	case 3:{
	 		if(pInst.inst.equals("bne") || pInst.inst.equals("beq") || pInst.inst.equals("j")){
	 			res.isBranch = 0;
	 		}
	 		if(!isDCacheHit){
	 			chkLoadfromCache(4);
	 			isDCacheHit = true;
	 			this.ExecCycles--;
	 		}
	 		if(this.ExecCycles == 1){
	 			updateRegFlags(this.pInst);
	 			this.csFinish = true;
	 			this.ex = clkCounter;
	 			this.isNextStateFree = true;
	 			break;
	 		}
	 		this.ExecCycles--;
	 	break;}
	 	case 4:{
	 		//this.raw = hasRAW(this.pInst);
	 		if(this.raw){this.h_raw='Y';}
	 		if(!this.raw){
	 			this.csFinish = true;
	 			this.wb = clkCounter;
	 			this.isNextStateFree = true;
	 		}
	 	break;}
	 	case 5: 
	 		updatewriteRegFlags(this.pInst);
 			makeResourcesFree(this.pInst);
 			this.csFinish = true;
 			this.isNextStateFree = true;
 			this.arg1= pInst.arg1;
 			this.arg2= pInst.arg2;
 			this.arg3= pInst.arg3;
 			this.inst= pInst.inst;
 			this.branch = pInst.branch;
 			if(pInst.inst.equals("bne") || pInst.inst.equals("j") || pInst.inst.equals("beq")){
 				this.wb = 0;
 				this.ex = 0;
	 		}
 			//System.out.println(pInst.inst+" "+pInst.arg1+" "+pInst.arg2+" "+pInst.arg3+" "+this.fe+" "+this.is +" "+this.rd+" "+this.ex +" "+this.wb);
 			break;
 		default: break;
 		
	 }
	 //System.out.println(pInst.inst+" "+pInst.arg1+" "+pInst.arg2+" "+pInst.arg3+" "+this.fe+" "+this.is +" "+this.rd+" "+this.ex +" "+this.wb);
		//this.clk++;
	 return prgramCounter;
 }
}

