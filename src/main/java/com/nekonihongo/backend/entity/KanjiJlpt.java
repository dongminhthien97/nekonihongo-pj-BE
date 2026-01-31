// src/main/java/com/nekonihongo/backend/entity/KanjiJlpt.java
package com.nekonihongo.backend.entity;

import com.nekonihongo.backend.enums.JlptLevelType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "kanji_jlpt")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KanjiJlpt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private JlptLevelType level; // THÊM CÁI NÀY

    @Column(nullable = false, length = 10)
    private String kanji; // 人, 日, 月...

    @Column(length = 50)
    private String hanViet; // Nhân, Nhật, Nguyệt...

    @Column(nullable = false, length = 100)
    private String meaning; // người, ngày, mặt trăng...

    @Column(length = 100)
    private String onYomi; // ジン, ニチ, ゲツ...

    @Column(length = 100)
    private String kunYomi; // ひと, ひ, つき...

    @Column(length = 20)
    private String stt; // "1", "2", "3"... để hiển thị STT

    // KHÔNG thêm trường mới nào khác
}