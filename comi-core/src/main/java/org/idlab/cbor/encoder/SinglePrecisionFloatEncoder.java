package org.idlab.cbor.encoder;

import java.io.OutputStream;

import org.idlab.cbor.CborEncoder;
import org.idlab.cbor.CborException;
import org.idlab.cbor.model.SinglePrecisionFloat;

public class SinglePrecisionFloatEncoder extends AbstractEncoder<SinglePrecisionFloat> {

	public SinglePrecisionFloatEncoder(CborEncoder encoder, OutputStream outputStream) {
		super(encoder, outputStream);
	}

	@Override
	public void encode(SinglePrecisionFloat dataItem) throws CborException {
		write((7 << 5) | 26);
		int bits = Float.floatToRawIntBits(dataItem.getValue());
		write((bits >> 24) & 0xFF);
		write((bits >> 16) & 0xFF);
		write((bits >> 8) & 0xFF);
		write((bits >> 0) & 0xFF);
	}

}
