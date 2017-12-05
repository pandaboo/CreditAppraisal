package calebslab.creditappraisal;

/**
 * Created by JH on 2017-12-04.
 */

public class BankInOutData {

    public String yyyymm = "";
    public int iInAmt = 0;
    public int iOutAmt = 0;

    public BankInOutData(String yyyymm, int iInAmt, int iOutAmt){
        this.yyyymm = yyyymm;
        this.iInAmt = iInAmt;
        this.iOutAmt = iOutAmt;
    }
}
