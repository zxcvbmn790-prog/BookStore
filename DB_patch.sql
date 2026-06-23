-- 회원가입 성공 메시지는 컨트롤러/뷰에서 처리
-- FAQ는 별도 JSP/Controller로 처리

-- 좋아요 테이블
CREATE TABLE IF NOT EXISTS book_like (
    like_id INT AUTO_INCREMENT PRIMARY KEY,
    isbn INT NOT NULL,
    userid VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_book_like UNIQUE (isbn, userid)
);

-- 평점 테이블
CREATE TABLE IF NOT EXISTS book_rating (
    rating_id INT AUTO_INCREMENT PRIMARY KEY,
    isbn INT NOT NULL,
    userid VARCHAR(100) NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_book_rating UNIQUE (isbn, userid)
);

-- 도서 상세 조회 시 사용할 집계 예시
SELECT
    b.isbn,
    b.bookname,
    COUNT(DISTINCT bl.like_id) AS like_count,
    COALESCE(ROUND(AVG(br.rating), 1), 0) AS avg_rating,
    COUNT(DISTINCT br.rating_id) AS rating_count
FROM book b
LEFT JOIN book_like bl ON b.isbn = bl.isbn
LEFT JOIN book_rating br ON b.isbn = br.isbn
GROUP BY b.isbn, b.bookname;

-- 기간별 판매 집계 예시
-- 일간
SELECT FORMATDATETIME(order_date, 'yyyy-MM-dd') AS period_label,
       COUNT(*) AS order_count,
       SUM(total_price) AS sales_amount
FROM orders
GROUP BY FORMATDATETIME(order_date, 'yyyy-MM-dd')
ORDER BY period_label DESC;

-- 주간
SELECT CONCAT(YEAR(order_date), '-W', RIGHT(CONCAT('00', ISO_WEEK(order_date)), 2)) AS period_label,
       COUNT(*) AS order_count,
       SUM(total_price) AS sales_amount
FROM orders
GROUP BY CONCAT(YEAR(order_date), '-W', RIGHT(CONCAT('00', ISO_WEEK(order_date)), 2))
ORDER BY MIN(order_date) DESC;

-- 월간
SELECT FORMATDATETIME(order_date, 'yyyy-MM') AS period_label,
       COUNT(*) AS order_count,
       SUM(total_price) AS sales_amount
FROM orders
GROUP BY FORMATDATETIME(order_date, 'yyyy-MM')
ORDER BY period_label DESC;

-- 연간
SELECT FORMATDATETIME(order_date, 'yyyy') AS period_label,
       COUNT(*) AS order_count,
       SUM(total_price) AS sales_amount
FROM orders
GROUP BY FORMATDATETIME(order_date, 'yyyy')
ORDER BY period_label DESC;
