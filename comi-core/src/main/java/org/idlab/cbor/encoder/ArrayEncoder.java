package org.idlab.cbor.encoder;

import java.io.OutputStream;
import java.util.List;

import org.idlab.cbor.CborEncoder;
import org.idlab.cbor.CborException;
import org.idlab.cbor.model.Array;
import org.idlab.cbor.model.DataItem;
import org.idlab.cbor.model.MajorType;

public class ArrayEncoder extends AbstractEncoder<Array> {

    public ArrayEncoder(CborEncoder encoder, OutputStream outputStream) {
        super(encoder, outputStream);
    }

    @Override
    public void encode(Array array) throws CborException {
        List<DataItem> dataItems = array.getDataItems();
        if (array.isChunked()) {
            encodeTypeChunked(MajorType.ARRAY);
        } else {
            encodeTypeAndLength(MajorType.ARRAY, dataItems.size());
        }
        for (DataItem dataItem : dataItems) {
            encoder.encode(dataItem);
        }
    }

}
