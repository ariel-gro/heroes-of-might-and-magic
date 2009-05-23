package tau.heroes.net;

public class ErrorMessage extends AsyncMessage
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String text;
	
	public ErrorMessage(String text)
	{
		this.text = text;
	}
	
	public String getText()
	{
		return text;
	}
}
