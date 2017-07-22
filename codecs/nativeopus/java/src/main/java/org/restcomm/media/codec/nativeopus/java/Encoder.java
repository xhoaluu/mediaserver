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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.restcomm.media.spi.dsp.Codec;
import org.restcomm.media.spi.format.Format;
import org.restcomm.media.spi.format.FormatFactory;
import org.restcomm.media.spi.memory.Frame;
import org.restcomm.media.spi.memory.Memory;
/**
 *
 * @author HoanHL
 */
public class Encoder implements Codec{
    
    private final static Format opus = FormatFactory.createAudioFormat("opus", 48000);
    private final static Format linear = FormatFactory.createAudioFormat("linear", 8000, 16, 1);
    
    private long linkReference = createCodec(8000);
    private int encodeLength;
    
    //maximum rate for opus is 512000 bits per second=64000 bytes per second = 1280 bytes per packet , we use 20ms frames on mms
    private byte[] encodedCache = new byte[1280];
    
    private native int encode(short[] rawAudio,byte[] encodedAudio,long linkReference);
    
    private native long createCodec(int bandwidth);
    
    private native int resetCodec(long linkReference,int bandwidth);

    @Override
    public Format getSupportedInputFormat() {
        return linear;
    }

    @Override
    public Format getSupportedOutputFormat() {
        return opus;
    }

    @Override
    public Frame process(Frame frame) {
        byte[] bytes = frame.getData();
        short[] data = new short[bytes.length/2];
        // to turn bytes to shorts as either big endian or little endian.
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(data);
        encodeLength=encode(data,encodedCache,linkReference);
        Frame res = Memory.allocate(encodeLength);
        System.arraycopy(encodedCache, 0, res.getData(), 0, encodeLength);
        res.setOffset(0);
        res.setLength(res.getData().length);
        res.setTimestamp(frame.getTimestamp());
        res.setDuration(frame.getDuration());
        res.setSequenceNumber(frame.getSequenceNumber());
        res.setEOM(frame.isEOM());
        res.setFormat(opus);
        return res;
    }
    
    public void reset()
    {
        resetCodec(linkReference,8000);
    }
    
}
