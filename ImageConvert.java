
import java.awt.*;
import java.awt.desktop.SystemSleepEvent;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;


public class ImageConvert {

	JFrame frame;
	JLabel lbIm1;
	BufferedImage imgOne;
	int width = 512; //1920;
    int height = 512; //1080;
    double sq3 = Math.sqrt(3);
    float h1;
    float h2;


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
					//byte a = 0;
					int r = bytes[ind];
					int g = bytes[ind+height*width];
                    int b = bytes[ind+height*width*2]; 

                    double alpha = 0.5*(2*r - g - b);
                    double beta = 0.5*sq3*(g-b);
                    double H = Math.atan(alpha/beta)*180;
                    if (H < 0) H += 360;
                    int pix;
                    

                    if ( H >= h1 && H <= h2) pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                    else {
                        int gray = (r+g+b)/3;
                        pix = 0xff000000 | ((gray & 0xff) << 16) | ((gray & 0xff) << 8) | (gray & 0xff);
                        //System.out.println(H);
                    }

					//int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
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

	public void showIms(String[] args){

		// Read a parameter from command line
        h1 = Float.parseFloat(args[1]);
        h2 = Float.parseFloat(args[2]);
		//System.out.println("The second parameter was: " + param1);

		// Read in the specified image
		imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		readImageRGB(width, height, args[0], imgOne);

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
		ImageConvert ren = new ImageConvert();
		ren.showIms(args);
	}

}