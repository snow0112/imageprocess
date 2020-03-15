
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
    double[][][] temp = new double[3][width][height];
    double[][][] coefficients = new double[3][width][height];
    double[][][] backup = new double[3][width][height]; 

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
            count++;
            for(int y = 0; y < height; y++){
                for(int x = 0; x < width; x++){
                    for (int channel = 0; channel < 3; channel++) {
                        coefficients[channel][x][y] = backup[channel][x][y];
                        temp[channel][x][y] = backup[channel][x][y];
                    }
                }
            }
            
            level = 0;
            while(level < count){
                IDWT( (int) Math.pow(2, level), 0 );
            }
            while (level < 9) IDWT( (int) Math.pow(2, level), 1 );
            drawing();
			frame.remove(lbIm1);
            Display(frame);
            
		}
    }

    private void add_detail(){
        int size =(int) Math.pow(2, count-1);
        for(int y = 0; y < height; y++) hp(-1,y,size);
        for(int x = 0; x < width; x++) hp(x, -1,size);
    }

    private void hp(int x, int y, int size){
        if (x == -1){

            System.out.println("x");
            
            for (int i = 0; i < size ; i++){
                double r2 = backup[0][i+size][y];
                double g2 = backup[1][i+size][y];
                double b2 = backup[2][i+size][y];

                for (int iter = 0; iter < width; iter++){
                    double xx = size*iter - i;
                    if (xx >= 0 && x < 0.5) {
                        coefficients[0][iter][y] += r2;
                        coefficients[1][iter][y] += g2;
                        coefficients[2][iter][y] += b2;
                    }
                    else if (xx >= 0.5 && xx < 1){
                        coefficients[0][iter][y] -= r2;
                        coefficients[1][iter][y] -= g2;
                        coefficients[2][iter++][y] -= b2;
                    }
                }
            }
        }
        else{
            System.out.println("y");
            for (int i = 0; i < size ; i++){
                double r2 = backup[0][x][i+size];
                double g2 = backup[1][x][i+size];
                double b2 = backup[2][x][i+size];

                for (int iter = 0; iter < height; iter++){
                    double xx = size*iter - i;
                    if (xx >= 0 && x < 0.5) {
                        coefficients[0][x][iter] += r2;
                        coefficients[1][x][iter] += g2;
                        coefficients[2][x][iter++] += b2;
                    }
                    else if (xx >= 0.5 && xx < 1){
                        coefficients[0][x][iter] -= r2;
                        coefficients[1][x][iter] -= g2;
                        coefficients[2][x][iter++] -= b2;
                    }
                }
            }
        }

    }
    
    private void drawing(){
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int r = (int)coefficients[0][x][y];
                int g = (int)coefficients[1][x][y];
                int b = (int)coefficients[2][x][y];
                int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                imgTwo.setRGB(x, y, pix);
            }
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
                double r1 = coefficients[0][iter][y];
                double g1 = coefficients[1][iter][y];
                double b1 = coefficients[2][iter++][y];
                double r2 = coefficients[0][iter][y];
                double g2 = coefficients[1][iter][y];
                double b2 = coefficients[2][iter++][y];
                temp[0][i][y] = (r1+r2)/2.0;
                temp[1][i][y] = (g1+g2)/2.0;
                temp[2][i][y] = (b1+b2)/2.0;
                temp[0][size+i][y] = (r1-r2)/2.0;
                temp[1][size+i][y] = (g1-g2)/2.0;
                temp[2][size+i][y] = (b1-b2)/2.0;
            }
        }
        else{
            int iter = 0;
            for (int i = 0; i < size ; i++){
                double r1 = coefficients[0][x][iter];
                double g1 = coefficients[1][x][iter];
                double b1 = coefficients[2][x][iter++];
                double r2 = coefficients[0][x][iter];
                double g2 = coefficients[1][x][iter];
                double b2 = coefficients[2][x][iter++];
                temp[0][x][i] = (r1+r2)/2.0;
                temp[1][x][i] = (g1+g2)/2.0;
                temp[2][x][i] = (b1+b2)/2.0;
                temp[0][x][size+i] = (r1-r2)/2.0;
                temp[1][x][size+i] = (g1-g2)/2.0;
                temp[2][x][size+i] = (b1-b2)/2.0;
                
            }
        }
    }

    private void updateimgone(){
        for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
                for (int channel = 0; channel < 3; channel++) coefficients[channel][x][y] = temp[channel][x][y];
            }
        }
    }

    private void DWT(int size){
        
        for(int x = 0; x < width; x++) encode(x, -1,size);
        updateimgone();
        for(int y = 0; y < height; y++) encode(-1,y,size);
        updateimgone();
        
    }
    
    private void decode(int x, int y, int size, int display){
        
        if (x == -1){
            int iter = 0;
            for (int i = 0; i < size ; i++){
                double r1 = coefficients[0][i][y];
                double g1 = coefficients[1][i][y];
                double b1 = coefficients[2][i][y];

                double r2 = coefficients[0][i+size][y];
                double g2 = coefficients[1][i+size][y];
                double b2 = coefficients[2][i+size][y];
                if (display == 1) {
                    r2 = 0;
                    g2 = 0;
                    b2 = 0;
                }

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

                double r1 = coefficients[0][x][i];
                double g1 = coefficients[1][x][i];
                double b1 = coefficients[2][x][i];
            
                double r2 = coefficients[0][x][i+size];
                double g2 = coefficients[1][x][i+size];
                double b2 = coefficients[2][x][i+size];
                if (display == 1) {
                    r2 = 0;
                    g2 = 0;
                    b2 = 0;
                }
                temp[0][x][iter] = (r1+r2);
                temp[1][x][iter] = (g1+g2);
                temp[2][x][iter++] = (b1+b2);
                temp[0][x][iter] = (r1-r2);
                temp[1][x][iter] = (g1-g2);
                temp[2][x][iter++] = (b1-b2);
            }
        }
    }

    private void IDWT(int size, int display){
        for(int y = 0; y < height; y++) decode(-1,y,size,display);
        updateimgone();
        for(int x = 0; x < width; x++) decode(x, -1,size,display);
        updateimgone();
        level++;
    }
    
    public void showIms(String[] args){

		// Read a parameter from command line
		int n = Integer.parseInt(args[1]);
		
		// Read in the specified image
		imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        readImageRGB(width, height, args[0], imgOne);
        imgTwo = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
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
                IDWT( (int) Math.pow(2, level), 1 );
            }
            // Use label to display the image
            drawing();
			frame = new JFrame();
			Display(frame);

		}else{

            // progressive DWT
            // entire DWT to 0
            while (level > count){
                DWT(  (int) Math.pow(2, level-1));
                level--;
            }
            // back up coefficients at level 0
            for(int y = 0; y < height; y++){
                for(int x = 0; x < width; x++){
                    for (int channel = 0; channel < 3; channel++) backup[channel][x][y] = coefficients[channel][x][y];
                }
            }
            
			int delay = 300;
			ActionListener listener = new TimeListener();
            timer = new Timer(delay, listener);

            while (level < 9) IDWT( (int) Math.pow(2, level), 1 );
            drawing();

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
