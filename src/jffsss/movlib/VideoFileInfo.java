package jffsss.movlib;

import java.io.File;

import jffsss.api.OpenSubtitlesHasher;

public class VideoFileInfo
{
	private FileInfo _FileInfo;
	private String _OpenSubtitlesHash;

	public VideoFileInfo(FileInfo _FileInfo, String _OpenSubtitlesHash)
	{
		this._FileInfo = _FileInfo;
		this._OpenSubtitlesHash = _OpenSubtitlesHash;
	}

	public static VideoFileInfo getFromFile(String _FilePath)
	{
		FileInfo _FileInfo = FileInfo.getFromFile(_FilePath);
		String _OpenSubtitlesHash = OpenSubtitlesHasher.computeHash(new File(_FilePath));
		return new VideoFileInfo(_FileInfo, _OpenSubtitlesHash);
	}

	public FileInfo getFileInfo()
	{
		return this._FileInfo;
	}

	public String getOpenSubtitlesHash()
	{
		return this._OpenSubtitlesHash;
	}
}