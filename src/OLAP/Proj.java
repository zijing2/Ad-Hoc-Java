package OLAP;

import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JForLoop;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JLabel;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JStatement;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JWhileLoop;


public class Proj {

	public static void main(String[] args) throws Exception {
		init();
		//IO.initByPrompt();
		IO.initByReadFile("src/TEST/test6");
		
//		MFConfig.initConfig(
//				//S
//				"prod,month,1_avg_quant,2_count_quant,3_avg_quant",
//				//N
//				"3",
//				//V
//				"prod,month",
//				//F
//				"1_avg_quant,2_count_quant,3_avg_quant", 
//				//O
//				"1.prod = prod and 1.month = month-1, 2.prod = prod and 2.month = month and 2.quant > avg(1.quant) and 2.quant < avg(3.quant), 3.prod = prod and 3.month = month+1",
//				//G
//				"3_avg_quant > 1_avg_quant");

		//topologicalSort
		Graph.initG();
		Graph.topoligicalSort();
		MFConfig.suchThatParser();
		MFConfig.havingParser();
		
		generateQ1Class();
	}
	
	public static void init(){
		TableSchema.InitTableSchema();
	}
	
	/*
	 * generate Q1 class (execute plan)
	 */
	public static void generateQ1Class() throws Exception{
		
		JCodeModel jc = new JCodeModel();
		File destDir = new File("src/");
		
		//generate MFStructure
		JDefinedClass dc_mfs = jc._class("OLAP.MFStructure");
		JType mfs_type = jc.parseType("MFStructure"); 
		for(int i=0;i<MFConfig.V.length;i++){
			if(TableSchema.isAttributeInt(MFConfig.V[i])){
				dc_mfs.field(JMod.PUBLIC, jc.INT, MFConfig.V[i]);
			}else{
				dc_mfs.field(JMod.PUBLIC, jc.parseType("String"), MFConfig.V[i]);
			}
		}
		for(int i=0;i<MFConfig.F.length;i++){
			if(MFConfig.F[i].indexOf("sum")!=-1 ||MFConfig.F[i].indexOf("count")!=-1 ||MFConfig.F[i].indexOf("avg")!=-1){
				dc_mfs.field(JMod.PUBLIC, jc.INT, MFConfig.F[i]);
			}else{
				dc_mfs.field(JMod.PUBLIC, jc.parseType("String"), MFConfig.F[i]);
			}
		}
		
		//generate getMFStructure Instance method
		JMethod getMFSInstance =  dc_mfs.method(JMod.PUBLIC, mfs_type, "getMFSInstance");
		JBlock getMFSInstanceBody = getMFSInstance.body();
		JVar mfs_temp = getMFSInstanceBody.decl(mfs_type, "mfs", JExpr._new(mfs_type));
		getMFSInstanceBody._return(mfs_temp);
				
		
		jc.build(destDir);
		
		JCodeModel cm = new JCodeModel();
		File destDir2 = new File("src/");
		JDefinedClass dc = cm._class("OLAP.Q1");
		
		//generate MFStructure ArrayList
		//JDefinedClass MFSClass = cm._class("MFStructure");
		JClass arrayListClass = cm.ref(ArrayList.class);
		JClass arrayListOfMFSClass = arrayListClass.narrow(dc_mfs);
		JVar mfs_arrayList = dc.field(JMod.PUBLIC + JMod.STATIC, arrayListOfMFSClass, "mfs_arraylist",JExpr._new(arrayListOfMFSClass)); 
		
		//######## generate main method ##########
		
		JMethod mainMethod =  dc.method(JMod.PUBLIC + JMod.STATIC, cm.VOID, "main");
		JBlock mainBody = mainMethod.body(); 
		mainMethod.param(cm.parseType("String[]"), "args");
		JClass ResultSetType = cm.ref(ResultSet.class);
		JClass DataType = cm.ref(Data.class);
		JClass Exception = cm.ref(Exception.class);
		JClass StringArr = cm.ref(String[].class);
		JVar mfs = mainBody.decl(mfs_type, "mfs");
		JVar topologicalSeq = mainBody.decl(cm.parseType("String"), "topologicalSeq", JExpr.lit(MFConfig.concatenate(Graph.seq_arr, "")));
		
		//the scanning loop
		JForLoop scanForLoop = mainBody._for();
		scanForLoop.init(cm.INT,"r", JExpr.lit(0)); 
		scanForLoop.test(JExpr.ref("r").lte(JExpr.lit(MFConfig.N)));
		scanForLoop.update(JExpr.ref("r").incr());
		JBlock scanf_body = scanForLoop.body();
		
		//scan by topological sort
		scanf_body.decl(cm.INT, "i", JExpr.ref("Integer").invoke("parseInt").arg(JExpr.lit("").plus(JExpr.ref("topologicalSeq").invoke("charAt").arg(JExpr.ref("r")))));
		
		//fetch data
		JTryBlock tblock = scanf_body._try();
		JCatchBlock cblock = tblock._catch(Exception);
		JBlock tbody = tblock.body();
		
		JVar rs = tbody.decl(ResultSetType, "rs", mainBody.invoke(JExpr.ref("Data"), "getSalesRow"));
		JLabel outer = tbody.label("outer");
		JWhileLoop whileLoop = tbody._while(JExpr.ref("rs").invoke("next"));
		JBlock wbody = whileLoop.body();
		//wbody.add(cm.ref(System.class).staticRef("out").invoke("println").arg(JExpr.ref("rs").invoke("getString").arg("quant")));
		
		JConditional scan_round_if = wbody._if(JExpr.ref("i").eq(JExpr.lit(0)));
		String[] aggr_func_0 = MFConfig.getAggreateFunctionByRound(0);
		JBlock scan_round_if_then = scan_round_if._then();
		JBlock scan_round_if_else = scan_round_if._else();
		JForLoop mfs_arrary_forloop= scan_round_if_then._for();
		mfs_arrary_forloop.init(cm.INT,"j", JExpr.lit(0)); 
		mfs_arrary_forloop.test(JExpr.ref("j").lt(JExpr.ref("mfs_arraylist").invoke("size")));
		mfs_arrary_forloop.update(JExpr.ref("j").incr());
		JExpression groupby_if_express = null;
		for(int y=0;y<MFConfig.V.length;y++){
			if(TableSchema.isAttributeInt(MFConfig.V[y])){
				if(groupby_if_express==null){
					groupby_if_express = JExpr.ref("mfs_arraylist")
					.invoke("get").arg(JExpr.ref("j")).ref(MFConfig.V[y])
					.eq(JExpr.ref("Integer").invoke("parseInt").arg(JExpr.ref("rs").invoke("getString").arg(MFConfig.V[y])));
				}else{
					groupby_if_express = groupby_if_express.cand(JExpr.ref("mfs_arraylist")
							.invoke("get").arg(JExpr.ref("j")).ref(MFConfig.V[y])
							.eq(JExpr.ref("Integer").invoke("parseInt").arg(JExpr.ref("rs").invoke("getString").arg(MFConfig.V[y]))));
				}
			}else{
				if(groupby_if_express==null){
					groupby_if_express = JExpr.ref("mfs_arraylist")
					.invoke("get").arg(JExpr.ref("j")).ref(MFConfig.V[y])
					.invoke("equals").arg(JExpr.ref("rs").invoke("getString").arg(MFConfig.V[y]));
				}else{
					 groupby_if_express = groupby_if_express.cand(JExpr.ref("mfs_arraylist")
								.invoke("get").arg(JExpr.ref("j")).ref(MFConfig.V[y])
								.invoke("equals").arg(JExpr.ref("rs").invoke("getString").arg(MFConfig.V[y])));
				}
			}
		}
		JConditional groupby_if = mfs_arrary_forloop.body()._if(groupby_if_express);
		for(int k=0;k<aggr_func_0.length;k++){
			if(aggr_func_0[k].indexOf("sum")!=-1){
				groupby_if._then().assign(JExpr.ref("mfs_arraylist").invoke("get").arg(JExpr.ref("j")).ref(aggr_func_0[k]),JExpr.ref("mfs_arraylist").invoke("get").arg(JExpr.ref("j")).ref(aggr_func_0[k]).plus(JExpr.ref("Integer").invoke("valueOf").arg(JExpr.ref("rs").invoke("getString").arg(aggr_func_0[k].split("_")[0]))));
			}
			if(aggr_func_0[k].indexOf("count")!=-1){
				groupby_if._then().assign(JExpr.ref("mfs_arraylist").invoke("get").arg(JExpr.ref("j")).ref(aggr_func_0[k]), JExpr.ref("mfs_arraylist").invoke("get").arg(JExpr.ref("j")).ref(aggr_func_0[k]).plus(JExpr.lit(1)));
			}
			if(aggr_func_0[k].indexOf("avg")!=-1){
				String[] temp = aggr_func_0[k].split("_");
				temp[1] = "sum";
				String sum = MFConfig.concatenate(temp, "_");
				temp[1] = "count";
				String count = MFConfig.concatenate(temp, "_");
				groupby_if._then().assign(JExpr.ref("mfs_arraylist").invoke("get").arg(JExpr.ref("j")).ref(aggr_func_0[k]), JExpr.ref("mfs_arraylist").invoke("get").arg(JExpr.ref("j")).ref(sum).div(JExpr.ref("mfs_arraylist").invoke("get").arg(JExpr.ref("j")).ref(count)));
			}
			
			if(aggr_func_0[k].indexOf("max")!=-1 || aggr_func_0[k].indexOf("min")!=-1){
				JConditional aggreate_function_null_if = groupby_if._then()._if(JExpr.ref("mfs_arraylist").invoke("get").arg(JExpr.ref("j")).ref(aggr_func_0[k]).eq(JExpr.ref("null")));
				JBlock aggreate_function_null_if_then = aggreate_function_null_if._then();
				JBlock aggreate_function_null_if_else = aggreate_function_null_if._else();
				
				if(aggr_func_0[k].indexOf("max")!=-1){
					aggreate_function_null_if_then.assign(JExpr.ref("mfs_arraylist").invoke("get").arg(JExpr.ref("j")).ref(aggr_func_0[k]), JExpr.ref("rs").invoke("getString").arg(aggr_func_0[k].split("_")[0]));
					JConditional max_if = aggreate_function_null_if_else._if(JExpr.ref("rs").invoke("getString").arg(aggr_func_0[k].split("_")[0]).invoke("compareTo").arg(JExpr.ref("mfs_arraylist").invoke("get").arg(JExpr.ref("j")).ref(aggr_func_0[k])).gt(JExpr.lit(0)));
					max_if._then().assign(JExpr.ref("mfs_arraylist").invoke("get").arg(JExpr.ref("j")).ref(aggr_func_0[k]), JExpr.ref("rs").invoke("getString").arg(aggr_func_0[k].split("_")[0]));
				}
				if(aggr_func_0[k].indexOf("min")!=-1){
					aggreate_function_null_if_then.assign(JExpr.ref("mfs_arraylist").invoke("get").arg(JExpr.ref("j")).ref(aggr_func_0[k]), JExpr.ref("rs").invoke("getString").arg(aggr_func_0[k].split("_")[0]));
					JConditional min_if = aggreate_function_null_if_else._if(JExpr.ref("rs").invoke("getString").arg(aggr_func_0[k].split("_")[0]).invoke("compareTo").arg(JExpr.ref("mfs_arraylist").invoke("get").arg(JExpr.ref("j")).ref(aggr_func_0[k])).lt(JExpr.lit(0)));
					min_if._then().assign(JExpr.ref("mfs_arraylist").invoke("get").arg(JExpr.ref("j")).ref(aggr_func_0[k]), JExpr.ref("rs").invoke("getString").arg(aggr_func_0[k].split("_")[0]));
				}
			}
		}
		groupby_if._then()._continue(outer);
		
		scan_round_if_then.assign(mfs, JExpr._new(mfs_type));
		for(int i=0;i<MFConfig.V.length;i++){
			if(TableSchema.isAttributeInt(MFConfig.V[i])){
				scan_round_if_then.assign(JExpr.ref("mfs").ref(MFConfig.V[i]), JExpr.ref("Integer").invoke("valueOf").arg(JExpr.ref("rs").invoke("getString").arg(MFConfig.V[i])));
			}else{
				scan_round_if_then.assign(JExpr.ref("mfs").ref(MFConfig.V[i]), JExpr.ref("rs").invoke("getString").arg(MFConfig.V[i]));
			}
		}
		for(int k=0;k<aggr_func_0.length;k++){
			if(aggr_func_0[k].indexOf("sum")!=-1){
				scan_round_if_then.assign(JExpr.ref("mfs").ref(aggr_func_0[k]), JExpr.ref("Integer").invoke("valueOf").arg(JExpr.ref("rs").invoke("getString").arg(aggr_func_0[k].split("_")[0])));
			}
			if(aggr_func_0[k].indexOf("count")!=-1){
				scan_round_if_then.assign(JExpr.ref("mfs").ref(aggr_func_0[k]), JExpr.lit(1));
			}
			if(aggr_func_0[k].indexOf("avg")!=-1){
				String[] temp = aggr_func_0[k].split("_");
				temp[1] = "sum";
				String sum = MFConfig.concatenate(temp, "_");
				temp[1] = "count";
				String count = MFConfig.concatenate(temp, "_");
				scan_round_if_then.assign(JExpr.ref("mfs").ref(aggr_func_0[k]), JExpr.ref("mfs").ref(sum).div(JExpr.ref("mfs").ref(count)));
			}
			
			if(aggr_func_0[k].indexOf("max")!=-1 || aggr_func_0[k].indexOf("min")!=-1){
				JConditional aggreate_function_null_if = scan_round_if_then._if(JExpr.ref("mfs").ref(aggr_func_0[k]).eq(JExpr.ref("null")));
				JBlock aggreate_function_null_if_then = aggreate_function_null_if._then();
				JBlock aggreate_function_null_if_else = aggreate_function_null_if._else();
				
				if(aggr_func_0[k].indexOf("max")!=-1){
					aggreate_function_null_if_then.assign(JExpr.ref("mfs").ref(aggr_func_0[k]), JExpr.ref("rs").invoke("getString").arg(aggr_func_0[k].split("_")[0]));
					JConditional max_if = aggreate_function_null_if_else._if(JExpr.ref("rs").invoke("getString").arg(aggr_func_0[k].split("_")[0]).invoke("compareTo").arg(JExpr.ref("mfs").ref(aggr_func_0[k])).gt(JExpr.lit(0)));
					max_if._then().assign(JExpr.ref("mfs").ref(aggr_func_0[k]), JExpr.ref("rs").invoke("getString").arg(aggr_func_0[k].split("_")[0]));
				}
				if(aggr_func_0[k].indexOf("min")!=-1){
					aggreate_function_null_if_then.assign(JExpr.ref("mfs").ref(aggr_func_0[k]), JExpr.ref("rs").invoke("getString").arg(aggr_func_0[k].split("_")[0]));
					JConditional min_if = aggreate_function_null_if_else._if(JExpr.ref("rs").invoke("getString").arg(aggr_func_0[k].split("_")[0]).invoke("compareTo").arg(JExpr.ref("mfs").ref(aggr_func_0[k])).lt(JExpr.lit(0)));
					min_if._then().assign(JExpr.ref("mfs").ref(aggr_func_0[k]), JExpr.ref("rs").invoke("getString").arg(aggr_func_0[k].split("_")[0]));
				}
			}
		}
		scan_round_if_then.add(JExpr.ref("mfs_arraylist").invoke("add").arg(JExpr.ref("mfs")));
		
		JForLoop arrayListForLoop = scan_round_if_else._for();
		arrayListForLoop.init(cm.INT,"j", JExpr.lit(0)); 
		arrayListForLoop.test(JExpr.ref("j").lt(JExpr.ref("mfs_arraylist").invoke("size")));
		arrayListForLoop.update(JExpr.ref("j").incr());
		JBlock arraylistf_body = arrayListForLoop.body();
		arraylistf_body.assign(JExpr.ref("mfs"), JExpr.ref("mfs_arraylist").invoke("get").arg(JExpr.ref("j")));
		
		//assign value to such that clause
		String[] all_attributes = TableSchema.getAllTableAttributes();
		for(int i=0;i<all_attributes.length;i++){
			for(int j=1;j<=MFConfig.N;j++){
				if(TableSchema.isAttributeInt(all_attributes[i])){
					arraylistf_body.decl(cm.INT, "_"+j+all_attributes[i], JExpr.ref("Integer").invoke("valueOf").arg(JExpr.ref("rs").invoke("getString").arg(all_attributes[i])));
				}else{
					arraylistf_body.decl(cm.parseType("String"), "_"+j+all_attributes[i], JExpr.ref("rs").invoke("getString").arg(all_attributes[i]));
				}
					
			}
		}
		for(int i=0;i<MFConfig.V.length;i++){
			if(TableSchema.isAttributeInt(MFConfig.V[i])){
				arraylistf_body.decl(cm.INT, MFConfig.V[i], JExpr.ref("Integer").invoke("valueOf").arg(JExpr.ref("mfs").ref(MFConfig.V[i])));
			}else{
				arraylistf_body.decl(cm.parseType("String"), MFConfig.V[i], JExpr.ref("mfs").ref(MFConfig.V[i]));
			}
		}
		for(int i=0;i<MFConfig.F.length;i++){
			String[] temp = MFConfig.F[i].split("_");
			String v = temp[1]+"_"+temp[2]+temp[0];
			if(TableSchema.isAttributeInt(MFConfig.F[i].split("_")[0])){
				arraylistf_body.decl(cm.INT, v, JExpr.ref("Integer").invoke("valueOf").arg(JExpr.ref("mfs").ref(MFConfig.F[i])));
			}else{
				if(MFConfig.F[i].split("_")[1].equals("sum")||MFConfig.F[i].split("_")[1].equals("count")||MFConfig.F[i].split("_")[1].equals("avg")){
					arraylistf_body.decl(cm.INT, v, JExpr.ref("Integer").invoke("valueOf").arg(JExpr.ref("mfs").ref(MFConfig.F[i])));
				}else{
					arraylistf_body.decl(cm.parseType("String"), v, JExpr.ref("Integer").invoke("valueOf").arg(JExpr.ref("mfs").ref(MFConfig.F[i])));
				}
			}
		}
		
		//render such that clause
		for(int i=0;i<MFConfig.N;i++){
			JConditional such_that_if = arraylistf_body._if(JExpr.direct(MFConfig.O[i]));
			JBlock such_that_if_then = such_that_if._then();
			
			//pass the such that predicate
			JConditional inner_scan_round_if = such_that_if_then._if(JExpr.ref("i").eq(JExpr.lit(i+1)));
			String[] aggr_funcs = MFConfig.getAggreateFunctionByRound(i+1);
			for(int k=0;k<aggr_funcs.length;k++){
				if(aggr_funcs[k].indexOf("sum")!=-1){
					inner_scan_round_if._then().assign(JExpr.ref("mfs").ref(aggr_funcs[k]), JExpr.ref("mfs").ref(aggr_funcs[k]).plus(JExpr.ref("Integer").invoke("valueOf").arg(JExpr.ref("rs").invoke("getString").arg(aggr_funcs[k].split("_")[0]))));
				}
				if(aggr_funcs[k].indexOf("count")!=-1){
					inner_scan_round_if._then().assign(JExpr.ref("mfs").ref(aggr_funcs[k]), JExpr.ref("mfs").ref(aggr_funcs[k]).plus(JExpr.lit(1)));
				}
				if(aggr_funcs[k].indexOf("avg")!=-1){
					String[] temp = aggr_funcs[k].split("_");
					temp[1] = "sum";
					String sum = MFConfig.concatenate(temp, "_");
					temp[1] = "count";
					String count = MFConfig.concatenate(temp, "_");
					inner_scan_round_if._then().assign(JExpr.ref("mfs").ref(aggr_funcs[k]), JExpr.ref("mfs").ref(sum).div(JExpr.ref("mfs").ref(count)));
				}
				
				if(aggr_funcs[k].indexOf("max")!=-1 || aggr_funcs[k].indexOf("min")!=-1){
					JConditional aggreate_function_null_if = inner_scan_round_if._then()._if(JExpr.ref("mfs").ref(aggr_funcs[k]).eq(JExpr.ref("null")));
					JBlock aggreate_function_null_if_then = aggreate_function_null_if._then();
					JBlock aggreate_function_null_if_else = aggreate_function_null_if._else();
					
					if(aggr_funcs[k].indexOf("max")!=-1){
						aggreate_function_null_if_then.assign(JExpr.ref("mfs").ref(aggr_funcs[k]), JExpr.ref("rs").invoke("getString").arg(aggr_funcs[k].split("_")[0]));
						JConditional max_if = aggreate_function_null_if_else._if(JExpr.ref("rs").invoke("getString").arg(aggr_funcs[k].split("_")[0]).invoke("compareTo").arg(JExpr.ref("mfs").ref(aggr_funcs[k])).gt(JExpr.lit(0)));
						max_if._then().assign(JExpr.ref("mfs").ref(aggr_funcs[k]), JExpr.ref("rs").invoke("getString").arg(aggr_funcs[k].split("_")[0]));
					}
					if(aggr_funcs[k].indexOf("min")!=-1){
						aggreate_function_null_if_then.assign(JExpr.ref("mfs").ref(aggr_funcs[k]), JExpr.ref("rs").invoke("getString").arg(aggr_funcs[k].split("_")[0]));
						JConditional min_if = aggreate_function_null_if_else._if(JExpr.ref("rs").invoke("getString").arg(aggr_funcs[k].split("_")[0]).invoke("compareTo").arg(JExpr.ref("mfs").ref(aggr_funcs[k])).lt(JExpr.lit(0)));
						min_if._then().assign(JExpr.ref("mfs").ref(aggr_funcs[k]), JExpr.ref("rs").invoke("getString").arg(aggr_funcs[k].split("_")[0]));
					}
				}
			}
		}
		
		//having clause (remove element in ArrayList which not satisfy having expression)
		if(MFConfig.G!=null){
			JVar arraylist_len = mainBody.decl(cm.INT, "arraylist_len", JExpr.ref("mfs_arraylist").invoke("size"));
			JForLoop havingArrayListLoop = mainBody._for();
			havingArrayListLoop.init(cm.INT,"z", JExpr.lit(0)); 
			havingArrayListLoop.test(JExpr.ref("z").lt(arraylist_len));
			havingArrayListLoop.update(JExpr.ref("z").incr());
			JBlock havingf_body = havingArrayListLoop.body();
			
			havingf_body.assign(JExpr.ref("mfs"), JExpr.ref("mfs_arraylist").invoke("get").arg(JExpr.ref("z")));
			for(int i=0;i<MFConfig.V.length;i++){
				if(TableSchema.isAttributeInt(MFConfig.V[i])){
					havingf_body.decl(cm.INT, MFConfig.V[i], JExpr.ref("Integer").invoke("valueOf").arg(JExpr.ref("mfs").ref(MFConfig.V[i])));
				}else{
					havingf_body.decl(cm.parseType("String"), MFConfig.V[i], JExpr.ref("mfs").ref(MFConfig.V[i]));
				}
			}
			for(int i=0;i<MFConfig.F.length;i++){
				if(TableSchema.isAttributeInt(MFConfig.F[i].split("_")[0])){
					havingf_body.decl(cm.INT, MFConfig.F[i], JExpr.ref("Integer").invoke("valueOf").arg(JExpr.ref("mfs").ref(MFConfig.F[i])));
				}else{
					if(MFConfig.F[i].split("_")[1].equals("sum")||MFConfig.F[i].split("_")[1].equals("count")||MFConfig.F[i].split("_")[1].equals("avg")){
						havingf_body.decl(cm.INT, MFConfig.F[i], JExpr.ref("Integer").invoke("valueOf").arg(JExpr.ref("mfs").ref(MFConfig.F[i])));
					}else{
						havingf_body.decl(cm.parseType("String"), MFConfig.F[i], JExpr.ref("Integer").invoke("valueOf").arg(JExpr.ref("mfs").ref(MFConfig.F[i])));
					}
						
				}
			}
			JConditional having_if = havingf_body._if(JExpr.direct(MFConfig.G));
			having_if._else().add(JExpr.ref("mfs_arraylist").invoke("remove").arg(JExpr.ref("z")));
			having_if._else().assign(arraylist_len, arraylist_len.minus(JExpr.lit(1)));
			having_if._else().assign(JExpr.ref("z"), JExpr.ref("z").minus(JExpr.lit(1)));
		}
		
		//render mfs_arraylist
		String table_header = "";
		for(int x=0; x<MFConfig.S.length; x++){
			String temp = String.format("%-"+MFConfig.S[x].length()+"s", MFConfig.S[x]);
			table_header += temp + "\t";
		}
		mainBody.add(cm.ref(System.class).staticRef("out").invoke("println").arg(table_header));
		
		JForLoop renderForLoop = mainBody._for();
		renderForLoop.init(cm.INT,"z", JExpr.lit(0)); 
		renderForLoop.test(JExpr.ref("z").lt(JExpr.ref("mfs_arraylist").invoke("size")));
		renderForLoop.update(JExpr.ref("z").incr());
		JBlock renderf_body = renderForLoop.body();
		JVar output_row = renderf_body.decl(cm.parseType("String"), "output_row", JExpr.lit(""));
		for(int x=0; x<MFConfig.S.length; x++){
			String af;
			if(MFConfig.S[x].indexOf("_")!=-1){
				String[] temp = MFConfig.S[x].split("_");
				String t = temp[0];
				temp[0] = temp[2];
				temp[2] = t;
				af = MFConfig.concatenate(temp, "_");
			}else{
				af = MFConfig.S[x];
			}
			String attr = af.split("_")[0];
			if(TableSchema.isAttributeInt(attr)){
				renderf_body.assign(output_row, output_row.plus(JExpr.ref("String").invoke("format").arg("%"+af.length()+"s").arg(JExpr.ref("mfs_arraylist").ref("get(z)").ref(af))).plus(JExpr.lit("\t")));
			}else{
				renderf_body.assign(output_row, output_row.plus(JExpr.ref("String").invoke("format").arg("%-"+af.length()+"s").arg(JExpr.ref("mfs_arraylist").ref("get(z)").ref(af))).plus(JExpr.lit("\t")));
			}
		}
		
		renderf_body.add(cm.ref(System.class).staticRef("out").invoke("println").arg(output_row));
		
		//###### end generate main method ##########
		
		cm.build(destDir2);
	}

}
