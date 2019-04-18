package org.idlab.cbor.decoder;

import java.io.InputStream;

import org.idlab.cbor.CborDecoder;
import org.idlab.cbor.CborException;
import org.idlab.cbor.model.SinglePrecisionFloat;

public class SinglePrecisionFloatDecoder extends AbstractDecoder<SinglePrecisionFloat> {

	public SinglePrecisionFloatDecoder(CborDecoder decoder, InputStream inputStream) {
		super(decoder, inputStream);
	}

	@Override
	public SinglePrecisionFloat decode(int initialByte) throws CborException {
		int bits = 0;
		bits |= nextSymbol() & 0xFF;
		bits <<= 8;
		bits |= nextSymbol() & 0xFF;
		bits <<= 8;
		bits |= nextSymbol() & 0xFF;
		bits <<= 8;
		bits |= nextSymbol() & 0xFF;
		return new SinglePrecisionFloat(Float.intBitsToFloat(bits));
	}

}
