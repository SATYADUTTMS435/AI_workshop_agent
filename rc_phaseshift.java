import java.lang.Math;

class RCValues {
	double R;
	double Rf;
	double C;

	RCValues(double R, double Rf, double C) {
		this.R = R;
		this.Rf = Rf;
		this.C = C;
    	}
}

public class rc_phaseshift {
	public static RCValues calculate(double f) {
		double[] caps = {
		 			1e-12, 10e-12, 100e-12,
            				1e-9, 10e-9, 100e-9,
            				1e-6, 10e-6
        		};
		double gain = 30;
		for (double C : caps) {
			double R = 1 / (2 * Math.PI * Math.sqrt(6) * f * C);
			if (R >= 1e3 && R <= 100e3) {
				double Rf = gain * R;
				return new RCValues(R, Rf, C);
            	}
        }


		double C = 1e-6;
		double R = 1 / (2 * Math.PI * Math.sqrt(6) * f * C);
		double Rf = gain * R;
		return new RCValues(R, Rf, C);
    	}

	public static String generateNetlist(RCValues v) {
        return String.format("""
$ 1 0.000015625 3.046768661252054 58 5 50
c 144 368 208 368 0 %e -2.544137674334456
c 208 368 272 368 0 %e -3.308245117520446
c 272 368 336 368 0 %e -0.9716832694680744
r 336 368 400 368 0 %f
r 208 368 208 448 0 %f
r 272 368 272 448 0 %f
g 208 448 208 464 0
g 272 448 272 464 0
a 400 384 496 384 0 15 -15 1000000 0.00006596531227299938 0
w 400 368 400 320 0
r 400 320 496 320 0 %f
w 496 320 496 384 0
w 496 384 512 384 0
w 512 384 512 288 0
w 512 288 144 288 0
w 144 288 144 368 0
g 400 400 400 448 0
""",
		v.C, v.C, v.C,
		v.R, v.R, v.R,
		v.Rf
		);
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Usage: java rc_phaseshift <frequency>");
			return;
		}

		double frequency = Double.parseDouble(args[0]);

		RCValues values = calculate(frequency);
		String netlist = generateNetlist(values);

		System.out.println("Generated Netlist:\n");
		System.out.println(netlist);
	}
}
