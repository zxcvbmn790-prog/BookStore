<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="account-shell">
    <div class="account-card delete-card">
        <div class="auth-top">
            <span class="section-badge">WITHDRAW</span>
            <h2>회원 탈퇴</h2>
            <p>탈퇴하면 장바구니, 주문 및 활동 정보가 함께 정리됩니다.</p>
        </div>

        <c:if test="${not empty deleteError}"><div class="auth-alert">${deleteError}</div></c:if>

        <form action="${pageContext.request.contextPath}/member/delete" method="post" class="auth-form" onsubmit="return confirm('정말 탈퇴하시겠습니까?');">
            <div class="form-field"><label for="password">비밀번호 확인</label><input type="password" id="password" name="password" required></div>
            <button type="submit" class="form-submit delete-submit">회원 탈퇴하기</button>
        </form>
    </div>
</section>
