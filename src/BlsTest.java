import it.unisa.dia.gas.jpbc.CurveParameters;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.DefaultCurveParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.io.ByteArrayInputStream;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class BlsTest {

	public static String curveParams = "type=a\nq=9882642904117175329151553553252351229902817053"
			+ "7954154869719707264887274916552228805607584116490046284509883309001532457"
			+ "986879277885241872021906840932513241346999389365188296460009947\n"
			+ "h=3224362694893486088748849015843729948945351335274588924643775571370152"
			+ "1031193083418924110592954582395114812811896992400310730276\n"
			+ "r=3064991081731777546575510593831386635550174528483098623\n"
			+ "exp2=181\nexp1=127\nsign1=-1\nsign0=-1";

	public BlsTest() {
	}

	public static void main(String[] args) {
		
		CurveParameters params = new DefaultCurveParameters()
		.load(new ByteArrayInputStream(curveParams.getBytes()));
		Pairing pairing = PairingFactory.getPairing(params);
		
		// Generate system parameters
		Element g = pairing.getG2().newRandomElement();
		

		// Generate the secret key
		Element x = pairing.getZr().newRandomElement();

		// Generate the corresponding public key
		Element pk = g.duplicate().powZn(x); // We need to duplicate g because
												// it's a system parameter.

		// Map the hash of the message m to some element of G1

		byte[] hash = "ABCDEF".getBytes(); // Generate an hash from m (48-bit
											// hash)
		Element h = pairing.getG1().newElement()
				.setFromHash(hash, 0, hash.length);

		// Generate the signature

		Element sig = h.powZn(x); // We can discard the value h, so we don't
									// need to duplicate it.

		// Map again the hash of the message m

		hash = "ABCDEF".getBytes(); // Generate an hash from m (48-bit hash)
		h = pairing.getG1().newElement().setFromHash(hash, 0, hash.length);

		// Verify the signature

		Element temp1 = pairing.pairing(sig, g);
		Element temp2 = pairing.pairing(h, pk);

		System.out.println("temp1 = " + temp1.toString());
		System.out.println("temp2 = " + temp2.toString());
		System.out.println("temp1 length = " + temp1.getLengthInBytes() * 8 + " bits");
		System.out.println("temp2 length = " + temp2.getLengthInBytes() * 8 + " bits");
		System.out.println(pairing.isSymmetric());
		System.out.println("g length = " + g.getLengthInBytes() * 8 + " bits");
		System.out.println("h length = " + h.getLengthInBytes() * 8 + " bits");
	}

}