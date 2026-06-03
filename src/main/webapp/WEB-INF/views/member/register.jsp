<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

<section class="auth-page">
    <div class="auth-card">
        <div class="auth-top">
            <span class="section-badge">REGISTER</span>
            <h2>새 계정을 만들어보세요</h2>
            <p>간단한 정보만 입력하면 바로 시작할 수 있어요.</p>
        </div>

        <form action="${pageContext.request.contextPath}/member/register" method="post" class="auth-form">
            <div class="form-field">
                <label for="username">아이디</label>
                <div class="input-group" style="display: flex; gap: 8px; width: 100%;">
                    <input type="text" id="username" name="username" placeholder="아이디를 입력하세요" required style="flex: 1;">
                    <button type="button" class="btn-check-duplicate" onclick="checkDuplicate()" style="flex-shrink: 0; padding: 0 16px; background: #6366f1; color: white; border: none; border-radius: 6px; cursor: pointer; font-size: 14px; font-weight: 500;">중복 확인</button>
                </div>
                <small id="usernameCheckMsg" style="display: block; margin-top: 4px; font-size: 12px;"></small>
            </div>
			
			<div class="form-field">
			                <label for="nickname">닉네임</label>
			                <input type="text" id="nickname" name="nickname" placeholder="닉네임을 입력하세요">
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

            <button type="submit" class="form-submit" style="margin-top: 16px;">회원가입</button>
        </form>

        <script type="text/javascript">
        /*function checkDuplicate() {
            const username = document.getElementById("username").value.trim();
            
            if(!username) {
                alert("아이디를 입력한 뒤 중복 확인을 해주세요.");
                document.getElementById("username").focus();
                return;
            }
            
            // 임시 확인용 알림창
            alert(username + " 아이디의 중복 여부를 확인합니다.");
        }*/
		
		function checkDuplicate() {
		    const username = $("#username").val().trim();
		    
		    if(!username) {
		        alert("아이디를 입력한 뒤 중복 확인을 해주세요.");
		        $("#username").focus();
		        return;
		    }

		    $.ajax({
		        url: "${pageContext.request.contextPath}/member/checkId", // 서버의 중복확인 URL
		        type: "GET",
		        data: { username: username }, // 서버로 보낼 파라미터 key-value
		        
		        success: function(isAvailable) {
		            // 서버에서 Boolean 타입(true/false)으로 결과가 넘어옵니다.
		            if(isAvailable === true) {
		                $("#usernameCheckMsg")
		                    .css("color", "#166534")
		                    .text("✓ 사용 가능한 아이디입니다.");
		            } else {
		                $("#usernameCheckMsg")
		                    .css("color", "#b91c1c")
		                    .text("✕ 이미 사용 중인 아이디입니다.");
		                $("#username").val("").focus(); // 입력창 비우고 포커스
		            }
		        },
		        error: function() {
		            alert("중복 확인 중 오류가 발생했습니다.");
		        }
		    });
		}
        </script>
        <div class="auth-links">
            <a href="${pageContext.request.contextPath}/member/login">이미 계정이 있나요? 로그인</a>
        </div>
    </div>
</section>