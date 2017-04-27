
package OLAP;

import java.sql.ResultSet;
import java.util.ArrayList;

public class Q1 {

    public static ArrayList<MFStructure> mfs_arraylist = new ArrayList<MFStructure>();

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
                            if (mfs_arraylist.get(j).cust.equals(rs.getString("cust"))&&(mfs_arraylist.get(j).month == Integer.parseInt(rs.getString("month")))) {
                                continue outer;
                            }
                        }
                        mfs = new MFStructure();
                        mfs.cust = rs.getString("cust");
                        mfs.month = Integer.valueOf(rs.getString("month"));
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
                            String cust = mfs.cust;
                            int month = Integer.valueOf(mfs.month);
                            if ((_1cust.compareTo(cust)==0 &&_1month>month)) {
                                if (i == 1) {
                                    mfs.quant_sum_1 = (mfs.quant_sum_1 + Integer.valueOf(rs.getString("quant")));
                                    mfs.quant_count_1 = (mfs.quant_count_1 + 1);
                                    mfs.quant_avg_1 = (mfs.quant_sum_1 /mfs.quant_count_1);
                                }
                            }
                            if ((_2cust.compareTo(cust)==0 &&_2month<month)) {
                                if (i == 2) {
                                    mfs.quant_sum_2 = (mfs.quant_sum_2 + Integer.valueOf(rs.getString("quant")));
                                    mfs.quant_count_2 = (mfs.quant_count_2 + 1);
                                    mfs.quant_avg_2 = (mfs.quant_sum_2 /mfs.quant_count_2);
                                    if (mfs.quant_max_2 == null) {
                                        mfs.quant_max_2 = rs.getString("quant");
                                    } else {
                                        if (rs.getString("quant").compareTo(mfs.quant_max_2)> 0) {
                                            mfs.quant_max_2 = rs.getString("quant");
                                        }
                                    }
                                    if (mfs.quant_min_2 == null) {
                                        mfs.quant_min_2 = rs.getString("quant");
                                    } else {
                                        if (rs.getString("quant").compareTo(mfs.quant_min_2)< 0) {
                                            mfs.quant_min_2 = rs.getString("quant");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception _x) {
            }
        }
        Data.getSalesRow();
        System.out.println("cust\tmonth\t1_sum_quant\t1_avg_quant\t1_count_quant\t2_sum_quant\t2_count_quant\t2_avg_quant\t");
        for (int z = 0; (z<mfs_arraylist.size()); z ++) {
            String output_row = "";
            output_row = ((output_row + String.format("%-4s", mfs_arraylist.get(z).cust))+"\t");
            output_row = ((output_row + String.format("%5s", mfs_arraylist.get(z).month))+"\t");
            output_row = ((output_row + String.format("%11s", mfs_arraylist.get(z).quant_sum_1))+"\t");
            output_row = ((output_row + String.format("%11s", mfs_arraylist.get(z).quant_avg_1))+"\t");
            output_row = ((output_row + String.format("%13s", mfs_arraylist.get(z).quant_count_1))+"\t");
            output_row = ((output_row + String.format("%11s", mfs_arraylist.get(z).quant_sum_2))+"\t");
            output_row = ((output_row + String.format("%13s", mfs_arraylist.get(z).quant_count_2))+"\t");
            output_row = ((output_row + String.format("%11s", mfs_arraylist.get(z).quant_avg_2))+"\t");
            System.out.println(output_row);
        }
    }

}
