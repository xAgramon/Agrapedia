package me.agramon.agrapedia.web;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.agramon.agrapedia.Helper;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SystemUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class KakaoPage extends Command {
    public KakaoPage() {
        super.name = "kakaopage";
        super.help = "\n\t• kkp  -  Shows the top 10 hottest manhwas on KakaoPage" +
                "\n\t• kkp get [seriesID]  -  Shows information about a manhwa (KKP-ID, Total Chapters, Latest Chapter)" +
                "\n\t• kkp search [name]  -  Shows the first 5 search results on KakaoPage" +
                "\n";
        super.cooldown = 5;
        super.category = new Category("Sources");
        super.aliases = new String[] {"kkp"};
    }

    @Override
    protected void execute(CommandEvent e) {
        String[] args = e.getArgs().split("\\s+");
        EmbedBuilder eb = new EmbedBuilder();

        if (args[0].equals("search")) {
            eb.setTitle("KakaoPage Search Results");
            eb.setDescription("*" + e.getArgs().substring(7) + "*");
            e.reply(eb.build());
            String url = "https://page.kakao.com/search?word=" + e.getArgs().substring(7).replaceAll(" ", "%20");
            kkpScraper(e, url, 5);

            // Gets info about a manhwa
        } else if (args[0].equals("get")) {
            if (args.length != 2) {
                eb.setDescription("The correct format is: >kkw get [seriesID]");
            } else {
                try {
                    String [] manhwaInfo = Helper.getManhwa(args[1]);
                    String [] chapterInfo = Helper.getLatestChapter(args[1]);

                    eb.setImage(manhwaInfo[1]);
                    eb.addField("Title:", "[" + manhwaInfo[0] + "]" + "(https://page.kakao.com/home?seriesId=" + args[1] + ")", false);
                    eb.addField("KKP-ID:", args[1], false);
                    eb.addField("Total Chapters: ", chapterInfo[0], false);
                    eb.addField("Latest Chapter: ", "[" + chapterInfo[1] + "](https://page.kakao.com/viewer?productId=" + chapterInfo[2] + ")", false);
                    e.reply(eb.build());

                } catch (URISyntaxException ex) {
                    EmbedBuilder eb2 = new EmbedBuilder().setTitle("Error!").setDescription(ex.toString());
                    ex.printStackTrace();
                    e.reply(eb2.build());
                }
            }
        } else {
            eb.setTitle("KakaoPage HOTTEST Manhwas!");
            e.reply(eb.build());
            kkpScraper(e, "https://page.kakao.com/up/today?categoryUid=10", 10);
        }
    }

    // Scrapes the search results on KakaoPage
    public void kkpScraper(CommandEvent e, String url, int num) {
        Document doc;
        List<String> series = new ArrayList<>();
        List<String> seriesImg = new ArrayList<>();
        List<String> seriesName = new ArrayList<>();
        String [] imgBlacklist = new String[] {"https://dn-img-page.kakao.com/download/resource?kid=bapr3k/hyF3aObGHM/lB8P17AEKoKtjL6MzzYBB0"};
        try {
            String os = null;
            if (SystemUtils.IS_OS_WINDOWS) {
                os = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36";
            } else if (SystemUtils.IS_OS_LINUX) {
                os = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2919.83 Safari/537.36";
            }

            // Connects to the new releases page and gets all the links
            doc = Jsoup.connect(url).userAgent(os).ignoreHttpErrors(true).get();
            Elements links = doc.select("a[href]");

            // Adds the first 10 series links to a list
            for(Element link : links){
                if (link.toString().contains("series")) {
                    if (series.size() >= num) {
                        break;
                    }
                    series.add("https://page.kakao.com" + link.attr("href"));
                }
            }

            // Adds the corresponding series cover images to a list
            Elements images = doc.select("img");
            for(Element img : images){
                if (!img.attr("data-src").isEmpty() && img.attr("data-src").contains("filename")) {
                    if (seriesImg.size() >= num) {
                        break;
                    }
                    String imgStr = "https:" + img.attr("data-src");

                    if (!ArrayUtils.contains(imgBlacklist, imgStr)) {
                        seriesImg.add(imgStr);
                    }
                }
            }

            // Adds the corresponding series titles to a list
            Elements names = doc.select("div[class=\"text-ellipsis webfont css-11602e0\"]");

            for (Element name : names) {
                if (seriesName.size() >= 10) {
                    break;
                }
                seriesName.add(name.text());
            }

        } catch (IOException ex) {
            EmbedBuilder eb2 = new EmbedBuilder().setTitle("Error!").setDescription(ex.toString());
            ex.printStackTrace();
            e.reply(eb2.build());
        }

        for (int i = 0; i < series.size(); i++) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.addField("Title: ", "[" + seriesName.get(i) + "]" + "(" + series.get(i) + ")", false);
            eb.addField("KKP-ID: ", series.get(i).replaceAll("[^0-9]", ""), false);
            eb.setThumbnail(seriesImg.get(i));
            e.reply(eb.build());
        }
    }
}
