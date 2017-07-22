/*
 * Copyright (C) 2017 TeleStax, Inc..
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.restcomm.media.codec.nativeopus.java;

import org.restcomm.media.spi.dsp.Codec;
import org.restcomm.media.spi.format.Format;
import org.restcomm.media.spi.format.FormatFactory;
import org.restcomm.media.spi.memory.Frame;
import org.restcomm.media.spi.memory.Memory;

/**
 *
 * @author HoanHL
 */
public class Decoder implements Codec{

    private final static Format opus = FormatFactory.createAudioFormat("opus", 48000);
    private final static Format linear = FormatFactory.createAudioFormat("linear", 8000, 16, 1);
    
    private long linkReference = createCodec(8000);
    private int decodeLength;
    
    //maximum packet duration (120ms; 5760 for 48kHz) , do not change may be usefull in future, currently will use much less
    private short[] decodedCache=new short[5760];
    
    private native int decode(byte[] encodedAudio, short[] rawAudio,long linkReference);
    
    private native long createCodec(int bandwidth);
    
    private native int resetCodec(long linkReference,int bandwidth);
    
    @Override
    public Format getSupportedInputFormat() {
        return opus;
    }

    @Override
    public Format getSupportedOutputFormat() {
        return linear;
    }

    @Override
    public Frame process(Frame frame) {
        byte[] inputData = frame.getData();
    	decodeLength=decode(frame.getData(), decodedCache, linkReference);
    	Frame res = Memory.allocate(decodeLength);
    	System.arraycopy(decodedCache, 0, res.getData(), 0, decodeLength);
        res.setOffset(0);
        res.setLength(res.getData().length);
        res.setTimestamp(frame.getTimestamp());
        res.setDuration(frame.getDuration());
        res.setSequenceNumber(frame.getSequenceNumber());
        res.setEOM(frame.isEOM());
        res.setFormat(linear);
        return res;
    }
    
    public void reset()
    {
        resetCodec(linkReference,8000);
    }
    
}
