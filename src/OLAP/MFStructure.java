
package OLAP;


public class MFStructure {

    public String cust;
    public int month;
    public int quant_sum_1;
    public int quant_count_1;
    public int quant_avg_1;
    public int quant_sum_2;
    public int quant_count_2;
    public int quant_avg_2;
    public String quant_max_2;
    public String quant_min_2;

    public MFStructure getMFSInstance() {
        MFStructure mfs = new MFStructure();
        return mfs;
    }

}
