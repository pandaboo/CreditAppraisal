package calebslab.creditappraisal;

public class Message {

        private String sMessageId;
        private String sThreadId;
        private String sAddress;                 //휴대폰번호
        private String sContactId;
        private String sContactId_string;
        private String sTimestamp;              //시간
        private String sBody;                    //문자내용
        private int   iProtocol=0;
        private int   iType=0;

        public Message(){}

        public String getMessageId() {
            return sMessageId;
        }

        public void setMessageId(String messageId) {
            this.sMessageId = messageId;
        }

        public String getThreadId() {
            return sThreadId;
        }

        public void setThreadId(String threadId) {
            this.sThreadId = threadId;
        }

        public String getAddress() {
            return sAddress;
        }

        public void setAddress(String address) {
            this.sAddress = address;
        }

        public String getContactId() {
            return sContactId;
        }

        public void setContactId(String contactId) {
            this.sContactId = contactId;
        }

        public String getContactId_string() {
            return sContactId_string;
        }

        public void setContactId_string(String contactId_string) {
            this.sContactId_string = contactId_string;
        }

        public String getTimestamp() {
            return sTimestamp;
        }

        public void setTimestamp(String timestamp) {
            this.sTimestamp = timestamp;
        }

        public String getBody() {
            return sBody;
        }

        public void setBody(String body) {
            this.sBody = body;
        }

        public int getProtocol() {
            return iProtocol;
        }

        public void setProtocol(int protocol) {
            this.iProtocol = protocol;
        }

        public int getType() {
            return iType;
        }

        public void setType(int type) {
            this.iType = type;
        }
    }
