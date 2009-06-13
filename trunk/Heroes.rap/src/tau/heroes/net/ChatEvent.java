package tau.heroes.net;


public class ChatEvent {
	private ChatMessage chatMessage;
	
	public ChatEvent(ChatMessage chatMessage) {
		this.chatMessage = chatMessage;
	}
	
	public ChatMessage getChatMessage() {
		return chatMessage;
	}
}
