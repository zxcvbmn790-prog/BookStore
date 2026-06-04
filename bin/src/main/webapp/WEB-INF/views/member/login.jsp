<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="auth-page">
    <div class="auth-card">
        <div class="auth-top">
            <span class="section-badge">LOGIN</span>
            <h2>다시 만나서 반가워요</h2>
            <p>아이디와 비밀번호를 입력해 로그인하세요.</p>
        </div>

        <c:if test="${param.error eq 'true'}">
            <div class="auth-alert">
                아이디 또는 비밀번호가 올바르지 않습니다.
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

        <div class="auth-links">
            <a href="${pageContext.request.contextPath}/member/register">회원가입 하러 가기</a>
        </div>
    </div>
</section>