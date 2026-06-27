<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="account-shell">
    <div class="account-card">
        <div class="auth-top">
            <span class="section-badge">MY PROFILE</span>
            <h2>회원 정보 수정</h2>
            <p>닉네임, 이메일, 전화번호를 수정할 수 있어요.</p>
        </div>

        <c:if test="${not empty profileMessage}"><div class="auth-alert" style="background:#ecfdf3; color:#166534; border-color:#bbf7d0;">${profileMessage}</div></c:if>
        <c:if test="${not empty profileError}"><div class="auth-alert">${profileError}</div></c:if>
        <c:if test="${not empty passwordMessage}"><div class="auth-alert" style="background:#ecfdf3; color:#166534; border-color:#bbf7d0;">${passwordMessage}</div></c:if>

        <form action="${pageContext.request.contextPath}/member/profile/update" method="post" class="auth-form">
            <div class="form-field"><label>아이디</label><input type="text" value="${member.username}" readonly></div>
            <div class="form-field"><label for="nickname">닉네임</label><input type="text" id="nickname" name="nickname" value="${member.nickname}"></div>
            <div class="form-field"><label for="email">이메일</label><input type="email" id="email" name="email" value="${member.email}"></div>
            <div class="form-field"><label for="phone">전화번호</label><input type="text" id="phone" name="phone" value="${member.phone}"></div>
            <button type="submit" class="form-submit">정보 저장</button>
        </form>

        <div class="account-link-grid">
            <a href="${pageContext.request.contextPath}/member/password" class="account-link-card">비밀번호 수정</a>
            <a href="${pageContext.request.contextPath}/member/delete" class="account-link-card danger">회원 탈퇴</a>
        </div>
    </div>
</section>
