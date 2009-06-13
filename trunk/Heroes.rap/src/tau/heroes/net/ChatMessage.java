package tau.heroes.net;

public class ChatMessage extends AsyncMessage
{
	private static final long serialVersionUID = 1L;
	private String text;
		
		public ChatMessage(String text)
		{
			this.text = text;
		}
		
		public String getText()
		{
			return text;
		}
}
