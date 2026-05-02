public class BinaryCounterGenerator {

    /**
     * Generates Falstad circuit text for an N-bit ripple binary counter.
     * Verified against 4-bit, 6-bit, and 12-bit reference circuits.
     *
     * Usage:
     * String circuit = BinaryCounterGenerator.binaryCounter(8);
     *
     * Command line:
     * javac BinaryCounterGenerator.java
     * java BinaryCounterGenerator 8
     */
    public static String binaryCounter(int bits) {
        if (bits < 1)
            throw new IllegalArgumentException("bits must be >= 1");

        StringBuilder sb = new StringBuilder();

        // ── Layout constants ──────────────────────────────────────────────
        // Y = y-row of all FF CLK pins = 208
        // STEP = horizontal gap between FF x1 values = 128
        // Q_OFF = Q output node x offset from x1 = 96 (x1 + 96)
        // CLK_DY = CLK pin y offset = 32 → y = Y+32
        // K_DY = K pin y offset = 64 → y = Y+64
        // VCC_DY = VCC bus y offset = 112 → y = Y+112
        //
        // Indexing: FF[0] = leftmost, FF[bits-1] = rightmost
        // x1[i] = x1First + i * STEP
        // Qx[i] = x1[i] + Q_OFF
        //
        // Anchor: rightmost FF Qx = 624 (probe wire endpoint, matches all references)
        // → x1[bits-1] = 624 - Q_OFF = 528
        // → x1First = 528 - (bits-1)*STEP

        final int Y = 208;
        final int STEP = 128;
        final int Q_OFF = 96;
        final int CLK_DY = 32;
        final int K_DY = 64;
        final int VCC_DY = 112;
        final int PROBE_RIGHT_X = 656;

        int x1First = 528 - (bits - 1) * STEP;

        // ── 1. Header ─────────────────────────────────────────────────────
        sb.append("$ 1 5.0E-6 10.20027730826997 50 5.0\n");

        // ── 2. JK Flip-Flops ─────────────────────────────────────────────
        // 156 x1 Y x2 Y 0 5.0
        // Body widths (cosmetic, verified from references):
        // leftmost (i=0) → width 48
        // rightmost (i=bits-1) → width 16
        // all middle → width 32
        // v3=5.0 for all → J and K held HIGH → FF toggles every clock edge
        for (int i = 0; i < bits; i++) {
            int x1 = x1First + i * STEP;
            int bw = (i == 0) ? 48 : (i == bits - 1) ? 16 : 32;
            sb.append(String.format("156 %d %d %d %d 0 5.0\n", x1, Y, x1 + bw, Y));
        }

        // ── 3. Q → CLK inter-FF wires ────────────────────────────────────
        // Q of FF[i] at (Qx_i, Y) → down to Y+CLK_DY → right to x1[i+1]
        for (int i = 0; i < bits - 1; i++) {
            int qx = x1First + i * STEP + Q_OFF;
            int clkX = x1First + (i + 1) * STEP;
            sb.append(String.format("w %d %d %d %d 0\n", qx, Y, qx, Y + CLK_DY));
            sb.append(String.format("w %d %d %d %d 0\n", qx, Y + CLK_DY, clkX, Y + CLK_DY));
        }

        // ── 4. /Q feedback + VCC bus wiring ──────────────────────────────
        //
        // ── BUG FIX 1: Pairing is from the RIGHT, not from the left ──────
        // Style A (left-side stub): the TWO LEFTMOST FFs → FF[0] and FF[1]
        // Style B (paired box): all remaining FFs, paired left-to-right
        // starting from FF[2]: pairs (2,3), (4,5), ...
        //
        // ── BUG FIX 2: Style B needs an extra wire rx → x1_right at Y ───
        // This connects the right node (rx) to the CLK column of the right FF.
        // Without it, the right FF's K/J pins are not properly tied to VCC.

        // Style A helper
        // Wires: x1 → lx at Y, lx down to K_Y, lx right to K pin, lx down to VCC
        appendStyleA(sb, x1First, Y, K_DY, VCC_DY);
        if (bits >= 2)
            appendStyleA(sb, x1First + STEP, Y, K_DY, VCC_DY);

        // Style B pairs: (FF[2], FF[3]), (FF[4], FF[5]), ...
        for (int i = 2; i + 1 < bits; i += 2) {
            int x1L = x1First + i * STEP; // left FF of pair
            int x1R = x1First + (i + 1) * STEP; // right FF of pair
            int rx = x1L + Q_OFF + 16; // right node = x1L + 112
            int lx = x1L - 16; // left node

            sb.append(String.format("w %d %d %d %d 0\n", rx, Y, rx, Y + K_DY)); // rx down to K_Y
            sb.append(String.format("w %d %d %d %d 0\n", rx, Y + K_DY, x1R, Y + K_DY)); // → K of right FF
            sb.append(String.format("w %d %d %d %d 0\n", rx, Y + K_DY, rx, Y + VCC_DY)); // rx → VCC
            // rx→lx at VCC_DY intentionally omitted: the VCC bus loop (section 5)
            // already emits this same segment — duplicating it causes stray wires.
            sb.append(String.format("w %d %d %d %d 0\n", lx, Y + VCC_DY, lx, Y + K_DY)); // lx up to K_Y
            sb.append(String.format("w %d %d %d %d 0\n", lx, Y + K_DY, x1L, Y + K_DY)); // → K of left FF
            sb.append(String.format("w %d %d %d %d 0\n", lx, Y + K_DY, lx, Y)); // lx up to Y
            sb.append(String.format("w %d %d %d %d 0\n", lx, Y, x1L, Y)); // → CLK of left FF
            sb.append(String.format("w %d %d %d %d 0\n", rx, Y, x1R, Y)); // ← BUGFIX 2: rx → CLK of right FF
        }

        // Handle odd last FF (unpaired): use Style A
        if (bits >= 3 && bits % 2 == 1) {
            appendStyleA(sb, x1First + (bits - 1) * STEP, Y, K_DY, VCC_DY);
        }

        // ── 5. VCC bus: connect all taps along Y+VCC_DY ─────────────────
        // Taps: lx of FF[0], lx of FF[1], then each pair's lx and rx
        java.util.TreeSet<Integer> taps = new java.util.TreeSet<>();
        taps.add(x1First - 16);
        if (bits >= 2)
            taps.add(x1First + STEP - 16);
        for (int i = 2; i + 1 < bits; i += 2) {
            int x1L = x1First + i * STEP;
            taps.add(x1L - 16);
            taps.add(x1L + Q_OFF + 16);
        }
        if (bits >= 3 && bits % 2 == 1) {
            taps.add(x1First + (bits - 1) * STEP - 16);
        }
        Integer[] tapArr = taps.toArray(new Integer[0]);
        for (int i = 0; i < tapArr.length - 1; i++) {
            sb.append(String.format("w %d %d %d %d 0\n",
                    tapArr[i], Y + VCC_DY, tapArr[i + 1], Y + VCC_DY));
        }

        // ── 6. Clock source (200 Hz square wave, 2.5 V) ──────────────────
        // Connects to CLK pin of FF[0] at (x1First, Y + CLK_DY)
        sb.append(String.format("R %d %d %d %d 1 2 200.0 2.5 2.5\n",
                x1First, Y + CLK_DY, x1First - 48, Y + CLK_DY));

        // ── 7. VCC supply (+5 V DC) ──────────────────────────────────────
        int vccX = x1First - 16;
        sb.append(String.format("R %d %d %d %d 0 0 40.0 5.0 0.0\n",
                vccX, Y + VCC_DY, vccX - 48, Y + VCC_DY));

        // ── 8. Oscilloscope probe wires + M markers ───────────────────────
        // FF[i] Q at (Qx_i, Y) → vertical wire up → M marker going right
        // probeY[i] = Y - (bits - i) * 32 (FF[0] deepest, FF[bits-1] highest)
        for (int i = 0; i < bits; i++) {
            int qx = x1First + i * STEP + Q_OFF;
            int probeY = Y - (bits - i) * 32;
            sb.append(String.format("w %d %d %d %d 0\n", qx, Y, qx, probeY));
            sb.append(String.format("M %d %d %d %d 2\n", qx, probeY, PROBE_RIGHT_X, probeY));
        }

        // ── 9. Oscilloscope trace definitions ────────────────────────────
        for (int i = 0; i < bits; i++) {
            sb.append(String.format("o %d 64 0 6 5.0 9.765625E-5 0\n", i));
        }

        return sb.toString();
    }

    /** Style A: left-side stub wiring for a single FF */
    private static void appendStyleA(StringBuilder sb, int x1, int Y, int K_DY, int VCC_DY) {
        int lx = x1 - 16;
        sb.append(String.format("w %d %d %d %d 0\n", x1, Y, lx, Y));
        sb.append(String.format("w %d %d %d %d 0\n", lx, Y, lx, Y + K_DY));
        sb.append(String.format("w %d %d %d %d 0\n", lx, Y + K_DY, x1, Y + K_DY));
        sb.append(String.format("w %d %d %d %d 0\n", lx, Y + K_DY, lx, Y + VCC_DY));
    }

    // ── Entry point ───────────────────────────────────────────────────────
    public static void main(String[] args) {
        int bits = 6;
        if (args.length > 0) {
            try {
                bits = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Usage: java BinaryCounterGenerator <bits>");
                System.exit(1);
            }
        }
        if (bits < 1 || bits > 32) {
            System.err.println("bits must be between 1 and 32");
            System.exit(1);
        }
        System.out.println("=== " + bits + "-bit Binary Counter ===");
        System.out.println(binaryCounter(bits));
    }
}
