package MainController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import Evaluation.Resources;
import Memory.DCache;
import Memory.ICache;
import Memory.Memory;
import Memory.MemoryBus;


public class MainController {
	public static String[] memory = new String[32];
	public static int[] config_data = new int[8];
	public static ArrayList<ProgramCounter> inst_list =new ArrayList<ProgramCounter>();
	public static int clockCtr = 0;
	public static int instNum = 0;
	
	public static void error(String str){
		System.out.println(str);
		System.exit(0);
	}
	public static String formatString(String S){
		if(S == null){
			S = "";
		}
		if(S.equals(" 0")){
			S = "";
		}
		for(int i=S.length();i < 11;i++){
			S = S+" ";
		}
	return S.toUpperCase();
	}
	
	public static void prettyprint(ArrayList<ScoreBoardPipeController> instPipeline, DCache DCache, ICache ICache, String Results){
	try{
		PrintWriter writer = new PrintWriter(Results, "UTF-8");
		/*
		System.out.print(formatString(""));
		System.out.print(formatString(""));
		System.out.print(formatString("Instruction"));
		System.out.print(formatString(""));
		System.out.print(formatString(""));
		System.out.print(formatString("Fetch"));
		System.out.print(formatString("Issue"));
		System.out.print(formatString("Read"));
		System.out.print(formatString("Exec"));
		System.out.print(formatString("Write"));
		System.out.println(" RAW WAW Struct");
		*/
		
		writer.print(formatString(""));
		writer.print(formatString(""));
		writer.print(formatString("Instruction"));
		writer.print(formatString(""));
		writer.print(formatString(""));
		writer.print(formatString("Fetch"));
		writer.print(formatString("Issue"));
		writer.print(formatString("Read"));
		writer.print(formatString("Exec"));
		writer.print(formatString("Write"));
		writer.println(" RAW WAW STRUCT");
		for(int i=0;i<instPipeline.size();i++){
			String Inst = "";
			String Branch = "";
			String arg1 = "";
			String arg2 = "";
			String arg3 = "";
			String fe = ""; String is=""; String rd= ""; String ex = ""; String wb ="";
			if(instPipeline.get(i).branch != null){
				Branch = instPipeline.get(i).branch+":";
			}
			Branch = formatString(Branch);
			Inst = formatString(instPipeline.get(i).inst);
			arg1 = formatString(instPipeline.get(i).arg1);
			arg2 = formatString(instPipeline.get(i).arg2);
			arg3 = formatString(instPipeline.get(i).arg3);
			fe = formatString(" "+Integer.toString(instPipeline.get(i).fe));
			is = formatString(" "+Integer.toString(instPipeline.get(i).is));
			rd = formatString(" "+Integer.toString(instPipeline.get(i).rd));
			ex = formatString(" "+Integer.toString(instPipeline.get(i).ex));
			wb = formatString(" "+Integer.toString(instPipeline.get(i).wb));
			writer.print(Branch+Inst+arg1+arg2+arg3+fe+is+rd+ex+wb+" ");
			//System.out.print(Branch+Inst+arg1+arg2+arg3+fe+is+rd+ex+wb+" ");
			writer.println(" "+instPipeline.get(i).h_war+"   "+instPipeline.get(i).h_waw+"    "+/*instPipeline.get(i).h_raw+"    "*/instPipeline.get(i).sh);
			//System.out.println(" "+instPipeline.get(i).h_war+"   "+instPipeline.get(i).h_waw+"    "+/*instPipeline.get(i).h_raw+"    "*/instPipeline.get(i).sh);
        }
		
		//System.out.println("Total number of access requests for instruction cache: "+ICache.getStatsRequests());
		//System.out.println("Number of instruction cache hits: "+ICache.getStatsHitss());
		writer.println("Total number of access requests for instruction cache: "+ICache.getStatsRequests());
		writer.println("Number of instruction cache hits: "+ICache.getStatsHitss());
		
		//System.out.println("Total number of access requests for data cache: "+DCache.getStatsRequests());
		//System.out.println("Number of data cache hits: "+DCache.getStatsHitss());
		writer.println("Total number of access requests for data cache: "+DCache.getStatsRequests());
		writer.println("Number of data cache hits: "+DCache.getStatsHitss());
		System.out.println("Simualtion Successful!\nOpen result.txt file for Simulation Outputs");
		writer.close();
	} catch (IOException e) {System.out.println(e);}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length == 4) {
            File inst = new File(args[0]);
            File config = new File(args[2]);
            String Datapath = args[1];
            try {
    			String line;
    			int counter = 0;
    			FileReader fileReader1 = new FileReader(config);
    			BufferedReader bufferedReader1 = new BufferedReader(fileReader1);
    			while ((line = bufferedReader1.readLine()) != null) {
    				String[] f_splited = line.split(":");
    				String[] s_splited = f_splited[1].split(",");
    				config_data[counter] =  Integer.parseInt(s_splited[0].trim());
    				counter++;
    				config_data[counter] =  Integer.parseInt(s_splited[1].trim());
    				//System.out.println(config_data[counter-1]+ ", "+config_data[counter]);
    				counter++;
    			}
    			fileReader1.close();
    			FileReader fileReader2 = new FileReader(inst);
    			BufferedReader bufferedReader2 = new BufferedReader(fileReader2);
    			
    			String branch, arg1, arg2, arg3, sinst;
    			
    			arg3=null;arg2=null;arg1=null;branch=null;sinst=null;
    			
    			while ((line = bufferedReader2.readLine()) != null) {
    				ProgramCounter pc = new ProgramCounter();
    				
    				String[] f_splited = line.split(":");
    				String[] a_splited = f_splited[f_splited.length-1].split(",");
    				String[] i_Splited = a_splited[0].trim().split(" ");
    				if(f_splited.length == 1){
    					branch = null;
    				}
    				else if(f_splited.length == 2){
    					branch = f_splited[0].trim().toLowerCase();
    				}
    				else{
    					error("The Inst.txt file is not correct::More than two branches to Inst");
    				}
    				if(a_splited.length == 3){
						arg3 = a_splited[2].trim().toLowerCase();
						arg2 = a_splited[1].trim().toLowerCase();
					}
					else if(a_splited.length == 2){
						arg3=null;
						arg2 = a_splited[1].trim().toLowerCase();
					}
					else if(a_splited.length == 1){
						arg2=null;arg3=null;
					}
					else{error("usage: branch<optional>: Instruction arg1<optional> arg2<optional> arg3<optional> ");}
    				if(i_Splited.length == 1){
    					sinst = "hlt";
    					arg1 = null;
    				}
    				else{
    					sinst = i_Splited[0].trim().toLowerCase();
    					arg1 = i_Splited[i_Splited.length-1].trim().toLowerCase();
    				}
    				pc.set(arg1, arg2, arg3, sinst, branch);
    				inst_list.add(pc);
    				instNum++;
    				//pc.print();
    			}
    			fileReader2.close();
    		} catch (IOException e) {e.printStackTrace();}
            ArrayList<ScoreBoardPipeController> instPipeline	= new ArrayList<ScoreBoardPipeController>();
            ClockController c = new ClockController();
            MemoryBus MB = new MemoryBus();
            Resources res =  new Resources(config_data);
            Memory mem = new Memory(Datapath);
            DCache DCache = new DCache(c, MB, Datapath, mem);
            ICache ICache = new ICache(config_data[6], config_data[7], c, MB);
            
            int prgramCounter = 0;
            int clock = ICache.fetchInstruction(prgramCounter);
            int clkCounter = 1;
            ScoreBoardPipeController p = new ScoreBoardPipeController(res, clock, inst_list.get(prgramCounter), clkCounter, c, MB, inst_list, DCache);
            instPipeline.add(p); 
            for(prgramCounter = 0;prgramCounter < inst_list.size()-1;){
            	boolean flag = false;
            	for(int i = 0;i<instPipeline.size();i++){
            		int temp = prgramCounter;
            		prgramCounter = instPipeline.get(i).update(clkCounter, prgramCounter+1, i+1, flag)-1;
            		if(prgramCounter != temp){
            			flag = true;
            		}
            	}
            	if(instPipeline.get(instPipeline.size()-1).isFetchFree){
            		prgramCounter++;
            		clock = ICache.fetchInstruction(prgramCounter);
            		p = new ScoreBoardPipeController(res, clock, inst_list.get(prgramCounter), clkCounter, c, MB, inst_list, DCache);
            		instPipeline.add(p);
            		//System.out.println(inst_list.get(prgramCounter).inst);
            	}
            	clkCounter++;
            	c.sync();
            }
            for(int i=0; i<100; i++){
            	for(int j = 0;j<instPipeline.size();j++){
            		instPipeline.get(j).update(clkCounter, prgramCounter, j+1, false);
            	}
            	clkCounter++;
            	c.sync();
            }
            /*//Prints added for testing
            for(int i=0;i<instPipeline.size();i++){
            	for(int j = 0;j<instPipeline.size();j++){
            		instPipeline.get(j).update(clkCounter, prgramCounter, j+1);
            	}
            	clkCounter++;
            	c.tick();
            	System.out.println(instPipeline.get(i).branch+" "+instPipeline.get(i).inst+" "+
            	instPipeline.get(i).arg1+" "+instPipeline.get(i).arg2+" "+instPipeline.get(i).arg3+" "+
            	instPipeline.get(i).fe+" "+instPipeline.get(i).is +" "+instPipeline.get(i).rd+" "+
            	instPipeline.get(i).ex +" "+instPipeline.get(i).wb+" "+instPipeline.get(i).h_war+" "+
            	instPipeline.get(i).h_waw+" "+instPipeline.get(i).h_raw+" "+instPipeline.get(i).sh);
            }*/
            prettyprint(instPipeline, DCache, ICache, args[3]);
            mem.writeData();
		}
		else{error("Usage: simulator inst.txt data.txt config.txt result.txt");}
	}

}
