package me.agramon.agrapedia.web;

import com.apptastic.rssreader.Item;
import com.apptastic.rssreader.RssReader;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.agramon.agrapedia.Helper;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MangaDex extends Command {
    public MangaDex() {
        super.name = "mangadex";
        super.help = "\n\t• md [name/seriesID]  -  Shows information on the manhwa (Manhwa ID, Latest Chapter, Group)" +
                "\n\t• md group [name/groupID]  -  Shows information on the group (Leader, Website, Discord, Date Founded, Description, Member(s)" +
                "\n\t• md search [name]  -  Shows the first 5 search results on MangaDex" +
                "\n\t• md searchall [name]  -  Shows the first page of search results on MangaDex" +
                "\n\t• md searchgroup [name]  -  Shows the first 10 group search results on MangaDex" +
                "\n";
        super.cooldown = 5;
        super.category = new Category("Sources");
        super.aliases = new String[] {"md"};
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "OptionalGetWithoutIsPresent"})
    public void execute(CommandEvent e) {
        String [] args = e.getArgs().split("\\s+");
        EmbedBuilder eb = new EmbedBuilder();

        // Shows search results in MangaDex
        if (args[0].equals("search") || args[0].equals("searchall")) {
            eb.setTitle("MangaDex Search Results");

            ArrayList<String> results;

            // Shows first 5 search results
            if (args[0].equals("search")) {
                results = Helper.searchMD(e.getArgs().substring(7), "manga", 5);
                eb.setDescription("*" + StringUtils.join(args, " ").replaceFirst("search", "").substring(1) + "*");

                // Shows first 20 first results
            } else {
                results = Helper.searchMD(e.getArgs().substring(10), "manga", 40);
                eb.setDescription("*" + StringUtils.join(args, " ").replaceFirst("searchall", "").substring(1) + "*");
            }

            e.reply(eb.build());

            for (String link : results) {
                try {
                    Matcher matcher = Pattern.compile("\\d+").matcher(link);
                    matcher.find();
                    String id = matcher.group();

                    eb.clear();
                    JSONObject chapterData = new JSONObject(getAPIResponse(new URL("https://mangadex.org/api/v2/manga/" + id + "/chapters/"))).getJSONObject("data");
                    JSONObject mangaData = new JSONObject(getAPIResponse(new URL("https://mangadex.org/api/v2/manga/" + id))).getJSONObject("data");
                    String summary = mangaData.get("description").toString();

                    eb.setTitle(mangaData.get("title").toString());
                    eb.setThumbnail(getCover(id));

                    /*
                    String raws = "";
                    if (summary.contains("url=")) {
                        raws = "  |  [Raws](" + summary.substring(summary.indexOf("url=") + 4, summary.indexOf("]", summary.indexOf("url=") + 4)) + ")";
                    }
                     */

                    eb.setDescription("[Series Link](https://mangadex.org" + link + ")");
                    eb.addField("MangaDex ID: ", id, false);
                    eb.addField("Rating: ", mangaData.getJSONObject("rating").get("bayesian").toString() + "/10", false);
                    eb.addField("Views: ", mangaData.get("views").toString(), false);
                    for (Object o : chapterData.getJSONArray("chapters")) {
                        if (((JSONObject) o).get("language").equals("gb")) {
                            eb.addField("Latest Chapter: ", ((JSONObject) o).get("chapter").toString(), false);
                            break;
                        }
                    }
                    int end = summary.length();
                    if (summary.contains("\n")) {
                        end = summary.indexOf("\n");
                    }
                    eb.addField("Summary: ", summary.substring(0, end), false);
                    e.reply(eb.build());

                } catch (IOException ex) { ex.printStackTrace(); }
            }

        } else if (args[0].equals("group")) {
            // Gets info about a group
            if (NumberUtils.isCreatable(args[1])) {
                try {
                    String[] info = getGroupInfo(args[1]);
                    eb.setTitle(info[0]);
                    eb.setThumbnail(info[6]);
                    eb.addField("Group ID:", info[7], false);
                    eb.addField("Leader: ", info[1], false);
                    eb.addField("Website: ", info[2], false);
                    eb.addField("Discord: ", info[3], false);
                    eb.addField("Founded: ", info[4], false);
                    eb.addField("Description: ", info[5], false);
                    eb.addField("Member(s): ", info[8], false);
                    e.reply(eb.build());
                } catch (IOException ex) {
                    EmbedBuilder eb2 = new EmbedBuilder().setTitle("Error!").setDescription(ex.toString());
                    ex.printStackTrace();
                    e.reply(eb2.build());
                }
            } else {
                try {
                    ArrayList<String> results = Helper.searchMD(e.getArgs().substring(5), "group", 1);
                    for (String id : results) {
                        String[] info = getGroupInfo(id);
                        eb.clear();
                        eb.setTitle(info[0]);
                        if (!info[6].equals("null")) {
                            eb.setThumbnail(info[6]);
                        }
                        eb.addField("Group ID:", id, false);
                        eb.addField("Leader:", info[1], false);
                        eb.addField("Website:", info[2], false);
                        eb.addField("Discord:", info[3], false);
                        eb.addField("Founded:", info[4], false);
                        eb.addField("Description:", info[5], false);
                        eb.addField("Member(s):", info[8], false);
                        e.reply(eb.build());
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }


        } else if (args[0].equals("searchgroup")){
            try {
                ArrayList<String> results = Helper.searchMD(e.getArgs().substring(12), "group", 10);
                for (String id : results) {
                    String[] info = getGroupInfo(id);
                    eb.clear();
                    eb.setTitle(info[0]);
                    if (!info[6].equals("null")) {
                        eb.setThumbnail(info[6]);
                    }
                    eb.addField("Group ID:", id, false);
                    eb.addField("Leader:", info[1], false);
                    eb.addField("Website:", info[2], false);
                    eb.addField("Discord:", info[3], false);
                    eb.addField("Founded:", info[4], false);
                    eb.addField("Description:", info[5], false);
                    eb.addField("Member(s):", info[8], false);
                    e.reply(eb.build());
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        } else {
            // Gets api if the arg is a number, or gets first search result otherwise

            if (e.getArgs().isEmpty()) {
                EmbedBuilder emptyArg = new EmbedBuilder().setDescription("The correct usage is: >md <id> | >md search <name> | >md group <group id>");
                e.reply(emptyArg.build());
                return;
            }
            try {
                URL url;
                String id;
                // If the args is an id, gets the API
                if (NumberUtils.isCreatable(e.getArgs())) {
                    url = new URL("https://mangadex.org/api/v2/manga/" + e.getArgs() + "/chapters/");
                    id = e.getArgs();

                    // Else it gets the id of the first search result
                } else {
                    String s = Helper.searchMD(e.getArgs(), "manga", 1).get(0);
                    Matcher m = Pattern.compile("\\d+").matcher(s);
                    m.find();
                    id = m.group();
                    url = new URL("https://mangadex.org/api/v2/manga/" + id + "/chapters/");
                }

                JSONObject chapterData = new JSONObject(getAPIResponse(url));
                JSONArray chapterList = chapterData.getJSONObject("data").getJSONArray("chapters");

                url = new URL("https://mangadex.org/api/v2/manga/" + id);
                JSONObject obj2 = new JSONObject(getAPIResponse(url)).getJSONObject("data");

                for (Object o : chapterList) {
                    if (((JSONObject) o).get("language").equals("gb")) {
                        eb.setTitle(((JSONObject) o).get("mangaTitle").toString());
                        eb.setThumbnail(getCover(id));
                        eb.setDescription("[Series Link](https://mangadex.org/title/" + ((JSONObject) o).get("mangaId").toString() +"/"+ ((JSONObject) o).get("mangaTitle").toString().replaceAll(" ", "-").replaceAll("'", "-") + ")");
                        eb.addField("MangaDex ID: ", ((JSONObject) o).get("mangaId").toString(), false);
                        eb.addField("Rating: ", obj2.getJSONObject("rating").get("bayesian").toString() + "/10", false);
                        eb.addField("Views: ", obj2.get("views").toString(), false);
                        eb.addField("Latest Chapter: ", ((JSONObject) o).get("chapter").toString(), false);
                        if (obj2.get("description").toString().contains("\n")) {
                            eb.addField("Summary: ", obj2.get("description").toString().substring(0, obj2.get("description").toString().indexOf("\n")), false);
                        } else {
                            eb.addField("Summary: ", obj2.get("description").toString(), false);
                        }
                        eb.addField("","", false);
                        break;
                    }
                }
                eb.addField("Dateㅤㅤ|ㅤㅤGroupㅤㅤ|ㅤㅤChapter", "", false);
                ArrayList<Item> chapters = getMDRSS(id);
                for (Item i : chapters) {
                    if (i.getDescription().get().contains("English") && eb.length() < 4000) {
                        eb.addField("", i.getPubDate().get().replaceAll("\\+0000", "") + "\n" + i.getDescription().get().substring(0, i.getDescription().get().indexOf(" - ")).replaceAll("Group: ", "") + "\n[" + i.getTitle().get().substring(i.getTitle().get().indexOf((" - ")) + 3).substring(i.getTitle().get().substring(i.getTitle().get().indexOf((" - ")) + 3).indexOf(",") + 1) + "](" + i.getLink().get() + ")\n", false);
                    }
                }

                e.reply(eb.build());

            } catch (IOException ex) {
                EmbedBuilder eb2 = new EmbedBuilder().setTitle("Error!").setDescription(ex.toString());
                ex.printStackTrace();
                e.reply(eb2.build());
            }
        }
    }

    // Gets group information from group id
    public String[] getGroupInfo(String id) throws IOException {
        String [] info = new String[9];

        JSONObject obj = new JSONObject(getAPIResponse(new URL("https://mangadex.org/api/v2/group/" + id)));
        info[0] = (obj.getJSONObject("data").get("name")).toString();
        info[1] = (obj.getJSONObject("data").getJSONObject("leader").get("name")).toString();
        info[2] = (obj.getJSONObject("data").get("website")).toString();
        info[3] = (obj.getJSONObject("data").get("discord")).toString();
        info[4] = (obj.getJSONObject("data").get("founded")).toString();
        info[5] = (obj.getJSONObject("data").get("description")).toString();
        info[6] = (obj.getJSONObject("data").get("banner")).toString();
        info[7] = (obj.getJSONObject("data").get("id")).toString();
        StringBuilder sb = new StringBuilder();
        JSONArray members = (obj.getJSONObject("data").getJSONArray("members"));

        for (Object o : members) {
            sb.append(((JSONObject) o).get("name")).append("\n");
        }
        info[8] = sb.toString();

        return info;
    }

    // Gets the lat est cover url of the series
    public String getCover(String id) throws IOException {
        JSONObject obj = new JSONObject(getAPIResponse(new URL("https://mangadex.org/api/v2/manga/" + id))).getJSONObject("data");
        return obj.get("mainCover").toString();
    }

    // Get MangaDex API response
    public String getAPIResponse(URL url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.addRequestProperty("User-Agent", "Mozilla/5.0");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        // Reads the API
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line).append("\n");
        }
        in.close();
        return response.toString();
    }

    public ArrayList<Item> getMDRSS(String id) throws IOException {
        RssReader reader = new RssReader();
        Stream<Item> rssFeed = reader.read("https://mangadex.org/rss/aVSnd4H7U9CtXTWhKsvqzAx6fDucmywp/manga_id/" + id);
        return (ArrayList<Item>) rssFeed.collect(Collectors.toList());
    }
}
