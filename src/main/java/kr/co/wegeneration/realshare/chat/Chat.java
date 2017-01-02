package kr.co.wegeneration.realshare.chat;

/**
 * @author greg
 * @since 6/21/13
 */
public class Chat {

    private String message;
    private String author;
    private long timestamp;
    private String messageType;
    private String voicePath;
    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private Chat() {
    }

    public Chat(String message, String author, long timestamp, String messageType) {
        this.message = message;
        this.author = author;
        this.timestamp = timestamp;
        this.messageType = messageType;
    }

    public Chat(String message, String author, long timestamp, String messageType, String voicePath) {
        this.message = message;
        this.author = author;
        this.timestamp = timestamp;
        this.messageType = messageType;
        this.voicePath = voicePath;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }

    public String getVoicePath() {
        return voicePath;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getMessageType() {
        return messageType;
    }

}


