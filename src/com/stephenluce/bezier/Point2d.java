package com.stephenluce.bezier;

public class Point2d implements Point {
	double x, y;

	@Override
	public double get(int index) {
		switch (index) {
		case 0:
			return x;
		case 1:
			return y;
		}
		return 0;
	}
}
