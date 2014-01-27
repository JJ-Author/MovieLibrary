package jffsss.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileNameCleaner {

	public FileNameCleaner() {
		// TODO Auto-generated constructor stub
	}
	
	public String getCleanedFilename(String name)
	{
		String result = name;
		result = result.replaceAll("(?i)\\.[a-z]{3}$", ""); 			//delete file suffix
		result = result.replace('_', ' ');								//use only blanks instead of underscore
		result = result.replaceAll("(?i)(\\D)(\\.)(\\S)", "$1 $3");		//if dots used as whitespace replace them with blank
		
		// remove some keywords indicating quality or format of the movie file
		result = result.replaceAll("(?i)720p?", "");
		result = result.replaceAll("(?i)1080[pi]?", "");
		result = result.replaceAll("(?i)CD ?\\d\\d?", "");
		result = result.replaceAll("(?i)DVD ?\\d\\d?", "");
		result = result.replaceAll("(?i)part ?\\d\\d?", "");
		result = result.replaceAll("(?i)bdrip", "");
		result = result.replaceAll("(?i)dvdrip", "");
		result = result.replaceAll("(?i)dubbed", "");
		result = result.replaceAll("(?i)[xh]?264", "");
		result = result.replaceAll("(?i)divx", "");
		result = result.replaceAll("(?i)xvid", "");
		result = result.replaceAll("(?i)ac3", "");
		result = result.replaceAll("(?i)dts", "");
		result = result.replaceAll("(?i)mp3", "");
		result = result.replaceAll("(?i)mp4", "");
	
		// remove anything between brackets
		result = result.replaceAll("(?i)\\(.*\\)", "");
		result = result.replaceAll("(?i)\\[.*\\]", "");
		//Pattern pattern = Pattern.compile();
		//Matcher matcher = pattern.matcher(name);
		//result =  matcher.replaceAll("");
		
		
		return result;
	}

}
