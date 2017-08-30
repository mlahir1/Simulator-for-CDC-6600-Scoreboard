package Evaluation;

import MainController.ProgramCounter;

public class Resources {
	public boolean fetch;
	public boolean issue;
	public boolean[] mult;
	public boolean[] add;
	public boolean[] div;
	public boolean intgrUnit;
	public boolean loadUnit;
	public boolean brunit;
	public int multCycles, addCycles, divCycles;
	public int multSize;
	public int addSize;
	public int divSize;
	public int[][] Reg;
	public int[][] FReg;
	public int isBranch;
	
	public Resources(int[] config) {
		this.Reg = new int[33][3]; this.FReg = new int[33][3]; this.fetch = false; this.issue =false;
		this.mult = new boolean[config[2]];this.multCycles = config[3]; this.add = new boolean[config[0]]; this.addCycles = config[1];
		this.div = new boolean[config[4]];this.divCycles = config[5]; this.brunit=false; this.intgrUnit = false; this.loadUnit = false; this.multSize = config[2];
		this.addSize = config[0]; this.divSize = config[4];this.isBranch = 0;
	}
	
	public void updateRegs(ProgramCounter pInst, int arg1){
		if(pInst.arg1 != null){
			char temp;
			int regval = 0;
			if(pInst.arg1.length() == 3){
				regval = Integer.parseInt(pInst.arg1.substring(1, 3));
			}
			else{
				regval = Character.getNumericValue(pInst.arg1.charAt(1));
			}
			temp = pInst.arg1.charAt(0);
			if(temp == 'r'){
				this.Reg[regval][0] = arg1;
			}
			if(temp == 'f'){
				this.FReg[regval][0] = arg1;
			}
		}
	}
	
	public char retrunRegtype(String arg) {
		if(arg == null){
			return 'x';
		}
		char temp = 'x';
		if(arg.indexOf('r') != -1){
			return 'r';
		}
		else if (arg.indexOf('f') != -1){
			return 'f';
		}
		else {
			return temp;
		}
	}
	
	public int returnRegNumber(String arg) {
		if(arg == null){
			return -1;
		}
		char type = retrunRegtype(arg);
		if(type == 'r' ){
			int ten;
			int one = -1;
			ten  = Character.getNumericValue(arg.charAt(arg.indexOf('r')+1));
			if(arg.length() < arg.indexOf('r')+2){
				one  = Character.getNumericValue(arg.charAt(arg.indexOf('r')+2));
			}
			if(one > 0 && one < 10){
				return (ten*10+one);
			}
			else if(ten > 0 && ten < 10){
				return ten;
			}
			
		}
		if(type == 'f'){
			int ten;
			int one = -1;
			ten  = Character.getNumericValue(arg.charAt(arg.indexOf('f')+1));
			if(arg.length() < arg.indexOf('f')+2){
				one  = Character.getNumericValue(arg.charAt(arg.indexOf('f')+2));
			}
			if(one > 0 && one < 10){
				return (ten*10+one);
			}
			else if(ten > 0 && ten < 10){
				return ten;
			}
		}
		return -1;
	}
}
