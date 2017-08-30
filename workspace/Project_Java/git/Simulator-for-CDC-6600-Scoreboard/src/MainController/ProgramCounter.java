package MainController;

public class ProgramCounter{  
	public String arg1;  
	public String arg2;
	public String arg3;
	public String inst;
	public String branch;
	ProgramCounter(){}
	void set(String a, String b, String c, String i, String br){
		this.arg1 = a;
		this.arg2 = b;
		this.arg3 = c;
		this.inst = i;
		this.branch = br;
	}
	void print(){
		System.out.println(this.branch+" :"+this.inst+" "+this.arg1+" "+this.arg2+" "+this.arg3);
	}
}