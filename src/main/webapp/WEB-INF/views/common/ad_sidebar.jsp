<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="ad-sidebar-inner">
    <div class="ad-sidebar-title">
        <i class="fas fa-bullhorn"></i>&nbsp; 추천 광고 도서
    </div>
    <div id="adBookList"></div>
</div>

<script>
(function() {
    var ctx = '${pageContext.request.contextPath}';
    fetch(ctx + '/book/ads')
        .then(function(res) { return res.json(); })
        .then(function(books) {
            if (!books || books.length === 0) return;
            var html = '';
            books.forEach(function(book) {
                var price = parseInt(book.price) || 0;
                var dr = book.discountRate || 0;
                var finalPrice = dr > 0 ? Math.floor(price - (price * dr / 100)) : price;
                var priceHtml = dr > 0
                    ? '<span class="ad-discount-badge">' + dr + '%</span>' + finalPrice.toLocaleString() + '원'
                    : price.toLocaleString() + '원';
                html += '<a href="' + ctx + '/book/view?isbn=' + book.isbn + '" class="ad-book-card">';
                html += book.image
                    ? '<img src="' + book.image + '" alt="" class="ad-book-img">'
                    : '<div class="ad-book-img ad-book-img-placeholder"></div>';
                html += '<div class="ad-book-info">'
                    + '<div class="ad-book-title">' + book.bookname + '</div>'
                    + '<div class="ad-book-price">' + priceHtml + '</div>'
                    + '</div></a>';
            });
            document.getElementById('adBookList').innerHTML = html;
            var sidebar = document.querySelector('.ad-sidebar');
            if (sidebar) sidebar.style.display = 'block';
        });
})();
</script>
