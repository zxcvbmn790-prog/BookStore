<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<style>
.bm-wrap { max-width: 1200px; margin: 0 auto; }

.bm-toolbar {
    display: flex; justify-content: space-between; align-items: center;
    margin-bottom: 24px; gap: 16px; flex-wrap: wrap;
}
.bm-search {
    display: flex; gap: 8px; flex: 1; max-width: 460px;
}
.bm-search input {
    flex: 1; padding: 10px 14px; border: 1px solid var(--line);
    border-radius: 8px; font-size: 14px; outline: none;
}
.bm-search input:focus { border-color: #333; }
.bm-search button {
    padding: 10px 20px; background: #333; color: #fff; border: none;
    border-radius: 8px; font-size: 14px; font-weight: 600; cursor: pointer;
}
.bm-search button:hover { background: #111; }

.bm-count { font-size: 14px; color: #888; }
.bm-count strong { color: #333; }

.bm-table {
    width: 100%; border-collapse: collapse; table-layout: fixed;
    background: #fff; border: 1px solid var(--line); border-radius: 16px;
    overflow: hidden;
}
.bm-table th, .bm-table td {
    padding: 14px 12px; text-align: center; vertical-align: middle;
    border-bottom: 1px solid var(--line); font-size: 14px;
}
.bm-table th {
    background: #f8f9fa; font-weight: 700; color: #555;
    white-space: nowrap; position: sticky; top: 0;
}
.bm-table tbody tr:hover { background: #f8fafc; }
.bm-table tbody tr:last-child td { border-bottom: none; }

.bm-table .col-isbn { width: 120px; }
.bm-table .col-img { width: 60px; }
.bm-table .col-name { width: auto; text-align: left; }
.bm-table .col-author { width: 130px; }
.bm-table .col-price { width: 100px; }
.bm-table .col-discount { width: 140px; }
.bm-table .col-final { width: 100px; }
.bm-table .col-action { width: 120px; }
.bm-table .col-ad { width: 70px; }

.bm-ad-toggle {
    position: relative;
    display: inline-block;
    width: 40px;
    height: 22px;
}
.bm-ad-toggle input { opacity: 0; width: 0; height: 0; }
.bm-ad-slider {
    position: absolute;
    cursor: pointer;
    top: 0; left: 0; right: 0; bottom: 0;
    background: #ccc;
    border-radius: 22px;
    transition: 0.2s;
}
.bm-ad-slider:before {
    position: absolute;
    content: "";
    height: 16px; width: 16px;
    left: 3px; bottom: 3px;
    background: #fff;
    border-radius: 50%;
    transition: 0.2s;
}
.bm-ad-toggle input:checked + .bm-ad-slider { background: #008bcc; }
.bm-ad-toggle input:checked + .bm-ad-slider:before { transform: translateX(18px); }

.bm-book-name {
    font-weight: 600; white-space: nowrap; overflow: hidden;
    text-overflow: ellipsis; max-width: 280px; display: block;
}
.bm-book-name a { color: inherit; text-decoration: none; }
.bm-book-name a:hover { color: #008bcc; }

.bm-thumb {
    width: 40px; height: 54px; object-fit: cover; border-radius: 4px;
    background: #f0f0f0;
}

.bm-discount-input {
    display: flex; align-items: center; justify-content: center; gap: 4px;
}
.bm-discount-input input {
    width: 54px; padding: 6px 8px; border: 1px solid #ddd; border-radius: 6px;
    text-align: center; font-size: 14px; font-weight: 600;
}
.bm-discount-input input:focus { border-color: #333; outline: none; }

.bm-save-btn {
    padding: 4px 10px; background: #e8f4fa; color: #008bcc; border: 1px solid #c4e3f3;
    border-radius: 6px; font-size: 12px; font-weight: 700; cursor: pointer;
}
.bm-save-btn:hover { background: #008bcc; color: #fff; }
.bm-save-btn.saved { background: #d4edda; color: #155724; border-color: #c3e6cb; }

.bm-final-price { font-weight: 700; color: #d9534f; }
.bm-original-price { color: #999; font-size: 12px; }

.bm-action-btns { display: flex; gap: 6px; justify-content: center; }
.bm-btn {
    padding: 6px 12px; border-radius: 6px; font-size: 12px; font-weight: 600;
    text-decoration: none; cursor: pointer; border: 1px solid #ddd;
}
.bm-btn-edit { background: #f0f0f0; color: #333; }
.bm-btn-edit:hover { background: #333; color: #fff; }
.bm-btn-del { background: #fff5f5; color: #d9534f; border-color: #f5c6cb; }
.bm-btn-del:hover { background: #d9534f; color: #fff; }

.bm-badge-discount {
    display: inline-block; background: #d9534f; color: #fff; font-size: 11px;
    font-weight: 700; padding: 1px 5px; border-radius: 3px;
}
</style>

<section class="cart-page">
    <div class="section-head">
        <span class="section-badge">ADMIN</span>
        <h2>도서 관리</h2>
        <p>도서 검색, 할인율 설정, 수정/삭제를 한 곳에서 관리하세요.</p>
    </div>

    <div class="bm-wrap">
        <div class="bm-toolbar">
            <form class="bm-search" action="${pageContext.request.contextPath}/admin/books" method="get">
                <input type="text" name="keyword" value="${keyword}" placeholder="도서명, 저자, ISBN으로 검색">
                <button type="submit">검색</button>
                <c:if test="${not empty keyword}">
                    <a href="${pageContext.request.contextPath}/admin/books"
                       style="padding:10px 14px; background:#f0f0f0; border-radius:8px; color:#666; text-decoration:none; font-size:14px;">초기화</a>
                </c:if>
            </form>
            <div class="bm-count">
                전체 <strong>${bookList.size()}</strong>권
                <c:if test="${not empty keyword}"> | "<strong>${keyword}</strong>" 검색결과</c:if>
            </div>
        </div>

        <div class="cart-panel" style="padding:0; overflow-x:auto;">
            <table class="bm-table">
                <colgroup>
                    <col class="col-isbn">
                    <col class="col-img">
                    <col class="col-name">
                    <col class="col-author">
                    <col class="col-price">
                    <col class="col-discount">
                    <col class="col-final">
                    <col class="col-ad">
                    <col class="col-action">
                </colgroup>
                <thead>
                    <tr>
                        <th>ISBN</th>
                        <th>표지</th>
                        <th style="text-align:left;">도서명</th>
                        <th>저자</th>
                        <th>정가</th>
                        <th>할인율</th>
                        <th>판매가</th>
                        <th>광고</th>
                        <th>관리</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty bookList}">
                            <c:forEach var="book" items="${bookList}">
                                <tr id="row-${book.isbn}">
                                    <td style="font-size:12px; color:#888;">${book.isbn}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty book.image}">
                                                <img src="${book.image}" class="bm-thumb" alt="">
                                            </c:when>
                                            <c:otherwise>
                                                <div class="bm-thumb" style="display:flex;align-items:center;justify-content:center;font-size:9px;color:#aaa;">N/A</div>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td style="text-align:left;">
                                        <span class="bm-book-name" title="${book.bookname}">
                                            <a href="${pageContext.request.contextPath}/book/view?isbn=${book.isbn}">${book.bookname}</a>
                                        </span>
                                    </td>
                                    <td style="font-size:13px; color:#666;">${book.author}</td>
                                    <td>${book.price}원</td>
                                    <td>
                                        <div class="bm-discount-input">
                                            <input type="number" id="dr-${book.isbn}" value="${book.discountRate}" min="0" max="99"
                                                   onchange="markChanged(${book.isbn})">
                                            <span>%</span>
                                            <button class="bm-save-btn" id="btn-${book.isbn}"
                                                    onclick="saveDiscount(${book.isbn})">저장</button>
                                        </div>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${book.discountRate > 0}">
                                                <span class="bm-badge-discount">${book.discountRate}%</span>
                                                <div class="bm-final-price" id="final-${book.isbn}">
                                                    <fmt:parseNumber var="p" value="${book.price}" type="number"/>
                                                    <fmt:formatNumber value="${p - (p * book.discountRate / 100)}" type="number" maxFractionDigits="0"/>원
                                                </div>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color:#999;" id="final-${book.isbn}">${book.price}원</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <label class="bm-ad-toggle" title="${book.ad ? '광고 중' : '광고 해제'}">
                                            <input type="checkbox" id="ad-${book.isbn}"
                                                   ${book.ad ? 'checked' : ''}
                                                   onchange="toggleAd(${book.isbn}, this.checked)">
                                            <span class="bm-ad-slider"></span>
                                        </label>
                                    </td>
                                    <td>
                                        <div class="bm-action-btns">
                                            <a href="${pageContext.request.contextPath}/admin/updateform?isbn=${book.isbn}" class="bm-btn bm-btn-edit">수정</a>
                                            <a href="${pageContext.request.contextPath}/admin/delete?isbn=${book.isbn}" class="bm-btn bm-btn-del"
                                               onclick="return confirm('정말 삭제하시겠습니까?')">삭제</a>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr><td colspan="9" style="padding:60px; color:#999; font-size:16px;">
                                <c:choose>
                                    <c:when test="${not empty keyword}">검색 결과가 없습니다.</c:when>
                                    <c:otherwise>등록된 도서가 없습니다.</c:otherwise>
                                </c:choose>
                            </td></tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</section>

<script>
function toggleAd(isbn, isAd) {
    var ctx = '${pageContext.request.contextPath}';
    fetch(ctx + '/admin/updateAd', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: 'isbn=' + isbn + '&isAd=' + isAd
    })
    .then(function(res) { return res.json(); })
    .then(function(data) {
        if (!data.success) {
            var cb = document.getElementById('ad-' + isbn);
            if (cb) cb.checked = !isAd;
            alert('광고 상태 변경에 실패했습니다.');
        }
    })
    .catch(function() {
        var cb = document.getElementById('ad-' + isbn);
        if (cb) cb.checked = !isAd;
        alert('오류가 발생했습니다.');
    });
}

function markChanged(isbn) {
    var btn = document.getElementById('btn-' + isbn);
    btn.classList.remove('saved');
    btn.textContent = '저장';
}

function saveDiscount(isbn) {
    var input = document.getElementById('dr-' + isbn);
    var btn = document.getElementById('btn-' + isbn);
    var rate = parseInt(input.value) || 0;
    if (rate < 0) rate = 0;
    if (rate > 99) rate = 99;
    input.value = rate;

    btn.textContent = '...';
    btn.disabled = true;

    var ctx = '${pageContext.request.contextPath}';
    fetch(ctx + '/admin/updateDiscount', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: 'isbn=' + isbn + '&discountRate=' + rate
    })
    .then(function(res) { return res.json(); })
    .then(function(data) {
        btn.disabled = false;
        if (data.success) {
            btn.textContent = '완료';
            btn.classList.add('saved');
            updateFinalPrice(isbn, rate);
        } else {
            btn.textContent = '실패';
        }
    })
    .catch(function() {
        btn.disabled = false;
        btn.textContent = '오류';
    });
}

function updateFinalPrice(isbn, rate) {
    var row = document.getElementById('row-' + isbn);
    var priceCell = row.querySelectorAll('td')[4];
    var priceText = priceCell.textContent.replace(/[^0-9]/g, '');
    var price = parseInt(priceText) || 0;
    var finalCell = document.getElementById('final-' + isbn);

    if (rate > 0) {
        var finalPrice = Math.floor(price - (price * rate / 100));
        finalCell.parentElement.innerHTML =
            '<span class="bm-badge-discount">' + rate + '%</span>' +
            '<div class="bm-final-price" id="final-' + isbn + '">' + finalPrice.toLocaleString() + '원</div>';
    } else {
        finalCell.parentElement.innerHTML =
            '<span style="color:#999;" id="final-' + isbn + '">' + price.toLocaleString() + '원</span>';
    }
}
</script>
