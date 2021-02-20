package me.agramon.agrapedia.web;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class NovelUpdates extends Command {

    public NovelUpdates() {
        super.name = "novelupdates";
        super.help = "\n\t• nu [name]  -  Shows information on the novel (Rating, Genres, Stats)" +
                "\n\t• nu search [name]  -  Shows the first 5 search results on NovelUpdates" +
                "\n\t• nu searchall [name]  -  Shows the first page of search results on NovelUpdates" +
                "\n";
        super.cooldown = 5;
        super.category = new Category("Sources");
        super.aliases = new String[] {"novelupdate", "nu"};
    }

    public void execute(CommandEvent e) {
        String[] args = e.getArgs().split("\\s+");

        // Shows the first 5 search results on NovelUpdates
        if (args[0].equals("search")) {
            nuScraper(e, 5);

        } else if (args[0].equals("searchall")) {
            // Shows the first 25 search results on NovelUpdates
            nuScraper(e, 25);

        } else {
            // Gets the first search result on NovelUpdates

            try {
                EmbedBuilder eb = new EmbedBuilder();
                String series = null;
                String seriesName = null;
                String seriesImg;

                Document doc = Jsoup.connect("https://www.novelupdates.com/?s=" + e.getArgs().replaceAll(" ", "%20") + "&post_type=seriesplans").userAgent("Mozilla/5.0").followRedirects(true).timeout(200000).ignoreHttpErrors(true).get();

                Elements elements = doc.select("a[href]");
                for (Element ele : elements) {
                    String link = ele.attr("href");
                    if (link.contains("series") && !link.contains("series-finder") && !link.contains("series-ranking") && !link.contains("latest-series") && !link.contains("series-filtering")) {
                        series = link;
                        seriesName = ele.text();
                        break;
                    }
                }

                doc = Jsoup.connect(series).userAgent("Mozilla/5.0").followRedirects(true).timeout(200000).ignoreHttpErrors(true).get();

                seriesImg = doc.select("img").first().attr("src");

                eb.setTitle(seriesName);
                eb.setDescription("[Series Link]" + "(" + series + ")");
                eb.setThumbnail(seriesImg);
                eb.addField("Rating:", doc.select("span[class=\"uvotes\"]").text(), false);
                eb.addField("Genres:", doc.select("div[id=\"seriesgenre\"]").text(), false);

                Elements otherNovels = doc.select("a[class=\"genre\"]");
                StringBuilder recommendations = new StringBuilder();
                for (Element rec : otherNovels) {
                    if (rec.attr("href").contains("series")) {
                        recommendations.append("[").append(rec.text()).append("](").append(rec.attr("href")).append(")ㅤ|ㅤ");
                    }
                }
                if (recommendations.toString().length() > 3) {
                    eb.addField("Recommendations:", recommendations.substring(0, recommendations.toString().length() - 3), false);
                }
                eb.addField("Summary:", doc.select("div[id=\"editdescription\"]").text(), false);
                eb.addField("", "", false);
                eb.addField("Dateㅤㅤ|ㅤㅤGroupㅤㅤ|ㅤㅤChapter", "", false);

                for (Element table : doc.select("table[id=myTable] > tbody")) {
                    //System.out.println(table);
                    for (Element row : table.select("tr")) {
                        Elements tds = row.select("td");
                        // Special Character for White Space
                        eb.addField("", tds.get(0).text() + "ㅤㅤㅤㅤ[" + tds.get(1).text() + "](" + tds.get(1).selectFirst("a[href]").attr("href") + ")ㅤㅤㅤㅤ[" + tds.get(2).text() + "](https://" + tds.get(2).selectFirst("a[href]").attr("abs:href") + ")", false);
                    }
                }

                e.reply(eb.build());

            } catch (Exception ex) {
                EmbedBuilder eb = new EmbedBuilder().setTitle("Error!").setDescription(ex.toString());
                e.reply(eb.build());
                ex.printStackTrace();
            }
        }
    }

    // Scrapes the search results on NovelUpdates
    public void nuScraper(CommandEvent e, int num) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("NovelUpdates Search Results");
        if (e.getArgs().split("\\s+")[0].equals("search")) {
            eb.setDescription("*" + StringUtils.join(e.getArgs().split("\\s+"), " ").replaceFirst("search", "").substring(1) + "*");
        } else {
            eb.setDescription("*" + StringUtils.join(e.getArgs().split("\\s+"), " ").replaceFirst("searchall", "").substring(1) + "*");

        }
        e.reply(eb.build());

        try {
            Document doc = Jsoup.connect("https://www.novelupdates.com/?s=" + e.getArgs().split("\\s+")[1].replaceAll(" ", "%20") + "&post_type=seriesplans").userAgent("Mozilla/5.0").timeout(200000).ignoreHttpErrors(true).get();

            Elements elements = doc.select("div[class=\"w-blog-list\"]");
            for (Element novel : elements) {
                Elements titles = novel.select("div[class=\"search_title\"]");
                Elements images = novel.select("div[class=\"search_img_nu\"]");
                Elements genres = novel.select("div[class=\"search_genre\"]");
                Elements stats = novel.select("div[class=\"search_stats\"]");
                Elements body = novel.select("div[class=\"search_body_nu\"]");

                for (int i = 0; i < Math.min(titles.size(), num); i++) {
                    eb = new EmbedBuilder();
                    eb.setTitle(titles.get(i).text());
                    eb.setDescription("[Series Link](" + titles.get(i).toString().substring(titles.get(i).toString().indexOf("href=") + 6, titles.get(i).toString().indexOf("\">", titles.get(i).toString().indexOf("href=") + 6)) + ")");
                    eb.setThumbnail(images.get(i).toString().substring(images.get(i).toString().indexOf("src=") + 5, images.get(i).toString().indexOf("\">", images.get(i).toString().indexOf("src=") + 1)));
                    eb.addField("Rating:", images.get(i).text(), false);
                    eb.addField("Genres:", genres.get(i).text(), false);
                    eb.addField("Stats:", stats.get(i).text(), false);
                    eb.addField("Summary:", body.get(i).ownText(), false);
                    e.reply(eb.build());
                }
            }

        } catch (Exception ex) {
            EmbedBuilder eb2 = new EmbedBuilder().setTitle("Error!").setDescription(ex.toString());
            ex.printStackTrace();
            e.reply(eb2.build());
        }
    }


}
