package jffsss.service.google;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import jffsss.util.d.DList;
import jffsss.util.d.DMap;
import jffsss.util.d.DObject;
import jffsss.util.d.DString;

public class GoogleAPI
{
	public GoogleAPI()
	{}

	public DObject requestSearch(String _Query, Integer _Page, Integer _Count) throws IOException
	{
		if (_Query == null || _Query.isEmpty())
			throw new IllegalArgumentException("\"_Query\" may not be null or empty.");
		if (_Page == null)
			_Page = 0;
		if (_Count == null)
			_Count = 10;
		String _URL = MessageFormat.format("https://www.google.com/search?q={0}&start={1}&num={2}", URLEncoder.encode(_Query, "UTF-8"), _Page, _Count);
		return parseRequestSearchResponse(this.executeAPI(_URL));
	}

	private static DObject parseRequestSearchResponse(Document _Response)
	{
		List<DObject> _Result = new ArrayList<DObject>();
		for (Element _ResponseTag : _Response.select("p:has(a)"))
		{
			try
			{
				Map<String, DObject> _ResultMap = new HashMap<String, DObject>();
				_ResultMap.put("Title", new DString(_ResponseTag.text()));
				Element _ResponseTagTag = _ResponseTag.nextElementSibling().select("td.j > font").first();
				{
					String _Text = _ResponseTagTag.getElementsByAttributeValue("color", "green").first().text();
					if (_Text.contains(" "))
						_Text = _Text.substring(0, _Text.indexOf(" "));
					_ResultMap.put("Link", new DString(_Text));
				}
				_ResponseTagTag.getElementsByTag("font").remove();
				_ResultMap.put("Snippet", new DString(_ResponseTagTag.text()));
				_Result.add(new DMap(_ResultMap));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return new DList(_Result);
	}

	private Document executeAPI(String _URL) throws IOException
	{
		HttpURLConnection _Connection = (HttpURLConnection) (new URL(_URL)).openConnection();
		try
		{
			_Connection.setDoOutput(false);
			_Connection.setDoInput(true);
			_Connection.setRequestMethod("GET");
			_Connection.setRequestProperty("User-Agent", "Mozilla/4.0");
			_Connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			_Connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
			InputStream _InputStream = _Connection.getInputStream();
			try
			{
				return Jsoup.parse(_InputStream, null, _URL);
			}
			finally
			{
				_InputStream.close();
			}
		}
		finally
		{
			_Connection.disconnect();
		}
	}
}