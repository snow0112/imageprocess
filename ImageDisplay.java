
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;


public class ImageDisplay {

	JFrame frame;
	JLabel lbIm1;
	BufferedImage imgOne, imgTwo;
	int width = 512;//1920;
	int height = 512;//1080;
	//private int deg;
	//private int x;
	//private int y;

	/** Read Image RGB
	 *  Reads the image of given width and height at the given imgPath into the provided BufferedImage.
	 */
	private void readImageRGB(int width, int height, String imgPath, BufferedImage img)
	{
		try
		{
			int frameLength = width*height*3;

			File file = new File(imgPath);
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			raf.seek(0);

			long len = frameLength;
			byte[] bytes = new byte[(int) len];

			raf.read(bytes);

			int ind = 0;
			for(int y = 0; y < height; y++)
			{
				for(int x = 0; x < width; x++)
				{
					byte a = 0;
					byte r = bytes[ind];
					byte g = bytes[ind+height*width];
					byte b = bytes[ind+height*width*2]; 

					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					//int pix = ((a << 24) + (r << 16) + (g << 8) + b);
					img.setRGB(x,y,pix);
					ind++;
				}
			}
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	private void Scale(float scale, BufferedImage old_img, BufferedImage new_img){
		int width2 = Math.round(width*scale);
		int height2 = Math.round(height*scale);
		//System.out.println("Scale to: " + width2 +","+ height2);
		//new_img = new BufferedImage(width2, height2,  BufferedImage.TYPE_INT_RGB);
		for(int y = 0; y < height2; y++)
			{
				for(int x = 0; x < width2; x++)
				{
					int oldx = Math.round(x/scale);
					int oldy = Math.round(y/scale);
					int pix = old_img.getRGB(oldx, oldy);
					new_img.setRGB(x, y, pix);
					//System.out.println(x+","+y);
				}
			}

	}
	
	private void Rotate(float angle, int width2, int height2, BufferedImage old_img, BufferedImage new_img){
		int cent_new_x = width2/2;
		int cent_new_y = height2/2;
		int cent_x = width/2;
		int cent_y = height/2;
		angle = 360-angle;
		for(int y = 0; y < height2; y++)
			{
				for(int x = 0; x < width2; x++)
				{
					int oldx = rotate_x(angle, x - cent_new_x, y - cent_new_y) + cent_x;
					int oldy = rotate_y(angle, x - cent_new_x, y - cent_new_y) + cent_y;
					int pix;
					if (oldx < 0 || oldy < 0 || oldx >= width || oldy >= height) pix = 0xffffffff;
					else pix = old_img.getRGB(oldx, oldy);
					new_img.setRGB(x, y, pix);
				}
			}
	}

	private int rotate_x(float angle, int x, int y){
		double rad = Math.toRadians(angle);
		double ans = Math.cos(rad)*x - Math.sin(rad)*y;
		return (int) ans;
	}
	private int rotate_y(float angle, int x, int y){
		double rad = Math.toRadians(angle);
		double ans = Math.sin(rad)*x + Math.cos(rad)*y;
		return (int) ans;
	}

	private void LPF(BufferedImage old_img, BufferedImage new_img){
		for(int y = 0; y < height; y++)
			{
				for(int x = 0; x < width; x++)
				{
					
				}
			}

	}
	public void showIms(String[] args){

		// Read a parameter from command line
		
		float scale = Float.parseFloat(args[1]);
		float rotation = Float.parseFloat(args[2]);
		int alising = Integer.parseInt(args[3]);
		int frames = Integer.parseInt(args[4]);
		int time = Integer.parseInt(args[5]);
		
		System.out.println("The second parameter (scale) was: " + scale);
		System.out.println("The third parameter (rotation) was: " + rotation);
		System.out.println("The forth parameter (alising) was: " + alising);
		System.out.println("The fifth parameter (frames) was: " + frames);
		System.out.println("The sixth parameter (time) was: " + time);
		
		// Read in the specified image
		imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		readImageRGB(width, height, args[0], imgOne);

		// Scale the image by scale
		if (scale != 1.0) {
			int width2 = Math.round(width*scale);
			int height2 = Math.round(height*scale);
			//System.out.println("Scale to: " + width2 +","+ height2);
			imgTwo = new BufferedImage(width2, height2,  BufferedImage.TYPE_INT_RGB);
			Scale(scale,imgOne, imgTwo);
			imgOne = imgTwo;
			width = width2;
			height = height2;
		}

		// Rotate the image
		while(rotation < 0) rotation += 360;
		while(rotation >= 360) rotation -= 360;
		if (rotation != 0){
			// calculate new size
			int left, right, up, low;
			left = 0; right = 0; up = 0; low = 0;
			int[][] corners = { {width,0},{0,height},{width,height} };
			for(int c = 0; c <3; c++){
				left = Math.min(left, rotate_x(rotation, corners[c][0], corners[c][1]));
				right = Math.max(right, rotate_x(rotation, corners[c][0], corners[c][1]));
				up = Math.min(up, rotate_y(rotation, corners[c][0], corners[c][1]));
				low = Math.max(low, rotate_y(rotation, corners[c][0], corners[c][1]));
			}
			int width2 = right - left;
			int height2 = low - up;
			//System.out.println("The third parameter (rotation) was: " + rotation + "; new size:" + width2 +","+ height2 );
			imgTwo = new BufferedImage(width2, height2,  BufferedImage.TYPE_INT_RGB);
			Rotate(rotation, width2, height2, imgOne, imgTwo);
			imgOne = imgTwo;
			width = width2;
			height = height2;
		}

		// Aliasing
		if (alising == 1){
			imgTwo = new BufferedImage(width, height,  BufferedImage.TYPE_INT_RGB);
			LPF(imgOne, imgTwo);
			imgOne = imgTwo;
		}

		// Use label to display the image
		frame = new JFrame();
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);

		lbIm1 = new JLabel(new ImageIcon(imgOne));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		frame.getContentPane().add(lbIm1, c);

		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		ImageDisplay ren = new ImageDisplay();
		ren.showIms(args);
	}

}
