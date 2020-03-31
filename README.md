# imagedisplay

It is the assignemnt of 2020 spring csci567 multimedia system design in USC.

The first part is about image scaling, rotation, alising.

To run the code from command line, first compile with:

>> javac ImageDisplay.java

and then, you can run it with:

>> java ImageDisplay Path_of_image S R A F T

Path_of_image is the name of the image, which will be provided in an 8 bit
per channel RGB format (Total 24 bits per pixel).

S will be a Scale value. This will control by how much the image has to be scaled. 

R will be a Rotation angle (given in degrees) that will suggest by how much the image has to be rotated about its center.

A will be a boolean value (0 or 1) suggesting whether or not you want to deal with Aliasing. A 0 signifies do nothing (aliasing will remain in your output) and a 1 signifies that anti-aliasing should be performed.

F will be number of Frames per second to render an animated transition from the initial image to the end state. This will a number greater than or equal to 0. A 0 signifies there is no animation and you would display just the final image.

T : Transition Time in seconds to show the transition from start to end state as.


The second part is about changing color space.

To run the code from command line, first compile with:

>> javac ImageConvert.java

and then, you can run it with:

>> java ImageConvert Path_of_image h1 h2

h1 and h2 will be a number between 0-360. This will provide the hue thresholds for deciding your segmentation boundary. 

h1 < h2

The image pixels are segmented based on two threshold values in the HSV color space, using the hue values as thresholds. All the pixels falling between these two hue thresholds will be displayed in the original color in the output image, whereas all the other pixels outside the threshold will be displayed in gray.


The third part is about image compression

To run the code from command line, first compile with:

>> javac ImageDWT.java

and then, you can run it with:

>> java ImageConvert Path_of_image n

n is an integral number from 0 to 9 that defines the low pass level to be used in decoding. For a given n, this translates to using 2n low pass coefficients in rows and columns respectively to use in the decoding process. 

Additionally, n could also take a value of -1 to show progressive decoding. In this case, it will go through the creation of the entire DWT representation till level 0. Then decode each level recursively and display the output. The first display will be at level 0, then level 1 and so on till reach level 9. You can see the image progressively improving with details.
