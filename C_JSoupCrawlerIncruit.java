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

// C_JSoupCrawlerIncruit.java
// This class handles crawling job postings from Incruit using Selenium and JSoup, and displays them in a Swing table UI.
public class C_JSoupCrawlerIncruit extends JPanel {

	// Stores the URL after Selenium search
	public static String incruitUrl;

	// Self-reference for inner class usage
	C_JSoupCrawlerIncruit self = this;
	// Unused, but could be used for clickable text
	JEditorPane clickableTextPane = new JEditorPane();

	// Table headers for job info and links
	Object[] DefaultHeaderTitles = { "회사", "공고", };
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
	public C_JSoupCrawlerIncruit(JFrame Main) {
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

	// Uses Selenium (HtmlUnitDriver) to search "웹 개발" on Incruit and get the result URL
	public String SeleniumFunction() {
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);
		System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
		WebDriver driver = new HtmlUnitDriver();
		driver.get("http://www.incruit.com/");
		WebElement elements = driver.findElement(By.xpath("//*[@id=\"kw\"]"));
		elements.sendKeys("웹 개발"); // Search for web development jobs
		elements.submit(); // Submit the search
		incruitUrl = driver.getCurrentUrl(); // Save the resulting URL
		System.out.println(incruitUrl);
		return incruitUrl;
	}

	// Initializes the UI components and layout
	public void compInit(JFrame Main) {
		this.wrapper.add(scrollForTitle);
		this.wrapper.add(scrollForButtons);
		tableSetTitle.enable(true);
		wrapper.setPreferredSize(new Dimension(1500, 850));
		scrollForTitle.setPreferredSize(new Dimension(1300, 800));
		scrollForTitle.setBorder(BorderFactory.createEmptyBorder());
		scrollForButtons.setPreferredSize(new Dimension(50, 800));
		scrollForButtons.setBorder(BorderFactory.createEmptyBorder());
		tableSetTitle.getColumn(0).setMinWidth(150);
		tableSetTitle.getColumn(1).sizeWidthToFit();
		tableSetButtons.packAll();
		self.add(scroll);
		scroll.setPreferredSize(new Dimension(1500, 700));
		setSize(1500, 800);
		System.out.println("화면 띄우기");
		setVisible(true);
	}

	// Uses JSoup to scrape job postings and links from the Incruit search results page
	public void JSoupFunction() {
		String url = incruitUrl;
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
			int i = 0;
			Elements titles = doc.select("div#content div.section ul.litype01 li");
			for (Element e : titles) {
				String companyTitle = (e.text());
				System.out.println("처음 따온 쌩 날 것 텍스트 전문 : " + companyTitle);
				System.out.println(i);
				i++;
				String[] getJobDetailInformation = companyTitle.split("스크랩");
				String getNoticementLine = getJobDetailInformation[0].replace("관심기업등록", "");
				String getCompanyName = getJobDetailInformation[0].split("관심기업등록")[0];
				dtmt.addRow(new Object[] { getCompanyName, getNoticementLine, "" });
			}
			i = 0;
			Elements links = doc.select("div#content div.section ul.litype01 li p.detail a");
			for (Element l : links) {
				String link = (l.attr("href"));
				System.out.println("잡은 링크 : " + link);
				System.out.println(i);
				tableSetButtons.getColumn("링크").setCellRenderer(new C_ButtonRenderer());
				tableSetButtons.getColumn("링크")
					.setCellEditor(new C_ButtonEditor(new JCheckBox(), tableSetButtons, dtmb));
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
				new C_JSoupCrawlerIncruit(null);
			}
		});
	}
}
