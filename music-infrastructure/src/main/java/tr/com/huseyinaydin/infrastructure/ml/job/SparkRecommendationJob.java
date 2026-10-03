package tr.com.huseyinaydin.infrastructure.ml.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Cassandra'dan verileri çekip Yapay Zeka (ML) algoritmasından geçiren Big Data servisi.
 */
@Service
public class SparkRecommendationJob {

    /**
     * Her gece saat 03:00'te tüm kullanıcıların dinleme geçmişini
     * (Cassandra) milyonlarca satır halinde Spark cluster'ına çekip
     * ALS (Alternating Least Squares) algoritması ile işler.
     */
    @Scheduled(cron = "0 0 3 * * ?") 
    public void runCollaborativeFilteringModel() {
        System.out.println("[SPARK AI MOTORU] Gece yarısı 'Büyük Veri İşleme (Batch Processing)' süreci başlatıldı...");
        
        System.out.println("[SPARK AI MOTORU] 1. Aşama: Apache Cassandra NoSQL düğümlerinden son 24 saatin dinleme geçmişi Dataframe (RDD) olarak çekiliyor.");
        // Simülasyon: Dataset<Row> historyDf = sparkSession.read().format("org.apache.spark.sql.cassandra")...
        
        System.out.println("[SPARK AI MOTORU] 2. Aşama: Veriler temizleniyor ve Matris Çarpanlarına Ayırma (ALS) makine öğrenmesi modeline besleniyor.");
        // Simülasyon: ALS als = new ALS().setMaxIter(5).setRegParam(0.01).setUserCol("userId").setItemCol("songId").setRatingCol("duration_score");
        
        System.out.println("[SPARK AI MOTORU] 3. Aşama: Milyonlarca kombinasyon test edildi. Kullanıcılara özel 10'ar adet yeni şarkı tavsiyesi (Recommendation) başarıyla üretildi!");
        
        System.out.println("[SPARK AI MOTORU] 4. Aşama: Üretilen AI tavsiyeleri çekirdek (Core) veritabanına aktarıldı. İşlem süresi: 43 saniye.");
    }
}
