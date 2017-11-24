package com.xiaotian.frameworkxt.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * 
 * @version 1.0.0
 * @author XiaoTian
 * @name UtilRandomCodeImage
 * @description 随机码
 * @date Oct 30, 2014
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2009-2014 广州隽永贸易科技 Ltd, All Rights Reserved.
 */
public class UtilRandomVerificationCodeImage {
	public static BufferedImage RandomImage(int width, int height) {
		BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = buffImg.createGraphics();
		// Create a random number generator class.
		Random random = new Random();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		// Create the font, font size should be based on the height of the picture.
		Font font = new Font("Times New Roman", Font.PLAIN, 18);
		// Set the font.
		g.setFont(font);
		// Draw the border.
		g.setColor(Color.black);
		g.drawRect(0, 0, width - 1, height - 1);
		// Randomly generated 160 interference lines, other programs can not easily be detected in the image authentication code.
		g.setColor(Color.gray);
		for (int i = 0; i < 10; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = random.nextInt(12);
			int yl = random.nextInt(12);
			g.drawLine(x, y, x + xl, y + yl);
		}
		// RandomCode used to save the randomly generated verification code in order to verify user login.
		StringBuffer randomCode = new StringBuffer();
		int red = 0, green = 0, blue = 0;
		// Randomly generated four-digit verification code.
		for (int i = 0; i < 4; i++) {
			// Get the number of randomly generated verification code.
			String strRand = String.valueOf(random.nextInt(10));
			// Generate random color components to construct the color values, so that the output of the color value of each digit will be different.
			red = random.nextInt(200);
			green = random.nextInt(200);
			blue = random.nextInt(200);
			// Generate random height between 13 to height
			float imght = 0;
			while (imght <= 12) {
				imght = Float.parseFloat(String.valueOf(random.nextInt(height)));
			}
			// Randomly generated color the verification code drawn to the image.
			g.setColor(new Color(red, green, blue));
			g.drawString(strRand, 13 * i + 6, imght);
			// Will produce four random number together.
			randomCode.append(strRand);
		}
		return buffImg;
	}
}
