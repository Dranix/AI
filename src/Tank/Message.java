package Tank;

public class Message implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private MessageType MessageType;
	private Object MessageObject;
	
	public MessageType getMessageType() {
		return MessageType;
	}
	public void setMessageType(MessageType messageType) {
		MessageType = messageType;
	}
	public Object getMessageObject() {
		return MessageObject;
	}
	public void setMessageObject(Object messageObject) {
		MessageObject = messageObject;
	}
	
	public Message(MessageType messageType, Object messageObject) {
		super();
		MessageType = messageType;
		MessageObject = messageObject;
	}
}

