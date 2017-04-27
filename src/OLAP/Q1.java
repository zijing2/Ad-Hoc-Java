
package OLAP;

import java.sql.ResultSet;
import java.util.ArrayList;

public class Q1 {

    public static ArrayList<OLAP.MFStructure> mfs_arraylist = new ArrayList<OLAP.MFStructure>();

    public static void main(String[] args) {
        MFStructure mfs;
        String topologicalSeq = "012";
        for (int r = 0; (r<= 2); r ++) {
            int i = Integer.parseInt((""+ topologicalSeq.charAt(r)));
            try {
                ResultSet rs = Data.getSalesRow();
                outer:
                while (rs.next()) {
                    if (i == 0) {
                        for (int j = 0; (j<mfs_arraylist.size()); j ++) {
                            if (mfs_arraylist.get(j).prod.equals(rs.getString("prod"))&&(mfs_arraylist.get(j).quant == Integer.parseInt(rs.getString("quant")))) {
                                continue outer;
                            }
                        }
                        mfs = new MFStructure();
                        mfs.prod = rs.getString("prod");
                        mfs.quant = Integer.valueOf(rs.getString("quant"));
                        mfs_arraylist.add(mfs);
                    } else {
                        for (int j = 0; (j<mfs_arraylist.size()); j ++) {
                            mfs = mfs_arraylist.get(j);
                            String _1prod = rs.getString("prod");
                            String _2prod = rs.getString("prod");
                            int _1month = Integer.valueOf(rs.getString("month"));
                            int _2month = Integer.valueOf(rs.getString("month"));
                            int _1year = Integer.valueOf(rs.getString("year"));
                            int _2year = Integer.valueOf(rs.getString("year"));
                            String _1state = rs.getString("state");
                            String _2state = rs.getString("state");
                            int _1quant = Integer.valueOf(rs.getString("quant"));
                            int _2quant = Integer.valueOf(rs.getString("quant"));
                            String _1cust = rs.getString("cust");
                            String _2cust = rs.getString("cust");
                            int _1day = Integer.valueOf(rs.getString("day"));
                            int _2day = Integer.valueOf(rs.getString("day"));
                            String prod = mfs.prod;
                            int quant = Integer.valueOf(mfs.quant);
                            int sum_1quant = Integer.valueOf(mfs.quant_sum_1);
                            int count_1quant = Integer.valueOf(mfs.quant_count_1);
                            int avg_1quant = Integer.valueOf(mfs.quant_avg_1);
                            int sum_2quant = Integer.valueOf(mfs.quant_sum_2);
                            int count_2quant = Integer.valueOf(mfs.quant_count_2);
                            int avg_2quant = Integer.valueOf(mfs.quant_avg_2);
                            int count_1prod = Integer.valueOf(mfs.prod_count_1);
                            int count_2prod = Integer.valueOf(mfs.prod_count_2);
                            if ((_1prod.compareTo(prod)==0 )) {
                                if (i == 1) {
                                    mfs.quant_sum_1 = (mfs.quant_sum_1 + Integer.valueOf(rs.getString("quant")));
                                    mfs.quant_count_1 = (mfs.quant_count_1 + 1);
                                    mfs.quant_avg_1 = (mfs.quant_sum_1 /mfs.quant_count_1);
                                    mfs.prod_count_1 = (mfs.prod_count_1 + 1);
                                }
                            }
                            if ((_2prod.compareTo(prod)==0 &&_2quant<quant)) {
                                if (i == 2) {
                                    mfs.quant_sum_2 = (mfs.quant_sum_2 + Integer.valueOf(rs.getString("quant")));
                                    mfs.quant_count_2 = (mfs.quant_count_2 + 1);
                                    mfs.quant_avg_2 = (mfs.quant_sum_2 /mfs.quant_count_2);
                                    mfs.prod_count_2 = (mfs.prod_count_2 + 1);
                                }
                            }
                        }
                    }
                }
            } catch (Exception _x) {
            }
        }
        Data.getSalesRow();
        int arraylist_len = mfs_arraylist.size();
        for (int z = 0; (z<arraylist_len); z ++) {
            mfs = mfs_arraylist.get(z);
            String prod = mfs.prod;
            int quant = Integer.valueOf(mfs.quant);
            int quant_sum_1 = Integer.valueOf(mfs.quant_sum_1);
            int quant_count_1 = Integer.valueOf(mfs.quant_count_1);
            int quant_avg_1 = Integer.valueOf(mfs.quant_avg_1);
            int quant_sum_2 = Integer.valueOf(mfs.quant_sum_2);
            int quant_count_2 = Integer.valueOf(mfs.quant_count_2);
            int quant_avg_2 = Integer.valueOf(mfs.quant_avg_2);
            int prod_count_1 = Integer.valueOf(mfs.prod_count_1);
            int prod_count_2 = Integer.valueOf(mfs.prod_count_2);
            if ((prod_count_2 == prod_count_1 / 2)) {
            } else {
                mfs_arraylist.remove(z);
                arraylist_len = (arraylist_len- 1);
                z = (z- 1);
            }
        }
        System.out.println("prod\tquant\t1_avg_quant\t2_avg_quant\t");
        for (int z = 0; (z<mfs_arraylist.size()); z ++) {
            String output_row = "";
            output_row = ((output_row + String.format("%-4s", mfs_arraylist.get(z).prod))+"\t");
            output_row = ((output_row + String.format("%5s", mfs_arraylist.get(z).quant))+"\t");
            output_row = ((output_row + String.format("%11s", mfs_arraylist.get(z).quant_avg_1))+"\t");
            output_row = ((output_row + String.format("%11s", mfs_arraylist.get(z).quant_avg_2))+"\t");
            System.out.println(output_row);
        }
    }

}
