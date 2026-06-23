<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="account-shell">
    <div class="account-card">
        <div class="auth-top">
            <span class="section-badge">PASSWORD</span>
            <h2>비밀번호 수정</h2>
            <p>현재 비밀번호를 확인한 뒤 새 비밀번호로 변경합니다.</p>
        </div>

        <c:if test="${not empty passwordError}"><div class="auth-alert">${passwordError}</div></c:if>

        <form action="${pageContext.request.contextPath}/member/password/update" method="post" class="auth-form">
            <div class="form-field"><label for="currentPassword">현재 비밀번호</label><input type="password" id="currentPassword" name="currentPassword" required></div>
            <div class="form-field"><label for="newPassword">새 비밀번호</label><input type="password" id="newPassword" name="newPassword" required></div>
            <div class="form-field"><label for="confirmPassword">새 비밀번호 확인</label><input type="password" id="confirmPassword" name="confirmPassword" required></div>
            <button type="submit" class="form-submit">비밀번호 변경</button>
        </form>
    </div>
</section>
