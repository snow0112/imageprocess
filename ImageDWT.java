
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.*;
import java.io.*;
import java.util.TimerTask;
import javax.swing.*;

public class ImageDWT {

	JFrame frame;
	JLabel lbIm1;
	BufferedImage imgOne, imgTwo;
	int width = 512;//1920;
	int height = 512;//1080;
	Timer timer;
	int count = 0;

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


	class TimeListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e){
			if (count >= 9) {
				timer.stop();
				return;
			}
			count += 1;
			//scale += ds;
			//rotation += dr;
			//imgTwo = Transformation2( scale, rotation);
			//frame.remove(lbIm1);
            //Display(frame);
            System.out.println("animation");
		}
	}
	
	private void Display(JFrame frame){
		// Use label to display the image
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

    private void DWT(){}
    private BufferedImage IDWT(){
        return imgTwo;
    }
    
    public void showIms(String[] args){

		// Read a parameter from command line
		int n = Integer.parseInt(args[1]);
		
		
		// Read in the specified image
		imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		readImageRGB(width, height, args[0], imgOne);


		if (n != -1 ){

            // normal DWT
            imgTwo = imgOne;

			// Use label to display the image
			frame = new JFrame();
			Display(frame);

		}else{

            // progressive DWT (entire DWT to 0)

			int delay = 300;
			ActionListener listener = new TimeListener();
			timer = new Timer(delay, listener);
			frame = new JFrame();
			Display(frame);
			timer.start();

		}
	}
	public static void main(String[] args) {
		ImageDWT ren = new ImageDWT();
		ren.showIms(args);
	}
}
