<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="auth-page">
    <div class="auth-card">
        <div class="auth-top">
            <span class="section-badge">REGISTER</span>
            <h2>새 계정을 만들어보세요</h2>
            <p>간단한 정보만 입력하면 바로 시작할 수 있어요.</p>
        </div>

        <c:if test="${param.error eq 'true'}">
            <div class="auth-alert">
                회원가입에 실패했습니다. 입력값을 다시 확인해주세요.
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/member/register" method="post" class="auth-form">
            <div class="form-field">
                <label for="username">아이디</label>
                <input type="text" id="username" name="username" placeholder="아이디를 입력하세요" required>
            </div>

            <div class="form-field">
                <label for="password">비밀번호</label>
                <input type="password" id="password" name="password" placeholder="비밀번호를 입력하세요" required>
            </div>

            <div class="form-field">
                <label for="phone">전화번호</label>
                <input type="text" id="phone" name="phone" placeholder="010-0000-0000">
            </div>

            <div class="form-field">
                <label for="email">이메일</label>
                <input type="email" id="email" name="email" placeholder="example@mail.com">
            </div>

            <button type="submit" class="form-submit">회원가입</button>
        </form>

        <div class="auth-links">
            <a href="${pageContext.request.contextPath}/member/login">이미 계정이 있나요? 로그인</a>
        </div>
    </div>
</section>