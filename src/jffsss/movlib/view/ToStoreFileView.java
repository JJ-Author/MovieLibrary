package jffsss.movlib.view;

import java.util.HashMap;
import java.util.Map;

import jffsss.movlib.ProbablyMovie;
import jffsss.movlib.ToStoreFile;
import jffsss.util.FileNameCleaner;
import jffsss.util.Listener;
import jffsss.util.Listeners;

import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.wtk.Border;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.FlowPane;
import org.apache.pivot.wtk.Label;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.TextInput;
/**
 * Displays all possible Movies for a file.
 * Also contains the Add Movie and Remove (file from list) button.   
 *
 */
public class ToStoreFileView
{
	private ToStoreFile _Model;
	private Border _Component;
	private Label _FilePathText;
	private TextInput _AddMovieText;
	private PushButton _AddMovieButton;
	private PushButton _RemoveButton;
	private FlowPane _ProbablyMovieViewsContainer;
	private Map<ProbablyMovie, ProbablyMovieView> _ProbablyMovieViews;
	/**
	 * Constructor, also draws the Remove and Add Buttons
	 * @param _Model Movies to display
	 */
	public ToStoreFileView(ToStoreFile _Model)
	{
		this._Model = _Model;
		BXMLSerializer _BXMLSerializer = new BXMLSerializer();
		try
		{
			this._Component = (Border) _BXMLSerializer.readObject(ToStoreFileView.class, "ToStoreFileView.bxml");
			this._FilePathText = (Label) (_BXMLSerializer.getNamespace().get("FilePathText"));
			this._AddMovieText = (TextInput) _BXMLSerializer.getNamespace().get("AddMovieText");
			this._AddMovieButton = (PushButton) _BXMLSerializer.getNamespace().get("AddMovieButton");
			this._RemoveButton = (PushButton) _BXMLSerializer.getNamespace().get("RemoveButton");
			this._ProbablyMovieViewsContainer = (FlowPane) _BXMLSerializer.getNamespace().get("ProbablyMovieViewsContainer");
			this._ProbablyMovieViews = new HashMap<ProbablyMovie, ProbablyMovieView>();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new NullPointerException();
		}
		this._Model.onUpdate().addListener(this._onUpdateListener); //Add Movie Button
		{
			ButtonPressListener _Listener = new ButtonPressListener()
			{
				@Override
				public void buttonPressed(Button _Button)
				{
					String _IMDbID = ToStoreFileView.this._AddMovieText.getText();
					ToStoreFileView.this._AddMovieText.setText("");
					ToStoreFileView.this._Model.addProbablyMovie(_IMDbID);
				}
			};
			this._AddMovieButton.getButtonPressListeners().add(_Listener);
		}
		{
			ButtonPressListener _Listener = new ButtonPressListener() //Remove Button
			{
				@Override
				public void buttonPressed(Button _Button)
				{
					ToStoreFileView.this.onAction.notifyListeners("Remove", null);
				}
			};
			this._RemoveButton.getButtonPressListeners().add(_Listener);
		}
	}

	private Listeners onAction = null;
	
	/**
	 * Get a collection of Listeners fpr this Class
	 * @return Listeners
	 */
	public Listeners onAction()
	{
		if (this.onAction == null)
			this.onAction = new Listeners(this);
		return this.onAction;
	}

	public Border getComponent()
	{
		return this._Component;
	}
	
	/**
	 * Get all Movies this File could relate to
	 * @return Underlying ToStoreFile
	 */
	public ToStoreFile getModel()
	{
		return this._Model;
	}
	
	/**
	 * Updates the Information of the File for which Movies are displayed (adds the cleaned Filename)
	 */
	public void updateVideoFileInfo()
	{
		if (this._Model.getVideoFileInfo() == null)
			this._FilePathText.setText("");
		else
		{
			FileNameCleaner fnc = new FileNameCleaner();
			//String _FileName = _FileInfo.getName().replaceAll("[.,_]", " ");
			this._FilePathText.setText(this._Model.getVideoFileInfo().getFileInfo().getPath()+"  ||  "+fnc.getCleanedFilename(this._Model.getVideoFileInfo().getFileInfo().getName()));
		}
	}
	
