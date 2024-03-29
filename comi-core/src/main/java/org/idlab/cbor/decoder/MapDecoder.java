package org.idlab.cbor.decoder;

import java.io.InputStream;

import org.idlab.cbor.CborDecoder;
import org.idlab.cbor.CborException;
import org.idlab.cbor.model.DataItem;
import org.idlab.cbor.model.Map;
import org.idlab.cbor.model.Special;

public class MapDecoder extends AbstractDecoder<Map> {

    public MapDecoder(CborDecoder decoder, InputStream inputStream) {
        super(decoder, inputStream);
    }

    @Override
    public Map decode(int initialByte) throws CborException {
        long length = getLength(initialByte);
        if (length == INFINITY) {
            return decodeInfinitiveLength();
        } else {
            return decodeFixedLength(length);
        }
    }

    private Map decodeInfinitiveLength() throws CborException {
        Map map = new Map();
        map.setChunked(true);
        if (decoder.isAutoDecodeInfinitiveMaps()) {
            for (;;) {
                DataItem key = decoder.decodeNext();
                if (Special.BREAK.equals(key)) {
                    break;
                }
                map.put(key, decoder.decodeNext());
            }
        }
        return map;
    }

    private Map decodeFixedLength(long length) throws CborException {
        Map map = new Map((int) length);
        for (long i = 0; i < length; i++) {
            map.put(decoder.decodeNext(), decoder.decodeNext());
        }
        return map;
    }

}
