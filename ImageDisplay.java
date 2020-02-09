
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.*;
import java.io.*;
import java.util.TimerTask;

import javax.swing.*;




public class ImageDisplay {

	JFrame frame;
	JLabel lbIm1;
	BufferedImage imgOne, imgTwo;
	int width = 512;//1920;
	int height = 512;//1080;
	float scale = 1;
	float rotation = 0;
	float ds;
	float dr;
	Timer timer;
	int total;
	int count = 0;
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

	private float rotate_x(float angle, int x, int y){
		double rad = Math.toRadians(angle);
		double ans = Math.cos(rad)*x - Math.sin(rad)*y;
		return (float)ans;
	}
	private float rotate_y(float angle, int x, int y){
		double rad = Math.toRadians(angle);
		double ans = Math.sin(rad)*x + Math.cos(rad)*y;
		return (float) ans;
	}

	public BufferedImage Transformation2(float scale, float rotation){
		// update from discussion (new rubric only show 512*512)
		BufferedImage new_img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		while(rotation < 0) rotation += 360;
		while(rotation >= 360) rotation -= 360;
		float angle = 360-rotation;

		int cent_x = width/2;
		int cent_y = height/2;

		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				int oldx = Math.round(rotate_x(angle, x - cent_x, y - cent_y)/scale) + cent_x;
				int oldy = Math.round(rotate_y(angle, x - cent_x, y - cent_y)/scale) + cent_y;
				int pix;
				if (oldx < 0 || oldy < 0 || oldx >= width || oldy >= height) pix = 0xffffffff;
				else pix = imgOne.getRGB(oldx, oldy);
				new_img.setRGB(x, y, pix);
			}
		}
		return new_img;
	}
	
	private void LPF(BufferedImage old_img, BufferedImage new_img){
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){

				int cnt = 0;
				int r = 0;
				int g = 0;
				int b = 0;

				for (int dx = -1; dx < 2; dx++){
					for (int dy = -1; dy < 2; dy++){
						if (x+dx >= 0 && x+dx < width && y+dy >=0 && y+dy < height ){
							cnt++;
							int p = old_img.getRGB(x+dx,y+dy);
							b += ( p & 0xff);
							p = p>>8;
							g += ( p & 0xff);
							p = p>>8;
							r += ( p & 0xff);
						}
					}
				}
				r /= cnt;
				g /= cnt;
				b /= cnt;
				int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
				new_img.setRGB(x, y, pix);

			}
		}

	}

	class TimeListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e){
			if (count >= total) {
				timer.stop();
				return;
			}
			count += 1;
			scale += ds;
			rotation += dr;
			imgTwo = Transformation2( scale, rotation);
			frame.remove(lbIm1);
			Display(frame);
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

	public void showIms(String[] args){

		// Read a parameter from command line
		
		int alising = Integer.parseInt(args[3]);
		float fps = Integer.parseInt(args[4]);
		float time = Integer.parseInt(args[5]);
		
		// Read in the specified image
		imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		readImageRGB(width, height, args[0], imgOne);


		// Aliasing
		if (alising == 1){
			imgTwo = new BufferedImage(width, height,  BufferedImage.TYPE_INT_RGB);
			LPF(imgOne, imgTwo);
		}

		if (time == 0 || fps == 0){

			// Transformation
			scale = Float.parseFloat(args[1]);
			rotation = Float.parseFloat(args[2]);
			imgTwo = Transformation2(scale, rotation);
			// Use label to display the image
			frame = new JFrame();
			Display(frame);

		}else{

			int delay = (int)(1000.0/fps);
			total = (int)(fps*time);
			float finalscale = Float.parseFloat(args[1]);
			float finalrotation = Float.parseFloat(args[2]);
			ds = (finalscale-1)/total;
			dr = finalrotation/total;
			ActionListener listener = new TimeListener();
			timer = new Timer(delay, listener);
			frame = new JFrame();
			Display(frame);
			timer.start();
		}
	}
	public static void main(String[] args) {
		ImageDisplay ren = new ImageDisplay();
		ren.showIms(args);
	}
}
