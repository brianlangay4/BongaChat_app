package com.example.bingamoney;

public class TransactionData2 {
    private String detail;
    private String trans_info1;
    private String trans_info2;
    //private byte[] image;

    private  String time;

    public TransactionData2(String detail, String trans_info1, String trans_info2, String time) {
        this.detail = detail;
        this.trans_info1 = trans_info1;
        this.trans_info2 = trans_info2;
        this.time = time;

    }

    public String getDetail() {
        return detail;
    }

    public String getTrans_info1() {
        return trans_info1;
    }

    public String getTrans_info2() {
        return trans_info2;
    }
    public String getTime() {
        return time;
    }


}


