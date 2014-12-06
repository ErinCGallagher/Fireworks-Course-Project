package firework;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel; 
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.applet.AudioClip;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Font;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.awt.Container;
import java.io.File;
import java.io.IOException;


public class Window extends JFrame{

	private JPanel mainPanel = new JPanel();
	private GridBagLayout layout;
	private GridBagConstraints constraints;

	private JLabel clockRunningLabel = null;
	private JLabel clockLabel;
	private  JButton StartButton = null;

	private double initialValue = 0;//Gives the spinners an initial value of 0
	JSpinner AngleSpinner; //determines the launch Angle
	JSpinner WindSpinner;//determines the wind velocity
	private double wind;//wind velocity in m/s
	private double angle;//launch angle in degrees

	private Timer clockTimer;
	private long startTime = System.currentTimeMillis();
	private double timeCounter = 0;//initializes the clockTime to 0
	int delay = 17; // milliseconds --> put in particle manager (time-lastTime)

	private ParticleManager manager;

	private BufferedImage backgroundImage;
	private BufferedImage img = null;
	private ImagePanel drawZone;
	private Graphics2D gDraw;
	int width, height;

	// The constructor for my Window class
	public Window () {

		// Call the constructor for JFrame
		super();
		Initializer();
	}

	//initializes the basic properties for the functioning window
	private void Initializer() {	

		setLayout(new BorderLayout());
		setTitle("Firework Display");

		// Clicking on the "X" close button will close the window and end the program.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Two different font sizes are used here.
		Font fTop = new Font("Arial", Font.PLAIN, 20);
		Font fRest = new Font("Arial", Font.BOLD, 16);


		clockRunningLabel = new JLabel();
		clockRunningLabel.setBounds(new Rectangle(15, 97, 129, 27));
		clockRunningLabel.setText("");
		clockRunningLabel.setFont(fRest);
		clockLabel = new JLabel();
		clockLabel.setBounds(new Rectangle(47, 58, 99, 30));
		clockLabel.setText("seconds");
		clockLabel.setFont(fRest);
		add(clockLabel, null);
		add(clockRunningLabel, null);

		//Adds the start button
		add(getStartButton(), null);

		//Adds the exit button
		JButton exitButton = new JButton("Exit");
		exitButton.setFont(fRest);
		exitButton.addActionListener(new EndingListener());


		// Create the larger column labels and put them at the top of the window.
		JLabel WindLabel = new JLabel("Wind Velocity");
		WindLabel.setHorizontalAlignment(JLabel.CENTER);
		WindLabel.setFont(fTop);
		JLabel AngleLabel = new JLabel("Launch Angle");
		AngleLabel.setHorizontalAlignment(JLabel.CENTER);
		AngleLabel.setFont(fTop);


		//Constructor for a number spinner with initial double value of 0 and step of 0.5
		SpinnerModel values = new SpinnerNumberModel(initialValue, initialValue - 10.0, initialValue +  10.0, 0.5);
		//create the Firing angle spinner
		AngleSpinner = new JSpinner(values);
		AngleSpinner.addChangeListener(new SpinnerListenerA());

		//Constructor for a number spinner with initial double value of 0 and step of 0.5
		SpinnerModel values2 = new SpinnerNumberModel(initialValue, initialValue - 20.0, initialValue +  20.0, 0.5);
		//create the Wind Velocity spinner
		WindSpinner = new JSpinner(values2);
		WindSpinner.addChangeListener(new SpinnerListenerW());


		//Creates the Right justified layout for the buttons at the bottom of the window
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bottomPanel.add(clockRunningLabel);
		bottomPanel.add(clockLabel);
		bottomPanel.add(StartButton);
		bottomPanel.add(exitButton);


		//create a drawing zone for the firework display
		drawZone = new ImagePanel();

		//open and read the background image 	
		try {
			backgroundImage = ImageIO.read(new File("cityScape.jpg"));
		} catch (IOException s) {
			System.out.println("could not find a valid image");
			System.exit(1);
		}


		//creates the Gridbag layout used for the buttons in the window
		layout = new GridBagLayout();
		mainPanel.setLayout(layout);
		constraints = new GridBagConstraints();

		Insets somePadding = new Insets(5, 10, 10, 10);
		Insets leftPadding = new Insets(5, 40, 5, 5);

		constraints.fill = GridBagConstraints.BOTH;

		//	GridBag Layout				r, c, w, h, padding
		addComponent(WindLabel,		    4, 1, 1, 1, somePadding, 0, 0, 0);
		addComponent(AngleLabel, 		4, 3, 1, 1, somePadding, 0, 0, 0);
		addComponent(WindSpinner,       4, 2, 1, 1, somePadding, 0, 0, 0);
		addComponent(AngleSpinner,      4, 4, 1, 1, somePadding, 0, 0, 0);


		add(mainPanel, BorderLayout.NORTH);
		add(bottomPanel, BorderLayout.SOUTH);
		add(drawZone, BorderLayout.CENTER);


		clockTimer = new Timer(delay, new ClockListener());

		pack();

	}		


