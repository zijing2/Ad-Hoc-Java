
package OLAP;

import java.sql.ResultSet;
import java.util.ArrayList;

public class Q1 {

    public static ArrayList<MFStructure> mfs_arraylist = new ArrayList<MFStructure>();

    public static void main(String[] args) {
        MFStructure mfs;
        String topologicalSeq = "0132";
        for (int r = 0; (r<= 3); r ++) {
            int i = Integer.parseInt((""+ topologicalSeq.charAt(r)));
            try {
                ResultSet rs = Data.getSalesRow();
                outer:
                while (rs.next()) {
                    if (i == 0) {
                        for (int j = 0; (j<mfs_arraylist.size()); j ++) {
                            if (mfs_arraylist.get(j).prod.equals(rs.getString("prod"))&&(mfs_arraylist.get(j).month == Integer.parseInt(rs.getString("month")))) {
                                continue outer;
                            }
                        }
                        mfs = new MFStructure();
                        mfs.prod = rs.getString("prod");
                        mfs.month = Integer.valueOf(rs.getString("month"));
                        mfs_arraylist.add(mfs);
                    } else {
                        for (int j = 0; (j<mfs_arraylist.size()); j ++) {
                            mfs = mfs_arraylist.get(j);
                            String _1prod = rs.getString("prod");
                            String _2prod = rs.getString("prod");
                            String _3prod = rs.getString("prod");
                            int _1month = Integer.valueOf(rs.getString("month"));
                            int _2month = Integer.valueOf(rs.getString("month"));
                            int _3month = Integer.valueOf(rs.getString("month"));
                            int _1year = Integer.valueOf(rs.getString("year"));
                            int _2year = Integer.valueOf(rs.getString("year"));
                            int _3year = Integer.valueOf(rs.getString("year"));
                            String _1state = rs.getString("state");
                            String _2state = rs.getString("state");
                            String _3state = rs.getString("state");
                            int _1quant = Integer.valueOf(rs.getString("quant"));
                            int _2quant = Integer.valueOf(rs.getString("quant"));
                            int _3quant = Integer.valueOf(rs.getString("quant"));
                            String _1cust = rs.getString("cust");
                            String _2cust = rs.getString("cust");
                            String _3cust = rs.getString("cust");
                            int _1day = Integer.valueOf(rs.getString("day"));
                            int _2day = Integer.valueOf(rs.getString("day"));
                            int _3day = Integer.valueOf(rs.getString("day"));
                            String prod = mfs.prod;
                            int month = Integer.valueOf(mfs.month);
                            int sum_1quant = Integer.valueOf(mfs.quant_sum_1);
                            int count_1quant = Integer.valueOf(mfs.quant_count_1);
                            int avg_1quant = Integer.valueOf(mfs.quant_avg_1);
                            int count_2quant = Integer.valueOf(mfs.quant_count_2);
                            int sum_3quant = Integer.valueOf(mfs.quant_sum_3);
                            int count_3quant = Integer.valueOf(mfs.quant_count_3);
                            int avg_3quant = Integer.valueOf(mfs.quant_avg_3);
                            if ((_1prod.compareTo(prod)==0 &&_1month==month-1)) {
                                if (i == 1) {
                                    mfs.quant_sum_1 = (mfs.quant_sum_1 + Integer.valueOf(rs.getString("quant")));
                                    mfs.quant_count_1 = (mfs.quant_count_1 + 1);
                                    mfs.quant_avg_1 = (mfs.quant_sum_1 /mfs.quant_count_1);
                                }
                            }
                            if ((_2prod.compareTo(prod)==0 &&_2month==month&&_2quant>avg_1quant&&_2quant<avg_3quant)) {
                                if (i == 2) {
                                    mfs.quant_count_2 = (mfs.quant_count_2 + 1);
                                }
                            }
                            if ((_3prod.compareTo(prod)==0 &&_3month==month+1)) {
                                if (i == 3) {
                                    mfs.quant_sum_3 = (mfs.quant_sum_3 + Integer.valueOf(rs.getString("quant")));
                                    mfs.quant_count_3 = (mfs.quant_count_3 + 1);
                                    mfs.quant_avg_3 = (mfs.quant_sum_3 /mfs.quant_count_3);
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
            int month = Integer.valueOf(mfs.month);
            int quant_sum_1 = Integer.valueOf(mfs.quant_sum_1);
            int quant_count_1 = Integer.valueOf(mfs.quant_count_1);
            int quant_avg_1 = Integer.valueOf(mfs.quant_avg_1);
            int quant_count_2 = Integer.valueOf(mfs.quant_count_2);
            int quant_sum_3 = Integer.valueOf(mfs.quant_sum_3);
            int quant_count_3 = Integer.valueOf(mfs.quant_count_3);
            int quant_avg_3 = Integer.valueOf(mfs.quant_avg_3);
            if ((quant_avg_3 > quant_avg_1)) {
            } else {
                mfs_arraylist.remove(z);
                arraylist_len = (arraylist_len- 1);
                z = (z- 1);
            }
        }
        System.out.println("prod\tmonth\t1_avg_quant\t2_count_quant\t3_avg_quant\t");
        for (int z = 0; (z<mfs_arraylist.size()); z ++) {
            String output_row = "";
            output_row = ((output_row + String.format("%-4s", mfs_arraylist.get(z).prod))+"\t");
            output_row = ((output_row + String.format("%5s", mfs_arraylist.get(z).month))+"\t");
            output_row = ((output_row + String.format("%11s", mfs_arraylist.get(z).quant_avg_1))+"\t");
            output_row = ((output_row + String.format("%13s", mfs_arraylist.get(z).quant_count_2))+"\t");
            output_row = ((output_row + String.format("%11s", mfs_arraylist.get(z).quant_avg_3))+"\t");
            System.out.println(output_row);
        }
    }

}