	/**
	 * Creates a ProbablyMovieView and adds it to the displayed Items. Also creates Listener to confirm it.
	 * @param _ProbablyMovie Base of the created ProbablyMovieView
	 */
	public void addProbablyMovieView(ProbablyMovie _ProbablyMovie)
	{
		ProbablyMovieView _ProbablyMovieView = this._ProbablyMovieViews.get(_ProbablyMovie);
		if (_ProbablyMovieView == null)
		{
			_ProbablyMovieView = new ProbablyMovieView(_ProbablyMovie);
			_ProbablyMovieView.onAction().addListener(this._OnActionListener);
			this._ProbablyMovieViewsContainer.add(_ProbablyMovieView.getComponent());
			this._ProbablyMovieViews.put(_ProbablyMovie, _ProbablyMovieView);
		}
	}
	
	/**
	 * Removes a ProbablyMovieView and its respective Listeners
	 * @param _ProbablyMovie ProbablyMovie whose View will be deleted
	 */
	public void removeProbablyMovieView(ProbablyMovie _ProbablyMovie)
	{
		ProbablyMovieView _ProbablyMovieView = this._ProbablyMovieViews.remove(_ProbablyMovie);
		if (_ProbablyMovieView != null)
		{
			_ProbablyMovieView.onAction().removeListener(this._OnActionListener);
			this._ProbablyMovieViewsContainer.remove(_ProbablyMovieView.getComponent());
		}
	}
	
	/**
	 * Removes all ProbablyMovieViews and their respective Listeners
	 */
	public void clearProbablyMovieViews()
	{
		for (ProbablyMovieView _ProbablyMovieView : this._ProbablyMovieViews.values())
		{
			_ProbablyMovieView.onAction().removeListener(this._OnActionListener);
			this._ProbablyMovieViewsContainer.remove(_ProbablyMovieView.getComponent());
		}
		this._ProbablyMovieViews.clear();
	}
	
	/**
	 * Removes the OnUpdateListener, disables Interactions with underlying ToStoreFile
	 */
	public void clean()
	{
		this._Model.onUpdate().removeListener(this._onUpdateListener);
	}

	private OnUpdateListener _onUpdateListener = new OnUpdateListener();
	
	/**
	 * Allows Interaction between a ToStoreFileView and its underlying ToStoreFile
	 *
	 */
	private class OnUpdateListener implements Listener
	{
		/**
		 * Updates Information of the displayed ToStoreFile
		 * 
		 * @param _Source Origin of the Action
		 * @Param _Command Requested Action
		 * @param _Arg Arguments for the Action
		 */
		@Override
		public void on(Object _Source, String _Command, Object _Arg)
		{
			if (_Source == ToStoreFileView.this._Model)
				switch (_Command)
				{
					case "SetVideoFileInfo":
						ToStoreFileView.this.updateVideoFileInfo();
						break;
					case "AddProbablyMovie":
						if (_Arg instanceof ProbablyMovie)
							ToStoreFileView.this.addProbablyMovieView((ProbablyMovie) _Arg);
						break;
				}
		}
	}

	private OnActionListener _OnActionListener = new OnActionListener();
	
	/**
	 * Button Interactions with the displayed ProbablyMovies
	 *
	 */
	private class OnActionListener implements Listener
	{
		/**
		 * Confirms a Movie of the displayed ProbablyMovies
		 * 
		 * @param _Source Origin of the Action
		 * @Param _Command Requested Action
		 * @param _Arg not used
		 */
		@Override
		public void on(Object _Source, String _Command, Object _Arg)
		{
			if (_Source instanceof ProbablyMovieView)
			{
				ProbablyMovieView _ProbablyMovieView = (ProbablyMovieView) _Source;
				switch (_Command)
				{
					case "Confirm":
						ToStoreFileView.this.onAction().notifyListeners("ConfirmProbablyMovie", _ProbablyMovieView.getModel());
						break;
				}
			}
		}
	}
}