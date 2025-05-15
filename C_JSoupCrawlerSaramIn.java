package Crawler;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXTable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

// C_JSoupCrawlerSaramIn.java
// This class handles crawling job postings from Saramin using Selenium and JSoup, and displays them in a Swing table UI.
public class C_JSoupCrawlerSaramIn extends JPanel {

	// Stores the URL after Selenium search
	public static String saramInUrl;

	// Self-reference for inner class usage
	C_JSoupCrawlerSaramIn self = this;
	// Unused, but could be used for clickable text
	JEditorPane clickableTextPane = new JEditorPane();

	// Table headers for job info and links
	Object[] DefaultHeaderTitles = { "회사명", "공고", "조건" };
	Object[] DefaultHeaderLinks = { "링크", "URL" };

	// Table models for job info and links
	private DefaultTableModel dtmt = new DefaultTableModel(DefaultHeaderTitles, 0);
	private DefaultTableModel dtmb = new DefaultTableModel(DefaultHeaderLinks, 0);

	// Tables for displaying job info and link buttons
	JXTable tableSetTitle = new JXTable(dtmt);
	JXTable tableSetButtons = new JXTable(dtmb);

	// Scroll panes for tables
	JScrollPane scrollForTitle = new JScrollPane(tableSetTitle);
	JScrollPane scrollForButtons = new JScrollPane(tableSetButtons);

	// Wrapper panel for layout
	JPanel wrapper = new JPanel();
	JScrollPane scroll = new JScrollPane(wrapper);

	// Button for opening links (used in custom cell editor)
	JButton linkedButtons;

	// StringBuilder for potential text accumulation (not used)
	StringBuilder sb = new StringBuilder();

	// Constructor: runs Selenium, JSoup, and initializes UI
	public C_JSoupCrawlerSaramIn(JFrame Main) {
		SeleniumFunction();
		JSoupFunction(); // JSoup must run after Selenium to get the correct URL
		compInit(Main);
	}

	// Adds an ActionListener to a button to open a link in the browser
	public void linksToButtons(String link) {
		linkedButtons.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URL(link).toURI());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	// Uses Selenium (HtmlUnitDriver) to search "웹 개발" on Saramin and get the result URL
	public String SeleniumFunction() {
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);
		System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
		WebDriver driver = new HtmlUnitDriver();
		driver.get("http://www.saramin.co.kr");
		WebElement elements = driver.findElement(By.id("combineSearchWord"));
		elements.sendKeys("웹 개발"); // Search for web development jobs
		elements.submit(); // Submit the search
		saramInUrl = driver.getCurrentUrl(); // Save the resulting URL
		System.out.println(saramInUrl);
		return saramInUrl;
	}

	// Initializes the UI components and layout
	public void compInit(JFrame Main) {
		this.wrapper.add(scrollForTitle);
		this.wrapper.add(scrollForButtons);
		tableSetTitle.enable(true);
		wrapper.setPreferredSize(new Dimension(1400, 500));
		scrollForTitle.setPreferredSize(new Dimension(1300, 500));
		scrollForTitle.setBorder(BorderFactory.createEmptyBorder());
		scrollForButtons.setPreferredSize(new Dimension(50, 500));
		scrollForButtons.setBorder(BorderFactory.createEmptyBorder());
		tableSetTitle.packAll();
		tableSetTitle.getColumn(0).setMinWidth(200);
		tableSetTitle.getColumn(1).setMinWidth(450);
		tableSetTitle.getColumn(2).setMinWidth(100);
		tableSetTitle.getColumn(2).sizeWidthToFit();
		tableSetButtons.packAll();
		self.add(scroll);
		scroll.setPreferredSize(new Dimension(1600, 500));
		setSize(1800, 500);
		setVisible(true);
	}

	// Uses JSoup to scrape job postings and links from the Saramin search results page
	public void JSoupFunction() {
		String url = saramInUrl;
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
			int i = 0;
			Elements titles = doc.select("ul.company_inbox li");
			for (Element e : titles) {
				String companyTitle = (e.text());
				String trimmedCompTitle = companyTitle.replaceAll("관심기업 등록", "333");
				String trimmedCompTitle2 = trimmedCompTitle.replaceAll("즉시지원", "333");
				String trimmedCompTitle3 = trimmedCompTitle2.replaceAll("스크랩", "333");
				String[] inputEachRow = trimmedCompTitle3.split("333");
				dtmt.addRow(new Object[] { inputEachRow[0], inputEachRow[2], inputEachRow[3] });
			}
			i = 0;
			Elements links = doc.select("ul.company_inbox li div.riin div.titbox h2.tit a");
			for (Element l : links) {
				String link = "http://www.saramin.co.kr" + (l.attr("href"));
				tableSetButtons.getColumn("링크").setCellRenderer(new C_ButtonRenderer());
				tableSetButtons.getColumn("링크").setCellEditor(new C_ButtonEditor(new JCheckBox(), tableSetButtons, dtmb));
				tableSetButtons.getColumnModel().getColumn(1).setMinWidth(0);
				tableSetButtons.getColumnModel().getColumn(1).setMaxWidth(0); // Hide URL column
				tableSetButtons.getColumn(0).setMinWidth(50);
				i++;
				dtmb.addRow(new Object[] { "", link });
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Main method for standalone testing
	public static void main(String[] args) {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
			UIManager.put("nimbusBase", Color.white);
			UIManager.put("nimbusBlueGrey", Color.white);
			UIManager.put("control", Color.white);
		} catch (Exception e) {}
		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				new C_JSoupCrawlerSaramIn(null);
			}
		});
	}
}
