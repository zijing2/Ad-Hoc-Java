
package OLAP;


public class MFStructure {

    public String prod;
    public int month;
    public int quant_sum_1;
    public int quant_count_1;
    public int quant_avg_1;
    public int quant_count_2;
    public int quant_sum_3;
    public int quant_count_3;
    public int quant_avg_3;

    public MFStructure getMFSInstance() {
        MFStructure mfs = new MFStructure();
        return mfs;
    }

}
