import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import cpabe.Cpabe;

public class CpabeParallelApp {

	public static void main(String[] args) throws Exception {

		String resultPath = System.getProperty("user.dir") + File.separator
				+ "result" + File.separator;

		int numofAttributes = 20;
		int policyType = AccessTreeBuilder.POLICY_RANDOM;
		String inputfile = resultPath + "input.pdf";
		String inputFileName = "input.pdf";

		if (args.length > 0) {
			if (args[0].equals("-h")) {
				System.out.println("Usage:");
				System.out
						.println("cpabe_parallel [numofAttributes] [policyType] [inputFile]");
				System.exit(0);
			}

			numofAttributes = Integer.parseInt(args[0]);
			policyType = Integer.parseInt(args[1]);
			if (args.length > 3) {
				inputfile = args[2];
				File f = new File(inputfile);
				inputFileName = f.getName();
			}
		}

		String pubfile = resultPath + "pub_key";
		String mskfile = resultPath + "master_key";
		String prvfile = resultPath + "prv_key";

		String encfile = resultPath + inputFileName + ".cpabe";
		String decfile = resultPath + inputFileName;

		String resultFile = resultPath + "result.csv";

		String attr_str = AccessTreeBuilder
				.getAttributesString(numofAttributes);
		String policy = AccessTreeBuilder.getPolicyString(numofAttributes,
				attr_str, policyType);

		System.out.println(policy);

		long time_start = 0;
		long time_end = 0;

		long setup_duration = 0;
		long keygen_duration = 0;
		long encrypt_duration = 0;
		long decrypt_duration = 0;

		System.out.println("CP-ABE Parallel App");

		Cpabe test = new Cpabe();
		time_start = System.currentTimeMillis();
		test.setup(pubfile, mskfile);
		time_end = System.currentTimeMillis();
		setup_duration = time_end - time_start;

		time_start = System.currentTimeMillis();
		test.keygen(pubfile, prvfile, mskfile, attr_str);
		time_end = System.currentTimeMillis();
		keygen_duration = time_end - time_start;

		time_start = System.currentTimeMillis();
		test.enc(pubfile, policy, inputfile, encfile);
		time_end = System.currentTimeMillis();
		encrypt_duration = time_end - time_start;

		time_start = System.currentTimeMillis();
		test.dec(pubfile, prvfile, encfile, decfile);
		time_end = System.currentTimeMillis();
		decrypt_duration = time_end - time_start;

		writeResult(resultFile, numofAttributes, policyType, setup_duration,
				keygen_duration, encrypt_duration, decrypt_duration);
		
	}

	/* connect element of array with blank */
	public static String array2Str(String[] arr) {
		int len = arr.length;
		String str = arr[0];

		for (int i = 1; i < len; i++) {
			str += " ";
			str += arr[i];
		}

		return str;
	}

	private static void writeResult(String resultFile, int numofAttributes,
			int policyType, long setup, long keygen, long encrypt, long decrypt) {
		try {
			File f = new File(resultFile);
			if (!f.exists()) {
				if (f.createNewFile()) {
					BufferedWriter output = new BufferedWriter(new FileWriter(
							f, true));
					output.append("time,app,numofAttrs,policyType,setup,keygen,encrypt,decrypt\n");
					output.close();
				}
			}
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String timestamp = df.format(new Date());
			BufferedWriter output = new BufferedWriter(new FileWriter(f, true));
			output.append(timestamp + ",CpabeParallel," + numofAttributes
					+ "," + policyType + "," + String.valueOf(setup) + ","
					+ String.valueOf(keygen) + "," + String.valueOf(encrypt)
					+ "," + String.valueOf(decrypt) + "\n");
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
