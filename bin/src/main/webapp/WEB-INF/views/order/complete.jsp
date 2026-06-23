<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<section class="cart-page">
    <div class="empty-box">
        <h3>주문이 완료되었습니다.</h3>
        <p>구매해주셔서 감사합니다.</p>

        <div class="cart-actions" style="margin-top:20px;">
            <a href="${pageContext.request.contextPath}/book/list" class="action-btn outline">도서 목록으로</a>
            <a href="${pageContext.request.contextPath}/order/list" class="action-btn dark">주문내역 보기</a>
        </div>
    </div>
</section>