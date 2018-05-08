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


public class C_JSoupCrawlerSaramIn extends JPanel {

	public static String saramInUrl;
	// ---------------------- 라벨

	C_JSoupCrawlerSaramIn self = this;
	JEditorPane clickableTextPane = new JEditorPane();

	Object[] DefaultHeaderTitles = { "회사명", "공고", "조건" };
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

	public C_JSoupCrawlerSaramIn(JFrame Main) {
		SeleniumFunction();
		JSoupFunction();
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

	public void compInit(JFrame Main) {

		this.wrapper.add(scrollForTitle);
		this.wrapper.add(scrollForButtons);
		tableSetTitle.enable(true);
		wrapper.setPreferredSize(new Dimension(1400, 500)); // 전체 패널 사이즈

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
		// -----------------------------------------폭 조정

		self.add(scroll);
		scroll.setPreferredSize(new Dimension(1600, 500)); // scrollPane 사이즈
		setSize(1800, 500); // JDialog 사이즈
//		setLocationRelativeTo(Main);
//		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);

	}

	public String SeleniumFunction() { // 사람인에서 매 순간 순간 검색해가며 찾음

		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);

		java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);

		System.setProperty("webdriver.chrome.driver", "chromedriver.exe"); // 아예 크롬 드라이버를 깔기 때문에 문제가 없음

		WebDriver driver = new HtmlUnitDriver();

		driver.get("http://www.saramin.co.kr");

		// 검색어 입력 - 사이트마다 다르기 때문에 다른 값을 입력해야함
		String id = "combineSearchWord";

		WebElement elements = driver.findElement(By.id(id));
		elements.sendKeys("웹 개발"); // 개발자 신입 이라 검색어 적기
		elements.submit(); // 버튼 클릭.
		saramInUrl = driver.getCurrentUrl(); // url 담기

		System.out.println(saramInUrl); // url 뽑아내기

		return saramInUrl;
	}

	public void JSoupFunction() {

		// ---------------------------------------- 테이블 생성

		String url = saramInUrl;
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
			int i = 0;
			Elements titles = doc.select("ul.company_inbox li");
			String trimmedCompTitle3 = null;
			for (Element e : titles) {
				String companyTitle = (e.text()); // 공고문
				System.out.println(companyTitle);
				System.out.println(i);
				i++;
				String trimmedCompTitle = companyTitle.replaceAll("관심기업 등록", "333");
				String trimmedCompTitle2 = trimmedCompTitle.replaceAll("즉시지원", "333");
				trimmedCompTitle3 = trimmedCompTitle2.replaceAll("스크랩", "333");

				String[] inputEachRow = trimmedCompTitle3.split("333");

				System.out.println("회사명 : " + inputEachRow[0]); // 제목
				System.out.println("공고 : " + inputEachRow[2]);
				System.out.println("비고 : " + inputEachRow[3]);

				// -----------------------트리밍

				dtmt.addRow(new Object[] { inputEachRow[0], inputEachRow[2], inputEachRow[3] }); // 배열에 담아서 넣기

			}

			i = 0;

			Elements links = doc.select("ul.company_inbox li div.riin div.titbox h2.tit a");

			for (Element l : links) {

				String link = "http://www.saramin.co.kr" + (l.attr("href"));

				System.out.println(i);
				tableSetButtons.getColumn("링크").setCellRenderer(new C_ButtonRenderer());
				tableSetButtons.getColumn("링크").setCellEditor(new C_ButtonEditor(new JCheckBox(), tableSetButtons, dtmb));

				tableSetButtons.getColumnModel().getColumn(1).setMinWidth(0);
				tableSetButtons.getColumnModel().getColumn(1).setMaxWidth(0); // url 테이블 숨기기

				i++;
				dtmb.addRow(new Object[] { "", link });
				System.out.println(link);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		// try {
		// Thread.sleep(500);
		// } catch (InterruptedException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

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
				new C_JSoupCrawlerSaramIn(null);
			}
		});
	}
}
