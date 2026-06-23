<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<section class="stats-page">
    <div class="section-head">
        <span class="section-badge">SALES</span>
        <h2>판매통계</h2>
        <p>전체 매출, 오늘 매출, 일자별 매출, 인기 도서를 확인할 수 있어.</p>
    </div>

    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-label">전체 매출액</div>
            <div class="stat-value">
                <fmt:formatNumber value="${summary.totalSales}" pattern="#,##0"/>원
            </div>
        </div>

        <div class="stat-card">
            <div class="stat-label">전체 주문건수</div>
            <div class="stat-value">
                <fmt:formatNumber value="${summary.totalOrders}" pattern="#,##0"/>건
            </div>
        </div>

        <div class="stat-card">
            <div class="stat-label">전체 판매권수</div>
            <div class="stat-value">
                <fmt:formatNumber value="${summary.totalQuantity}" pattern="#,##0"/>권
            </div>
        </div>

        <div class="stat-card">
            <div class="stat-label">오늘 매출액</div>
            <div class="stat-value">
                <fmt:formatNumber value="${summary.todaySales}" pattern="#,##0"/>원
            </div>
        </div>
    </div>

	<div class="stats-panel">
	    <h3>최근 7일 매출 그래프</h3>
	    <div class="chart-box">
	        <canvas id="salesChart"></canvas>
	    </div>
	</div>
	
    <div class="stats-panel">
        <h3>최근 7일 매출</h3>

        <c:choose>
            <c:when test="${not empty dailySalesList}">
                <table class="stats-table">
                    <thead>
                        <tr>
                            <th>날짜</th>
                            <th>주문건수</th>
                            <th>매출액</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="day" items="${dailySalesList}">
                            <tr>
                                <td>${day.orderDay}</td>
                                <td>
                                    <fmt:formatNumber value="${day.orderCount}" pattern="#,##0"/>건
                                </td>
                                <td>
                                    <fmt:formatNumber value="${day.salesAmount}" pattern="#,##0"/>원
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <div class="empty-box">
                    아직 주문 데이터가 없습니다.
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="stats-panel">
        <h3>베스트셀러 TOP 5</h3>

        <c:choose>
            <c:when test="${not empty topBookList}">
                <table class="stats-table">
                    <thead>
                        <tr>
                            <th>순위</th>
                            <th>도서명</th>
                            <th>ISBN</th>
                            <th>판매수량</th>
                            <th>매출액</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="book" items="${topBookList}" varStatus="status">
                            <tr>
                                <td>${status.index + 1}</td>
                                <td>${book.bookname}</td>
                                <td>${book.isbn}</td>
                                <td>
                                    <fmt:formatNumber value="${book.totalQuantity}" pattern="#,##0"/>권
                                </td>
                                <td>
                                    <fmt:formatNumber value="${book.totalSales}" pattern="#,##0"/>원
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <div class="empty-box">
                    아직 판매 집계 데이터가 없습니다.
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</section>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

<script>
    const salesLabels = [
        <c:forEach var="day" items="${dailySalesList}" varStatus="status">
            '${day.orderDay}'<c:if test="${not status.last}">,</c:if>
        </c:forEach>
    ];

    const salesAmounts = [
        <c:forEach var="day" items="${dailySalesList}" varStatus="status">
            ${day.salesAmount}<c:if test="${not status.last}">,</c:if>
        </c:forEach>
    ];

    const orderCounts = [
        <c:forEach var="day" items="${dailySalesList}" varStatus="status">
            ${day.orderCount}<c:if test="${not status.last}">,</c:if>
        </c:forEach>
    ];

    const ctx = document.getElementById('salesChart');

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: salesLabels,
            datasets: [
                {
                    label: '일별 매출액',
                    data: salesAmounts,
                    borderWidth: 1
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false
        }
    });
</script>