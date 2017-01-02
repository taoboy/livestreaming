package kr.co.wegeneration.realshare.models;

/**
 * Created by User on 2015-09-28.
 */
public class ActivityLog {



    private boolean requestSent;
    private long timelog;
    private String activityLog;
    private String read_yn;
    private String log_id;
    private String user_id;

    public ActivityLog(String log_id, String activityLog, long timelog, Boolean requestSent, String read_yn, String user_id) {
        this.log_id = log_id;
        this.activityLog = activityLog;
        this.timelog = timelog;
        this.requestSent = requestSent;
        this.read_yn  =read_yn;
        this.user_id  =user_id;
    }
    public boolean isrequestSent() {
        return requestSent;
    }

    public void setrequestSent(boolean requestSent) {
        this.requestSent = requestSent;
    }


    public long getTimelog() {
        return timelog;
    }

    public void setTimelog(long timelog) {
        this.timelog = timelog;
    }

    public String getActivityLog() {
        return activityLog;
    }

    public void setActivityLog(String activityLog) {
        this.activityLog = activityLog;
    }

    public String getRead_yn() {
        return read_yn;
    }

    public void setRead_yn(String read_yn) {
        this.read_yn = read_yn;
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
