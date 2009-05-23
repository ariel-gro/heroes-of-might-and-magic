package tau.heroes.net;

import tau.heroes.db.UserInfo;

public class LoginOKMessage extends AsyncMessage
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private UserInfo userInfo;
	
	public LoginOKMessage(UserInfo userInfo)
	{
		this.userInfo = userInfo;
	}
	
	public UserInfo getUserInfo()
	{
		return userInfo;
	}
}
