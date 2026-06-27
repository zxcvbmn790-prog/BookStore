<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

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

    .result-box {
        display: none;
        margin-top: 20px;
        padding: 24px;
        background: #ecfdf3;
        border: 1px solid #bbf7d0;
        border-radius: 12px;
        text-align: center;
    }
    .result-box.show { display: block; }

    .form-submit:disabled {
        background: #d1d5db;
        cursor: not-allowed;
    }
</style>

<section class="auth-page">
    <div class="auth-card">
        <div class="auth-top">
            <span class="section-badge">FIND PASSWORD</span>
            <h2>비밀번호를 잊으셨나요?</h2>
        </div>

        <div id="formArea">
            <div class="auth-form">
                <div class="form-field">
                    <label for="username">아이디</label>
                    <input type="text" id="username" name="username" placeholder="아이디를 입력하세요" required>
                </div>

                <div class="form-field">
                    <label for="email">이메일</label>
                    <div style="display: flex; gap: 8px; width: 100%;">
                        <input type="email" id="email" name="email" placeholder="가입하신 이메일을 입력하세요" required style="flex: 1;">
                        <button type="button" id="btnSendOtp" class="btn-inline" onclick="sendOtp()">인증번호 발송</button>
                    </div>
                </div>

                <div id="otpSection" class="otp-section">
                    <div class="otp-row">
                        <input type="text" id="otpInput" class="otp-input" maxlength="6"
                               placeholder="------" inputmode="numeric" pattern="[0-9]*">
                        <span class="otp-timer" id="otpTimer"></span>
                        <button type="button" class="btn-inline" onclick="verifyOtp()" id="btnVerifyOtp"
                                style="padding: 0 20px;" disabled>확인</button>
                    </div>
                    <small id="otpMsg" class="otp-msg"></small>
                </div>
            </div>
        </div>

        <div id="resultBox" class="result-box">
            <p style="margin:0; color:#166534;">
                임시 비밀번호가 입력하신 이메일로 발송되었습니다.<br>
                로그인 후 반드시 비밀번호를 변경해주세요.
            </p>
            <a href="${pageContext.request.contextPath}/member/login" class="form-submit"
               style="display:inline-block; margin-top:14px; text-decoration:none;">로그인 하러 가기</a>
        </div>

        <div class="auth-links">
            <a href="${pageContext.request.contextPath}/member/login">로그인으로 돌아가기</a>
            <span style="color:#d1d5db; margin: 0 6px;">|</span>
            <a href="${pageContext.request.contextPath}/member/findId">아이디 찾기</a>
        </div>
    </div>
</section>

<script type="text/javascript">
var ctx = "${pageContext.request.contextPath}";
var timerInterval;
var remaining = 0;

function sendOtp() {
    var username = $("#username").val().trim();
    var email = $("#email").val().trim();

    if (!username) {
        alert("아이디를 입력해주세요.");
        $("#username").focus();
        return;
    }
    if (!email) {
        alert("이메일을 입력해주세요.");
        $("#email").focus();
        return;
    }

    $("#btnSendOtp").prop("disabled", true).text("발송 중...");

    $.ajax({
        url: ctx + "/member/findPassword/sendOtp",
        type: "POST",
        data: { username: username, email: email },
        success: function(res) {
            showOtpMsg(res.message, "success");
            remaining = res.remainingSec || 180;
            startTimer();
            $("#otpSection").addClass("show");
            $("#otpInput").val("").focus();
            $("#btnSendOtp").prop("disabled", false).text("재발송");
            $("#btnVerifyOtp").prop("disabled", false);
            $("#username, #email").prop("readonly", true).css("background", "#f3f4f6");
        },
        error: function(xhr) {
            var res = xhr.responseJSON;
            alert(res ? res.message : "인증 요청에 실패했습니다.");
            $("#btnSendOtp").prop("disabled", false).text("인증번호 발송");
        }
    });
}

function verifyOtp() {
    var otp = $("#otpInput").val().trim();
    if (!otp || otp.length !== 6) {
        showOtpMsg("6자리 인증번호를 입력해주세요.", "error");
        $("#otpInput").focus();
        return;
    }

    $("#btnVerifyOtp").prop("disabled", true).text("확인 중...");

    $.ajax({
        url: ctx + "/member/findPassword/verifyOtp",
        type: "POST",
        data: { otp: otp },
        success: function(res) {
            clearInterval(timerInterval);
            $("#formArea").hide();
            $("#resultBox").addClass("show");
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

$(document).ready(function() {
    $("#otpInput").on("input", function() {
        this.value = this.value.replace(/[^0-9]/g, "");
    });
    $("#otpInput").on("keyup", function(e) {
        if (e.keyCode === 13 && this.value.length === 6) { verifyOtp(); }
    });
});
</script>