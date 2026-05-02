class RValues {
	double R;
	double Rf;

	RValues(double R, double Rf) {
		this.R = R;
		this.Rf = Rf;
    	}
}

public class inverting_amp {
	public static RValues calculate(double gain) {
		double R = 1000;
		double Rf = gain * R;
		return new RValues(R, Rf);
		}

	public static String generateNetlist(RValues v) {
        return String.format("""
$ 1 5.0E-6 10 57 5.0
v 96 256 96 112 0 1 1000.0 0.001 0.0
g 96 256 96 304 0
r 96 112 192 112 0 %f
r 192 144 336 144 0 %f
w 336 144 336 192 0
w 192 112 192 144 0
w 192 144 192 176 0
w 96 256 192 256 0
w 192 208 192 256 0
a 192 192 336 192 0
O 336 192 400 192 0
o 0 64 0 2 5.0 0.025
o 10 64 0 2 20.0 9.765625E-5
""",
		v.R, v.Rf
		);
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Usage: java inverting_amp <gain>");
			return;
		}

		double gain = Double.parseDouble(args[0]);

		RValues values = calculate(gain);
		String netlist = generateNetlist(values);

		System.out.println("Generated Netlist:\n");
		System.out.println(netlist);
	}
}
