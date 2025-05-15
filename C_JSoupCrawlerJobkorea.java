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
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory.UIColorHighlighter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

// C_JSoupCrawlerJobkorea.java
// This class handles crawling job postings from JobKorea using Selenium and JSoup, and displays them in a Swing table UI.
public class C_JSoupCrawlerJobkorea extends JPanel {

	// Stores the URL after Selenium search
	public static String jobKoreaUrl;

	// Self-reference for inner class usage
	C_JSoupCrawlerJobkorea self = this;
	// Unused, but could be used for clickable text
	JEditorPane clickableTextPane = new JEditorPane();

	// Table headers for job info and links
	Object[] DefaultHeaderTitles = { "기업", "공고", "분류", "경력" };
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
	public C_JSoupCrawlerJobkorea(JFrame Main) {
		SeleniumFunction();
		JSoupFunction(); // JSoup must run after Selenium to get the correct URL
		compInit(Main);
	}

	// Uses Selenium (HtmlUnitDriver) to search "웹 개발" on JobKorea and get the result URL
	public String SeleniumFunction() {
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);
		System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
		WebDriver driver = new HtmlUnitDriver();
		driver.get("http://www.jobkorea.co.kr");
		WebElement elements = driver.findElement(By.xpath("//*[@id=\"stext\"]"));
		elements.sendKeys("웹 개발"); // Search for web development jobs
		elements.submit(); // Submit the search
		jobKoreaUrl = driver.getCurrentUrl(); // Save the resulting URL
		System.out.println(jobKoreaUrl);
		return jobKoreaUrl;
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

	// Initializes the UI components and layout
	public void compInit(JFrame Main) {
		this.wrapper.add(scrollForTitle);
		this.wrapper.add(scrollForButtons);
		tableSetTitle.enable(false);
		wrapper.setPreferredSize(new Dimension(1400, 500));
		tableSetTitle.packAll();
		tableSetTitle.getColumn(0).setMinWidth(150);
		tableSetTitle.getColumn(1).setMinWidth(400);
		tableSetTitle.getColumn(2).setMinWidth(350);
		tableSetTitle.getColumn(3).setMinWidth(150);
		tableSetButtons.packAll();
		scrollForTitle.setPreferredSize(new Dimension(1300, 650));
		scrollForTitle.setBorder(BorderFactory.createEmptyBorder());
		scrollForButtons.setPreferredSize(new Dimension(50, 650));
		scrollForButtons.setBorder(BorderFactory.createEmptyBorder());
		self.add(scroll);
		scroll.setPreferredSize(new Dimension(1400, 650));
		setSize(1400, 700);
		setVisible(true);
	}

	// Uses JSoup to scrape job postings and links from the JobKorea search results page
	public void JSoupFunction() {
		String url = jobKoreaUrl;
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
			int i = 0;
			Elements titles = doc.select("article#smGiList ul.detailList li");
			for (Element e : titles) {
				String companyTitle = (e.text() + "\t");
				System.out.println(i);
				i++;
				String trimmedCompTitle0 = companyTitle.replaceAll("│", "333");
				String trimmedCompTitle1 = trimmedCompTitle0.replaceAll("관심기업", "333");
				String trimmedCompTitle2 = trimmedCompTitle1.replaceAll("즉시지원", "333");
				String trimmedCompTitle3 = trimmedCompTitle2.replaceAll("스크랩", "333");
				String[] split = trimmedCompTitle3.split("333");
				dtmt.addRow(new Object[] { split[0], split[1], split[3], split[4] });
			}
			i = 0;
			Elements links = doc.select("section#cnt article#smGiList div.list ul.detailList li span.jobInfo a");
			for (Element l : links) {
				String link = "http://www.jobkorea.co.kr" + (l.attr("href"));
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
				new C_JSoupCrawlerJobkorea(null);
			}
		});
	}
}
