// Crawler.java
// This is the main entry point for the Job-Crawler application. It creates the main window and tabbed interface for job site crawlers.
package Crawler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class Crawler extends JFrame {
	// Self-reference for inner class usage
	Crawler self = this;
	// Tab names for each job site
	final static String tabNameSaramIn = "사람인";
	final static String tabNameJobKorea = "잡코리아";
	final static String tabNameIncruit = "인쿠르트";
	final static int extraWindowWidth = 100;

	// Constructor: sets up look and feel, then creates the GUI
	public Crawler() {
		System.out.println("C_Recruitment 1");
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
			UIManager.put("nimbusBase", Color.WHITE);
			UIManager.put("nimbusBlueGrey", Color.WHITE);
			UIManager.put("control", Color.WHITE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("2 createAndShowGUI();");
		createAndShowGUI();
	}

	// Adds the tabbed pane with each job site panel to the main window
	public static void addComponentToPane(Container pane) {
		System.out.println(3);
		JTabbedPane tabbedPane = new JTabbedPane();
		// Create the "cards" (tabs)

		JPanel card1 = new JPanel() {
			// Override preferred size to add extra width
			public Dimension getPreferredSize() {
				Dimension size = super.getPreferredSize();
				size.width += extraWindowWidth;
				return size;
			}
		};
		C_JSoupCrawlerIncruit incruitPane = new C_JSoupCrawlerIncruit(null);
		card1.add(incruitPane);
		incruitPane.setPreferredSize(new Dimension(1400, 500));

		JPanel card2 = new JPanel();
		C_JSoupCrawlerJobkorea jobkoreaPane = new C_JSoupCrawlerJobkorea(null);
		jobkoreaPane.setPreferredSize(new Dimension(1400, 500));
		card2.add(jobkoreaPane);

		JPanel card3 = new JPanel();
		C_JSoupCrawlerSaramIn saramin = new C_JSoupCrawlerSaramIn(null);
		saramin.setPreferredSize(new Dimension(1400, 500));
		card3.add(saramin);

		tabbedPane.addTab(tabNameIncruit, card1);
		tabbedPane.addTab(tabNameJobKorea, card2);
		tabbedPane.addTab(tabNameSaramIn, card3);

		pane.add(tabbedPane, BorderLayout.SOUTH);
	}

	// Creates and shows the main application window
	private static void createAndShowGUI() {
		System.out.println("2");
		JDialog frame = new JDialog();
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		System.out.println("4");
		addComponentToPane(frame.getContentPane());
		// Show the window
		frame.pack();
		frame.setVisible(true);
	}
	
	// Main method: launches the application
	public static void main(String[] args) {
		new Crawler();
	}
	
}
