package com.consumer.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;

@Service
public class ConsumerService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private RestTemplate restTemplate;

    @RabbitListener(queues = "myQueue")
    public void receivedMessage(Map<String, Object> message) {
        System.out.println("📩 Menerima Data: " + message);
        
        try {
            // 1. Ekstraksi Data
            String id = String.valueOf(message.get("idOrder"));
            String produkId = String.valueOf(message.get("produkId"));
            String namaProduk = String.valueOf(message.get("namaProduk"));
            String namaPelanggan = String.valueOf(message.get("namaPelanggan"));
            String jumlah = String.valueOf(message.get("jumlahDipesan"));
            String total = String.valueOf(message.get("total"));
            String status = String.valueOf(message.get("status"));
            String tanggal = String.valueOf(message.get("tanggal"));

            // 2. Potong Stok via LoadBalancer (Panggil nama service "PRODUK")
            try {
                String url = "http://PRODUK/api/produk/reduce-stok/" + produkId + "/" + jumlah;
                restTemplate.put(url, null);
                System.out.println("✅ Stok Berhasil Dipotong di Service PRODUK");
            } catch (Exception e) {
                System.out.println("❌ Gagal potong stok: " + e.getMessage());
            }

            // 3. Kirim Email Struk
            sendEmail(id, namaPelanggan, namaProduk, jumlah, total, status, tanggal);

        } catch (Exception e) {
            System.out.println("❌ Gagal proses pesan: " + e.getMessage());
        }
    }

    public void sendEmail(String id, String pelanggan, String produk, String jumlah, String total, String status, String tanggal) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom("jfnew24@gmail.com");
            helper.setTo("jfnew24@gmail.com");
            helper.setSubject("Konfirmasi Order #" + id + " - " + pelanggan);

            String html = 
                "<div style='font-family: Arial, sans-serif; border: 1px solid #ddd; padding: 20px; max-width: 500px;'>" +
                    "<h2 style='color: #2563eb; text-align: center;'>DETAIL PESANAN</h2>" +
                    "<hr>" +
                    "<p><b>Nama Pelanggan:</b> " + pelanggan + "</p>" +
                    "<p><b>Tanggal:</b> " + tanggal + "</p>" +
                    "<p><b>Status:</b> <span style='color: green; font-weight: bold;'>" + status + "</span></p>" +
                    "<table style='width: 100%; border-top: 1px solid #eee; margin-top: 10px;'>" +
                        "<tr>" +
                            "<td style='padding: 10px 0;'><b>" + produk + "</b> (x" + jumlah + ")</td>" +
                            "<td style='text-align: right;'><b>Rp " + total + "</b></td>" +
                        "</tr>" +
                    "</table>" +
                    "<p style='text-align: center; margin-top: 20px; color: #888;'>Terima kasih sudah berbelanja!</p>" +
                "</div>";

            helper.setText(html, true);
            mailSender.send(mimeMessage);
            System.out.println("✅ Email berhasil dikirim.");
        } catch (Exception e) {
            System.out.println("❌ Email error: " + e.getMessage());
        }
    }
}
