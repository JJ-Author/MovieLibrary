package jffsss.service.opensubtitles;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jffsss.util.d.D;
import jffsss.util.d.DObject;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class OpenSubtitlesAPI
{
	private static final String USER_AGENT = "OS Test User Agent";
	private static final String END_POINT = "http://api.opensubtitles.org/xml-rpc";
	private static final String LANGUAGE = "en";

	private XmlRpcClient _Client;

	public OpenSubtitlesAPI()
	{
		this._Client = new XmlRpcClient();
		XmlRpcClientConfigImpl _Config = new XmlRpcClientConfigImpl();
		try
		{
			_Config.setServerURL(new URL(END_POINT));
		}
		catch (MalformedURLException e)
		{}
		this._Client.setConfig(_Config);
	}

	public String requestLogIn(String _Username, String _Password) throws IOException
	{
		List<String> _Params = new ArrayList<String>();
		_Params.add(_Username);
		_Params.add(_Password);
		_Params.add(LANGUAGE);
		_Params.add(USER_AGENT);
		DObject _Response = this.executeAPI("LogIn", _Params);
		String _Token = _Response.asMap().get("token").asString();
		if (_Token == null)
			throw new IOException("OpenSubtitles login error: token is null");
		return _Token;
	}

	public void requestLogOut(String _Token) throws IOException
	{
		List<String> _Params = new ArrayList<String>();
		_Params.add(_Token);
		this.executeAPI("LogOut", _Params);
	}

	public DObject requestCheckMovieHash2(String _Token, List<String> _Hashes) throws IOException
	{
		List<Object> _Params = new ArrayList<Object>();
		_Params.add(_Token);
		_Params.add(_Hashes);
		DObject _Response = executeAPI("CheckMovieHash2", _Params);
		return _Response.asMap().get("data");
	}

	private DObject executeAPI(String _Method, List<?> _Params) throws IOException
	{
		try
		{
			DObject _Response = D.fromXmlRpc(this._Client.execute(_Method, _Params));
			StatusLine _StatusLine = StatusLine.fromStatusCode(getStatusCode(_Response.asMap().get("status").asString()));
			if (!_StatusLine.isSuccess())
				throw new IOException(_StatusLine.toString());
			return _Response;
		}
		catch (Exception e)
		{
			throw new IOException("OpenSubtitles API error: " + _Method, e);
		}
	}

	private static String getStatusCode(String _String)
	{
		return _String.split(" ", 2)[0];
	}
}