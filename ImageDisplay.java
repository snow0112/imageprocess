
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

					int pix = old_img.getRGB(x, y);
					new_img.setRGB(x, y, pix);
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
			System.out.println("Scale to: " + width2 +","+ height2);
			imgTwo = new BufferedImage(width2, height2,  BufferedImage.TYPE_INT_RGB);
			Scale(scale,imgOne, imgTwo);
		}

		// Use label to display the image
		frame = new JFrame();
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);

		lbIm1 = new JLabel(new ImageIcon(imgTwo));

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
