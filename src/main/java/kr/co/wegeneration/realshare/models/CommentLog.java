package kr.co.wegeneration.realshare.models;

import java.sql.Timestamp;

/**
 * Created by User on 2015-09-28.
 */
public class CommentLog {



    private Long reg_date;
    private String comment;
    private String user_id;
    private String user_name;
    private String log_id;

    public CommentLog(String log_id,String user_id, String user_name, String comment, Long reg_date) {
        this.log_id = log_id;
        this.user_id = user_id;
        this.user_name = user_name;
        this.comment = comment;
        this.reg_date = reg_date;
    }
    public Long getReg_Date() {
        return reg_date;
    }

    public void setReg_date(boolean requestSent) {
        this.reg_date = reg_date;
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getLog_id() {
        return log_id;
    }

    public void setLog_id(String log_id) {
        this.log_id = log_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


}
