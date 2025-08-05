package com.example.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class KeelungSightsCrawler {
    private static final String BASE_URL = "https://www.travelking.com.tw/tourguide/taiwan/keelungcity/";

    /**
     * 取得指定 zone 的所有景點
     * @param zoneFilter 例如 "七堵"
     */
    public Sight[] getItems(String zoneFilter) throws IOException {
        System.out.println("Connecting to " + BASE_URL);
        Document doc = Jsoup.connect(BASE_URL)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                            .timeout(10000)
                            .get();

        // 只抓 #guide-point 區塊內的 <h4> 作為區域標題
        Element guidePoint = doc.getElementById("guide-point");
        if (guidePoint == null) {
            System.err.println("Cannot find #guide-point section");
            return new Sight[0];
        }
        Elements headings = guidePoint.select("h4");
        System.out.println("Found zone headings: " + headings.size());

        List<Sight> list = new ArrayList<>();
        for (Element heading : headings) {
            String zoneName = heading.text().trim();
            System.out.println("Zone header: " + zoneName);
            if (!zoneName.contains(zoneFilter)) {
                System.out.println("  -> skip, not matching filter");
                continue;
            }

            Element ul = heading.nextElementSibling();
            if (ul == null || !"ul".equals(ul.tagName())) {
                System.out.println("  -> next sibling is not <ul>: " + (ul != null ? ul.tagName() : "null"));
                continue;
            }

            Elements links = ul.select("li a[href]");
            System.out.println("  Links found: " + links.size());
            for (Element link : links) {
                String detailUrl = link.absUrl("href");
                System.out.println("    Detail URL: " + detailUrl);

                Document detailDoc = Jsoup.connect(detailUrl)
                                          .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                                          .timeout(10000)
                                          .get();

                Sight s = new Sight();
                // 名稱：schema.org meta[itemprop=name]
                Element metaName = detailDoc.selectFirst("meta[itemprop=name]");
                s.setSightName(metaName != null ? metaName.attr("content").trim() :
                                 detailDoc.selectFirst("h1").text().trim());
                s.setZone(zoneName);

                // 類別：span.point_pc + span[property=rdfs:label] strong
                Element catEl = detailDoc.selectFirst("span.point_pc + span[property=rdfs:label] strong");
                s.setCategory(catEl != null ? catEl.text().trim() : "");

                // 圖片 URL：meta[property=og:image] 或 link[rel=image_src]
                Element metaImg = detailDoc.selectFirst("meta[property=og:image]");
                String photoUrl = metaImg != null ? metaImg.attr("content") :
                                  detailDoc.selectFirst("link[rel=image_src]").attr("href");
                s.setPhotoURL(photoUrl);

                // 地址：#point_data div.address span[property=vcard:street-address]
                Element addrSpan = detailDoc.selectFirst("#point_data div.address span[property=vcard:street-address]");
                s.setAddress(addrSpan != null ? addrSpan.text().trim() : "");

                // 描述：schema.org meta[itemprop=description]
                Element metaDesc = detailDoc.selectFirst("meta[itemprop=description]");
                s.setDescription(metaDesc != null ? metaDesc.attr("content").trim() : "");

                System.out.println("      Parsed sight: " + s.getSightName());
                list.add(s);
            }
        }

        System.out.println("Total sights collected: " + list.size());
        return list.toArray(new Sight[0]);
    }

    public static void main(String[] args) {
        try {
            KeelungSightsCrawler crawler = new KeelungSightsCrawler();
            Sight[] sights = crawler.getItems("七堵");
            for (Sight s : sights) {
                System.out.println("------------");
                System.out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
