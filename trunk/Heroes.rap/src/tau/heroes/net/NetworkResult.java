package tau.heroes.net;

public class NetworkResult<T>
{
	private T result;
	private String errorMessage;
	
	public NetworkResult(T result)
	{
		this.result = result;
	}
	
	public NetworkResult(T result, String errorMessage)
	{
		this (result);
		this.errorMessage = errorMessage;
	}
	
	public T getResult()
	{
		return result;
	}
	
	public String getErrorMessage()
	{
		return errorMessage;
	}
}
