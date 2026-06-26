<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="ad-sidebar-inner">
    <div class="ad-sidebar-title">
        <i class="fas fa-bullhorn"></i>&nbsp; 추천 광고 도서
    </div>
    <div id="adBookList">
        <div class="ad-loading">불러오는 중...</div>
    </div>
</div>

<script>
(function() {
    var ctx = '${pageContext.request.contextPath}';
    fetch(ctx + '/book/ads')
        .then(function(res) { return res.json(); })
        .then(function(books) {
            var container = document.getElementById('adBookList');
            if (!books || books.length === 0) {
                var sidebar = container.closest('.ad-sidebar');
                if (sidebar) sidebar.style.display = 'none';
                return;
            }
            var html = '';
            books.forEach(function(book) {
                var price = parseInt(book.price) || 0;
                var dr = book.discountRate || 0;
                var finalPrice = dr > 0 ? Math.floor(price - (price * dr / 100)) : price;
                var priceHtml = dr > 0
                    ? '<span class="ad-discount-badge">' + dr + '%</span>' + finalPrice.toLocaleString() + '원'
                    : price.toLocaleString() + '원';
                html += '<a href="' + ctx + '/book/view?isbn=' + book.isbn + '" class="ad-book-card">';
                if (book.image) {
                    html += '<img src="' + book.image + '" alt="" class="ad-book-img">';
                } else {
                    html += '<div class="ad-book-img ad-book-img-placeholder"></div>';
                }
                html += '<div class="ad-book-info">';
                html += '<div class="ad-book-title">' + book.bookname + '</div>';
                html += '<div class="ad-book-price">' + priceHtml + '</div>';
                html += '</div></a>';
            });
            container.innerHTML = html;
        })
        .catch(function() {
            var container = document.getElementById('adBookList');
            if (container) {
                var sidebar = container.closest('.ad-sidebar');
                if (sidebar) sidebar.style.display = 'none';
            }
        });
})();
</script>