	//Takes the supplied value from the Gridbag layout construction and creates
	//the proper links to the specific components
	private void addComponent(Component component, int row, int column, int width, int height,
			Insets outsidePad, int insidePad, double weightx, double weighty) {
		constraints.gridx = column;
		constraints.gridy = row;
		constraints.gridwidth = width;
		constraints.gridheight = height;
		constraints.insets = outsidePad;
		constraints.ipady = insidePad;
		constraints.weightx = weightx;
		constraints.weighty = weighty;
		layout.setConstraints(component, constraints);
		mainPanel.add(component);
	} // end addComponent


	//A ChangeListerner for the launch Angle input
	private class SpinnerListenerA implements ChangeListener {
		public void stateChanged(ChangeEvent cEvent) {

		}
	}

	//A ChangeListener for the Wind Velocity Input
	private class SpinnerListenerW implements ChangeListener {
		public void stateChanged(ChangeEvent cEvent) {
		}
	}


	// Carried out by exit button
	private class EndingListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// Just close the window and halt the program.
			System.exit(0);
		}
	} // end EndingListener class


	// The Listener class for the start button.
	private JButton getStartButton() {
		if (StartButton == null) {
			StartButton = new JButton();
			StartButton.setFont(new Font("Arial", Font.BOLD, 16));
			StartButton.setText("Start");
			StartButton.addActionListener(new ActionListener() {


				//performed everytime the start button is pressed        	
				public void actionPerformed(ActionEvent e) {
					if (clockTimer.isRunning())
						clockTimer.stop();

					//resets the start time to the current time in ms
					startTime=System.currentTimeMillis();

					img =new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); 
					gDraw = img.createGraphics();

					angle = (double) AngleSpinner.getValue(); //gets the value from spinner
					wind = (double) WindSpinner.getValue(); //gets the value from spinner

					// Changes the "running" label
					clockRunningLabel.setText("  Running");
					clockRunningLabel.setForeground(Color.GREEN);


					//instantiates the particle Manager
					try {
						manager = new ParticleManager(wind, angle);
					} catch (EnvironmentException f) {
						System.out.println(f.getMessage());
						return;
					} catch (EmitterException t) {
						System.out.println(t.getMessage());			
						return;
					}

					drawZone.repaint();

					//calls the start function in the Particle Manager supplying it with the current time
					//Particles start being launched
					manager.start(0);
					//starts the timer
					clockTimer.start();

				}
			});

		}

		return StartButton;
	} // end getStartButton


	//The clockListener is called when the start button is pressed
	private class ClockListener implements ActionListener {


		public ClockListener() {
			super();
		}

		public void actionPerformed(ActionEvent e) {

			drawZone.repaint();//repaints the imagePanel	
		}
	}  

	private class ImagePanel extends JPanel {
		double[] position = null;
		int renderSize;
		ArrayList<Particle> fireworks;

		public ImagePanel() {
			super();

		}

		public void paint(Graphics g) {
			super.paint(g);

			width = getWidth();
			height = getHeight();

			Graphics2D g2D = (Graphics2D)g;
			g2D.clearRect(0, 0, width, height);

			if (img != null){
				g2D.drawImage(img, 0,0, null);

				g.drawImage(backgroundImage,0,0,width,height,0,0, backgroundImage.getWidth(), backgroundImage.getHeight(), null);

				//calculates the timedisplayed on the windo and converts it to seconds
				timeCounter = ((System.currentTimeMillis()-startTime)/1000.0);

				// Display the number of seconds on the Window.
				clockLabel.setText("" + timeCounter);

				//passes in delay so the Runge-kutta knows the time interval
				fireworks = manager.getFireworks(timeCounter, delay/1000.0);

				//Runs through the ArrayList at each interval and gets the position, renderSize and colour values
				for (Particle firework : fireworks){
					position  = firework.getPosition();
					renderSize = firework.getRenderSize();

					//The width of the and height of the imagePanel is 24 meters
					//converts meters to pixels
					int x = (int) ((width+20)/2 + width*(position[0]/24));
					int y = (int) ((height-25) - height*(position[1]/24));
					g.setColor(firework.getColour());
					g.fillOval(x, y, renderSize, renderSize);


				}
				if (fireworks.size() == 0)
					clockTimer.stop();

				//create launchTube
				g.setColor(Color.red);
				g.fillRect(width/2, height-40, 20, 50);
			}
		}
	}     

}



