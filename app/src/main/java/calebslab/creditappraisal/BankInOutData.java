package calebslab.creditappraisal;

public class BankInOutData {

    public String yyyymm = "";
    public int iInAmt = 0;
    public int iOutAmt = 0;

    public BankInOutData(String yyyymm, int iInAmt, int iOutAmt){
        this.yyyymm = yyyymm;
        this.iInAmt = iInAmt;
        this.iOutAmt = iOutAmt;
    }

    public int getInAmt() {
        return iInAmt;
    }

    public int getOutAmt() {
        return iOutAmt;
    }

    public String getDate() {
        return yyyymm;
    }
}
