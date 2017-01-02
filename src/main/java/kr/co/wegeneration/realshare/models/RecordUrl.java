package kr.co.wegeneration.realshare.models;

/**
 * Created by User on 2015-09-30.
 */
public class RecordUrl {


    private String reg_date;
    private String record_url;
    private String user_id;

    public RecordUrl(String user_id, String record_url, String reg_date) {
        this.user_id = user_id;
        this.record_url = record_url;
        this.reg_date = reg_date;
    }
    public void setReg_date() {
        this.reg_date = reg_date;
    }

    public void setRecord_url(String record_url) {
        this.record_url = record_url;
    }


    public String getReg_date () {
        return reg_date;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public String getRecord_url() {
        return record_url;
    }

    public String getUserId() {
        return user_id;
    }

}
