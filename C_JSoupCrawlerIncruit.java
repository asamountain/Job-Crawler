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



public class C_JSoupCrawlerIncruit extends JPanel {

	public static String incruitUrl;

	// ---------------------- 라벨

	C_JSoupCrawlerIncruit self = this;
	JEditorPane clickableTextPane = new JEditorPane();

	Object[] DefaultHeaderTitles = { "회사", "공고", };
	Object[] DefaultHeaderLinks = { "링크", "URL" };

	private DefaultTableModel dtmt = new DefaultTableModel(DefaultHeaderTitles, 0);
	private DefaultTableModel dtmb = new DefaultTableModel(DefaultHeaderLinks, 0);

	JXTable tableSetTitle = new JXTable(dtmt);
	JXTable tableSetButtons = new JXTable(dtmb);

	JScrollPane scrollForTitle = new JScrollPane(tableSetTitle);
	JScrollPane scrollForButtons = new JScrollPane(tableSetButtons);

	JPanel wrapper = new JPanel();
	JScrollPane scroll = new JScrollPane(wrapper);

	// --------------------------------테이블 설정

	JButton linkedButtons;

	// ------------------버튼

	StringBuilder sb = new StringBuilder();

	public C_JSoupCrawlerIncruit(JFrame Main) {

		SeleniumFunction();
		JSoupFunction();// Jsoup이 Selenium보다 먼저 시작되어선 안됨, Selenium이 먼저되어야 url을 따올 수 있음
		compInit(Main);

	}

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

	public String SeleniumFunction() { // 사람인에서 매 순간 순간 검색해가며 찾음

		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);

		java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);

		System.setProperty("webdriver.chrome.driver", "chromedriver.exe"); // 아예 크롬 드라이버를 깔기 때문에 문제가 없음

		WebDriver driver = new HtmlUnitDriver();

		driver.get("http://www.incruit.com/");

		// 검색어 입력 - 사이트마다 다르기 때문에 다른 값을 입력해야함

		WebElement elements = driver.findElement(By.xpath("//*[@id=\"kw\"]"));
		elements.sendKeys("웹 개발"); // 단어 검색 -----------------------------
									// elements.sendKeys(keywordInput=JTextfield.getText())
		elements.submit(); // 버튼 클릭.
		incruitUrl = driver.getCurrentUrl(); // url 담기

		System.out.println(incruitUrl); // url 뽑아내기
		return incruitUrl;
	}

	public void compInit(JFrame Main) {

		this.wrapper.add(scrollForTitle);
		this.wrapper.add(scrollForButtons);
		tableSetTitle.enable(true);
		wrapper.setPreferredSize(new Dimension(1500, 850)); // 전체 패널 사이즈

		scrollForTitle.setPreferredSize(new Dimension(1300, 800));
		scrollForTitle.setBorder(BorderFactory.createEmptyBorder());
		scrollForButtons.setPreferredSize(new Dimension(50, 800));
		scrollForButtons.setBorder(BorderFactory.createEmptyBorder());

		tableSetTitle.getColumn(0).setMinWidth(150);
		tableSetTitle.getColumn(1).sizeWidthToFit();
		tableSetButtons.packAll();
		// -----------------------------------------폭 조정

		self.add(scroll);
		scroll.setPreferredSize(new Dimension(1500, 700)); // scrollPane 사이즈
		setSize(1500, 800); // JDialog 사이즈
		// setLocationRelativeTo(Main);
		// setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		System.out.println("화면 띄우기");
		setVisible(true);

	}

	public void JSoupFunction() {

		// ---------------------------------------- 테이블 생성

		String url = incruitUrl;

		Document doc;
		try {
			doc = Jsoup.connect(url).get();
			int i = 0;

			Elements titles = doc.select("div#content div.section ul.litype01 li");
			for (Element e : titles) {
				String companyTitle = (e.text()); // 공고문
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
				tableSetButtons.getColumnModel().getColumn(1).setMaxWidth(0); // url 테이블 숨기기
				// tableSetButtons.getColumn(0).
				tableSetButtons.getColumn(0).setMinWidth(50);

				i++;

				dtmb.addRow(new Object[] { "", link });
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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

		} catch (Exception e) {
		}

		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				new C_JSoupCrawlerIncruit(null);
			}
		});
	}
}
