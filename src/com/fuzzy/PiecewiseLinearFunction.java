package com.fuzzy;

public class PiecewiseLinearFunction implements IMembershipFunction {
	// / <summary>
	// / Vector of (X,Y) coordinates for end/start of each line.
	// / </summary>
	protected Point[] points = null;

	protected PiecewiseLinearFunction() {
		points = null;
	}

	public PiecewiseLinearFunction(Point[] points) throws Exception {
		this.points = points;

		// check if X points are in a sequence and if Y values are in [0..1]
		// range
		for (int i = 0, n = points.length; i < n; i++) {
			if ((points[i].Y < 0) || (points[i].Y > 1))
				throw new Exception(
						"Y value of points must be in the range of [0, 1].");

			if (i == 0)
				continue;

			if (points[i - 1].X > points[i].X)
				throw new Exception(
						"Points must be in crescent order on X axis.");
		}
	}

	public double GetMembership(double x) {
		// no values belong to the fuzzy set, if there are no points in the
		// piecewise function
		if (points.length == 0)
			return 0.0;

		// if X value is less than the first point, so first point's Y will be
		// returned as membership
		if (x < points[0].X)
			return points[0].Y;

		// looking for the line that contains the X value
		for (int i = 1, n = points.length; i < n; i++) {
			// the line with X value starts in points[i-1].X and ends at
			// points[i].X
			if (x < points[i].X) {
				// points to calculate line's equation
				double y1 = points[i].Y;
				double y0 = points[i - 1].Y;
				double x1 = points[i].X;
				double x0 = points[i - 1].X;
				// angular coefficient
				double m = (y1 - y0) / (x1 - x0);
				// returning the membership - the Y value for this X
				return m * (x - x0) + y0;
			}
		}

		// X value is more than last point, so last point Y will be returned as
		// membership
		return points[points.length - 1].Y;
	}

	public double getLeftLimit() {
		return points[0].X;
	}

	public double getRightLimit() {
		return points[points.length - 1].X;
	}
}
