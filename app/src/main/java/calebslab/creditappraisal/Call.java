package calebslab.creditappraisal;

/**
 * 작성자 : 김기훈
 * 내용 : 전화내역 관련 클래스
 * inComing : 수신
 * outGoing : 발신
 */

public class Call {

    private int inComing;
    private int outGoing;
    private int inDuration;
    private int outDuration;

    public Call(int inComing, int outGoing, int inDuration, int outDuration) {
        this.inComing = inComing;
        this.outGoing = outGoing;
        this.inDuration = inDuration;
        this.outDuration = outDuration;
    }

    public int getInDuration() {
        return inDuration;
    }

    public void setInDuration(int inDuration) {
        this.inDuration += inDuration;
    }

    public int getOutDuration() {
        return outDuration;
    }

    public void setOutDuration(int outDuration) {
        this.outDuration += outDuration;
    }

    public int getInComing() {
        return inComing;
    }

    public void setInComing(int inComing) {
        this.inComing = inComing;
    }

    public int getOutGoing() {
        return outGoing;
    }

    public void setOutGoing(int outGoing) {
        this.outGoing = outGoing;
    }
}
