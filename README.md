# Ad-Hoc-Java

This is a Extended-SQL evaluation simulation.

## GOAL

Standard-SQL has a drawback in group by clause which ties the group and aggregation function.
Extended-SQL can solve this problem by adding several grouping variables to define the range of
each group.

## DEPENDENCY  Libraries
1. JavaSE 1.8
2. postgresql-9.4.1211.jar
3. codemodel-2.6.jar ( a java template render engine, design base on DOM )

## DEPLOY

1. Download the source code from Repo: https://github.com/zijing2/Ad-Hoc-Java
2. Download the postgresql-9.4.1211.jar driver and codemodel-2.6.jar from git
3. Build path->Configure Build path->add Extend Jars and choose the two .jar package above
4. Start your postgreSQL service
5. Go to Driver.java and modify your SQL administration information at line 16~18

## USAGE

1. Suppose you are using eclipse to import the project.
2. Go to Proj.java choose the input method at line 35~36. Default is by reading file, you can 
   comment out line 36 and cancel the comment in line 35 to use prompt. Test case is in 
   src/TEST/test* 
3. Compile and run Proj.java and you will get a new MFStructure.java and Q1.java. Access 
   MFStructure.java first to avoid the eclipse cache. And then go to Q1.java.
4. Compile and run Q1.java, you will get the result on the console panel.

## PS

1. For the extra credit, I also finish the topological sort to solve the dependency of grouping  
   variable.
2. If you are annoying compiling and are interested in using interpretive language to solve the
   problem, it's welcome to download my javascript engine version of ESQL in 
   https://github.com/zijing2/Ad-Hoc( using Zijing_branch as trunk, trunk is broken). 
   But as Professor. Kim say, this is not what database do.
   Database always produce a plan, meaning a .java program, first, and then evaluate.



