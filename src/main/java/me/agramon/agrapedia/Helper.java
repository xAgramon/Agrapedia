package me.agramon.agrapedia;

import org.apache.commons.lang3.SystemUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Objects;

import static java.lang.Thread.sleep;

public class Helper {

    // Gets the TOTAL CHAPTERS | LATEST CHAPTER TITLE | CHAPTER LINK
    public static String [] getLatestChapter(String id) throws URISyntaxException {
        String url = "https://page.kakao.com/home?seriesId=" + id + "&orderby=desc";

        // Check what OS you're on
        String os = null;
        if (SystemUtils.IS_OS_WINDOWS) {
            os = "chromedriver.exe";
        } else if (SystemUtils.IS_OS_LINUX) {
            os = "chromedriver";
        }
        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + File.separator + "ChromeDriver" + File.separator + os);

        WebDriver driver = null;
        String totalChapters = null;
        String latestChapter = null;
        String chapterLink = null;
        try {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200","--ignore-certificate-errors","--disable-extensions","--no-sandbox","--disable-dev-shm-usage");
            driver = new ChromeDriver(options);

            driver.get(url);

            String html = driver.getPageSource();
            Document doc = Jsoup.parse(html);

            Element totalChapterElement = doc.select("div[class=\"webfont css-1pysja1\"]").first();

            Element latestChapterElement = doc.select("div[class=\"text-ellipsis webfont css-1mn7vax\"]").first();

            Element linkElement = doc.select("ul[class=\"css-14gr98z\"]").first();

            if (totalChapterElement == null || latestChapterElement == null || linkElement == null) {
                driver.close();
                return new String [] {"-1", "-1", "-1"};
            }

            totalChapters = totalChapterElement.text().replaceAll("[^0-9]", "");

            latestChapter = latestChapterElement.text();

            chapterLink = linkElement.toString().substring(68, 76);

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Something went wrong... restarting browser!");
            Objects.requireNonNull(driver).close();
            getLatestChapter(id);
        }

        driver.close();
        return new String [] {totalChapters, latestChapter , chapterLink};
    }

    // Gets manhwa id and manhwa link
    public static String[] getManhwa(String id) {
        try {
            String url = "https://page.kakao.com/home?seriesId=" + id + "&orderby=desc";

            String os = null;
            if (SystemUtils.IS_OS_WINDOWS) {
                os = "Mozilla/5.0";
            } else if (SystemUtils.IS_OS_LINUX) {
                os = "Mozilla/5.0";
            }

            Document doc = Jsoup.connect(url).userAgent(os).timeout(200000).ignoreHttpErrors(true).get();

            Element manhwa = doc.select("div[class=\"css-1y42t5x\"]").first();

            String image = "https:" + manhwa.toString().substring(manhwa.toString().indexOf("img src=") + 9, manhwa.toString().indexOf("&", manhwa.toString().indexOf("img src=")));

            String name = manhwa.toString().substring(manhwa.toString().indexOf("alt=") + 5, manhwa.toString().indexOf("class", manhwa.toString().indexOf("alt=")) - 2);

            return new String [] {name, image};
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String[] {"", ""};
    }

    // Gets search results in MangaDex
    public static ArrayList<String> searchMD(String args, String type, int num) {
        ArrayList<String> results = new ArrayList<>();
        WebDriver driver;
        try {

            // Check what OS you're on
            String os = null;
            if (SystemUtils.IS_OS_WINDOWS) {
                os = "chromedriver.exe";
            } else if (SystemUtils.IS_OS_LINUX) {
                os = "chromedriver";
            }
            System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + File.separator + "ChromeDriver" + File.separator + os);

            ChromeOptions options = new ChromeOptions();

            String profile = System.getProperty("user.dir") + File.separator + "ChromeDriver" + File.separator + "scoped_dir10648_2096044232";
            options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200","--ignore-certificate-errors","--disable-extensions","--no-sandbox","--disable-dev-shm-usage", "--user-data-dir=" + profile);
            driver = new ChromeDriver(options);

            driver.get("https://mangadex.org/login");
            sleep(1000);
            try {
                driver.findElement(By.id("remember_me")).click();
                driver.findElement(By.id("login_username")).sendKeys(Config.get("MDEMAIL"));
                driver.findElement(By.id("login_password")).sendKeys(Config.get("MDPASSWORD"), Keys.ENTER);
                sleep(5000);
            } catch (Exception ex) { System.out.println("No need to login!"); }

            String url;

            // Searches mangas
            if (type.equals("manga")) {
                url = "https://mangadex.org/search?title=" + args.replaceAll(" ", "%20");
                driver.get(url);
                Document doc = Jsoup.parse(driver.getPageSource());

                Elements ele = doc.select("a[href]");

                for (Element link : ele) {
                    if (link.attr("href").contains("title") && !results.contains(link.attr("href")) && results.size() < num && link.attr("href").length() > 10 && !link.attr("href").contains("30461")) {
                        results.add(link.attr("href"));
                    }
                }

                // Searches groups
            } else if (type.equals("group")) {
                url = "https://mangadex.org/groups/0/1/" + args.replaceAll(" ", "%20");
                driver.get(url);
                Document doc = Jsoup.parse(driver.getPageSource());

                for (Element table : doc.select("table[class=table table-striped table-hover table-sm] > tbody")) {
                    //System.out.println(table);
                    for (Element row : table.select("tr")) {
                        Elements tds = row.select("td");
                        if (tds.get(0).toString().contains("English") && results.size() < num) {
                            results.add(tds.get(1).toString().substring(tds.get(1).toString().indexOf("/group/") + 7, tds.get(1).toString().indexOf("/", tds.get(1).toString().indexOf("/group/") + 7)));
                        }
                    }
                }
            }

            driver.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return results;
    }
}
