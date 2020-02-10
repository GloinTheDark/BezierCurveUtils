package com.stephenluce.bezier;
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

public class Bezier3 {

	// A real-cube-roots-only function:
	static double cuberoot(double v) {
		if (v < 0) {
			return -Math.pow(-v, 1.0 / 3.0);
		}
		return Math.pow(v, 1.0 / 3.0);
	}

	/**
	 * filters, sorts and removes duplicates
	 * @param d input list of doubles
	 * @return filtered sorted output
	 */
	static List<Double> filterSort(double... d) {
		TreeSet<Double> set = new TreeSet<Double>();
		for (double e : d) {
			if (0 <= e && e <= 1) {
				set.add(e);
			}
		}
		return new ArrayList<Double>(set);
	}
		
	static double castlejau(double u, double a, double b, double c, double d) {
		double v = 1 - u;
		
		double ab = a * v + b * u;
		double bc = b * v + c * u;
		double cd = c * v + d * u;

		double abc = ab * v + bc * u;
		double bcd = bc * v + cd * u;

		double abcd = abc * v + bcd * u;
		
		return abcd;
	}
	
	/**
	 * 
	 * @param d any number of double arguments
	 * @return The arguments as a double[]
	 */
	static double[] doubles(double...d) {
		return d;
	}

	/*
	 * Splits a Bezier curve into two at location u.
	 * 
	 * Returns 2 sets of 4 points.
	 */
	static double[][] split(double u, double a, double b, double c, double d) {
		double v = 1 - u;
		
		double ab = a * v + b * u;
		double bc = b * v + c * u;
		double cd = c * v + d * u;

		double abc = ab * v + bc * u;
		double bcd = bc * v + cd * u;

		double abcd = abc * v + bcd * u;
		
		return new double[][] {doubles(a,ab,abc,abcd),doubles(abcd,bcd,cd,d)};
	}

	// Now then: given cubic coordinates {pa, pb, pc, pd} find all roots.
	// Cardano's Algorithm
	// Adapted from...
	// https://pomax.github.io/bezierinfo/#extremities
	static List<Double> getCubicRoots(double pa, double pb, double pc, double pd) {
		double a = (3 * pa - 6 * pb + 3 * pc);
		double b = (-3 * pa + 3 * pb);
		double c = pa;
		double d = (-pa + 3 * pb - 3 * pc + pd);

		// do a check to see whether we even need cubic solving:
		if (approximately(d, 0)) {
			// this is not a cubic curve.
			if (approximately(a, 0)) {
				// in fact, this is not a quadratic curve either.
				if (approximately(b, 0)) {
					// in fact in fact, there are no solutions.
					return filterSort();
				}
				// linear solution
				return filterSort(-c / b);
			}
			// quadratic solution
			double q = Math.sqrt(b * b - 4 * a * c);
			double a2 = 2 * a;
			return filterSort((q - b) / a2, (-b - q) / a2);
		}

		// at this point, we know we need a cubic solution.

		a /= d;
		b /= d;
		c /= d;

		double p = (3 * b - a * a) / 3;
		double p3 = p / 3;
		double q = (2 * a * a * a - 9 * a * b + 27 * c) / 27;
		double q2 = q / 2;
		double discriminant = q2 * q2 + p3 * p3 * p3;

		// and some variables we're going to use later on:
		double u1;
		double v1;
		double root1;
		double root2;
		double root3;

		// three possible real roots:
		if (discriminant < 0) {
			double mp3 = -p / 3;
			double mp33 = mp3 * mp3 * mp3;
			double r = Math.sqrt(mp33);
			double t = -q / (2 * r);
			double cosphi = t < -1 ? -1 : t > 1 ? 1 : t;
			double phi = Math.acos(cosphi);
			double crtr = cuberoot(r);
			double t1 = 2 * crtr;
			root1 = t1 * Math.cos(phi / 3) - a / 3;
			root2 = t1 * Math.cos((phi + 2 * Math.PI) / 3) - a / 3;
			root3 = t1 * Math.cos((phi + 4 * Math.PI) / 3) - a / 3;
			return filterSort(root1, root2, root3);
		}

		// three real roots, but two of them are equal:
		if (discriminant == 0) {
			u1 = q2 < 0 ? cuberoot(-q2) : -cuberoot(q2);
			root1 = 2 * u1 - a / 3;
			root2 = -u1 - a / 3;
			return filterSort(root1, root2);
		}

		// one real root, two complex roots
		double sd = Math.sqrt(discriminant);
		u1 = cuberoot(sd - q2);
		v1 = cuberoot(sd + q2);
		root1 = u1 - v1 - a / 3;
		return filterSort(root1);
	}

	private static boolean approximately(double a, double b) {
		return Math.abs(a - b) < 1e-15;
	}

	public static void main(String[] args) {

		Random r = new Random();

		for (int i = 0; i < 100; i++) {
			int a = r.nextInt(19) - 9;
			int b = r.nextInt(19) - 9;
			int c = r.nextInt(19) - 9;
			int d = r.nextInt(19) - 9;

			t1(a, b, c, d);
		}
		
		t1(-3,1,1,-3);
	}

	private static void t1(int a, int b, int c, int d) {
		List<Double> cubicRoots = getCubicRoots(a, b, c, d);
		System.out.printf("(%2d,%2d,%2d,%2d) = %s\n", a, b, c, d, cubicRoots);
	}

	
}
