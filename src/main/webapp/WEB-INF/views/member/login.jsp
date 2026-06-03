<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<style>
    .auth-divider {
        display: flex;
        align-items: center;
        gap: 14px;
        margin: 24px 0 20px;
        color: #9ca3af;
        font-size: 14px;
        font-weight: 700;
    }

    .auth-divider::before,
    .auth-divider::after {
        content: "";
        flex: 1;
        height: 1px;
        background: #e5e7eb;
    }

    .kakao-login-btn {
        width: 100%;
        height: 48px;
        border-radius: 12px;
        background: #FEE500;
        color: #191919;
        text-decoration: none;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        gap: 10px;
        font-size: 16px;
        font-weight: 800;
        transition: filter 0.2s ease, transform 0.2s ease;
    }

    .kakao-login-btn:hover {
        filter: brightness(0.97);
        transform: translateY(-1px);
    }

    .kakao-icon {
        width: 22px;
        height: 22px;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
    }

    .kakao-icon svg {
        width: 22px;
        height: 22px;
        display: block;
        fill: #191919;
    }
</style>

<section class="auth-page">
    <div class="auth-card">
        <div class="auth-top">
            <span class="section-badge">LOGIN</span>
            <h2>다시 만나서 반가워요</h2>
            <p>아이디와 비밀번호를 입력해 로그인하세요.</p>
        </div>

        <c:if test="${not empty authMessage}">
            <div class="auth-alert" style="background:#ecfdf3; color:#166534; border-color:#bbf7d0;">
                    ${authMessage}
            </div>
        </c:if>

        <c:if test="${param.registered eq 'true'}">
            <div class="auth-alert" style="background:#ecfdf3; color:#166534; border-color:#bbf7d0;">
                회원가입이 완료되었습니다. 로그인해 주세요.
            </div>
        </c:if>

        <c:if test="${param.error eq 'true'}">
            <div class="auth-alert">
                아이디 또는 비밀번호가 올바르지 않습니다.
            </div>
        </c:if>

        <c:if test="${not empty authError}">
            <div class="auth-alert">
                    ${authError}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/member/login" method="post" class="auth-form">
            <div class="form-field">
                <label for="username">아이디</label>
                <input type="text" id="username" name="username" placeholder="아이디를 입력하세요" required>
            </div>

            <div class="form-field">
                <label for="password">비밀번호</label>
                <input type="password" id="password" name="password" placeholder="비밀번호를 입력하세요" required>
            </div>

            <button type="submit" class="form-submit">로그인</button>
        </form>

        <div class="auth-divider" aria-hidden="true">
            <span>또는</span>
        </div>

        <a href="${pageContext.request.contextPath}/member/kakaoStart" class="kakao-login-btn">
            <span class="kakao-icon" aria-hidden="true">
                <svg viewBox="0 0 24 24" role="img" focusable="false">
                    <path d="M12 3C6.48 3 2 6.58 2 10.99c0 2.78 1.78 5.23 4.48 6.66l-.72 2.64c-.11.39.34.7.67.47l3.15-2.1c.78.15 1.59.23 2.42.23 5.52 0 10-3.58 10-7.99C22 6.58 17.52 3 12 3z"/>
                </svg>
            </span>
            카카오 로그인
        </a>

        <div class="auth-links">
            <a href="${pageContext.request.contextPath}/member/register">회원가입 하러 가기</a>
        </div>
    </div>
</section>