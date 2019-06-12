package com.myGDX.snake;

public abstract class Util {
	public static boolean isOverlapping(int x1, int y1, int h1, int w1, int x2, int y2, int h2, int w2){
		int top1 = y1 + h1;
		int right1 = x1 + w1;
		int top2 = y2 + h2;
		int right2 = x2 + w2;
		return !(y1 >= top2 || y2 >= top1 || x1 >= right2 || x2 >= right1);

	}
}