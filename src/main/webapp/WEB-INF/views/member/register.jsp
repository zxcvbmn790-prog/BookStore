<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://www.google.com/recaptcha/api.js" async defer></script>

<style>
    .btn-inline {
        flex-shrink: 0;
        padding: 0 16px;
        background: #6366f1;
        color: white;
        border: none;
        border-radius: 6px;
        cursor: pointer;
        font-size: 14px;
        font-weight: 500;
        height: 44px;
    }
    .btn-inline:hover { background: #4f46e5; }
    .btn-inline:disabled { background: #9ca3af; cursor: not-allowed; }

    .otp-section {
        display: none;
        margin-top: 10px;
        padding: 16px;
        background: #f9fafb;
        border-radius: 10px;
        border: 1px solid #e5e7eb;
        overflow: hidden;
    }
    .otp-section.show { display: block; }

    .otp-row {
        display: flex;
        gap: 8px;
        align-items: center;
        width: 100%;
    }
    .otp-input {
        width: 0;
        min-width: 0;
        flex: 1 1 0;
        text-align: center;
        font-size: 20px;
        font-weight: 700;
        letter-spacing: 8px;
        padding: 10px 4px;
        border: 2px solid #e5e7eb;
        border-radius: 8px;
        outline: none;
        box-sizing: border-box;
    }
    .otp-input:focus { border-color: #6366f1; }

    .otp-timer {
        font-size: 15px;
        font-weight: 700;
        color: #6366f1;
        min-width: 42px;
        flex-shrink: 0;
        text-align: center;
    }
    .otp-timer.expired { color: #ef4444; }

    .otp-msg {
        display: block;
        margin-top: 8px;
        font-size: 12px;
    }
    .otp-msg.error { color: #b91c1c; }
    .otp-msg.success { color: #166534; }

    .verified-badge {
        display: none;
        align-items: center;
        gap: 6px;
        margin-top: 6px;
        padding: 6px 12px;
        background: #ecfdf3;
        color: #166534;
        border-radius: 6px;
        font-size: 13px;
        font-weight: 600;
    }
    .verified-badge.show { display: inline-flex; }

    .form-submit:disabled {
        background: #d1d5db;
        cursor: not-allowed;
    }
</style>

<section class="auth-page">
    <div class="auth-card">
        <div class="auth-top">
            <span class="section-badge">REGISTER</span>
            <h2>새 계정을 만들어보세요</h2>
            <p>간단한 정보만 입력하면 바로 시작할 수 있어요.</p>
        </div>

        <form id="registerForm" action="${pageContext.request.contextPath}/member/register" method="post" class="auth-form">

            <!-- 아이디 -->
            <div class="form-field">
                <label for="username">아이디</label>
                <div style="display: flex; gap: 8px; width: 100%;">
                    <input type="text" id="username" name="username" placeholder="아이디를 입력하세요" required style="flex: 1;">
                    <button type="button" class="btn-inline" onclick="checkDuplicate()">중복 확인</button>
                </div>
                <small id="usernameCheckMsg" style="display: block; margin-top: 4px; font-size: 12px;"></small>
            </div>

            <!-- 닉네임 -->
            <div class="form-field">
                <label for="nickname">닉네임</label>
                <input type="text" id="nickname" name="nickname" placeholder="닉네임을 입력하세요">
            </div>

            <!-- 비밀번호 -->
            <div class="form-field">
                <label for="password">비밀번호</label>
                <input type="password" id="password" name="password" placeholder="비밀번호를 입력하세요" required>
            </div>

            <!-- 전화번호 -->
            <div class="form-field">
                <label for="phone">전화번호</label>
                <input type="text" id="phone" name="phone" placeholder="010-0000-0000">
            </div>

            <!-- 이메일 + 인증 -->
            <div class="form-field">
                <label for="email">이메일 <span style="color:#ef4444;">*</span></label>
                <div style="display: flex; gap: 8px; width: 100%;">
                    <input type="email" id="email" name="email" placeholder="example@mail.com" required style="flex: 1;">
                    <button type="button" id="btnSendOtp" class="btn-inline" onclick="sendOtp()">인증번호 발송</button>
                </div>
                <span id="verifiedBadge" class="verified-badge"><i class="fas fa-check-circle"></i> 이메일 인증 완료</span>
            </div>
            
            <!-- reCAPTCHA 자동 가입 방지 -->
			<div class="form-field">
			    <label>자동 가입 방지</label>
			
			    <div class="g-recaptcha"
			         data-sitekey="6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI">
			    </div>
			
			    <small style="display:block; margin-top:6px; color:#6b7280;">
			        인증번호 발송 전에 로봇이 아님을 확인해주세요.
			    </small>
			</div>

            <!-- OTP 입력 영역 -->
            <div id="otpSection" class="otp-section">
                <div class="otp-row">
                    <input type="text" id="otpInput" class="otp-input" maxlength="6"
                           placeholder="------" inputmode="numeric" pattern="[0-9]*">
                    <span class="otp-timer" id="otpTimer"></span>
                    <button type="button" class="btn-inline" onclick="verifyOtp()" id="btnVerifyOtp"
                            style="padding: 0 20px;">확인</button>
                </div>
                <small id="otpMsg" class="otp-msg"></small>
            </div>

            <button type="submit" id="btnRegister" class="form-submit" style="margin-top: 16px;" disabled>회원가입</button>
        </form>

        <div class="auth-links">
            <a href="${pageContext.request.contextPath}/member/login">이미 계정이 있나요? 로그인</a>
        </div>
    </div>
</section>

<script type="text/javascript">
var ctx = "${pageContext.request.contextPath}";
var emailVerified = false;
var timerInterval;
var remaining = 0;

// ==================== 아이디 중복 확인 ====================
function checkDuplicate() {
    var username = $("#username").val().trim();
    if (!username) {
        alert("아이디를 입력한 뒤 중복 확인을 해주세요.");
        $("#username").focus();
        return;
    }
    $.ajax({
        url: ctx + "/member/checkId",
        type: "GET",
        data: { username: username },
        success: function(isAvailable) {
            if (isAvailable === true) {
                $("#usernameCheckMsg").css("color", "#166534").text("✓ 사용 가능한 아이디입니다.");
            } else {
                $("#usernameCheckMsg").css("color", "#b91c1c").text("✕ 이미 사용 중인 아이디입니다.");
                $("#username").val("").focus();
            }
        },
        error: function() { alert("중복 확인 중 오류가 발생했습니다."); }
    });
}

// ==================== OTP 발송 ====================
// ==================== OTP 발송 ====================
function sendOtp() {
    var email = $("#email").val().trim();

    if (!email) {
        alert("이메일을 입력해주세요.");
        $("#email").focus();
        return;
    }

    // reCAPTCHA 토큰 가져오기
    var captchaToken = "";

    if (typeof grecaptcha !== "undefined") {
        captchaToken = grecaptcha.getResponse();
    }

    if (!captchaToken) {
        alert("로봇이 아닙니다 인증을 먼저 완료해주세요.");
        return;
    }

    $("#btnSendOtp").prop("disabled", true).text("발송 중...");

    $.ajax({
        url: ctx + "/member/sendOtp",
        type: "POST",
        data: {
            email: email,
            "g-recaptcha-response": captchaToken
        },
        success: function(res) {
            showOtpMsg(res.message, "success");
            remaining = res.remainingSec || 180;
            startTimer();

            $("#otpSection").addClass("show");
            $("#otpInput").val("").focus();
            $("#btnSendOtp").prop("disabled", false).text("재발송");
            $("#btnVerifyOtp").prop("disabled", false);

            // 한 번 사용한 reCAPTCHA는 초기화
            if (typeof grecaptcha !== "undefined") {
                grecaptcha.reset();
            }
        },
        error: function(xhr) {
            var res = xhr.responseJSON;
            alert(res ? res.message : "이메일 발송에 실패했습니다.");

            $("#btnSendOtp").prop("disabled", false).text("인증번호 발송");

            // 실패해도 다시 체크하게 초기화
            if (typeof grecaptcha !== "undefined") {
                grecaptcha.reset();
            }
        }
    });
}

// ==================== OTP 검증 ====================
function verifyOtp() {
    var otp = $("#otpInput").val().trim();
    if (!otp || otp.length !== 6) {
        showOtpMsg("6자리 인증번호를 입력해주세요.", "error");
        $("#otpInput").focus();
        return;
    }

    $("#btnVerifyOtp").prop("disabled", true).text("확인 중...");

    $.ajax({
        url: ctx + "/member/verifyOtp",
        type: "POST",
        data: { otp: otp },
        success: function(res) {
            emailVerified = true;
            clearInterval(timerInterval);

            // OTP 영역 숨기고 인증 완료 표시
            $("#otpSection").removeClass("show");
            $("#verifiedBadge").addClass("show");

            // 이메일 필드와 발송 버튼 비활성화
            $("#email").prop("readonly", true).css("background", "#f3f4f6");
            $("#btnSendOtp").hide();

            // 회원가입 버튼 활성화
            $("#btnRegister").prop("disabled", false);

            showOtpMsg("", "");
        },
        error: function(xhr) {
            var res = xhr.responseJSON;
            if (res && res.result === "expired") {
                showOtpMsg(res.message, "error");
                clearInterval(timerInterval);
                remaining = 0;
                updateTimerDisplay();
                $("#btnVerifyOtp").prop("disabled", true).text("확인");
            } else {
                showOtpMsg(res ? res.message : "오류가 발생했습니다.", "error");
                $("#btnVerifyOtp").prop("disabled", false).text("확인");
            }
        }
    });
}

// ==================== 타이머 ====================
function startTimer() {
    clearInterval(timerInterval);
    updateTimerDisplay();
    timerInterval = setInterval(function() {
        remaining--;
        updateTimerDisplay();
        if (remaining <= 0) {
            clearInterval(timerInterval);
            $("#btnVerifyOtp").prop("disabled", true);
        }
    }, 1000);
}

function updateTimerDisplay() {
    var el = $("#otpTimer");
    if (remaining <= 0) {
        el.text("만료").addClass("expired");
    } else {
        var m = Math.floor(remaining / 60);
        var s = remaining % 60;
        el.text(m + ":" + (s < 10 ? "0" : "") + s).removeClass("expired");
    }
}

function showOtpMsg(msg, type) {
    $("#otpMsg").text(msg).removeClass("error success");
    if (type) { $("#otpMsg").addClass(type); }
}

// ==================== 이메일 변경 시 인증 초기화 ====================
$(document).ready(function() {
    $("#email").on("input", function() {
        if (emailVerified) {
            emailVerified = false;
            $("#verifiedBadge").removeClass("show");
            $("#btnRegister").prop("disabled", true);
            $("#email").prop("readonly", false).css("background", "");
            $("#btnSendOtp").show().text("인증번호 발송");
        }
    });

    // OTP 입력란 숫자만 허용
    $("#otpInput").on("input", function() {
        this.value = this.value.replace(/[^0-9]/g, "");
    });
    // OTP 6자리 입력 후 엔터
    $("#otpInput").on("keyup", function(e) {
        if (e.keyCode === 13 && this.value.length === 6) { verifyOtp(); }
    });

    // 폼 전송 시 인증 여부 재확인
    $("#registerForm").on("submit", function(e) {
        if (!emailVerified) {
            e.preventDefault();
            alert("이메일 인증을 먼저 완료해주세요.");
        }
    });
});
</script>
