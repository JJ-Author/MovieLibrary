package jffsss.service.opensubtitles;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public class OpenSubtitlesHasher
{
	private OpenSubtitlesHasher()
	{}

	private static final int HASH_CHUNK_SIZE = 64 * 1024;

	public static String computeHash(File _File) throws IOException
	{
		long _Size = _File.length();
		long _ChunkSizeForFile = Math.min(HASH_CHUNK_SIZE, _Size);
		//@SuppressWarnings("resource")
		FileChannel _FileChannel = new FileInputStream(_File).getChannel();
		try
		{
			long _Head = computeHashForChunk(_FileChannel.map(MapMode.READ_ONLY, 0, _ChunkSizeForFile));
			long _Tail = computeHashForChunk(_FileChannel.map(MapMode.READ_ONLY, Math.max(_Size - HASH_CHUNK_SIZE, 0), _ChunkSizeForFile));
			return String.format("%016x", _Size + _Head + _Tail);
		}
		finally
		{
			_FileChannel.close();
		}
	}

	public static String computeHash(InputStream _InputStream, long _Length) throws IOException
	{
		int _ChunkSizeForFile = (int) Math.min(HASH_CHUNK_SIZE, _Length);
		byte[] oChunkBytes = new byte[(int) Math.min(2 * HASH_CHUNK_SIZE, _Length)];
		DataInputStream _DataInputStream = new DataInputStream(_InputStream);
		_DataInputStream.readFully(oChunkBytes, 0, _ChunkSizeForFile);
		long _Position = _ChunkSizeForFile;
		long _RailChunkPosition = _Length - _ChunkSizeForFile;
		while (_Position < _RailChunkPosition && (_Position += _DataInputStream.skip(_RailChunkPosition - _Position)) >= 0)
		{}
		_DataInputStream.readFully(oChunkBytes, _ChunkSizeForFile, oChunkBytes.length - _ChunkSizeForFile);
		long _Head = computeHashForChunk(ByteBuffer.wrap(oChunkBytes, 0, _ChunkSizeForFile));
		long _Tail = computeHashForChunk(ByteBuffer.wrap(oChunkBytes, oChunkBytes.length - _ChunkSizeForFile, _ChunkSizeForFile));
		return String.format("%016x", _Length + _Head + _Tail);
	}

	private static long computeHashForChunk(ByteBuffer _ByteBuffer)
	{
		LongBuffer _LongBuffer = _ByteBuffer.order(ByteOrder.LITTLE_ENDIAN).asLongBuffer();
		long _Hash = 0;
		while (_LongBuffer.hasRemaining())
			_Hash += _LongBuffer.get();
		return _Hash;
	}
}