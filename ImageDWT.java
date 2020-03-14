
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
    int[][][] temp = new int[3][width][height];
    int[][][] coefficients = new int[3][width][height];

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
    
    private void encode(int x, int y, int size){
        //System.out.println(x +" "+y +" "+ size);
        if (x == -1){
            //System.out.println(x +" "+y +" "+ size);
            int iter = 0;
            for (int i = 0; i < size ; i++){
                //int pix1 = imgOne.getRGB(iter++,y);
                //int pix2 = imgOne.getRGB(iter++,y);
                int r1 = coefficients[0][iter][y];
                int g1 = coefficients[1][iter][y];
                int b1 = coefficients[2][iter++][y];
                int r2 = coefficients[0][iter][y];
                int g2 = coefficients[1][iter][y];
                int b2 = coefficients[2][iter++][y];
                temp[0][i][y] = (r1+r2)/2;
                temp[1][i][y] = (g1+g2)/2;
                temp[2][i][y] = (b1+b2)/2;
                temp[0][size+i][y] = (r1-r2)/2;
                temp[1][size+i][y] = (g1-g2)/2;
                temp[2][size+i][y] = (b1-b2)/2;
            }
        }
        else{
            int iter = 0;
            for (int i = 0; i < size ; i++){
                int r1 = coefficients[0][x][iter];
                int g1 = coefficients[1][x][iter];
                int b1 = coefficients[2][x][iter++];
                int r2 = coefficients[0][x][iter];
                int g2 = coefficients[1][x][iter];
                int b2 = coefficients[2][x][iter++];
                temp[0][x][i] = (r1+r2)/2;
                temp[1][x][i] = (g1+g2)/2;
                temp[2][x][i] = (b1+b2)/2;
                temp[0][x][size+i] = (r1-r2)/2;
                temp[1][x][size+i] = (g1-g2)/2;
                temp[2][x][size+i] = (b1-b2)/2;
                
            }
        }
    }

    private void updateimgone(){
        for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
                coefficients[0][x][y] = temp[0][x][y];
                coefficients[1][x][y] = temp[1][x][y];;
                coefficients[2][x][y] = temp[2][x][y];;
            }
        }
    }

    private void DWT(int size){
        
        for(int x = 0; x < width; x++) encode(x, -1,size);
        updateimgone();
        for(int y = 0; y < height; y++) encode(-1,y,size);
        updateimgone();
        
    }
    
    private void decode(int x, int y, int size){
        
        if (x == -1){
            int iter = 0;
            for (int i = 0; i < size ; i++){
                int r1 = coefficients[0][i][y];
                int g1 = coefficients[1][i][y];
                int b1 = coefficients[2][i][y];
                int r2 = 0;
                int g2 = 0;
                int b2 = 0;
                temp[0][iter][y] = (r1+r2);
                temp[1][iter][y] = (g1+g2);
                temp[2][iter++][y] = (b1+b2);
                temp[0][iter][y] = (r1-r2);
                temp[1][iter][y] = (g1-g2);
                temp[2][iter++][y] = (b1-b2);
            }
        }
        else{
            int iter = 0;
            for (int i = 0; i < size ; i++){

                int r1 = coefficients[0][x][i];
                int g1 = coefficients[1][x][i];
                int b1 = coefficients[2][x][i];
                int r2 = 0;
                int g2 = 0;
                int b2 = 0;
                temp[0][x][iter] = (r1+r2);
                temp[1][x][iter] = (g1+g2);
                temp[2][x][iter++] = (b1+b2);
                temp[0][x][iter] = (r1-r2);
                temp[1][x][iter] = (g1-g2);
                temp[2][x][iter++] = (b1-b2);
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
                int pix = imgOne.getRGB(x, y);
                int r = (pix >> 16) & 0xff;
                int g = (pix >> 8) & 0xff;
                int b = (pix) & 0xff;
                coefficients[0][x][y] = r;
                coefficients[1][x][y] = g;
                coefficients[2][x][y] = b;
                temp[0][x][y] = r;
                temp[1][x][y] = g;
                temp[2][x][y] = b;
            }
        }

		if (n != -1 ){

            // normal DWT
            while (level > n){
                DWT(  (int) Math.pow(2, level-1));
                level--;
            }

            while (level < 9){
                IDWT( (int) Math.pow(2, level) );
                level++;
            }
            for(int y = 0; y < height; y++){
                for(int x = 0; x < width; x++){
                    int r = coefficients[0][x][y];
                    int g = coefficients[1][x][y];
                    int b = coefficients[2][x][y];
                    int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                    imgOne.setRGB(x, y, pix);
                }
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
