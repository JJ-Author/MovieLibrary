package jffsss.movlib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jffsss.api.MyMovieAPI;
import jffsss.util.concurrent.AbstractBufferedExecutor;
import jffsss.util.d.DObject;

import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskExecutionException;

public class GetMovieInfo extends Task<MovieInfo>
{
	private static BufferedExecutor _BufferedExecutor = new BufferedExecutor();

	private String _IMDbID;

	public GetMovieInfo(String _IMDbID)
	{
		this._IMDbID = _IMDbID;
	}

	public MovieInfo execute() throws TaskExecutionException
	{
		try
		{
			return _BufferedExecutor.execute(this._IMDbID);
		}
		catch (Throwable e)
		{
			throw new TaskExecutionException(e);
		}
	}

	private static class BufferedExecutor extends AbstractBufferedExecutor<String, MovieInfo>
	{
		public BufferedExecutor()
		{
			super(1, 1000, 500);
		}

		@Override
		protected void execute()
		{
			List<String> _IMDbIDs = this.pollInputs(10);
			if (_IMDbIDs.isEmpty())
				return;
			try
			{
				MyMovieAPI _IMDbAPI = new MyMovieAPI();
				Map<String, MovieInfo> _Results = parseResponse(_IMDbAPI.requestMoviesByID(_IMDbIDs));
				for (String _IMDbID : _IMDbIDs)
					this.setResult(_IMDbID, _Results.get(_IMDbID));
			}
			catch (Exception e)
			{
				this.setFault(e);
			}
		}

		private static Map<String, MovieInfo> parseResponse(DObject _Response) throws Exception
		{
			try
			{
				Map<String, MovieInfo> _ResultMap = new HashMap<String, MovieInfo>();
				for (DObject _ResponseListElement : _Response.asList())
				{
					Map<String, DObject> _ResponseListMap = _ResponseListElement.asMap();
					String _Title = _ResponseListMap.get("title").asString();
					Integer _Year = _ResponseListMap.get("year").parseAsInteger();
					String _Plot;
					try
					{
						try
						{
							_Plot = _ResponseListMap.get("plot_simple").asString();
						}
						catch (Exception e)
						{
							_Plot = _ResponseListMap.get("plot").asString();
						}
					}
					catch (Exception e)
					{
						_Plot = null;
					}
					List<String> _Genres;
					try
					{
						List<DObject> _ResponseListMapList = _ResponseListMap.get("genres").asList();
						_Genres = new ArrayList<String>();
						for (DObject _ResponseListMapListElement : _ResponseListMapList)
							_Genres.add(_ResponseListMapListElement.asString());
					}
					catch (Exception e)
					{
						_Genres = null;
					}
					List<String> _Directors;
					try
					{
						List<DObject> _ResponseListMapList = _ResponseListMap.get("directors").asList();
						_Directors = new ArrayList<String>();
						for (DObject _ResponseListMapListElement : _ResponseListMapList)
							_Directors.add(_ResponseListMapListElement.asString());
					}
					catch (Exception e)
					{
						_Directors = null;
					}
					List<String> _Writers;
					try
					{
						List<DObject> _ResponseListMapList = _ResponseListMap.get("writers").asList();
						_Writers = new ArrayList<String>();
						for (DObject _ResponseListMapListElement : _ResponseListMapList)
							_Writers.add(_ResponseListMapListElement.asString());
					}
					catch (Exception e)
					{
						_Writers = null;
					}
					List<String> _Actors;
					try
					{
						List<DObject> _ResponseListMapList = _ResponseListMap.get("actors").asList();
						_Actors = new ArrayList<String>();
						for (DObject _ResponseListMapListElement : _ResponseListMapList)
							_Actors.add(_ResponseListMapListElement.asString());
					}
					catch (Exception e)
					{
						_Actors = null;
					}
					String _IMDbID = _ResponseListMap.get("imdb_id").asString().substring(2);
					Double _IMDbRating;
					try
					{
						_IMDbRating = _ResponseListMap.get("rating").parseAsDouble();
					}
					catch (Exception e)
					{
						_IMDbRating = null;
					}
					String _PosterSource;
					try
					{
						_PosterSource = _ResponseListMap.get("poster").asMap().get("cover").asString();

					}
					catch (Exception e)
					{
						_PosterSource = null;
					}
					MovieInfo _MovieInfo = new MovieInfo(_Title, _Year, _Plot, _Genres, _Directors, _Writers, _Actors, _IMDbID, _IMDbRating, _PosterSource);
					_ResultMap.put(_IMDbID, _MovieInfo);
				}
				return _ResultMap;
			}
			catch (Exception e)
			{
				throw new Exception("Parse Exception : " + _Response);
			}
		}
	}
}