package com.volmit.react.util;

import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.IOException;

public class Unzip
{
	private UnzipState state;
	private UnzipType type;
	private UnzipStatus status;
	private UnzipMonitor monitor;
	private File file;
	private File outputDestination;
	private int bufferSize;
	private byte[] buffer;

	public Unzip(UnzipMonitor monitor, File file, File outputDestination, int bufferSize)
	{
		this.file = file;
		this.outputDestination = outputDestination;
		this.monitor = monitor;
		this.bufferSize = bufferSize;
		buffer = new byte[bufferSize];
		status = new UnzipStatus();
		state = UnzipState.IDLE;
		type = UnzipType.INDETERMINATE;
	}

	public void start() throws IOException
	{
		if(state.equals(UnzipState.UNZIPPING))
		{
			throw new IOException("Unzip already running!");
		}
		if(!outputDestination.exists())
			outputDestination.mkdirs();

		try
		{
			long time = M.ms();
			int read = 0;
			long size = file.length();

			UnzipState lastState = state;
			status.setBytesTotal(size);
			status.setBytesUnzipped(0);
			type = size <= 0 ? UnzipType.INDETERMINATE : UnzipType.DETERMINATE;
			state = UnzipState.UNZIPPING;
			monitor.onUnzipStateChanged(this, lastState, state);
			monitor.onUnzipStarted(this);
			ZipUtil.unpack(file, outputDestination);
			lastState = state;
			state = UnzipState.FINISHED;
			monitor.onUnzipStateChanged(this, lastState, state);
			monitor.onUnzipFinished(this);
		}
		catch(Throwable e)
		{
			Ex.t(e);
			UnzipState lastState = state;
			state = UnzipState.FAILED;
			monitor.onUnzipStateChanged(this, lastState, state);
			monitor.onUnzipFailed(this);
			throw new IOException("Unzip Failed", e);
		}
	}

	public UnzipState getState()
	{
		return state;
	}

	public UnzipType getType()
	{
		return type;
	}

	public UnzipStatus getStatus()
	{
		return status;
	}

	public UnzipMonitor getMonitor()
	{
		return monitor;
	}

	public File getFile()
	{
		return file;
	}

	public int getBufferSize()
	{
		return bufferSize;
	}
}
