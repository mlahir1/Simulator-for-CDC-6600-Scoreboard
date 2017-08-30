package Evaluation;

import MainController.ProgramCounter;
import Memory.DCache;

public class Execution {
	
	Resources res;
	DCache DCache;
	public Execution(Resources Resource, DCache DCache) {
		// TODO Auto-generated constructor stub
		this.res = Resource;
		this.DCache = DCache;
	}
	public int getArgVal(String arg1){
		int regval;
		if(arg1 != null){
			if(arg1.length() == 3){
				regval = Integer.parseInt(arg1.substring(1, 3));
			}
			else{
				regval = Character.getNumericValue(arg1.charAt(1));
			}
			return regval;
		}
		return 0;
	}
	
	public void executeInst(ProgramCounter pInst, String data){
		int r1,r2,r3;
		switch(pInst.inst){
			case "lw":
				r1 = res.returnRegNumber(pInst.arg1);
				int value = Conversion.toInt(data);
				res.Reg[r1][0] = value; 
				break;
			case "sw":
				r1 = res.returnRegNumber(pInst.arg1);
				r2 = res.returnRegNumber(pInst.arg2);
				String data1 = Conversion.toBinary(res.Reg[r1][0]);
				int off = Integer.parseInt(pInst.arg2.substring(0, pInst.arg2.indexOf('(')));
				int adress = res.Reg[r2][0]+off; 
				DCache.updateValue(adress, data1);
				break;
			case "l.d":
			case "s.d":
			case "hlt":
			case "j":
			case "beq":
			case "bne":
			case "add.d":
			case "div.d":
			case "mul.d":
			case "sub.d":
				break;
			case "dadd":
				r1 = getArgVal(pInst.arg1);
				r2 = getArgVal(pInst.arg2);
				r3 = getArgVal(pInst.arg3);
				res.Reg[r1][0] = res.Reg[r2][0]+res.Reg[r3][0];
				break;
			case "daddi":
				r1 = getArgVal(pInst.arg1);
				r2 = getArgVal(pInst.arg2);
				r3 = Integer.parseInt(pInst.arg3);
				res.Reg[r1][0] = res.Reg[r2][0] + r3;
				break;
			case "dsub":
				r1 = getArgVal(pInst.arg1);
				r2 = getArgVal(pInst.arg2);
				r3 = getArgVal(pInst.arg3);
				res.Reg[r1][0] = res.Reg[r2][0]-res.Reg[r3][0];
				break;
			case "dsubi":
				r1 = getArgVal(pInst.arg1);
				r2 = getArgVal(pInst.arg2);
				r3 = Integer.parseInt(pInst.arg3);
				res.Reg[r1][0] = res.Reg[r2][0] - r3;
				break;
			case "and":
				r1 = getArgVal(pInst.arg1);
				r2 = getArgVal(pInst.arg2);
				r3 = getArgVal(pInst.arg3);
				res.Reg[r1][0] = res.Reg[r2][0]&res.Reg[r3][0];
				break;
			case "andi":
				r1 = getArgVal(pInst.arg1);
				r2 = getArgVal(pInst.arg2);
				r3 = Integer.parseInt(pInst.arg3);
				res.Reg[r1][0] = res.Reg[r2][0] & r3;
				break;
			case "li":
				r1 = getArgVal(pInst.arg1);
				r2 = Integer.parseInt(pInst.arg2);
				res.Reg[r1][0] = r2;
				break;
			case "lui":
				r1 = getArgVal(pInst.arg1);
				r2 = Integer.parseInt(pInst.arg2);
				res.Reg[r1][0] = (int)(Math.pow(2, 16)*r2);
				break;
			default: 
				System.out.println("Invalid Instruction given as Input");
				System.exit(0);
		}
	}
}
