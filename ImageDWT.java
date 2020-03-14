
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
	BufferedImage imgOne, imgTwo, imgcoe;
	int width = 512;//1920;
	int height = 512;//1080;
	Timer timer;
    int count = 0;
    int level = 9;

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

    private int newc(int pix1, int pix2){
        int r1 = (pix1 >> 16) & 0xff;
        int g1 = (pix1 >> 8) & 0xff;
        int b1 = (pix1) & 0xff;
        int r2 = (pix2 >> 16) & 0xff;
        int g2 = (pix2 >> 8) & 0xff;
        int b2 = (pix2) & 0xff;

        int r = (r1 + r2) /2;
        int g = (g1 + g2) /2;
        int b = (b1 + b2) /2;
        int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
        return pix;
    }

    private int newd(int pix1, int pix2){
        int r1 = (pix1 >> 16) & 0xff;
        int g1 = (pix1 >> 8) & 0xff;
        int b1 = (pix1) & 0xff;
        int r2 = (pix2 >> 16) & 0xff;
        int g2 = (pix2 >> 8) & 0xff;
        int b2 = (pix2) & 0xff;

        int r = (r1 - r2) /2;
        int g = (g1 - g2) /2;
        int b = (b1 - b2) /2;
        int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
        return pix;
    }
    
    private void encode(int x, int y, int size){
        //System.out.println(x +" "+y +" "+ size);
        if (x == -1){
            //System.out.println(x +" "+y +" "+ size);
            int iter = 0;
            for (int i = 0; i < size ; i++){
                int pix1 = imgOne.getRGB(iter++,y);
                int pix2 = imgOne.getRGB(iter++,y);
                imgcoe.setRGB(i, y, newc(pix1,pix2));
                imgcoe.setRGB(size+i, y, newd(pix1,pix2));  
            }
        }
        else{
            int iter = 0;
            for (int i = 0; i < size ; i++){
                int pix1 = imgOne.getRGB(x,iter++);
                int pix2 = imgOne.getRGB(x,iter++);
                imgcoe.setRGB(x, i, newc(pix1,pix2));
                imgcoe.setRGB(x, size+i, newd(pix1,pix2));
                
            }
        }
    }

    private void updateimgone(){
        for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
                imgOne.setRGB(x, y, imgcoe.getRGB(x, y));
            }
        }
    }

    private void DWT(int size){
        
        for(int x = 0; x < width; x++) encode(x, -1,size);
        updateimgone();
        for(int y = 0; y < height; y++) encode(-1,y,size);
        updateimgone();
        
    }

    private int oldc1(int pix1, int pix2){
        int r1 = (pix1 >> 16) & 0xff;
        int g1 = (pix1 >> 8) & 0xff;
        int b1 = (pix1) & 0xff;
        int r2 = (pix2 >> 16) & 0xff;
        int g2 = (pix2 >> 8) & 0xff;
        int b2 = (pix2) & 0xff;
        //r2 = 0; g2 = 0; b2 = 0;
        //System.out.println(pix2+" "+r2+" "+g2+" "+b2);
        
        int r = (r1 + r2);
        int g = (g1 + g2);
        int b = (b1 + b2);
        int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
        return pix;
    }

    private int oldc2(int pix1, int pix2){
        int r1 = (pix1 >> 16) & 0xff;
        int g1 = (pix1 >> 8) & 0xff;
        int b1 = (pix1) & 0xff;
        int r2 = (pix2 >> 16) & 0xff;
        int g2 = (pix2 >> 8) & 0xff;
        int b2 = (pix2) & 0xff;
        //r2 = 0; g2 = 0; b2 = 0;

        int r = (r1 - r2);
        int g = (g1 - g2);
        int b = (b1 - b2);
        int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
        return pix;
    }
    
    private void decode(int x, int y, int size){
        
        if (x == -1){
            int iter = 0;
            for (int i = 0; i < size ; i++){
                int pix1 = imgOne.getRGB(i,y);
                int pix2 = imgOne.getRGB(size+i,y);
                System.out.println(pix2);
                imgcoe.setRGB(iter++, y, oldc1(pix1,pix2));
                imgcoe.setRGB(iter++, y, oldc2(pix1,pix2));  
            }
        }
        else{
            int iter = 0;
            for (int i = 0; i < size ; i++){
                int pix1 = imgOne.getRGB(x,i);
                int pix2 = imgOne.getRGB(x,size+i);
                imgcoe.setRGB(x, iter++, oldc1(pix1,pix2));
                imgcoe.setRGB(x, iter++, oldc2(pix1,pix2));
            }
        }
    }

    private void IDWT(int size){
        for(int y = 0; y < height; y++) decode(-1,y,size);
        updateimgone();
        for(int x = 0; x < width; x++) decode(x, -1,size);
        updateimgone();
    }
    
    public void showIms(String[] args){

		// Read a parameter from command line
		int n = Integer.parseInt(args[1]);
		
		// Read in the specified image
		imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        readImageRGB(width, height, args[0], imgOne);
        imgcoe = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
                imgcoe.setRGB(x, y, imgOne.getRGB(x, y));
            }
        }

		if (n != -1 ){

            // normal DWT
            while (level > n){
                DWT(  (int) Math.pow(2, level-1));
                level--;
            }

            double size = Math.pow(2, level);
            int pix0 = 0xff000000;
            for(int y = 0; y < height; y++){
				for(int x = 0; x < width; x++){
                    if (x >= size || y >= size) imgOne.setRGB(x, y, pix0);
                }
            }

            while (level < 9){
                IDWT( (int) Math.pow(2, level) );
                level++;
            }
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
