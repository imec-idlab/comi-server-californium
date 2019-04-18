package org.idlab.cbor.decoder;

import java.io.InputStream;
import java.math.BigInteger;

import org.idlab.cbor.CborDecoder;
import org.idlab.cbor.CborException;
import org.idlab.cbor.model.NegativeInteger;

public class NegativeIntegerDecoder extends AbstractDecoder<NegativeInteger> {

	private static final BigInteger MINUS_ONE = BigInteger.valueOf(-1);

	public NegativeIntegerDecoder(CborDecoder decoder, InputStream inputStream) {
		super(decoder, inputStream);
	}

	@Override
	public NegativeInteger decode(int initialByte) throws CborException {
		return new NegativeInteger(MINUS_ONE.subtract(getLengthAsBigInteger(initialByte)));
	}

}
