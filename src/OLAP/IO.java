package OLAP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class IO {
	
	/*
	 * prompt user to input 6 operand
	 */
	public static void initByPrompt() throws endProgramExeption{
		String gv = null, ga = null, af = null
				,pr = null, pd = null, hg = null;
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while(MFConfig.F==null||MFConfig.G==null||MFConfig.N==null
				||MFConfig.O==null||MFConfig.S==null||MFConfig.V==null){
			try {
				//validate number of grouping variables
				if(MFConfig.N==null){
					System.out.println("NUMBER OF GROUPING VARIABLES(n):");
					gv = br.readLine();
					isEndProgram(gv);
					MFConfig.checkN(gv);
					MFConfig.initN(gv);
				}
				
				//validate grouping attribute
				if(MFConfig.V==null){
					System.out.println("GROUPING ATTRIBUTES(V): for example(cust,prod)");
					ga = br.readLine();
					isEndProgram(ga);
					MFConfig.checkV(ga);
					MFConfig.initV(ga);
				}
				
				//validate aggregate function
				if(MFConfig.F==null){
					System.out.println("F-VECT([F]): for example(1_sum_quant, 1_avg_quant, 2_sum_quant)");
					af = br.readLine();
					isEndProgram(af);
					MFConfig.checkF(af);
					MFConfig.initF(af);
				}
				
				//validate select attributes
				if(MFConfig.S==null){
					System.out.println("SELECT ATTRIBUTE(S): for example(cust,prod,1_sum_quant,2_sum_quant)");
					pr = br.readLine();
					isEndProgram(pr);
					MFConfig.checkS(pr);
					MFConfig.initS(pr);
				}
				
				//validate "such that" AKA sigma
				//String temp;
				if(MFConfig.O==null){
					pd = "";
//					for(int i=1; i <= Integer.parseInt(gv);i++){
//						System.out.println("SELECT CONDITION-VECT([σ]) for groupping variable X" + i + ": for example(1.cust=cust and 1.state = 'NY')");
//						temp = br.readLine();
//						MFConfig.checkO(temp);
//						if(i<Integer.parseInt(gv)){
//							pd += temp + ",";
//						}else{
//							pd += temp;
//						}
//					}
					System.out.println("SELECT CONDITION-VECT([σ]):");
					pd = br.readLine();
					MFConfig.initO(pd);
				}
				
				//validate having clause
				System.out.println("HAVING_CONDITION(G):");
				hg = br.readLine();
				MFConfig.checkG(hg);
				MFConfig.initG(hg);
				
			}catch(IOException e1){
				System.out.print(e1);
			}catch(endProgramExeption e2){
				throw new endProgramExeption();
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public static void isEndProgram(String s) throws endProgramExeption{
		if(s.equalsIgnoreCase("quit")){
			throw new endProgramExeption();
		}
	}
	
	public static String initByReadFile(String path){
		File file = new File(path);
		StringBuilder sb = new StringBuilder();
		String absolutePath = file.getAbsolutePath();
		System.out.println(absolutePath);
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			ArrayList<String> list = new ArrayList<String>();
			String temp = null;
			while((temp = br.readLine()) != null){
				list.add(temp);
			}
			if(list.size()>=1){
				MFConfig.initN(list.get(0));
			}
			if(list.size()>=2){
				MFConfig.initV(list.get(1));
			}
			if(list.size()>=3){
				MFConfig.initF(list.get(2));
			}
			if(list.size()>=4){
				MFConfig.initS(list.get(3));
			}
			if(list.size()>=5){
				MFConfig.initO(list.get(4));
			}
			if(list.size()>=6){
				MFConfig.initG(list.get(5));
			}
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}
	
}
