package net.pushover.client;

/**
 * 
 * @author Sean Scanlon <sean.scanlon@gmail.com>
 * 
 * @since Dec 18, 2012
 */
public class PushoverMessage {

    private String apiToken;

    private String userId;

    private String message;

    private String device;

    private String title;

    private String url;

    private String titleForURL;

    private MessagePriority priority = MessagePriority.NORMAL;

    private Long timestamp;

    private String sound;

    private PushoverMessage() {
        // use the builder
    }

    public static Builder builderWithApiToken(String token) {
        return new Builder().setApiToken(token);
    }

    public static class Builder {

        private PushoverMessage msg;

        public Builder() {
            msg = new PushoverMessage();
        }

        public PushoverMessage build() {
            // TODO: validate message!
            return msg;
        }

        /**
         * (required) - your application's API token
         */
        public Builder setApiToken(String apiToken) {
            msg.apiToken = apiToken;
            return this;
        }

        /**
         * (required) - the user key (not e-mail address) of your user (or you), viewable when
         * logged into the dashboard
         */
        public Builder setUserId(String userId) {
            msg.userId = userId;
            return this;
        }

        /**
         * (required) - your message
         */
        public Builder setMessage(String message) {
            msg.message = message;
            return this;
        }

        /**
         * (optional) - your user's device identifier to send the message directly to that device,
         * rather than all of the user's devices
         */
        public Builder setDevice(String device) {
            msg.device = device;
            return this;
        }

        /**
         * (optional) - your message's title, otherwise uses your app's name
         */
        public Builder setTitle(String title) {
            msg.title = title;
            return this;
        }

        /**
         * (optional) - a supplementary URL to show with your message
         */
        public Builder setUrl(String url) {
            msg.url = url;
            return this;
        }

        /**
         * (optional) - a title for your supplementary URL
         */
        public Builder setTitleForURL(String titleForURL) {
            msg.titleForURL = titleForURL;
            return this;
        }

        /**
         * (optional) - set to MessagePriority.HIGH to display as high-priority and bypass quiet
         * hours, or MessagePriority.QUIET to always send as a quiet notification
         */
        public Builder setPriority(MessagePriority priority) {
            msg.priority = priority;
            return this;
        }

        /**
         * (optional) - set to a Unix timestamp to have your message show with a particular time,
         * rather than now
         */
        public Builder setTimestamp(Long timestamp) {
            msg.timestamp = timestamp;
            return this;
        }

        /**
         * (optional) - set to the name of one of the sounds supported by device clients to override
         * the user's default sound choice
         */
        public Builder setSound(String sound) {
            msg.sound = sound;
            return this;
        }
    }

    public String getApiToken() {
        return apiToken;
    }

    public String getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public String getDevice() {
        return device;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getTitleForURL() {
        return titleForURL;
    }

    public MessagePriority getPriority() {
        return priority;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getSound() {
        return sound;
    }

}
