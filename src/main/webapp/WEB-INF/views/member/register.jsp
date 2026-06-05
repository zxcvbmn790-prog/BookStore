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
			        
			        <button type="button" class="btn-check-duplicate" 
			                onclick="
			                    const val = document.getElementById('username').value.trim();
			                    if(!val) { alert('아이디를 입력해주세요.'); return; }
			                    
			                    $.ajax({
			                        url: '${pageContext.request.contextPath}/member/checkId',
			                        type: 'GET',
			                        data: { username: val },
			                        success: function(res) {
			                            if(res === true) {
			                                $('#usernameCheckMsg').css('color', '#166534').text('✓ 사용 가능한 아이디입니다.');
			                                window.isIdChecked = true;
			                            } else {
			                                $('#usernameCheckMsg').css('color', '#b91c1c').text('✕ 이미 사용 중인 아이디입니다.');
			                                $('#username').val('').focus();
			                                window.isIdChecked = false;
			                            }
			                        },
			                        error: function() { alert('통신 실패! 서버 연결을 확인하세요.'); }
			                    });
			                " 
			                style="flex-shrink: 0; padding: 0 16px; background: #6366f1; color: white; border: none; border-radius: 6px; cursor: pointer; font-size: 14px; font-weight: 500;">
			            중복 확인
			        </button>
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

            <button type="submit" id="btnRegister" class="form-submit" style="margin-top: 16px;">회원가입</button>
        </form>

		<script type="text/javascript">
		        window.isIdChecked = false;

		        $(document).ready(function() {
		            $("#username").on("input", function() {
		                if(window.isIdChecked) {
		                    window.isIdChecked = false;
		                    $("#usernameCheckMsg")
		                        .css("color", "#ea580c")
		                        .text("⚠️ 아이디가 변경되었습니다. 다시 중복 확인을 해주세요.");
		                }
		            });

		            $(".auth-form").on("submit", function(e) {
		                console.log("폼 서브밋 이벤트 발생. 중복 확인 상태: " + window.isIdChecked);
		                
		                if(window.isIdChecked === false) {
		                    alert("아이디 중복 확인을 완료하지 않았습니다.\n중복 확인 버튼을 먼저 눌러주세요.");
		                    $("#username").focus();
		                    
		                    e.preventDefault(); 
		                    return false;
		                }
		            });
		        });
		</script>
        
        <div class="auth-links">
            <a href="${pageContext.request.contextPath}/member/login">이미 계정이 있나요? 로그인</a>
        </div>
    </div>
</section>