package com.volmit.react.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@SuppressWarnings("hiding")
public interface StreamConstructor<I extends InputStream, O extends OutputStream>
{
	I constructInput(InputStream base) throws IOException;

	O constructOutput(OutputStream base) throws IOException;
}
