
package OLAP;


public class MFStructure {

    public String prod;
    public int quant;
    public int quant_sum_1;
    public int quant_count_1;
    public int quant_avg_1;
    public int quant_sum_2;
    public int quant_count_2;
    public int quant_avg_2;
    public int prod_count_1;
    public int prod_count_2;

    public MFStructure getMFSInstance() {
        MFStructure mfs = new MFStructure();
        return mfs;
    }

}
