package jffsss.util;

import java.util.Collection;

public class StringUtils
{
	private StringUtils()
	{}

	public static String join(Collection<?> _Strings, String _Delimiter)
	{
		return join(_Strings.toArray(), _Delimiter);
	}

	public static String join(Object[] _Strings, String _Delimiter)
	{
		StringBuilder _StringBuilder = new StringBuilder();
		for (Object _String : _Strings)
		{
			if (_StringBuilder.length() > 0)
				_StringBuilder.append(_Delimiter);
			_StringBuilder.append(_String);
		}
		return _StringBuilder.toString();
	}

	public static String[] split(String _String, String _Delimiter)
	{
		return _String.split(_Delimiter);
	}
}