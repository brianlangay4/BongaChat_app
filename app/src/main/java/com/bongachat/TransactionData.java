package com.example.bingamoney;

public class TransactionData {
    private String detail;
    private String trans_info1;
    private String trans_info2;
    private  String time;
    private byte[] image;

    public TransactionData(String detail, String trans_info1, String trans_info2, String time,byte[] image) {
        this.detail = detail;
        this.trans_info1 = trans_info1;
        this.trans_info2 = trans_info2;
        this.time = time;
        this.image = image;

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
    public byte[] getImage() {
        return image;
    }


}


