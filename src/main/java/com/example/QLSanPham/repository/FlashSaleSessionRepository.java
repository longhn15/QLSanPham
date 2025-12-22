// package com.example.QLSanPham.repository;

// import java.time.LocalDateTime;
// import java.util.Optional;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;

// import com.example.QLSanPham.entity.FlashSaleSession;

// @Repository
// public interface FlashSaleSessionRepository extends JpaRepository<FlashSaleSession, Long> {

//     // Câu query "Ăn tiền": Tìm phiên sale đang diễn ra dựa vào thời gian thực
//     // Logic: StartTime <= Hiện tại <= EndTime VÀ Status = ACTIVE
//     @Query("SELECT s FROM FlashSaleSession s WHERE s.startTime <= :now AND s.endTime >= :now AND s.status = 'ACTIVE'")
//     Optional<FlashSaleSession> findActiveSession(@Param("now") LocalDateTime now);

//     // Kiểm tra xem có khung giờ nào chồng lấn nhau không (Dùng khi Admin tạo lịch
//     // mới)
//     @Query("SELECT COUNT(s) > 0 FROM FlashSaleSession s WHERE " +
//             "(:start BETWEEN s.startTime AND s.endTime) OR " +
//             "(:end BETWEEN s.startTime AND s.endTime)")
//     boolean existsOverlappingSession(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
// }