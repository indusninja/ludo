package com.fuzzy;

public class TrapezoidalFunction extends PiecewiseLinearFunction {

	/*
	 * If the value is Left, the trapezoid has the left edge, but right is open
	 * (/--). If the value is Right, the trapezoid has the right edge, but left
	 * is open (--\).
	 */
	public enum EdgeType {
		Left, Right
	};

	private void init(int size) {
		points = new Point[size];
	}

	// "m1" X value where the degree of membership starts to raise.
	// "m2" X value where the degree of membership reaches the maximum value.
	// "m3" X value where the degree of membership starts to fall.
	// "m4" X value where the degree of membership reaches the minimum value.
	// "max" The maximum value that the membership will reach, [0, 1].
	// "min" The minimum value that the membership will reach, [0, 1].
	public TrapezoidalFunction(double m1, double m2, double m3, double m4,
			double max, double min) {
		init(4);
		points[0] = new Point(m1, min);
		points[1] = new Point(m2, max);
		points[2] = new Point(m3, max);
		points[3] = new Point(m4, min);
	}

	// multipoint Trapezoid with standard range of [0..1] for membership
	public TrapezoidalFunction(double m1, double m2, double m3, double m4) {
		this(m1, m2, m3, m4, 1.0, 0.0);
	}

	// Represents a Triangular membership - m1: bottom left side, m2: maximum
	// tip, m3: bottom right side
	public TrapezoidalFunction(double m1, double m2, double m3, double max,
			double min) {
		init(3);
		points[0] = new Point(m1, min);
		points[1] = new Point(m2, max);
		points[2] = new Point(m3, min);
	}

	public TrapezoidalFunction(double m1, double m2, double m3) {
		this(m1, m2, m3, 1.0, 0.0);
	}

	// Edge dependent Trapezoid - only 2 points for the fuzzy side towards the
	// edge
	public TrapezoidalFunction(double m1, double m2, double max, double min,
			EdgeType edge) {
		init(2);
		if (edge == EdgeType.Left) {
			points[0] = new Point(m1, min);
			points[1] = new Point(m2, max);
		} else {
			points[0] = new Point(m1, max);
			points[1] = new Point(m2, min);
		}
	}

	// Trapezoid with fuzziness only on the edge side with membership ranging
	// from [0..1]
	public TrapezoidalFunction(double m1, double m2, EdgeType edge) {
		this(m1, m2, 1.0, 0.0, edge);
	}
}
