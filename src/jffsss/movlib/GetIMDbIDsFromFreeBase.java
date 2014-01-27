package jffsss.movlib;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jffsss.api.FreeBaseAPI;
import jffsss.util.Utils;
import jffsss.util.d.DObject;

import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskExecutionException;
import jffsss.util.FileNameCleaner;

public class GetIMDbIDsFromFreeBase extends Task<Map<String, Double>>
{
	private VideoFileInfo _VideoFileInfo;

	public GetIMDbIDsFromFreeBase(VideoFileInfo _VideoFileInfo)
	{
		this._VideoFileInfo = _VideoFileInfo;
	}

	@Override
	public Map<String, Double> execute() throws TaskExecutionException
	{
		try
		{
			FreeBaseAPI _API = new FreeBaseAPI();
			String langs = "de,en";
			//Exakte Suche
			DObject _Response = _API.requestSearch(true, null, "(all name{full}:\"" + tokenizeFileName(this._VideoFileInfo.getFileInfo()) + "\" type:/film/film)", "(key:/authority/imdb/title/)", 5, langs);
			//falls kein ergebnis dann --> reihenfolge erhaltende Suche der Wörter
			if (HasNoHit(_Response))
					{
						_Response = _API.requestSearch(true, null, "(all name{phrase}:\"" + tokenizeFileName(this._VideoFileInfo.getFileInfo()) + "\" type:/film/film)", "(key:/authority/imdb/title/)", 5, langs);
						System.out.println(tokenizeFileName(this._VideoFileInfo.getFileInfo())+": NO HITS full");
					}
			//falls immer noch kein ergebnis dann suche in jedem film auf jedem schlüssel danach
			if (HasNoHit(_Response))
					{
						_Response = _API.requestSearch(true, "\""+tokenizeFileName(this._VideoFileInfo.getFileInfo())+"\"", "(all type:/film/film)", "(key:/authority/imdb/title/)", 5, langs);
						System.out.println(tokenizeFileName(this._VideoFileInfo.getFileInfo())+": NO HITS phrase");
					}
			if (HasNoHit(_Response))
			{
				System.out.println(tokenizeFileName(this._VideoFileInfo.getFileInfo())+": NO HITS query");
			}
			return parseResponse(_Response);
		}
		catch (Exception e)
		{
			throw new TaskExecutionException(e);
		}
	}

	private static Map<String, Double> parseResponse(DObject _Response)
	{
		Map<String, Double> _ResultMap = new HashMap<String, Double>();
		if (_Response != null)
			try
			{
				List<DObject> _ResponseMapList = _Response.asMap().get("result").asList();
				for (DObject _ResponseMapListElement : _ResponseMapList)
					try
					{
						Map<String, DObject> _ResponseMapListMap = _ResponseMapListElement.asMap();
						String _IMDbID = _ResponseMapListMap.get("output").asMap().get("key:/authority/imdb/title/").asMap().get("/type/object/key").asList().get(0).asString().substring(24);
						Double _Factor = _ResponseMapListMap.get("score").parseAsDouble(1.0);
						_ResultMap.put(_IMDbID, _Factor);
					}
					catch (Exception e)
					{}
			}
			catch (Exception e)
			{
				throw new RuntimeException("FreeBaseParse");
			}
		return _ResultMap;
	}
	
	private static boolean HasNoHit(DObject _Response)
	{
		//System.out.print("#HITS:  ");
		if (_Response != null)
			try
			{
				int hits = _Response.asMap().get("hits").parseAsInteger();
				//System.out.println(hits);
				if (hits!=0)
					return false;
			}
			catch (Exception e)
			{
				throw new RuntimeException("FreeBaseParse");
			}
		return true;
	}
	
	public static String tokenizeFileName(FileInfo _FileInfo)
	{
		FileNameCleaner fnc = new FileNameCleaner();
		//String _FileName = _FileInfo.getName().replaceAll("[.,_]", " ");
		return fnc.getCleanedFilename(_FileInfo.getName());//Utils.split(_FileName, " ").get(0);
	}
}