# Keelung Sights Crawler — Day 1

以 **Jsoup** 實作的基隆景點爬蟲（《新生學習菜單》作業 Day 1）。
本專案會連線至 TravelKing 基隆市旅遊頁面，取得指定區域（zone，例如「七堵」）的景點清單，
並進一步進入景點詳細頁抓取以下欄位：

- `sightName`（景點名稱）
- `zone`（行政區）
- `category`（分類標籤）
- `photoURL`（代表照片 URL）
- `address`（地址）
- `description`（簡介/描述）

> 注意：請僅作為學習用途，爬取頻率請控制，並尊重目標網站的 **使用條款/robots.txt** 與著作權。

---

## 專案結構（關鍵檔案）

```
keelung-crawler/
├─ src/main/java/com/example/crawler/
│  ├─ KeelungSightsCrawler.java   // 主要爬蟲程式（含 main）
│  └─ Sight.java                  // 資料模型 (JavaBean)
└─ pom.xml                        // Maven 設定（含 shade plugin）
```

---

## 需求環境

- Java 8+（JDK 1.8 或以上）
- Maven 3.x

---

## 建置與執行

1) 於專案根目錄建置 **fat-jar**：
```bash
mvn -q -DskipTests package
```

2) 執行（shade 產生的可執行 Jar）：
```bash
java -jar target/keelung-crawler-1.0-SNAPSHOT-shaded.jar
```

> 預設在 `main` 方法中以 **「七堵」** 作為查詢區域；若要更換區域，請修改 `KeelungSightsCrawler.main(...)` 裡傳入 `getItems("七堵")` 的字串。

---

## 執行過程與輸出

- 啟動後會先抓取 **基隆市旅遊指南主頁**，尋找各區塊中的景點清單連結，
  再逐一造訪每個景點詳細頁，解析欄位並印出。
- 終端機輸出包含：
  - 連線與解析過程的簡要日誌（找到幾個區塊、幾個連結…）
  - 每筆 `Sight` 的完整欄位（由 `toString()` 格式化）

---

## 常見問題（FAQ）

- **Timeout/連線太慢**：可調整 `Jsoup.connect(...).timeout(10000)` 的毫秒數，或降低請求頻率。
- **解析不到欄位**：目標頁面 DOM 結構如有變動，請微調 `select(...)` 的 CSS 選擇器。
- **亂碼**：請確認 Console/IDE 使用 UTF-8。

---

## 後續（Day 2 以後）

- Day 2：以 Spring Boot 暴露 `/SightAPI?zone=七堵` 回傳 JSON
- Day 3：RWD 前端（七區按鈕＋卡片/折疊）
- Day 4：接 MongoDB，啟動時匯入資料、API 從 DB 查詢
- Day 5：Docker 化並部署至雲端（Railway/Render）

---

## 授權

此範例僅供教學/練習用途。
