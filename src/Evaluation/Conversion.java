package Evaluation;

public class Conversion {
	public static int toInt(String data){
		int val = 0;
		for(int i = 1; i < data.length(); i++){
			val = val * 2 + (data.charAt(i)-'0');
		}
		if(data.charAt(0) == '1'){
			val = val + Integer.MIN_VALUE;
		}
		return val;
	}
	
	public static String toBinary(int data){
		char bin[] = new char[32];
		for(int i = 0 ; i < bin.length; i++){
			bin[i] = '0';
		}
		long Val;
		if(data < 0){
			Val = data + Integer.MIN_VALUE;
			bin[0] = '1';
		}else{
			Val = data;
			bin[0] = '0';
		}
		int ind = 31;
		while(Val != 0){
			int mod = (int)(Val%2);
			if(mod == 0){
				bin[ind] = '0';
			}else{
				bin[ind] = '1';
			}
			ind--;
			Val = Val/2;
		}
		return new String(bin);
	}
}